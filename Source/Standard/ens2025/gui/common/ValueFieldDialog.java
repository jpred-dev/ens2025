package ens2025.gui.common;

import java.util.function.UnaryOperator;

import customexception.CustomException;
import ens2025.gui.common.properties.DataProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ValueFieldDialog extends Stage {
	private TextField field;
	private DataProperty data;
	private Label label;
	private Button save;
	private Button cancel;
	private TextFormatter<String> tf;
	private UnaryOperator<TextFormatter.Change> filter;

	private HBox textContainer;
	private HBox buttonContainer;
	private VBox finalContainer;
	
	private final double fieldWidth;
	
	private final static double BUTTON_WIDTH = 70;
	private final static double TEXT_SPACING = 10;
	private final static double BUTTON_SPACING = 10;

	private final static double FINAL_PAD = 10;
	private final static double FINAL_SPACING = 10;
	
	public ValueFieldDialog(Window owner, DataProperty data, int fieldMaxLength, double fieldWidth) {
		this.data = data;
		this.fieldWidth = fieldWidth;
		setTitle("Editar valor");
		initOwner(owner);
		
		label = new Label("Nuevo valor:");
		field = new TextField(data.get());
		
		save = new Button("OK");
		cancel = new Button("Cancelar");
		
		textContainer = new HBox();
		buttonContainer = new HBox();
		finalContainer = new VBox();
		
		filter = change -> {
			String newText = change.getControlNewText();
			
			if (newText.length() > fieldMaxLength)
				return null;
			return change;
		};
		
		tf = new TextFormatter<>(filter);
		
		Scene scene = new Scene(finalContainer);
		setScene(scene);
		
		setLookAndFeel();
		setProperties();
		setContainers();
		setBehavior();
		
		showAndWait();
	}
	
	private void setContainers() {
		textContainer.getChildren().addAll(label, field);
		buttonContainer.getChildren().addAll(save, cancel);
		finalContainer.getChildren().addAll(textContainer, buttonContainer);
	}
	
	private void setProperties() {
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		field.setTextFormatter(tf);
	}
	
	private void setLookAndFeel() {
		label.setFont(AppFonts.getStandardFont());
		save.setFont(AppFonts.getStandardFont());
		cancel.setFont(AppFonts.getStandardFont());
		field.setFont(AppFonts.getValueFont());
		
		field.setMinWidth(fieldWidth);
		field.setMaxWidth(fieldWidth+25);

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
		field.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				try {
					data.set(field.getText());
					close();
				} catch (CustomException ex) {
					showAlert();
				}
			}
		});
		
		save.setOnAction(e -> {
			try {
				data.set(field.getText());
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
