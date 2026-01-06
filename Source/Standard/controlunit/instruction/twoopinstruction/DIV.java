package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;
import enums.Flag;

public final class DIV extends TwoOpInstruction {

	public DIV(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData dividend = arc.getValue(op1);
		RData divisor = arc.getValue(op2);
		RWData result = dividend.getMutableCopy();
		
		if (dividend.isEqual(0)) {
			/**
			 * Caso específico: dividendo 0.
			 * Resultado 0.
			 * Flags:
			 * CARRY: Desactivado.
			 * OVERFLOW: Desactivado.
			 * PARITY: Desactivado.
			 * SIGN: Desactivado.
			 * ZERO: Activado.
			 */
			
			result.assign(0);
			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.OVERFLOW, false);
			arc.setFlag(Flag.PARITY, false);
			arc.setFlag(Flag.SIGN, false);
			arc.setFlag(Flag.ZERO, true);
		}
		else if (dividend.isEqual(RWData.MIN_VALUE) && divisor.isEqual(-1)) {
			/**
			 * Caso específico: valor mínimo con signo dividido entre -1.
			 * Resultado MIN_VALUE.
			 * Flags:
			 * CARRY: Desactivado.
			 * OVERFLOW: Activado.
			 * PARITY: Activado.
			 * SIGN: Activado.
			 * ZERO: Desactivado.
			 */
			
			result.assign(RWData.MIN_VALUE);
			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.OVERFLOW, true);
			arc.setFlag(Flag.PARITY, true);
			arc.setFlag(Flag.SIGN, true);
			arc.setFlag(Flag.ZERO, false);
		}
		else {
			result.divide(divisor);

			//Salvo en el caso anterior, la división no produce overflow, y nunca produce carry.
			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.OVERFLOW, false);
			
			//Se comprueba el resto de flags con normalidad:
			
			//Flag PARITY.
			checkParity(result, arc);

			//Flag SIGN.
			checkSign(result, arc);

			//Flag ZERO.
			checkZero(result, arc);
		}

		arc.writeAccumulator(result);
	}
}
