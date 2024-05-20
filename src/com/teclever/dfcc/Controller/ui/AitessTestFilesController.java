package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.model.AitessDownloadCode;
import com.teclever.dfcc.model.AitessMacroFiles;
import com.teclever.dfcc.model.AitessMacroFiles.AitessMacroDetails;
import com.teclever.dfcc.model.AitessTestFiles;
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

public class AitessTestFilesController {
	private GridPane testFilesParentGridPane = new GridPane();
	private GridPane testFilesTitleGridPane = new GridPane();
	private GridPane testFilemidGridPane = new GridPane();
	private GridPane testFileTableGridPane = new GridPane();

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
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();

	private TableViewFactory<AitessTestFiles> userFactory = new TestFilesTableViewFactory();
	private CustomTableView<AitessTestFiles> customTableView_testFiles;

	public AitessTestFilesController() {
		uut_type_field = new ComboBox<>();
		test_type_field = new ComboBox<>();
		initializeUUTTypeComboBox();
	}

	public GridPane testFilesConfigParentGrid() {
		testFilesParentGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AitessTestFiles.css").toExternalForm());
		testFilesParentGridPane.getStyleClass().add("testFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		testFilesParentGridPane.setPadding(new Insets(10));
		testFilesParentGridPane.setVgap(5);

		testFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		testFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		testFilesParentGridPane.add(testFilesTopContainer(), 0, 0);
		testFilesParentGridPane.add(testFilesMiddleContainer(), 0, 1);
		testFilesParentGridPane.add(createAitessTestFilesTable(), 0, 2);

		return testFilesParentGridPane;
	}

	private GridPane testFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		testFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		testFilesTitleGridPane.add(headerHbox(), 0, 0);
		testFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return testFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("TEST FILES");
		headerLabel.getStyleClass().add("testFiles-headerLabel");

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

	private GridPane testFilesMiddleContainer() {
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

		testFilemidGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		testFilemidGridPane.getRowConstraints().addAll(firstRow);
		testFilemidGridPane.setAlignment(Pos.CENTER);
		testFilemidGridPane.setPadding(new Insets(10));

		testFilemidGridPane.add(createUutTypeField(), 0, 0);
		testFilemidGridPane.add(createTestTypeField(), 1, 0);
		testFilemidGridPane.add(createAitessType(), 2, 0);
		testFilemidGridPane.add(createDriverNameLabel(), 3, 0);
		testFilemidGridPane.add(createConfigLabel(), 4, 0);

		testFilemidGridPane.getStyleClass().add("testFiles-Container");
		return testFilemidGridPane;
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
		setAitessTestFilesTableData();
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

	private GridPane createAitessTestFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFileTableGridPane.getColumnConstraints().addAll(firstColumn);
		testFileTableGridPane.getRowConstraints().addAll(firstRow);

		setAitessTestFilesTableData();
		return testFileTableGridPane;
	}

	private void onClickAddFileButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(testFilesParentGridPane.getScene().getWindow());
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

	private void setAitessTestFilesTableData() {
		List<TestFileDto> testFileList = testPlanFileManagement.getAllTestFiles(RUN_CONFIG_ID);
		ObservableList<AitessTestFiles> tableData = FXCollections.observableArrayList();

		for (TestFileDto testFileDto : testFileList) {
			AitessTestFiles testFileData = new AitessTestFiles();
			testFileData.setFileName(testFileDto.getTestFileName());
			tableData.add(testFileData);
		}

		customTableView_testFiles = userFactory.createTableView(tableData, true, false);

		customTableView_testFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessTestFiles> selectedItems = customTableView_testFiles.getSelectedItems();
			for (AitessTestFiles rowData : selectedItems) {
			}
		});
		testFileTableGridPane.add(customTableView_testFiles, 0, 0);
	}
}

class TestFilesTableViewFactory implements TableViewFactory<AitessTestFiles> {
	@Override
	public CustomTableView<AitessTestFiles> createTableView(ObservableList<AitessTestFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessTestFiles.class, addUserColumn, addCheckboxColumn);
	}
}

//package com.teclever.dfcc.Controller.ui;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
//import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
//import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
//import com.teclever.dfcc.datastore.dto.TestFileDto;
//import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
//import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
//import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
//import com.teclever.dfcc.model.TestFiles;
//import com.teclever.dfcc.utils.CustomTableView;
//import com.teclever.dfcc.utils.TableViewFactory;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.RowConstraints;
//
//class TestFilesTableViewFactory implements TableViewFactory<TestFiles> {
//	@Override
//	public CustomTableView<TestFiles> createTableView(ObservableList<TestFiles> items, boolean addUserColumn,
//			boolean addCheckboxColumn) {
//		return new CustomTableView<>(items, TestFiles.class, addUserColumn, addCheckboxColumn);
//	}
//}
//
//public class AitessTestFilesController {
//
//	private GridPane testFilesMainGridPane = new GridPane();
//	private GridPane testFilesTitleGridPane = new GridPane();
//	private GridPane testFilesTableGridPane = new GridPane();
//	private GridPane testFilesOptionGridPane = new GridPane();
//	
//	private HBox uutTypeHBox =new HBox();
//	private HBox testTypeHBox = new HBox();
//	private HBox aitessTypeHBox = new HBox();
//	private HBox driverTypeHBox = new HBox();
//	private HBox configFileHBox = new HBox();
//	
//	private ComboBox<String> uutTypeComboBox = new ComboBox<String>();
//	private ComboBox<String> testTypeComboBox = new ComboBox<String>();
//	private Label aitessTypeLabel = new Label();
//	private Label driverTypeLabel = new Label();
//	private Label configFileLabel = new Label();
//
//	Map<String, String> uutTypeMap = new HashMap<>();
//	Map<String, String> testTypeMap = new HashMap<>();
//
//	
//	AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
//	RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
//	TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
//	
//	public void refreshVddConfigList() {
////		setTableData();
//	}
//	
//	public GridPane createVddConfigGridPane() {
//		testFilesMainGridPane.getStylesheets()
//				.add(getClass().getResource("/com/teclever/dfcc/ui/css/TestFiles.css").toExternalForm());
//		testFilesMainGridPane.getStyleClass().add("test-file-container");
//		
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(7);
//		RowConstraints  thirdRow= new RowConstraints();
//		thirdRow.setPercentHeight(86);
//		
//		testFilesMainGridPane.setPadding(new Insets(10));
//		testFilesMainGridPane.setVgap(5);
//		testFilesMainGridPane.getColumnConstraints().addAll(firstColumn);
//		testFilesMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//
//		testFilesMainGridPane.add(createTestFilesTitleGridPane(), 0, 0);
//		testFilesMainGridPane.add(createTestFilesOptionPane(), 0, 1);
//		testFilesMainGridPane.add(createTestFilesTable(), 0, 2);
//		return testFilesMainGridPane;
//	}
//
//	private GridPane createTestFilesTitleGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		testFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		testFilesTitleGridPane.getRowConstraints().addAll(firstRow);
//
//		HBox titleBox = new HBox();
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		Label title = new Label("Test Files");
//		title.getStyleClass().add("test-file-title");
//		titleBox.getChildren().add(title);
//
//		HBox addUserBox = new HBox();
//		addUserBox.setAlignment(Pos.CENTER_RIGHT);
//		Button addUserBtn = new Button("Upload File");
//		addUserBtn.getStyleClass().add("test-file-upload-btn");
//		addUserBox.getChildren().add(addUserBtn);
//
//		addUserBtn.setOnAction(e -> {
////			uploadfile();
//		});
//
//		testFilesTitleGridPane.add(titleBox, 0, 0);
//		testFilesTitleGridPane.add(addUserBox, 1, 0);
//
//		return testFilesTitleGridPane;
//	}
//
//	private GridPane createTestFilesOptionPane() {
//		testFilesOptionGridPane.getStyleClass().add("test-file-option-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(17);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(17);
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(18);
//		ColumnConstraints fourthColumn = new ColumnConstraints();
//		fourthColumn.setPercentWidth(18);
//		ColumnConstraints fifthColumn = new ColumnConstraints();
//		fifthColumn.setPercentWidth(28);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		testFilesOptionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn);
//		testFilesOptionGridPane.getRowConstraints().addAll(firstRow);
//		
//		testFilesOptionGridPane.add(createUutTypeBox(), 0, 0);
//		testFilesOptionGridPane.add(createTestTypeBox(), 1, 0);
//		testFilesOptionGridPane.add(createAitessTypeBox(), 2, 0);
//		testFilesOptionGridPane.add(createDriverTypeBox(), 3, 0);
//		testFilesOptionGridPane.add(createConfigFileBox(), 4, 0);
//		
//		return testFilesOptionGridPane;
//	}
//
//	private HBox createUutTypeBox() {
//		uutTypeHBox.setAlignment(Pos.CENTER);
//		uutTypeComboBox.setPromptText("UUT TYPE");
//		UUTMasterDetailsDto[] uutList=aitessConfigurationManagement.getAllUUT();
//		for(UUTMasterDetailsDto list : uutList) {
//			uutTypeMap.put(list.getUutType(),list.getUutId());
//		}
//		
//		 uutTypeComboBox.setOnAction(e ->{
//			setTestTypeData();
//		 });
//		
//        uutTypeComboBox.getItems().addAll(uutTypeMap.keySet());
//        uutTypeHBox.getChildren().add(uutTypeComboBox);
//		return uutTypeHBox;
//	}
//
//	private HBox createTestTypeBox() {
//		testTypeHBox.setAlignment(Pos.CENTER);
//		testTypeComboBox.setPromptText("TEST TYPE");
//		setTestTypeData();
//		 testTypeComboBox.setOnAction(e -> {
//			 aitessTypeLabel.setVisible(false);
//			 driverTypeLabel.setVisible(false);
//			 configFileLabel.setVisible(false);
//			 setDataForSelectedUutAndTest();
//		 });
//        testTypeHBox.getChildren().add(testTypeComboBox);
//		return testTypeHBox;
//	}
//
//	private void setTestTypeData() {
//	    testTypeMap.clear();
//	    String uutTypeId = uutTypeMap.get(uutTypeComboBox.getValue());
//	    if(uutTypeId != null) {
//		    TestTypeMasterDetailsDto[] uutList = runConfigurationManagement.getTestTypeByUUTId(uutTypeId);
//	        for (TestTypeMasterDetailsDto list : uutList) {
//		        testTypeMap.put(list.getTestName(), list.getTestTypeId());
//		    }
//		    testTypeComboBox.getItems().clear();
//		    testTypeComboBox.getItems().addAll(testTypeMap.keySet());
//	    }
//	}
//
//
//	private HBox createAitessTypeBox() {
//	    aitessTypeHBox.setAlignment(Pos.CENTER);
//	    aitessTypeLabel.getStyleClass().add("test-file-label");
//	    aitessTypeLabel.setVisible(false);
//	    HBox.setHgrow(aitessTypeLabel, Priority.ALWAYS);
//	    aitessTypeHBox.getChildren().add(aitessTypeLabel);
//	    return aitessTypeHBox;
//	}
//
//	private HBox createDriverTypeBox() {
//	    driverTypeHBox.setAlignment(Pos.CENTER);
//	    driverTypeLabel.getStyleClass().add("test-file-label");
//	    driverTypeLabel.setVisible(false);
//	    HBox.setHgrow(driverTypeLabel, Priority.ALWAYS);
//	    driverTypeHBox.getChildren().add(driverTypeLabel);
//	    return driverTypeHBox;
//	}
//
//	private HBox createConfigFileBox() {
//	    configFileHBox.setAlignment(Pos.CENTER);
//	    configFileLabel.getStyleClass().add("test-file-label");
//	    configFileLabel.setVisible(false);
//	    HBox.setHgrow(configFileLabel, Priority.ALWAYS);
//	    configFileHBox.getChildren().add(configFileLabel);
//	    return configFileHBox;
//	}
//
//
//	private Node createTestFilesTable() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		testFilesTableGridPane.getColumnConstraints().addAll(firstColumn);
//		testFilesTableGridPane.getRowConstraints().addAll(firstRow);
//		setDataForSelectedUutAndTest();
//		return testFilesTableGridPane;
//	}
//	
//
//	private void setTableData(String runConfigId) {
//		ObservableList<TestFiles> tableData = FXCollections.observableArrayList();
//		if(runConfigId != null) {
//			List<TestFileDto> testFileList = testPlanFileManagement.getAllTestFiles(runConfigId);
//			if (testFileList.size() > 0 ) {
//				for (TestFileDto file :testFileList) {
//					TestFiles testFile = new TestFiles();
//					testFile.setTestFileName(file.getTestFileName());		
//					tableData.add(testFile);
//				}
//			}
//		}else {
//			 tableData.clear();
//		}
//		
//		TableViewFactory<TestFiles> userFactory = new TestFilesTableViewFactory();
//		CustomTableView<TestFiles> customTableView = userFactory.createTableView(tableData, true, false);
//
//		testFilesTableGridPane.add(customTableView, 0, 0);
//	}
//
//	private void setDataForSelectedUutAndTest() {
//    	setTableData(null);
//	    String uutTypeId = uutTypeMap.get(uutTypeComboBox.getValue());
//	    String testTypeId = testTypeMap.get(testTypeComboBox.getValue());
//	    if(uutTypeId != null && testTypeId != null) {
//	    	List<RunConfigurationDto> runCongifList = runConfigurationManagement.getAllRunConfig();
//	    	if(runCongifList.size() > 0) {
//	    		for(RunConfigurationDto runConfig : runCongifList ) {
//		    		if(runConfig.getUutId().equals(uutTypeId)) {
//		    			if(runConfig.getTestTypeId().equals(testTypeId)) {
//		    				aitessTypeLabel.setVisible(true);
//		    				aitessTypeLabel.setText(runConfig.getAitess());
//		    				
//		    				driverTypeLabel.setVisible(true);
//		    				driverTypeLabel.setText(runConfig.getDriver());
//		    				
//		    				configFileLabel.setVisible(true);
//		    				configFileLabel.setText(runConfig.getConfigFile());
//		    				
//		    				setTableData(runConfig.getRunConfigId());
//		    			}
//		    		}
//		    	}
//	    	}   		
//	    }
//	}
//}
