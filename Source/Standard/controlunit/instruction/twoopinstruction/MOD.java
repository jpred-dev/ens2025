package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;
import enums.Flag;

public final class MOD extends TwoOpInstruction {

	public MOD(InstructionData inst) {
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
		else {
			result.modulo(divisor);

			//El módulo nunca produce carry ni overflow.
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
