package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.AdaFilter1;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.model.PowerAuto;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

class ADAFilter1TableViewFactory implements TableViewFactory<AdaFilter1> {
	@Override
	public CustomTableView<AdaFilter1> createTableView(ObservableList<AdaFilter1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AdaFilter1.class, addUserColumn, addCheckboxColumn);
	}
}

public class AdavncedDataAnalysisFilter1TableController {


	private GridPane adaFiletr1MainContainerGridPane = new GridPane();
	
//	UUT
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	
//	Signal Name
	private ComboBox<String> signalNameField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> signalNameDataList;
	private ObservableList<String> signalNameTypeList = FXCollections.observableArrayList();
	
//Step No
	private ComboBox<String> stepField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> stepDataList;
	private ObservableList<String> stepTypeList = FXCollections.observableArrayList();
	
//TPGPH No
		private ComboBox<String> tpgphField = new ComboBox<String>();
		private ObservableList<UUTMasterDetailsDto> tpgphDataList;
		private ObservableList<String> tpgphTypeList = FXCollections.observableArrayList();
		
		
		private StackPane adaFilter1StackPane = new StackPane();

		private TableViewFactory<AdaFilter1> adaFilter1DataFactory = new ADAFilter1TableViewFactory();

		private ObservableList<AdaFilter1> adaFiletr1DataList = FXCollections.observableArrayList();

		private CustomTableView<AdaFilter1> adaFiletr1DataTableView;
		
	
		public GridPane createFilter1TableContainerGridPane() {
			adaFiletr1MainContainerGridPane.getChildren().clear();
			adaFiletr1MainContainerGridPane.getColumnConstraints().clear();
			adaFiletr1MainContainerGridPane.getRowConstraints().clear();

			adaFiletr1MainContainerGridPane.getStylesheets().add(getClass()
					.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
			adaFiletr1MainContainerGridPane.getStyleClass().add("advanced-testing-container");

			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(100);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(10);

			RowConstraints secondRow = new RowConstraints();
			secondRow.setPercentHeight(90);

			

			adaFiletr1MainContainerGridPane.setHgap(10);
			adaFiletr1MainContainerGridPane.setVgap(10);

			adaFiletr1MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
			adaFiletr1MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
			adaFiletr1MainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

			adaFiletr1MainContainerGridPane.add(createFilterGridePane(), 0, 0);
			adaFiletr1MainContainerGridPane.add(createFilter1Table(), 0, 1);
			
			

			return adaFiletr1MainContainerGridPane;
		}
	
	
		private GridPane createFilterGridePane() {
			GridPane filterGridPane = new GridPane();

			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(25);

			ColumnConstraints secondColumn = new ColumnConstraints();
			secondColumn.setPercentWidth(25);

			ColumnConstraints thirdColumn = new ColumnConstraints();
			thirdColumn.setPercentWidth(25);

			ColumnConstraints fourthColumn = new ColumnConstraints();
			fourthColumn.setPercentWidth(25);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(100);

			filterGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
			filterGridPane.getRowConstraints().addAll(firstRow);


			HBox uutHbox = new HBox(10);
			uutHbox.setAlignment(Pos.CENTER_LEFT);
			uutHbox.setFillHeight(true);
			uutTypeField.setPromptText("select UUT Type");
			signalNameField.setPromptText("select Signal Name");
			stepField.setPromptText("select Step");
			tpgphField.setPromptText("select TPGPH");
			uutHbox.getChildren().add(uutTypeField);

			HBox signalHbox = new HBox(10);
			signalHbox.setAlignment(Pos.CENTER);
			signalHbox.getChildren().addAll(signalNameField);

			HBox stepHbox = new HBox(10);
			stepHbox.setAlignment(Pos.CENTER_RIGHT);
			stepHbox.getChildren().addAll(stepField);
			
			HBox tpgphHbox = new HBox(10);
			tpgphHbox.setAlignment(Pos.CENTER_RIGHT);
			tpgphHbox.getChildren().addAll(tpgphField);

			filterGridPane.add(uutHbox, 0, 0);
			filterGridPane.add(signalHbox, 1, 0);
			filterGridPane.add(stepHbox, 2, 0);
			filterGridPane.add(tpgphHbox, 3, 0);

			return filterGridPane;
		}
		
		
		private ScrollPane createFilter1Table() {
			adaFiletr1DataList.clear();
						
			
			ScrollPane tableScrollPane = new ScrollPane(adaFiletr1DataTableView);
			 Task<Void> task = new Task<Void>() {
			        @Override
			        protected Void call() throws Exception {
			
			
			
			

			adaFiletr1DataTableView = adaFilter1DataFactory.createTableView(adaFiletr1DataList, false, false);
			

			adaFiletr1DataTableView.getColumns().forEach(column -> {

//				column.setMinWidth(column.getText().length() * 14);
				String colNamne=column.getText();
//				////System.out.println(colNamne);
				switch (colNamne) {
				


				default:
//					column.setMinWidth(120);
//					column.setMaxWidth(120);
					break;
				}
			
				
			});

			


			if(adaFiletr1DataList.size() == 0) {
				tableScrollPane.setFitToWidth(true);
			}
			
			tableScrollPane.setFitToHeight(true);
			 return null;  
			        }
				
			        @Override
					protected void succeeded() {
				    	Platform.runLater(() -> {
			            	
			 	               tableScrollPane.setContent(adaFiletr1DataTableView);
			 	              tableScrollPane.setFitToHeight(true);
			 	            });

			               
			           
					}

					@Override
					protected void failed() {

						Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
					}
				};
				new Thread(task).start();
			
			return tableScrollPane;
		}

	
		
		
	
	
	
	
	
	
	
}
