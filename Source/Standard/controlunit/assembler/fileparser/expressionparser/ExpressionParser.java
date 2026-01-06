package controlunit.assembler.fileparser.expressionparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import controlunit.assembler.fileparser.filedata.FileData;
import customexception.DivideByZeroException;
import customexception.ExpressionIntegrityException;
import customexception.InvalidCharacterException;
import customexception.InvalidValueException;
import data.RData;
import data.RWData;

public class ExpressionParser {
	private FileData fd;
	private Stack<Token> opStack;
	private Stack<ValueToken> valStack;
	private static final Set<Character> OP_CHARS = new HashSet<>(Arrays.asList('-', '+', '*', '/', '%'));
	private static final Set<Character> PAR_CHARS = new HashSet<>(Arrays.asList('(', ')'));

	public ExpressionParser() {
		opStack = new Stack<>();
		valStack = new Stack<>();
	}
	
	public void loadFileData(FileData fd) {
		this.fd = fd;
	}
	
	public void close() {
		fd = null;
	}

	private boolean isValidCharacter(char c) {
		if (Character.isDigit(c) || isOperator(c) || PAR_CHARS.contains(c))
			return true;
		return false;
	}
	
	private boolean isOperator(char c) {
		return OP_CHARS.contains(c);
	}
	
	private TokenType getOpFromChar(char c) {
		TokenType tt = null;
		
		if (c == '-') {
			tt = TokenType.SUB;
		}
		else if (c == '+') {
			tt = TokenType.ADD;
		}
		else if (c == '*') {
			tt = TokenType.MUL;
		}
		else if (c == '/') {
			tt = TokenType.DIV;
		}
		else if (c == '%') {
			tt = TokenType.MOD;
		}
		
		return tt;
	}
	
	public RData parseExpression() {
		TokenType lastToken = null;
		char c = fd.peekChar(true);
		int val;
		int parCount = 0;
		
		if (Character.isDigit(c) == false && c != '-' && c != '(') {
			valStack.clear();
			opStack.clear();
			InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no es válido.");
			ex.setLine(fd.getLineNumber());
			throw ex;	
		}
	
		while (isValidCharacter(c) == true) {
			if (c == '-') {
				if (lastToken == null) {
					opStack.push(new Token(TokenType.NEG));
					lastToken = TokenType.NEG;
				}
				
				else {
					switch (lastToken) {
						case L_PAR:
							opStack.push(new Token(TokenType.NEG));
							lastToken = TokenType.NEG;
							break;
						case DEC:
						case R_PAR:
						case HEX:
							opStack.push(new Token(TokenType.SUB));
							lastToken = TokenType.SUB;
							break;
						default:
							valStack.clear();
							opStack.clear();
							InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no puede ir situado detrás de otro operador.");
							ex.setLine(fd.getLineNumber());
							throw ex;
					}
				}
				fd.increaseIndex();
			}
			
			else if (Character.isDigit(c) == true) {
				ValueToken vt;
				if (lastToken == null) {
					vt = parseValueToken();
					valStack.push(vt);
					lastToken = vt.getType();
				}
				else {
					switch (lastToken) {
						case L_PAR:
						case ADD:
						case SUB:
						case MUL:
						case DIV:
						case MOD:
						case NEG:
							vt = parseValueToken();
							valStack.push(vt);
							lastToken = vt.getType();
							break;
						default:
							valStack.clear();
							opStack.clear();
							InvalidCharacterException ex = new InvalidCharacterException("No se admiten cadenas numéricas inmediatamente después de un cierre de paréntesis u otra cadena numérica.");
							ex.setLine(fd.getLineNumber());
							throw ex;
					}
				}
			}
			
			else if (c == '(') {
				if (lastToken == null) {
					opStack.push(new Token(TokenType.L_PAR));
					lastToken = TokenType.L_PAR;
					parCount++;
				}
				
				else {
					switch (lastToken) {
						case L_PAR:
						case ADD:
						case SUB:
						case MUL:
						case DIV:
						case MOD:
						case NEG:
							opStack.push(new Token(TokenType.L_PAR));
							lastToken = TokenType.L_PAR;
							parCount++;
							break;
						default:
							valStack.clear();
							opStack.clear();
							InvalidCharacterException ex = new InvalidCharacterException("No se admiten paréntesis abiertos inmediatamente después de un cierre de paréntesis o una cadena numérica.");
							ex.setLine(fd.getLineNumber());
							throw ex;
					}
				}
				fd.increaseIndex();
			}
			
			else if (c == ')') {
				if (lastToken == null) {
					valStack.clear();
					opStack.clear();
					InvalidCharacterException ex = new InvalidCharacterException("No se admite un cierre de paréntesis al inicio de una expresión aritmética.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				
				if (parCount <= 0) {
					valStack.clear();
					opStack.clear();
					ExpressionIntegrityException ex = new ExpressionIntegrityException("Se ha recibido un cierre de paréntesis sin haber ninguno abierto.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				
				switch (lastToken) {
					case DEC:
					case HEX:
					case R_PAR:
						Token top = opStack.peek();
						while (top.getType() != TokenType.L_PAR) {
							applyOp();
							top = opStack.peek();
						}
						lastToken = TokenType.R_PAR;
						parCount--;
						break;
					default:
						valStack.clear();
						opStack.clear();
						InvalidCharacterException ex = new InvalidCharacterException("Solamente se admite un cierre de paréntesis tras una cadena numérica u otro cierre de paréntesis.");
						ex.setLine(fd.getLineNumber());
						throw ex;
				}
				fd.increaseIndex();
			}
			
			else if (isOperator(c) == true) {
				if (lastToken == null) {
					valStack.clear();
					opStack.clear();
					InvalidCharacterException ex = new InvalidCharacterException("No se admiten operadores binarios al inicio de una expresión aritmética.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				else {
					Token current = new Token(getOpFromChar(c));
					switch (lastToken) {
						case DEC:
						case HEX:
						case R_PAR:
							if (opStack.isEmpty()) {
								//Si la pila está vacía, no hay otros operadores con los que comparar; se inserta directamente.
								opStack.push(current);
							}
							else {
								//En caso contrario, se observa la precedencia del primer operador de la pila.
								//Si es mayor o igual que la del operador recién leído, se extrae de la pila y se opera.
								//Este proceso se repite hasta encontrar en la pila un operador de menor precedencia, un paréntesis abierto (que tiene precedencia 0) o hasta que la pila quede vacía.
								//Finalmente, se inserta el operador recién creado.
								Token top = opStack.peek();
								int topPrecedence = precedence(top.getType());
								int currentPrecedence = precedence(current.getType());
								
								while (topPrecedence >= currentPrecedence && opStack.isEmpty() == false) {
									applyOp();
									
									if (opStack.isEmpty() == false) {
										top = opStack.peek();
										topPrecedence = precedence(top.getType());
									}
								}
								
								opStack.push(current);
								
							}
							break;
						default:
							valStack.clear();
							opStack.clear();
							InvalidCharacterException ex = new InvalidCharacterException("Solamente se admiten operadores binarios tras una cadena numérica o un cierre de paréntesis.");
							ex.setLine(fd.getLineNumber());
							throw ex;
					}
					
					lastToken = current.getType();
					fd.increaseIndex();
				}
			}
			
			c = fd.peekChar(true);
			
		}
		
		/**
		 * CASOS POR LOS QUE UNA EXPRESIÓN PUEDE TERMINAR DE LEERSE SIN ERRORES Y ESTAR MAL:
		 * > Número de paréntesis distinto de 0.
		 * > El último token leído es distinto de una cadena DEC/HEX o de un paréntesis cerrado.
		 */
		if (parCount != 0) {
			valStack.clear();
			opStack.clear();
			ExpressionIntegrityException ex = new ExpressionIntegrityException("Se ha finalizado la expresión con paréntesis sin cerrar.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		if (lastToken != TokenType.HEX && lastToken != TokenType.DEC && lastToken != TokenType.R_PAR) {
			valStack.clear();
			opStack.clear();
			ExpressionIntegrityException ex = new ExpressionIntegrityException("La expresión solamente puede finalizar mediante una cadena numérica o un paréntesis cerrado.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		//Una vez se ha procesado la expresión completa, se procede a vaciar la pila de operadores si aún quedasen elementos dentro.
		//Finalmente, se usa el último token restante en la pila de valores para crear el Data que se envía de vuelta.
		while (opStack.isEmpty() == false)
			applyOp();

		val = valStack.pop().getValue();
		
		valStack.clear();
		opStack.clear();
		
		return new RWData(val);
	}
	
	private void applyOp() {
		Token op;
		ValueToken val1, val2;
		int result;

		validateOpStack();
		validateValStack();
		
		op = opStack.pop();
		switch (op.getType()) {
			case L_PAR:
				return;
			case NEG:
				val2 = valStack.pop();
				if (val2.getType() == TokenType.HEX) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("No se puede aplicar un cambio de signo a un valor hexadecimal.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				result = 0 - val2.getValue();

				if (val2.getValue() == RWData.MIN_VALUE) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("El cambio de signo de " + RWData.MIN_VALUE + " produce desbordamiento.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				
				break;
			case DIV:
				val2 = valStack.pop();
				if (val2.getValue() == 0) {
					valStack.clear();
					opStack.clear();
					DivideByZeroException ex = new DivideByZeroException();
					ex.setLine(fd.getLineNumber());
					throw ex;
				}

				validateValStack();
				val1 = valStack.pop();
				result = val1.getValue() / val2.getValue();
				
				if (val1.getValue() == RWData.MIN_VALUE && val2.getValue() == -1) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("La división " + RWData.MIN_VALUE + "/(-1) produce desbordamiento.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				break;
			case MOD:
				val2 = valStack.pop();
				if (val2.getValue() == 0) {
					valStack.clear();
					opStack.clear();
					DivideByZeroException ex = new DivideByZeroException();
					ex.setLine(fd.getLineNumber());
					throw ex;
				}

				validateValStack();
				val1 = valStack.pop();
				result = val1.getValue() % val2.getValue();
				break;
			case MUL:
				val2 = valStack.pop();
				validateValStack();
				val1 = valStack.pop();
				result = val1.getValue() * val2.getValue();
				
				if (result == RWData.MIN_VALUE && (val1.getValue() == -1 || val2.getValue() == -1)) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("La multiplicación " + RWData.MIN_VALUE + "*(-1) produce desbordamiento.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				
				if (result < RWData.MIN_VALUE || result > RWData.MAX_UNSIGNED_VALUE) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("El valor " + result + " excede los límites permitidos.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				break;
			case ADD:
				val2 = valStack.pop();
				validateValStack();
				val1 = valStack.pop();
				result = val1.getValue() + val2.getValue();
				if (result < RWData.MIN_VALUE || result > RWData.MAX_UNSIGNED_VALUE) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("El valor " + result + " excede los límites permitidos.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				break;
			case SUB:
				val2 = valStack.pop();
				validateValStack();
				val1 = valStack.pop();
				result = val1.getValue() - val2.getValue();
				

				if (val1.getValue() == 0 && val2.getValue() == RWData.MIN_VALUE) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("La resta 0-(" + RWData.MIN_VALUE + ") produce desbordamiento.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				
				if (result < RWData.MIN_VALUE || result > RWData.MAX_UNSIGNED_VALUE) {
					valStack.clear();
					opStack.clear();
					InvalidValueException ex = new InvalidValueException("El valor " + result + " excede los límites permitidos.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
				break;
			
			//Nunca se debería llegar aquí:
			default:
				result = Integer.MAX_VALUE;
				break;
		}
		valStack.push(new ValueToken(TokenType.DEC, result));
	}
	
	private void validateOpStack() {
		if (opStack.isEmpty()) {
			valStack.clear();
			opStack.clear();
			ExpressionIntegrityException ex = new ExpressionIntegrityException("No quedan operadores que extraer de la pila.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
	}
	
	private void validateValStack() {
		if (valStack.isEmpty()) {
			valStack.clear();
			opStack.clear();
			ExpressionIntegrityException ex = new ExpressionIntegrityException("Se ha intentado extraer un valor de una pila de operandos vacía.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
	}
	
	private int precedence(TokenType t) {
		int pre;
		
		switch(t) {
			case NEG:
				pre = 3;
				break;
			case DIV:
			case MOD:
			case MUL:
				pre = 2;
				break;
			case ADD:
			case SUB:
				pre = 1;
				break;
			
			//Nunca debería llegarse aquí.
			default:
				pre = 0;
				break;
		}
		
		return pre;
	}
	
	private ValueToken parseValueToken() {
		StringBuilder sb = new StringBuilder("");
		ValueToken vt = null;
		char c = fd.consumeChar(true);
		int val = Integer.MAX_VALUE;
		
		//Si el primer carácter es un 0, se comprueba si el siguiente es x/X, y si el siguiente es un dígito
		//o una letra perteneciente al conjunto hexadecimal. Si no lo es, se producirá un error; si lo es,
		//se seguirán leyendo tantos dígitos/caracteres hexadecimales como haya hasta encontrar uno distinto.
		if (c == '0') {
			sb.append(c);
			c = fd.peekChar(false);
			if (Character.isDigit(c)) {
				sb.append(fd.processDecimal(false));
				val = Integer.parseInt(sb.toString(), 10);
				vt = new ValueToken(TokenType.DEC, val);
			}
			else if (c == 'x' || c == 'X') {
				fd.consumeChar(false); //Nos saltamos la X
				c = fd.consumeChar(false);
				if (fd.isHexChar(c)) {
					sb.append(c);
					sb.append(fd.processHex(false));
					val = Integer.parseInt(sb.toString(), 16);
					vt = new ValueToken(TokenType.HEX, val);
				}
				else {
					valStack.clear();
					opStack.clear();
					InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un valor hexadecimal, se ha recibido '" + c + "'.");
					ex.setLine(fd.getLineNumber());
					throw ex;
				}
			}
			else {
				val = 0;
				vt = new ValueToken(TokenType.HEX, val);
			}
		}
		
		//Si no empieza un 0, pero sí por otro dígito, se introduce dicho dígito en la cadena y tantos dígitos como lea después.
		else if (Character.isDigit(c)) {
			sb.append(c);
			sb.append(fd.processDecimal(false));
			val = Integer.parseInt(sb.toString(), 10);
			vt = new ValueToken(TokenType.DEC, val);
		}
		
		else {
			valStack.clear();
			opStack.clear();
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un valor numérico, se ha recibido '" + c + "'.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}
		
		if (val < RWData.MIN_VALUE || val > RWData.MAX_UNSIGNED_VALUE) {
			valStack.clear();
			opStack.clear();
			InvalidValueException ex = new InvalidValueException("El valor " + val + " excede los límites permitidos.");
			ex.setLine(fd.getLineNumber());
			throw ex;
		}

		return vt;
		
	}	
}
