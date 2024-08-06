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

public class ConfigurationController {
	private GridPane configurationMainGridPane = new GridPane();
	private GridPane configurationHeadingGridPane = new GridPane();
	private GridPane configurationTabsGridpPane = new GridPane();
	
	private HBox titleBox = new HBox();
	private Label title = new Label();
	
	private TabPane configurationTabPane = new TabPane();
	
	private StackPane testingStackPane = new StackPane();
	private StackPane faultCodeStackPane = new StackPane();
	
	ConfigurationTestController configurationTestController = new ConfigurationTestController();
	FaultCodeController faultCodeController = new FaultCodeController();
	
	public GridPane createConfigurationGridPane() {
		configurationMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/Configuration.css").toExternalForm());
		configurationMainGridPane.getStyleClass().add("configuration-main-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		
		configurationMainGridPane.setPadding(new Insets(5));
		configurationMainGridPane.setVgap(5);
		configurationMainGridPane.setHgap(5);
		configurationMainGridPane.getColumnConstraints().addAll(firstColumn);
		configurationMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
		
		configurationMainGridPane.add(createHeadingBox(), 0, 0);
		configurationMainGridPane.add(createConfigurationTabsGridPane(), 0,1);
				
		return configurationMainGridPane;
	}
	
	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		configurationHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		configurationHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("AITESS CONFIGURATION");
		title.getStyleClass().add("headerLabel");
		titleBox.getChildren().add(title);
		
		configurationHeadingGridPane.add(titleBox, 0, 0);
		
		return configurationHeadingGridPane;
	}
		
		private GridPane createConfigurationTabsGridPane() {
			configurationTabsGridpPane.getStyleClass().add("configuration-tabs-container");
			
			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(100);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(100);
			
			configurationTabsGridpPane.setPadding(new Insets(5));
			
			configurationTabsGridpPane.getColumnConstraints().addAll(firstColumn);
			configurationTabsGridpPane.getRowConstraints().addAll(firstRow);
			
			configurationTabsGridpPane.add(createConfigurationTabs(), 0, 0);
			
			
			return configurationTabsGridpPane;
		}
		private TabPane createConfigurationTabs() {
		    Tab tab1 = new Tab("Testing");
		    Tab tab2 = new Tab("Fault Code");

		    
		    StackPane tab1StackPane = createTab1Content();
		    StackPane tab2StackPane = createTab2Content();

		    
		    tab1.setContent(tab1StackPane);
		    tab1.setClosable(false);

		    tab2.setContent(tab2StackPane);
		    tab2.setClosable(false);


		    configurationTabPane.getTabs().addAll(tab1, tab2);

		    configurationTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
		        if (newTab == tab2) {
		            showTab2Content();
		        } else if (newTab == tab1) {
		            showTab1Content();
		        }
		    });
		   
		    showTab1Content();

		    return configurationTabPane;
		}
		private void showTab1Content() {
			testingStackPane.toFront();
		}
		
		private void showTab2Content() {
			faultCodeStackPane.toFront();
		}
		
		private StackPane createTab1Content() {
			testingStackPane.getStyleClass().add("tab-content-container");
			testingStackPane.getChildren().add(configurationTestController.createConfigurationTestGridPane());
			return testingStackPane;
//		    hwatpTestStackPane.getStyleClass().add("tab-content-container");
//		    hwatpTestStackPane.getChildren().add(advancedTestingHWATPTesting.createAdvancedTestingTab1GridPane());
//		    return hwatpTestStackPane;
		}
		private StackPane createTab2Content() {
			faultCodeStackPane.getStyleClass().add("tab-content-container");
			faultCodeStackPane.getChildren().add(faultCodeController.createFaultCodeConfigGridPane());
			return faultCodeStackPane;
//		    hwatpTestStackPane.getStyleClass().add("tab-content-container");
//		    hwatpTestStackPane.getChildren().add(advancedTestingHWATPTesting.createAdvancedTestingTab1GridPane());
//		    return hwatpTestStackPane;
		}
	}
