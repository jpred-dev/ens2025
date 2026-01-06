package controlunit.assembler.fileparser;

import controlunit.assembler.fileparser.expressionparser.ExpressionParser;
import controlunit.assembler.fileparser.filedata.FileData;
import controlunit.assembler.fileparser.instructionparser.InstructionParser;
import controlunit.assembler.util.InstructionBlueprint;
import customexception.EmptyFileException;
import customexception.FileManagementException;
import data.RData;
import enums.InstructionType;

public class FileParser {
	private FileData fd;
	private InstructionParser ip;
	private ExpressionParser xp;
	
	public FileParser() {
		fd = null;
		ip = new InstructionParser();
		xp = new ExpressionParser();
	}
	
	public void loadFile(String fileName) {
		fd = new FileData(fileName);
		
		if (fd.isEmpty())
			throw new EmptyFileException();
		
		ip.loadFileData(fd);
		xp.loadFileData(fd);
	}
	
	public void close() {
		fd = null;
		ip.close();
		xp.close();
	}
	
	public InstructionBlueprint parseInstruction(InstructionType it) {
		checkForFileLoaded();
		return ip.parseInstruction(it);
	}
	
	public RData getData(boolean ignoreSpaces) {
		checkForFileLoaded();
		return fd.getData(ignoreSpaces);
	}
	
	public String getMnemonic(boolean ignoreSpaces) {
		checkForFileLoaded();
		return fd.getMnemonic(ignoreSpaces);
	}
	
	public RData parseExpression() {
		checkForFileLoaded();
		return xp.parseExpression();
	}
	
	public boolean seekEOS() {
		checkForFileLoaded();
		return fd.seekEOS();
	}
	
	public boolean seekChar(char c, boolean ignoreSpaces) {
		checkForFileLoaded();
		return fd.seekChar(c, ignoreSpaces);
	}
	
	public char peekChar(boolean ignoreSpaces) {
		checkForFileLoaded();
		return fd.peekChar(ignoreSpaces);
	}

	public char consumeChar(boolean ignoreSpaces) {
		checkForFileLoaded();
		return fd.consumeChar(ignoreSpaces);
	}
	
	public boolean newLine() {
		checkForFileLoaded();
		return fd.newLine();
	}

	public int getLineNumber() {
		checkForFileLoaded();
		return fd.getLineNumber();
	}
	
	private void checkForFileLoaded() {
		if (fd == null)
			throw new FileManagementException("No se ha cargado ningún fichero.");
	}
}
