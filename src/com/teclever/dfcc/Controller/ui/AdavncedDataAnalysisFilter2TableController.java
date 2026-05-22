package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.AdaFilter1;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class ADAFilter2TableViewFactory implements TableViewFactory<AdaFilter1> {
	@Override
	public CustomTableView<AdaFilter1> createTableView(ObservableList<AdaFilter1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AdaFilter1.class, addUserColumn, addCheckboxColumn);
	}
}

class DetailedDataTableViewFactory2 implements TableViewFactory<DetailedData> {
	@Override
	public CustomTableView<DetailedData> createTableView(ObservableList<DetailedData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DetailedData.class, addUserColumn, addCheckboxColumn);
	}
}

public class AdavncedDataAnalysisFilter2TableController {

	private GridPane adaFiletr2MainContainerGridPane = new GridPane();
	private ProgressIndicator progressIndicator = new ProgressIndicator();
	private VBox progressbox = new VBox();
	private String lastLoadedUutId = null;
//	UUT
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();

//	Stage Name
	private ComboBox<String> stageName = new ComboBox<String>();
//	Serial Number
	private ComboBox<String> serialnumber = new ComboBox<String>();
//	Session Name
	private ComboBox<String> sessioName = new ComboBox<String>();
	
//	Signal Name
	private ComboBox<String> signalNameField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> signalNameDataList;
	private ObservableList<String> signalNameTypeList = FXCollections.observableArrayList();
	
//	Sru Type
	private ComboBox<String> sruTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> sruTypeDataList;
	private ObservableList<String> sruTypeList = FXCollections.observableArrayList();

//Step No
	private ComboBox<String> stepField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> stepDataList;
	private ObservableList<String> stepTypeList = FXCollections.observableArrayList();

//TPGPH No
	private ComboBox<String> tpgphField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> tpgphDataList;
	private ObservableList<String> tpgphTypeList = FXCollections.observableArrayList();

//Channels	
	private ComboBox<String> channelField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> channelDataList;
	private ObservableList<String> channelTypeList = FXCollections.observableArrayList();

	private StackPane adaFilter2StackPane = new StackPane();

	private TableViewFactory<AdaFilter1> adaFilter2DataFactory = new ADAFilter1TableViewFactory();

	private ObservableList<AdaFilter1> adaFiletr2DataList = FXCollections.observableArrayList();

	private CustomTableView<AdaFilter1> adaFiletr2DataTableView;

	private String selectedSessionId;
	private String selectedStageName;
	private String selectedStageId;
	private String selectedStep;
	private String selectedTpgph;
	private String selectedSru;
	private String selectedSignal;

	private String selectedUutId;
	private String selectedSno;

	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("Advanced Failure Display");

	private HBox serialHbox = new HBox(10);
	private Label dfccSNoLabel = new Label("DFCC Serial Number : ");
	
	private Label dfccSessionName = new Label("Session Name : ");

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private TextField dfccSNoText = new TextField();
	private Label editableLabel = new Label("Click to edit");
	private TextField editField = new TextField(editableLabel.getText());
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private Label stageNameLabel = new Label("Stage Name");

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String uutId;
	private SessionService s = new SessionService();
	
	private Button print = new Button("Print");
	private Button view = new Button("View Result");
//	private Button filter = new Button("Filter Result");
	
	private Label note = new Label("Note: This will analyze only failed results");

	
	
	private TableViewFactory<DetailedData> detailedDataFactory = new DetailedDataTableViewFactory2();
	private ObservableList<DetailedData> detailedDataList = FXCollections.observableArrayList();
	private CustomTableView<DetailedData> detailedDataTableView;

	private FilteredList<DetailedData> filteredData = new FilteredList<>(detailedDataList, p -> true);

	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	private TrailSessionEntityService t = new TrailSessionEntityService();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	public AdavncedDataAnalysisFilter2TableController() {


	    // Normal sessions
	    SessionResponse s1 = s.getAllSession();
	    sessionList = s1.getListOfSession();

	    // Trial sessions
	    TrailSessionResponse t1 = t.getActiveTrailSessionId();
	    sessionListTrail = t1.getListOfSession();
		initializeUUTTypeComboBox();

	}

	public GridPane createFilter2TableContainerGridPane() {
//		 filter.setDisable(true);
		 view.setDisable(true);
		adaFiletr2MainContainerGridPane.getChildren().clear();
		adaFiletr2MainContainerGridPane.getColumnConstraints().clear();
		adaFiletr2MainContainerGridPane.getRowConstraints().clear();

		adaFiletr2MainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		adaFiletr2MainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(73);

		adaFiletr2MainContainerGridPane.setHgap(10);
		adaFiletr2MainContainerGridPane.setVgap(10);

		adaFiletr2MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		adaFiletr2MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		adaFiletr2MainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		adaFiletr2MainContainerGridPane.add(headingGridPane(), 0, 0);
		adaFiletr2MainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		adaFiletr2MainContainerGridPane.add(createDetailedDataTable(), 0, 2);

		return adaFiletr2MainContainerGridPane;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(13);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(13);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(13);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(13);

		ColumnConstraints fivthColumn = new ColumnConstraints();
		fivthColumn.setPercentWidth(13);

		ColumnConstraints sixthColumn = new ColumnConstraints();
		sixthColumn.setPercentWidth(13);

		ColumnConstraints seventhColumn = new ColumnConstraints();
		seventhColumn.setPercentWidth(13);



		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);
		
		RowConstraints sedcondRow = new RowConstraints();
		sedcondRow.setPercentHeight(50);
		
		

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fivthColumn, sixthColumn,seventhColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow, sedcondRow);

		dfccSNoLabel.getStyleClass().add("nonheading-label");

		HBox uutHbox = new HBox(10);
		uutHbox.setAlignment(Pos.CENTER_LEFT);
		uutHbox.setFillHeight(true);
		uutTypeField.setPromptText("select UUT");
		serialnumber.setPromptText("select Serial No");
		serialnumber.setEditable(true);
		sessioName.setPromptText("select Session");
		sessioName.setEditable(true);
		stageName.setPromptText("select Stage");
		stageName.setEditable(true);
		uutHbox.getChildren().add(uutTypeField);

		HBox serialHbox = new HBox(5);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(5);
		sessionHbox.setAlignment(Pos.CENTER);

		dfccSessionName.getStyleClass().add("nonheading-label");
		sessionHbox.getChildren().addAll(sessioName);

		serialHbox.getChildren().addAll(serialnumber);

		HBox stageNameHbox = new HBox(5);
		stageNameHbox.setAlignment(Pos.CENTER_RIGHT);
		stageNameLabel.getStyleClass().add("nonheading-label");
		stageNameHbox.getChildren().addAll(stageName);

		HBox signalHbox = new HBox(5);
		signalHbox.setAlignment(Pos.CENTER);
		signalHbox.getChildren().addAll(signalNameField);
		signalNameField.setPromptText("select Signal");
		signalNameField.setEditable(true);

		HBox stepHbox = new HBox(5);
		stepHbox.setAlignment(Pos.CENTER_RIGHT);
		stepHbox.getChildren().addAll(stepField);
		stepField.setPromptText("select Step");
		stepField.setEditable(true);
		
		HBox tpgphHbox = new HBox(10);
		tpgphHbox.setAlignment(Pos.CENTER_RIGHT);
		tpgphHbox.getChildren().addAll(tpgphField);
		tpgphField.setPromptText("select Tpgph");
		tpgphField.setEditable(true);
		
		HBox sruTypeHbox = new HBox(5);
		sruTypeHbox.setAlignment(Pos.CENTER_RIGHT);
		sruTypeHbox.getChildren().addAll(sruTypeField);
		sruTypeField.setPromptText("select Sru");
		sruTypeField.setEditable(true);
		

//		signalNameField.valueProperty().addListener((obs, o, n) -> applyFilters());
//		tpgphField.valueProperty().addListener((obs, o, n) -> applyFilters());
//		stepField.valueProperty().addListener((obs, o, n) -> applyFilters());
//		sruTypeField.valueProperty().addListener((obs, o, n) -> applyFilters());
//		stageName.valueProperty().addListener((obs, o, n) -> applyFilters());
//		serialnumber.valueProperty().addListener((obs, o, n) -> applyFilters());
//		sessioName.valueProperty().addListener((obs, o, n) -> applyFilters());
		
		serialAlternateGridPane.setHgap(10);
		serialAlternateGridPane.setAlignment(Pos.CENTER);
		
		
		serialAlternateGridPane.add(uutHbox, 0, 0);
		serialAlternateGridPane.add(serialHbox, 1, 0);
		serialAlternateGridPane.add(sessionHbox, 2, 0);
		serialAlternateGridPane.add(stageNameHbox, 3, 0);
		serialAlternateGridPane.add(signalHbox, 4, 0);
		serialAlternateGridPane.add(stepHbox, 5, 0);
		serialAlternateGridPane.add(tpgphHbox, 6, 0);
		serialAlternateGridPane.add(printButtonHbox(), 0, 1, 7, 1);

		return serialAlternateGridPane;
	}

//	private void applyFilters() {
//	    filteredData.setPredicate(data -> {
//
//	        // Signal Name filter
//	        if (signalNameField.getValue() != null && !signalNameField.getValue().trim().equalsIgnoreCase(
//	                data.getSignalName() == null ? "" : data.getSignalName().trim())) {
//	            return false;
//	        }
//
//	        // TPGPH filter
////	        //System.out.println("TPGPH ::" + tpgphField.getValue());
//	        if (tpgphField.getValue() != null && !tpgphField.getValue().trim().equalsIgnoreCase(
//	                data.getTpgphNo() == null ? "" : data.getTpgphNo().trim())) {
//	            return false;
//	        }
//
//	        // Step filter
//	        if (stepField.getValue() != null && !stepField.getValue().trim().equalsIgnoreCase(
//	                data.getStepNo() == null ? "" : data.getStepNo().trim())) {
//	            return false;
//	        }
//
//	        // SRU filter
//	        if (sruTypeField.getValue() != null && !sruTypeField.getValue().trim().equalsIgnoreCase(
//	                data.getFaultySru() == null ? "" : data.getFaultySru().trim())) {
//	            return false;
//	        }
//
//	        // Stage filter
//	        if (stageName.getValue() != null && !stageName.getValue().trim().equalsIgnoreCase(
//	                data.getStageName() == null ? "" : data.getStageName().trim())) {
//	            return false;
//	        }
//
//	        // Serial Number filter
//	        if (serialnumber.getValue() != null && !serialnumber.getValue().trim().equalsIgnoreCase(
//	                data.getUnitSerialNo() == null ? "" : data.getUnitSerialNo().trim())) {
//	            return false;
//	        }
//
//	        // Session filter
//	        if (sessioName.getValue() != null && !sessioName.getValue().trim().equalsIgnoreCase(
//	                data.getSessionName() == null ? "" : data.getSessionName().trim())) {
//	            return false;
//	        }
//
//	        return true;
//	    });
//	}
	
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
	
	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
//Suji
			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			selectedUutId = uutId;
			serialnumber.getSelectionModel().clearSelection();
			sessioName.getSelectionModel().clearSelection();
			sruTypeField.getSelectionModel().clearSelection();
			stepField.getSelectionModel().clearSelection();
			tpgphField.getSelectionModel().clearSelection();
			channelField.getSelectionModel().clearSelection();
			signalNameField.getSelectionModel().clearSelection();
			
			view.setDisable(false);
//			initalizeSerialNoComboBox(uutId);

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
//
//			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
//			selectedSno = serialnumber.getSelectionModel().getSelectedItem();
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
	    }
	    serialnumber.setItems(dfccSNList);
	    addSearchFunctionality(serialnumber, dfccSNList);
	    
//	    serialnumber.setOnAction((event) -> {
//
//			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
//			selectedSno = serialnumber.getSelectionModel().getSelectedItem();
//			initializeSessionComboBox(selectedSerialNumber);
//
//		});
//	    serialnumber.setOnAction(event -> {
//	        String selectedSerialNo = serialnumber.getSelectionModel().getSelectedItem();
//	        sessioName.getSelectionModel().clearSelection();
//	        selectedSno = selectedSerialNo;
//	        sruTypeField.getSelectionModel().clearSelection();
//			stepField.getSelectionModel().clearSelection();
//			tpgphField.getSelectionModel().clearSelection();
//			channelField.getSelectionModel().clearSelection();
//			signalNameField.getSelectionModel().clearSelection();
//	        if (selectedSerialNo != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
//	            initializeSessionComboBox(selectedSerialNo);
//	        }else {
//	        	initializeTrialSessionComboBox(selectedSerialNo);
//	        }
//	    });


	   
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
	    
//	    sessioName.setOnAction(event -> {
//			String selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
//			sruTypeField.getSelectionModel().clearSelection();
//			stepField.getSelectionModel().clearSelection();
//			tpgphField.getSelectionModel().clearSelection();
//			channelField.getSelectionModel().clearSelection();
//			signalNameField.getSelectionModel().clearSelection();
//			if (selectedSessionName != null) {
//				TrailSessionDto selectedSession = filterSessionList.stream()
//						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);
//
//				if (selectedSession != null) {
//					selectedSessionId = selectedSession.getSessionId();
//				}
//				getStageName(selectedSessionId);
//			}
//		});

	   
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

//		stageName.setOnAction(event -> {
//
//			selectedStageName = stageName.getSelectionModel().getSelectedItem();
//
//			selectedStageId = stageNameId.entrySet().stream()
//					.filter(e -> Objects.equals(selectedStageName, e.getValue())).map(Map.Entry::getKey).findFirst()
//					.orElse(null);
//
//
//		});
	}

	public GridPane headingGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(70);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		headingGridPane.getRowConstraints().add(firstRow);
		headingGridPane.getStyleClass().add("dataanalysis-testing-build-save");
		headingGridPane.add(headingHbox(), 0, 0);
		note.setStyle("-fx-font-weight: bold;");
		
		note.setAlignment(Pos.CENTER_RIGHT);
		headingGridPane.add(note, 1, 0);
		

		return headingGridPane;

	}

	private HBox headingHbox() {
		HBox headingHbox = new HBox(10);
		headingHbox.getStyleClass().add("dataanalysis-testing-second-container");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().addAll(pageHeading);
		

		return headingHbox;
	}
	
	private HBox printButtonHbox() {
	    HBox saveButtonHbox = new HBox();
	    saveButtonHbox.setAlignment(Pos.CENTER);
	    saveButtonHbox.setSpacing(5);
	    saveButtonHbox.getChildren().addAll(view, print);
	 // Keep track of the last loaded UUT
	    

	    view.setOnAction(e -> {
	        if (selectedUutId == null) {
	            Notifications.showErrorAlert("Please select UUT and try again.");
	            return;
	        }

//	        //System.out.println("Selected UUT: " + selectedUutId);

	        // Only reload table if UUT changed
	        
	            createDetailedDataTable(); // reload fresh data for new UUT
//	            filter.setDisable(false);
	            view.setDisable(true);
	       
	    });
	    
//	    filter.setOnAction(e -> {
//	    	 applyFilters();
//	    	 view.setDisable(true);
//	    });
	    print.setOnAction(e -> {
	        if (filteredData == null || filteredData.isEmpty()) {
	            Notifications.showErrorAlert("No data available to export");
	            return;
	        }

	        // Open FileChooser to select path
	        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
	        fileChooser.setTitle("Save PDF");
	        fileChooser.getExtensionFilters().add(
	                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
	        fileChooser.setInitialFileName("Detailed_Result_" +
	                new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date()) + ".pdf");

	        File selectedFile = fileChooser.showSaveDialog(print.getScene().getWindow());
	        if (selectedFile == null) {
	            // User cancelled
	            return;
	        }

	        Task<Void> pdfTask = new Task<>() {
	            @Override
	            protected Void call() throws Exception {
	                exportDetailedTableToPdf(new ArrayList<>(filteredData), selectedFile);
	                return null;
	            }

	            @Override
	            protected void succeeded() {
	                Platform.runLater(() ->
	                        Notifications.showSuccessAlert("PDF saved successfully:\n" + selectedFile.getAbsolutePath())
	                );
	            }

	            @Override
	            protected void failed() {
	                getException().printStackTrace(); // print error to console
	                Platform.runLater(() ->
	                        Notifications.showErrorAlert("Failed to generate PDF")
	                );
	            }
	        };

	        new Thread(pdfTask).start();
	    });

	    return saveButtonHbox;
	}
	private void exportDetailedTableToPdf(List<DetailedData> dataList, File file) throws Exception {

	    // Rotate A4 for landscape orientation
	    Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);

	    // Ensure parent directories exist
	    if (file.getParentFile() != null) {
	        file.getParentFile().mkdirs();
	    }

	    PdfWriter.getInstance(document, new FileOutputStream(file));
	    document.open();

	    // Fonts
	    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
	    Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

	    // Title
	    Paragraph title = new Paragraph("Detailed Result Report", headerFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    title.setSpacingAfter(10);
	    document.add(title);

	    // 15 columns to match your data
	    PdfPTable table = new PdfPTable(15);
	    table.setWidthPercentage(100);

	    // Optional: set relative widths for better readability
	    table.setWidths(new float[]{
	            1f, 2f, 2f, 2f, 2f, 2f, 1.5f, 1.5f, 1.5f, 2f, 1f, 2f, 1.5f, 1f, 2f
	    });

	    // Add header row
	    addHeader(table, headerFont,
	            "Sl No", "Unit Serial No", "Session Name", "Stage Name", "Test Name", "RDF Name",
	            "TPGPH", "Step", "Expected", "Measured", "Unit", "Signal", "SRU", "Mode", "Channel Value"
	    );

	    // Add table data
	    for (DetailedData d : dataList) {
	        addCell(table, bodyFont, d.getSlNo());
	        addCell(table, bodyFont, d.getUnitSerialNo());
	        addCell(table, bodyFont, d.getSessionName());
	        addCell(table, bodyFont, d.getStageName());
	        addCell(table, bodyFont, d.getTestName());
	        addCell(table, bodyFont, d.getRdfName());
	        addCell(table, bodyFont, d.getTpgphNo());
	        addCell(table, bodyFont, d.getStepNo());
	        addCell(table, bodyFont, d.getExpectedValue());
	        addCell(table, bodyFont, d.getMeasuredValueCh1_Ch2_Ch3_Ch4());
	        addCell(table, bodyFont, d.getUnit());
	        addCell(table, bodyFont, d.getSignalName());
	        addCell(table, bodyFont, d.getFaultySru());
	        addCell(table, bodyFont, d.getTestMode());
	        addCell(table, bodyFont, d.getFaultyChannelValue());
	    }

	    document.add(table);
	    document.close();
	}
	
	private void addHeader(PdfPTable table, Font font, String... headers) {
	    for (String h : headers) {
	        PdfPCell cell = new PdfPCell(new Phrase(h, font));
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        table.addCell(cell);
	    }
	}

	private void addCell(PdfPTable table, Font font, String value) {
	    table.addCell(new PdfPCell(new Phrase(value == null ? "" : value, font)));
	}

	
	private void exportDetailedTableToPdf(List<DetailedData> dataList) throws Exception {

	    Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);

	    String fileName = "Detailed_Result_"
	            + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date())
	            + ".pdf";

	    String filePath;
	    if (!DFCCConstant.isJarBuild) {
	        filePath = "C:\\Users\\sharn\\Downloads\\" + fileName;
	    } else {
	        filePath = System.getProperty("user.dir")
	                + File.separator + "Reports"
	                + File.separator + fileName;
	    }

	    PdfWriter.getInstance(document, new FileOutputStream(filePath));
	    document.open();

	    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
	    Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

	    Paragraph title = new Paragraph("Detailed Result Report", headerFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    title.setSpacingAfter(10);
	    document.add(title);

	    PdfPTable table = new PdfPTable(12); // adjust column count if needed
	    table.setWidthPercentage(100);

	    addHeader(table, headerFont,
	            "Sl No", "Test Name", "RDF Name", "TPGPH",
	            "Step", "Expected", "Measured",
	            "Unit", "Signal", "SRU", "Mode", "Channel Value"
	    );

	    for (DetailedData d : dataList) {
	        addCell(table, bodyFont, d.getSlNo());
	        addCell(table, bodyFont, d.getTestName());
	        addCell(table, bodyFont, d.getRdfName());
	        addCell(table, bodyFont, d.getTpgphNo());
	        addCell(table, bodyFont, d.getStepNo());
	        addCell(table, bodyFont, d.getExpectedValue());
	        addCell(table, bodyFont, d.getMeasuredValueCh1_Ch2_Ch3_Ch4());
	        addCell(table, bodyFont, d.getUnit());
	        addCell(table, bodyFont, d.getSignalName());
	        addCell(table, bodyFont, d.getFaultySru());
	        addCell(table, bodyFont, d.getTestMode());
	        addCell(table, bodyFont, d.getFaultyChannelValue());
	    }

	    document.add(table);
	    document.close();
	}
	



//	public ScrollPane createDetailedDataTable() {
//
//		detailedDataList.clear();
//
//		ScrollPane tableScrollPane = new ScrollPane(detailedDataTableView);
//
//		Task<Void> task = new Task<Void>() {
//			@Override
//			protected Void call() {
//
//				ResultDetailedResponse response;
//
//				
//
////	                response = resultExecutionManagement
////	                        .getResultExecutionDetailedListForStages(
////	                                selectedSessionId, selectedStageId);
//					response = resultExecutionManagement
//							//.getResultExecutionDetailedListForStagesFromMysql(selectedSessionId, selectedStageId);
//																				//UUT id  ,serialNo, sessionId, stageId  
//							.getResultExecutionDetailedListForStagesFromMysql(selectedUutId, null, null, null);
////					//System.out.println("Check in Failure bcjk method ::"+ selectedUutId);
////					//System.out.println("Rspo msg Mnai "+response.getMsg());
//					
//				
//
//				if (response.getCode() == 1 && response.getResultDetailedList() != null) {
//
//					Set<String> signalSet = new HashSet<>();
//					Set<String> unitserialNoSet = new HashSet<>();
//					Set<String> sessionSet = new HashSet<>();
//					Set<String> stageSet = new HashSet<>();
//					Set<String> stepSet = new HashSet<>();
//					Set<String> tpgphSet = new HashSet<>();
//					Set<String> sruSet = new HashSet<>();
//
//					int i = 1;
//
//					for (ResultDetailedDTO data : response.getResultDetailedList()) {
//
//						DetailedData newDetailedData = new DetailedData();
//
//						newDetailedData.setSlNo(String.valueOf(i++));
//						newDetailedData.setUnitSerialNo(data.getUnitSerialNo());
//						newDetailedData.setSessionName(data.getUnitSession());
//						newDetailedData.setStageName(data.getStageName());
//						newDetailedData.setTestName(data.getTestName());
//						newDetailedData.setRdfName(data.getRdfName());
//						newDetailedData.setTpgphNo(data.getTpgph());
//						newDetailedData.setStepNo(data.getStepName());
//						newDetailedData.setExpectedValue(data.getExpectedValue());
//						newDetailedData.setMeasuredValueCh1_Ch2_Ch3_Ch4(data.getFaultyChannel());
//						newDetailedData.setUnit(data.getUnit());
//						newDetailedData.setSignalName(data.getSignalName());
//						newDetailedData.setFaultySru(data.getFaultySRU());
//						newDetailedData.setTestMode(data.getTestMode());
//						newDetailedData.setFaultyChannelValue(data.getFaultyChannelValue());
//
//						Platform.runLater(() -> detailedDataList.add(newDetailedData));
//
//						if (data.getSignalName() != null)
//							signalSet.add(data.getSignalName());
//						if (data.getStepName() != null)
//							stepSet.add(data.getStepName());
//						if (data.getTpgph() != null)
//							tpgphSet.add(data.getTpgph());
//						if (data.getFaultySRU() != null)
//							sruSet.add(data.getFaultySRU());
//						if (data.getUnitSerialNo() != null)
//							unitserialNoSet.add(data.getUnitSerialNo());
//						if (data.getUnitSession() != null)
//							sessionSet.add(data.getUnitSession());
//						if (data.getStageId() != null)
//							stageSet.add(data.getStageName());
//					}
//
//					Platform.runLater(() -> {
//						signalNameField.setItems(FXCollections.observableArrayList(signalSet));
//						addSearchFunctionality(signalNameField, FXCollections.observableArrayList(signalSet));
//						stepField.setItems(FXCollections.observableArrayList(stepSet));
//						addSearchFunctionality(stepField, FXCollections.observableArrayList(stepSet));
//						tpgphField.setItems(FXCollections.observableArrayList(tpgphSet));
//						addSearchFunctionality(tpgphField, FXCollections.observableArrayList(tpgphSet));
//						sruTypeField.setItems(FXCollections.observableArrayList(sruSet));
//						addSearchFunctionality(sruTypeField, FXCollections.observableArrayList(sruSet));
//						
//						
//						serialnumber.setItems(FXCollections.observableArrayList(unitserialNoSet));
//						addSearchFunctionality(serialnumber, FXCollections.observableArrayList(unitserialNoSet));
//						
//						sessioName.setItems(FXCollections.observableArrayList(sessionSet));
//						addSearchFunctionality(sessioName, FXCollections.observableArrayList(sessionSet));
//						
//						stageName.setItems(FXCollections.observableArrayList(stageSet));
//						addSearchFunctionality(stageName, FXCollections.observableArrayList(stageSet));
//						
//						
////						stageName = new ComboBox<String>();
//////						Serial Number
////						private ComboBox<String> serialnumber = new ComboBox<String>();
//////						Session Name
////						private ComboBox<String> sessioName
//						
//					});
//
//				} else {
//					Platform.runLater(() -> Notifications.showErrorAlert(response.getMsg()));
//				}
//
//				return null;
//			}
//
//			@Override
//			protected void succeeded() {
//
//				Platform.runLater(() -> {
//					hideProgressIndicator();
//					// RESET FILTERS when new data is loaded
//					filteredData.setPredicate(p -> true);
//
//					if (detailedDataTableView == null) {
//						detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);
//
//						Label placeholder = new Label("Select any session data from session result table..");
//						placeholder.setStyle("-fx-font-size:20px;");
//						detailedDataTableView.setPlaceholder(placeholder);
//
//						detailedDataTableView.getColumns().forEach(col -> {
//							col.setMinWidth(col.getText().length() * 14);
//							updateDetailedData((TableColumn<DetailedData, String>) col);
//						});
//					}
//
//					detailedDataTableView.setItems(filteredData);
//					tableScrollPane.setContent(detailedDataTableView);
//					tableScrollPane.setFitToHeight(true);
//				});
//			}
//
//			@Override
//			protected void failed() {
//				Platform.runLater(() ->{
//				hideProgressIndicator();
//				Notifications.showErrorAlert("Failed to retrieve data");});
//			}
//		};
//		task.setOnRunning(evt -> showProgressIndicator());
//		new Thread(task).start();
//		return tableScrollPane;
//	}
	
	public ScrollPane createDetailedDataTable() {

	    detailedDataList.clear();
	    ScrollPane tableScrollPane = new ScrollPane();

	    Task<List<DetailedData>> task = new Task<>() {
	        @Override
	        protected List<DetailedData> call() {

	            ResultDetailedResponse response =
	                    resultExecutionManagement
	                            .getResultExecutionDetailedListForStagesFromMysql(
	                                    selectedUutId, null, null, null);

	            if (response.getCode() != 1 || response.getResultDetailedList() == null) {
	                throw new RuntimeException(response.getMsg());
	            }

	            List<DetailedData> tempList = new ArrayList<>();

	            int i = 1;
	            for (ResultDetailedDTO data : response.getResultDetailedList()) {

	                DetailedData d = new DetailedData();

	                d.setSlNo(String.valueOf(i++));
	                d.setUnitSerialNo(data.getUnitSerialNo());
	                d.setSessionName(data.getUnitSession());
	                d.setStageName(data.getStageName());
	                d.setTestName(data.getTestName());
	                d.setRdfName(data.getRdfName());
	                d.setTpgphNo(data.getTpgph());
	                d.setStepNo(data.getStepName());
	                d.setExpectedValue(data.getExpectedValue());
	                d.setMeasuredValueCh1_Ch2_Ch3_Ch4(data.getFaultyChannel());
	                d.setUnit(data.getUnit());
	                d.setSignalName(data.getSignalName());
	                d.setFaultySru(data.getFaultySRU());
	                d.setTestMode(data.getTestMode());
	                d.setFaultyChannelValue(data.getFaultyChannelValue());

	                tempList.add(d);
	            }

	            return tempList;
	        }
	    };

	    task.setOnRunning(e -> showProgressIndicator());

	    task.setOnSucceeded(e -> {
	        hideProgressIndicator();

	        List<DetailedData> result = task.getValue();
	        detailedDataList.setAll(result);

	        setupFilters(result);
	        applyFilters(); // VERY IMPORTANT

	        if (detailedDataTableView == null) {
	            detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);

	            Label placeholder = new Label("Select any UUT and click view result...");
	            placeholder.setStyle("-fx-font-size:20px;");
	            
	            detailedDataTableView.getColumns().forEach(col -> {
					col.setMinWidth(col.getText().length() * 14);
					updateDetailedData((TableColumn<DetailedData, String>) col);
				});
	            detailedDataTableView.setPlaceholder(placeholder);
	        }

	        detailedDataTableView.setItems(filteredData);
	        tableScrollPane.setContent(detailedDataTableView);
	        tableScrollPane.setFitToHeight(true);
	    });

	    task.setOnFailed(e -> {
	        hideProgressIndicator();
	        Notifications.showErrorAlert(task.getException().getMessage());
	    });

	    new Thread(task).start();

	    setupFilterListeners(); // attach listeners once

	    return tableScrollPane;
	}
	
	private void setupFilters(List<DetailedData> dataList) {

	    Set<String> signalSet = new HashSet<>();
	    Set<String> stepSet = new HashSet<>();
	    Set<String> tpgphSet = new HashSet<>();
	    Set<String> sruSet = new HashSet<>();
	    Set<String> stageSet = new HashSet<>();
	    Set<String> serialSet = new HashSet<>();
	    Set<String> sessionSet = new HashSet<>();

	    for (DetailedData d : dataList) {
	        if (d.getSignalName() != null) signalSet.add(d.getSignalName());
	        if (d.getStepNo() != null) stepSet.add(d.getStepNo());
	        if (d.getTpgphNo() != null) tpgphSet.add(d.getTpgphNo());
	        if (d.getFaultySru() != null) sruSet.add(d.getFaultySru());
	        if (d.getStageName() != null) stageSet.add(d.getStageName());
	        if (d.getUnitSerialNo() != null) serialSet.add(d.getUnitSerialNo());
	        if (d.getSessionName() != null) sessionSet.add(d.getSessionName());
	    }

	    setComboItems(signalNameField, signalSet);
	    setComboItems(stepField, stepSet);
	    setComboItems(tpgphField, tpgphSet);
	    setComboItems(sruTypeField, sruSet);
	    setComboItems(stageName, stageSet);
	    setComboItems(serialnumber, serialSet);
	    setComboItems(sessioName, sessionSet);
	}
	
	private boolean match(String filterValue, String dataValue) {
	    if (filterValue == null || filterValue.isEmpty()) return true;

	    String f = filterValue.trim().toLowerCase();
	    String d = dataValue == null ? "" : dataValue.trim().toLowerCase();

	    return d.equals(f);
	}
	
	private void setupFilterListeners() {

	    signalNameField.valueProperty().addListener((obs, o, n) -> applyFilters());
	    tpgphField.valueProperty().addListener((obs, o, n) -> applyFilters());
	    stepField.valueProperty().addListener((obs, o, n) -> applyFilters());
	    sruTypeField.valueProperty().addListener((obs, o, n) -> applyFilters());
	    stageName.valueProperty().addListener((obs, o, n) -> applyFilters());
	    serialnumber.valueProperty().addListener((obs, o, n) -> applyFilters());
	    sessioName.valueProperty().addListener((obs, o, n) -> applyFilters());
	}
	

	private void setComboItems(ComboBox<String> combo, Set<String> data) {
	    ObservableList<String> list = FXCollections.observableArrayList(data);
	    combo.setItems(list);
	    addSearchFunctionality(combo, list);
	}
	
	private void applyFilters() {

	    filteredData.setPredicate(data -> {

	        if (!match(signalNameField.getValue(), data.getSignalName())) return false;
	        if (!match(tpgphField.getValue(), data.getTpgphNo())) return false;
	        if (!match(stepField.getValue(), data.getStepNo())) return false;
	        if (!match(sruTypeField.getValue(), data.getFaultySru())) return false;
	        if (!match(stageName.getValue(), data.getStageName())) return false;
	        if (!match(serialnumber.getValue(), data.getUnitSerialNo())) return false;
	        if (!match(sessioName.getValue(), data.getSessionName())) return false;

	        return true;
	    });
	}

	private void updateDetailedData(TableColumn<DetailedData, String> column) {

		column.setCellFactory(col -> new TableCell<DetailedData, String>() {
			private Label label;

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setGraphic(null);
				} else {
					if (label == null) {
						label = new Label();
						label.setWrapText(false);
						label.setAlignment(Pos.CENTER);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						setStyle("-fx-alignment: CENTER;");
					}
					label.setText(item);
					label.setStyle("-fx-text-fill: #000000;");
					label.setMinWidth(label.getText().length() * 14);
					setGraphic(label);
					this.setMinWidth(label.getText().length() * 14);
					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
				}
			}
		});
	}
	//sai28012026
		 private void showProgressIndicator() {
				StackPane parentStackPane= (StackPane) adaFiletr2MainContainerGridPane.getParent().getParent();
			if (!parentStackPane.getChildren().contains(progressbox)) {
				progressbox.getChildren().add(progressIndicator);
				progressbox.setAlignment(Pos.CENTER);
				parentStackPane.getChildren().add(progressbox);
			}
		}

			private void hideProgressIndicator() {
				StackPane parentStackPane= (StackPane) adaFiletr2MainContainerGridPane.getParent().getParent();
				if(parentStackPane.getChildren().contains(progressbox)) {
					parentStackPane.getChildren().remove(progressbox);
				progressbox.getChildren().clear(); // Clean up for next use
				}
			}

}
