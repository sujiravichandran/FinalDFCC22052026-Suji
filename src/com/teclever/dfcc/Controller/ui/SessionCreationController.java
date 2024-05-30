package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.FaultCodeList;
import com.teclever.dfcc.model.SessionDetails;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.model.SubStage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SessionCreationController {
	private GridPane sessionCreationParentGridPane = new GridPane();
	private HBox sessionCreationMiddleBox = new HBox(10);
	private Button createButton = new Button("OPEN SESSION");
	private Button cancelButton = new Button("CANCEL");

	// leftBox
	private TextField sessionNameField = new TextField();
	private Label startRemarksLabel = new Label("Start Remarks");
	private TextArea startRemarksTextArea = new TextArea();
	private GridPane sessionEntryGridPane = new GridPane();
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private TextField dfccSNoField = new TextField();
	private TextField dfccPartNoField = new TextField();
	private Button addButton = new Button("+");

	// middleBox
	private GridPane sessionTypeGridPane = new GridPane();
	private ComboBox<String> sessionTypeField = new ComboBox<>();
	private Label selectStageLabel = new Label("Select Stages");
	private TreeView<String> treeView = new TreeView<>();

	private VBox leftContainer = new VBox(10);
	private VBox middleContainer = new VBox(10);
	private VBox rightContainer = new VBox(10);
	private String ROLE_ID;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ObservableList<SessionMasterDTO> sessionDataList;
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private String SESSION_TYPE_ID;

	private TableView<SessionDetails> sessionDetailsTableView = new TableView<>();
	private TableColumn<SessionDetails, String> sessionNameColumn = new TableColumn<>("Session Name");
	private TableColumn<SessionDetails, String> dateColumn = new TableColumn<>("Date");

	// FAULT CODE TABLE
	private TableView<FaultCodeList> faultCodeTableView = new TableView<>();
	private TableColumn<FaultCodeList, Boolean> selectedColumn = new TableColumn<>();
	private TableColumn<FaultCodeList, Integer> codeColumn = new TableColumn<>("Code");
	private TableColumn<FaultCodeList, String> descriptionColumn = new TableColumn<>("Description");
	private List<String> selectedFaultCodeList = new ArrayList<>();

	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private StageConfiguration stageConfig = new StageConfiguration();
	private FaultCodeConfiguration faultCodeConfig = new FaultCodeConfiguration();
	private SessionManagement sessionManagement =new SessionManagement();

	private ObservableList<StageOne> session_l1Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l2Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l3Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l4Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l5Data = FXCollections.observableArrayList();

	private final ObservableList<SessionDetails> sessionData = FXCollections.observableArrayList(
			new SessionDetails("session78", "2/9/2023"), new SessionDetails("session12", "5/10/2023"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session5", "30/01/2024"));
	private double originalTextAreaHeight = 0;

	public SessionCreationController() {
//		sessionNameField.setFocusTraversable(false);
		this.ROLE_ID = UserData.getRoleId();
		if (ROLE_ID.equals("RL_ID_4")) {
			middleContainer.setVisible(false);
			middleContainer.setManaged(false);
		}

		initializeUUTTypeComboBox();
		initializeSessionTypeComboBox();
		initializeSessionNameTableView();
		initializeSearchFunctionality();
		rightContainer.setDisable(true);
		enablingFunction(true);

	}

	private ObservableList<StageOne> getStageOneFromDb(String sessionTypeId) {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = sessionManagement.getLevelOneStageMasterBySessionId(sessionTypeId);

		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
				System.out.println("CHECK::: " + levelOneDto.getStageName());
				StageOne stage = new StageOne();
				stage.setL1_name(levelOneDto.getStageName());
				stage.setId(levelOneDto.getLevelOneId());
				stageList.add(stage);
			}
		}
		return stageList;
	}
	private ObservableList<SubStage> getSubStagesFromDb(String parentId) {
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
				subStageList.add(stage);
			}
		}
		return subStageList;
	}

	private TreeView<String> createTreeViewWithCheckBoxes(ObservableList<StageOne> stageList) {
	    CustomCheckBoxTreeItem<String> rootItem = new CustomCheckBoxTreeItem<>("Stages", null);
	    rootItem.setExpanded(true);

	    Map<String, SubStage> subStageMap = new HashMap<>();  

	    for (StageOne stage : stageList) {
	        CustomCheckBoxTreeItem<String> item = new CustomCheckBoxTreeItem<>(stage.getL1_name(), stage.getId());
	        rootItem.getChildren().add(item);
	        addSubStages(item, stage.getId(), subStageMap);
	    }

	    TreeView<String> treeView = new TreeView<>(rootItem);
	    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
	    treeView.setShowRoot(false);

	    return treeView;
	}

	private void addSubStages(CustomCheckBoxTreeItem<String> parentItem, String parentId, Map<String, SubStage> subStageMap) {
	    ObservableList<SubStage> subStages = getSubStagesFromDb(parentId);
	    for (SubStage subStage : subStages) {
	        CustomCheckBoxTreeItem<String> subItem = new CustomCheckBoxTreeItem<>(subStage.getL_name(), parentId);
	        parentItem.getChildren().add(subItem);
	        subStageMap.put(subItem.getValue(), subStage); 

	        subItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
	            @Override
	            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	                if (newValue) {
	                    System.out.println("Selected SubStage ID: " + subStage.getpId() + ";;" + subStage.getId() + "----" + subItem.getParentId());
	                }
	            }
	        });

	        if (subStage.isHasNext()) {
	            addSubStages(subItem, subStage.getId(), subStageMap);
	        }
	    }
	}
	// TABLEVIEW- SESSIONNAME(LEFT CONTAINER)
	private void initializeSessionNameTableView() {
		sessionNameColumn.setCellValueFactory(new PropertyValueFactory<>("sessionName"));
		sessionNameColumn.setReorderable(false);
		sessionNameColumn.setStyle("-fx-alignment: CENTER;");

		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		dateColumn.setReorderable(false);
		dateColumn.setStyle("-fx-alignment: CENTER;");

		sessionDetailsTableView.getColumns().add(sessionNameColumn);
		sessionDetailsTableView.getColumns().add(dateColumn);
		sessionDetailsTableView.setItems(sessionData);
		sessionDetailsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		sessionDetailsTableView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						sessionNameField.setText(newSelection.getSessionName());
						enablingSessionTable(false);
						enablingFunction(false);
					}
				});
	}

	// ENABLING SESSION NAME TABLE WITH STARTREMARKS TEXTAREA HEIGHT
	// ADJUSTMENT(RIGHT CONTAINER)
	private void enablingSessionTable(boolean value) {
		sessionDetailsTableView.setVisible(value);
		sessionDetailsTableView.setManaged(value);
		adjustStartRemarksTextAreaHeight(value);
	}

	private void initializeSearchFunctionality() {
		FilteredList<SessionDetails> filteredList = new FilteredList<>(sessionData, p -> true);
		SortedList<SessionDetails> sortedList = new SortedList<>(filteredList);
		sessionNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(sessionDetail -> {
				String lowerCaseFilter = newValue.toLowerCase();
				return sessionDetail.getSessionName().toLowerCase().contains(lowerCaseFilter);
			});
			sortedList.comparatorProperty().bind(sessionDetailsTableView.comparatorProperty());
			sessionDetailsTableView.setItems(sortedList);
			if (filteredList.isEmpty()) {
				enablingSessionTable(false);
				addButton.setDisable(false);
				createButton.setText("Create Session");
				enablingFunction(true);
			} else {
				enablingSessionTable(true);
				addButton.setDisable(true);
				createButton.setText("Open Session");
			}
		});
	}

	private void enablingFunction(boolean value) {
		sessionEntryGridPane.setDisable(value);
		startRemarksLabel.setDisable(value);
		startRemarksTextArea.setDisable(value);
		middleContainer.setDisable(value);
	}

	public GridPane createSession() {
		sessionCreationParentGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/SessionCreation.css").toExternalForm());
		sessionCreationParentGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(80);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(10);

		sessionCreationParentGridPane.getColumnConstraints().addAll(firstColumn);
		sessionCreationParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		sessionCreationParentGridPane.add(createSessionTitle(), 0, 0);
		sessionCreationParentGridPane.add(createSessionDetails(), 0, 1);
		sessionCreationParentGridPane.add(createSessionButtonBox(), 0, 2);
		sessionCreationParentGridPane.getStyleClass().add("session-creation-main-container");
		return sessionCreationParentGridPane;
	}

	private HBox createSessionTitle() {
		Label headerLabel = new Label("SESSION DETAILS");
		headerLabel.getStyleClass().add("session-creation-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createSessionButtonBox() {
		createButton.setOnAction(e -> {
//			System.out.println("FAULT CODE: " + selectedFaultCodeList);
			StackPane parent1 = (StackPane) sessionCreationParentGridPane.getParent();
			parent1.getChildren().clear();

			UserDashboardController userDashboardController = new UserDashboardController();
			parent1.getChildren().add(userDashboardController.createUserDashboard());
		});

		cancelButton.setOnAction(e -> {
			Stage stage = (Stage) sessionCreationParentGridPane.getScene().getWindow();
			stage.close();
		});

		HBox buttonHbox = new HBox(20);
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.getChildren().addAll(createButton, cancelButton);
		return buttonHbox;
	}

	private HBox createSessionDetails() {
		leftContainer.prefWidthProperty().bind(sessionCreationMiddleBox.widthProperty().divide(3));
		middleContainer.prefWidthProperty().bind(sessionCreationMiddleBox.widthProperty().divide(3));
		rightContainer.prefWidthProperty().bind(sessionCreationMiddleBox.widthProperty().divide(3));

		sessionCreationMiddleBox.setAlignment(Pos.CENTER);
		sessionCreationMiddleBox.getChildren().addAll(createLeftContainer(), createMiddleContainer(),
				createRightContainer());
		return sessionCreationMiddleBox;
	}

	private VBox createLeftContainer() {
		Label sessionNameLabel = new Label("Session Name");
		sessionNameLabel.getStyleClass().add("field-label");
		addButton.setOnAction(e -> onSessionNameAdd());

		GridPane sessionNameFieldBox = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(92);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(8);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);
		sessionNameFieldBox.getColumnConstraints().addAll(column1, column2);
		sessionNameFieldBox.getRowConstraints().addAll(row1);
		sessionNameFieldBox.add(sessionNameField, 0, 0);
		sessionNameFieldBox.add(addButton, 1, 0);
		sessionNameFieldBox.setHgap(10);

		Label uutTypeLabel = new Label("UUT Type");
		Label dfccSNoLabel = new Label("DFCC Serial No");
		Label dfccPartNoLabel = new Label("DFCC Part No");

		uutTypeLabel.getStyleClass().add("field-label");
		dfccSNoLabel.getStyleClass().add("field-label");
		dfccPartNoLabel.getStyleClass().add("field-label");
		uutTypeField.prefWidthProperty().bind(sessionEntryGridPane.widthProperty());
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(33);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(33);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);
		sessionEntryGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sessionEntryGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		sessionEntryGridPane.add(uutTypeLabel, 0, 0);
		sessionEntryGridPane.add(dfccSNoLabel, 0, 1);
		sessionEntryGridPane.add(dfccPartNoLabel, 0, 2);
		sessionEntryGridPane.add(uutTypeField, 1, 0);
		sessionEntryGridPane.add(dfccSNoField, 1, 1);
		sessionEntryGridPane.add(dfccPartNoField, 1, 2);
		sessionEntryGridPane.setVgap(10);

		startRemarksLabel.getStyleClass().add("field-label");

		leftContainer.getChildren().addAll(sessionNameLabel, sessionNameFieldBox, sessionDetailsTableView,
				sessionEntryGridPane, startRemarksLabel, startRemarksTextArea);
		leftContainer.getStyleClass().add("session-creation-container");
		return leftContainer;
	}

	private void onSessionNameAdd() {
		enablingSessionTable(false);
		enablingFunction(false);
	}

	private VBox createRightContainer() {
		initializeFaultCodeTableView();
		populateFaultCodeTableView();

		Button addFaultCodeButton = new Button("ADD FAULT CODE");
		addFaultCodeButton.prefWidthProperty().bind(rightContainer.widthProperty());

		Label selectedFaultCodeLabel = new Label("Selected Fault Code");
		selectedFaultCodeLabel.getStyleClass().add("field-label");

		TextArea faultCodeTextArea = new TextArea();
		addFaultCodeButton.setOnAction(e -> {
			selectedFaultCodeList.clear();
			StringBuilder selectedFaultCodes = new StringBuilder();
			for (FaultCodeList faultCode : faultCodeTableView.getItems()) {
				if (faultCode.isSelected()) {
					selectedFaultCodes.append(faultCode.getCode()).append(" - ").append(faultCode.getCodeDescription())
							.append("\n");
					selectedFaultCodeList.add(faultCode.getFaultCodeId());
				}
			}
			faultCodeTextArea.setText(selectedFaultCodes.toString());
		});

		rightContainer.getStyleClass().add("session-creation-container");
		rightContainer.getChildren().addAll(faultCodeTableView, addFaultCodeButton, selectedFaultCodeLabel,
				faultCodeTextArea);
		return rightContainer;
	}

	// FETCHING ALL FAULTCODE LIST
	private void populateFaultCodeTableView() {
		ObservableList<FaultCodeList> faultCodeList = FXCollections.observableArrayList();
		FaultCodeResponse response = faultCodeConfig.getFaultCodeList();
		if (response.getResponse().getResponseCode() == 1) {
			for (FaultCodeDTO faultCodeDto : response.getFaultCodeList()) {
				FaultCodeList faultCode = new FaultCodeList();
				faultCode.setCode(faultCodeDto.getFaultCode());
				faultCode.setFaultCodeId(faultCodeDto.getFaultCodeMasterId());
				faultCode.setCodeDescription(faultCodeDto.getFaultCodeDescription());
				faultCodeList.add(faultCode);
			}
		}
		faultCodeTableView.setItems(faultCodeList);
	}

	// TABLEVIEW- FAULTCODE(RIGHT CONTAINER)
	private void initializeFaultCodeTableView() {
		selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
		selectedColumn.setReorderable(false);
		selectedColumn.setStyle("-fx-alignment: CENTER;");
		selectedColumn
				.setCellFactory(new Callback<TableColumn<FaultCodeList, Boolean>, TableCell<FaultCodeList, Boolean>>() {
					@Override
					public TableCell<FaultCodeList, Boolean> call(TableColumn<FaultCodeList, Boolean> param) {
						return new TableCell<FaultCodeList, Boolean>() {
							private final CheckBox checkBox = new CheckBox();

							@Override
							protected void updateItem(Boolean item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
								} else {
									checkBox.setSelected(item);
									checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
										FaultCodeList faultCode = getTableView().getItems().get(getIndex());
										faultCode.setSelected(isNowSelected);
									});
									setGraphic(checkBox);
								}
							}
						};
					}
				});

		codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
		codeColumn.setReorderable(false);
		codeColumn.setStyle("-fx-alignment: CENTER;");

		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("codeDescription"));
		descriptionColumn.setReorderable(false);
		descriptionColumn.setStyle("-fx-alignment: CENTER-LEFT;");

		faultCodeTableView.getColumns().add(selectedColumn);
		faultCodeTableView.getColumns().add(codeColumn);
		faultCodeTableView.getColumns().add(descriptionColumn);
		faultCodeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private VBox createMiddleContainer() {
		Label sessionTypeLabel = new Label("Session Type");
		sessionTypeLabel.getStyleClass().add("field-label");
		sessionTypeField.prefWidthProperty().bind(sessionTypeGridPane.widthProperty());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		sessionTypeGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sessionTypeGridPane.getRowConstraints().addAll(firstRow);
		sessionTypeGridPane.add(sessionTypeLabel, 0, 0);
		sessionTypeGridPane.add(sessionTypeField, 1, 0);

		selectStageLabel.getStyleClass().add("field-label");
		treeView = createTreeViewWithCheckBoxes(session_l1Data);
		treeView.prefHeightProperty().bind(middleContainer.heightProperty());

		middleContainer.getStyleClass().add("session-creation-container");
		middleContainer.getChildren().addAll(sessionTypeGridPane, selectStageLabel, treeView);
		return middleContainer;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);
		uutTypeField.setOnAction((event) -> UUT_ID = fetchUutId(uutTypeField.getValue()));
	}

	private void initializeSessionTypeComboBox() {
		sessionDataList = FXCollections.observableArrayList(stageConfig.getSessionMasterList());
		for (SessionMasterDTO sessionType : sessionDataList) {
			sessionTypeList.add(sessionType.getSessionTypeName());
		}
		sessionTypeField.setItems(sessionTypeList);
		sessionTypeField.setOnAction((event) -> {
			SESSION_TYPE_ID = fetchSessionTypeId(sessionTypeField.getValue());
			session_l1Data = getStageOneFromDb(SESSION_TYPE_ID);
			
			treeView = createTreeViewWithCheckBoxes(session_l1Data);
	        treeView.prefHeightProperty().bind(middleContainer.heightProperty());
	        middleContainer.getChildren().set(2, treeView);
	        
			System.out.println("S1: " + session_l1Data.toString());
			if (SESSION_TYPE_ID.equals("ST2")) {
				rightContainer.setDisable(false);
			} else {
				rightContainer.setDisable(true);
			}

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

	private String fetchSessionTypeId(String sessionType) {
		for (SessionMasterDTO type : sessionDataList) {
			if (type.getSessionTypeName().equals(sessionType)) {
				return type.getSessionMasterId();
			}
		}
		return null;
	}

	private void adjustStartRemarksTextAreaHeight(boolean tableVisible) {
		if (!tableVisible) {
			double totalHeight = leftContainer.getHeight();
			double otherComponentsHeight = 0;
			for (Node node : leftContainer.getChildren()) {
				if (node != startRemarksTextArea) {
					otherComponentsHeight += node.getBoundsInParent().getHeight();
				}
			}

			startRemarksTextArea.setPrefHeight(totalHeight + otherComponentsHeight);
		} else {
			if (originalTextAreaHeight == 0) {
				originalTextAreaHeight = startRemarksTextArea.getPrefHeight();
			}
			startRemarksTextArea.setPrefHeight(originalTextAreaHeight);
		}
	}
}
class CustomCheckBoxTreeItem<T> extends CheckBoxTreeItem<T> {
    private String parentId;

    public CustomCheckBoxTreeItem(T value, String parentId) {
        super(value);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }
}