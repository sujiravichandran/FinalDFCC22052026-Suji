package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UserCenterContentController {

	private static UserCenterContentController instance;
	
	private StackPane centerStackPane = new StackPane();
	private StackPane dashboardStackPane = new StackPane();
	private StackPane testingStackPane = new StackPane();
	private StackPane resultsStackPane = new StackPane();
	private StackPane selfTestStackPane = new StackPane();
	private StackPane lruTestStackPane = new StackPane();
	private StackPane sessionTestingStackPane = new StackPane();
	private StackPane advancedTestingStackPane = new StackPane();
	private StackPane trialsConfigStackPane = new StackPane();
	private StackPane currentExecutionResultStackPane = new StackPane();
	private StackPane currentSessionResultStackPane = new StackPane();
	private StackPane currentUnitResultStackPane = new StackPane();
	private StackPane currentStageResultStackPane = new StackPane();
	private StackPane pqtReportStackPane = new StackPane();
	private StackPane essReportStackPane = new StackPane();
	private StackPane datapackReportStackPane = new StackPane();
	private StackPane configurationStackPane = new StackPane();
	private StackPane logBookStackPane = new StackPane();
	private StackPane reportsUploadStackPane = new StackPane();
	
	private TerminalController terminalController = new TerminalController();
	private SessionManagement sessionManagement = new SessionManagement();

	public UserCenterContentController() {
		TerminalPopupController terminalPopupController = new TerminalPopupController();
		centerStackPane.getChildren().addAll(dashboardStackPane);
		terminalController.launchTerminal();
	}
	public static UserCenterContentController getInstance() {
		if (instance == null) {
			synchronized (UserCenterContentController.class) {
				if (instance == null) {
					instance = new UserCenterContentController();
				}
			}
		}
		return instance;
	}
	public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu ,String sessionId, String stageId) {
		
		GridPane main = (GridPane) bottomMidTopGridPane.getParent().getParent();
		GridPane child = (GridPane) main.getChildren().get(0);
		TreeView<Label> menuItem = (TreeView<Label>) child.getChildren().get(0);
		VBox subMenuItem = (VBox) child.getChildren().get(1);

				
		for (TreeItem<Label> childItem : menuItem.getRoot().getChildren()) {
		    Label item = childItem.getValue();

		    for (TreeItem<Label> childSubItem : childItem.getChildren()) {
		        Label subItem = childSubItem.getValue();
		        if (subItem.getText().equals(selectedMenu)) {
		            menuItem.getSelectionModel().select(childSubItem);
		        } else {
		            menuItem.getSelectionModel().clearSelection(menuItem.getRow(childSubItem));
		        }
		    }

		    if (childItem.getChildren().isEmpty()) {
		        if (item.getText().equals(selectedMenu)) {
		            menuItem.getSelectionModel().select(childItem);
		        } else {
		            menuItem.getSelectionModel().clearSelection(menuItem.getRow(childItem));
		        }
		    }
		}
		
		for (Node node : subMenuItem.getChildren()) {
			if (node instanceof Label) {
				Label newLabel = (Label) node;
				if(!newLabel.getText().equals(selectedMenu)) {					
					((Label) node).getStyleClass().remove("selected");
				}
			}
		}


		switch (selectedMenu) {
		case "Dashboard":
			dashboardStackPane.toFront();
			break;
		case "Testing":
			testingStackPane.toFront();
			break;
		case "Results":
			resultsStackPane.toFront();
			break;
		case "Self Test":
			if (!centerStackPane.getChildren().contains(selfTestStackPane)) {
				SelfTestController selfTestController = new SelfTestController();
				selfTestStackPane.getChildren().add(selfTestController.createSelfTestMainContainerGridPane());
				centerStackPane.getChildren().add(selfTestStackPane);
			} else {
				selfTestStackPane.toFront();
			}

			break;

		case "SRU/LRU Test":
			if (!centerStackPane.getChildren().contains(lruTestStackPane)) {
				LRUTestingController lruTestController = new LRUTestingController();
				lruTestStackPane.getChildren().add(lruTestController.createlruTestMainContainerGridPane());
				centerStackPane.getChildren().add(lruTestStackPane);
			} else {
				lruTestStackPane.toFront();
			}
			
			break;
			
		case "Session Testing":
			if (!centerStackPane.getChildren().contains(sessionTestingStackPane)) {
				SessionTestingController sessionTestingController = new SessionTestingController();
				sessionTestingStackPane.getChildren().add(sessionTestingController.createSessionTestingGridPane(false));
				centerStackPane.getChildren().add(sessionTestingStackPane);
			} else {
				sessionTestingStackPane.toFront();
			}
			
			break;
			
		case "Advanced Testing":
			if (!centerStackPane.getChildren().contains(advancedTestingStackPane)) {
				AdvancedTestingController advancedTestingController = new AdvancedTestingController();
				advancedTestingStackPane.getChildren().add(advancedTestingController.createAdvancedTestingGridPane());
				centerStackPane.getChildren().add(advancedTestingStackPane);
			} else {
				advancedTestingStackPane.toFront();
			}
			
			break;
			
		case "Trials Config" :
			if (!centerStackPane.getChildren().contains(trialsConfigStackPane)) {
				TrialsConfigurationController trialsConfigurationController = new TrialsConfigurationController();
				trialsConfigStackPane.getChildren().add(trialsConfigurationController.createTrialsConfigMainGridPane());
				centerStackPane.getChildren().add(trialsConfigStackPane);
			} else {
				trialsConfigStackPane.toFront();
			}
			
			break;
			
		case "Trials Testing":
			if (!centerStackPane.getChildren().contains(sessionTestingStackPane)) {
				SessionTestingController sessionTestingController = new SessionTestingController();
				SessionManagement sessionManagement = new SessionManagement();
				if(!sessionManagement.getFinalizeStatus()) {
					Notifications.showWarningAlert("Please finalize the trials configuration on the trials config page if you want to run trials testing..");
					return;
				}
				sessionTestingStackPane.getChildren().add(sessionTestingController.createSessionTestingGridPane(true));
				centerStackPane.getChildren().add(sessionTestingStackPane);
			} else {
				sessionTestingStackPane.toFront();
			}
			
			break;
					
		case "Current Execution" :			
			CurrentExecutionResultController currentExcecutionResultController = new CurrentExecutionResultController();
			if (centerStackPane.getChildren().contains(currentExecutionResultStackPane)) {
				currentExecutionResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentExecutionResultStackPane);
			}
			currentExecutionResultStackPane.getChildren().add(currentExcecutionResultController.createCurrentExecutionResultGridPane(null,null,false));
			centerStackPane.getChildren().add(currentExecutionResultStackPane);
			
			break;
	
			
		case "Unit Results" :
			CurrentUnitResultController currentUnitResultController = new CurrentUnitResultController();
			if (centerStackPane.getChildren().contains(currentUnitResultStackPane)) {
				currentUnitResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentUnitResultStackPane);
			}
			currentUnitResultStackPane.getChildren().add(currentUnitResultController.createcurrentUnitResultGridPane());
			centerStackPane.getChildren().add(currentUnitResultStackPane);
			
			break;

		case "Session Results" :
			CurrentSessionResultController currentSessionResultController = new CurrentSessionResultController();
			if (centerStackPane.getChildren().contains(currentSessionResultStackPane)) {
				if(sessionId == null) {
					currentSessionResultStackPane.toFront();
					break ;
				}
				currentSessionResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentSessionResultStackPane);
			}
			currentSessionResultStackPane.getChildren().add(currentSessionResultController.createCurrentSessionResultGridPane(sessionId));
			centerStackPane.getChildren().add(currentSessionResultStackPane);	
			
			break;
			
		case "Stage Results" :	
			CurrentExecutionResultController currentExcecutionResultController1 = new CurrentExecutionResultController();
			if (centerStackPane.getChildren().contains(currentStageResultStackPane)) {
				if(sessionId == null && stageId == null) {
					currentStageResultStackPane.toFront();
					break ;
				}
				currentStageResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentStageResultStackPane);
			}
			currentStageResultStackPane.getChildren().add(currentExcecutionResultController1.createCurrentExecutionResultGridPane(sessionId,stageId,true));
			centerStackPane.getChildren().add(currentStageResultStackPane);	
			
			break;
			
		case "PQT Report" :				
			if (!centerStackPane.getChildren().contains(pqtReportStackPane)) {
				ReportController reportControllerForPQT = new ReportController();
				pqtReportStackPane.getChildren().add(reportControllerForPQT.createReportGridPane("PQT REPORT"));
				centerStackPane.getChildren().add(pqtReportStackPane);
			} else {
				pqtReportStackPane.toFront();
			}
			
			break;
			
		case "ESS Report" :				
			if (!centerStackPane.getChildren().contains(essReportStackPane)) {
				ReportController reportControllerForESS = new ReportController();
				essReportStackPane.getChildren().add(reportControllerForESS.createReportGridPane("ESS REPORT"));
				centerStackPane.getChildren().add(essReportStackPane);
			} else {
				essReportStackPane.toFront();
			}
			
			break;

		case "Datapack Report" :			
			if (!centerStackPane.getChildren().contains(datapackReportStackPane)) {
				ReportController reportControllerForDatapack = new ReportController();
				datapackReportStackPane.getChildren().add(reportControllerForDatapack.createReportGridPane("DATAPACK REPORT"));
				centerStackPane.getChildren().add(datapackReportStackPane);
			} else {
				datapackReportStackPane.toFront();
			}
			
			break;

		case "Upload" :	
			if (!centerStackPane.getChildren().contains(reportsUploadStackPane)) {
				ReportsUploadController uploadController = new ReportsUploadController();
				reportsUploadStackPane.getChildren().add(uploadController.createUploadReportGridPane(selectedMenu));
				centerStackPane.getChildren().add(reportsUploadStackPane);
			} else {
				reportsUploadStackPane.toFront();
			}
			
			break;
			
			
		case "Configuration":
			if (!centerStackPane.getChildren().contains(configurationStackPane)) {
				ConfigurationController configurationController = new ConfigurationController();
				configurationStackPane.getChildren().add(configurationController.createConfigurationGridPane());
				centerStackPane.getChildren().add(configurationStackPane);
			} else {
				configurationStackPane.toFront();
			}
			
			break;
			
		case "Log Book":
			if (!centerStackPane.getChildren().contains(logBookStackPane)) {
				LogBookController logBookController = new LogBookController();
				logBookStackPane.getChildren().add(logBookController.createLogBookMainGridPane());
				centerStackPane.getChildren().add(logBookStackPane);
			} else {
				logBookStackPane.toFront();
			}
			
			break;
			
		case "Show Terminal":
			terminalController.createTerminalPopup();
			break;

		case "End Session":
			if(StateMachine.getTestState() == TestState.PENDING) {				
				Response response = sessionManagement.endSession();
				if(response.getResponseCode() == 1) {
					clearAllData();
				}else {
					Notifications.showErrorAlert(response.getResponseMessage());
				}
			}else if(StateMachine.getTestState() == TestState.PAUSED){
				Notifications.showWarningAlert("Please stop "+StateMachine.getRunningTestName()+" test before ending session");
			}
			break ;			
		}
		
			
		

		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
			bottomMidTopGridPane.getChildren().add(centerStackPane);
		}
		
        StateMachine.userActionFlagProperty().addListener((observable, oldValue, newValue) ->{
        	if(newValue) {
        		Platform.runLater(() -> terminalController.createTerminalPopup());
        		StateMachine.getUserActionFlag().set(false);
        	}
        });
	}
	private void clearAllData() {
		StateMachine.resetStateMachine();
		SelfTestStateObject.resetSelfTestStateObject();
		LRUTestStateObject.resetLRUTestStateObject();
		SessionTestStateObject.resetSessionTestStateObject();
		AdvancedTestStateObject.resetAdvancedTestStateObject();
		
		Platform.exit();
	}

}
