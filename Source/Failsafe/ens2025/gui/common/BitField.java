package ens2025.gui.common;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class BitField extends TextField {
	private BooleanProperty value;
	public static final double WIDTH = 25;
	
	public BitField(BooleanProperty value) {
		this.value = value;
		
		setProperties();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setLookAndFeel() {
		setMinWidth(WIDTH);
		setMaxWidth(WIDTH);
		setFont(AppFonts.getValueFont());
	}
	
	private void setProperties() {
		refreshTextValue();
		setEditable(false);
	}
	
	private void setBehavior() {
		setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
				new BitFieldDialog(this.getScene().getWindow(), value);
		});
		
		setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER)
				new BitFieldDialog(this.getScene().getWindow(), value);
		});
		
		value.addListener((observable, oldVal, newVal) -> {
			refreshTextValue();
		});
	}
	
	public void refreshTextValue() {
		if (value.get())
			setText("1");
		else
			setText("0");
	}
	
	public BooleanProperty getProperty() {
		return value;
	}
}