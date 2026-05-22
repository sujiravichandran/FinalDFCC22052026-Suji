package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class AdvancedResultsController {

	
private GridPane dataAnalysisAdvancedResultsMainContainerGridPane = new GridPane();



	
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("Advanced Results");
	
	private TabPane adaResultsTabPane = new TabPane();
	private StackPane filter1StackPane = new StackPane();
	private StackPane filter2StackPane = new StackPane();
	private StackPane filter3StackPane = new StackPane();
	
	private AdavncedDataAnalysisFilter1TableController adavncedDataAnalysisFilter1TableController = new AdavncedDataAnalysisFilter1TableController();
	private AdavncedDataAnalysisFilter2TableController adavncedDataAnalysisFilter2TableController = new AdavncedDataAnalysisFilter2TableController();
	private AdvancedDataAnalyisiFilterTableDateTimeController advancedDataAnalyisiFilterTableDateTimeController = new AdvancedDataAnalyisiFilterTableDateTimeController();

	
	public GridPane createAdvancedResultsMainContainerGridPane() {
		dataAnalysisAdvancedResultsMainContainerGridPane.getStylesheets().add(getClass()
			.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		dataAnalysisAdvancedResultsMainContainerGridPane.getStyleClass().add("dashboard-main-container");

	ColumnConstraints firstColumn = new ColumnConstraints();
	firstColumn.setPercentWidth(100);
	
	RowConstraints firstRow = new RowConstraints();
	firstRow.setPercentHeight(100);
	
	
	dataAnalysisAdvancedResultsMainContainerGridPane.setHgap(10);
	dataAnalysisAdvancedResultsMainContainerGridPane.setVgap(10);

	dataAnalysisAdvancedResultsMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
	dataAnalysisAdvancedResultsMainContainerGridPane.getRowConstraints().addAll(firstRow);
	dataAnalysisAdvancedResultsMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));
	
	dataAnalysisAdvancedResultsMainContainerGridPane.add(createManualTestingTabs(), 0, 0);
	


	return dataAnalysisAdvancedResultsMainContainerGridPane;
}


	private TabPane createManualTestingTabs() {
	    Tab tab1 = new Tab("Filter Table1");
	    Tab tab2 = new Tab("Filter Table2");
	    Tab tab3 = new Tab("Filter Date&Time");

	   
	   
	    
	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content(); 
	    StackPane tab3StackPane = createTab3Content();

//	    
	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);
	    
	    tab3.setContent(tab3StackPane);
	    tab3.setClosable(false);

	
	    
	    
	    adaResultsTabPane.getTabs().addAll(tab1, tab2, tab3);
	    

	    adaResultsTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        }
	        if (newTab == tab3) {
	            showTab3Content();
	        } 
	        else {
	            showTab1Content();
	        }
	    });

	    showTab1Content();

	    return adaResultsTabPane;
	}

	private void showTab1Content() {
		filter1StackPane.toFront();
	}

	private void showTab2Content() {
		filter2StackPane.toFront();
	}
	
	private void showTab3Content() {
		filter3StackPane.toFront();
	}


	private StackPane createTab1Content() {
		filter1StackPane.getStyleClass().add("tab-content-main-container");
		filter1StackPane.getChildren().add(adavncedDataAnalysisFilter1TableController.createFilter1TableContainerGridPane());
	    return filter1StackPane;
	}

	private StackPane createTab2Content() {
		filter2StackPane.getStyleClass().add("tab-content-main-container");
		filter2StackPane.getChildren().add(adavncedDataAnalysisFilter2TableController.createFilter2TableContainerGridPane());
	    return filter2StackPane;
	}
	
	private StackPane createTab3Content() {
		filter3StackPane.getStyleClass().add("tab-content-main-container");
		filter3StackPane.getChildren().add(advancedDataAnalyisiFilterTableDateTimeController.createFilterDateTimeTableContainerGridPane());
	    return filter3StackPane;
	}
	
	
	
	
	
	
	
	
	
	
	
}
