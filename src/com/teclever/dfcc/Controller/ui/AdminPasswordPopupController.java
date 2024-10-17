package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
    private PasswordField confirmPasswordField;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private VBox confirmPasswordVBox;

    @FXML
    private Label headerLabel;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label newPasswordLabel;

    @FXML
    private VBox newPasswordVBox;

    @FXML
    private Button saveButton;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private TextField newPasswordTextField;

    @FXML
    private TextField confirmPasswordTextField;

    UserManagementModule userManagementModule = new UserManagementModule();

    public void initialize() {
        changePasswordMainContainer.getStylesheets()
                .add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AdminPassword.css").toExternalForm());
        createLaunchTypePopupContent();
        setupPasswordVisibilityToggle();
    }

    private void createLaunchTypePopupContent() {
        closeButton.setOnAction(e -> closeLaunchTypePopup());
        saveButton.setOnAction(e -> updateAdminPassword());
    }

    private void setupPasswordVisibilityToggle() {
    	showPasswordCheckBox.getStyleClass().add("show-password-checkbox");
    	
        newPasswordTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        newPasswordTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());

        newPasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        newPasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        newPasswordTextField.textProperty().bindBidirectional(newPasswordField.textProperty());

        confirmPasswordTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        confirmPasswordTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());

        confirmPasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        confirmPasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    private void updateAdminPassword() {
        String newPassword = showPasswordCheckBox.isSelected() ? newPasswordTextField.getText() : newPasswordField.getText();
        String confirmPassword = showPasswordCheckBox.isSelected() ? confirmPasswordTextField.getText() : confirmPasswordField.getText();

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

