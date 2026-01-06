package ens2025.gui.common.adapters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import controlunit.ControlUnit;
import data.RData;
import ens2025.gui.common.properties.DataProperty;
import enums.Flag;
import enums.RegisterID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class RegisterBankAdapter {
	private Map<RegisterID, DataProperty> registers;
	private Map<Flag, BooleanProperty> flags;
	private ControlUnit cu;
	private static final Set<RegisterID> ADDRESS_REGISTERS = new HashSet<>(Arrays.asList(RegisterID.PC, RegisterID.SP, RegisterID.IX, RegisterID.IY));
	
	public RegisterBankAdapter(ControlUnit cu, SettingsAdapter sa) {
		RData reg;
		
		this.cu = cu;
		registers = new HashMap<>();
		for (RegisterID r : RegisterID.values()) {
			reg = cu.readRegister(r);
			if (ADDRESS_REGISTERS.contains(r))
				registers.put(r, new DataProperty(reg, sa, true));
			else
				registers.put(r, new DataProperty(reg, sa, false));
		}
		
		flags = new HashMap<>();
		for (Flag f : Flag.values()) {
			flags.put(f, new SimpleBooleanProperty(cu.getFlag(f)));
		}
	}
	
	public void reload() {
		reloadRegisters();
		reloadFlags();
	}
	
	private void reloadRegisters() {
		for (RegisterID r : RegisterID.values()) 
			registers.get(r).set(cu.readRegister(r));
	}
	
	private void reloadFlags() {
		for (Flag f : Flag.values())
			flags.get(f).set(cu.getFlag(f));
	}
	
	public void setRegister(RegisterID r, DataProperty value) {
		cu.writeRegister(r, value.getData());
		if (r == RegisterID.SR)
			reloadFlags();
		registers.get(r).set(value);
	}

	public void setRegister(RegisterID r, RData value) {
		cu.writeRegister(r, value);
		if (r == RegisterID.SR)
			reloadFlags();
		registers.get(r).set(value);
	}
	
	public DataProperty getRegisterDataProperty(RegisterID r) {
		return registers.get(r);
	}

	public RData getRegisterData(RegisterID r) {
		return registers.get(r).getData();
	}

	public void resetRegisters() {
		cu.resetRegisters();
		reload();
	}

	public BooleanProperty getFlagProperty(Flag flag) {
		return flags.get(flag);
	}
}
