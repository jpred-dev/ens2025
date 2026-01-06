package controlunit.assembler.fileparser.instructionparser;

import java.util.ArrayList;
import java.util.List;

import controlunit.assembler.fileparser.filedata.FileData;
import controlunit.assembler.util.InstructionBlueprint;
import controlunit.assembler.util.OperandBlueprint;
import controlunit.codex.ISACodex;
import controlunit.codex.VerificationCodex;
import customexception.InstructionNotImplementedException;
import customexception.InvalidCharacterException;
import customexception.InvalidRegisterException;
import customexception.InvalidValueException;
import data.RData;
import data.RWData;
import enums.AddressingMode;
import enums.InstructionType;
import enums.RegisterID;

public class InstructionParser {
	private FileData fd;

	public InstructionParser() { }
	
	public void loadFileData(FileData fd) {
		this.fd = fd;
	}

	public void close() {
		fd = null;
	}
	
	public InstructionBlueprint parseInstruction(InstructionType it) {
		List<OperandBlueprint> opList = new ArrayList<>();
		
		//El escenario por defecto es sin operandos, así que solamente se comprueba si es una instrucción con uno o más operandos.
		if (InstructionType.isOneOpInstruction(it) || InstructionType.isOneOpIOInstruction(it))
			opList.add(parseOperand());
		else if (InstructionType.isTwoOpInstruction(it)) {
			opList.add(parseOperand());
			
			if (fd.seekChar(',', true) == false) {
				InvalidCharacterException ex = new InvalidCharacterException("Se esperaba el carácter ',' tras el primer operando.");
				ex.setLine(fd.getLineNumber());
				throw ex;
			}

			opList.add(parseOperand());
		}

		if (fd.seekEOS() == false) {
			char c = fd.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba final de línea, se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}

		try {
			validateInstruction(it, opList);
		} catch (InstructionNotImplementedException ex) {
			ex.setLine(fd.getLineNumber() + 1);
			throw ex;
		}
		
		return new InstructionBlueprint(it, opList);
	}
	
	private void validateInstruction(InstructionType it, List<OperandBlueprint> ops) {
		switch (ops.size()) {
			case 0:
				VerificationCodex.validateInstruction(it, AddressingMode.NONE, AddressingMode.NONE);
				break;
			case 1:
				VerificationCodex.validateInstruction(it, ops.get(0).getAddressingMode(), AddressingMode.NONE);
				break;
			case 2:
				VerificationCodex.validateInstruction(it, ops.get(0).getAddressingMode(), ops.get(1).getAddressingMode());
				break;
		}
	}
	
	
	private OperandBlueprint parseOperand() {
		OperandBlueprint op = null;
		char c = fd.consumeChar(true);
	
		switch (c) {
			case '\0':
				InvalidCharacterException ex1 = new InvalidCharacterException("Encontrado final de línea inesperado.");
				ex1.setLine(fd.getLineNumber());
				throw ex1;
			case '/':
				op = parseMemory();
				break;
				
			case '#':
				op = parseLiteralOrRelative();
				break;
				
			case '.':
				op = parseRegister();
				break;
				
			case '[':
				op = parseIndirect();
				break;
				
			case '$':
				op = parseRelativeToPC();
				break;
				
			default: 
				InvalidCharacterException ex2 = new InvalidCharacterException("Se esperaba un carácter de direccionamiento válido, se ha encontrado '" + c + "'.");
				ex2.setLine(fd.getLineNumber());
				throw ex2;
		}
		return op;
	}
	
	private OperandBlueprint parseMemory() {
		OperandBlueprint op = null;
		char c = fd.peekChar(false);

		if (c == '-' || Character.isDigit(c)) {
			RData val = fd.getData(false).getMutableCopy();
			op = new OperandBlueprint(AddressingMode.MEMORY, val);
		}
		else if (Character.isLetter(c)) {
			String mnemonic = fd.getMnemonic(false);
			op = new OperandBlueprint(AddressingMode.MEMORY, new RWData(0));
			op.markAsReference(mnemonic);
		}
		else {
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un mnemónico o entero válidos, se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		return op;
	}
	
	private OperandBlueprint parseLiteralOrRelative() {
		OperandBlueprint op = null;
		RWData val = new RWData(0);
		String mnemonic = null;
		
		char c = fd.peekChar(false);

		if (Character.isLetter(c)) {
			mnemonic = fd.getMnemonic(false);
			op = new OperandBlueprint(AddressingMode.LITERAL, val);
		}
		else
			val.assign(fd.getData(false));
		
		c = fd.peekChar(false);
		
		if (c  == '[')
			op = parseRelativeToIndex(val);
		else
			op = new OperandBlueprint(AddressingMode.LITERAL, val);

		//Si no se ha generado una etiqueta, el mnemónico será null y no tendrá efecto.
		op.markAsReference(mnemonic);
		
		return op;
	}
	
	private OperandBlueprint parseRelativeToIndex(RData val) {
		RegisterID reg;
		AddressingMode addr = null;
		
		
		if (ISACodex.isOffsetValid(val) == false) {
			InvalidValueException ex = new InvalidValueException("El valor de desplazamiento no está en el rango permitido (debe estar comprendido entre -128 y 255, ambos incluidos).");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		fd.consumeChar(false); //Ahora sí, se consume.
		if (fd.seekChar('.', false) == false) {
			char c = fd.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un carácter '.', se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		reg = RegisterID.getFromName(fd.getMnemonic(false));

		if (reg == RegisterID.IX)
			addr = AddressingMode.RELATIVE_IX;
		else if (reg == RegisterID.IY)
			addr = AddressingMode.RELATIVE_IY;
		else {
			InvalidRegisterException ex = new InvalidRegisterException("Mnemónico de registro incorrecto. Solamente se admiten IX/IY.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		if (fd.seekChar(']', false) == false) {
			char c = fd.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un carácter ']', se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		return new OperandBlueprint(addr, reg, val);
	}
	
	private OperandBlueprint parseRegister() {
		RegisterID reg;
		reg = RegisterID.getFromName(fd.getMnemonic(false));
		
		if (reg == null) {
			InvalidRegisterException ex = new InvalidRegisterException("Mnemónico de registro no válido.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		return new OperandBlueprint(AddressingMode.REGISTER, reg);
	}
	
	private OperandBlueprint parseIndirect() {
		RegisterID reg;
		
		if (fd.seekChar('.', false) == false) {
			char c = fd.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un carácter '.', se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		reg = RegisterID.getFromName(fd.getMnemonic(false));
		if (reg == null) {
			InvalidRegisterException ex = new InvalidRegisterException("Mnemónico de registro no válido.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		if (fd.seekChar(']', false) == false) {
			char c = fd.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un carácter ']', se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		return new OperandBlueprint(AddressingMode.INDIRECT, reg);
	}
	
	private OperandBlueprint parseRelativeToPC() {
		OperandBlueprint op = null;
		char c = fd.peekChar(false);
		
		if (c == '-' || Character.isDigit(c)) {
			RData val = fd.getData(false);
			
			if (ISACodex.isOffsetValid(val) == false) {
				InvalidValueException ex = new InvalidValueException("El valor de desplazamiento no está en el rango permitido (debe estar comprendido entre -128 y 255, ambos incluidos).");
				ex.setLine(fd.getLineNumber());
				throw ex;
			}
			
			op = new OperandBlueprint(AddressingMode.RELATIVE_PC, val);
		}
		else if (Character.isLetter(c)) {
			String mnemonic = fd.getMnemonic(false);
			op = new OperandBlueprint(AddressingMode.RELATIVE_PC, new RWData(0));
			op.markAsReference(mnemonic);
		}
		else {
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un mnemónico o entero válidos, se ha encontrado '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		return op;
	}
}
