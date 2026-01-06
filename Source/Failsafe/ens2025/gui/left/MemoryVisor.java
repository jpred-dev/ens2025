package ens2025.gui.left;

import data.RData;
import data.RWData;
import ens2025.gui.common.AppFonts;
import ens2025.gui.common.ValueFieldDialog;
import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.MemoryCell;
import ens2025.gui.common.adapters.SettingsAdapter;
import ens2025.gui.common.properties.DataProperty;
import enums.RegisterID;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MemoryVisor extends VBox {
	private Label title;
	private MemoryVisorToolbar mvt;
	private DataProperty sp;
	private ControlUnitAdapter cua;
	private SettingsAdapter sa;
	private StackPane visorContainer;
	
	private TableView<MemoryCell> tv;
	private TableColumn<MemoryCell, Boolean> pcColumn;
	private TableColumn<MemoryCell, String> addressColumn;
	private TableColumn<MemoryCell, String> valueColumn;
	private TableColumn<MemoryCell, Boolean> codeZoneColumn;
	private TableColumn<MemoryCell, Boolean> stackZoneColumn;
	private TableColumn<MemoryCell, Boolean> IXColumn;
	private TableColumn<MemoryCell, Boolean> IYColumn;
	
	private static final double SPACING = 5;
	
	private static final double PC_WIDTH = 26;
	private static final double ADDRESS_WIDTH = 50;
	private static final double VALUE_WIDTH = 50;
	private static final double CODE_WIDTH = 22;
	private static final double STACK_WIDTH = 22;
	private static final double IX_WIDTH = 26;
	private static final double IY_WIDTH = 26;
	
	private static final double CELL_HEIGHT = 18;
	private static final double SCROLLBAR_WIDTH = 15; //Ancho adicional para que la scrollbar del visor no colisione con la última columna.
	
	public static final double TOTAL_WIDTH = PC_WIDTH + ADDRESS_WIDTH + VALUE_WIDTH + CODE_WIDTH + STACK_WIDTH + IX_WIDTH + IY_WIDTH + SCROLLBAR_WIDTH;
	
	public MemoryVisor(ControlUnitAdapter cua, SettingsAdapter sa) {
		this.cua = cua;
		this.sa = sa;
		title = new Label("Visor de memoria");
		sp = cua.readRegisterDataProperty(RegisterID.SP);

		tv = new TableView<>();
		visorContainer = new StackPane();
		mvt = new MemoryVisorToolbar(cua, sa, tv);
		
		pcColumn = new TableColumn<>("PC");
		addressColumn = new TableColumn<>("Dir");
		valueColumn = new TableColumn<>("Valor");
		codeZoneColumn = new TableColumn<>("C");
		stackZoneColumn = new TableColumn<>("P");
		IXColumn = new TableColumn<>("IX");
		IYColumn = new TableColumn<>("IY");

		setLookAndFeel();
		setContainers();
		setProperties();
		setBehavior();
	}
	
	private void setBehavior() {
		sp.addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				tv.refresh();
			});
		});

		tv.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				RData address = tv.getSelectionModel().getSelectedItem().getAddressProperty().getData();
				DataProperty data = new DataProperty(new RWData(), sa, false);
				new ValueFieldDialog(tv.getScene().getWindow(), data, 6, 60);
				cua.writeMemory(address, data);
				cua.signalMemoryUpdate(data);
				
				e.consume();
			}
		});
	}
	
	private void setProperties() {
		tv.setRowFactory(tab -> {
			TableRow<MemoryCell> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && row.isEmpty() == false) {
					RData address = row.getItem().getAddressProperty().getData();
					DataProperty data = new DataProperty(new RWData(), sa, false);
					new ValueFieldDialog(tab.getScene().getWindow(), data, 6, 60);
					cua.writeMemory(address, data);
					cua.signalMemoryUpdate(data);
				}
			});
			
			return row;
		});
		
		pcColumn.setResizable(false);
		addressColumn.setResizable(false);
		valueColumn.setResizable(false);
		codeZoneColumn.setResizable(false);
		stackZoneColumn.setResizable(false);
		IXColumn.setResizable(false);
		IYColumn.setResizable(false);

		pcColumn.setReorderable(false);
		addressColumn.setReorderable(false);
		valueColumn.setReorderable(false);
		codeZoneColumn.setReorderable(false);
		stackZoneColumn.setReorderable(false);
		IXColumn.setReorderable(false);
		IYColumn.setReorderable(false);

		pcColumn.setSortable(false);
		addressColumn.setSortable(false);
		valueColumn.setSortable(false);
		codeZoneColumn.setSortable(false);
		stackZoneColumn.setSortable(false);
		IXColumn.setSortable(false);
		IYColumn.setSortable(false);
		

		pcColumn.setCellValueFactory(cellData -> cellData.getValue().getPCProperty());
		addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddressProperty());
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValueProperty());
		codeZoneColumn.setCellValueFactory(cellData -> cellData.getValue().getCodeProperty());
		stackZoneColumn.setCellValueFactory(cellData -> cellData.getValue().getStackProperty());
		IXColumn.setCellValueFactory(cellData -> cellData.getValue().getIXProperty());
		IYColumn.setCellValueFactory(cellData -> cellData.getValue().getIYProperty());
		
		
		VBox.setVgrow(tv, Priority.ALWAYS);
		VBox.setVgrow(visorContainer, Priority.ALWAYS);
	}
	
	private void setContainers() {
		tv.setItems(cua.getMemoryCells());
		tv.getColumns().add(pcColumn);
		tv.getColumns().add(addressColumn);
		tv.getColumns().add(valueColumn);
		tv.getColumns().add(codeZoneColumn);
		tv.getColumns().add(stackZoneColumn);
		tv.getColumns().add(IXColumn);
		tv.getColumns().add(IYColumn);
		tv.setSortPolicy(tab -> null);
		
		visorContainer.getChildren().add(tv);
		
		getChildren().addAll(title, mvt, visorContainer);
	}
	
	private void setLookAndFeel() {
		setCellFactories();
		
		tv.setFixedCellSize(CELL_HEIGHT);
		
		pcColumn.setMinWidth(PC_WIDTH);
		pcColumn.setMaxWidth(PC_WIDTH);
		addressColumn.setMinWidth(ADDRESS_WIDTH);
		addressColumn.setMaxWidth(ADDRESS_WIDTH);
		valueColumn.setMinWidth(VALUE_WIDTH);
		valueColumn.setMaxWidth(VALUE_WIDTH);
		codeZoneColumn.setMinWidth(CODE_WIDTH);
		codeZoneColumn.setMaxWidth(CODE_WIDTH);
		stackZoneColumn.setMinWidth(STACK_WIDTH);
		stackZoneColumn.setMaxWidth(STACK_WIDTH);
		IXColumn.setMinWidth(IX_WIDTH);
		IXColumn.setMaxWidth(IX_WIDTH);
		IYColumn.setMinWidth(IY_WIDTH);
		IYColumn.setMaxWidth(IY_WIDTH);
		
		tv.setMinWidth(TOTAL_WIDTH);
		tv.setMaxWidth(TOTAL_WIDTH);
		
		//Configuración de la fuente.
		title.setFont(AppFonts.getStandardBoldFont());
		
		this.setSpacing(SPACING);
		
	}
	
	private void setCellFactories() {
		String cellStyle = "-fx-alignment: CENTER;"
					 + "-fx-font-family:'" + AppFonts.valueFont.getFamily() + "';"
					 + "-fx-font-weight: normal;"
					 + "-fx-font-size: " + AppFonts.valueFont.getSize() + ";"
					 + "-fx-padding: 0 0 0 0";

		pcColumn.setCellFactory(column -> new TableCell<MemoryCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? ">" : "");
					setStyle(cellStyle);
				}
			}
		});
		
		addressColumn.setCellFactory(column -> new TableCell<MemoryCell, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				setStyle(cellStyle);
			}
		});
		
		valueColumn.setCellFactory(column -> new TableCell<MemoryCell, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				setStyle(cellStyle);
			}
		});
		
		codeZoneColumn.setCellFactory(column -> new TableCell<MemoryCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? "X" : "");
					setStyle(cellStyle);
				}
			}
		});

		stackZoneColumn.setCellFactory(column -> new TableCell<MemoryCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				setText(null);
				setGraphic(null);
				setStyle(null);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					if (sp.getData().isEqual(this.getIndex()))
						setText("<");
					else
						setText(item ? "X" : "");
					setStyle(cellStyle);
				}
			}
		});
		
		IXColumn.setCellFactory(column -> new TableCell<MemoryCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? "<" : "");
					setStyle(cellStyle);
				}
			}
		});
		
		IYColumn.setCellFactory(column -> new TableCell<MemoryCell, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null) {
					setText(" ");
					setGraphic(null);
				} else {
					setText(item ? "<" : "");
					setStyle(cellStyle);
				}
			}
		});
	}
	
	public void refresh() {
		tv.refresh();
	}
}
