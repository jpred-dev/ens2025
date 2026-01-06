package ens2025.gui.left;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueField;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import enums.RegisterID;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StackZoneContainer extends VBox {
	private ControlUnitAdapter cua;
	private Label title;
	
	private HBox box;
	private Label startLabel;
	private ValueField start;
	private Label endLabel;
	private Label end;
	
	private static final double HSPACING = 10;
	private static final double VSPACING = 10;
	
	public StackZoneContainer(ControlUnitAdapter cua, SettingsAdapter sa) {
		this.cua = cua;

		title = new Label("Zona de pila");
		
		startLabel = new Label("Desde:");
		start = new ValueField(sa.getStackStartProperty());
		
		endLabel = new Label("Hasta:");
		end = new Label(cua.readRegisterDataProperty(RegisterID.SP).get());
		
		box = new HBox();
		
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setContainers() {
		box.getChildren().addAll(startLabel, start, endLabel, end);
		this.getChildren().addAll(title, box);
	}
	
	private void setBehavior() {
		cua.readRegisterDataProperty(RegisterID.SP).addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				end.setText(cua.readRegisterDataProperty(RegisterID.SP).get());
			});
		});
	}
	
	private void setLookAndFeel() {
		title.setFont(AppFonts.getStandardBoldFont());
		startLabel.setFont(AppFonts.getStandardFont());
		endLabel.setFont(AppFonts.getStandardFont());
		end.setFont(AppFonts.getStandardFont());
		
		setAlignment(Pos.CENTER_LEFT);
		box.setSpacing(HSPACING);
		setSpacing(VSPACING);
	}
}
