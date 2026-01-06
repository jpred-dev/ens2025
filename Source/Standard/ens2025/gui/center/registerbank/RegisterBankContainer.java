package ens2025.gui.center.registerbank;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import io.IOInterface;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterBankContainer extends VBox {
	private Label title;
	private ResetButton reset;
	private CheckBox check;
	private RegisterGrid reg;
	private FlagGrid flag;
	private HBox innerContainer;
	private VBox attachedBox;
	private SettingsAdapter sa;


	private static final double ATTACHED_WIDTH = FlagGrid.TOTAL_WIDTH;
	private static final double FIELD_HEIGHT = 23;
	private static final double BUTTON_HEIGHT = FIELD_HEIGHT;
	private static final double RESET_WIDTH = ATTACHED_WIDTH;
	private static final double MAIN_SPACING = 5;
	private static final double INNER_SPACING = 15;
	private static final double VERTICAL_ATTACH_SPACING = 3;
	public static final double TOTAL_WIDTH = RegisterGrid.TOTAL_WIDTH + FlagGrid.TOTAL_WIDTH + INNER_SPACING;
	
	public RegisterBankContainer(ControlUnitAdapter cua, SettingsAdapter sa, IOInterface io) {
		this.sa = sa;
		title = new Label("Banco de registros y biestables");
		reg = new RegisterGrid(cua, sa, io);
		
		flag = new FlagGrid(cua);
		reset = new ResetButton("Reiniciar valores", cua);
		check = new CheckBox("Reiniciar si H=1");
		attachedBox = new VBox();
		innerContainer = new HBox();
		
		setContainers();
		setProperties();
		setLookAndFeel();
		setBehavior();
	}
	
	private void setProperties() {
		check.setSelected(sa.getResetRegistersWithNewRun());
	}
	
	private void setBehavior() {
		check.setOnAction(e -> {
			sa.setResetRegistersWithNewRun(check.isSelected());
		});
	}
	
	private void setContainers() {
		attachedBox.getChildren().addAll(flag, reset, check);
		innerContainer.getChildren().addAll(reg, attachedBox);
		this.getChildren().addAll(title, innerContainer);
	}
	
	private void setLookAndFeel() {
		reset.setFont(AppFonts.getStandardFont());
		check.setFont(AppFonts.getStandardFont());
		title.setFont(AppFonts.getStandardBoldFont());
		
		this.setMinWidth(TOTAL_WIDTH);
		//this.setMaxWidth(TOTAL_WIDTH);
		this.setSpacing(MAIN_SPACING);
		attachedBox.setMinWidth(ATTACHED_WIDTH);
		//attachedBox.setMaxWidth(ATTACHED_WIDTH);

		reset.setMinHeight(BUTTON_HEIGHT);
		//reset.setPrefHeight(BUTTON_HEIGHT);
		//reset.setMaxHeight(BUTTON_HEIGHT);
		reset.setMinWidth(RESET_WIDTH);
		//reset.setMaxWidth(RESET_WIDTH);
		reset.setAlignment(Pos.CENTER);
		
		check.setMinWidth(RESET_WIDTH);
		//check.setMaxWidth(RESET_WIDTH);
		check.setAlignment(Pos.CENTER);
		
		innerContainer.setSpacing(INNER_SPACING);
		attachedBox.setSpacing(VERTICAL_ATTACH_SPACING);
	}
}
