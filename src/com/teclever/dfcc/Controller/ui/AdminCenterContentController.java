package com.teclever.dfcc.Controller.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdminCenterContentController {

	private StackPane centerStackPane = new StackPane();
	private StackPane userManagementStackPane = new StackPane();
	private StackPane VDDConfigStackPane = new StackPane();
	private StackPane faultCodeConfigStackPane = new StackPane();
	private StackPane stageConfigStackPane = new StackPane();
	private StackPane aitessMasterStackPane = new StackPane();
	private StackPane runConfigurationStackPane = new StackPane();
	private StackPane testFilesStackPane = new StackPane();
	private StackPane symbolFilesConfigStackPane = new StackPane();
	private StackPane macroFilesConfigStackPane = new StackPane();
	private StackPane downloadCodeConfigStackPane = new StackPane();
	private StackPane cpciCardStackPane = new StackPane();
	private StackPane macroButtonsStackPane = new StackPane();
	
	UserManagementController userManagementController = new UserManagementController();
	StageConfigurationController stageConfig = new StageConfigurationController();
	VDDConfigurationController vddConfigurationController = new VDDConfigurationController();
	AitessMasterController aitessMasterController = new AitessMasterController();
	RunConfigurationController runConfigurationController = new RunConfigurationController();
	AitessTestFilesController testFilesController = new AitessTestFilesController();
	AitessSymbolFilesController symbolFilesConfig = new AitessSymbolFilesController();
	AitessMacroFilesController macroFilesConfig = new AitessMacroFilesController();
	AitessDownloadCodeController downloadCodeConfig = new AitessDownloadCodeController();
	MacroButtonsController macroButtonsController = new MacroButtonsController();
	CPCICardController cpciCardController = new CPCICardController();
	
	public AdminCenterContentController() {
		userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
		stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
		VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
		aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
		runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
		testFilesStackPane.getChildren().add(testFilesController.testFilesConfigParentGrid());
		symbolFilesConfigStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
		macroFilesConfigStackPane.getChildren().add(macroFilesConfig.macroFilesConfigParentGrid());
		downloadCodeConfigStackPane.getChildren().add(downloadCodeConfig.downloadCodeConfigParentGrid());
		macroButtonsStackPane.getChildren().add(macroButtonsController.createMacroButtonsMainGridPane());
		cpciCardStackPane.getChildren().add(cpciCardController.createcpciCardConfigGridPane());
		
		centerStackPane.getChildren().addAll(userManagementStackPane, stageConfigStackPane, VDDConfigStackPane,
				aitessMasterStackPane, runConfigurationStackPane, testFilesStackPane, symbolFilesConfigStackPane,
				macroFilesConfigStackPane, downloadCodeConfigStackPane,macroButtonsStackPane,cpciCardStackPane);
	}

	public void createAdminCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
		switch (selectedMenu) {
		case "User Management":
			userManagementStackPane.toFront();
			break;
		case "Stage Config":
			stageConfigStackPane.toFront();
			break;
		case "VDD Config":
			VDDConfigStackPane.toFront();
			break;
		case "AITESS Master":
			aitessMasterStackPane.toFront();
			break;
		case "Run Config":
			runConfigurationStackPane.toFront();
			break;
		case "Test Files":
			testFilesStackPane.toFront();
			break;
		case "Symbol Files":
			symbolFilesConfigStackPane.toFront();
			break;
		case "Macro Files":
			macroFilesConfigStackPane.toFront();
			break;
		case "Download Code":
			downloadCodeConfigStackPane.toFront();
			break;
		case "MACRO Buttons":
			macroButtonsStackPane.toFront();
			break;
		case "cPCI card's Details":
			cpciCardStackPane.toFront();
			break;
		case "Results":
			faultCodeConfigStackPane.toFront();
			break;
		}

		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
			bottomMidTopGridPane.getChildren().add(centerStackPane);
		}
	}

}

//package com.teclever.dfcc.Controller.ui;
//
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.StackPane;
//
//public class AdminCenterContentController {
//
//	private StackPane centerStackPane = new StackPane();
//	private StackPane userManagementStackPane = new StackPane();
//	private StackPane VDDConfigStackPane = new StackPane();
//	private StackPane faultCodeConfigStackPane = new StackPane();
//	private StackPane stageConfigStackPane = new StackPane();
//	private StackPane aitessMasterStackPane = new StackPane();
//	private StackPane runConfigurationStackPane = new StackPane();
//	private StackPane testFilesStackPane = new StackPane();
//	private StackPane symbolFilesConfigStackPane = new StackPane();
//	private StackPane macroFilesConfigStackPane = new StackPane();
//	private StackPane downloadCodeConfigStackPane = new StackPane();
//
//	public void createAdminCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
//		switch (selectedMenu) {
//		case "User Management":
//			UserManagementController userManagementController = new UserManagementController();
//			if (centerStackPane.getChildren().contains(userManagementStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(userManagementStackPane);
//				if (removed) {
//					userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
//					centerStackPane.getChildren().add(userManagementStackPane);
//				} else {
//					System.out.println("User-Node was not found or couldn't be removed.");
//				}
//			} else {
//				userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
//				centerStackPane.getChildren().add(userManagementStackPane);
//			}
////            	userManagementStackPane.toFront();
//			break;
//
//		case "Stage Config":
//			stageConfigStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
//			StageConfigurationController stageConfig = new StageConfigurationController();
//			if (centerStackPane.getChildren().contains(stageConfigStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(stageConfigStackPane);
//				if (removed) {
//					stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
//					centerStackPane.getChildren().add(stageConfigStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
//				centerStackPane.getChildren().add(stageConfigStackPane);
//			}
//			break;
//
//		case "VDD Config":
//			VDDConfigurationController vddConfigurationController = new VDDConfigurationController();
//			if (centerStackPane.getChildren().contains(VDDConfigStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(VDDConfigStackPane);
//				if (removed) {
//					VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
//					centerStackPane.getChildren().add(VDDConfigStackPane);
//				} else {
//					System.out.println("User-Node was not found or couldn't be removed.");
//				}
//			} else {
//				VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
//				centerStackPane.getChildren().add(VDDConfigStackPane);
//			}
//			VDDConfigStackPane.toFront();
//			break;
//
//		case "AITESS Master":
//
//			aitessMasterStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
//			AitessMasterController aitessMasterController = new AitessMasterController();
//			if (centerStackPane.getChildren().contains(aitessMasterStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(aitessMasterStackPane);
//				if (removed) {
//					aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
//					centerStackPane.getChildren().add(aitessMasterStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
//				centerStackPane.getChildren().add(aitessMasterStackPane);
//			}
//			aitessMasterStackPane.toFront();
//			break;
//
//		case "Run Config":
//
//			runConfigurationStackPane.setStyle("-fx-background-color:white;-fx-background-radius:15px;");
//			RunConfigurationController runConfigurationController = new RunConfigurationController();
//			if (centerStackPane.getChildren().contains(runConfigurationStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(runConfigurationStackPane);
//				if (removed) {
//					runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
//					centerStackPane.getChildren().add(runConfigurationStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
//				centerStackPane.getChildren().add(runConfigurationStackPane);
//			}
//			runConfigurationStackPane.toFront();
//			break;
//
//		case "Test Files":
//			AitessTestFilesController testFilesController = new AitessTestFilesController();
//			if (centerStackPane.getChildren().contains(testFilesStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(testFilesStackPane);
//				if (removed) {
//					testFilesStackPane.getChildren().add(testFilesController.testFilesConfigParentGrid());
//					centerStackPane.getChildren().add(testFilesStackPane);
//				} else {
//					System.out.println("User-Node was not found or couldn't be removed.");
//				}
//			} else {
//				testFilesStackPane.getChildren().add(testFilesController.testFilesConfigParentGrid());
//				centerStackPane.getChildren().add(testFilesStackPane);
//			}
//			break;
//
//		case "Symbol Files":
//			AitessSymbolFilesController symbolFilesConfig = new AitessSymbolFilesController();
//			if (centerStackPane.getChildren().contains(symbolFilesConfigStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(symbolFilesConfigStackPane);
//				if (removed) {
//					symbolFilesConfigStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
//					centerStackPane.getChildren().add(symbolFilesConfigStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				symbolFilesConfigStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
//				centerStackPane.getChildren().add(symbolFilesConfigStackPane);
//			}
//
//			break;
//
//		case "Macro Files":
//			AitessMacroFilesController macroFilesConfig = new AitessMacroFilesController();
//			if (centerStackPane.getChildren().contains(macroFilesConfigStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(macroFilesConfigStackPane);
//				if (removed) {
//					macroFilesConfigStackPane.getChildren().add(macroFilesConfig.macroFilesConfigParentGrid());
//					centerStackPane.getChildren().add(macroFilesConfigStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				macroFilesConfigStackPane.getChildren().add(macroFilesConfig.macroFilesConfigParentGrid());
//				centerStackPane.getChildren().add(macroFilesConfigStackPane);
//			}
//
//			break;
//
//		case "Download Code":
//			AitessDownloadCodeController downloadCodeConfig = new AitessDownloadCodeController();
//			if (centerStackPane.getChildren().contains(downloadCodeConfigStackPane)) {
//				boolean removed = centerStackPane.getChildren().remove(downloadCodeConfigStackPane);
//				if (removed) {
//					downloadCodeConfigStackPane.getChildren().add(downloadCodeConfig.downloadCodeConfigParentGrid());
//					centerStackPane.getChildren().add(downloadCodeConfigStackPane);
//				} else {
//					System.out.println("Stage-Node was not found or couldn't be removed.");
//				}
//			} else {
//				downloadCodeConfigStackPane.getChildren().add(downloadCodeConfig.downloadCodeConfigParentGrid());
//				centerStackPane.getChildren().add(downloadCodeConfigStackPane);
//			}
//
//			break;
//		case "Results":
//			
//			faultCodeConfigStackPane.toFront();
//			break;
//		}
//
//		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
//			bottomMidTopGridPane.getChildren().add(centerStackPane);
//		}
//	}
//
//}
