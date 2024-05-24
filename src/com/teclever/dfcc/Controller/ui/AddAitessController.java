package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.utils.CustomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AddAitessController {
    @FXML
    private Label headinglbl;
    @FXML
    private Pane adduser;
    @FXML
    private HBox hboxCancel;
    @FXML
    private HBox hboxSave;
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
    @FXML
    private TextField textDriverVersion;
    
    AitessMasterController mainPageController;
    
    
    public void setMainPageController(AitessMasterController mainPageController) {
    	this.mainPageController=mainPageController;
    }
    
    
    public void initialize() {
        CustomButton saveButton = new CustomButton("SAVE", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
                    aitessConfigurationDTO.setDriverVersion(AddAitessController.this.textDriverVersion.getText());
                    aitessConfigurationDTO.setUutId(nameId.get(AitessMasterController.UUTdropdownValue));
                    AitessConfigurationManagement configurationManagement = new AitessConfigurationManagement();
                    AitessConfigurationResponse response = configurationManagement.addAitessConfig(
                            aitessConfigurationDTO, nameId.get(AitessMasterController.UUTdropdownValue));
                    System.out.println("Response Code: " + response.getResponseCode());
                    System.out.println("Response Message: " + response.getResponseMessage());
                    mainPageController.refresh();
                    Stage stage = (Stage) AddAitessController.this.adduser.getScene().getWindow();
                    stage.close();
                }
            }
        });
        saveButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
        this.hboxSave.getChildren().add(saveButton);
        this.hboxSave.setAlignment(Pos.CENTER);

        CustomButton cancelButton = new CustomButton("CANCEL", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) AddAitessController.this.adduser.getScene().getWindow();
                stage.close();
            }
        });
        cancelButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
        this.hboxCancel.getChildren().add(cancelButton);
        this.hboxCancel.setAlignment(Pos.CENTER);
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
        if (textDriverVersion.getText().isEmpty()) {
            showAlert("Driver Version is required");
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