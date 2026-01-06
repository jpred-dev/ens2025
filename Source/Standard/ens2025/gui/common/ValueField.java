package ens2025.gui.common;

import ens2025.gui.common.properties.DataProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class ValueField extends TextField {
	private DataProperty value;
	
	public static final double WIDTH = 60;
	public static final int MAX_LENGTH = 6;
	
	public ValueField(DataProperty value) {
		this.value = value;
		
		setProperties();
		setLookAndFeel();
		setBehavior();
	}
	
	public void set(String value) {
		setText(value);
	}
	
	private void setLookAndFeel() {
		setMinWidth(WIDTH);
		setMaxWidth(WIDTH);
		setFont(AppFonts.getValueFont());
	}
	
	private void setProperties() {
		setText(value.get());
		setEditable(false);
	}
	
	private void setBehavior() {
		setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
				new ValueFieldDialog(this.getScene().getWindow(), value, MAX_LENGTH, WIDTH);
		});
		
		setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER)
				new ValueFieldDialog(this.getScene().getWindow(), value, MAX_LENGTH, WIDTH);
		});
		
		value.addListener((observable, oldVal, newVal) -> {
			setText(newVal);
		});
	}
}
