package controlunit.instruction.twoopinstruction;

import controlunit.architecture.Architecture;
import controlunit.instruction.InstructionData;
import data.RData;
import data.RWData;
import enums.Flag;

public class MUL extends TwoOpInstruction {

	public MUL(InstructionData inst) {
		super(inst);
	}

	@Override
	public void run(Architecture arc) {
		RData value1 = arc.getValue(op1);
		RData value2 = arc.getValue(op2);
		RWData result = value1.getMutableCopy();
		
		/**
		 * Caso específico: al menos un operando tiene valor 0.
		 * Resultado 0.
		 * Flags:
		 * CARRY: Desactivado.
		 * OVERFLOW: Desactivado.
		 * PARITY: Desactivado.
		 * SIGN: Desactivado.
		 * ZERO: Activado.
		 */
		if (value1.isEqual(0) || value2.isEqual(0)) {
			result.assign(0);
			arc.writeAccumulator(result);

			arc.setFlag(Flag.CARRY, false);
			arc.setFlag(Flag.OVERFLOW, false);
			arc.setFlag(Flag.PARITY, false);
			arc.setFlag(Flag.SIGN, false);
			arc.setFlag(Flag.CARRY, true);
		}
		
		else {
			result.multiply(value2);
			arc.writeAccumulator(result);
			
			/**
			 * Caso específico: resultado MIN_VALUE y un operando con valor -1.
			 * Flags:
			 * CARRY: Activado.
			 * OVERFLOW: Activado.
			 * PARITY: Activado.
			 * SIGN: Activado.
			 * ZERO: Desactivado.
			 */
			
			if (result.isEqual(RWData.MIN_VALUE) && (value1.isEqual(-1) || value2.isEqual(-1))) {
				arc.setFlag(Flag.CARRY, true);
				arc.setFlag(Flag.OVERFLOW, true);
				arc.setFlag(Flag.PARITY, true);
				arc.setFlag(Flag.SIGN, true);
				arc.setFlag(Flag.ZERO, false);
			}
			
			//Para cualquier otro caso calculamos flags con normalidad.
			else {
				//Flag CARRY.
				arc.setFlag(Flag.CARRY, carryCalc(value1, value2));
				
				//Flag OVERFLOW.
				arc.setFlag(Flag.OVERFLOW, overflowCalc(value1, value2, result));
				
				//Flag PARITY.
				checkParity(result, arc);
						
				//Flag SIGN.
				checkSign(result, arc);
						
				//Flag ZERO.
				checkZero(result, arc);
			}
		}	
	}
	
	/**
	 * Flag CARRY:
	 * 
	 * Para evitar recurrir a un tamaño de dato mayor, se separan en mitades ambos operandos (v1_1, v1_2, v2_1, v2_2). El resultado final, que ocuparía dos palabras de
	 * tamaño estándar de N bits, se calcularía así:
	 * 
	 * v1_1*v2_1 desplazados N bits + (v1_1*v2_2 + v1_2*v2_1) desplazados N/2 bits + v1_2*v2_2.
	 * 
	 * Puesto que solamente nos interesa la palabra menos significativa para el resultado, y el resto sirve para calcular el carry, podemos ejecutar las operaciones en cierto orden
	 * y, si en algún momento el carry es positivo, podemos dejar de calcular.
	 * 
	 * - En primer lugar, calculamos v1_1*v2_1. Como la totalidad del resultado ocupa la palabra más significativa del resultado completo, cualquier valor distinto de 0 indica que ha habido carry.
	 * - Luego, calculamos v1_1*v2_2 y v1_2*v2_1. Si cualquiera de ellos tiene un bit en su mitad más significativa a 1, al desplazarlos N/2 ese bit quedaría fuera de la palabra menos significativa
	 *   del resultado completo, indicando carry.
	 * - Procedemos a sumar los resultados anteriores. Si la suma tiene un bit a 1 en su mitad más significativa, de nuevo, se produce carry.
	 * - Finalmente, si tras desplazar el resultado anterior y sumarle v1_2*v2_2 se produce carry (usamos el mismo procedimiento que para detectar carry en la instrucción de suma), entonces hay carry.
	 * - Si no se ha detectado un carry tras estos procedimientos, la multiplicación completa no genera carry.
	 */
	private boolean carryCalc(RData value1, RData value2) {
		RWData v1_1 = new RWData(value1);
		RWData v1_2 = new RWData(value1);
		RWData v2_1 = new RWData(value2);
		RWData v2_2 = new RWData(value2);
		RWData first = new RWData(0);
		RWData second = new RWData(0);
		RWData third = new RWData(0);
		RWData temp = new RWData(0);
		
		//Para las mitades más significativas basta un desplazamiento sin signo a la derecha:
		v1_1.urShift(RWData.HALF_SIZE);
		v2_1.urShift(RWData.HALF_SIZE);
		
		//Para las menos significativas, se aplica como máscara el valor máximo sin signo de media palabra:
		v1_2.bitAnd(RWData.HALF_WORD_MAX_UNSIGNED);
		v2_2.bitAnd(RWData.HALF_WORD_MAX_UNSIGNED);

		//Parte 1: v1_1*v2_1. Si el producto no es 0, hay carry.
		first.assign(v1_1);
		first.multiply(v2_1);
		
		if (first.isEqual(0) == false)
			return true;
		
		//Parte 2: evaluar primero si v1_1*v2_2 supera el valor máximo de media palabra, luego si v2_1*v1_2 supera ese valor, y luego si lo hace la suma de ambos.
		second.assign(v1_1);
		second.multiply(v2_2);
		if (second.isGreater(RWData.HALF_WORD_MAX_UNSIGNED))
			return true;
		
		temp.assign(v1_2);
		temp.multiply(v2_1);
		if (temp.isGreater(RWData.HALF_WORD_MAX_UNSIGNED))
			return true;
		
		second.add(temp);
		if (second.isGreater(RWData.HALF_WORD_MAX_UNSIGNED))
			return true;
		
		//Parte 3: desplazar el resultado anterior y sumárselo a v1_2*v2_2. Se calculará el carry mediante el algoritmo normal de suma.
		second.lShift(RWData.HALF_SIZE);
		third.assign(v1_2);
		third.multiply(v2_2);
		
		temp.assign(second);
		temp.add(third);
		
		//Si ambos sumandos son negativos, o si uno es negativo y el resultado es mayor o igual que 0, hay carry.
		if (second.isNegative() && third.isNegative())
			return true;
		
		if ((second.isNegative() || third.isNegative()) && temp.isPositiveOrZero())
			return true;
		
		return false;
	}
	
	/**
	 * Flag OVERFLOW:
	 * 
	 * Si al dividir el resultado entre el primer operando y no da como resultado el segundo operando, hay overflow.
	 * No es necesario controlar que el denominador no sea 0 porque el caso específico de que un operando sea 0 se maneja al inicio.
	 */
	
	private boolean overflowCalc(RData value1, RData value2, RData result) {
		RWData div = new RWData(result);
		
		div.divide(value1);
		if (div.isEqual(value2) == true)
			return false;
		
		return true;
	}

}
