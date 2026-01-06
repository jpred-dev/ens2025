package controlunit.codex;

import controlunit.instruction.Operand;
import customexception.InstructionArchitectureException;
import data.RData;
import data.RWData;
import enums.AddressingMode;
import enums.InstructionType;
import enums.RegisterID;
import settings.Settings;

public class ISACodex {
	//CONSTANTES DEDICADAS A OBTENER EL CÓDIGO DE INSTRUCCIÓN:
	private static final RData INSTRUCTION_TYPE_MASK = new RWData(0xFFC0); //Máscara de aislamiento de bits de código de instrucción.
	public static final RData INSTRUCTION_CODE_SHIFT = new RWData(6); //Desplazamiento necesario para alinear el LSB del código con el LSB de la palabra.
	
	//CONSTANTES DEDICADAS A OBTENER EL TIPO DE DIRECCIONAMIENTO DE CADA OPERANDO.
	private static final RData FIRST_ADDRESS_MASK = new RWData(0x0038); //Máscara de aislamiento de bits del primer modo de direccionamiento.
	private static final RData SECOND_ADDRESS_MASK = new RWData(0x0007); //Máscara de aislamiento de bits del segundo modo de direccionamiento.
	public static final RData FIRST_ADDRESS_CODE_SHIFT = new RWData(3); //Desplazamiento necesario para alinear el LSB del código de direccionamiento con el LSB de la palabra.
	public static final RData SECOND_ADDRESS_CODE_SHIFT = new RWData(0);
	
	//CONSTANTES DEDICADAS A OBTENER EL VALOR DE UN OPERANDO.
	public static final RData FIRST_OPERAND_MASK = new RWData(0xFF00);
	public static final RData SECOND_OPERAND_MASK = new RWData(0x00FF);
	public static final RData FIRST_OPERAND_SHIFT = new RWData(RWData.HALF_SIZE);
	public static final RData SECOND_OPERAND_SHIFT = new RWData(0);
	
	//CONSTANTES DE RESTRICCIÓN DE TAMAÑO.
	private static final RData RELATIVE_MAX_UNSIGNED_VALUE = new RWData(0xFF);
	private static final RData RELATIVE_MIN_VALUE = new RWData(0xFF80);
	
	
	public static InstructionType getInstructionType(RData word) {
		RWData temp = word.getMutableCopy();
		InstructionType it;

		//Aplicación de la máscara para aislar el código de instrucción y desplazamiento para alinear su LSB con el de la palabra.
		temp.bitAnd(INSTRUCTION_TYPE_MASK);
		temp.urShift(INSTRUCTION_CODE_SHIFT);
		
		it = InstructionType.getFromId(temp.getValue());
		if (it == null)
			throw new InstructionArchitectureException("Código de instrucción no válido.");
		
		return it;
	}
	
	public static AddressingMode getFirstAddressingMode(RData word) {
		return getAddressingMode(word, FIRST_ADDRESS_MASK, FIRST_ADDRESS_CODE_SHIFT);
	}
	
	public static AddressingMode getSecondAddressingMode(RData word) {
		return getAddressingMode(word, SECOND_ADDRESS_MASK, SECOND_ADDRESS_CODE_SHIFT);
	}
	
	private static AddressingMode getAddressingMode(RData word, RData mask, RData shift) {
		RWData w = word.getMutableCopy();
		AddressingMode addr;
		
		//Aplicación de la máscara para aislar el direccionamiento y desplazamiento para alinear su LSB con el de la palabra.
		w.bitAnd(mask);
		w.urShift(shift);
		
		addr = AddressingMode.getFromId(w.getValue());
		if (addr == null)
			throw new InstructionArchitectureException("Código de direccionamiento no válido.");
		
		return addr;
	}
	
	public static Operand getFirstOperand(RData word, AddressingMode addr, Settings set) {
		return getOperand(word, FIRST_OPERAND_MASK, FIRST_OPERAND_SHIFT, addr, set);
	}
	
	public static Operand getSecondOperand(RData word, AddressingMode addr, Settings set) {
		return getOperand(word, SECOND_OPERAND_MASK, SECOND_OPERAND_SHIFT, addr, set);
	}
	
	private static Operand getOperand(RData word, RData mask, RData shift, AddressingMode addr, Settings set) {
		Operand op;
		RWData w = word.getMutableCopy();
		RegisterID reg;

		switch(addr) {
			//En caso de direccionamiento inmediato o directo a memoria, el valor del operando es la palabra completa.
			case LITERAL:
			case MEMORY:
				op = new Operand(addr, word, set);
				break;
				
			//En caso de direccionamiento directo a registro o indirecto, el valor del operando es un identificador de registro obtendo de una parte de la palabra.
			case REGISTER:
			case INDIRECT:
				w.bitAnd(mask); //Se aplica la máscara para dejar solamente la parte relevante.
				w.urShift(shift); //Se desplaza hacia la derecha para alinear su LSB con el de la palabra.
				reg = RegisterID.getFromId(w.getValue());
				if (reg == null) 
					throw new InstructionArchitectureException("Código de registro no válido.");
				op = new Operand(addr, reg, set);
				break;
			
			//En caso de direccionamiento relativo a índice o a PC, un fragmento de la palabra es el desplazamiento que se aplica al PC.
			//Según el modo de direccionamiento, el valor del registro será distinto.
			case RELATIVE_IX:
				w.bitAnd(mask);
				w.urShift(shift);
				op = new Operand(addr, RegisterID.IX, w, set);
				break;
			case RELATIVE_IY:
				w.bitAnd(mask);
				w.urShift(shift);
				op = new Operand(addr, RegisterID.IY, w, set);
				break;
			case RELATIVE_PC:
				w.bitAnd(mask);
				w.urShift(shift);
				op = new Operand(addr, RegisterID.PC, w, set);
				break;

			//Nunca se debería llegar aquí.
			default:
				op = null;
				break;
		}
		
		return op;
	}
	
	public static boolean isOffsetValid(RData value) {
		if ((value.isPositiveOrZero() && value.isGreater(RELATIVE_MAX_UNSIGNED_VALUE)) || (value.isNegative() && value.isLess(RELATIVE_MIN_VALUE)))
			return false;
		return true;
	}
}
