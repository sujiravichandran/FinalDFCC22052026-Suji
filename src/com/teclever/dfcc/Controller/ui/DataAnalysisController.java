package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;



public class DataAnalysisController {

	
	private GridPane dataAnalysisMainContainerGridPane = new GridPane();
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private GridPane closeButtonGridPane = new GridPane();
	private HBox closeButtonHbox = new HBox(10);
	private Button closeButton = new Button("Close");
	
	
	
	
	
	private Label pageHeading = new Label("DATA ANAYSIS");
	public GridPane createDashboardMainContainerGridPane() {

		dataAnalysisMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(60);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(10);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(5);

		dataAnalysisMainContainerGridPane.setVgap(5);

		System.out.println("Entred Dataanalysis");

		dataAnalysisMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dataAnalysisMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);
		dataAnalysisMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		dataAnalysisMainContainerGridPane.add(headingGridPane(), 0, 0);
		
		dataAnalysisMainContainerGridPane.add(closeButtonGridPane(), 0, 4);

		return dataAnalysisMainContainerGridPane;
	}
	
	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);
		headingGridPane.getStyleClass().add("selfTest-top-container");

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}
	
	private HBox headingHbox() {
		headingHbox.getStyleClass().add("dataanalysis-testing-right-container");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}
	
	public GridPane closeButtonGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		closeButtonGridPane.getColumnConstraints().addAll(firstColumn);
		closeButtonGridPane.getRowConstraints().add(firstRow);
		closeButtonGridPane.getStyleClass().add("selfTest-top-container");

		closeButtonGridPane.add(closeButtonHbox(), 0, 0);

		return closeButtonGridPane;
		
	}
	
	private HBox closeButtonHbox() {
		
		closeButtonHbox.getStyleClass().add("dataanalysis-testing-right-container");
		closeButtonHbox.setAlignment(Pos.CENTER);
		closeButtonHbox.getChildren().add(closeButton);
		closeButtonHbox.setMaxWidth(Double.MAX_VALUE);
		closeButton.setOnAction(e -> {
		    Stage stage = (Stage) closeButton.getScene().getWindow();
		    stage.close();
		});

		return closeButtonHbox;
	}
	
	
	
}
