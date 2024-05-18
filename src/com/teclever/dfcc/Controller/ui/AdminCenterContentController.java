package com.teclever.dfcc.Controller.ui;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdminCenterContentController {

    private StackPane centerStackPane = new StackPane();
    private StackPane userManagementStackPane = new StackPane();
    private StackPane VDDConfigStackPane = new StackPane();
    private StackPane faultCodeConfigStackPane = new StackPane();
    private StackPane stageConfigStackPane = new StackPane();
    private StackPane testFilesStackPane = new StackPane();
    private StackPane symbolFilesConfigStackPane = new StackPane();
    private StackPane aitessMasterStackPane = new StackPane();
    private StackPane runConfigurationStackPane = new StackPane();


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
                
            case "Test Files":
          	  TestFilesController testFilesController = new TestFilesController();
            	if (centerStackPane.getChildren().contains(testFilesStackPane)) {
            	    boolean removed = centerStackPane.getChildren().remove(testFilesStackPane);
            	    if (removed) {
            	    	testFilesStackPane.getChildren().add(testFilesController.createVddConfigGridPane());
            	        centerStackPane.getChildren().add(testFilesStackPane);
            	    } else {
            	        System.out.println("User-Node was not found or couldn't be removed.");
            	    }
            	} else {
            		testFilesStackPane.getChildren().add(testFilesController.createVddConfigGridPane());
        	        centerStackPane.getChildren().add(testFilesStackPane);
            	}
            	break;
            	
 case "Symbol Files":
            	
            	symbolFilesConfigStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
            	SymbolFilesController symbolFilesConfig = new SymbolFilesController();
            	if (centerStackPane.getChildren().contains(symbolFilesConfigStackPane)) {
            	    boolean removed = centerStackPane.getChildren().remove(symbolFilesConfigStackPane);
            	    if (removed) {
            	    	symbolFilesConfigStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
            	        centerStackPane.getChildren().add(symbolFilesConfigStackPane);
            	    } else {
            	        System.out.println("Stage-Node was not found or couldn't be removed.");
            	    }
            	} else {
            		symbolFilesConfigStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
        	        centerStackPane.getChildren().add(symbolFilesConfigStackPane);
            	}
    			symbolFilesConfigStackPane.toFront();
    			break;
    			
               	
     case "AITESS Master":
                	
    	 aitessMasterStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
                	AitessMasterController aitessMasterController = new AitessMasterController();
                	if (centerStackPane.getChildren().contains(aitessMasterStackPane)) {
                	    boolean removed = centerStackPane.getChildren().remove(aitessMasterStackPane);
                	    if (removed) {
                	    	aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
                	        centerStackPane.getChildren().add(aitessMasterStackPane);
                	    } else {
                	        System.out.println("Stage-Node was not found or couldn't be removed.");
                	    }
                	} else {
                		aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
            	        centerStackPane.getChildren().add(aitessMasterStackPane);
                	}
                	aitessMasterStackPane.toFront();
        			break;
                
        			
     case "Run Config":
     	
    	 runConfigurationStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
    	 RunConfigurationController runConfigurationController = new RunConfigurationController();
                	if (centerStackPane.getChildren().contains(runConfigurationStackPane)) {
                	    boolean removed = centerStackPane.getChildren().remove(runConfigurationStackPane);
                	    if (removed) {
                	    	runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
                	        centerStackPane.getChildren().add(runConfigurationStackPane);
                	    } else {
                	        System.out.println("Stage-Node was not found or couldn't be removed.");
                	    }
                	} else {
                		runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
            	        centerStackPane.getChildren().add(runConfigurationStackPane);
                	}
                	runConfigurationStackPane.toFront();
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
