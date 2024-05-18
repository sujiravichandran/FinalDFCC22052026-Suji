package com.teclever.dfcc.Controller.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.model.TestFiles;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

class TestFilesTableViewFactory implements TableViewFactory<TestFiles> {
	@Override
	public CustomTableView<TestFiles> createTableView(ObservableList<TestFiles> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, TestFiles.class, addUserColumn, addCheckboxColumn);
	}
}

public class TestFilesController {

	private GridPane testFilesMainGridPane = new GridPane();
	private GridPane testFilesTitleGridPane = new GridPane();
	private GridPane testFilesTableGridPane = new GridPane();
	private GridPane testFilesOptionGridPane = new GridPane();
	
	private HBox uutTypeHBox =new HBox();
	private HBox testTypeHBox = new HBox();
	private HBox aitessTypeHBox = new HBox();
	private HBox driverTypeHBox = new HBox();
	private HBox configFileHBox = new HBox();
	
	private ComboBox<String> uutTypeComboBox = new ComboBox<String>();
	private ComboBox<String> testTypeComboBox = new ComboBox<String>();
	private Label aitessTypeLabel = new Label();
	private Label driverTypeLabel = new Label();
	private Label configFileLabel = new Label();

	Map<String, String> uutTypeMap = new HashMap<>();
	Map<String, String> testTypeMap = new HashMap<>();

	
	AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
	RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
	TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	
	public void refreshVddConfigList() {
//		setTableData();
	}
	
	public GridPane createVddConfigGridPane() {
		testFilesMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/TestFiles.css").toExternalForm());
		testFilesMainGridPane.getStyleClass().add("test-file-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints  thirdRow= new RowConstraints();
		thirdRow.setPercentHeight(86);
		
		testFilesMainGridPane.setPadding(new Insets(10));
		testFilesMainGridPane.setVgap(5);
		testFilesMainGridPane.getColumnConstraints().addAll(firstColumn);
		testFilesMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		testFilesMainGridPane.add(createTestFilesTitleGridPane(), 0, 0);
		testFilesMainGridPane.add(createTestFilesOptionPane(), 0, 1);
		testFilesMainGridPane.add(createTestFilesTable(), 0, 2);
		return testFilesMainGridPane;
	}

	private GridPane createTestFilesTitleGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		testFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		HBox titleBox = new HBox();
		titleBox.setAlignment(Pos.CENTER_LEFT);
		Label title = new Label("Test Files");
		title.getStyleClass().add("test-file-title");
		titleBox.getChildren().add(title);

		HBox addUserBox = new HBox();
		addUserBox.setAlignment(Pos.CENTER_RIGHT);
		Button addUserBtn = new Button("Upload File");
		addUserBtn.getStyleClass().add("test-file-upload-btn");
		addUserBox.getChildren().add(addUserBtn);

		addUserBtn.setOnAction(e -> {
//			uploadfile();
		});

		testFilesTitleGridPane.add(titleBox, 0, 0);
		testFilesTitleGridPane.add(addUserBox, 1, 0);

		return testFilesTitleGridPane;
	}

	private GridPane createTestFilesOptionPane() {
		testFilesOptionGridPane.getStyleClass().add("test-file-option-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(17);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(17);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(18);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(18);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(28);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		testFilesOptionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn);
		testFilesOptionGridPane.getRowConstraints().addAll(firstRow);
		
		testFilesOptionGridPane.add(createUutTypeBox(), 0, 0);
		testFilesOptionGridPane.add(createTestTypeBox(), 1, 0);
		testFilesOptionGridPane.add(createAitessTypeBox(), 2, 0);
		testFilesOptionGridPane.add(createDriverTypeBox(), 3, 0);
		testFilesOptionGridPane.add(createConfigFileBox(), 4, 0);
		
		return testFilesOptionGridPane;
	}

	private HBox createUutTypeBox() {
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeComboBox.setPromptText("UUT TYPE");
		UUTMasterDetailsDto[] uutList=aitessConfigurationManagement.getAllUUT();
		for(UUTMasterDetailsDto list : uutList) {
			uutTypeMap.put(list.getUutType(),list.getUutId());
		}
		
		 uutTypeComboBox.setOnAction(e ->{
			setTestTypeData();
		 });
		
        uutTypeComboBox.getItems().addAll(uutTypeMap.keySet());
        uutTypeHBox.getChildren().add(uutTypeComboBox);
		return uutTypeHBox;
	}

	private HBox createTestTypeBox() {
		testTypeHBox.setAlignment(Pos.CENTER);
		testTypeComboBox.setPromptText("TEST TYPE");
		setTestTypeData();
		 testTypeComboBox.setOnAction(e -> {
			 aitessTypeLabel.setVisible(false);
			 driverTypeLabel.setVisible(false);
			 configFileLabel.setVisible(false);
			 setDataForSelectedUutAndTest();
		 });
        testTypeHBox.getChildren().add(testTypeComboBox);
		return testTypeHBox;
	}

	private void setTestTypeData() {
	    testTypeMap.clear();
	    String uutTypeId = uutTypeMap.get(uutTypeComboBox.getValue());
	    if(uutTypeId != null) {
		    TestTypeMasterDetailsDto[] uutList = runConfigurationManagement.getTestTypeByUUTId(uutTypeId);
	        for (TestTypeMasterDetailsDto list : uutList) {
		        testTypeMap.put(list.getTestName(), list.getTestTypeId());
		    }
		    testTypeComboBox.getItems().clear();
		    testTypeComboBox.getItems().addAll(testTypeMap.keySet());
	    }
	}


	private HBox createAitessTypeBox() {
	    aitessTypeHBox.setAlignment(Pos.CENTER);
	    aitessTypeLabel.getStyleClass().add("test-file-label");
	    aitessTypeLabel.setVisible(false);
	    HBox.setHgrow(aitessTypeLabel, Priority.ALWAYS);
	    aitessTypeHBox.getChildren().add(aitessTypeLabel);
	    return aitessTypeHBox;
	}

	private HBox createDriverTypeBox() {
	    driverTypeHBox.setAlignment(Pos.CENTER);
	    driverTypeLabel.getStyleClass().add("test-file-label");
	    driverTypeLabel.setVisible(false);
	    HBox.setHgrow(driverTypeLabel, Priority.ALWAYS);
	    driverTypeHBox.getChildren().add(driverTypeLabel);
	    return driverTypeHBox;
	}

	private HBox createConfigFileBox() {
	    configFileHBox.setAlignment(Pos.CENTER);
	    configFileLabel.getStyleClass().add("test-file-label");
	    configFileLabel.setVisible(false);
	    HBox.setHgrow(configFileLabel, Priority.ALWAYS);
	    configFileHBox.getChildren().add(configFileLabel);
	    return configFileHBox;
	}


	private Node createTestFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFilesTableGridPane.getColumnConstraints().addAll(firstColumn);
		testFilesTableGridPane.getRowConstraints().addAll(firstRow);
		setDataForSelectedUutAndTest();
		return testFilesTableGridPane;
	}
	

	private void setTableData(String runConfigId) {
		ObservableList<TestFiles> tableData = FXCollections.observableArrayList();
		if(runConfigId != null) {
			List<TestFileDto> testFileList = testPlanFileManagement.getAllTestFiles(runConfigId);
			if (testFileList.size() > 0 ) {
				for (TestFileDto file :testFileList) {
					TestFiles testFile = new TestFiles();
					testFile.setTestFileName(file.getTestFileName());		
					tableData.add(testFile);
				}
			}
		}else {
			 tableData.clear();
		}
		
		TableViewFactory<TestFiles> userFactory = new TestFilesTableViewFactory();
		CustomTableView<TestFiles> customTableView = userFactory.createTableView(tableData, true, false);

		testFilesTableGridPane.add(customTableView, 0, 0);
	}

	private void setDataForSelectedUutAndTest() {
    	setTableData(null);
	    String uutTypeId = uutTypeMap.get(uutTypeComboBox.getValue());
	    String testTypeId = testTypeMap.get(testTypeComboBox.getValue());
	    if(uutTypeId != null && testTypeId != null) {
	    	List<RunConfigurationDto> runCongifList = runConfigurationManagement.getAllRunConfig();
	    	if(runCongifList.size() > 0) {
	    		for(RunConfigurationDto runConfig : runCongifList ) {
		    		if(runConfig.getUutId().equals(uutTypeId)) {
		    			if(runConfig.getTestTypeId().equals(testTypeId)) {
		    				aitessTypeLabel.setVisible(true);
		    				aitessTypeLabel.setText(runConfig.getAitess());
		    				
		    				driverTypeLabel.setVisible(true);
		    				driverTypeLabel.setText(runConfig.getDriver());
		    				
		    				configFileLabel.setVisible(true);
		    				configFileLabel.setText(runConfig.getConfigFile());
		    				
		    				setTableData(runConfig.getRunConfigId());
		    			}
		    		}
		    	}
	    	}   		
	    }
	}
}
