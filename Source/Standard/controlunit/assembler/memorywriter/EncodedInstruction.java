package controlunit.assembler.memorywriter;

import java.util.ArrayList;
import java.util.List;

import data.RData;

public class EncodedInstruction {
	private List<RData> memBlock;
	
	public EncodedInstruction () {
		memBlock = new ArrayList<>();
	}
	
	public void addWord(RData word) {
		memBlock.add(word.getMutableCopy());
	}
	
	public int getBlockSize() {
		return memBlock.size();
	}
	
	public RData getWord(int index) {
		return memBlock.get(index);
	}
}
