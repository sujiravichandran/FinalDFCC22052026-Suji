package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

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

public class AdvancedDataAnalysisManualTestingController {
	private GridPane ManualTestingMainContainerGridPane = new GridPane();
	private GridPane manualTestingTabsGridpPane = new GridPane();
	private TabPane manualTestingTabPane = new TabPane();
	
	private StackPane ch1StackPane = new StackPane();
	private StackPane ch2StackPane = new StackPane();
	private StackPane ch3StackPane = new StackPane();
	private StackPane ch4StackPane = new StackPane();
	private StackPane powerManStackPane = new StackPane();
	private StackPane powerAutoStackPane = new StackPane();
	private StackPane modeCodeStackPane = new StackPane();
	
	
	ManualTesting1553Ch1 manualTesting1553Ch1 = new ManualTesting1553Ch1();
	ManualTesting1553Ch2 manualTesting1553Ch2 = new ManualTesting1553Ch2();
	ManualTesting1553Ch3 manualTesting1553Ch3 = new ManualTesting1553Ch3();
	ManualTesting1553Ch4 manualTesting1553Ch4 = new ManualTesting1553Ch4();
	ManualTestingPowerMan manualTestingPowerMan = new ManualTestingPowerMan();
	ManualTestingPowerAuto manualTestingPowerAuto = new ManualTestingPowerAuto();	
	ManualTesting1553BInterface manualTesting1553BInterface = new ManualTesting1553BInterface();
//	private HBox headingHbox = new HBox(10);
//	private GridPane headingGridPane = new GridPane();
//	private Label pageHeading = new Label("Manual Testing");

	public GridPane createManualTestingMainContainerGridPane() {
		ManualTestingMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		ManualTestingMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);


		ManualTestingMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		ManualTestingMainContainerGridPane.getRowConstraints().addAll(firstRow);
//		ManualTestingMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		ManualTestingMainContainerGridPane.add(createManualTestingTabsGridPane(), 0, 0);

		return ManualTestingMainContainerGridPane;
	}

	private GridPane createManualTestingTabsGridPane() {
//		manualTestingTabsGridpPane.getStyleClass().add("advanced-testing-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
//		manualTestingTabsGridpPane.setPadding(new Insets(5));
		
		manualTestingTabsGridpPane.getColumnConstraints().addAll(firstColumn);
		manualTestingTabsGridpPane.getRowConstraints().addAll(firstRow);
		
		manualTestingTabsGridpPane.add(createManualTestingTabs(), 0, 0);
		
		return manualTestingTabsGridpPane;
	}

	private TabPane createManualTestingTabs() {
	    Tab tab1 = new Tab("1553-TTG-CH1");
	    Tab tab2 = new Tab("1553-TTG-CH2");
	    Tab tab3 = new Tab("1553-TTG-CH3");
	    Tab tab4 = new Tab("1553-TTG-CH4");
	    Tab tab5 = new Tab("Power Man");
	    Tab tab6 = new Tab("Power Auto");
	    Tab tab7 = new Tab("1553-Mode Code");
	   
	   
	    
	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content();
	    StackPane tab3StackPane = createTab3Content();
	    StackPane tab4StackPane = createTab4Content();
	    StackPane tab5StackPane = createTab5Content();
	    StackPane tab6StackPane = createTab6Content();
	    StackPane tab7StackPane = createTab7Content();
//	    
	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);

	    tab3.setContent(tab3StackPane);
	    tab3.setClosable(false);

	    tab4.setContent(tab4StackPane);
	    tab4.setClosable(false);
	    
	    tab5.setContent(tab5StackPane);
	    tab5.setClosable(false);
	    
	    tab6.setContent(tab6StackPane);
	    tab6.setClosable(false);
	    
	    tab7.setContent(tab7StackPane);
	    tab7.setClosable(false);

 if(StateMachine.currentSessionDetails.getUutType().equals("DFCC-MK1")) {
	 manualTestingTabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6, tab7);
	    }else {
	    	manualTestingTabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6);
	    }
	    
	    

	    manualTestingTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        } else if (newTab == tab3) {
	            showTab3Content();
	        }else if (newTab == tab4) {
	            showTab4Content();
	        } else if (newTab == tab5) {
	            showTab5Content();
	        } else if (newTab == tab6) {
	            showTab6Content();
	        } else if (newTab == tab7) {
	            showTab7Content();
	        }else {
	            showTab1Content();
	        }
	    });

	    showTab1Content();

	    return manualTestingTabPane;
	}

	private void showTab1Content() {
		ch1StackPane.toFront();
	}

	private void showTab2Content() {
		ch2StackPane.toFront();
	}

	private void showTab3Content() {
		ch3StackPane.toFront();
	}
	
	private void showTab4Content() {
		ch4StackPane.toFront();
	}
	
	private void showTab5Content() {
		powerManStackPane.toFront();
	}
	
	private void showTab6Content() {
		powerAutoStackPane.toFront();
	}
	
	private void showTab7Content() {
		modeCodeStackPane.toFront();
	}

	private StackPane createTab1Content() {
		ch1StackPane.getStyleClass().add("tab-content-main-container");
		ch1StackPane.getChildren().add(manualTesting1553Ch1.createlinkFilesMainContainerGridPane());
	    return ch1StackPane;
	}

	private StackPane createTab2Content() {
		ch2StackPane.getStyleClass().add("tab-content-main-container");
		ch2StackPane.getChildren().add(manualTesting1553Ch2.createlinkFilesMainContainerGridPane());
	    return ch2StackPane;
	}
	
	private StackPane createTab3Content() {
		ch3StackPane.getStyleClass().add("tab-content-main-container");
		ch3StackPane.getChildren().add(manualTesting1553Ch3.createlinkFilesMainContainerGridPane());
	    return ch3StackPane;
	}
	
	private StackPane createTab4Content() {
		ch4StackPane.getStyleClass().add("tab-content-main-container");
		ch4StackPane.getChildren().add(manualTesting1553Ch4.createlinkFilesMainContainerGridPane());
	    return ch4StackPane;
	}


	private StackPane createTab5Content() {
		powerManStackPane.getStyleClass().add("tab-content-main-container");
		powerManStackPane.getChildren().add(manualTestingPowerMan.createlinkFilesMainContainerGridPane());
	    return powerManStackPane;
	}

	private StackPane createTab6Content() {
		powerAutoStackPane.getStyleClass().add("tab-content-main-container");
		powerAutoStackPane.getChildren().add(manualTestingPowerAuto.createPowerAutoMainContainerGridPane());
	    return powerAutoStackPane;
	}

	
	private StackPane createTab7Content() {
		modeCodeStackPane.getStyleClass().add("tab-content-main-container");
		modeCodeStackPane.getChildren().add(manualTesting1553BInterface.create1553BMainContainerGridPane());
	    return modeCodeStackPane;
	}
	

}
