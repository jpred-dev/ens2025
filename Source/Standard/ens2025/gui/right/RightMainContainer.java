package ens2025.gui.right;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RightMainContainer extends VBox {
	private NextInstructionDisplay nid;
	private CodeVisor dv;
	private BooleanProperty isRunning;

	private static final double TOP_PAD = 0;
	private static final double RIGHT_PAD = 0;
	private static final double BOTTOM_PAD = 0;
	private static final double LEFT_PAD = 0;
	private static final double SPACING = 5;
	private static final double HPAD = RIGHT_PAD + LEFT_PAD;
	
	public static final double TOTAL_WIDTH = CodeVisor.TOTAL_WIDTH + HPAD;
	
	public RightMainContainer(ControlUnitAdapter cua) {
		isRunning = cua.getRunningProperty();
		dv = new CodeVisor(cua);
		nid = new NextInstructionDisplay(cua);
		
		setBehavior();
		setContainers();
		setLookAndFeel();
		setProperties();
	}

	private void setProperties() {
		VBox.setVgrow(dv, Priority.ALWAYS);
	}

	private void setLookAndFeel() {
		setSpacing(SPACING);
		setPadding(new Insets(TOP_PAD, RIGHT_PAD, BOTTOM_PAD, LEFT_PAD));
	}

	private void setContainers() {
		this.getChildren().addAll(nid, dv);
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
}
