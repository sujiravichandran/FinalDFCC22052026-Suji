package com.teclever.dfcc.Controller.ui;

import java.util.HashMap;

import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.utils.CustomButton;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AddAitessController {
    @FXML
    private Label headinglbl;
    @FXML
    private Pane adduser;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label labelAitessCommand;
    @FXML
    private Label labelAitessVersion;
    @FXML
    private Label labelAitessname;
    @FXML
    private Label labelLoadDriver;
    @FXML
    private Label labelUnLoadDriver;
    @FXML
    private Label labelDriverName;
    @FXML
    private Label labelDriverVersion;
    @FXML
    private TextField textAitessCommand;
    @FXML
    private TextField textAitessName;
    @FXML
    private TextField textAitessVersion;
    @FXML
    private TextField textLoadDriver;
    @FXML
    private TextField textUnLoadDriver;
    @FXML
    private TextField textDriverName;

    
    AitessMasterController mainPageController;
    
    public void setMainPageController(AitessMasterController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public void initialize() {
    	
    	
    	setupAddButton();
    	setupCancelButton();

        // Set fixed size for the stage after the scene is fully initialized
        Platform.runLater(() -> {
            Stage stage = (Stage) adduser.getScene().getWindow();
            stage.setMinWidth(450); // Set your desired width
            stage.setMaxWidth(450);
            stage.setMinHeight(564); // Set your desired height
            stage.setMaxHeight(564);
            stage.setResizable(false);
        });
    }

    private void setupAddButton() {

		saveButton.setOnAction(e -> {
			handleSave();
		});
		saveButton.setAlignment(Pos.CENTER);
		this.saveButton.setAlignment(Pos.CENTER);
	}
    
    private void handleSave() {
    	  if (validateFields()) {
              UUTMasterDetailsDto[] uutDataList;
              HashMap<String, String> nameId = new HashMap<>();
              AitessConfigurationManagement configManager = new AitessConfigurationManagement();
              UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList = configManager.getAllUUT();
              int n = uutDataList.length;
              int n2 = 0;
              while (n2 < n) {
                  UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
                  nameId.put(uutType.getUutType(), uutType.getUutId());
                  ++n2;
              }
              AitessConfigurationDto aitessConfigurationDTO = new AitessConfigurationDto();
              aitessConfigurationDTO.setAitessName(AddAitessController.this.textAitessName.getText());
              aitessConfigurationDTO.setAitessCommand(AddAitessController.this.textAitessCommand.getText());
              aitessConfigurationDTO.setAitessVersion(AddAitessController.this.textAitessVersion.getText());
              aitessConfigurationDTO.setDriverName(AddAitessController.this.textDriverName.getText());
              aitessConfigurationDTO.setLoadDriverCommand(AddAitessController.this.textLoadDriver.getText());
              aitessConfigurationDTO.setUnloadDriverCommand(AddAitessController.this.textUnLoadDriver.getText());
              
              AitessConfigurationManagement configurationManagement = new AitessConfigurationManagement();
              AitessConfigurationResponse response = configurationManagement.addAitessConfig(
                      aitessConfigurationDTO);
              if(response.getResponseCode() == 1) {
                  mainPageController.refresh();
                  Stage stage = (Stage) AddAitessController.this.adduser.getScene().getWindow();
                  stage.close();
            	  Notifications.showSuccessAlert(response.getResponseMessage());
              }else if(response.getResponseCode() == 0){
            	  Notifications.showErrorAlert(response.getResponseMessage());
              }

          }
      }

    private void setupCancelButton() {
		cancelButton.setOnAction(e -> {
			handleCancel();
		});

		this.cancelButton.setAlignment(Pos.CENTER);
	}

    private void handleCancel() {
    	Stage stage = (Stage) AddAitessController.this.adduser.getScene().getWindow();
        stage.close();
    }
    
    private boolean validateFields() {
        if (textAitessName.getText().isEmpty()) {
            showAlert("AITESS Name is required");
            return false;
        }
        if (textAitessCommand.getText().isEmpty()) {
            showAlert("AITESS Command is required");
            return false;
        }
        if (textAitessVersion.getText().isEmpty()) {
            showAlert("AITESS Version is required");
            return false;
        }
        if (textDriverName.getText().isEmpty()) {
            showAlert("Driver Name is required");
            return false;
        }
        if (textLoadDriver.getText().isEmpty()) {
            showAlert("Load Driver is required");
            return false;
        }
        if (textUnLoadDriver.getText().isEmpty()) {
            showAlert("UnLoad Driver is required");
            return false;
        }

        return true;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void alertBox(String text, ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(text);
        alert.showAndWait().ifPresent(response -> {
            Stage stage = (Stage)((CustomButton)actionEvent.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}
