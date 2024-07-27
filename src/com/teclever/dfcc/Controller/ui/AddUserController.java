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
import com.teclever.dfcc.utils.CustomButton;
import com.teclever.dfcc.utils.Notifications;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddUserController {

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

	public void setMainPageController(UserManagementController mainPageController) {
		this.mainPageController = mainPageController;
	}

	public void setuserData(String userId) {
		createAddUserPopup(userId);
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
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
		}
//		else if (imageData == null) {
//			Notifications.showWarningAlert("Please select digiatl signature...");
//			return;
//		} 
		else if (!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
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

	    // Check if the username contains special characters
	    if (userNameTextField.getText().matches(".*[&+*+#+$+%+@].*")) {
	        Notifications.showErrorAlert("Please enter a Correct User Name");
	        return;  // Exit the method if special characters are found
	    } else {
	        addUserData.setLoginName(userNameTextField.getText());
	        addUserData.setPassword(confirmPasswordTextField.getText());

	        if (imageData == null) {
	            // Show confirmation alert
	            Alert alert = new Alert(AlertType.CONFIRMATION);
	            alert.setTitle("Confirmation Dialog");
	            alert.setHeaderText("Digital Signature Missing");
	            alert.setContentText("Are you sure you want to proceed without adding a Digital Signature?");
	            
	            // Capture the response from the user
	            alert.showAndWait().ifPresent(response -> {
	                if (response == ButtonType.OK) {
	                    // Proceed with adding the user without a digital signature
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
	}

	private void handleEditUserData(String userId) {
		UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
		addUserData.setUserId(userId);
		addUserData.setLoginName(userNameTextField.getText());
		addUserData.setPassword(confirmPasswordTextField.getText());

		if (imageData == null) {
			Notifications.showWarningAlert("Are You Sure you are not added Digital Sign ");
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

//package com.teclever.dfcc.Controller.ui;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.nio.file.Files;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.ResourceBundle;
//
//import javax.imageio.ImageIO;
//
//import com.teclever.datastore.dto.Response;
//import com.teclever.dfcc.UserData;
//import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
//import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
//import com.teclever.dfcc.utils.Notifications;
//
//import javafx.embed.swing.SwingFXUtils;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//public class AddUserController implements Initializable {
//
//	@FXML
//	private AnchorPane addUserMainContainer;
//	@FXML
//	private HBox addUserFormHeading;
//	@FXML
//	private VBox addUserForm;
//
//	private Label heading = new Label();
//	
//	private HBox userTypeContainerBox = new HBox();
//	private ComboBox<String> userTypeBox = new ComboBox<>();
//	
//	private VBox userNameBox = new VBox();
//	private Label userNameLabel = new Label();;
//	private TextField userNameTextField = new TextField();
//
//	private VBox passwordBox = new VBox();
//	private Label passwordLabel = new Label();
//	private TextField passwordTextField = new TextField();
//	
//	private VBox confirmPasswordBox = new VBox();
//	private Label confirmPasswordLabel = new Label();
//	private TextField confirmPasswordTextField = new TextField();
//	
//	private VBox digitalSignBox = new VBox();
//	private Label digitalSignatureLabel = new Label();
//	private HBox digitalSignaturePane = new HBox();
//	private ImageView signImage = new ImageView();
//	
//	private HBox uploadButtonBox = new HBox();
//	
//	private HBox buttonContainerBox = new HBox(50);
//	private Button addButton = new Button();
//	private Button cancelButton = new Button();
//
//	private Map<String, String> userRoleType = new HashMap<>();
//
//	UserManagementModule userManagement = new UserManagementModule();
//
//	String imageUrl;
//
//	byte[] imageData;
//
//	private UserManagementController mainPageController;
//	
//	AdminCenterContentController adminCenterContentController = new AdminCenterContentController();
//	UserManagementController userManagementController = new UserManagementController();
//
//	public void setMainPageController(UserManagementController mainPageController) {
//		this.mainPageController = mainPageController;
//	}
//
//	public void setuserData(String userId) {
//		System.out.println("Session userData: " + userId);
//		createAddUserPopup(userId);
//	}
//
//	public void initialize(URL arg0, ResourceBundle arg1) {
//		addUserMainContainer.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AddUser.css").toExternalForm());
//		addUserRoleType();
//	}
//
//	public void addUserRoleType() {
//		if(UserData.getRoleId().equals("RL_ID_1")) {
//			userRoleType.put("SQUADRON USER", "RL_ID_4");
//			userRoleType.put("SQUADRON ADMIN", "RL_ID_2");
//			userRoleType.put("BEL USER", "RL_ID_3");
//		}else if(UserData.getRoleId().equals("RL_ID_2")) {
//			userRoleType.put("SQUADRON USER", "RL_ID_4");
//		}	
//	}
//
//	private void createAddUserPopup(String userId) {
//
//		addUserForm.setSpacing(10);
//		
//		heading.setText("Add User");
//		heading.getStyleClass().add("adduser-title");
//		addUserFormHeading.getChildren().add(heading);
//
//		userTypeBox.getStyleClass().add("adduser-combo");
//		userTypeBox.setPromptText("Select User Role");
//		userTypeBox.getItems().addAll(userRoleType.keySet());
//		userTypeContainerBox.setAlignment(Pos.CENTER);
//		userTypeContainerBox.getChildren().add(userTypeBox);
//
//	
//		userNameLabel.setText("User Name");
//		userNameLabel.getStyleClass().add("adduser-label");
//		userNameTextField.getStyleClass().add("adduser-input");
//		userNameBox.getChildren().addAll(userNameLabel,userNameTextField);
//
//		passwordLabel.setText("Enter Password");
//		passwordLabel.getStyleClass().add("adduser-label");
//		passwordTextField.getStyleClass().add("adduser-input");
//		passwordBox.getChildren().addAll(passwordLabel,passwordTextField);
//
//		confirmPasswordLabel.setText("Confirm Password");
//		confirmPasswordLabel.getStyleClass().add("adduser-label");
//		confirmPasswordTextField.getStyleClass().add("adduser-input");
//		confirmPasswordBox.getChildren().addAll(confirmPasswordLabel,confirmPasswordTextField);
//		
//		digitalSignatureLabel.setText("Digital Signature");
//		digitalSignatureLabel.getStyleClass().add("adduser-label");
//		digitalSignaturePane.getStyleClass().add("adduser-sign");
//		digitalSignaturePane.setAlignment(Pos.CENTER);
//		signImage.setPreserveRatio(true);
//		signImage.setFitHeight(130);
//		signImage.setFitWidth(130);
//		digitalSignaturePane.getChildren().add(signImage);
//		digitalSignBox.getChildren().addAll(digitalSignatureLabel,digitalSignaturePane);
//
//
//		uploadButtonBox.setAlignment(Pos.CENTER);
//		Button uploadButton = new Button("Upload");
//
//		uploadButton.setOnAction(e -> uploadImage());
//
//		uploadButtonBox.getChildren().add(uploadButton);
//
//		buttonContainerBox.getStyleClass().add("button-box");
//
//		addButton = new Button("Add User");
//		cancelButton = new Button("Cancel");
//
//		addButton.setOnAction(e -> handleValidateData(null));
//		cancelButton.setOnAction(e -> handleCancelButton());
//
//		buttonContainerBox.getChildren().addAll(cancelButton,addButton);
//
//		if (userId != null) {
//			heading.setText("Update User");
//			userTypeBox.setVisible(false);
//			userTypeBox.setManaged(false);
//			passwordLabel.setText("Enter New Password");
//			confirmPasswordLabel.setText("Confirm New Password");
//			addButton.setText("Update User");
//			addButton.setOnAction(e -> handleValidateData(userId));
//			setEditUserData(userId);
//		}
//
//		addUserForm.getChildren().addAll(userTypeContainerBox,userNameBox, passwordBox, confirmPasswordBox, digitalSignBox,
//				 uploadButtonBox, buttonContainerBox);
//	}
//
//	private void uploadImage() {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Choose Image File");
//		fileChooser.getExtensionFilters()
//				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
//		File selectedFile = fileChooser.showOpenDialog(addUserMainContainer.getScene().getWindow());
//		if (selectedFile != null) {
//			long maxSizeBytes = 500 * 1024;
//			if (selectedFile.length() > maxSizeBytes) {
//				Notifications.showWarningAlert("Selected file exceeds the maximum allowed size (500KB).");
//				return;
//			}
//			Image image = new Image(selectedFile.toURI().toString());
//			signImage.setImage(image);
//			try {
//				imageData = Files.readAllBytes(selectedFile.toPath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void setEditUserData(String userId) {
//		UserLoginDetailsDto userDetails = userManagement.getUserByUserId(userId);
//		userNameTextField.setText(userDetails.getLoginName());
//		userTypeBox.setValue(userDetails.getRoleId());
//		Blob blob = userDetails.getDigitalSignature();
//		if (blob != null) {
//			try (InputStream is = blob.getBinaryStream()) {
//				Image image = new Image(is);
//				signImage.setImage(image);
//				imageData = convertImageToByteArray(image);
//			} catch (SQLException | IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private byte[] convertImageToByteArray(Image image) {
//		byte[] imageData = null;
//		try {
//			File tempFile = File.createTempFile("temp", ".png");
//			tempFile.deleteOnExit();
//			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempFile);
//			imageData = Files.readAllBytes(tempFile.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return imageData;
//	}
//
//	private void handleValidateData(String userId) {
//		String newPassword = passwordTextField.getText();
//		String confirmPassword = confirmPasswordTextField.getText();
//		if (userId == null && userTypeBox.getValue() == null) {
//			Notifications.showWarningAlert("Please select user role...");
//			return;
//		} else if (userNameTextField.getText().isEmpty()) {
//			Notifications.showWarningAlert("Please enter user name...");
//			return;
//		} else if (passwordTextField.getText().isEmpty()) {
//			Notifications.showWarningAlert("Please enter user password...");
//			return;
//		} else if (confirmPasswordTextField.getText().isEmpty()) {
//			Notifications.showWarningAlert("Please enter confirm password...");
//			return;
//		} else if (imageData == null) {
//			Notifications.showWarningAlert("Please select digiatl signature...");
//			return;
//		} else if (!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
//			Notifications.showWarningAlert("Password and confirm password mismatch..");
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
//		}else {
//			if (userId == null) {
//				handleAddNewUser();
//			} else {
//				handleEditUserData(userId);
//			}
//		}
//	}
//
//	private void handleAddNewUser() {
//		UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
//		addUserData.setRoleId(userRoleType.get(userTypeBox.getValue()));;
//		addUserData.setLoginName(userNameTextField.getText());
//		addUserData.setPassword(confirmPasswordTextField.getText());
//		try {
//			addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		UserLoginDetailsDto userDetails = userManagement.addUser(addUserData);
//		if (userDetails.getResponse().getResponseCode() == 1) {
//			Notifications.showSuccessAlert(userDetails.getResponse().getResponseMessage());
//			mainPageController.refreshUserList();
//			Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
//			stage.close();
//			
//		} else {
//			Notifications.showErrorAlert(userDetails.getResponse().getResponseMessage());
//		}
//	}
//
//	private void handleEditUserData(String userId) {
//		UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
//		addUserData.setUserId(userId);
//		addUserData.setLoginName(userNameTextField.getText());
//		addUserData.setPassword(confirmPasswordTextField.getText());
//		try {
//			addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		Response editDetails = userManagement.updateUser(addUserData);
//		if (editDetails.getResponseCode() == 1) {
//			Notifications.showSuccessAlert(editDetails.getResponseMessage());
//			mainPageController.refreshUserList();
//			Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
//			stage.close();
//		} else {
//			Notifications.showErrorAlert(editDetails.getResponseMessage());
//		}
//
//	}
//
//	private void handleCancelButton() {
//		Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
//		stage.close();
//	}
//
//
//
//}

//<?xml version="1.0" encoding="UTF-8"?>
//
//<?import javafx.geometry.Insets?>
//<?import javafx.scene.layout.AnchorPane?>
//<?import javafx.scene.layout.HBox?>
//<?import javafx.scene.layout.VBox?>
//
//<AnchorPane fx:id="addUserMainContainer" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="500.0" prefWidth="400.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.teclever.dfcc.Controller.ui.AddUserController">
//   <children>
//      <HBox fx:id="addUserFormHeading" alignment="CENTER" prefHeight="50.0" prefWidth="400.0" />
//      <VBox fx:id="addUserForm" alignment="CENTER_LEFT" layoutY="50.0" prefHeight="400.0" prefWidth="400.0" spacing="10.0">
//         <padding>
//            <Insets bottom="20.0" left="30.0" right="30.0" />
//         </padding>
//       </VBox>
//   </children>
//</AnchorPane>
