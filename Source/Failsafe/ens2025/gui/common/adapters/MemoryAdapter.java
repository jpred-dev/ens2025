package ens2025.gui.common.adapters;

import controlunit.ControlUnit;
import data.RData;
import data.RWData;
import ens2025.gui.common.properties.DataProperty;
import enums.RegisterID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MemoryAdapter {
	private ObservableList<MemoryCell> cellList;
	private ControlUnit cu;
	private SettingsAdapter sa;

	public MemoryAdapter(ControlUnit cu, SettingsAdapter sa) {
		RData mem;
		RWData address = new RWData();
		MemoryCell cell;
		this.cu = cu;
		this.sa = sa;
		
		cellList = FXCollections.observableArrayList();
		
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			address.assign(i);
			mem = cu.readMemory(address);
			cell = new MemoryCell(address, mem, sa);
			
			cellList.add(cell);
			updateZoneProperties(cellList.get(i));
		}

		cellList.get(cu.readRegister(RegisterID.PC).getValue()).setPCProperty(true);
		cellList.get(cu.readRegister(RegisterID.IX).getValue()).setIXProperty(true);
		cellList.get(cu.readRegister(RegisterID.IY).getValue()).setIYProperty(true);
	}
	
	public ObservableList<MemoryCell> getMemoryCells() {
		return cellList;
	}
	
	public void refresh() {
		DataProperty addressProperty;
		DataProperty valueProperty;
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			addressProperty = cellList.get(i).getAddressProperty();
			valueProperty = cellList.get(i).getValueProperty();
			valueProperty.set(valueProperty.get());
			addressProperty.set(addressProperty.get());
		}
	}
	
	public void reload() {
		RWData address = new RWData(0);
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			cellList.get(i).getValueProperty().set(cu.readMemory(address));
			updateZoneProperties(cellList.get(i));
			address.add(1);
		}
	}
	
	public void writeAt(RData address, DataProperty data) {
		cu.writeMemory(address, data.getData());
		cellList.get(address.getValue()).getValueProperty().set(data.getData());
	}

	public DataProperty fetchAt(RData address) {
		return cellList.get(address.getValue()).getValueProperty();
	}
	
	public void setPC(DataProperty address, boolean value) {
		cellList.get(address.getData().getValue()).setPCProperty(value);
	}
	
	public void setCodeProperty(int address, boolean value) {
		cellList.get(address).setCodeProperty(value);
	}
	
	public void setStackProperty(int address, boolean value) {
		cellList.get(address).setStackProperty(value);
	}

	public void setIXProperty(RData address, boolean value) {
		cellList.get(address.getValue()).setIXProperty(value);
	}

	public void setIYProperty(RData address, boolean value) {
		cellList.get(address.getValue()).setIYProperty(value);
	}

	public void wipe() {
		RWData index = new RWData(0);
		RData wipe = new RWData(0);
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++) {
			cu.writeMemory(index, wipe);
			cellList.get(i).getValueProperty().set(new RWData(0));
			index.add(1);
		}
	}
	
	public void updateStackZone() {
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++)
			cellList.get(i).setStackProperty(isInStackZone(cellList.get(i)));
	}
	
	public void updateCodeZone() {
		for (int i=0; i<=RWData.MAX_UNSIGNED_VALUE; i++)
			cellList.get(i).setCodeProperty(isInCodeZone(cellList.get(i)));
	}
	
	private void updateZoneProperties(MemoryCell cell) {
		cell.setCodeProperty(isInCodeZone(cell));
		cell.setStackProperty(isInStackZone(cell));
	}

	private boolean isInCodeZone(MemoryCell cell) {
		RData address = cell.getAddressProperty().getData();
		
		if (address.isGreaterEqual(sa.getCodeStart()) && address.isLessEqual(sa.getCodeEnd()))
			return true;
		return false;
	}
	
	private boolean isInStackZone(MemoryCell cell) {
		RData address = cell.getAddressProperty().getData();
		
		if (address.isGreaterEqual(sa.getStackStart()) && address.isLessEqual(cu.getStackPointer()))
			return true;
		else if (address.isLessEqual(sa.getStackStart()) && address.isGreaterEqual(cu.getStackPointer()))
			return true;
		
		return false;
	}
}
