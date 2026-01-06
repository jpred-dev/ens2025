package ens2025.gui.top;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import settings.NumericalRepresentation;
import settings.StackGrowth;

public class SettingsToolbar extends HBox {
	private Label numLabel;
	private Label stackLabel;
	private Label invLabel;
	private Label overflowLabel;
	private ComboBox<String> numerical;
	private ComboBox<String> stackGrowth;
	private ToggleButton pcInvadesStack;
	private ToggleButton spInvadesCode;
	private ToggleButton overButton;
	private ToggleButton underButton;
	private SettingsAdapter sa;
	private BooleanProperty isRunning;

	private static final double NUMERICAL_WIDTH = 140;
	private static final double GROWTH_WIDTH = 91;
	private static final double COMBO_WIDTH = NUMERICAL_WIDTH + GROWTH_WIDTH;
	
	private static final double NUMLABEL_WIDTH = 30;
	private static final double STACKLABEL_WIDTH = 60;
	private static final double INVLABEL_WIDTH = 68;
	private static final double FLOWLABEL_WIDTH = 64;
	private static final double LABEL_WIDTH = NUMLABEL_WIDTH + STACKLABEL_WIDTH + INVLABEL_WIDTH + FLOWLABEL_WIDTH;
	
	private static final double PC_STACK_BUTTON = 64;
	private static final double SP_CODE_BUTTON = 83;
	private static final double OVERFLOW_BUTTON = 64;
	private static final double UNDERFLOW_BUTTON = 71;
	private static final double BUTTON_WIDTH = PC_STACK_BUTTON + SP_CODE_BUTTON + OVERFLOW_BUTTON + UNDERFLOW_BUTTON;
	
	public static final double TOTAL_WIDTH = COMBO_WIDTH + LABEL_WIDTH + BUTTON_WIDTH;
	
	public SettingsToolbar(ControlUnitAdapter cua, SettingsAdapter sa) {
		this.sa = sa;
		
		isRunning = cua.getRunningProperty();
		
		numLabel = new Label("Base: ");
		stackLabel = new Label("  Crec. pila: ");
		invLabel = new Label("  Invasiones: ");
		overflowLabel = new Label("  Desb. pila: ");
		numerical = new ComboBox<>();
		pcInvadesStack = new ToggleButton("PC->Pila");
		spInvadesCode = new ToggleButton("SP->Código");
		overButton = new ToggleButton("Overflow");
		underButton = new ToggleButton("Underflow");
		
		
		for (NumericalRepresentation nr : NumericalRepresentation.values())
			numerical.getItems().add(nr.getDescription());

		stackGrowth = new ComboBox<>();
		for (StackGrowth sg : StackGrowth.values())
			stackGrowth.getItems().add(sg.getDescription());

		
		setLookAndFeel();
		setContainers();
		setBehavior();
		initComponents();
	}
	
	private void setLookAndFeel() {
		pcInvadesStack.setFont(AppFonts.getStandardFont());
		spInvadesCode.setFont(AppFonts.getStandardFont());
		overButton.setFont(AppFonts.getStandardFont());
		underButton.setFont(AppFonts.getStandardFont());
		numLabel.setMinWidth(NUMLABEL_WIDTH);
		//numLabel.setMaxWidth(NUMLABEL_WIDTH);
		stackLabel.setMinWidth(STACKLABEL_WIDTH);
		//stackLabel.setMaxWidth(STACKLABEL_WIDTH);
		invLabel.setMinWidth(INVLABEL_WIDTH);
		//invLabel.setMaxWidth(INVLABEL_WIDTH);
		overflowLabel.setMinWidth(FLOWLABEL_WIDTH);
		//overflowLabel.setMaxWidth(FLOWLABEL_WIDTH);
		
		
		numerical.setMinWidth(NUMERICAL_WIDTH);
		//numerical.setMaxWidth(NUMERICAL_WIDTH);
		stackGrowth.setMinWidth(GROWTH_WIDTH);
		//stackGrowth.setMaxWidth(GROWTH_WIDTH);
		

		pcInvadesStack.setMinWidth(PC_STACK_BUTTON);
		//pcInvadesStack.setMaxWidth(PC_STACK_BUTTON);
		spInvadesCode.setMinWidth(SP_CODE_BUTTON);
		//spInvadesCode.setMaxWidth(SP_CODE_BUTTON);
		overButton.setMinWidth(OVERFLOW_BUTTON);
		//overButton.setMaxWidth(OVERFLOW_BUTTON);
		underButton.setMinWidth(UNDERFLOW_BUTTON);
		//underButton.setMaxWidth(UNDERFLOW_BUTTON);

		numLabel.setFont(Font.font(AppFonts.stdFont.getFamily(), FontWeight.BOLD, AppFonts.stdFont.getSize()-2));
		stackLabel.setFont(Font.font(AppFonts.stdFont.getFamily(), FontWeight.BOLD, AppFonts.stdFont.getSize()-2));
		invLabel.setFont(Font.font(AppFonts.stdFont.getFamily(), FontWeight.BOLD, AppFonts.stdFont.getSize()-2));
		overflowLabel.setFont(Font.font(AppFonts.stdFont.getFamily(), FontWeight.BOLD, AppFonts.stdFont.getSize()-2));
		
	}
	
	private void setContainers() {
		getChildren().add(numLabel);
		getChildren().add(numerical);
		getChildren().add(stackLabel);
		getChildren().add(stackGrowth);
		getChildren().add(invLabel);
		getChildren().add(pcInvadesStack);
		getChildren().add(spInvadesCode);
		getChildren().add(overflowLabel);
		getChildren().add(overButton);
		getChildren().add(underButton);
	}
	
	private void initComponents() {
		pcInvadesStack.setSelected(sa.getCheckPCInvadingStack());
		spInvadesCode.setSelected(sa.getCheckSPInvadingCode());
		overButton.setSelected(sa.getCheckOverflow());
		underButton.setSelected(sa.getCheckUnderflow());
		numerical.setValue(sa.getNumericalRepresentation().getDescription());
		stackGrowth.setValue(sa.getStackGrowth().getDescription());
	}
	
	private void setBehavior() {
		pcInvadesStack.selectedProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCheckPCInvadingStack(newVal);
			});
		});
		
		spInvadesCode.selectedProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCheckSPInvadingCode(newVal);
			});
		});
		
		overButton.selectedProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCheckOverflow(newVal);
			});
		});
		
		underButton.selectedProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setCheckUnderflow(newVal);
			});
		});
		
		numerical.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setNumericalRepresentation(NumericalRepresentation.getFromDescription(newVal));
			});
		});
		
		stackGrowth.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				sa.setStackGrowth(StackGrowth.getFromDescription(newVal));
			});
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
