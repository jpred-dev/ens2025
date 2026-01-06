package controlunit.assembler.memorywriter;

import java.util.List;

import controlunit.assembler.util.InstructionBlueprint;
import controlunit.assembler.util.OperandBlueprint;
import controlunit.assembler.util.Reference;
import controlunit.codex.ISACodex;
import controlunit.codex.VerificationCodex;
import customexception.InvalidValueException;
import data.RData;
import data.RWData;
import enums.AddressingMode;
import enums.InstructionType;
import enums.RegisterID;

public class Encoder {
	private MemoryPointer mp;
	
	public Encoder(MemoryPointer mp) {
		this.mp = mp;
	}

	public EncodedInstruction encodeInstruction(InstructionBlueprint ib, List<Reference> refList) {
		List<OperandBlueprint> ops = ib.getOps();
		InstructionType it = ib.getInstructionType();
		AddressingMode addr1, addr2;
		EncodedInstruction ei = new EncodedInstruction();
		RWData firstWord = new RWData(0);
		RWData secondWord = new RWData(0);
		RWData thirdWord = new RWData(0);
		RWData mask = new RWData(it.getId());
		
		Reference ref;
		
		
		mask.lShift(ISACodex.INSTRUCTION_CODE_SHIFT);
		firstWord.bitOr(mask);
		

		switch (ops.size()) {
			case 0:
				ei.addWord(firstWord);
				break;
	
			case 1:
				addr1 = ops.get(0).getAddressingMode();
				mask.assign(addr1.getId());
				mask.lShift(ISACodex.FIRST_ADDRESS_CODE_SHIFT);
				firstWord.bitOr(mask);
				ei.addWord(firstWord);
				ref = checkReference(ops.get(0), 1);
				
				if (ref != null)
					refList.add(ref);
				else
					secondWord.assign(encodeOperand(secondWord, ops.get(0), ISACodex.FIRST_OPERAND_SHIFT, ISACodex.FIRST_OPERAND_MASK));
				
				ei.addWord(secondWord);

				break;
				
			case 2:
				addr1 = ops.get(0).getAddressingMode();
				addr2 = ops.get(1).getAddressingMode();

				mask.assign(addr1.getId());
				mask.lShift(ISACodex.FIRST_ADDRESS_CODE_SHIFT);
				firstWord.bitOr(mask);
				
				mask.assign(addr2.getId());
				mask.lShift(ISACodex.SECOND_ADDRESS_CODE_SHIFT);
				firstWord.bitOr(mask);
				
				ei.addWord(firstWord);
				
				//Caso especial: ninguno de los operandos es de palabra completa.
				if (VerificationCodex.isFullWord(addr1) == false && VerificationCodex.isFullWord(addr2) == false) {
					ref = checkUnifiedReference(ops.get(0), 1);
					if (ref != null) { 
						refList.add(ref);}
					else
						secondWord.assign(encodeOperand(secondWord, ops.get(0), ISACodex.FIRST_OPERAND_SHIFT, ISACodex.FIRST_OPERAND_MASK));
					
					ref = checkUnifiedReference(ops.get(1), 2);
					if (ref != null)
						refList.add(ref);
					else
						secondWord.assign(encodeOperand(secondWord, ops.get(1), ISACodex.SECOND_OPERAND_SHIFT, ISACodex.SECOND_OPERAND_MASK));
	
					ei.addWord(secondWord);
					
				}
				else {
					ref = checkReference(ops.get(0), 1);
					if (ref != null) { 
						refList.add(ref);}
					else
						secondWord.assign(encodeOperand(secondWord, ops.get(0), ISACodex.FIRST_OPERAND_SHIFT, ISACodex.FIRST_OPERAND_MASK));
					
					ref = checkReference(ops.get(1), 2);
					if (ref != null)
						refList.add(ref);
					else
						thirdWord.assign(encodeOperand(thirdWord, ops.get(1), ISACodex.SECOND_OPERAND_SHIFT, ISACodex.SECOND_OPERAND_MASK));
	
					ei.addWord(secondWord);
					ei.addWord(thirdWord);
				}
				break;
				
			default:
				break;
		}
		
		return ei;
	}
	
	private Reference checkReference(OperandBlueprint op, int opNumber) {
		String label = op.getReference();
		Reference ref = null;
		RWData pointer = new RWData(mp.getPointer());
		
		
		if (label != null) {
			AddressingMode addr = op.getAddressingMode();
			if (VerificationCodex.isFullWord(addr)) {
				pointer.add(opNumber);
				ref = new Reference(pointer, label, false);
			}
			else if (addr == AddressingMode.RELATIVE_PC) {
				pointer.add(2); //posición que avanza el puntero por la palabra con códigos de instrucción y direccionamiento y la que avanza por el primer operando.
				ref = new Reference(pointer, label, true);
			}
			else if (addr == AddressingMode.RELATIVE_IX || addr == AddressingMode.RELATIVE_IY) {
				pointer.add(opNumber);
				ref = new Reference(pointer, label, false, opNumber); //El último parámetro indica en qué mitad de la palabra se ubicará la referencia.
			}
		}
		
		return ref;
	}
	
	private Reference checkUnifiedReference(OperandBlueprint op, int opNumber) {
		String label = op.getReference();
		Reference ref = null;
		RWData pointer = new RWData(mp.getPointer());
		
		if (label != null) {
			pointer.add(1); //Se sitúa la copia del puntero de escritura sobre la segunda palabra.
			ref = new Reference(pointer, label, false, opNumber); //El último parámetro indica en qué mitad de la palabra se ubicará la referencia.
		}
		
		return ref;
	}
	
	private RData encodeOperand(RData word, OperandBlueprint op, RData shift, RData opMask) {
		RWData value = new RWData(word);
		RWData mask = new RWData(0);
		AddressingMode addr = op.getAddressingMode();
		
		
		switch (addr) {
			case LITERAL:
			case MEMORY:
				value.assign(op.getData());
				break;
			
			case REGISTER:
			case INDIRECT:
				RegisterID reg = op.getRegister();
				mask.assign(reg.getId());
				mask.lShift(shift);
				mask.bitAnd(opMask);
				value.bitOr(mask);
				break;
			
			case RELATIVE_IX:
			case RELATIVE_IY:
			case RELATIVE_PC:
				mask.assign(op.getData());
				if (ISACodex.isOffsetValid(mask) == false) {
					InvalidValueException ex = new InvalidValueException("El valor de desplazamiento " + value.getValue() + " excede los límites permitidos.");
					throw ex;
				}
				mask.lShift(shift);
				mask.bitAnd(opMask);
				value.bitOr(mask);
				break;

			default:
				break;
		}
		
		return value;
	}
	
}
