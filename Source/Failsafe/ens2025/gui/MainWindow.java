package ens2025.gui;

import controlunit.ControlUnit;
import customexception.CustomException;
import ens2025.gui.center.CenterMainContainer;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.IOAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import ens2025.gui.left.LeftMainContainer;
import ens2025.gui.right.RightMainContainer;
import ens2025.gui.top.TopMainContainer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import settings.Settings;

public class MainWindow extends Application {
	private BorderPane bp;
	private TopMainContainer tmc;
	private RightMainContainer rmc;
	private CenterMainContainer cmc;
	private LeftMainContainer lmc;
	private Settings set;
	private SettingsAdapter sa;
	private IOAdapter io;
	private ControlUnit cu;
	private ControlUnitAdapter cua;
	private Scene scene;
	private Stage primaryStage;
	
	private static final double TOP_PAD = 10;
	private static final double RIGHT_PAD = 10;
	private static final double BOTTOM_PAD = 10;
	private static final double LEFT_PAD = 10;
	private static final double HPAD = RIGHT_PAD + LEFT_PAD;
	private static final double EXTRA_WIDTH = 0;
	
	private static final double TOP_WIDTH = TopMainContainer.TOTAL_WIDTH + HPAD;
	private static final double CENTER_WIDTH = LeftMainContainer.TOTAL_WIDTH + CenterMainContainer.TOTAL_WIDTH + RightMainContainer.TOTAL_WIDTH + HPAD + EXTRA_WIDTH;
	private static final double MIN_WIDTH = Math.max(TOP_WIDTH, CENTER_WIDTH);
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		set = new Settings();
		sa = new SettingsAdapter(set);
		IOAdapter ioa = new IOAdapter(sa);
		io = ioa;

		
		//Fallback para captura de excepciones.
		Thread.currentThread().setUncaughtExceptionHandler((thread, ex) -> {
			if(ex instanceof CustomException)
				io.printLog(ex.getMessage());
			else
				io.printlnLog("ERROR GORDO: " + ex.getMessage());
		});

		try {
			sa.loadSettingsSequence();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		
		cu = new ControlUnit(set, io);
		cua = new ControlUnitAdapter(cu, sa, io);
		cua.resetRegisters();

		tmc = new TopMainContainer(cua, sa, ioa);
		lmc = new LeftMainContainer(cua, sa);
		cmc = new CenterMainContainer(cua, sa, ioa);
		rmc = new RightMainContainer(cua);
		
		bp = new BorderPane();
		
		scene = new Scene(bp, MIN_WIDTH, 900); // Ancho y alto iniciales de la ventana
		
		scene.getStylesheets().add(getClass().getResource("/ens2025/gui/resources/styles/styles.css").toExternalForm());
		
		setLookAndFeel();
		setBehavior();
		setContainers();
		
		this.primaryStage.show();
		
	}
	
	@Override
	public void stop() throws Exception {
		set.saveSettingsToFile();
		super.stop();
	}
	
	private void setLookAndFeel() {
		bp.setPadding(new Insets(TOP_PAD, RIGHT_PAD, BOTTOM_PAD, LEFT_PAD));
		bp.setMinWidth(MIN_WIDTH);
		primaryStage.setMinWidth(MIN_WIDTH);
		primaryStage.setTitle("ENS2025");
	}
	
	private void setBehavior() {
		
	}
	
	private void setContainers() {
		bp.setTop(tmc);
		bp.setLeft(lmc);
		bp.setCenter(cmc);
		bp.setRight(rmc);
		primaryStage.setScene(scene);
	}
}
