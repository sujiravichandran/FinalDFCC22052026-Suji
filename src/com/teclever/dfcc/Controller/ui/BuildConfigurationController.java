package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationManagement;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationReport;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BuildConfigurationController {

	private GridPane dataAnalysisBuildConfigurationMainContainerGridPane = new GridPane();
	private GridPane buildConfigurationMidContainerGridPane = new GridPane();
	
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label(" BUILD CONFIGURATION");
	private SessionService s = new SessionService();
	private GridPane filterResultGridPane = new GridPane();
	private BuildConfigurationPopup buildConfigurationPopup1 = new BuildConfigurationPopup();
	private BuildConfigurationPopup2 buildConfigurationPopup2 = new BuildConfigurationPopup2();
	private BuildConfigurationPopup3 buildConfigurationPopup3 = new BuildConfigurationPopup3();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private Button submit = new Button("Submit");
	private HBox selectionBoxSESSION = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private Pane dynamicContent = new VBox();
	
	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();
	private Label versionNo = new Label("Version No: ");
	private ComboBox<String> versionNo2 = new ComboBox<>();

	private Label dateLabel = new Label("Date: ");
	private DatePicker datePicker = new DatePicker();
	
	private Button close = new Button("Close");
	private Button save = new Button("Save");
	private Button fetch = new Button("Fetch");
	private Button print = new Button("Print");
	
	private String date;
	private Stage parentStage;
	
	private GridPane topSecondGridPane = new GridPane();

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String session_ID;
	private String selectedUttId;
	private String selectedSession;
	private String selectedSerialNo;
	private String uutId;
	private String sessionId;
	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

//	private Label pageHeading = new Label("QUALITY MANAGEMENT / EW&A");
//	private Label secondTitle = new Label("BUILD CONFIGURATION OF DFCC");
//	private Label belPno = new Label("BEL PART NO: 1160 000 395 75");
//	private Label modType = new Label("MOD'A'");

	public BuildConfigurationController() {
		unitDataList.clear();
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();
//		getCurrentUnitResultData(currentSessionDetails.getUutId());
	}

	public GridPane createBuildConfigurationMainContainerGridPane() {

		initializeUUTTypeComboBox();
		print.setDisable(true);
		dataAnalysisBuildConfigurationMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisBuildConfigurationMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		dataAnalysisBuildConfigurationMainContainerGridPane.setHgap(5);
		dataAnalysisBuildConfigurationMainContainerGridPane.setVgap(5);

		dataAnalysisBuildConfigurationMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dataAnalysisBuildConfigurationMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
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
	        	if (uutTypeField.getSelectionModel().getSelectedItem() == null ||
	        		    slNoField.getSelectionModel().getSelectedItem() == null) {

	        		    Notifications.showErrorAlert("Please select UUT Type & Serial No");
	        		    return;
	        		}

	        	DFCCConstant.selectedFetchBuildConfig="";
	        	
	        	if(selectedSerialNo.equals(DFCCConstant.selectedBuildConfig)) {
	        		Notifications.showErrorAlert("Please change the selection and try again. You are in the same selction");
	        		return;
	        	}
	        	 
	        	
	        	
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

	            //CRITICAL LINE
	            VBox.setVgrow(popup, Priority.ALWAYS);
	            DFCCConstant.selectedBuildConfig=selectedSerialNo;
	        	
	        	
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
		buildConfigurationMidContainerGridPane.add(dynamicContent, 0, 2);
		


		return buildConfigurationMidContainerGridPane;

	}

	public GridPane topSecondGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);
		
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(20);


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

	private GridPane createFilterSelectionGridPane() {
		

		filterResultGridPane.getStyleClass().add("data-analysis-second-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);



		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		filterResultGridPane.setPadding(new Insets(5));

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

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initializeDfccSNComboBox(uutId);

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

	private void initializeDfccSNComboBox(String uutTypeId) {
		dfccSNList.clear();
		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
				.collect(Collectors.toList());

		Set<String> seenDfccSNos = new HashSet<>();

		for (SessionDto dfccSn : filterSessionList) {
			String dfccSNo = dfccSn.getDfccSNo();
			if (seenDfccSNos.add(dfccSNo)) {
				dfccSNList.add(dfccSNo);
			}
		}

	}

	// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER);
		selectionHBoxUUTSN.getChildren().add(slNoField);
		slNoField.setOnAction(event -> {
			 Platform.runLater(() -> {
		    versionNo2.setValue(null);
		    datePicker.setValue(null);
			submit.setDisable(false);
			 });
			selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
			selectedSession = "";
			if (selectedSerialNo != null) {
//				initializeSessionComboBox(selectedSerialNo);
				DFCCConstant.selectedSNoBuildConfig = selectedSerialNo;
				serialNo2.setText(DFCCConstant.selectedSNoBuildConfig);
				BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();
				List<String> versionList =buildConfigurationManagement.getVersionList(DFCCConstant.selectedSNoBuildConfig);
				versionNo2.setItems(FXCollections.observableArrayList(versionList));
			}

		});

		slNoField.setItems(dfccSNList);

		return selectionHBoxUUTSN;
	}

	// SESSION FIELD
	private HBox createSessionComboBox() {
		sessionNameField.setPromptText("Session");
		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
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
		versionNoHbox.setPadding(new Insets(20));
		versionNo.getStyleClass().add("title-label");
		versionNo2.getStyleClass().add("title-label");
		versionNo2.setOnAction(event -> {
			submit.setDisable(true);
			DFCCConstant.selectedVersionNoBuildConfig=versionNo2.getValue();
		});
		
		versionNoHbox.getChildren().addAll(versionNo, versionNo2);

		return versionNoHbox;
	}

	private HBox dateHbox() {
		HBox dateHbox = new HBox(10);
	    dateHbox.setAlignment(Pos.CENTER);
	    dateHbox.setPadding(new Insets(20));
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
	            if (string == null || string.trim().isEmpty()) return null;
	            return LocalDate.parse(string, formatter);
	        }
	    });

	    datePicker.setDayCellFactory(dp -> new DateCell() {
	        @Override
	        public void updateItem(LocalDate date, boolean empty) {
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
		saveButtonHbox.getChildren().addAll(save, fetch, print);
		

		
		print.setOnAction(e -> {

		    if (uutTypeField.getSelectionModel().getSelectedItem() == null ||
		        slNoField.getSelectionModel().getSelectedItem() == null ||
		        versionNo2.getSelectionModel().getSelectedItem() == null ||
		        datePicker.getValue() == null) {

		        Notifications.showErrorAlert("Please select UUT Type, Serial No, Version No, Date");
		        return;
		    }

		    try {
		        BuildConfigurationReport buildConfigurationReport = new BuildConfigurationReport();
		        buildConfigurationReport.generateBuildConfigurationReport(
		            serialNo2.getText(),
		            datePicker.getEditor().getText(),
		            versionNo2.getEditor().getText(),
		            DFCCConstant.selectedUutBuildConfig
		        );

		        System.out.println("Check file path" +buildConfigurationReport.getLastGeneratedFilePath() );
		        printPDF(buildConfigurationReport.getLastGeneratedFilePath());

		    } catch (Exception ex) {
		        ex.printStackTrace();
		        Notifications.showErrorAlert("Printing failed: " + ex.getMessage());
		    }
		});



		
		
		fetch.setOnAction(e -> {
			
			DFCCConstant.selectedFetchBuildConfig="Fetch";
			boolean ok =
					serialNo2.getText() != null &&
    
    			   versionNo2.getValue() != null;
    		
    		if(!ok) {
    			Notifications.showErrorAlert("Please select serail No & Version No");
        		return;
    		}

        	 
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

            //CRITICAL LINE
            VBox.setVgrow(popup, Priority.ALWAYS);
            DFCCConstant.selectedBuildConfig=selectedSerialNo;
		});
		
		
		save.setOnAction(e -> {
			if(versionNo2.getEditor().getText().trim().isEmpty()){
				Notifications.showErrorAlert("Please enter version");
				return;
			}
			
			print.setDisable(false);
			
			if (DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT1")) {
			buildConfigurationPopup1.saveButton(serialNo2.getText(), versionNo2.getEditor().getText());
			}else if(DFCCConstant.selectedUutBuildConfig.equalsIgnoreCase("UUT2")) {
				buildConfigurationPopup2.saveButton(serialNo2.getText(), versionNo2.getEditor().getText());
			}else {
				buildConfigurationPopup3.saveButton(serialNo2.getText(), versionNo2.getEditor().getText());
			}
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		    LocalDate selectedDate = datePicker.getValue();

		    if (selectedDate != null) {
		        date = selectedDate.format(formatter);
		    } else {
		       Notifications.showErrorAlert("Please select Date");
		       return;
		    }

			Notifications.showSuccessAlert("Build Configuration saved successfully");
			
		});
		return saveButtonHbox;
	}
	
	public static void printPDF(String pdfPath) {
	    try {
	        ProcessBuilder pb = new ProcessBuilder(
	        		 "cmd.exe", "/c",
	                 "start", "msedge", "/print", "\"" + pdfPath + "\""
	            
	            
	        );
	        pb.start();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        Notifications.showErrorAlert("Printing failed: " + ex.getMessage());
	    }
	}

	
	private HBox closeButtonHbox() {
	    HBox closeButtonHbox = new HBox();
	    closeButtonHbox.setAlignment(Pos.CENTER_LEFT);
	    closeButtonHbox.getChildren().add(close);
	    closeButtonHbox.setPadding(new Insets(10));
		close.setOnAction(e -> {

	        Stage popupStage = (Stage) close.getScene().getWindow();
	        popupStage.close();

	        if (parentStage != null) {
	            parentStage.close();
	        }
		});

		return closeButtonHbox;
	}


}
