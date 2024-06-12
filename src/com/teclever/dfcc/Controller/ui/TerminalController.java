package com.teclever.dfcc.Controller.ui;

import java.io.IOException;

import com.teclever.dfcc.DFCCConstant;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TerminalController {
	
	 private Stage terminalStage; 

	    public void createTerminalPopup() {
	        if (terminalStage == null) {
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
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
	    
	    public void launchTerminal() {
	        if (terminalStage == null) {
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
	                Parent root = loader.load();
	                terminalStage = new Stage();
	                terminalStage.initModality(Modality.APPLICATION_MODAL);
	                terminalStage.initStyle(StageStyle.UNDECORATED);
	                terminalStage.setScene(new Scene(root));
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } 
	    }
}








//package com.teclever.dfcc.Controller.ui;
//
//import java.io.IOException;
//
//import com.teclever.dfcc.DFCCConstant;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//
//public class TerminalController {
//	
//	 private Stage terminalStage; 
//
//	    public void createTerminalPopup() {
//	    	System.out.println(terminalStage+"---");
//	        if (terminalStage == null) {
//	            try {
//	                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
//	                Parent root = loader.load();
//	                terminalStage = new Stage();
//	                terminalStage.initModality(Modality.APPLICATION_MODAL);
//	                terminalStage.initStyle(StageStyle.UNDECORATED);
//	                terminalStage.setScene(new Scene(root));
//	                
////	                TerminalPopupController controller = loader.getController();
////	                controller.startBackgroundThread();
//
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
//	        }
//	        terminalStage.show();
//	    }
//
//	    public void hideTerminalPopup() {
//	        if (terminalStage != null) {
//	            terminalStage.hide();
//	        }
//	    }
//}
//
