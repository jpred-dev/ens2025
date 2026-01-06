package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RWData;
import enums.Flag;

public final class NEG extends OneOpInstruction {

	public NEG(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RWData value = arc.getValue(op).getMutableCopy();
		
		if (value.isEqual(0)) {
			
			/**
			 * Podemos obviar directamente la operación 0 - 0 y activar o desactivar los flags pertinentes.
			 * CARRY: Desactivado.
			 * OVERFLOW: Desactivado.
			 * PARITY: Desctivado.
			 * SIGN: Desactivado.
			 * ZERO: Activado.
			 */
			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.OVERFLOW, false);
			arc.setFlag(Flag.PARITY, false);
			arc.setFlag(Flag.SIGN, false);
			arc.setFlag(Flag.ZERO, true);
		}
		else if (value.isEqual(RWData.MIN_VALUE)) {
			/**
			 * Para el caso 0 - MIN_VALUE, el resultado es MIN_VALUE también, así que podemos obviar la operación.
			 * además, se activan los flags del siguiente modo:
			 * CARRY: Activado.
			 * OVERFLOW: Activado.
			 * PARITY: Activado.
			 * SIGN: Activado.
			 * ZERO: Desactivado.
			 */
			arc.setFlag(Flag.CARRY, true);
			arc.setFlag(Flag.OVERFLOW, true);
			arc.setFlag(Flag.PARITY, true);
			arc.setFlag(Flag.SIGN, true);
			arc.setFlag(Flag.ZERO, false);
		}
		else {
			//Para cualquier otro caso, se calcula mediante 0 - value.
			RWData result = new RWData(0);
			result.subtract(value);
			value.assign(result);
			
			//Los flags de OVERFLOW y ZERO no se activan nunca y el de CARRY se activa siempre; los demás se calculan con normalidad.
			arc.setFlag(Flag.CARRY, true);
			arc.setFlag(Flag.OVERFLOW, false);
			arc.setFlag(Flag.ZERO, false);

			//Flag PARITY.
			checkParity(result, arc);
			
			//Flag SIGN.
			checkSign(result, arc);

			arc.setValue(op, value);
		}
	}
}