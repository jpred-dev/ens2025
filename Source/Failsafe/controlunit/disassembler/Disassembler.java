package controlunit.disassembler;

import java.util.ArrayList;
import java.util.List;

import controlunit.architecture.Architecture;
import controlunit.codex.ISACodex;
import controlunit.codex.VerificationCodex;
import controlunit.instruction.Operand;
import customexception.CustomException;
import customexception.MemoryIndexOutOfBoundsException;
import data.RData;
import data.RWData;
import enums.AddressingMode;
import enums.InstructionType;
import settings.Settings;

public class Disassembler {
	private Architecture arc;
	private Settings set;
	
	public Disassembler(Architecture arc, Settings set) {
		this.arc = arc;
		this.set = set;
	}
	
	public List<DisassembledInstruction> disassembleInstructions(RData index, int n) {
		List<DisassembledInstruction> list = new ArrayList<>();
		DisassembledInstruction inst;
		RWData ind = index.getMutableCopy();
		boolean endReached = false;
		
		while(--n >= 0 && endReached == false) {
			inst = disassemble(ind);
			list.add(inst);
			//Si el índice tiene valor 0 tras el desensamblado, significa que se ha llegado al final de la memoria.
			//Por lo tanto, no se muestran más instrucciones.
			if (ind.isEqual(0))
				endReached = true;
		}
		
		return list;
	}
	
	private DisassembledInstruction disassemble(RWData index) {
		RWData indexClone = index.getMutableCopy();
		RData word;
		InstructionType iType;
		AddressingMode addr1, addr2;
		DisassembledInstruction inst;
		List<Operand> opList = new ArrayList<>();
		boolean breakpoint = false;
		int size = 1; //Siempre hay un tamaño de al menos una palabra.
		
		word = arc.readMemory(indexClone);
		
		if (arc.hasBreakpoint(indexClone))
			breakpoint = true;
		
		try {
			iType = ISACodex.getInstructionType(word);
			addr1 = ISACodex.getFirstAddressingMode(word);
			addr2 = ISACodex.getSecondAddressingMode(word);
			//Se verifica que la combinación de mnemónico de instrucción y tipos de direccionamiento de los operandos es válida.
			VerificationCodex.validateInstruction(iType, addr1, addr2);
		} catch (CustomException ex) {
			return wrongInstruction(index, breakpoint);
		}

		//Si el segundo direccionamiento es nulo, se trata de una instrucción de 0-1 operandos.
		if (addr2 == AddressingMode.NONE) {
			//Si el primer direccionamiento también es nulo, se trata de una instrucción sin operandos y se va directamente al final sin añadir operandos a la lista.
			//Si no lo es, es una instrucción de 1 operando.
			if (addr1 != AddressingMode.NONE) {
				try {
					//Se extrae una palabra, previa comprobación de desbordamiento.
					word = incrementIndexClone(indexClone);
					if (arc.hasBreakpoint(indexClone))
						breakpoint = true;
					opList.add(ISACodex.getFirstOperand(word, addr1, set));
					size += 1;
				} catch(CustomException e) {
					return wrongInstruction(index, breakpoint);
				}
			}
		}
		//Si el segundo direccionamiento no es nulo, se trata de una instrucción de 2 operandos.
		else {
			//Si uno o ambos direccionamientos son direccionamientos de palabra completa, se extraen dos palabras, con sus respectivos aumentos de indexClone y comprobaciones.
			if (VerificationCodex.isFullWord(addr1) || VerificationCodex.isFullWord(addr2)) {
				RData auxword;
				
				try {
					//Se extraen dos palabras; esto implica incrementar dos veces el índice y comprobar dos veces que no haya desbordamiento.
					word = incrementIndexClone(indexClone);
					if (arc.hasBreakpoint(indexClone))
						breakpoint = true;
					auxword = incrementIndexClone(indexClone);
					if (arc.hasBreakpoint(indexClone))
						breakpoint = true;
					opList.add(ISACodex.getFirstOperand(word, addr1, set));
					opList.add(ISACodex.getSecondOperand(auxword, addr2, set));
					size += 2;
				} catch(CustomException e) {
					return wrongInstruction(index, breakpoint);
				}
			}
			else {
				try {
					//Se extrae una palabra, previa comprobación de desbordamiento.
					word = incrementIndexClone(indexClone);
					if (arc.hasBreakpoint(indexClone))
						breakpoint = true;
					opList.add(ISACodex.getFirstOperand(word, addr1, set));
					opList.add(ISACodex.getSecondOperand(word, addr2, set));
					size += 1;
				} catch(CustomException e) {
					return wrongInstruction(index, breakpoint);
				}
			}
		}
		
		inst = new DisassembledInstruction(index, iType, opList, size, set, breakpoint);
		
		index.assign(indexClone); //Se iguala el índice al valor del placeholder utilizado.
		index.add(1); //Se incrementa una última vez el valor del índice para dejarlo apuntando a la siguiente palabra.
		
		return inst;
	}
	
	private RData incrementIndexClone(RWData indexClone) {
		indexClone.add(1);
		if(indexClone.isEqual(0) == true)
			throw new MemoryIndexOutOfBoundsException();
		return arc.readMemory(indexClone);
	}
	
	private DisassembledInstruction wrongInstruction(RWData index, boolean breakpoint) {
		DisassembledInstruction inst = new DisassembledInstruction(index, breakpoint);
		index.add(1);
		
		return inst;
	}
	
}
