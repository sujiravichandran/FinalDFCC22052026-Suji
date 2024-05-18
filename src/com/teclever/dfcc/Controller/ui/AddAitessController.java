package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.utils.CustomButton;
import java.util.HashMap;
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
    private Label labelDriverCommand;
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
    private TextField textDriverCommand;
    @FXML
    private TextField textDriverName;
    @FXML
    private TextField textDriverVersion;
    AitessMasterController mainPageController;
    
    
    public void setMainPageController(AitessMasterController mainPageController) {
    	this.mainPageController=mainPageController;
    }
    
    
    public void initialize() {
        CustomButton saveButton = new CustomButton("SAVE", new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                UUTMasterDetailsDto[] uutDataList;
                HashMap<String, String> nameId = new HashMap<String, String>();
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
                aitessConfigurationDTO.setDriverCommand(AddAitessController.this.textDriverCommand.getText());
                aitessConfigurationDTO.setDriverVersion(AddAitessController.this.textDriverVersion.getText());
                aitessConfigurationDTO.setUutId((String)nameId.get(AitessMasterController.UUTdropdownValue));
                AitessConfigurationManagement configurationManagement = new AitessConfigurationManagement();
                AitessConfigurationResponse response = configurationManagement.addAitessConfig(aitessConfigurationDTO, (String)nameId.get(AitessMasterController.UUTdropdownValue));
                System.out.println("Response Code: " + response.getResponseCode());
                System.out.println("Response Message: " + response.getResponseMessage());
                mainPageController.refresh();
                Stage stage = (Stage)AddAitessController.this.adduser.getScene().getWindow();
                stage.close();
                
            }
        });
        saveButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
        this.hboxSave.getChildren().add(saveButton);
        this.hboxSave.setAlignment(Pos.CENTER);
        CustomButton cancelButton = new CustomButton("CANCEL", new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage)AddAitessController.this.adduser.getScene().getWindow();
                stage.close();
            }
        });
        cancelButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
        this.hboxCancel.getChildren().add(cancelButton);
        this.hboxCancel.setAlignment(Pos.CENTER);
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