package ens2025.gui.right;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import enums.RegisterID;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NextInstructionDisplay extends VBox {
	private Label title;
	private Label inst;
	private ControlUnitAdapter cua;
	private BooleanProperty instructionAtPCChanged;
	
	private static final double SPACING = 5;
	
	public NextInstructionDisplay(ControlUnitAdapter cua) {
		this.cua = cua;
		instructionAtPCChanged = cua.getInstructionAtPCChangedProperty();
		title = new Label("Siguiente instrucción");
		inst = new Label(cua.getDisassembledInstructionAtPC());
		
		this.getChildren().addAll(title, inst);
		setLookAndFeel();
		setBehavior();
	}
	
	private void setBehavior() {
		cua.readRegisterDataProperty(RegisterID.PC).addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				inst.setText(cua.getDisassembledInstructionAtPC());
			});
		});

		instructionAtPCChanged.set(false);
		instructionAtPCChanged.addListener((observable, oldVal, newVal) -> {
			if (newVal == true)
				Platform.runLater(() -> {
					inst.setText(cua.getDisassembledInstructionAtPC());
					instructionAtPCChanged.set(false);
				});
		});
		
	}
	
	private void setLookAndFeel() {
		setSpacing(SPACING);
		inst.setAlignment(Pos.BASELINE_CENTER);
		
		title.setFont(AppFonts.getStandardBoldFont());
		inst.setFont(AppFonts.getStandardFont());
	}
	
}
