package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class DataAnalysisUnitFailureController {
	private GridPane dataAnalysisUnitFailureMainContainerGridPane = new GridPane();
	
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("Unit Failures");
	
	public GridPane createDashboardMainContainerGridPane() {

		dataAnalysisUnitFailureMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisUnitFailureMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		
		dataAnalysisUnitFailureMainContainerGridPane.setHgap(10);
		dataAnalysisUnitFailureMainContainerGridPane.setVgap(10);

		dataAnalysisUnitFailureMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dataAnalysisUnitFailureMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
		dataAnalysisUnitFailureMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));
		
		dataAnalysisUnitFailureMainContainerGridPane.add(headingGridPane(), 0, 0, 2, 1);
		
	

		return dataAnalysisUnitFailureMainContainerGridPane;
	}
	
	
	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		headingHbox.getStyleClass().add("dataanalysis-testing-right-container");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

}
