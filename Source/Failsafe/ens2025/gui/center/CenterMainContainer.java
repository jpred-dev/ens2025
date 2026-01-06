package ens2025.gui.center;

import ens2025.gui.center.registerbank.RegisterBankContainer;
import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.IOAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CenterMainContainer extends VBox {
	private RegisterBankContainer rbb;
	private Label conLabel;
	private Label logLabel;
	private LogArea log;
	private ConsoleArea console;
	private VBox logBox;
	private VBox conBox;
	private SplitPane splitBox;
	private ControlUnitAdapter cua;
	private BooleanProperty isRunning;
	private StringProperty textDump;
	
	private static final double SPACING = 10;
	private static final double TOP_PAD = 0;
	private static final double RIGHT_PAD = 5;
	private static final double BOTTOM_PAD = 0;
	private static final double LEFT_PAD = 5;
	
	private static final double HPAD = RIGHT_PAD + LEFT_PAD;
	
	public static final double MIN_WIDTH = RegisterBankContainer.TOTAL_WIDTH;
	public static final double TOTAL_WIDTH = MIN_WIDTH + HPAD;
	
	public CenterMainContainer(ControlUnitAdapter cua, SettingsAdapter sa, IOAdapter io) {
		this.textDump = io.getTextDumpProperty();
		this.cua = cua;
		
		isRunning = cua.getRunningProperty();
		rbb = new RegisterBankContainer(cua, sa, io);

		logLabel = new Label("Mensajes");
		conLabel = new Label("Consola");
		log = new LogArea(io);
		console = new ConsoleArea(cua, io);

		logBox = new VBox();
		conBox = new VBox();
		splitBox = new SplitPane();
		
		setProperties();
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setBehavior() {
		isRunning.addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				if (newVal == true)
					rbb.setDisable(true);
				else
					rbb.setDisable(false);
			});
		});
		
		textDump.addListener((observable, oldVal, newVal) -> {
			if (newVal != "") {
				cua.saveLogs(newVal, log.getText(), console.getText());
				textDump.set("");
			}
		});
	}

	private void setProperties() {
		splitBox.setOrientation(Orientation.VERTICAL);
		log.setEditable(false);
		//log.setWrapText(true);
		VBox.setVgrow(splitBox, Priority.ALWAYS);
		VBox.setVgrow(log, Priority.ALWAYS);
		VBox.setVgrow(console, Priority.ALWAYS);
	}
	
	private void setContainers() {
		logBox.getChildren().addAll(logLabel, log);
		conBox.getChildren().addAll(conLabel, console);
		splitBox.getItems().addAll(logBox, conBox);
		this.getChildren().addAll(rbb, splitBox);
	}
	
	private void setLookAndFeel() {
		logLabel.setFont(AppFonts.getStandardBoldFont());
		conLabel.setFont(AppFonts.getStandardBoldFont());
		logBox.setMinWidth(MIN_WIDTH);
		conBox.setMinWidth(MIN_WIDTH);
		this.setMinWidth(MIN_WIDTH);
		
		setPadding(new Insets(TOP_PAD, RIGHT_PAD, BOTTOM_PAD, LEFT_PAD));
		setSpacing(SPACING);
	}
}
