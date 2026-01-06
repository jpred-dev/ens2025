package controlunit.architecture.memory;

import data.RData;
import data.RWData;

public class Memory {
	private RWData[] mem;
	
	public Memory() {
		mem = new RWData[RWData.MAX_UNSIGNED_VALUE+1];
		
		for (int i = 0; i <= RWData.MAX_UNSIGNED_VALUE; i++) {
			mem[i] = new RWData();
		}
	}
	
	public RData fetchAt(RData index) {
		return mem[index.getValue()].getImmutableCopy();
	}
	
	public RData fetchAt(int index) {
		return mem[index].getImmutableCopy();
	}
	
	public void writeAt(RData index, RData value) {
		mem[index.getValue()].assign(value);
	}
	
	public void writeAt(int index, RData value) {
		mem[index].assign(value);
	}

	public void wipe() {
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			mem[i].assign(0);
		}
	}
	
	public void loadMemory(Memory newMem) {
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			mem[i].assign(newMem.fetchAt(i));
		}
	}
	
	public Memory dumpMemory() {
		Memory dump = new Memory();
		
		dump.loadMemory(this);
		
		return dump;
	}
}
