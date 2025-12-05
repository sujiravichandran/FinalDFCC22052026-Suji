package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.model.DashBoardTempData;
import com.teclever.dfcc.model.PowerAuto;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

class AllTempDataTableViewFactory implements TableViewFactory<DashBoardTempData> {
	@Override
	public CustomTableView<DashBoardTempData> createTableView(ObservableList<DashBoardTempData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DashBoardTempData.class, addUserColumn, addCheckboxColumn);
	}
}


public class BLSTempData {

	private GridPane blsTempMainContainerGridPane = new GridPane();

	private GridPane blsTempDataContainerGridPane = new GridPane();
	
	private StackPane tempDataStackPane = new StackPane();
	
	private ObservableList<DashBoardTempData> tempDataTable1DataList = FXCollections.observableArrayList();
	private CustomTableView<DashBoardTempData> tempDataTableDataTableView;
	private TableViewFactory<DashBoardTempData> tempDataTableDataFactory = new AllTempDataTableViewFactory	();
	

	public GridPane createBlsTempDataMainContainerGridPane() {
		blsTempMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		blsTempMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		blsTempMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		blsTempMainContainerGridPane.getRowConstraints().addAll(firstRow);
//			ManualTestingMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

			blsTempMainContainerGridPane.add(createTableContent(), 0, 0);

		return blsTempMainContainerGridPane;
	}

	
	
	private StackPane createTableContent() {
		tempDataStackPane.getStyleClass().add("tab-content-container");
		tempDataStackPane.getChildren().clear();
		tempDataStackPane.getChildren().add(createTempDataTable());
		return tempDataStackPane;
	}

	
	public class TempDataTable {
	    private String channel;
	    private String low;
	    private String high;

	    public TempDataTable(String channel, String low, String high) {
	        this.channel = channel;
	        this.low = low;
	        this.high = high;
	    }

	}

	private DashBoardTempData createRow(String channelName) {
	    DashBoardTempData row = new DashBoardTempData();
	    row.setChannel(channelName);
	    row.setLow("");
	    row.setHigh("");
	    return row;
	}
	private void createDefaultColumns() {

	    TableColumn<DashBoardTempData, String> chColumn = new TableColumn<>("Channel");
	    chColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));

	    TableColumn<DashBoardTempData, String> lowColumn = new TableColumn<>("Low");
	    lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));

	    TableColumn<DashBoardTempData, String> highColumn = new TableColumn<>("High");
	    highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));

	    tempDataTableDataTableView.getColumns().setAll(chColumn, lowColumn, highColumn);
	}

	private ScrollPane createTempDataTable() {
	    tempDataTable1DataList.clear();

	    ScrollPane tableScrollPane = new ScrollPane();
	    tableScrollPane.setFitToWidth(true);
	    tableScrollPane.setFitToHeight(true);

	    Task<Void> task = new Task<>() {
	        @Override
	        protected Void call() throws Exception {

	        	tempDataTable1DataList.add(createRow("CH1"));
	        	tempDataTable1DataList.add(createRow("CH2"));
	        	tempDataTable1DataList.add(createRow("CH3"));
	        	tempDataTable1DataList.add(createRow("CH4"));


	            return null;
	        }	

	        @Override
	        protected void succeeded() {
	            super.succeeded();

	            
	            tempDataTableDataTableView =
	                    tempDataTableDataFactory.createTableView(tempDataTable1DataList, false, false);
	            
	            tempDataTableDataTableView.getColumns().forEach(column -> {

//	    			column.setMinWidth(column.getText().length() * 14);
	    			String colNamne=column.getText();
	    			switch (colNamne) {
	    			case "CHANNEL":
	    				column.setMinWidth(10);
	    				column.setMaxWidth(10);
	    				break;
	    			


	    			default:
//	    				column.setMinWidth(120);
//	    				column.setMaxWidth(120);
	    				break;
	    			}
	            });
	            
	            createDefaultColumns();

	            tableScrollPane.setContent(tempDataTableDataTableView);
	        }
	    };

	    new Thread(task).start();

	    return tableScrollPane;
	}


}
