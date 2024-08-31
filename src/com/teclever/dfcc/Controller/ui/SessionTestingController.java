package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject.SessionTestResult;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

	private ObservableMap<String, String> testFileMap;

	private RunConfigurationService runConfigurationService = new RunConfigurationService();
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private TestProcessManagement testProcessManagement = new TestProcessManagement();
	private SessionManagement sessionManagement = new SessionManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();

	private String selectedStageId = null;
	private String selectedTestTypeId = null;
	private boolean isTrailSession = false;

	private TableView<SessionTestResult> sessionTestTable = new TableView<>();

	public GridPane createSessionTestingGridPane(boolean status) {
		if (status) {
			isTrailSession = true;
		}
		getSessionTestData();
		initializeRdfFileCopyPopup();
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

		return sessionTestingTreeviewGridPane;
	}

	private GridPane createSessionTestingRightSide() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(80);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);

		sessionTestingListMainGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingListMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
		sessionTestingListMainGridPane.setHgap(5);
		sessionTestingListMainGridPane.setVgap(5);
		sessionTestingListMainGridPane.add(createTestListView(), 0, 0);
		sessionTestingListMainGridPane.add(createButtonBox(), 0, 1);

		return sessionTestingListMainGridPane;
	}

	private VBox createTestListView() {
		testListView.getStyleClass().add("session-testing-list-view");

		testListVBox.getChildren().addAll(testListView);
		testListVBox.getStyleClass().add("session-testing-right-container");
		return testListVBox;
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

			SessionTestStateObject.setRunningTestLeafId(selectedStageId);
			callStartTest(selectedStageId, "SESSION TEST", selectedTestTypeId, testFileIds);

		});

		startButton.setOnAction(e -> {
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

			SessionTestStateObject.setRunningTestLeafId(selectedStageId);
			callStartTest(selectedStageId, "SESSION TEST", selectedTestTypeId, testFileIds);
		});

		SessionTestStateObject.runningTestLeafStatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				SessionTestStateObject.getRunningTestLeafStatus().set(false);
				StateMachine.setTestState(TestState.COMPLETED);
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
				setStateMachineCurrentL1StageId();
			}
		});

		pauseButton.setOnAction(e -> {
			StateMachine.setTestState(TestState.PAUSED);
			startButton.setText("Resume");
			pauseButton.setDisable(true);
			startButton.setDisable(false);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e -> {
			StateMachine.setTestState(TestState.STOPPED);
			startButton.setText("Start");
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			startButton.setDisable(false);
			runAllButton.setDisable(false);
		});

		repeatCountLabel.setText("Repeat Count");
		repeatCountLabel.getStyleClass().add("session-testing-repeat-count-label");
		repeatCountTextField.getStyleClass().add("session-testing-repeat-count-input");
		repeatCountTextField.setText("1");

		repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				repeatCountTextField.setText(oldValue);
			} else if (newValue.length() > 3) {
				repeatCountTextField.setText(oldValue);
			}
		});

		repeatCountVBox.setAlignment(Pos.CENTER);
		repeatCountVBox.getChildren().addAll(repeatCountLabel, repeatCountTextField);

		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getChildren().addAll(repeatCountVBox, runAllButton, startButton, pauseButton, stopButton);
		return buttonHBox;
	}

	private GridPane createSessionTestingResultsGridPane() {
		sessionTestingResultsGridPane.getStyleClass().add("session-testing-result-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		sessionTestingResultsGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingResultsGridPane.getRowConstraints().addAll(firstRow);

		sessionTestingResultsGridPane.add(createResultTableView(), 0, 0);

		return sessionTestingResultsGridPane;
	}

	private void getSessionTestData() {
		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream().filter(stage -> {
			return !(stage.isDefaultStatus() || stage.isAdvanceStatus());
		}).forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			SessionTestStateObject.addL1StageMap(l1StageId, stage);
			SessionTestStateObject.addL1MandatoryStatus(l1StageId, stage.isMandatoryStatus());
			SessionTestStateObject.addL1ContinueWithErrorStatus(l1StageId, stage.isContinueWithErrorStatus());
		});

		observableStageList.stream().forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			String l2StageId = stage.getL2StageId();
			String l3StageId = stage.getL3StageId();
			String l4StageId = stage.getL4StageId();
			String l5StageId = stage.getL5StageId();
			if (l2StageId != null && SessionTestStateObject.getL1StageMap().containsKey(l1StageId)) {
				StageIdName l2StageObject = new StageIdName();
				l2StageObject.setParentId(l1StageId);
				l2StageObject.setStageId(l2StageId);
				l2StageObject.setStageName(stage.getL2StageName());
				if (l3StageId == null && stage.getTestTypeId() != null) {
					l2StageObject.setTestTypeId(stage.getTestTypeId());
					SessionTestStateObject.getEndLeafMap().put(l2StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l2StageId);
				}
				SessionTestStateObject.addL2StageMap(l2StageId, l2StageObject);
			}

			if (l3StageId != null && SessionTestStateObject.getL2StageMap().containsKey(l2StageId)) {
				StageIdName l3StageObject = new StageIdName();
				l3StageObject.setParentId(l2StageId);
				l3StageObject.setStageId(l3StageId);
				l3StageObject.setStageName(stage.getL3StageName());
				if (l4StageId == null && stage.getTestTypeId() != null) {
					l3StageObject.setTestTypeId(stage.getTestTypeId());
					SessionTestStateObject.getEndLeafMap().put(l3StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l3StageId);
				}
				SessionTestStateObject.addL3StageMap(l3StageId, l3StageObject);
			}

			if (l4StageId != null && SessionTestStateObject.getL3StageMap().containsKey(l3StageId)) {
				StageIdName l4StageObject = new StageIdName();
				l4StageObject.setParentId(l3StageId);
				l4StageObject.setStageId(l4StageId);
				l4StageObject.setStageName(stage.getL4StageName());
				if (l5StageId == null && stage.getTestTypeId() != null) {
					l4StageObject.setTestTypeId(stage.getTestTypeId());
					SessionTestStateObject.getEndLeafMap().put(l4StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l4StageId);
				}
				SessionTestStateObject.addL4StageMap(l4StageId, l4StageObject);
			}

			if (l5StageId != null && SessionTestStateObject.getL4StageMap().containsKey(l4StageId)) {
				StageIdName l5StageObject = new StageIdName();
				l5StageObject.setParentId(l4StageId);
				l5StageObject.setStageId(l5StageId);
				l5StageObject.setStageName(stage.getL5StageName());
				if (stage.getTestTypeId() != null) {
					l5StageObject.setTestTypeId(stage.getTestTypeId());
					SessionTestStateObject.getEndLeafMap().put(l5StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l5StageId);
				}
				SessionTestStateObject.addL5StageMap(l5StageId, l5StageObject);
			}
		});

		for (Entry<StageIdName, String> status : SessionTestStateObject.getEndLeafMap().entrySet()) {

		}
		setStateMachineCurrentL1StageId();
	}

	private TreeView<Label> createTreeView() {
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		rootItem.setGraphic(null);

		for (Entry<String, StageObject> l1_stage : SessionTestStateObject.getL1StageMap().entrySet()) {
			Label newL1StageLabel = new Label(l1_stage.getValue().getL1StageName());
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
//		System.out.println("STAGE ID : "+stageId+"  TEST TYPE ID : "+testTypeId);
//		System.out.println("Running End Leaf Id : "+ SessionTestStateObject.getRunningTestLeafId());

		SessionTestStateObject.getStageIdWithFileIds()
				.addListener((MapChangeListener.Change<? extends String, ? extends ObservableSet<String>> change) -> {
					if (change.wasAdded()) {
//				System.out.println("Added: " + change.getKey() + " -> " + change.getValueAdded());
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
			System.out.println("---------Disable---------");
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

	private void setTestListViewData(ObservableMap<String, String> testFileMap, boolean checkboxDisable,
			String stageId) {
		testListView.getItems().clear();
		checkBoxes.clear();

		Set<String> stageCompleteTestList = SessionTestStateObject.getStageIdWithFileIds().get(stageId);

		for (Map.Entry<String, String> entry : testFileMap.entrySet()) {
			String test = entry.getValue();
			CheckBox newCheckBox = new CheckBox(test);
			newCheckBox.setId(entry.getKey());
			newCheckBox.getStyleClass().add("session-testing-checkbox");
			newCheckBox.setWrapText(true);
			if (checkboxDisable) {
				newCheckBox.setDisable(checkboxDisable);
			} else {
				if (stageCompleteTestList != null && stageCompleteTestList.contains(entry.getKey())) {
					newCheckBox.setDisable(true);
				} else {
					newCheckBox.setDisable(false);
				}
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

//		List<String> fileId = Arrays.asList("TestFiled28e199702f147a89213b8c4d910178b", "TestFile6510b1756972433999b865861836e5ce", "TestFilea9d5320eab5a45e9aca6ca6d43ae49d9");
//
//		Task<Void> task = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//				for (int i = 0; i < fileId.size(); i++) {
//					Thread.sleep(5000); 
//					SessionTestStateObject.setStageIdWithFileIds("L3_004",fileId.get(i));
//				}
//				return null;
//			}
//		};
//		new Thread(task).start();

	}

	private void disableCheckBox(String stageId) {
//		System.out.println(stageId + "   " + SessionTestStateObject.getRunningTestLeafId()+"  "+selectedStageId);
		if (selectedStageId.equals(stageId)) {
			checkBoxes.forEach(e -> {
				String fileId = e.getId();
				ObservableSet<String> completedFileId = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
				if (completedFileId.contains(fileId)) {
					e.setSelected(false);
					e.setDisable(true);
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
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				boolean isContinueWithError = SessionTestStateObject.getL1ContinueWithErrorStatus()
						.get(SessionTestStateObject.getCurrentRunningStageId());

				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;
				int repeatCount = Integer.parseInt(repeatCountTextField.getText());

				Response response = testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID,
						repeatCount, testFileIds, isContinueWithError, stageName, testTypeId);

				return null;
			}
		};

		new Thread(task).start();
	}

	private TableView<SessionTestResult> createResultTableView() {
		sessionTestTable = createTableView();

		return sessionTestTable;

	}

	private TableView<SessionTestResult> createTableView() {
		TableView<SessionTestResult> tableView = new TableView<>();
		tableView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setPrefHeight(900);

		TableColumn<SessionTestResult, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setMaxWidth(1100);
		fileNameColumn.setMaxWidth(1100);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<SessionTestResult, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setMinWidth(200);
		resultColumn.setMaxWidth(200);
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

		tableView.getColumns().addAll(fileNameColumn, resultColumn);
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
						setStyle("-fx-background-color: green;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;");
					} else {
						setText(item);
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}

	private void initializeRdfFileCopyPopup() {
		SessionTestStateObject.isRdfFileCopyPopupStatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				try {
					FXMLLoader rdfFileCopyPopup = new FXMLLoader(getClass()
							.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/RdfFileCopy.fxml"));
					Parent root = rdfFileCopyPopup.load();

					Stage stage = new Stage();
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.initStyle(StageStyle.UNDECORATED);
					stage.centerOnScreen();
					
					SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(false);
					
					Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 1000) / 2;
				    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 500) / 2;
				    stage.setX(centerX);
				    stage.setY(centerY);
				   
					stage.setScene(new Scene(root));
					stage.showAndWait();
				    
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}