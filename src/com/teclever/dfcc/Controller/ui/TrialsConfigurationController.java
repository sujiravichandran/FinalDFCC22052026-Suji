package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.TrailSaveResponse;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.model.SubStage;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TrialsConfigurationController {
	private GridPane trialsConfigMainGridPane = new GridPane();
	private GridPane headingGridPane = new GridPane();
	private GridPane trialsConfigBottomGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();

	private HBox buttonBox = new HBox();
	private Button finalizeButton = new Button("Finalize Config");

	private TextField stageNameField = new TextField();
	private Button addNewStageButton = new Button("Add New Stage");
	private ListView<String> stage1_listView = new ListView<>();
	private TreeView<Node> sessionTreeView = new TreeView<>();

	private ObservableList<String> stage1List = FXCollections.observableArrayList();
	private ObservableList<StageOne> session_l1Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l2Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l3Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l4Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l5Data = FXCollections.observableArrayList();

	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private Map<String, String> sessionTypeMap = new HashMap<>();

	private RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private TestMapingController testMapingController = new TestMapingController();
	private StageConfiguration stageConfig = new StageConfiguration();
	private SessionManagement sessionManagement = new SessionManagement();

	private String UUT_ID;
	private String RUN_CONFIG_ID;
	private String SESSION_ID;

	private boolean FINALIZE_CONFIG = false;

	public TrialsConfigurationController() {
		FINALIZE_CONFIG = sessionManagement.getFinalizeStatus();
		UUT_ID = currentSessionDetails.getUutId();
		SESSION_ID = currentSessionDetails.getSessionId();
		List<SessionMasterDTO> sessionTypeList = stageConfig.getSessionMasterList();
		initializeSessionTypeMap(sessionTypeList);
		fetchTrialsData();
		disableDispaly();
	}

	public void refreshTrialsList() {
		fetchTrialsData();
		disableDispaly();
	}

	private void initializeSessionTypeMap(List<SessionMasterDTO> sessionTypeList) {
		for (SessionMasterDTO session : sessionTypeList) {
			sessionTypeMap.put(session.getSessionMasterId(), session.getSessionTypeName());
		}
	}

	public GridPane createTrialsConfigMainGridPane() {
		trialsConfigMainGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/TrialsConfiguration.css")
						.toExternalForm());
		trialsConfigMainGridPane.getStyleClass().add("trialsConfig-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		trialsConfigMainGridPane.setPadding(new Insets(5));
		trialsConfigMainGridPane.setVgap(5);
		trialsConfigMainGridPane.setHgap(5);

		trialsConfigMainGridPane.getColumnConstraints().addAll(firstColumn);
		trialsConfigMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		trialsConfigMainGridPane.add(createHeadingBox(), 0, 0);
		trialsConfigMainGridPane.add(createTrialsConfigBottomGridPane(), 0, 1);

		return trialsConfigMainGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(34);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("TRIALS CONFIGURATION");
		title.getStyleClass().add("trialsConfig-title");
		titleBox.getChildren().add(title);

		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(finalizeButton);

		finalizeButton.setOnAction(e -> {
			handleFinalizeButton();
		});

		headingGridPane.add(titleBox, 0, 0);
		headingGridPane.add(buttonBox, 2, 0);

		return headingGridPane;
	}

	private void handleFinalizeButton() {
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to finalize trials configuration? Once finalized, it cannot be modified.";
		Notifications.showConfirmationDialog(title, contentText, () -> finalizedSaving());
	}

	private void finalizedSaving() {
		TrailSaveResponse response = sessionManagement.finalize(SESSION_ID);

		if (response.getCode() == 1) {
			Notifications.showSuccessAlert("Trials Configuration Successfully Finalized");
			FINALIZE_CONFIG = true;
			disableDispaly();
			getAllStagesData();
			
		} else if (response.getCode() == 0) {
			
			Map<String, String> dataMap = response.getStageNameMessage();

			StringBuilder keyList = new StringBuilder();
			for (String key : dataMap.keySet()) {
				keyList.append(key).append("\n");
			}
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("No test files have been configured for the following stages:");

			TextArea textArea = new TextArea(keyList.toString());
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setPrefSize(300, 200);

			alert.getDialogPane().setExpandableContent(textArea);
			alert.showAndWait();
		}
	}
	
	private void getAllStagesData() {
		SessionStageMapResponse data = sessionManagement
				.getAllSessionStageMapping(currentSessionDetails.getSessionId());
		if (data.getResponse().getResponseCode() == 1) {
			StateMachine.setStageDatalist(data.getListOfStageObject());
		} else {
			Debug.printDebug("Error in getAllStagesData : " + data.getResponse().getResponseMessage());
		}
	}

	private void disableDispaly() {

		finalizeButton.setDisable(FINALIZE_CONFIG);
		stageNameField.setDisable(FINALIZE_CONFIG);
		if (trialsConfigBottomGridPane.getChildren().size() == 2) {
			trialsConfigBottomGridPane.getChildren().get(1).setDisable(FINALIZE_CONFIG);
		}
		
	}

	private GridPane createTrialsConfigBottomGridPane() {
		trialsConfigBottomGridPane.getStyleClass().add("trialsConfig-container");

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);

		trialsConfigBottomGridPane.setHgap(10);
		trialsConfigBottomGridPane.getColumnConstraints().addAll(column1, column2);
		trialsConfigBottomGridPane.getRowConstraints().addAll(row1);

		return trialsConfigBottomGridPane;
	}

	private ObservableList<StageOne> getStage1FromDb(String uutId) {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = sessionManagement.getLevelOneStageMasterBySessionId(uutId, "ST4");

		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
				StageOne stage = new StageOne();
				stage.setL1_name(levelOneDto.getStageName());
				stage.setId(levelOneDto.getLevelOneId());
				stage.setDefault(levelOneDto.isDefaultStatus());
				stage.setMandatory(levelOneDto.isMandatoryStatus());
				stage.setContinueWithError(levelOneDto.isContinueWithErrorStatus());
				stage.setAdvancedTest(levelOneDto.isAdvanceTestStatus());
				stage.setHasNext("Y".equals(levelOneDto.getNextLevel()));
				List<String> sessionIdsList = Arrays.asList(levelOneDto.getSessionIds().split(","));
				stage.setSessionType(new ArrayList<>(sessionIdsList));

				if (!levelOneDto.isDefaultStatus()) {
					stageList.add(stage);
				}
			}
		}
		return stageList;
	}

	private VBox stageConfigTreeView() {

		addNewStageButton.setDisable(true);

		stageNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			addNewStageButton.setDisable(newValue.trim().isEmpty());
		});

		addNewStageButton.setOnAction(e -> addNewStage());

		HBox stage1HBox = new HBox(10, stageNameField, addNewStageButton);
		stage1HBox.setAlignment(Pos.CENTER_LEFT);

		VBox treeVBox = new VBox(10, stage1HBox, stage1_listView, sessionTreeView);
		treeVBox.getStyleClass().add("trialsConfig-bottom-view-container");
		treeVBox.setPadding(new Insets(10));

		return treeVBox;
	}

	private void fetchTrialsData() {
		session_l1Data = getStage1FromDb(UUT_ID);
		trialsConfigBottomGridPane.add(stageConfigTreeView(), 0, 0);
		initializeStage1ListView();
		initializeTreeView();
		initializeListeners();
		setSearchableTextField();
	}

	private void initializeStage1ListView() {
		stage1List.clear();
		session_l1Data.forEach(sessionStage -> stage1List.add(sessionStage.getL1_name()));
		stage1_listView.setItems(stage1List);
		enablingStage1ListView(false);
	}

	private void enablingStage1ListView(boolean isVisible) {
		stage1_listView.setVisible(isVisible);
		stage1_listView.setManaged(isVisible);
		sessionTreeView.setPrefHeight(isVisible ? 550 : 800);
	}

	private void initializeListeners() {
		stageNameField.focusedProperty().addListener((obs, oldValue, newValue) -> enablingStage1ListView(newValue));
		stage1_listView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				stageNameField.setText(newValue);
				stageNameField.requestFocus();
				stageNameField.end();
				enablingStage1ListView(false);
			}
		});
	}

	private void setSearchableTextField() {
		FilteredList<StageOne> filteredData = new FilteredList<>(session_l1Data, p -> true);
		SortedList<StageOne> sortedList = new SortedList<>(filteredData);

		stageNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(sessionStage -> {
				boolean isEmpty = (newValue == null || newValue.isEmpty());
				boolean containsText = sessionStage.getL1_name().trim().toLowerCase()
						.contains(newValue.toLowerCase().trim());

				addNewStageButton.setDisable(!filteredData.isEmpty());
				enablingStage1ListView(!filteredData.isEmpty());

				return isEmpty || containsText;
			});

			ObservableList<String> stringList = FXCollections.observableArrayList();
			sortedList.forEach(sessionStage -> stringList.add(sessionStage.getL1_name()));
			stage1_listView.setItems(stringList);
		});
	}

	private void initializeTreeView() {
		TreeItem<Node> root = new TreeItem<>(new Label("Sessions"));
		root.setExpanded(true);

		for (StageOne stageL1 : session_l1Data) {
			GridPane level1_container = createLevel1UI(stageL1);
			TreeItem<Node> sessionItem = new TreeItem<>(level1_container);
			root.getChildren().add(sessionItem);
			if (stageL1.isHasNext()) {
				session_l2Data = getSubStagesFromDb(stageL1.getId(), stageL1.isDefault(), stageL1.isAdvancedTest());
				createTreeItemsForL2(stageL1, sessionItem, session_l2Data);
			}
		}

		sessionTreeView.setRoot(root);
		sessionTreeView.getStyleClass().add("tree-view");
		sessionTreeView.setShowRoot(false);

		sessionTreeView.getSelectionModel().selectedItemProperty().removeListener(selectionListener);
		sessionTreeView.getSelectionModel().selectedItemProperty().addListener(selectionListener);

		sessionTreeView.setCellFactory(tv -> new TreeCell<Node>() {
			@Override
			protected void updateItem(Node item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setGraphic(item);
					TreeItem<Node> treeItem = getTreeItem();
					int depth = 0;
					while (treeItem != null && treeItem.getParent() != null) {
						treeItem = treeItem.getParent();
						depth++;
					}
					this.setPadding(new Insets(3, 0, 3, depth * 10));
				}
			}
		});
	}

	private final ChangeListener<TreeItem<Node>> selectionListener = (observable, oldValue, newValue) -> {
		if (newValue != null) {
			Node selectedNode = newValue.getValue();

			Boolean isInterfaceTest = extractStageName(selectedNode);
			if (isInterfaceTest) {
				String message = "This configuration is similar to the SRU Test configuration.\n"
						+ "Move LRU/SRU/... path to view the configuration.";
				Notifications.showWarningAlert(message);
			}

			Map<String, String> testInfoMap = extractTestTypeInfoFromNode(selectedNode);
			if (testInfoMap != null) {
				String testTypeID = testInfoMap.get("testTypeName");
				RUN_CONFIG_ID = fetchRunConfigID(testTypeID);
				if (RUN_CONFIG_ID != null) {
					trialsConfigBottomGridPane.getChildren()
							.removeIf(node -> GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 1);
					trialsConfigBottomGridPane
							.add(testMapingController.TestMappingView(RUN_CONFIG_ID, selectedNode.getId()), 1, 0);
					disableDispaly();
				} else {
					trialsConfigBottomGridPane.getChildren()
							.removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 0);

					Label warningLabel = createLabel("Please add test files for selected test type", "warning-label");
					HBox warningBox = new HBox(10, warningLabel);
					warningBox.setAlignment(Pos.CENTER);
					trialsConfigBottomGridPane.add(warningBox, 1, 0);
					disableDispaly();
				}
			}
		}
	};

	private String fetchRunConfigID(String testTypeID) {
		List<RunConfigurationDto> allRunConfigData = runConfig.getAllRunConfig();
		String runConfigId = null;
		for (RunConfigurationDto runConfigDto : allRunConfigData) {
			if (runConfigDto.getUutId().equals(UUT_ID) && runConfigDto.getTestTypeId().equals(testTypeID)) {
				runConfigId = runConfigDto.getRunConfigId();
				break;
			}
		}
		return runConfigId;
	}

	private boolean extractStageName(Node node) {
		if (node instanceof GridPane) {
			GridPane gridPane = (GridPane) node;
			for (Node child : gridPane.getChildren()) {
				if (child instanceof Label) {
					Label newLabel = (Label) child;
					if (newLabel.getText().trim().toLowerCase().equals("interface")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Map<String, String> extractTestTypeInfoFromNode(Node node) {
		Map<String, String> testInfoMap = new HashMap<>();

		if (node instanceof GridPane) {
			GridPane gridPane = (GridPane) node;
			for (Node child : gridPane.getChildren()) {
				if (child instanceof HBox) {
					HBox hBox = (HBox) child;
					String hBoxId = hBox.getId();
					if (hBoxId != null && !hBoxId.isEmpty()) {
						testInfoMap.put("testTypeName", hBoxId);
						return testInfoMap;
					}
				}
			}
		}
		return null;
	}

	private GridPane createLevel1UI(StageOne stage1) {
		Label l1_label = createLabel(stage1.getL1_name(), "session-stage-l1-label");

		HBox labelContainer = new HBox(20, l1_label);
		labelContainer.setAlignment(Pos.CENTER_LEFT);
		ArrayList<String> sessionTypes = stage1.getSessionType();
		if (!sessionTypes.isEmpty()) {
			HBox sessionTypeHbox = new HBox(10);
			sessionTypeHbox.setAlignment(Pos.CENTER);

			for (String typeId : sessionTypes) {
				String typeName = sessionTypeMap.get(typeId);
				Label typeField = createLabel(typeName, "session-type-label");
				typeField.setPrefWidth(Region.USE_COMPUTED_SIZE);
				typeField.setAlignment(Pos.CENTER);
				sessionTypeHbox.getChildren().add(typeField);
			}
			labelContainer.getChildren().add(sessionTypeHbox);
		}

		Button addBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/AddIcon.png", "ADD",
				event -> addSubStage(stage1.getId(), stage1.getL1_name()));
		Button editBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/Edit.png", "Edit",
				event -> editStage1(stage1));
		Button delBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/delete.png", "DEL",
				event -> deleteStage(stage1.getId(), stage1.getL1_name()));

		HBox buttonsContainer = new HBox(10);
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		buttonsContainer.setVisible(false);

		if (!stage1.isDefault() && !stage1.isAdvancedTest()) {
			buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
		}

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHalignment(HPos.RIGHT);
		GridPane gridPaneL1 = new GridPane();
		gridPaneL1.getColumnConstraints().addAll(column1, column2);
		gridPaneL1.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> buttonsContainer.setVisible(true));
		gridPaneL1.addEventFilter(MouseEvent.MOUSE_EXITED, e -> buttonsContainer.setVisible(false));

		gridPaneL1.add(labelContainer, 0, 0);
		gridPaneL1.add(buttonsContainer, 1, 0);
		gridPaneL1.setId(stage1.getId());
		gridPaneL1.setPadding(new Insets(10, 0, 0, 0));

		return gridPaneL1;
	}

	private void createTreeItemsForL2(StageOne stageL1, TreeItem<Node> parentItem, ObservableList<SubStage> s2) {
		for (SubStage stageL2 : s2) {
			if (stageL2.getpId().equals(stageL1.getId())) {
				GridPane gridPaneL2 = createSubStagesUI(stageL2, parentItem);
				if (gridPaneL2 == null) {
					continue;
				}
				TreeItem<Node> stageItemL2 = new TreeItem<>(gridPaneL2);
				parentItem.getChildren().add(stageItemL2);
				if (stageL2.isHasNext()) {
					session_l3Data = getSubStagesFromDb(stageL2.getId(), stageL2.isParentDefault(),
							stageL2.isParentAdvancedTest());
					if (!stageL2.isParentAdvancedTest() || (stageL2.isParentAdvancedTest()
							&& !stageL2.getL_name().trim().toLowerCase().contains("interface"))) {
						createTreeItemsForL3(stageL2, stageItemL2, session_l3Data);
					}

				} else {
					createStageFieldUI(stageL2, gridPaneL2);
				}

			}
		}
	}

	private void createTreeItemsForL3(SubStage stageL2, TreeItem<Node> parentItem, ObservableList<SubStage> s3) {
		for (SubStage stageL3 : s3) {
			if (stageL3.getpId().equals(stageL2.getId())) {
				GridPane gridPaneL3 = createSubStagesUI(stageL3, parentItem);
				if (gridPaneL3 == null) {
					continue;
				}
				TreeItem<Node> stageItemL3 = new TreeItem<>(gridPaneL3);
				parentItem.getChildren().add(stageItemL3);

				if (stageL3.isHasNext()) {
					session_l4Data = getSubStagesFromDb(stageL3.getId(), stageL3.isParentDefault(),
							stageL3.isParentAdvancedTest());
					createTreeItemsForL4(stageL3, stageItemL3, session_l4Data);
				} else {
					createStageFieldUI(stageL3, gridPaneL3);
				}
			}
		}
	}

	private void createTreeItemsForL4(SubStage stageL3, TreeItem<Node> parentItem, ObservableList<SubStage> s4) {
		for (SubStage stageL4 : s4) {
			if (stageL4.getpId().equals(stageL3.getId())) {

				GridPane gridPaneL4 = createSubStagesUI(stageL4, parentItem);
				if (gridPaneL4 == null) {
					continue;
				}

				TreeItem<Node> stageItemL4 = new TreeItem<>(gridPaneL4);
				parentItem.getChildren().add(stageItemL4);

				if (stageL4.isHasNext()) {
					session_l5Data = getSubStagesFromDb(stageL4.getId(), stageL4.isParentDefault(),
							stageL4.isParentAdvancedTest());
					createTreeItemsForL5(stageL4, stageItemL4, session_l5Data);
				} else {
					createStageFieldUI(stageL4, gridPaneL4);
				}
			}
		}
	}

	private void createTreeItemsForL5(SubStage stageL4, TreeItem<Node> parentItem, ObservableList<SubStage> s5) {
		for (SubStage stageL5 : s5) {
			if (stageL5.getpId().equals(stageL4.getId())) {
				GridPane gridPaneL5 = createSubStagesUI(stageL5, parentItem);
				if (gridPaneL5 == null) {
					continue;
				}
				TreeItem<Node> stageItemL5 = new TreeItem<>(gridPaneL5);
				parentItem.getChildren().add(stageItemL5);
				createStageFieldUI(stageL5, gridPaneL5);
			}
		}
	}

	private GridPane createSubStagesUI(SubStage stage, TreeItem<Node> parentItem) {

		Label stageLabel = createLabel(stage.getL_name(), "stage-label");

		Button addBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/AddIcon.png", "ADD",
				event -> addSubStage(stage.getId(), stage.getL_name()));
		Button delBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/delete.png", "DEL",
				event -> deleteStage(stage.getId(), stage.getL_name()));
		Button editBtn = createImageButton(DFCCConstant.JARSTRING + "/Resources/Images/Edit.png", "Edit", event -> {
			String testTypeName = fetchTestTypeNameById(stage.getTestType());
			editStage(stage.getId(), stage.getpId(), stage.getL_name(), testTypeName);
		});

		HBox buttonsContainer = new HBox(10);
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		buttonsContainer.setPadding(new Insets(0, 10, 0, 0));
		buttonsContainer.setVisible(false);

		if (stage.getId().startsWith("L5")) {
			buttonsContainer.getChildren().addAll(editBtn, delBtn);
		} else if (!stage.isParentDefault() && !stage.isParentAdvancedTest()) {
			buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
		} else if (stage.isParentAdvancedTest()) {
			if (stage.getId().startsWith("L2")) {
				if (stage.getL_name().trim().toLowerCase().contains("hwatp")) {
					buttonsContainer.getChildren().addAll(addBtn);
				} else if (stage.getL_name().trim().toLowerCase().contains("custom test")) {
					return null;
				}
			}
		}

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHalignment(HPos.RIGHT);

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 0, 0, 0));
		gridPane.getColumnConstraints().addAll(column1, column2);
		gridPane.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> buttonsContainer.setVisible(true));
		gridPane.addEventFilter(MouseEvent.MOUSE_EXITED, e -> buttonsContainer.setVisible(false));

		gridPane.add(stageLabel, 0, 0);
		gridPane.add(buttonsContainer, 1, 0);
		gridPane.setId(stage.getId());
		return gridPane;
	}

	private void createStageFieldUI(SubStage stage, GridPane gridPane) {
		String testTypeName = fetchTestTypeNameById(stage.getTestType());

		Label stageField = createLabel(testTypeName, "testType_label");
		stageField.setPrefWidth(Region.USE_COMPUTED_SIZE);
		stageField.setAlignment(Pos.CENTER);

		Label stageLabel = createLabel(stage.getL_name(), "stage-label");

		HBox stageHBox = createStageHBox(stageLabel, stageField);
		stageHBox.setId(stage.getTestType());
		gridPane.getChildren()
				.removeIf(node -> node instanceof HBox && ((HBox) node).getChildren().contains(stageField));

		gridPane.add(stageHBox, 0, 0);
	}

	private ObservableList<SubStage> getSubStagesFromDb(String parentId, boolean isDefault, boolean isAdvancedTest) {
		ObservableList<SubStage> subStageList = FXCollections.observableArrayList();
		StageMasterLevelsResponse response = stageConfig.getStageLevelMaster(parentId);

		if (response.getResponse().getResponseCode() == 1) {
			for (StageMasterLevelDto levelDto : response.getLevelsResponse()) {
				SubStage stage = new SubStage();
				stage.setpId(levelDto.getParentId());
				stage.setId(levelDto.getLevelId());
				stage.setL_name(levelDto.getStageName());
				stage.setTestType(levelDto.getTestType());
				stage.setHasNext("Y".equals(levelDto.getNextLevel()));
				stage.setParentDefault(isDefault);
				stage.setParentAdvancedTest(isAdvancedTest);
				subStageList.add(stage);
			}
		}

		return subStageList;
	}

	private String fetchTestTypeNameById(String testTypeId) {
		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestTypeId().equals(testTypeId)) {
				return testType.getTestName();
			}
		}
		return null;
	}

	private Button createImageButton(String imagePath, String tooltipText, EventHandler<ActionEvent> action) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);

		Button button = new Button("", imageView);
		button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
		button.setOnAction(action);
		return button;
	}

	private Label createLabel(String text, String styleClass) {
		Label label = new Label(text);
		label.setPadding(new Insets(0, 10, 0, 10));
		label.getStyleClass().add(styleClass);
		return label;
	}

	private HBox createStageHBox(Node... nodes) {
		HBox hBox = new HBox(10);
		hBox.getChildren().addAll(nodes);
		hBox.setAlignment(Pos.CENTER_LEFT);
		return hBox;
	}

	private void addNewStage() {
		if (FINALIZE_CONFIG) {
			return;
		}
		openStagePopup(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddTrialStage.fxml",
				controller -> controller.setNewStageData(stageNameField.getText().trim(), UUT_ID));
	}

	private void addSubStage(String id, String stageName) {
		if (FINALIZE_CONFIG) {
			return;
		}
		openStagePopup(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddTrialStage.fxml",
				controller -> controller.setAddSubStageData(UUT_ID, id, stageName));
	}

	private void editStage1(StageOne stage1) {
		if (FINALIZE_CONFIG) {
			return;
		}
		openStagePopup(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddTrialStage.fxml",
				controller -> controller.setEditStage1Data(stage1, UUT_ID));
	}

	private void editStage(String id, String pId, String stageName, String testtype) {
		if (FINALIZE_CONFIG) {
			return;
		}
		openStagePopup(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddTrialStage.fxml",
				controller -> controller.setEditSubStageData(UUT_ID, id, pId, stageName, testtype));
	}

	private void deleteStage(String id, String name) {
		if (FINALIZE_CONFIG) {
			return;
		}
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to delete this Stage: " + name + "?";

		Notifications.showConfirmationDialog(title, contentText, () -> deleteStageData(id));
	}

	private void deleteStageData(String id) {
		Response response = stageConfig.deleteStageLevelById(id);
		if (response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
			refreshTrialsList();
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	private void openStagePopup(String fxmlPath, Consumer<AddTrialStageController> controllerConsumer) {
		try {
			FXMLLoader addStagePopup = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = addStagePopup.load();

			AddTrialStageController controller = addStagePopup.getController();
			controllerConsumer.accept(controller);
			controller.setMainPageController(this);

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

}
