package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.instruction.InstructionData;
import controlunit.instruction.oneopinstruction.OneOpInstruction;
import io.IOInterface;

public abstract class OneOpIOInstruction extends OneOpInstruction {
	
	protected IOInterface io;

	public OneOpIOInstruction(InstructionData inst) {
		super(inst);
		this.io = inst.getIO();
	}
}
