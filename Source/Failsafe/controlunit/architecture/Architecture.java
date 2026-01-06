package controlunit.architecture;

import controlunit.architecture.breakpointmanager.BreakpointManager;
import controlunit.architecture.memory.Memory;
import controlunit.architecture.registerbank.RegisterBank;
import controlunit.instruction.Operand;
import customexception.InvadingPointerException;
import customexception.MemoryIndexOutOfBoundsException;
import customexception.StackException;
import data.RData;
import data.RWData;
import enums.Flag;
import enums.RegisterID;
import settings.Settings;
import settings.StackGrowth;

public class Architecture {
	
	private RegisterBank rb;
	private Memory mem;
	private BreakpointManager bpm;
	private Settings set;
	
	public Architecture(Settings set) {
		this.set = set;
		rb = new RegisterBank();
		writeRegister(RegisterID.SP, set.getStackStart());
		mem = new Memory();
		bpm = new BreakpointManager();
	}
	
	public boolean hasBreakpoint(RData index) {
		return bpm.hasBreakpoint(index);
	}
	
	public void setBreakpoint(RData index, boolean value) {
		bpm.setBreakpoint(index, value);
	}
	
	public void resetRegisterBank() {
		RWData zero = new RWData(0);
		
		for (RegisterID reg : RegisterID.values()) {
			if (reg != RegisterID.SP)
				writeRegister(reg, zero);
		}

		writeRegister(RegisterID.SP, set.getStackStart());
	}
	
	public void increaseProgramCounter() {
		RWData programCounter = readRegister(RegisterID.PC).getMutableCopy();

		programCounter.add(1);
		
		if (programCounter.isEqual(0))
			throw new MemoryIndexOutOfBoundsException("El contador de programa ha alcanzado el final de la memoria.");
		
		writeRegister(RegisterID.PC, programCounter);
	}
	
	public void increaseStackPointer() {
		RWData stackPointer = readRegister(RegisterID.SP).getMutableCopy();
		
		if (set.getStackGrowth() == StackGrowth.POSITIVE) {
			stackPointer.add(1);
			//Si el crecimiento es positivo y el nuevo valor del SP es la primera dirección de memoria, se produce un stack overflow.
			if (set.getCheckOverflow() && stackPointer.isEqual(0))
				throw new StackException("Se ha producido un overflow (desbordamiento) de pila.");
		}
		else {
			stackPointer.subtract(1);
			//Si el crecimiento es positivo y el nuevo valor del SP es la última dirección de memoria, se produce un stack overflow.
			if (set.getCheckOverflow() && stackPointer.isEqual(RWData.MAX_UNSIGNED_VALUE))
				throw new StackException("Se ha producido un overflow (desbordamiento) de pila.");
		}

		writeRegister(RegisterID.SP, stackPointer);
	}
	
	public void decreaseStackPointer() {
		RWData stackPointer = readRegister(RegisterID.SP).getMutableCopy();
		
		//No es necesario hacer aquí comprobación de stack underflow, ya se gestiona en writeRegister().
		if (set.getStackGrowth() == StackGrowth.POSITIVE)
			stackPointer.subtract(1);
		else
			stackPointer.add(1);

		writeRegister(RegisterID.SP, stackPointer);
	}
	
	public void setFlag(Flag f, boolean status) {
		rb.setFlag(f, status);
	}
	
	public boolean getFlag(Flag f) {
		return rb.getFlag(f);
	}
	
	public RData getStringPointer(Operand op) {
		RData index = null;
		
		switch (op.getAddressingMode()) {
			//En caso de direccionamiento directo a memoria, se trata como direccionamiento inmediato:
			//Se devuelve el valor literal de la dirección, no lo que hay en ella.
			case MEMORY: {
				index = op.getData();
				break;
			}
			
			//El direccionamiento indirecto utiliza la dirección contenida en el registro como dirección del puntero.
			case INDIRECT: {
				RegisterID reg = op.getRegister();
				index = readRegister(reg);
				break;
			}
			
			case RELATIVE_IX:
			case RELATIVE_IY: {
				RWData offset = new RWData(op.getData());
				RegisterID reg = op.getRegister();
				RWData temp = readRegister(reg).getMutableCopy();
				
				if (offset.convertToOffset() == true)
					temp.subtract(offset);
				else
					temp.add(offset);
				
				index = temp;
				break;
			}
			
			default:
				//No debería darse nunca un caso de enum nulo, con valor NONE o con un valor distinto a los empleados para la obtención de un puntero de cadena.
				break;
		}
		
		return index;
	}
	
	public RData getJumpValue(Operand op) {
		RData value = null;
		
		switch(op.getAddressingMode()) {
			//En caso de direccionamiento directo a memoria, se trata como direccionamiento inmediato:
			//Se devuelve el valor literal de la dirección, no lo que hay en ella.
			//Por ejemplo: si en la dirección 0xAAAA está almacenado el valor 0xBBBB, BR /0xAAAA salta a 0xAAAA, no a 0xBBBB
			case MEMORY: {
				value = op.getData();
				break;
			}
			
			//El direccionamiento indirecto sí funciona como en la asignación de valor estándar:
			//El valor de salto es el contenido en la dirección apuntada por el contenido del registro en cuestión.
			//Por ejemplo: si R0 tiene valor 0xAAAA, y en la dirección 0xAAAA está almacenado el valor 0xBBBB, BR [.R0] equivale saltar a 0xBBBB
			case INDIRECT: {
				RegisterID reg = op.getRegister();
				RData index = readRegister(reg);
				value = readMemory(index);
				break;
			}
			
			//Al igual que en el direccionamiento directo a memoria, se devuelve la dirección (en este caso del PC)
			//tras aplicar el offset, no el contenido de la misma.
			case RELATIVE_PC: {
				RWData offset = new RWData(op.getData());
				RegisterID reg = op.getRegister();
				RWData index = readRegister(reg).getMutableCopy();
				
				
				if (offset.convertToOffset() == true)
					index.subtract(offset);
				else
					index.add(offset);
				value = index;
				break;
			}
			
			default: {
				//No debería darse nunca un caso de enum nulo, con valor NONE o con un valor distinto a los empleados para salto.
				break;
			}
		}
		
		return value;
	}
	
	public RData getValue(Operand op) {
		RData value = null;

		switch(op.getAddressingMode()) {
			//En caso de direccionamiento inmediato, se devuelve directamente el valor almacenado en el operando.
			case LITERAL: {
				value = op.getData();
				break;
			}
			
			//En caso de direccionamiento directo a memoria, se devuelve el valor almacenado en la posición de memoria facilitada en el operando.
			case MEMORY: {
				RData index = op.getData();
				value = readMemory(index);
				break;
			}
			
			//En caso de direccionamiento directo a registro, se devuelve el valor almacenado en el registro identificado por el operando.
			case REGISTER: {
				RegisterID reg = op.getRegister();
				value = readRegister(reg);
				break;
			}
			
			//En caso de direccionamiento indirecto, se obtiene el valor del registro identificado por el operando y se usa dicho valor como índice
			//para recuperar de la memoria el valor a devolver.
			case INDIRECT: {
				RegisterID reg = op.getRegister();
				RData index = readRegister(reg);
				value = readMemory(index);
				break;
			}
			
			//En caso de direccionamiento relativo a registro, se obtiene del operando el valor de desplazamiento respecto al registro y el identificador
			//del registro a usar como referencia. Se extrae el valor de dicho registro, se le aplica el desplazamiento, y se usa el resultado como índice
			//para obtener de la memoria el valor a devolver.
			case RELATIVE_IX:
			case RELATIVE_IY: {
				RWData offset = new RWData(op.getData());
				RegisterID reg = op.getRegister();
				RWData index = readRegister(reg).getMutableCopy();
				
				
				if (offset.convertToOffset() == true)
					index.subtract(offset);
				else
					index.add(offset);
				value = readMemory(index);
				break;
			}
			
			default: {
				//No debería darse nunca un caso de enum nulo o con valor NONE o relativo a PC.
				break;
			}
		}
		
		return value;
	}
	
	public void setValue(Operand op, RData value) {
		switch(op.getAddressingMode()) {
			case MEMORY: {
				RData index = op.getData();
				writeMemory(index, value);
				break;
			}
			
			case REGISTER: {
				RegisterID reg = op.getRegister();
				writeRegister(reg, value);
				break;
			}
			
			case INDIRECT: {
				RegisterID reg = op.getRegister();
				RData index = readRegister(reg);
				writeMemory(index, value);
				break;
			}
			
			case RELATIVE_IX:
			case RELATIVE_IY: {
				RWData offset = new RWData(op.getData());
				RegisterID reg = op.getRegister();
				RWData index = readRegister(reg).getMutableCopy();
				
				if (offset.convertToOffset() == true)
					index.subtract(offset);
				else
					index.add(offset);
				writeMemory(index, value);
				break;
			}

			//No debería darse nunca un caso de enum nulo o con valores NONE.
			//Por motivos obvios, tampoco hay instrucciones que escriban en un literal.
			default: {
				break;
			}
		}
	}
	
	public RData getStackPointer() {
		return readRegister(RegisterID.SP);
	}
	
	public RData getProgramCounter() {
		return readRegister(RegisterID.PC);
	}
	
	public void writeAccumulator(RData value) {
		writeRegister(RegisterID.A, value);
	}
	
	public void setProgramCounter(RData value) {
		writeRegister(RegisterID.PC, value);
	}

	public RData readRegister(RegisterID reg) {
		return rb.readRegister(reg);
	}
	
	public void writeRegister(RegisterID reg, RData value) {
		if (reg == RegisterID.PC && set.getCheckPCInvadingStack() == true) {
			if (value.isGreaterEqual(set.getStackStart()) && value.isLessEqual(readRegister(RegisterID.SP)))
				throw new InvadingPointerException("El contador de programa ha invadido la zona de pila.");
			else if (value.isGreaterEqual(readRegister(RegisterID.SP)) && value.isLessEqual(set.getStackStart()))
				throw new InvadingPointerException("El contador de programa ha invadido la zona de pila.");
		}
		else if (reg == RegisterID.SP) {
			if (set.getCheckSPInvadingCode() && value.isGreaterEqual(set.getCodeStart()) && value.isLessEqual(set.getCodeEnd()))
				throw new InvadingPointerException("El puntero de pila ha invadido la zona de código.");
			
			//Si, por el motivo que sea, el SP va a terminar en una zona "anterior" al origen de la pila, se produce underflow.
			if (set.getCheckUnderflow()) {
				if (set.getStackGrowth() == StackGrowth.POSITIVE && value.isLess(set.getStackStart()))
					throw new StackException("Se ha producido un underflow (subdesbordamiento) de pila.");
				else if (set.getStackGrowth() == StackGrowth.NEGATIVE && value.isGreater(set.getStackStart()))
					throw new StackException("Se ha producido un underflow (subdesbordamiento) de pila.");
			}
		}
		
		rb.writeRegister(reg, value);
	}
	
	public RData readMemory(RData index) {
		return mem.fetchAt(index);
	}
	
	public void writeMemory(RData index, RData value) {
		mem.writeAt(index, value);
	}
	
	public void loadMemory(Memory newMem) {
		mem.loadMemory(newMem);
	}
	
	public Memory dumpMemory() {
		return mem.dumpMemory();
	}

	public void wipeMemory() {
		mem.wipe();
	}
}
