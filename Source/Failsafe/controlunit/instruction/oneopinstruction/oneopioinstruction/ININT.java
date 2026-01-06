package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import customexception.CustomException;
import data.RData;
import data.RWData;

public class ININT extends OneOpIOInstruction {

	public ININT(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value;
		
		try {
			value = io.readData();
		} catch (CustomException ex) {
			value = new RWData(0);
		}

		arc.setValue(op, value);
	}

}
