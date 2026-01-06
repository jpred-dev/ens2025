package ens2025.gui.center;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.IOAdapter;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class LogArea extends TextArea {
	private StringProperty logOutputProperty;
	
	private static final Set<KeyCode> RESTRICTED_CONTROL_CODES = new HashSet<>(Arrays.asList(KeyCode.X, KeyCode.Y, KeyCode.Z));
	
	public LogArea(IOAdapter io) {
		logOutputProperty = io.getLogProperty();
		setEditable(false);
		
		setMenu();
		setBehavior();
		setLookAndFeel();
	}
	
	private void setLookAndFeel() {
		this.setFont(AppFonts.getStandardFont());
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
		wipe.setOnAction(e -> this.clear());
		
		menu.getItems().addAll(selectAll, copy, wipe);
		this.setContextMenu(menu);
	}
	
	private void setBehavior() {
		logOutputProperty.addListener((observable, oldVal, newVal) -> {
			if (logOutputProperty.get() != "")
				this.appendText(logOutputProperty.get());
		});
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (e.isControlDown() && RESTRICTED_CONTROL_CODES.contains(e.getCode()))
				e.consume();
			if (e.isControlDown() && e.getCode() == KeyCode.W) {
				this.clear();
				e.consume();
			}
		});
	}
}
