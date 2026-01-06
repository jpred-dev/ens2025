package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class CALL extends OneOpInstruction {

	public CALL(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getJumpValue(op);
		RData stackPointer = arc.getStackPointer();
		RData programCounter = arc.getProgramCounter();
		
		arc.writeMemory(stackPointer, programCounter);
		arc.increaseStackPointer();
		arc.setProgramCounter(value);
	}
}
