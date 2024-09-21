package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.datastore.response.StagesRemarksResponse;
import com.teclever.dfcc.datastore.dto.StageRemarksResponse;
import com.teclever.dfcc.datastore.dto.StagesRemarksDto;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddRemarkController {
	
	@FXML
	private AnchorPane addRemarkMainPane;
	@FXML
    private HBox addRemarkFormHeading;
    @FXML
    private VBox addRemarksForm;
    @FXML
    private HBox buttonBox;
    @FXML
    private Button closeButton;
    @FXML
    private Label headerLabel;
    @FXML
    private Button saveButton;
    
    private SessionManagement sessionManagement = new SessionManagement();
    private ObservableList<StagesRemarksDto> stagesRemarkList = FXCollections.observableArrayList();
    
	public void setSessionIdandReportType(String sessionId, String reportType) {
		getRemarkData(sessionId, reportType);
	}
    
    private void getRemarkData(String sessionId, String reportType) {
		StageRemarksResponse response = sessionManagement.getStagesRemarks(sessionId, reportType);

		if(response.getResponseCode() == 1) {
			stagesRemarkList = FXCollections.observableArrayList(response.getRemarks());
			createTable(response.getRemarks());
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMsg());
		}
	
	}

    private void createTable(List<StagesRemarksDto> list) {
    	TableView<StagesRemarksDto> tableView = new TableView<>();
    	tableView.getStyleClass().add("add-remark-table");

    	TableColumn<StagesRemarksDto, String> nameColumn = new TableColumn<>("STAGE NAME");
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("levelOneName"));

    	TableColumn<StagesRemarksDto, String> valueColumn = new TableColumn<>("REMARKS");
    	valueColumn.setCellValueFactory(new PropertyValueFactory<>("remarks"));

    	valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    	valueColumn.setOnEditCommit(event -> {
    		StagesRemarksDto object = event.getRowValue();
    	    object.setRemarks(event.getNewValue());
    	});
    	
    	nameColumn.setReorderable(false);
    	nameColumn.setSortable(false);
    	valueColumn.setReorderable(false);
    	valueColumn.setSortable(false);

    	
    	tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	tableView.setEditable(true);
        tableView.setPrefHeight(450); 

    	tableView.getColumns().addAll(nameColumn, valueColumn);
    	
    	closeButton.setOnAction(e -> {
			Stage stage = (Stage) addRemarkMainPane.getScene().getWindow();
			stage.close();
		});
    	
    	saveButton.setOnAction(e -> {
			StagesRemarksResponse response = sessionManagement.updateStagesRemarks(list);
			if(response.getResponseCode() == 1) {
				Notifications.showSuccessAlert(response.getResponseMessage());
				Stage stage = (Stage) addRemarkMainPane.getScene().getWindow();
				stage.close();
			}else if(response.getResponseCode() == 0) {
				Notifications.showErrorAlert(response.getResponseMessage());
			}
		});
    	

    	tableView.setItems(FXCollections.observableArrayList(list));

    	addRemarksForm.getChildren().add(tableView);
    }
    
    
   
}
