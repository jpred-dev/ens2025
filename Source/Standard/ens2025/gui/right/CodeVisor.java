package ens2025.gui.right;

import ens2025.gui.common.AppFonts;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.DisassembledInstCell;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CodeVisor extends VBox {
	private Label title;
	private CodeVisorToolbar cvt;
	private ControlUnitAdapter cua;
	
	private TableView<DisassembledInstCell> tv;
	private TableColumn<DisassembledInstCell, Boolean> breakColumn;
	private TableColumn<DisassembledInstCell, Boolean> pcColumn;
	private TableColumn<DisassembledInstCell, String> addressColumn;
	private TableColumn<DisassembledInstCell, String> instColumn;

	private static final double SPACING = 5;
	
	private static final double BREAK_WIDTH = 26;
	private static final double PC_WIDTH = 26;
	private static final double ADDRESS_WIDTH = 50;
	private static final double INST_WIDTH = 200;

	private static final double CELL_HEIGHT = 18;
	private static final double SCROLLBAR_WIDTH = 15; //Ancho adicional para que la scrollbar del visor no colisione con la última columna.
	
	public static final double TOTAL_WIDTH = BREAK_WIDTH + PC_WIDTH + ADDRESS_WIDTH + INST_WIDTH + SCROLLBAR_WIDTH;
	
	public CodeVisor(ControlUnitAdapter cua) {
		this.cua = cua;
		title = new Label("Visor de código");

		tv = new TableView<>();
		cvt = new CodeVisorToolbar(cua);
		
		breakColumn = new TableColumn<>("BP");
		pcColumn = new TableColumn<>("PC");
		addressColumn = new TableColumn<>("Dir");
		instColumn = new TableColumn<>("Instrucción");
		
		setLookAndFeel();
		setContainers();
		setProperties();
	}
	
	private void setProperties() {
		tv.setRowFactory(tab -> {
			TableRow<DisassembledInstCell> row = new TableRow<>();

			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (!row.isEmpty()))
					cua.toggleBreakpoint(row.getIndex());
			});
			
			return row;
		});
		
		
		tv.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				int index = tv.getSelectionModel().getSelectedIndex();
				cua.toggleBreakpoint(index);
			}
		});
		
		
		breakColumn.setResizable(false);
		pcColumn.setResizable(false);
		addressColumn.setResizable(false);
		instColumn.setResizable(false);

		breakColumn.setReorderable(false);
		pcColumn.setReorderable(false);
		addressColumn.setReorderable(false);
		instColumn.setReorderable(false);
		
		breakColumn.setSortable(false);
		pcColumn.setSortable(false);
		addressColumn.setSortable(false);
		instColumn.setSortable(false);
		
		
		breakColumn.setCellValueFactory(cellData -> cellData.getValue().getBreakpointProperty());
		pcColumn.setCellValueFactory(cellData -> cellData.getValue().getPCProperty());
		addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddressProperty());
		instColumn.setCellValueFactory(cellData -> cellData.getValue().getInstructionProperty());
		

		VBox.setVgrow(tv, Priority.ALWAYS);
	}
	
	private void setContainers() {
		tv.setItems(cua.getDisassembledInstCells());
		tv.getColumns().add(breakColumn);
		tv.getColumns().add(pcColumn);
		tv.getColumns().add(addressColumn);
		tv.getColumns().add(instColumn);
		getChildren().addAll(title, cvt, tv);
	}
	
	private void setLookAndFeel() {
		setCellFactories();
		
		tv.setFixedCellSize(CELL_HEIGHT);
		
		breakColumn.setMinWidth(BREAK_WIDTH);
		breakColumn.setMaxWidth(BREAK_WIDTH);
		pcColumn.setMinWidth(PC_WIDTH);
		pcColumn.setMaxWidth(PC_WIDTH);
		addressColumn.setMinWidth(ADDRESS_WIDTH);
		addressColumn.setMaxWidth(ADDRESS_WIDTH);
		instColumn.setMinWidth(INST_WIDTH);
		instColumn.setMaxWidth(INST_WIDTH);

		tv.setMinWidth(TOTAL_WIDTH);
		tv.setMaxWidth(TOTAL_WIDTH);
		
		this.setSpacing(SPACING);
		
		//Configuración de la fuente.
		title.setFont(AppFonts.getStandardBoldFont());
	}
	
	private void setCellFactories() {
		String style = "-fx-alignment: CENTER;"
					 + "-fx-font-family:'" + AppFonts.valueFont.getFamily() + "';"
					 + "-fx-font-size: " + AppFonts.valueFont.getSize() + ";"
					 + "-fx-padding: 0 0 0 0";

		breakColumn.setCellFactory(column -> new TableCell<DisassembledInstCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				setText(null);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? "\u2b24" : "");
					setStyle(style);
				}
			}
		});
		
		pcColumn.setCellFactory(column -> new TableCell<DisassembledInstCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				setText(null);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? "\uD83E\uDC82" : "");
					setStyle(style);
				}
			}
		});
		
		addressColumn.setCellFactory(column -> new TableCell<DisassembledInstCell, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				setStyle(style);
			}
		});
		
		instColumn.setCellFactory(column -> new TableCell<DisassembledInstCell, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				setStyle(style);
			}
		});
	}
}
