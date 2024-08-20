package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.customtestmanagement.AdvanceCustom1TestingManagement;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.MacroListResponse;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.SymbolListResponse;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AdvancedTestingCustomTesting1 {

	private GridPane tab3MainGridPane = new GridPane();
	private HBox testTypeHBox = new HBox(10);
	private ComboBox<String> testTypeComboBox = new ComboBox<>();
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	private HBox leftTitleHBox = new HBox(20);
	private CheckBox ch1CheckBox = new CheckBox("CH-1");
	private CheckBox ch2CheckBox = new CheckBox("CH-2");
	private CheckBox ch3CheckBox = new CheckBox("CH-3");
	private CheckBox ch4CheckBox = new CheckBox("CH-4");
	
	private GridPane terminalCommandGridPane = new GridPane();
	private Label symbolLabel = new Label("Symbol");
	private Label typeLabel = new Label("Type");
	private Label minValueLabel = new Label("Min Value");
	private Label maxValueLabel = new Label("Max Value");
	private Label ipDataLabel = new Label("Input Data");

	private ComboBox<String> symbolComboBox = new ComboBox<String>();
	private TextField typeTextField = new TextField();
	private TextField minValueTextField = new TextField();
	private TextField maxValueTextField = new TextField();
	private TextField ipDataTextField = new TextField();

	private ObservableList<SymbolDto> symbolDataList;
	private ObservableList<String> symbolList = FXCollections.observableArrayList();
	private ObservableList<MacroDto> macroDataList;
	private ObservableList<String> macroList = FXCollections.observableArrayList();

	private VBox terminalButtonVBox = new VBox(10);
	private Button terminalAddButton = new Button("Add");
	private Button terminalRunButton = new Button("Run");

	private GridPane macroGridPane = new GridPane();
	private Label macroLabel = new Label("Macro");
	private ComboBox<String> macroComboBox = new ComboBox<String>();

	private VBox macroButtonVBox = new VBox(10);
	private Button macroAddButton = new Button("Add");
	private Button macroRunButton = new Button("Run");

	private GridPane rightTopGridPane = new GridPane();
	private HBox rightTitleHBox = new HBox();
	private Label rightLabel = new Label("User Test");

	private GridPane userTestGridPane = new GridPane();
	private TextArea userTestTextArea = new TextArea();
	private HBox userTestHBox = new HBox(15);
	private Button userTestRunButton = new Button("Run");

	private Label testNameLabel = new Label("Test Name");
	private TextField testNameTextField = new TextField();

	private RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
	private AdvanceCustom1TestingManagement advanceCustom1TestingManagement = new AdvanceCustom1TestingManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	
	private String UUT_ID;
	private String TEST_TYPE_ID;

	public GridPane createAdvancedTestingTab3GridPane() {
		UUT_ID = StateMachine.currentSessionDetails.getUutId();
		enableOrDisable(true);
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(30);

		tab3MainGridPane.setVgap(5);
		tab3MainGridPane.setHgap(5);
		tab3MainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		tab3MainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		tab3MainGridPane.add(createLeftSideTitle(), 0, 0, 1, 2);
		tab3MainGridPane.add(createLeftTerminalBox(), 0, 1);
		tab3MainGridPane.add(createLeftMacroBox(), 0, 2);

		tab3MainGridPane.add(createRightSideTop(), 1, 0, 1, 3);
		tab3MainGridPane.add(createRightTextArea(), 1, 1, 1, 2);
		return tab3MainGridPane;
	}

	private HBox createLeftSideTitle() {
		leftTitleHBox.getStyleClass().add("advanced-testing-custom-tab-container");
		leftTitleHBox.setAlignment(Pos.TOP_LEFT);

		ch1CheckBox.getStyleClass().add("custom-checkbox");
		ch2CheckBox.getStyleClass().add("custom-checkbox");
		ch3CheckBox.getStyleClass().add("custom-checkbox");
		ch4CheckBox.getStyleClass().add("custom-checkbox");

		leftTitleHBox.getChildren().addAll(ch1CheckBox, ch2CheckBox, ch3CheckBox, ch4CheckBox);

		return leftTitleHBox;
	}

	private GridPane createLeftTerminalBox() {
		terminalCommandGridPane.getStyleClass().add("advanced-testing-custom-tab-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(40);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);
		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPercentHeight(20);
		terminalCommandGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		for (int i = 0; i < 5; i++) {
			terminalCommandGridPane.getRowConstraints().add(rowConstraints);
		}

		symbolLabel.getStyleClass().add("form-label");
		typeLabel.getStyleClass().add("form-label");
		minValueLabel.getStyleClass().add("form-label");
		maxValueLabel.getStyleClass().add("form-label");
		ipDataLabel.getStyleClass().add("form-label");

		terminalCommandGridPane.add(symbolLabel, 0, 0);
		terminalCommandGridPane.add(typeLabel, 0, 1);
		terminalCommandGridPane.add(minValueLabel, 0, 2);
		terminalCommandGridPane.add(maxValueLabel, 0, 3);
		terminalCommandGridPane.add(ipDataLabel, 0, 4);

		symbolComboBox.getStyleClass().add("form-textfield");
		typeTextField.getStyleClass().add("form-textfield");
		minValueTextField.getStyleClass().add("form-textfield");
		maxValueTextField.getStyleClass().add("form-textfield");
		ipDataTextField.getStyleClass().add("form-textfield");

		terminalCommandGridPane.add(symbolComboBox, 1, 0);
		terminalCommandGridPane.add(typeTextField, 1, 1);
		terminalCommandGridPane.add(minValueTextField, 1, 2);
		terminalCommandGridPane.add(maxValueTextField, 1, 3);
		terminalCommandGridPane.add(ipDataTextField, 1, 4);

		symbolComboBox.prefWidthProperty().bind(terminalCommandGridPane.widthProperty());
		symbolComboBox.setItems(symbolList);
		symbolComboBox.setEditable(true);
		addSearchFunctionality(symbolComboBox, symbolList);

		symbolComboBox.setOnAction(e -> {
			handleSymbolSection(symbolComboBox.getValue());
		});

		terminalCommandGridPane.add(createTerminalButtonBox(), 2, 0, 1, 5);

		return terminalCommandGridPane;
	}

	private void handleSymbolSection(String symbolName) {
		if (symbolName != null && symbolDataList != null) {
			for (SymbolDto symbol : symbolDataList) {
				if (symbolName.equals(symbol.getSymbolName())) {
					typeTextField.setText(symbol.getSymbolType());
					minValueTextField.setText(symbol.getMin());
					maxValueTextField.setText(symbol.getMax());
				}
			}
		}
	}

	private void addSearchFunctionality(ComboBox<String> comboBox, ObservableList<String> items) {
		TextField editor = comboBox.getEditor();
		comboBox.setOnKeyReleased(event -> {
			String filter = editor.getText();
			if (filter.isEmpty()) {
				comboBox.setItems(items);
			} else {
				ObservableList<String> filteredItems = FXCollections.observableArrayList();
				for (String item : items) {
					if (item.toLowerCase().contains(filter.toLowerCase())) {
						filteredItems.add(item);
					}
				}
				comboBox.setItems(filteredItems);
				comboBox.show();
			}
		});

		comboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				editor.setText(newValue);
				comboBox.setItems(items);
			}
		});
	}

	private VBox createTerminalButtonBox() {
		terminalButtonVBox.setAlignment(Pos.CENTER);
		terminalButtonVBox.getChildren().addAll(terminalAddButton, terminalRunButton);

		terminalAddButton.setOnAction(e -> {
			handleSymbolAddOrRun(false);
		});

		terminalRunButton.setOnAction(e -> {
			handleSymbolAddOrRun(true);
		});
		
		AdvancedTestStateObject.customTest1StatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				StateMachine.setTestState(TestState.COMPLETED);
				AdvancedTestStateObject.customTest1StatusProperty().set(true);
			}
		
		});
		
		return terminalButtonVBox;
	}

	private GridPane createLeftMacroBox() {
		macroGridPane.getStyleClass().add("advanced-testing-custom-tab-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(40);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		macroGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		macroGridPane.getRowConstraints().add(firstRow);

		macroLabel.getStyleClass().add("form-label");
		macroComboBox.getStyleClass().add("form-textfield");

		macroGridPane.add(macroLabel, 0, 0);
		macroGridPane.add(macroComboBox, 1, 0);
		macroGridPane.add(createMacroButton(), 2, 0);

		macroComboBox.prefWidthProperty().bind(macroGridPane.widthProperty());
		macroComboBox.setItems(macroList);
		macroComboBox.setEditable(true);

		addSearchFunctionality(macroComboBox, macroList);

		return macroGridPane;
	}

	private VBox createMacroButton() {
		macroButtonVBox.setAlignment(Pos.CENTER);
		macroButtonVBox.getChildren().addAll(macroAddButton, macroRunButton);

		macroAddButton.setOnAction(e -> {
			handleMacroAddOrRun(false);
		});

		macroRunButton.setOnAction(e -> {
			handleMacroAddOrRun(true);
		});

		return macroButtonVBox;
	}

	private GridPane createRightSideTop() {
		rightTopGridPane.getStyleClass().add("advanced-testing-custom-tab-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		rightTopGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		rightTopGridPane.getRowConstraints().addAll(firstRow);

		rightTopGridPane.add(createRightSideTitle(), 0, 0);
		rightTopGridPane.add(createTestTypeComboBox(), 1, 0);
		return rightTopGridPane;
	}

	private HBox createRightSideTitle() {
		rightLabel.getStyleClass().add("title-label");
		rightTitleHBox.setAlignment(Pos.TOP_LEFT);
		rightTitleHBox.getChildren().add(rightLabel);
		return rightTitleHBox;
	}

	private HBox createTestTypeComboBox() {
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
			getDataByUUTandTestId();
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

	private GridPane createRightTextArea() {
		userTestGridPane.getStyleClass().add("advanced-testing-custom-tab-container");
		userTestTextArea.getStyleClass().add("user-test-textarea");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(85);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(15);
		userTestGridPane.getColumnConstraints().addAll(firstColumn);
		userTestGridPane.getRowConstraints().addAll(firstRow, secondRow);

		userTestGridPane.add(userTestTextArea, 0, 0);
		userTestGridPane.add(createUserTestButtonBox(), 0, 1);

		return userTestGridPane;
	}

	private HBox createUserTestButtonBox() {
		userTestHBox.setAlignment(Pos.CENTER);
		testNameLabel.getStyleClass().add("form-label");
		testNameTextField.getStyleClass().add("form-textfield");
		userTestHBox.getChildren().addAll(testNameLabel, testNameTextField, userTestRunButton);

		userTestRunButton.setOnAction(e -> {
			handleUserTestRun();
		});

		return userTestHBox;
	}

	private void getDataByUUTandTestId() {

		clearFiledValues();

		SymbolListResponse symbolResponse = advanceCustom1TestingManagement.getAllSymbolsForAdavanceTest(UUT_ID,
				TEST_TYPE_ID);
		if (symbolResponse.getResponse().getResponseCode() == 1) {
			symbolDataList = FXCollections.observableArrayList(symbolResponse.getListOfSymbolDto());
			for (SymbolDto symbol : symbolDataList) {
				symbolList.add(symbol.getSymbolName());
			}
		} else if (symbolResponse.getResponse().getResponseCode() == 0) {
			Notifications.showErrorAlert(symbolResponse.getResponse().getResponseMessage());
		}

		MacroListResponse macroResponse = advanceCustom1TestingManagement.getAllMacrosForAdavanceTest(UUT_ID,
				TEST_TYPE_ID);
		if (macroResponse.getResponse().getResponseCode() == 1) {
			macroDataList = FXCollections.observableArrayList(macroResponse.getListOfMacroDto());
			for (MacroDto macro : macroDataList) {
				macroList.add(macro.getMacroName());
			}
		} else if (macroResponse.getResponse().getResponseCode() == 0) {
			Notifications.showErrorAlert(macroResponse.getResponse().getResponseMessage());
		}

	}

	private void clearFiledValues() {
		symbolList.clear();
		macroList.clear();
		symbolComboBox.setValue(null);
		macroComboBox.setValue(null);
		typeTextField.setText(null);
		minValueTextField.setText(null);
		maxValueTextField.setText(null);
		ipDataTextField.setText(null);
		userTestTextArea.clear();
	}

	private void enableOrDisable(boolean status) {
		symbolComboBox.setDisable(status);
		macroComboBox.setDisable(status);
		typeTextField.setDisable(status);
		minValueTextField.setDisable(status);
		maxValueTextField.setDisable(status);
		ipDataTextField.setDisable(status);
		userTestTextArea.setDisable(status);
		testNameTextField.setDisable(status);
		terminalAddButton.setDisable(status);
		terminalRunButton.setDisable(status);
		macroAddButton.setDisable(status);
		macroRunButton.setDisable(status);
		userTestRunButton.setDisable(status);
	}

	private void handleSymbolAddOrRun(boolean runStatus) {
		StringBuilder errorMessage = new StringBuilder();
		String symbolName = symbolComboBox.getValue();
		String symbolType = typeTextField.getText();
		String minValue = minValueTextField.getText();
		String maxValue = maxValueTextField.getText();
		String inputData = ipDataTextField.getText();
		String ch1Status = ch1CheckBox.isSelected() ? "1" : "0" ; 
		String ch2Status = ch2CheckBox.isSelected() ? "1" : "0" ; 
		String ch3Status = ch3CheckBox.isSelected() ? "1" : "0" ; 
		String ch4Status = ch4CheckBox.isSelected() ? "1" : "0" ; 

		if (symbolName == null || symbolName.isEmpty())
			errorMessage.append("Please select or add any symbol.\n");
		if (symbolType == null || symbolType.isEmpty())
			errorMessage.append("Symbol type is empty.\n");
		if (minValue == null || minValue.isEmpty())
			errorMessage.append("Minimum value is empty.\n");
		if (maxValue == null || maxValue.isEmpty())
			errorMessage.append("Maximum value is empty.\n");
		if (inputData == null || inputData.isEmpty())
			errorMessage.append("Input data is empty.\n");

		if (errorMessage.length() > 0) {
			Notifications.showErrorAlert(errorMessage.toString());
			return;
		}

		String formattedData = symbolName+"("+ch1Status+ch2Status+ch3Status+ch4Status+") = "+inputData+";";

		if (runStatus) {
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.ADVANCED_TEST);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				return;
			}
			
			String response = advanceCustom1TestingManagement.customOneRun(formattedData, TEST_TYPE_ID);
			
		} else {
			userTestTextArea.appendText(formattedData + "\n");
		}
	}

	private void handleMacroAddOrRun(boolean runStatus) {
		StringBuilder errorMessage = new StringBuilder();
		String macroName = macroComboBox.getValue();
		if (macroName == null || macroName.isEmpty())
			errorMessage.append("Please select or add any macro.\n");

		if (errorMessage.length() > 0) {
			Notifications.showErrorAlert(errorMessage.toString());
			return;
		}
		String formattedData = "macn = "+macroName+";";
		if (runStatus) {
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.ADVANCED_TEST);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				return;
			}

			String response = advanceCustom1TestingManagement.customOneRun(formattedData, TEST_TYPE_ID);

		} else {
			userTestTextArea.appendText(formattedData + "\n");
		}
	}

	private void handleUserTestRun() {
		String stageId = AdvancedTestStateObject.getCustomTest1Id();
		
		StringBuilder errorMessage = new StringBuilder();
		String testName = testNameTextField.getText();
		if (testName == null || testName.isEmpty())
			errorMessage.append("Please select test file name.\n");

		if (errorMessage.length() > 0) {
			Notifications.showErrorAlert(errorMessage.toString());
			return;
		}

		List<String> testFileData = new ArrayList<String>();
		String text = userTestTextArea.getText();
		String[] lines = text.split("\\r?\\n");

		for (String line : lines) {
			testFileData.add(line);
		}
		
		
		if (!checkAitessStatus.isBothAitessOn()) {
			return;
		}
		TestState currentState = StateMachine.getTestState();
		if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
				|| currentState == TestState.STOPPED) {
			StateMachine.setTestState(TestState.RUNNING);
			StateMachine.setRunningTestName(RunningTestName.ADVANCED_TEST);
		} else if (currentState == TestState.RUNNING) {
			Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
			return;
		} else if (currentState == TestState.PAUSED) {
			Notifications.showWarningAlert(
					StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
			return;
		}
		
		Response response = advanceCustom1TestingManagement.customOneRunTestFile(stageId, testName, testFileData, TEST_TYPE_ID);
		
	}
}
