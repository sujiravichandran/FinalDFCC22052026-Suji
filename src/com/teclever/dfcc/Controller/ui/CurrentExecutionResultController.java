package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;


class BriefDataTableViewFactory implements TableViewFactory<BriefData> {
	@Override
	public CustomTableView<BriefData> createTableView(ObservableList<BriefData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, BriefData.class, addUserColumn, addCheckboxColumn);
	}
}

class DetailedDataTableViewFactory implements TableViewFactory<DetailedData> {
	@Override
	public CustomTableView<DetailedData> createTableView(ObservableList<DetailedData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DetailedData.class, addUserColumn, addCheckboxColumn);
	}
}




public class CurrentExecutionResultController {

    private GridPane currentExecutionResultGridPane = new GridPane();
    private GridPane currentExecutionResultHeadingGridPane = new GridPane();
    private GridPane currentExecutionResultTabsGridPane = new GridPane();
    
	private HBox titleBox = new HBox();
	private Label title = new Label();
	
    
	private TabPane currentExecutionResultTabPane = new TabPane();
	
	private StackPane briefDataStackPane = new StackPane();
	private StackPane detailedDataStackPane = new StackPane();
	
    
	private TableViewFactory<BriefData> briefDataFactory = new BriefDataTableViewFactory();
	private TableViewFactory<DetailedData> detailedDataFactory = new DetailedDataTableViewFactory();
	
	private ObservableList<BriefData> briefDataList = FXCollections.observableArrayList();
	private ObservableList<DetailedData> detailedDataList = FXCollections.observableArrayList();
	
	private CustomTableView<BriefData> briefDataTableView ;
	private CustomTableView<DetailedData> detailedDataTableView ;
	
	ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	
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
	    briefDataStackPane.getChildren().add(createBriefDataTable());
	    return briefDataStackPane;
	}

	private StackPane createTab2Content() {
		detailedDataStackPane.getStyleClass().add("tab-content-container");
		detailedDataStackPane.getChildren().add(createDetailedDataTable());
	    return detailedDataStackPane;
	}

	private ScrollPane createBriefDataTable() {
		ResultExecutionResponse response = resultExecutionManagement.getResultExecutionListBriefListForStages(StateMachine.currentSessionDetails.getSessionId());
		System.out.println(response.getCode());
		System.out.println(response.geteMsg());
		System.out.println("Session Id On UI"+StateMachine.currentSessionDetails.getSessionId());
		System.out.println("Result On the UI"+response.getResultDTOList().size());
		if(response.getCode() == 1 && response.getResultDTOList() != null) {
			int i = 1;
			for(ResultExecutionDTO data :response.getResultDTOList()) {
				BriefData newBriefData = new BriefData();
				
				newBriefData.setsNo(String.valueOf(i));
				newBriefData.setRdfName(data.getRdfFile());
				newBriefData.setdStarCount(data.getDStarCount());
				newBriefData.setStatus(data.getStatus());
				newBriefData.setCompletedTime(data.getEndTime());
				
				briefDataList.add(newBriefData);
				i++;
			}
		}
	
		
		
		briefDataTableView = briefDataFactory.createTableView(briefDataList, false, false);

		briefDataTableView.getColumns().forEach(column -> {   
        	column.setMinWidth(column.getText().length()*13);
        	updateBriefData((TableColumn<BriefData, String>) column);
        });
		
        
        ScrollPane tableScrollPane = new ScrollPane(briefDataTableView);
        
        if(briefDataList.size() == 0) {
        	tableScrollPane.setFitToWidth(true);
        }
//        tableScrollPane.setFitToWidth(true);
        tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}
	
	
	private void updateBriefData(TableColumn<BriefData, String> column) {
	    column.setCellFactory(col -> new TableCell<BriefData, String>() {
	        private Label label;

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (item == null || empty) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                if (label == null) {
	                    label = new Label();
	                    label.setWrapText(false);
	                    label.setAlignment(Pos.CENTER); 
	                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	                    setStyle("-fx-alignment: CENTER;"); 
	                }
	                label.setText(item);
	                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
	                label.setMinWidth(label.getText().length() * 12);
	                setGraphic(label);
	                this.setMinWidth(label.getText().length() * 12);
	                col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
	            }
	        }
	    });
	}
	
	
	
	public ScrollPane createDetailedDataTable() {
		
		ResultDetailedResponse response = resultExecutionManagement.getResultExecutionDetailedListForStages(StateMachine.currentSessionDetails.getSessionId());
		
		System.out.println(response.getCode());
		System.out.println(response.geteMsg());
		
		if(response.getCode() == 1 && response.getResultDetailedList() != null) {
			int i = 1;
			for(ResultDetailedDTO data :response.getResultDetailedList()) {
				
				DetailedData newDetailedData = new DetailedData();
				
				newDetailedData.setSNo(String.valueOf(i));
				newDetailedData.setTestName(data.getTestName());
				newDetailedData.setRdfName(data.getRdfName());
				newDetailedData.setTpgphNo(data.getTpgph());
				newDetailedData.setStepNo(data.getStepName());
				newDetailedData.setExpectedValue(data.getExpectedValue());
				newDetailedData.setMoniterdOutput(data.getMeasuredValue());
				newDetailedData.setUnit(data.getUnit());
				newDetailedData.setSignalName(data.getSignalName());
				newDetailedData.setFaultyChannel(data.getFaultyChannel());

		        detailedDataList.add(newDetailedData);

				i++;
			}
		}

        detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);


        detailedDataTableView.getColumns().forEach(column -> {
            column.setMinWidth(column.getText().length() * 13);
            updateDetailedData((TableColumn<DetailedData, String>) column);
        });

        ScrollPane tableScrollPane = new ScrollPane(detailedDataTableView);
        tableScrollPane.setFitToHeight(true);
        return tableScrollPane;
    }

	private void updateDetailedData(TableColumn<DetailedData, String> column) {
	    column.setCellFactory(col -> new TableCell<DetailedData, String>() {
	        private Label label;

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (item == null || empty) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                if (label == null) {
	                    label = new Label();
	                    label.setWrapText(false);
	                    label.setAlignment(Pos.CENTER); 
	                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	                    setStyle("-fx-alignment: CENTER;"); 
	                }
	                label.setText(item);
	                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
	                label.setMinWidth(label.getText().length() * 12);
	                setGraphic(label);
	                this.setMinWidth(label.getText().length() * 12);
	                col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
	            }
	        }
	    });
	}
}

// change column width

//private ScrollPane createBriefDataTable() {
//	briefDataTableView = briefDataFactory.createTableView(briefDataList, false, false);
//
//	briefDataTableView.getColumns().forEach(column -> {     	
//    	double minWidth = column.getText().length()*13;
//    	column.setMinWidth(minWidth);
//    });
//	
//    
//    ScrollPane tableScrollPane = new ScrollPane(briefDataTableView);
//    tableScrollPane.setFitToWidth(true);
//    tableScrollPane.setFitToHeight(true);
//	return tableScrollPane;
//}
	
	
// wrapping table data text
	
//	public ScrollPane createDetailedDataTable() {
//        DetailedData newData = new DetailedData();
//
//        newData.setExpectedValue("setExpectedValuesetExpectedValue");
//        newData.setFaultyChannel("setFaultyChannelsetFaultyChannel");
//        newData.setMoniterdOutput("setMoniterdOutputsetMoniterdOutput");
//        newData.setRdfName("afvdsbhfsanasdbfvdsafdsanmfdsafbdsafjabfdsa");
//        newData.setSignalName("fcvghjlkmajkfdslafsda");
//        newData.setStepNo("asdf");
//        newData.setTestName("asdfdsafdsafdsafsadfdsafdsafdsaF");
//        newData.setTpgphNo("avsdhgjdbv");
//        newData.setUnit("safdsafds");
//
//        detailedDataList.add(newData);
//
//        detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);
//
//        // Custom cell factory to wrap text
//        detailedDataTableView.getColumns().forEach(column -> {
//            // Cast the column to TableColumn<DetailedData, String> and update it
//            update((TableColumn<DetailedData, String>) column);
//
//            // Calculate minimum width based on content
//            column.setMinWidth(column.getText().length() * 13);
//        });
//
//        ScrollPane tableScrollPane = new ScrollPane(detailedDataTableView);
//        tableScrollPane.setFitToHeight(true);
//        return tableScrollPane;
//    }
//
//    private void update(TableColumn<DetailedData, String> column) {
//        column.setCellFactory(col -> new TableCell<DetailedData, String>() {
//            private Text text;
//
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null || empty) {
//                    setText(null);
//                    setGraphic(null);
//                } else {
//                    if (text == null) {
//                        text = new Text();
//                        text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
//                    }
//                    text.setText(item);
//                    setGraphic(text);
//                }
//            }
//        });
//    }











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
