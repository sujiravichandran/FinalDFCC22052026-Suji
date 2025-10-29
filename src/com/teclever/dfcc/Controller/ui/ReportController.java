package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
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
import com.teclever.dfcc.reportgeneration.ReportGenerationNew;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

    private ComboBox<String> sessionNameField = new ComboBox<String>();
    private ObservableList<SessionList> sessionDataList;
	private ObservableList<String> sessionNameList = FXCollections.observableArrayList();
	private List<String> sessionNameList1;
	private String SESSION_ID;
	
    private Button addOtherFiles = new Button("Add Other Files");
    private Button addRemarks = new Button("Add Remarks");

	private ListView<ReportConfigDto> listView = new ListView<>();;
	private String REPORT_TYPE = null;
	private ObservableList<ReportConfigDto> reportData = FXCollections.observableArrayList();

	private ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private SessionManagement sessionManagement = new SessionManagement();
	private ReportTreeviewController reportTreeviewController = new ReportTreeviewController(this);

	public GridPane createReportGridPane(String reportType) {
		if ("PQT REPORT".equals(reportType)) {
			REPORT_TYPE = "PQT";
		} else if ("ESS REPORT".equals(reportType)) {
			REPORT_TYPE = "ESS";
		} else {
			REPORT_TYPE = "DataPack";
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
		reportMainGridPane.add(createBottomGridPane(), 0, 2);

		initializeUUTTypeComboBox();
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
        
        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(100);
        
        reportComboBoxGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
        reportComboBoxGridPane.getRowConstraints().addAll(firstRow);
        
        reportComboBoxGridPane.add(createUutBox(), 0, 0);
        reportComboBoxGridPane.add(createSessionNameBox(), 1, 0);
        reportComboBoxGridPane.add(createAddFilesBox(), 2, 0);
        if(REPORT_TYPE.equals("ESS")) {        	
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
	
	private HBox createSessionNameBox() {
		sessionNameField.setPromptText("SESSION NAME");

        HBox sessionNameHBox = new HBox(10);
        sessionNameHBox.setAlignment(Pos.CENTER);
        sessionNameHBox.getChildren().add(sessionNameField);

        return sessionNameHBox;
	}
	
	private HBox createAddFilesBox() {		
		HBox addFilesHBox = new HBox(10);
		addFilesHBox.setAlignment(Pos.CENTER);
		addFilesHBox.getChildren().add(addOtherFiles);
		
		addOtherFiles.setOnAction(e ->{
			if(UUT_ID != null && SESSION_ID != null) {				
				updateListViewWithSelectedLabel(null);
			}else {
				Notifications.showWarningAlert("Please select UUT type and session name...");
			}
		});
		
		return addFilesHBox;
	}
	

	private HBox createAddRemarkBox() {
		HBox addRemarksHBox = new HBox(10);
		addRemarksHBox.setAlignment(Pos.CENTER);
		addRemarksHBox.getChildren().add(addRemarks);
		
		addRemarks.setOnAction(e ->{
			if(UUT_ID != null && SESSION_ID != null) {	
				handleRemarkPopup();
			}else {
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
			UUT_ID = fetchUutId(uutTypeField.getValue());
			if(UUT_ID != null) {	
				reportTreeviewController.initializeReportTreeView(null);
				initializeSessionNameComboBox(UUT_ID);
				reportData.clear();
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

	private void initializeSessionNameComboBox(String uutId) {
		sessionNameField.getItems().clear();
		SESSION_ID = null;
		SessionListResponse response = sessionManagement.getSessionDataByUUTId(uutId);
		if(response.getResponse().getResponseCode() == 1) {			
			sessionDataList = FXCollections.observableArrayList(response.getListOfSession());
			for (SessionList session : sessionDataList) {
				sessionNameList.add(session.getSessionName());		
			}
//			Suji Changed for PQT & ESS report Session Name filering Bug::
			if(REPORT_TYPE.equals("PQT")) {
				
			sessionNameList1 = sessionNameList.stream().filter(s -> s.contains("PQT")).toList();
			}else if(REPORT_TYPE.equals("ESS")) {
				sessionNameList1 = sessionNameList.stream().filter(s -> s.contains("FRU") || s.contains("Production")).toList();
			}
//			End::
			sessionNameField.setItems(FXCollections.observableArrayList(sessionNameList1));
			sessionNameField.setOnAction((event) -> {
				SESSION_ID = fetchSessionId(sessionNameField.getValue());
				if(SESSION_ID != null){	
					reportTreeviewController.initializeReportTreeView(SESSION_ID);
					getSavedReportData();
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
		if(!REPORT_TYPE.equals("DataPack")) {			
			reportHeadingGridPane.add(createDownloadButton(), 1, 0);
		}

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
	    
	    downloadButton.setOnAction(e -> {
	        if (UUT_ID != null && SESSION_ID != null) {    
	        	Task<Response> task = new Task<Response>() {
	                @Override
	                protected Response call() throws Exception {
	                    ReportGenerationNew reportGenerationNew = new ReportGenerationNew();
	                    Response response = null;
	                    
	                    if (REPORT_TYPE.equals("PQT")) {
	    					response = reportGenerationNew.generatePQTReport(SESSION_ID);
	    				} else if (REPORT_TYPE.equals("ESS")) {
	    					response = reportGenerationNew.generateEssReport(SESSION_ID);
	    				} 
	                    return response;
	                }
	            };

	            task.setOnSucceeded(event -> {
	                Response response = task.getValue();
	                if (response != null) {
	                    if (response.getResponseCode() == 1) {
	                        Notifications.showSuccessAlert(response.getResponseMessage());
	                        Platform.runLater(() -> {
	                        	viewReport.viewReportPopup(response);			
								});
	                    } else {
	                        Notifications.showErrorAlert(response.getResponseMessage());
	                    }
	                } else {
	                    Notifications.showErrorAlert("Failed to generate the report.");
	                }
	            });

	            task.setOnFailed(event -> {
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

					Notifications.showConfirmationDialog(title, contentText, () -> handleDeleteReportData(hbox.getId()));
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

					if(item.getLevelOneName() != null ) {
						hierarchy.append(item.getLevelOneName() +" -> ");
					}
					if(item.getLevelTwoName() != null) {
						hierarchy.append(item.getLevelTwoName() +" -> ");
					}
					if(item.getLevelThreeName() != null) {
						hierarchy.append(item.getLevelThreeName() +" -> ");
					}
					if(item.getLevelFourName() != null) {
						hierarchy.append(item.getLevelFourName() +" -> ");
					}
					if(item.getLevelFiveName() != null) {
						hierarchy.append(item.getLevelFiveName() +" -> ");
					}
								        
					if(item.getFileName() != null) {
						hierarchy.append(item.getFileName());
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
		
		if(response.getResponseCode() == 1) {
			getSavedReportData();
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	public void updateListViewWithSelectedLabel(String[] idComponents) {
		ReportConfigDto newReportConfig = new ReportConfigDto();
		if(idComponents != null) {
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
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg","*.pdf"));

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
		if(selectedFilePath.size() > 0) {			
			handleSaveReportdData(newReportConfig);
			
			if(REPORT_TYPE == "DataPack") {
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
			FXMLLoader addRemarkPopup = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AddRemark.fxml"));
			Parent root = addRemarkPopup.load();

			AddRemarkController addRemarkController  = addRemarkPopup.getController();
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

