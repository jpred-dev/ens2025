package ens2025.gui.left;

import data.RWData;
import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueField;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.MemoryCell;
import ens2025.gui.common.adapters.SettingsAdapter;
import ens2025.gui.common.properties.DataProperty;
import enums.RegisterID;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MemoryVisorToolbar extends VBox {
	private HBox container1;
	private HBox container2;
	private ValueField address;
	private DataProperty index;
	private Button pc;
	private Button sp;
	private Button ix;
	private Button iy;
	private Button reset;
	private TableView<MemoryCell> tv;
	private ControlUnitAdapter cua;
	private SettingsAdapter sa;

	private static final double FIELD_HEIGHT = 23;
	private static final double BUTTON_HEIGHT = FIELD_HEIGHT;
	private static final double RESET_WIDTH = 160;
	
	private static final double BUTTON_WIDTH = 52;
	private static final double BUTTON_WIDTH_EXTRA = BUTTON_WIDTH+1;
	
	private static final double VSPACING = 5;
	private static final double HSPACING = 5;
	
	public MemoryVisorToolbar(ControlUnitAdapter cua, SettingsAdapter sa, TableView<MemoryCell> tv) {
		this.cua = cua;
		this.sa = sa;
		this.tv = tv;
		index = new DataProperty(new RWData(), sa, true);
		address = new ValueField(index);
		
		pc = new Button("Ir a PC");
		sp = new Button("Ir a SP");
		ix = new Button("Ir a IX");
		iy = new Button("Ir a IY");
		reset = new Button("Reiniciar memoria");
		
		
		container1 = new HBox();
		container2 = new HBox();
		
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setLookAndFeel() {
		pc.setFont(AppFonts.getStandardFont());
		sp.setFont(AppFonts.getStandardFont());
		ix.setFont(AppFonts.getStandardFont());
		iy.setFont(AppFonts.getStandardFont());
		reset.setFont(AppFonts.getStandardFont());
		
		address.setMinHeight(FIELD_HEIGHT);
		//address.setMaxHeight(FIELD_HEIGHT);
		reset.setMinHeight(FIELD_HEIGHT);
		//reset.setMaxHeight(FIELD_HEIGHT);
		
		pc.setMinHeight(BUTTON_HEIGHT);
		//pc.setMaxHeight(BUTTON_HEIGHT);
		sp.setMinHeight(BUTTON_HEIGHT);
		//sp.setMaxHeight(BUTTON_HEIGHT);
		ix.setMinHeight(BUTTON_HEIGHT);
		//ix.setMaxHeight(BUTTON_HEIGHT);
		iy.setMinHeight(BUTTON_HEIGHT);
		//iy.setMaxHeight(BUTTON_HEIGHT);
		
		
		reset.setMinWidth(RESET_WIDTH);
		//reset.setMaxWidth(RESET_WIDTH);
		
		pc.setMinWidth(BUTTON_WIDTH_EXTRA);
		//pc.setMaxWidth(BUTTON_WIDTH_EXTRA);
		sp.setMinWidth(BUTTON_WIDTH);
		//sp.setMaxWidth(BUTTON_WIDTH);
		ix.setMinWidth(BUTTON_WIDTH);
		//ix.setMaxWidth(BUTTON_WIDTH);
		iy.setMinWidth(BUTTON_WIDTH_EXTRA);
		//iy.setMaxWidth(BUTTON_WIDTH_EXTRA);
		
		reset.setPadding(new Insets(0, 0, 0, 0));
		pc.setPadding(new Insets(0, 0, 0, 0));
		sp.setPadding(new Insets(0, 0, 0, 0));
		ix.setPadding(new Insets(0, 0, 0, 0));
		iy.setPadding(new Insets(0, 0, 0, 0));
		
		reset.setAlignment(Pos.CENTER);
		pc.setAlignment(Pos.CENTER);
		sp.setAlignment(Pos.CENTER);
		ix.setAlignment(Pos.CENTER);
		iy.setAlignment(Pos.CENTER);
		
		container1.setSpacing(HSPACING);
		container2.setSpacing(HSPACING);
		this.setSpacing(VSPACING);
		
		container1.setAlignment(Pos.CENTER_LEFT);
		container2.setAlignment(Pos.CENTER_LEFT);
	}
	
	private void setContainers() {
		container1.getChildren().addAll(address, reset);
		container2.getChildren().addAll(pc, sp, ix, iy);
		this.getChildren().addAll(container1, container2);
	}
	
	private void setBehavior() {
		pc.setOnAction(e -> {
			int index = cua.readRegisterDataProperty(RegisterID.PC).getData().getValue();
			tv.scrollTo(index);
			tv.getSelectionModel().select(index);
			tv.requestFocus();
		});
		
		sp.setOnAction(e -> {
			int index = cua.readRegisterDataProperty(RegisterID.SP).getData().getValue();
			tv.scrollTo(index);
			tv.getSelectionModel().select(index);
			tv.requestFocus();
		});
		
		ix.setOnAction(e -> {
			int index = cua.readRegisterDataProperty(RegisterID.IX).getData().getValue();
			tv.scrollTo(index);
			tv.getSelectionModel().select(index);
			tv.requestFocus();
		});
		
		iy.setOnAction(e -> {
			int index = cua.readRegisterDataProperty(RegisterID.IY).getData().getValue();
			tv.scrollTo(index);
			tv.getSelectionModel().select(index);
			tv.requestFocus();
		});
		
		index.addListener((observable, oldVal, newVal) -> {
			tv.scrollTo(index.getData().getValue());
			tv.getSelectionModel().select(index.getData().getValue());
			tv.requestFocus();
		});
		
		sa.getNumericalRepresentationProperty().addListener((observable, oldVal, newVal) -> {
			index.set(index);
		});
		
		reset.setOnAction(e -> {
			cua.wipeMemory();
		});
	}
}
