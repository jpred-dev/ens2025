package controlunit.assembler.util;

import data.RData;

public class Reference {
	private RData memPos;
	private String label;
	private boolean isRelativeToPC;
	private int opSegment;
	
	public Reference(RData memPos, String label, boolean isRelativeToPC) {
		this.memPos = memPos;
		this.label = label;
		this.isRelativeToPC = isRelativeToPC;
		this.opSegment = -1;
	}
	
	public Reference(RData memPos, String label, boolean isRelativeToPC, int opSegment) {
		this.memPos = memPos;
		this.label = label;
		this.isRelativeToPC = isRelativeToPC;
		this.opSegment = opSegment;
	}
	
	public String getLabel() {
		return label;
	}
	
	public RData getPointer() {
		return memPos;
	}
	
	public boolean isRelativeToPC() {
		return isRelativeToPC;
	}
	
	public int getOpSegment() {
		return opSegment;
	}
}
