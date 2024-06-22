package com.teclever.dfcc.Controller.ui;

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
	private StackPane customTestStackPane = new StackPane();
	
	private AdvancedTestingHWATPTesting advancedTestingHWATPTesting = new AdvancedTestingHWATPTesting();
	
	public GridPane createAdvancedTestingGridPane() {
		advancedTestingMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AdvancedTesting.css").toExternalForm());
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
	    Tab tab3 = new Tab("Custom Testing");

	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content();
	    StackPane tab3StackPane = createTab3Content();

	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);

	    tab3.setContent(tab3StackPane);
	    tab3.setClosable(false);


	    advancedTestingTabPane.getTabs().addAll(tab1, tab2, tab3);

	    advancedTestingTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        } else if (newTab == tab3) {
	            showTab3Content();
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
	    customTestStackPane.toFront();
	}


	private StackPane createTab1Content() {
	    hwatpTestStackPane.getStyleClass().add("tab-content-container");
	    hwatpTestStackPane.getChildren().add(advancedTestingHWATPTesting.createAdvancedTestingTab1GridPane());
	    return hwatpTestStackPane;
	}

	private StackPane createTab2Content() {
	    interfaceTestStackPane.setStyle("-fx-background-color: green;");
	    return interfaceTestStackPane;
	}

	private StackPane createTab3Content() {
	    customTestStackPane.setStyle("-fx-background-color: yellow;");
	    return customTestStackPane;
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
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
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

//		TableColumn<TestSummary, String> channelColumn = new TableColumn<>("Channel");
//		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
//		setupchannelColumn(channelColumn);
//		testSummaryList.addListener((ListChangeListener<? super TestSummary>) change -> {
//			while (change.next()) {
//			  if (change.wasAdded()) {
//			      int lastIndex = testSummaryList.size()-1;
//			  	Platform.runLater(() -> {
//			          tableView.scrollTo(lastIndex);
//			          tableView.getSelectionModel().select(lastIndex);
//			          tableView.getFocusModel().focus(lastIndex);
//			      });
//			  }
//			}
//			});

		tableView.getColumns().addAll(fileNameColumn, resultColumn);
//		tableView.setItems(testSummaryList); 
		
		return tableView;
	}

}
