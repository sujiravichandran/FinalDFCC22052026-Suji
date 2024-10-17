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
	private StackPane aitessTestFileStackPane = new StackPane();
	private StackPane aitessSymbolFilesStackPane = new StackPane();
	private StackPane aitessMacroFilesStackPane = new StackPane();
	private StackPane aitessDownloadCodeStackPane = new StackPane();

	private StackPane ofpMasterStackPane = new StackPane();
	private StackPane ofpTestFilesStackPane = new StackPane();
	private StackPane ofpSymbolFilesStackPane = new StackPane();
	private StackPane ofpMacroFilesStackPane = new StackPane();
	private StackPane ofpDownloadCodeStackPane = new StackPane();

	private StackPane cpciCardStackPane = new StackPane();
	private StackPane macroButtonsStackPane = new StackPane();

	
	public AdminCenterContentController() {
		UserManagementController userManagementController = new UserManagementController();
		userManagementStackPane.getChildren().add(userManagementController.createUserManagemenGridPane());
		centerStackPane.getChildren().addAll(userManagementStackPane);
	}

	public void createAdminCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
		switch (selectedMenu) {
		case "User Management":
			userManagementStackPane.toFront();
			break;
		case "Stage Config":
			if (!centerStackPane.getChildren().contains(stageConfigStackPane)) {
				StageConfigurationController stageConfig = new StageConfigurationController();
				stageConfigStackPane.getChildren().add(stageConfig.stageConfigParentGrid());
				centerStackPane.getChildren().add(stageConfigStackPane);
			} else {
				stageConfigStackPane.toFront();
			}
			break;
		case "VDD Config":
			if (!centerStackPane.getChildren().contains(VDDConfigStackPane)) {
				VDDConfigurationController vddConfigurationController = new VDDConfigurationController();
				VDDConfigStackPane.getChildren().add(vddConfigurationController.createVddConfigGridPane());
				centerStackPane.getChildren().add(VDDConfigStackPane);
			} else {
				VDDConfigStackPane.toFront();
			}
			break;
		case "Fault Code Config":
			FaultCodeConfigurationController faultCodeConfigurationController = FaultCodeConfigurationController.getInstance();
			if (!centerStackPane.getChildren().contains(faultCodeConfigStackPane)) {
				faultCodeConfigStackPane.getChildren()
						.add(faultCodeConfigurationController.createFaultCodeConfigGridPane());
				centerStackPane.getChildren().add(faultCodeConfigStackPane);
			} else {
				faultCodeConfigStackPane.toFront();
				faultCodeConfigurationController.updateData();
			}
			break;
		case "AITESS Version":
			if (!centerStackPane.getChildren().contains(aitessMasterStackPane)) {
				AitessMasterController aitessMasterController = new AitessMasterController();
				aitessMasterStackPane.getChildren().add(aitessMasterController.aitessMasterGridPane());
				centerStackPane.getChildren().add(aitessMasterStackPane);
			} else {
				aitessMasterStackPane.toFront();
			}
			break;
		case "Run Config":
			if (!centerStackPane.getChildren().contains(runConfigurationStackPane)) {
				RunConfigurationController runConfigurationController = new RunConfigurationController();
				runConfigurationStackPane.getChildren().add(runConfigurationController.runconfigurationGridPane());
				centerStackPane.getChildren().add(runConfigurationStackPane);
			} else {
				runConfigurationStackPane.toFront();
			}
			break;
		case "Test Files":
			if (!centerStackPane.getChildren().contains(aitessTestFileStackPane)) {
				AitessTestFilesController testFilesController = new AitessTestFilesController();
				aitessTestFileStackPane.getChildren().add(testFilesController.testFilesConfigParentGrid());
				centerStackPane.getChildren().add(aitessTestFileStackPane);
			} else {
				aitessTestFileStackPane.toFront();
			}
			break;
		case "Symbol Files":
			if (!centerStackPane.getChildren().contains(aitessSymbolFilesStackPane)) {
				AitessSymbolFilesController symbolFilesConfig = new AitessSymbolFilesController();
				aitessSymbolFilesStackPane.getChildren().add(symbolFilesConfig.symbolFilesConfigParentGrid());
				centerStackPane.getChildren().add(aitessSymbolFilesStackPane);
			} else {
				aitessSymbolFilesStackPane.toFront();
			}
			break;
		case "Macro Files":
			if (!centerStackPane.getChildren().contains(aitessMacroFilesStackPane)) {
				AitessMacroFilesController macroFilesConfig = new AitessMacroFilesController();
				aitessMacroFilesStackPane.getChildren().add(macroFilesConfig.macroFilesConfigParentGrid());
				centerStackPane.getChildren().add(aitessMacroFilesStackPane);
			} else {
				aitessMacroFilesStackPane.toFront();
			}
			break;
		case "Download Code":
			if (!centerStackPane.getChildren().contains(aitessDownloadCodeStackPane)) {
				AitessDownloadCodeController downloadCodeConfig = new AitessDownloadCodeController();
				aitessDownloadCodeStackPane.getChildren().add(downloadCodeConfig.downloadCodeConfigParentGrid());
				centerStackPane.getChildren().add(aitessDownloadCodeStackPane);
			} else {
				aitessDownloadCodeStackPane.toFront();
			}
			break;
		case "OFP Version":
			if (!centerStackPane.getChildren().contains(ofpMasterStackPane)) {
				OFPMasterController ofpMasterController = new OFPMasterController();
				ofpMasterStackPane.getChildren().add(ofpMasterController.ofpMasterConfigParentGrid());
				centerStackPane.getChildren().add(ofpMasterStackPane);
			} else {
				ofpMasterStackPane.toFront();
			}
			break;
		case "Test Files-OFP":
			OFPTestFilesController ofpTestFilesController = OFPTestFilesController.getInstance();
			if (!centerStackPane.getChildren().contains(ofpTestFilesStackPane)) {
				ofpTestFilesStackPane.getChildren().add(ofpTestFilesController.ofpTestFileParentGrid());
				centerStackPane.getChildren().add(ofpTestFilesStackPane);
			} else {
				ofpTestFilesStackPane.toFront();
				ofpTestFilesController.updateData();
			}
			break;
		case "Symbol Files-OFP":
			OFPSymbolFilesController ofpSymbolFilesController = OFPSymbolFilesController.getInstance();
			if (!centerStackPane.getChildren().contains(ofpSymbolFilesStackPane)) {
				ofpSymbolFilesStackPane.getChildren().add(ofpSymbolFilesController.ofpSymbolFileParentGrid());
				centerStackPane.getChildren().add(ofpSymbolFilesStackPane);
			} else {
				ofpSymbolFilesStackPane.toFront();
				ofpSymbolFilesController.updateData();
			}
			break;
		case "Macro Files-OFP":
			OFPMacroFilesController ofpMacroFilesController = OFPMacroFilesController.getInstance();
			if (!centerStackPane.getChildren().contains(ofpMacroFilesStackPane)) {
				ofpMacroFilesStackPane.getChildren().add(ofpMacroFilesController.ofpMacroFileParentGrid());
				centerStackPane.getChildren().add(ofpMacroFilesStackPane);
			} else {
				ofpMacroFilesStackPane.toFront();
				ofpMacroFilesController.updateData();
			}
			break;
		case "Download Plan":
			OFPDownloadCodeController ofpDownloadCodeController = OFPDownloadCodeController.getInstance();
			if (!centerStackPane.getChildren().contains(ofpDownloadCodeStackPane)) {
				ofpDownloadCodeStackPane.getChildren().add(ofpDownloadCodeController.ofpDownloadCodeParentGrid());
				centerStackPane.getChildren().add(ofpDownloadCodeStackPane);
			} else {
				ofpDownloadCodeStackPane.toFront();
				ofpDownloadCodeController.updateData();
			}
			break;
		case "MACRO Buttons":
			if (!centerStackPane.getChildren().contains(macroButtonsStackPane)) {
				MacroButtonsController macroButtonsController = new MacroButtonsController();
				macroButtonsStackPane.getChildren().add(macroButtonsController.createMacroButtonsMainGridPane());
				centerStackPane.getChildren().add(macroButtonsStackPane);
			} else {
				macroButtonsStackPane.toFront();
			}
			break;
		case "cPCI card's Details":
			CPCICardController cpciCardController = CPCICardController.getInstance();
			if (!centerStackPane.getChildren().contains(cpciCardStackPane)) {
				cpciCardStackPane.getChildren().add(cpciCardController.createcpciCardConfigGridPane());
				centerStackPane.getChildren().add(cpciCardStackPane);
			} else {
				cpciCardStackPane.toFront();
				cpciCardController.updateData();
			}
			break;
		case "Launch type":
			LaunchTypeController launchTypeController = new LaunchTypeController();
			launchTypeController.createLaunchTypePopup();
			break;

		case "Admin Password":
			AdminPasswordController adminPasswordController = new AdminPasswordController();
			adminPasswordController.createAdminPasswordPopup();
			break;

		case "CheckSum Data":
			CheckSumController checkSumController = new CheckSumController();
			checkSumController.createCheckSumDataPopup();
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
