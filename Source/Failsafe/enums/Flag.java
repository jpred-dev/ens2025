package enums;

import data.RData;
import data.RWData;

public enum Flag {
	ZERO(0b00_000001, "Z"),
	CARRY(0b00_000010, "C"),
	OVERFLOW(0b00_000100, "V"),
	PARITY(0b00_001000, "P"),
	SIGN(0b00_010000, "S"),
	HALT(0b00_100000, "H");
	
	private Flag(Number i, String name) {
		mask = new RWData(i);
		this.name = name;
	}
	
	private String name;
	private RData mask;
	
	public RData getMask() {
		return mask.getImmutableCopy();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
