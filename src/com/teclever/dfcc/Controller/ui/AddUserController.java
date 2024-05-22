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
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.utils.Notifications;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddUserController implements Initializable {

	@FXML
	private AnchorPane addUserMainContainer;
	@FXML
	private HBox addUserFormHeading;
	@FXML
	private VBox addUserForm;
	@FXML
	private Label heading;
	@FXML
	private HBox userTypeContainerBox;
	@FXML
	private ComboBox<String> userTypeBox;
	@FXML
	private Label userNameLabel;
	@FXML
	private TextField userNameTextField;
	@FXML
	private Label passwordLabel;
	@FXML
	private TextField passwordTextField;
	@FXML
	private Label confirmPasswordLabel;
	@FXML
	private TextField confirmPasswordTextField;
	@FXML
	private Label digitalSignatureLabel;
	@FXML
	private HBox digitalSignaturePane;
	@FXML
	private HBox buttonContainerBox;
	@FXML
	private Button addButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ImageView signImage;

	private Map<String, String> userRoleType = new HashMap<>();

	UserManagementModule userManagement = new UserManagementModule();

	String imageUrl;

	byte[] imageData;

	private UserManagementController mainPageController;
	
	AdminCenterContentController adminCenterContentController = new AdminCenterContentController();
	UserManagementController userManagementController = new UserManagementController();

	public void setMainPageController(UserManagementController mainPageController) {
		this.mainPageController = mainPageController;
	}

	public void setuserData(String userId) {
		System.out.println("Session userData: " + userId);
		createAddUserPopup(userId);
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		addUserMainContainer.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AddUser.css").toExternalForm());
		addUserRoleType();
	}

	public void addUserRoleType() {
		userRoleType.put("SQUADRON ADMIN", "RL_ID_2");
		userRoleType.put("BEL USER", "RL_ID_3");
		userRoleType.put("SQUADRON USER", "RL_ID_4");
	}

	private void createAddUserPopup(String userId) {
		heading = new Label("Add User");
		heading.getStyleClass().add("adduser-title");
		addUserFormHeading.getChildren().add(heading);

		addUserForm.setSpacing(4);

		userTypeContainerBox = new HBox();
		userTypeContainerBox.setAlignment(Pos.CENTER);

		userTypeBox = new ComboBox<>();
		userTypeBox.getStyleClass().add("adduser-combo");
		userTypeBox.setPromptText("Select User Role");
		userTypeBox.getItems().addAll(userRoleType.keySet());

		userTypeContainerBox.getChildren().add(userTypeBox);

		userNameLabel = new Label("User Name");
		userNameLabel.getStyleClass().add("adduser-label");

		userNameTextField = new TextField();
		userNameTextField.getStyleClass().add("adduser-input");

		passwordLabel = new Label("Enter Password");
		passwordLabel.getStyleClass().add("adduser-label");

		passwordTextField = new TextField();
		passwordTextField.getStyleClass().add("adduser-input");

		confirmPasswordLabel = new Label("Confirm Password");
		confirmPasswordLabel.getStyleClass().add("adduser-label");

		confirmPasswordTextField = new TextField();
		confirmPasswordTextField.getStyleClass().add("adduser-input");

		digitalSignatureLabel = new Label("Digital Signature");
		digitalSignatureLabel.getStyleClass().add("adduser-label");

		digitalSignaturePane = new HBox();
		digitalSignaturePane.getStyleClass().add("adduser-sign");
		digitalSignaturePane.setAlignment(Pos.CENTER);

		signImage = new ImageView();
		signImage.setPreserveRatio(true);
		signImage.setFitHeight(130);
		signImage.setFitWidth(130);

		digitalSignaturePane.getChildren().add(signImage);

		HBox uploadButtonBox = new HBox();
		uploadButtonBox.setAlignment(Pos.CENTER);
		Button uploadButton = new Button("Upload");

		uploadButton.setOnAction(e -> uploadImage());

		uploadButtonBox.getChildren().add(uploadButton);

		buttonContainerBox = new HBox(15);
		buttonContainerBox.getStyleClass().add("button-box");

		addButton = new Button("Add User");
		cancelButton = new Button("Cancel");

		addButton.setOnAction(e -> handleValidateData(null));
		cancelButton.setOnAction(e -> handleCancelButton());

		buttonContainerBox.getChildren().addAll(cancelButton,addButton);

		if (userId != null) {
			heading.setText("Update User");
			userTypeBox.setVisible(false);
			userTypeBox.setManaged(false);
			passwordLabel.setText("Enter New Password");
			confirmPasswordLabel.setText("Confirm New Password");
			addButton.setText("Update User");
			addButton.setOnAction(e -> handleValidateData(userId));
			setEditUserData(userId);
		}

		addUserForm.getChildren().addAll(userTypeContainerBox, userNameLabel, userNameTextField, passwordLabel,
				passwordTextField, confirmPasswordLabel, confirmPasswordTextField, digitalSignatureLabel,
				digitalSignaturePane, uploadButtonBox, buttonContainerBox);
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
		userTypeBox.setValue(userDetails.getRoleId());
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
		String newPassword = passwordTextField.getText();
		String confirmPassword = confirmPasswordTextField.getText();
		if (userId == null && userTypeBox.getValue() == null) {
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
		} else if (imageData == null) {
			Notifications.showWarningAlert("Please select digiatl signature...");
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
		}else {
			if (userId == null) {
				handleAddNewUser();
			} else {
				handleEditUserData(userId);
			}
		}
	}

	private void handleAddNewUser() {
		UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
		addUserData.setRoleId(userRoleType.get(userTypeBox.getValue()));;
		addUserData.setLoginName(userNameTextField.getText());
		addUserData.setPassword(confirmPasswordTextField.getText());
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

	private void handleEditUserData(String userId) {
		UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
		addUserData.setUserId(userId);
		addUserData.setLoginName(userNameTextField.getText());
		addUserData.setPassword(confirmPasswordTextField.getText());
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

	private void handleCancelButton() {
		Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
		stage.close();
	}



}



//try {
//	FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/AddUser.fxml"));
//	Parent root = addUserPopup.load();
//
//	AddUserController controller = addUserPopup.getController();
//	if (userData != null) {
//		controller.setuserData(userData.getUserId());
//	} else {
//		controller.setuserData(null);
//	}
//	controller.setMainPageController(this);
//
//	Stage stage = new Stage();
//	stage.setTitle("Edit User");
//	stage.initModality(Modality.APPLICATION_MODAL);
//	stage.initStyle(StageStyle.UNDECORATED);
//
//	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
//    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 400) / 2;
//    stage.setX(centerX);
//    stage.setY(centerY);
//	
//	stage.setScene(new Scene(root));
//	stage.showAndWait();
//} catch (IOException e) {
//	e.printStackTrace();
//}
