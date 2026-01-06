package ens2025.gui.center;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.IOAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class ConsoleArea extends TextArea {
	private BooleanProperty isRunning;
	private BooleanProperty promptProperty;
	private StringProperty consoleOutputProperty;
	private StringProperty consoleInputProperty;
	private IntegerProperty textLengthProperty;
	private TextFormatter<String> tf;
	
	//private static final Set<KeyCode> RESTRICTED_CODES = new HashSet<>(Arrays.asList(KeyCode.BACK_SPACE, KeyCode.DELETE, KeyCode.CUT, KeyCode.PASTE, KeyCode.TAB));
	private static final Set<KeyCode> RESTRICTED_CONTROL_CODES = new HashSet<>(Arrays.asList(KeyCode.X, KeyCode.Y, KeyCode.Z));
	
	public ConsoleArea(ControlUnitAdapter cua, IOAdapter io) {
		isRunning = cua.getRunningProperty();
		promptProperty = io.getPromptProperty();
		consoleOutputProperty = io.getConsoleOutputProperty();
		consoleInputProperty = io.getConsoleInputProperty();
		textLengthProperty = new SimpleIntegerProperty(0);
		
		createFormatter();
		setTextFormatter(tf);
		
		setEditable(false);
		
		setMenu();
		setBehavior();
		setLookAndFeel();
	}
	
	private void setLookAndFeel() {
		this.setFont(AppFonts.getStandardFont());
	}
	
	private void createFormatter() {
		tf = new TextFormatter<>(change -> {
			if (change.getRangeStart() >= textLengthProperty.get())
				return change;
			else {
				if (change.getRangeEnd() > textLengthProperty.get() && change.getRangeStart() < textLengthProperty.get()) {
					change.setRange(textLengthProperty.get(), change.getRangeEnd());
					change.setText(change.getText());
					change.setCaretPosition(Math.max(textLengthProperty.get(), change.getCaretPosition()));
					change.setAnchor(Math.max(textLengthProperty.get(), change.getAnchor()));
					return change;
				}
				else if (change.getRangeEnd() <= textLengthProperty.get()) {
					change.setText("");
					change.setRange(textLengthProperty.get(), textLengthProperty.get());
					change.setCaretPosition(textLengthProperty.get());
					change.setAnchor(textLengthProperty.get());
					return change;
				}
			}
			return change;
		});
	}
	
	private void setMenu() {
		ContextMenu menu = new ContextMenu();
		
		MenuItem selectAll = new MenuItem("Seleccionar todo");
		KeyCombination selectAllAcc = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
		selectAll.setAccelerator(selectAllAcc);
		selectAll.setOnAction(e -> this.selectAll());
		
		MenuItem copy = new MenuItem("Copiar");
		KeyCombination copyAcc = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
		copy.setAccelerator(copyAcc);
		copy.setOnAction(e -> this.copy());
		
		MenuItem wipe = new MenuItem("Limpiar");
		KeyCombination wipeAcc = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
		wipe.setAccelerator(wipeAcc);
		wipe.setOnAction(e -> {
			this.textLengthProperty.set(0);
			this.clear();
		});
		
		menu.getItems().addAll(selectAll, copy, wipe);
		this.setContextMenu(menu);
	}
	
	private void setBehavior() {
		promptProperty.addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				if (newVal == true) {
					textLengthProperty.set(getText().length());
					setEditable(true);
					positionCaret(textLengthProperty.get());
					requestFocus();
				} else
					setEditable(false);
			});
		});
		
		consoleOutputProperty.addListener((observable, oldVal, newVal) -> {
			if (newVal != "") {
				appendText(newVal);
				consoleOutputProperty.set("");
			}
		});
		
		isRunning.addListener((observable, oldVal, newVal) -> {
			if (newVal == false)
				this.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false));
		});
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (promptProperty.get() == true) {
				if (e.getCode() == KeyCode.ENTER) {
					Platform.runLater(() -> {
						String currentText = this.getText();
						String inputText = "";
						if (currentText.length() >= textLengthProperty.get())
							inputText = currentText.substring(textLengthProperty.get());
						consoleInputProperty.set(inputText);
						textLengthProperty.set(getText().length());
						promptProperty.set(false);
					});
				}
				
				if(e.isControlDown() && e.getCode() == KeyCode.W) {
					e.consume();
				}
			}
			if (e.isControlDown() && RESTRICTED_CONTROL_CODES.contains(e.getCode()))
				e.consume();
		});
		
		this.addEventFilter(KeyEvent.KEY_TYPED, e -> {
			if (promptProperty.get() == true) {
				if (getCaretPosition() <= textLengthProperty.get())
					positionCaret(textLengthProperty.get());
			}
		});
	}
}
