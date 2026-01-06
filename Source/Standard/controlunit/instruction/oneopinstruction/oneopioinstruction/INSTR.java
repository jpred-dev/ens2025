package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import customexception.MemoryIndexOutOfBoundsException;
import data.RData;
import data.RWData;

public class INSTR extends OneOpIOInstruction {

	public INSTR(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		String str = io.readStr();
		RWData mem = arc.getStringPointer(op).getMutableCopy();
		RData value;
		
		//Caso límite: si se recibe un string vacío, se escribe un '\0'.
		if (str.isEmpty()) {
			value = new RWData('\0');
			arc.writeMemory(mem, value);
		}
		else {
			if (str.charAt(str.length()-1) == '\n')
				str = str.substring(0, str.length()-1); //Se recorta el \n del final si se ha 'colado'.

			for(int i=0; i<str.length(); i++) {
				//Se lee la posición actual del string y se almacena en memoria.
				value = new RWData(str.charAt(i));
				arc.writeMemory(mem, value);
				mem.add(1);
				//Se comprueba que la memoria no desborda.
				if (mem.isEqual(0)) {
					throw new MemoryIndexOutOfBoundsException("La cadena introducida sobrepasa la memoria disponible.");
				}
			}
			arc.writeMemory(mem, new RWData(0)); //Se añade un \0 al final de la cadena.
		}
	}

}
