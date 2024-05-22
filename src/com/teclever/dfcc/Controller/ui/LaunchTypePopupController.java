package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
	private AnchorPane launchTypeMainContainer;
	@FXML
	private HBox launchTypeHeading;
	@FXML
	private VBox launchTypeForm;
	

	private Label launchTypeTitle = new Label();
	private HBox option1HBox = new HBox(20);
	private HBox option2HBox = new HBox(20);
	private ToggleGroup optionGroup = new ToggleGroup();
	private RadioButton option1RadioButton = new RadioButton();
	private RadioButton option2RadioButton = new RadioButton();
	private Label option1Label = new Label();
	private Label option2Label = new Label();
	private HBox buttonHBox = new HBox(50);
	private Button closeButton = new Button();
	private Button saveButton = new Button();
	
	UserManagementModule userManagementModule = new UserManagementModule();
	
	public void initialize() {
		launchTypeMainContainer.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/LaunchType.css").toExternalForm());
		createLaunchTypePopupContent();
	}
	
	private void createLaunchTypePopupContent() {
		launchTypeTitle.setText("Select Launch Type");
		launchTypeTitle.getStyleClass().add("launch-type-title");
		launchTypeHeading.getChildren().add(launchTypeTitle);
		
		option1RadioButton.setText("Option-1");
		option1RadioButton.setToggleGroup(optionGroup);
		option1RadioButton.setSelected(true);
		option1RadioButton.setUserData("o1");
		option1RadioButton.getStyleClass().add("launch-type-radio-btn");
		option1Label.setText("Bel-Admin , Bel-User");
		option1Label.getStyleClass().add("launch-type-radio-btn-label");
		option1HBox.getChildren().addAll(option1RadioButton,option1Label);
		
		option2RadioButton.setText("Option-2");
		option2RadioButton.setToggleGroup(optionGroup);
		option2RadioButton.setUserData("o2");
		option2RadioButton.getStyleClass().add("launch-type-radio-btn");
		option2Label.setText("Bel-Admin , Squadron-Admin , Squadron-User");
		option2Label.getStyleClass().add("launch-type-radio-btn-label");
		option2HBox.getChildren().addAll(option2RadioButton,option2Label);
		
		closeButton.setText("Close");
		closeButton.getStyleClass().add("launch-type-btn");
		saveButton.setText("Save");
		saveButton.getStyleClass().add("launch-type-btn");
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getChildren().addAll(closeButton,saveButton);
		
		closeButton.setOnAction(e -> closeLaunchTypePopup());
		
		saveButton.setOnAction(e -> saveLaunchType());
		
		launchTypeForm.getChildren().addAll(option1HBox, option2HBox, buttonHBox);
	}

	private void saveLaunchType() {
		String option = (String) optionGroup.getSelectedToggle().getUserData();
		Response response = userManagementModule.updateOption(option);
		if(response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
			Stage stage = (Stage) launchTypeMainContainer.getScene().getWindow();
			stage.close();
		}else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
		
	}

	private void closeLaunchTypePopup() {
		Stage stage = (Stage) launchTypeMainContainer.getScene().getWindow();
		stage.close();
	}
}
