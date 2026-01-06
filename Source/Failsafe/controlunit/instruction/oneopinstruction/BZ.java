package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import enums.Flag;

public final class BZ extends OneOpInstruction {
	public BZ(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getJumpValue(op);
		if (arc.getFlag(Flag.ZERO) == true)
			arc.setProgramCounter(value);
	}
}
