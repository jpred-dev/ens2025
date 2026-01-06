package ens2025.gui.common;

import customexception.CustomException;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class BitFieldDialog extends Stage {
	private Label label;
	private Button save;
	private Button cancel;
	private BooleanProperty data;
	private ToggleGroup radioGroup;
	private RadioButton inactive;
	private RadioButton active;

	private HBox textContainer;
	private HBox buttonContainer;
	private VBox finalContainer;
	
	private final static double BUTTON_WIDTH = 70;
	private final static double TEXT_SPACING = 10;
	private final static double BUTTON_SPACING = 10;

	private final static double FINAL_PAD = 10;
	private final static double FINAL_SPACING = 10;
	
	public BitFieldDialog(Window owner, BooleanProperty data) {
		this.data = data;
		setTitle("Editar valor");
		initOwner(owner);
		
		label = new Label("Nuevo valor:");
		radioGroup = new ToggleGroup();
		inactive = new RadioButton("0");
		active = new RadioButton("1");
		
		save = new Button("OK");
		cancel = new Button("Cancelar");
		
		textContainer = new HBox();
		buttonContainer = new HBox();
		finalContainer = new VBox();
		
		Scene scene = new Scene(finalContainer);
		setScene(scene);
		
		setLookAndFeel();
		setProperties();
		setContainers();
		setBehavior();
		
		showAndWait();
	}
	
	private void setContainers() {
		textContainer.getChildren().addAll(label, inactive, active);
		buttonContainer.getChildren().addAll(save, cancel);
		finalContainer.getChildren().addAll(textContainer, buttonContainer);
	}
	
	private void setProperties() {
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		inactive.setToggleGroup(radioGroup);
		active.setToggleGroup(radioGroup);
		
		if (data.get() == true)
			active.setSelected(true);
		else
			inactive.setSelected(true);
		
	}
	
	private void setLookAndFeel() {
		label.setFont(AppFonts.getStandardFont());
		inactive.setFont(AppFonts.getStandardFont());
		active.setFont(AppFonts.getStandardFont());
		save.setFont(AppFonts.getStandardFont());
		cancel.setFont(AppFonts.getStandardFont());
		save.setMinWidth(BUTTON_WIDTH);
		//save.setMaxWidth(BUTTON_WIDTH);
		cancel.setMinWidth(BUTTON_WIDTH);
		//cancel.setMaxWidth(BUTTON_WIDTH);

		textContainer.setAlignment(Pos.CENTER);
		textContainer.setSpacing(TEXT_SPACING);
		buttonContainer.setSpacing(BUTTON_SPACING);
		finalContainer.setSpacing(FINAL_SPACING);
		finalContainer.setPadding(new Insets(FINAL_PAD));
	}
	
	private void setBehavior() {
		
		save.setOnAction(e -> {
			try {
				if (inactive.selectedProperty().get() == true)
					data.set(false);
				else if (active.selectedProperty().get() == true)
					data.set(true);
				close();
			} catch (CustomException ex) {
				showAlert();
			}
		});
		
		cancel.setOnAction(e -> close());
	}
	
	private void showAlert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error de escritura");
		alert.setHeaderText(null);
		alert.setContentText("Formato no válido");
		alert.getDialogPane().setMaxWidth(200);
		alert.showAndWait();
	}
}
