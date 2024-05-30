package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.dto.UserGetAllResponse;
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.model.User;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class UserTableViewFactory implements TableViewFactory<User> {
	@Override
	public CustomTableView<User> createTableView(ObservableList<User> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, User.class, addUserColumn, addCheckboxColumn);
	}
}




public class UserManagementController {

	private GridPane userManagementGridPane = new GridPane();
	private GridPane userManagementTitleGridPane = new GridPane();
	private GridPane userManagementTableGridPane = new GridPane();
	private GridPane addEditUserGridPane = new GridPane();
	private Map<String, String> userRoleType = new HashMap<>();
	private final UserManagementModule userManagement = new UserManagementModule();
	private byte[] imageData;
	
	public void addUserRoleType() {
		userRoleType.put("BEL USER", "RL_ID_2");
		userRoleType.put("SQUADRAN ADMIN", "RL_ID_3");
		userRoleType.put("SQUADRAN USER", "RL_ID_4");
	}
	
	public UserManagementController() {
	    addUserRoleType();
	}
	
	public void refreshUserList() {
		setTableData();
	}

	
	public GridPane createUserManagemenGridPane() {

		userManagementGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/UserManagement.css").toExternalForm());
		userManagementGridPane.getStyleClass().add("user-management-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		userManagementGridPane.setPadding(new Insets(10));
		userManagementGridPane.setVgap(5);
		userManagementGridPane.getColumnConstraints().addAll(firstColumn);
		userManagementGridPane.getRowConstraints().addAll(firstRow, secondRow);

		userManagementGridPane.add(createUserManagementTitleGridPane(), 0, 0);
		userManagementGridPane.add(createUserTable(), 0, 1);

		return userManagementGridPane;
	}

	private GridPane createUserManagementTitleGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		userManagementTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		userManagementTitleGridPane.getRowConstraints().addAll(firstRow);

		HBox titleBox = new HBox();
		titleBox.setAlignment(Pos.CENTER_LEFT);
		Label title = new Label("USER MANAGEMENT");
		title.getStyleClass().add("user-management-title");
		titleBox.getChildren().add(title);

		HBox addUserBox = new HBox();
		addUserBox.setAlignment(Pos.CENTER_RIGHT);
		Button addUserBtn = new Button("+ ADD USER");
		addUserBox.getChildren().add(addUserBtn);

		addUserBtn.setOnAction(e -> {
			handleAddEditButtonClicked(null);
		});

		userManagementTitleGridPane.add(titleBox, 0, 0);
		userManagementTitleGridPane.add(addUserBox, 1, 0);

		return userManagementTitleGridPane;
	}

	public GridPane createUserTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		userManagementTableGridPane.getColumnConstraints().addAll(firstColumn);
		userManagementTableGridPane.getRowConstraints().addAll(firstRow);

		setTableData();
		
		return userManagementTableGridPane;
	}
	
	public void setTableData() {
		UserGetAllResponse userList = userManagement.getAllUsers();
		ObservableList<User> data = FXCollections.observableArrayList();
		if (userList.getResponse().getResponseCode() != 0) {
			for (UserLoginDetailsDto user : userList.getUserList()) {
				User userData = new User();
				userData.setId(user.getUserId());
				userData.setUserName(user.getLoginName());
				userData.setRoleType(user.getRoleId());
				userData.setDigitalSignature(user.getDigitalSignature());
				data.add(userData);
			}
		}

		TableViewFactory<User> userFactory = new UserTableViewFactory();
		CustomTableView<User> customTableView = userFactory.createTableView(data, true, false);
		customTableView.addEventHandler(CustomTableView.EDIT_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<User> selectedItems = customTableView.getSelectedItems();
			for (User rowData : selectedItems) {
				handleAddEditButtonClicked(rowData);
			}
		});

		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<User> selectedItems = customTableView.getSelectedItems();
			for (User rowData : selectedItems) {
				handleDeleteButtonClicked(rowData);
			}
		});

		userManagementTableGridPane.add(customTableView, 0, 0);
	} 

	private void handleAddEditButtonClicked(User userData) {
		try {
			FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/AddUser.fxml"));
			Parent root = addUserPopup.load();

			AddUserController controller = addUserPopup.getController();
			if (userData != null) {
				controller.setuserData(userData.getId());
			} else {
				controller.setuserData(null);
			}
			controller.setMainPageController(this);

			Stage stage = new Stage();
			stage.setTitle("Edit User");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

//			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//		    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
//		    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 400) / 2;
//		    stage.setX(centerX);
//		    stage.setY(centerY);
			
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleDeleteButtonClicked(User userData) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete user: " + userData.getUserName() + "?");

		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		alert.showAndWait().ifPresent(buttonType -> {
			if (buttonType == buttonTypeYes) {
				deleteUser(userData.getId());
			}
		});
	}

	private void deleteUser(String userId) {
		Response response = userManagement.deleteUser(userId);
		if (response.getResponseCode() != 0) {
			Notifications.showSuccessAlert("User deleted");
			refreshUserList();			
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}

	}

}



//package com.teclever.dfcc.Controller.ui;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.imageio.ImageIO;
//
//import com.teclever.datastore.dto.Response;
//import com.teclever.dfcc.datastore.dto.UserGetAllResponse;
//import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
//import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
//import com.teclever.dfcc.model.User;
//import com.teclever.dfcc.utils.CustomTableView;
//import com.teclever.dfcc.utils.Notifications;
//import com.teclever.dfcc.utils.TableViewFactory;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.geometry.Rectangle2D;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Modality;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//
//class UserTableViewFactory implements TableViewFactory<User> {
//	@Override
//	public CustomTableView<User> createTableView(ObservableList<User> items, boolean addUserColumn,
//			boolean addCheckboxColumn) {
//		return new CustomTableView<>(items, User.class, addUserColumn, addCheckboxColumn);
//	}
//}
//
//
//
//
//public class UserManagementController {
//
//	private GridPane userManagementGridPane = new GridPane();
//	private GridPane userManagementTitleGridPane = new GridPane();
//	private GridPane userManagementTableGridPane = new GridPane();
//	private GridPane addEditUserGridPane = new GridPane();
//	private Map<String, String> userRoleType = new HashMap<>();
//	private final UserManagementModule userManagement = new UserManagementModule();
//	private byte[] imageData;
//	
//	public void addUserRoleType() {
//		userRoleType.put("BEL USER", "RL_ID_2");
//		userRoleType.put("SQUADRAN ADMIN", "RL_ID_3");
//		userRoleType.put("SQUADRAN USER", "RL_ID_4");
//	}
//	
//	public UserManagementController() {
//	    addUserRoleType();
//	}
//	
//	public void refreshUserList() {
//		createUserManagemenGridPane();
//	}
//
//	
//	public GridPane createUserManagemenGridPane() {
//
//		userManagementGridPane.getStylesheets()
//				.add(getClass().getResource("/com/teclever/dfcc/ui/css/UserManagement.css").toExternalForm());
//		userManagementGridPane.getStyleClass().add("user-management-container");
//		
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		userManagementGridPane.setPadding(new Insets(10));
//		userManagementGridPane.setVgap(5);
//		userManagementGridPane.getColumnConstraints().addAll(firstColumn);
//		userManagementGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		userManagementGridPane.add(createUserManagementTitleGridPane(), 0, 0);
//		userManagementGridPane.add(createUserTable(), 0, 1);
//
//		return userManagementGridPane;
//	}
//
//	private GridPane createUserManagementTitleGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		userManagementTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		userManagementTitleGridPane.getRowConstraints().addAll(firstRow);
//
//		HBox titleBox = new HBox();
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		Label title = new Label("USER MANAGEMENT");
//		title.getStyleClass().add("user-management-title");
//		titleBox.getChildren().add(title);
//
//		HBox addUserBox = new HBox();
//		addUserBox.setAlignment(Pos.CENTER_RIGHT);
//		Button addUserBtn = new Button("+ ADD USER");
//		addUserBtn.getStyleClass().add("add-user-btn");
//		addUserBox.getChildren().add(addUserBtn);
//
//		addUserBtn.setOnAction(e -> {
//			handleAddEditButtonClicked(null);
//		});
//
//		userManagementTitleGridPane.add(titleBox, 0, 0);
//		userManagementTitleGridPane.add(addUserBox, 1, 0);
//
//		return userManagementTitleGridPane;
//	}
//
//	private GridPane createUserTable() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		userManagementTableGridPane.getColumnConstraints().addAll(firstColumn);
//		userManagementTableGridPane.getRowConstraints().addAll(firstRow);
//
//		UserGetAllResponse userList = userManagement.getAllUsers();
//		ObservableList<User> data = FXCollections.observableArrayList();
//		if (userList.getResponse().getResponseCode() != 0) {
//			for (UserLoginDetailsDto user : userList.getUserList()) {
//				User userData = new User();
//				userData.setUserId(user.getUserId());
//				userData.setUserName(user.getLoginName());
//				userData.setRoleType(user.getRoleId());
//				userData.setDigitalSignature(user.getDigitalSignature());
//				data.add(userData);
//			}
//		}
//
//		TableViewFactory<User> userFactory = new UserTableViewFactory();
//		CustomTableView<User> customTableView = userFactory.createTableView(data, true, false);
//		customTableView.addEventHandler(CustomTableView.EDIT_BUTTON_CLICKED_EVENT, event -> {
//			ObservableList<User> selectedItems = customTableView.getSelectedItems();
//			for (User rowData : selectedItems) {
//				handleAddEditButtonClicked(rowData);
//			}
//		});
//
//		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
//			ObservableList<User> selectedItems = customTableView.getSelectedItems();
//			for (User rowData : selectedItems) {
//				handleDeleteButtonClicked(rowData);
//			}
//		});
//
//		userManagementTableGridPane.add(customTableView, 0, 0);
//
//		return userManagementTableGridPane;
//	}
//
//	private void handleAddEditButtonClicked(User userData) {
//		StackPane parentPane = (StackPane) userManagementGridPane.getParent();
//		parentPane.getChildren().add(addEditUserPopup(userData));
//	}
//
//	private void handleDeleteButtonClicked(User userData) {
//		Alert alert = new Alert(AlertType.CONFIRMATION);
//		alert.setTitle("Confirmation Dialog");
//		alert.setHeaderText(null);
//		alert.setContentText("Are you sure you want to delete user: " + userData.getUserName() + "?");
//
//		ButtonType buttonTypeYes = new ButtonType("Yes");
//		ButtonType buttonTypeNo = new ButtonType("No");
//
//		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
//
//		alert.showAndWait().ifPresent(buttonType -> {
//			if (buttonType == buttonTypeYes) {
//				deleteUser(userData.getUserId());
//			}
//		});
//	}
//
//	private void deleteUser(String userId) {
//		Response response = userManagement.deleteUser(userId);
//		if (response.getResponseCode() != 0) {
//			Notifications.showSuccessAlert("User deleted");
//			
//		} else {
//			Notifications.showErrorAlert(response.getResponseMessage());
//		}
//
//	}
//
//	private GridPane addEditUserPopup(User userData) {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(30);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(40);
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(30);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(80);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(10);
//
//		addEditUserGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
//		addEditUserGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//
//		VBox addEditUserVBox = new VBox(10);
//		addEditUserVBox.getStyleClass().add("add-edit-user-popup");
//		addEditUserVBox.getStylesheets()
//				.add(getClass().getResource("/com/teclever/dfcc/ui/css/UserManagement.css").toExternalForm());
//
//		HBox titleHBox = new HBox(5);
//		titleHBox.setAlignment(Pos.CENTER);
//		Label titleLabel = new Label("ADD USER");
//		titleLabel.getStyleClass().add("add-edit-title");
//		titleHBox.getChildren().add(titleLabel);
//		
//		HBox roleTypeBox = new HBox();
//		roleTypeBox.setAlignment(Pos.CENTER);
//		ComboBox<String> userRoleComboBox = new ComboBox<>();
//		userRoleComboBox.getStyleClass().add("add-user-role-box");
//		userRoleComboBox.setPromptText("Select User Role");
//		userRoleComboBox.getItems().addAll(userRoleType.keySet());
//		roleTypeBox.getChildren().add(userRoleComboBox);
//		
//		VBox userNameVBox = new VBox(5);
//		userNameVBox.setAlignment(Pos.CENTER_LEFT);
//		Label userNameLabel = new Label("User Name");
//		userNameLabel.getStyleClass().add("add-user-popup-label");
//		TextField userNameTextField = new TextField();
//		userNameTextField.getStyleClass().add("add-user-popup-input");
//		userNameVBox.getChildren().addAll(userNameLabel, userNameTextField);
//
//		VBox newPasswordVBox = new VBox(5);
//		newPasswordVBox.setAlignment(Pos.CENTER_LEFT);
//		Label newPasswordLabel = new Label("Password");
//		newPasswordLabel.getStyleClass().add("add-user-popup-label");
//		TextField newPasswordTextField = new TextField();
//		newPasswordTextField.getStyleClass().add("add-user-popup-input");
//		newPasswordVBox.getChildren().addAll(newPasswordLabel, newPasswordTextField);
//
//		VBox confirmPasswordVBox = new VBox(5);
//		confirmPasswordVBox.setAlignment(Pos.CENTER_LEFT);
//		Label confirmPasswordLabel = new Label("Confirm Password");
//		confirmPasswordLabel.getStyleClass().add("add-user-popup-label");
//		TextField confirmPasswordTextField = new TextField();
//		confirmPasswordTextField.getStyleClass().add("add-user-popup-input");
//		newPasswordVBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordTextField);
//
//		VBox digitalSignVBox = new VBox(5);
//		digitalSignVBox.setAlignment(Pos.CENTER_LEFT);
//		Label digitalSignLabel = new Label("Digital Signature");
//		digitalSignLabel.getStyleClass().add("add-user-popup-label");
//		HBox digitalSignaturePane = new HBox();
//		digitalSignaturePane.getStyleClass().add("add-user-sign-box");
//		digitalSignaturePane.setAlignment(Pos.CENTER);
//		ImageView digitalSignImage = new ImageView();
//		digitalSignImage.setPreserveRatio(true);
//		digitalSignImage.setFitHeight(150);
//		digitalSignImage.setFitWidth(150);
//		digitalSignaturePane.getChildren().add(digitalSignImage);
//		digitalSignVBox.getChildren().addAll(digitalSignLabel,digitalSignaturePane);
//		
//		HBox uploadButtomBox = new HBox();
//		uploadButtomBox.setAlignment(Pos.CENTER);
//		Button uploadButton = new Button("Upload");
//		uploadButton.getStyleClass().add("add-user-popup-btn");
//		uploadButtomBox.getChildren().addAll(uploadButton);
//		
//		HBox buttomBox = new HBox(50);
//		buttomBox.setAlignment(Pos.CENTER);
//		Button closeButton = new Button("Close");
//		Button updateButton = new Button("Add User");
//		closeButton.getStyleClass().add("add-user-popup-btn");
//		updateButton.getStyleClass().add("add-user-popup-btn");
//		buttomBox.getChildren().addAll(closeButton, updateButton);
//
//		if(userData == null) {
//			addEditUserVBox.getChildren().addAll(titleHBox, roleTypeBox, userNameVBox, newPasswordVBox, confirmPasswordVBox,digitalSignVBox,  uploadButtomBox, buttomBox);
//		}else if (!(userData.getUserId()).isEmpty()) {
//			titleLabel.setText("Update User");
//			newPasswordLabel.setText("New Password");
//			updateButton.setText("Update User");
//			setEditUserImage(userNameTextField, digitalSignImage,userData.getUserId());
//			addEditUserVBox.getChildren().addAll(titleHBox, userNameVBox, newPasswordVBox, confirmPasswordVBox,digitalSignVBox,  uploadButtomBox, buttomBox);
//		}
//
//		uploadButton.setOnAction(e -> uploadImage(digitalSignImage));
//		
//		closeButton.setOnAction(e ->{
//			StackPane parentPane = (StackPane) userManagementGridPane.getParent();
//			parentPane.getChildren().remove(addEditUserGridPane);
//		});
//		
//		
//		updateButton.setOnAction(e -> {
//			String role = userRoleComboBox.getValue();
//			String userName = userNameTextField.getText();
//			String newPassword = newPasswordTextField.getText();
//			String confirmPassword = confirmPasswordTextField.getText();
//			
//			if(role == null) {
//				Notifications.showWarningAlert("Please Select User Role...");
//				return;
//			}else if (userName.isEmpty()) {
//				Notifications.showWarningAlert("Please Enter User Name...");
//				return;
//			}else if (newPassword.isEmpty()) {
//				Notifications.showWarningAlert("Please Enter New Password...");
//				return;
//			} else if (confirmPassword.isEmpty()) {
//				Notifications.showWarningAlert("Please Enter Confirm Password...");
//				return;
//			} else if (imageData == null) {
//				Notifications.showErrorAlert("Please select digiatl signature...");
//				return;
//			} else if (!newPassword.equals(confirmPassword)) {
//				Notifications.showErrorAlert("Password and Confirm Password Mismatched...");
//				return;
//			}else if (confirmPassword.length() < 8) {
//				Notifications.showErrorAlert("Password must be at least 8 characters long...");
//				return;
//			} else if (!confirmPassword.matches(".*[a-z].*")) {
//				Notifications.showErrorAlert("Password must contain at least one lowercase letter...");
//				return;
//			} else if (!confirmPassword.matches(".*[A-Z].*")) {
//				Notifications.showErrorAlert("Password must contain at least one uppercase letter...");
//				return;
//			} else if (!confirmPassword.matches(".*\\d.*")) {
//				Notifications.showErrorAlert("Password must contain at least one digit...");
//				return;
//			} else if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
//				Notifications.showErrorAlert("Password must contain at least one special character...");
//				return;
//			} else {
//				if(userData == null) {
//					handleAddNewUser(role, userName, confirmPassword, imageData);
//				}else if (!(userData.getUserId()).isEmpty()) {
//					handleEditUserData(userData.getRoleType(), userData.getUserId(),userName,confirmPassword,imageData);
//				}
//			}
//		});
//
//		addEditUserGridPane.add(addEditUserVBox, 1, 1);
//
//		return addEditUserGridPane;
//	}
//	
//	private void uploadImage(ImageView digitalSignImage) {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Choose Image File");
//		fileChooser.getExtensionFilters()
//				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
//		File selectedFile = fileChooser.showOpenDialog(null);
//		if (selectedFile != null) {
//			long maxSizeBytes = 500 * 1024;
//			if (selectedFile.length() > maxSizeBytes) {
//				Notifications.showWarningAlert("Selected file exceeds the maximum allowed size (500KB).");
//				return;
//			}
//			Image image = new Image(selectedFile.toURI().toString());
//			digitalSignImage.setImage(image);
//			try {
//				imageData = Files.readAllBytes(selectedFile.toPath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//		
//		private void setEditUserImage(TextField userNameTextField, ImageView digitalSignImage, String userId) {
//			UserLoginDetailsDto userDetails = userManagement.getUserByUserId(userId);
//			Blob blob = userDetails.getDigitalSignature();
//			userNameTextField.setText(userDetails.getLoginName());
//			if (blob != null) {
//				try (InputStream is = blob.getBinaryStream()) {
//					Image image = new Image(is);
//					digitalSignImage.setImage(image);
//					imageData = convertImageToByteArray(image);
//				} catch (SQLException | IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		private byte[] convertImageToByteArray(Image image) {
//			byte[] imageData = null;
//			try {
//				File tempFile = File.createTempFile("temp", ".png");
//				tempFile.deleteOnExit();
//				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempFile);
//				imageData = Files.readAllBytes(tempFile.toPath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return imageData;
//		}
//		
//		private void handleAddNewUser(String role, String userName, String confirmPassword, byte[] image) {
//			UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
//			addUserData.setRoleId(role);
//			addUserData.setLoginName(userName);
//			addUserData.setPassword(confirmPassword);
//			try {
//				addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(image));
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//			UserLoginDetailsDto userDetails = userManagement.addUser(addUserData);
//			if (userDetails.getResponse().getResponseCode() == 1) {
//				Notifications.showSuccessAlert(userDetails.getResponse().getResponseMessage());
//				StackPane parentPane = (StackPane) userManagementGridPane.getParent();
//				parentPane.getChildren().remove(addEditUserGridPane);
//			} else {
//				Notifications.showErrorAlert(userDetails.getResponse().getResponseMessage());
//			}
//		}
//
//		private void handleEditUserData(String role,String userId, String userName, String confirmPassword, byte[] image) {
//			UserLoginDetailsDto addUserData = new UserLoginDetailsDto();
//			addUserData.setRoleId(role);
//			addUserData.setUserId(userId);
//			addUserData.setLoginName(userName);
//			addUserData.setPassword(confirmPassword);
//			try {
//				addUserData.setDigitalSignature(new javax.sql.rowset.serial.SerialBlob(image));
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//			UserLoginDetailsDto userDetails = userManagement.addUser(addUserData);
//			if (userDetails.getResponse().getResponseCode() == 1) {
//				Notifications.showSuccessAlert(userDetails.getResponse().getResponseMessage());
//				StackPane parentPane = (StackPane) userManagementGridPane.getParent();
//				parentPane.getChildren().remove(addEditUserGridPane);
//			} else {
//				Notifications.showErrorAlert(userDetails.getResponse().getResponseMessage());
//			}
//
//		}
//
//}