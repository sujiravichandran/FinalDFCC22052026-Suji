package com.teclever.dfcc.Controller.ui;

import javafx.event.EventTarget;

import java.util.List;

import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.datastore.response.UUTLogBookResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
//import com.jfoenix.controls.JFXTimePicker;
import javafx.stage.Stage;

public class LogBookController {
	private GridPane logBookMainGridPane = new GridPane();
	private GridPane logBookHeadingGridPane = new GridPane();
	private GridPane logBookSelectionGridPane = new GridPane();
	private GridPane logBookMidGridPane = new GridPane();
	private GridPane logBookBottomGridPane = new GridPane();
	private GridPane logBookAitessTextAreaGridPane = new GridPane();
	private GridPane logBookUUTTextAreaGridPane = new GridPane();
	private GridPane logBookUserInputTextAreaGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();

	private HBox leftTitleHBox = new HBox();
	private Label leftLabel = new Label("Aitess Configuration");

	private HBox rightTitleHBox = new HBox();
	private Label rightLabel = new Label("UUT Configuration");

	private HBox bottomTitleHBox = new HBox();
	private HBox bottomButtonHBox = new HBox();
	private Button bottomSubmitButton = new Button("Submit");
	private Label bottomLabel = new Label("User Input");

	private HBox selectionHBoxUUTType = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);
	private HBox selectionBoxSESSION = new HBox(10);
	private HBox datePickerFromHBox = new HBox(10);
	private HBox datePickerToHBox = new HBox(10);


	private DatePicker fromDate = new DatePicker();
	private DatePicker toDate = new DatePicker();


	private TextArea aitessTextArea = new TextArea();
	private TextArea uutTextArea = new TextArea();
	private TextArea userInputTextArea = new TextArea();

	public ComboBox<String> uutTypeField = new ComboBox<>();
	public ComboBox<String> uutSerialNoField = new ComboBox<>();
	public ComboBox<String> sessionField = new ComboBox<>();
	private ComboBox<String> fromTimePicker = new ComboBox<>();
    private ComboBox<String> toTimePicker = new ComboBox<>();

	private ObservableList<SessionDTO> sessionNameDataList;
	public static String sesssionNameValue;
	public static String sessionId;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	 String selectedUUTType;

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

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
		logBookSelectionGridPane.getRowConstraints().addAll(firstRow,secondRow);

		logBookSelectionGridPane.add(createUUTypeComboBox(), 0, 0);
		logBookSelectionGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		logBookSelectionGridPane.add(createSessionComboBox(), 2, 0);
		logBookSelectionGridPane.add(createFromDatePickerComboBox(), 3, 0);
		logBookSelectionGridPane.add(createToDatePickerComboBox(), 4, 0);
		logBookSelectionGridPane.add(createFromTimePicker(), 3, 1);
	    logBookSelectionGridPane.add(createToTimePicker(), 4, 1);

		return logBookSelectionGridPane;
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
            for (int minute = 0; minute < 60; minute += 15) { // Interval of 5 minutes
                timeOptions.add(String.format("%02d:%02d", hour, minute));
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
	        
	        createUUTBasedList(selectedUUTType);
	        
	    });
	}

	private void createUUTBasedList(String uutType) {
	    String selectedUUTType = fetchUutId(uutType);

	    if (selectedUUTType != null) {
	    	
	    	ApplicationLogbookManagement app = new ApplicationLogbookManagement();
		    List<ApplicationLogBookDto> logBookEntries = app.getApplicationLogBooksByUUTId(selectedUUTType);
		    for(ApplicationLogBookDto s: logBookEntries)
		    {
		    	System.out.println("-----   "+s.getDetails());
		    }
		    System.out.println("LogBoook" + app.getApplicationLogBooksByUUTId(selectedUUTType) );
		    
		    List<String> logBookTexts = logBookEntries.stream()
		                                              .map(ApplicationLogBookDto::toString) // Assuming toString() is overridden or use a custom method
		                                              .toList();
		    
		    System.out.println();
		    
		    String logBookText = String.join("\n", logBookTexts);
		    
		    aitessTextArea.setText(logBookText);
	    	
	        System.out.println("Selected UUT ID: " + selectedUUTType);
	    } else {
	        System.out.println("No UUT found for the given type.");
	    }
	}
//	
//	  private String fetchTestTypeId(String testTypeName) {
//	        for (TestTypeMasterDetailsDto testType : testTypeDataList) {
//	            if (testType.getTestName().equals(testTypeName)) {
//	                return testType.getTestTypeId();
//	            }
//	        }
//	        return null;
//	    }
	
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

//    // UUT S/N TYPE FIELD
//    private void initializeSessionComboBox() {
//        sessionNameDataList = FXCollections.observableArrayList(configManager.getAllUUT());
//        for (UUTMasterDetailsDto uut : uutDataList) {
//            uutTypeList.add(uut.getUutType());
//        }
//        uutTypeField.setItems(uutTypeList);
//        uutTypeField.setOnAction((event) -> {
//            UUT_ID = fetchUutId(uutTypeField.getValue());
//        });
//    }

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
	
	private void generateDataAitessTextArea(String selectedUUTType) {
		
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
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(20);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(80);

		logBookBottomGridPane.setPadding(new Insets(5));
		logBookBottomGridPane.setHgap(5);
		logBookBottomGridPane.setVgap(5);
		logBookBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		logBookBottomGridPane.getRowConstraints().addAll(firstRow, secondRow);

		logBookBottomGridPane.add(createBottomTitle(), 0, 0);
		logBookBottomGridPane.add(createUserInputTextArea(), 0, 1, 2, 1);
		logBookBottomGridPane.add(createBottomButton(), 1,0);


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

	private HBox createBottomButton() {
	    bottomSubmitButton.getStyleClass().add("logBook-container");
	    bottomButtonHBox.setAlignment(Pos.CENTER_RIGHT);
	    bottomButtonHBox.getChildren().add(bottomSubmitButton);
	    
	    bottomSubmitButton.setOnAction(e -> {
	        String userInput = userInputTextArea.getText();
	        
	        UUTLogbookManagement uutLogBookManagement = new UUTLogbookManagement();
	        UUTLogBookDto uutDto = new UUTLogBookDto();
	        uutDto.setSessionId(currentSessionDetails.getSessionId());
	        uutDto.setUsername(StateMachine.getCurrentUserLogin());
	        uutDto.setUutSerialNumber(currentSessionDetails.getDfccSerialNumber());
	        uutDto.setUutId(currentSessionDetails.getUutId());
	        uutDto.setDetails(userInput);
	        
	        uutLogBookManagement.addUUTLogBook(uutDto);
	        
	        UUTLogBookResponse response = uutLogBookManagement.addUUTLogBook(uutDto);
	        		 if(response.getResponseCode() == 1) {
	               	  Notifications.showSuccessAlert(response.getResponseMessage());
	                 }else if(response.getResponseCode() == 0){
	               	  Notifications.showErrorAlert(response.getResponseMessage());
	                 }
	        		 userInputTextArea.clear();
	    });

	    return bottomButtonHBox;
	}
}
