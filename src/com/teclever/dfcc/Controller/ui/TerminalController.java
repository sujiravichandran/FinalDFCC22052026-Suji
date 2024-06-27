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
                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
                Parent root = loader.load();
                terminalStage = new Stage();
                terminalStage.initModality(Modality.APPLICATION_MODAL);
                terminalStage.initStyle(StageStyle.UNDECORATED);
                terminalStage.setScene(new Scene(root));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        terminalStage.show();
    }



    public void launchTerminal() {
        if (terminalStage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
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
//import javafx.animation.FadeTransition;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.util.Duration;
//
//public class TerminalController {
//
//    private Stage terminalStage; 
//
//    public void createTerminalPopup() {
//        if (terminalStage == null) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
//                Parent root = loader.load();
//                terminalStage = new Stage();
//                terminalStage.initModality(Modality.APPLICATION_MODAL);
//                terminalStage.initStyle(StageStyle.UNDECORATED);
//                terminalStage.setScene(new Scene(root));
//                
//                terminalStage.setX(200);
//                terminalStage.setY(200);
//
//                FadeTransition fadeIn = new FadeTransition(Duration.millis(5000), root);
//                fadeIn.setFromValue(0.0);
//                fadeIn.setToValue(1.0);
//                fadeIn.play();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        terminalStage.show();
//    }
//
//    public void hideTerminalPopup() {
//        if (terminalStage != null) {
//            Parent root = terminalStage.getScene().getRoot();
//            FadeTransition fadeOut = new FadeTransition(Duration.millis(5000), root);
//            fadeOut.setFromValue(1.0);
//            fadeOut.setToValue(0.0);
//            fadeOut.setOnFinished(event -> terminalStage.hide());
//            fadeOut.play();
//        }
//    }
//
//    public void launchTerminal() {
//        if (terminalStage == null) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/TerminalPopup.fxml"));
//                Parent root = loader.load();
//                terminalStage = new Stage();
//                terminalStage.initModality(Modality.APPLICATION_MODAL);
//                terminalStage.initStyle(StageStyle.UNDECORATED);
//                terminalStage.setScene(new Scene(root));
//                
//                terminalStage.setX(200);
//                terminalStage.setY(200);
//
//                // Optionally add the fade-in animation here as well
//                FadeTransition fadeIn = new FadeTransition(Duration.millis(5000), root);
//                fadeIn.setFromValue(0.0);
//                fadeIn.setToValue(1.0);
//                fadeIn.play();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
