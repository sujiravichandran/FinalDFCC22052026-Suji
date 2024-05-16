package com.teclever.dfcc.Controller.ui;

import java.io.ByteArrayInputStream;
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
import com.teclever.dfcc.model.User;
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

	byte[] imageData;

	private UserConfigurationController mainPageController;

	private String roleId;

	public void setMainPageController(UserConfigurationController mainPageController) {
		this.mainPageController = mainPageController;
	}

	public void setuserData(String userId) {
		System.out.println("Session userData: " + userId);
		createAddUserPopup(userId);
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
//		addUserMainContainer.getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/AddUser.css").toExternalForm());
		addUserRoleType();
	}

	public void addUserRoleType() {
		userRoleType.put("BEL USER", "RL_ID_2");
		userRoleType.put("SQUADRAN ADMIN", "RL_ID_3");
		userRoleType.put("SQUADRAN USER", "RL_ID_4");
	}
	
	public <T> Image getUserImage(T item) {
        if (item instanceof User) {
            User user = (User) item;
            Blob digitalSignatureBlob = user.getDigitalSignature();
            if (digitalSignatureBlob != null) {
                try (InputStream inputStream = digitalSignatureBlob.getBinaryStream()) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    Image image = new Image(byteArrayInputStream);
                    
                    return image;
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
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
		double preferredWidth = 100; // Set your preferred size
		double preferredHeight = 50;
		signImage.setFitWidth(preferredWidth);
		signImage.setFitHeight(preferredHeight);

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

		buttonContainerBox.getChildren().addAll(addButton, cancelButton);

		if (userId != null) {
			heading.setText("Update User");
			userTypeBox.setVisible(false);
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
			double preferredWidth = 100; // Set your preferred size
			double preferredHeight = 50;
			signImage.setFitWidth(preferredWidth);
			signImage.setFitHeight(preferredHeight);
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
		userId = userDetails.getUserId();
		roleId = userDetails.getRoleId();
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
		addUserData.setRoleId(userRoleType.get(userTypeBox.getValue()));
		addUserData.setLoginName(userNameTextField.getText());
		addUserData.setPassword(confirmPasswordTextField.getText());
		try {
			addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		UserLoginDetailsDto addUserDetails = userManagement.addUser(addUserData);
		if (addUserDetails.getResponse().getResponseCode() == 1) {
			Notifications.showSuccessAlert(addUserDetails.getResponse().getResponseMessage());
			Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
			stage.close();
			mainPageController.refreshUserList();
		} else {
			Notifications.showErrorAlert(addUserDetails.getResponse().getResponseMessage());
		}
	}

	private void handleEditUserData(String userId) {
		UserLoginDetailsDto EditUserData = new UserLoginDetailsDto();
		EditUserData.setUserId(userId);
		EditUserData.setRoleId(roleId);
		EditUserData.setLoginName(userNameTextField.getText());
		EditUserData.setPassword(confirmPasswordTextField.getText());
		try {
			EditUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(imageData));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Response EditUserDetails = userManagement.updateUser(EditUserData);
		System.out.println();
		if (EditUserDetails.getResponseCode() == 1) {
			Notifications.showSuccessAlert(EditUserDetails.getResponseMessage());
			Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
			stage.close();
			mainPageController.refreshUserList();
		} else {
			Notifications.showErrorAlert(EditUserDetails.getResponseMessage());
		}

	}

	private void handleCancelButton() {
		Stage stage = (Stage) addUserMainContainer.getScene().getWindow();
		stage.close();
	}

}
