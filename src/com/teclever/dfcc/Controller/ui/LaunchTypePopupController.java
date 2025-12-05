package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.SystemConfig;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LaunchTypePopupController {
	
    @FXML
    private HBox buttonBox;
    @FXML
    private Button closeButton;
    @FXML
    private Label headerLabel;
    @FXML
    private VBox launchTypeForm;
    @FXML
    private HBox launchTypeFormHeading;
    @FXML
    private AnchorPane launchTypeMainContainer;
    @FXML
    private HBox option1HBox;
    @FXML
    private Label option1Label;
    @FXML
    private RadioButton option1RadioButton;
    @FXML
    private HBox option2HBox;
    @FXML
    private Label option2Label;
    @FXML
    private RadioButton option2RadioButton;
    @FXML
    private Button saveButton;
    
    private ToggleGroup optionGroup = new ToggleGroup();
	UserManagementModule userManagementModule = new UserManagementModule();
	String previousOption;
	
	public void initialize() {
		launchTypeMainContainer.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LaunchType.css").toExternalForm());
		createLaunchTypePopupContent();
	}
	
	private void createLaunchTypePopupContent() {
		SystemConfig response = SystemConfigManagement.getConfiguration();
		if(response.getLaunchType() != null) {
			previousOption = response.getLaunchType();
		}
		
		option1RadioButton.setToggleGroup(optionGroup);
		option1RadioButton.setSelected(true);
		option1RadioButton.setUserData("o1");
		option1RadioButton.getStyleClass().add("launch-type-radio-btn");
		option1Label.setText("Bel-Admin , Bel-User");
		
		
		option2RadioButton.setToggleGroup(optionGroup);
		option2RadioButton.setUserData("o2");
		option2RadioButton.getStyleClass().add("launch-type-radio-btn");
		option2Label.setText("Bel-Admin , Squadron-Admin , Squadron-User");
		
	
//		closeButton.setOnAction(e -> closeLaunchTypePopup());
		
		saveButton.setOnAction(e -> saveLaunchType());
		
		if(previousOption.equals("o1")) {
			option1RadioButton.setSelected(true);
		}else {
			option2RadioButton.setSelected(true);
		}
		
	}

	private void saveLaunchType() {
		String option = (String) optionGroup.getSelectedToggle().getUserData();
		Response response = userManagementModule.updateOption(option);
		if(response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
			Stage stage = (Stage) launchTypeMainContainer.getScene().getWindow();
			stage.close();
			Platform.exit();
			System.exit(0);
		}else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
		
	}
	 @FXML
	 void closeLaunchTypePopup(ActionEvent event) {
		Stage stage = (Stage) launchTypeMainContainer.getScene().getWindow();
		stage.close();
	}
}


