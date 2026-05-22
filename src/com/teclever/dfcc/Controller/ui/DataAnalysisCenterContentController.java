package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.utils.Notifications;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DataAnalysisCenterContentController {
	
	
	private static DataAnalysisCenterContentController instance;
	
	private StackPane centerStackPane = new StackPane();
	private StackPane dataAnalysisStackPane = new StackPane();
	private StackPane deviationStackPane = new StackPane();
	private StackPane buildConfigurationStackPane = new StackPane();
	private StackPane advancedResultsStackPane = new StackPane();
	private StackPane linkTestTypesStackPane = new StackPane();
	private StackPane manualTestingStackPane = new StackPane();
	private StackPane cumulativeStackPane = new StackPane();
	
	public static DataAnalysisCenterContentController getInstance() {
		if (instance == null) {
			synchronized (DataAnalysisCenterContentController.class) {
				if (instance == null) {
					instance = new DataAnalysisCenterContentController();
				}
			}
		}
		return instance;
	}

	public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu, String sessionId,
			SessionData rowData) {

		StackPane main = (StackPane) bottomMidTopGridPane.getParent().getParent();


		for (Node node : main.getChildren()) {
			if (node instanceof Label) {
				Label newLabel = (Label) node;
				if (!newLabel.getText().equals(selectedMenu)) {
					((Label) node).getStyleClass().remove("selected");
				}
			}
		}

		
		switch (selectedMenu) {
		
		
		
		case "Data Analysis":
			if (!centerStackPane.getChildren().contains(dataAnalysisStackPane)) {
				DataAnalysisController2 dataAnalysisController2 =new DataAnalysisController2();
				
				dataAnalysisStackPane.getChildren().add(dataAnalysisController2.createDashboardMainContainerGridPane());
				centerStackPane.getChildren().add(dataAnalysisStackPane);
			} else {
				dataAnalysisStackPane.toFront();
			}
			break;
		case "Deviation Result":
			if (!centerStackPane.getChildren().contains(deviationStackPane)) {
				DeviationController deviationController = new DeviationController();
				
				deviationStackPane.getChildren().add(deviationController.deviationResultGridPane());
				centerStackPane.getChildren().add(deviationStackPane);
			} else {
				deviationStackPane.toFront();
			}

			break;
			
		case "Build Configuration":
			DFCCConstant.buildDashboard = false;
			if (!centerStackPane.getChildren().contains(buildConfigurationStackPane)) {
				BuildConfigurationController buildConfigurationController =new BuildConfigurationController();
				
				buildConfigurationStackPane.getChildren().add(buildConfigurationController.createBuildConfigurationMainContainerGridPane());
				centerStackPane.getChildren().add(buildConfigurationStackPane);
			} else {
				buildConfigurationStackPane.toFront();
			}
			
			break;
			
		case "Advanced Failure":
			if (!centerStackPane.getChildren().contains(advancedResultsStackPane)) {
				AdvancedResultsController advancedResultsController =new AdvancedResultsController();
				AdavncedDataAnalysisFilter2TableController adavncedDataAnalysisFilter2TableController = new AdavncedDataAnalysisFilter2TableController();
				
//				advancedResultsStackPane.getChildren().add(advancedResultsController.createAdvancedResultsMainContainerGridPane());
				advancedResultsStackPane.getChildren().add(adavncedDataAnalysisFilter2TableController.createFilter2TableContainerGridPane());
				centerStackPane.getChildren().add(advancedResultsStackPane);
			} else {
				advancedResultsStackPane.toFront();
			}

			break;
		case "Link Fail Types":
			if (!centerStackPane.getChildren().contains(linkTestTypesStackPane)) {
				DFCCConstant.linkTestType = false;
				
				LinkTestTypesController linkFilesController =new LinkTestTypesController();
				
				linkTestTypesStackPane.getChildren().add(linkFilesController.createlinkTestTypesMainContainerGridPane());
				centerStackPane.getChildren().add(linkTestTypesStackPane);
			} else {
				linkTestTypesStackPane.toFront();
			}
            break;
			
		case "Manual Testing":
			if (!centerStackPane.getChildren().contains(manualTestingStackPane)) {
				AdvancedDataAnalysisManualTestingController advancedDataAnalysisManuaTestingController =new AdvancedDataAnalysisManualTestingController();
				
				manualTestingStackPane.getChildren().add(advancedDataAnalysisManuaTestingController.createManualTestingMainContainerGridPane());
				centerStackPane.getChildren().add(manualTestingStackPane);
			} else {
				manualTestingStackPane.toFront();
			}

			break;
			
		case "Cumulative":
			if (!centerStackPane.getChildren().contains(cumulativeStackPane)) {
				CumulativeController cumulativeController = new CumulativeController();
				cumulativeStackPane.getChildren().add(cumulativeController.cumulativeResultGridPane());
				centerStackPane.getChildren().add(cumulativeStackPane);
			} else {
				cumulativeStackPane.toFront();
			}

			break;
		
		}
		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
			bottomMidTopGridPane.getChildren().add(centerStackPane);
		}
	}

}
