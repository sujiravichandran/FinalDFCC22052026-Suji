package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.LoadDriver;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class LoadDriverController {

	private GridPane loadDriverMainGridPane = new GridPane();
	private GridPane loadDriverSubGridPane = new GridPane();
	private HBox titleBox = new HBox();
	private Label titleLabel = new Label();
	private TableView<LoadDriver> loadDriverTable = new TableView<>();
	private HBox buttonBox = new HBox();
	private Button okButton = new Button();
	
	public GridPane createLoadDriverPage() {
		loadDriverMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoadDriver.css").toExternalForm());
		loadDriverMainGridPane.getStyleClass().add("load-driver-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(10);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(80);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(10);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(80);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(10);

		loadDriverMainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn,thirdColumn);
		loadDriverMainGridPane.getRowConstraints().addAll(firstRow,secondRow,thirdRow);
		
		loadDriverMainGridPane.add(createLoadDriverPageContent(), 1, 1);
		
		return loadDriverMainGridPane;
	}

	private GridPane createLoadDriverPageContent() {
		loadDriverSubGridPane.getStyleClass().add("load-driver-sub-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(80);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(10);
		
		loadDriverSubGridPane.getColumnConstraints().addAll(firstColumn);
		loadDriverSubGridPane.getRowConstraints().addAll(firstRow,secondRow,thirdRow);
		
		loadDriverSubGridPane.add(createLoadDriverTitle(), 0, 0);
		loadDriverSubGridPane.add(createLoadDriverTable(), 0, 1);
		loadDriverSubGridPane.add(createLoadDriverButton(), 0, 2);
		
		return loadDriverSubGridPane;
	}

	private HBox createLoadDriverTitle() {		
		titleLabel.setText("Load Driver Status");
		titleLabel.getStyleClass().add("load-driver-title");
		
		titleBox.setAlignment(Pos.CENTER);
		titleBox.getChildren().add(titleLabel);
		return titleBox;
	}

	private TableView<LoadDriver> createLoadDriverTable() {
		loadDriverTable = createTableView();
		
		return loadDriverTable;
	}

	private TableView<LoadDriver> createTableView() {
		TableView<LoadDriver> tableView = new TableView<>();
		tableView.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tableView.setPrefHeight(900); 

		TableColumn<LoadDriver, String> cardNameColumn = new TableColumn<>("Card Name");
		cardNameColumn.setCellValueFactory(new PropertyValueFactory<>("cardName"));
		cardNameColumn.setReorderable(false);
		cardNameColumn.setSortable(false);
		cardNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<LoadDriver, String> foundCardCountColumn = new TableColumn<>("Founded Cards");
		foundCardCountColumn.setCellValueFactory(new PropertyValueFactory<>("foundCardCount"));
		foundCardCountColumn.setReorderable(false);
		foundCardCountColumn.setSortable(false);
		foundCardCountColumn.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<LoadDriver, String> actualCardCountColumn = new TableColumn<>("Actual Cards");
		actualCardCountColumn.setCellValueFactory(new PropertyValueFactory<>("actualCardCount"));
		actualCardCountColumn.setReorderable(false);
		actualCardCountColumn.setSortable(false);
		actualCardCountColumn.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<LoadDriver, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//		setupStatusColumn(statusColumn);

		tableView.getColumns().addAll(cardNameColumn, foundCardCountColumn, actualCardCountColumn, statusColumn);
//		tableView.setItems(checkSumTableData);
		
		return tableView;
	}
	
	private void setupStatusColumn(TableColumn<LoadDriver, String> statusColumn) {
		statusColumn.setReorderable(false);
		statusColumn.setSortable(false);
		statusColumn.setCellFactory(column -> new TableCell<LoadDriver, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
//						loadDriverStatusResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}

	private HBox createLoadDriverButton() {
		okButton.setText("OK");
		
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().add(okButton);
		return buttonBox;
	}
	
}
