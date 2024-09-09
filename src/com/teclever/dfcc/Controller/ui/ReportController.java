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

public class ReportController {

	private GridPane reportMainGridPane = new GridPane();
	private GridPane reportHeadingGridPane = new GridPane();
	private GridPane reportBottomGridPane = new GridPane();
	private GridPane reportBottomLeftGridPane = new GridPane();
	private GridPane reportBottomRightGridPane = new GridPane();
	
    private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Download");

	public GridPane createReportGridPane(String reportType) {
		reportMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
		reportMainGridPane.getStyleClass().add("report-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		reportMainGridPane.setPadding(new Insets(5));
		reportMainGridPane.getColumnConstraints().addAll(firstColumn);
		reportMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		reportMainGridPane.add(createHeadingBox(reportType), 0, 0);
		reportMainGridPane.add(createBottomGridPane(), 0, 1);

		return reportMainGridPane;
	}

	private GridPane createHeadingBox(String reportType) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		reportHeadingGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		reportHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText(reportType);
		title.getStyleClass().add("report-title");
		titleBox.getChildren().add(title);
		
		reportHeadingGridPane.add(titleBox, 0, 0);
		reportHeadingGridPane.add(createDownloadButton(), 1, 0);
		
		return reportHeadingGridPane;
	}
	
	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);
		
		downloadButton.setOnAction(e ->{

		});
		
		return buttonBox;
	}

	private GridPane createBottomGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		reportBottomGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		reportBottomGridPane.getRowConstraints().addAll(firstRow);
		reportBottomGridPane.setHgap(5);
		
		reportBottomGridPane.add(createLeftGridPane(), 0, 0);
		reportBottomGridPane.add(createRightGridPane(), 1, 0);
				
		return reportBottomGridPane;
	}

	private GridPane createLeftGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		reportBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
		reportBottomLeftGridPane.getRowConstraints().addAll(firstRow);
		
		ReportTreeviewController reportTreeviewController = new ReportTreeviewController();
		
		reportBottomLeftGridPane.add(reportTreeviewController.createReportTreeView(), 0, 0);		
		return reportBottomLeftGridPane;
	}

	private GridPane createRightGridPane() {
		reportBottomRightGridPane.setStyle("-fx-background-color:green;");
		return reportBottomRightGridPane;
	}

}






//package com.teclever.dfcc.Controller.ui;
//
//import com.teclever.dfcc.DFCCConstant;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//
//public class ReportPQTController {
//
//	private GridPane pqtResultMainGridPane = new GridPane();
//	private GridPane pqtResultHeadingGridPane = new GridPane();
//	private GridPane pqtResultBottomGridPane = new GridPane();
//	private GridPane pqtResultBottomLeftGridPane = new GridPane();
//	private GridPane pqtResultBottomRightGridPane = new GridPane();
//	
//    private HBox titleBox = new HBox();
//	private Label title = new Label();
//	private HBox buttonBox = new HBox();
//	private Button downloadButton = new Button("Download");
//
//	public GridPane createPQTResultGridPane(String reportType) {
//		pqtResultMainGridPane.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
//		pqtResultMainGridPane.getStyleClass().add("report-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		pqtResultMainGridPane.setPadding(new Insets(5));
//		pqtResultMainGridPane.getColumnConstraints().addAll(firstColumn);
//		pqtResultMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		pqtResultMainGridPane.add(createHeadingBox(reportType), 0, 0);
//		pqtResultMainGridPane.add(createBottomGridPane(), 0, 1);
//
//		return pqtResultMainGridPane;
//	}
//
//	private GridPane createHeadingBox(String reportType) {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultHeadingGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		pqtResultHeadingGridPane.getRowConstraints().addAll(firstRow);
//	
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		title.setText(reportType);
//		title.getStyleClass().add("report-title");
//		titleBox.getChildren().add(title);
//		
//		pqtResultHeadingGridPane.add(titleBox, 0, 0);
//		pqtResultHeadingGridPane.add(createDownloadButton(), 1, 0);
//		
//		return pqtResultHeadingGridPane;
//	}
//	
//	private HBox createDownloadButton() {
//		buttonBox.setAlignment(Pos.CENTER_RIGHT);
//		buttonBox.getChildren().add(downloadButton);
//		
//		downloadButton.setOnAction(e ->{
//
//		});
//		
//		return buttonBox;
//	}
//
//	private GridPane createBottomGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultBottomGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		pqtResultBottomGridPane.getRowConstraints().addAll(firstRow);
//		pqtResultBottomGridPane.setHgap(5);
//		
//		pqtResultBottomGridPane.add(createLeftGridPane(), 0, 0);
//		pqtResultBottomGridPane.add(createRightGridPane(), 1, 0);
//				
//		return pqtResultBottomGridPane;
//	}
//
//	private GridPane createLeftGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
//		pqtResultBottomLeftGridPane.getRowConstraints().addAll(firstRow);
//		
//		ReportTreeviewController reportTreeviewController = new ReportTreeviewController();
//		
//		pqtResultBottomLeftGridPane.add(reportTreeviewController.createReportTreeView(), 0, 0);		
//		return pqtResultBottomLeftGridPane;
//	}
//
//	private GridPane createRightGridPane() {
//		pqtResultBottomRightGridPane.setStyle("-fx-background-color:green;");
//		return pqtResultBottomRightGridPane;
//	}
//
//}
