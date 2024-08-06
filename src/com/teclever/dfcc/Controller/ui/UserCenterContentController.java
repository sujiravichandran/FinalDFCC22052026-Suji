package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class UserCenterContentController {

	private StackPane centerStackPane = new StackPane();
	private StackPane dashboardStackPane = new StackPane();
	private StackPane testingStackPane = new StackPane();
	private StackPane resultsStackPane = new StackPane();
	private StackPane selfTestStackPane = new StackPane();
	private StackPane lruTestStackPane = new StackPane();
	private StackPane sessionTestingStackPane = new StackPane();
	private StackPane advancedTestingStackPane = new StackPane();
	private StackPane currentExecutionResultStackPane = new StackPane();
	private StackPane configurationStackPane = new StackPane();
	
	private TerminalController terminalController = new TerminalController();

	public UserCenterContentController() {
		TerminalPopupController terminalPopupController = new TerminalPopupController();
		centerStackPane.getChildren().addAll(dashboardStackPane);
		terminalController.launchTerminal();
	}

	public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu) {
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
				sessionTestingStackPane.getChildren().add(sessionTestingController.createSessionTestingGridPane());
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
			
		case "Configuration":
			if (!centerStackPane.getChildren().contains(configurationStackPane)) {
				ConfigurationController configurationController = new ConfigurationController();
				configurationStackPane.getChildren().add(configurationController.createConfigurationGridPane());
				centerStackPane.getChildren().add(configurationStackPane);
			} else {
				configurationStackPane.toFront();
			}
			
			break;
		
		case "Current Execution Results" :
			if (!centerStackPane.getChildren().contains(currentExecutionResultStackPane)) {
				CurrentExecutionResultController currentExcecutionResultController = new CurrentExecutionResultController();
				currentExecutionResultStackPane.getChildren().add(currentExcecutionResultController.createCurrentExecutionResultGridPane());
				centerStackPane.getChildren().add(currentExecutionResultStackPane);
			} else {
				currentExecutionResultStackPane.toFront();
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
