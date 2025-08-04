package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private final SystemConfigManagement systemConfigManagement = new SystemConfigManagement();
	
	
	
	public StackPane createBottomGridPane() {	
		bottomStackPane.setId("bottomStackpane");
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
	
	    bottomSubPane.getStyleClass().add("bottom-sub-container");
		bottomStackPane.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/BottomContainer.css").toExternalForm());


	    bottomGridPane.add(bottomSubPane, 1, 1);
	    
		    
	    bottomStackPane.getChildren().add(bottomGridPane);
		 
	    Image backgroundImage = new Image(DFCCConstant.JARSTRING+"/Resources/Images/TejasBG_3_1600x800.png");
	    ImageView backgroundImageView = new ImageView(backgroundImage);

		StackPane.setAlignment(backgroundImageView, Pos.TOP_LEFT);
		
	    LoginFormController loginFormController = new LoginFormController();
	
	   
	    bottomStackPane.getChildren().add(backgroundImageView);
	    bottomStackPane.getChildren().add(loginFormController.createLoginForm());
	    
	    
	 // Edited By: SUJI
//		Change Made for Point:103(Mail:7-Jul-Observations_in_testing_Teclever_Date_SAT))
//		Change Made on successful macro execution pop-up message
	    ValidateResponse checkSumData = systemConfigManagement.validateConfig();
		List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
		List<String> allCheckSums = new ArrayList<>();
		
		if (checkSumData.getResponse().getResponseCode() == 1) {
			for (CheckSum data : checkSumDataList) {
				allCheckSums.add(data.getMsg());
			}
		
		}
		
		boolean allOk = allCheckSums.stream()
			    .allMatch(msg -> msg != null && msg.trim().equalsIgnoreCase("OK"));

			if (!allOk) {
			    bottomStackPane.getChildren().add(loginFormController.getCheckSumData());
			}
		 loginFormController.getCheckSumData();
		 
//			Exit;
//			Point: 103
	    return bottomStackPane;
	}
}
