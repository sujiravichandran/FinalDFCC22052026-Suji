package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class TopContainerController {
	
	private GridPane topGridPane = new GridPane();
	
	public GridPane createTopGridPane() {
		topGridPane.setId("topContainer");
		topGridPane.getStylesheets().add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/TopContainer.css").toExternalForm());
		
		ColumnConstraints topLeftColumn = new ColumnConstraints();
		topLeftColumn.setPercentWidth(20);
		
		ColumnConstraints topMidColumn = new ColumnConstraints();
		topMidColumn.setPercentWidth(60);
		
		ColumnConstraints topRightColumn = new ColumnConstraints();
		topRightColumn.setPercentWidth(20);
		
		RowConstraints topRow = new RowConstraints();
		topRow.setPercentHeight(100);
		
		topGridPane.getColumnConstraints().addAll(topLeftColumn,topMidColumn,topRightColumn);
		topGridPane.getRowConstraints().add(topRow);
				
		topGridPane.add(createBelLogo(), 0, 0);
		topGridPane.add(createTitle(), 1, 0);
		topGridPane.add(createTecleverLogo(), 2, 0);
		topGridPane.getStyleClass().add("top-container");

		return topGridPane;		
	}
	
	private HBox createBelLogo() {
		ImageView imageView = new ImageView();
        Image image = new Image(DFCCConstant.JARSTRING+"/Resources/Images/bell2.png"); 
        imageView.setImage(image);
        imageView.setFitWidth(350);
        imageView.setFitHeight(120);
       
        HBox belLogo = new HBox();
        belLogo.setAlignment(Pos.CENTER);
        belLogo.getChildren().add(imageView);

		return belLogo;
	}
	
	private VBox createTitle() {
		Label heading = new Label("DFCC TESTING AND DATA HANDLING SOFTWARE");
	    Label subHeading = new Label("DESIGNED AND DEVELOPED BY TECLEVER SOLUTIONS");
	    heading.getStyleClass().add("companyName");
	    subHeading.getStyleClass().add("companyNameSub");
	    
		VBox companyName = new VBox(10);
		companyName.setAlignment(Pos.CENTER);
		companyName.getChildren().addAll(heading,subHeading);
		return companyName;
	}

	private HBox createTecleverLogo() {
		ImageView imageView = new ImageView();
        Image image = new Image(DFCCConstant.JARSTRING+"/Resources/Images/teclever.png"); 
        imageView.setImage(image);
        imageView.setFitWidth(350);
        imageView.setFitHeight(120);
       
        HBox tecleverLogo = new HBox();
        tecleverLogo.setAlignment(Pos.CENTER);
        tecleverLogo.getChildren().add(imageView);

		return tecleverLogo;
	}
}
