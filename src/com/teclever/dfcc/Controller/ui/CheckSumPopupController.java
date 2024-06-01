package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
import com.teclever.dfcc.datastore.filemanagement.ValidateChecksum;
import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckSumPopupController {
	@FXML
	private AnchorPane checkSumMainContainer;
	@FXML
	private HBox checkSumHeading;
	@FXML
	private VBox checkSumDataBox;

	private Label checkSumTitle = new Label();

	ValidateChecksum validateChecksum = new ValidateChecksum();

	public void initialize() {
		checkSumMainContainer.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CheckSum.css").toExternalForm());
		createCheckSumPopupContent();
	}


	private void createCheckSumPopupContent() {
		checkSumTitle.setText("Checksum Data");
		checkSumTitle.getStyleClass().add("check-sum-title");
		checkSumHeading.getChildren().add(checkSumTitle);
		
		createCheckSumData();	
	}


	private void createCheckSumData() {
		ValidateResponse checkSumData = validateChecksum.validate();
		List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
		ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

		if (checkSumData.getResponse().getResponseCode() == 0) {
			Notifications.showErrorAlert(checkSumData.getResponse().getResponseMessage());
			Platform.exit();
		} else if (checkSumData.getResponse().getResponseCode() == 1) {
			for (CheckSum data : checkSumDataList) {
				CheckSumList checkSumUiDto = new CheckSumList();
				checkSumUiDto.setFileName(data.getFile());
				checkSumUiDto.setCheckSumValue(data.getChecksumValue());
				checkSumUiDto.setStatus(data.getMsg());
				checkSumTableData.add(checkSumUiDto);
			}
		}
		
		TableView<CheckSumList> tableView = new TableView<>();
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		 tableView.setPrefHeight(900); 

		TableColumn<CheckSumList, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> checkSumValueColumn = new TableColumn<>("CheckSum Value");
		checkSumValueColumn.setCellValueFactory(new PropertyValueFactory<>("checkSumValue"));
		checkSumValueColumn.setReorderable(false);
		checkSumValueColumn.setSortable(false);
		checkSumValueColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		setupStatusColumn(statusColumn);

		tableView.getColumns().addAll(fileNameColumn, checkSumValueColumn, statusColumn);
		tableView.setItems(checkSumTableData);

		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		Button closeButton = new Button("Close");
		buttonBox.getChildren().add(closeButton);
		buttonBox.getStyleClass().add("check-sum-ok-btn");
		
		closeButton.setOnAction(e ->{
			Stage stage = (Stage) checkSumMainContainer.getScene().getWindow();
			stage.close();
		});
		
		checkSumDataBox.getChildren().addAll(tableView,buttonBox);
	}
	private void setupStatusColumn(TableColumn<CheckSumList, String> statusColumn) {
		statusColumn.setReorderable(false);
		statusColumn.setSortable(false);
		statusColumn.setCellFactory(column -> new TableCell<CheckSumList, String>() {
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
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if("NO VDD INFO".equalsIgnoreCase(item)) {
						setText("No VDD Info");
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}else if("NO FILE".equalsIgnoreCase(item)) {
						setText("No File");
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}
}
