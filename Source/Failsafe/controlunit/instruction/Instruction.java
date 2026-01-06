package controlunit.instruction;

import controlunit.architecture.Architecture;
import data.RData;
import enums.Flag;

public abstract class Instruction {
	public Instruction(InstructionData inst) {}
	public abstract void run(Architecture arc);
	
	public static void checkParity(RData d, Architecture arc) {
		if (d.bitCount() % 2 == 1)
			arc.setFlag(Flag.PARITY, true);
		else
			arc.setFlag(Flag.PARITY, false);
	}
	
	public static void checkSign(RData d, Architecture arc) {
		if (d.isPositiveOrZero())
			arc.setFlag(Flag.SIGN, false);
		else
			arc.setFlag(Flag.SIGN, true);
	}
	
	public static void checkZero(RData d, Architecture arc) {
		if (d.isEqual(0))
			arc.setFlag(Flag.ZERO, true);
		else
			arc.setFlag(Flag.ZERO, false);
	}
}
