package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.internal.build.AllowSysOut;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.ReportCofigurationManagement;
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.datastore.dto.ReportConfigResponse;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.reportgeneration.DataPackReportGeneration;
import com.teclever.dfcc.reportgeneration.HistoryReport;
import com.teclever.dfcc.reportgeneration.ReportGenerationNew;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class ReportController {

	private GridPane reportMainGridPane = new GridPane();
	private GridPane reportHeadingGridPane = new GridPane();
	private GridPane reportComboBoxGridPane = new GridPane();
	private GridPane reportBottomGridPane = new GridPane();
	private GridPane reportBottomLeftGridPane = new GridPane();
	private GridPane reportBottomRightGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Download");

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ComboBox<String> slNoField = new ComboBox<String>();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private DataPackReportGeneration dataPackReportGeneration = new DataPackReportGeneration();

	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private ObservableList<SessionList> sessionDataList;
	private ObservableList<String> sessionNameList = FXCollections.observableArrayList();

	private ObservableList<SessionList> sessionDataListH;
	private ObservableList<String> sessionNameListH = FXCollections.observableArrayList();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	private List<SessionDto> sessionNameList1;
	private String SESSION_ID;
	private String selectedUttId;
	private String sessionType;
	SessionService s = new SessionService();

	private HBox selectionBoxSESSION = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);

	private Button addOtherFiles = new Button("Add Other Files");
	private Button addRemarks = new Button("Add Remarks");

	private ListView<ReportConfigDto> listView = new ListView<>();;
	private String REPORT_TYPE = null;
	private ObservableList<ReportConfigDto> reportData = FXCollections.observableArrayList();
	private TrailSessionEntityService t = new TrailSessionEntityService();
	private ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private SessionManagement sessionManagement = new SessionManagement();
	private ReportTreeviewController reportTreeviewController = new ReportTreeviewController(this);
	private HistoryReport historyReport = new HistoryReport();

	private String selectedUutId;
	private String selectedSno;

	public ReportController() {
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		// Trial sessions
		TrailSessionResponse t1 = t.getActiveTrailSessionId();
		sessionListTrail = t1.getListOfSession();
//		////System.out.println("Start sessionList" + sessionList.size());
		initializeUUTTypeComboBox();
	}

	public GridPane createReportGridPane(String reportType) {
		if ("PQT REPORT".equals(reportType)) {
			REPORT_TYPE = "PQT";
		} else if ("ESS REPORT".equals(reportType)) {
			REPORT_TYPE = "ESS";
		} else if ("DATAPACK REPORT".equals(reportType)) {
			REPORT_TYPE = "DataPack";
		} else {
			REPORT_TYPE = "History";
		}
		reportMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
		reportMainGridPane.getStyleClass().add("report-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		reportMainGridPane.setVgap(5);
		reportMainGridPane.setPadding(new Insets(5));
		reportMainGridPane.getColumnConstraints().addAll(firstColumn);
		reportMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		reportMainGridPane.add(createHeadingBox(reportType), 0, 0);
		reportMainGridPane.add(createComboBoxGridPane(), 0, 1);
		if (!REPORT_TYPE.equals("History")) {
		reportMainGridPane.add(createBottomGridPane(), 0, 2);
		}
		getSavedReportData();

		return reportMainGridPane;
	}

	private GridPane createComboBoxGridPane() {
		reportComboBoxGridPane.getStyleClass().add("report-top-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(20);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(20);
		ColumnConstraints fivthColumn = new ColumnConstraints();
		fivthColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportComboBoxGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fivthColumn);
		reportComboBoxGridPane.getRowConstraints().addAll(firstRow);

		reportComboBoxGridPane.add(createUutBox(), 0, 0);
		reportComboBoxGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		reportComboBoxGridPane.add(createSessionComboBox(), 2, 0);
		reportComboBoxGridPane.add(createAddFilesBox(), 3, 0);
		if (REPORT_TYPE.equals("ESS")) {
//			Suji edited for Button Overlapping issue : point:57
			reportComboBoxGridPane.getChildren().remove(createAddFilesBox());
//			End
			reportComboBoxGridPane.add(createAddRemarkBox(), 3, 0);
		}

		return reportComboBoxGridPane;
	}

	private HBox createUutBox() {
		uutTypeField.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uutTypeField);

		return uutTypeHBox;
	}

	// SESSION FIELD
	private HBox createSessionComboBox() {
		sessionNameField.setPromptText("Session");
		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
		selectionBoxSESSION.setAlignment(Pos.CENTER_LEFT);
		sessionNameField.setEditable(true);
		selectionBoxSESSION.getChildren().add(sessionNameField);

		return selectionBoxSESSION;
	}

	private HBox createAddFilesBox() {
		HBox addFilesHBox = new HBox(10);
		addFilesHBox.setAlignment(Pos.CENTER);
		if (REPORT_TYPE.equals("PQT")) {
		addFilesHBox.getChildren().add(addOtherFiles);
//		if (REPORT_TYPE.equals("History")) {
//			Platform.runLater(() -> {
//				addOtherFiles.setDisable(true);
//			});
//		}
		addOtherFiles.setOnAction(e -> {
			if (UUT_ID != null && SESSION_ID != null) {
				updateListViewWithSelectedLabel(null);
			} else {
				Notifications.showWarningAlert("Please select UUT type and session name...");
			}
		});
		}
		return addFilesHBox;
	}

	private HBox createAddRemarkBox() {
		HBox addRemarksHBox = new HBox(10);
		addRemarksHBox.setAlignment(Pos.CENTER);
		addRemarksHBox.getChildren().add(addRemarks);

		addRemarks.setOnAction(e -> {
			if (UUT_ID != null && SESSION_ID != null) {
				handleRemarkPopup();
			} else {
				Notifications.showWarningAlert("Please select UUT type and session name...");
			}
		});

		return addRemarksHBox;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {

			reportTreeviewController.initializeReportTreeView(null);
			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			String uutId = fetchUutId(selectedUUTType);
			selectedUttId = uutId;
//				////System.out.println("UUT ID check" + uutId);
			UUT_ID = uutId;
			selectedUutId = uutId;
			initializeDfccSNComboBox(uutId);
			reportData.clear();

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

//	private void initializeDfccSNComboBox(String uutTypeId) {
//	    dfccSNList.clear();
////	    ////System.out.println("Check Session List Size " + sessionList.size()+"  " +uutTypeId );
//	    List<SessionDto> filterSessionList = sessionList.stream()
//	            .filter(t -> t.getUutId().equals(uutTypeId))
//	            .collect(Collectors.toList());
//
//	    Set<String> seenDfccSNos = new HashSet<>();
//
//	    for (SessionDto dfccSn : filterSessionList) {
//	        String dfccSNo = dfccSn.getDfccSNo();
//	        if (seenDfccSNos.add(dfccSNo)) { // Only adds if not already in the set
//	            dfccSNList.add(dfccSNo);
//	        }
//	    }
//
//
//	    slNoField.setOnAction(event -> {
//		    String selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
//		    selectedSno = selectedSerialNo;
//		    if (selectedSerialNo != null) {
//		    	initializeSessionNameComboBox(selectedSerialNo);
//		    }
//		});
//
//	    slNoField.setItems(dfccSNList);
//	}

	private void initializeDfccSNComboBox(String uutTypeId) {
////System.out.println("CHECK UUT ID ::" +uutTypeId);
		dfccSNList.clear();
		Set<String> seenDfccSNos = new HashSet<>();

		if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {

			List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
					.collect(Collectors.toList());

			for (SessionDto dfccSn : filterSessionList) {
				String dfccSNo = dfccSn.getDfccSNo();
				if (seenDfccSNos.add(dfccSNo)) {
					dfccSNList.add(dfccSNo);
				}
			}

		} else {

			List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
					.filter(t -> t.getUutId().equals(uutTypeId)).collect(Collectors.toList());
			for (TrailSessionDto dfccSn : filterSessionList) {
				String dfccSNo = dfccSn.getDfccSNo();
				if (seenDfccSNos.add(dfccSNo)) {
					dfccSNList.add(dfccSNo);
				}
			}

			// //System.out.println("CHeck List " + dfccSNList.size());
		}

		addSearchFunctionality(slNoField, dfccSNList);
		slNoField.setOnAction(event -> {
			String selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
			selectedSno = selectedSerialNo;
			// //System.out.println("Check SN SELECTION :: " +selectedSno ) ;
			if (selectedSno != null) {
				initializeSessionNameComboBox(selectedSno);
			}
		});

		slNoField.setItems(dfccSNList);
	}

	// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER_LEFT);
		slNoField.setEditable(true);
		selectionHBoxUUTSN.getChildren().add(slNoField);

		return selectionHBoxUUTSN;
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

	private void initializeSessionNameComboBox(String serialNo) {
		sessionNameField.getSelectionModel().clearSelection();
		sessionNameField.setValue(null);
		sessionNameField.getEditor().clear();
		sessionNameField.setItems(FXCollections.observableArrayList());
		sessionNameList.clear();
		sessionNameList1 = FXCollections.observableArrayList();

		SESSION_ID = null;

		if (!(StateMachine.currentSessionDetails.getSessionTypeName() == null)
				&& StateMachine.currentSessionDetails.getSessionTypeName().equals("Trail")) {
			sessionType = "Trails";
		} else {
			sessionType = "Other";
		}

//		Suji edited for History Report:
		if (REPORT_TYPE.equals("History")) {

			sessionNameListH.clear();

			SessionListResponse response1 = sessionManagement.getSessionDataByUUTIdwithEndedSession(selectedUttId,
					sessionType);

			if (response1.getResponse().getResponseCode() == 1) {

				List<SessionList> filteredList = response1.getListOfSession().stream()
						.filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
				for (SessionList session1 : filteredList) {
					sessionNameListH.add(session1.getSessionName());
				}
				sessionDataList = FXCollections.observableArrayList(filteredList);
			}

			ObservableList<String> list = FXCollections.observableArrayList(sessionNameListH);
			sessionNameField.setItems(FXCollections.observableArrayList(sessionNameListH));
			addSearchFunctionality(sessionNameField, list);

		}
		SessionListResponse response = sessionManagement.getSessionDataByUUTId(selectedUttId);

		if (response.getResponse().getResponseCode() == 1) {

			sessionDataList = FXCollections.observableArrayList(response.getListOfSession());

			for (SessionList session : sessionDataList) {

				sessionNameList.add(session.getSessionName());

			}

			if (REPORT_TYPE.equals("PQT")) {

				// //System.out.println("CHECK PAT SLNO ::: " +selectedSno );
				SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
				List<SessionList> lst = response1.getListOfSession();

				lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
				lst = lst.stream().filter(e -> e.getSessionType().equals("ST5")).collect(Collectors.toList());
//				lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

				List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
						.collect(Collectors.toList());

				sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
				addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));
			} else if (REPORT_TYPE.equals("ESS")) {
				if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {
					// //System.out.println("CHECK PAT SLNO ::: " +selectedSno );
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("ST1") || e.getSessionType().equals("ST3")
							|| e.getSessionType().equals("ST2")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1)

					);
					addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));
				} else {
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("TRIALS")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
					addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));
				}
			} else if (REPORT_TYPE.equals("DataPack")) {

				if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {

					// //System.out.println("CHECK PAT SLNO ::: " +selectedSno );
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("ST1") || e.getSessionType().equals("ST3")
							|| e.getSessionType().equals("ST2")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
					addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));

				} else {
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("TRIALS")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
					addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));

				}
			} else {

				if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {
					// //System.out.println("CHECK PAT SLNO ::: " +selectedSno );
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("ST1") || e.getSessionType().equals("ST3")
							|| e.getSessionType().equals("ST2")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
				} else {
					SessionListResponse response1 = sessionManagement.getSessionDataByUUTId(selectedUutId);
					List<SessionList> lst = response1.getListOfSession();

					lst = lst.stream().filter(e -> e.getDfccSNo().equals(selectedSno)).collect(Collectors.toList());
					lst = lst.stream().filter(e -> e.getSessionType().equals("TRIALS")).collect(Collectors.toList());
//					lst = lst.stream().filter(e -> e.getEndDate() == null).collect(Collectors.toList());

					List<String> sessionNameList1 = lst.stream().map(SessionList::getSessionName)
							.collect(Collectors.toList());

					sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
					addSearchFunctionality(sessionNameField, FXCollections.observableArrayList(sessionNameList1));
				}

			}

//			End::

			sessionNameField.setOnAction((event) -> {

				SESSION_ID = fetchSessionId(sessionNameField.getValue());
				//System.out.println("Check Sesssion ID::" + SESSION_ID);
				if (SESSION_ID != null) {
					if (!REPORT_TYPE.equals("History")) {
//						addOtherFiles.setDisable(true);
						reportTreeviewController.initializeReportTreeView(SESSION_ID);
						getSavedReportData();
					}
				}
			});
		}
	}

	private String fetchSessionId(String sessionName) {
		for (SessionList session : sessionDataList) {
			if (session.getSessionName().equals(sessionName)) {
				return session.getSessionId();
			}
		}
		return null;
	}

	private GridPane createHeadingBox(String reportType) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportHeadingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		reportHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText(reportType);
		title.getStyleClass().add("report-title");
		titleBox.getChildren().add(title);

		reportHeadingGridPane.add(titleBox, 0, 0);
		reportHeadingGridPane.add(createDownloadButton(), 1, 0);

		return reportHeadingGridPane;
	}

//	Before Changing
//	private HBox createDownloadButton() {
//		buttonBox.setAlignment(Pos.CENTER_RIGHT);
//		buttonBox.getChildren().add(downloadButton);
//
//		downloadButton.setOnAction(e -> {
//			if(UUT_ID != null && SESSION_ID != null) {	
//				Response response = null;
//				if (REPORT_TYPE.equals("PQT")) {
//					ReportGenerationNew reportGenerationNew = new ReportGenerationNew();
//					response = reportGenerationNew.generatePQTReport(SESSION_ID);
//				} else if (REPORT_TYPE.equals("ESS")) {
//					ReportGenerationNew reportGenerationNew = new ReportGenerationNew();
//					response = reportGenerationNew.generateEssReport(SESSION_ID);
//				} 
//				if(response.getResponseCode() == 1) {
//					Notifications.showSuccessAlert(response.getResponseMessage());
//				}else if(response.getResponseCode() == 0) {
//					Notifications.showErrorAlert(response.getResponseMessage());
//				}
//			}else {
//				Notifications.showWarningAlert("Please select UUT type and session name.");
//			}
//		});
//
//		return buttonBox;
//	}

	ViewReportController viewReport = new ViewReportController();

//	After Changing
	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);
		// progress Spinner sai 21112025
		ProgressIndicator loader = new ProgressIndicator();
		loader.setMaxSize(70, 70);
		loader.setVisible(false);
		Platform.runLater(() -> {
			if (reportMainGridPane.getParent() instanceof StackPane parentStack) {
				if (!parentStack.getChildren().contains(loader)) {
					parentStack.getChildren().add(loader);
					StackPane.setAlignment(loader, Pos.CENTER);
				}
			}
		});

		downloadButton.setOnAction(e -> {
			if (UUT_ID != null && SESSION_ID != null) {
				Task<Response> task = new Task<Response>() {
					@Override
					protected Response call() throws Exception {
						Platform.runLater(() -> loader.setVisible(true));

						ReportGenerationNew reportGenerationNew = new ReportGenerationNew();
						Response response = null;

						if (REPORT_TYPE.equals("PQT")) {
							response = reportGenerationNew.generatePQTReport(SESSION_ID);
						} else if (REPORT_TYPE.equals("ESS")) {
							response = reportGenerationNew.generateEssReport(SESSION_ID);
						} else if (REPORT_TYPE.equals("DataPack")) {
							// response =DataPackReportGeneration.generateDataPackReport(SESSION_ID);
							response = sessionFileManagement.dataPackDownload(SESSION_ID);
						} else if (REPORT_TYPE.equals("History")) {
//	    					////System.out.println("Check Report Type :" + REPORT_TYPE + SESSION_ID );

							response = historyReport.generateSummaryResultForSessionNew(SESSION_ID);
//	    					response = reportGenerationNew.generateHISTORYReport(SESSION_ID);
						}
						return response;
					}
				};

				task.setOnSucceeded(event -> {
					loader.setVisible(false);
					Response response = task.getValue();
					if (response != null) {
						// changed by sai for popup sequence 17122025
						// Check if the response was successful (responseCode == 1)
						if (response.getResponseCode() == 1) {
//	                        Notifications.showSuccessAlert(response.getResponseMessage());
							Platform.runLater(() -> {
								// Create a success alert
								Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
								successAlert.setTitle("Success");
								successAlert.setHeaderText(null);
								successAlert.setContentText(response.getResponseMessage());

								// Show success alert and wait for it to be closed
								successAlert.showAndWait().ifPresent(buttonType -> {
									// After success alert is closed, show the report popup
									if (!REPORT_TYPE.equals("DataPack")) {
										viewReport.viewReportPopup(response);
									}
								});

							});

						} else {
							Notifications.showErrorAlert("Failed to generate the report.");
						}
					}
				});

				task.setOnFailed(event -> {
					loader.setVisible(false);
					Notifications.showErrorAlert("Error generating the report.");
					task.getException().printStackTrace();
				});

				Thread thread = new Thread(task);
				thread.setDaemon(true);
				thread.start();
			} else {
				Notifications.showWarningAlert("Please select UUT type and session name.");
			}
		});

		return buttonBox;
	}

	private GridPane createBottomGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		reportBottomGridPane.getRowConstraints().addAll(firstRow);
		reportBottomGridPane.setHgap(5);

		reportBottomGridPane.add(createLeftGridPane(null), 0, 0);
		reportBottomGridPane.add(createRightGridPane(), 1, 0);
		
		return reportBottomGridPane;
	}

	private GridPane createLeftGridPane(String sessionId) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
		reportBottomLeftGridPane.getRowConstraints().addAll(firstRow);

		reportBottomLeftGridPane.add(reportTreeviewController.createReportLeftGridPane(), 0, 0);
		return reportBottomLeftGridPane;
	}

	private GridPane createRightGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomRightGridPane.getColumnConstraints().addAll(firstColumn);
		reportBottomRightGridPane.getRowConstraints().addAll(firstRow);

		reportBottomRightGridPane.add(createListView(), 0, 0);
		return reportBottomRightGridPane;
	}

	private ListView<ReportConfigDto> createListView() {
		return listView;
	}

	private void updateListView() {
		listView.setItems(reportData);

		listView.setCellFactory(lv -> new ListCell<ReportConfigDto>() {
			private final HBox hbox = new HBox();
			private final Text text = new Text();
			private final Button deleteButton = new Button("Remove");

			{
				deleteButton.setVisible(false);

				hbox.setAlignment(Pos.CENTER_LEFT);
				HBox.setHgrow(text, Priority.ALWAYS);
				hbox.getChildren().addAll(text, deleteButton);

				setOnMouseEntered(event -> deleteButton.setVisible(true));
				setOnMouseExited(event -> deleteButton.setVisible(false));

				deleteButton.setOnAction(event -> {
					String title = "Confirmation Dialog";
					String contentText = "Are you sure you want to remove this file?";

					Notifications.showConfirmationDialog(title, contentText,
							() -> handleDeleteReportData(hbox.getId()));
				});
			}

			@Override
			protected void updateItem(ReportConfigDto item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					StringBuilder hierarchy = new StringBuilder();

					if (item.getLevelOneName() != null) {
						hierarchy.append(item.getLevelOneName() + " -> ");
					}
					if (item.getLevelTwoName() != null) {
						hierarchy.append(item.getLevelTwoName() + " -> ");
					}
					if (item.getLevelThreeName() != null) {
						hierarchy.append(item.getLevelThreeName() + " -> ");
					}
					if (item.getLevelFourName() != null) {
						hierarchy.append(item.getLevelFourName() + " -> ");
					}
					if (item.getLevelFiveName() != null) {
						hierarchy.append(item.getLevelFiveName() + " -> ");
					}
					if (item.getFileName() != null) {
					    String fileName = new File(item.getFileName()).getName();
					    hierarchy.append(fileName);
					}

					hbox.setId(item.getReportConfigId());
					
					text.setText(hierarchy.toString());
					setGraphic(hbox);
				}
			}
		});

	}

	SessionFileManagement sessionFileManagement = new SessionFileManagement();

	private void handleDeleteReportData(String id) {
		Response response = reportCofigurationManagement.deleteFileName(id);

		if (response.getResponseCode() == 1) {
			getSavedReportData();
		} else if (response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	public void updateListViewWithSelectedLabel(String[] idComponents) {
		ReportConfigDto newReportConfig = new ReportConfigDto();
		if (idComponents != null) {
			for (int i = 0; i < idComponents.length; i++) {
				if (i == 0)
					newReportConfig.setLevelOneId(idComponents[i]);
				if (i == 1)
					newReportConfig.setLevelTwoId(idComponents[i]);
				if (i == 2)
					newReportConfig.setLevelThreeId(idComponents[i]);
				if (i == 3)
					newReportConfig.setLevelFourId(idComponents[i]);
				if (i == 4)
					newReportConfig.setLevelFiveId(idComponents[i]);
			}
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Configuration File");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.pdf"));

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(reportMainGridPane.getScene().getWindow());
		List<String> selectedFilePath = new ArrayList<>();
		if (selectedFiles != null && !selectedFiles.isEmpty()) {
			for (File file : selectedFiles) {
				selectedFilePath.add(file.getAbsolutePath());
			}
			newReportConfig.setFileNameWitFullPath(selectedFilePath);
		}

		newReportConfig.setReportType(REPORT_TYPE);
		newReportConfig.setSessionId(SESSION_ID);
		if (selectedFilePath.size() > 0) {
			handleSaveReportdData(newReportConfig);

			if (REPORT_TYPE == "DataPack") {
				sessionFileManagement.copyToDataPack(reportData, SESSION_ID);
			}
		}
	}

	private void handleSaveReportdData(ReportConfigDto newReportConfig) {
		Response response = reportCofigurationManagement.addReportConfig(newReportConfig);
		if (response.getResponseCode() == 1) {
			getSavedReportData();
		} else if (response.getResponseCode() == 0) {
//			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	private void getSavedReportData() {
		reportData.clear();
		ReportConfigResponse response = reportCofigurationManagement.getAllReportConfig(SESSION_ID, REPORT_TYPE);
		if (response.getResponse().getResponseCode() == 1) {
			reportData = FXCollections.observableArrayList(response.getListOfReportConfigDto());
			Platform.runLater(() -> {
				updateListView();
			});

		} else if (response.getResponse().getResponseCode() == 0) {
//			Notifications.showErrorAlert(response.getResponse().getResponseMessage());
		}
	}

	private void handleRemarkPopup() {
		try {
			FXMLLoader addRemarkPopup = new FXMLLoader(
					getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddRemark.fxml"));
			Parent root = addRemarkPopup.load();

			AddRemarkController addRemarkController = addRemarkPopup.getController();
			addRemarkController.setSessionIdandReportType(SESSION_ID, REPORT_TYPE);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

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

}
