package ens2025.gui.right;

import data.RWData;
import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueField;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import enums.RegisterID;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CodeVisorToolbar extends VBox {
	private HBox container1;
	private HBox container2;
	private ValueField address;
	private Button pc;
	private Button offsetPos;
	private Button offsetNeg;
	private Button reset;
	private ControlUnitAdapter cua;

	private static final double FIELD_HEIGHT = 23;
	private static final double BUTTON_HEIGHT = FIELD_HEIGHT;
	
	private static final double RESET_WIDTH = 219;
	private static final double PC_WIDTH = 184;
	private static final double OFFSET_WIDTH = 45;
	
	private static final double VSPACING = 5;
	private static final double HSPACING = 5;
	
	public CodeVisorToolbar(ControlUnitAdapter cua) {
		this.cua = cua;
		address = new ValueField(cua.getDisassembleStart());
		reset = new Button("Eliminar puntos de ruptura");
		pc = new Button("Desensamblar desde PC");
		offsetPos = new Button("+1");
		offsetNeg = new Button("-1");
		container1 = new HBox();
		container2 = new HBox();
		
		setContainers();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setContainers() {
		container1.getChildren().addAll(address, reset);
		container2.getChildren().addAll(pc, offsetPos, offsetNeg);
		getChildren().addAll(container1, container2);
	}
	
	private void setLookAndFeel() {
		pc.setFont(AppFonts.getStandardFont());
		reset.setFont(AppFonts.getStandardFont());
		offsetPos.setFont(AppFonts.getStandardFont());
		offsetNeg.setFont(AppFonts.getStandardFont());
		
		address.setMinHeight(FIELD_HEIGHT);
		//address.setMaxHeight(FIELD_HEIGHT);
		reset.setMinHeight(BUTTON_HEIGHT);
		//reset.setMaxHeight(BUTTON_HEIGHT);
		
		pc.setMinHeight(BUTTON_HEIGHT);
		//pc.setMaxHeight(BUTTON_HEIGHT);
		offsetPos.setMinHeight(BUTTON_HEIGHT);
		//offsetPos.setMaxHeight(BUTTON_HEIGHT);
		offsetNeg.setMinHeight(BUTTON_HEIGHT);
		//offsetNeg.setMaxHeight(BUTTON_HEIGHT);
		
		reset.setMinWidth(RESET_WIDTH);
		//reset.setMaxWidth(RESET_WIDTH);
		
		pc.setMinWidth(PC_WIDTH);
		//pc.setMaxWidth(PC_WIDTH);
		offsetPos.setMinWidth(OFFSET_WIDTH);
		//offsetPos.setMaxWidth(OFFSET_WIDTH);
		offsetNeg.setMinWidth(OFFSET_WIDTH);
		//offsetNeg.setMaxWidth(OFFSET_WIDTH);

		reset.setPadding(new Insets(0, 0, 0, 0));
		pc.setPadding(new Insets(0, 0, 0, 0));
		offsetPos.setPadding(new Insets(0, 0, 0, 0));
		offsetNeg.setPadding(new Insets(0, 0, 0, 0));
		
		reset.setAlignment(Pos.CENTER);
		pc.setAlignment(Pos.CENTER);
		offsetPos.setAlignment(Pos.CENTER);
		offsetNeg.setAlignment(Pos.CENTER);
		
		container1.setSpacing(HSPACING);
		container2.setSpacing(HSPACING);
		this.setSpacing(VSPACING);
	}
	
	private void setBehavior() {
		pc.setOnAction(event -> {
			cua.setDisassembleStart(cua.readRegisterDataProperty(RegisterID.PC));
		});
		
		offsetPos.setOnAction(event -> {
			RWData address = new RWData(cua.getDisassembleStart().getData());
			
			if (address.isEqual(RWData.MAX_UNSIGNED_VALUE) == false) {
				address.add(1);
				cua.getDisassembleStart().set(address);
			}
		});
		
		offsetNeg.setOnAction(event -> {
			RWData address = new RWData(cua.getDisassembleStart().getData());
			
			if (address.isEqual(0) == false) {
				address.subtract(1);
				cua.getDisassembleStart().set(address);
			}
		});
		
		reset.setOnAction(event -> {
			cua.resetBreakpoints();
		});
	}
}
