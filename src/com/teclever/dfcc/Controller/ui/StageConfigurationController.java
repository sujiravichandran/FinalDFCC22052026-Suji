package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.SessionStage;
import com.teclever.dfcc.model.SessionSubStage;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageConfigurationController {
	private GridPane stageParentGrid = new GridPane();
	private StackPane stageConfigStackPane = new StackPane();
	private HBox stageConfigTopHbox = new HBox(10);
	private HBox stageConfigMidHbox = new HBox(30);
	private GridPane stageConfigBottomGridPane = new GridPane();

	private ComboBox<String> uut_type_field;
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String RUN_CONFIG_ID;

	private ComboBox<String> user_type_field;
	private ObservableList<UUTMasterDetailsDto> userDataList;
//	private ObservableList<String> userTypeList = FXCollections.observableArrayList();
	private String[] userTypeList = { "BEL USER", "SQUADRON USER" };
	private String USER_ROLE_ID;

	private TextField stageNameField = new TextField();
	private Button addNewStageButton = new Button("Add New Stage");
	private ListView<String> stage1_listView = new ListView<>();
	private TreeView<Node> sessionTreeView = new TreeView<>();
	private ObservableList<SessionStage> session_l1Data = FXCollections.observableArrayList();
	private ObservableList<SessionSubStage> session_l2Data = FXCollections.observableArrayList();
	private ObservableList<SessionSubStage> session_l3Data = FXCollections.observableArrayList();
	private ObservableList<SessionSubStage> session_l4Data = FXCollections.observableArrayList();
	private ObservableList<SessionSubStage> session_l5Data = FXCollections.observableArrayList();

	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private StageConfiguration stageConfig = new StageConfiguration();
	private TestMapingController testMapingController = new TestMapingController();
	private Notifications notify;

	public StageConfigurationController() {
		uut_type_field = new ComboBox<>();
		initializeUUTTypeComboBox();

		user_type_field = new ComboBox<>();
		initializeUserTypeComboBox();

		stageConfigBottomGridPane.setDisable(true);
	}

	public void refreshStageList() {
		onClickGETButton();
	}

	public GridPane stageConfigParentGrid() {
		stageParentGrid.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/StageConfiguration.css").toExternalForm());

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(100);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(7);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(7);
		RowConstraints row3 = new RowConstraints();
		row3.setPercentHeight(86);

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
		Label uutLabel = new Label("Select UUT Type:");
		uutLabel.getStyleClass().add("stageConfig-comboBox-label");

		Label userLabel = new Label("Select User Type:");
		userLabel.getStyleClass().add("stageConfig-comboBox-label");

		Button getButton = new Button("GET");
		getButton.setOnAction(e -> onClickGETButton());

		stageConfigMidHbox.getChildren().addAll(uutLabel, uut_type_field, userLabel, user_type_field, getButton);
		stageConfigMidHbox.getStyleClass().add("stageConfig-container");
		stageConfigMidHbox.setAlignment(Pos.CENTER);
		return stageConfigMidHbox;
	}

	private void onClickGETButton() {
		if (UUT_ID == null) {
			notify.showErrorAlert("Please select UUT Type");
		} else if (USER_ROLE_ID == null) {
			notify.showErrorAlert("Please select User Type");
		} else {
			session_l1Data = getStage1FromDb(UUT_ID, USER_ROLE_ID);

			stageConfigBottomGridPane.setDisable(false);
			initializeStage1ListView();
			initializeTreeView();
			initializeListeners();
			setSearchableTextField();
		}
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uut_type_field.setItems(uutTypeList);
		uut_type_field.setOnAction((event) -> UUT_ID = fetchUutId(uut_type_field.getValue()));
	}

	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	private void initializeUserTypeComboBox() {
		user_type_field.getItems().addAll(userTypeList);
		user_type_field.setOnAction((event) -> USER_ROLE_ID = fetchUserRoleId(user_type_field.getValue()));
	}

	private String fetchUserRoleId(String userType) {
		switch (userType) {
		case "BEL USER":
			return "RL_ID_2";
		case "SQUADRON USER":
			return "RL_ID_4";
		default:
			return "";
		}
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

		stageConfigBottomGridPane.add(stageConfigTreeView(), 0, 0);
//		stageConfigBottomGridPane.add(testMapingController.TestMappingView("null","null"), 1, 0);

		stageConfigBottomGridPane.getStyleClass().add("stageConfig-container");
		stageConfigBottomGridPane.setHgap(10);

		return stageConfigBottomGridPane;
	}

	private VBox stageConfigTreeView() {
		VBox treeVBox = new VBox(10);
		HBox stage1HBox = new HBox(10);

		addNewStageButton.setOnAction(e -> addNewStage());

		stage1HBox.getChildren().addAll(stageNameField, addNewStageButton);
		stage1HBox.setAlignment(Pos.CENTER_LEFT);

		treeVBox.getChildren().addAll(stage1HBox, stage1_listView, sessionTreeView);
		treeVBox.getStyleClass().add("stageConfig-bottom-view-container");
		treeVBox.setPadding(new Insets(10));
		return treeVBox;
	}

	private void initializeStage1ListView() {
		ObservableList<String> stage1List = FXCollections.observableArrayList();
		for (SessionStage sessionStage : session_l1Data) {
			stage1List.add(sessionStage.getL1_name());
		}
		stage1_listView.setItems(stage1List);
		enablingStage1ListView(false);
	}

	private void enablingStage1ListView(boolean value) {
		stage1_listView.setVisible(value);
		stage1_listView.setManaged(value);
		if (value) {
			stage1_listView.setPrefHeight(250);
			sessionTreeView.setPrefHeight(550);
		} else {
			sessionTreeView.setPrefHeight(800);
		}
	}

	private void setSearchableTextField() {
		FilteredList<SessionStage> filteredData = new FilteredList<>(session_l1Data, p -> true);
		SortedList<SessionStage> sortedList = new SortedList<>(filteredData);

		stageNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(sessionStage -> {
				if (newValue == null || newValue.isEmpty()) {
					addNewStageButton.setDisable(false);
					return true;
				}
				if (sessionStage.getL1_name().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				}

				return false;
			});
			if (filteredData.isEmpty()) {
				addNewStageButton.setDisable(false);
				enablingStage1ListView(false);
			} else {
				addNewStageButton.setDisable(true);
				enablingStage1ListView(true);
			}
			ObservableList<String> stringList = FXCollections.observableArrayList();
			sortedList.forEach(sessionStage -> stringList.add(sessionStage.getL1_name()));

			stage1_listView.setItems(stringList);

		});
	}

	private void initializeListeners() {
		stageNameField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				enablingStage1ListView(true);
			}
		});

		stage1_listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				stageNameField.setText(newValue);
				enablingStage1ListView(false);
			}
		});
	}

	private void initializeTreeView() {
		TreeItem<Node> root = new TreeItem<>(new Label("Sessions"));
		root.setExpanded(true);

		for (SessionStage stageL1 : session_l1Data) {
			System.out.println("S!1 : " + stageL1.getId());
			GridPane level1_container = createL1UI(stageL1);
			TreeItem<Node> sessionItem = new TreeItem<>(level1_container);
			root.getChildren().add(sessionItem);
			if (stageL1.isHasNext()) {
				session_l2Data = getStagesFromDb(stageL1.getId());
				createL2TreeItems(stageL1, sessionItem, session_l2Data);
			}
		}

		sessionTreeView.setRoot(root);
		sessionTreeView.getStyleClass().add("tree-view");
		sessionTreeView.setShowRoot(false);
		sessionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			if (newValue != null) {
				Node selectedNode = newValue.getValue();
				Map<String, String> testInfoMap = extractTestTypeInfoFromNode(selectedNode);

				if (testInfoMap != null) {
					String testTypeName = testInfoMap.get("testTypeName");
					String stageId = testInfoMap.get("stageId");
					String testTypeID = fetchTestTypeIdByName(testTypeName);
					RUN_CONFIG_ID = fetchRunConfigID(testTypeID);
					if (RUN_CONFIG_ID != null) {
						System.out.println("Run Configuration ID: " + RUN_CONFIG_ID);
						stageConfigBottomGridPane.setDisable(false);
//		                stageConfigBottomGridPane.getChildren().remove(testMapingController.TestMappingView("null","null"));
						stageConfigBottomGridPane.add(
								testMapingController.TestMappingView(UUT_ID, RUN_CONFIG_ID, testTypeID, stageId), 1, 0);
					} else {
						System.out.println("No matching run config found for UUT ID: " + UUT_ID + " and Test Type ID: "
								+ testTypeID);
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
		List<com.teclever.dfcc.datastore.dto.RunConfigurationDto> allRunConfigData = runConfig.getAllRunConfig();
		String runConfigId = null;
		for (com.teclever.dfcc.datastore.dto.RunConfigurationDto runConfigDto : allRunConfigData) {
			if (runConfigDto.getUutId().equals(UUT_ID) && runConfigDto.getTestTypeId().equals(testTypeID)) {
				runConfigId = runConfigDto.getRunConfigId();
				break;
			}
		}
		return runConfigId;
	}

	private String fetchTestTypeIdByName(String testTypeName) {
		ObservableList<TestTypeMasterDetailsDto> testTypeDataList = FXCollections
				.observableArrayList(runConfig.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestName().equals(testTypeName)) {
				return testType.getTestTypeId();
			}
		}
		return null;
	}

	private Map<String, String> extractTestTypeInfoFromNode(Node node) {
		Map<String, String> testInfoMap = new HashMap<>();

		if (node instanceof GridPane) {
			GridPane gridPane = (GridPane) node;
			for (Node child : gridPane.getChildren()) {
				if (child instanceof HBox) {
					HBox hBox = (HBox) child;
					testInfoMap.put("stageId", hBox.getId());
					for (Node hBoxChild : hBox.getChildren()) {
						if (hBoxChild instanceof Label && hBoxChild.getStyleClass().contains("label_field")) {
							Label label = (Label) hBoxChild;
							testInfoMap.put("testTypeName", label.getText());
						}
//						if (hBoxChild instanceof Label && hBoxChild.getStyleClass().contains("stage-label")) {
//							Label label = (Label) hBoxChild;
//							testInfoMap.put("stageId", label.getText()); 
//						}
					}
				}
			}
		}
		return testInfoMap.isEmpty() ? null : testInfoMap;
	}
//	private String extractTestTypeNameFromNode(Node node) {
//	    if (node instanceof GridPane) {
//	        GridPane gridPane = (GridPane) node;
//	        for (Node child : gridPane.getChildren()) {
//	            if (child instanceof HBox) {
//	                HBox hBox = (HBox) child;
//	                for (Node hBoxChild : hBox.getChildren()) {
//	                    if (hBoxChild instanceof Label && hBoxChild.getStyleClass().contains("label_field")) {
//	                        Label label = (Label) hBoxChild;
//	                        return label.getText();
////	                    	
//	                    }
//	                }
//	            }
//	        }
//	    }
//	    return null;
//	}

	private GridPane createL1UI(SessionStage stage1) {
		Label l1_label = new Label(stage1.getL1_name());
		l1_label.getStyleClass().add("session-stage-l1-label");

		HBox labelContainer = new HBox(20, l1_label);
		labelContainer.setAlignment(Pos.CENTER_LEFT);
		ArrayList<String> sessionTypes = stage1.getSessionType();
		if (!sessionTypes.isEmpty()) {
			HBox sessionTypeHbox = new HBox(10);
			sessionTypeHbox.setAlignment(Pos.CENTER);
			for (String type : sessionTypes) {
				Label typeField = createStageLabel(type, "session-type-label");
				typeField.setPrefWidth(Region.USE_COMPUTED_SIZE);
				typeField.setAlignment(Pos.CENTER);
				sessionTypeHbox.getChildren().add(typeField);
			}
			labelContainer.getChildren().add(sessionTypeHbox);
		}

		GridPane gridPaneL1 = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHalignment(HPos.RIGHT);
		gridPaneL1.getColumnConstraints().addAll(column1, column2);

		HBox buttonsContainer = new HBox(10);
		buttonsContainer.setId(stage1.getId());
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		ImageView addBtn = createImageButton("/Resources/Images/add.jpg", "ADD", event -> {
			System.out.println("ADD button clicked for stage: " + stage1.toString());
			addSubStage(stage1.getId(), stage1.getL1_name());
		});
		ImageView editBtn = createImageButton("/Resources/Images/Edit.png", "Edit", event -> {
			System.out.println("Edit button clicked for stage: " + stage1.toString());
			editStage1(stage1);
		});
		ImageView delBtn = createImageButton("/Resources/Images/delete.png", "DEL", event -> {
			System.out.println("DEL button clicked for stage: " + stage1.toString());
			deleteStage(stage1.getId());
		});

		buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
		buttonsContainer.setVisible(false);

		gridPaneL1.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
			buttonsContainer.setVisible(true);
		});

		gridPaneL1.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
			buttonsContainer.setVisible(false);
		});

		gridPaneL1.add(labelContainer, 0, 0);
		gridPaneL1.add(buttonsContainer, 1, 0);

		return gridPaneL1;
	}

	private GridPane createStageUI(SessionSubStage stage, GridPane gridPane, HBox buttonsContainer,
			TreeItem<Node> parentItem) {
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHalignment(HPos.RIGHT);
		gridPane.getColumnConstraints().addAll(column1, column2);

		Label stageLabel = createStageLabel(stage.getL_name(), "stage-label");

		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		ImageView addBtn = createImageButton("/Resources/Images/AddIcon.png", "ADD", event -> {
			System.out.println("ADD button clicked for stage: " + stage.toString());
			addSubStage(stage.getId(), stage.getL_name());
		});
		ImageView editBtn = createImageButton("/Resources/Images/Edit.png", "Edit", event -> {
			System.out.println("Edit button clicked for stage: " + stage.toString());
			String testTypeName = fetchTestTypeNameById(stage.getTestType());
			editStage(stage.getId(), stage.getpId(), stage.getL_name(), testTypeName);
		});
		ImageView delBtn = createImageButton("/Resources/Images/delete.png", "DEL", event -> {
			System.out.println("DEL button clicked for stage: " + stage.toString());
			deleteStage(stage.getId());
		});

		buttonsContainer.getChildren().addAll(addBtn, editBtn, delBtn);
		buttonsContainer.setVisible(false);

		gridPane.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> buttonsContainer.setVisible(true));
		gridPane.addEventFilter(MouseEvent.MOUSE_EXITED, e -> buttonsContainer.setVisible(false));

		gridPane.add(stageLabel, 0, 0);
		gridPane.add(buttonsContainer, 1, 0);
		return gridPane;
	}

	private void createStageFieldUI(SessionSubStage stage, GridPane gridPane, HBox buttonsContainer) {
		System.out.println(stage.getTestType());
		System.out.println("+++++++++++++++++++++" + stage.getId());
		String testTypeName = fetchTestTypeNameById(stage.getTestType());
		Label stageField = createStageLabel(testTypeName, "label_field");
		stageField.setPrefWidth(Region.USE_COMPUTED_SIZE);
		stageField.setAlignment(Pos.CENTER);

		HBox stageHBox = createStageHBox(createStageLabel(stage.getL_name(), "stage-label"), stageField,
				(HBox) gridPane.getChildren().get(1));

		gridPane.add(stageHBox, 0, 0);
		gridPane.add(buttonsContainer, 1, 0);
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

	private void createL2TreeItems(SessionStage stageL1, TreeItem<Node> parentItem,
			ObservableList<SessionSubStage> s2) {
		for (SessionSubStage stageL2 : s2) {
			if (stageL2.getpId().equals(stageL1.getId())) {
				HBox buttonsContainer = new HBox(10);
				buttonsContainer.setId(stageL2.getId());
				GridPane gridPaneL2 = new GridPane();
				gridPaneL2 = createStageUI(stageL2, gridPaneL2, buttonsContainer, parentItem);

				TreeItem<Node> stageItemL2 = new TreeItem<>(gridPaneL2);
				parentItem.getChildren().add(stageItemL2);
				if (stageL2.isHasNext()) {
					session_l3Data = getStagesFromDb(stageL2.getId());
					createL3TreeItems(stageL2, stageItemL2, session_l3Data);
				} else {
					createStageFieldUI(stageL2, gridPaneL2, buttonsContainer);
				}

			}
		}
	}

	private void createL3TreeItems(SessionSubStage stageL2, TreeItem<Node> parentItem,
			ObservableList<SessionSubStage> s3) {
		for (SessionSubStage stageL3 : s3) {
			if (stageL3.getpId().equals(stageL2.getId())) {
				HBox buttonsContainer = new HBox(10);
				buttonsContainer.setId(stageL3.getId());
				GridPane gridPaneL3 = new GridPane();
				gridPaneL3 = createStageUI(stageL3, gridPaneL3, buttonsContainer, parentItem);

				TreeItem<Node> stageItemL3 = new TreeItem<>(gridPaneL3);
				parentItem.getChildren().add(stageItemL3);

				if (stageL3.isHasNext()) {
					session_l4Data = getStagesFromDb(stageL3.getId());
					createL4TreeItems(stageL3, stageItemL3, session_l4Data);
				} else {
					createStageFieldUI(stageL3, gridPaneL3, buttonsContainer);
				}
			}
		}
	}

	private void createL4TreeItems(SessionSubStage stageL3, TreeItem<Node> parentItem,
			ObservableList<SessionSubStage> s4) {
		for (SessionSubStage stageL4 : s4) {
			if (stageL4.getpId().equals(stageL3.getId())) {
				HBox buttonsContainer = new HBox(10);
				buttonsContainer.setId(stageL4.getId());
				GridPane gridPaneL4 = new GridPane();
				gridPaneL4 = createStageUI(stageL4, gridPaneL4, buttonsContainer, parentItem);

				TreeItem<Node> stageItemL4 = new TreeItem<>(gridPaneL4);
				parentItem.getChildren().add(stageItemL4);

				if (stageL4.isHasNext()) {
					session_l5Data = getStagesFromDb(stageL4.getId());
					createL5TreeItems(stageL4, stageItemL4, session_l5Data);
				} else {
					createStageFieldUI(stageL4, gridPaneL4, buttonsContainer);
				}
			}
		}
	}

	private void createL5TreeItems(SessionSubStage stageL4, TreeItem<Node> parentItem,
			ObservableList<SessionSubStage> s5) {
		for (SessionSubStage stageL5 : s5) {
			if (stageL5.getpId().equals(stageL4.getId())) {
				HBox buttonsContainer = new HBox(10);
				buttonsContainer.setId(stageL5.getId());
				GridPane gridPaneL5 = new GridPane();
				gridPaneL5 = createStageUI(stageL5, gridPaneL5, buttonsContainer, parentItem);

				TreeItem<Node> stageItemL5 = new TreeItem<>(gridPaneL5);
				parentItem.getChildren().add(stageItemL5);

				createStageFieldUI(stageL5, gridPaneL5, buttonsContainer);
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
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addNewStage() {
		try {
			FXMLLoader addStagePopup = new FXMLLoader(
					getClass().getResource("/com/teclever/dfcc/ui/fxml/AddStage.fxml"));
			Parent root = addStagePopup.load();
			AddStageController controller = addStagePopup.getController();
			controller.setNewStageData(stageNameField.getText(), UUT_ID, USER_ROLE_ID);
			controller.setMainPageController(this);
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void addSubStage(String id, String stageName) {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setAddSubStageData(UUT_ID, id, stageName));
	}

	private void editStage1(SessionStage stage1) {
		System.out.println("STAGE_1- update: " + stage1.toString());
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setEditStage1Data(stage1, UUT_ID, USER_ROLE_ID));
	}

	private void editStage(String id, String pId, String stageName, String testtype) {
		openStagePopup("/com/teclever/dfcc/ui/fxml/AddStage.fxml",
				controller -> controller.setEditStageData(UUT_ID, id, pId, stageName, testtype));
	}

	private void deleteStage(String id) {
		com.teclever.datastore.dto.Response response = stageConfig.deleteStageLevelById(id);
		System.out.println("DEL--------------"+id);
		if (response.getResponseCode() == 1) {
			notify.showSuccessAlert(response.getResponseMessage());
			refreshStageList();
		} else {
			notify.showErrorAlert(response.getResponseMessage());
		}
	}

	private ObservableList<SessionStage> getStage1FromDb(String uutId, String userType) {
		ObservableList<SessionStage> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = stageConfig.getLevelOneStageMaster(uutId, userType);
		List<LevelOneDto> levelOneResponse = response.getLevelOneResponse();

		if (response.getResponse().getResponseCode() == 1) {

			for (LevelOneDto levelOneDto : levelOneResponse) {
				SessionStage stage = new SessionStage();

				stage.setL1_name(levelOneDto.getStageName());
				stage.setId(levelOneDto.getLevelOneId());

				ArrayList<String> sessionIdsList = new ArrayList<>(
						Arrays.asList(levelOneDto.getSessionIds().split(",")));
				stage.setSessionType(sessionIdsList);

				if (levelOneDto.getNextLevel().equals("Y")) {
					stage.setHasNext(true);
				} else if (levelOneDto.getNextLevel().equals("N")) {
					stage.setHasNext(false);
				}
				stageList.add(stage);
			}
		}
		return stageList;
	}

	private ObservableList<SessionSubStage> getStagesFromDb(String parentId) {
		ObservableList<SessionSubStage> subStageList = FXCollections.observableArrayList();
		StageMasterLevelsResponse response = stageConfig.getStageLevelMaster(parentId);
		List<StageMasterLevelDto> levelTwoResponse = response.getLevelsResponse();

		if (response.getResponse().getResponseCode() == 1) {
			for (StageMasterLevelDto levelDto : levelTwoResponse) {
				SessionSubStage stage = new SessionSubStage();
				stage.setpId(levelDto.getParentId());
				stage.setId(levelDto.getLevelId());
				stage.setL_name(levelDto.getStageName());
				stage.setTestType(levelDto.getTestType());
				stage.setHasNext(false);
				if (levelDto.getNextLevel().equals("Y")) {
					stage.setHasNext(true);
				} else if (levelDto.getNextLevel().equals("N")) {
					stage.setHasNext(false);
				}
				subStageList.add(stage);
			}
		}
		return subStageList;
	}

	private ImageView createImageButton(String imagePath, String tooltipText, EventHandler<ActionEvent> action) {
		Image image = new Image(getClass().getResourceAsStream(imagePath));
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);
		imageView.setOnMouseClicked(e -> action.handle(new ActionEvent()));
		imageView.setStyle("-fx-background-color: white;");
		return imageView;
	}

	private Label createStageLabel(String text, String styleClass) {
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
