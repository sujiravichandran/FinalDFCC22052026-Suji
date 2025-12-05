package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class DashboardControllerCenter {

	private GridPane dashboardCenterMainContainerGridPane = new GridPane();
	private GridPane dashboardTabsGridpPane = new GridPane();
	private TabPane dashboardTabPane = new TabPane();
	
	private StackPane mainStackPane = new StackPane();
	private StackPane blstackPane = new StackPane();


	private DashboardController dashboardController = new DashboardController();
	private BlsMainContainer blsMainContainer = new BlsMainContainer();	
	
	public GridPane createDashboardCenterMainContainerGridPane() {
		dashboardCenterMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		dashboardCenterMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);


		dashboardCenterMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dashboardCenterMainContainerGridPane.getRowConstraints().addAll(firstRow);
//		ManualTestingMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		dashboardCenterMainContainerGridPane.add(createManualTestingTabsGridPane(), 0, 0);

		return dashboardCenterMainContainerGridPane;
	}
	
	private GridPane createManualTestingTabsGridPane() {
//		manualTestingTabsGridpPane.getStyleClass().add("advanced-testing-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
//		manualTestingTabsGridpPane.setPadding(new Insets(5));
		
		dashboardTabsGridpPane.getColumnConstraints().addAll(firstColumn);
		dashboardTabsGridpPane.getRowConstraints().addAll(firstRow);
		
		dashboardTabsGridpPane.add(createManualTestingTabs(), 0, 0);
		
		return dashboardTabsGridpPane;
	}

	
	
	private TabPane createManualTestingTabs() {
	    Tab tab1 = new Tab("Results");
	    Tab tab2 = new Tab("BLS");
	    Tab tab3 = new Tab("temp");

	   
	   
	    
	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content();
	 
//	    
	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);



 if(StateMachine.currentSessionDetails.getUutType().equals("DFCC-MK1")) {
	 dashboardTabPane.getTabs().addAll(tab1, tab2);
	    }else {
	    	dashboardTabPane.getTabs().addAll(tab1, tab2);
	    }
	    
	    

 dashboardTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        }else if(newTab == tab3){
	        	
	        }else {
	            showTab1Content();
	        }
	    });

	    showTab1Content();

	    return dashboardTabPane;
	}

	private void showTab1Content() {
		mainStackPane.toFront();
	}
	
	private void showTab2Content() {
		blstackPane.toFront();
	}

	

	private StackPane createTab1Content() {
		mainStackPane.getStyleClass().add("tab-content-main-container");
		mainStackPane.getChildren().add(dashboardController.createDashboardMainContainerGridPane());
	    return mainStackPane;
	}

	private StackPane createTab2Content() {
		blstackPane.getStyleClass().add("tab-content-main-container");
		blstackPane.getChildren().add(blsMainContainer.createBlsMainContainerGridPane());
	    return blstackPane;
	}

	


	
	
	
	
	
}
