package controlunit.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controlunit.architecture.memory.Memory;
import controlunit.assembler.fileparser.FileParser;
import controlunit.assembler.memorywriter.MemoryWriter;
import controlunit.assembler.util.InstructionBlueprint;
import controlunit.assembler.util.Reference;
import customexception.EmptyFileException;
import customexception.InvalidCharacterException;
import customexception.LabelException;
import data.RData;
import data.RWData;
import enums.InstructionType;
import io.IOInterface;
import settings.Settings;

public class Assembler {
	private String waitingLabel;
	private Map<String, RData> labelMap;
	private List<Reference> refList;
	private boolean endProcessing;
	private FileParser fp;
	private MemoryWriter mw;
	private IOInterface io;
	
	private static final Map<Character, Character> ESCAPE_MAP;
	static {
		Map<Character, Character> temp = new HashMap<>();
		temp.put('n', '\n');
		temp.put('t', '\t');
		temp.put('"', '\"');
		temp.put('\\', '\\');
		temp.put('0', '\0');
		ESCAPE_MAP = Collections.unmodifiableMap(temp);
	}
	
	public Assembler(Settings set, IOInterface io) {
		this.io = io;
		fp = new FileParser();
		mw = new MemoryWriter(set);
	}
	
	private void loadFile(String fileName) {
		waitingLabel = null;
		mw.reset();
		refList = new ArrayList<>();
		labelMap = new HashMap<>();
		fp.loadFile(fileName);
	}
	
	private void close() {
		fp.close();
		mw.close();
		refList = null;
		labelMap = null;
	}
	
	public Memory processFile(String fileName) {
		String mnemonic = null;
		Memory mem;
		
		io.printlnLog("Ensamblando fichero " + fileName);
		
		try {
			loadFile(fileName);
			endProcessing = false;
		} catch (EmptyFileException ex) {
			endProcessing = true;
		}
		
		
		while (endProcessing == false) {
			mnemonic = fp.getMnemonic(true);
			
			//Si se recibe un string vacío significa que se ha alcanzado un final de línea (si previamente se ha leído una etiqueta) o que la línea solamente contiene caracteres de espacio.
			if (mnemonic.isEmpty()) {
				//Se solicita una nueva línea y se reinicia el bucle. Si resulta ser la última línea, primero se indica que debe terminar al iniciar la siguiente iteración.
				if (fp.newLine() == false)
					endProcessing = true;
				continue;
			}
			
			if (isAllCase(mnemonic) == true) {
				String temp = mnemonic.toUpperCase();
				
				DirectiveType dir = DirectiveType.getFromName(temp);
				if (dir != null) {
					processDirective(dir);
					continue; //para evitar tanto una estructura de ifs anidados como llamadas posteriores innecesarias si se trata de una directiva
				}
				
				InstructionType iType = InstructionType.getFromName(temp);
				if (iType != null) {
					processInstruction(iType);
					continue;
				}
				
				processLabel(mnemonic);
			}
			else
				processLabel(mnemonic);
			
			if (fp.peekChar(true) == '\0' && fp.newLine() == false)
				endProcessing = true;
		}

		//Si queda una etiqueta vacía al final del procesado, se le asigna el último valor conocido de puntero de escritura.
		manageLabel(mw.getPointerData());

		mw.writeReferences(refList, labelMap);
		
		mem = mw.getMemory();
		mw.commitZoneBoundaries();
		
		
		io.printlnLog("Líneas procesadas: " + (fp.getLineNumber()+1));
		io.printlnLog(fileName + " ensamblado correctamente.");
		
		close();
		
		return mem;
	}
	
	private void manageLabel(RData value) {
		if (waitingLabel != null) {
			labelMap.put(waitingLabel, value.getMutableCopy());
			waitingLabel = null;
		}
	}
	
	private void processDirective(DirectiveType dir) {
		switch(dir) {
			case END:
				processDirEND();
				break;
			case EQU:
				processDirEQU();
				break;
			case ORG:
				processDirORG();
				break;
			case RES:
				processDirRES();
				break;
			case DATA:
				processDirDATA();
				break;
		}
	}
	
	private void processDirEND() {
		if (fp.seekEOS() == false) {
			char c = fp.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba final de línea, se ha encontrado '" + c + "'.");
			ex.setLine(fp.getLineNumber());
			throw ex;
		}
		endProcessing = true;
	}
	
	private void processDirEQU() {
		RData value = new RWData(fp.parseExpression());
		manageLabel(value); //Si hay una etiqueta a la espera, se asigna el valor. Si no la hay, no se produce error, simplemente se pierde el valor.
	}
	
	private void processDirORG() {
		RData value = new RWData(fp.parseExpression());
		manageLabel(mw.getPointerData());
		mw.resetPointerOverflow();
		mw.setPointer(value);
	}
	
	private void processDirRES() {
		RData value = new RWData(fp.parseExpression());
		manageLabel(mw.getPointerData());
		mw.setReservedMemory(value);
	}
	
	private void processDirDATA() {
		RData number;
		char c = fp.peekChar(true);
		manageLabel(mw.getPointerData());

		if (c == '\"')
			processDataString();
		else if (Character.isDigit(c) || c == '-') {
			number = fp.getData(true);
			mw.writeData(number);
		}
		
		c = fp.consumeChar(true);
		while (c != '\0') {
			if (c != ',') {
				InvalidCharacterException ex = new InvalidCharacterException("Se esperaba el carácter ',' se ha recibido '" + c + "'.");
				ex.setLine(fp.getLineNumber());
				throw ex;
			}
			else if (fp.seekEOS()) {
				InvalidCharacterException ex = new InvalidCharacterException("La lista de datos de la directiva DATA no puede terminar en ','.");
				ex.setLine(fp.getLineNumber());
				throw ex;
			}
			
			c = fp.peekChar(true);
			
			if (c == '\"')
				processDataString();
			else if (Character.isDigit(c) || c == '-') {
				number = fp.getData(true);
				mw.writeData(number);
			}
			else {
				InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un entero o una cadena de caracteres encapsulada en comillas, se ha recibido '" + c + "'.");
				ex.setLine(fp.getLineNumber());
				throw ex;
			}
				
			
			c = fp.consumeChar(true);
		}

	}
	
	private void processDataString() {
		char c;
		char esc;
		RData data;
		
		c = fp.consumeChar(true);
		if (c != '\"') {
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba un carácter de comilla doble (\"), se ha recibido '" + c + "'.");
			ex.setLine(fp.getLineNumber());
			throw ex;
		}
		
		c = fp.consumeChar(false);
		while (c != '\"') {
			if (c == '\\') {
				esc = fp.consumeChar(false);
				if (ESCAPE_MAP.get(esc) == null){
					InvalidCharacterException ex = new InvalidCharacterException("'" + c + "" + esc + "'" + " no es una secuencia de escape válida.");
					ex.setLine(fp.getLineNumber());
					throw ex;
				}
				c = ESCAPE_MAP.get(esc);
			}
			
			data = new RWData(c);
			mw.writeData(data);
			c = fp.consumeChar(false);
		}
		mw.writeData(new RWData('\0'));
	}
	
	private boolean processLabel(String label) {
		if (fp.seekChar(':', true) == false) {
			char c = fp.peekChar(false);
			InvalidCharacterException ex = new InvalidCharacterException("Se esperaba el carácter ':' se ha recibido '" + c + "'.");
			ex.setLine(fp.getLineNumber());
			throw ex;
		}
		
		if (labelMap.containsKey(label)) {
			LabelException ex = new LabelException("La etiqueta '" + label + "' ya se ha definido con anterioridad.");
			ex.setLine(fp.getLineNumber());
			throw ex;
		}
		
		if (waitingLabel != null) {
			LabelException ex = new LabelException("No se pudo procesar la etiqueta '" + label + "' ya hay una etiqueta pendiente de asignación.");
			ex.setLine(fp.getLineNumber());
			throw ex;
		}
		
		labelMap.put(label, mw.getPointerData());
		waitingLabel = label;
		
		return true;
	}
	
	private void processInstruction(InstructionType it) {
		InstructionBlueprint ib = null;
		
		ib = fp.parseInstruction(it);

		manageLabel(mw.getPointerData());
		mw.encodeAndWrite(ib, refList);
	}
	
	private boolean isAllCase(String s) {
		if (s.equals(s.toLowerCase()) || s.equals(s.toUpperCase()))
			return true;
		return false;
	}

}

