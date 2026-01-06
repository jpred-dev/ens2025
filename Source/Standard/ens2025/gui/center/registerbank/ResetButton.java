package ens2025.gui.center.registerbank;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import javafx.scene.control.Button;

public class ResetButton extends Button {
	private ControlUnitAdapter cua;

	public ResetButton(String text, ControlUnitAdapter cua) {
		super(text);
		this.cua = cua;
		setBehavior();
	}

	private void setBehavior() {
		this.setOnAction(e -> {
			cua.resetRegisters();
		});
	}
}
