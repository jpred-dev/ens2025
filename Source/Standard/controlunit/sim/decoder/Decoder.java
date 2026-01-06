package controlunit.sim.decoder;

import java.util.ArrayList;
import java.util.List;

import controlunit.architecture.Architecture;
import controlunit.codex.ISACodex;
import controlunit.codex.VerificationCodex;
import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;
import controlunit.instruction.Operand;
import customexception.BreakpointEncounteredException;
import data.RData;
import enums.AddressingMode;
import enums.InstructionType;
import io.IOInterface;
import settings.Settings;

public class Decoder {
	
	private Architecture arc;
	private IOInterface io;
	private Settings set;

	public Decoder(Architecture arc, IOInterface io, Settings set) {
		this.arc = arc;
		this.io = io;
		this.set = set;
	}
	
	public Instruction getInstruction(boolean ignoreBreakpoint) {
		RData pc = arc.getProgramCounter(); //Valor del PC al inicio de la decodificación para hacer rollback en caso de detectar un breakpoint no ignorable.
		RData word;
		InstructionType iType;
		AddressingMode addr1, addr2;
		List<Operand> opList = new ArrayList<>();
		Instruction inst;
		
		//Se extrae una palabra, se comprueba si se debe detener la decodificación/ejecución por encontrar un breakpoint y se aumenta el PC real.
		word = fetchWord(pc, ignoreBreakpoint);
		
		iType = ISACodex.getInstructionType(word);
		addr1 = ISACodex.getFirstAddressingMode(word);
		addr2 = ISACodex.getSecondAddressingMode(word);
		
		VerificationCodex.validateInstruction(iType, addr1, addr2);
		
		//Si el segundo direccionamiento es nulo, se trata de una instrucción de 0-1 operandos.
		if (addr2 == AddressingMode.NONE) {
			//Si el primer direccionamiento también es nulo, se trata de una instrucción sin operandos, por lo que pasamos directamente al final sin introducir nada en la lista de operandos.
			//En caso de que no lo sea, se extrae una palabra adicional donde estará almacenado el operando.
			if (addr1 != AddressingMode.NONE) {
				word = fetchWord(pc, ignoreBreakpoint);
				opList.add(ISACodex.getFirstOperand(word, addr1, set));
			}
		}
		else {
			//Si el segundo direccionamiento no es nulo, se trata de una instrucción de 2 operandos.
			if (VerificationCodex.isFullWord(addr1) || VerificationCodex.isFullWord(addr2)) {
				//Si uno o ambos direccionamientos son direccionamientos de palabra completa, se extraen dos palabras adicionales.
				RData auxword;
				
				word = fetchWord(pc, ignoreBreakpoint);
				auxword = fetchWord(pc, ignoreBreakpoint);
				//Se obtienen los operandos y se almacenan en la lista.
				opList.add(ISACodex.getFirstOperand(word, addr1, set));
				opList.add(ISACodex.getSecondOperand(auxword, addr2, set));
			}
			else {
				//Si ni el primer ni el segundo direccionamiento son de palabra completa, los dos operandos están contenidos en una sola palabra.
				//Se extrae una única palabra adicional, se hace la comprobación de breakpoint y se aumenta el PC.
				word = fetchWord(pc, ignoreBreakpoint);
				
				//Se obtienen los operandos y se almacenan en la lista.
				opList.add(ISACodex.getFirstOperand(word, addr1, set));
				opList.add(ISACodex.getSecondOperand(word, addr2, set));
				
			}
		}
		
		//Se crea la instrucción con la lista cargada con los operandos por el procedimiento anterior.
		inst = createInstruction(iType, opList);
		
		return inst;
	}
	
	private RData fetchWord(RData rollback, boolean ignoreBreakpoint) {
		RData pc = arc.getProgramCounter();
		RData word = arc.readMemory(pc);
		
		if (ignoreBreakpoint == false && arc.hasBreakpoint(pc)) {
			arc.setProgramCounter(rollback);
			throw new BreakpointEncounteredException();
		}

		arc.increaseProgramCounter();
		
		return word;
	}
	
	private Instruction createInstruction(InstructionType iType, List<Operand> opList) {
		InstructionData iData = new InstructionData(opList, io);
		return iType.getConstructor().apply(iData);
	}
}
