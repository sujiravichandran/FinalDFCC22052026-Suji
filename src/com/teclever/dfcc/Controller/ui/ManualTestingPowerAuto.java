package com.teclever.dfcc.Controller.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.PowerAutoDataAnalysis;
import com.teclever.datastore.entities.PowerManDataAnalysis;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.PowerAuto;
import com.teclever.dfcc.model.PowerManMk1;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
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

class ManualTesting1PowerAutoDataTableViewFactory implements TableViewFactory<PowerAuto> {
	@Override
	public CustomTableView<PowerAuto> createTableView(ObservableList<PowerAuto> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, PowerAuto.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTestingPowerAuto {

	private GridPane powerManMk1MainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private HBox headingHbox3 = new HBox(10);
	private Label titleLabel = new Label();
	private Label chLabel = new Label();
	private Label chLabel2 = new Label();

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

	private StackPane powerManMk1Table1StackPane = new StackPane();
	private GridPane powerManMk1Table1GridPane = new GridPane();
	private StackPane powerManMk12Table1StackPane = new StackPane();
	private GridPane powerManMk12Table1GridPane = new GridPane();

	private StackPane powerManMk1Table2StackPane = new StackPane();
	private GridPane powerManMk1Table2GridPane = new GridPane();
	private GridPane powerManMk1Table22GridPane = new GridPane();

	private StackPane powerManMk1Table22StackPane = new StackPane();

	private ObservableList<PowerAuto> powerAutoMk1Table1DataList = FXCollections.observableArrayList();
	private CustomTableView<PowerAuto> powerAutoMk1Table1DataTableView;
	private TableViewFactory<PowerAuto> powerAutoMk1Table1DataFactory = new ManualTesting1PowerAutoDataTableViewFactory();

	private String uutId = "UUT1";
	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label();

	private Label tmpUutLabel;
	private Label tmpSerialLabel;
	private Label tmpSessionLabel;
	private Label tmpStageLabel;

	private HBox tbNoHBox = new HBox();
	private Label tbNol1 = new Label("TB NO.:001 ");
	private Label tbNol2 = new Label("TB NO.:009 ");
	private Label tbNol3 = new Label("TB NO.:015 ");
	private Label passFail1 = new Label("PASS/FAIL");
	private Label passFail2 = new Label("PASS/FAIL");
	private Label passFail3 = new Label("PASS/FAIL");
	private Label testRep = new Label("TESTING REP: ");
	private Label testRepDate = new Label("DATE: ");
	private Label tiqmRep = new Label("TI/QM REP: ");
	private Label tiqmRepDate = new Label("DATE: ");
	private SessionService s = new SessionService();
	private HBox testCarriedOutHBox = new HBox();
	private Label testCarriedOutBy = new Label("TEST CARRIED OUT BY:");

	private HBox dateHBox = new HBox();
	private Label date = new Label("DATE: ");

	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");
	private Button saveButton = new Button("Save");
	private Button fetchButton = new Button("Fetch");

	private String selectedSessionId;
	private String selectedStageName;
	private String selectedStageId;

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

	private String selectedUutId;
	private String selectedSno;
	
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();

	private TrailSessionEntityService t = new TrailSessionEntityService();

	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();

	public ManualTestingPowerAuto() {

		// Normal sessions
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		// Trial sessions
		TrailSessionResponse t1 = t.getActiveTrailSessionId();
		sessionListTrail = t1.getListOfSession();

		initializeUUTTypeComboBox();

	}

	public GridPane createPowerAutoMainContainerGridPane() {
		saveButton.setDisable(true);
		fetchButton.setDisable(true);
		printButton.setDisable(true);
		powerManMk1MainContainerGridPane.getChildren().clear();
		powerManMk1MainContainerGridPane.getColumnConstraints().clear();
		powerManMk1MainContainerGridPane.getRowConstraints().clear();

		powerManMk1MainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		powerManMk1MainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(9);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(35);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(20);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(5);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(5);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(10);

		powerManMk1MainContainerGridPane.setHgap(10);
		powerManMk1MainContainerGridPane.setVgap(10);

		powerManMk1MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		powerManMk1MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,
				sixthRow, seventhRow);
		powerManMk1MainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		powerManMk1MainContainerGridPane.add(createHeadingBox(), 0, 0);
		powerManMk1MainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);

		powerManMk1MainContainerGridPane.add(createPowerAutoTable1Content(), 0, 2);

		powerManMk1MainContainerGridPane.add(createNoteBox(), 0, 3);
		powerManMk1MainContainerGridPane.add(createRepBox(), 0, 4);
		powerManMk1MainContainerGridPane.add(createRepDateBox(), 0, 5);
		powerManMk1MainContainerGridPane.add(createPrintButtonHbox(), 0, 6);

		return powerManMk1MainContainerGridPane;
	}

	private GridPane createHeadingBox() {

		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(33);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(33);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);

		topBoxGridPane.getColumnConstraints().addAll(firstColumn);
		topBoxGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		// 🔥 CREATE NEW INSTANCES every time (NO reuse)
		HBox headingHbox1 = new HBox();
		HBox headingHbox2 = new HBox();
		HBox headingHbox3 = new HBox();

		Label title = new Label(titleLabel.getText());
		Label ch = new Label(chLabel.getText());
		Label ch2 = new Label(chLabel2.getText());

		headingHbox1.setAlignment(Pos.CENTER);
		title.setUnderline(true);
		title.getStyleClass().add("advanced-testing-title");

		headingHbox2.setAlignment(Pos.CENTER);
		ch.getStyleClass().add("advanced-testing-title");

		headingHbox3.setAlignment(Pos.CENTER);
		ch2.getStyleClass().add("advanced-testing-title");
		noteLabel.setStyle("advanced-testing-title");

		headingHbox1.getChildren().add(title);
		headingHbox2.getChildren().add(ch);
		headingHbox3.getChildren().add(ch2);

		topBoxGridPane.add(headingHbox1, 0, 0);
		topBoxGridPane.add(headingHbox2, 0, 1);
		topBoxGridPane.add(headingHbox3, 0, 2);

		return topBoxGridPane;
	}

	private GridPane createNoteBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(60);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(20);

		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(20);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(10);

		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(10);

		RowConstraints row3 = new RowConstraints();
		row3.setPercentHeight(10);

		grid.getColumnConstraints().addAll(col1, col2, col3);

		grid.getRowConstraints().addAll(row1, row2, row3);

		grid.add(noteLabel, 0, 0);
		grid.add(tbNol1, 1, 0);
		grid.add(passFail1, 2, 0);

		grid.add(tbNol2, 1, 1);
		grid.add(passFail2, 2, 1);

		grid.add(tbNol3, 1, 2);
		grid.add(passFail3, 2, 2);

		noteLabel.getStyleClass().add("nonheading-label");

		tbNol1.getStyleClass().add("nonheading-label");
		tbNol2.getStyleClass().add("nonheading-label");
		tbNol3.getStyleClass().add("nonheading-label");

		passFail1.getStyleClass().add("nonheading-label");
		passFail2.getStyleClass().add("nonheading-label");
		passFail3.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(tbNol1, HPos.RIGHT);
		GridPane.setHalignment(passFail1, HPos.LEFT);

		GridPane.setHalignment(tbNol2, HPos.RIGHT);
		GridPane.setHalignment(passFail2, HPos.LEFT);

		GridPane.setHalignment(tbNol3, HPos.RIGHT);
		GridPane.setHalignment(passFail3, HPos.LEFT);

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
		serialnumber.setPromptText("select Serial No");
		serialnumber.setEditable(true);
		sessioName.setPromptText("select Session");
		sessioName.setEditable(true);
		stageName.setPromptText("select Stage Name");
		stageName.setEditable(true);
		uutHbox.getChildren().add(uutTypeField);

		HBox serialHbox = new HBox(10);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);

		dfccSessionName.getStyleClass().add("nonheading-label");
		sessionHbox.getChildren().addAll(sessioName);

		serialHbox.getChildren().addAll(serialnumber);

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

	private void getSerialNo() {
		List serailNumber = FXCollections.observableArrayList(unitGetDetailsManagement.getAllDfccSerialNo());
		serialnumber.setItems((ObservableList<String>) serailNumber);

	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
			fetchButton.setDisable(true);
			printButton.setDisable(true);

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initalizeSerialNoComboBox(uutId);

			selectedUutId = uutId;
			
			if (uutId.equalsIgnoreCase("UUT1")) {
                titleLabel.setText("LCA DFCC MK-1 POWER CHECK OBSERVATIONS");
                chLabel.setText("STEADY STATE INPUT CURRENT MONITOR");
                chLabel2.setText("(AS OBSERVED IN FRONT PANEL METERS OF DFCC MK-1 POWER SUPPLIES IN AETS)");
                noteLabel.setText("IMPORTANT: These Test Result are 'FOR RECORD PURPOSE ONLY.'");
            } else if (uutId.equalsIgnoreCase("UUT2")) {
                titleLabel.setText("LCA DFCC MK-1A POWER CHECK OBSERVATIONS");
                chLabel.setText("STEADY STATE INPUT CURRENT MONITOR");
                chLabel2.setText("(AS OBSERVED IN FRONT PANEL METERS OF DFCC MK-1A POWER SUPPLIES IN AETS)");
                noteLabel.setText("IMPORTANT: These Test Result are 'FOR RECORD PURPOSE ONLY.'");
            } else {
                titleLabel.setText("LCA DFCC MK-2 POWER CHECK OBSERVATIONS");
                chLabel.setText("STEADY STATE INPUT CURRENT MONITOR");
                chLabel2.setText("(AS OBSERVED IN FRONT PANEL METERS OF DFCC MK-2 POWER SUPPLIES IN AETS)");
                noteLabel.setText("IMPORTANT: These Test Result are 'FOR RECORD PURPOSE ONLY.'");
            }

			
			createPowerAutoMainContainerGridPane();

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
	
	private void addSearchFunctionality(ComboBox<String> comboBox, ObservableList<String> originalItems) {

		comboBox.setEditable(true);
		comboBox.setItems(originalItems);

		TextField editor = comboBox.getEditor();

		editor.setOnKeyReleased(event -> {

			String text = editor.getText();

			ObservableList<String> filteredList = FXCollections.observableArrayList();

			if (text == null || text.isEmpty()) {
				filteredList.addAll(originalItems);
			} else {
				for (String item : originalItems) {
					if (item.toLowerCase().contains(text.toLowerCase())) {
						filteredList.add(item);
					}
				}
			}

			comboBox.setItems(filteredList);
			comboBox.getEditor().positionCaret(text.length());
			comboBox.show();
		});

// Prevent auto-selection
		comboBox.setOnAction(e -> {
			if (comboBox.getSelectionModel().getSelectedItem() != null) {
				editor.setText(comboBox.getSelectionModel().getSelectedItem());
			}
		});
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
//			selectedSno = serialnumber.getSelectionModel().getSelectedItem();
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
			selectedSno = serialnumber.getSelectionModel().getSelectedItem();
			 if (selectedSno != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
		            initializeSessionComboBox(selectedSno);
		        }else {
		        	initializeTrialSessionComboBox(selectedSno);
		        }

		});

	    
	}
	
	private void initializeTrialSessionComboBox(String selectedDfccNo) {

	    sessionTypeList.clear();

	    List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
	            .filter(t ->
	                    Objects.equals(t.getUutId(), selectedUutId) &&
	                    Objects.equals(t.getDfccSNo(), selectedSno) &&
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
				.filter(t -> Objects.equals(t.getUutId(), selectedUutId) && Objects.equals(t.getDfccSNo(), selectedSno)
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

	private StackPane createPowerAutoTable1Content() {

		powerManMk1Table1StackPane.getChildren().clear();
		powerManMk1Table1StackPane.getStyleClass().add("tab-content-container");
		powerManMk1Table1StackPane.getChildren().clear();
		powerManMk1Table1StackPane.getChildren().add(powerAutoTableResultGridPane());
		return powerManMk1Table1StackPane;
	}

	private GridPane powerAutoTableResultGridPane() {
		powerManMk1Table1GridPane.getChildren().clear();
		powerManMk1Table1GridPane.getColumnConstraints().clear();
		powerManMk1Table1GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		powerManMk1Table1GridPane.getColumnConstraints().add(firstColumn);
		powerManMk1Table1GridPane.getRowConstraints().add(firstRow);

		powerManMk1Table1GridPane.add(createPowerAutoMk1Table1DataTable(), 0, 0);

		return powerManMk1Table1GridPane;
	}

	private ScrollPane createPowerAutoMk1Table1DataTable() {

		powerAutoMk1Table1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(powerAutoMk1Table1DataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// --- ONLY ONE DEFAULT ROW ---
				PowerAuto dto = new PowerAuto();
				dto.setInputVoltageSetting("001");
				dto.setDescription("28");
				dto.setExpectedValue("5.3");
				powerAutoMk1Table1DataList.add(dto);

				PowerAuto dto1 = new PowerAuto();
				dto1.setInputVoltageSetting("009");
				dto1.setDescription("16");
				dto1.setExpectedValue("8.2");
				powerAutoMk1Table1DataList.add(dto1);

				PowerAuto dto2 = new PowerAuto();
				dto2.setInputVoltageSetting("015");
				dto2.setDescription("32");
				dto2.setExpectedValue("5.0");
				powerAutoMk1Table1DataList.add(dto2);

				// Create table after data is ready
				powerAutoMk1Table1DataTableView = powerAutoMk1Table1DataFactory
						.createTableView(powerAutoMk1Table1DataList, false, false);

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					powerAutoMk1Table1DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						case "INPUT VOLTAGE SETTING":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(130);
							Label mainHeader = new Label("TEST BLOCK NUMBER");
							mainHeader.setWrapText(true);
							mainHeader.setTextAlignment(TextAlignment.CENTER);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinWidth(130);
							mainHeader.setMaxWidth(130);
							VBox headerBox = new VBox(mainHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("inputvoltagesetting"));
							break;

						case "DESCRIPTION":
							column.setText(null);
							column.setMinWidth(300);
							column.setMaxWidth(300);
							Label mainHeader1 = new Label("INPUTVOLTAGE SETTING\n(VOLTS DC)");
							mainHeader1.setWrapText(true);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinWidth(300);
							mainHeader1.setMaxWidth(300);
							VBox headerBox1 = new VBox(mainHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("description"));
							break;

						case "EXPECTED VALUE":
							column.setText(null);
							column.setMinWidth(350);
							column.setMaxWidth(350);
							Label mainHeader2 = new Label("MAXIMUM VALUE\n(AMPERES)");
							mainHeader2.setWrapText(true);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinWidth(350);
							mainHeader2.setMaxWidth(350);
							VBox headerBox2 = new VBox(mainHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("expectedvalue"));
							break;

						case "CHANNEL1":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader3 = new Label("CHANNEL 1\n(AMPERES)");
							mainHeader3.setWrapText(true);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinWidth(185);
							mainHeader3.setMaxWidth(185);
							VBox headerBox3 = new VBox(mainHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
							break;

						case "CHANNEL2":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader4 = new Label("CHANNEL 2\n(AMPERES)");
							mainHeader4.setWrapText(true);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinWidth(185);
							mainHeader4.setMaxWidth(185);
							VBox headerBox4 = new VBox(mainHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel2"));
							break;

						case "CHANNEL3":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader5 = new Label("CHANNEL 3\n(AMPERES)");
							mainHeader5.setWrapText(true);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinWidth(185);
							mainHeader5.setMaxWidth(185);
							VBox headerBox5 = new VBox(mainHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel3"));
							break;

						case "CHANNEL4":
							column.setText(null);
							column.setMinWidth(190);
							column.setMaxWidth(190);
							Label mainHeader6 = new Label("CHANNEL 4\n(AMPERES)");
							mainHeader6.setWrapText(true);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setMinWidth(190);
							mainHeader6.setMaxWidth(190);
							VBox headerBox6 = new VBox(mainHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel4"));
							break;

						default:
//							column.setMinWidth(120);
//							column.setMaxWidth(120);
							break;
						}

					});

					tableScrollPane.setContent(powerAutoMk1Table1DataTableView);
					powerAutoMk1Table1DataTableView.setEditable(true);

					// -------- INPUT VOLTAGE ----------
					TableColumn<PowerAuto, String> inputColumn = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(0);

					inputColumn.setCellValueFactory(new PropertyValueFactory<>("inputVoltageSetting"));
					inputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					inputColumn.setOnEditCommit(e -> e.getRowValue().setInputVoltageSetting(e.getNewValue()));

					// -------- DESCRIPTION ----------
					TableColumn<PowerAuto, String> descColumn = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(1);

					descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
					descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					descColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

					// -------- EXPECTED VALUE ----------
					TableColumn<PowerAuto, String> expectedColumn = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(2);

					expectedColumn.setCellValueFactory(new PropertyValueFactory<>("expectedValue"));
					expectedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					expectedColumn.setOnEditCommit(e -> e.getRowValue().setExpectedValue(e.getNewValue()));

					// -------- CHANNEL 1 ----------
					TableColumn<PowerAuto, String> ch1Column = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(3);

					ch1Column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
					ch1Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch1Column.setOnEditCommit(e -> e.getRowValue().setChannel1(e.getNewValue()));

					// -------- CHANNEL 2 ----------
					TableColumn<PowerAuto, String> ch2Column = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(4);

					ch2Column.setCellValueFactory(new PropertyValueFactory<>("channel2"));
					ch2Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch2Column.setOnEditCommit(e -> e.getRowValue().setChannel2(e.getNewValue()));

					// -------- CHANNEL 3 ----------
					TableColumn<PowerAuto, String> ch3Column = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(5);

					ch3Column.setCellValueFactory(new PropertyValueFactory<>("channel3"));
					ch3Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch3Column.setOnEditCommit(e -> e.getRowValue().setChannel3(e.getNewValue()));

					// -------- CHANNEL 4 ----------
					TableColumn<PowerAuto, String> ch4Column = (TableColumn<PowerAuto, String>) powerAutoMk1Table1DataTableView
							.getColumns().get(6);

					ch4Column.setCellValueFactory(new PropertyValueFactory<>("channel4"));
					ch4Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch4Column.setOnEditCommit(e -> e.getRowValue().setChannel4(e.getNewValue()));

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to load PowerMan MK1 data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
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

	private boolean isEmpty(String value) {
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

			

			PowerAuto dto1 = powerAutoMk1Table1DataList.get(0);
			PowerAuto dto2 = powerAutoMk1Table1DataList.get(1);
			PowerAuto dto3 = powerAutoMk1Table1DataList.get(2);
			if (isEmpty(dto1.getChannel1()) || isEmpty(dto1.getChannel2()) || isEmpty(dto1.getChannel3())
					|| isEmpty(dto1.getChannel4()) || isEmpty(dto2.getChannel1()) || isEmpty(dto2.getChannel2())
					|| isEmpty(dto2.getChannel3()) || isEmpty(dto2.getChannel4()) || isEmpty(dto3.getChannel1())
					|| isEmpty(dto3.getChannel2()) || isEmpty(dto3.getChannel3()) || isEmpty(dto3.getChannel4())) {
				Notifications.showErrorAlert("Please enter the all the field values");
				return;
			}
			PowerAutoDataAnalysis powerAuto = new PowerAutoDataAnalysis();
			powerAuto.setFirstRowch1(dto1.getChannel1());
			powerAuto.setFirstRowch2(dto1.getChannel2());
			powerAuto.setFirstRowch3(dto1.getChannel3());
			powerAuto.setFirstRowch4(dto1.getChannel4());

			powerAuto.setSecondRowch1(dto2.getChannel1());
			powerAuto.setSecondRowch2(dto2.getChannel2());
			powerAuto.setSecondRowch3(dto2.getChannel3());
			powerAuto.setSecondRowch4(dto2.getChannel4());

			powerAuto.setThirdRowch1(dto3.getChannel1());
			powerAuto.setThirdRowch2(dto3.getChannel2());
			powerAuto.setThirdRowch3(dto3.getChannel3());
			powerAuto.setThirdRowch4(dto3.getChannel4());

			advancedDataAnalysisManagement.addPowerAutoConfig(powerAuto, selectedSessionId, selectedStageId);
			Notifications.showSuccessAlert("Data Saved Successfully");

		});

		fetchButton.setOnAction(e -> {
			PowerAutoDataAnalysis entity = advancedDataAnalysisManagement.getPowerAutoConfig(selectedSessionId,
					selectedStageId);

			if (entity.getPowerAutoId() == 0) {
				Notifications.showErrorAlert("Not configured");
				return;
			}
			
			printButton.setDisable(false);
			PowerAuto dto1 = powerAutoMk1Table1DataList.get(0);
			PowerAuto dto2 = powerAutoMk1Table1DataList.get(1);
			PowerAuto dto3 = powerAutoMk1Table1DataList.get(2);

			dto1.setChannel1(entity.getFirstRowch1());
			dto1.setChannel2(entity.getFirstRowch2());
			dto1.setChannel3(entity.getFirstRowch3());
			dto1.setChannel4(entity.getFirstRowch4());

			dto2.setChannel1(entity.getSecondRowch1());
			dto2.setChannel2(entity.getSecondRowch2());
			dto2.setChannel3(entity.getSecondRowch3());
			dto2.setChannel4(entity.getSecondRowch4());

			dto3.setChannel1(entity.getThirdRowch1());
			dto3.setChannel2(entity.getThirdRowch2());
			dto3.setChannel3(entity.getThirdRowch3());
			dto3.setChannel4(entity.getThirdRowch4());

			powerAutoMk1Table1DataTableView.refresh();
			
			if(!powerAutoMk1Table1DataList.isEmpty() )
			{
				Notifications.showSuccessAlert("Data fetched Successfully.");	
			}else {
				Notifications.showSuccessAlert("Data not configured.");
			}

		});

		printButton.setOnAction(e -> {

			if (uutTypeField.getValue() == null || serialnumber.getValue() == null || sessioName.getValue() == null
					|| stageName.getValue() == null) {
				Notifications.showErrorAlert("Please select all the required details to print.");
				return;
			}
			PowerAuto dto1 = powerAutoMk1Table1DataList.get(0);
			PowerAuto dto2 = powerAutoMk1Table1DataList.get(1);
			PowerAuto dto3 = powerAutoMk1Table1DataList.get(2);
			if (dto1.getChannel1() == null || dto1.getChannel2() == null || dto1.getChannel3() == null
					|| dto1.getChannel4() == null || dto2.getChannel1() == null || dto2.getChannel2() == null
					|| dto2.getChannel3() == null || dto2.getChannel4() == null || dto3.getChannel1() == null
					|| dto3.getChannel2() == null || dto3.getChannel3() == null || dto3.getChannel4() == null) {
				Notifications.showErrorAlert("Please enter the all the field values and properly save check");
				return;
			}


			printButtonContainer.setVisible(false);
			printButtonContainer.setManaged(false);
			
			

			powerManMk1MainContainerGridPane.applyCss();
			powerManMk1MainContainerGridPane.layout();

			Stage stage = (Stage) powerManMk1MainContainerGridPane.getScene().getWindow();

			tmpUutLabel = new Label("UUT Type : " + 
		            (uutTypeField.getValue() == null ? "" : uutTypeField.getValue()));

		    tmpSerialLabel = new Label("Serial No : " + 
		            (serialnumber.getValue() == null ? "" : serialnumber.getValue()));

		    tmpSessionLabel = new Label("Session Name : " + 
		            (sessioName.getValue() == null ? "" : sessioName.getValue()));

		    tmpStageLabel = new Label("Stage Name : " + 
		            (stageName.getValue() == null ? "" : stageName.getValue()));
		    
			tmpUutLabel.getStyleClass().add("nonheading-label");
			tmpSerialLabel.getStyleClass().add("nonheading-label");
			tmpSessionLabel.getStyleClass().add("nonheading-label");
			tmpStageLabel.getStyleClass().add("nonheading-label");
			
			tmpSessionLabel.setWrapText(true);
			tmpStageLabel.setWrapText(true);
			powerAutoMk1Table1DataTableView.getSelectionModel().clearSelection();
			
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

				powerManMk1MainContainerGridPane.applyCss();
				powerManMk1MainContainerGridPane.layout();
//				powerAutoMk1Table1DataTableView.getSelectionModel().clearSelection();
				exportPageToPDF(stage, powerManMk1MainContainerGridPane);
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

				powerManMk1MainContainerGridPane.applyCss();
				powerManMk1MainContainerGridPane.layout();
			}
		});

		return printButtonContainer;
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
