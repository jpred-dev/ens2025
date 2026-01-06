package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public class WRINT extends OneOpIOInstruction {

	public WRINT(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getValue(op);
		
		io.printData(value);
	}

}
