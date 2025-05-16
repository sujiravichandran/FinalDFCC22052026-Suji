package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.Optional;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EndRemarksController {

	@FXML
	private Button cancelButton;

	@FXML
	private Pane endremarks;

	@FXML
	private Label headinglbl;

	@FXML
	private Label labelEndRemarks;

	@FXML
	private Button saveButton;

	@FXML
	private TextArea textEnterEndRemarsks;

	private static String userInput;

	SessionManagement sessionmanagement = new SessionManagement();

	private static final int MAX_CHAR_LIMIT = 50;

	@FXML
	public void initialize() {
		saveButton.setOnAction(e -> handleSaveButtonAction());
		cancelButton.setOnAction(e -> handleCancelButtonAction());
		textEnterEndRemarsks.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > MAX_CHAR_LIMIT) {
				textEnterEndRemarsks.setText(oldValue); // Revert to old value if limit exceeded
			}
		});
	}

	public void endSessionPopup() {
		try {
			FXMLLoader endRemarkPopup = new FXMLLoader(
					getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/EndRemarks.fxml"));
			Parent root = endRemarkPopup.load();

			EndRemarksController controller = endRemarkPopup.getController();

			Stage stage = new Stage();
			stage.setTitle("Edit User");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

			stage.setScene(new Scene(root));
			stage.showAndWait();

			userInput = controller.getUserInput();

			if (userInput != null && !userInput.trim().isEmpty()) {
				saveEndremarks();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getUserInput() {
		return userInput;
	}

	private void saveEndremarks() {
		AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement
				.getInstance();
		TestState currentState = StateMachine.getTestState();
		if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
				|| currentState == TestState.STOPPED) {
			// Show confirmation dialog before ending the session
			showEndRemarksDialog("Confirm End Session",
					"Are you sure you want to end the current session and close the application?", () -> {

						Response response = sessionmanagement.endSession(userInput, false);
						if (response.getResponseCode() == 1) {
							aitessProcessControlManagement.endAllProcessOnLogout();
							Platform.exit();
							System.exit(0);
						} else {
							Notifications.showErrorAlert(response.getResponseMessage());
						}
					});

		} else if (currentState == TestState.PAUSED || currentState == TestState.RUNNING) {
			Notifications.showWarningAlert("Please stop the " + StateMachine.getRunningTestName()
					+ " test before ending the current session.");
		}
	}

	private void showEndRemarksDialog(String title, String message, Runnable onConfirm) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);

			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No");

			alert.getButtonTypes().setAll(yesButton, noButton);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == yesButton) {
				onConfirm.run(); // Execute the confirm action
			}
		});
	}

	@FXML
	private void handleSaveButtonAction() {
	    userInput = textEnterEndRemarsks.getText();

	    if (userInput == null || userInput.trim().isEmpty()) {
	   
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.setTitle("Input Required");
	        alert.setHeaderText(null);
	        alert.setContentText("Please enter the end remarks to end the session.");
	        alert.showAndWait();
	        return;
	    }

	    Stage stage = (Stage) saveButton.getScene().getWindow();
	    stage.close();
	}

	@FXML
	private void handleCancelButtonAction() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();

	}

}
