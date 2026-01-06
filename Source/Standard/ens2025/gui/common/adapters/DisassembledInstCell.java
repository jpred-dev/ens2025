package ens2025.gui.common.adapters;

import controlunit.ControlUnit;
import controlunit.disassembler.DisassembledInstruction;
import ens2025.gui.common.properties.DataProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DisassembledInstCell {
	private DataProperty address;
	private StringProperty instruction;
	private BooleanProperty isPC;
	private BooleanProperty breakpoint;
	private int size;

	public DisassembledInstCell(ControlUnit cu, SettingsAdapter sa, DisassembledInstruction inst) {
		address = new DataProperty(inst.getAddress(), sa, true);
		instruction = new SimpleStringProperty(inst.getInstructionString());
		isPC = new SimpleBooleanProperty(false);
		breakpoint = new SimpleBooleanProperty(inst.hasBreakpoint());
		size = inst.getSize();
	}
	
	public DataProperty getAddressProperty() {
		return address;
	}
	
	public StringProperty getInstructionProperty() {
		return instruction;
	}
	
	public BooleanProperty getPCProperty() {
		return isPC;
	}
	
	public BooleanProperty getBreakpointProperty() {
		return breakpoint;
	}
	
	public void setPCProperty(boolean value) {
		isPC.set(value);
	}
	
	public void setBreakpointProperty(boolean value) {
		breakpoint.set(value);
	}

	public int getInstructionSize() {
		return size;
	}
}
