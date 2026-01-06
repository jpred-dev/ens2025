package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class MOVE extends TwoOpInstruction {

	public MOVE(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getValue(op1);
		
		arc.setValue(op2, value);
	}
}
