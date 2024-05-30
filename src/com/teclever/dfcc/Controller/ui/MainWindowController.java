package com.teclever.dfcc.Controller.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class MainWindowController{


	@FXML
	private AnchorPane mainWindow;
	
		
	public void initialize() {
		mainWindow.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/MainWindow.css").toExternalForm());
		GridPane mainGridPane = new GridPane();
		mainGridPane.prefWidthProperty().bind(mainWindow.prefWidthProperty());
		mainGridPane.prefHeightProperty().bind(mainWindow.prefHeightProperty());
		
		mainGridPane.getStyleClass().add("main-container");

		ColumnConstraints columnLeft = new ColumnConstraints();
		columnLeft.setPercentWidth(0.5);
		ColumnConstraints columnMid = new ColumnConstraints();
		columnMid.setPercentWidth(99);
		ColumnConstraints columnRight = new ColumnConstraints();
		columnRight.setPercentWidth(0.5);

		RowConstraints rowTop = new RowConstraints();
		rowTop.setPercentHeight(1);
		RowConstraints rowMid = new RowConstraints();
		rowMid.setPercentHeight(97);
		RowConstraints rowBottom = new RowConstraints();
		rowBottom.setPercentHeight(2);
		
		
		mainGridPane.getColumnConstraints().addAll(columnLeft, columnMid, columnRight);
		mainGridPane.getRowConstraints().addAll(rowTop, rowMid, rowBottom);
		
		
		MainContainerController mainContainerController = new MainContainerController();
		mainGridPane.add(mainContainerController.createSubGridPane(), 1, 1);
	
		mainWindow.getChildren().add(mainGridPane);
	}
	
}