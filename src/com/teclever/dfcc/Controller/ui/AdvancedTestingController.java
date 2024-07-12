package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.TestSummary;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class AdvancedTestingController {

	private GridPane advancedTestingMainGridPane = new GridPane();
	private GridPane advancedTestingHeadingGridPane = new GridPane();
	private GridPane advancedTestingTabsGridpPane = new GridPane();
	private GridPane advancedTestingResultsGridPane = new GridPane();
	
	private HBox titleBox = new HBox();
	private Label title = new Label();
	
	private TabPane advancedTestingTabPane = new TabPane();
	
	
	private StackPane hwatpTestStackPane = new StackPane();
	private StackPane interfaceTestStackPane = new StackPane();
	private StackPane customTest1StackPane = new StackPane();
	private StackPane customTest2StackPane = new StackPane();
	
	AdvancedTestingHWATPTesting advancedTestingHWATPTesting = new AdvancedTestingHWATPTesting();
	AdvancedTestingInterfaceTesting advancedTestingInterfaceTesting = new AdvancedTestingInterfaceTesting();
	AdvancedTestingCustomTesting1 advancedTestingCustomTesting1 = new AdvancedTestingCustomTesting1();
	AdvancedTestingCustomTesting2 advancedTestingCustomTesting2 = new AdvancedTestingCustomTesting2();
	
	public GridPane createAdvancedTestingGridPane() {
		advancedTestingMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AdvancedTesting.css").toExternalForm());
		advancedTestingMainGridPane.getStyleClass().add("advanced-testing-container");
	
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);
		
		advancedTestingMainGridPane.setPadding(new Insets(5));
		advancedTestingMainGridPane.setVgap(5);
		advancedTestingMainGridPane.setHgap(5);
		advancedTestingMainGridPane.getColumnConstraints().addAll(firstColumn);
		advancedTestingMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		
		advancedTestingMainGridPane.add(createHeadingBox(), 0, 0);
		advancedTestingMainGridPane.add(createAdvancedTestingTabsGridPane(), 0, 1);
		advancedTestingMainGridPane.add(createAdvancedTestingResultsGridPane(), 0, 2);
				
		
		return advancedTestingMainGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		advancedTestingHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		advancedTestingHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("ADVANCED TESTING");
		title.getStyleClass().add("advanced-testing-title");
		titleBox.getChildren().add(title);
		
		advancedTestingHeadingGridPane.add(titleBox, 0, 0);
		
		return advancedTestingHeadingGridPane;
	}

	private GridPane createAdvancedTestingTabsGridPane() {
		advancedTestingTabsGridpPane.getStyleClass().add("advanced-testing-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		advancedTestingTabsGridpPane.setPadding(new Insets(5));
		
		advancedTestingTabsGridpPane.getColumnConstraints().addAll(firstColumn);
		advancedTestingTabsGridpPane.getRowConstraints().addAll(firstRow);
		
		advancedTestingTabsGridpPane.add(createAdvancedTestingTabs(), 0, 0);
		
		return advancedTestingTabsGridpPane;
	}

	private TabPane createAdvancedTestingTabs() {
	    Tab tab1 = new Tab("HWATP / HSI Testing");
	    Tab tab2 = new Tab("Interface Testing");
	    Tab tab3 = new Tab("Custom Testing-1");
	    Tab tab4 = new Tab("Custom Testing-2");
	    
	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content();
	    StackPane tab3StackPane = createTab3Content();
	    StackPane tab4StackPane = createTab4Content();
	    
	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);

	    tab3.setContent(tab3StackPane);
	    tab3.setClosable(false);

	    tab4.setContent(tab4StackPane);
	    tab4.setClosable(false);

	    advancedTestingTabPane.getTabs().addAll(tab1, tab2, tab3, tab4);

	    advancedTestingTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        } else if (newTab == tab3) {
	            showTab3Content();
	        }else if (newTab == tab4) {
	            showTab4Content();
	        } else {
	            showTab1Content();
	        }
	    });

	    showTab1Content();

	    return advancedTestingTabPane;
	}

	private void showTab1Content() {
	    hwatpTestStackPane.toFront();
	}

	private void showTab2Content() {
	    interfaceTestStackPane.toFront();
	}

	private void showTab3Content() {
	    customTest1StackPane.toFront();
	}
	
	private void showTab4Content() {
	    customTest2StackPane.toFront();
	}

	private StackPane createTab1Content() {
	    hwatpTestStackPane.getStyleClass().add("tab-content-container");
	    hwatpTestStackPane.getChildren().add(advancedTestingHWATPTesting.createAdvancedTestingTab1GridPane());
	    return hwatpTestStackPane;
	}

	private StackPane createTab2Content() {
	    interfaceTestStackPane.getStyleClass().add("tab-content-container");
	    interfaceTestStackPane.getChildren().add(advancedTestingInterfaceTesting.createAdvancedTestingTab2GridPane());
	    return interfaceTestStackPane;
	}

	private StackPane createTab3Content() {
	    customTest1StackPane.getStyleClass().add("tab-content-container");
	    customTest1StackPane.getChildren().add(advancedTestingCustomTesting1.createAdvancedTestingTab3GridPane());
	    return customTest1StackPane;
	}

	private StackPane createTab4Content() {
		customTest2StackPane.getStyleClass().add("tab-content-container");
		customTest2StackPane.getChildren().add(advancedTestingCustomTesting2.createAdvancedTestingTab4GridPane());
	    return customTest2StackPane;
	}

	private GridPane createAdvancedTestingResultsGridPane() {
		advancedTestingResultsGridPane.getStyleClass().add("advanced-testing-result-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		advancedTestingResultsGridPane.getColumnConstraints().addAll(firstColumn);
		advancedTestingResultsGridPane.getRowConstraints().addAll(firstRow);
		
		advancedTestingResultsGridPane.add(createResultTableView(), 0, 0);
		
		return advancedTestingResultsGridPane;
	}
	
	private TableView<TestSummary> createResultTableView() {
		TableView<TestSummary> tableView = new TableView<>();
		tableView.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		 tableView.setPrefHeight(900); 

		TableColumn<TestSummary, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<TestSummary, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setStyle("-fx-alignment: CENTER;");


		tableView.getColumns().addAll(fileNameColumn, resultColumn);
		
		return tableView;
	}

}
