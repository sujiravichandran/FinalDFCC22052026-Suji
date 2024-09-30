package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.AitessSymbolFiles.AitessSymbolDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
		aitessSymbolPopupMainContainer.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AitessSymbolPopup.css").toExternalForm());
		symbolTableView.getStyleClass().add("symbol-table");
		symbolNameColumn.setCellValueFactory(new PropertyValueFactory<>("symbolName"));
		symbolNameColumn.setReorderable(false);
		symbolNameColumn.setSortable(false);
		symbolNameColumn.setStyle("-fx-alignment: CENTER;");
		
		symbolTypeColumn.setCellValueFactory(new PropertyValueFactory<>("symbolType"));
		symbolTypeColumn.setReorderable(false);
		symbolTypeColumn.setSortable(false);
		symbolTypeColumn.setStyle("-fx-alignment: CENTER;");
		
		minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
		minColumn.setReorderable(false);
		minColumn.setSortable(false);
		minColumn.setStyle("-fx-alignment: CENTER;");
		
		maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
		maxColumn.setReorderable(false);
		maxColumn.setSortable(false);
		maxColumn.setStyle("-fx-alignment: CENTER;");					
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
