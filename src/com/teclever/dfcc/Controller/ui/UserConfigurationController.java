package com.teclever.dfcc.Controller.ui;

import java.io.IOException;

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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class UserTableViewFactory implements TableViewFactory<User> {

	@Override
	public CustomTableView<User> createTableView(ObservableList<User> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, User.class, addUserColumn, addCheckboxColumn);
	}

}

public class UserConfigurationController {
	@FXML
	private Pane displayTable;
	@FXML
	private TextArea checkerTextArea;

	@FXML
	private AnchorPane centerpane;

	UserManagementModule userManagement = new UserManagementModule();

	public void refreshUserList() {
		initialize();
	}

	public void initialize() {
		UserGetAllResponse userList = userManagement.getAllUsers();
		ObservableList<User> data = FXCollections.observableArrayList();
		if (userList.getResponse().getResponseCode() != 0) {
			for (UserLoginDetailsDto user : userList.getUserList()) {
				User userData = new User();
				userData.setUserId(user.getUserId());
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

//		customTableView.addEventHandler(CustomTableView.COLUMN_BUTTON_CLICKED_EVENT, event -> {
//			handleAddEditButtonClicked(null);
//		});

		customTableView.setPrefWidth(1617);
		customTableView.setPrefHeight(793);
		displayTable.setStyle("-fx-background-color: #ffffff;");

		displayTable.getChildren().add(customTableView);
	}

	private void handleAddEditButtonClicked(User userData) {
//		try {
//			FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/AddUser.fxml"));
//			Parent root = addUserPopup.load();
//
//			AddUserController controller = addUserPopup.getController();
//			if (userData != null) {
//				controller.setuserData(userData.getUserId());
//			} else {
//				controller.setuserData(null);
//			}
//			controller.setMainPageController(this);
//
//			Stage stage = new Stage();
//			stage.setTitle("Edit User");
//			stage.initModality(Modality.APPLICATION_MODAL);
//			stage.initStyle(StageStyle.UNDECORATED);
//			stage.setScene(new Scene(root));
//			stage.showAndWait();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
				deleteUser(userData.getUserId());
			}
		});
	}

	private void deleteUser(String userId) {
		Response response = userManagement.deleteUser(userId);
		if (response.getResponseCode() != 0) {
			Notifications.showSuccessAlert("User deleted");
			initialize();
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}

	}

}
