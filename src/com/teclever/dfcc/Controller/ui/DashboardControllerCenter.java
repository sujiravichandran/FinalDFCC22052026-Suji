package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

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

	private GridPane mainStackPane = new GridPane();
	private StackPane blsGraphStackPane = new StackPane();
	private StackPane buildConfigurationStackPane = new StackPane();
	private StackPane filesUploadStackPane = new StackPane();
	private StackPane failTypeStackPane = new StackPane();

	private DashboardController dashboardController = new DashboardController();
	private BlsTemperatureController blsTemperatureController = new BlsTemperatureController();
	private BuildConfigurationController buildConfigurationController = new BuildConfigurationController();
	private DashboardFileUploadController dashboardFileUploadController = new DashboardFileUploadController();
	private LinkTestTypesController linkTestTypesController = new LinkTestTypesController();

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
//		Tab tab1 = new Tab("Home");
		Tab tab2 = new Tab("Bls Temperature Graph");
		Tab tab3 = new Tab("Build Configuration");
		Tab tab4 = new Tab("Files Upload");
		Tab tab5 = new Tab("Fail Types");
//		StackPane tab1StackPane = createTab1Content();
		GridPane tab1StackPane = createTab1Content();
		StackPane tab2StackPane = createTab2Content();
		StackPane tab3StackPane = createTab3Content();
		StackPane tab4StackPane = createTab4Content();
		StackPane tab5StackPane = createTab5Content();
		
		
//		tab1.setContent(tab1StackPane);
//		tab1.setClosable(false);

		tab2.setContent(tab2StackPane);
		tab2.setClosable(false);

		tab3.setContent(tab3StackPane);
		tab3.setClosable(false);
		
		tab4.setContent(tab4StackPane);
		tab4.setClosable(false);
		
		tab5.setContent(tab5StackPane);
		tab5.setClosable(false);
		

		
		if (currentSessionDetails.getSessionTypeID().equals("ST2")) {
			dashboardTabPane.getTabs().addAll( tab2,tab3, tab4, tab5);
		}else {
			dashboardTabPane.getTabs().addAll(tab3, tab4, tab5);
		}
		

		dashboardTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
			if (newTab == tab2) {
				showTab2Content();
			} else if (newTab == tab3) {
				showTab3Content();
			} else if (newTab == tab4) {
				showTab4Content();
				
			} else if (newTab == tab5) {
				showTab5Content();
				
			} 
		});

		showTab2Content();

		return dashboardTabPane;
	}

//	private void showTab1Content() {
//		mainStackPane.toFront();
//	}

	private void showTab2Content() {
		blsGraphStackPane.toFront();
	}
	
	private void showTab3Content() {
		buildConfigurationStackPane.toFront();
	}
	
	private void showTab4Content() {
		filesUploadStackPane.toFront();
	}
	
	private void showTab5Content() {
		failTypeStackPane.toFront();
	}

	private GridPane createTab1Content() {
		mainStackPane.getStyleClass().add("tab-content-main-container");
		mainStackPane.getChildren().add(dashboardController.createDashboardMainContainerGridPane());
		return mainStackPane;
	}

	private StackPane createTab2Content() {
		blsGraphStackPane.getStyleClass().add("tab-content-main-container");
		blsGraphStackPane.getChildren().add(blsTemperatureController.createBlsTempMainContainerGridPane());
		return blsGraphStackPane;
	}
	
	
	private StackPane createTab3Content() {
		DFCCConstant.buildDashboard = true;
		buildConfigurationStackPane.getStyleClass().add("tab-content-main-container");
		buildConfigurationStackPane.getChildren().add(buildConfigurationController.createBuildConfigurationMainContainerGridPane());
		return buildConfigurationStackPane;
	}
	
	private StackPane createTab4Content() {
		filesUploadStackPane.getStyleClass().add("tab-content-main-container");
		filesUploadStackPane.getChildren().add(dashboardFileUploadController.dashboardFilesUploadConfigParentGrid());
		return filesUploadStackPane;
	}
	
	private StackPane createTab5Content() {
		DFCCConstant.linkTestType = true;
		failTypeStackPane.getStyleClass().add("tab-content-main-container");
		failTypeStackPane.getChildren().add(linkTestTypesController.createlinkTestTypesMainContainerGridPane());
		return failTypeStackPane;
	}

}
