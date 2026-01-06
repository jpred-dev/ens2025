package controlunit.assembler.memorywriter;

import java.util.List;
import java.util.Map;

import controlunit.architecture.memory.Memory;
import controlunit.assembler.util.Reference;
import controlunit.codex.ISACodex;
import customexception.InvalidValueException;
import customexception.LabelException;
import data.RData;
import data.RWData;
import settings.Settings;

public class Writer {
	private Memory mem;
	private MemoryPointer mp;
	private RWData tempCodeStart;
	private RWData tempCodeEnd;
	private Settings set;
	
	public Writer(MemoryPointer mp, Settings set) {
		this.mem = null;
		this.mp = mp;
		this.set = set;
		tempCodeStart = new RWData(0);
		tempCodeEnd = new RWData(0);
	}
	
	public void writeInstruction(EncodedInstruction ei) {
		for (int i=0; i<ei.getBlockSize(); i++)
			writeData(ei.getWord(i));
	}
	
	public void writeReferences(List<Reference> refList, Map<String, RData> labelMap) {
		String label;
		Reference ref;
		RWData labelPointer;
		RWData refPointer;
		RWData temp;
		
		for (int i=0; i<refList.size(); i++) {
			ref = refList.get(i);
			label = ref.getLabel();
			if (labelMap.get(label) == null) {
				LabelException ex = new LabelException("No se ha definido la etiqueta '" + label + "', no se puede asignar la referencia.");
				throw ex;
			}
			
			labelPointer = labelMap.get(label).getMutableCopy();
			
			switch(ref.getOpSegment()) {
				case 1:
					if (ISACodex.isOffsetValid(labelPointer) == false) {
						InvalidValueException ex = new InvalidValueException("El valor de desplazamiento " + labelPointer.getValue() + " para la etiqueta '" + label + "' excede los límites permitidos.");
						throw ex;
					}
					labelPointer.lShift(ISACodex.FIRST_OPERAND_SHIFT);
					labelPointer.bitAnd(ISACodex.FIRST_OPERAND_MASK);
					break;
				case 2:
					if (ISACodex.isOffsetValid(labelPointer) == false) {
						InvalidValueException ex = new InvalidValueException("El valor de desplazamiento " + labelPointer.getValue() + " para la etiqueta '" + label + "' excede los límites permitidos.");
						throw ex;
					}
					labelPointer.lShift(ISACodex.SECOND_OPERAND_SHIFT);
					labelPointer.bitAnd(ISACodex.SECOND_OPERAND_MASK);
					break;
				default:
					break;
			}
			
			refPointer = ref.getPointer().getMutableCopy();
			
			if (ref.isRelativeToPC()) {
				//El puntero original apunta a la primera posición del PC tras ejecutar la instrucción completa. Esto es necesario para calcular correctamente el desplazamiento
				//con respecto a la etiqueta. Una vez se calcula ese desplazamiento, se resta 1 al puntero de la referencia para asignar la posición del operando donde debe insertarse.
				labelPointer.subtract(refPointer);
				refPointer.subtract(1);
				
				//Primero se comprueba que el valor de desplazamiento está en el rango permitido.
				if (ISACodex.isOffsetValid(labelPointer) == false) {
					InvalidValueException ex = new InvalidValueException("El valor de desplazamiento " + labelPointer.getValue() + " para la etiqueta '" + label + "' excede los límites permitidos.");
					throw ex;
				}
				
				//Se desplaza a la izquierda el valor de salto, como corresponde a un primer operando.
				labelPointer.lShift(ISACodex.FIRST_OPERAND_SHIFT);
				labelPointer.bitAnd(ISACodex.FIRST_OPERAND_MASK);
			}

			//Como la zona de la referencia se deja vacía, se puede extraer la palabra, aplicar con OR la referencia, y volverla a introducir.
			temp = mem.fetchAt(refPointer).getMutableCopy();
			temp.bitOr(labelPointer);
			mem.writeAt(refPointer, temp);
		}
	}
	
	public void writeData(RData data) {
		if (tempCodeStart.isGreater(mp.getPointer()))
			tempCodeStart.assign(mp.getPointer());
		if (tempCodeEnd.isLess(mp.getPointer()))
			tempCodeEnd.assign(mp.getPointer());

		mem.writeAt(mp.getPointer(), data);
		mp.increasePointer();
	}
	
	public void setReservedMemory(RData res) {
		//Primero se comprueba si la dirección de origen de reserva es válida como nueva dirección de inicio de código
		if (tempCodeStart.isGreater(mp.getPointer()))
			tempCodeStart.assign(mp.getPointer());
		
		//Se aplica la cantidad de memoria a reservar como offset
		mp.offsetPointer(res);
		
		//Se comprueba si la dirección de la última posición de memoria reservada es válida como nueva dirección de final de código
		if (tempCodeEnd.isLess(mp.getPointer()))
			tempCodeEnd.assign(mp.getPointer());
	}
	
	public void newMemory() {
		tempCodeStart = new RWData(RWData.MAX_UNSIGNED_VALUE);
		tempCodeEnd = new RWData(0);
		mem = new Memory();
	}
	
	public Memory getMemory() {
		return mem;
	}
	
	public void close() {
		mem = null;
	}

	public void commitZoneBoundaries() {
		//Caso límite: fichero vacío (no se ha escrito código). Se asigna 0 a los marcadores de inicio y final de código. Como el final ya es 0, solamente hace falta asignarlo al inicio.
		if (tempCodeStart.isEqual(RWData.MAX_UNSIGNED_VALUE) && tempCodeEnd.isEqual(0))
			tempCodeStart.assign(0);
		
		set.setCodeStart(tempCodeStart);
		set.setCodeEnd(tempCodeEnd);
	}
}
