package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.model.FaultCodeConfig;
import com.teclever.dfcc.model.VDDConfiguraion;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

class FaultCodeConfigTableViewFactory implements TableViewFactory<FaultCodeConfig> {
	@Override
	public CustomTableView<FaultCodeConfig> createTableView(ObservableList<FaultCodeConfig> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, FaultCodeConfig.class, addUserColumn, addCheckboxColumn);
	}
}


public class FaultCodeConfigurationController {

	private GridPane faultCodeConfigMainGridPane = new GridPane();
	private GridPane faultCodeConfigTitleGridPane = new GridPane();
	private GridPane faultCodeConfigTableGridPane = new GridPane();
	
	FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
	
	public void refreshFaultCodeConfigList() {
		setTableData();
	}
	
	public GridPane createFaultCodeConfigGridPane() {
		faultCodeConfigMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/FaultCodeConfiguration.css").toExternalForm());
		faultCodeConfigMainGridPane.getStyleClass().add("fault-code-config-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);
		
		faultCodeConfigMainGridPane.setPadding(new Insets(10));
		faultCodeConfigMainGridPane.setVgap(5);
		faultCodeConfigMainGridPane.getColumnConstraints().addAll(firstColumn);
		faultCodeConfigMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		faultCodeConfigMainGridPane.add(createFaultCodeConfigTitleGridPane(), 0, 0);
		faultCodeConfigMainGridPane.add(createFaultCodeConfigTable(), 0, 1);
		return faultCodeConfigMainGridPane;
	}

	private GridPane createFaultCodeConfigTitleGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		faultCodeConfigTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		faultCodeConfigTitleGridPane.getRowConstraints().addAll(firstRow);

		HBox titleBox = new HBox();
		titleBox.setAlignment(Pos.CENTER_LEFT);
		Label title = new Label("FAULT CODE CONFIGURATION");
		title.getStyleClass().add("fault-code-config-title");
		titleBox.getChildren().add(title);

		HBox addUserBox = new HBox(10);
		addUserBox.setAlignment(Pos.CENTER_RIGHT);
		Button addUserBtn = new Button("ADD FILE");
		addUserBox.getChildren().add(addUserBtn);

		addUserBtn.setOnAction(e -> {
			uploadfile();
		});

		faultCodeConfigTitleGridPane.add(titleBox, 0, 0);
		faultCodeConfigTitleGridPane.add(addUserBox, 1, 0);

		return faultCodeConfigTitleGridPane;
	}
	

	private GridPane createFaultCodeConfigTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		faultCodeConfigTableGridPane.getColumnConstraints().addAll(firstColumn);
		faultCodeConfigTableGridPane.getRowConstraints().addAll(firstRow);

		setTableData();
		
		return faultCodeConfigTableGridPane;
	}

	private void setTableData() {
		FaultCodeResponse faultCodeList =faultCodeConfiguration.getFaultCodeList();
		ObservableList<FaultCodeConfig> tableData = FXCollections.observableArrayList();
		System.out.println(faultCodeList.getFaultCodeList());
		if (faultCodeList.getResponse().getResponseCode() != 0) {
			for (FaultCodeDTO faultCode : faultCodeList.getFaultCodeList()) {	
				
					FaultCodeConfig faultCodeData = new FaultCodeConfig();
					faultCodeData.setId(faultCode.getFaultCodeMasterId());
					faultCodeData.setFaultCode(faultCode.getFaultCode());
					faultCodeData.setDescription(faultCode.getFaultCodeDescription());
					faultCodeData.setFilePath(faultCode.getFaultCodeFilePath());	
					
					tableData.add(faultCodeData);
			}
		}

		TableViewFactory<FaultCodeConfig> userFactory = new FaultCodeConfigTableViewFactory();
		CustomTableView<FaultCodeConfig> customTableView = userFactory.createTableView(tableData, false, false);

		faultCodeConfigTableGridPane.add(customTableView, 0, 0);
	}
	
	private void uploadfile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlxs", "*.csv"));
		File selectedFile = fileChooser.showOpenDialog(faultCodeConfigMainGridPane.getScene().getWindow());
		 if (selectedFile != null) {
	            String filePath = selectedFile.getAbsolutePath();
	            FaultCodeResponse response = faultCodeConfiguration.faultCodeFile(filePath);
	            if(response.getResponse().getResponseCode()==1) {
	            	Notifications.showSuccessAlert("File Uploaded Successfully");
	            	refreshFaultCodeConfigList();
	            }else if(response.getResponse().getResponseCode()==0) {
	            	Notifications.showSuccessAlert(response.getResponse().getResponseMessage());
	            }
	      }		
	}
}
