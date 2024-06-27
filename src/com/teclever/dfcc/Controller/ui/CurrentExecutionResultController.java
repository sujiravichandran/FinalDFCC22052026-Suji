package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.Aitess;
import com.teclever.dfcc.utils.CustomTableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;




public class CurrentExecutionResultController {

    private GridPane currentExecutionResultGridPane = new GridPane();
    private GridPane currentExecutionResultHeadingGridPane = new GridPane();
    private GridPane currentExecutionResultTabsGridPane = new GridPane();
    
	private HBox titleBox = new HBox();
	private Label title = new Label();
	
    
	private TabPane currentExecutionResultTabPane = new TabPane();
	
	private StackPane briefDataStackPane = new StackPane();
	private StackPane detailedDataStackPane = new StackPane();
	
    AitessTableViewFactory driverFactory = new AitessTableViewFactory();

	
    public GridPane createCurrentExecutionResultGridPane() {
        currentExecutionResultGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CurrentExecutionResults.css").toExternalForm());
        currentExecutionResultGridPane.getStyleClass().add("current-execution-result-container");

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(100);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

        currentExecutionResultGridPane.setPadding(new Insets(5));
        currentExecutionResultGridPane.getColumnConstraints().addAll(firstColumn);
        currentExecutionResultGridPane.getRowConstraints().addAll(firstRow, secondRow);


        currentExecutionResultGridPane.add(createHeadingBox(), 0, 0);
        currentExecutionResultGridPane.add(createCurrentExecutionResultTabsGridPane(), 0, 1);
        return currentExecutionResultGridPane;
    }
    
	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentExecutionResultHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		currentExecutionResultHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("CURRENT EXECUTION RESULTS");
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);
		
		currentExecutionResultHeadingGridPane.add(titleBox, 0, 0);
		
		return currentExecutionResultHeadingGridPane;
	}
	
	private GridPane createCurrentExecutionResultTabsGridPane() {
		currentExecutionResultTabsGridPane.getStyleClass().add("current-execution-result-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentExecutionResultTabsGridPane.setPadding(new Insets(5));
		
		currentExecutionResultTabsGridPane.getColumnConstraints().addAll(firstColumn);
		currentExecutionResultTabsGridPane.getRowConstraints().addAll(firstRow);
		
		currentExecutionResultTabsGridPane.add(createCurrentExecutionResultTabs(), 0, 0);
		return currentExecutionResultTabsGridPane;
	}

	private TabPane createCurrentExecutionResultTabs() {
	    Tab tab1 = new Tab("Brief Data");
	    Tab tab2 = new Tab("Detailed Data");

	    StackPane tab1StackPane = createTab1Content();
	    StackPane tab2StackPane = createTab2Content();

	    tab1.setContent(tab1StackPane);
	    tab1.setClosable(false);

	    tab2.setContent(tab2StackPane);
	    tab2.setClosable(false);



	    currentExecutionResultTabPane.getTabs().addAll(tab1, tab2);

	    currentExecutionResultTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	        if (newTab == tab2) {
	            showTab2Content();
	        } else {
	            showTab1Content();
	        }
	    });

	    showTab1Content();

	    return currentExecutionResultTabPane;
	}

	private void showTab1Content() {
	    briefDataStackPane.toFront();
	}

	private void showTab2Content() {
	    detailedDataStackPane.toFront();
	}

	private StackPane createTab1Content() {
	    briefDataStackPane.getStyleClass().add("tab-content-container");
	    briefDataStackPane.getChildren().add(createBriefTable());
	    return briefDataStackPane;
	}

	private StackPane createTab2Content() {
	    detailedDataStackPane.setStyle("-fx-background-color: green;");
	    return detailedDataStackPane;
	}

	private ScrollPane createBriefTable() {
		ObservableList<Aitess> driverData = FXCollections.observableArrayList();
		CustomTableView<Aitess> customTableView = driverFactory.createTableView(driverData, false, false);

        customTableView.getColumns().forEach(column -> {     	
        	double minWidth = column.getText().length()*13;
        	column.setMinWidth(minWidth);
        });
        
        ScrollPane tableScrollPane = new ScrollPane(customTableView);
        tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}
}










//package com.teclever.dfcc.Controller.ui;
//
//import com.teclever.dfcc.model.Aitess;
//import com.teclever.dfcc.utils.CustomTableView;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.RowConstraints;
//
//
//
//
//public class CurrentExecutionResultController {
//
//    private GridPane currentExecutionResultGridPane = new GridPane();
//    
//    
//    AitessTableViewFactory driverFactory = new AitessTableViewFactory();
//	
//	
//    public GridPane createCurrentExecutionResultGridPane() {
//        currentExecutionResultGridPane.setStyle("-fx-background-color:red;");
//
//        // Setting up GridPane constraints
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(200);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(200);
//
//        currentExecutionResultGridPane.setPadding(new Insets(5));
//        currentExecutionResultGridPane.getColumnConstraints().addAll(firstColumn);
//        currentExecutionResultGridPane.getRowConstraints().addAll(firstRow);
//
//        
//		ObservableList<Aitess> driverData = FXCollections.observableArrayList();
//		CustomTableView<Aitess> customTableView = driverFactory.createTableView(driverData, false, false);
//
//        customTableView.getColumns().forEach(column -> {     	
//        	double minWidth = column.getText().length()*13;
//        	column.setMinWidth(minWidth);
//        });
//        
// 
//        ScrollPane tableScrollPane = new ScrollPane(customTableView);
//        tableScrollPane.setFitToHeight(true);
//
//        currentExecutionResultGridPane.add(tableScrollPane, 0, 0);
//
//        return currentExecutionResultGridPane;
//    }
//}










//package com.teclever.dfcc.Controller.ui;
//
//import javafx.geometry.Insets;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.RowConstraints;
//
//public class CurrentExecutionResultController {
//
//    private GridPane currentExecutionResultGridPane = new GridPane();
//
//    public GridPane createCurrentExecutionResultGridPane() {
//        currentExecutionResultGridPane.setStyle("-fx-background-color:red;");
//
//        // Setting up GridPane constraints
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(200);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(200);
//
//        currentExecutionResultGridPane.setPadding(new Insets(5));
//        currentExecutionResultGridPane.getColumnConstraints().addAll(firstColumn);
//        currentExecutionResultGridPane.getRowConstraints().addAll(firstRow);
//
//        // Creating TableView
//        TableView<String> tableView = new TableView<>();
//
//        // Define multiple columns
//        TableColumn<String, String> column1 = new TableColumn<>("Column 1");
//        column1.setCellValueFactory(data -> {
//            return null; // Replace with your actual cell value factory
//        });
//
//        TableColumn<String, String> column2 = new TableColumn<>("Column 2");
//        column2.setCellValueFactory(data -> {
//            return null; // Replace with your actual cell value factory
//        });
//
//        TableColumn<String, String> column3 = new TableColumn<>("Column 3");
//        column3.setCellValueFactory(data -> {
//            return null; // Replace with your actual cell value factory
//        });
//        TableColumn<String, String> column4 = new TableColumn<>("Columnafdsasad 4");
//        TableColumn<String, String> column5 = new TableColumn<>("Columnsadfvcxb 5");
//        TableColumn<String, String> column6 = new TableColumn<>("Columnvxzcvxzcvxczv 6");
//        TableColumn<String, String> column7 = new TableColumn<>("Columnxzcvxzcvcxz 7");
//        TableColumn<String, String> column8 = new TableColumn<>("Columnzxcvxczvxcz 8");
//        TableColumn<String, String> column9 = new TableColumn<>("Columnxzvcxzcvxczv 9");
//        TableColumn<String, String> column10 = new TableColumn<>("Columncxvcxzvcxzvxz 10");
//        
//        column1.setMinWidth(200);
//        column2.setMinWidth(200);
//        column3.setMinWidth(200);
//        column4.setMinWidth(200);
//        column5.setMinWidth(200);
//        column6.setMinWidth(200);
//        column7.setMinWidth(200);
//        column8.setMinWidth(200);
//        column9.setMinWidth(200);
//        column10.setMinWidth(200);
//        
//        tableView.getColumns().addAll(column1, column2, column3,column4,column5,column6,column7,column8,column9,column10);
//
//        ScrollPane scrollPane = new ScrollPane(tableView);
////        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
//
//        // Enable horizontal scrolling
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        // Adding ScrollPane to GridPane
//        currentExecutionResultGridPane.add(scrollPane, 0, 0);
//
//        return currentExecutionResultGridPane;
//    }
//}
