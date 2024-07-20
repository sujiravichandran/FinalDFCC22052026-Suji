package com.teclever.dfcc.Controller.ui;

import java.io.File;
import com.teclever.datastore.response.OfpConfigurationResponse;
import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.utils.Notifications;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
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
	       
	     // Set fixed size for the stage after the scene is fully initialized
	        Platform.runLater(() -> {
	            Stage stage = (Stage) addOfpMainContainer.getScene().getWindow();
	            stage.setMinWidth(400); // Set your desired width
	            stage.setMaxWidth(400);
	            stage.setMinHeight(345); // Set your desired height
	            stage.setMaxHeight(345);
	            stage.setResizable(false);
	        });
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
        if (validatefields()) {
            Stage stage = (Stage) addOfpForm.getScene().getWindow();
            
            Platform.runLater(() -> {
                stage.getScene().setCursor(Cursor.WAIT);
                setControlsDisabled(stage.getScene().getRoot(), true);
            });

            OfpConfigurationDto ofpConfigurationDto = new OfpConfigurationDto();
            ofpConfigurationDto.setUutId(UUT_ID);
            ofpConfigurationDto.setOfpName(ofp_name_field.getText());
            ofpConfigurationDto.setOfpVersion(ofp_version_field.getText());
            ofpConfigurationDto.setConfigFile(selectedConfigFile);

            Task<OfpConfigurationResponse> saveTask = new Task<OfpConfigurationResponse>() {
                @Override
                protected OfpConfigurationResponse call() throws Exception {
                	OfpConfigurationResponse res = ofpConfig.addOfpConfig(ofpConfigurationDto, UUT_ID);
                	System.out.println(res.getResponseMessage()+"--");
                    return res;
                }
            };

            saveTask.setOnSucceeded(workerStateEvent -> {
                Platform.runLater(() -> {
                    stage.getScene().setCursor(Cursor.DEFAULT);
                    setControlsDisabled(stage.getScene().getRoot(), false);
                    stage.close();
                    mainPageController.refresh();
                });
                Notifications.showSuccessAlert(saveTask.getValue().getResponseMessage());
            });

            saveTask.setOnFailed(workerStateEvent -> {
                Platform.runLater(() -> {
                    stage.getScene().setCursor(Cursor.DEFAULT);
                    setControlsDisabled(stage.getScene().getRoot(), false);
                });
                Notifications.showErrorAlert(saveTask.getValue().getResponseMessage());
            });

            // Start the task on a background thread
            new Thread(saveTask).start();
        }
    }

    
	private void setControlsDisabled(Node root, boolean disabled) {
	    for (Node node : root.lookupAll("*")) {
	        if (node instanceof Control) {
	            ((Control) node).setDisable(disabled);
	        }
	    }
	}
	
    private boolean validatefields() {
    	if(ofp_name_field.getText().isEmpty()) {
    		Notifications.showErrorAlert("Please Enter the OFP Name");
    		return false;
    	}
    	if(ofp_version_field.getText().isEmpty()) {
    		Notifications.showErrorAlert("Please Enter the OFP Version");
    		return false;
    	}
    	if (selectedConfigFile == null || selectedConfigFile.isEmpty()) {
    		Notifications.showErrorAlert("Please select the Configuration File.");
    		return false;
		}
		return true;
    	
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