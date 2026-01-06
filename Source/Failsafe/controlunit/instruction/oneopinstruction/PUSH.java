package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class PUSH extends OneOpInstruction {

	public PUSH(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value = arc.getValue(op); //Valor a almacenar en la posición de memoria marcada por el SP.
		RData stackPointer = arc.getStackPointer(); //Valor actual del SP.
		
		arc.writeMemory(stackPointer, value); //Se escribe en la posición marcada por el SP el valor.
		arc.increaseStackPointer(); //Se incrementa el valor del SP.
	}
}
