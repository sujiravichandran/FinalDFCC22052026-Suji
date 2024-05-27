package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class UserCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane dashboardStackPane = new StackPane();
    private StackPane testingStackPane = new StackPane();
    private StackPane resultsStackPane = new StackPane();
    
    private TerminalController terminalController = new TerminalController();

 
    public UserCenterContentController() {
        centerStackPane.getChildren().addAll(dashboardStackPane, testingStackPane, resultsStackPane);
    }

    public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
        switch (selectedMenu) {
            case "Dashboard":
            	dashboardStackPane.setStyle("-fx-background-color:green;-fx-background-radius:15px;");
            	dashboardStackPane.toFront();
                break;
            case "Testing":
                testingStackPane.setStyle("-fx-background-color:red;-fx-background-radius:15px;");
                testingStackPane.toFront();
                break;
            case "Results":
                resultsStackPane.setStyle("-fx-background-color:yellow;-fx-background-radius:15px;");
                resultsStackPane.toFront();
                break;
            case "Show Terminal":
    			terminalController.createTerminalPopup();
    			break;
    		}
        
        
        
        if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
            bottomMidTopGridPane.getChildren().add(centerStackPane);
        }
    }


}
