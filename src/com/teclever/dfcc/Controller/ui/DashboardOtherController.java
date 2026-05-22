package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardOtherController {
	private GridPane otherDashBoardMainContainerGridPane = new GridPane();
	
	 private final Button dashBoardOtherButton = new Button("Build Config");
	 private final Button dashBoardFileUploadButton = new Button("File Upload / View");
	 private final Button dashBoardFailTypeButton = new Button("Fail Type");
	
	 private Stage popupStage;
	
	
	public GridPane createDashboardMainContainerGridPane() {

		otherDashBoardMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Dashboard.css").toExternalForm());
		otherDashBoardMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);


		otherDashBoardMainContainerGridPane.setVgap(5);


		otherDashBoardMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		otherDashBoardMainContainerGridPane.getRowConstraints().addAll(firstRow);
		
		otherDashBoardMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		otherDashBoardMainContainerGridPane.add(createDashBoardBuildButtonGridPane(), 0, 0);
	

		return otherDashBoardMainContainerGridPane;
	}
	
	
	 private GridPane createDashBoardBuildButtonGridPane() {

	        GridPane grid = new GridPane();

	        ColumnConstraints col = new ColumnConstraints();
	        col.setPercentWidth(50);
	        
	        ColumnConstraints col2 = new ColumnConstraints();
	        col.setPercentWidth(50);

	        RowConstraints row = new RowConstraints();
	        row.setPercentHeight(100);
	        
	        

	        grid.getColumnConstraints().addAll(col, col2);
	        grid.getRowConstraints().add(row);
	        grid.getStyleClass().add("selfTest-top-container");

	        HBox hbox = new HBox(dashBoardOtherButton);
	        grid.add(hbox, 0, 0);
	        
	        HBox hbox2 = new HBox(dashBoardFileUploadButton);
	        grid.add(hbox2, 1, 0);

	        dashBoardOtherButton.setOnAction(e -> 
	        
	        openPopupBuildConfig());
	        
	        dashBoardFileUploadButton.setOnAction(e -> 
	        
	        openFileUploadPopup());

	        return grid;
	    }

	    private void openPopupBuildConfig() {
	    	 StateMachine.setDashboardBuildConfig(true);
	        if (popupStage != null && popupStage.isShowing()) {
	            popupStage.toFront();
	            return;
	        }

	        // ✅ NEW controller EVERY TIME
	        BuildConfigurationController controller =
	                new BuildConfigurationController();

	        GridPane root =
	                controller.createBuildConfigurationMainContainerGridPane();
	        popupStage = new Stage();
	        popupStage.setTitle("Build Configuration");
	        popupStage.initModality(Modality.APPLICATION_MODAL);
	        popupStage.setMaximized(true);
	        popupStage.setScene(new Scene(root));
	        popupStage.initStyle(StageStyle.UNDECORATED);

	        popupStage.setOnHidden(e -> popupStage = null);
	        popupStage.show();
	    }
	    
	    private void openFileUploadPopup() {
	    	 StateMachine.setDashboardBuildConfig(true);
	        if (popupStage != null && popupStage.isShowing()) {
	            popupStage.toFront();
	            return;
	        }

	        // ✅ NEW controller EVERY TIME
	        DashboardFileUploadController controller =
	                new DashboardFileUploadController();

	        GridPane root =
	                controller.dashboardFilesUploadConfigParentGrid();
	        popupStage = new Stage();
	        popupStage.setTitle("File Upload");
	        popupStage.initModality(Modality.APPLICATION_MODAL);
	        popupStage.setMaximized(true);
	        popupStage.setScene(new Scene(root));
	        popupStage.initStyle(StageStyle.UNDECORATED);

	        popupStage.setOnHidden(e -> popupStage = null);
	        popupStage.show();
	    }
	
	
}
