package controlunit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import controlunit.architecture.Architecture;
import controlunit.architecture.memory.Memory;
import controlunit.assembler.Assembler;
import controlunit.disassembler.DisassembledInstruction;
import controlunit.disassembler.Disassembler;
import controlunit.sim.Simulator;
import customexception.FileManagementException;
import data.RData;
import data.RWData;
import enums.Flag;
import enums.RegisterID;
import io.IOInterface;
import settings.Settings;

public class ControlUnit {
	private Architecture arc;
	private Disassembler disassembler;
	private Simulator sim;
	private Assembler assembler;
	private Settings set;
	
	public ControlUnit(Settings set, IOInterface io) {
		arc = new Architecture(set);
		assembler = new Assembler(set, io);
		disassembler = new Disassembler(arc, set);
		sim = new Simulator(set, arc, io);
		this.set = set;
	}
	
	//Métodos de ensamblado
	public void assembleFile(String fileName) {
		Memory mem = assembler.processFile(fileName);
		
		arc.loadMemory(mem);
		set.recalcStackStart();
		arc.resetRegisterBank();
	}
	
	//Métodos de simulación.
	public void runSimulation() {
		sim.runSimulation();		
	}
	
	public void debugSimulation() {
		sim.debugSimulation();
	}
	
	public void runSimulationOnce() {
		sim.runSimulationOnce();
	}
	
	//Métodos de desensamblado.
	public List<DisassembledInstruction> disassembleInstructions(RData index, int n) {
		return disassembler.disassembleInstructions(index, n);
	}
	
	//Métodos de comunicación con la arquitectura.
	//Pertinentes a la memoria.
	public boolean hasBreakpoint(RData index) {
		return arc.hasBreakpoint(index);
	}
	
	public void setBreakpoint(RData index, boolean value) {
		arc.setBreakpoint(index, value);
	}
	
	public RData readMemory(RData index) {
		return arc.readMemory(index);
	}
	
	public void wipeMemory() {
		arc.wipeMemory();
	}
	
	public void writeMemory(RData index, RData value) {
		arc.writeMemory(index, value);
	}

	//Pertinentes al banco de registros.
	public RData getStackPointer() {
		return arc.getStackPointer();
	}

	public RData getProgramCounter() {
		return arc.getProgramCounter();
	}
	
	public RData readRegister(RegisterID reg) {
		return arc.readRegister(reg);
	}
	
	public void writeRegister(RegisterID reg, RData value) {
		arc.writeRegister(reg, value);
	}
	
	public boolean getFlag(Flag f) {
		return arc.getFlag(f);
	}
	
	public void setFlag(Flag f, boolean status) {
		arc.setFlag(f, status);
	}
	
	public void resetRegisters() {
		arc.resetRegisterBank();
	}
	
	//Otros.
	
	//Volcado de datos en fichero.
	public void writeMemoryToFile(String name) {
		try (OutputStream st = new FileOutputStream(name)) {
			writeMemoryToStream(st);
			writeRegisterBankToStream(st);
			writeZoneBoundariesToStream(st);
		} catch (FileNotFoundException e) {
			throw new FileManagementException("Error durante la apertura del fichero.");
		} catch (IOException e) {
			throw new FileManagementException("Error durante la escritura del fichero.");
		}
	}
	
	//Volcado de datos de la memoria.
	private void writeMemoryToStream(OutputStream stream) throws IOException {
		Memory mem = arc.dumpMemory();
		
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			mem.fetchAt(i).writeToStream(stream);
		}
	}
	
	//Volcado de datos del banco de registros.
	private void writeRegisterBankToStream(OutputStream stream) throws IOException {
		for (RegisterID reg : RegisterID.values())
			readRegister(reg).writeToStream(stream);
	}
	
	//Volcado de las zonas de código y pila.
	private void writeZoneBoundariesToStream(OutputStream stream) throws IOException {
		set.getCodeStart().writeToStream(stream);
		set.getCodeEnd().writeToStream(stream);
		set.getStackStart().writeToStream(stream);
	}
	
	//Recuperación de datos de un fichero.
	public void loadMemoryFromFile(String name) {
		try (InputStream st = new FileInputStream(name)) {
			loadMemoryFromStream(st);
			loadRegisterBankFromStream(st);
			loadZoneBoundariesFromStream(st);
		} catch (FileNotFoundException e) {
			throw new FileManagementException("Error durante la apertura del fichero.");
		} catch (IOException e) {
			throw new FileManagementException("Error durante la lectura del fichero.");
		}
	}
	
	//Recuperación de datos de la memoria.
	private void loadMemoryFromStream(InputStream stream) throws IOException {
		RWData value = new RWData(0);
		Memory mem = new Memory();
		
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			value.readFromStream(stream);
			mem.writeAt(i, value);
		}
		
		arc.loadMemory(mem);
	}
	
	//Recuperación de datos del banco de registros.
	private void loadRegisterBankFromStream(InputStream stream) throws IOException {
		RWData value = new RWData();
		Map<RegisterID, RData> registers = new HashMap<>();
		
		for (RegisterID reg : RegisterID.values()) {
			value.readFromStream(stream);
			registers.put(reg, value.getImmutableCopy());
		}
		
		for (RegisterID reg : RegisterID.values()) {
			writeRegister(reg, registers.get(reg));
		}
	}
	
	//Recuperación de las zonas de código y pila.
	private void loadZoneBoundariesFromStream(InputStream stream) throws IOException {
		RWData codeStart = new RWData();
		RWData codeEnd = new RWData();
		RWData stackStart = new RWData();
		
		codeStart.readFromStream(stream);
		codeEnd.readFromStream(stream);
		stackStart.readFromStream(stream);
		
		set.setCodeStart(codeStart);
		set.setCodeEnd(codeEnd);
		set.setStackStart(stackStart);
	}
	
	public AtomicBoolean getStopFlag() {
		return sim.getStopFlag();
	}
	
	public void setStopFlag(Boolean value) {
		sim.setStopFlag(value);
	}
}

