package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.model.AitessSymbolFiles.AitessSymbolDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AitessSymbolPopupController {

	@FXML
	private AnchorPane aitessSymbolPopupMainContainer;

	@FXML
	private TableView<AitessSymbolDetails> symbolTableView;

	@FXML
	private TableColumn<AitessSymbolDetails, String> symbolNameColumn;

	@FXML
	private TableColumn<AitessSymbolDetails, String> symbolTypeColumn;

	@FXML
	private TableColumn<AitessSymbolDetails, String> minColumn;

	@FXML
	private TableColumn<AitessSymbolDetails, String> maxColumn;

	@FXML
	public void initialize() {
		symbolNameColumn.setCellValueFactory(new PropertyValueFactory<>("symbolName"));
		symbolTypeColumn.setCellValueFactory(new PropertyValueFactory<>("symbolType"));
		minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
		maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));

		setColumnCenterAlignment(symbolNameColumn);
		setColumnCenterAlignment(symbolTypeColumn);
		setColumnCenterAlignment(minColumn);
		setColumnCenterAlignment(maxColumn);
	}

	private <T> void setColumnCenterAlignment(TableColumn<AitessSymbolDetails, T> column) {
		column.setCellFactory(new Callback<TableColumn<AitessSymbolDetails, T>, TableCell<AitessSymbolDetails, T>>() {
			@Override
			public TableCell<AitessSymbolDetails, T> call(TableColumn<AitessSymbolDetails, T> param) {
				return new TableCell<AitessSymbolDetails, T>() {
					@Override
					protected void updateItem(T item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText(null);
							setStyle("");
						} else {
							setText(item.toString());
							setAlignment(Pos.CENTER); 
						}
					}
				};
			}
		});
	}

	public void setSymbolDetails(List<AitessSymbolDetails> symbolDetails) {
		ObservableList<AitessSymbolDetails> detailsData = FXCollections.observableArrayList(symbolDetails);
		symbolTableView.setItems(detailsData);
	}

	@FXML
	void onClickOk(ActionEvent event) {
		Stage stage = (Stage) aitessSymbolPopupMainContainer.getScene().getWindow();
		stage.close();
	}
}
