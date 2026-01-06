package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class POP extends OneOpInstruction {

	public POP(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		arc.decreaseStackPointer(); //Se reduce el SP.
		RData stackPointer = arc.getStackPointer(); //Valor actual del SP.
		RData value = arc.readMemory(stackPointer); //Valor contenido en la posición de memoria apuntada por el SP.
		
		arc.setValue(op, value); //Se almacena el valor obtenido en el registro o posición de memoria indicado por el operando.
	}
}
