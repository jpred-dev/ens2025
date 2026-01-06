package controlunit.instruction.oneopinstruction;

import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;
import controlunit.instruction.Operand;

public abstract class OneOpInstruction extends Instruction {
	protected Operand op;
	
	public OneOpInstruction(InstructionData inst) {
		super(inst);
		this.op = inst.getOp(0);
	}
}
