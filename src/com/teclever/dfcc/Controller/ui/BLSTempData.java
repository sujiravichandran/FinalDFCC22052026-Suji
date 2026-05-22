package com.teclever.dfcc.Controller.ui;

import java.util.Date;
import java.util.List;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;


import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.ChannelValuesDTO;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.dashboard.TemperatureDashBoardDTO;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.model.DashBoardTempData;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class AllTempDataTableViewFactory implements TableViewFactory<DashBoardTempData> {
	@Override
	public CustomTableView<DashBoardTempData> createTableView(ObservableList<DashBoardTempData> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DashBoardTempData.class, addUserColumn, addCheckboxColumn);
	}
}

public class BLSTempData {

	private GridPane blsTempMainContainerGridPane = new GridPane();

	private GridPane blsTempDataContainerGridPane = new GridPane();

	private StackPane tempDataStackPane = new StackPane();

	private DashboardManagement dashboardManagement = new DashboardManagement();
	private ObservableList<DashBoardTempData> tempDataTable1DataList = FXCollections.observableArrayList();
	private CustomTableView<DashBoardTempData> tempDataTableDataTableView;
	private TableViewFactory<DashBoardTempData> tempDataTableDataFactory = new AllTempDataTableViewFactory();
	private ScrollPane tempTableScrollPane = new ScrollPane();

	public BLSTempData() {
		
		StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
		    if (newVal != null) {
		        Platform.runLater(() -> {
		            String sessionType = currentSessionDetails.getSessionTypeID();
		            if ("ST2".equals(sessionType)) {
		                createTempDataTable();
		            }
		        });
		    }
		});

	}

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

	public ScrollPane createTempDataTable() {

		// clear list
		if (tempDataTable1DataList != null)
			tempDataTable1DataList.clear();

		tempTableScrollPane.setFitToWidth(true);
		tempTableScrollPane.setFitToHeight(true);
		tempTableScrollPane.setContent(null);
		Task<Void> task = new Task<Void>() {
			
			final double[] ch1LowSC = { Double.POSITIVE_INFINITY };
			final double[] ch1HighSC = { Double.NEGATIVE_INFINITY };
			final double[] ch2LowSC = { Double.POSITIVE_INFINITY };
			final double[] ch2HighSC = { Double.NEGATIVE_INFINITY };
			final double[] ch3LowSC = { Double.POSITIVE_INFINITY };
			final double[] ch3HighSC = { Double.NEGATIVE_INFINITY };
			final double[] ch4LowSC = { Double.POSITIVE_INFINITY };
			final double[] ch4HighSC = { Double.NEGATIVE_INFINITY };

			final double[] ch1LowAEC = { Double.POSITIVE_INFINITY };
			final double[] ch1HighAEC = { Double.NEGATIVE_INFINITY };
			final double[] ch2LowAEC = { Double.POSITIVE_INFINITY };
			final double[] ch2HighAEC = { Double.NEGATIVE_INFINITY };
			final double[] ch3LowAEC = { Double.POSITIVE_INFINITY };
			final double[] ch3HighAEC = { Double.NEGATIVE_INFINITY };
			final double[] ch4LowAEC = { Double.POSITIVE_INFINITY };
			final double[] ch4HighAEC = { Double.NEGATIVE_INFINITY };

			@Override
			protected Void call() {
				TemperatureDashBoardDTO tmpDetails = dashboardManagement
						.getTemparatureBySessionId(StateMachine.currentSessionDetails.getSessionId());

				List<ChannelValuesDTO> channelList = (tmpDetails == null) ? null : tmpDetails.getBlsChannelsValues();
				////System.out.println("Check Temp data Size::" + channelList.size());
				

				if (channelList != null && !channelList.isEmpty()) {

					for (ChannelValuesDTO dto : channelList) {

						double v1 = dto.getCh1SCValue();
						////System.out.println("Check Value V1 Outside:" + v1);
						if (v1 != 0.0)
							ch1LowSC[0] = Math.min(ch1LowSC[0], v1);
						ch1HighSC[0] = Math.max(ch1HighSC[0], v1);
						////System.out.println("Check Value V1 for Temp:" +ch1LowSC[0] + ch1HighSC[0] );

						double v2 = dto.getCh2SCValue();
						if (v2 != 0.0)
							ch2LowSC[0] = Math.min(ch2LowSC[0], v2);
						ch2HighSC[0] = Math.max(ch2HighSC[0], v2);

						double v3 = dto.getCh3SCValue();
						if (v3 != 0.0)
							ch3LowSC[0] = Math.min(ch3LowSC[0], v3);
						ch3HighSC[0] = Math.max(ch3HighSC[0], v3);

						double v4 = dto.getCh4SCValue();
						if (v4 != 0.0)
							ch4LowSC[0] = Math.min(ch4LowSC[0], v4);
						ch4HighSC[0] = Math.max(ch4HighSC[0], v4);

						double v5 = dto.getCh1AECValue();
						if (v5 != 0.0)
							ch1LowAEC[0] = Math.min(ch1LowAEC[0], v5);
						ch1HighAEC[0] = Math.max(ch1HighAEC[0], v5);

						double v6 = dto.getCh2AECValue();
						if (v6 != 0.0)
							ch2LowAEC[0] = Math.min(ch2LowAEC[0], v6);
						ch2HighAEC[0] = Math.max(ch2HighAEC[0], v6);

						double v7 = dto.getCh3AECValue();
						if (v7 != 0.0)
							ch3LowAEC[0] = Math.min(ch3LowAEC[0], v7);
						ch3HighAEC[0] = Math.max(ch3HighAEC[0], v7);

						double v8 = dto.getCh4AECValue();
						if (v8 != 0.0)
							ch4LowAEC[0] = Math.min(ch4LowAEC[0], v8);
						ch4HighAEC[0] = Math.max(ch4HighAEC[0], v8);
					}
				}

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {
				ObservableList<DashBoardTempData> items = FXCollections.observableArrayList();

				TableView<DashBoardTempData> tv = new TableView<>();
				tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				tv.setStyle(getMessage());
				// ---- NEW ROTATED COLUMNS ----
				TableColumn<DashBoardTempData, String> colChannel = new TableColumn<>("CHANNEL");
				colChannel.setCellValueFactory(new PropertyValueFactory<>("channel"));

				TableColumn<DashBoardTempData, Number> colLow = new TableColumn<>("LOW");
				colLow.setCellValueFactory(new PropertyValueFactory<>("ch1")); // reuse ch1 for LOW

				TableColumn<DashBoardTempData, Number> colHigh = new TableColumn<>("HIGH");
				colHigh.setCellValueFactory(new PropertyValueFactory<>("ch2")); // reuse ch2 for HIGH
				
				// --- Display "N/A" instead of Infinity ---
				colLow.setCellFactory(col -> new TableCell<DashBoardTempData, Number>() {
				    @Override
				    protected void updateItem(Number value, boolean empty) {
				        super.updateItem(value, empty);

				        if (empty || value == null ||
				            value.doubleValue() == Double.POSITIVE_INFINITY ||
				            value.doubleValue() == Double.NEGATIVE_INFINITY) {
				            setText("N/A");
				        } else {
				            setText(String.format("%.2f", value.doubleValue()));
				        }
				    }
				});
				
				colHigh.setCellFactory(col -> new TableCell<DashBoardTempData, Number>() {
				    @Override
				    protected void updateItem(Number value, boolean empty) {
				        super.updateItem(value, empty);

				        if (empty || value == null ||
				            value.doubleValue() == Double.POSITIVE_INFINITY ||
				            value.doubleValue() == Double.NEGATIVE_INFINITY) {
				            setText("N/A");
				        } else {
				            setText(String.format("%.2f", value.doubleValue()));
				        }
				    }
				});




				tv.getColumns().addAll(colChannel, colLow, colHigh);

				// ---- CREATE ROWS: CH1, CH2, CH3, CH4 ----
				DashBoardTempData rowCh1 = new DashBoardTempData();
			
				rowCh1.setChannel("CH1");
				
				DashBoardTempData rowCh2 = new DashBoardTempData();
				rowCh2.setChannel("CH2");

				DashBoardTempData rowCh3 = new DashBoardTempData();
				rowCh3.setChannel("CH3");

				DashBoardTempData rowCh4 = new DashBoardTempData();
				rowCh4.setChannel("CH4");
				
				
				

				// ---- ASSIGN VALUES BASED ON SC/AEC ----
				if ("UUT1".equals(currentSessionDetails.getUutId())) {
				if (DFCCConstant.temDataSelection.equalsIgnoreCase("SC")) {
					rowCh1.setCh1(ch1LowSC[0]);
					rowCh1.setCh2(ch1HighSC[0]);
					rowCh2.setCh1(ch2LowSC[0]);
					rowCh2.setCh2(ch2HighSC[0]);
					rowCh3.setCh1(ch3LowSC[0]);
					rowCh3.setCh2(ch3HighSC[0]);
					rowCh4.setCh1(ch4LowSC[0]);
					rowCh4.setCh2(ch4HighSC[0]);
				
				} else {
				
					rowCh1.setCh1(ch1LowAEC[0]);
					rowCh1.setCh2(ch1HighAEC[0]);
					rowCh2.setCh1(ch2LowAEC[0]);
					rowCh2.setCh2(ch2HighAEC[0]);
					rowCh3.setCh1(ch3LowAEC[0]);
					rowCh3.setCh2(ch3HighAEC[0]);
					rowCh4.setCh1(ch4LowAEC[0]);
					rowCh4.setCh2(ch4HighAEC[0]);
				
				}}else {
					rowCh1.setCh1(ch1LowSC[0]);
					rowCh1.setCh2(ch1HighSC[0]);
					rowCh2.setCh1(ch2LowSC[0]);
					rowCh2.setCh2(ch2HighSC[0]);
					rowCh3.setCh1(ch3LowSC[0]);
					rowCh3.setCh2(ch3HighSC[0]);
					rowCh4.setCh1(ch4LowSC[0]);
					rowCh4.setCh2(ch4HighSC[0]);
				}
				
			
			

				// ---- Add rows to table ----
				items.addAll(rowCh1, rowCh2, rowCh3, rowCh4);
				tv.setItems(items);
				tv.setFixedCellSize(47);
				tv.prefHeightProperty().bind(tv.fixedCellSizeProperty().multiply(tv.getItems().size()).add(47));
				tv.minHeightProperty().bind(tv.prefHeightProperty());
				tv.maxHeightProperty().bind(tv.prefHeightProperty());


				//  NOT NEEDED
//				Button refreshBtn = new Button("Refresh");
//				refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
//
//				refreshBtn.setOnAction(e -> {
//					
//					
//					
//
//					if (DFCCConstant.temDataSelection.equalsIgnoreCase("SC")) {
//
//						rowCh1.setCh1(ch1LowSC[0]);
//						rowCh1.setCh1(ch1HighSC[0]);
//						rowCh2.setCh2(ch2LowSC[0]);
//						rowCh2.setCh2(ch2HighSC[0]);
//						rowCh3.setCh3(ch3LowSC[0]);
//						rowCh3.setCh3(ch3HighSC[0]);
//						rowCh4.setCh4(ch4LowSC[0]);
//						rowCh4.setCh4(ch4HighSC[0]);
//
//					} else {
//
//						rowCh1.setCh1(ch1LowAEC[0]);
//						rowCh1.setCh1(ch1HighAEC[0]);
//						rowCh2.setCh2(ch2LowAEC[0]);
//						rowCh2.setCh2(ch2HighAEC[0]);
//						rowCh3.setCh3(ch3LowAEC[0]);
//						rowCh3.setCh3(ch3HighAEC[0]);
//						rowCh4.setCh4(ch4LowAEC[0]);
//						rowCh4.setCh4(ch4HighAEC[0]);
//					}
//					
//					//Not needed
//					
//					
//
//					tv.refresh();
//				});

//				HBox refreshBox = new HBox(refreshBtn);
//				refreshBox.setAlignment(Pos.CENTER);
//				refreshBox.setPadding(new Insets(10));

				VBox wrapper = new VBox(tv);
				wrapper.setFillWidth(true);

				tempTableScrollPane.setContent(wrapper);

				tempTableScrollPane.setFitToHeight(true);
				tempTableScrollPane.setFitToWidth(true);
				});
			}
				

		};

		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
		tempTableScrollPane.setFitToHeight(true);
		tempTableScrollPane.setFitToWidth(true);
		return tempTableScrollPane;
	}

}
