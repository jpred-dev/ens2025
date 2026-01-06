package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;

public final class INCHAR extends OneOpIOInstruction {

	public INCHAR(InstructionData inst) {
		super(inst);
	}
	
	@Override
	public void run(Architecture arc) {
		char c = io.readChar();
		RData value = new RWData(c);

		arc.setValue(op, value);
	}
}
