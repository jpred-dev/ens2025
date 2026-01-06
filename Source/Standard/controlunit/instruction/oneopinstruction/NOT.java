package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RWData;

public final class NOT extends OneOpInstruction {

	public NOT(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RWData value = arc.getValue(op).getMutableCopy();
		
		value.bitInvert();
		
		arc.setValue(op, value);
	}
}
