package controlunit.instruction.noopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;

public final class NOP extends NoOpInstruction {
	public NOP(InstructionData inst) { super(inst); }
	
	public void run(Architecture arc) { }
}
