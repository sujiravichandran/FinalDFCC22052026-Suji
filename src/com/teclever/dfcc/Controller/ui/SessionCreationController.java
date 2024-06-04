package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionDTOResponse;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.FaultCodeList;
import com.teclever.dfcc.model.SessionDetails;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.model.SubStage;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Notifications;

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
import javafx.scene.control.TreeItem;
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
	private Label headerLabel = new Label("SESSION DETAILS");
	private HBox sessionCreationMiddleBox = new HBox(10);
	private Button createButton = new Button("OPEN SESSION");
	private Button cancelButton = new Button("CANCEL");

	// leftBox
	private TextField sessionNameField = new TextField();
	private TextField dfccSNoField = new TextField();
	private TextField dfccPartNoField = new TextField();
	private Label startRemarksLabel = new Label("Start Remarks");
	private Label sessionNameLabel = new Label("Session Name");
	private Label uutTypeLabel = new Label("UUT Type");
	private Label dfccSNoLabel = new Label("DFCC Serial No");
	private Label dfccPartNoLabel = new Label("DFCC Part No");
	private TextArea startRemarksTextArea = new TextArea();
	private GridPane sessionEntryGridPane = new GridPane();
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private Button addButton = new Button("+");

	// middleBox
	private GridPane sessionTypeGridPane = new GridPane();
	private ComboBox<String> sessionTypeField = new ComboBox<>();
	private Label selectStageLabel = new Label("Select Stages");
	private TreeView<String> treeView = new TreeView<>();
	
	private TreeView<HBox> selectedSessionStageTreeView = new TreeView<>();

	// rightBox
	private Button addFaultCodeButton = new Button("ADD FAULT CODE");
	private Label selectedFaultCodeLabel = new Label("Selected Fault Code");
	private TextArea faultCodeTextArea = new TextArea();

	private VBox leftContainer = new VBox(10);
	private VBox middleContainer = new VBox(10);
	private VBox rightContainer = new VBox(10);
	private String ROLE_ID;
	private String USER_ID;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ObservableList<SessionMasterDTO> sessionDataList;
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private String SESSION_TYPE_ID;

	// SESSION NAME TABLE
	private TableView<SessionDetails> sessionDetailsTableView = new TableView<>();
	private TableColumn<SessionDetails, String> sessionNameColumn = new TableColumn<>("Session Name");
	private TableColumn<SessionDetails, String> dateColumn = new TableColumn<>("Date");
	private ObservableList<SessionDetails> sessionNameDetailsList = FXCollections.observableArrayList();
	private String SESSION_ID;

	// FAULT CODE TABLE
	private TableView<FaultCodeList> faultCodeTableView = new TableView<>();
	private TableColumn<FaultCodeList, Boolean> selectedColumn = new TableColumn<>();
	private TableColumn<FaultCodeList, Integer> codeColumn = new TableColumn<>("Code");
	private TableColumn<FaultCodeList, String> descriptionColumn = new TableColumn<>("Description");
	private List<String> selectedFaultCodeList = new ArrayList<>();

	private UserDashboardController userDashboardController = new UserDashboardController();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private FaultCodeConfiguration faultCodeConfig = new FaultCodeConfiguration();
	private SessionManagement sessionManagement = new SessionManagement();
	private StageConfiguration stageConfig = new StageConfiguration();
	private LoadDriverController loadDriverController = new LoadDriverController();
	private RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();

	private ObservableList<StageOne> session_l1Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l2Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l3Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l4Data = FXCollections.observableArrayList();
	private ObservableList<SubStage> session_l5Data = FXCollections.observableArrayList();

	private double originalTextAreaHeight = 0;
	private List<List<String>> filteredHierarchies;
	private Set<List<String>> selectedHierarchies = new HashSet<>();

	public SessionCreationController() {
		this.ROLE_ID = UserData.getRoleId();
		if (ROLE_ID.equals("RL_ID_4")) {
			middleContainer.setVisible(false);
			middleContainer.setManaged(false);
		}
		this.USER_ID = UserData.getUserId();
		if (USER_ID != null) {
			populateSessionTableView();
		}

		initializeUUTTypeComboBox();
		initializeSessionTypeComboBox();
		initializeSessionNameTableView();
		initializeSearchFunctionality();
		rightContainer.setDisable(true);
		enablingFunction(true);

	}

	public GridPane createSession() {
		sessionCreationParentGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/SessionCreation.css").toExternalForm());
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
		headerLabel.getStyleClass().add("session-creation-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createSessionButtonBox() {
		createButton.setOnAction(e -> {
			if (createButton.getText().equals("Create Session")) {
				saveNewSession();
			}else if (createButton.getText().equals("Open Session")) {
				openExistingSession();
			}
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

	private void openExistingSession() {	
		StateMachine.currentSessionDetails.setUutId(fetchUutId(uutTypeField.getValue()));
		StateMachine.currentSessionDetails.setUutType(uutTypeField.getValue());
		StateMachine.currentSessionDetails.setSessionTypeID(fetchSessionTypeId(sessionTypeField.getValue()));
		StateMachine.currentSessionDetails.setSessionTypeName(sessionTypeField.getValue());
		StateMachine.currentSessionDetails.setSessionId(SESSION_ID);
		StateMachine.currentSessionDetails.setSessionName(sessionNameField.getText());
				
		StackPane parent1 = (StackPane) sessionCreationParentGridPane.getParent();
		parent1.getChildren().clear();
		parent1.getChildren().add(userDashboardController.createUserDashboard());
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
		sessionNameLabel.getStyleClass().add("field-label");
		uutTypeLabel.getStyleClass().add("field-label");
		dfccSNoLabel.getStyleClass().add("field-label");
		dfccPartNoLabel.getStyleClass().add("field-label");
		startRemarksLabel.getStyleClass().add("field-label");

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

		leftContainer.getChildren().addAll(sessionNameLabel, sessionNameFieldBox, sessionDetailsTableView,
				sessionEntryGridPane, startRemarksLabel, startRemarksTextArea);
		leftContainer.getStyleClass().add("session-creation-container");
		return leftContainer;
	}

	private void populateSessionTableView() {
		SessionListResponse response = sessionManagement.getAllSessionData(USER_ID);
		if (response.getResponse().getResponseCode() == 1) {
			for (SessionList sessionDto : response.getListOfSession()) {
				SessionDetails sessionDetails = new SessionDetails();
				sessionDetails.setSessionName(sessionDto.getSessionName());
				sessionDetails.setSessionId(sessionDto.getSessionId());
				sessionDetails.setDate(sessionDto.getCreationDate());
				sessionNameDetailsList.add(sessionDetails);
			}
		}
		sessionDetailsTableView.setItems(sessionNameDetailsList);
	}

	// ENABLING SESSION NAME TABLE WITH STARTREMARKS TEXTAREA HEIGHT
	// ADJUSTMENT(RIGHT CONTAINER)
	private void enablingSessionTable(boolean value) {
		sessionDetailsTableView.setVisible(value);
		sessionDetailsTableView.setManaged(value);
		adjustStartRemarksTextAreaHeight(value);
	}

	private void initializeSearchFunctionality() {
		FilteredList<SessionDetails> filteredList = new FilteredList<>(sessionNameDetailsList, p -> true);
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
		sessionDetailsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		sessionDetailsTableView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						sessionNameField.setText(newSelection.getSessionName());
						SESSION_ID = newSelection.getSessionId();
						enablingSessionTable(false);
						enablingFunction(false);
						if (SESSION_ID != null) {
							retriveSessionDetailsUsingSessionID();
						}
					}
				});
	}

	private void retriveSessionDetailsUsingSessionID() {
		SessionDTOResponse sessionListResponse = sessionManagement.getSessionDetailById(SESSION_ID);
		if (sessionListResponse.getResponse().getResponseCode() == 1) {

			uutTypeField.setValue(fetchUUTNameById(sessionListResponse.getUutId()));
			dfccPartNoField.setText(String.valueOf(sessionListResponse.getDfccPartNo()));
			dfccSNoField.setText(String.valueOf(sessionListResponse.getDfccSNo()));
			startRemarksTextArea.setText(sessionListResponse.getStartRemarks());
			sessionTypeField.setValue(fetchSessionNameById(sessionListResponse.getSessionTypeMasterId()));
			
			uutTypeField.setDisable(true);
			sessionTypeField.setDisable(true);
			rightContainer.setDisable(true);
			
			List<StageObject> stageObjList = sessionListResponse.getSessionStagesList();
			
   
			if(middleContainer.getChildren().contains(treeView)) {
				middleContainer.getChildren().remove(treeView);
			}else if(middleContainer.getChildren().contains(selectedSessionStageTreeView)) {
				middleContainer.getChildren().remove(selectedSessionStageTreeView);
			}
				
   
		       Map<String,String> l1StageMap = new HashMap<>();
		       Map<String,StageIdName> l2StageMap = new HashMap<>();
		       Map<String,StageIdName> l3StageMap = new HashMap<>();
		       Map<String,StageIdName> l4StageMap = new HashMap<>();
		       Map<String,StageIdName> l5StageMap = new HashMap<>();
		       
		       for (StageObject stageObject : stageObjList) {
		    	   l1StageMap.put(stageObject.getL1StageId(),stageObject.getL1StageName());
		       }
		       
		       for (Map.Entry<String, String> l1Stage : l1StageMap.entrySet()) {
		    	   for (StageObject stageObject : stageObjList) {
			    	   if(l1Stage.getKey().equals(stageObject.getL1StageId())) {
			    		   if(stageObject.getL2StageId() == null) continue;
			    		   StageIdName l2StageObject = new StageIdName();
			    		   l2StageObject.setParentId(l1Stage.getKey());
			    		   l2StageObject.setStageId(stageObject.getL2StageId());
			    		   l2StageObject.setStageName(stageObject.getL2StageName());
			    		   if(stageObject.getL3StageId() == null && stageObject.getTestTypeId() != null) {
			    			   l2StageObject.setTestTypeId(stageObject.getTestTypeId());
			    		   }
			    		   l2StageMap.put(stageObject.getL2StageId(), l2StageObject);
			    	   }
			       }
		    	}
		       for (Map.Entry<String, StageIdName> l2Stage : l2StageMap.entrySet()) {
		    	   for (StageObject stageObject : stageObjList) {
			    	   if(l2Stage.getKey().equals(stageObject.getL2StageId())) {
			    		   if(stageObject.getL3StageId() == null) continue;
			    		   StageIdName l3StageObject = new StageIdName();
			    		   l3StageObject.setParentId(l2Stage.getKey());
			    		   l3StageObject.setStageId(stageObject.getL3StageId());
			    		   l3StageObject.setStageName(stageObject.getL3StageName());
			    		   if(stageObject.getL4StageId() == null && stageObject.getTestTypeId() != null) {
			    			   l3StageObject.setTestTypeId(stageObject.getTestTypeId());
			    		   }
			    		   l3StageMap.put(stageObject.getL3StageId(), l3StageObject);
			    	   }
			       }
		    	}
		       
		       for (Map.Entry<String, StageIdName> l3Stage : l3StageMap.entrySet()) {
		    	   for (StageObject stageObject : stageObjList) {
			    	   if(l3Stage.getKey().equals(stageObject.getL3StageId())) {
			    		   if(stageObject.getL4StageId() == null) continue;
			    		   StageIdName l4StageObject = new StageIdName();
			    		   l4StageObject.setParentId(l3Stage.getKey());
			    		   l4StageObject.setStageId(stageObject.getL4StageId());
			    		   l4StageObject.setStageName(stageObject.getL4StageName());
			    		   if(stageObject.getL5StageId() == null && stageObject.getTestTypeId() != null) {
			    			   l4StageObject.setTestTypeId(stageObject.getTestTypeId());
			    		   }
			    		   l4StageMap.put(stageObject.getL4StageId(), l4StageObject);
			    	   }
			       }
		    	}
		       
		       for (Map.Entry<String, StageIdName> l4Stage : l4StageMap.entrySet()) {
		    	   for (StageObject stageObject : stageObjList) {
			    	   if(l4Stage.getKey().equals(stageObject.getL4StageId())) {
			    		   if(stageObject.getL5StageId() == null) continue;
			    		   StageIdName l5StageObject = new StageIdName();
			    		   l5StageObject.setParentId(l4Stage.getKey());
			    		   l5StageObject.setStageId(stageObject.getL5StageId());
			    		   l5StageObject.setStageName(stageObject.getL5StageName());
			    		   if(stageObject.getTestTypeId() != null) {
			    			   l5StageObject.setTestTypeId(stageObject.getTestTypeId());
			    		   }
			    		   l5StageMap.put(stageObject.getL5StageId(), l5StageObject);
			    	   }
			       }
		    	}
		       
		       for (Map.Entry<String, StageIdName> entry : l2StageMap.entrySet()) {
		           String key = entry.getKey();
		           StageIdName value = entry.getValue();
		           System.out.println("Key: " + key + ", name: " + value.getStageName() +" parent: " + value.getParentId() + ", test: " + value.getTestTypeId());
		       }

		       TreeItem<HBox> rootItem = new TreeItem<>();
		       rootItem.setExpanded(true);
		       rootItem.setGraphic(null);

		       for (Map.Entry<String, String> l1Stage : l1StageMap.entrySet()) {
		           Label l1StageLabel = new Label(l1Stage.getValue());
		           l1StageLabel.setId(l1Stage.getKey());
		           l1StageLabel.getStyleClass().add("treeview-label");

		           HBox l1HBox = new HBox(l1StageLabel);
		           l1HBox.setAlignment(Pos.CENTER_LEFT);
		           TreeItem<HBox> l1StageItem = new TreeItem<>(l1HBox);

		           for (Map.Entry<String, StageIdName> l2Stage : l2StageMap.entrySet()) {
		               String l2Key = l2Stage.getKey();
		               StageIdName l2Value = l2Stage.getValue();
		               if (l1Stage.getKey().equals(l2Value.getParentId())) {
		                   Label l2StageLabel = new Label(l2Value.getStageName());
		                   l2StageLabel.setId(l2Key);
		                   l2StageLabel.getStyleClass().add("treeview-label");

		                   HBox l2HBox = new HBox(10,l2StageLabel);
		                   l2HBox.setAlignment(Pos.CENTER_LEFT);

		                   if (l2Value.getTestTypeId() != null) {
		                       Label l2TestTypeLabel = new Label(fetchTestTypeNameById(l2Value.getTestTypeId())); // Assuming StageIdName has a getTestType() method
		                       l2TestTypeLabel.getStyleClass().add("treeview-test-type-label");
		                       l2HBox.getChildren().add(l2TestTypeLabel);
		                   }

		                   TreeItem<HBox> l2StageItem = new TreeItem<>(l2HBox);

		                   for (Map.Entry<String, StageIdName> l3Stage : l3StageMap.entrySet()) {
		                       String l3Key = l3Stage.getKey();
		                       StageIdName l3Value = l3Stage.getValue();
		                       if (l2Stage.getKey().equals(l3Value.getParentId())) {
		                           Label l3StageLabel = new Label(l3Value.getStageName());
		                           l3StageLabel.setId(l3Key);
		                           l3StageLabel.getStyleClass().add("treeview-label");

		                           HBox l3HBox = new HBox(10,l3StageLabel);
		                           l3HBox.setAlignment(Pos.CENTER_LEFT);

		                           if (l3Value.getTestTypeId() != null) {
		                               Label l3TestTypeLabel = new Label(fetchTestTypeNameById(l3Value.getTestTypeId()));
		                               l3TestTypeLabel.getStyleClass().add("treeview-test-type-label");
		                               l3HBox.getChildren().add(l3TestTypeLabel);
		                           }

		                           TreeItem<HBox> l3StageItem = new TreeItem<>(l3HBox);

		                           for (Map.Entry<String, StageIdName> l4Stage : l4StageMap.entrySet()) {
		                               String l4Key = l4Stage.getKey();
		                               StageIdName l4Value = l4Stage.getValue();
		                               if (l3Stage.getKey().equals(l4Value.getParentId())) {
		                                   Label l4StageLabel = new Label(l4Value.getStageName());
		                                   l4StageLabel.setId(l4Key);
		                                   l4StageLabel.getStyleClass().add("treeview-label");

		                                   HBox l4HBox = new HBox(10,l4StageLabel);
		                                   l4HBox.setAlignment(Pos.CENTER_LEFT);
		                                   
		                                   if (l4Value.getTestTypeId() != null) {
		                                       Label l4TestTypeLabel = new Label(fetchTestTypeNameById(l4Value.getTestTypeId()));
		                                       l4TestTypeLabel.getStyleClass().add("treeview-test-type-label");
		                                       l4HBox.getChildren().add(l4TestTypeLabel);
		                                   }

		                                   TreeItem<HBox> l4StageItem = new TreeItem<>(l4HBox);

		                                   for (Map.Entry<String, StageIdName> l5Stage : l5StageMap.entrySet()) {
		                                       String l5Key = l5Stage.getKey();
		                                       StageIdName l5Value = l5Stage.getValue();
		                                       if (l4Stage.getKey().equals(l5Value.getParentId())) {
		                                           Label l5StageLabel = new Label(l5Value.getStageName());
		                                           l5StageLabel.setId(l5Key);
		                                           l5StageLabel.getStyleClass().add("treeview-label");

		                                           HBox l5HBox = new HBox(10,l5StageLabel);
		                                           l5HBox.setAlignment(Pos.CENTER_LEFT);
		                                           
		                                           if (l5Value.getTestTypeId() != null) {
		                                               Label l5TestTypeLabel = new Label(fetchTestTypeNameById(l5Value.getTestTypeId()));
		                                               l5TestTypeLabel.getStyleClass().add("treeview-test-type-label");
		                                               l5HBox.getChildren().add(l5TestTypeLabel);
		                                           }

		                                           TreeItem<HBox> l5StageItem = new TreeItem<>(l5HBox);
		                                           l4StageItem.getChildren().add(l5StageItem);
		                                       }
		                                   }

		                                   l3StageItem.getChildren().add(l4StageItem);
		                               }
		                           }

		                           l2StageItem.getChildren().add(l3StageItem);
		                       }
		                   }

		                   l1StageItem.getChildren().add(l2StageItem);
		               }
		           }

		           rootItem.getChildren().add(l1StageItem);
		       }

				selectedSessionStageTreeView.prefHeightProperty().bind(middleContainer.heightProperty());
				selectedSessionStageTreeView.setRoot(rootItem);
				selectedSessionStageTreeView.getStyleClass().add("session-tree-view");
				selectedSessionStageTreeView.setShowRoot(false);     

	
		    middleContainer.getChildren().add(selectedSessionStageTreeView);


	        			
			if(sessionListResponse.getFaultCodeMappingList()!=null) {
				List<FaultCodeDTO> faultCodeMappingList = sessionListResponse.getFaultCodeMappingList();
				StringBuilder faultCodeTextBuilder = new StringBuilder();
				for (FaultCodeDTO faultCodeDTO : faultCodeMappingList) {
					faultCodeTextBuilder.append(faultCodeDTO.getFaultCode()).append("-")
							.append(faultCodeDTO.getFaultCodeDescription()).append("\n");
				}
				faultCodeTextArea.setText(faultCodeTextBuilder.toString());
			}else {
				faultCodeTextArea.clear();
				rightContainer.setDisable(true);
			}

			uutTypeField.setEditable(false);
			dfccPartNoField.setEditable(false);
			dfccSNoField.setEditable(false);
			startRemarksTextArea.setEditable(false);
			sessionTypeField.setEditable(false);
			faultCodeTextArea.setEditable(false);

		} else {
			Notifications.showErrorAlert("Failed to fetch Data for this session");
		}

	}
	
	private String fetchTestTypeNameById(String testTypeId) {
	    ObservableList<TestTypeMasterDetailsDto> testTypeDataList = FXCollections.observableArrayList(runConfigurationManagement.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestTypeId().equals(testTypeId)) {
				return testType.getTestName();
			}
		}
		return null;
	}

	private void enablingFunction(boolean value) {
		sessionEntryGridPane.setDisable(value);
		startRemarksLabel.setDisable(value);
		startRemarksTextArea.setDisable(value);
		middleContainer.setDisable(value);
		rightContainer.setDisable(value);
	}

	private void saveNewSession() {
		List<SessionToStagesMappingDTO> sessionStagesList = new ArrayList<>();
		for (List<String> hierarchy : filteredHierarchies) {
			SessionToStagesMappingDTO mappingDTO = new SessionToStagesMappingDTO();
			if (hierarchy.size() > 0) {
				if(hierarchy.get(0).equals("0")) {
					continue;
				}
				mappingDTO.setTestTypeId(hierarchy.get(0));
			}
			if (hierarchy.size() > 1) {
				mappingDTO.setLevelOneStageId(hierarchy.get(1));
//				System.out.println("H1: " + hierarchy.get(1));
			}
			if (hierarchy.size() > 2) {
				mappingDTO.setLevelTwoStageId(hierarchy.get(2));
//				System.out.println("H2: " + hierarchy.get(2));
			}
			if (hierarchy.size() > 3) {
				mappingDTO.setLevelThreeStageId(hierarchy.get(3));
//				System.out.println("H3: " + hierarchy.get(3));
			}
			if (hierarchy.size() > 4) {
				mappingDTO.setLevelFourStageId(hierarchy.get(4));
//				System.out.println("H4: " + hierarchy.get(4));
			}
			if (hierarchy.size() > 5) {
				mappingDTO.setLevelFiveStageId(hierarchy.get(5));
//				System.out.println("H5: " + hierarchy.get(5));
			}
			mappingDTO.setSessionId(SESSION_TYPE_ID);
			sessionStagesList.add(mappingDTO);
		}

		SessionDTO sessionDTO = new SessionDTO();
		sessionDTO.setUutId(UUT_ID);
//		sessionDTO.setDfccType(UUT_ID);
		sessionDTO.setSessionName(sessionNameField.getText());
		sessionDTO.setSessionTypeMasterId(SESSION_TYPE_ID);
		sessionDTO.setUserId(USER_ID);
		sessionDTO.setDfccSNo(dfccSNoField.getText());
		sessionDTO.setDfccPartNo(dfccPartNoField.getText());
		sessionDTO.setStartRemarks(startRemarksTextArea.getText());
		sessionDTO.setSessionStagesList(sessionStagesList);
		sessionDTO.setFaultCodeMappingList(selectedFaultCodeList);

		Response response = sessionManagement.saveSession(sessionDTO);
	
		if (response.getResponseCode() == 1) {
			
			StateMachine.currentSessionDetails.setUutId(UUT_ID);
			StateMachine.currentSessionDetails.setUutType(uutTypeField.getValue());
			StateMachine.currentSessionDetails.setSessionTypeID(SESSION_TYPE_ID);
			StateMachine.currentSessionDetails.setSessionTypeName(sessionTypeField.getValue());
			StateMachine.currentSessionDetails.setSessionName(sessionNameField.getText());
			
			
			System.out.println("Session Created Successfully..!");
			StackPane parent1 = (StackPane) sessionCreationParentGridPane.getParent();
			parent1.getChildren().clear();
			parent1.getChildren().add(userDashboardController.createUserDashboard());
//			parent1.getChildren().add(loadDriverController.createLoadDriverPage());
		} else {
			System.out.println("Session Not Created " + response.getResponseMessage());
		}
	}

	private void onSessionNameAdd() {
		enablingSessionTable(false);
		enablingFunction(false);
		clearFields();
		uutTypeField.setDisable(false);
		sessionTypeField.setDisable(false);
		if(middleContainer.getChildren().contains(treeView)) {
			middleContainer.getChildren().remove(treeView);
		}
		middleContainer.getChildren().add(treeView);
	}

	private void clearFields() {
		dfccPartNoField.clear();
		dfccSNoField.clear();
		startRemarksTextArea.clear();
		uutTypeField.getSelectionModel().clearSelection();
		sessionTypeField.getSelectionModel().clearSelection();
		faultCodeTextArea.clear();
		rightContainer.setDisable(true);
		
		uutTypeField.setEditable(true);
		dfccPartNoField.setEditable(true);
		dfccSNoField.setEditable(true);
		startRemarksTextArea.setEditable(true);
		sessionTypeField.setEditable(true);
		faultCodeTextArea.setEditable(true);
	}

	private VBox createRightContainer() {
		initializeFaultCodeTableView();
		populateFaultCodeTableView();

		addFaultCodeButton.prefWidthProperty().bind(rightContainer.widthProperty());
		selectedFaultCodeLabel.getStyleClass().add("field-label");

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

	private void addSubStages(CustomCheckBoxTreeItem<String> parentItem, String parentId,
			Map<String, SubStage> subStageMap) {
		ObservableList<SubStage> subStages = getSubStagesFromDb(parentId);
		for (SubStage subStage : subStages) {
			CustomCheckBoxTreeItem<String> subItem = new CustomCheckBoxTreeItem<>(subStage.getL_name(),
					subStage.getId());
			subItem.setParentId(parentId);
			if (!subStage.isHasNext()) {
				subItem.setTestType(subStage.getTestType());
			}
			parentItem.getChildren().add(subItem);
			subStageMap.put(subItem.getValue(), subStage);
			subItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
				List<String> allParentIdsIncludingSelf = subItem.getAllParentIdsIncludingSelf();
				if (newValue) {
					filteredHierarchies = displaySelectedHierarchy(allParentIdsIncludingSelf, subItem.getTestType());
//					filteredHierarchies
//							.forEach(hierarchy -> System.out.println("Selected Node Hierarchy: " + hierarchy));
				} else {
					removeHierarchy(allParentIdsIncludingSelf, subItem.getTestType());
//					filteredHierarchies
//							.forEach(hierarchy -> System.out.println("DE-Selected Node Hierarchy: " + hierarchy));
				}
			});
			if (subStage.isHasNext()) {
				addSubStages(subItem, subStage.getId(), subStageMap);
			}
		}
	}

	private ObservableList<StageOne> getStageOneFromDb(String sessionTypeId) {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = sessionManagement.getLevelOneStageMasterBySessionId(sessionTypeId);
		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
//				System.out.println("CHECK::: " + levelOneDto.getStageName());
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

			if (SESSION_TYPE_ID!=null && SESSION_TYPE_ID.equals("ST2")) {
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

	private String fetchUUTNameById(String uutId) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutId().equals(uutId)) {
				return uut.getUutType();
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

	private String fetchSessionNameById(String sessionTypeId) {
		for (SessionMasterDTO type : sessionDataList) {
			if (type.getSessionMasterId().equals(sessionTypeId)) {
				return type.getSessionTypeName();
			}
		}
		return null;
	}

	private List<List<String>> displaySelectedHierarchy(List<String> hierarchy, String testType) {
		List<String> fullHierarchy = new ArrayList<>(hierarchy);
		if (testType != null) {
			fullHierarchy.add(0, testType);
		}else {
			fullHierarchy.add(0, "0");
		}
		selectedHierarchies.add(fullHierarchy);
		return filterAndSortHierarchies();
	}

	private void removeHierarchy(List<String> hierarchy, String testType) {
		List<String> fullHierarchy = new ArrayList<>();
		if (testType != null) {
			fullHierarchy.add(testType);
		}
		fullHierarchy.addAll(hierarchy);
		selectedHierarchies.remove(fullHierarchy);
		filteredHierarchies = filterAndSortHierarchies();
	}

	private List<List<String>> filterAndSortHierarchies() {
		List<List<String>> sortedHierarchies = new ArrayList<>(selectedHierarchies);
		sortedHierarchies.sort((a, b) -> {
			int minLength = Math.min(a.size(), b.size());
			for (int i = 0; i < minLength; i++) {
				int compare = a.get(i).compareTo(b.get(i));
				if (compare != 0) {
					return compare;
				}
			}
			return Integer.compare(a.size(), b.size());
		});

		List<List<String>> filteredHierarchies = new ArrayList<>();
		for (int i = 0; i < sortedHierarchies.size(); i++) {
			boolean isChild = false;
			List<String> hierarchy = sortedHierarchies.get(i);
			for (int j = 0; j < i; j++) {
				List<String> parentHierarchy = sortedHierarchies.get(j);
				if (parentHierarchy.size() < hierarchy.size()
						&& hierarchy.subList(0, parentHierarchy.size()).equals(parentHierarchy)) {
					isChild = true;
					break;
				}
			}
			if (!isChild) {
				filteredHierarchies.add(hierarchy);
			}
		}
		return filteredHierarchies;
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
	private String id;
	private String parentId;
	private String testType;

	public CustomCheckBoxTreeItem(T value, String id) {
		super(value);
		this.id = id;
		this.parentId = null;
		this.testType = null;
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public List<String> getAllParentIdsIncludingSelf() {
		List<String> parentIds = new ArrayList<>();
		TreeItem<T> currentItem = this;
		while (currentItem != null) {
			if (currentItem instanceof CustomCheckBoxTreeItem) {
				String currentItemId = ((CustomCheckBoxTreeItem<T>) currentItem).getId();
				if (currentItemId != null) {
					parentIds.add(currentItemId);
				}
			}
			currentItem = currentItem.getParent();
		}
		Collections.reverse(parentIds);
		return parentIds;
	}
}
