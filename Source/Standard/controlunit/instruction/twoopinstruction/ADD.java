package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;
import enums.Flag;

public final class ADD extends TwoOpInstruction {

	public ADD(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value1 = arc.getValue(op1);
		RData value2 = arc.getValue(op2);
		RWData result = value1.getMutableCopy();
		
		result.add(value2);

		arc.writeAccumulator(result);
		
		/**
		 * Flag CARRY:
		 * 
		 * Se activa si ambos operandos son negativos, o si uno es negativo y el resultado es mayor o igual que cero.
		 * Es decir, uno o ambos operandos tienen un 1 como MSB en su representación sin signo y el resultado tiene un 0 en su MSB.
		 */
		if ((value1.isNegative() && value2.isNegative()) || ((value1.isNegative() || value2.isNegative()) && result.isPositiveOrZero()))
			arc.setFlag(Flag.CARRY, true);
		else
			arc.setFlag(Flag.CARRY, false);
		
		/**
		 * Flag OVERFLOW:
		 * 
		 * Dos posibles causas:
		 * - Si ambos operandos son positivos y el resultado es negativo.
		 * - Si ambos operandos son negativos y el resultado es positivo o cero.
		 * 		Esto último se debe al caso límite de MIN_VALUE + MIN_VALUE, cuya suma, tras ajustarse al tamaño de la variable,
		 * 		es cero. Sin embargo, es imposible que dos números positivos sumen cero en la representación C2, por lo que el
		 * 		resultado de la suma de dos positivos debe ser estrictamente negativo para que se considere overflow.
		 */
		if ((value1.isPositive() && value2.isPositive() && result.isNegative())
		|| (value1.isNegative() && value2.isNegative() && result.isPositiveOrZero()))
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
