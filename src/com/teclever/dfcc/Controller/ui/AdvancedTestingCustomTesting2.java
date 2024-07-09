package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class AdvancedTestingCustomTesting2 {

	private GridPane tab4MainGridPane = new GridPane();
	private HBox testTypeHBox = new HBox(10);
	private ComboBox<String> testTypeComboBox = new ComboBox<>();
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();
	
	private VBox selectTestFileVBox = new VBox(10);
	private HBox selectTestFileHBox = new HBox(10);
	private Label selectTestFileLabel = new Label("Select Test File");
	private Label addTestFileLabel = new Label("+");
	private HBox selectedTestFileHBox = new HBox(10);
	private Label selectedTestFileName = new Label("--------");
	private Button selectTestFileRunButton = new Button("Run");
	
	private VBox downloadCodeVBox = new VBox(10);
	private HBox downloadCodeHBox = new HBox(10);
	private Label downloadCodeLabel = new Label("Download Code");
	private Label addDownloadCodeLabel = new Label("+");
	private HBox selectedDownloadCodeHBox = new HBox(10);
	private Label selectedDownloadCodeName = new Label("--------");
	private Button downloadCodeRunButton = new Button("Run");
	
	private GridPane memoryTestGridPane = new GridPane();

	private Label memoryTestLabel = new Label("Memory Test");
	
	private Label memoryTypeLabel = new Label("Memory Type");
	private Label rwTypeLabel = new Label("R/W Type");
	private Label startAddressLabel = new Label("Start Address");
	private Label endAddressLabel = new Label("End Address");
	private Label ipDataLabel = new Label("I/P Data");
	
	
	private ComboBox<String> memoryTypeComboBox = new ComboBox<>();
	private ComboBox<String> rwTypeComboBox = new ComboBox<>();
	private TextField startAddressTextField = new TextField();
	private TextField endAddressTextField = new TextField();
	private TextField ipDataTextField = new TextField();
	
	private VBox memoryTestButtonBox = new VBox();
	private Button memoryTestRunButton = new Button("Run");
	
	RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
	
	public GridPane createAdvancedTestingTab4GridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(14);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(43);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(43);
		
		tab4MainGridPane.setVgap(5);
		tab4MainGridPane.setHgap(5);
		tab4MainGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		tab4MainGridPane.getRowConstraints().addAll(firstRow,secondRow,thirdRow);
		
		tab4MainGridPane.add(createTestTypeComboBox(), 0, 0, 2, 1);
		tab4MainGridPane.add(createSelectTestFileBox(), 0, 1);
		tab4MainGridPane.add(createDownloadBox(), 0, 2);
		tab4MainGridPane.add(createMemoryTestBox(), 1, 1, 1, 2);
		
		return tab4MainGridPane;
	}

	private HBox createTestTypeComboBox() {
		testTypeHBox.getStyleClass().add("advanced-testing-custom-tab-container");
		testTypeComboBox.setPromptText("TEST TYPE");

		testTypeHBox.setAlignment(Pos.TOP_RIGHT);
		testTypeHBox.getChildren().add(testTypeComboBox);
		initializeTestTypeComboBox();
		return testTypeHBox;
	}

	private void initializeTestTypeComboBox() {
		testTypeDataList = FXCollections.observableArrayList(runConfigurationManagement.getTestTypeByUUTId(StateMachine.currentSessionDetails.getUutId()));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
		}
		testTypeComboBox.setItems(testTypeList);
	}
	

	private VBox createSelectTestFileBox() {
		selectTestFileVBox.getStyleClass().add("advanced-testing-custom-tab-container");
		selectTestFileVBox.setAlignment(Pos.CENTER);

		selectTestFileLabel.getStyleClass().add("form-label");
		addTestFileLabel.getStyleClass().add("form-label-add-button");
		selectedTestFileName.getStyleClass().add("form-label-selected-text");
		
		selectedTestFileName.setMaxWidth(400);
		selectedTestFileName.setWrapText(true);
		
		selectTestFileHBox.getChildren().addAll(selectTestFileLabel,addTestFileLabel);
		selectedTestFileHBox.getChildren().addAll(selectedTestFileName,selectTestFileRunButton);
		selectTestFileHBox.setAlignment(Pos.CENTER);
		selectedTestFileHBox.setAlignment(Pos.CENTER);
	
		selectTestFileVBox.getChildren().addAll(selectTestFileHBox,selectedTestFileHBox);
		
		addTestFileLabel.setOnMouseClicked(e -> {
			uploadFile("selectTestFile");
		});
		return selectTestFileVBox;
	}

	private VBox createDownloadBox() {
		downloadCodeVBox.getStyleClass().add("advanced-testing-custom-tab-container");
		downloadCodeVBox.setAlignment(Pos.CENTER);

		downloadCodeLabel.getStyleClass().add("form-label");
		addDownloadCodeLabel.getStyleClass().add("form-label-add-button");
		selectedDownloadCodeName.getStyleClass().add("form-label-selected-text");
		
		selectedDownloadCodeName.setMaxWidth(400);
		selectedDownloadCodeName.setWrapText(true);
		
		downloadCodeHBox.getChildren().addAll(downloadCodeLabel,addDownloadCodeLabel);
		selectedDownloadCodeHBox.getChildren().addAll(selectedDownloadCodeName,downloadCodeRunButton);
		downloadCodeHBox.setAlignment(Pos.CENTER);
		selectedDownloadCodeHBox.setAlignment(Pos.CENTER);
		
		downloadCodeVBox.getChildren().addAll(downloadCodeHBox,selectedDownloadCodeHBox);
		
		addDownloadCodeLabel.setOnMouseClicked(e -> {
			uploadFile("downloadCode");
		});
		
		return downloadCodeVBox;
	}
	

	
	private GridPane createMemoryTestBox() {
		memoryTestGridPane.getStyleClass().add("advanced-testing-custom-tab-container");
	
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(40);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);
		
	    memoryTestGridPane.getColumnConstraints().addAll(firstColumn,secondColumn,thirdColumn);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);
		
		memoryTestGridPane.getRowConstraints().add(firstRow);
		
		RowConstraints rowConstraints = new RowConstraints();
	    rowConstraints.setPercentHeight(17);
		for (int i = 0; i < 5; i++) {
			memoryTestGridPane.getRowConstraints().add(rowConstraints);
	    }
		
		memoryTestLabel.getStyleClass().add("title-label");
		
		memoryTestGridPane.add(memoryTestLabel, 0, 0, 2, 1);
		
		memoryTypeLabel.getStyleClass().add("form-label");
		rwTypeLabel.getStyleClass().add("form-label");
		startAddressLabel.getStyleClass().add("form-label");
		endAddressLabel.getStyleClass().add("form-label");
		ipDataLabel.getStyleClass().add("form-label");
		
		memoryTestGridPane.add(memoryTypeLabel, 0, 1);
		memoryTestGridPane.add(rwTypeLabel, 0, 2);
		memoryTestGridPane.add(startAddressLabel, 0, 3);
		memoryTestGridPane.add(endAddressLabel, 0, 4);
		memoryTestGridPane.add(ipDataLabel, 0, 5);

		startAddressTextField.getStyleClass().add("form-textfield");
	    endAddressTextField.getStyleClass().add("form-textfield");
	    ipDataTextField.getStyleClass().add("form-textfield");

	    memoryTestGridPane.add(memoryTypeComboBox, 1, 1);
	    memoryTestGridPane.add(rwTypeComboBox, 1, 2);
	    memoryTypeComboBox.setMaxWidth(Double.MAX_VALUE); 
	    rwTypeComboBox.setMaxWidth(Double.MAX_VALUE);
	    
	    memoryTestGridPane.add(startAddressTextField, 1, 3);
	    memoryTestGridPane.add(endAddressTextField, 1, 4);
	    memoryTestGridPane.add(ipDataTextField, 1, 5);
	    
	    memoryTestGridPane.add(createMemoryTestButtonBox(),2 ,1 , 1, 5);
		
		return memoryTestGridPane;
	}

	private VBox createMemoryTestButtonBox() {
		memoryTestButtonBox.setAlignment(Pos.CENTER);
		memoryTestButtonBox.getChildren().add(memoryTestRunButton);
		return memoryTestButtonBox;
	}
	
	
	private void uploadFile(String type) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		  fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(tab4MainGridPane.getScene().getWindow());
		 if (selectedFile != null) {
	            String filePath = selectedFile.getAbsolutePath();
	            String fileName = selectedFile.getName();
	    		if(type.equalsIgnoreCase("selectTestFile")) {
	    			selectedTestFileName.setText(fileName);
	    		}else if(type.equalsIgnoreCase("downloadCode")) {
	    			selectedDownloadCodeName.setText(fileName);
	    		}
	      }	
	}
}
