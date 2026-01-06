package controlunit.assembler.fileparser.filedata;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import customexception.CustomException;
import customexception.FileManagementException;
import customexception.InvalidCharacterException;
import data.RData;
import data.RWData;

public class FileData {
	private List<String> lines;
	private String currentLine;
	private int stringIndex;
	private int listIndex;

	private static final Set<Character> HEX_LETTERS = new HashSet<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f'));

	public FileData(String fname) {
		try {
			lines = Files.readAllLines(Paths.get(fname), Charset.forName("UTF-8"));
		} catch (MalformedInputException ex) {
			throw new FileManagementException("Se ha detectado un fichero con codificación incompatible con UTF-8.\n"
					+ "Prueba a eliminar caracteres especiales (palabras con acento, ñ, etc) de tus comentarios, "
					+ "o recodifica el documento como UTF-8 sin BOM.");
		} catch (IOException ex) {
			throw new FileManagementException("Error en la lectura del fichero de código.");
		}

		stringIndex = 0;
		listIndex = 0;
		
		if (lines.size() == 0)
			currentLine = null;
		else {
			String[] lineChunks = lines.get(listIndex).split(";");
			
			if (lineChunks.length == 0) //Evitamos caso límite de ';' como único carácter de la línea.
				currentLine = "";
			else
				currentLine = lineChunks[0];
		}
	}
	
	public boolean isEmpty() {
		if (currentLine == null)
			return true;
		return false;
	}
	
	public boolean newLine() {
		String[] lineChunks;
		listIndex++;
		if (listIndex == lines.size())
			return false;
		
		lineChunks = lines.get(listIndex).split(";");
		
		if (lineChunks.length == 0) //Evitamos caso límite de ';' como único carácter de la línea.
			currentLine = "";
		else
			currentLine = lineChunks[0];
		stringIndex = 0;
		return true;
	}
	
	public void increaseIndex() {
		stringIndex++;
	}
	
	public RData getData(boolean ignoreSpaces) {
		StringBuilder sb = new StringBuilder("");
		char c = consumeChar(ignoreSpaces);
		RData value;
		
		//Si el primer carácter es un 0, se comprueba si el siguiente es x/X, y si el siguiente es un dígito
		//o una letra perteneciente al conjunto hexadecimal. Si no lo es, se producirá un error; si lo es,
		//se seguirán leyendo tantos dígitos/caracteres hexadecimales como haya hasta encontrar uno distinto.
		if (c == '0') {
			sb.append(c);
			c = peekChar(false);
			if (Character.isDigit(c))
				sb.append(processDecimal(false));
			else if (c == 'x' || c == 'X') {
				sb.append(consumeChar(false));
				c = consumeChar(false);
				if (isHexChar(c)) {
					sb.append(c);
					sb.append(processHex(false));
				}
				else {
					InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no es válido.");
					ex.setLine(getLineNumber());
					throw ex;	
				}
			}
		}
		
		//Si no empieza un 0, pero sí por otro dígito, se introduce dicho dígito en la cadena y tantos dígitos como lea después.
		else if (Character.isDigit(c)) {
			sb.append(c);
			sb.append(processDecimal(false));
		}
		
		//Finalmente, si empieza por un símbolo negativo, debe ir seguido de uno o más decimales.
		else if (c == '-') {
			sb.append(c);
			c = peekChar(false);
			if (Character.isDigit(c) == false) {
				InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no es válido.");
				ex.setLine(getLineNumber());
				throw ex;	
			}
			increaseIndex();
			sb.append(c);
			sb.append(processDecimal(false));
		}
		
		else {
			InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no es válido.");
			ex.setLine(getLineNumber());
			throw ex;	
		}
		
		try {
			value = new RWData(sb.toString());
		} catch (CustomException ex) {
			ex.setLine(getLineNumber());
			throw ex;
		}
		
		return value;
	}
	
	public String processDecimal(boolean ignoreSpaces) {
		StringBuilder sb = new StringBuilder("");
		char c = peekChar(ignoreSpaces);
		
		while (Character.isDigit(c)) {
			sb.append(c);
			increaseIndex();
			c = peekChar(false);
		}
		
		return sb.toString();
	}
	
	public String processHex(boolean ignoreSpaces) {
		StringBuilder sb = new StringBuilder("");
		char c = peekChar(ignoreSpaces);
		
		while (isHexChar(c)) {
			sb.append(c);
			increaseIndex();
			c = peekChar(false);
		}
		
		return sb.toString();
	}
	
	public boolean isHexChar(char c) {
		if (Character.isDigit(c) || HEX_LETTERS.contains(c))
			return true;
		return false;
	}
	
	public String getMnemonic(boolean ignoreSpaces) {
		StringBuilder sb = new StringBuilder("");

		char c = peekChar(ignoreSpaces);
		
		//Si toda la cadena consiste en espacios en blanco o se ha llegado al final de cadena, se devuelve un string vacío.
		if (c == '\0')
			return "";
		if (Character.isLetter(c) == false) {
			InvalidCharacterException ex = new InvalidCharacterException("El carácter '" + c + "' no es válido.");
			ex.setLine(getLineNumber());
			throw ex;	
		}

		while (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
			sb.append(c);
			increaseIndex();
			c = peekChar(false);
		}
		
		return sb.toString();
	}
	
	public boolean seekEOS() {
		char c = peekChar(true);
		
		return (c == '\0');
	}
	
	public boolean seekChar(char c, boolean ignoreSpaces) {
		if (c != consumeChar(ignoreSpaces))
			return false;
		
		return true;
	}
	
	public char consumeChar(boolean ignoreSpaces) {
		char c = peekChar(ignoreSpaces);
		
		//No queremos intentar avanzar el índice cuando ya estamos en el último elemento de la cadena!
		if (c != '\0')
			increaseIndex();
		
		return c;
	}
	
	public char peekChar(boolean ignoreSpaces) {
		char c = currentChar();
		
		if (ignoreSpaces == true) {
			while (Character.isWhitespace(c)) {
				increaseIndex();
				c = currentChar();
			}
		}

		return c;
	}
	
	private char currentChar() {
		if (isEOS())
			return '\0';
		return currentLine.charAt(stringIndex);
	}

	public int getLineNumber() {
		return listIndex;
	}
	
	public boolean isEOS() {
		if (stringIndex == currentLine.length())
			return true;
		return false;
	}
}
