package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.dfcc.datastore.dto.VDDDto;
import com.teclever.dfcc.datastore.dto.VDDResponse;
import com.teclever.dfcc.datastore.filemanagement.VDDManagement;
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

class VDDConfiguraionTableViewFactory implements TableViewFactory<VDDConfiguraion> {
	@Override
	public CustomTableView<VDDConfiguraion> createTableView(ObservableList<VDDConfiguraion> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, VDDConfiguraion.class, addUserColumn, addCheckboxColumn);
	}
}


public class VDDConfigurationController {

	private GridPane vddConfigMainGridPane = new GridPane();
	private GridPane vddConfigTitleGridPane = new GridPane();
	private GridPane vddConfigTableGridPane = new GridPane();
	
	VDDManagement vddManagement = new VDDManagement();
	
	public void refreshVddConfigList() {
		setTableData();
	}
	
	public GridPane createVddConfigGridPane() {
		vddConfigMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/VDDConfiguration.css").toExternalForm());
		vddConfigMainGridPane.getStyleClass().add("vdd-config-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);
		
		vddConfigMainGridPane.setPadding(new Insets(10));
		vddConfigMainGridPane.setVgap(5);
		vddConfigMainGridPane.getColumnConstraints().addAll(firstColumn);
		vddConfigMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		vddConfigMainGridPane.add(createVddConfigTitleGridPane(), 0, 0);
		vddConfigMainGridPane.add(createVddConfigTable(), 0, 1);
		return vddConfigMainGridPane;
	}

	private GridPane createVddConfigTitleGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		vddConfigTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		vddConfigTitleGridPane.getRowConstraints().addAll(firstRow);

		HBox titleBox = new HBox();
		titleBox.setAlignment(Pos.CENTER_LEFT);
		Label title = new Label("VDD CONFIGURATION");
		title.getStyleClass().add("vdd-config-title");
		titleBox.getChildren().add(title);

		HBox addUserBox = new HBox();
		addUserBox.setAlignment(Pos.CENTER_RIGHT);
		Button addUserBtn = new Button("ADD FILE");
		addUserBox.getChildren().add(addUserBtn);

		addUserBtn.setOnAction(e -> {
			uploadfile();
		});

		vddConfigTitleGridPane.add(titleBox, 0, 0);
		vddConfigTitleGridPane.add(addUserBox, 1, 0);

		return vddConfigTitleGridPane;
	}
	

	private GridPane createVddConfigTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		vddConfigTableGridPane.getColumnConstraints().addAll(firstColumn);
		vddConfigTableGridPane.getRowConstraints().addAll(firstRow);

		setTableData();
		
		return vddConfigTableGridPane;
	}

	private void setTableData() {
		VDDResponse vddList =vddManagement.getListOfVDD();
		ObservableList<VDDConfiguraion> tableData = FXCollections.observableArrayList();
		if (vddList.getResponse().getResponseCode() != 0) {
			for (VDDDto vdd : vddList.getvDDList()) {
				VDDConfiguraion vddData = new VDDConfiguraion();
				vddData.setFileName(vdd.getFileName());
				vddData.setPath(vdd.getFilePath());
				vddData.setChecksumValue(vdd.getFileCheckSum());
				vdd.setFilePath(null);
				
				tableData.add(vddData);
			}
		}

		TableViewFactory<VDDConfiguraion> userFactory = new VDDConfiguraionTableViewFactory();
		CustomTableView<VDDConfiguraion> customTableView = userFactory.createTableView(tableData, false, false);

		vddConfigTableGridPane.add(customTableView, 0, 0);
	}
	
	private void uploadfile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(vddConfigMainGridPane.getScene().getWindow());
		 if (selectedFile != null) {
	            String filePath = selectedFile.getAbsolutePath();
	            VDDResponse response = vddManagement.extractingVDDFile(filePath);
	            if(response.getResponse().getResponseCode()==1) {
	            	Notifications.showSuccessAlert("File Uploaded Successfully");
	            	refreshVddConfigList();
	            }else if(response.getResponse().getResponseCode()==0) {
	            	Notifications.showSuccessAlert(response.getResponse().getResponseMessage());
	            }
	      }		
	}
}
