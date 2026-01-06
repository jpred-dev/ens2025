package ens2025.gui.center.registerbank;

import java.util.ArrayList;
import java.util.List;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import enums.Flag;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FlagGrid extends HBox {
	private VBox container1;
	private VBox container2;
	private VBox container3;
	private List<FlagField> fields;

	private static final double INNER_SPACING = 2;
	private static final double MAIN_SPACING = 5;
	public static final double TOTAL_WIDTH = FlagField.TOTAL_WIDTH*3 + MAIN_SPACING*2;

	public FlagGrid(ControlUnitAdapter cua) {
		fields = new ArrayList<>();
		container1 = new VBox();
		container2 = new VBox();
		container3 = new VBox();
		
		for(Flag f : Flag.values()) {
			FlagField ff = new FlagField(cua, f);
			fields.add(ff);
		}

		setLookAndFeel();
		setContainers();
	}
	
	private void setContainers() {
		container1.getChildren().addAll(fields.get(0), fields.get(1));
		container2.getChildren().addAll(fields.get(2), fields.get(3));
		container3.getChildren().addAll(fields.get(4), fields.get(5));
		this.getChildren().addAll(container1, container2, container3);
	}
	
	private void setLookAndFeel() {
		container1.setSpacing(INNER_SPACING);
		container2.setSpacing(INNER_SPACING);
		container3.setSpacing(INNER_SPACING);
		this.setSpacing(MAIN_SPACING);
	}
}
