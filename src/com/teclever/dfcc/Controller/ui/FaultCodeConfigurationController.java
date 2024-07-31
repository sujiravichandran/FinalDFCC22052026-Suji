package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.model.FaultCodeConfig;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
	private GridPane midGridPane = new GridPane();
	
	private HBox midHBoxUUTType = new HBox(10);
	
	private Button addUserBtn = new Button("ADD FAULT CODES");
	
	public ComboBox<String> uutTypeField = new ComboBox<>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
	
	
	public void refreshFaultCodeConfigList() {
		setTableData();
	}
	
	public GridPane createFaultCodeConfigGridPane() {
		initializeUUTTypeComboBox();
		faultCodeConfigMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/FaultCodeConfiguration.css").toExternalForm());
		faultCodeConfigMainGridPane.getStyleClass().add("fault-code-config-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);
		
		faultCodeConfigMainGridPane.setPadding(new Insets(10));
		faultCodeConfigMainGridPane.setVgap(5);
		faultCodeConfigMainGridPane.getColumnConstraints().addAll(firstColumn);
		faultCodeConfigMainGridPane.getRowConstraints().addAll(firstRow,secondRow, thirdRow);

		faultCodeConfigMainGridPane.add(createFaultCodeConfigTitleGridPane(), 0, 0);
		faultCodeConfigMainGridPane.add(runConfigurationMidContainer(), 0, 1);
		faultCodeConfigMainGridPane.add(createFaultCodeConfigTable(), 0, 2);
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
	
		addUserBtn.setDisable(true);
		addUserBox.getChildren().add(addUserBtn);

		addUserBtn.setOnAction(e -> {
			uploadfile();
		});

		faultCodeConfigTitleGridPane.add(titleBox, 0, 0);
		
		faultCodeConfigTitleGridPane.add(addUserBox, 1, 0);

		return faultCodeConfigTitleGridPane;
	}
	
	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);
		uutTypeField.setOnAction((event) -> {
			addUserBtn.setDisable(false);
			UUT_ID = fetchUutId(uutTypeField.getValue());
			if(UUT_ID != null) {				
				setTableData();
			}
		});
	}
	
	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	
	private GridPane runConfigurationMidContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		midGridPane.getColumnConstraints().addAll(firstColumn);
		midGridPane.getRowConstraints().addAll(firstRow);

		midGridPane.add(createUUTypeComboBox(), 0, 0);

		midGridPane.getStyleClass().add("fault-code-Container");
		return midGridPane;
	}
	
	private HBox createUUTypeComboBox() {
		uutTypeField.setPromptText("UUT TYPE");
		midHBoxUUTType.setPadding(new Insets(0, 0, 0, 18.5));
		midHBoxUUTType.setAlignment(Pos.CENTER_LEFT);
		midHBoxUUTType.getChildren().add(uutTypeField);

		return midHBoxUUTType;
	}
	
	

	private GridPane createFaultCodeConfigTable() {
		faultCodeConfigTableGridPane.getStyleClass().add("fault-code-Container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		faultCodeConfigTableGridPane.getColumnConstraints().addAll(firstColumn);
		faultCodeConfigTableGridPane.getRowConstraints().addAll(firstRow);

		
		return faultCodeConfigTableGridPane;
	}
	


	private void setTableData() {
		FaultCodeResponse faultCodeList =faultCodeConfiguration.getFaultCodeList(UUT_ID);
		ObservableList<FaultCodeConfig> tableData = FXCollections.observableArrayList();
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
	            System.out.println(UUT_ID);
	            FaultCodeResponse response = faultCodeConfiguration.faultCodeFile(filePath,UUT_ID);
	            System.out.println(response.getResponse().getResponseCode()+"   "+response.getResponse().getResponseMessage());
	            if(response.getResponse().getResponseCode()==1) {
	            	Notifications.showSuccessAlert("File Uploaded Successfully");
	            	refreshFaultCodeConfigList();
	            }else if(response.getResponse().getResponseCode()==0) {
	            	Notifications.showErrorAlert(response.getResponse().getResponseMessage());
	            }
	      }		
	}
}
