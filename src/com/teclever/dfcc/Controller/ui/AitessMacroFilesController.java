package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.model.AitessMacroFiles;
import com.teclever.dfcc.model.AitessMacroFiles.AitessMacroDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AitessMacroFilesController {
	private GridPane macroFilesParentGridPane = new GridPane();
	private GridPane macroFilesTitleGridPane = new GridPane();
	private GridPane macroFilemidGridPane = new GridPane();
	private GridPane macroFileTableGridPane = new GridPane();

	private Label aitessTypeLabel = new Label("AITESS TYPE");
	private Label driverLabel = new Label("DRIVER");
	private Label configFileLabel = new Label("CONFIG FILE");

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
	private MacroFileManagement macroFileManagement = new MacroFileManagement();

	private TableViewFactory<AitessMacroFiles> userFactory = new MacroFilesTableViewFactory();
	private CustomTableView<AitessMacroFiles> customTableView_macroFiles;

	public AitessMacroFilesController() {
		uut_type_field = new ComboBox<>();
		test_type_field = new ComboBox<>();
		initializeUUTTypeComboBox();
	}

	public GridPane macroFilesConfigParentGrid() {
		macroFilesParentGridPane.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/AitessMacroFiles.css").toExternalForm());
		macroFilesParentGridPane.getStyleClass().add("macroFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		macroFilesParentGridPane.setPadding(new Insets(10));
		macroFilesParentGridPane.setVgap(5);

		macroFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		macroFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		macroFilesParentGridPane.add(macroFilesTopContainer(), 0, 0);
		macroFilesParentGridPane.add(macroFilesMiddleContainer(), 0, 1);
		macroFilesParentGridPane.add(createAitessMacroFilesTable(), 0, 2);

		return macroFilesParentGridPane;
	}

	private GridPane macroFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		macroFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		macroFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		macroFilesTitleGridPane.add(headerHbox(), 0, 0);
		macroFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return macroFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("MACRO FILES");
		headerLabel.getStyleClass().add("macroFiles-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER_LEFT);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createButtonHbox() {

		Button addFileButton = new Button("ADD FILE");
		addFileButton.setOnAction(e -> onClickAddFileButton());
		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addFileButton);
		return headerButtonHbox;
	}

	private GridPane macroFilesMiddleContainer() {
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

		macroFilemidGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		macroFilemidGridPane.getRowConstraints().addAll(firstRow);
		macroFilemidGridPane.setAlignment(Pos.CENTER);
		macroFilemidGridPane.setPadding(new Insets(10));

		macroFilemidGridPane.add(createUutTypeField(), 0, 0);
		macroFilemidGridPane.add(createTestTypeField(), 1, 0);
		macroFilemidGridPane.add(createAitessType(), 2, 0);
		macroFilemidGridPane.add(createDriverNameLabel(), 3, 0);
		macroFilemidGridPane.add(createConfigLabel(), 4, 0);

		macroFilemidGridPane.getStyleClass().add("macroFiles-Container");
		return macroFilemidGridPane;
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
				return uut.getUutId();
			}
		}
		return null;
	}

	private void initializeTestTypeComboBox() {
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
		setAitessMacroFilesTableData();
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

	private GridPane createAitessMacroFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		macroFileTableGridPane.getColumnConstraints().addAll(firstColumn);
		macroFileTableGridPane.getRowConstraints().addAll(firstRow);

		setAitessMacroFilesTableData();
		return macroFileTableGridPane;
	}
	private void onClickAddFileButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(macroFilesParentGridPane.getScene().getWindow());
//		 if (selectedFile != null) {
//	            String filePath = selectedFile.getAbsolutePath();
//	            VDDResponse response = vddManagement.extractingVDDFile(filePath);
//	            if(response.getResponse().getResponseCode()==1) {
//	            	Notifications.showSuccessAlert("File Uploaded Successfully");
//	            	refreshVddConfigList();
//	            }else if(response.getResponse().getResponseCode()==0) {
//	            	Notifications.showSuccessAlert(response.getResponse().getResponseMessage());
//	            }
//	      }	
	}

	private void setAitessMacroFilesTableData() {
		
		List<MacroDto> macroFileDtoList = macroFileManagement.getAllMacros(RUN_CONFIG_ID);
		ObservableList<AitessMacroFiles> tableData = FXCollections.observableArrayList();
		List<AitessMacroDetails> detailsList = new ArrayList<>();

		for (MacroDto macroDto : macroFileDtoList) {
			AitessMacroFiles existingFile = tableData.stream()
					.filter(f -> f.getFileName().equals(macroDto.getFileName())).findFirst().orElse(null);

			if (existingFile == null) {
				AitessMacroFiles newFile = new AitessMacroFiles();
				newFile.setFileName(macroDto.getFileName());
				tableData.add(newFile);
			}

			AitessMacroDetails macrodetails = new AitessMacroDetails();
			macrodetails.setFileName(macroDto.getFileName());
			macrodetails.setMacroName(macroDto.getMacroName());;
			detailsList.add(macrodetails);
		}
		
		
		customTableView_macroFiles = userFactory.createTableView(tableData, true, false);
		customTableView_macroFiles.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessMacroFiles> selectedItems = customTableView_macroFiles.getSelectedItems();
			for (AitessMacroFiles rowData : selectedItems) {
				try {
					FXMLLoader addStagePopup = new FXMLLoader(
							getClass().getResource("/com/teclever/dfcc/ui/fxml/AitessMacroPopup.fxml"));
					Parent root = addStagePopup.load();

					AitessMacroPopupController controller = new AitessMacroPopupController();
					controller = addStagePopup.getController();
					List<AitessMacroDetails> fileDetails = detailsList.stream()
							.filter(detail -> detail.getFileName().equals(rowData.getFileName()))
							.collect(Collectors.toList());
					controller.setMacroDetails(fileDetails);
					
					Stage stage = new Stage();
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.initStyle(StageStyle.UNDECORATED);
					stage.centerOnScreen();
					stage.setScene(new Scene(root));
					stage.showAndWait();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		customTableView_macroFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessMacroFiles> selectedItems = customTableView_macroFiles.getSelectedItems();
			for (AitessMacroFiles rowData : selectedItems) {
			}
		});
		macroFileTableGridPane.add(customTableView_macroFiles, 0, 0);
	}
}

class MacroFilesTableViewFactory implements TableViewFactory<AitessMacroFiles> {
	@Override
	public CustomTableView<AitessMacroFiles> createTableView(ObservableList<AitessMacroFiles> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessMacroFiles.class, addUserColumn, addCheckboxColumn);
	}
}
