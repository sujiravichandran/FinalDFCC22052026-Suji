package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddUserController implements Initializable {

    @FXML
    private VBox addUserForm;

    @FXML
    private HBox addUserFormHeading;

    @FXML
    private AnchorPane addUserMainContainer;

    @FXML
    private HBox buttonBox;

    @FXML
    private Button closeButton;

    @FXML
    private VBox confirmPasswordBox;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private PasswordField confirmPasswordTextField;

    @FXML 
    private VBox digitalSignBox;

    @FXML
    private HBox digitalSignImageBox;

    @FXML
    private Label digitalSignLabel;

    @FXML
    private Label headerLabel;

    @FXML
    private VBox passwordBox;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button saveButton;

    @FXML
    private ImageView signImage;

    @FXML
    private Button uploadButton;

    @FXML
    private HBox uploadButtonBox;

    @FXML
    private VBox userNameBox;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private ComboBox<String> userRoleComboBox;

    @FXML
    private VBox userTypeBox;

    private UserManagementController mainPageController;

    private Map<String, String> userRoleType = new HashMap<>();
    String imageUrl;
    byte[] imageData;

    UserManagementModule userManagement = new UserManagementModule();
    AdminCenterContentController adminCenterContentController = new AdminCenterContentController();
    UserManagementController userManagementController = new UserManagementController();

    private boolean isAllowedCharacter(String character) {
        return character.matches("[a-zA-Z0-9._-]");
    }

    public void setMainPageController(UserManagementController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public void setuserData(String userId) {
        createAddUserPopup(userId);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userNameTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!isAllowedCharacter(event.getCharacter())) {
                event.consume();
            }
        });

        addUserMainContainer.getStylesheets().add(getClass()
                .getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/AddUser.css").toExternalForm());
    }

    private void createAddUserPopup(String userId) {
        if (UserData.getRoleId().equals("RL_ID_1")) {
            userRoleType.put("SQUADRON USER", "RL_ID_4");
            userRoleType.put("SQUADRON ADMIN", "RL_ID_2");
            userRoleType.put("BEL USER", "RL_ID_3");
        } else if (UserData.getRoleId().equals("RL_ID_2")) {
            userRoleType.put("SQUADRON USER", "RL_ID_4");
        }

        userRoleComboBox.getItems().addAll(userRoleType.keySet());

        uploadButton.setOnAction(e -> uploadImage());
        saveButton.setOnAction(e -> handleValidateData(null));
        closeButton.setOnAction(e -> handleCancelButton());

        if (userId != null) {
            headerLabel.setText("Update User");
            userTypeBox.setVisible(false);
            userTypeBox.setManaged(false);
            passwordLabel.setText("Enter New Password");
            confirmPasswordLabel.setText("Confirm New Password");
            saveButton.setText("Update");
            saveButton.setOnAction(e -> handleValidateData(userId));
            setEditUserData(userId);
        }

    }

    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(addUserMainContainer.getScene().getWindow());
        if (selectedFile != null) {
            long maxSizeBytes = 500 * 1024;
            if (selectedFile.length() > maxSizeBytes) {
                Notifications.showWarningAlert("Selected file exceeds the maximum allowed size (500KB).");
                return;
            }
            Image image = new Image(selectedFile.toURI().toString());
            signImage.setImage(image);
            try {
                imageData = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setEditUserData(String userId) {
        UserLoginDetailsDto userDetails = userManagement.getUserByUserId(userId);
        userNameTextField.setText(userDetails.getLoginName());
        userRoleComboBox.setValue(userDetails.getRoleId());
        Blob blob = userDetails.getDigitalSignature();
        if (blob != null) {
            try (InputStream is = blob.getBinaryStream()) {
                Image image = new Image(is);
                signImage.setImage(image);
                imageData = convertImageToByteArray(image);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] convertImageToByteArray(Image image) {
        byte[] imageData = null;
        try {
            File tempFile = File.createTempFile("temp", ".png");
            tempFile.deleteOnExit();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempFile);
            imageData = Files.readAllBytes(tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageData;
    }

    private void handleValidateData(String userId) {
        String confirmPassword = confirmPasswordTextField.getText();
        if (userId == null && userRoleComboBox.getValue() == null) {
            Notifications.showWarningAlert("Please select user role...");
            return;
        } else if (userNameTextField.getText().isEmpty()) {
            Notifications.showWarningAlert("Please enter user name...");
            return;
        } else if (passwordTextField.getText().isEmpty()) {
            Notifications.showWarningAlert("Please enter user password...");
            return;
        } else if (confirmPasswordTextField.getText().isEmpty()) {
            Notifications.showWarningAlert("Please enter confirm password...");
            return;
        } else if (!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            Notifications.showWarningAlert("Password and confirm password mismatch..");
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
        } else {
            if (userId == null) {
                handleAddNewUser();
            } else {
                handleEditUserData(userId);
            }
        }
    }

    private void handleAddNewUser() {
        UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
        addUserData.setRoleId(userRoleType.get(userRoleComboBox.getValue()));
        addUserData.setLoginName(userNameTextField.getText());
        addUserData.setPassword(confirmPasswordTextField.getText());

        if (imageData == null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Digital Signature Missing");
            alert.setContentText("Are you sure you want to proceed without adding a Digital Signature?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    UserLoginDetailsDto userDetails = userManagement.addUser(addUserData);
                    if (userDetails.getResponse().getResponseCode() == 1) {
                        Notifications.showSuccessAlert(userDetails.getResponse().getResponseMessage());
                        mainPageController.refreshUserList();
                        Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
                        stage.close();
                    } else {
                        Notifications.showErrorAlert(userDetails.getResponse().getResponseMessage());
                    }
                }
            });
        } else {
            try {
                addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            UserLoginDetailsDto userDetails = userManagement.addUser(addUserData);
            if (userDetails.getResponse().getResponseCode() == 1) {
                Notifications.showSuccessAlert(userDetails.getResponse().getResponseMessage());
                mainPageController.refreshUserList();
                Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
                stage.close();
            } else {
                Notifications.showErrorAlert(userDetails.getResponse().getResponseMessage());
            }
        }
    }

    private void handleEditUserData(String userId) {
        UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
        addUserData.setUserId(userId);
        addUserData.setLoginName(userNameTextField.getText());
        addUserData.setPassword(confirmPasswordTextField.getText());

        if (imageData == null) {
//            Notifications.showWarningAlert("Are You Sure you are not added Digital Sign ");
            Response editDetails = userManagement.updateUser(addUserData);
            if (editDetails.getResponseCode() == 1) {
                Notifications.showSuccessAlert(editDetails.getResponseMessage());
                mainPageController.refreshUserList();
                Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
                stage.close();
            } else {
                Notifications.showErrorAlert(editDetails.getResponseMessage());
            }
        } else {
            try {
                addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Response editDetails = userManagement.updateUser(addUserData);
            if (editDetails.getResponseCode() == 1) {
                Notifications.showSuccessAlert(editDetails.getResponseMessage());
                mainPageController.refreshUserList();
                Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
                stage.close();
            } else {
                Notifications.showErrorAlert(editDetails.getResponseMessage());
            }
        }
    }

    private void handleCancelButton() {
        Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
        stage.close();
    }
}
