package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdminCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane userManagementStackPane = new StackPane();
    private StackPane VDDConfigStackPane = new StackPane();
    private StackPane faultCodeConfigStackPane = new StackPane();

 
    public AdminCenterContentController() {
        centerStackPane.getChildren().addAll(userManagementStackPane, VDDConfigStackPane, faultCodeConfigStackPane); 
    }

    public void createAdminCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
        switch (selectedMenu) {
            case "User Management":
            	userManagementStackPane.setStyle("-fx-background-color:green;-fx-background-radius:15px;");
            	userManagementStackPane.toFront();
                break;
            case "VDD Config":
                VDDConfigStackPane.setStyle("-fx-background-color:red;-fx-background-radius:15px;");
                VDDConfigStackPane.toFront();
                break;
            case "Results":
                faultCodeConfigStackPane.setStyle("-fx-background-color:yellow;-fx-background-radius:15px;");
                faultCodeConfigStackPane.toFront();
                break;
        }
        
        
        if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
            bottomMidTopGridPane.getChildren().add(centerStackPane);
        }
    }

   
 
}
