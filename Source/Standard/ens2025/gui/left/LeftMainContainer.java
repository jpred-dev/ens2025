package ens2025.gui.left;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LeftMainContainer extends VBox {
	private CodeZoneContainer czc;
	private StackZoneContainer szc;
	private MemoryVisor mv;
	private BooleanProperty isRunning;

	private static final double TOP_PAD = 0;
	private static final double RIGHT_PAD = 0;
	private static final double BOTTOM_PAD = 0;
	private static final double LEFT_PAD = 0;
	private static final double SPACING = 5;
	private static final double HPAD = RIGHT_PAD + LEFT_PAD;
	
	public static final double TOTAL_WIDTH = MemoryVisor.TOTAL_WIDTH + HPAD;
	
	public LeftMainContainer(ControlUnitAdapter cua, SettingsAdapter sa) {
		isRunning = cua.getRunningProperty();
		
		czc = new CodeZoneContainer(sa);
		szc = new StackZoneContainer(cua, sa);
		mv = new MemoryVisor(cua, sa);

		setBehavior();
		setContainers();
		setLookAndFeel();
		setProperties();
	}
	
	private void setProperties() {
		VBox.setVgrow(mv, Priority.ALWAYS);
	}

	private void setLookAndFeel() {
		setSpacing(SPACING);
		setPadding(new Insets(TOP_PAD, RIGHT_PAD, BOTTOM_PAD, LEFT_PAD));
	}

	private void setContainers() {
		this.getChildren().addAll(czc, szc, mv);
	}

	private void setBehavior() {
		isRunning.addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				if (newVal == true)
					this.setDisable(true);
				else
					this.setDisable(false);
			});
		});
	}

	public void refresh() {
		mv.refresh();
	}
}
