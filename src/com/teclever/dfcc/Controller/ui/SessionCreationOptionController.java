package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SessionCreationOptionController {

	private GridPane sessionCreationOptionGridPane = new GridPane();
	private VBox sessionCreationOptionButtonVBox = new VBox(30);

	public GridPane createSessionOption() {
		sessionCreationOptionGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionCreation.css")
						.toExternalForm());
		sessionCreationOptionGridPane.getStyleClass().add("session-creation-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(40);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(20);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(20);

		sessionCreationOptionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		sessionCreationOptionGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		sessionCreationOptionGridPane.add(createOptionButton(), 1, 1);

		return sessionCreationOptionGridPane;
	}

	private VBox createOptionButton() {
		
		sessionCreationOptionButtonVBox.getStyleClass().add("session-creation-option-button-container");
		sessionCreationOptionButtonVBox.setPadding(new Insets(20));
		sessionCreationOptionButtonVBox.setAlignment(Pos.CENTER);
		
		Button newSessionButton = new Button("Create New Session");
		Button existingSessionButton = new Button("Open Existing Session");
		newSessionButton.setMaxWidth(Double.MAX_VALUE);
		existingSessionButton.setMaxWidth(Double.MAX_VALUE);
		
		newSessionButton.getStyleClass().add("custom-button");
		existingSessionButton.getStyleClass().add("custom-button");

		sessionCreationOptionButtonVBox.getChildren().addAll(newSessionButton, existingSessionButton);

		VBox.setVgrow(newSessionButton, Priority.ALWAYS);
		VBox.setVgrow(existingSessionButton, Priority.ALWAYS);
		
		newSessionButton.setOnAction(e ->{			
			handleSessionCreationPageType(true);
		});
		
		existingSessionButton.setOnAction(e ->{
			handleSessionCreationPageType(false);
		});

		return sessionCreationOptionButtonVBox;
	}

	private void handleSessionCreationPageType(boolean newSession) {
		StackPane parent = (StackPane) sessionCreationOptionGridPane.getParent();
		parent.getChildren().clear();
		SessionCreationController sessionCreationController=new SessionCreationController();
		parent.getChildren().add(sessionCreationController.createSession(newSession));	
	}

}
