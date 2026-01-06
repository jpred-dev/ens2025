package tests;

import java.util.ArrayList;
import java.util.List;

import controlunit.architecture.Architecture;
import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;
import controlunit.instruction.Operand;
import controlunit.instruction.oneopinstruction.DEC;
import controlunit.instruction.oneopinstruction.INC;
import controlunit.instruction.oneopinstruction.NEG;
import controlunit.instruction.twoopinstruction.ADD;
import controlunit.instruction.twoopinstruction.DIV;
import controlunit.instruction.twoopinstruction.MOD;
import controlunit.instruction.twoopinstruction.MUL;
import controlunit.instruction.twoopinstruction.SUB;
import data.RData;
import data.RWData;
import ens2025.console.CLInterface;
import enums.AddressingMode;
import enums.Flag;
import enums.RegisterID;
import settings.Settings;

public class DataTests {
	public static void main(String[] args) {
		Settings set = new Settings();
		CLInterface io = new CLInterface(set);
		Architecture arc = new Architecture(set);
		RWData d1 = new RWData(0);
		RWData d2 = new RWData(0);
		List<Operand> li = new ArrayList<>();
		InstructionData id;
		Operand op1, op2;
		
		for (int i=RWData.MIN_VALUE; i<=RWData.MAX_VALUE; i++) {
			for (int j=RWData.MIN_VALUE; j<=RWData.MAX_VALUE; j++) {
				d1.assign(i);
				d2.assign(j);
				op1 = new Operand(AddressingMode.LITERAL, d1, set);
				op2 = new Operand(AddressingMode.LITERAL, d2, set);
				li.add(op1);
				li.add(op2);
				id = new InstructionData(li, io);
				
				checkADD(d1, d2, id, arc);
				checkSUB(d1, d2, id, arc);
				checkMUL(d1, d2, id, arc);
				
				if (j != 0) {
					checkDIV(d1, d2, id, arc);
					checkMOD(d1, d2, id, arc);
				}
				checkINC(d1, d2, id, arc);
				checkDEC(d1, d2, id, arc);
				checkNEG(d1, d2, id, arc);
				
				li.clear();
			}
			if (i%500 == 0) {
				System.out.println(i);
			}
		}
		
		System.out.println("Comprobación exitosa.");
	}
	
	private static void checkADD(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		String instName = "ADD";
		Long signedResult = Long.valueOf(d1.getValue()) + Long.valueOf(d2.getValue());
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue() + d2.getValue());
		Instruction inst = new ADD(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkSUB(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		String instName = "SUB";
		Long signedResult = Long.valueOf(d1.getValue()) - Long.valueOf(d2.getValue());
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue() - d2.getValue());
		Instruction inst = new SUB(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkMUL(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		String instName = "MUL";
		Long signedResult = Long.valueOf(d1.getValue()) * Long.valueOf(d2.getValue());
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue() * d2.getValue());
		Instruction inst = new MUL(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkDIV(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		String instName = "DIV";
		Long signedResult = Long.valueOf(d1.getValue()) / Long.valueOf(d2.getValue());
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue() / d2.getValue());
		Instruction inst = new DIV(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkMOD(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		String instName = "MOD";
		Long signedResult = Long.valueOf(d1.getValue()) % Long.valueOf(d2.getValue());
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue() % d2.getValue());
		Instruction inst = new MOD(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkINC(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		
		String instName = "INC";
		Long signedResult = Long.valueOf(d1.getValue()) + 1L;
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue()) + 1L;
		Instruction inst = new INC(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkDEC(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		
		String instName = "DEC";
		Long signedResult = Long.valueOf(d1.getValue()) - 1L;
		Long unsignedResult = Integer.toUnsignedLong(d1.getValue()) - 1L;
		Instruction inst = new DEC(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
	
	private static void checkNEG(RData d1, RData d2, InstructionData id, Architecture arc) {
		int mask = 0xFFFF;
		int i = d1.getValue();
		int j = d2.getValue();
		
		String instName = "NEG";
		Long signedResult = 0L - Long.valueOf(d1.getValue());
		Long unsignedResult = 0L - Integer.toUnsignedLong(d1.getValue());
		Instruction inst = new NEG(id);
		
		
		inst.run(arc);
		assert (unsignedResult > RWData.MAX_UNSIGNED_VALUE) == (arc.getFlag(Flag.CARRY)) : ("inst=" + instName + "i=" + i + " j=" + j + " CARRY ERROR");
		assert (signedResult < RWData.MIN_VALUE || signedResult > RWData.MAX_VALUE) == (arc.getFlag(Flag.OVERFLOW)) : ("inst=" + instName + "i=" + i + " j=" + j + " OVERFLOW ERROR");
		assert (signedResult == 0) == (arc.getFlag(Flag.ZERO)) : ("inst=" + instName + "i=" + i + " j=" + j + " ZERO ERROR");
		assert (signedResult < 0) == (arc.getFlag(Flag.SIGN)) : ("inst=" + instName + "i=" + i + " j=" + j + " SIGN ERROR");
		
		signedResult &= mask;
		assert (Long.bitCount(signedResult) % 2 == 1) == (arc.getFlag(Flag.PARITY)) : ("inst=" + instName + "i=" + i + " j=" + j + " PARITY ERROR");
		assert (arc.readRegister(RegisterID.A).isEqual(signedResult)) : ("inst=" + instName + "i=" + i + " j=" + j + " VALUE ERROR");
	}
}
