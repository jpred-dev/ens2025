package ens2025.gui.common.adapters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import controlunit.ControlUnit;
import customexception.CustomException;
import customexception.FileManagementException;
import data.RData;
import data.RWData;
import ens2025.gui.common.properties.DataProperty;
import enums.Flag;
import enums.RegisterID;
import io.IOInterface;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import settings.StackGrowth;

public class ControlUnitAdapter {
	private ControlUnit cu;
	private IOInterface io;
	private MemoryAdapter mem;
	private RegisterBankAdapter reg;
	private SettingsAdapter sa;
	private DisassembledCodeAdapter dc;
	private BooleanProperty isRunning;
	
	public ControlUnitAdapter(ControlUnit cu, SettingsAdapter sa, IOInterface io) {
		this.cu = cu;
		this.sa = sa;
		this.io = io;
		
		isRunning = new SimpleBooleanProperty(false);
		
		mem = new MemoryAdapter(cu, sa);
		reg = new RegisterBankAdapter(cu, sa);
		dc = new DisassembledCodeAdapter(cu, sa);
		
		setBehavior();
	}
	
	private void setBehavior() {
		sa.getNumericalRepresentationProperty().addListener((observable, oldVal, newVal) -> {
			mem.refresh();
			reg.reload();
			dc.refresh();
			dc.setInstructionAtPCChangedProperty(true);
		});
		
		sa.getStackGrowthProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				RWData index = new RWData();
				if (sa.getStackGrowth() == StackGrowth.POSITIVE)
					for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
						index.assign(i);
						if (sa.getStackStart().isLessEqual(index) && cu.readRegister(RegisterID.SP).isGreaterEqual(index))
							mem.setStackProperty(i, true);
						else
							mem.setStackProperty(i, false);
					}
				else
					for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
						index.assign(i);
						if (cu.readRegister(RegisterID.SP).isLessEqual(index) && sa.getStackStart().isGreaterEqual(index))
							mem.setStackProperty(i, true);
						else
							mem.setStackProperty(i, false);
					}
			});
		});
		
		reg.getRegisterDataProperty(RegisterID.PC).addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				DataProperty prev = new DataProperty(new RWData(oldVal), sa, false);
				DataProperty current = new DataProperty(new RWData(newVal), sa, false);
				mem.setPC(prev, false);
				mem.setPC(current, true);
				dc.setPC(current);
			});
		});
		
		
		sa.getCodeStartProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCodeStart(newVal);
				mem.updateCodeZone();
			});
		});
		
		
		sa.getCodeEndProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCodeEnd(newVal);
				mem.updateCodeZone();
			});
		});
		
		sa.getStackStartProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setStackStart(newVal);
				mem.updateStackZone();
			});
		});
	}
	
	
	/***********************************
	 * 								   *
	 *   MÉTODOS RELATIVOS A MEMORIA   *
	 *								   *
	 ***********************************/
	
	public void writeMemory(RData address, DataProperty value) {
		mem.writeAt(address, value);
		reload();
	}
	
	public DataProperty fetchMemory(RData address) {
		return mem.fetchAt(address);
	}
	
	public ObservableList<MemoryCell> getMemoryCells() {
		return mem.getMemoryCells();
	}

	public void wipeMemory() {
		mem.wipe();
		dc.reload();
	}

	public void setMemoryStackProperty(int address, boolean value) {
		mem.setStackProperty(address, value);
	}

	public void setIXProperty(RData address, boolean value) {
		mem.setIXProperty(address, value);
	}

	public void setIYProperty(RData address, boolean value) {
		mem.setIYProperty(address, value);
	}

	public void updateStackZone() {
		mem.updateStackZone();
	}

	
	/*************************************
	 * 								     *
	 *   MÉTODOS RELATIVOS A REGISTROS   *
	 *								     *
	 *************************************/
	
	public void setRegister(RegisterID r, DataProperty value) {
		reg.setRegister(r, value);
	}

	public void setRegister(RegisterID r, RData value) {
		reg.setRegister(r, value);
	}
	
	public DataProperty readRegisterDataProperty(RegisterID r) {
		return reg.getRegisterDataProperty(r);
	}

	public RData readRegisterData(RegisterID r) {
		return reg.getRegisterData(r);
	}
	
	public void resetRegisters() {
		reg.resetRegisters();
		dc.reload();
	}

	public BooleanProperty getFlagProperty(Flag flag) {
		return reg.getFlagProperty(flag);
	}

	public void setFlag(Flag flag, Boolean status) {
		cu.setFlag(flag, status);
		reg.reload();
	}

	
	/*******************************************
	 * 										   *
	 *   MÉTODOS RELATIVOS AL DESENSAMBLADOR   *
	 *										   *
	 *******************************************/	
	
	public ObservableList<DisassembledInstCell> getDisassembledInstCells() {
		return dc.getCells();
	}

	public void toggleBreakpoint(int index) {
		RWData address = new RWData(dc.getAddress(index));
		int size = dc.getInstructionSize(index);
		int initial = address.getValue();
		boolean val = dc.getBreakpoint(index);
		
		for (int i=initial; i < initial+size; i++) {
			cu.setBreakpoint(address, !val);
			address.add(1);
		}
		dc.setBreakpoint(index, !val);
	}

	public DataProperty getDisassembleStart() {
		return dc.getDisassembleStart();
	}

	public void setDisassembleStart(DataProperty address) {
		dc.setDisassembleStart(address);
	}

	public void resetBreakpoints() {
		RWData address = new RWData();
		for (int i=0; i<dc.getCells().size(); i++) {
			cu.setBreakpoint(address, false);
			dc.setBreakpoint(i, false);
			address.add(1);
		}
	}
	
	public String getDisassembledInstructionAtPC() {
		return cu.disassembleInstructions(cu.readRegister(RegisterID.PC), 1).get(0).getInstructionString();
	}
	
	public BooleanProperty getInstructionAtPCChangedProperty() {
		return dc.getInstructionAtPCChangedProperty();
	}
	
	public void signalMemoryUpdate(DataProperty data) {
		dc.setLastUpdatedProperty(data);
		dc.signalMemoryUpdate();
	}

	
	/************************************
	 * 									*
	 *   MÉTODOS RELATIVOS A FICHEROS   *
	 *									*
	 ************************************/		
	
	
	public void assembleFile(String filePath) {
		try {
			cu.assembleFile(filePath);
			sa.reloadZones();
			reload();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
	}
	
	public void loadMemoryFromFile(String filePath) {
		try {
			cu.loadMemoryFromFile(filePath);
			sa.reloadZones();
			reload();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
	}

	public void writeMemoryToFile(String filePath) {
		try {
			cu.writeMemoryToFile(filePath);
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
	}

	public void saveLogs(String fileName, String log, String console) {
		try {
			FileWriter fw = new FileWriter(fileName);
			PrintWriter  pw = new PrintWriter(fw);
			pw.println("---- Mensajes ----");
			pw.println(log);
			pw.println();
			pw.println("---- Consola ----");
			pw.println(console);
			pw.println();
			pw.close();
		} catch (IOException e) {
			//Se genera una excepción específica y se captura internamente; es responsabilidad del CUA gestionarla.
			try {
				throw new FileManagementException("Error guardando los mensajes y la consola del programa.");
			} catch (FileManagementException ex) {
				io.printlnLog(ex.getMessage());
			}
		}
	}
	
	
	/**************************************
	 * 									  *
	 *   MÉTODOS RELATIVOS A SIMULACIÓN   *
	 *									  *
	 **************************************/	
	
	public void run() {
		try {
			cu.runSimulation();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		Platform.runLater(() -> {
			reload();
			
		});
	}

	public void step() {
		try {
			cu.runSimulationOnce();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		Platform.runLater(() -> {
			reload();
			
		});
	}

	public void debug() {
		try {
			cu.debugSimulation();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		Platform.runLater(() -> {
			reload();
			
		});
	}
	
	
	/*********************
	 * 					 *
	 *   OTROS MÉTODOS   *
	 *					 *
	 *********************/	
	
	
	public void reload() {
		mem.reload();
		reg.reload();
		dc.reload();
	}
	
	public AtomicBoolean getStopFlag() {
		return cu.getStopFlag();
	}
	
	public BooleanProperty getRunningProperty() {
		return isRunning;
	}
	
	public void setRunningProperty(boolean value) {
		isRunning.set(value);
	}


}
