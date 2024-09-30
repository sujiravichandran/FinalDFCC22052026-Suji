package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.AitessMacroFiles.AitessMacroDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AitessMacroPopupController {

	@FXML
	private AnchorPane aitessMacroPopupMainContainer;

	@FXML
	private Label headerLabel;

	@FXML
	private TableColumn<AitessMacroDetails, String> macroNameColumn;

	@FXML
	private TableView<AitessMacroDetails> macroTableView;

	@FXML
	public void initialize() {
		aitessMacroPopupMainContainer.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AitessMacroPopup.css").toExternalForm());
		macroTableView.getStyleClass().add("macro-table");
		macroNameColumn.setCellValueFactory(new PropertyValueFactory<>("macroName"));
		macroNameColumn.setReorderable(false);
		macroNameColumn.setSortable(false);
		macroNameColumn.setStyle("-fx-alignment: CENTER;");
	}

	public void setMacroDetails(List<AitessMacroDetails> macroDetails) {
		ObservableList<AitessMacroDetails> detailsData = FXCollections.observableArrayList(macroDetails);
		macroTableView.setItems(detailsData);
	}

	@FXML
	void onClickOk(ActionEvent event) {
		Stage stage = (Stage) aitessMacroPopupMainContainer.getScene().getWindow();
		stage.close();
	}

}