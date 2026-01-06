package controlunit.assembler.memorywriter;

import customexception.MemoryIndexOutOfBoundsException;
import data.RData;
import data.RWData;

public class MemoryPointer {
	private RWData pointer;
	private boolean overflow;

	public MemoryPointer() {
		pointer = new RWData(0);
		overflow = false;
	}
	
	public RData getPointer() {
		return pointer.getImmutableCopy();
	}
	
	public void setPointer(RData ind) {
		pointer.assign(ind);
	}
	
	public void offsetPointer(RData offset) {
		pointer.add(offset);
		//Si la suma del índice actual y la expresión de la directiva (que de por sí no puede superar el valor máximo sin signo)
		//supera el valor máximo sin signo, el resultado siempre será menor que el desplazamiento original.
		if (pointer.isLess(offset))
			throw new MemoryIndexOutOfBoundsException("La directiva RES ha excedido el límite de memoria.");
	}
	
	public void increasePointer() {
		if (overflow == true)
			throw new MemoryIndexOutOfBoundsException("El puntero de escritura del ensamblador ha alcanzado el final de la memoria.");
		pointer.add(1);
		//Si al sumar 1 el puntero de escritura pasa a valer 0 es evidente que ha desbordado.
		if (pointer.isEqual(0))
			overflow = true;
	}
	
	public boolean getOverflow() {
		return overflow;
	}
	
	public void resetOverflow() {
		overflow = false;
	}
	
	public void resetPointer() {
		pointer.assign(0);
		overflow = false;
	}
}
