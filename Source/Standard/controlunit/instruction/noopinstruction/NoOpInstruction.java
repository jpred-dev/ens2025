package controlunit.instruction.noopinstruction;

import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;

public abstract class NoOpInstruction extends Instruction {
	public NoOpInstruction(InstructionData inst) { super(inst); }
}
