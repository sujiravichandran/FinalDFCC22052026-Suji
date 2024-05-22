package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.datastore.response.OfpConfigurationResponse;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.utils.Notifications;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddOFPController {

	 @FXML
	    private VBox addOfpForm;

	    @FXML
	    private HBox addOfpHeading;

	    @FXML
	    private AnchorPane addOfpMainContainer;

	    @FXML
	    private Button add_ofp_button;

	    @FXML
	    private Button cancel_button;

	    @FXML
	    private Label headerLabel;

	    @FXML
	    private TextField ofp_name_field;

	    @FXML
	    private TextField ofp_version_field;

	    @FXML
	    private Button select_config_button;
	    
	   
	    @FXML
	    private void initialize() {
	        Tooltip ofpTooltip = new Tooltip();
	        ofpTooltip.textProperty().bind(select_config_button.textProperty());
	        select_config_button.setTooltip(ofpTooltip);
	    }
    @FXML
    private TextField stage_parent_name_field1;
    private OFPMasterController mainPageController;
    private String selectedConfigFile;
    private String UUT_ID;
    
    public void setMainPageController(OFPMasterController mainPageController) {
		this.mainPageController = mainPageController;
	}
    
    private OfpConfigurationManagement ofpConfig=new OfpConfigurationManagement();
    public void setUutID(String uutId) {
    	this.UUT_ID=uutId;
    }

    @FXML
    void onClickAddOfp(ActionEvent event) {
    	OfpConfigurationDto ofpConfigurationDto=new OfpConfigurationDto();
    	ofpConfigurationDto.setUutId(UUT_ID);
    	ofpConfigurationDto.setOfpName(ofp_name_field.getText());
    	ofpConfigurationDto.setOfpVersion(ofp_version_field.getText());
    	ofpConfigurationDto.setConfigFile(selectedConfigFile);
    	
    	
 	    
    	OfpConfigurationResponse response= ofpConfig.addOfpConfig(ofpConfigurationDto, UUT_ID);
    	if(response.getResponseCode() == 1) {
    		Stage stage = (Stage) addOfpMainContainer.getScene().getWindow();
    		stage.close();
    		mainPageController.refresh();
    	}else {
    		Notifications.showErrorAlert(response.getResponseMessage());
    	}
    	
    }
   

    @FXML
    void onClickSelectConfigFile(ActionEvent event) {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Configuration File");
//		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(addOfpMainContainer.getScene().getWindow());
		 if (selectedFile != null) {
			 selectedConfigFile = selectedFile.getAbsolutePath();
			 select_config_button.setText(selectedConfigFile);
			 
	      }	

    }
    @FXML
    void onClickCancel(ActionEvent event) {
    	Stage stage = (Stage) addOfpMainContainer.getScene().getWindow();
		stage.close();
    }

}