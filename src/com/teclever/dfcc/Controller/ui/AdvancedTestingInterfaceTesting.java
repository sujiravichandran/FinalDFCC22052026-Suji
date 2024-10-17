package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AdvancedTestingInterfaceTesting {

	private GridPane tab2MainGridPane = new GridPane();
	private VBox leftSideVBox = new VBox(20);
	private GridPane rightSideGridPane = new GridPane();

	private List<RadioButton> stageListRadioButtons = new ArrayList<>();
	private ListView<RadioButton> stageListView = new ListView<>();

	private List<CheckBox> checkBoxes = new ArrayList<>();
	private ListView<CheckBox> testListView = new ListView<>();
	private VBox testListVBox = new VBox();
	private HBox buttonHBox = new HBox(5);
	private Button startButton = new Button("Start");
	private Button runAllButton = new Button("Run All");
	private Button stopButton = new Button("Stop");
	private Button pauseButton = new Button("Pause");

	private VBox repeatCountVBox = new VBox();
	private Label repeatCountLabel = new Label();
	private TextField repeatCountTextField = new TextField();

	private String selectedStageId = null;
	private String selectedTestTypeId = null;
	
	private VBox buttonMainVBox = new VBox(15);
	private HBox allButtonHBox = new HBox(15);
	private HBox progressBarHBox = new HBox(5);
	private ProgressBar testProgressBar = new ProgressBar();
	private Label percentageLabel = new Label("0%");

	private RunConfigurationService runConfigurationService = new RunConfigurationService();
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private TestProcessManagement testProcessManagement = new TestProcessManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();

	public GridPane createAdvancedTestingTab2GridPane() {
		getInterfaceTestingStagesData();
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		tab2MainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		tab2MainGridPane.getRowConstraints().addAll(firstRow);

		tab2MainGridPane.setHgap(5);

		tab2MainGridPane.add(createLeftSide(), 0, 0);
		tab2MainGridPane.add(createRightSide(), 1, 0);

		return tab2MainGridPane;
	}

	private void getInterfaceTestingStagesData() {
		ObservableList<StageObject> observableStageList = FXCollections
				.observableArrayList(StateMachine.getStageDatalist());
		observableStageList.stream()
				.filter(stage -> "Advanced Test".equalsIgnoreCase(stage.getL1StageName())
						&& "Interface Test".equalsIgnoreCase(stage.getL2StageName()))
				.filter(stage -> stage.getL3StageId() != null && stage.getL4StageId() != null)
				.forEach(stage -> {
					TestCardData newCard = new TestCardData(stage.getL4StageId(), stage.getL4StageName(),
							stage.getTestTypeId(), null);
					AdvancedTestStateObject.addInterfaceTestList(newCard);
				});
	}

	private VBox createLeftSide() {
		ObservableList<TestCardData> interfaceTestList = AdvancedTestStateObject.getInterfaceTestList();

		leftSideVBox.getStyleClass().add("advanced-testing-left-container");
		stageListView.getStyleClass().add("advanced-testing-radio-list-view");

		ToggleGroup toggleGroup = new ToggleGroup();

		for (TestCardData test : interfaceTestList) {
			RadioButton newRadioButton = new RadioButton(test.getCardName());
			newRadioButton.setId(test.getCardId());
			newRadioButton.setUserData(test);
			newRadioButton.getStyleClass().add("advanced-testing-radio-button");
			newRadioButton.setWrapText(true);
			newRadioButton.setToggleGroup(toggleGroup);
			stageListRadioButtons.add(newRadioButton);
			stageListView.getItems().add(newRadioButton);
		}

		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				RadioButton selectedRadioButton = (RadioButton) newValue;
				TestCardData selectedData = (TestCardData) selectedRadioButton.getUserData();

				getTestListByStageId(selectedData.getCardId(), selectedData.getTestTypeId());
				selectedStageId = selectedData.getCardId();
				selectedTestTypeId = selectedData.getTestTypeId();
			}
		});

		leftSideVBox.setAlignment(Pos.CENTER);
		leftSideVBox.getChildren().addAll(stageListView);
		return leftSideVBox;
	}

	private void getTestListByStageId(String stageId, String testTypeId) {
		String runConfigId = runConfigurationService
				.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
		currentSessionDetails.setRunConfigId(runConfigId);
		String ID = stageId;
		TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
		if (testFileResponse.getTestFilesIdName() == null) {
			testListView.getItems().clear();
			Platform.runLater(() -> {
				Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
			});
			return;
		}

		ObservableMap<String, String> testFileMap = FXCollections.observableMap(testFileResponse.getTestFilesIdName());
		setTestListViewData(testFileMap);
	}

	private void setTestListViewData(ObservableMap<String, String> testFileMap) {
		testListView.getItems().clear();
		checkBoxes.clear();

		for (Map.Entry<String, String> entry : testFileMap.entrySet()) {
			String filePath = entry.getValue();
			File file = new File(filePath);
			CheckBox newCheckBox = new CheckBox(file.getName());
			newCheckBox.setId(entry.getKey());
			newCheckBox.getStyleClass().add("advanced-testing-checkbox");
			newCheckBox.setWrapText(true);
			checkBoxes.add(newCheckBox);
			testListView.getItems().add(newCheckBox);

			newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				boolean anySelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);
				startButton.setDisable(!anySelected);
			});
		}
	}

	private GridPane createRightSide() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(77);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(23);

		rightSideGridPane.getColumnConstraints().addAll(firstColumn);
		rightSideGridPane.getRowConstraints().addAll(firstRow, secondRow);
		rightSideGridPane.setVgap(5);
		rightSideGridPane.add(createTestListView(), 0, 0);
		rightSideGridPane.add(createButtonBox(), 0, 1);
		return rightSideGridPane;
	}

	private VBox createTestListView() {
		testListView.getStyleClass().add("advanced-testing-list-view");


		testListVBox.getChildren().add(testListView);
		testListVBox.getStyleClass().add("advanced-testing-right-container");
		return testListVBox;
	}

	private HBox createButtonBox() {
		buttonHBox.getStyleClass().add("advanced-testing-right-container");
		Image playImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/play.png"));
		Image stopImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/stop.png"));
		Image pauseImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/pause.png"));

		ImageView playImageView = new ImageView(playImage);
		playImageView.getStyleClass().add("button-image");
		playImageView.setFitHeight(25);
		playImageView.setFitWidth(25);
		playImageView.setPreserveRatio(true);
		playImageView.setSmooth(true);

		ImageView stopImageView = new ImageView(stopImage);
		stopImageView.getStyleClass().add("button-image");
		stopImageView.setFitHeight(25);
		stopImageView.setFitWidth(25);
		stopImageView.setPreserveRatio(true);
		stopImageView.setSmooth(true);

		ImageView pauseImageView = new ImageView(pauseImage);
		pauseImageView.getStyleClass().add("button-image");
		pauseImageView.setFitHeight(25);
		pauseImageView.setFitWidth(25);
		pauseImageView.setPreserveRatio(true);
		pauseImageView.setSmooth(true);

		startButton.setGraphic(playImageView);
		startButton.setGraphicTextGap(10);
		stopButton.setGraphic(stopImageView);
		stopButton.setGraphicTextGap(10);
		pauseButton.setGraphic(pauseImageView);
		pauseButton.setGraphicTextGap(10);

		startButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);

		runAllButton.setOnAction(e -> {
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
					currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
					currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
					"clicked on Run All in Interface Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			List<String> testFileIds = new ArrayList<>();
			for (CheckBox checkbox : checkBoxes) {
				if (!checkbox.isDisable()) {
					testFileIds.add(checkbox.getId());
				}
			}
			if (testFileIds.size() == 0) {
				Notifications.showWarningAlert("Please Select Test File...");
				return;
			}

			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {
				startButton.setDisable(true);
				runAllButton.setDisable(true);
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.SESSION_TEST);
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}

			callStartTest(selectedStageId, "INTERFACE TEST", selectedTestTypeId, testFileIds);
		});

		startButton.setOnAction(e -> {
			if (startButton.getText().equalsIgnoreCase("Resume")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Resume in Inteface Testing");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			}else {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Start in Interface Testing");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			}
			if (startButton.getText().equalsIgnoreCase("Resume")) {
				StateMachine.setTestState(TestState.RUNNING);
				startButton.setText("Start");
				startButton.setDisable(true);
				pauseButton.setDisable(false);
				stopButton.setDisable(false);
				return;
			}
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			List<String> testFileIds = new ArrayList<>();
			for (CheckBox checkbox : checkBoxes) {
				if (checkbox.isSelected()) {
					testFileIds.add(checkbox.getId());
				}
			}
			if (testFileIds.size() == 0) {
				Notifications.showWarningAlert("Please Select Test File...");
				return;
			}

			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {
				startButton.setDisable(true);
				runAllButton.setDisable(true);
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.SESSION_TEST);
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}

			callStartTest(selectedStageId, "INTERFACE TEST", selectedTestTypeId, testFileIds);
		});

		pauseButton.setOnAction(e -> {
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
					currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
					currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
					"clicked on Pause in Interface Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StateMachine.setTestState(TestState.PAUSED);
			startButton.setText("Resume");
			pauseButton.setDisable(true);
			startButton.setDisable(false);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e -> {
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
					currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
					currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
					"clicked on Stop in Interface Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StateMachine.setTestState(TestState.STOPPED);
			startButton.setText("Start");
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			startButton.setDisable(false);
			runAllButton.setDisable(false);
		});

		repeatCountLabel.setText("Repeat Count");
		repeatCountLabel.getStyleClass().add("advanced-testing-repeat-count-label");
		repeatCountTextField.getStyleClass().add("advanced-testing-repeat-count-input");
		repeatCountTextField.setText("1");
		repeatCountVBox.getStyleClass().add("repeat-count-vbox");
		repeatCountTextField.setAlignment(Pos.CENTER);

		repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				repeatCountTextField.setText(oldValue);
			} else if (newValue.length() > 3) {
				repeatCountTextField.setText(oldValue);
			}
		});

		repeatCountVBox.setAlignment(Pos.CENTER);
		repeatCountVBox.setPadding(new Insets(5));
		repeatCountVBox.getChildren().addAll(repeatCountLabel, repeatCountTextField);

		buttonHBox.setAlignment(Pos.CENTER);
		
		testProgressBar.setProgress(0);
		testProgressBar.getStyleClass().add("progress-bar");
		percentageLabel.getStyleClass().add("progress-label");

		progressBarHBox.setAlignment(Pos.CENTER);

		allButtonHBox.getChildren().addAll(runAllButton, startButton, pauseButton, stopButton);
		progressBarHBox.getChildren().addAll(testProgressBar, percentageLabel);
		buttonMainVBox.getChildren().addAll(allButtonHBox, progressBarHBox);

		buttonHBox.getChildren().addAll(repeatCountVBox, buttonMainVBox);
		
		AdvancedTestStateObject.interfaceTestStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				StateMachine.setTestState(TestState.COMPLETED);
				AdvancedTestStateObject.interfaceTestStatusProperty().set(true);
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
			}
		
		});
		
		AdvancedTestStateObject.runnedInterfaceestFileCountProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null ) {
				double percentage = (double) AdvancedTestStateObject.getRunnedInterfaceTestFileCount().get() / AdvancedTestStateObject.getTotalInterfaceSelectedTestFileCount();
				double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
				Platform.runLater(() -> {
					testProgressBar.setProgress(roundedPercentage);
					percentageLabel.setText((int) (roundedPercentage * 100) + "%");
				});
			}
		});
		
		return buttonHBox;
	}

	private void callStartTest(String stageId, String stageName, String testTypeId, List<String> testFileIds) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;
				int repeatCount = Integer.parseInt(repeatCountTextField.getText());

				int totalTestFileCount = testFileIds.size() * repeatCount;
				AdvancedTestStateObject.setTotalInterfaceSelectedTestFileCount(totalTestFileCount);
				Platform.runLater(()->{
					percentageLabel.setText("0%");
				});
				AdvancedTestStateObject.getRunnedInterfaceTestFileCount().set(0);
			

				Response response = testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID,
						repeatCount, testFileIds, true, stageName, testTypeId, null);

				return null;
			}
		};

		new Thread(task).start();
	}
}
