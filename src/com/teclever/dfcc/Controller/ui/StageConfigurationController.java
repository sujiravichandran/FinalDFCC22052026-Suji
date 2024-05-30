package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.model.SubStage;
import com.teclever.dfcc.utils.Notifications;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class StageConfigurationController {
	private GridPane stageParentGrid = new GridPane();
	private GridPane stageConfigBottomGridPane = new GridPane();
	private HBox stageConfigTopHbox = new HBox(10);
	private HBox stageConfigMidHbox = new HBox(30);

	private ComboBox<String> uut_type_field = new ComboBox<>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String RUN_CONFIG_ID;

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

	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private TestMapingController testMapingController = new TestMapingController();
	private StageConfiguration stageConfig = new StageConfiguration();

	public StageConfigurationController() {
		initializeUUTTypeComboBox();
		List<SessionMasterDTO> sessionTypeList = stageConfig.getSessionMasterList();
		initializeSessionTypeMap(sessionTypeList);
	}

	public void refreshStageList() {
		fetchStageData();
	}

	public GridPane stageConfigParentGrid() {
		stageParentGrid.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/StageConfiguration.css").toExternalForm());

		stageParentGrid.getStyleClass().add("stageConfig-main-container");
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(100);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(5);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(7);
		RowConstraints row3 = new RowConstraints();
		row3.setPercentHeight(88);

		stageParentGrid.getColumnConstraints().addAll(column1);
		stageParentGrid.getRowConstraints().addAll(row1, row2, row3);
		stageParentGrid.setPadding(new Insets(10));
		stageParentGrid.setVgap(5);

		stageParentGrid.add(stageConfigTopContainer(), 0, 0);
		stageParentGrid.add(stageConfigMidContainer(), 0, 1);
		stageParentGrid.add(stageConfigBottomContainer(), 0, 2);

		enablingStage1ListView(false);
		return stageParentGrid;
	}

	private HBox stageConfigTopContainer() {
		Label headerLabel = new Label("STAGE CONFIGURATION");
		headerLabel.getStyleClass().add("stageConfig-header-label");

		stageConfigTopHbox.setAlignment(Pos.CENTER_LEFT);
		stageConfigTopHbox.getChildren().add(headerLabel);
		return stageConfigTopHbox;
	}

	private HBox stageConfigMidContainer() {
		Label uutLabel = createLabel("Select UUT Type:", "stageConfig-comboBox-label");

		stageConfigMidHbox.getChildren().addAll(uutLabel, uut_type_field);
		stageConfigMidHbox.getStyleClass().add("stageConfig-container");
		stageConfigMidHbox.setAlignment(Pos.CENTER);
		return stageConfigMidHbox;
	}

	private void fetchStageData() {
		if (UUT_ID == null) {
			Notifications.showErrorAlert("Please select UUT Type");
		} else {
			session_l1Data = getStage1FromDb(UUT_ID);
			stageConfigBottomGridPane.add(stageConfigTreeView(), 0, 0);
			initializeStage1ListView();
			initializeTreeView();
			initializeListeners();
			setSearchableTextField();
		}
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uut_type_field.setItems(uutTypeList);
		uut_type_field.setOnAction((event) -> {
			UUT_ID = fetchUutId(uut_type_field.getValue());
			stage1List.clear();
			stageConfigBottomGridPane.getChildren()
					.removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 0);
			fetchStageData();
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

	private GridPane stageConfigBottomContainer() {
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);

		stageConfigBottomGridPane.getColumnConstraints().addAll(column1, column2);
		stageConfigBottomGridPane.getRowConstraints().addAll(row1);

		stageConfigBottomGridPane.getStyleClass().add("stageConfig-container");
		stageConfigBottomGridPane.setHgap(10);
		return stageConfigBottomGridPane;
	}

	private VBox stageConfigTreeView() {
		addNewStageButton.setOnAction(e -> addNewStage());

		HBox stage1HBox = new HBox(10, stageNameField, addNewStageButton);
		stage1HBox.setAlignment(Pos.CENTER_LEFT);

		VBox treeVBox = new VBox(10, stage1HBox, stage1_listView, sessionTreeView);
		treeVBox.getStyleClass().add("stageConfig-bottom-view-container");
		treeVBox.setPadding(new Insets(10));

		return treeVBox;
	}

	private void initializeStage1ListView() {
		session_l1Data.forEach(sessionStage -> stage1List.add(sessionStage.getL1_name()));
		stage1_listView.setItems(stage1List);
		enablingStage1ListView(false);
	}

	private void enablingStage1ListView(boolean isVisible) {
		stage1_listView.setVisible(isVisible);
		stage1_listView.setManaged(isVisible);
		sessionTreeView.setPrefHeight(isVisible ? 550 : 800);
	}

	private void setSearchableTextField() {
		FilteredList<StageOne> filteredData = new FilteredList<>(session_l1Data, p -> true);
		SortedList<StageOne> sortedList = new SortedList<>(filteredData);

		stageNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(sessionStage -> {
				boolean isEmpty = (newValue == null || newValue.isEmpty());
				boolean containsText = sessionStage.getL1_name().toLowerCase().contains(newValue.toLowerCase());

				addNewStageButton.setDisable(!filteredData.isEmpty());
				enablingStage1ListView(!filteredData.isEmpty());

				return isEmpty || containsText;
			});

			ObservableList<String> stringList = FXCollections.observableArrayList();
			sortedList.forEach(sessionStage -> stringList.add(sessionStage.getL1_name()));
			stage1_listView.setItems(stringList);
		});
	}

	private void initializeSessionTypeMap(List<SessionMasterDTO> sessionTypeList) {
		for (SessionMasterDTO session : sessionTypeList) {
			sessionTypeMap.put(session.getSessionMasterId(), session.getSessionTypeName());
		}
	}

	private void initializeListeners() {
		stageNameField.focusedProperty().addListener((obs, oldValue, newValue) -> enablingStage1ListView(newValue));
		stage1_listView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				stageNameField.setText(newValue);
				enablingStage1ListView(false);
			}
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
				session_l2Data = getSubStagesFromDb(stageL1.getId(), stageL1.isDefault());
				createTreeItemsForL2(stageL1, sessionItem, session_l2Data);
			}
		}

		sessionTreeView.setRoot(root);
		sessionTreeView.getStyleClass().add("tree-view");
		sessionTreeView.setShowRoot(false);
		sessionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			System.out.println("PARENT CHECK: "+newValue.getValue().getId());
			if (newValue != null) {
				String stageId = newValue.getValue().getId();
				Node selectedNode = newValue.getValue();
				Map<String, String> testInfoMap = extractTestTypeInfoFromNode(selectedNode);
				if (testInfoMap != null) {
					String testTypeID = testInfoMap.get("testTypeName");
					RUN_CONFIG_ID = fetchRunConfigID(testTypeID);

					if (RUN_CONFIG_ID != null) {
						ObservableList<Node> children = stageConfigBottomGridPane.getChildren();
						children.removeIf(
								node -> GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 1);
						stageConfigBottomGridPane.add(testMapingController.TestMappingView(RUN_CONFIG_ID, stageId), 1,
								0);
					} else {
						stageConfigBottomGridPane.getChildren().removeIf(
								node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 0);

						Label warningLabel = createLabel("Please add test files for selected test type",
								"warning-label");
						HBox warningBox = new HBox(10, warningLabel);
						warningBox.setAlignment(Pos.CENTER);
						stageConfigBottomGridPane.add(warningBox, 1, 0);
					}

				}
			}
		});
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

	private String fetchTestTypeNameById(String testTypeId) {
		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestTypeId().equals(testTypeId)) {
				return testType.getTestName();
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

		Button addBtn = createImageButton("/Resources/Images/AddIcon.png", "ADD",
				event -> addSubStage(stage1.getId(), stage1.getL1_name()));
		Button editBtn = createImageButton("/Resources/Images/Edit.png", "Edit", event -> editStage1(stage1));
		Button delBtn = createImageButton("/Resources/Images/delete.png", "DEL", event -> deleteStage(stage1.getId()));

		HBox buttonsContainer = new HBox(10);
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
		buttonsContainer.setVisible(false);

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

	private GridPane createSubStagesUI(SubStage stage, TreeItem<Node> parentItem) {

		Label stageLabel = createLabel(stage.getL_name(), "stage-label");

		Button addBtn = createImageButton("/Resources/Images/AddIcon.png", "ADD",
				event -> addSubStage(stage.getId(), stage.getL_name()));
		Button delBtn = createImageButton("/Resources/Images/delete.png", "DEL", event -> deleteStage(stage.getId()));
		Button editBtn = createImageButton("/Resources/Images/Edit.png", "Edit", event -> {
			String testTypeName = fetchTestTypeNameById(stage.getTestType());
			editStage(stage.getId(), stage.getpId(), stage.getL_name(), testTypeName);
		});

		HBox buttonsContainer = new HBox(10);
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		buttonsContainer.setPadding(new Insets(0, 10, 0, 0));
		buttonsContainer.setVisible(false);
		if (stage.isParentDefault()) {
			buttonsContainer.getChildren().addAll(addBtn);
		} else if (stage.getId().startsWith("L5")) {
			buttonsContainer.getChildren().addAll(editBtn, delBtn);
		} else {
			buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
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
		System.out.println("iiiiiiiiii" + gridPane.getId());
		gridPane.getChildren()
				.removeIf(node -> node instanceof HBox && ((HBox) node).getChildren().contains(stageField));
		gridPane.add(stageHBox, 0, 0);
	}

	private void createTreeItemsForL2(StageOne stageL1, TreeItem<Node> parentItem, ObservableList<SubStage> s2) {
		for (SubStage stageL2 : s2) {
			if (stageL2.getpId().equals(stageL1.getId())) {
				GridPane gridPaneL2 = createSubStagesUI(stageL2, parentItem);

				TreeItem<Node> stageItemL2 = new TreeItem<>(gridPaneL2);
				parentItem.getChildren().add(stageItemL2);
				if (stageL2.isHasNext()) {
					session_l3Data = getSubStagesFromDb(stageL2.getId(), stageL2.isParentDefault());
					createTreeItemsForL3(stageL2, stageItemL2, session_l3Data);
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
				TreeItem<Node> stageItemL3 = new TreeItem<>(gridPaneL3);
				parentItem.getChildren().add(stageItemL3);

				if (stageL3.isHasNext()) {
					session_l4Data = getSubStagesFromDb(stageL3.getId(), stageL3.isParentDefault());
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
				TreeItem<Node> stageItemL4 = new TreeItem<>(gridPaneL4);
				parentItem.getChildren().add(stageItemL4);

				if (stageL4.isHasNext()) {
					session_l5Data = getSubStagesFromDb(stageL4.getId(), stageL4.isParentDefault());
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
				TreeItem<Node> stageItemL5 = new TreeItem<>(gridPaneL5);
				parentItem.getChildren().add(stageItemL5);
				createStageFieldUI(stageL5, gridPaneL5);
			}
		}
	}

	private void openStagePopup(String fxmlPath, Consumer<AddStageController> controllerConsumer) {
		try {
			FXMLLoader addStagePopup = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = addStagePopup.load();

			AddStageController controller = addStagePopup.getController();
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

	private void addNewStage() {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setNewStageData(stageNameField.getText(), UUT_ID));
	}

	private void addSubStage(String id, String stageName) {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setAddSubStageData(UUT_ID, id, stageName));
//		sessionTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
	}

	private void editStage1(StageOne stage1) {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setEditStage1Data(stage1, UUT_ID));
	}

	private void editStage(String id, String pId, String stageName, String testtype) {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setEditSubStageData(UUT_ID, id, pId, stageName, testtype));
	}

	private void deleteStage(String id) {
		Response response = stageConfig.deleteStageLevelById(id);
		if (response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
			refreshStageList();
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	private ObservableList<StageOne> getStage1FromDb(String uutId) {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = stageConfig.getLevelOneStageMaster(uutId);

		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
				StageOne stage = new StageOne();
				stage.setL1_name(levelOneDto.getStageName());
				stage.setId(levelOneDto.getLevelOneId());
				stage.setDefault(levelOneDto.isDefaultStatus());
				stage.setHasNext("Y".equals(levelOneDto.getNextLevel()));
				List<String> sessionIdsList = Arrays.asList(levelOneDto.getSessionIds().split(","));
				stage.setSessionType(new ArrayList<>(sessionIdsList));

				stageList.add(stage);
			}
		}
		return stageList;
	}

	private ObservableList<SubStage> getSubStagesFromDb(String parentId, boolean isDefault) {
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
				subStageList.add(stage);
			}
		}

		return subStageList;
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

}
