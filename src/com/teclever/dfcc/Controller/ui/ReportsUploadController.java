package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.model.Upload;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

class UploadTableViewFactory implements TableViewFactory<Upload> {

	@Override
	public CustomTableView<Upload> createTableView(ObservableList<Upload> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Upload.class, addUserColumn, addCheckboxColumn);

	}
}

public class ReportsUploadController {

	private GridPane reportsUploadMainGridPane = new GridPane();
	private GridPane containerHeadingGridPane = new GridPane();
	private GridPane reportComboBoxGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();
	HBox bottomHbox = new HBox(30);

	private String uploadDateAndTime;
	private String fileName;
	private String REPORT_TYPE = "UPLOAD";

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private ObservableList<SessionList> sessionDataList;
	private ObservableList<String> sessionNameList = FXCollections.observableArrayList();
	private String SESSION_ID ;

	private ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private SessionManagement sessionManagement = new SessionManagement();

	public ReportsUploadController() {
		setupDisplayTable();
	}

	public void refresh() {
		setupDisplayTable();
	}

	public GridPane createUploadReportGridPane(String reportType) {
		initializeUUTTypeComboBox();

		REPORT_TYPE = "UPLOAD";
		

		reportsUploadMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Upload.css").toExternalForm());
		reportsUploadMainGridPane.getStyleClass().add("upload-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		reportsUploadMainGridPane.setPadding(new Insets(5));
		reportsUploadMainGridPane.setVgap(5);
		reportsUploadMainGridPane.setHgap(5);
		reportsUploadMainGridPane.getColumnConstraints().addAll(firstColumn);
		reportsUploadMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		reportsUploadMainGridPane.add(createHeadingBox(), 0, 0);
		reportsUploadMainGridPane.add(createComboBoxGridPane(), 0, 1);
		reportsUploadMainGridPane.add(uploadBottomContainer(), 0, 2);
//		configurationMainGridPane.add(createConfigurationTabsGridPane(), 0,1);

		return reportsUploadMainGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		containerHeadingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		containerHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);

		title.setText("UPLOAD");

		title.getStyleClass().add("headerLabel");
		titleBox.getChildren().addAll(title);

		containerHeadingGridPane.add(titleBox, 0, 0);
		containerHeadingGridPane.add(createButtonBox(), 1, 0);

		return containerHeadingGridPane;
	}

	private GridPane createComboBoxGridPane() {
		reportComboBoxGridPane.getStyleClass().add("report-top-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportComboBoxGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		reportComboBoxGridPane.getRowConstraints().addAll(firstRow);

		reportComboBoxGridPane.add(createUutBox(), 0, 0);
		reportComboBoxGridPane.add(createSessionNameBox(), 1, 0);

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

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
			UUT_ID = fetchUutId(uutTypeField.getValue());
			if (UUT_ID != null) {
				initializeSessionNameComboBox(UUT_ID);
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
		if (response.getResponse().getResponseCode() == 1) {
			sessionDataList = FXCollections.observableArrayList(response.getListOfSession());
			for (SessionList session : sessionDataList) {
				sessionNameList.add(session.getSessionName());
			}
			sessionNameField.setItems(sessionNameList);
			sessionNameField.setOnAction((event) -> {
				SESSION_ID = fetchSessionId(sessionNameField.getValue());
				if (SESSION_ID != null) {
					setupDisplayTable();
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

	private HBox createButtonBox() {
		HBox buttonBox = new HBox();
		Button uploadButton = new Button("Upload");
		uploadButton.setOnAction(e -> uploadFileselection());

		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(uploadButton);
		return buttonBox;
	}

	private void uploadFileselection() {
		
		UUT_ID = uutTypeField.getSelectionModel().getSelectedItem();
		SESSION_ID = fetchSessionId(sessionNameField.getValue());

		if (uutTypeField.getSelectionModel().getSelectedItem() == null) {
			Notifications.showErrorAlert("Please select UUT.");
			return;
		}

		if (sessionNameField.getSelectionModel().getSelectedItem() == null) {
			Notifications.showErrorAlert("Please select Session Name.");
			return;
		}

		SessionFileManagement sessionFileName = new SessionFileManagement();

		ReportCofigurationManagement reportConfig = new ReportCofigurationManagement();

		ReportConfigResponse response = reportCofigurationManagement.getAllReportConfig(SESSION_ID, REPORT_TYPE);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(reportsUploadMainGridPane.getScene().getWindow());

		if (selectedFiles != null) {
			List<ReportConfigDto> uploadFile = new ArrayList<ReportConfigDto>();
			List<String> filesList = new ArrayList<String>();
			for (File file : selectedFiles) {
				fileName = file.getAbsolutePath();
				ReportConfigDto upload = new ReportConfigDto();
				upload.setFileName(fileName);
				filesList.add(fileName);
				upload.setFileNameWitFullPath(filesList);
				upload.setReportType("UPLOAD");
				upload.setSessionId(SESSION_ID);
				uploadFile.add(upload);
				sessionFileName.copyToUpload(uploadFile, SESSION_ID);
				reportConfig.addReportConfig(upload);
				setupDisplayTable();
			}
			
		}

	}

	private void setupDisplayTable() {
		String css = this.getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm();
		this.bottomHbox.getStylesheets().add(css);

		ReportCofigurationManagement reportConfig = new ReportCofigurationManagement();
		ReportConfigResponse response = reportConfig.getAllReportConfig(SESSION_ID, REPORT_TYPE);
		List<ReportConfigDto> uploadFile = response.getListOfReportConfigDto();

		ObservableList<Upload> uploads = FXCollections.observableArrayList();

		for (ReportConfigDto reportconfig : uploadFile) {
			Upload uploadData = new Upload();
			 String fullPath = reportconfig.getFileName();
			 uploadData.setFullPath(fullPath);
			    String fileName = Paths.get(fullPath).getFileName().toString();
			    System.out.println("FileName in Data upload" + fullPath);
			uploadData.setFileName(fileName);
			uploadData.setUploadDateAndTime(reportconfig.getUploadDate());
			uploadData.setId(reportconfig.getReportConfigId());
			uploads.add(uploadData);
		}

		UploadTableViewFactory driverFactory = new UploadTableViewFactory();
		CustomTableView customTableView = driverFactory.createTableView(uploads, true, false);

		customTableView.setPrefWidth(1613.0);
		customTableView.setPrefHeight(1000.0);
		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<Upload> selectedItems = customTableView.getSelectedItems();
			for (Upload upload : selectedItems) {
				handleDeleteButtonClicked(upload);
			}
		});
		
		customTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
		    ObservableList<Upload> selectedItems = customTableView.getSelectedItems();
		    if (selectedItems == null || selectedItems.isEmpty()) {
		        Notifications.showWarningAlert("No file selected!");
		        return;
		    }

		    for (Upload upload : selectedItems) {
		        try {
		            String filePath = upload.getFullPath();
		            if (filePath == null || filePath.isEmpty()) {
		                Notifications.showErrorAlert("File path not found for " + upload.getFileName());
		                continue;
		            }

		            File file = new File(filePath);

		            if (file.exists()) {
		                Desktop.getDesktop().open(file);
		            } else {
		                Notifications.showErrorAlert("File not found: " + filePath);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		            Notifications.showErrorAlert("Failed to open file: " + e.getMessage());
		        }
		    }
		});


		this.bottomHbox.getChildren().clear();
		this.bottomHbox.getChildren().add(customTableView);
	}

	private HBox uploadBottomContainer() {
		bottomHbox.getStyleClass().add("aitessMaster-Container");

		return bottomHbox;
	}

	
	private void handleDeleteButtonClicked(Upload uplodDto) {
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to delete Aitess Run Configuration: " + uplodDto.getId() + "?";

		Notifications.showConfirmationDialog(title, contentText, () -> handleDeleteReportData(uplodDto.getId()));
	}

	private void handleDeleteReportData(String ReportConfigId) {
		
		ReportCofigurationManagement reportConfig = new ReportCofigurationManagement();
		ReportConfigResponse response = reportConfig.getAllReportConfig(SESSION_ID, REPORT_TYPE);
		List<ReportConfigDto> uploadFile = response.getListOfReportConfigDto();
		Response responses = reportCofigurationManagement.deleteFileName(ReportConfigId);

		for (ReportConfigDto reportconfig : uploadFile) {
			ReportConfigId= reportconfig.getReportConfigId();
		}
		
		if (responses.getResponseCode() == 1) {
			setupDisplayTable();
		} else if (responses.getResponseCode() == 0) {
			Notifications.showErrorAlert(responses.getResponseMessage());
		}
		setupDisplayTable();
	}

}
