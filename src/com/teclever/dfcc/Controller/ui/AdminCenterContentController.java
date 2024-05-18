package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdminCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane userManagementStackPane = new StackPane();
    private StackPane VDDConfigStackPane = new StackPane();
    private StackPane faultCodeConfigStackPane = new StackPane();
    private StackPane stageConfigStackPane = new StackPane();



 
    public void createAdminCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
        switch (selectedMenu) {
            case "User Management":
                UserManagementController userManagementController = new UserManagementController();
            	if (centerStackPane.getChildren().contains(userManagementStackPane)) {
            	    boolean removed = centerStackPane.getChildren().remove(userManagementStackPane);
            	    if (removed) {
            	      	userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
            	        centerStackPane.getChildren().add(userManagementStackPane);
            	    } else {
            	        System.out.println("User-Node was not found or couldn't be removed.");
            	    }
            	} else {
            	  	userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
        	        centerStackPane.getChildren().add(userManagementStackPane);
            	}
//            	userManagementStackPane.toFront();
                break;
                
            case "Stage Config":
            	stageConfigStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
            	StageConfigurationController stageConfig = new StageConfigurationController();
            	if (centerStackPane.getChildren().contains(stageConfigStackPane)) {
            	    boolean removed = centerStackPane.getChildren().remove(stageConfigStackPane);
            	    if (removed) {
            	    	stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
            	        centerStackPane.getChildren().add(stageConfigStackPane);
            	    } else {
            	        System.out.println("Stage-Node was not found or couldn't be removed.");
            	    }
            	} else {
            		stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
        	        centerStackPane.getChildren().add(stageConfigStackPane);
            	}
//            	userManagementStackPane.toFront();
                break;
                
            case "VDD Config":
                VDDConfigurationController vddConfigurationController = new VDDConfigurationController();
            	if (centerStackPane.getChildren().contains(VDDConfigStackPane)) {
            	    boolean removed = centerStackPane.getChildren().remove(VDDConfigStackPane);
            	    if (removed) {
            	      	VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
            	        centerStackPane.getChildren().add(VDDConfigStackPane);
            	    } else {
            	        System.out.println("User-Node was not found or couldn't be removed.");
            	    }
            	} else {
            		VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
        	        centerStackPane.getChildren().add(VDDConfigStackPane);
            	}
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
