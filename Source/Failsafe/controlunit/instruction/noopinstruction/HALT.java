package controlunit.instruction.noopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import enums.Flag;

public final class HALT extends NoOpInstruction {
	public HALT(InstructionData inst) { super(inst); }
	
	public void run(Architecture arc) {
		arc.setFlag(Flag.HALT, true);
	}
}

