package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class WRCHAR extends OneOpIOInstruction {

	public WRCHAR(InstructionData inst) {
		super(inst);
	}
	
	@Override
	public void run(Architecture arc) {
		RData value = arc.getValue(op);
		char c = value.getChar();
		
		io.printChar(c);
	}

}
