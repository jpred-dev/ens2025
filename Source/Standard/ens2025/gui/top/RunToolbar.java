package ens2025.gui.top;

import java.util.concurrent.atomic.AtomicBoolean;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import io.IOInterface;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class RunToolbar extends HBox {
	private Button runButton;
	private Button stepButton;
	private Button debugButton;
	private Button stopButton;
	private Tooltip runTooltip;
	private Tooltip stepTooltip;
	private Tooltip debugTooltip;
	private Tooltip stopTooltip;
	private ControlUnitAdapter cua;
	private BooleanProperty isRunning;
	private AtomicBoolean stopFlag;
	private IOInterface io;

	private static final double BUTTON_SIZE = 30;
	private static final int RUN_ICON_SIZE = 30;
	private static final int STEP_ICON_SIZE = 25;
	private static final int DEBUG_ICON_SIZE = 25;
	private static final int STOP_ICON_SIZE = 25;
	
	public static final double TOTAL_WIDTH = BUTTON_SIZE * 4;
	
	public RunToolbar(ControlUnitAdapter cua, IOInterface io) {
		this.cua = cua;
		this.io = io;
		stopFlag = cua.getStopFlag();
		
		runButton = new Button("\u25b6");
		stepButton = new Button("\u23ef");
		debugButton = new Button("\ud83d\udd77");
		stopButton = new Button("\u25a0");

		runTooltip = new Tooltip("Simular");
		stepTooltip = new Tooltip("Simular una instrucción");
		debugTooltip = new Tooltip("Depurar (simular hasta punto de ruptura)");
		stopTooltip = new Tooltip("Detener simulación");

		stopButton.setDisable(true);
		isRunning = cua.getRunningProperty();
		
		setContainers();
		setBehavior();
		setLookAndFeel();
	}
	
	private void setContainers() {
		Tooltip.install(runButton, runTooltip);
		Tooltip.install(stepButton, stepTooltip);
		Tooltip.install(debugButton, debugTooltip);
		Tooltip.install(stopButton, stopTooltip);

		getChildren().addAll(runButton, stepButton, debugButton, stopButton);
	}
	
	private void setLookAndFeel() {
		setButtonAppearance(runButton, RUN_ICON_SIZE);
		setButtonAppearance(stepButton, STEP_ICON_SIZE);
		setButtonAppearance(debugButton, DEBUG_ICON_SIZE);
		setButtonAppearance(stopButton, STOP_ICON_SIZE);

		runTooltip.setFont(AppFonts.getStandardFont());
		stepTooltip.setFont(AppFonts.getStandardFont());
		debugTooltip.setFont(AppFonts.getStandardFont());
		stopTooltip.setFont(AppFonts.getStandardFont());
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
		isRunning.addListener((observable, oldVal, newVal) -> {
			if (newVal == true) {
				runButton.setDisable(true);
				stepButton.setDisable(true);
				debugButton.setDisable(true);
				stopButton.setDisable(false);
			}
			else {
				runButton.setDisable(false);
				stepButton.setDisable(false);
				debugButton.setDisable(false);
				stopButton.setDisable(true);
			}
		});
		
		runButton.setOnAction(e -> {
			isRunning.set(true);
			Thread runningThread = new Thread(() -> {
				cua.run();
				Platform.runLater(() -> {
					stopFlag.set(true);

					isRunning.set(false);
				});
				Thread.currentThread().interrupt();
			});
			
			runningThread.setDaemon(true);
			runningThread.start();
		});
		
		stepButton.setOnAction(e -> {
			isRunning.set(true);
			Thread runningThread = new Thread(() -> {
				cua.step();
				
				
				Platform.runLater(() -> {

					isRunning.set(false);
				});
				Thread.currentThread().interrupt();
			});
			
			runningThread.setDaemon(true);
			runningThread.start();
		});
		
		debugButton.setOnAction(e -> {
			isRunning.set(true);
			Thread runningThread = new Thread(() -> {
				cua.debug();
				Platform.runLater(() -> {
					stopFlag.set(true);

					isRunning.set(false);
				});
				Thread.currentThread().interrupt();
			});
			
			runningThread.setDaemon(true);
			runningThread.start();
		});
		
		stopButton.setOnAction(e -> {
			isRunning.set(false);
			stopFlag.set(true);
			cua.reload();
			io.printlnLog("Simulación detenida por el usuario.");
		});
	}
	
}
