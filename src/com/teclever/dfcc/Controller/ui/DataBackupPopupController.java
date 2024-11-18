package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DataBackupPopupController {

	@FXML
	private AnchorPane dataBackupMainContainer;
	@FXML
	private VBox dataBackupVBox;
	@FXML
	private HBox dataBackupHeading;
	@FXML
	private Label headerLabel;
	@FXML
	private GridPane dataBackupGridPane;
	@FXML
	private HBox buttonHBox;
	
	private Label selectSessionLabel = new Label("Select Session");
	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private ObservableList<SessionList> sessionDataList;
	private ObservableList<String> sessionNameList = FXCollections.observableArrayList();
	private String SESSION_ID;
	
	private Label selectPathLabel = new Label("Select Path");
	private Button selectPathButton = new Button("Select");
	
	private Button copyButton = new Button("Backup");
	private Button closeButton = new Button("Close");

	private SessionManagement sessionManagement = new SessionManagement();
	private SessionFileManagement sessionFileManagement = new SessionFileManagement();
	
	public void initialize() {
		dataBackupMainContainer.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataBackup.css").toExternalForm());
		createDataBackupPopupContent();
		initializeSessionNameComboBox();
	}

	private void createDataBackupPopupContent() {
		dataBackupGridPane.add(selectSessionLabel, 0, 0);
		dataBackupGridPane.add(sessionNameField, 1, 0);
		dataBackupGridPane.add(selectPathLabel, 0, 1);
		dataBackupGridPane.add(selectPathButton, 1, 1);
		
		GridPane.setHgrow(sessionNameField, Priority.ALWAYS); 
		sessionNameField.setMaxWidth(Double.MAX_VALUE);
		
		GridPane.setHgrow(selectPathButton, Priority.ALWAYS); 
		selectPathButton.setMaxWidth(Double.MAX_VALUE);
				
		selectPathButton.setOnAction(e->{
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(dataBackupMainContainer.getScene().getWindow());

			if(selectedDirectory != null){
				String absolutePath = selectedDirectory.getAbsolutePath();
				selectPathButton.setText(absolutePath);
				Tooltip tooltip = new Tooltip(selectPathButton.getText());
		        Tooltip.install(selectPathButton, tooltip);
		        tooltip.setShowDelay(Duration.ZERO);
		        tooltip.setHideDelay(Duration.ZERO);
			}
		});
		
		copyButton.setOnAction(e ->{
			handleCopyFolder();
		});
		
		closeButton.setOnAction(e ->{
			handleClosePopup(); 
		});
		
		buttonHBox.getChildren().addAll(copyButton, closeButton);
	}

	private void initializeSessionNameComboBox() {
		sessionNameField.getItems().clear();
		SESSION_ID = null;
		SessionListResponse response = sessionManagement.getAllSessionData(currentSessionDetails.getUserId());
		if(response.getResponse().getResponseCode() == 1) {			
			sessionDataList = FXCollections.observableArrayList(response.getListOfSession());
			for (SessionList session : sessionDataList) {
				sessionNameList.add(session.getSessionName());
			}
			sessionNameField.setItems(sessionNameList);
			sessionNameField.setOnAction((event) -> {
				SESSION_ID = fetchSessionId(sessionNameField.getValue());
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
	
	private void handleClosePopup() {
		Stage stage = (Stage) dataBackupMainContainer.getScene().getWindow();
		stage.close();
	}
	
	private void handleCopyFolder() {
		if(SESSION_ID == null) {
			Notifications.showWarningAlert("Please Select Session.");
		}else if(selectPathButton.getText().equalsIgnoreCase("Select")){
			Notifications.showWarningAlert("Please Select Path.");
		}else {
			Response response = sessionFileManagement.backupData(SESSION_ID, selectPathButton.getText());
			if(response.getResponseCode() == 1) {
				handleClosePopup();
			}else {
				Notifications.showErrorAlert(response.getResponseMessage());
			}
		}
	}

}
