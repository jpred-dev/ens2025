package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import enums.Flag;

public final class BNV extends OneOpInstruction {
	
	public BNV(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getJumpValue(op);
		if (arc.getFlag(Flag.OVERFLOW) == false)
			arc.setProgramCounter(value);
	}
}
