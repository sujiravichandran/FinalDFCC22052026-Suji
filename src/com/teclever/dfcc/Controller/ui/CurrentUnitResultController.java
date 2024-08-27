package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

class UnitDataTableViewFactory implements TableViewFactory<UnitData> {
	@Override
	public CustomTableView<UnitData> createTableView(ObservableList<UnitData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, UnitData.class, addUserColumn, addCheckboxColumn);
	}
}

public class CurrentUnitResultController {

    private GridPane currentUnitResultGridPane = new GridPane();
    private GridPane currentUnitResultHeadingGridPane = new GridPane();
    private GridPane currentUnitResultTableGridPane = new GridPane();
	
    private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Download");
	
	private ScrollPane tableScrollPane = new ScrollPane();
	
	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();
	
	private TableViewFactory<UnitData> unitDataFactory = new UnitDataTableViewFactory();
	private CustomTableView<UnitData> unitDataTableView ;
	
	
    public GridPane createcurrentUnitResultGridPane() {
    	currentUnitResultGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CurrentExecutionResults.css").toExternalForm());
    	currentUnitResultGridPane.getStyleClass().add("current-execution-result-container");

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(100);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		currentUnitResultGridPane.setPadding(new Insets(5));
		currentUnitResultGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultGridPane.getRowConstraints().addAll(firstRow, secondRow);


		currentUnitResultGridPane.add(createHeadingBox(), 0, 0);
		currentUnitResultGridPane.add(createcurrentUnitResultTableGridPane(), 0, 1);
        return currentUnitResultGridPane;
    }
    
	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentUnitResultHeadingGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		currentUnitResultHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("CURRENT UNIT RESULTS");
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);
		
		currentUnitResultHeadingGridPane.add(titleBox, 0, 0);
		currentUnitResultHeadingGridPane.add(createDownloadButton(), 1, 0);
		
		return currentUnitResultHeadingGridPane;
	}
	
	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);
		
		downloadButton.setOnAction(e ->{
		
		});
		
		return buttonBox;
	}
	
	private GridPane createcurrentUnitResultTableGridPane() {
		currentUnitResultTableGridPane.getStyleClass().add("current-execution-result-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentUnitResultTableGridPane.setPadding(new Insets(5));
		
		currentUnitResultTableGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultTableGridPane.getRowConstraints().addAll(firstRow);
		
		currentUnitResultTableGridPane.add(createCurrentUnitResultTable(), 0, 0);
		return currentUnitResultTableGridPane;
	}
	
	private ScrollPane createCurrentUnitResultTable() {

		unitDataTableView = unitDataFactory.createTableView(unitDataList, false, false);

		unitDataTableView.getColumns().forEach(column -> {   
        	column.setMinWidth(column.getText().length()*16);
        	updateUnitData((TableColumn<UnitData, String>) column);
        });
		
		
		tableScrollPane.setContent(unitDataTableView);
		tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}
	

	private void updateUnitData(TableColumn<UnitData, String> column) {
	    column.setCellFactory(col -> new TableCell<UnitData, String>() {
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
	                label.setStyle("-fx-text-fill: white; ");
	                label.setMinWidth(label.getText().length() * 16);
	                setGraphic(label);
	                this.setMinWidth(label.getText().length() * 16);
	                col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
	            }
	        }
	    });
	}
	
}
