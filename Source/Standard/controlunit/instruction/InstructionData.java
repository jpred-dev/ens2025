package controlunit.instruction;

import java.util.List;

import io.IOInterface;

public class InstructionData {
	private List<Operand> opList;
	private IOInterface io;
	
	//Constructor genérico.
	public InstructionData(List<Operand> opList, IOInterface io) {
		this.opList = opList;
		this.io = io;
	}
	
	public Operand getOp(int index) {
		if (opList == null || opList.size() < index + 1)
			return null;
		return opList.get(index);
	}
	
	public IOInterface getIO() {
		return io;
	}
}
