package data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import customexception.DataParsingException;
import customexception.DivideByZeroException;

public class RWData implements RData {
	private int value;
	//Constantes
	public static final int DATA_SIZE = 16;
	public static final int HALF_SIZE = 8;
	public static final int HALF_WORD_MAX_UNSIGNED = 255;
	public static final int MAX_UNSIGNED_VALUE = 65535;
	public static final int MAX_VALUE = 32767;
	public static final int MIN_VALUE = -32768;
	private static final int MAX_OFFSET_POSITIVE = 127;
	private static final int OFFSET_SIGNCHANGE_VALUE = 256;
	private static final int OFFSET_MASK = 0xFF;
	private static final int MASK = 0xFFFF;
	/**
	 * Se aplica esta máscara tras cada operación con el valor interno mientras el tamaño de palabra usado por el simulador sea de 16 bits.
	 * De esta manera, siempre quedan los 16 bits más significativos a 0 y se evitan comportamientos no definidos.
	 * Si se modifica el simulador para 32 bits, basta con modificar la máscara por 0xFFFFFFFF.
	 */
	
	public RWData(Number n) {
		value = fitSize(n) & MASK;
	}
	
	private int fitSize(Number n) {
		return n.intValue() & MASK;
	}
	
	public RWData(char c) {
		String str = String.valueOf(c);
		//Se aplica una máscara para aislar el byte menos significativo.
		value = str.codePointAt(0) & 0xFF;
	}
	
	public RWData(RData d) {
		value = d.getValue();
	}
	
	public RWData() {
		value = 0;
	}
	
	public RWData(String line) {
		this.value = parseString(line);
	}
	
	private int parseString(String line) {
		String sub;
		int i = 0;
		
		line = line.toLowerCase().trim();
		try {
			
			//Caso 1: Intentar capturar posible hexadecimal positivo.
			if (line.length() > 2 && line.startsWith("0x")) {
				sub = line.substring(2);
				i = Integer.parseInt(sub, 16);
			}
			
			//Caso 2: Intentar capturar posible hexadecimal negativo.
			//código desactivado: por requisito no se deben admitir hexadecimales negativos.
			/*else if (line.length() > 3 && line.startsWith("-0x")) {
				sub = line.substring(3);
				i = Integer.parseInt(sub, 16);
				i *= -1;
			}*/
			
			//Resto de casos: Intentar capturar posible decimal.
			else
				i = Integer.parseInt(line, 10);
		} catch (NumberFormatException ex) {
			throw new DataParsingException("La cadena numérica" + line + " contiene caracteres inválidos.");
		}
		
		if (i > MAX_UNSIGNED_VALUE || i < MIN_VALUE) 
			throw new DataParsingException("El valor " + i + " excede los límites.");

		return i & MASK;
	}

	@Override
	public int getValue() {
		return fitSize(value) & MASK;
	}
	
	public void readFromStream(InputStream stream) throws IOException {
		int num = DATA_SIZE/8;
		RWData val = new RWData(0);
		Number b; //Almacenamiento temporal del byte leído.
		RWData temp = new RWData(0); //Tipo Data para transferir el byte.
		byte[] bytes = new byte[num];
		
		stream.read(bytes);
		
		//Como los valores están guardados en formato Big Endian, se lee directamente byte a byte sin problemas.
		for (int i=0; i<num; i++) {
			val.lShift(8);
			b = bytes[i];
			temp.assign(b);
			temp.bitAnd(0xFF);
			val.add(temp);
		}
		
		assign(val);
	}
	
	public void assign(String line) {
		this.value = parseString(line);
	}
	
	/**
	 * Operadores con valores numéricos.
	 */
	
	public void assign(Number n) {
		value = fitSize(n);
		value &= MASK;
	}
	
	public void add(Number n) {
		value += fitSize(n);
		value &= MASK;
	}
	
	public void subtract(Number n) {
		value -= fitSize(n);
		value &= MASK;
	}
	
	public void multiply(Number n) {
		value *= fitSize(n);
		value &= MASK;
	}
	
	public void divide(Number n) {
		if (fitSize(n) == 0) {
			throw new DivideByZeroException();
		}
		
		//Se realizan las extensiones de signo pertinentes a los operandos:
		//Dividendo:
		//Si el valor con signo es negativo, se aplica un OR de la máscara negada (los 16 primeros bits se ponen a 1)
		if (value > MAX_VALUE)
			value |= ~MASK;
		
		//Divisor:
		if (fitSize(n) > MAX_VALUE)
			value /=  fitSize(n) | ~MASK;
		else
			value /= fitSize(n);

		//Se devuelve el valor recortado.
		value &= MASK;
	}
	
	public void modulo(Number n) {
		if (fitSize(n) == 0) {
			throw new DivideByZeroException();
		}
		
		//Se realizan las extensiones de signo pertinentes a los operandos:
		//Dividendo:
		//Si el valor con signo es negativo, se aplica un OR de la máscara negada (los 16 primeros bits se ponen a 1)
		if (value > MAX_VALUE)
			value |= ~MASK;
		
		//Divisor:
		if (fitSize(n) > MAX_VALUE)
			value %=  fitSize(n) | ~MASK;
		else
			value %= fitSize(n);

		//Se devuelve el valor recortado.
		value &= MASK;
	}
	
	public void bitAnd(Number n) {
		value &= fitSize(n);
		value &= MASK;
	}
	
	public void bitOr(Number n) {
		value |= fitSize(n);
		value &= MASK;
	}
	
	public void bitXor(Number n) {
		value ^= fitSize(n);
		value &= MASK;
	}
	
	public void bitInvert() {
		value = ~value;
		value &= MASK;
	}
	
	public void urShift(Number n) {
		value &= MASK;
		value >>>= fitSize(n);
	}
	
	public void rShift(Number n) {
		value &= MASK;
		
		//Si el valor con signo es negativo, se aplica un OR de la máscara negada (los 16 primeros bits se ponen a 1)
		if (value > MAX_VALUE)
			value |= ~MASK;
		value >>= fitSize(n);
		
		//Se vuelve a aplicar la máscara por si se ha cambiado el signo.
		value &= MASK;
	}
	
	public void lShift(Number n) {
		value <<= fitSize(n);
		value &= MASK;
	}
	
	public boolean convertToOffset() {
		value &= OFFSET_MASK;
		if (value > MAX_OFFSET_POSITIVE) {
			value = (OFFSET_SIGNCHANGE_VALUE - value) & OFFSET_MASK;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isPositive() {
		return (value > 0 && value <= MAX_VALUE);
	}
	
	@Override
	public boolean isPositiveOrZero() {
		return (value <= MAX_VALUE);
	}
	
	@Override
	public boolean isNegative() {
		return (value > MAX_VALUE);
	}
	
	@Override
	public boolean isNegativeOrZero() {
		return (value == 0 || value > MAX_VALUE);
	}
	
	@Override
	public boolean isGreater(Number n) {
		value &= MASK;
		
		return (value > fitSize(n));
	}
	
	@Override
	public boolean isGreaterEqual(Number n) {
		value &= MASK;
		
		return (value >= fitSize(n));
	}
	
	@Override
	public boolean isLess(Number n) {
		value &= MASK;
		
		return (value < fitSize(n));
	}
	
	@Override
	public boolean isLessEqual(Number n) {
		value &= MASK;
		
		return (value <= fitSize(n));
	}
	
	@Override
	public boolean isEqual(Number n) {
		value &= MASK;
		return (value == fitSize(n));
	}
	
	public void assign(RData d)		{ assign(d.getValue()); }
	public void add(RData d) 		{ add(d.getValue()); }
	public void subtract(RData d) 	{ subtract(d.getValue()); }
	public void multiply(RData d)	{ multiply(d.getValue()); }
	public void divide(RData d)		{ divide(d.getValue()); }
	public void modulo(RData d) 	{ modulo(d.getValue()); }
	public void bitAnd(RData d) 	{ bitAnd(d.getValue()); }
	public void bitOr(RData d)	 	{ bitOr(d.getValue()); }
	public void bitXor(RData d) 	{ bitXor(d.getValue()); }
	public void urShift(RData d)	{ urShift(d.getValue()); }
	public void rShift(RData d) 	{ rShift(d.getValue()); }
	public void lShift(RData d) 	{ lShift(d.getValue()); }
	
	@Override
	public boolean isGreater(RData d) 		{ return isGreater(d.getValue()); }
	
	@Override
	public boolean isGreaterEqual(RData d)	{ return isGreaterEqual(d.getValue()); }
	
	@Override
	public boolean isLess(RData d)			{ return isLess(d.getValue()); }
	
	@Override
	public boolean isLessEqual(RData d)		{ return isLessEqual(d.getValue()); }
	
	@Override
	public boolean isEqual(RData d)			{ return isEqual(d.getValue()); }

	@Override
	public RWData getMutableCopy() {
		return new RWData(value);
	}
	
	@Override
	public RData getImmutableCopy() {
		return new RWData(value);
	}
	
	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		int num = DATA_SIZE/8;
		RWData val = getMutableCopy();
		byte[] bytes = new byte[num];
		
		//Se parsean primero los bytes menos significativos y se añaden en las últimas posiciones del array para preservar el formato Big Endian.
		for (int i=num-1; i>=0; i--) {
			Number v = val.getValue();
			bytes[i] = v.byteValue();
			val.urShift(8);
		}
		
		stream.write(bytes);
	}
	
	@Override
	public int bitCount() {
		value &= MASK;
		return Integer.bitCount(value);
	}
	
	@Override
	public char getChar() {
		/**
		 * Puesto que solamente se usa el byte menos significativo como código para el carácter, éste siempre tendrá
		 * un identificador entre 0 y 255. Por otro lado, Java utiliza representación de 16 bits para sus caracteres.
		 * Por ambos motivos, queda garantizado que cualquier llamada a getChar() devolverá siempre un array con un
		 * único elemento dentro, así que se puede recurrir a c[0] en cada iteración sin miedo a dejar fuera información.
		 */
		Number n = value;
		char[] c = Character.toChars(n.intValue());
		return c[0];
	}
	
	/**
	 * Representación en string.
	 */
	
	@Override
	public String getHexString() {
		StringBuilder sb = new StringBuilder("0x");
		String hex = Integer.toHexString(value).toUpperCase();
		
		if (value <= 0xF)
			sb.append("000").append(hex);
		else if (value <= 0xFF)
			sb.append("00").append(hex);
		else if (value <= 0xFFF)
			sb.append("0").append(hex);
		else
			sb.append(hex);
		
		return sb.toString();
	}
	
	@Override
	public String getHalfHexString() {
		//Llamar a getHexString y devolver directamente "0x" + resultado.substring(4)?
		StringBuilder sb = new StringBuilder("0x");
		String hex = Integer.toHexString(value).toUpperCase();
		
		if (value <= 0xF)
			sb.append("0").append(hex);
		else if (value <= 0xFF)
			sb.append(hex);
		else if (value <= 0xFFF)
			sb.append(hex.substring(1));
		else
			sb.append(hex.substring(2));
		
		return sb.toString();
	}
	
	@Override
	public String getDecString() {
		Number n = value;
		String str = Short.toString(n.shortValue());
		return str;
	}
	
	@Override
	public String getHalfDecString() {
		Number n = value;
		byte b = n.byteValue();
		String str = Byte.toString(b);
		
		return str;
	}
	
	@Override
	public String getUnsignedDecString() {
		Number n = value;
		Integer udec = Short.toUnsignedInt(n.shortValue()) & MASK;
		String str = Integer.toString(udec);
		return str;
	}
	
	@Override
	public String getBinString() {
		String temp = "0000000000000000";
		String bin = Integer.toBinaryString(value & MASK);
		String str = temp.substring(bin.length()) + bin;
		return str;
	}
}
