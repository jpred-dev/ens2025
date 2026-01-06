package ens2025.gui.common.adapters;

import java.util.List;

import controlunit.ControlUnit;
import controlunit.disassembler.DisassembledInstruction;
import data.RData;
import data.RWData;
import ens2025.gui.common.properties.DataProperty;
import enums.RegisterID;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DisassembledCodeAdapter {
	private ObservableList<DisassembledInstCell> cellList;
	private DataProperty startingIndex;
	private BooleanProperty hasMemoryUpdated;
	private DataProperty lastAddressUpdated;
	private ControlUnit cu;
	private SettingsAdapter sa;
	private BooleanProperty instructionAtPCChanged;
	
	public DisassembledCodeAdapter(ControlUnit cu, SettingsAdapter sa) {
		this.cu = cu;
		this.sa = sa;
		cellList = FXCollections.observableArrayList();
		startingIndex = new DataProperty(new RWData(0), sa, true);
		hasMemoryUpdated = new SimpleBooleanProperty(false);
		lastAddressUpdated = new DataProperty(new RWData(0), sa, true);
		instructionAtPCChanged = new SimpleBooleanProperty(false);
		
		
		generateList();
		setBehavior();
	}

	
	private void setBehavior() {
		hasMemoryUpdated.addListener((observable, oldVal, newVal) -> {
			//Solamente se actúa si el nuevo valor es true, para evitar entrar en bucle cuando al final del evento se ponga a false.
			if (newVal == true) {
				instructionAtPCChanged.set(true);
				//además, solamente se actúa si la dirección en la que se ha modificado el valor es posterior a la dirección de inicio de desensamblado actual.
				if (startingIndex.getData().isLessEqual(lastAddressUpdated.getData())) { 
					Platform.runLater(() -> {
						generateList();
						hasMemoryUpdated.set(false);
					});
				}
			}
		});
		
		startingIndex.addListener((observable, oldVal, newVal) -> {
			generateList();
		});
	}

	private void generateList() {
		List<DisassembledInstruction> instructions = cu.disassembleInstructions(startingIndex.getData(), RWData.MAX_UNSIGNED_VALUE);

		cellList.clear();

		for (int i=0; i<instructions.size(); i++) {
			cellList.add(new DisassembledInstCell(cu, sa, instructions.get(i)));
		}

		setPC(new DataProperty(cu.readRegister(RegisterID.PC), sa, true));
	}
	
	public BooleanProperty getUpdateFlagProperty() {
		return hasMemoryUpdated;
	}
	
	public DataProperty getLastUpdatedProperty() {
		return lastAddressUpdated;
	}
	
	public ObservableList<DisassembledInstCell> getCells() {
		return cellList;
	}

	public void setLastUpdatedProperty(DataProperty data) {
		this.lastAddressUpdated.set(data);
	}
	
	public void signalMemoryUpdate() {
		this.hasMemoryUpdated.set(true);
	}

	public void refresh() {
		generateList();
	}

	public RData getAddress(int index) {
		return cellList.get(index).getAddressProperty().getData();
	}
	
	public int getInstructionSize(int index) {
		return cellList.get(index).getInstructionSize();
	}
	
	public boolean getBreakpoint(int index) {
		return cellList.get(index).getBreakpointProperty().get();
	}

	public void setBreakpoint(int index, boolean value) {
		cellList.get(index).setBreakpointProperty(value);
	}

	public DataProperty getDisassembleStart() {
		return startingIndex;
	}

	public void setDisassembleStart(DataProperty address) {
		startingIndex.set(address);
	}

	public void setPC(DataProperty address) {
		Platform.runLater(() -> {
			instructionAtPCChanged.set(true);
			if (address.getData().isGreaterEqual(startingIndex.getData())) {
				for (int i=0; i<cellList.size(); i++) {
					if (cellList.get(i).getAddressProperty().getData().isEqual(address.getData()))
						cellList.get(i).setPCProperty(true);
					else
						cellList.get(i).setPCProperty(false);
				}
			}
		});
	}
	
	public BooleanProperty getInstructionAtPCChangedProperty() {
		return instructionAtPCChanged;
	}
	
	public void setInstructionAtPCChangedProperty(boolean val) {
		instructionAtPCChanged.set(val);
	}


	public void reload() {
		Platform.runLater(() -> {
			startingIndex.set(cu.getProgramCounter());
			generateList();
		});
	}
}
