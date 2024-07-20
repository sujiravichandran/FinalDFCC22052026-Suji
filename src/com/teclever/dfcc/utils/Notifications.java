package com.teclever.dfcc.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Notifications {
	public static void showSuccessAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void showErrorAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void showWarningAlert(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	 public static void showConfirmationDialog(String title, String contentText, Runnable onConfirm) {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(contentText);

	        ButtonType buttonTypeYes = new ButtonType("Yes");
	        ButtonType buttonTypeNo = new ButtonType("No");

	        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

	        alert.showAndWait().ifPresent(buttonType -> {
	            if (buttonType == buttonTypeYes) {
	                onConfirm.run();
	            }
	        });
	    }
}
