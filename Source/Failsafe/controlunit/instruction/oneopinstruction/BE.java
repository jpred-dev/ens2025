package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import enums.Flag;

public final class BE extends OneOpInstruction {
	
	public BE(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getJumpValue(op);
		if (arc.getFlag(Flag.PARITY) == false)
			arc.setProgramCounter(value);
	}
}
