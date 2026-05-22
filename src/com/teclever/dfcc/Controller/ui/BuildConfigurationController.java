package com.teclever.dfcc.Controller.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationManagement;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationReport;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class BuildConfigurationController {

	private GridPane dataAnalysisBuildConfigurationMainContainerGridPane = new GridPane();
	private GridPane buildConfigurationMidContainerGridPane = new GridPane();

	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label(" Build Configuration");
	private SessionService s = new SessionService();
	private TrailSessionEntityService t = new TrailSessionEntityService();

	private BuildConfigurationPopup buildConfigurationPopup1 = new BuildConfigurationPopup();
	private BuildConfigurationPopup2 buildConfigurationPopup2 = new BuildConfigurationPopup2();
	private BuildConfigurationPopup3 buildConfigurationPopup3 = new BuildConfigurationPopup3();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private Button submit = new Button("Submit");
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private Pane dynamicContent = new VBox();

	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();
	private Label versionNo = new Label("Version No: ");
	private ComboBox<String> versionNo2 = new ComboBox<>();

	private Label recordBox = new Label("Select to View: ");
	private ComboBox<String> recordComboBox = new ComboBox<>();

	private Label dateLabel = new Label("Date: ");
	private Label savedDate = new Label();
	private DatePicker datePicker = new DatePicker();

	private Button close = new Button("Close");
	private Button save = new Button("Save");
	private Button fetch = new Button("Fetch");
	private Button print = new Button("Print");

	private String date;
	private Stage parentStage;

	private GridPane topSecondGridPane = new GridPane();
	private GridPane topThirdGridPane = new GridPane();

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String session_ID;
	private String selectedUttId;
	private String selectedSession;
	private String selectedSerialNo;
	private String selectedVersionNo;
	private String uutId;
	private String sessionId;
	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

//	private Label pageHeading = new Label("QUALITY MANAGEMENT / EW&A");
//	private Label secondTitle = new Label("BUILD CONFIGURATION OF DFCC");
//	private Label belPno = new Label("BEL PART NO: 1160 000 395 75");
//	private Label modType = new Label("MOD'A'");

	public BuildConfigurationController() {
		unitDataList.clear();

		// Normal sessions
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		// Trial sessions
		TrailSessionResponse t1 = t.getActiveTrailSessionId();
		sessionListTrail = t1.getListOfSession();

//		    ////System.out.println("Normal session size: " + sessionList.size());
//		    ////System.out.println("Trial session size: " + sessionListTrail.size());
	}

	public GridPane createBuildConfigurationMainContainerGridPane() {

		initializeUUTTypeComboBox();
		print.setDisable(true);
		fetch.setDisable(true);
		save.setDisable(true);
		dataAnalysisBuildConfigurationMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisBuildConfigurationMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();

		RowConstraints thirdRow = new RowConstraints();

		if (StateMachine.isDashboardBuildConfig()) {

			secondRow.setPercentHeight(88);
			thirdRow.setPercentHeight(5);
			dataAnalysisBuildConfigurationMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow,
					thirdRow);
			dataAnalysisBuildConfigurationMainContainerGridPane.add(closeButtonHbox(), 0, 2);
		} else {
			secondRow.setPercentHeight(93);
			dataAnalysisBuildConfigurationMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
		}

		dataAnalysisBuildConfigurationMainContainerGridPane.setHgap(5);
		dataAnalysisBuildConfigurationMainContainerGridPane.setVgap(5);

		dataAnalysisBuildConfigurationMainContainerGridPane.getColumnConstraints().addAll(firstColumn);

//		dataAnalysisBuildConfigurationMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		dataAnalysisBuildConfigurationMainContainerGridPane.add(headingGridPane(), 0, 0);

		dataAnalysisBuildConfigurationMainContainerGridPane.add(createBuildConfigurationGridPane(), 0, 1);

		return dataAnalysisBuildConfigurationMainContainerGridPane;
	}

	private HBox createSubmitButton() {
		HBox submitHBox = new HBox();
		submitHBox.setAlignment(Pos.CENTER);

		submitHBox.getChildren().add(submit);

		submit.setOnAction(event -> {
			Platform.runLater(() -> {
				if (uutTypeField.getSelectionModel().getSelectedItem() == null
						|| slNoField.getSelectionModel().getSelectedItem() == null) {

					Notifications.showErrorAlert("Please select UUT Type & Serial No");
					return;
				}

				DFCCConstant.selectedFetchBuildConfig = "";

//	        	if(selectedSerialNo.equals(DFCCConstant.selectedBuildConfig)) {
//	        		Notifications.showErrorAlert("Please change the serial number selection and click Submit. Only then will you be able to create a new configuration.");
//	        		return;
//	        	}
//	        	 

				dynamicContent.getChildren().clear();
				versionNo2.setEditable(true);

				GridPane popup = null;

				if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT1")) {
					popup = buildConfigurationPopup1.createBuildConfigurationMainPopupContainerGridPane();
				} else if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT2")) {
					popup = buildConfigurationPopup2.createBuildConfigurationMainPopupContainerGridPane();
				} else {
					popup = buildConfigurationPopup3.createBuildConfigurationMainPopupContainerGridPane();
				}

				dynamicContent.getChildren().add(popup);

				// CRITICAL LINE
				VBox.setVgrow(popup, Priority.ALWAYS);
				DFCCConstant.selectedBuildConfig = selectedSerialNo;
				fetch.setDisable(true);
				save.setDisable(false);

			});
		});

		return submitHBox;
	}

	public GridPane headingGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);
		headingGridPane.getStyleClass().add("dataanalysis-testing-build-save");
		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		HBox headingHbox = new HBox(10);
		headingHbox.getStyleClass().add("dataanalysis-testing-second-container");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private GridPane createBuildConfigurationGridPane() {

		buildConfigurationMidContainerGridPane.setHgap(5);
		buildConfigurationMidContainerGridPane.setVgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(10);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(83);

		buildConfigurationMidContainerGridPane.getColumnConstraints().addAll(firstColumn);
		buildConfigurationMidContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		buildConfigurationMidContainerGridPane.add(createFilterSelectionGridPane(), 0, 0);
		buildConfigurationMidContainerGridPane.add(topSecondGridPane(), 0, 1);
//		buildConfigurationMidContainerGridPane.add(topthirdGridPane(), 0, 2);
		buildConfigurationMidContainerGridPane.add(dynamicContent, 0, 2);

		return buildConfigurationMidContainerGridPane;
	}

	public GridPane topSecondGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		topSecondGridPane.getStyleClass().add("data-analysis-second-container");
		topSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		topSecondGridPane.getRowConstraints().add(firstRow);

		topSecondGridPane.add(serialNoHbox(), 0, 0);
		topSecondGridPane.add(versionNoHbox(), 1, 0);

		topSecondGridPane.add(dateHbox(), 2, 0);

		topSecondGridPane.add(saveButtonHbox(), 3, 0);

		return topSecondGridPane;

	}

	public GridPane topthirdGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		topThirdGridPane.getStyleClass().add("data-analysis-second-container");
		topThirdGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		topThirdGridPane.getRowConstraints().add(firstRow);

		topThirdGridPane.add(recordHbox(), 0, 0);
//		topThirdGridPane.add(fetch, 1, 0);

		return topThirdGridPane;

	}

	private GridPane createFilterSelectionGridPane() {
		GridPane filterResultGridPane = new GridPane();

		filterResultGridPane.getStyleClass().add("data-analysis-second-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		filterResultGridPane.setPadding(new Insets(5));

		filterResultGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		filterResultGridPane.getRowConstraints().addAll(firstRow);

		filterResultGridPane.add(createUutBox(), 0, 0);
		filterResultGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		filterResultGridPane.add(createSubmitButton(), 2, 0);

		return filterResultGridPane;
	}

	private HBox createUutBox() {
		uutTypeField.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uutTypeField);

		return uutTypeHBox;
	}

//	private void initializeUUTTypeComboBox() {
//		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
//
//		for (UUTMasterDetailsDto uut : uutDataList) {
//			uutTypeList.add(uut.getUutType());
//		}
//		
//if(StateMachine.isDashboardBuildConfig()) {
//			currentSessionDetails.getUutId();
//		}
//
//		uutTypeField.setItems(uutTypeList);
//
//		uutTypeField.setOnAction((event) -> {
//
//			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
//			uutId = fetchUutId(selectedUUTType);
//			initializeDfccSNComboBox(uutId);
//			recordComboBox.getItems().clear();
//
//			DFCCConstant.selectedUutBuildConfig = uutId;
//		});
//	}

	private void initializeUUTTypeComboBox() {

		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		if (DFCCConstant.buildDashboard) {

			String currentUutId = currentSessionDetails.getUutId();

			// Find corresponding UUT Type
			for (UUTMasterDetailsDto uut : uutDataList) {
				if (uut.getUutId().equals(currentUutId)) {
					uutTypeField.setValue(uut.getUutType());
					break;
				}
			}
			// Disable editing
			uutTypeField.setDisable(true);

			// Set constant
			DFCCConstant.selectedUutBuildConfig = currentUutId;
			submit.setDisable(true);
			datePicker.setDisable(true);
			print.setDisable(true);
			// Load Serial Number ComboBox
			initializeDfccSNComboBox(currentUutId);
		}

		uutTypeField.setOnAction((event) -> {

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);

			initializeDfccSNComboBox(uutId);
			recordComboBox.getItems().clear();

			DFCCConstant.selectedUutBuildConfig = uutId;
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
//		dfccSNList.clear();
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
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
//
//	}

	private void initializeDfccSNComboBox(String uutTypeId) {

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
		}

		slNoField.setItems(dfccSNList);
	}

	// UUT SERIAL NUMBER FIELD
//	private HBox createUUTSerialNoComboBox() {
//		HBox selectionHBoxUUTSN = new HBox(10);
//		slNoField.setPromptText("UUT S/N");
////		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
//		selectionHBoxUUTSN.setAlignment(Pos.CENTER);
//		slNoField.setEditable(true);
//		selectionHBoxUUTSN.getChildren().add(slNoField);
//		
//		slNoField.setItems(dfccSNList);
//		addSearchFunctionality(slNoField,dfccSNList );
//		
//		slNoField.setOnAction(event -> {
//			 Platform.runLater(() -> {
//		    versionNo2.setValue(null);
//		    datePicker.setValue(null);
//		    fetch.setDisable(true);
//			submit.setDisable(false);
//			save.setDisable(true);
//			print.setDisable(true);
//			 });
//			selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
//			selectedSession = "";
//			if (selectedSerialNo != null) {
////				initializeSessionComboBox(selectedSerialNo);
//				DFCCConstant.selectedSNoBuildConfig = selectedSerialNo;
//				serialNo2.setText(DFCCConstant.selectedSNoBuildConfig);
//				BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();
//				List<String> versionList =buildConfigurationManagement.getVersionList(DFCCConstant.selectedUutBuildConfig, DFCCConstant.selectedSNoBuildConfig);
//				
//				if(versionList!=null ||!versionList.isEmpty()) {
//					 Platform.runLater(() -> {
//					print.setDisable(true);
//					 });
//				}
//				versionNo2.setItems(FXCollections.observableArrayList(versionList));
//				
//			}
//			
//		});
//
//	
//
//		return selectionHBoxUUTSN;
//	}

	private HBox createUUTSerialNoComboBox() {

		HBox selectionHBoxUUTSN = new HBox(10);
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setAlignment(Pos.CENTER);

		slNoField.setEditable(true);
		selectionHBoxUUTSN.getChildren().add(slNoField);

		slNoField.setItems(dfccSNList);
		addSearchFunctionality(slNoField, dfccSNList);

		// Dashboard Build Config Default Selection
		if (DFCCConstant.buildDashboard) {

			String currentSerialNo = currentSessionDetails.getDfccSerialNumber();

			slNoField.setValue(currentSerialNo);
			slNoField.setDisable(true);

			DFCCConstant.selectedSNoBuildConfig = currentSerialNo;
			serialNo2.setText(currentSerialNo);

			BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();
			List<String> versionList = buildConfigurationManagement.getVersionList(DFCCConstant.selectedUutBuildConfig,
					currentSerialNo);

			versionNo2.setItems(FXCollections.observableArrayList(versionList));
		}

		slNoField.setOnAction(event -> {

			Platform.runLater(() -> {
				versionNo2.setValue(null);
				datePicker.setValue(null);
				fetch.setDisable(true);
				submit.setDisable(false);
				save.setDisable(true);
				print.setDisable(true);
			});

			selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
			selectedSession = "";

			if (selectedSerialNo != null) {

				DFCCConstant.selectedSNoBuildConfig = selectedSerialNo;
				serialNo2.setText(DFCCConstant.selectedSNoBuildConfig);

				BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();
				List<String> versionList = buildConfigurationManagement
						.getVersionList(DFCCConstant.selectedUutBuildConfig, DFCCConstant.selectedSNoBuildConfig);

				if (versionList != null && !versionList.isEmpty()) {
					Platform.runLater(() -> print.setDisable(true));
				}

				versionNo2.setItems(FXCollections.observableArrayList(versionList));
			}
		});

		return selectionHBoxUUTSN;
	}

	// SESSION FIELD
	private HBox createSessionComboBox() {
		HBox selectionBoxSESSION = new HBox(10);
		sessionNameField.setPromptText("Session");
//		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
		selectionBoxSESSION.setAlignment(Pos.CENTER);
		selectionBoxSESSION.getChildren().add(sessionNameField);

		return selectionBoxSESSION;
	}

	private String fetchSessionId(String sessionType) {
		for (SessionDto sessionId : sessionList) {
			if (sessionId.getSessionName().equals(sessionType)) {
				return sessionId.getSessionId();
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

//	    // UUT SESSION NAME TYPE FIELD
	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();

		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo)) // Correct
																														// filtering
																														// condition
				.collect(Collectors.toList());

		for (SessionDto sessionName : filterSessionList) {
			sessionTypeList.add(sessionName.getSessionName());
		}

		sessionNameField.setOnAction(event -> {
			selectedSession = sessionNameField.getSelectionModel().getSelectedItem();
			sessionId = fetchSessionId(selectedSession);

		});

		sessionNameField.setItems(sessionTypeList);
	}

	private HBox serialNoHbox() {
		HBox serialNoHbox = new HBox(10);
		serialNoHbox.setAlignment(Pos.CENTER);
		serialNo.getStyleClass().add("title-label");
		serialNo2.getStyleClass().add("title-label");
		serialNoHbox.getChildren().addAll(serialNo, serialNo2);

		return serialNoHbox;
	}

	private HBox versionNoHbox() {
		HBox versionNoHbox = new HBox(10);
		versionNoHbox.setAlignment(Pos.CENTER);
//		versionNoHbox.setPadding(new Insets(20));
		versionNo.getStyleClass().add("title-label");
		versionNo2.getStyleClass().add("title-label");
		versionNo2.setOnAction(event -> {
			submit.setDisable(true);
			fetch.setDisable(false);
			save.setDisable(true);

			DFCCConstant.selectedVersionNoBuildConfig = versionNo2.getValue();
			selectedVersionNo = versionNo2.getSelectionModel().getSelectedItem();
			if (versionNo2.getItems().contains(selectedVersionNo)) {
				datePicker.setDisable(true);
			} else {
				datePicker.setDisable(false);
			}
			if (selectedVersionNo != null) {
//				initializeSessionComboBox(selectedSerialNo);
//				////System.out.println("Checking entred version" + selectedVersionNo );
				Platform.runLater(() -> {
					topSecondGridPane.getChildren().removeIf(
							node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 2);

					topSecondGridPane.add(dateHbox(), 2, 0);
				});

				BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();
				List<String> historyDataList = buildConfigurationManagement
						.getDateHistoryList(DFCCConstant.selectedUutBuildConfig, selectedVersionNo);
//				////System.out.println("Last updatd date size: " + historyDataList.size());
				recordComboBox.setItems(FXCollections.observableArrayList(historyDataList));

			}

		});

		versionNoHbox.getChildren().addAll(versionNo, versionNo2);

		return versionNoHbox;
	}

	private HBox recordHbox() {
		HBox recordHbox = new HBox(10);
		recordHbox.setAlignment(Pos.CENTER);
//		recordHbox.setPadding(new Insets(20));
		recordBox.getStyleClass().add("title-label");
		recordComboBox.getStyleClass().add("title-label");
		recordComboBox.setOnAction(event -> {
		});

		recordHbox.getChildren().addAll(recordBox, recordComboBox);

		return recordHbox;
	}

	private HBox saveDateHbox() {
		HBox saveDateHbox = new HBox(10);
		saveDateHbox.setAlignment(Pos.CENTER);
//	    dateHbox.setPadding(new Insets(20));
		dateLabel.getStyleClass().add("title-label");

		savedDate.getStyleClass().add("title-label");

		datePicker.getStyleClass().add("date-picker");

		saveDateHbox.getChildren().addAll(dateLabel, savedDate);
		return saveDateHbox;
	}

	private HBox dateHbox() {
		HBox dateHbox = new HBox(10);
		dateHbox.setAlignment(Pos.CENTER);
//	    dateHbox.setPadding(new Insets(20));
		dateLabel.getStyleClass().add("title-label");
		datePicker.getStyleClass().add("date-picker");

		dateHbox.getChildren().addAll(dateLabel, datePicker);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		datePicker.setPromptText("Select Date");

		datePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				return (date != null) ? formatter.format(date) : "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string == null || string.trim().isEmpty())
					return null;
				return LocalDate.parse(string, formatter);
			}
		});

		datePicker.setDayCellFactory(dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				save.setDisable(false);
				submit.setDisable(false);
				fetch.setDisable(true);
				print.setDisable(true);
				super.updateItem(date, empty);
				if (date != null && date.isAfter(LocalDate.now())) {
					setDisable(true);
				}
			}
		});

		return dateHbox;
	}

	private HBox saveButtonHbox() {
		HBox saveButtonHbox = new HBox();
		saveButtonHbox.setAlignment(Pos.CENTER_LEFT);
		saveButtonHbox.setSpacing(5);
		saveButtonHbox.getChildren().addAll(save, print, fetch);

		print.setOnAction(e -> {
			if ((uutTypeField.getSelectionModel().getSelectedItem() == null
					|| slNoField.getSelectionModel().getSelectedItem() == null
					|| versionNo2.getSelectionModel().getSelectedItem() == null || DFCCConstant.savedDate == null)) {

				Notifications.showErrorAlert("Please select UUT Type, Serial No, Version No");
				return;
			}

//		    //System.out.println(" DFCCConstant.savedDate " +  DFCCConstant.savedDate );
			DFCCConstant.buildFetched = false;
			try {
				BuildConfigurationReport buildConfigurationReport = new BuildConfigurationReport();
				buildConfigurationReport.generateBuildConfigurationReport(DFCCConstant.selectedUutBuildConfig,
						serialNo2.getText(), datePicker.getEditor().getText(), versionNo2.getEditor().getText(),
						DFCCConstant.selectedUutBuildConfig, DFCCConstant.savedDate.toString());

//		        ////System.out.println("Check file path" +buildConfigurationReport.getLastGeneratedFilePath() );
				printPDF(buildConfigurationReport.getLastGeneratedFilePath());

			} catch (Exception ex) {
				ex.printStackTrace();
				Notifications.showErrorAlert("Printing failed: " + ex.getMessage());
			}
		});

		fetch.setOnAction(e -> {

			DFCCConstant.selectedFetchBuildConfig = "Fetch";

			if (uutTypeField.getSelectionModel().getSelectedItem() == null
					|| slNoField.getSelectionModel().getSelectedItem() == null
					|| versionNo2.getSelectionModel().getSelectedItem() == null) {

				Notifications.showErrorAlert("Please select UUT Type, Serial No, Version No");
				return;
			}
			if (!DFCCConstant.buildDashboard) {
				print.setDisable(false);
			}
			

			DFCCConstant.buildFetched = true;

			DFCCConstant.selectedBuildConfigDate = recordComboBox.getSelectionModel().getSelectedItem();

			dynamicContent.getChildren().clear();
			versionNo2.setEditable(false);

			GridPane popup = null;

			if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT1")) {
				popup = buildConfigurationPopup1.createBuildConfigurationMainPopupContainerGridPane();
			} else if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT2")) {
				popup = buildConfigurationPopup2.createBuildConfigurationMainPopupContainerGridPane();
			} else {
				popup = buildConfigurationPopup3.createBuildConfigurationMainPopupContainerGridPane();
			}

			dynamicContent.getChildren().add(popup);
			VBox.setVgrow(popup, Priority.ALWAYS);

			DFCCConstant.selectedBuildConfig = selectedSerialNo;

			// WAIT until backend sets savedDate
			PauseTransition delay = new PauseTransition(Duration.millis(300));

			delay.setOnFinished(event -> {

				savedDate.setText(DFCCConstant.savedDate);

//		        //System.out.println("Updated savedDate : " + savedDate.getText());

				topSecondGridPane.getChildren()
						.removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 2);

				topSecondGridPane.add(saveDateHbox(), 2, 0);

			});

			delay.play();
			save.setDisable(true);
		});

		save.setOnAction(e -> {

			if (versionNo2.getEditor().getText().trim().isEmpty()) {
				Notifications.showErrorAlert("Please enter version");
				return;
			}

			// CONFIRMATION POPUP
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to save the data?\nOnce saved, it cannot be edited.");

			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No");

			alert.getButtonTypes().setAll(yesButton, noButton);

			Optional<ButtonType> result = alert.showAndWait();

			// If user clicks NO -> return
			if (result.isEmpty() || result.get() != yesButton) {
				return;
			}

			// Continue saving if YES clicked

			DFCCConstant.selectedBuildConfigDate = datePicker.getEditor().getText();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate selectedDate = datePicker.getValue();
			date = savedDate.getText();
			if (date == null) {

				if (selectedDate != null) {
					date = selectedDate.format(formatter);
				} else {
					Notifications.showErrorAlert("Please select Date");
					return;
				}
			}

			if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT1")) {
				buildConfigurationPopup1.saveButton(DFCCConstant.selectedUutBuildConfig, serialNo2.getText(),
						versionNo2.getEditor().getText(), datePicker.getEditor().getText());

			} else if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT2")) {

				buildConfigurationPopup2.saveButton(DFCCConstant.selectedUutBuildConfig, serialNo2.getText(),
						versionNo2.getEditor().getText(), datePicker.getEditor().getText());

			} else {

				buildConfigurationPopup3.saveButton(DFCCConstant.selectedUutBuildConfig, serialNo2.getText(),
						versionNo2.getEditor().getText(), datePicker.getEditor().getText());
			}
			Platform.runLater(() -> {
				versionNo2.setValue(null);
				datePicker.setValue(null);
				uutTypeField.setValue(null);
				slNoField.setValue(null);
				serialNo2.setText("");
				fetch.setDisable(true);
				submit.setDisable(true);
				save.setDisable(true);
				print.setDisable(true);
				dynamicContent.getChildren().clear();

			});

			Notifications.showSuccessAlert("Build Configuration saved successfully");

		});
		return saveButtonHbox;
	}

	public static void printPDF(String pdfPath) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			ProcessBuilder pb;

			if (os.contains("win")) {
				pb = new ProcessBuilder("cmd.exe", "/c", "start", "msedge", "/print", pdfPath);
			} else if (os.contains("mac")) {
				pb = new ProcessBuilder("open", pdfPath);
			} else { // Linux/Unix
				pb = new ProcessBuilder("xdg-open", pdfPath);
			}
			pb.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			Notifications.showErrorAlert("Printing failed: " + ex.getMessage());
		}
	}

	private HBox closeButtonHbox() {

		Button close = new Button("Close");

		HBox closeButtonHbox = new HBox(close);
		closeButtonHbox.setAlignment(Pos.CENTER);
//	    closeButtonHbox.setPadding(new Insets(10));

		close.setOnAction(e -> {
			uutTypeField.getSelectionModel().clearSelection();
			uutTypeField.getEditor().clear();

			slNoField.getSelectionModel().clearSelection();
			slNoField.getEditor().clear();

			versionNo2.getSelectionModel().clearSelection();
			versionNo2.getEditor().clear();

			recordComboBox.getSelectionModel().clearSelection();
			recordComboBox.getEditor().clear();

			StateMachine.setDashboardBuildConfig(false);

			Stage popupStage = (Stage) close.getScene().getWindow();
			popupStage.close();
//	        headingGridPane.getChildren().clear();   
//	        buildConfigurationMidContainerGridPane.getChildren().clear();
//	        topThirdGridPane.getChildren().clear();
//	        topSecondGridPane.getChildren().clear();
//	        topThirdGridPane.getChildren().clear();
//            dataAnalysisBuildConfigurationMainContainerGridPane
//                    .getChildren().clear();

			if (parentStage != null) {
				parentStage.close();

			}
		});

		return closeButtonHbox;
	}

}
