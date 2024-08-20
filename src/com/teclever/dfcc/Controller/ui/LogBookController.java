package com.teclever.dfcc.Controller.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.response.UUTLogBookResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class LogBookController {
	private GridPane logBookMainGridPane = new GridPane();
	private GridPane logBookHeadingGridPane = new GridPane();
	private GridPane logBookSelectionGridPane = new GridPane();
	private GridPane logBookMidGridPane = new GridPane();
	private GridPane logBookBottomGridPane = new GridPane();
	private GridPane logBookAitessTextAreaGridPane = new GridPane();
	private GridPane logBookUUTTextAreaGridPane = new GridPane();
	private GridPane logBookUserInputTextAreaGridPane = new GridPane();
	SessionService s = new SessionService();
	private HBox titleBox = new HBox();
	private Label title = new Label();

	private HBox leftTitleHBox = new HBox();
	private Label leftLabel = new Label("Application Log");

	private HBox rightTitleHBox = new HBox();
	private Label rightLabel = new Label("UUT Log");

	private HBox bottomTitleHBox = new HBox();
	private HBox bottomButtonHBox = new HBox();
	private HBox refreshButtonHBox = new HBox();
	private Button bottomSubmitButton = new Button("Submit");
	private Button refreshButton = new Button("Refresh");
	private Label bottomLabel = new Label("User Input");

	private HBox selectionHBoxUUTType = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);
	private HBox selectionBoxSESSION = new HBox(10);
	private HBox datePickerFromHBox = new HBox(10);
	private HBox datePickerToHBox = new HBox(10);
	private HBox userInputDatePickerToHBox = new HBox(10);

	private DatePicker fromDate = new DatePicker();
	private DatePicker toDate = new DatePicker();
	private DatePicker userInputDate = new DatePicker();
	
	private TextArea aitessTextArea = new TextArea();
	private TextArea uutTextArea = new TextArea();
	private TextArea userInputTextArea = new TextArea();

	public ComboBox<String> uutTypeField = new ComboBox<>();
	public ComboBox<String> uutSerialNoField = new ComboBox<>();
	public ComboBox<String> sessionField = new ComboBox<>();
	
	private ComboBox<String> fromTimePicker = new ComboBox<>();
	private ComboBox<String> toTimePicker = new ComboBox<>();
	private ComboBox<String> userInputTimePicker = new ComboBox<>();

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> sessionDfccId = new HashMap<String, String>();
	List<SessionDto> sessionList = new ArrayList<SessionDto>();

	String selectedUUTType;
	String selectedSessionType;
	String selectedDfccSN;
	Date selectedFromDate;
	Date selectedUserInputDate;// Get the selected date as a LocalDate
	Date selectedToDate;
	String seclectFromTime;
	String seclectToTime;
	
	
	String uutLogData = null;
	String sessionLogData = null;
	String dfccSNLogData = null;
	String fromDateLogData = null;
	String toDateLogData = null;
	
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	public LogBookController() {
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();
		
		for (SessionDto session : sessionList) {
			sessionNameId.put(session.getSessionName(), session.getSessionId());
			sessionDfccId.put(session.getSessionName(), session.getDfccSNo());
			
	    }
		
		populateAllDataAitess();
		populateAllDataUUT();
	}

	public GridPane createLogBookMainGridPane() {
		initializeUUTTypeComboBox();
		logBookMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LogBook.css").toExternalForm());
		logBookMainGridPane.getStyleClass().add("log-book-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(14);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(53);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(26);

		logBookMainGridPane.setPadding(new Insets(5));
		logBookMainGridPane.setVgap(5);
		logBookMainGridPane.setHgap(5);
		logBookMainGridPane.getColumnConstraints().addAll(firstColumn);
		logBookMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);

		logBookMainGridPane.add(createHeadingBox(), 0, 0);
		logBookMainGridPane.add(createLogBookSelectionGridPane(), 0, 1);
		logBookMainGridPane.add(createLogBookMidGridPane(), 0, 2);
		logBookMainGridPane.add(createLogBookBottomGridPane(), 0, 3);

		return logBookMainGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		logBookHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		logBookHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("LOG BOOK");
		title.getStyleClass().add("headerLabel");
		titleBox.getChildren().add(title);

		logBookHeadingGridPane.add(titleBox, 0, 0);

		return logBookHeadingGridPane;
	}

	private GridPane createLogBookSelectionGridPane() {
		logBookSelectionGridPane.getStyleClass().add("logbook-mid-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(20);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(20);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		logBookSelectionGridPane.setPadding(new Insets(5));

		logBookSelectionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		logBookSelectionGridPane.getRowConstraints().addAll(firstRow, secondRow);

		logBookSelectionGridPane.add(createUUTypeComboBox(), 0, 0);
		logBookSelectionGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		logBookSelectionGridPane.add(createSessionComboBox(), 2, 0);
		logBookSelectionGridPane.add(createFromDatePickerComboBox(), 3, 0);
		logBookSelectionGridPane.add(createToDatePickerComboBox(), 4, 0);
		logBookSelectionGridPane.add(createFromTimePicker(), 3, 1);
		logBookSelectionGridPane.add(createToTimePicker(), 4, 1);
		logBookSelectionGridPane.add(createRefreshButton(), 2, 1);

		return logBookSelectionGridPane;
	}

	private void populateAllDataAitess() {

		ApplicationLogbookManagement app = new ApplicationLogbookManagement();

		List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(null, null, null, null, null,null);
		ObservableList<String> fileData = FXCollections.observableArrayList();
		for (ApplicationLogBookDto s : logBookEntries) {
			uutLogData = s.getDetails();
			fileData.add(uutLogData);
		}

		StringBuilder text = new StringBuilder();
        for (String item : fileData) {
            text.append(item).append("\n");
        }

        // Set the text to the TextArea
        aitessTextArea.setText(text.toString());

        // Optional: Set the TextArea to be non-editable
        aitessTextArea.setEditable(false);

	}
	
	private void populateAllDataUUT() {

		UUTLogbookManagement app = new UUTLogbookManagement();

		List<UUTLogBookDto> logBookEntries = app.getUUTLogBooksByDate(null, null, null, null, null, null);
		ObservableList<String> fileData = FXCollections.observableArrayList();
		for (UUTLogBookDto s : logBookEntries) {
			uutLogData = s.getDetails();
			fileData.add(uutLogData);
		}

		StringBuilder text = new StringBuilder();
        for (String item : fileData) {
            text.append(item).append("\n");
        }

        // Set the text to the TextArea
        uutTextArea.setText(text.toString());

        // Optional: Set the TextArea to be non-editable
        uutTextArea.setEditable(false);

	}

	private HBox createFromTimePicker() {
		fromTimePicker.setPromptText("FROM TIME");

		// Populate time picker with combined hour and minute
		fromTimePicker.setItems(FXCollections.observableArrayList(generateTimeOptions()));

		HBox timePickerHBox = new HBox(5);
		timePickerHBox.setAlignment(Pos.CENTER_LEFT);
		timePickerHBox.setPadding(new Insets(0, 0, 0, 18.5));
		timePickerHBox.getChildren().add(fromTimePicker);

		return timePickerHBox;
	}

	private HBox createToTimePicker() {
		toTimePicker.setPromptText("TO TIME");

		// Populate time picker with combined hour and minute
		toTimePicker.setItems(FXCollections.observableArrayList(generateTimeOptions()));

		HBox timePickerHBox = new HBox(5);
		timePickerHBox.setAlignment(Pos.CENTER_LEFT);
		timePickerHBox.setPadding(new Insets(0, 0, 0, 18.5));
		timePickerHBox.getChildren().add(toTimePicker);

		return timePickerHBox;
	}

	private ObservableList<String> generateTimeOptions() {
	    ObservableList<String> timeOptions = FXCollections.observableArrayList();
	    for (int hour = 0; hour < 24; hour++) {
	        for (int minute = 0; minute < 60; minute += 15) { // Interval of 15 minutes
	            timeOptions.add(String.format("%02d:%02d:00", hour, minute)); // Seconds are always "00"
	        }
	    }
	    return timeOptions;
	}

	// UUT TYPE FIELD
	private void initializeUUTTypeComboBox() {

		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			String uutId = fetchUutId(selectedUUTType);
			System.out.println("UUT TYPE: " + selectedUUTType + ", UUT ID: " + uutId);
			initializeSessionComboBox(uutId);
			initializeDfccSNComboBox(uutId);
		});

	}

	private String createAitessLogData(String uutType) {

		String selectedUUTType = fetchUutId(uutType);
		if (selectedUUTType != null) {

			ApplicationLogbookManagement app = new ApplicationLogbookManagement();

			List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(selectedUUTType, selectedDfccSN, selectedSessionType,
					selectedFromDate, selectedToDate,StateMachine.getCurrentUserLogin());
			ObservableList<String> fileData = FXCollections.observableArrayList();
			for (ApplicationLogBookDto s : logBookEntries) {
				uutLogData = s.getDetails();
				System.out.println(s.getTimestamp());
				fileData.add(uutLogData);
			}
			StringBuilder text = new StringBuilder();
	        for (String item : fileData) {
	            text.append(item).append("\n");
	        }

	        // Set the text to the TextArea
	        aitessTextArea.setText(text.toString());

	        // Optional: Set the TextArea to be non-editable
	        aitessTextArea.setEditable(false);

			
			
		

		} else {
			System.out.println("No UUT found for the given type.");
		}
		return selectedUUTType;
	}
	

	
	private String createUutLogData(String uutType) {

		String selectedUUTType = fetchUutId(uutType);
		if (selectedUUTType != null) {

			UUTLogbookManagement app = new UUTLogbookManagement();

			List<UUTLogBookDto> logBookEntries = app.getUUTLogBooksByDate(selectedUUTType, selectedDfccSN, selectedSessionType, selectedFromDate, selectedToDate, StateMachine.getCurrentUserLogin());
			ObservableList<String> fileData = FXCollections.observableArrayList();
			for (UUTLogBookDto s : logBookEntries) {
				uutLogData = s.getDetails();
				System.out.println(s.getTimestamp());
				fileData.add(uutLogData);
			}
			
			StringBuilder text = new StringBuilder();
	        for (String item : fileData) {
	            text.append(item).append("\n");
	        }

	        // Set the text to the TextArea
	        uutTextArea.setText(text.toString());

	        // Optional: Set the TextArea to be non-editable
	        uutTextArea.setEditable(false);

		} else {
			System.out.println("No UUT found for the given type.");
		}
		return selectedUUTType;
	}


	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	private HBox createUUTypeComboBox() {
		uutTypeField.setPromptText("UUT TYPE");
		selectionHBoxUUTType.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTType.setAlignment(Pos.CENTER_LEFT);
		selectionHBoxUUTType.getChildren().add(uutTypeField);

		return selectionHBoxUUTType;
	}

	// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		uutSerialNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER_LEFT);
		selectionHBoxUUTSN.getChildren().add(uutSerialNoField);

		return selectionHBoxUUTSN;
	}

	// SESSION FIELD
	private HBox createSessionComboBox() {
		sessionField.setPromptText("Session");
		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
		selectionBoxSESSION.setAlignment(Pos.CENTER_LEFT);
		selectionBoxSESSION.getChildren().add(sessionField);

		return selectionBoxSESSION;
	}

//    // UUT SESSION NAME TYPE FIELD
	private void initializeSessionComboBox(String uutTypeId) {
		sessionTypeList.clear();

		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
				.collect(Collectors.toList());
		for (SessionDto sessionName : filterSessionList) {
			sessionTypeList.add(sessionName.getSessionName());
			System.out.println("Session Name:" + sessionName.getSessionName());

		}

		sessionField.setItems(sessionTypeList);
	}

	
//  // UUT SESSION DFCC S/N FIELD
	private void initializeDfccSNComboBox(String uutTypeId) {
		dfccSNList.clear();

		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
				.collect(Collectors.toList());
		
		for (SessionDto dfccSn : filterSessionList) {
			dfccSNList.add(dfccSn.getDfccSNo());
			System.out.println("Dfcc SN:" + dfccSn.getDfccSNo());

		}

		uutSerialNoField.setItems(dfccSNList);
	}

	

	
	
	// DATE PICKER - FROM
	private HBox createFromDatePickerComboBox() {
		fromDate.setPromptText("FROM DATE");
		datePickerFromHBox.setPadding(new Insets(0, 0, 0, 18.5));
		datePickerFromHBox.setAlignment(Pos.CENTER_LEFT);
		datePickerFromHBox.getChildren().add(fromDate);

		return datePickerFromHBox;
	}

	// DATE PICKER - TO
	private HBox createToDatePickerComboBox() {
		toDate.setPromptText("TO DATE");
		datePickerToHBox.setPadding(new Insets(0, 0, 0, 18.5));
		datePickerToHBox.setAlignment(Pos.CENTER_LEFT);
		datePickerToHBox.getChildren().add(toDate);

		return datePickerToHBox;
	}

	private HBox createRefreshButton() {
		refreshButton.getStyleClass().add("logBook-container");
	    refreshButtonHBox.setAlignment(Pos.CENTER);
	    refreshButtonHBox.getChildren().add(refreshButton);
	    refreshButton.setOnAction(e -> {
	    refreshButton();
	    });
	    return refreshButtonHBox;
	}

	private void refreshButton() {
		
	        // Get selected UUT type, serial number, and session type
	        selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
	        selectedDfccSN = uutSerialNoField.getSelectionModel().getSelectedItem();
	        selectedSessionType = sessionNameId.get(sessionField.getSelectionModel().getSelectedItem());

	        // Handle date selection
	        LocalDate localFromDate = fromDate.getValue();
	        LocalDate localToDate = toDate.getValue();
	        
	        

	        // Handle time selection from ComboBox
	        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	        LocalTime selectedFromTime = null;
	        LocalTime selectedToTime = null;

	        if (fromTimePicker.getSelectionModel().getSelectedItem() != null) {
	            selectedFromTime = LocalTime.parse(fromTimePicker.getSelectionModel().getSelectedItem(), timeFormatter);
	        }
	        if (toTimePicker.getSelectionModel().getSelectedItem() != null) {
	            selectedToTime = LocalTime.parse(toTimePicker.getSelectionModel().getSelectedItem(), timeFormatter);
	        }

	        // Combine date and time into Date objects
	        if (localFromDate != null && selectedFromTime != null) {
	            selectedFromDate = Date.from(LocalDateTime.of(localFromDate, selectedFromTime)
	                    .atZone(ZoneId.systemDefault()).toInstant());
	            System.out.println("From Date And Atime :" + selectedFromDate );
	            
	        } else if (localFromDate != null) { // If time is not selected, use start of the day
	            selectedFromDate = Date.from(localFromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        }

	        if (localToDate != null && selectedToTime != null) {
	            selectedToDate = Date.from(LocalDateTime.of(localToDate, selectedToTime)
	                    .atZone(ZoneId.systemDefault()).toInstant());
	            System.out.println("to Date And Atime :" + selectedToDate );
	        } else if (localToDate != null) { // If time is not selected, use end of the day
	            selectedToDate = Date.from(localToDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        }

	        // Process log data based on selected UUT type
	        if (selectedUUTType != null) {
	            createAitessLogData(selectedUUTType);
	            createUutLogData(selectedUUTType);
	        }
	  

	}

	private GridPane createLogBookMidGridPane() {
		logBookMidGridPane.getStyleClass().add("logbook-mid-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		logBookMidGridPane.setPadding(new Insets(5));
		logBookMidGridPane.setHgap(2);
//        logBookMidGridPane.setVgap(5);

		logBookMidGridPane.getColumnConstraints().addAll(firstColumn);
		logBookMidGridPane.getRowConstraints().addAll(firstRow, secondRow);

		logBookMidGridPane.add(createLeftSideTitle(), 0, 0, 1, 2);
		logBookMidGridPane.add(createAitessTextArea(), 0, 1);

		logBookMidGridPane.add(createRightSideTitle(), 1, 0, 2, 2);
		logBookMidGridPane.add(createUutTextArea(), 1, 1);

		return logBookMidGridPane;
	}

	private HBox createLeftSideTitle() {
		leftTitleHBox.getStyleClass().add("logBook-container");
		leftLabel.getStyleClass().add("title-label");
		leftTitleHBox.setAlignment(Pos.TOP_LEFT);

		leftTitleHBox.getChildren().add(leftLabel);
		return leftTitleHBox;
	}

	private HBox createRightSideTitle() {
		rightTitleHBox.getStyleClass().add("logBook-container");
		rightLabel.getStyleClass().add("title-label");
		rightTitleHBox.setAlignment(Pos.TOP_LEFT);
		rightTitleHBox.getChildren().add(rightLabel);
		return rightTitleHBox;
	}

	private GridPane createAitessTextArea() {
		aitessTextArea.getStyleClass().add("logbook-textarea");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		logBookAitessTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
		logBookAitessTextAreaGridPane.getRowConstraints().addAll(firstRow);

		logBookAitessTextAreaGridPane.add(aitessTextArea, 0, 0);

		return logBookAitessTextAreaGridPane;
	}

	private GridPane createUutTextArea() {
		uutTextArea.getStyleClass().add("logbook-textarea");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		logBookUUTTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
		logBookUUTTextAreaGridPane.getRowConstraints().addAll(firstRow);

		logBookUUTTextAreaGridPane.add(uutTextArea, 0, 0);

		return logBookUUTTextAreaGridPane;
	}

	private GridPane createLogBookBottomGridPane() {
		logBookBottomGridPane.getStyleClass().add("logbook-mid-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(20);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(80);

		logBookBottomGridPane.setPadding(new Insets(5));
		logBookBottomGridPane.setHgap(5);
		logBookBottomGridPane.setVgap(5);
		logBookBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		logBookBottomGridPane.getRowConstraints().addAll(firstRow, secondRow);

		logBookBottomGridPane.add(createBottomTitle(), 0, 0);
		logBookBottomGridPane.add(createUserInputTextArea(), 0, 1, 4, 1);
		logBookBottomGridPane.add(createBottomButton(), 3, 0);
		logBookBottomGridPane.add(createUserDatePickerComboBox(), 1, 0);
		logBookBottomGridPane.add(userInputTimePicker(), 2, 0);
		
		
		

		return logBookBottomGridPane;
	}

	private HBox createBottomTitle() {
		bottomTitleHBox.getStyleClass().add("logBook-bottom-container");
		bottomLabel.getStyleClass().add("title-label");
		bottomTitleHBox.setAlignment(Pos.TOP_LEFT);
		bottomTitleHBox.getChildren().add(bottomLabel);
		return bottomTitleHBox;
	}

	private GridPane createUserInputTextArea() {
		userInputTextArea.getStyleClass().add("logbook-textarea");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		logBookUserInputTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
		logBookUserInputTextAreaGridPane.getRowConstraints().addAll(firstRow);

		logBookUserInputTextAreaGridPane.add(userInputTextArea, 0, 0);

		return logBookUserInputTextAreaGridPane;
	}
	
	private HBox userInputTimePicker() {
		userInputTimePicker.setPromptText("USER TIME");

		// Populate time picker with combined hour and minute
		userInputTimePicker.setItems(FXCollections.observableArrayList(generateUserInputTimeOptions()));

		HBox timePickerHBox = new HBox(5);
		timePickerHBox.setAlignment(Pos.CENTER_LEFT);
		timePickerHBox.setPadding(new Insets(0, 0, 0, 18.5));
		timePickerHBox.getChildren().add(userInputTimePicker);

		return timePickerHBox;
	}

	private ObservableList<String> generateUserInputTimeOptions() {
	    ObservableList<String> timeOptions = FXCollections.observableArrayList();
	    for (int hour = 0; hour < 24; hour++) {
	        for (int minute = 0; minute < 60; minute += 15) { // Interval of 15 minutes
	            timeOptions.add(String.format("%02d:%02d:00", hour, minute)); // Seconds are always "00"
	        }
	    }
	    return timeOptions;
	}

	private HBox createUserDatePickerComboBox() {
		userInputDate.setPromptText("USER DATE");
		userInputDatePickerToHBox.setPadding(new Insets(0, 0, 0, 18.5));
		userInputDatePickerToHBox.setAlignment(Pos.CENTER_LEFT);
		userInputDatePickerToHBox.getChildren().add(userInputDate);

		return userInputDatePickerToHBox;
	}
	

	private HBox createBottomButton() {
		
	    bottomSubmitButton.getStyleClass().add("logBook-container");
	    bottomButtonHBox.setAlignment(Pos.CENTER_RIGHT);
	    bottomButtonHBox.getChildren().add(bottomSubmitButton);

	    bottomSubmitButton.setOnAction(e -> {
	        String userInput = userInputTextArea.getText();
	        
	        
	        
	        selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
	       String uutId = fetchUutId(selectedUUTType);
	        selectedDfccSN = uutSerialNoField.getSelectionModel().getSelectedItem();
	        selectedSessionType = sessionNameId.get(sessionField.getSelectionModel().getSelectedItem());
	        
	        if (userInput.isEmpty()) {
	            Notifications.showErrorAlert("Please enter the user data.");
	            return; // Stop execution if the input is empty
	        }
	        
	        if (uutTypeField.getSelectionModel().getSelectedItem() == null) {
	            Notifications.showErrorAlert("Please select UUT.");
	            return;
	        }
	        
	        if (uutSerialNoField.getSelectionModel().getSelectedItem() == null) {
	            Notifications.showErrorAlert("Please select UUT Serial No.");
	            return;
	        }
	        
	        if (sessionField.getSelectionModel().getSelectedItem() == null) {
	            Notifications.showErrorAlert("Please select Session Name.");
	            return;
	        }

	        LocalDate localUserInputDate = userInputDate.getValue();
	        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	        LocalTime selectedUserInputTime = null;
	        
	        if (userInputTimePicker.getSelectionModel().getSelectedItem() != null) {
	        	selectedUserInputTime = LocalTime.parse(userInputTimePicker.getSelectionModel().getSelectedItem(), timeFormatter);
	        }
	        if (localUserInputDate != null && selectedUserInputTime != null) {
	        	selectedUserInputDate = Date.from(LocalDateTime.of(localUserInputDate, selectedUserInputTime)
	                    .atZone(ZoneId.systemDefault()).toInstant());
	            System.out.println("User Input Time:" + selectedUserInputDate );
	        }
	        // If all checks pass, proceed with the submission
	        UUTLogbookManagement uutLogBookManagement = new UUTLogbookManagement();
	        UUTLogBookDto uutDto = new UUTLogBookDto();
	        uutDto.setSessionId(selectedSessionType);
	        System.out.println("sessionName:: " + selectedSessionType);
	        uutDto.setUutId(uutId);
	        
	        if(selectedUserInputDate!=null) {
	        uutDto.setTimestamp(selectedUserInputDate);
	       
	        }else {
	        	uutDto.setTimestamp(new Date());
	        }
	        uutDto.setUsername(StateMachine.getCurrentUserLogin());
	        uutDto.setUutSerialNumber(selectedDfccSN);
	        uutDto.setDetails(userInput);
	        
	        userInputDate.setValue(null);
	        userInputTimePicker.setValue(null);
            
	        userInputTextArea.clear();
            
            // Add UUT logbook entry and get the response
	        UUTLogBookResponse response = uutLogBookManagement.addUUTLogBook(uutDto);
	        
//	        if (response.getResponseCode() == 1) {
//	            Notifications.showSuccessAlert(response.getResponseMessage());
//	            userInputTextArea.clear(); // Clear input only on success
//	        } else if (response.getResponseCode() == 0) {
//	            Notifications.showErrorAlert(response.getResponseMessage());
//	        }
	        refreshButton(); 
	    });
	   
	    return bottomButtonHBox;
	}

}










//package com.teclever.dfcc.Controller.ui;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import com.teclever.datastore.dto.SessionDto;
//import com.teclever.datastore.dto.SessionResponse;
//import com.teclever.datastore.response.UUTLogBookResponse;
//import com.teclever.datastore.service.SessionService;
//import com.teclever.dfcc.DFCCConstant;
//import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
//import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
//import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
//import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
//import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
//import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
//import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
//import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
//import com.teclever.dfcc.stateMachine.StateMachine;
//import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
//import com.teclever.dfcc.utils.Notifications;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//
//public class LogBookController {
//	private GridPane logBookMainGridPane = new GridPane();
//	private GridPane logBookHeadingGridPane = new GridPane();
//	private GridPane logBookSelectionGridPane = new GridPane();
//	private GridPane logBookMidGridPane = new GridPane();
//	private GridPane logBookBottomGridPane = new GridPane();
//	private GridPane logBookAitessTextAreaGridPane = new GridPane();
//	private GridPane logBookUUTTextAreaGridPane = new GridPane();
//	private GridPane logBookUserInputTextAreaGridPane = new GridPane();
//	SessionService s = new SessionService();
//	private HBox titleBox = new HBox();
//	private Label title = new Label();
//
//	private HBox leftTitleHBox = new HBox();
//	private Label leftLabel = new Label("Aitess Configuration");
//
//	private HBox rightTitleHBox = new HBox();
//	private Label rightLabel = new Label("UUT Configuration");
//
//	private HBox bottomTitleHBox = new HBox();
//	private HBox bottomButtonHBox = new HBox();
//	private HBox refreshButtonHBox = new HBox();
//	private Button bottomSubmitButton = new Button("Submit");
//	private Button bottomRefreshButton = new Button("Refresh");
//	private Label bottomLabel = new Label("User Input");
//
//	private HBox selectionHBoxUUTType = new HBox(10);
//	private HBox selectionHBoxUUTSN = new HBox(10);
//	private HBox selectionBoxSESSION = new HBox(10);
//	private HBox datePickerFromHBox = new HBox(10);
//	private HBox datePickerToHBox = new HBox(10);
//
//	private DatePicker fromDate = new DatePicker();
//	private DatePicker toDate = new DatePicker();
//
//	private ListView<String> aitessDataList = new ListView<>();
//	private TextArea uutTextArea = new TextArea();
//	private TextArea userInputTextArea = new TextArea();
//
//	public ComboBox<String> uutTypeField = new ComboBox<>();
//	public ComboBox<String> uutSerialNoField = new ComboBox<>();
//	public ComboBox<String> sessionField = new ComboBox<>();
//	private ComboBox<String> fromTimePicker = new ComboBox<>();
//	private ComboBox<String> toTimePicker = new ComboBox<>();
//
//	private ObservableList<UUTMasterDetailsDto> uutDataList;
//	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
//
//	private ObservableList<SessionMasterDTO> sessionNameDetailsList;
//	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
//	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
//	private ObservableList<String> fromDateList = FXCollections.observableArrayList();
//	private Map<String, String> sessionNameId = new HashMap<String, String>();
//	private Map<String, String> sessionDfccId = new HashMap<String, String>();
//	private Map<String, String> fromDateId = new HashMap<String, String>();
//	List<SessionDto> sessionList = new ArrayList<SessionDto>();
//	private SessionManagement sessionManagement = new SessionManagement();
//
//	String selectedUUTType;
//	String selectedSessionType;
//	String selectedDfccSN;
//	Date selectedFromDate; // Get the selected date as a LocalDate
////	Date selectedFromDateAsDate = Date.from(selectedFromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	Date selectedToDate;
//	
//	String uutLogData = null;
//	String sessionLogData = null;
//	String dfccSNLogData = null;
//	String fromDateLogData = null;
//	String toDateLogData = null;
//	
//	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
//
//	public LogBookController() {
//		SessionResponse s1 = s.getAllSession();
//		sessionList = s1.getListOfSession();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		
//		for (SessionDto session : sessionList) {
//			sessionNameId.put(session.getSessionName(), session.getSessionId());
//			sessionDfccId.put(session.getSessionName(), session.getDfccSNo());
//			Date startDate = session.getStartDate();
//	        if (startDate != null) {
//	            String dateAsString = dateFormat.format(startDate);
//	            fromDateId.put(session.getSessionName(), dateAsString);
//	        } else {
//	            // Handle the case where startDate is null if needed
//	            fromDateId.put(session.getSessionName(), "No Date");
//	        }
//	    }
//		
//		populateAllData();
//	}
//
//	public GridPane createLogBookMainGridPane() {
//		initializeUUTTypeComboBox();
//		logBookMainGridPane.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LogBook.css").toExternalForm());
//		logBookMainGridPane.getStyleClass().add("log-book-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(14);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(53);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(26);
//
//		logBookMainGridPane.setPadding(new Insets(5));
//		logBookMainGridPane.setVgap(5);
//		logBookMainGridPane.setHgap(5);
//		logBookMainGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);
//
//		logBookMainGridPane.add(createHeadingBox(), 0, 0);
//		logBookMainGridPane.add(createLogBookSelectionGridPane(), 0, 1);
//		logBookMainGridPane.add(createLogBookMidGridPane(), 0, 2);
//		logBookMainGridPane.add(createLogBookBottomGridPane(), 0, 3);
//
//		return logBookMainGridPane;
//	}
//
//	private GridPane createHeadingBox() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		logBookHeadingGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookHeadingGridPane.getRowConstraints().addAll(firstRow);
//
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		title.setText("LOG BOOK");
//		title.getStyleClass().add("headerLabel");
//		titleBox.getChildren().add(title);
//
//		logBookHeadingGridPane.add(titleBox, 0, 0);
//
//		return logBookHeadingGridPane;
//	}
//
//	private GridPane createLogBookSelectionGridPane() {
//		logBookSelectionGridPane.getStyleClass().add("logbook-mid-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(20);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(20);
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(20);
//		ColumnConstraints fourthColumn = new ColumnConstraints();
//		fourthColumn.setPercentWidth(20);
//		ColumnConstraints fifthColumn = new ColumnConstraints();
//		fifthColumn.setPercentWidth(20);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(50);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//
//		logBookSelectionGridPane.setPadding(new Insets(5));
//
//		logBookSelectionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
//				fifthColumn);
//		logBookSelectionGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		logBookSelectionGridPane.add(createUUTypeComboBox(), 0, 0);
//		logBookSelectionGridPane.add(createUUTSerialNoComboBox(), 1, 0);
//		logBookSelectionGridPane.add(createSessionComboBox(), 2, 0);
//		logBookSelectionGridPane.add(createFromDatePickerComboBox(), 3, 0);
//		logBookSelectionGridPane.add(createToDatePickerComboBox(), 4, 0);
//		logBookSelectionGridPane.add(createFromTimePicker(), 3, 1);
//		logBookSelectionGridPane.add(createToTimePicker(), 4, 1);
//		logBookSelectionGridPane.add(createRefreshButton(), 2, 1);
//
//		return logBookSelectionGridPane;
//	}
//
//	private void populateAllData() {
//
//		ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//		List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(null, null, null, null, null,null);
//		ObservableList<String> fileData = FXCollections.observableArrayList();
//		for (ApplicationLogBookDto s : logBookEntries) {
//			uutLogData = s.getDetails();
//			fileData.add(uutLogData);
//		}
//
//		aitessDataList.setItems(fileData);
//
//	}
//
//	private HBox createFromTimePicker() {
//		fromTimePicker.setPromptText("FROM TIME");
//
//		// Populate time picker with combined hour and minute
//		fromTimePicker.setItems(FXCollections.observableArrayList(generateTimeOptions()));
//
//		HBox timePickerHBox = new HBox(5);
//		timePickerHBox.setAlignment(Pos.CENTER_LEFT);
//		timePickerHBox.setPadding(new Insets(0, 0, 0, 18.5));
//		timePickerHBox.getChildren().add(fromTimePicker);
//
//		return timePickerHBox;
//	}
//
//	private HBox createToTimePicker() {
//		toTimePicker.setPromptText("TO TIME");
//
//		// Populate time picker with combined hour and minute
//		toTimePicker.setItems(FXCollections.observableArrayList(generateTimeOptions()));
//
//		HBox timePickerHBox = new HBox(5);
//		timePickerHBox.setAlignment(Pos.CENTER_LEFT);
//		timePickerHBox.setPadding(new Insets(0, 0, 0, 18.5));
//		timePickerHBox.getChildren().add(toTimePicker);
//
//		return timePickerHBox;
//	}
//
//	private ObservableList<String> generateTimeOptions() {
//		ObservableList<String> timeOptions = FXCollections.observableArrayList();
//		for (int hour = 0; hour < 24; hour++) {
//			for (int minute = 0; minute < 60; minute += 15) { // Interval of 5 minutes
//				timeOptions.add(String.format("%02d:%02d", hour, minute));
//			}
//		}
//		return timeOptions;
//	}
//
//	// UUT TYPE FIELD
//	private void initializeUUTTypeComboBox() {
//
//		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
//
//		for (UUTMasterDetailsDto uut : uutDataList) {
//			uutTypeList.add(uut.getUutType());
//		}
//
//		uutTypeField.setItems(uutTypeList);
//
//		uutTypeField.setOnAction((event) -> {
//
//			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
//			String uutId = fetchUutId(selectedUUTType);
//			System.out.println("UUT TYPE: " + selectedUUTType + ", UUT ID: " + uutId);
//			initializeSessionComboBox(uutId);
//			initializeDfccSNComboBox(uutId);
////			initializeFromDate(uutId);
//		});
//
//	}
//
//	private void createUUTBasedList(String uutType) {
//
//		String selectedUUTType = fetchUutId(uutType);
//		if (selectedUUTType != null) {
//
//			ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//			List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(selectedUUTType, null, null,
//					null, null,null);
//			ObservableList<String> fileData = FXCollections.observableArrayList();
//			for (ApplicationLogBookDto s : logBookEntries) {
//				uutLogData = s.getDetails();
//				fileData.add(uutLogData);
//			}
//			aitessDataList.getItems().clear();
//			aitessDataList.setItems(fileData);
//
//		} else {
//			System.out.println("No UUT found for the given type.");
//		}
//
//	}
//
//
//	private String fetchUutId(String uutType) {
//		for (UUTMasterDetailsDto uut : uutDataList) {
//			if (uut.getUutType().equals(uutType)) {
//				return uut.getUutId();
//			}
//		}
//		return null;
//	}
//
//	private HBox createUUTypeComboBox() {
//		uutTypeField.setPromptText("UUT TYPE");
//		selectionHBoxUUTType.setPadding(new Insets(0, 0, 0, 18.5));
//		selectionHBoxUUTType.setAlignment(Pos.CENTER_LEFT);
//		selectionHBoxUUTType.getChildren().add(uutTypeField);
//
//		return selectionHBoxUUTType;
//	}
//
//	// UUT SERIAL NUMBER FIELD
//	private HBox createUUTSerialNoComboBox() {
//		uutSerialNoField.setPromptText("UUT S/N");
//		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
//		selectionHBoxUUTSN.setAlignment(Pos.CENTER_LEFT);
//		selectionHBoxUUTSN.getChildren().add(uutSerialNoField);
//
//		return selectionHBoxUUTSN;
//	}
//
//	// SESSION FIELD
//	private HBox createSessionComboBox() {
//		sessionField.setPromptText("Session");
//		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
//		selectionBoxSESSION.setAlignment(Pos.CENTER_LEFT);
//		selectionBoxSESSION.getChildren().add(sessionField);
//
//		return selectionBoxSESSION;
//	}
//
////    // UUT SESSION NAME TYPE FIELD
//	private void initializeSessionComboBox(String uutTypeId) {
//		sessionTypeList.clear();
//
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
//				.collect(Collectors.toList());
//		for (SessionDto sessionName : filterSessionList) {
//			sessionTypeList.add(sessionName.getSessionName());
//			System.out.println("Session Name:" + sessionName.getSessionName());
//
//		}
//
//		sessionField.setItems(sessionTypeList);
//	}
//
//	private void createSessionNameBasedList(String sessionName) {
//		selectedSessionType = sessionNameId.get(sessionName);
//
//		if (selectedSessionType != null) {
//			System.out.println("UUT TYPEsession: " + fetchUutId(selectedUUTType));
//			System.out.println("Selected Session Type.......: " + selectedSessionType);
//
//			ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//			List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(fetchUutId(selectedUUTType),
//					null, selectedSessionType, null, null,null);
//
//			System.out.println("LogBook Entries Size: " + logBookEntries.size());
//
//			if (logBookEntries != null && !logBookEntries.isEmpty()) {
//				ObservableList<String> fileData = FXCollections.observableArrayList();
//				for (ApplicationLogBookDto s : logBookEntries) {
//					sessionLogData = s.getDetails();
//					System.out.println("Session Log Data " + fileData.add(sessionLogData));
//				}
//				aitessDataList.getItems().clear();
//				aitessDataList.setItems(fileData);
//			} else {
//				System.out.println("No log entries found for the selected criteria DFCC SN.");
//			}
//		} else {
//			System.out.println("No Session Type found for the given DFCCSN.");
//		}
//	}
////  // UUT SESSION DFCC S/N FIELD
//	private void initializeDfccSNComboBox(String uutTypeId) {
//		dfccSNList.clear();
//
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
//				.collect(Collectors.toList());
//		
//		for (SessionDto dfccSn : filterSessionList) {
//			dfccSNList.add(dfccSn.getDfccSNo());
//			System.out.println("Dfcc SN:" + dfccSn.getDfccSNo());
//
//		}
//
//		uutSerialNoField.setItems(dfccSNList);
//	}
//
//
//	private void createDfccSNBasedList(String dfccSn) {
//		
//
//		if (selectedDfccSN != null) {
//			System.out.println("UUT TYPEsession: " + fetchUutId(selectedUUTType));
//			System.out.println("DFCCC SSSNNNe.......: " + selectedDfccSN);
//
//			ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//			List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(fetchUutId(selectedUUTType),
//					selectedDfccSN, selectedSessionType, null, null,null);
//
//			System.out.println("LogBook Entries Size: " + logBookEntries.size());
//
//			if (logBookEntries != null && !logBookEntries.isEmpty()) {
//				ObservableList<String> fileData = FXCollections.observableArrayList();
//				for (ApplicationLogBookDto s : logBookEntries) {
//					dfccSNLogData = s.getDetails();
//					System.out.println("DFCC Log Data " + fileData.add(dfccSNLogData));
//				}
//				aitessDataList.getItems().clear();
//				aitessDataList.setItems(fileData);
//			} else {
//				System.out.println("No log entries found for the selected criteria.");
//			}
//		} else {
//			System.out.println("No Session Type found for the given session name.");
//		}
//	}
//	
//	
//
//	private void createFromDateBasedList(Date selectedFromDate) {
//		if (selectedFromDate != null) {
//		
//	    ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//	    List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(
//	            fetchUutId(selectedUUTType),
//	            selectedDfccSN,
//	            selectedSessionType,
//	            selectedFromDate, // Pass the Date object
//	            null,
//	            null
//	    );
//	    
//	    if (logBookEntries != null && !logBookEntries.isEmpty()) {
//	        ObservableList<String> fileData = FXCollections.observableArrayList();
//	        for (ApplicationLogBookDto s : logBookEntries) {
//	        	fromDateLogData = s.getDetails(); 
//	            fileData.add(fromDateLogData);
//	            System.out.println("DFCC Log Data: based on date " + s.getTimestamp());
//	        }
//	        aitessDataList.setItems(fileData);
//	    }} else {
//	        System.out.println("No log entries found for the selected criteria.");
//	    }
//	}
//
//	
//	private void createToDateBasedList(Date selectedFromDate) {
//		if (selectedToDate != null) {
//		
//	    ApplicationLogbookManagement app = new ApplicationLogbookManagement();
//
//	    List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByDate(
//	            fetchUutId(selectedUUTType),
//	            selectedDfccSN,
//	            selectedSessionType,
//	            selectedFromDate, // Pass the Date object
//	            selectedToDate,
//	            null
//	    );
//	    
//	    if (logBookEntries != null && !logBookEntries.isEmpty()) {
//	        ObservableList<String> fileData = FXCollections.observableArrayList();
//	        for (ApplicationLogBookDto s : logBookEntries) {
//	        	toDateLogData = s.getDetails(); 
//	            fileData.add(toDateLogData);
//	            System.out.println("DFCC Log Data: based on TOO DATE " + toDateLogData);
//	        }
//	        aitessDataList.setItems(fileData);
//	    }} else {
//	        System.out.println("No log entries found for the selected criteria.");
//	    }
//	}
//	
//	
//	// DATE PICKER - FROM
//	private HBox createFromDatePickerComboBox() {
//		fromDate.setPromptText("FROM DATE");
//		datePickerFromHBox.setPadding(new Insets(0, 0, 0, 18.5));
//		datePickerFromHBox.setAlignment(Pos.CENTER_LEFT);
//		datePickerFromHBox.getChildren().add(fromDate);
//
//		return datePickerFromHBox;
//	}
//
//	// DATE PICKER - TO
//	private HBox createToDatePickerComboBox() {
//		toDate.setPromptText("TO DATE");
//		datePickerToHBox.setPadding(new Insets(0, 0, 0, 18.5));
//		datePickerToHBox.setAlignment(Pos.CENTER_LEFT);
//		datePickerToHBox.getChildren().add(toDate);
//
//		return datePickerToHBox;
//	}
//
//	private HBox createRefreshButton() {
//		bottomRefreshButton.getStyleClass().add("logBook-container");
//		refreshButtonHBox.setAlignment(Pos.CENTER);
//		refreshButtonHBox.getChildren().add(bottomRefreshButton);
//
//		bottomRefreshButton.setOnAction(e -> {
//			selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
//			if (selectedUUTType != null) {
//				System.out.println("Inside Method UUT  " + selectedUUTType);
//				createUUTBasedList(selectedUUTType);
//
//			}
//
//			selectedSessionType = sessionField.getSelectionModel().getSelectedItem();
//			if (selectedSessionType != null) {
//				System.out.println("Inside Method " + selectedSessionType);
//				createSessionNameBasedList(selectedSessionType);
//			}
//	
//		
//	
//		selectedDfccSN = uutSerialNoField.getSelectionModel().getSelectedItem();
//		if (selectedDfccSN != null) {
//			System.out.println("Inside Method DFCC SN " + selectedDfccSN);
//			createDfccSNBasedList(selectedDfccSN);
//		}
//		
//		
//		LocalDate selectedFromDate = fromDate.getValue();
//		if (selectedFromDate != null) {
//			System.out.println("Inside Method Date Fromsfrg " + selectedFromDate);
//		    // Convert LocalDate to Date
//		    Date date = Date.from(selectedFromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		    
//		    // Pass the Date object to your method
//		    System.out.println("----------------");
//		    createFromDateBasedList(date);
//		}
//		});
//		return refreshButtonHBox;
//	}
//
//	private GridPane createLogBookMidGridPane() {
//		logBookMidGridPane.getStyleClass().add("logbook-mid-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		logBookMidGridPane.setPadding(new Insets(5));
//		logBookMidGridPane.setHgap(2);
////        logBookMidGridPane.setVgap(5);
//
//		logBookMidGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookMidGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		logBookMidGridPane.add(createLeftSideTitle(), 0, 0, 1, 2);
//		logBookMidGridPane.add(createAitessTextArea(), 0, 1);
//
//		logBookMidGridPane.add(createRightSideTitle(), 1, 0, 2, 2);
//		logBookMidGridPane.add(createUutTextArea(), 1, 1);
//
//		return logBookMidGridPane;
//	}
//
//	private HBox createLeftSideTitle() {
//		leftTitleHBox.getStyleClass().add("logBook-container");
//		leftLabel.getStyleClass().add("title-label");
//		leftTitleHBox.setAlignment(Pos.TOP_LEFT);
//
//		leftTitleHBox.getChildren().add(leftLabel);
//		return leftTitleHBox;
//	}
//
//	private HBox createRightSideTitle() {
//		rightTitleHBox.getStyleClass().add("logBook-container");
//		rightLabel.getStyleClass().add("title-label");
//		rightTitleHBox.setAlignment(Pos.TOP_LEFT);
//		rightTitleHBox.getChildren().add(rightLabel);
//		return rightTitleHBox;
//	}
//
//	private GridPane createAitessTextArea() {
//		aitessDataList.getStyleClass().add("logbook-textarea");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		logBookAitessTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookAitessTextAreaGridPane.getRowConstraints().addAll(firstRow);
//
//		logBookAitessTextAreaGridPane.add(aitessDataList, 0, 0);
//
//		return logBookAitessTextAreaGridPane;
//	}
//
//	private GridPane createUutTextArea() {
//		uutTextArea.getStyleClass().add("logbook-textarea");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		logBookUUTTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookUUTTextAreaGridPane.getRowConstraints().addAll(firstRow);
//
//		logBookUUTTextAreaGridPane.add(uutTextArea, 0, 0);
//
//		return logBookUUTTextAreaGridPane;
//	}
//
//	private GridPane createLogBookBottomGridPane() {
//		logBookBottomGridPane.getStyleClass().add("logbook-mid-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(20);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(80);
//
//		logBookBottomGridPane.setPadding(new Insets(5));
//		logBookBottomGridPane.setHgap(5);
//		logBookBottomGridPane.setVgap(5);
//		logBookBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		logBookBottomGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		logBookBottomGridPane.add(createBottomTitle(), 0, 0);
//		logBookBottomGridPane.add(createUserInputTextArea(), 0, 1, 2, 1);
//		logBookBottomGridPane.add(createBottomButton(), 1, 0);
//
//		return logBookBottomGridPane;
//	}
//
//	private HBox createBottomTitle() {
//		bottomTitleHBox.getStyleClass().add("logBook-bottom-container");
//		bottomLabel.getStyleClass().add("title-label");
//		bottomTitleHBox.setAlignment(Pos.TOP_LEFT);
//		bottomTitleHBox.getChildren().add(bottomLabel);
//		return bottomTitleHBox;
//	}
//
//	private GridPane createUserInputTextArea() {
//		userInputTextArea.getStyleClass().add("logbook-textarea");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		logBookUserInputTextAreaGridPane.getColumnConstraints().addAll(firstColumn);
//		logBookUserInputTextAreaGridPane.getRowConstraints().addAll(firstRow);
//
//		logBookUserInputTextAreaGridPane.add(userInputTextArea, 0, 0);
//
//		return logBookUserInputTextAreaGridPane;
//	}
//
//	private HBox createBottomButton() {
//		bottomSubmitButton.getStyleClass().add("logBook-container");
//		bottomButtonHBox.setAlignment(Pos.CENTER_RIGHT);
//		bottomButtonHBox.getChildren().add(bottomSubmitButton);
//
//		bottomSubmitButton.setOnAction(e -> {
//			String userInput = userInputTextArea.getText();
//
//			UUTLogbookManagement uutLogBookManagement = new UUTLogbookManagement();
//			UUTLogBookDto uutDto = new UUTLogBookDto();
//			uutDto.setSessionId(currentSessionDetails.getSessionId());
//			uutDto.setUsername(StateMachine.getCurrentUserLogin());
//			uutDto.setUutSerialNumber(currentSessionDetails.getDfccSerialNumber());
//			uutDto.setUutId(currentSessionDetails.getUutId());
//			uutDto.setDetails(userInput);
//
//			uutLogBookManagement.addUUTLogBook(uutDto);
//
//			UUTLogBookResponse response = uutLogBookManagement.addUUTLogBook(uutDto);
//			if (response.getResponseCode() == 1) {
//				Notifications.showSuccessAlert(response.getResponseMessage());
//			} else if (response.getResponseCode() == 0) {
//				Notifications.showErrorAlert(response.getResponseMessage());
//			}
//			userInputTextArea.clear();
//		});
//
//		return bottomButtonHBox;
//	}
//}
