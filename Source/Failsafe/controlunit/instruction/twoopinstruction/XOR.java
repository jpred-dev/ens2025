package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;

public final class XOR extends TwoOpInstruction {

	public XOR(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RWData result = arc.getValue(op1).getMutableCopy();
		RData value = arc.getValue(op2);
		
		result.bitXor(value);
		arc.writeAccumulator(result);
	}
}
