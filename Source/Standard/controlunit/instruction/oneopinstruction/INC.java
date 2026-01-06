package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RWData;
import enums.Flag;

public final class INC extends OneOpInstruction {

	public INC(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RWData value = arc.getValue(op).getMutableCopy();

		value.add(1);

		arc.setValue(op, value);
		
		/**
		 * Flag CARRY:
		 * 
		 * Si el resultado es igual a 0, significa que se ha sumado 1 a -1 (todo unos en representación C2).
		 * De paso, se activa o desactiva el flag ZERO según corresponda.
		 */
		if (value.isEqual(0)) {
			arc.setFlag(Flag.CARRY, true);
			arc.setFlag(Flag.ZERO, true);
		}
		else {
			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.ZERO, false);
		}
		
		/**
		 * Flag OVERFLOW:
		 * 
		 * Si el resultado es igual a MIN_VALUE, significa que se ha sumado 1 a MAX_VALUE.
		 */
		if (value.isEqual(RWData.MIN_VALUE))
			arc.setFlag(Flag.OVERFLOW, true);
		else
			arc.setFlag(Flag.OVERFLOW, false);

		
		//Flag PARITY.
		checkParity(value, arc);
		
		//Flag SIGN.
		checkSign(value, arc);
	}
}
