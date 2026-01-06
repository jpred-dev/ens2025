package ens2025.gui.center.registerbank;

import customexception.InvadingPointerException;
import customexception.StackException;
import data.RData;
import data.RWData;
import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueField;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import enums.RegisterID;
import io.IOInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RegisterField extends HBox {
	private Label label;
	private ValueField field;
	private RegisterID regID;
	private ControlUnitAdapter cua;
	private IOInterface io;
	private boolean isRevertingPC = false;

	private static final double LABEL_WIDTH = 17;
	private static final double FIELD_WIDTH = ValueField.WIDTH;
	public static final double TOTAL_WIDTH = LABEL_WIDTH + FIELD_WIDTH;
	
	public RegisterField(ControlUnitAdapter cua, SettingsAdapter sa, IOInterface io, RegisterID reg) {
		this.cua = cua;
		this.io = io;
		this.regID = reg;
		field = new ValueField(cua.readRegisterDataProperty(reg));
		label = new Label(reg.toString());
		
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setContainers() {
		this.getChildren().addAll(label, field);
	}
	
	private void setBehavior() {
		cua.readRegisterDataProperty(regID).addListener((observable, oldVal, newVal) -> {
			RData register = new RWData(cua.readRegisterData(regID));
			try {
				if (isRevertingPC)
					return;
				cua.setRegister(regID, register);
				
				if (regID == RegisterID.SP) {
					cua.updateStackZone();
				}
				
				else if (regID == RegisterID.IX) {
					RData old = new RWData(oldVal);
					RData current = new RWData(newVal);
					cua.setIXProperty(old, false);
					cua.setIXProperty(current, true);
				}
				
				else if (regID == RegisterID.IY) {
					RData old = new RWData(oldVal);
					RData current = new RWData(newVal);
					cua.setIYProperty(old, false);
					cua.setIYProperty(current, true);
				}
			} catch (InvadingPointerException ex) {
				isRevertingPC = true;
				io.printlnLog(ex.getMessage());
				cua.readRegisterDataProperty(regID).set(oldVal);
				isRevertingPC = false;
			} catch (StackException ex) {
				isRevertingPC = true;
				io.printlnLog(ex.getMessage());
				cua.readRegisterDataProperty(regID).set(oldVal);
				isRevertingPC = false;
			}
		});
	}
	
	private void setLookAndFeel() {
		label.setFont(AppFonts.getStandardFont());
		label.setMinWidth(LABEL_WIDTH);
		//label.setMaxWidth(LABEL_WIDTH);
		
		setAlignment(Pos.CENTER_LEFT);
	}
}
