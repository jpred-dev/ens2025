package controlunit.codex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import customexception.InstructionNotImplementedException;
import enums.AddressingMode;
import enums.InstructionType;

public class VerificationCodex {
	
	public static void validateInstruction(InstructionType it, AddressingMode addr1, AddressingMode addr2) {
		if (FIRST_OP_MAP.get(it).contains(addr1) == false)
			throw new InstructionNotImplementedException("La instrucción " + it.toString() + " no es compatible con el modo de direccionamiento " + addr1.toString() + " en el primer operando.");

		if (SECOND_OP_MAP.get(it).contains(addr2) == false)
			throw new InstructionNotImplementedException("La instrucción " + it.toString() + " no es compatible con el modo de direccionamiento " + addr2.toString() + " en el segundo operando.");
	}

	public static boolean isFullWord(AddressingMode addr) {
		return FULL_WORD_ADDRESSING.contains(addr);
	}
	
	//Conjunto de direccionamientos que ocupan una palabra entera como argumento
	private static final Set<AddressingMode> FULL_WORD_ADDRESSING = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.MEMORY));
	
	private static final Map<InstructionType, Set<AddressingMode>> FIRST_OP_MAP;
	private static final Map<InstructionType, Set<AddressingMode>> SECOND_OP_MAP;

	//Conjuntos de direccionamientos permitidos para cada instrucción en su primer operando
	private static final Set<AddressingMode> NOP_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> HALT_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> MOVE_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> PUSH_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> POP_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> ADD_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> SUB_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> MUL_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> DIV_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> MOD_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> INC_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> DEC_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> NEG_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> CMP_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> AND_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> OR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> XOR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> NOT_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> BR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BZ_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BNZ_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BP_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BN_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BV_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BNV_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BC_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BNC_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BE_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> BO_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	
	private static final Set<AddressingMode> CALL_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_PC));
	private static final Set<AddressingMode> RET_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> INCHAR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> ININT_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> INSTR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> WRCHAR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> WRINT_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> WRSTR_FIRST_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));

	//Conjuntos de direccionamientos permitidos para cada instrucción en su segundo operando
	private static final Set<AddressingMode> NOP_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> HALT_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> MOVE_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> PUSH_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> POP_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> ADD_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> SUB_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> MUL_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> DIV_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> MOD_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> INC_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> DEC_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> NEG_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> CMP_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	
	private static final Set<AddressingMode> AND_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> OR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> XOR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.LITERAL, AddressingMode.REGISTER, AddressingMode.MEMORY, AddressingMode.INDIRECT, AddressingMode.RELATIVE_IX, AddressingMode.RELATIVE_IY));
	private static final Set<AddressingMode> NOT_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> BR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BZ_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BNZ_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BP_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BN_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BV_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BNV_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BC_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BNC_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BE_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> BO_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> CALL_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> RET_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	private static final Set<AddressingMode> INCHAR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> ININT_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> INSTR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> WRCHAR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> WRINT_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	private static final Set<AddressingMode> WRSTR_SECOND_OP_SET = new HashSet<>(Arrays.asList(AddressingMode.NONE));
	
	//Inicialización del mapa de primer operando
	static {
		Map<InstructionType, Set<AddressingMode>> tempMap = new HashMap<>();
		tempMap.put(InstructionType.NOP, NOP_FIRST_OP_SET);
		tempMap.put(InstructionType.HALT, HALT_FIRST_OP_SET);
		tempMap.put(InstructionType.MOVE, MOVE_FIRST_OP_SET);
		tempMap.put(InstructionType.PUSH, PUSH_FIRST_OP_SET);
		tempMap.put(InstructionType.POP, POP_FIRST_OP_SET);
		tempMap.put(InstructionType.ADD, ADD_FIRST_OP_SET);
		tempMap.put(InstructionType.SUB, SUB_FIRST_OP_SET);
		tempMap.put(InstructionType.MUL, MUL_FIRST_OP_SET);
		tempMap.put(InstructionType.DIV, DIV_FIRST_OP_SET);
		tempMap.put(InstructionType.MOD, MOD_FIRST_OP_SET);
		tempMap.put(InstructionType.INC, INC_FIRST_OP_SET);
		tempMap.put(InstructionType.DEC, DEC_FIRST_OP_SET);
		tempMap.put(InstructionType.NEG, NEG_FIRST_OP_SET);
		tempMap.put(InstructionType.CMP, CMP_FIRST_OP_SET);
		tempMap.put(InstructionType.AND, AND_FIRST_OP_SET);
		tempMap.put(InstructionType.OR, OR_FIRST_OP_SET);
		tempMap.put(InstructionType.XOR, XOR_FIRST_OP_SET);
		tempMap.put(InstructionType.NOT, NOT_FIRST_OP_SET);
		tempMap.put(InstructionType.BR, BR_FIRST_OP_SET);
		tempMap.put(InstructionType.BZ, BZ_FIRST_OP_SET);
		tempMap.put(InstructionType.BNZ, BNZ_FIRST_OP_SET);
		tempMap.put(InstructionType.BP, BP_FIRST_OP_SET);
		tempMap.put(InstructionType.BN, BN_FIRST_OP_SET);
		tempMap.put(InstructionType.BV, BV_FIRST_OP_SET);
		tempMap.put(InstructionType.BNV, BNV_FIRST_OP_SET);
		tempMap.put(InstructionType.BC, BC_FIRST_OP_SET);
		tempMap.put(InstructionType.BNC, BNC_FIRST_OP_SET);
		tempMap.put(InstructionType.BE, BE_FIRST_OP_SET);
		tempMap.put(InstructionType.BO, BO_FIRST_OP_SET);
		tempMap.put(InstructionType.CALL, CALL_FIRST_OP_SET);
		tempMap.put(InstructionType.RET, RET_FIRST_OP_SET);
		tempMap.put(InstructionType.INCHAR, INCHAR_FIRST_OP_SET);
		tempMap.put(InstructionType.ININT, ININT_FIRST_OP_SET);
		tempMap.put(InstructionType.INSTR, INSTR_FIRST_OP_SET);
		tempMap.put(InstructionType.WRCHAR, WRCHAR_FIRST_OP_SET);
		tempMap.put(InstructionType.WRINT, WRINT_FIRST_OP_SET);
		tempMap.put(InstructionType.WRSTR, WRSTR_FIRST_OP_SET);
		FIRST_OP_MAP = Collections.unmodifiableMap(tempMap);
	}
	
	//Inicialización del mapa de segundo operando
	static {
		Map<InstructionType, Set<AddressingMode>> tempMap2 = new HashMap<>();
		tempMap2.put(InstructionType.NOP, NOP_SECOND_OP_SET);
		tempMap2.put(InstructionType.HALT, HALT_SECOND_OP_SET);
		tempMap2.put(InstructionType.MOVE, MOVE_SECOND_OP_SET);
		tempMap2.put(InstructionType.PUSH, PUSH_SECOND_OP_SET);
		tempMap2.put(InstructionType.POP, POP_SECOND_OP_SET);
		tempMap2.put(InstructionType.ADD, ADD_SECOND_OP_SET);
		tempMap2.put(InstructionType.SUB, SUB_SECOND_OP_SET);
		tempMap2.put(InstructionType.MUL, MUL_SECOND_OP_SET);
		tempMap2.put(InstructionType.DIV, DIV_SECOND_OP_SET);
		tempMap2.put(InstructionType.MOD, MOD_SECOND_OP_SET);
		tempMap2.put(InstructionType.INC, INC_SECOND_OP_SET);
		tempMap2.put(InstructionType.DEC, DEC_SECOND_OP_SET);
		tempMap2.put(InstructionType.NEG, NEG_SECOND_OP_SET);
		tempMap2.put(InstructionType.CMP, CMP_SECOND_OP_SET);
		tempMap2.put(InstructionType.AND, AND_SECOND_OP_SET);
		tempMap2.put(InstructionType.OR, OR_SECOND_OP_SET);
		tempMap2.put(InstructionType.XOR, XOR_SECOND_OP_SET);
		tempMap2.put(InstructionType.NOT, NOT_SECOND_OP_SET);
		tempMap2.put(InstructionType.BR, BR_SECOND_OP_SET);
		tempMap2.put(InstructionType.BZ, BZ_SECOND_OP_SET);
		tempMap2.put(InstructionType.BNZ, BNZ_SECOND_OP_SET);
		tempMap2.put(InstructionType.BP, BP_SECOND_OP_SET);
		tempMap2.put(InstructionType.BN, BN_SECOND_OP_SET);
		tempMap2.put(InstructionType.BV, BV_SECOND_OP_SET);
		tempMap2.put(InstructionType.BNV, BNV_SECOND_OP_SET);
		tempMap2.put(InstructionType.BC, BC_SECOND_OP_SET);
		tempMap2.put(InstructionType.BNC, BNC_SECOND_OP_SET);
		tempMap2.put(InstructionType.BE, BE_SECOND_OP_SET);
		tempMap2.put(InstructionType.BO, BO_SECOND_OP_SET);
		tempMap2.put(InstructionType.CALL, CALL_SECOND_OP_SET);
		tempMap2.put(InstructionType.RET, RET_SECOND_OP_SET);
		tempMap2.put(InstructionType.INCHAR, INCHAR_SECOND_OP_SET);
		tempMap2.put(InstructionType.ININT, ININT_SECOND_OP_SET);
		tempMap2.put(InstructionType.INSTR, INSTR_SECOND_OP_SET);
		tempMap2.put(InstructionType.WRCHAR, WRCHAR_SECOND_OP_SET);
		tempMap2.put(InstructionType.WRINT, WRINT_SECOND_OP_SET);
		tempMap2.put(InstructionType.WRSTR, WRSTR_SECOND_OP_SET);
		SECOND_OP_MAP = Collections.unmodifiableMap(tempMap2);
	}
	

}
