package controlunit.instruction.oneopinstruction.oneopioinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import customexception.MemoryIndexOutOfBoundsException;
import data.RData;
import data.RWData;

public final class WRSTR extends OneOpIOInstruction {

	public WRSTR(InstructionData inst) {
		super(inst);
	}
	
	@Override
	public void run(Architecture arc) {
		StringBuilder sb = new StringBuilder();
		RWData mem = arc.getStringPointer(op).getMutableCopy();
		RData value = arc.readMemory(mem);
		char c = value.getChar();
		
		while (c != '\0') {
			sb.append(c);
			
			mem.add(1);
			
			if (mem.isEqual(0)) {
				throw new MemoryIndexOutOfBoundsException("Se ha llegado al final de la memoria sin encontrar un carácter de fin de cadena.");
			}
			
			value = arc.readMemory(mem);
			c = value.getChar();
		}
		
		io.printStr(sb.toString());
	}

}