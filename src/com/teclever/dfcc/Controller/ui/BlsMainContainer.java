package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class BlsMainContainer {
	private GridPane blsMainContainerGridPane = new GridPane();
	private BLSTempData bLSTempData = new BLSTempData();
	private BlsTemperatureController blsTemperatureController = new BlsTemperatureController();
	
	public GridPane createBlsMainContainerGridPane() {
		blsMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		blsMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		blsMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		blsMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
//			ManualTestingMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		blsMainContainerGridPane.add(bLSTempData.createBlsTempDataMainContainerGridPane(), 0, 0);
		blsMainContainerGridPane.add(blsTemperatureController.createBlsTempMainContainerGridPane(), 0, 1);

		return blsMainContainerGridPane;
	}
	
	
	
	
	
	
}
