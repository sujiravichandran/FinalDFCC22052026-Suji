package com.teclever.dfcc.Controller.ui;

import java.io.InputStream;

import com.teclever.dfcc.DFCCConstant;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class SQDashboardController {
	
	
	private GridPane dashBoardMainContainerGridPane = new GridPane();
	private GridPane headingGridPane = new GridPane();
	public GridPane createDashboardMainContainerGridPane() {

	    // Add CSS
	    dashBoardMainContainerGridPane.getStylesheets().add(
	            getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Dashboard.css").toExternalForm()
	    );
	    dashBoardMainContainerGridPane.getStyleClass().add("dashboard-main-container");

	    // Column & Row constraints (100% each)
	    ColumnConstraints firstColumn = new ColumnConstraints();
	    firstColumn.setPercentWidth(100);

	    RowConstraints firstRow = new RowConstraints();
	    firstRow.setPercentHeight(100);

	    dashBoardMainContainerGridPane.setVgap(5);
	    dashBoardMainContainerGridPane.getColumnConstraints().add(firstColumn);
	    dashBoardMainContainerGridPane.getRowConstraints().add(firstRow);
	    dashBoardMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));

	    // Load image and add directly to main GridPane
	    Platform.runLater(() -> {
	        // Clear previous children (if any)
	        dashBoardMainContainerGridPane.getChildren().clear();

	        // Load image from classpath
	        InputStream stream = getClass().getResourceAsStream(
	                DFCCConstant.JARSTRING + "/Resources/Images/Reggie2.png"
	        );

	        if (stream == null) {
	            //System.out.println("❌ Image not found. Check path and Resources folder!");
	            return;
	        }

	        Image image = new Image(stream);
	        ImageView imageView = new ImageView(image);

	        imageView.setPreserveRatio(true);

	        // Bind image size to main GridPane
	        imageView.setFitWidth(1234);  // max width
	        imageView.setFitHeight(1303); // max height
	        imageView.setPreserveRatio(true);

	        // Add image to the main GridPane
	        dashBoardMainContainerGridPane.getChildren().add(imageView);

	        // Align image to bottom center
	        GridPane.setValignment(imageView, VPos.BOTTOM);
	        GridPane.setHalignment(imageView, HPos.CENTER);
	    });

	    return dashBoardMainContainerGridPane;
	}
	
	
	
}
