package com.teclever.dfcc.Controller.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class BottomContainerController {
	
	private StackPane bottomStackPane = new StackPane();
	private Pane bottomPane = new Pane();
	private Pane bottomSubPane = new Pane();
	private GridPane bottomGridPane = new GridPane();
	
	public StackPane createBottomGridPane() {			
		bottomPane.prefWidthProperty().bind(bottomStackPane.prefWidthProperty());
		bottomPane.prefHeightProperty().bind(bottomStackPane.prefHeightProperty());
		bottomStackPane.getChildren().add(bottomPane);
		
		
	    ColumnConstraints columnLeft = new ColumnConstraints();
	    columnLeft.setPercentWidth(6);
		ColumnConstraints columnMid = new ColumnConstraints();
	    columnMid.setPercentWidth(88);
	    ColumnConstraints columnRight = new ColumnConstraints();
	    columnRight.setPercentWidth(6);
	
	    RowConstraints rowTop = new RowConstraints();
	    rowTop.setPercentHeight(15);
	    RowConstraints rowMid = new RowConstraints();
	    rowMid.setPercentHeight(70);
	    RowConstraints rowBottom = new RowConstraints();
	    rowBottom.setPercentHeight(15);
	
	    bottomGridPane.getColumnConstraints().addAll(columnLeft, columnMid, columnRight);
	    bottomGridPane.getRowConstraints().addAll(rowTop, rowMid, rowBottom);
	
	    bottomSubPane.prefWidthProperty().bind(columnMid.prefWidthProperty());
	    bottomSubPane.prefHeightProperty().bind(rowMid.prefHeightProperty());
	
//	    bottomSubPane.setStyle("-fx-background-color: #31363F;-fx-border-radius:10px;-fx-background-radius:10px");
	    bottomSubPane.getStyleClass().add("bottom-sub-container");
		bottomStackPane.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/BottomContainer.css").toExternalForm());


	    bottomGridPane.add(bottomSubPane, 1, 1);
	    
		    
	    bottomStackPane.getChildren().add(bottomGridPane);
		 
	    Image backgroundImage = new Image("/Resources/Images/TejasBG_3_1600x800.png");
	    ImageView backgroundImageView = new ImageView(backgroundImage);

		StackPane.setAlignment(backgroundImageView, Pos.TOP_LEFT);
		
	    LoginFormController loginFormController = new LoginFormController();
	
	   
	    bottomStackPane.getChildren().add(backgroundImageView);
	    bottomStackPane.getChildren().add(loginFormController.createLoginForm());
	    bottomStackPane.getChildren().add(loginFormController.getCheckSumData());


	    return bottomStackPane;
	}
}
