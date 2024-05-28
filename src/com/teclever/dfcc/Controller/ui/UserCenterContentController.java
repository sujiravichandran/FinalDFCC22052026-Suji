package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class UserCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane dashboardStackPane = new StackPane();
    private StackPane testingStackPane = new StackPane();
    private StackPane resultsStackPane = new StackPane();
    private StackPane selfTestStackPane = new StackPane();
    private StackPane lruTestStackPane = new StackPane();

    SelfTestController selfTestController = new SelfTestController();
    
    LRUTestingController lruTestController = new LRUTestingController();
    
    public UserCenterContentController() {
        
        
        selfTestStackPane.getChildren().add(selfTestController.createSelfTestMainContainerGridPane());
        
        lruTestStackPane.getChildren().add(lruTestController.createlruTestMainContainerGridPane());
        
        centerStackPane.getChildren().addAll(dashboardStackPane, testingStackPane, resultsStackPane, selfTestStackPane, lruTestStackPane);
        
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
            case "Self Test":
            	selfTestStackPane.toFront();
                break;
                
            case "SRU/LRU Test":
            	lruTestStackPane.toFront();
                break;
                
        }
        
        
        if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
            bottomMidTopGridPane.getChildren().add(centerStackPane);
        }
    }


}
