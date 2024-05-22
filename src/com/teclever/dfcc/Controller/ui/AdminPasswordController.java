package com.teclever.dfcc.Controller.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AdminPasswordController {
	public void createAdminPasswordPopup() {
		try {
			FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/AdminPassword.fxml"));
			Parent root = addUserPopup.load();
			Stage stage = new Stage();
			stage.setTitle("Change Password");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
		    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 200) / 2;
		    stage.setX(centerX);
		    stage.setY(centerY);
			
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
