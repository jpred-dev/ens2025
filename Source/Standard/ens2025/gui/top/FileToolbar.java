package ens2025.gui.top;

import java.io.File;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.IOAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class FileToolbar extends HBox {
	private Button assembleButton;
	private Button openButton;
	private Button saveButton;
	private Button logButton;
	private Tooltip assembleTooltip;
	private Tooltip openTooltip;
	private Tooltip saveTooltip;
	private Tooltip logTooltip;
	private ControlUnitAdapter cua;
	private StringProperty textDump;
	private BooleanProperty isRunning;

	private static final double BUTTON_SIZE = 30;
	private static final int ASSEMBLE_ICON_SIZE = 20;
	private static final int OPEN_ICON_SIZE = 20;
	private static final int SAVE_ICON_SIZE = 20;
	private static final int LOG_ICON_SIZE = 20;
	
	public static final double TOTAL_WIDTH = BUTTON_SIZE * 4;
	
	public FileToolbar(ControlUnitAdapter cua, IOAdapter io) {
		this.cua = cua;
		textDump = io.getTextDumpProperty();
		
		assembleButton = new Button("\uD83D\uddce");
		openButton = new Button("\uD83D\udcc2");
		saveButton = new Button("\uD83D\uDcbe");
		logButton = new Button("\uD83D\uDccb");

		assembleTooltip = new Tooltip("Abrir y ensamblar fichero");
		openTooltip = new Tooltip("Cargar imagen de memoria");
		saveTooltip = new Tooltip("Guardar imagen de memoria");
		logTooltip = new Tooltip("Guardar mensajes y consola");
		
		isRunning = cua.getRunningProperty();

		setLookAndFeel();
		setContainers();
		setBehavior();
	}
	
	private void setContainers() {
		Tooltip.install(assembleButton, assembleTooltip);
		Tooltip.install(openButton, openTooltip);
		Tooltip.install(saveButton, saveTooltip);
		Tooltip.install(logButton, logTooltip);
		
		getChildren().addAll(assembleButton, openButton, saveButton, logButton);
	}
	
	private void setLookAndFeel() {
		setButtonAppearance(assembleButton, ASSEMBLE_ICON_SIZE);
		setButtonAppearance(openButton, OPEN_ICON_SIZE);
		setButtonAppearance(saveButton, SAVE_ICON_SIZE);
		setButtonAppearance(logButton, LOG_ICON_SIZE);
		
		assembleTooltip.setFont(AppFonts.getStandardFont());
		openTooltip.setFont(AppFonts.getStandardFont());
		saveTooltip.setFont(AppFonts.getStandardFont());
		logTooltip.setFont(AppFonts.getStandardFont());
	}

	private void setButtonAppearance(Button b, int textSize) {
		b.setMinHeight(BUTTON_SIZE);
		b.setMaxHeight(BUTTON_SIZE);
		b.setMinWidth(BUTTON_SIZE);
		b.setMaxWidth(BUTTON_SIZE);
		b.setFont(AppFonts.getIconFont());
		b.setStyle("-fx-background-radius: 0;");
		b.setPadding(new Insets(0));
	}
	
	private void setBehavior() {
		assembleButton.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Seleccionar archivo a ensamblar");
			
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichero Fuente Ensamblador", "*.ens"),
				new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
			);
			
			File selectedFile = fc.showOpenDialog(assembleButton.getScene().getWindow());
			
			if (selectedFile != null) {
				String filePath = selectedFile.getAbsolutePath();
				cua.assembleFile(filePath);
			}
		});
		
		openButton.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Seleccionar imagen de memoria para cargar");
			
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichero Imagen Memoria", "*.mem"),
				new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
			);
			
			File selectedFile = fc.showOpenDialog(openButton.getScene().getWindow());
			
			if (selectedFile != null) {
				String filePath = selectedFile.getAbsolutePath();
				cua.loadMemoryFromFile(filePath);
			}
		});
		
		saveButton.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Guardar imagen de la memoria");
			
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichero Imagen Memoria", "*.mem"),
				new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
			);
			
			File selectedFile = fc.showSaveDialog(saveButton.getScene().getWindow());
			
			if (selectedFile != null) {
				String filePath = selectedFile.getAbsolutePath();
				cua.writeMemoryToFile(filePath);
			}
		});
		
		logButton.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Seleccionar imagen de memoria para guardar");
			
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichero de texto plano", "*.txt"),
				new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
			);
			
			File selectedFile = fc.showSaveDialog(logButton.getScene().getWindow());
			
			if (selectedFile != null) {
				String filePath = selectedFile.getAbsolutePath();
				textDump.set(filePath);
			}
		});
		
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
