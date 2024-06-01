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

public class TopContainerAfterLoginController {
	
	private GridPane topGridPane = new GridPane();
	private ImageView belImageView = new ImageView();
	private ImageView tecleverImageView = new ImageView();
	private HBox belLogoHBox = new HBox();
	private HBox tecleverLogoHBox = new HBox();
	
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
        Image image = new Image(DFCCConstant.JARSTRING+"/Resources/Images/bell2.png"); 
        belImageView.setImage(image);
        belImageView.setFitWidth(250);
        belImageView.setFitHeight(90);
        belImageView.setCache(false);
       
        belLogoHBox.setAlignment(Pos.CENTER);
        belLogoHBox.getChildren().add(belImageView);

		return belLogoHBox;
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
        Image image = new Image(DFCCConstant.JARSTRING+"/Resources/Images/teclever.png"); 
        tecleverImageView.setImage(image);
        tecleverImageView.setFitWidth(200);
        tecleverImageView.setFitHeight(90);
       
        tecleverLogoHBox.setAlignment(Pos.CENTER);
        tecleverLogoHBox.getChildren().add(tecleverImageView);

		return tecleverLogoHBox;
	}
		
}
