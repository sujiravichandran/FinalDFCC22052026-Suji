package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelStatusBeforeTestResponse;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.model.SessionDetails;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject.SessionTestResult;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class SessionTestingController {
	private GridPane sessionTestingMainGridPane = new GridPane();
	private GridPane sessionTestingHeadingGridPane = new GridPane();
	private GridPane sessionTestingTreeviewGridPane = new GridPane();
	private GridPane sessionTestingListMainGridPane = new GridPane();
	private GridPane sessionTestingResultsGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();

	private TreeView<Label> sessionTreeView = new TreeView<>();

	private List<CheckBox> checkBoxes = new ArrayList<>();
	private ListView<CheckBox> testListView = new ListView<>();
	private VBox testListVBox = new VBox();
	private HBox buttonHBox = new HBox(5);

	private Button startButton = new Button("Start");
	private Button stopButton = new Button("Stop");
	private Button pauseButton = new Button("Pause");
	private Button runAllButton = new Button("Run All");

	private VBox repeatCountVBox = new VBox();
	private Label repeatCountLabel = new Label();
	private TextField repeatCountTextField = new TextField();
	private Label repeatNotLabel = new Label();

	private ObservableMap<String, String> testFileMap;

	private RunConfigurationService runConfigurationService = new RunConfigurationService();
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private TestProcessManagement testProcessManagement = new TestProcessManagement();
	private SessionManagement sessionManagement = new SessionManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
//	private AitessProcessControlManagement aitessProcessControlManagement = new AitessProcessControlManagement();

	private TextField testNameField = new TextField();

	private VBox searchVBox = new VBox(5);

	private String selectedStageId = null;
	private String selectedTestTypeId = null;
	private boolean isTrailSession = false;

	private TableView<SessionTestResult> sessionTestTable = new TableView<>();

	private VBox buttonMainVBox = new VBox(15);
	private HBox allButtonHBox = new HBox(5);
	private HBox progressBarHBox = new HBox(5);
	private ProgressBar testProgressBar = new ProgressBar();
	private Label percentageLabel = new Label("0%");

	private double progress = 0.1;

	public SessionTestingController() {

		initializeSearch();

	}

	private void clearTextField() {
		if (!testNameField.getText().isEmpty()) {
			testNameField.clear();
		}
	}

	public void initializeSearch() {

		testNameField.setPromptText("Search...");

		testNameField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));
		testNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				clearTextField();
			}
		});

	}

	public GridPane createSessionTestingGridPane(boolean status) {
		if (status) {
			isTrailSession = true;
		}
//		getSessionTestData();
		setStateMachineCurrentL1StageId();
		sessionTestingMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());
		sessionTestingMainGridPane.getStyleClass().add("session-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);

		sessionTestingMainGridPane.setPadding(new Insets(5));
		sessionTestingMainGridPane.setVgap(5);
		sessionTestingMainGridPane.setHgap(5);
		sessionTestingMainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sessionTestingMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		sessionTestingMainGridPane.add(createHeadingBox(), 0, 0, 2, 1);
		sessionTestingMainGridPane.add(createSessionTestingLeftSide(), 0, 1);
		sessionTestingMainGridPane.add(createSessionTestingRightSide(), 1, 1);
		sessionTestingMainGridPane.add(createSessionTestingResultsGridPane(), 0, 2, 2, 1);

		return sessionTestingMainGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		sessionTestingHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		if (isTrailSession) {
			title.setText("TRIALS TESTING");
		} else {
			title.setText("SESSION TESTING");
		}

		title.getStyleClass().add("session-testing-title");
		titleBox.getChildren().add(title);

		sessionTestingHeadingGridPane.add(titleBox, 0, 0);

		return sessionTestingHeadingGridPane;
	}

	private GridPane createSessionTestingLeftSide() {
		sessionTestingTreeviewGridPane.getStyleClass().add("session-testing-left-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		sessionTestingTreeviewGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingTreeviewGridPane.getRowConstraints().addAll(firstRow);

		sessionTestingTreeviewGridPane.add(createTreeView(), 0, 0);

		getStatusForAllStage();

		return sessionTestingTreeviewGridPane;
	}

	private GridPane createSessionTestingRightSide() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(57);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);

		sessionTestingListMainGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingListMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		sessionTestingListMainGridPane.setHgap(5);
		sessionTestingListMainGridPane.setVgap(5);
		sessionTestingListMainGridPane.add(createSearchFile(), 0, 0);
		sessionTestingListMainGridPane.add(createTestListView(), 0, 1);
		sessionTestingListMainGridPane.add(createButtonBox(), 0, 2);

		return sessionTestingListMainGridPane;
	}

	private VBox createSearchFile() {
		searchVBox.getStyleClass().add("session-testing-right-text-field");
		searchVBox.getChildren().add(testNameField);
		return searchVBox;

	}

	private VBox createTestListView() {
		testListView.getStyleClass().add("session-testing-list-view");

		testListVBox.getChildren().addAll(testListView);
		testListVBox.getStyleClass().add("session-testing-right-container");
		return testListVBox;
	}

	List<String> testFiles = new ArrayList<>();

//	 public void useTestFiles() {
//	        testFiles = testProcessManagement.getTestFiles();
//	    } 

	int testFileSize;

	private void showAlert() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Input Required");
		alert.setHeaderText(null);
		alert.setContentText("Please enter a repeat count between 1 and 100.");
		alert.showAndWait();
	}

	private HBox createButtonBox() {

		buttonHBox.getStyleClass().add("session-testing-right-container");
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
			StateMachine.setConfirmTestStop(false);
			if (StateMachine.isConfirmTestFileCompleted()) {

				Notifications.showWarningAlert("Please Wait until" +StateMachine.getRunningTestName() +" test Completes");
				return;
			}
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Run All in Session Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
//			List<String> testFileIds = new ArrayList<>();

//			int testFileLines = testProcessManagement.testFileLinesCount();

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

//			if (DFCCConstant.entered == true) {
//				if (aitessProcessControlManagement.checkChannelStatusBeforeAnyTest().equals(true)
//						|| aitessProcessControlManagement.checkOnlineStatusBeforeAnyTestFile().equals(true)) {
//
//					stopButton.setDisable(true);
//
//				} else {
//					stopButton.setDisable(false);
//				}
//			}

			SessionTestStateObject.setRunningTestLeafId(selectedStageId);
			callStartTest(selectedStageId, "SESSION TEST", selectedTestTypeId, testFileIds);

		});

		startButton.setOnAction(e -> {
			if (!startButton.getText().equalsIgnoreCase("Resume")) {
			StateMachine.setConfirmTestStop(false);
			if (StateMachine.isConfirmTestFileCompleted()) {

				Notifications.showWarningAlert("Please Wait until" +StateMachine.getRunningTestName() +" test Completes");
				return;
			}
			}
			
			if (startButton.getText().equalsIgnoreCase("Resume")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Resume in Session Testing");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			} else {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Start in Session Testing");
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

			if (repeatCountTextField == null) {
				showAlert();
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

			SessionTestStateObject.setRunningTestLeafId(selectedStageId);
			callStartTest(selectedStageId, "SESSION TEST", selectedTestTypeId, testFileIds);
		});

		SessionTestStateObject.runningTestLeafStatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				System.out.println("New Value for Session Test::::" + newValue);
				SessionTestStateObject.getRunningTestLeafStatus().set(false);
				StateMachine.setTestState(TestState.COMPLETED);
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
				setStateMachineCurrentL1StageId();
				getStatusForAllStage();
			}
		});

		pauseButton.setOnAction(e -> {
			StateMachine.setConfirmTestStop(true);
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Pause in Session Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StateMachine.setTestState(TestState.PAUSED);
			startButton.setText("Resume");
			pauseButton.setDisable(true);
			startButton.setDisable(false);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e -> {

			if(!StateMachine.isConfirmTestStop()) {
				Notifications.showErrorAlert("Please Wait Aitess is Switching");
				return;
			}else {
				StateMachine.setConfirmTestStop(false);
			}
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Stop in Session Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StateMachine.setTestState(TestState.STOPPED);
			startButton.setText("Start");
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			startButton.setDisable(false);
			runAllButton.setDisable(false);
		});

		repeatCountLabel.setText("Repeat Count");
		repeatNotLabel.setText("(Note:Enter Value from 1 to 100)");
		repeatNotLabel.setTextFill(Color.WHITE);
		repeatNotLabel.setWrapText(true);
		repeatNotLabel.getStyleClass().add("session-testing-repeat-count-label-info");
		repeatCountLabel.getStyleClass().add("session-testing-repeat-count-label");
		repeatCountTextField.getStyleClass().add("session-testing-repeat-count-input");
		repeatCountTextField.setText("1");
		repeatCountVBox.getStyleClass().add("repeat-count-vbox");
		repeatCountTextField.setAlignment(Pos.CENTER);

		TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
			String newText = change.getControlNewText();

			try {
				int value = Integer.parseInt(newText);
				if (value >= 1 && value <= 100) {
					return change;
				}
			} catch (NumberFormatException e) {
			}

			return null;
		});

		repeatCountTextField.setTextFormatter(textFormatter);

//		repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//			if (!newValue.matches("\\d*")) {
//				repeatCountTextField.setText(oldValue);
//			} else if (newValue.length() > 3) {
//				repeatCountTextField.setText(oldValue);
//			}
//		});

		repeatCountVBox.setAlignment(Pos.CENTER);
		repeatCountVBox.setPadding(new Insets(5));
		repeatCountVBox.getChildren().addAll(repeatCountLabel, repeatCountTextField, repeatNotLabel);

		buttonHBox.setAlignment(Pos.CENTER);

		testProgressBar.setProgress(0);
		testProgressBar.getStyleClass().add("progress-bar");
		percentageLabel.getStyleClass().add("progress-label");

		progressBarHBox.setAlignment(Pos.CENTER);

		buttonMainVBox.setPadding(new Insets(25, 0, 0, 0));
		progressBarHBox.setPadding(new Insets(5, 0, 0, 0));
		allButtonHBox.getChildren().addAll(runAllButton, startButton, pauseButton, stopButton);
		progressBarHBox.getChildren().addAll(testProgressBar, percentageLabel);
		buttonMainVBox.getChildren().addAll(allButtonHBox, progressBarHBox);

		buttonHBox.getChildren().addAll(repeatCountVBox, buttonMainVBox);

//		Before Changing 100%
//		SessionTestStateObject.runnedTestFileCountProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null ) {
//				double percentage = (double) SessionTestStateObject.getRunnedTestFileCount().get() /  SessionTestStateObject.getTotalSelectedTestFileCount();
//				System.out.println("Session Test Files Count" + SessionTestStateObject.getRunnedTestFileCount().get());
//				System.out.println("GET TOTAL SELECTED FILE COUNt" + SessionTestStateObject.getTotalSelectedTestFileCount());
//				
//				double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
//				
//				System.out.println("Rounded Percentage "+ (roundedPercentage * 100) );
//				
//				Platform.runLater(() -> {
//					testProgressBar.setProgress(roundedPercentage);
//					percentageLabel.setText((int) (roundedPercentage * 100) + "%");
//				});
//			}
//		});
//		
//		return buttonHBox;
//	}

//	After Changing :
		SessionTestStateObject.runnedTestFileCountProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && SessionTestStateObject.getTotalSelectedTestFileCount() > 0) {
				double percentage = (double) SessionTestStateObject.getRunnedTestFileCount().get()
						/ SessionTestStateObject.getTotalSelectedTestFileCount();
				double roundedPercentage = Math.round(percentage * 100.0) / 100.0;


//	        if(SessionTestStateObject.getTotalSelectedTestFileCount() == 1) {
//	        	Platform.runLater(() -> {
//	                testProgressBar.setProgress(roundedPercentage);
//	                percentageLabel.setText((int) (roundedPercentage * 100) + "%");
//	            });
//	        }

				if (roundedPercentage <= 1.0) { // Less than 100%
					Platform.runLater(() -> {
						testProgressBar.setProgress(roundedPercentage);
						percentageLabel.setText((int) (roundedPercentage * 100) + "%");
					});
				} else if (roundedPercentage > 1.0) {
					// Set to 100%
					Platform.runLater(() -> {
						testProgressBar.setProgress(roundedPercentage);
						percentageLabel.setText("100%");
					});
				}
			}
		});

		return buttonHBox;
	}

//	List<CheckBox> checkBoxes = new ArrayList<>();

	private GridPane createSessionTestingResultsGridPane() {
		sessionTestingResultsGridPane.getStyleClass().add("session-testing-result-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		sessionTestingResultsGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingResultsGridPane.getRowConstraints().addAll(firstRow);

		sessionTestingResultsGridPane.add(createResultTableViewSession(), 0, 0);

		return sessionTestingResultsGridPane;
	}

	private TreeView<Label> createTreeView() {
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		rootItem.setGraphic(null);

		for (Entry<String, StageObject> l1_stage : SessionTestStateObject.getL1StageMap().entrySet()) {
			Label newL1StageLabel = new Label(l1_stage.getValue().getL1StageName());
			newL1StageLabel.setId(l1_stage.getKey());
			newL1StageLabel.setUserData(l1_stage.getValue());
			newL1StageLabel.getStyleClass().add("l1_stage-label");
			TreeItem<Label> sessionItem = new TreeItem<Label>(newL1StageLabel);
			createL2Stage(sessionItem, l1_stage);
			rootItem.getChildren().add(sessionItem);
		}

		sessionTreeView.setRoot(rootItem);
		sessionTreeView.getStyleClass().add("session-tree-view");
		sessionTreeView.setShowRoot(false);

		sessionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Label selectedLabel = newValue.getValue();

				if (newValue.getChildren().isEmpty()) {
					Entry<String, StageIdName> userData = (Entry<String, StageIdName>) selectedLabel.getUserData();
					getTestListByStageId(userData.getValue().getStageId(), userData.getValue().getTestTypeId());
					selectedStageId = userData.getValue().getStageId();
					selectedTestTypeId = userData.getValue().getTestTypeId();
				}
			}
		});

		return sessionTreeView;
	}

	private void createL2Stage(TreeItem<Label> l1_root, Entry<String, StageObject> l1_stage) {
		for (Entry<String, StageIdName> l2_stage : SessionTestStateObject.getL2StageMap().entrySet()) {
			if (l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
				Label newL2StageLabel = new Label(l2_stage.getValue().getStageName());
				newL2StageLabel.setId(l2_stage.getKey());
				newL2StageLabel.setUserData(l2_stage);
				newL2StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL2StageLabel);
				createL3Stage(childItem, l2_stage);
				l1_root.getChildren().add(childItem);
			}
		}
	}

	private void createL3Stage(TreeItem<Label> l2_root, Entry<String, StageIdName> l2_stage) {
		for (Entry<String, StageIdName> l3_stage : SessionTestStateObject.getL3StageMap().entrySet()) {
			if (l2_stage.getKey().equals(l3_stage.getValue().getParentId())) {
				Label newL3StageLabel = new Label(l3_stage.getValue().getStageName());
				newL3StageLabel.setId(l3_stage.getKey());
				newL3StageLabel.setUserData(l3_stage);
				newL3StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL3StageLabel);
				createL4Stage(childItem, l3_stage);
				l2_root.getChildren().add(childItem);
			}
		}
	}

	private void createL4Stage(TreeItem<Label> l3_root, Entry<String, StageIdName> l3_stage) {
		for (Entry<String, StageIdName> l4_stage : SessionTestStateObject.getL4StageMap().entrySet()) {
			if (l3_stage.getKey().equals(l4_stage.getValue().getParentId())) {
				Label newL4StageLabel = new Label(l4_stage.getValue().getStageName());
				newL4StageLabel.setId(l4_stage.getKey());
				newL4StageLabel.setUserData(l4_stage);
				newL4StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL4StageLabel);
				createL5Stage(childItem, l4_stage);
				l3_root.getChildren().add(childItem);
			}
		}
	}

	private void createL5Stage(TreeItem<Label> l4_root, Entry<String, StageIdName> l4_stage) {
		for (Entry<String, StageIdName> l5_stage : SessionTestStateObject.getL5StageMap().entrySet()) {
			if (l4_stage.getKey().equals(l5_stage.getValue().getParentId())) {
				Label newL5StageLabel = new Label(l5_stage.getValue().getStageName());
				newL5StageLabel.setUserData(l5_stage);
				newL5StageLabel.setId(l5_stage.getKey());
				newL5StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL5StageLabel);
				l4_root.getChildren().add(childItem);
			}
		}
	}

	private void getTestListByStageId(String stageId, String testTypeId) {
		SessionTestStateObject.getStageIdWithFileIds()
				.addListener((MapChangeListener.Change<? extends String, ? extends ObservableSet<String>> change) -> {
					if (change.wasAdded()) {
						disableCheckBox(change.getKey());
					}

				});
		setStateMachineCurrentL1StageId();

		String L1StageId = null;
		boolean mandatoryStatus = false;
		boolean continueWithErrorStatus = false;
		boolean checkboxDisable = false;

		for (Entry<String, ObservableList<String>> l1Stage : SessionTestStateObject.getL1StagesWithEndLeadId()
				.entrySet()) {
			if (l1Stage.getValue().contains(stageId)) {
				L1StageId = l1Stage.getKey();
			}
		}

		if (L1StageId != null) {
			mandatoryStatus = SessionTestStateObject.getL1MandatoryStatus().get(L1StageId);
			continueWithErrorStatus = SessionTestStateObject.getL1ContinueWithErrorStatus().get(L1StageId);
		}

		if (!L1StageId.equals(SessionTestStateObject.getCurrentRunningStageId())) {
			checkboxDisable = true;
			Debug.printDebug("---------Disable---------");
		} else if (L1StageId.equals(SessionTestStateObject.getCurrentRunningStageId())) {
			if (mandatoryStatus) {
				boolean breakForLoop = false;
				boolean isPreviousPending = false;

				for (String l1EndLeaf : SessionTestStateObject.getL1StagesWithEndLeadId().get(L1StageId)) {
					for (Entry<StageIdName, String> endLeaf : SessionTestStateObject.getEndLeafMap().entrySet()) {
						if (endLeaf.getKey().getStageId().equals(l1EndLeaf)) {
							if (endLeaf.getValue().toLowerCase().trim().equals("pending")) {
								if (isPreviousPending) {
									checkboxDisable = true;
								}
								isPreviousPending = true;
								if (endLeaf.getKey().getStageId().equals(stageId)) {
									breakForLoop = true;
								}
							}
						}

						if (breakForLoop) {
							break;
						}
					}
				}
			} else {
				for (Entry<StageIdName, String> endLeaf : SessionTestStateObject.getEndLeafMap().entrySet()) {
					if (endLeaf.getKey().getStageId().equals(stageId)) {
						if (endLeaf.getValue().toLowerCase().trim().equals("completed")) {
							checkboxDisable = true;
						}
					}
				}
			}

		}

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

		testFileMap = FXCollections.observableMap(testFileResponse.getTestFilesIdName());

		setTestListViewData(testFileMap, checkboxDisable, stageId);
	}

	private void filterList(String keyword) {
		String trimmedKeyword = keyword.trim();

		if (trimmedKeyword.isEmpty()) {
			setTestListViewData(testFileMap, false, "stageId");
			return;
		} else {
			ObservableMap<String, String> filteredMap = FXCollections.observableHashMap();
			for (Map.Entry<String, String> entry : testFileMap.entrySet()) {

				if (entry.getValue().toLowerCase().contains(trimmedKeyword.toLowerCase())) {
					filteredMap.put(entry.getKey(), entry.getValue());
				}

			}

			setTestListViewData(filteredMap, false, "stageId");
		}

	}

	private void setTestListViewData(ObservableMap<String, String> testFileMap, boolean checkboxDisable,
			String stageId) {

		testListView.getItems().clear();
		checkBoxes.clear();
		Set<String> stageCompleteTestList = SessionTestStateObject.getStageIdWithFileIds().get(stageId);

		for (Map.Entry<String, String> entry : testFileMap.entrySet()) {
			String filePath = entry.getValue();
			File file = new File(filePath);
			CheckBox newCheckBox = new CheckBox(file.getName());
			newCheckBox.setMnemonicParsing(false);
			newCheckBox.setId(entry.getKey());
			newCheckBox.getStyleClass().add("session-testing-checkbox");
			newCheckBox.setWrapText(true);

			if (checkboxDisable || (stageCompleteTestList != null && stageCompleteTestList.contains(entry.getKey()))) {
				newCheckBox.setDisable(true);
			}

			checkBoxes.add(newCheckBox);
			testListView.getItems().add(newCheckBox);


			newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				boolean anySelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);
				if (StateMachine.getTestState() != TestState.RUNNING) {
					startButton.setDisable(!anySelected);
				}
			});
		}

	}

//	private void disableCheckBox(String stageId) {
////		Debug.printDebug("stageId + "   " + SessionTestStateObject.getRunningTestLeafId()+"  "+selectedStageId);
//		if (selectedStageId.equals(stageId)) {
//			checkBoxes.forEach(e -> {
//				String fileId = e.getId();
//				ObservableSet<String> completedFileId = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
//				if (completedFileId.contains(fileId)) {
//					e.setSelected(false);
//					e.setDisable(true);
//				}
//			});
//		}
//	}
	
	private void disableCheckBox(String stageId) {
//		Debug.printDebug("stageId + "   " + SessionTestStateObject.getRunningTestLeafId()+"  "+selectedStageId);
		if (selectedStageId.equals(stageId)) {
			checkBoxes.forEach(e -> {
				String fileId = e.getId();
				ObservableSet<String> completedFileId = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
				if(!StateMachine.getTestState().equals(StateMachine.TestState.STOPPED))
				{
					if (completedFileId.contains(fileId)) {
						e.setSelected(false);
						e.setDisable(true);
						
					}	
				}else {
					if (completedFileId.contains(fileId)) {
						e.setSelected(false);
						e.setDisable(false);
					}
				}
			});
		}
	}

	private void setStateMachineCurrentL1StageId() {
		boolean founded = false;
		for (Entry<String, ObservableList<String>> l1Stage : SessionTestStateObject.getL1StagesWithEndLeadId()
				.entrySet()) {
			ObservableList<String> l1StageList = l1Stage.getValue();
			if (founded) {
				break;
			}
			for (Entry<StageIdName, String> endLeaf : SessionTestStateObject.getEndLeafMap().entrySet()) {
				if (l1StageList.contains(endLeaf.getKey().getStageId())) {
					if (endLeaf.getValue().equalsIgnoreCase("PENDING")) {
						SessionTestStateObject.setCurrentRunningStageId(l1Stage.getKey());
						founded = true;
						break;
					}
				}
			}
		}
	}

	private void callStartTest(String stageId, String stageName, String testTypeId, List<String> testFileIds) {
		Task<Response> task = new Task<Response>() {
			@Override
			protected Response call() throws Exception {

				boolean isContinueWithError = SessionTestStateObject.getL1ContinueWithErrorStatus()
						.get(SessionTestStateObject.getCurrentRunningStageId());

				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;
				int repeatCount = Integer.parseInt(repeatCountTextField.getText());

				testFileSize = testFileIds.size();
				int totalTestFileCount = testFileSize * repeatCount;
				SessionTestStateObject.setTotalSelectedTestFileCount(totalTestFileCount);
				Platform.runLater(() -> {
					percentageLabel.setText("0%");
				});
				SessionTestStateObject.getRunnedTestFileCount().set(0);

				return testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID, repeatCount,
						testFileIds, isContinueWithError, stageName, testTypeId, null);

			}
		};

		task.setOnSucceeded(event -> {
			Response response = task.getValue(); // Get the response
			if (response.getResponseCode() == 0) {
				Debug.printDebug("Session Test Task Response received: " + response.getResponseMessage());

				StateMachine.setTestState(TestState.PENDING);
				runAllButton.setDisable(false);
				startButton.setDisable(false);
				pauseButton.setDisable(true);
				stopButton.setDisable(true);

				Notifications.showErrorAlert(response.getResponseMessage());
			}
		});

		task.setOnFailed(event -> {
			runAllButton.setDisable(false);
			startButton.setDisable(false);
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			Throwable exception = task.getException();
			Debug.printDebug("Session Test Task failed with exception: " + exception.getMessage());
		});

		new Thread(task).start();
	}

	private TableView<SessionTestResult> createResultTableViewSession() {
		sessionTestTable = createTableView();

		return sessionTestTable;

	}

	private TableView<SessionTestResult> createTableView() {
		TableView<SessionTestResult> tableView = new TableView<>();
		tableView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());

		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setPrefHeight(900);

		TableColumn<SessionTestResult, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setMaxWidth(825);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		// Custom cell to show ellipsis for file path
		fileNameColumn.setCellFactory(
				new Callback<TableColumn<SessionTestResult, String>, TableCell<SessionTestResult, String>>() {
					@Override
					public TableCell<SessionTestResult, String> call(TableColumn<SessionTestResult, String> col) {
						return new TableCell<SessionTestResult, String>() {
							@Override
							protected void updateItem(String filePath, boolean empty) {
								super.updateItem(filePath, empty);
								if (empty || filePath == null) {
									setText(null);
								} else {
									File file = new File(filePath);
									setText(file.getName());
								}
							}

						};
					}
				});

		TableColumn<SessionTestResult, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setMinWidth(300);
		resultColumn.setMaxWidth(300);
		resultColumn.setStyle("-fx-alignment: CENTER;");
		rewriteColumn(resultColumn);

		SessionTestStateObject.getSessionTestResults()
				.addListener((ListChangeListener<? super SessionTestResult>) change -> {
					while (change.next()) {
						if (change.wasAdded()) {
							int lastIndex = SessionTestStateObject.getSessionTestResults().size() - 1;
							Platform.runLater(() -> {
								tableView.scrollTo(lastIndex);
								tableView.getSelectionModel().select(lastIndex);
								tableView.getFocusModel().focus(lastIndex);
							});
						}
					}
				});

		// View Button Column
		TableColumn<SessionTestResult, Void> viewButtonColumn = new TableColumn<>();
		viewButtonColumn.setCellFactory(col -> new TableCell<SessionTestResult, Void>() {
			private final Button viewButton = new Button("View");

			{
				viewButton.setOnAction(e -> {
					SessionTestResult sessionTestResult = getTableView().getItems().get(getIndex());
					File file = new File(sessionTestResult.getFileName());

					// Check if the file exists before trying to open it
					if (file.exists()) {
						try {
							String os = System.getProperty("os.name").toLowerCase();
							if (os.contains("win")) {
								// Windows-specific code
								Desktop desktop = Desktop.getDesktop();
								if (desktop.isSupported(Desktop.Action.OPEN)) {
									desktop.open(file);
								} else {
									Debug.printDebug("Open action not supported on this platform.");
								}
							} else if (os.contains("nix") || os.contains("nux")) {
								// Linux-specific code using xdg-open
								// Ensure the file path is absolute
								File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
								new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
							} else {
								Debug.printDebug("Unsupported OS: " + os);
							}
						} catch (IOException ex) {
							Debug.printDebug("Error opening file: " + ex.getMessage());
						}
					} else {
						Debug.printDebug("File does not exist: " + file.getAbsolutePath());
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(viewButton);
				}
			}
		});
		viewButtonColumn.setReorderable(false);
		viewButtonColumn.setSortable(false);
		viewButtonColumn.setMaxWidth(100);

		tableView.getColumns().addAll(fileNameColumn, resultColumn, viewButtonColumn);
		tableView.setItems(SessionTestStateObject.getSessionTestResults());

		return tableView;
	}

	private void rewriteColumn(TableColumn<SessionTestResult, String> resultColumn) {
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setCellFactory(column -> new TableCell<SessionTestResult, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: green;-fx-alignment: CENTER;-fx-text-fill:white");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					} else {
						setText(item);
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					}
				}
			}
		});
	}

	private void getStatusForAllStage() {
		List<StageObject> stageList = new ArrayList<StageObject>();
		SessionStageMapResponse response = sessionManagement
				.getAllSessionStage_IdsWithResult(currentSessionDetails.getSessionId());
		if (response.getResponse().getResponseCode() == 1 && response.getListOfStageObject() != null) {
			stageList.addAll(response.getListOfStageObject());
		} else {
		}
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream().forEach(stage -> {
			if (stage.getL1StageId() != null) {
				if (stage.getL2StageId() == null) {
					changeTreeViewBG(sessionTreeView.getRoot(), stage.getL1StageId(), stage.getStatus());
				}
			}
			if (stage.getL2StageId() != null) {
				if (stage.getL3StageId() == null) {
					changeTreeViewBG(sessionTreeView.getRoot(), stage.getL2StageId(), stage.getStatus());
				}
			}
			if (stage.getL3StageId() != null) {
				if (stage.getL4StageId() == null) {
					changeTreeViewBG(sessionTreeView.getRoot(), stage.getL3StageId(), stage.getStatus());
				}
			}
			if (stage.getL4StageId() != null) {
				if (stage.getL5StageId() == null) {
					changeTreeViewBG(sessionTreeView.getRoot(), stage.getL4StageId(), stage.getStatus());
				}
			}
			if (stage.getL5StageId() != null) {
				changeTreeViewBG(sessionTreeView.getRoot(), stage.getL5StageId(), stage.getStatus());
			}

		});

	}

	private void changeTreeViewBG(TreeItem<Label> item, String stageId, String status) {
		if (item.getValue() != null) {
			if (item.getValue().getId().equals(stageId)) {
				Label label = item.getValue();

				if (!label.getStyleClass().contains(status)) {
					label.getStyleClass().clear();
					label.getStyleClass().addAll("label", "l1_stage-label", status.toLowerCase());
				}
			}
		}

		for (TreeItem<Label> child : item.getChildren()) {
			changeTreeViewBG(child, stageId, status);
		}
	}

}
