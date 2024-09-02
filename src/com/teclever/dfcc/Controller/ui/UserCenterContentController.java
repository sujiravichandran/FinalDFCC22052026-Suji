package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
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
	private StackPane configurationStackPane = new StackPane();
	private StackPane logBookStackPane = new StackPane();
	
	private TerminalController terminalController = new TerminalController();

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
	public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu ,String id) {
		
//		GridPane main = (GridPane) bottomMidTopGridPane.getParent().getParent();
//		GridPane child = (GridPane) main.getChildren().get(0);
//		TreeView<Label> menuItem = (TreeView<Label>) child.getChildren().get(0);
//		VBox subMenuItem = (VBox) child.getChildren().get(1);
//
//		for (TreeItem<Label> childItem : menuItem.getRoot().getChildren()) {
//			System.out.println("---------------------");
//			Label item = childItem.getValue();
//			if (!childItem.getChildren().isEmpty()) {
//				for (TreeItem<Label> childSubItem : childItem.getChildren()) {
//					Label subItem = childItem.getValue();
//					System.out.println(menuItem.getSelectionModel().getSelectedItems());
//					System.out.println(childSubItem);
//					System.out.println(menuItem.getSelectionModel().getSelectedItems().contains(childSubItem));
//					  if (menuItem.getSelectionModel().getSelectedItems().contains(childSubItem)) {
//			                System.out.println("The sub-item is selected: " + subItem.getText());
//			            } else {
//			                System.out.println("The sub-item is not selected: " + subItem.getText());
//			            }
//				}
//			}
//		}

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
		
		case "Execution Results" :
			if (!centerStackPane.getChildren().contains(currentExecutionResultStackPane)) {
				CurrentExecutionResultController currentExcecutionResultController = new CurrentExecutionResultController();
				currentExecutionResultStackPane.getChildren().add(currentExcecutionResultController.createCurrentExecutionResultGridPane());
				centerStackPane.getChildren().add(currentExecutionResultStackPane);
			} else {
				currentExecutionResultStackPane.toFront();
			}
			
			break;
			
		case "Session Results" :
			CurrentSessionResultController currentSessionResultController = new CurrentSessionResultController();
			if (centerStackPane.getChildren().contains(currentSessionResultStackPane)) {
				currentSessionResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentSessionResultStackPane);
			}
			currentSessionResultStackPane.getChildren().add(currentSessionResultController.createCurrentSessionResultGridPane(id));
			centerStackPane.getChildren().add(currentSessionResultStackPane);	
			
			break;
			
		case "Unit Results" :
			if (!centerStackPane.getChildren().contains(currentUnitResultStackPane)) {
				CurrentUnitResultController currentUnitResultController = new CurrentUnitResultController();
				currentUnitResultStackPane.getChildren().add(currentUnitResultController.createcurrentUnitResultGridPane());
				centerStackPane.getChildren().add(currentUnitResultStackPane);
			} else {
				currentUnitResultStackPane.toFront();
			}
			
			break;
		case "Show Terminal":
			terminalController.createTerminalPopup();
			break;

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

}
