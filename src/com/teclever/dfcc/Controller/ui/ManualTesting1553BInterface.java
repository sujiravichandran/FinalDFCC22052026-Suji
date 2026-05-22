package com.teclever.dfcc.Controller.ui;

import java.awt.image.BufferedImage;
import javafx.scene.control.ButtonBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.Interface1553B_Mode;
import com.teclever.datastore.service.Interface1553B_ModeService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Interface1553B;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

class ManualTesting1553BDataTableViewFactory implements TableViewFactory<Interface1553B> {
	@Override
	public CustomTableView<Interface1553B> createTableView(ObservableList<Interface1553B> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Interface1553B.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTesting1553BInterface {

	private GridPane interface1553BMainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private HBox headingHbox3 = new HBox(10);
	private Label titleLabel = new Label("1553B INTERFACE MODE CODE CHECKS");

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();

	private HBox serialHbox = new HBox(10);
	private Label dfccSNoLabel = new Label("DFCC Serial Number : ");
	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ComboBox<String> sessioName = new ComboBox<String>();
	private Label dfccSessionName = new Label("Session Name : ");

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private TextField dfccSNoText = new TextField();
	private Label editableLabel = new Label("Click to edit");
	private TextField editField = new TextField(editableLabel.getText());
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private Label stageNameLabel = new Label("Stage Name");
	private ComboBox<String> stageName = new ComboBox<String>();

	private StackPane interface1553BTable1StackPane = new StackPane();
	private GridPane interface1553BTable1GridPane = new GridPane();

	private StackPane interface1553BTable22StackPane = new StackPane();

	private Button saveButton = new Button("Save");
	private Button fetchButton = new Button("Fetch");

	private String selectedStageId;

	private boolean saveFlag = false;

	private ObservableList<Interface1553B> interface1553BTable1DataList = FXCollections.observableArrayList();
	private CustomTableView<Interface1553B> interface1553BTable1DataTableView;
	private TableViewFactory<Interface1553B> interface1553BTable1DataFactory = new ManualTesting1553BDataTableViewFactory();

	private String uutId = "UUT1";
	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label("NOTE:\n"
			+ "(1) Testing should be done Two Channels at a time. Ch 1 and Ch 2 are tested First. Ch 3 and Ch 4 are tested Next.\n"
			+ "\n" + "(2) @ → Data ‘1717’ to be Loaded at SBA 100V and Monitored at Memory Location 003C8204 in DFCC.\n"
			+ "\n"
			+ "(3) $ → Results in ‘Last Commanded Word’ Transmitted from DFCC (‘A811’ for Ch 1 & 3, ‘B011’ for Ch 2 & 4)\n"
			+ "\n"
			+ "(4) $ → Results in ‘Last Commanded Word’ Transmitted from DFCC. For the procedure mentioned in Appendix 13, Last Command word is (‘ABF1’ for Ch1 & 3, ‘B3F1’ for Ch2 & 4)");

	private Label tmpUutLabel;
	private Label tmpSerialLabel;
	private Label tmpSessionLabel;
	private Label tmpStageLabel;

	private HBox tbNoHBox = new HBox();
	private Label testEquipment = new Label(
			"TEST EQUIPMENT USED: SerialBus Analyser, LORAL SBA 100V or Equivalent(PATS++)");
	private Label tbNol1 = new Label("TB NO.:134  PASS/FAIL");
	private Label passFail1 = new Label("PASS/FAIL");
	private Label testRep = new Label("TESTING REP: ");
	private Label testRepDate = new Label("DATE: ");
	private Label tiqmRep = new Label("TI/QM REP: ");
	private Label tiqmRepDate = new Label("DATE: ");
	private SessionService s = new SessionService();

	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");
	private Button checkButton = new Button("Check");

	private String selectedSessionId;
	private String selectedStageName;

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String t1Ch1;
	private String t1Ch2;
	private String t1Ch3;
	private String t1Ch4;

	private String t2Ch1;
	private String t2Ch2;
	private String t2Ch3;
	private String t2Ch4;

	private String selectedUttId;
	private String selectedSNo;
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();

	private TrailSessionEntityService t = new TrailSessionEntityService();
	public ManualTesting1553BInterface() {

		// Normal sessions
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		// Trial sessions
		TrailSessionResponse t1 = t.getActiveTrailSessionId();
		sessionListTrail = t1.getListOfSession();

		initializeUUTTypeComboBox();

	}

	public GridPane create1553BMainContainerGridPane() {
		saveButton.setDisable(true);
		fetchButton.setDisable(true);
		printButton.setDisable(true);
		interface1553BMainContainerGridPane.getChildren().clear();
		interface1553BMainContainerGridPane.getColumnConstraints().clear();
		interface1553BMainContainerGridPane.getRowConstraints().clear();

		interface1553BMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		interface1553BMainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(17);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(83);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(25);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(3);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(3);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(5);

		interface1553BMainContainerGridPane.setHgap(2);
		interface1553BMainContainerGridPane.setVgap(2);

		interface1553BMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		interface1553BMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow,
				fivthRow, sixthRow, seventhRow);
		interface1553BMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		interface1553BMainContainerGridPane.add(createHeadingBox(), 0, 0);
		interface1553BMainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		interface1553BMainContainerGridPane.add(create1553BInterfaceTableContent(), 0, 2);
		interface1553BMainContainerGridPane.add(createNoteBox(), 0, 3);
		interface1553BMainContainerGridPane.add(createTestEquipmentBox(), 0, 4);
		interface1553BMainContainerGridPane.add(createRepBox(), 0, 5);
		interface1553BMainContainerGridPane.add(createRepDateBox(), 0, 6);

		return interface1553BMainContainerGridPane;
	}

	private GridPane createHeadingBox() {

		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(70);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		topBoxGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		topBoxGridPane.getRowConstraints().addAll(firstRow);

		// 🔥 CREATE NEW INSTANCES every time (NO reuse)
		HBox headingHbox1 = new HBox();

		Label title = new Label(titleLabel.getText());
		titleLabel.setUnderline(true);

		headingHbox1.setAlignment(Pos.CENTER_RIGHT);
		title.setUnderline(true);
		title.getStyleClass().add("advanced-testing-title");

		noteLabel.setStyle("advanced-testing-title");

		headingHbox1.getChildren().add(title);

		topBoxGridPane.add(headingHbox1, 0, 0);
		topBoxGridPane.add(createPrintButtonHbox(), 1, 0);

		return topBoxGridPane;
	}

	private GridPane createNoteBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(100);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);

		grid.getColumnConstraints().addAll(col1);

		grid.getRowConstraints().addAll(row1);

		grid.add(noteLabel, 0, 0);

		noteLabel.getStyleClass().add("nonheading-label");

		return grid;
	}

	private GridPane createTestEquipmentBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(70);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(30);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);

		grid.getColumnConstraints().addAll(col1);

		grid.getRowConstraints().addAll(row1);

		grid.add(testEquipment, 0, 0);
		grid.add(tbNol1, 1, 0);

		tbNol1.getStyleClass().add("nonheading-label");
		testEquipment.getStyleClass().add("nonheading-label");

		return grid;
	}

	private GridPane createRepBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		grid.getColumnConstraints().addAll(col1, col2);

		grid.add(testRep, 0, 0);
		grid.add(tiqmRep, 1, 0);
		testRep.getStyleClass().add("nonheading-label");
		tiqmRep.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(testRep, HPos.CENTER);
		GridPane.setHalignment(tiqmRep, HPos.CENTER);

		return grid;
	}

	private GridPane createRepDateBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		grid.getColumnConstraints().addAll(col1, col2);

		grid.add(testRepDate, 0, 0);
		grid.add(tiqmRepDate, 1, 0);

		testRepDate.getStyleClass().add("nonheading-label");
		tiqmRepDate.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(testRepDate, HPos.CENTER);
		GridPane.setHalignment(tiqmRepDate, HPos.CENTER);

		return grid;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow);

		dfccSNoLabel.getStyleClass().add("nonheading-label");

		HBox uutHbox = new HBox(10);
		uutHbox.setAlignment(Pos.CENTER_LEFT);
		uutHbox.setFillHeight(true);
		uutTypeField.setPromptText("select UUT Type");
		uutHbox.getChildren().add(uutTypeField);

		HBox serialHbox = new HBox(10);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);

		dfccSessionName.getStyleClass().add("nonheading-label");
		sessionHbox.getChildren().addAll(sessioName);
		sessioName.setPromptText("select Session");
		sessioName.setEditable(true);
		serialHbox.getChildren().addAll(serialnumber);
		serialnumber.setPromptText("select Serial No");
		serialnumber.setEditable(true);
		stageName.setPromptText("select Stage Name");
		stageName.setEditable(true);

		HBox stageNameHbox = new HBox(10);
		stageNameHbox.setAlignment(Pos.CENTER_RIGHT);
		stageNameLabel.getStyleClass().add("nonheading-label");
		stageNameHbox.getChildren().addAll(stageName);

		serialAlternateGridPane.add(uutHbox, 0, 0);
		serialAlternateGridPane.add(serialHbox, 1, 0);
		serialAlternateGridPane.add(sessionHbox, 2, 0);
		serialAlternateGridPane.add(stageNameHbox, 3, 0);

		return serialAlternateGridPane;
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

	private void getSerialNo() {
		List serailNumber = FXCollections.observableArrayList(unitGetDetailsManagement.getAllDfccSerialNo());
		serialnumber.setItems((ObservableList<String>) serailNumber);

	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeList.removeAll(Arrays.asList("DFCC-MK1A", "DFCC-MK2"));
		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
			stageList.clear();
			stageName.getSelectionModel().clearSelection();
			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initalizeSerialNoComboBox(uutId);

			selectedUttId = uutId;
			create1553BMainContainerGridPane();
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

//	private void initalizeSerialNoComboBox(String uutId) {
//		dfccSNList.clear();
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutId))
//				.collect(Collectors.toList());
//
//		Set<String> seenDfccSNos = new HashSet<>();
//
//		for (SessionDto dfccSn : filterSessionList) {
//			String dfccSNo = dfccSn.getDfccSNo();
//			if (seenDfccSNos.add(dfccSNo)) {
//				dfccSNList.add(dfccSNo);
//			}
//		}
//		serialnumber.setItems(dfccSNList);
//		serialnumber.setOnAction((event) -> {
//			selectedSNo = serialnumber.getSelectionModel().getSelectedItem();
//			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
//			initializeSessionComboBox(selectedSerialNumber);
//
//		});
//	}
	
	private void initalizeSerialNoComboBox(String uutTypeId) {

	    dfccSNList.clear();
	    Set<String> seenDfccSNos = new HashSet<>();

	    if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {

	        List<SessionDto> filterSessionList = sessionList.stream()
	                .filter(t -> t.getUutId().equals(uutTypeId))
	                .collect(Collectors.toList());

	        for (SessionDto dfccSn : filterSessionList) {
	            String dfccSNo = dfccSn.getDfccSNo();
	            if (seenDfccSNos.add(dfccSNo)) {
	                dfccSNList.add(dfccSNo);
	            }
	        }

	    } else {

	        List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
	                .filter(t -> t.getUutId().equals(uutTypeId))
	                .collect(Collectors.toList());
	        for (TrailSessionDto dfccSn : filterSessionList) {
	            String dfccSNo = dfccSn.getDfccSNo();
	            if (seenDfccSNos.add(dfccSNo)) {
	                dfccSNList.add(dfccSNo);
	            }
	        }
	        ////System.out.println("Suji check Link Failty pe ::: " +dfccSNList.size() );
	    }
	    
	    
	    serialnumber.setItems(dfccSNList);
	    addSearchFunctionality(serialnumber, dfccSNList);
	    serialnumber.setOnAction((event) -> {

			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
			selectedSNo = serialnumber.getSelectionModel().getSelectedItem();
			 if (selectedSNo != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
		            initializeSessionComboBox(selectedSNo);
		        }else {
		        	initializeTrialSessionComboBox(selectedSNo);
		        }

		});

	    
	}
	
	private void initializeTrialSessionComboBox(String selectedDfccNo) {

	    sessionTypeList.clear();

	    List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
	            .filter(t ->
	                    Objects.equals(t.getUutId(), selectedUttId) &&
	                    Objects.equals(t.getDfccSNo(), selectedSNo) &&
	                    t.getEndDate() == null
	            )
	            .sorted(Comparator
	                    .comparing(TrailSessionDto::getUutId)
	                    .thenComparing(TrailSessionDto::getDfccSNo))
	            .collect(Collectors.toList());

	    for (TrailSessionDto sessionName : filterSessionList) {
	        sessionTypeList.add(sessionName.getSessionName());
	    }
	    sessioName.setItems(sessionTypeList);
	    addSearchFunctionality(sessioName, sessionTypeList);
	    sessioName.setOnAction(event -> {
			String selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
			
			interface1553BTable1DataList.clear();
			if (selectedSessionName != null) {
				TrailSessionDto selectedSession = filterSessionList.stream()
						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);

				if (selectedSession != null) {
					selectedSessionId = selectedSession.getSessionId();
				}
				getStageName(selectedSessionId);
				
			}
		});

	   
	}

	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();

//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo))
//				.collect(Collectors.toList());

		List<SessionDto> filterSessionList = sessionList.stream()
				.filter(t -> Objects.equals(t.getUutId(), selectedUttId) && Objects.equals(t.getDfccSNo(), selectedSNo)
						&& t.getEndDate() == null)
				.sorted(Comparator.comparing(SessionDto::getUutId).thenComparing(SessionDto::getDfccSNo))
				.collect(Collectors.toList());

		for (SessionDto sessionName : filterSessionList) {
			sessionTypeList.add(sessionName.getSessionName());

		}

		sessioName.setItems(sessionTypeList);
	    addSearchFunctionality(sessioName, sessionTypeList);
		sessioName.setOnAction(event -> {
			String selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
			if (selectedSessionName != null) {
				SessionDto selectedSession = filterSessionList.stream()
						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);

				if (selectedSession != null) {
					selectedSessionId = selectedSession.getSessionId();
				}
				getStageName(selectedSessionId);
			}
		});

	}

	private void getStageName(String sessionId) {

		stageList = unitGetDetailsManagement.getStageDetailsForSession(selectedSessionId);

		// IMPORTANT: clear old data to avoid accumulation
		stageNameId.clear();
		stageName.getItems().clear();

		for (UnitSessionDetailsDTO dto : stageList) {
			if (dto.getStageId() != null && dto.getStageName() != null) {
				stageNameId.put(dto.getStageId(), dto.getStageName());
			}
		}

		// Filter out null values explicitly
		List<String> filteredStageNames = stageNameId.values().stream().filter(Objects::nonNull).toList();

		stageName.setItems(FXCollections.observableArrayList(filteredStageNames));
	    addSearchFunctionality(stageName, FXCollections.observableArrayList(filteredStageNames));
		stageName.setOnAction(event -> {

			selectedStageName = stageName.getSelectionModel().getSelectedItem();

			selectedStageId = stageNameId.entrySet().stream()
					.filter(e -> Objects.equals(selectedStageName, e.getValue())).map(Map.Entry::getKey).findFirst()
					.orElse(null);
			
			saveButton.setDisable(false);
			fetchButton.setDisable(false);
		});
	}

	private StackPane create1553BInterfaceTableContent() {

		interface1553BTable1StackPane.getChildren().clear();
		interface1553BTable1StackPane.getStyleClass().add("tab-content-container");
		interface1553BTable1StackPane.getChildren().clear();
		interface1553BTable1StackPane.getChildren().add(interface1553BableResultGridPane());
		return interface1553BTable1StackPane;
	}

	private GridPane interface1553BableResultGridPane() {
		interface1553BTable1GridPane.getChildren().clear();
		interface1553BTable1GridPane.getColumnConstraints().clear();
		interface1553BTable1GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		interface1553BTable1GridPane.getColumnConstraints().add(firstColumn);
		interface1553BTable1GridPane.getRowConstraints().add(firstRow);

		interface1553BTable1GridPane.add(create1553BIntrefaceTable1DataTable(), 0, 0);

		return interface1553BTable1GridPane;
	}

	private ScrollPane create1553BIntrefaceTable1DataTable() {

		// Clear old data
		interface1553BTable1DataList.clear();

		// Create exactly 7 rows
		for (int i = 0; i < 7; i++) {
			interface1553BTable1DataList.add(new Interface1553B());
		}

		ScrollPane tableScrollPane = new ScrollPane();
		tableScrollPane.setFitToHeight(true);
		tableScrollPane.setFitToWidth(true);

		// Create TableView
		interface1553BTable1DataTableView = interface1553BTable1DataFactory
				.createTableView(interface1553BTable1DataList, false, false);

		// Fix columns + headers
		interface1553BTable1DataTableView.getColumns().forEach(column -> {
			String colName = column.getText();
			column.setText(null);

			Label header = new Label(colName.replace("C", "")); // C1→1, C2→2 etc.
			header.setWrapText(true);
			header.setAlignment(Pos.CENTER);

			VBox box = new VBox(header);
			box.setAlignment(Pos.CENTER);
			box.setPadding(new Insets(4, 2, 4, 2));

			column.setGraphic(box);
			column.setCellValueFactory(new PropertyValueFactory<>(colName.toLowerCase()));
		});

		// Ensure resize
		interface1553BTable1DataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

		// Copy DTO Columns
		List<TableColumn<Interface1553B, ?>> dtoColumns = new ArrayList<>(
				interface1553BTable1DataTableView.getColumns());

		interface1553BTable1DataTableView.getColumns().clear();

		// Top merged headers
		TableColumn<Interface1553B, ?> testResultHeader = new TableColumn<>("Test Result Status (P: Pass; F: Fail)");
		testResultHeader.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

		TableColumn<Interface1553B, ?> modeHeader = new TableColumn<>("MODE CODE");
		modeHeader.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

		// Left static labels
		TableColumn<Interface1553B, String> leftLabelCol = new TableColumn<>("");
		leftLabelCol.setMinWidth(250);
		leftLabelCol.setMaxWidth(250);

		List<String> leftLabels = Arrays.asList("Associated Data word (HEX)", "Data Word Address in DFCC", "T/R Status",
				"Channel 1", "Channel 2", "Channel 3", "Channel 4");

		leftLabelCol.setCellValueFactory(cell -> {
			int idx = interface1553BTable1DataTableView.getItems().indexOf(cell.getValue());
			return new SimpleStringProperty(idx < leftLabels.size() ? leftLabels.get(idx) : "");
		});

		dtoColumns.add(0, leftLabelCol);

		// Default values
		List<String> d0 = Arrays.asList("--", "--", "--", "--", "--", "--", "--", "--", "1616", "@", "$", "1919");
		List<String> d1 = Arrays.asList("--", "--", "--", "--", "--", "--", "--", "--", "003C8200", "--", "--",
				"003C820C");
		List<String> d2 = Arrays.asList("T", "T", "T", "T", "T", "T", "T", "T", "T", "R", "T", "T");
		List<String> d3 = Arrays.asList("P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P");
		List<String> d4 = Arrays.asList("P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P");
		List<String> d5 = Arrays.asList("P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P");
		List<String> d6 = Arrays.asList("P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P");

		List<List<String>> defaults = Arrays.asList(d0, d1, d2, d3, d4, d5, d6);

		// Apply to all editable columns
		for (int colPos = 1; colPos < dtoColumns.size(); colPos++) {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			TableColumn<Interface1553B, Object> col = (TableColumn) dtoColumns.get(colPos);

			final int dataIndex = colPos - 1;

			col.setCellFactory(param -> new TextFieldTableCell<Interface1553B, Object>(new StringConverter<Object>() {

				@Override
				public String toString(Object object) {
					return object == null ? "" : object.toString();
				}

				@Override
				public Object fromString(String string) {
					return string;
				}
			}) {

				@Override
				public void updateItem(Object item, boolean empty) {
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
						return;
					}

					int row = getIndex();

					// If row has default values
					if (row < defaults.size()) {

						Interface1553B dto = interface1553BTable1DataList.get(row);

						// Get current stored value from DTO
						String currentValue = getValueFromDto(dto, dataIndex);

						if (currentValue == null || currentValue.isEmpty()) {
							// Apply default only ONCE
							String defaultValue = defaults.get(row).get(dataIndex);
							updateDto(dto, dataIndex, defaultValue);
							setText(defaultValue);
						} else {
							// Already edited → DO NOT overwrite
							setText(currentValue);
						}

						return;
					}

					// Normal row behavior
					setText(item == null ? "" : item.toString());
				}

				@Override
				public void commitEdit(Object newValue) {
					super.commitEdit(newValue);

					int rowIndex = getIndex();

					if (rowIndex < 0 || rowIndex >= getTableView().getItems().size()) {
						return;
					}

					// DTO from table
					Interface1553B dto = getTableView().getItems().get(rowIndex);

					// Save edited value
					String finalValue = (newValue == null ? "" : newValue.toString());
					updateDto(dto, dataIndex, finalValue);

					// Update main list
					interface1553BTable1DataList.set(rowIndex, dto);

					// Refresh UI
					getTableView().refresh();
				}

			});

			col.setEditable(true);
		}

		modeHeader.getColumns().addAll(dtoColumns);
		testResultHeader.getColumns().add(modeHeader);

		interface1553BTable1DataTableView.getColumns().add(testResultHeader);

		tableScrollPane.setContent(interface1553BTable1DataTableView);

		return tableScrollPane;
	}

	private void updateDto(Interface1553B dto, int colIndex, String value) {
		switch (colIndex) {
		case 0:
			dto.setC1(value);
			break;
		case 1:
			dto.setC2(value);
			break;
		case 2:
			dto.setC3(value);
			break;
		case 3:
			dto.setC4(value);
			break;
		case 4:
			dto.setC5(value);
			break;
		case 5:
			dto.setC6(value);
			break;
		case 6:
			dto.setC7(value);
			break;
		case 7:
			dto.setC8(value);
			break;
		case 8:
			dto.setC16(value);
			break;
		case 9:
			dto.setC17(value);
			break;
		case 10:
			dto.setC18(value);
			break;
		case 11:
			dto.setC19(value);
			break;
		}
	}

	private String getValueFromDto(Interface1553B dto, int index) {
		switch (index) {
		case 0:
			return dto.getC1();
		case 1:
			return dto.getC2();
		case 2:
			return dto.getC3();
		case 3:
			return dto.getC4();
		case 4:
			return dto.getC5();
		case 5:
			return dto.getC6();
		case 6:
			return dto.getC7();
		case 7:
			return dto.getC8();
		case 8:
			return dto.getC16();
		case 9:
			return dto.getC17();
		case 10:
			return dto.getC18();
		case 11:
			return dto.getC19();
		}
		return "";
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	private Label createMergedCell(String text, boolean bold, int span) {
		Label lbl = new Label(text);
		lbl.setMinHeight(35);
		lbl.setAlignment(Pos.CENTER);
		lbl.setMaxWidth(Double.MAX_VALUE);
		lbl.setStyle("-fx-border-color: black; -fx-padding: 5;" + (bold ? "-fx-font-weight: bold;" : ""));
		return lbl;
	}

	private Label createCell(String text, boolean bold) {
		Label lbl = new Label(text);
		lbl.setMinSize(80, 35);
		lbl.setAlignment(Pos.CENTER);
		lbl.setStyle("-fx-border-color: black; -fx-padding: 5;" + (bold ? "-fx-font-weight: bold;" : ""));
		return lbl;
	}

	private void addRow(GridPane table, int rowIndex, String rowTitle, String... values) {
		Label title = createCell(rowTitle, true);
		table.add(title, 0, rowIndex);

		for (int i = 0; i < values.length; i++) {
			table.add(createCell(values[i], false), i + 1, rowIndex);
		}
	}

	private boolean isInRange(String value) {
		try {
			double v = Double.parseDouble(value);
			return v >= 385.4 && v <= 412.6;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean allChannelsInRange(String ch1, String ch2, String ch3, String ch4) {
		return isInRange(ch1) && isInRange(ch2) && isInRange(ch3) && isInRange(ch4);
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	private HBox createPrintButtonHbox() {

		HBox printButtonContainer = new HBox(); // only create once
		printButtonContainer.setAlignment(Pos.CENTER);
		printButtonContainer.setSpacing(5);
		printButtonContainer.getChildren().addAll(saveButton, fetchButton, printButton);

		saveButton.setOnAction(e -> {
			if (selectedSessionId == null || selectedStageId == null) {
				Notifications.showErrorAlert("Please select Session and Stage.");
				return;
			}

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Please Confirm");
			alert.setContentText(
					"Do you want to continue to save with all pass press 'YES' (or) to edit and save press 'NO'");

			// Remove default OK/CANCEL buttons
			alert.getButtonTypes().clear();

			// Create YES and NO buttons
			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

			// Add them to the alert
			alert.getButtonTypes().addAll(yesButton, noButton);

			// Show popup and check result
			Optional<ButtonType> result = alert.showAndWait();

			Interface1553B dto = interface1553BTable1DataList.get(3);
			Interface1553B dto1 = interface1553BTable1DataList.get(4);
			Interface1553B dto2 = interface1553BTable1DataList.get(5);
			Interface1553B dto3 = interface1553BTable1DataList.get(6);
			if (result.isPresent() && result.get() == yesButton) {

				interface1553BTable1DataTableView.setEditable(false);

				List<Interface1553B> selectedDtos = interface1553BTable1DataList.subList(3, 7);

				List<Interface1553B_Mode> modeList = new ArrayList<>();

				int channelNo = 1; // 🔴 start from CH1

				for (Interface1553B dtoUi : selectedDtos) {

					Interface1553B_Mode mode = new Interface1553B_Mode();

					// 🔴 AUTO CHANNEL ASSIGNMENT
					mode.setChannelNo("Ch" + channelNo);
					channelNo++;

					mode.setValue01(dtoUi.getC1());
					mode.setValue02(dtoUi.getC2());
					mode.setValue03(dtoUi.getC3());
					mode.setValue04(dtoUi.getC4());
					mode.setValue05(dtoUi.getC5());
					mode.setValue06(dtoUi.getC6());
					mode.setValue07(dtoUi.getC7());
					mode.setValue08(dtoUi.getC8());
					mode.setValue16(dtoUi.getC16());
					mode.setValue17(dtoUi.getC17());
					mode.setValue18(dtoUi.getC18());
					mode.setValue19(dtoUi.getC19());

					mode.setSessionId(selectedSessionId);
					mode.setStageId(selectedStageId);

					modeList.add(mode);
				}

				Interface1553B_ModeService interface1553B_ModeService = new Interface1553B_ModeService();

				Response response = interface1553B_ModeService.addInterface1553BForAllChannels(modeList);

				if (response.getResponseCode() == 1) {
					Notifications.showSuccessAlert(response.getResponseMessage());
				} else {
					Notifications.showErrorAlert(response.getResponseMessage());
				}

			} else {
				interface1553BTable1DataTableView.setEditable(true);
			}

		});

		fetchButton.setOnAction(a -> {

			if (selectedSessionId == null || selectedStageId == null) {
				Notifications.showErrorAlert("Please select Session and Stage");
				return;
			}
			printButton.setDisable(false);
			Interface1553B_ModeService interface1553B_ModeService = new Interface1553B_ModeService();

			List<Interface1553B_Mode> dbList = interface1553B_ModeService.getInterfaceModeChecks(selectedSessionId,
					selectedStageId);

			if (dbList == null || dbList.isEmpty()) {
//		        Notifications.showInfoAlert("No data found");
				return;
			}

			// 🔴 Channel rows start index
			int startIndex = 3;

			// 🔴 We expect max 4 channel rows
			int expectedChannelRows = 4;

			// 🔴 Ensure UI list has enough rows (VERY IMPORTANT)
			while (interface1553BTable1DataList.size() < startIndex + expectedChannelRows) {
				interface1553BTable1DataList.add(new Interface1553B());
			}

			// 🔴 Calculate safe update count
			int rowsToUpdate = Math.min(dbList.size(), expectedChannelRows);

			// 🔴 Update existing UI rows instead of replacing list
			for (int i = 0; i < rowsToUpdate; i++) {

				Interface1553B_Mode mode = dbList.get(i);
				Interface1553B uiRow = interface1553BTable1DataList.get(startIndex + i);

				uiRow.setC1(mode.getValue01());
				uiRow.setC2(mode.getValue02());
				uiRow.setC3(mode.getValue03());
				uiRow.setC4(mode.getValue04());
				uiRow.setC5(mode.getValue05());
				uiRow.setC6(mode.getValue06());
				uiRow.setC7(mode.getValue07());
				uiRow.setC8(mode.getValue08());
				uiRow.setC16(mode.getValue16());
				uiRow.setC17(mode.getValue17());
				uiRow.setC18(mode.getValue18());
				uiRow.setC19(mode.getValue19());
			}

			interface1553BTable1DataTableView.refresh();

			if (!interface1553BTable1DataList.isEmpty()) {
				Notifications.showSuccessAlert("Data Fetched Successfully");
			} else {
				Notifications.showSuccessAlert("There is no data to fetch");
			}
		});

		printButton.setOnAction(e -> {

			if (uutTypeField.getValue() == null || serialnumber.getValue() == null || sessioName.getValue() == null
					|| stageName.getValue() == null) {
				Notifications.showErrorAlert("Please select all the required details to print.");
				return;
			}

			printButtonContainer.setVisible(false);
			printButtonContainer.setManaged(false);
			
			
			interface1553BMainContainerGridPane.applyCss();
			interface1553BMainContainerGridPane.layout();

			Stage stage = (Stage) interface1553BMainContainerGridPane.getScene().getWindow();

			tmpUutLabel = new Label("UUT Type : " + 
		            (uutTypeField.getValue() == null ? "" : uutTypeField.getValue()));

		    tmpSerialLabel = new Label("Serial No : " + 
		            (serialnumber.getValue() == null ? "" : serialnumber.getValue()));

		    tmpSessionLabel = new Label("Session Name : " + 
		            (sessioName.getValue() == null ? "" : sessioName.getValue()));

		    tmpStageLabel = new Label("Stage Name : " + 
		            (stageName.getValue() == null ? "" : stageName.getValue()));
		    
		    tmpSessionLabel.setWrapText(true);
			tmpStageLabel.setWrapText(true);
			interface1553BTable1DataTableView.getSelectionModel().clearSelection();
			tmpUutLabel.getStyleClass().add("nonheading-label");
			tmpSerialLabel.getStyleClass().add("nonheading-label");
			tmpSessionLabel.getStyleClass().add("nonheading-label");
			tmpStageLabel.getStyleClass().add("nonheading-label");
			
			tmpSerialLabel.setWrapText(true);
//			tmpSerialLabel.setMaxWidth(150);

			tmpSessionLabel.setWrapText(true);
//			tmpSessionLabel.setMaxWidth(500);

			tmpStageLabel.setWrapText(true);
//			tmpStageLabel.setMaxWidth(300);
			
			HBox uutBox = (HBox) uutTypeField.getParent();
			HBox serialBox = (HBox) serialnumber.getParent();
			HBox sessionBox = (HBox) sessioName.getParent();
			HBox stageBox = (HBox) stageName.getParent();

			try {

				uutTypeField.setVisible(false);
				serialnumber.setVisible(false);
				sessioName.setVisible(false);
				stageName.setVisible(false);

				uutBox.getChildren().add(tmpUutLabel);
				serialBox.getChildren().add(tmpSerialLabel);
				sessionBox.getChildren().add(tmpSessionLabel);
				stageBox.getChildren().add(tmpStageLabel);

				interface1553BMainContainerGridPane.applyCss();
				interface1553BMainContainerGridPane.layout();
				interface1553BTable1DataTableView.getSelectionModel().clearSelection();
				exportPageToPDF(stage, interface1553BMainContainerGridPane);
				Notifications.showSuccessAlert("Report has been downloaded successfully");
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {

				uutBox.getChildren().remove(tmpUutLabel);
				serialBox.getChildren().remove(tmpSerialLabel);
				sessionBox.getChildren().remove(tmpSessionLabel);
				stageBox.getChildren().remove(tmpStageLabel);

				uutTypeField.setVisible(true);
				serialnumber.setVisible(true);
				sessioName.setVisible(true);
				stageName.setVisible(true);

				printButtonContainer.setVisible(true);
				printButtonContainer.setManaged(true);

				interface1553BMainContainerGridPane.applyCss();
				interface1553BMainContainerGridPane.layout();
			}
		});

		return printButtonContainer;
	}

	private Interface1553B mapModeToUi(Interface1553B_Mode mode) {

		Interface1553B ui = new Interface1553B();

		// 🔴 THIS IS THE MISSING PART

		ui.setC1(mode.getValue01());
		ui.setC2(mode.getValue02());
		ui.setC3(mode.getValue03());
		ui.setC4(mode.getValue04());
		ui.setC5(mode.getValue05());
		ui.setC6(mode.getValue06());
		ui.setC7(mode.getValue07());
		ui.setC8(mode.getValue08());
		ui.setC16(mode.getValue16());
		ui.setC17(mode.getValue17());
		ui.setC18(mode.getValue18());
		ui.setC19(mode.getValue19());

		return ui;
	}

	private void exportPageToPDF(Stage stage, GridPane root) {

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save PDF");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

		File file = chooser.showSaveDialog(stage);
		if (file == null)
			return;

		try {
			// Take snapshot of full page
			SnapshotParameters params = new SnapshotParameters();
			params.setTransform(new Scale(2, 2)); // Higher resolution
			WritableImage image = root.snapshot(params, null);

			// Convert snapshot to BufferedImage
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

			// Convert BufferedImage → ImageData
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			ImageData imgData = ImageDataFactory.create(baos.toByteArray());
			Image pdfImage = new Image(imgData);

			// Create PDF document
			PdfWriter writer = new PdfWriter(file.getAbsolutePath());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A3.rotate());

			// Fit to page
			pdfImage.scaleToFit(PageSize.A3.rotate().getWidth(), PageSize.A3.rotate().getHeight());
			pdfImage.setAutoScale(true);

			// Add image to PDF
			document.add(pdfImage);

			document.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
