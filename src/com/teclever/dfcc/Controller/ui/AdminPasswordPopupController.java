
package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPasswordPopupController {

	@FXML
	private HBox buttonBox;

	@FXML
	private VBox changePasswordForm;

	@FXML
	private HBox changePasswordFormHeading;

	@FXML
	private AnchorPane changePasswordMainContainer;

	@FXML
	private Button closeButton;

	@FXML
	private TextField confirmPasswordField;

	@FXML
	private Label confirmPasswordLabel;

	@FXML
	private VBox confirmPasswordVBox;

	@FXML
	private Label headerLabel;

	@FXML
	private TextField newPasswordField;

	@FXML
	private Label newPasswordLabel;

	@FXML
	private VBox newPasswordVBox;

	@FXML
	private Button saveButton;

	UserManagementModule userManagementModule = new UserManagementModule();

	public void initialize() {
		changePasswordMainContainer.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AdminPassword.css").toExternalForm());
		createLaunchTypePopupContent();
	}

	private void createLaunchTypePopupContent() {
		closeButton.setOnAction(e -> closeLaunchTypePopup());
		saveButton.setOnAction(e -> updateAdminPassword());
	}

	private void updateAdminPassword() {
		String newPassword = newPasswordField.getText();
		String confirmPassword = confirmPasswordField.getText();

		if (newPassword.isEmpty()) {
			Notifications.showWarningAlert("Please Enter New Password...");
			return;
		} else if (confirmPassword.isEmpty()) {
			Notifications.showWarningAlert("Please Enter Confirm Password...");
			return;
		} else if (!newPassword.equals(confirmPassword)) {
			Notifications.showErrorAlert("Password and Confirm Password Mismatched...");
			return;
		}

		StringBuilder errorMessage = new StringBuilder();
		if (confirmPassword.length() < 8) {
			errorMessage.append("Password must be at least 8 characters long.\n");
		}
		if (!confirmPassword.matches(".*[a-z].*")) {
			errorMessage.append("Password must contain at least one lowercase letter.\n");
		}
		if (!confirmPassword.matches(".*[A-Z].*")) {
			errorMessage.append("Password must contain at least one uppercase letter.\n");
		}
		if (!confirmPassword.matches(".*\\d.*")) {
			errorMessage.append("Password must contain at least one digit.\n");
		}
		if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
			errorMessage.append("Password must contain at least one special character.\n");
		}

		if (errorMessage.length() > 0) {
			Notifications.showErrorAlert(errorMessage.toString());
			return;
		}

		Response response = userManagementModule.changePasswordForBellAdmin(confirmPassword);
		if (response.getResponseCode() == 1) {
			Stage stage = (Stage) changePasswordMainContainer.getScene().getWindow();
			stage.close();
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}

	}

	private void closeLaunchTypePopup() {
		Stage stage = (Stage) changePasswordMainContainer.getScene().getWindow();
		stage.close();
	}
}

//package com.teclever.dfcc.Controller.ui;
//
//import com.teclever.datastore.dto.Response;
//import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
//import com.teclever.dfcc.utils.Notifications;
//
//import javafx.fxml.FXML;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class AdminPasswordPopupController {
//	@FXML
//	private AnchorPane changePasswordMainContainer;
//	@FXML
//	private HBox changePasswordHeading;
//	@FXML
//	private VBox changePasswordForm;
//
//	private Label changePasswordTitle = new Label();
//	private VBox newPasswordVBox = new VBox();
//	private Label newPasswordLabel = new Label();
//	private TextField newPasswordField = new TextField();
//	private VBox confirmPasswordVBox = new VBox();
//	private Label confirmPasswordLabel = new Label();
//	private TextField confirmPasswordField = new TextField();
//	private HBox buttonHBox = new HBox(50);
//	private Button closeButton = new Button();
//	private Button saveButton = new Button();
//
//	UserManagementModule userManagementModule = new UserManagementModule();
//
//	public void initialize() {
//		changePasswordMainContainer.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AdminPassword.css").toExternalForm());
//		createLaunchTypePopupContent();
//	}
//
//	private void createLaunchTypePopupContent() {
//		changePasswordTitle.setText("Change Password");
//		changePasswordTitle.getStyleClass().add("change-password-title");
//		changePasswordHeading.getChildren().add(changePasswordTitle);
//
//		newPasswordLabel.setText("New Password");
//		newPasswordLabel.getStyleClass().add("change-password-label");
//		newPasswordField.getStyleClass().add("change-password-input");
//		newPasswordVBox.getChildren().addAll(newPasswordLabel, newPasswordField);
//
//		confirmPasswordLabel.setText("Confirm Password");
//		confirmPasswordLabel.getStyleClass().add("change-password-label");
//		confirmPasswordField.getStyleClass().add("change-password-input");
//		confirmPasswordVBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);
//
//		closeButton.setText("Close");
//		closeButton.getStyleClass().add("change-password-btn");
//		saveButton.setText("Save");
//		saveButton.getStyleClass().add("change-password-btn");
//		buttonHBox.setAlignment(Pos.CENTER);
//		buttonHBox.getStyleClass().add("change-password-btn-box");
//		buttonHBox.getChildren().addAll(closeButton, saveButton);
//
//		closeButton.setOnAction(e -> closeLaunchTypePopup());
//
//		saveButton.setOnAction(e -> updateAdminPassword());
//
//		changePasswordForm.getChildren().addAll(newPasswordVBox, confirmPasswordVBox, buttonHBox);
//	}
//
//	private void updateAdminPassword() {
//		String newPassword = newPasswordField.getText();
//		String confirmPassword = confirmPasswordField.getText();
//
//		if (newPassword.isEmpty()) {
//			Notifications.showWarningAlert("Please Enter New Password...");
//			return;
//		} else if (confirmPassword.isEmpty()) {
//			Notifications.showWarningAlert("Please Enter Confirm Password...");
//			return;
//		} else if (!newPassword.equals(confirmPassword)) {
//			Notifications.showErrorAlert("Password and Confirm Password Mismatched...");
//			return;
//		}
//
//		StringBuilder errorMessage = new StringBuilder();
//		if (confirmPassword.length() < 8) {
//			errorMessage.append("Password must be at least 8 characters long.\n");
//		}
//		if (!confirmPassword.matches(".*[a-z].*")) {
//			errorMessage.append("Password must contain at least one lowercase letter.\n");
//		}
//		if (!confirmPassword.matches(".*[A-Z].*")) {
//			errorMessage.append("Password must contain at least one uppercase letter.\n");
//		}
//		if (!confirmPassword.matches(".*\\d.*")) {
//			errorMessage.append("Password must contain at least one digit.\n");
//		}
//		if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
//			errorMessage.append("Password must contain at least one special character.\n");
//		}
//
//		if (errorMessage.length() > 0) {
//			Notifications.showErrorAlert(errorMessage.toString());
//			return;
//		}
//
//		Response response = userManagementModule.changePasswordForBellAdmin(confirmPassword);
//		if (response.getResponseCode() == 1) {
//			Stage stage = (Stage) changePasswordMainContainer.getScene().getWindow();
//			stage.close();
//		} else {
//			Notifications.showErrorAlert(response.getResponseMessage());
//		}
//
//	}
//
//	private void closeLaunchTypePopup() {
//		Stage stage = (Stage) changePasswordMainContainer.getScene().getWindow();
//		stage.close();
//	}
//}
