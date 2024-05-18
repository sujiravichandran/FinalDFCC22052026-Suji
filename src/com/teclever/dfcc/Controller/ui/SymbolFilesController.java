package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.model.SymbolFile;
import com.teclever.dfcc.model.User;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class SymbolFilesController {
	private GridPane symbolFilesParentGridPane = new GridPane();
	private GridPane symbolFilesTitleGridPane = new GridPane();
	private GridPane symbolFilemidGridPane = new GridPane();
	private GridPane symbolFileTableGridPane = new GridPane();

	private Label aitessTypeLabel = new Label("AITESS TYPE");
	private Label driverLabel = new Label("DRIVER");
	private Label configFileLabel;

	private ComboBox<String> uut_type_field;
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ComboBox<String> test_type_field;
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();
	private String TEST_TYPE_ID;
	private String RUN_CONFIG_ID;

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private SymbolFileManagement symbolFileManagement = new SymbolFileManagement();

	private TableViewFactory<SymbolFile> userFactory = new SymbolFileTableViewFactory();
	private CustomTableView<SymbolFile> customTableView_symbolFiles;

	public SymbolFilesController() {
		uut_type_field = new ComboBox<>();
		test_type_field = new ComboBox<>();
		initializeUUTTypeComboBox();
	}

	public GridPane symbolFilesConfigParentGrid() {
		symbolFilesParentGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/SymbolFiles.css").toExternalForm());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		symbolFilesParentGridPane.setPadding(new Insets(10));
		symbolFilesParentGridPane.setVgap(5);

		symbolFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		symbolFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		symbolFilesParentGridPane.add(symbolFilesTopContainer(), 0, 0);
		symbolFilesParentGridPane.add(symbolFilesMiddleContainer(), 0, 1);
		symbolFilesParentGridPane.add(createSymbolFileTable(), 0, 2);

		return symbolFilesParentGridPane;
	}

	private GridPane symbolFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		symbolFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		symbolFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		symbolFilesTitleGridPane.add(headerHbox(), 0, 0);
		symbolFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return symbolFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("SYMBOL FILES");
		headerLabel.getStyleClass().add("symbolFiles-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER_LEFT);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createButtonHbox() {

		Button addSymbolButton = new Button("ADD SYMBOLS");
//		addButton.setOnAction(e -> onClickGETButton());
		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addSymbolButton);
		return headerButtonHbox;
	}

	private GridPane symbolFilesMiddleContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(15);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(15);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(15);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(15);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(40);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		symbolFilemidGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		symbolFilemidGridPane.getRowConstraints().addAll(firstRow);
		symbolFilemidGridPane.setAlignment(Pos.CENTER);
		symbolFilemidGridPane.setPadding(new Insets(10));

		symbolFilemidGridPane.add(createUutTypeField(), 0, 0);
		symbolFilemidGridPane.add(createTestTypeField(), 1, 0);
		symbolFilemidGridPane.add(createAitessType(), 2, 0);
		symbolFilemidGridPane.add(createDriverNameLabel(), 3, 0);
		symbolFilemidGridPane.add(createConfigLabel(), 4, 0);

		symbolFilemidGridPane.getStyleClass().add("symbolFiles-Container");
		return symbolFilemidGridPane;
	}

	private HBox createUutTypeField() {
		uut_type_field.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uut_type_field);

		return uutTypeHBox;
	}

	private HBox createTestTypeField() {
		test_type_field.setPromptText("TEST TYPE");

		HBox testTypeHBox = new HBox(10);
		testTypeHBox.setAlignment(Pos.CENTER);
		testTypeHBox.getChildren().add(test_type_field);

		return testTypeHBox;
	}

	private HBox createAitessType() {

		aitessTypeLabel.setPadding(new Insets(10));
		aitessTypeLabel.setPrefWidth(200);
		aitessTypeLabel.setAlignment(Pos.CENTER);
		aitessTypeLabel.setPadding(new Insets(0, 0, 0, 0));

		Tooltip aitessTooltip = new Tooltip();
		aitessTooltip.textProperty().bind(aitessTypeLabel.textProperty());
		aitessTypeLabel.setTooltip(aitessTooltip);

		HBox aitessTypeHBox = new HBox(10);
		aitessTypeHBox.setAlignment(Pos.CENTER);
		aitessTypeHBox.getChildren().add(aitessTypeLabel);
		aitessTypeLabel.getStyleClass().add("label_field");

		return aitessTypeHBox;
	}

	private HBox createDriverNameLabel() {
		driverLabel.setPadding(new Insets(10));
		driverLabel.setPrefWidth(200);
		driverLabel.setAlignment(Pos.CENTER);
		driverLabel.setPadding(new Insets(0, 0, 0, 0));
		driverLabel.getStyleClass().add("label_field");

		Tooltip driverTooltip = new Tooltip();
		driverTooltip.textProperty().bind(driverLabel.textProperty());
		driverLabel.setTooltip(driverTooltip);

		HBox driverTypeHBox = new HBox(10);
		driverTypeHBox.setAlignment(Pos.CENTER);
		driverTypeHBox.getChildren().add(driverLabel);

		return driverTypeHBox;
	}

	private HBox createConfigLabel() {
		configFileLabel = new Label("CONFIG FILE");
		configFileLabel.setAlignment(Pos.CENTER);
		configFileLabel.getStyleClass().add("label_field");
		configFileLabel.setPadding(new Insets(0, 0, 0, 0));
		configFileLabel.setMaxWidth(Double.MAX_VALUE);

		Tooltip configFileTooltip = new Tooltip();
		configFileTooltip.textProperty().bind(configFileLabel.textProperty());
		configFileLabel.setTooltip(configFileTooltip);

		HBox configLabelHBox = new HBox(10);
		configLabelHBox.setAlignment(Pos.CENTER);
		configLabelHBox.setMaxWidth(Double.MAX_VALUE);
		configLabelHBox.getChildren().add(configFileLabel);

		HBox.setHgrow(configFileLabel, Priority.ALWAYS);
		HBox.setHgrow(configLabelHBox, Priority.ALWAYS);

		return configLabelHBox;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uut_type_field.setItems(uutTypeList);
		uut_type_field.setOnAction((event) -> {
			this.UUT_ID = fetchUutId(uut_type_field.getValue());
			testTypeList.clear();
			initializeTestTypeComboBox();
		});
	}

	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				System.out.println("234 " + uut.getUutId());
				return uut.getUutId();
			}
		}
		return null;
	}

	private void initializeTestTypeComboBox() {
		System.out.println("COMBO_UUT: " + UUT_ID);
		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
		}
		test_type_field.setItems(testTypeList);
		test_type_field.setOnAction((event) -> fetchingTableData());
	}

	private String fetchTestTypeId(String testTypeName) {
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestName().equals(testTypeName)) {
				return testType.getTestTypeId();
			}
		}
		return null;
	}

	private void fetchingTableData() {
		TEST_TYPE_ID = fetchTestTypeId(test_type_field.getValue());
		RUN_CONFIG_ID = fetchRunConfigID(TEST_TYPE_ID);
		setSymbolFileTableData();
	}

	private String fetchRunConfigID(String testTypeID) {
		List<RunConfigurationDto> allRunConfigData = runConfig.getAllRunConfig();
		String runConfigId = null;
		for (RunConfigurationDto runConfigDto : allRunConfigData) {
			if (runConfigDto.getUutId().equals(UUT_ID) && runConfigDto.getTestTypeId().equals(testTypeID)) {
				runConfigId = runConfigDto.getRunConfigId();
				aitessTypeLabel.setText(runConfigDto.getAitess());
				driverLabel.setText(runConfigDto.getDriver());
				configFileLabel.setText(runConfigDto.getConfigFile());
				break;
			} else {
				aitessTypeLabel.setText("AITESS TYPE");
				driverLabel.setText("DRIVER");
				configFileLabel.setText("CONFIG FILE");
			}
		}
		return runConfigId;
	}

	private GridPane createSymbolFileTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		symbolFileTableGridPane.getColumnConstraints().addAll(firstColumn);
		symbolFileTableGridPane.getRowConstraints().addAll(firstRow);

		setSymbolFileTableData();
		return symbolFileTableGridPane;
	}

	private void setSymbolFileTableData() {
		List<SymbolDto> symbolDtoList = symbolFileManagement.getAllSymbols(RUN_CONFIG_ID);
		ObservableList<SymbolFile> tableData = FXCollections.observableArrayList();

		for (SymbolDto symbols : symbolDtoList) {
			SymbolFile symbolData = new SymbolFile();
			symbolData.setFileName(symbols.getFileName());
			tableData.add(symbolData);
		}
		customTableView_symbolFiles = userFactory.createTableView(tableData, true, false);
		customTableView_symbolFiles.addEventHandler(CustomTableView.EDIT_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<SymbolFile> selectedItems = customTableView_symbolFiles.getSelectedItems();
			for (SymbolFile rowData : selectedItems) {
				System.out.println(rowData);
//				handleAddEditButtonClicked(rowData);
			}
		});

		customTableView_symbolFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<SymbolFile> selectedItems = customTableView_symbolFiles.getSelectedItems();
			for (SymbolFile rowData : selectedItems) {
//				handleDeleteButtonClicked(rowData);
			}
		});
		symbolFileTableGridPane.add(customTableView_symbolFiles, 0, 0);
	}
}

class SymbolFileTableViewFactory implements TableViewFactory<SymbolFile> {
	@Override
	public CustomTableView<SymbolFile> createTableView(ObservableList<SymbolFile> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, SymbolFile.class, addUserColumn, addCheckboxColumn);
	}
}
