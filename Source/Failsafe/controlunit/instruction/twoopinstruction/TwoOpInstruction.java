package controlunit.instruction.twoopinstruction;

import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;
import controlunit.instruction.Operand;

public abstract class TwoOpInstruction extends Instruction {
	
	protected Operand op1, op2;
	
	public TwoOpInstruction(InstructionData inst) {
		super(inst);
		op1 = inst.getOp(0);
		op2 = inst.getOp(1);
	}
}
