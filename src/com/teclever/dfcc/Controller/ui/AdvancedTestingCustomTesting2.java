package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.customtestmanagement.AdvanceCustom1TestingManagement;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class AdvancedTestingCustomTesting2 {

	private GridPane tab4MainGridPane = new GridPane();
	private HBox testTypeHBox = new HBox(10);
	private ComboBox<String> testTypeComboBox = new ComboBox<>();
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	private VBox selectTestFileVBox = new VBox(5);
	private HBox selectTestFileHBox = new HBox(10);
	private Label selectTestFileLabel = new Label("Test File");
	private Label addTestFileLabel = new Label("+");
	private HBox selectedTestFileHBox = new HBox(10);
	private Label selectedTestFileName = new Label();
	private Button selectTestFileRunButton = new Button("Run");

	private VBox downloadCodeVBox = new VBox(5);
	private HBox downloadCodeHBox = new HBox(10);
	private Label downloadCodeLabel = new Label("Download Code");
	private Label addDownloadCodeLabel = new Label("+");
	private HBox selectedDownloadCodeHBox = new HBox(10);
	private Label selectedDownloadCodeName = new Label();
	private Button downloadCodeRunButton = new Button("Run");

//	private HBox baseFileHBox = new HBox(10);
//	private Label baseFileLabel = new Label("Base File");
//	private Label addBaseFileLabel = new Label("+");
//	private HBox selectedBaseFileHBox = new HBox(10);
//	private Label selectedBaseFileName = new Label();

	private HBox checkSumFileHBox = new HBox(10);
	private Label checkSumFileLabel = new Label("CheckSum File");
	private Label addCheckSumFileLabel = new Label("+");
	private HBox selectedCheckSumFileHBox = new HBox(10);
	private Label selectedCheckSumFileName = new Label();

	private GridPane memoryTestGridPane = new GridPane();

	private Label memoryTestLabel = new Label("Memory Test");

	private Label memoryTypeLabel = new Label("Memory Type");
	private Label rwTypeLabel = new Label("R/W Type");
	private Label startAddressLabel = new Label("Start Address");
	private Label endAddressLabel = new Label("End Address");
	private Label ipDataLabel = new Label("I/P Data");

	private ComboBox<String> memoryTypeComboBox = new ComboBox<>();
	private ComboBox<String> rwTypeComboBox = new ComboBox<>();
	private TextField startAddressTextField = new TextField();
	private TextField endAddressTextField = new TextField();
	private TextField ipDataTextField = new TextField();

	private VBox memoryTestButtonBox = new VBox();
	private Button memoryTestRunButton = new Button("Run");

	private RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
	private AdvanceCustom1TestingManagement advanceCustom1TestingManagement = new AdvanceCustom1TestingManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();


	private String selectedTestFilePath;
	private String selectedDownloadCodeFilePath;
	private String selectedCheckSumFilePath;

	private String UUT_ID;
	private String TEST_TYPE_ID;

	public GridPane createAdvancedTestingTab4GridPane() {
		UUT_ID = StateMachine.currentSessionDetails.getUutId();
		enableOrDisable(true);
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(24);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(65);

		tab4MainGridPane.setVgap(5);
		tab4MainGridPane.setHgap(5);
		tab4MainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		tab4MainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		tab4MainGridPane.add(createTestTypeComboBox(), 0, 0, 2, 1);
		tab4MainGridPane.add(createSelectTestFileBox(), 0, 1);
		tab4MainGridPane.add(createDownloadBox(), 0, 2);
		tab4MainGridPane.add(createMemoryTestBox(), 1, 1, 1, 2);

		return tab4MainGridPane;
	}

	private HBox createTestTypeComboBox() {
		testTypeHBox.getStyleClass().add("advanced-testing-custom-tab-container");
		testTypeComboBox.setPromptText("TEST TYPE");

		testTypeHBox.setAlignment(Pos.TOP_RIGHT);
		testTypeHBox.getChildren().add(testTypeComboBox);
		initializeTestTypeComboBox();
		return testTypeHBox;
	}

	private void initializeTestTypeComboBox() {
		testTypeDataList = FXCollections.observableArrayList(
				runConfigurationManagement.getTestTypeByUUTId(StateMachine.currentSessionDetails.getUutId()));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
		}
		testTypeComboBox.setItems(testTypeList);

		testTypeComboBox.setOnAction(e -> {
			TEST_TYPE_ID = fetchTestTypeId(testTypeComboBox.getValue());
			enableOrDisable(false);
		});
	}

	private String fetchTestTypeId(String testTypeName) {
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestName().equals(testTypeName)) {
				return testType.getTestTypeId();
			}
		}
		return null;
	}

	private VBox createSelectTestFileBox() {
		selectTestFileVBox.getStyleClass().add("advanced-testing-custom-tab-container");
		selectTestFileVBox.setAlignment(Pos.CENTER);

		selectTestFileLabel.getStyleClass().add("form-label");
		addTestFileLabel.getStyleClass().add("form-label-add-button");
		selectedTestFileName.getStyleClass().add("form-label-selected-text");

		
		selectTestFileLabel.setPrefWidth(150);
		selectedTestFileName.setPrefWidth(480);
		selectedTestFileName.setWrapText(true);

		selectTestFileHBox.getChildren().addAll(selectTestFileLabel, addTestFileLabel);
		selectedTestFileHBox.getChildren().addAll(selectedTestFileName, selectTestFileRunButton);
		selectTestFileHBox.setAlignment(Pos.CENTER_LEFT);
		selectedTestFileHBox.setAlignment(Pos.CENTER_LEFT);

		selectTestFileVBox.getChildren().addAll(selectTestFileHBox, selectedTestFileHBox);

		addTestFileLabel.setOnMouseClicked(e -> {
			uploadFile("selectTestFile");
		});

		selectTestFileRunButton.setOnAction(e -> {
			handleRunTestFile(true);
		});

		return selectTestFileVBox;
	}

	private VBox createDownloadBox() {
		downloadCodeVBox.getStyleClass().add("advanced-testing-custom-tab-container");
		downloadCodeVBox.setAlignment(Pos.CENTER);

//		Download Code
		downloadCodeLabel.getStyleClass().add("form-label");
		addDownloadCodeLabel.getStyleClass().add("form-label-add-button");
		selectedDownloadCodeName.getStyleClass().add("form-label-selected-text");

		downloadCodeLabel.setPrefWidth(150);
		selectedDownloadCodeName.setPrefWidth(480);
		selectedDownloadCodeName.setWrapText(true);

		downloadCodeHBox.getChildren().addAll(downloadCodeLabel, addDownloadCodeLabel);
		selectedDownloadCodeHBox.getChildren().add(selectedDownloadCodeName);
		downloadCodeHBox.setAlignment(Pos.CENTER_LEFT);
		selectedDownloadCodeHBox.setAlignment(Pos.CENTER_LEFT);

//		Base File
//		baseFileLabel.getStyleClass().add("form-label");
//		addBaseFileLabel.getStyleClass().add("form-label-add-button");
//		selectedBaseFileName.getStyleClass().add("form-label-selected-text");
//
//		baseFileLabel.setPrefWidth(150);
//		selectedBaseFileName.setPrefWidth(480);
//		selectedBaseFileName.setWrapText(true);
//
//		baseFileHBox.getChildren().addAll(baseFileLabel, addBaseFileLabel);
//		selectedBaseFileHBox.getChildren().add(selectedBaseFileName);
//		baseFileHBox.setAlignment(Pos.CENTER_LEFT);
//		selectedBaseFileHBox.setAlignment(Pos.CENTER_LEFT);
		
//		End File
		checkSumFileLabel.getStyleClass().add("form-label");
		addCheckSumFileLabel.getStyleClass().add("form-label-add-button");
		selectedCheckSumFileName.getStyleClass().add("form-label-selected-text");

		checkSumFileLabel.setPrefWidth(150);
		selectedCheckSumFileName.setPrefWidth(480);
		selectedCheckSumFileName.setWrapText(true);

		checkSumFileHBox.getChildren().addAll(checkSumFileLabel, addCheckSumFileLabel);
		selectedCheckSumFileHBox.getChildren().addAll(selectedCheckSumFileName,downloadCodeRunButton);
		checkSumFileHBox.setAlignment(Pos.CENTER_LEFT);
		selectedCheckSumFileHBox.setAlignment(Pos.CENTER_LEFT);

//		downloadCodeVBox.getChildren().addAll(baseFileHBox, selectedBaseFileHBox, downloadCodeHBox,
//				selectedDownloadCodeHBox, endFileHBox, selectedEndFileHBox);
		downloadCodeVBox.getChildren().addAll(downloadCodeHBox,
				selectedDownloadCodeHBox, checkSumFileHBox, selectedCheckSumFileHBox);

		addDownloadCodeLabel.setOnMouseClicked(e -> {
			uploadFile("downloadCode");
		});
		
		addCheckSumFileLabel.setOnMouseClicked(e -> {
			uploadFile("checksumFile");
		});

		downloadCodeRunButton.setOnAction(e -> {
			handleRunTestFile(false);
		});
		

		AdvancedTestStateObject.customTest2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				StateMachine.setTestState(TestState.COMPLETED);
				AdvancedTestStateObject.customTest2StatusProperty().set(true);
			}

		});

		return downloadCodeVBox;
	}

	private GridPane createMemoryTestBox() {
		memoryTestGridPane.getStyleClass().add("advanced-testing-custom-tab-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(40);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);

		memoryTestGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);

		memoryTestGridPane.getRowConstraints().add(firstRow);

		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPercentHeight(17);
		for (int i = 0; i < 5; i++) {
			memoryTestGridPane.getRowConstraints().add(rowConstraints);
		}

		memoryTestLabel.getStyleClass().add("title-label");

		memoryTestGridPane.add(memoryTestLabel, 0, 0, 2, 1);

		memoryTypeLabel.getStyleClass().add("form-label");
		rwTypeLabel.getStyleClass().add("form-label");
		startAddressLabel.getStyleClass().add("form-label");
		endAddressLabel.getStyleClass().add("form-label");
		ipDataLabel.getStyleClass().add("form-label");

		memoryTestGridPane.add(memoryTypeLabel, 0, 1);
		memoryTestGridPane.add(rwTypeLabel, 0, 2);
		memoryTestGridPane.add(startAddressLabel, 0, 3);
		memoryTestGridPane.add(endAddressLabel, 0, 4);
		memoryTestGridPane.add(ipDataLabel, 0, 5);

		startAddressTextField.getStyleClass().add("form-textfield");
		endAddressTextField.getStyleClass().add("form-textfield");
		ipDataTextField.getStyleClass().add("form-textfield");

		memoryTestGridPane.add(memoryTypeComboBox, 1, 1);
		memoryTestGridPane.add(rwTypeComboBox, 1, 2);
		memoryTypeComboBox.setMaxWidth(Double.MAX_VALUE);
		rwTypeComboBox.setMaxWidth(Double.MAX_VALUE);

		memoryTestGridPane.add(startAddressTextField, 1, 3);
		memoryTestGridPane.add(endAddressTextField, 1, 4);
		memoryTestGridPane.add(ipDataTextField, 1, 5);

		memoryTestGridPane.add(createMemoryTestButtonBox(), 2, 1, 1, 5);

		return memoryTestGridPane;
	}

	private VBox createMemoryTestButtonBox() {
		memoryTestButtonBox.setAlignment(Pos.CENTER);
		memoryTestButtonBox.getChildren().add(memoryTestRunButton);
		return memoryTestButtonBox;
	}

	private void uploadFile(String type) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		if (type.equalsIgnoreCase("selectTestFile")) {			
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Test File", "*.tst","*.tpf","*.com"));
		}else if (type.equalsIgnoreCase("downloadCode")) {
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Download Code", "*.run","*.chk"));			
		}else if (type.equalsIgnoreCase("checksumFile")) {
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CheckSum File", "*.run","*.chk"));						
		}
		File selectedFile = fileChooser.showOpenDialog(tab4MainGridPane.getScene().getWindow());
		if (selectedFile != null) {
			String filePath = selectedFile.getAbsolutePath();
			String fileName = selectedFile.getName();
			if (type.equalsIgnoreCase("selectTestFile")) {
				selectedTestFileName.setText(fileName);
				selectedTestFilePath = filePath;
			} else if (type.equalsIgnoreCase("downloadCode")) {
				selectedDownloadCodeName.setText(fileName);
				selectedDownloadCodeFilePath = filePath;
			} else if (type.equalsIgnoreCase("checksumFile")) {
				selectedCheckSumFileName.setText(fileName);
				selectedCheckSumFilePath = filePath;
			}
		}
	}

	private void enableOrDisable(boolean status) {
		addTestFileLabel.setDisable(status);
//		addBaseFileLabel.setDisable(status);
		addDownloadCodeLabel.setDisable(status);
		addCheckSumFileLabel.setDisable(status);
		selectTestFileRunButton.setDisable(status);
		downloadCodeRunButton.setDisable(status);
		memoryTypeComboBox.setDisable(status);
		rwTypeComboBox.setDisable(status);
		startAddressTextField.setDisable(status);
		endAddressTextField.setDisable(status);
		ipDataTextField.setDisable(status);
		memoryTestRunButton.setDisable(status);
	}

	private void handleRunTestFile(boolean isTestFile) {
		if (isTestFile) {
			if (selectedTestFilePath != null) {
				String stageId = AdvancedTestStateObject.getCustomTest2UserDefinedTestId();
//				System.out.println("TEST FILE");
//				System.out.println(UUT_ID + "  " + TEST_TYPE_ID + "  " + selectedTestFilePath + " " + stageId);
				if(checkAndSetTestState()) {					
					Response response = advanceCustom1TestingManagement.customTwoRunTestFile(stageId, selectedTestFilePath, TEST_TYPE_ID);
				}
			} else {
				Notifications.showWarningAlert("Select a test file to run.");
			}
		} else {
			if (selectedDownloadCodeFilePath != null) {
				if(selectedCheckSumFilePath != null) {					
					String stageId = AdvancedTestStateObject.getCustomTest2DownloadCodeTestId();
//					System.out.println("DOWNLOAD CODE");
//					System.out.println(UUT_ID + "  " + TEST_TYPE_ID + "  " + selectedDownloadCodeFilePath + " " + stageId);
//					System.out.println("CheckSum File");
//					System.out.println(UUT_ID + "  " + TEST_TYPE_ID + "  " + selectedCheckSumFilePath + " " + stageId);
					if(checkAndSetTestState()) {
						Response response = advanceCustom1TestingManagement.customTwoRunDownloadFile(stageId, selectedDownloadCodeFilePath, selectedCheckSumFilePath, TEST_TYPE_ID);
					}
				}else {
					Notifications.showWarningAlert("Select a checksum file to run.");
				}
			} else {
				Notifications.showWarningAlert("Select a download code file to run.");
			}
		}
	}
	
	
	private boolean checkAndSetTestState() {
		if (!checkAitessStatus.isBothAitessOn()) {
			return false;
		}

		TestState currentState = StateMachine.getTestState();

		if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
				|| currentState == TestState.STOPPED) {
			StateMachine.setTestState(TestState.RUNNING);
			StateMachine.setRunningTestName(RunningTestName.ADVANCED_TEST);
		} else if (currentState == TestState.RUNNING) {
			Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
			return false;
		} else if (currentState == TestState.PAUSED) {
			Notifications
					.showWarningAlert(StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
			return false;
		}

		return true;
	}
}
