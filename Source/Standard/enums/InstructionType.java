package enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import controlunit.instruction.Instruction;
import controlunit.instruction.InstructionData;
import controlunit.instruction.noopinstruction.HALT;
import controlunit.instruction.noopinstruction.NOP;
import controlunit.instruction.noopinstruction.RET;
import controlunit.instruction.oneopinstruction.BC;
import controlunit.instruction.oneopinstruction.BE;
import controlunit.instruction.oneopinstruction.BN;
import controlunit.instruction.oneopinstruction.BNC;
import controlunit.instruction.oneopinstruction.BNV;
import controlunit.instruction.oneopinstruction.BNZ;
import controlunit.instruction.oneopinstruction.BO;
import controlunit.instruction.oneopinstruction.BP;
import controlunit.instruction.oneopinstruction.BR;
import controlunit.instruction.oneopinstruction.BV;
import controlunit.instruction.oneopinstruction.BZ;
import controlunit.instruction.oneopinstruction.CALL;
import controlunit.instruction.oneopinstruction.DEC;
import controlunit.instruction.oneopinstruction.INC;
import controlunit.instruction.oneopinstruction.NEG;
import controlunit.instruction.oneopinstruction.NOT;
import controlunit.instruction.oneopinstruction.POP;
import controlunit.instruction.oneopinstruction.PUSH;
import controlunit.instruction.oneopinstruction.oneopioinstruction.INCHAR;
import controlunit.instruction.oneopinstruction.oneopioinstruction.ININT;
import controlunit.instruction.oneopinstruction.oneopioinstruction.INSTR;
import controlunit.instruction.oneopinstruction.oneopioinstruction.WRCHAR;
import controlunit.instruction.oneopinstruction.oneopioinstruction.WRINT;
import controlunit.instruction.oneopinstruction.oneopioinstruction.WRSTR;
import controlunit.instruction.twoopinstruction.ADD;
import controlunit.instruction.twoopinstruction.AND;
import controlunit.instruction.twoopinstruction.CMP;
import controlunit.instruction.twoopinstruction.DIV;
import controlunit.instruction.twoopinstruction.MOD;
import controlunit.instruction.twoopinstruction.MOVE;
import controlunit.instruction.twoopinstruction.MUL;
import controlunit.instruction.twoopinstruction.OR;
import controlunit.instruction.twoopinstruction.SUB;
import controlunit.instruction.twoopinstruction.XOR;

public enum InstructionType {
	NOP("NOP", 0, NOP::new),
	HALT("HALT", 1, HALT::new),
	MOVE("MOVE", 2, MOVE::new),
	PUSH("PUSH", 3, PUSH::new),
	POP("POP", 4, POP::new),
	ADD("ADD", 5, ADD::new),
	SUB("SUB", 6, SUB::new),
	MUL("MUL", 7, MUL::new),
	DIV("DIV", 8, DIV::new),
	MOD("MOD", 9, MOD::new),
	INC("INC", 10, INC::new),
	DEC("DEC", 11, DEC::new),
	NEG("NEG", 12, NEG::new),
	CMP("CMP", 13, CMP::new),
	AND("AND", 14, AND::new),
	OR("OR", 15, OR::new),
	XOR("XOR", 16, XOR::new),
	NOT("NOT", 17, NOT::new),
	BR("BR", 18, BR::new),
	BZ("BZ", 19, BZ::new),
	BNZ("BNZ", 20, BNZ::new),
	BP("BP", 21, BP::new),
	BN("BN", 22, BN::new),
	BV("BV", 23, BV::new),
	BNV("BNV", 24, BNV::new),
	BC("BC", 25, BC::new),
	BNC("BNC", 26, BNC::new),
	BE("BE", 27, BE::new),
	BO("BO", 28, BO::new),
	CALL("CALL", 29, CALL::new),
	RET("RET", 30, RET::new),
	INCHAR("INCHAR", 31, INCHAR::new),
	ININT("ININT", 32, ININT::new),
	INSTR("INSTR", 33, INSTR::new),
	WRCHAR("WRCHAR", 34, WRCHAR::new),
	WRINT("WRINT", 35, WRINT::new),
	WRSTR("WRSTR", 36, WRSTR::new);
	
	InstructionType(String name, int id, Function<InstructionData, ? extends Instruction> constructor) {
		this.name = name;
		this.id = id;
		this.constructor = constructor;
	}
	
	//Datos básicos asociados al tipo enumerado
	private String name;
	private int id;
	private Function<InstructionData, ? extends Instruction> constructor;
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public Function<InstructionData, ? extends Instruction> getConstructor() {
		return constructor;
	}
	
	//SUBCONJUNTOS Y MÉTODOS DE DETERMINACIÓN DE PERTENENCIA
	//Instrucciones sin operandos
	private static final Set<InstructionType> NO_OP_SUBSET = new HashSet<>(Arrays.asList(NOP, HALT, RET));
	public static final boolean isNoOpInstruction(InstructionType it) {
		return NO_OP_SUBSET.contains(it);
	}
	
	//Instrucciones con un operando
	private static final Set<InstructionType> ONE_OP_SUBSET = new HashSet<>(Arrays.asList(PUSH, POP, INC, DEC, NEG, NOT, BR, BZ, BNZ, BP, BN, BV, BNV, BC, BNC, BE, BO, CALL));
	public static final boolean isOneOpInstruction(InstructionType it) {
		return ONE_OP_SUBSET.contains(it);
	}
	
	//Instrucciones E/S con un operando
	private static final Set<InstructionType> ONE_OP_IO_SUBSET = new HashSet<>(Arrays.asList(INCHAR, ININT, INSTR, WRCHAR, WRINT, WRSTR));
	public static final boolean isOneOpIOInstruction(InstructionType it) {
		return ONE_OP_IO_SUBSET.contains(it);
	}
	
	//Instrucciones con dos operandos
	private static final Set<InstructionType> TWO_OP_SUBSET	= new HashSet<>(Arrays.asList(MOVE, ADD, SUB, MUL, DIV, MOD, CMP, AND, OR, XOR));
	public static final boolean isTwoOpInstruction(InstructionType it) {
		return TWO_OP_SUBSET.contains(it);
	}
	
	//Mapa y método para obtener el tipo en base al valor numérico asociado.
	private static final Map<Integer, InstructionType> ID_MAP;
	static {
		Map<Integer, InstructionType> temp = new HashMap<>();
		for(InstructionType i : InstructionType.values())
			temp.put(i.id, i);
		ID_MAP = Collections.unmodifiableMap(temp);
	}
	public static final InstructionType getFromId(int id) {
		return ID_MAP.get(id);
	}
	
	//Mapa y método para obtener el tipo en base al nombre asociado.
	private static final Map<String, InstructionType> NAME_MAP;
	static {
		Map<String, InstructionType> temp2 = new HashMap<>();
		for(InstructionType i : InstructionType.values())
			temp2.put(i.name, i);
		NAME_MAP = Collections.unmodifiableMap(temp2);
	}
	public static final InstructionType getFromName(String name) {
		return NAME_MAP.get(name);
	}
	
}
