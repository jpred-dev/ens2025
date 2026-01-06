package ens2025.gui.left;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueField;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CodeZoneContainer extends VBox {
	private Label title;
	private HBox box;
	private Label startLabel;
	private ValueField start;
	private Label endLabel;
	private ValueField end;

	private static final double HSPACING = 10;
	private static final double VSPACING = 10;
	
	public CodeZoneContainer(SettingsAdapter sa) {
		title = new Label("Zona de código");
		
		startLabel = new Label("Desde:");
		start = new ValueField(sa.getCodeStartProperty());

		endLabel = new Label("Hasta:");
		end = new ValueField(sa.getCodeEndProperty());

		box = new HBox();

		setContainers();
		setLookAndFeel();
	}
	
	private void setContainers() {
		box.getChildren().addAll(startLabel, start, endLabel, end);
		this.getChildren().addAll(title, box);
	}
	
	private void setLookAndFeel() {
		title.setFont(AppFonts.getStandardBoldFont());
		startLabel.setFont(AppFonts.getStandardFont());
		endLabel.setFont(AppFonts.getStandardFont());
		
		setAlignment(Pos.CENTER_LEFT);
		box.setSpacing(HSPACING);
		setSpacing(VSPACING);
	}
}
