package ens2025.gui.common.adapters;

import data.RData;
import ens2025.gui.common.properties.DataProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MemoryCell {
	private DataProperty address;
	private DataProperty value;
	private BooleanProperty isCode;
	private BooleanProperty isStack;
	private BooleanProperty isPC;
	private BooleanProperty isIX;
	private BooleanProperty isIY;

	
	public MemoryCell(RData address, RData value, SettingsAdapter sa) {
		this.address = new DataProperty(address, sa, true);
		this.value = new DataProperty(value, sa, false);
		isPC = new SimpleBooleanProperty(false);
		isCode = new SimpleBooleanProperty(false);
		isStack = new SimpleBooleanProperty(false);
		isIX = new SimpleBooleanProperty(false);
		isIY = new SimpleBooleanProperty(false);
	}
	
	public DataProperty getAddressProperty() {
		return address;
	}
	
	public DataProperty getValueProperty() {
		return value;
	}
	
	public BooleanProperty getPCProperty() {
		return isPC;
	}
	
	public BooleanProperty getCodeProperty() {
		return isCode;
	}
	
	public BooleanProperty getStackProperty() {
		return isStack;
	}
	
	public BooleanProperty getIXProperty() {
		return isIX;
	}
	
	public BooleanProperty getIYProperty() {
		return isIY;
	}
	
	public void setCodeProperty(boolean value) {
		isCode.set(value);
	}
	
	public void setStackProperty(boolean value) {
		isStack.set(value);
	}
	
	public void setPCProperty(boolean value) {
		isPC.set(value);
	}
	
	public void setIXProperty(boolean value) {
		isIX.set(value);
	}
	
	public void setIYProperty(boolean value) {
		isIY.set(value);
	}
}
