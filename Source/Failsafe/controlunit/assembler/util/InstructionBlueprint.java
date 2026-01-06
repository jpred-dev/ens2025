package controlunit.assembler.util;

import java.util.List;

import enums.InstructionType;

public class InstructionBlueprint {
	private InstructionType it;
	private List<OperandBlueprint> opList;
	
	public InstructionBlueprint(InstructionType it, List<OperandBlueprint> opList) {
		this.it = it;
		this.opList = opList;
	}
	
	public InstructionType getInstructionType() {
		return it;
	}

	public List<OperandBlueprint> getOps() {
		return opList;
	}
}
