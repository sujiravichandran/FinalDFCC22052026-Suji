package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
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
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.FaultCodeList;
import com.teclever.dfcc.model.SessionDetails;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.model.SubStage;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.scene.control.Alert.AlertType;
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
	private Button backButton = new Button("BACK");

	// leftBox
	private TextField sessionNameField = new TextField();
	private TextField dfccSNoField = new TextField();
	private TextField dfccPartNoField = new TextField();
	private Label startRemarksLabel = new Label("Start Remarks");
	private Label sessionNameLabel = new Label("Search Session");
	private Label uutTypeLabel = new Label("UUT Type");
	private Label dfccSNoLabel = new Label("DFCC Serial No");
	private Label dfccPartNoLabel = new Label("DFCC Part No");
	private TextArea startRemarksTextArea = new TextArea();
	private GridPane sessionEntryGridPane = new GridPane();
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private TextField uutTypeTextField = new TextField();
	private Label sessionTypeLabel = new Label("Session Type");
	private GridPane sessionTypeGridPane = new GridPane();
	private GridPane uutTypeGridPane = new GridPane();
	private ComboBox<String> sessionTypeField = new ComboBox<>();
	private Label modTypeLabel = new Label("Mod Selection");
	private GridPane modTypeGridPane = new GridPane();
	private ComboBox<String> modTypeComboBox = new ComboBox<>();
	private TextField modTypeTextField = new TextField();
	private ObservableList<String> modTypes = FXCollections.observableArrayList();

	// middleBox
	private Label selectStageLabel = new Label("Select Stages");
	private TreeView<String> treeView = new TreeView<>();

	private TreeView<HBox> selectedSessionStageTreeView = new TreeView<>();

	// rightBox
	private GridPane ofpVersionGridPane = new GridPane();
	private Label ofpVersionLabel = new Label("OFP Version");
	private ComboBox<String> ofpVersionField = new ComboBox<>();
	private TextField ofpVersionTextField = new TextField();

	private Button addFaultCodeButton = new Button("Add/Remove Fault Code");
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

	private List<OfpConfigurationDto> ofpDataList;
	private ObservableList<String> ofpList = FXCollections.observableArrayList();
	private String OFP_CONFIG_ID;

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

	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private FaultCodeConfiguration faultCodeConfig = new FaultCodeConfiguration();
	private SessionManagement sessionManagement = new SessionManagement();
	private StageConfiguration stageConfig = new StageConfiguration();
	private LoadDriverController loadDriverController = new LoadDriverController();
	private RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
	private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();

	private ObservableList<StageOne> session_l1Data = FXCollections.observableArrayList();
	private ObservableList<FaultCodeList> faultCodeList = FXCollections.observableArrayList();

	private List<List<String>> filteredHierarchies;
	private Set<List<String>> selectedHierarchies = new HashSet<>();

	private boolean newSession;

	private List<List<String>> selectedStagesList = new ArrayList<>();
	private static String userInput;

	public SessionCreationController() {
		this.ROLE_ID = UserData.getRoleId();
		this.USER_ID = UserData.getUserId();

		if (ROLE_ID.equals("RL_ID_4")) {
			middleContainer.setVisible(false);
			middleContainer.setManaged(false);
			rightContainer.setDisable(false);
		} else {
			rightContainer.setDisable(true);
		}

		if (USER_ID != null) {
			populateSessionTableView();
		}

		initializeUUTTypeComboBox();
		initializeSessionTypeComboBox();
	}

	public GridPane createSession(boolean newSession) {
		this.newSession = newSession;
		enableSessionTable(newSession);
		sessionCreationParentGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionCreation.css")
						.toExternalForm());
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
		if (newSession) {
			createButton.setText("CREATE SESSION");
		} else {
			createButton.setText("OPEN SESSION");
		}
		createButton.setOnAction(e -> {
			if (createButton.getText().equals("CREATE SESSION")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on CREATE SESSION button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				saveNewSession();
			} else if (createButton.getText().equals("OPEN SESSION")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on OPEN SESSION button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				openExistingSession();
			}
		});

		cancelButton.setOnAction(e -> {
			String title = "Confirmation Dialog";
			String contentText = "Are you sure you want to exit the application";
			Notifications.showConfirmationDialog(title, contentText, () -> {
				Stage stage = (Stage) sessionCreationParentGridPane.getScene().getWindow();
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on EXIT button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				stage.close();
			});
		});

		backButton.setOnAction(e -> {
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on BACK button");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StackPane parent = (StackPane) sessionCreationParentGridPane.getParent();
			parent.getChildren().clear();
			SessionCreationOptionController sessionCreationOptionController = new SessionCreationOptionController();
			parent.getChildren().add(sessionCreationOptionController.createSessionOption());
		});

		HBox buttonHbox = new HBox(20);
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.getChildren().addAll(createButton, cancelButton, backButton);
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
		sessionNameLabel.getStyleClass().add("field-label");
		uutTypeLabel.getStyleClass().add("field-label");
		modTypeLabel.getStyleClass().add("field-label");
		dfccSNoLabel.getStyleClass().add("field-label");
		dfccPartNoLabel.getStyleClass().add("field-label");
		startRemarksLabel.getStyleClass().add("field-label");

		sessionTypeLabel.getStyleClass().add("field-label");

		GridPane sessionNameFieldBox = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(100);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);
		sessionNameFieldBox.getColumnConstraints().addAll(column1);
		sessionNameFieldBox.getRowConstraints().addAll(row1);
		sessionNameFieldBox.add(sessionNameField, 0, 0);

		ColumnConstraints uutLabelColumn = new ColumnConstraints();
		uutLabelColumn.setPercentWidth(40);
		ColumnConstraints uutFieldColumn = new ColumnConstraints();
		uutFieldColumn.setPercentWidth(60);
		RowConstraints uutRow = new RowConstraints();
		uutRow.setPercentHeight(100);
		uutTypeGridPane.getColumnConstraints().addAll(uutLabelColumn, uutFieldColumn);
		uutTypeGridPane.getRowConstraints().add(uutRow);
		uutTypeGridPane.add(uutTypeLabel, 0, 0);
		if (newSession) {
			uutTypeGridPane.add(uutTypeField, 1, 0);
		} else {
			uutTypeGridPane.add(uutTypeTextField, 1, 0);
			uutTypeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					UUT_ID = fetchUutId(newValue);
				}
			});
		}
		uutTypeField.prefWidthProperty().bind(uutTypeGridPane.widthProperty());

		ColumnConstraints sessionTypeLabelColumn = new ColumnConstraints();
		sessionTypeLabelColumn.setPercentWidth(40);
		ColumnConstraints sessionTypeFieldColumn = new ColumnConstraints();
		sessionTypeFieldColumn.setPercentWidth(60);
		RowConstraints sessionTypeRow = new RowConstraints();
		sessionTypeRow.setPercentHeight(100);
		sessionTypeGridPane.getColumnConstraints().addAll(sessionTypeLabelColumn, sessionTypeFieldColumn);
		sessionTypeGridPane.getRowConstraints().add(sessionTypeRow);
		sessionTypeGridPane.add(sessionTypeLabel, 0, 0);
		sessionTypeGridPane.add(sessionTypeField, 1, 0);
		sessionTypeField.prefWidthProperty().bind(sessionTypeGridPane.widthProperty());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);
		sessionEntryGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sessionEntryGridPane.getRowConstraints().addAll(firstRow, secondRow);

		sessionEntryGridPane.add(dfccSNoLabel, 0, 0);
		sessionEntryGridPane.add(dfccSNoField, 1, 0);
		sessionEntryGridPane.add(dfccPartNoLabel, 0, 1);
		sessionEntryGridPane.add(dfccPartNoField, 1, 1);
		dfccPartNoField.setEditable(false);
		sessionEntryGridPane.setVgap(10);

		ColumnConstraints modTypeLabelColumn = new ColumnConstraints();
		modTypeLabelColumn.setPercentWidth(40);
		ColumnConstraints modTypeComboBoxColumn = new ColumnConstraints();
		modTypeComboBoxColumn.setPercentWidth(25);
		ColumnConstraints modTypeGapColumn = new ColumnConstraints();
		modTypeGapColumn.setPercentWidth(5);
		ColumnConstraints modTypeTextFieldColumn = new ColumnConstraints();
		modTypeTextFieldColumn.setPercentWidth(30);
		RowConstraints modTypeRow = new RowConstraints();
		modTypeRow.setPercentHeight(100);
		modTypeGridPane.getColumnConstraints().addAll(modTypeLabelColumn, modTypeComboBoxColumn, modTypeGapColumn,
				modTypeTextFieldColumn);
		modTypeGridPane.getRowConstraints().add(modTypeRow);

		for (char c = 'A'; c <= 'Z'; c++) {
			modTypes.add("Mod-" + c);
		}
		modTypeComboBox.setItems(modTypes);
		
		modTypeComboBox.prefWidthProperty().bind(modTypeGridPane.widthProperty());
		modTypeGridPane.add(modTypeLabel, 0, 0);
		modTypeGridPane.add(modTypeComboBox, 1, 0);
		modTypeGridPane.add(modTypeTextField, 3, 0);

		modTypeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue.length() > 20 || !newValue.matches("[A-Z0-9_-]*")) {
		        String filtered = newValue.replaceAll("[^a-zA-Z0-9_-]", "").toUpperCase();
		        modTypeTextField.setText(filtered.length() > 20 ? filtered.substring(0, 20) : filtered);
		    } else {
		        modTypeTextField.setText(newValue.toUpperCase());
		    }
		});
		
//		dfccSNoField.textProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue.length() > 15 || !newValue.matches("[A-Z0-9]*")) {
//		        String filtered = newValue.replaceAll("[^A-Z0-9]", "");
//		        dfccSNoField.setText(filtered.length() > 15 ? filtered.substring(0, 15) : filtered);
//		    }
//		});



		dfccSNoField.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if (!newValue) { // When the TextField loses focus
		        String value = dfccSNoField.getText().toUpperCase(); // convert to uppercase

		        if ("UUT1".equals(UUT_ID)) {
		            // Regex: starts with PT or SP, followed by exactly 3 digits
		            if (!value.matches("^(PT|SP)\\d{3}$")) {
		                Alert alert = new Alert(Alert.AlertType.ERROR);
		                alert.setTitle("Invalid Input");
		                alert.setHeaderText(null);
		                alert.setContentText("Please enter like this: PT001 or SP001");
		                alert.showAndWait();
		                dfccSNoField.clear();
		            } else {
		                dfccSNoField.setText(value); // valid input
		            }
		        } else {
		            // Other UUT_ID: allow only digits, max 3
		        	//value.matches("[1-9]\\d{2}")
		            if (!value.matches("[0-9]\\d{2}")) { // if not 0-3 digits
		                Alert alert = new Alert(Alert.AlertType.ERROR);
		                alert.setTitle("Invalid Input");
		                alert.setHeaderText(null);
		                alert.setContentText("Serial number should be maximum 3 digits!");
		                alert.showAndWait();
		                dfccSNoField.clear();
		            } else {
		                dfccSNoField.setText(value); // valid input
		            }
		        }
		    }
		});
		
		startRemarksTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			startRemarksTextArea.setText(newValue.length() > 50 ? newValue.substring(0, 50) : newValue);    
		});

		if (newSession) {
			if (ROLE_ID.equals("RL_ID_3")) {
				leftContainer.getChildren().addAll(uutTypeGridPane, sessionTypeGridPane, sessionEntryGridPane,
						modTypeGridPane, startRemarksLabel, startRemarksTextArea);
				startRemarksTextArea.setPrefHeight(800);
			} else if (ROLE_ID.equals("RL_ID_4")) {
				leftContainer.getChildren().addAll(uutTypeGridPane, sessionEntryGridPane, modTypeGridPane,
						startRemarksLabel, startRemarksTextArea);
				startRemarksTextArea.setPrefHeight(800);
			}

		} else {
			leftContainer.getChildren().addAll(sessionNameLabel, sessionNameField, sessionDetailsTableView,
					uutTypeGridPane, sessionEntryGridPane, startRemarksLabel, startRemarksTextArea);
			uutTypeTextField.setEditable(false);
			dfccSNoField.setEditable(false);
			dfccPartNoField.setEditable(false);
			startRemarksTextArea.setEditable(false);
		}

		leftContainer.getStyleClass().add("session-creation-container");
		return leftContainer;
	}

	private void populateSessionTableView() {
		SessionListResponse response = sessionManagement.getAllSessionDataByRoleId(ROLE_ID);
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

	private void enableSessionTable(boolean status) {
		sessionDetailsTableView.setPrefHeight(300);
		sessionDetailsTableView.setVisible(!status);
		sessionDetailsTableView.setManaged(!status);
		if (!status) {
			initializeSessionNameTableView();
			initializeSearchFunctionality();

			rightContainer.setDisable(true);
		} else {
			sessionTypeField.setDisable(true);
			createButton.setDisable(true);
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
		sessionDetailsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		sessionDetailsTableView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						sessionNameField.setText(newSelection.getSessionName());
						SESSION_ID = newSelection.getSessionId();
						if (SESSION_ID != null) {
							retriveSessionDetailsUsingSessionID();
						}
					}
				});
	}

	private void initializeSearchFunctionality() {
		FilteredList<SessionDetails> filteredList = new FilteredList<>(sessionNameDetailsList, p -> true);
		SortedList<SessionDetails> sortedList = new SortedList<>(filteredList);
		sessionNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(sessionDetail -> {
				String lowerCaseFilter = newValue.toLowerCase().trim();
				return sessionDetail.getSessionName().toLowerCase().trim().contains(lowerCaseFilter);
			});
			sortedList.comparatorProperty().bind(sessionDetailsTableView.comparatorProperty());
			sessionDetailsTableView.setItems(sortedList);

		});
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);
		uutTypeField.setOnAction((event) -> {
			refreshFaultCode();
			sessionTypeField.setDisable(false);
			createButton.setDisable(false);
			UUT_ID = fetchUutId(uutTypeField.getValue());
			initializeOfpComboBox();
			refreshSessionTypeComboBox();
			if (ROLE_ID.equals("RL_ID_4")) {
				setDefaultSessionTypeSelection();
			}
			if("UUT1".equals(UUT_ID)) {
				dfccPartNoField.setText("1160 000 395 75");
			}else if("UUT2".equals(UUT_ID)) {
				dfccPartNoField.setText("1104 001 333 85");
			}else if("UUT3".equals(UUT_ID)) {
				dfccPartNoField.setText("1100 025 954 72");
			}
		});
	}

	private void initializeOfpComboBox() {
		ofpVersionField.getItems().clear();
		ofpDataList = FXCollections.observableArrayList(ofpConfig.getOfpConfig(UUT_ID));
		for (OfpConfigurationDto ofp : ofpDataList) {
			ofpList.add(ofp.getOfpVersion());
		}
		ofpVersionField.setItems(ofpList);

		ofpVersionField.setOnAction(e -> {
			if (ofpVersionField.getValue() != null) {
				OFP_CONFIG_ID = fetchOfpConfigId(ofpVersionField.getValue());
				refreshFaultCode();
				populateFaultCodeTableView();
			}
		});
	}

	private String fetchOfpConfigId(String ofpVerison) {
		for (OfpConfigurationDto ofp : ofpDataList) {
			if (ofp.getOfpVersion().equals(ofpVerison)) {
				return ofp.getOfpConfigId();
			}
		}
		return null;
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

	private void initializeSessionTypeComboBox() {
		sessionDataList = FXCollections.observableArrayList(stageConfig.getSessionMasterList());
		for (SessionMasterDTO sessionType : sessionDataList) {
			sessionTypeList.add(sessionType.getSessionTypeName());
		}
		if ("UUT1".equals(UUT_ID)) {
		    sessionTypeList.removeIf(e -> "BLS".equals(e));
		}
		sessionTypeField.setItems(sessionTypeList);
		sessionTypeField.setOnAction((event) -> {
			if (sessionTypeField.getValue() != null && !sessionTypeField.getValue().isEmpty()) {
				handleSessionTypeSelection();
			}
		});
	}

	private void handleSessionTypeSelection() {
		refreshFaultCode();
		SESSION_TYPE_ID = fetchSessionTypeId(sessionTypeField.getValue());
		final String[] activeTrailIdHolder = {null};
		List <String> sessionName = new ArrayList<String>();
		if (SESSION_TYPE_ID.equals("ST4")) {
			
			boolean trailsActiveStatus = sessionManagement.isActiveTrailsPresent();
//			////System.out.println("Chck trailsActiveStatus " + trailsActiveStatus);
			SessionListResponse response = sessionManagement.getAllSessionDataByRoleId(ROLE_ID);
			String uut = uutTypeField.getValue();
			if (response.getResponse().getResponseCode() == 1) {
				for (SessionList sessionDto : response.getListOfSession()) {
//					////System.out.println("UUT " + uut);
//					////System.out.println("sessionName uP" + sessionDto.getSessionName());
					sessionName.add(sessionDto.getSessionName());
					if(sessionDto.getSessionId().startsWith("TSSN")) {
						activeTrailIdHolder[0] = sessionDto.getSessionId();
					}	
				}
			}
			
			boolean matchFound = sessionName.stream()
				    .anyMatch(s -> 
				        s.toLowerCase().contains(uut.toLowerCase()) &&
				        s.toLowerCase().contains("_trial")
				    );

			////System.out.println("matchFound" + matchFound);
			
			if (trailsActiveStatus && matchFound) {
//				////System.out.println("Check entred");
				String title = "Confirmation Dialog";
				String contentText = "The existing trial session is still active, so a new trial session cannot be created. Do you want do end trail session?";
				Notifications.showConfirmationDialog(title, contentText, () -> endTrailSesion(activeTrailIdHolder[0]));
				Platform.runLater(() -> {
					sessionTypeField.setValue(null);
				});
			}
		}

		session_l1Data = getStageOneFromDb(SESSION_TYPE_ID);

		treeView = createTreeViewWithCheckBoxes(session_l1Data);
		treeView.prefHeightProperty().bind(middleContainer.heightProperty());
		middleContainer.getChildren().set(1, treeView);

		if (SESSION_TYPE_ID != null && SESSION_TYPE_ID.equals("ST3")) {
			rightContainer.setDisable(false);
		} else {
			rightContainer.setDisable(true);
		}

	}
	
	private void endTrailSesion(String activeTrailSessionId) {
		currentSessionDetails.setSessionId(activeTrailSessionId);
		showEndRemarksDialog("Confirm End Session", "Are you sure you want to end the current session and close the application?", () -> {
	            Response response = sessionManagement.endSession(userInput,false);
	            if (response.getResponseCode() == 1) {
					currentSessionDetails.setSessionId(null);
	            	Notifications.showSuccessAlert("Trial session ended successfully");
	            } else {
	                Notifications.showErrorAlert(response.getResponseMessage());
	            }
	        },
			() -> {
				currentSessionDetails.setSessionId(null);
			}
		);
	}
	
	public static void showEndRemarksDialog(String title, String contentText, Runnable onConfirm, Runnable onCancel) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setHeight(300);
			alert.setWidth(500);
			alert.setContentText(contentText);

			TextArea endRemarksTextArea = new TextArea();
			endRemarksTextArea.setPromptText("Enter End Remarks");
			endRemarksTextArea.setPrefHeight(300);
			endRemarksTextArea.setPrefWidth(500);
			endRemarksTextArea.setWrapText(true);
			

			VBox inputDialog = new VBox();
			inputDialog.getChildren().add(endRemarksTextArea);
			alert.getDialogPane().setContent(inputDialog);

			endRemarksTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			endRemarksTextArea.setText(newValue.length() > 50 ? newValue.substring(0, 50) : newValue);
			});

			ButtonType buttonTypeSave = new ButtonType("Save");
			ButtonType buttonTypeCancel = new ButtonType("Cancel");

			alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeCancel);

			Button saveButton = (Button) alert.getDialogPane().lookupButton(buttonTypeSave);
			Button cancelButton = (Button) alert.getDialogPane().lookupButton(buttonTypeCancel);

			saveButton.addEventFilter(ActionEvent.ACTION, event -> {
				userInput = endRemarksTextArea.getText();

				if (userInput == null || userInput.trim().isEmpty()) {
					Alert alertText = new Alert(AlertType.INFORMATION);
					alertText.setHeaderText(null);
					alertText.setContentText("Please Enter END REMARKS");
					alertText.showAndWait();

					event.consume();
				} else {
					onConfirm.run();
				}
			});
			
			cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
	            if (onCancel != null) {
	                onCancel.run();
	            }
	        });

	        alert.showAndWait();
		});
	}

	private void setDefaultSessionTypeSelection() {
		refreshFaultCode();
		SESSION_TYPE_ID = "ST3";
		session_l1Data = getStageOneFromDb(SESSION_TYPE_ID);

		treeView = createTreeViewWithCheckBoxes(session_l1Data);
		treeView.prefHeightProperty().bind(middleContainer.heightProperty());
		middleContainer.getChildren().set(1, treeView);
		rightContainer.setDisable(false);
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

	private void refreshSessionTypeComboBox() {
		sessionTypeList.clear();
		sessionDataList = FXCollections.observableArrayList(stageConfig.getSessionMasterList());
		for (SessionMasterDTO sessionType : sessionDataList) {
			sessionTypeList.add(sessionType.getSessionTypeName());
		}
		if ("UUT1".equals(UUT_ID)) {
		    sessionTypeList.removeIf(e -> "BLS".equals(e));
		}
		
		sessionTypeField.setItems(sessionTypeList);
	}

	private String fetchTestTypeNameById(String testTypeId) {
		ObservableList<TestTypeMasterDetailsDto> testTypeDataList = FXCollections
				.observableArrayList(runConfigurationManagement.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestTypeId().equals(testTypeId)) {
				return testType.getTestName();
			}
		}
		return null;
	}

	private VBox createMiddleContainer() {
		selectStageLabel.getStyleClass().add("field-label");
		treeView.prefHeightProperty().bind(middleContainer.heightProperty());

		middleContainer.getStyleClass().add("session-creation-container");
		middleContainer.getChildren().addAll(selectStageLabel, treeView);
		return middleContainer;
	}

	private ObservableList<StageOne> getStageOneFromDb(String sessionTypeId) {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = sessionManagement.getLevelOneStageMasterBySessionId(UUT_ID,
				sessionTypeId);
		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
				if (UUT_ID.equals(levelOneDto.getUutId())) {
					StageOne stage = new StageOne();
					stage.setL1_name(levelOneDto.getStageName());
					stage.setId(levelOneDto.getLevelOneId());
					stage.setDefault(levelOneDto.isDefaultStatus());
					stage.setMandatory(levelOneDto.isMandatoryStatus());
					stage.setContinueWithError(levelOneDto.isContinueWithErrorStatus());
					stage.setAdvancedTest(levelOneDto.isAdvanceTestStatus());
					if (!sessionTypeId.equals("ST4")) {
						stageList.add(stage);
					}
				}
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
		if (filteredHierarchies != null) {
			filteredHierarchies.clear();
			selectedHierarchies.clear();
		}
		Map<String, SubStage> subStageMap = new HashMap<>();

		for (StageOne stage : stageList) {
			if (stage.isAdvancedTest() || stage.isDefault()) {
				continue;
			}
			CustomCheckBoxTreeItem<String> item = new CustomCheckBoxTreeItem<>(stage.getL1_name(), stage.getId());
			if (!ROLE_ID.equals("RL_ID_4")) {
			item.setSelected(true);
			}
			rootItem.getChildren().add(item);
			addSubStages(item, stage.getId(), subStageMap);
		}

		treeView.setCellFactory(tv -> {
			CheckBoxTreeCell<String> cell = new CheckBoxTreeCell<>();
			cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {
				if (newItem instanceof CustomCheckBoxTreeItem) {
					CustomCheckBoxTreeItem<String> customItem = (CustomCheckBoxTreeItem<String>) newItem;
					cell.setDisable(customItem.isDisabled());
				}
			});
			return cell;
		});
		treeView.setRoot(filterDisabledItems(rootItem));
		treeView.setShowRoot(false);
		return treeView;
	}

	private CustomCheckBoxTreeItem<String> filterDisabledItems(CustomCheckBoxTreeItem<String> root) {
		CustomCheckBoxTreeItem<String> filteredRoot = new CustomCheckBoxTreeItem<>("Stages", null);
		for (TreeItem<String> item : root.getChildren()) {
			CustomCheckBoxTreeItem<String> customItem = (CustomCheckBoxTreeItem<String>) item;
			if (!customItem.isDisabled()) {
				filteredRoot.getChildren().add(item);
			}
		}
		return filteredRoot;
	}

	private void addSubStages(CustomCheckBoxTreeItem<String> parentItem, String parentId,
			Map<String, SubStage> subStageMap) {
		ObservableList<SubStage> subStages = getSubStagesFromDb(parentId);
		for (SubStage subStage : subStages) {
			CustomCheckBoxTreeItem<String> subItem = new CustomCheckBoxTreeItem<>(subStage.getL_name(),
					subStage.getId());
			subItem.setSelected(true);
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
//							.forEach(hierarchy ->Debug.printDebug("Selected Node Hierarchy: " + hierarchy));
				} else {
					removeHierarchy(allParentIdsIncludingSelf, subItem.getTestType());
//					filteredHierarchies
//							.forEach(hierarchy ->Debug.printDebug("DE-Selected Node Hierarchy: " + hierarchy));
				}
			});
			if (subStage.isHasNext()) {
				addSubStages(subItem, subStage.getId(), subStageMap);
			}
		}
	}

	private List<List<String>> displaySelectedHierarchy(List<String> hierarchy, String testType) {
		List<String> fullHierarchy = new ArrayList<>(hierarchy);
		if (testType != null) {
			fullHierarchy.add(0, testType);
		} else {
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

	private void saveNewSession() {

		getSelectedStageList();

		StringBuilder errorMessage = new StringBuilder();
		if (sessionTypeField.getValue() == null || sessionTypeField.getValue().isEmpty()) {
//			//System.out.println("  conD 1");
			if (!ROLE_ID.equals("RL_ID_4")) {
//				//System.out.println("  conD 1 -1") ;
				errorMessage.append("Please Select Session Type...\n");
			}
		}
		
		
		
		if (ROLE_ID.equals("RL_ID_4")){
//			//System.out.println("  conD 2");
			if (selectedFaultCodeList.size() == 0) { 
				Notifications.showErrorAlert("Please add fault code for FRU session...\n");
//				errorMessage.append("Please add fault code for FRU session...\n");
				return;
			}else {
			sessionTypeField.setValue("FRU");
			}
		}
		else{
//			//System.out.println("  conD 3");
			if (sessionTypeField.getValue().toLowerCase().trim().contains("fru")) {
				if (selectedFaultCodeList.size() == 0) {
					errorMessage.append("Please add fault code for FRU session...\n");
				}
			}
		}
		
		
		if (dfccSNoField.getText().trim() == null || dfccSNoField.getText().trim().isEmpty()) {
			errorMessage.append("Please add DFCC serial number...\n");
		}
		if (modTypeComboBox.getValue() == null || modTypeComboBox.getValue().isEmpty()) {
				errorMessage.append("Please Select Mod Type...\n");
		}
		if (dfccPartNoField.getText().trim() == null || dfccPartNoField.getText().trim().isEmpty()) {
			errorMessage.append("Please add DFCC part number...\n");
		}
		if (startRemarksTextArea.getText().trim() == null || startRemarksTextArea.getText().trim().isEmpty()) {
			errorMessage.append("Please add remark...\n");
		}

		if (errorMessage.length() > 0) {
			Notifications.showErrorAlert(errorMessage.toString());
			return;
		}
		List<SessionToStagesMappingDTO> sessionStagesList = new ArrayList<>();
		for (List<String> hierarchy : selectedStagesList) {
//			Debug.printDebug(");
			SessionToStagesMappingDTO mappingDTO = new SessionToStagesMappingDTO();
			if (hierarchy.size() > 0) {
				if (hierarchy.get(0).equals("0")) {
					continue;
				}
				mappingDTO.setTestTypeId(hierarchy.get(0));
			}

//			Debug.printDebug("hierarchy);
			if (hierarchy.size() > 1) {
				mappingDTO.setLevelOneStageId(hierarchy.get(1));
			}
			if (hierarchy.size() > 2) {
				mappingDTO.setLevelTwoStageId(hierarchy.get(2));
			}
			if (hierarchy.size() > 3) {
				mappingDTO.setLevelThreeStageId(hierarchy.get(3));
			}
			if (hierarchy.size() > 4) {
				mappingDTO.setLevelFourStageId(hierarchy.get(4));
			}
			if (hierarchy.size() > 5) {
				mappingDTO.setLevelFiveStageId(hierarchy.get(5));
			}
			mappingDTO.setSessionId(SESSION_TYPE_ID);
			sessionStagesList.add(mappingDTO);
		}

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDateTime = currentDateTime.format(formatter);
		String sessionName = null;

		
		if (ROLE_ID.equals("RL_ID_4")) {
			String modTypePart = modTypeTextField.getText().trim();
			sessionName = uutTypeField.getValue().trim() + "_" + dfccSNoField.getText().trim() + "_" + 
			               modTypeComboBox.getValue().trim() + "_" + 
			               (modTypePart.isEmpty() ? "" : modTypePart + "_") + formattedDateTime;

		} else {
			String modTypePart = modTypeTextField.getText().trim();
			sessionName = uutTypeField.getValue().trim() + "_" + sessionTypeField.getValue().trim() + "_" +
			              dfccSNoField.getText().trim() + "_" + modTypeComboBox.getValue().trim() + "_" + 
			              (modTypePart.isEmpty() ? "" : modTypePart + "_") + formattedDateTime;

		}

		SessionDTO sessionDTO = new SessionDTO();
		sessionDTO.setUutId(UUT_ID);
		sessionDTO.setSessionName(sessionName);
		sessionDTO.setSessionTypeMasterId(SESSION_TYPE_ID);
		sessionDTO.setUserId(USER_ID);
		sessionDTO.setDfccSNo(dfccSNoField.getText().trim());
		sessionDTO.setDfccPartNo(dfccPartNoField.getText().trim());
		sessionDTO.setStartRemarks(startRemarksTextArea.getText().trim());
		sessionDTO.setSessionStagesList(sessionStagesList);
		sessionDTO.setOfpConfigId(OFP_CONFIG_ID);
		sessionDTO.setFaultCodeMappingList(selectedFaultCodeList);

		Response response;
		GetObjResponse trailResponse;
		String msg = "";
		int code = 0;

		if (!SESSION_TYPE_ID.equals("ST4")) {
			if (sessionDTO.getSessionStagesList().size() == 0 && !DFCCConstant.roleId.equals("RL_ID_4")) {
				Notifications.showErrorAlert("Please Select Session Stages !!");
				return;
			}

//			if(DFCCConstant.roleId.equals("RL_ID_4"))
//			{
//				if (sessionDTO.getSessionStagesList().size() == 0) {
//					Notifications.showErrorAlert("Please Select Session Stages !!");
//					return;
//				}
//			}
			response = sessionManagement.saveSession(sessionDTO);
			code = response.getResponseCode();
			msg = response.getResponseMessage();
		} else {
			trailResponse = sessionManagement.saveTrailSessionEntity(sessionDTO);
			code = trailResponse.getResponse().getResponseCode();
			msg = trailResponse.getResponse().getResponseMessage();
		}

		if (code == 1) {

			currentSessionDetails.setUutId(UUT_ID);
			currentSessionDetails.setUutType(uutTypeField.getValue());
			currentSessionDetails.setSessionTypeID(SESSION_TYPE_ID);
			currentSessionDetails.setSessionTypeName(sessionTypeField.getValue());
			currentSessionDetails.setSessionName(sessionName);
			currentSessionDetails.setDfccSerialNumber(dfccSNoField.getText().trim());

			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "session " + sessionName + " created");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

			UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
			UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "session " + sessionName + " created");
			uutLogbookManagement.addUUTLogBook(uutLogBookDto);

			StackPane parent1 = (StackPane) sessionCreationParentGridPane.getParent();
			parent1.getChildren().clear();
			parent1.getChildren().add(loadDriverController.createLoadDriverPage());
		} else {
			Debug.printDebug("Session Not Created " + msg);
		}

	}

	private void getSelectedStageList() {
		selectedStagesList.clear();
		TreeItem<String> root = treeView.getRoot();
		getSelectedStages(root);
	}

	private void getSelectedStages(TreeItem<String> root) {
		for (TreeItem<String> l1Stage : root.getChildren()) {
			CustomCheckBoxTreeItem<String> l1StageItemChild = (CustomCheckBoxTreeItem<String>) l1Stage;
			if (l1StageItemChild.isSelected() || l1StageItemChild.isIndeterminate()) {
				List<String> currentPath = new LinkedList<>();
				currentPath.add(l1StageItemChild.getId());
				getSelectedSubStage(l1StageItemChild, currentPath);
			}
		}
	}

	private void getSelectedSubStage(CustomCheckBoxTreeItem<String> parentItem, List<String> currentPath) {
		for (TreeItem<String> subStage : parentItem.getChildren()) {
			CustomCheckBoxTreeItem<String> subStageItemChild = (CustomCheckBoxTreeItem<String>) subStage;
			if (subStageItemChild.isSelected() || subStageItemChild.isIndeterminate()) {
				List<String> newPath = new LinkedList<>(currentPath);
				newPath.add(subStageItemChild.getId());
				if (subStageItemChild.isLeaf()) {
					newPath.add(0, subStageItemChild.getTestType());
					selectedStagesList.add(newPath);
				}
				getSelectedSubStage(subStageItemChild, newPath);
			}
		}
	}

	private void openExistingSession() {
		if (SESSION_ID == null) {
			Notifications.showWarningAlert("Select any existing session");
			return;
		}
		currentSessionDetails.setUutId(fetchUutId(uutTypeTextField.getText().trim()));
		currentSessionDetails.setUutType(uutTypeTextField.getText());
		currentSessionDetails.setSessionTypeID(fetchSessionTypeId(sessionTypeField.getValue()));
		currentSessionDetails.setSessionTypeName(sessionTypeField.getValue());
		currentSessionDetails.setSessionId(SESSION_ID);
		currentSessionDetails.setSessionName(sessionNameField.getText());
		currentSessionDetails.setDfccSerialNumber(dfccSNoField.getText().trim());

		UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
		UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
				currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
				StateMachine.getCurrentUserLogin(), new Date(),
				"session " + currentSessionDetails.getSessionName() + " opened");
		uutLogbookManagement.addUUTLogBook(uutLogBookDto);
		
		String currentDirectory = new File(
				SessionFileManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getParent();
		
		currentDirectory = currentDirectory+File.separator+".output";
		
		StateMachine.setHomelocation(Paths.get(currentDirectory));

		StackPane parent1 = (StackPane) sessionCreationParentGridPane.getParent();
		parent1.getChildren().clear();
		parent1.getChildren().add(loadDriverController.createLoadDriverPage());

	}

	private void retriveSessionDetailsUsingSessionID() {
		SessionDTOResponse sessionListResponse = sessionManagement.getSessionDetailById(SESSION_ID);

		if (sessionListResponse.getResponse().getResponseCode() == 1) {

			uutTypeTextField.setText(fetchUUTNameById(sessionListResponse.getUutId()));
			dfccPartNoField.setText(String.valueOf(sessionListResponse.getDfccPartNo()));
			dfccSNoField.setText(String.valueOf(sessionListResponse.getDfccSNo()));
			startRemarksTextArea.setText(sessionListResponse.getStartRemarks());
			sessionTypeField.setValue(fetchSessionNameById(sessionListResponse.getSessionTypeMasterId()));

			String ofpVersion = fetchOfpVersionByUUTIdandConfigId(uutTypeTextField.getText(),
					sessionListResponse.getOfpConfigId());

			ofpVersionTextField.setText(ofpVersion);

			List<StageObject> stageObjList = sessionListResponse.getSessionStagesList();

			if (middleContainer.getChildren().contains(treeView)) {
				middleContainer.getChildren().remove(treeView);
			} else if (middleContainer.getChildren().contains(selectedSessionStageTreeView)) {
				middleContainer.getChildren().remove(selectedSessionStageTreeView);
			}

			Map<String, String> l1StageMap = new LinkedHashMap<>();
			Map<String, StageIdName> l2StageMap = new LinkedHashMap<>();
			Map<String, StageIdName> l3StageMap = new LinkedHashMap<>();
			Map<String, StageIdName> l4StageMap = new LinkedHashMap<>();
			Map<String, StageIdName> l5StageMap = new LinkedHashMap<>();

			if (stageObjList != null) {
				for (StageObject stageObject : stageObjList) {
					if (!stageObject.isDefaultStatus() && !stageObject.isAdvanceStatus()) {
						l1StageMap.put(stageObject.getL1StageId(), stageObject.getL1StageName());
					}
				}
			}

			for (Map.Entry<String, String> l1Stage : l1StageMap.entrySet()) {
				for (StageObject stageObject : stageObjList) {
					if (l1Stage.getKey().equals(stageObject.getL1StageId())) {
						if (stageObject.getL2StageId() == null)
							continue;
						StageIdName l2StageObject = new StageIdName();
						l2StageObject.setParentId(l1Stage.getKey());
						l2StageObject.setStageId(stageObject.getL2StageId());
						l2StageObject.setStageName(stageObject.getL2StageName());
						if (stageObject.getL3StageId() == null && stageObject.getTestTypeId() != null) {
							l2StageObject.setTestTypeId(stageObject.getTestTypeId());
						}
						l2StageMap.put(stageObject.getL2StageId(), l2StageObject);
					}
				}
			}
			for (Map.Entry<String, StageIdName> l2Stage : l2StageMap.entrySet()) {
				for (StageObject stageObject : stageObjList) {
					if (l2Stage.getKey().equals(stageObject.getL2StageId())) {
						if (stageObject.getL3StageId() == null)
							continue;
						StageIdName l3StageObject = new StageIdName();
						l3StageObject.setParentId(l2Stage.getKey());
						l3StageObject.setStageId(stageObject.getL3StageId());
						l3StageObject.setStageName(stageObject.getL3StageName());
						if (stageObject.getL4StageId() == null && stageObject.getTestTypeId() != null) {
							l3StageObject.setTestTypeId(stageObject.getTestTypeId());
						}
						l3StageMap.put(stageObject.getL3StageId(), l3StageObject);
					}
				}
			}

			for (Map.Entry<String, StageIdName> l3Stage : l3StageMap.entrySet()) {
				for (StageObject stageObject : stageObjList) {
					if (l3Stage.getKey().equals(stageObject.getL3StageId())) {
						if (stageObject.getL4StageId() == null)
							continue;
						StageIdName l4StageObject = new StageIdName();
						l4StageObject.setParentId(l3Stage.getKey());
						l4StageObject.setStageId(stageObject.getL4StageId());
						l4StageObject.setStageName(stageObject.getL4StageName());
						if (stageObject.getL5StageId() == null && stageObject.getTestTypeId() != null) {
							l4StageObject.setTestTypeId(stageObject.getTestTypeId());
						}
						l4StageMap.put(stageObject.getL4StageId(), l4StageObject);
					}
				}
			}

			for (Map.Entry<String, StageIdName> l4Stage : l4StageMap.entrySet()) {
				for (StageObject stageObject : stageObjList) {
					if (l4Stage.getKey().equals(stageObject.getL4StageId())) {
						if (stageObject.getL5StageId() == null)
							continue;
						StageIdName l5StageObject = new StageIdName();
						l5StageObject.setParentId(l4Stage.getKey());
						l5StageObject.setStageId(stageObject.getL5StageId());
						l5StageObject.setStageName(stageObject.getL5StageName());
						if (stageObject.getTestTypeId() != null) {
							l5StageObject.setTestTypeId(stageObject.getTestTypeId());
						}
						l5StageMap.put(stageObject.getL5StageId(), l5StageObject);
					}
				}
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

						HBox l2HBox = new HBox(10, l2StageLabel);
						l2HBox.setAlignment(Pos.CENTER_LEFT);

						if (l2Value.getTestTypeId() != null) {
							Label l2TestTypeLabel = new Label(fetchTestTypeNameById(l2Value.getTestTypeId()));
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

								HBox l3HBox = new HBox(10, l3StageLabel);
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

										HBox l4HBox = new HBox(10, l4StageLabel);
										l4HBox.setAlignment(Pos.CENTER_LEFT);

										if (l4Value.getTestTypeId() != null) {
											Label l4TestTypeLabel = new Label(
													fetchTestTypeNameById(l4Value.getTestTypeId()));
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

												HBox l5HBox = new HBox(10, l5StageLabel);
												l5HBox.setAlignment(Pos.CENTER_LEFT);

												if (l5Value.getTestTypeId() != null) {
													Label l5TestTypeLabel = new Label(
															fetchTestTypeNameById(l5Value.getTestTypeId()));
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
			if (sessionListResponse.getFaultCodeMappingList() != null) {
				List<FaultCodeDTO> faultCodeMappingList = sessionListResponse.getFaultCodeMappingList();
				StringBuilder faultCodeTextBuilder = new StringBuilder();
				for (FaultCodeDTO faultCodeDTO : faultCodeMappingList) {
					faultCodeTextBuilder.append(faultCodeDTO.getFaultCode()).append("-")
							.append(faultCodeDTO.getFaultCodeDescription()).append("\n");
				}
				faultCodeTextArea.setText(faultCodeTextBuilder.toString());
			} else {
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

	private String fetchOfpVersionByUUTIdandConfigId(String uutId, String configId) {
		ofpDataList = FXCollections.observableArrayList(ofpConfig.getOfpConfig(UUT_ID));
		for (OfpConfigurationDto ofp : ofpDataList) {
			if (ofp.getOfpConfigId().equals(configId)) {
				return ofp.getOfpVersion();
			}
		}
		return null;
	}

	private VBox createRightContainer() {
		initializeFaultCodeTableView();
//		populateFaultCodeTableView();

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

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		ofpVersionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpVersionGridPane.getRowConstraints().add(firstRow);

		ofpVersionLabel.getStyleClass().add("field-label");

		ofpVersionGridPane.add(ofpVersionLabel, 0, 0);
		if (newSession) {
			ofpVersionGridPane.add(ofpVersionField, 1, 0);
		} else {
			ofpVersionGridPane.add(ofpVersionTextField, 1, 0);
		}
		ofpVersionField.prefWidthProperty().bind(ofpVersionGridPane.widthProperty());

		rightContainer.getStyleClass().add("session-creation-container");
		rightContainer.getChildren().addAll(ofpVersionGridPane, faultCodeTableView, 
				selectedFaultCodeLabel, faultCodeTextArea, addFaultCodeButton);
		return rightContainer;
	}

	// FETCHING ALL FAULTCODE LIST
	private void populateFaultCodeTableView() {
		FaultCodeResponse response = faultCodeConfig.getFaultCodeList(UUT_ID, OFP_CONFIG_ID);
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

	private void refreshFaultCode() {
		selectedFaultCodeList.clear();
		faultCodeTextArea.clear();
		faultCodeList.clear();
//		for (FaultCodeList faultCode : faultCodeTableView.getItems()) {
//			faultCode.setSelected(false);
//		}
//		faultCodeTableView.refresh();
	}

}

class CustomCheckBoxTreeItem<T> extends CheckBoxTreeItem<T> {
	private String id;
	private String parentId;
	private String testType;
	private boolean disabled;

	public CustomCheckBoxTreeItem(T value, String id) {
		super(value);
		this.id = id;
		this.parentId = null;
		this.testType = null;
		this.disabled = false; // Default to not disabled
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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
