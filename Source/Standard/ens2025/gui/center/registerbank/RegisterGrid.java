package ens2025.gui.center.registerbank;

import java.util.HashMap;
import java.util.Map;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import enums.RegisterID;
import io.IOInterface;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterGrid extends HBox {
	private VBox container1;
	private VBox container2;
	private VBox container3;
	private VBox container4;
	private Map<RegisterID, RegisterField> fields;

	private static final double INNER_SPACING = 2;
	private static final double MAIN_SPACING = 7;
	public static final double TOTAL_WIDTH = RegisterField.TOTAL_WIDTH*4 + MAIN_SPACING*3;
	
	public RegisterGrid(ControlUnitAdapter cua, SettingsAdapter sa, IOInterface io) {
		fields = new HashMap<>();
		container1 = new VBox();
		container2 = new VBox();
		container3 = new VBox();
		container4 = new VBox();
		
		for(RegisterID reg : RegisterID.values()) {
			RegisterField rf = new RegisterField(cua, sa, io, reg);
			fields.put(reg, rf);
		}
		
		setContainers();
		setLookAndFeel();
	}
	
	private void setContainers() {
		this.getChildren().addAll(container1, container2, container3, container4);

		for(int i=0; i<fields.size()/4; i++)
			container1.getChildren().add(fields.get(RegisterID.getFromId(i)));
		for(int i=fields.size()/4; i<fields.size()/2; i++)
			container2.getChildren().add(fields.get(RegisterID.getFromId(i)));
		for(int i=fields.size()/2; i<fields.size()*3/4; i++)
			container3.getChildren().add(fields.get(RegisterID.getFromId(i)));
		for(int i=fields.size()*3/4; i<fields.size(); i++)
			container4.getChildren().add(fields.get(RegisterID.getFromId(i)));
	}
	
	private void setLookAndFeel() {
		this.setMinWidth(TOTAL_WIDTH);
		//this.setMaxWidth(TOTAL_WIDTH);
		this.setSpacing(MAIN_SPACING);

		container1.setSpacing(INNER_SPACING);
		container2.setSpacing(INNER_SPACING);
		container3.setSpacing(INNER_SPACING);
		container4.setSpacing(INNER_SPACING);
	}
}
