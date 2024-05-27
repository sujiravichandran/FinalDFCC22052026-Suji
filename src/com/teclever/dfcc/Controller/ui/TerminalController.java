package com.teclever.dfcc.Controller.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TerminalController {
	
	 private Stage terminalStage; 

	    public void createTerminalPopup() {
	        if (terminalStage == null) {
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
	                Parent root = loader.load();
	                terminalStage = new Stage();
	                terminalStage.initModality(Modality.APPLICATION_MODAL);
	                terminalStage.initStyle(StageStyle.UNDECORATED);
	                terminalStage.setScene(new Scene(root));
	                
//	                TerminalPopupController controller = loader.getController();
//	                controller.startBackgroundThread();

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        terminalStage.show();
	    }

	    public void hideTerminalPopup() {
	        if (terminalStage != null) {
	            terminalStage.hide();
	        }
	    }
}

