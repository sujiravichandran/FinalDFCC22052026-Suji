package com.teclever.dfcc.Controller.ui;
import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.testmanagement.TestManagerManagement;
import com.teclever.dfcc.model.LoadDriver;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
public class LoadDriverController {
	private GridPane loadDriverMainGridPane = new GridPane();
	private GridPane loadDriverSubGridPane = new GridPane();
	private HBox titleBox = new HBox();
	private Label titleLabel = new Label();
	private TableView<LoadDriver> loadDriverTable = new TableView<>();
	private HBox buttonBox = new HBox();
	private Button okButton = new Button();
	private List<DriverCard> loadDriverDataList ;
	private ObservableList<LoadDriver> loadDriverTableData = FXCollections.observableArrayList();
	private Boolean loadDriverStatusResult = true;

	private ProgressIndicator progressIndicator = new ProgressIndicator();
	private VBox box = new VBox();
	
	TestManagerManagement testManagerManagement = new TestManagerManagement();
	
	
	
	public GridPane createLoadDriverPage() {
		getLoadDriverData();
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
	public void getLoadDriverData() {
	    Task<Void> task = new Task<Void>() {
	        @Override
	        protected Void call() throws Exception {
	            DriverCardDetailsResponse loadDriverResponseList = testManagerManagement.preLoadDriver();
	            loadDriverDataList = loadDriverResponseList.getDriverCardDetails();
	            if (loadDriverDataList.size() > 0) {
	                Platform.runLater(() -> setTableData());
	            }
	            return null;
	        }
	    };

	    task.setOnFailed(evt -> {
	         hideProgressIndicator();
	        task.getException().printStackTrace();
	    });

	    task.setOnSucceeded(evt -> {
	    	 hideProgressIndicator();
	    });

	    task.setOnRunning(evt -> {
	    	if(DFCCConstant.isJarBuild) {	    		
	    		showProgressIndicator();
	    	}
	    });

	    new Thread(task).start();
	}

	private void showProgressIndicator() {
		StackPane parentStackPane= (StackPane) loadDriverSubGridPane.getParent().getParent();
		box.getChildren().add(progressIndicator);
		box.setAlignment(Pos.CENTER);
		parentStackPane.getChildren().add(box);
	}
	

	private void hideProgressIndicator() {
		StackPane parentStackPane= (StackPane) loadDriverSubGridPane.getParent().getParent();
		if(parentStackPane.getChildren().contains(box)) {
			parentStackPane.getChildren().remove(box);
		}
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
		
		TableColumn<LoadDriver, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		setupStatusColumn(statusColumn);
		tableView.getColumns().addAll(cardNameColumn, statusColumn);
		
		tableView.setPlaceholder(new Label(""));
		tableView.setItems(loadDriverTableData);
		
		return tableView;
	}
	
	private void setTableData() {
		for(DriverCard list : loadDriverDataList) {
			LoadDriver loadDriver = new LoadDriver();
			loadDriver.setCardName(list.getCardName());
			loadDriver.setStatus(list.getMsg());
			
			loadDriverTableData.add(loadDriver);
		}
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
						loadDriverStatusResult = false;
						setStyle("-fx-background-color: #FA9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}
	private HBox createLoadDriverButton() {
		okButton.setText("OK");
		
		okButton.setOnAction(e -> {
			if (loadDriverStatusResult) {
					StackPane parent = (StackPane) loadDriverMainGridPane.getParent();
					parent.getChildren().remove(loadDriverMainGridPane);
					UserDashboardController userDashboardController = new UserDashboardController();
					parent.getChildren().add(userDashboardController.createUserDashboard());
			} else {
				Platform.exit();
			}
		});

		
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(okButton);
		return buttonBox;
	}
	
}
