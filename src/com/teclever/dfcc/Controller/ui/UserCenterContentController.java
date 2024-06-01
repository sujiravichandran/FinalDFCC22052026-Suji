package com.teclever.dfcc.Controller.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class UserCenterContentController {

	private StackPane centerStackPane = new StackPane();
	private StackPane dashboardStackPane = new StackPane();
	private StackPane testingStackPane = new StackPane();
	private StackPane resultsStackPane = new StackPane();
	private StackPane selfTestStackPane = new StackPane();
	private StackPane lruTestStackPane = new StackPane();

	private TerminalController terminalController = new TerminalController();

	public UserCenterContentController() {
		centerStackPane.getChildren().addAll(dashboardStackPane);
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
	
		case "Show Terminal":
			terminalController.createTerminalPopup();
			break;

		}

		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
			bottomMidTopGridPane.getChildren().add(centerStackPane);
		}
	}

}
