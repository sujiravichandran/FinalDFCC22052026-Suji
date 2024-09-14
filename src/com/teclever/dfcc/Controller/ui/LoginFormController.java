package com.teclever.dfcc.Controller.ui;

import java.util.List;

import org.hibernate.internal.build.AllowSysOut;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SingleChecksum;
import com.teclever.datastore.response.SingleChecksumResponse;
import com.teclever.datastore.service.SingleChecksumService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.LoginResponse;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
import com.teclever.dfcc.datastore.filemanagement.ChecksumManagement;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginFormController {

	private GridPane loginConatinerGridPane = new GridPane();
	private GridPane loginGridPane = new GridPane();
	private final SystemConfigManagement systemConfigManagement = new SystemConfigManagement();
	private final UserManagementModule userManagementModule = new UserManagementModule();
	TopContainerAfterLoginController topContainerAfterLoginController = new TopContainerAfterLoginController();
	ChecksumManagement checksumManagement = new ChecksumManagement();

	Boolean checkSumFinalResult = true;

	public GridPane createLoginForm() {
		loginConatinerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(6);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(44);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(44);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(6);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(70);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(15);

		loginConatinerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		loginConatinerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		loginConatinerGridPane.add(createLoginpage(), 2, 1);

		return loginConatinerGridPane;

	}

	private GridPane createLoginpage() {
		loginGridPane.setVgap(5);
		loginGridPane.getStyleClass().add("login-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(10);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(80);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(10);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(20);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(15);
		RowConstraints fifthRow = new RowConstraints();
		fifthRow.setPercentHeight(5);
		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(15);
		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(10);

		loginGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		loginGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fifthRow, sixthRow,
				seventhRow);

		GridPane loginTitleGrid = new GridPane();
		ColumnConstraints loginTitleColumnm = new ColumnConstraints();
		loginTitleColumnm.setPercentWidth(50);
		ColumnConstraints loginLogoColumn = new ColumnConstraints();
		loginLogoColumn.setPercentWidth(50);
		RowConstraints loginTitleRow = new RowConstraints();
		loginTitleRow.setPercentHeight(100);
		loginTitleGrid.getColumnConstraints().addAll(loginTitleColumnm, loginLogoColumn);
		loginTitleGrid.getRowConstraints().addAll(loginTitleRow);
		Label loginLabel = new Label("Login");
		loginLabel.getStyleClass().add("login-title");

		StackPane loginLogoStackPane = new StackPane();
		ImageView loginLogo = new ImageView();
		Image image = new Image(DFCCConstant.JARSTRING + "/Resources/Images/login_title.png");
		loginLogo.setImage(image);
		loginLogo.setFitWidth(180);
		loginLogo.setFitHeight(120);
		loginLogoStackPane.getChildren().add(loginLogo);
		loginLogoStackPane.setAlignment(Pos.CENTER_RIGHT);

		loginTitleGrid.add(loginLabel, 0, 0);
		loginTitleGrid.add(loginLogoStackPane, 1, 0);

		GridPane userNameGrid = new GridPane();
		ColumnConstraints userNameColumn = new ColumnConstraints();
		userNameColumn.setPercentWidth(100);
		RowConstraints userNameRow = new RowConstraints();
		userNameRow.setPercentHeight(100);
		userNameGrid.getColumnConstraints().addAll(userNameColumn);
		userNameGrid.getRowConstraints().addAll(userNameRow);
		VBox userNameBox = new VBox(5);
		Label userNameLabel = new Label("User name");
		userNameLabel.getStyleClass().add("login-label");
		TextField userNameTextField = new TextField();
		userNameTextField.getStyleClass().add("login-input");
		userNameBox.getChildren().addAll(userNameLabel, userNameTextField);
		userNameGrid.add(userNameBox, 0, 0);

		GridPane passwordGrid = new GridPane();
		ColumnConstraints passwordColumn = new ColumnConstraints();
		passwordColumn.setPercentWidth(100);
		RowConstraints passwordRow = new RowConstraints();
		passwordRow.setPercentHeight(100);
		passwordGrid.getColumnConstraints().addAll(passwordColumn);
		passwordGrid.getRowConstraints().addAll(passwordRow);
		VBox passwordBox = new VBox(5);
		Label passwordLabel = new Label("Password");
		passwordLabel.getStyleClass().add("login-label");
		PasswordField passwordHideField = new PasswordField();
		passwordHideField.getStyleClass().add("login-input");

		TextField passwordTextField = new TextField();
		passwordTextField.getStyleClass().add("login-input");
		passwordBox.getChildren().addAll(passwordLabel, passwordHideField);
		passwordGrid.add(passwordBox, 0, 0);

		GridPane checkboxGrid = new GridPane();
		ColumnConstraints checkboxColumn = new ColumnConstraints();
		checkboxColumn.setPercentWidth(100);
		ColumnConstraints checkboxColumn1 = new ColumnConstraints();
		checkboxColumn1.setPercentWidth(100);
		RowConstraints checkboxRow = new RowConstraints();
		checkboxRow.setPercentHeight(100);
		checkboxGrid.getColumnConstraints().addAll(checkboxColumn, checkboxColumn1);
		checkboxGrid.getRowConstraints().addAll(checkboxRow);
		CheckBox rememberMeCheckbox = new CheckBox("Remember Me");
		rememberMeCheckbox.getStyleClass().add("checkbox-label");
		CheckBox showPasswordCheckbox = new CheckBox("Show Password");
		showPasswordCheckbox.getStyleClass().add("checkbox-label");
		checkboxGrid.add(rememberMeCheckbox, 0, 0);
		checkboxGrid.add(showPasswordCheckbox, 1, 0);
		checkboxColumn1.setHalignment(HPos.RIGHT);

		showPasswordCheckbox.setOnAction(event -> {
			if (showPasswordCheckbox.isSelected()) {
				passwordTextField.setText(passwordHideField.getText());
				passwordBox.getChildren().remove(passwordHideField);
				passwordBox.getChildren().add(passwordTextField);
			} else {
				passwordHideField.setText(passwordTextField.getText());
				passwordBox.getChildren().remove(passwordTextField);
				passwordBox.getChildren().add(passwordHideField);
			}
		});

		GridPane loginButtonGrid = new GridPane();
		loginButtonGrid.setVgap(20);
		ColumnConstraints loginButtonColumn = new ColumnConstraints();
		loginButtonColumn.setPercentWidth(100);
		RowConstraints loginButtonEmptyRow = new RowConstraints();
		loginButtonEmptyRow.setPercentHeight(10);
		RowConstraints loginButtonRow = new RowConstraints();
		loginButtonRow.setPercentHeight(90);
		loginButtonGrid.getColumnConstraints().addAll(loginButtonColumn);
		loginButtonGrid.getRowConstraints().addAll(loginButtonEmptyRow, loginButtonRow);

		VBox loginButtonBox = new VBox();
		Label loginButton = new Label("Login");
		loginButtonBox.getStyleClass().add("login-btn-box");
		loginButtonBox.setAlignment(Pos.CENTER);
		loginButton.getStyleClass().add("login-btn");
		loginButtonBox.getChildren().add(loginButton);

		loginButtonGrid.add(loginButtonBox, 0, 1);

		loginGridPane.add(loginTitleGrid, 1, 1);
		loginGridPane.add(userNameGrid, 1, 2);
		loginGridPane.add(passwordGrid, 1, 3);
		loginGridPane.add(checkboxGrid, 1, 4);
		loginGridPane.add(loginButtonGrid, 1, 5);

		loginButtonBox.setOnMouseClicked(e -> {

			String userName = userNameTextField.getText();
			String password = null;

			if (showPasswordCheckbox.isSelected()) {
				password = passwordTextField.getText();
			} else {
				password = passwordHideField.getText();
			}

			if (userName.isEmpty()) {
				Notifications.showWarningAlert("Please Enter Username...");
				return;
			} else if (password.isEmpty()) {
				Notifications.showWarningAlert("Please Enter Password...");
				return;
			}

			LoginResponse loginResponse = userManagementModule.validateUser(userName, password);

			if (loginResponse.getResponse().getResponseCode() == 101) {
				handleBelAdminPasswordChange();
			} else if (loginResponse.getResponse().getResponseCode() == 1) {

				UserData.setRoleId(loginResponse.getRoleId());
				if (loginResponse.getUserId() != null) {
					UserData.setUserId(loginResponse.getUserId());
					currentSessionDetails.setUserId(loginResponse.getUserId());
				}

				GridPane mainContainerGridPane = (GridPane) loginGridPane.getParent().getParent().getParent();
				mainContainerGridPane.getRowConstraints().get(0).setPercentHeight(10);
				mainContainerGridPane.getRowConstraints().get(1).setPercentHeight(90);

				ObservableList<Node> childrenToRemove = FXCollections.observableArrayList();
				for (Node child : mainContainerGridPane.getChildren()) {
					Integer rowIndex = GridPane.getRowIndex(child);
					if (rowIndex != null && rowIndex == 0) {
						childrenToRemove.add(child);
					}
				}

				mainContainerGridPane.getChildren().removeAll(childrenToRemove);
				mainContainerGridPane.add(topContainerAfterLoginController.createTopGridPane(), 0, 0);

				Parent parent = loginGridPane.getParent();
				if (parent instanceof GridPane) {
					StackPane parent1 = (StackPane) parent.getParent();
					parent1.getChildren().clear();

					if (loginResponse.getRoleId().equals("RL_ID_1") || loginResponse.getRoleId().equals("RL_ID_2")) {
						AdminDashboardController adminDashboardController = new AdminDashboardController();
						parent1.getChildren().add(adminDashboardController.createAdminDashboard());
					} else if (loginResponse.getRoleId().equals("RL_ID_3")
							|| loginResponse.getRoleId().equals("RL_ID_4")) {

//						SessionCreationController sessionCreationController=new SessionCreationController();
//						parent1.getChildren().add(sessionCreationController.createSession());			

						/* To Run the Script File please uncomment below */

						// running script File
						if (DFCCConstant.isJarBuild) {
							SingleChecksumService a = new SingleChecksumService();
							SingleChecksumResponse res = a.getData();
							for (SingleChecksum s : res.getSingleChecksums()) {
								StateMachine.setScriptFileLocation(s.getScriptFileLocation());
							}
							checksumManagement.selectUserScriptFile(StateMachine.getScriptFileLocation());
						}
//						SingleChecksumService a = new SingleChecksumService();
//						SingleChecksumResponse res = a.getData();
//						for (SingleChecksum s : res.getSingleChecksums()) {
//							StateMachine.setScriptFileLocation(s.getScriptFileLocation());
//						}
//						checksumManagement.selectUserScriptFile(StateMachine.getScriptFileLocation());

						parent1.getChildren().add(getCheckSumDataUserLogin());

					}
				}
			} else {
				Notifications.showErrorAlert(loginResponse.getResponse().getResponseMessage());
			}
		});

		userNameTextField.setText("BelUser");
		passwordHideField.setText("Admin@123");

		return loginGridPane;
	}

	public void handleBelAdminPasswordChange() {
		GridPane passwordChangePage = createPasswordChangePage();

		StackPane overlay = new StackPane();
		overlay.setStyle(
				"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
		overlay.getChildren().add(passwordChangePage);

		StackPane root = (StackPane) loginGridPane.getParent().getParent();
		root.getChildren().add(overlay);
	}

	private GridPane createPasswordChangePage() {
		GridPane passwordChangePage = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(35);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(35);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(28);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(44);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(28);

		passwordChangePage.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		passwordChangePage.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		VBox changeAdminPasswordBVBox = new VBox(10);
		changeAdminPasswordBVBox.getStyleClass().add("admin-password-change-popup");
		changeAdminPasswordBVBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox titleHBox = new HBox(5);
		titleHBox.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("Change Password");
		titleLabel.getStyleClass().add("popup-title");
		titleHBox.getChildren().add(titleLabel);

		VBox newPasswordVBox = new VBox(10);
		newPasswordVBox.setAlignment(Pos.CENTER_LEFT);
		Label newPasswordLabel = new Label("New Password");
		newPasswordLabel.getStyleClass().add("popup-label");
		PasswordField newPasswordField = new PasswordField();
		newPasswordField.getStyleClass().add("popup-input");
		newPasswordVBox.getChildren().addAll(newPasswordLabel, newPasswordField);

		VBox confirmPasswordVBox = new VBox(10);
		confirmPasswordVBox.setAlignment(Pos.CENTER_LEFT);
		Label confirmPasswordLabel = new Label("Confirm Password");
		confirmPasswordLabel.getStyleClass().add("popup-label");
		PasswordField confirmPasswordField = new PasswordField();
		confirmPasswordField.getStyleClass().add("popup-input");
		confirmPasswordVBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

		CheckBox showPasswordCheckBox = new CheckBox("Show Password");
		showPasswordCheckBox.getStyleClass().add("show-password-checkbox");

		SimpleBooleanProperty showPassword = new SimpleBooleanProperty();
		SimpleStringProperty newPasswordText = new SimpleStringProperty();
		SimpleStringProperty confirmPasswordText = new SimpleStringProperty();

		newPasswordField.textProperty().bindBidirectional(newPasswordText);
		confirmPasswordField.textProperty().bindBidirectional(confirmPasswordText);

		showPasswordCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
			if (isSelected) {
				TextField newPasswordTextField = new TextField();
				newPasswordTextField.getStyleClass().add("popup-input");
				newPasswordTextField.setText(newPasswordField.getText());
				newPasswordTextField.textProperty().bindBidirectional(newPasswordText);
				newPasswordVBox.getChildren().set(1, newPasswordTextField);

				TextField confirmPasswordTextField = new TextField();
				confirmPasswordTextField.getStyleClass().add("popup-input");
				confirmPasswordTextField.setText(confirmPasswordField.getText());
				confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordText);
				confirmPasswordVBox.getChildren().set(1, confirmPasswordTextField);
			} else {
				PasswordField newPasswordPasswordField = new PasswordField();
				newPasswordPasswordField.getStyleClass().add("popup-input");
				newPasswordPasswordField.setText(newPasswordText.get());
				newPasswordPasswordField.textProperty().bindBidirectional(newPasswordText);
				newPasswordVBox.getChildren().set(1, newPasswordPasswordField);

				PasswordField confirmPasswordPasswordField = new PasswordField();
				confirmPasswordPasswordField.getStyleClass().add("popup-input");
				confirmPasswordPasswordField.setText(confirmPasswordText.get());
				confirmPasswordPasswordField.textProperty().bindBidirectional(confirmPasswordText);
				confirmPasswordVBox.getChildren().set(1, confirmPasswordPasswordField);
			}
		});

		HBox buttomBox = new HBox(20);
		buttomBox.setAlignment(Pos.CENTER);
		Button exitButton = new Button("Exit");
		Button updateButton = new Button("Update Password");
		exitButton.getStyleClass().add("popup-btn");
		updateButton.getStyleClass().add("popup-btn");
		buttomBox.getChildren().addAll(updateButton, exitButton);

		changeAdminPasswordBVBox.getChildren().addAll(titleHBox, newPasswordVBox, confirmPasswordVBox,
				showPasswordCheckBox, buttomBox);

		exitButton.setOnAction(e -> Platform.exit());
		updateButton.setOnAction(e -> {
			String newPassword = newPasswordText.get();
			String confirmPassword = confirmPasswordText.get();

			if (newPassword.isEmpty()) {
				Notifications.showWarningAlert("Please Enter New Password...");
				return;
			} else if (confirmPassword.isEmpty()) {
				Notifications.showWarningAlert("Please Enter Confirm Password...");
				return;
			} else if (!newPassword.equals(confirmPassword)) {
				Notifications.showErrorAlert("Password and Confirm Password Mismatched...");
				return;
			} else if (confirmPassword.length() < 8) {
				Notifications.showErrorAlert("Password must be at least 8 characters long...");
				return;
			} else if (!confirmPassword.matches(".*[a-z].*")) {
				Notifications.showErrorAlert("Password must contain at least one lowercase letter...");
				return;
			} else if (!confirmPassword.matches(".*[A-Z].*")) {
				Notifications.showErrorAlert("Password must contain at least one uppercase letter...");
				return;
			} else if (!confirmPassword.matches(".*\\d.*")) {
				Notifications.showErrorAlert("Password must contain at least one digit...");
				return;
			} else if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
				Notifications.showErrorAlert("Password must contain at least one special character...");
				return;
			} else {
				Response response = userManagementModule.changePasswordForBellAdmin(confirmPassword);
				if (response.getResponseCode() == 1) {
					GridPane mainContainerGridPane = (GridPane) loginGridPane.getParent().getParent().getParent();
					mainContainerGridPane.getRowConstraints().get(0).setPercentHeight(10);
					mainContainerGridPane.getRowConstraints().get(1).setPercentHeight(90);

					ObservableList<Node> childrenToRemove = FXCollections.observableArrayList();
					for (Node child : mainContainerGridPane.getChildren()) {
						Integer rowIndex = GridPane.getRowIndex(child);
						if (rowIndex != null && rowIndex == 0) {
							childrenToRemove.add(child);
						}
					}

					mainContainerGridPane.getChildren().removeAll(childrenToRemove);
					mainContainerGridPane.add(topContainerAfterLoginController.createTopGridPane(), 0, 0);

					Parent parent = loginGridPane.getParent();
					if (parent instanceof GridPane) {
						StackPane parent1 = (StackPane) parent.getParent();
						parent1.getChildren().clear();
						UserData.setRoleId("RL_ID_1");
						AdminDashboardController adminDashboardController = new AdminDashboardController();
						parent1.getChildren().add(adminDashboardController.createAdminDashboard());
					}
				}
			}
		});

		passwordChangePage.add(changeAdminPasswordBVBox, 1, 1);

		return passwordChangePage;
	}

	private GridPane createPasswordChangePage1() {
		GridPane passwordChangePage = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(35);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(35);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(30);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(40);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(30);

		passwordChangePage.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		passwordChangePage.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		VBox changeAdminPasswordBVBox = new VBox(10);
		changeAdminPasswordBVBox.getStyleClass().add("admin-password-change-popup");
		changeAdminPasswordBVBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox titleHBox = new HBox(5);
		titleHBox.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("Change Password");
		titleLabel.getStyleClass().add("popup-title");
		titleHBox.getChildren().add(titleLabel);

		VBox newPasswordVBox = new VBox(10);
		newPasswordVBox.setAlignment(Pos.CENTER_LEFT);
		Label newPasswordLabel = new Label("New Password");
		newPasswordLabel.getStyleClass().add("popup-label");
		TextField newPasswordTextField = new TextField();
		newPasswordTextField.getStyleClass().add("popup-input");
		newPasswordVBox.getChildren().addAll(newPasswordLabel, newPasswordTextField);

		VBox confirmPasswordVBox = new VBox(10);
		confirmPasswordVBox.setAlignment(Pos.CENTER_LEFT);
		Label confirmPasswordLabel = new Label("Confirm Password");
		confirmPasswordLabel.getStyleClass().add("popup-label");
		TextField confirmPasswordTextField = new TextField();
		confirmPasswordTextField.getStyleClass().add("popup-input");
		newPasswordVBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordTextField);

		HBox buttomBox = new HBox(20);
		buttomBox.setAlignment(Pos.CENTER);
		Button exitButton = new Button("Exit");
		Button updateButton = new Button("Update Password");
		exitButton.getStyleClass().add("popup-btn");
		updateButton.getStyleClass().add("popup-btn");
		buttomBox.getChildren().addAll(exitButton, updateButton);

		changeAdminPasswordBVBox.getChildren().addAll(titleHBox, newPasswordVBox, confirmPasswordVBox, buttomBox);

		exitButton.setOnAction(e -> Platform.exit());
		updateButton.setOnAction(e -> {
			String newPassword = newPasswordTextField.getText();
			String confirmPassword = confirmPasswordTextField.getText();

			if (newPassword.isEmpty()) {
				Notifications.showWarningAlert("Please Enter New Password...");
				return;
			} else if (confirmPassword.isEmpty()) {
				Notifications.showWarningAlert("Please Enter Confirm Password...");
				return;
			} else if (!newPassword.equals(confirmPassword)) {
				Notifications.showErrorAlert("Password and Confirm Password Mismatched...");
				return;
			} else if (confirmPassword.length() < 8) {
				Notifications.showErrorAlert("Password must be at least 8 characters long...");
				return;
			} else if (!confirmPassword.matches(".*[a-z].*")) {
				Notifications.showErrorAlert("Password must contain at least one lowercase letter...");
				return;
			} else if (!confirmPassword.matches(".*[A-Z].*")) {
				Notifications.showErrorAlert("Password must contain at least one uppercase letter...");
				return;
			} else if (!confirmPassword.matches(".*\\d.*")) {
				Notifications.showErrorAlert("Password must contain at least one digit...");
				return;
			} else if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
				Notifications.showErrorAlert("Password must contain at least one special character...");
				return;
			} else {
				Response response = userManagementModule.changePasswordForBellAdmin(confirmPassword);
				if (response.getResponseCode() == 1) {

					GridPane mainContainerGridPane = (GridPane) loginGridPane.getParent().getParent().getParent();
					mainContainerGridPane.getRowConstraints().get(0).setPercentHeight(10);
					mainContainerGridPane.getRowConstraints().get(1).setPercentHeight(90);

					ObservableList<Node> childrenToRemove = FXCollections.observableArrayList();
					for (Node child : mainContainerGridPane.getChildren()) {
						Integer rowIndex = GridPane.getRowIndex(child);
						if (rowIndex != null && rowIndex == 0) {
							childrenToRemove.add(child);
						}
					}

					mainContainerGridPane.getChildren().removeAll(childrenToRemove);
					mainContainerGridPane.add(topContainerAfterLoginController.createTopGridPane(), 0, 0);
					Parent parent = loginGridPane.getParent();
					if (parent instanceof GridPane) {
						StackPane parent1 = (StackPane) parent.getParent();
						parent1.getChildren().clear();
						UserData.setRoleId("RL_ID_1");
						AdminDashboardController adminDashboardController = new AdminDashboardController();
						parent1.getChildren().add(adminDashboardController.createAdminDashboard());
					}
				}
			}
		});

		passwordChangePage.add(changeAdminPasswordBVBox, 1, 1);

		return passwordChangePage;
	}

	public GridPane getCheckSumData() {
		ValidateResponse checkSumData = systemConfigManagement.validateConfig();
		List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
		ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

		if (checkSumData.getResponse().getResponseCode() == 0) {
			Notifications.showErrorAlert(checkSumData.getResponse().getResponseMessage());
			Platform.exit();
		} else if (checkSumData.getResponse().getResponseCode() == 1) {
			for (CheckSum data : checkSumDataList) {
				CheckSumList checkSumUiDto = new CheckSumList();
				checkSumUiDto.setFileName(data.getFile());
				checkSumUiDto.setCheckSumValue(data.getChecksumValue());
				checkSumUiDto.setStatus(data.getMsg());
				checkSumTableData.add(checkSumUiDto);
			}
		}

		GridPane newGridPane = new GridPane();
		newGridPane.setStyle(
				"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
		setupGridPane(newGridPane);

		VBox checkSumDataBox = createCheckSumDataBox(checkSumTableData);

		newGridPane.add(checkSumDataBox, 1, 1);

		return newGridPane;
	}

	public GridPane getCheckSumDataUserLogin() {

		if (DFCCConstant.isJarBuild) {

			ValidateResponse response = checksumManagement.compare();

			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (response.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(response.getResponse().getResponseMessage());
				Platform.exit();
			} else if (response.getResponse().getResponseCode() == 1) {
				for (CheckSum data : response.getCheckSumList()) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBoxUserLogin(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		} else {
			ValidateResponse checkSumData = systemConfigManagement.validateConfig();
			List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (checkSumData.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(checkSumData.getResponse().getResponseMessage());
				Platform.exit();
			} else if (checkSumData.getResponse().getResponseCode() == 1) {
				for (CheckSum data : checkSumDataList) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBoxUserLogin(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		}

		/* To Run the Script File please uncomment below */

//		ValidateResponse response = checksumManagement.compare();
//
//		
//		ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();
//
//		if (response.getResponse().getResponseCode() == 0) {
//			Notifications.showErrorAlert(response.getResponse().getResponseMessage());
//			Platform.exit();
//		} else if (response.getResponse().getResponseCode() == 1) {
//			for (CheckSum data : response.getCheckSumList()) {
//				CheckSumList checkSumUiDto = new CheckSumList();
//				checkSumUiDto.setFileName(data.getFile());
//				checkSumUiDto.setCheckSumValue(data.getChecksumValue());
//				checkSumUiDto.setStatus(data.getMsg());
//				checkSumTableData.add(checkSumUiDto);
//			}
//		}
//
//		GridPane newGridPane = new GridPane();
//		newGridPane.setStyle(
//				"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
//		setupGridPane(newGridPane);
//
//		VBox checkSumDataBox = createCheckSumDataBoxUserLogin(checkSumTableData);
//
//		newGridPane.add(checkSumDataBox, 1, 1);
//
//		return newGridPane;
	}

	private void setupGridPane(GridPane gridPane) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(2);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(96);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(2);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(5);

		gridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		gridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
	}

	private VBox createCheckSumDataBoxUserLogin(ObservableList<CheckSumList> checkSumTableData) {
		VBox checkSumDataBox = new VBox(10);
		checkSumDataBox.setAlignment(Pos.CENTER);
		checkSumDataBox.getStyleClass().add("check-sum-data-box");
		checkSumDataBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox checkSumTitleBox = new HBox();
		checkSumTitleBox.setAlignment(Pos.CENTER);
		Label checkSumLabel = new Label("CheckSum Details");
		checkSumLabel.getStyleClass().add("check-sum-title");
		checkSumTitleBox.getChildren().add(checkSumLabel);

		TableView<CheckSumList> tableView = createTableView(checkSumTableData);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		Button okButton = new Button("OK");
		okButton.getStyleClass().add("check-sum-ok-btn");
		buttonBox.getChildren().add(okButton);

		okButton.setOnAction(e -> {
			if (checkSumFinalResult) {
				Parent parent = checkSumDataBox.getParent();
				if (parent instanceof GridPane) {
					StackPane parent1 = (StackPane) parent.getParent();
					parent1.getChildren().remove(parent);
					SessionCreationOptionController sessionCreationOptionController = new SessionCreationOptionController();
					parent1.getChildren().add(sessionCreationOptionController.createSessionOption());
				}
			} else {
				Platform.exit();
			}
		});

		checkSumDataBox.getChildren().addAll(checkSumTitleBox, tableView, buttonBox);

		return checkSumDataBox;
	}

	private VBox createCheckSumDataBox(ObservableList<CheckSumList> checkSumTableData) {
		VBox checkSumDataBox = new VBox(10);
		checkSumDataBox.setAlignment(Pos.CENTER);
		checkSumDataBox.getStyleClass().add("check-sum-data-box");
		checkSumDataBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox checkSumTitleBox = new HBox();
		checkSumTitleBox.setAlignment(Pos.CENTER);
		Label checkSumLabel = new Label("CheckSum Details");
		checkSumLabel.getStyleClass().add("check-sum-title");
		checkSumTitleBox.getChildren().add(checkSumLabel);

		TableView<CheckSumList> tableView = createTableView(checkSumTableData);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		Button okButton = new Button("OK");
		okButton.getStyleClass().add("check-sum-ok-btn");
		buttonBox.getChildren().add(okButton);

		okButton.setOnAction(e -> {
			if (checkSumFinalResult) {
				Parent parent = checkSumDataBox.getParent();
				if (parent instanceof GridPane) {
					StackPane parent1 = (StackPane) parent.getParent();
					parent1.getChildren().remove(parent);
				}
			} else {
				Platform.exit();
			}
		});

		checkSumDataBox.getChildren().addAll(checkSumTitleBox, tableView, buttonBox);

		return checkSumDataBox;
	}

	private TableView<CheckSumList> createTableView(ObservableList<CheckSumList> checkSumTableData) {
		TableView<CheckSumList> tableView = new TableView<>();
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tableView.setPrefHeight(900);

		TableColumn<CheckSumList, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> checkSumValueColumn = new TableColumn<>("CheckSum Value");
		checkSumValueColumn.setCellValueFactory(new PropertyValueFactory<>("checkSumValue"));
		checkSumValueColumn.setReorderable(false);
		checkSumValueColumn.setSortable(false);
		checkSumValueColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		setupStatusColumn(statusColumn);

		tableView.getColumns().addAll(fileNameColumn, checkSumValueColumn, statusColumn);
		tableView.setItems(checkSumTableData);

		return tableView;
	}

	private void setupStatusColumn(TableColumn<CheckSumList, String> statusColumn) {
		statusColumn.setReorderable(false);
		statusColumn.setSortable(false);
		statusColumn.setCellFactory(column -> new TableCell<CheckSumList, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("No File".equalsIgnoreCase(item)) {
						setText("No File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("Extra File".equalsIgnoreCase(item)) {
						setText("Extra File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}
}
