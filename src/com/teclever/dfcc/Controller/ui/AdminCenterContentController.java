package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdminCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane userManagementStackPane = new StackPane();
    private StackPane VDDConfigStackPane = new StackPane();
    private StackPane faultCodeConfigStackPane = new StackPane();



 
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
                
            case "VDD Config":
//                VDDConfigController vddConfigController = new VDDConfigController();
//            	if (centerStackPane.getChildren().contains(VDDConfigStackPane)) {
//            	    boolean removed = centerStackPane.getChildren().remove(VDDConfigStackPane);
//            	    System.out.println("inside vdd contains");
//            	    if (removed) {
//            	    	  System.out.println("inside vdd remove");
//                    	VDDConfigStackPane.getChildren().add(vddConfigController.createUserManagemenGridPane());
//
//            	        centerStackPane.getChildren().add(VDDConfigStackPane);
//            	    } else {
//            	        System.out.println("Vdd-Node was not found or couldn't be removed.");
//            	    }
//            	} else {
//                	VDDConfigStackPane.getChildren().add(vddConfigController.createUserManagemenGridPane());
//        	        centerStackPane.getChildren().add(VDDConfigStackPane);
//            	    System.out.println("Vdd-Node is not a child of the StackPane.");
//            	}
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
