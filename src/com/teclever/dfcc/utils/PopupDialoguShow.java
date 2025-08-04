package com.teclever.dfcc.utils;

import com.teclever.dfcc.DFCCConstant;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupDialoguShow {



	public void wdmStatusPopup() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/WdmConfirmationPopup.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.setTitle("Edit User");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (Exception e) {
			e.getLocalizedMessage();
		}
	}
	
		
}
