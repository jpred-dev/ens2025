package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;
import enums.Flag;

public final class SUB extends TwoOpInstruction {

	public SUB(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData min = arc.getValue(op1);
		RData sub = arc.getValue(op2);
		RWData result = min.getMutableCopy();
		
		result.subtract(sub);

		arc.writeAccumulator(result);
		
		/**
		 * Flag CARRY:
		 * 
		 * Dos posibles causas:
		 * - Si el minuendo es mayor o igual que cero y el sustraendo es o bien negativo o bien mayor que el minuendo.
		 * - Si el minuendo y el sustraendo son negativos y el sustraendo es mayor que el minuendo.
		 */
		  
		if ((min.isPositiveOrZero() && (sub.isNegative() || sub.isGreater(min))) 
		|| (min.isNegative() && sub.isNegative() && sub.isGreater(min)))
			arc.setFlag(Flag.CARRY, true);
		else
			arc.setFlag(Flag.CARRY, false);
		
		/**
		 * Flag OVERFLOW:
		 * 
		 * Dos posibles causas:
		 * - Si el minuendo es mayor o igual que cero y el sustraendo y la diferencia son negativos.
		 * 		Esto se debe al caso límite 0-MIN_VALUE, que dará como resultado MIN_VALUE.
		 * - Si el minuendo es negativo y el sustraendo y la diferencia son positivos.
		 */
		if ((min.isPositiveOrZero() && sub.isNegative() && result.isNegative())
		|| (min.isNegative() && sub.isPositive() && result.isPositive()))
			arc.setFlag(Flag.OVERFLOW, true);
		else
			arc.setFlag(Flag.OVERFLOW, false);
		
		//Flag PARITY.
		checkParity(result, arc);

		//Flag SIGN.
		checkSign(result, arc);

		//Flag ZERO.
		checkZero(result, arc);
	}
}