package controlunit.instruction.oneopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RWData;
import enums.Flag;

public final class DEC extends OneOpInstruction {

	public DEC(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RWData value = arc.getValue(op).getMutableCopy();

		value.subtract(1);

		arc.setValue(op, value);
		
		/**
		 * Flag CARRY:
		 * 
		 * Si el resultado es igual a -1, significa que se ha restado 1 a 0.
		 */
		if (value.isEqual(-1))
			arc.setFlag(Flag.CARRY, true);
		else
			arc.setFlag(Flag.CARRY, false);
		
		/**
		 * Flag OVERFLOW:
		 * 
		 * Si el resultado es igual a MAX_VALUE, significa que se ha restado 1 a MIN_VALUE.
		 */
		if (value.isEqual(RWData.MAX_VALUE))
			arc.setFlag(Flag.OVERFLOW, true);
		else
			arc.setFlag(Flag.OVERFLOW, false);

		//Flag PARITY.
		checkParity(value, arc);
		
		//Flag SIGN.
		checkSign(value, arc);
		
		//Flag ZERO.
		checkZero(value, arc);
	}
}
