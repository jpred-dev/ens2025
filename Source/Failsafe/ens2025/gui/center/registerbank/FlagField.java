package ens2025.gui.center.registerbank;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.BitField;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import enums.Flag;
import enums.RegisterID;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class FlagField extends HBox {
	private Label label;
	private BitField field;
	private Flag flag;
	private ControlUnitAdapter cua;
	
	private static final double LABEL_WIDTH = 10;
	private static final double FIELD_WIDTH = BitField.WIDTH;
	public static final double TOTAL_WIDTH = LABEL_WIDTH + FIELD_WIDTH;
	
	public FlagField(ControlUnitAdapter cua, Flag flag) {
		this.cua = cua;
		this.flag = flag;
		field = new BitField(cua.getFlagProperty(flag));
		label = new Label(flag.toString());
		
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setContainers() {
		this.getChildren().addAll(label, field);
	}
	
	private void setBehavior() {
		cua.getFlagProperty(flag).addListener((observable, oldVal, newVal) -> {
			cua.setFlag(flag, newVal);
			cua.setRegister(RegisterID.SP, cua.readRegisterData(RegisterID.SP));
		});
	}
	
	private void setLookAndFeel() {
		label.setFont(AppFonts.getStandardFont());
		label.setMinWidth(LABEL_WIDTH);
		//label.setMaxWidth(LABEL_WIDTH);
		setAlignment(Pos.CENTER_LEFT);
	}
}
