package com.teclever.dfcc.Controller.ui;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class MainContainerController {
	
	private GridPane subGridPane = new GridPane();
	private final TopContainerController topContainerController = new TopContainerController();
	private final BottomContainerController bottomContainerController = new BottomContainerController();

	public GridPane createSubGridPane() {
		subGridPane.setVgap(10);
		subGridPane.setId("mainContainer");
		ColumnConstraints subColumn = new ColumnConstraints();
		subColumn.setPercentWidth(100);

		RowConstraints subRowTop = new RowConstraints();
		subRowTop.setPercentHeight(15);
		RowConstraints subRowBottom = new RowConstraints();
		subRowBottom.setPercentHeight(85);

		subGridPane.getColumnConstraints().add(subColumn);
		subGridPane.getRowConstraints().addAll(subRowTop, subRowBottom);
			
		subGridPane.add(topContainerController.createTopGridPane(), 0, 0);
		subGridPane.add(bottomContainerController.createBottomGridPane(), 0, 1);

		return subGridPane;
	}
}
