package controlunit.instruction.noopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;

public final class RET extends NoOpInstruction {
	public RET(InstructionData inst) { super(inst); }
	
	public void run(Architecture arc) {
		arc.decreaseStackPointer();
	
		RData sp = arc.getStackPointer(); //Se obtiene el valor actual del SP.
		RData value = arc.readMemory(sp); //Se extrae de la memoria el valor almacenado en la dirección apuntada por el SP.
		arc.setProgramCounter(value); //Se almacena ese valor obtenido en el PC.
	}
}
