package controlunit.assembler.memorywriter;

import java.util.List;
import java.util.Map;

import controlunit.architecture.memory.Memory;
import controlunit.assembler.util.InstructionBlueprint;
import controlunit.assembler.util.Reference;
import data.RData;
import settings.Settings;

public class MemoryWriter {
	private MemoryPointer mp;
	private Writer writer;
	private Encoder encoder;
	
	public MemoryWriter(Settings set) {
		mp = new MemoryPointer();
		encoder = new Encoder(mp);
		writer = new Writer(mp, set);
	}
	
	public void reset() {
		mp.resetPointer();
		writer.newMemory();
	}
	
	public void resetPointer() {
		mp.resetPointer();
	}
	
	public MemoryPointer getPointer() {
		return mp;
	}
	
	public void setPointer(RData ind) {
		mp.setPointer(ind);
	}
	
	public void resetPointerOverflow() {
		mp.resetOverflow();
	}
	
	public RData getPointerData() {
		return mp.getPointer();
	}
	
	public void encodeAndWrite(InstructionBlueprint ib, List<Reference> refList) {
		EncodedInstruction ei = encoder.encodeInstruction(ib, refList);
		writer.writeInstruction(ei);
	}
	
	public void writeData(RData data) {
		writer.writeData(data.getImmutableCopy());
	}
	
	public void setReservedMemory(RData data) {
		writer.setReservedMemory(data);
	}
	
	public void commitZoneBoundaries() {
		writer.commitZoneBoundaries();
	}
	
	public Memory getMemory() {
		return writer.getMemory();
	}
	
	public void writeReferences(List<Reference> refList, Map<String, RData> labelMap) {
		writer.writeReferences(refList, labelMap);
	}
	
	public void close() {
		mp.resetPointer();
		writer.close();
	}
}
