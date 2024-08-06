package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ConfigDatResponse;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ConfigurationTestController {

	private GridPane configurationTestMainGridPane = new GridPane();
	private GridPane containerMidGridPane = new GridPane();
	private GridPane configurationListGridPane = new GridPane();
	private HBox testTypeComboHBox = new HBox(10);
	private ComboBox<String> testTypeComboBox = new ComboBox<>();
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ListView<String> configurationListView = new ListView<>();

	RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	
	public static String testTypeValue;
	public static String testTypeId;
	public GridPane createConfigurationTestGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(9);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(91);

		configurationTestMainGridPane.setVgap(5);
		configurationTestMainGridPane.setHgap(5);
		configurationTestMainGridPane.getColumnConstraints().addAll(firstColumn);
		configurationTestMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		configurationTestMainGridPane.add(createTopContainer(), 0, 0);
		configurationTestMainGridPane.add(createConfigurationListController(), 0, 1);

		return configurationTestMainGridPane;
	}

	private GridPane createTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		containerMidGridPane.getColumnConstraints().addAll(firstColumn);
		containerMidGridPane.getRowConstraints().addAll(firstRow);

		containerMidGridPane.getStyleClass().add("configuration-Container");

		containerMidGridPane.add(createTestTypeComboBox(), 0, 0);

		return containerMidGridPane;
	}

	private HBox createTestTypeComboBox() {
		testTypeComboHBox.getStyleClass().add("configuration-custom-tab-container");
		testTypeComboBox.setPromptText("SELECT TEST TYPE");
		testTypeComboHBox.setAlignment(Pos.CENTER_RIGHT);
		testTypeComboHBox.getChildren().add(testTypeComboBox);
		initializeTestTypeComboBox();
		return testTypeComboHBox;
	}

	private void initializeTestTypeComboBox() {
		testTypeDataList = FXCollections.observableArrayList(
				runConfig.getTestTypeByUUTId(StateMachine.currentSessionDetails.getUutId()));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
			
		}
		testTypeComboBox.setItems(testTypeList);
		testTypeComboBox.setOnAction(e -> createConfigurationList());
	}

	private GridPane createConfigurationListController() {
		configurationListGridPane.getStyleClass().add("configuration-code-Container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		configurationListGridPane.getColumnConstraints().addAll(firstColumn);
		configurationListGridPane.getRowConstraints().addAll(firstRow);

		configurationListGridPane.add(configurationListView, 0, 0);

		return configurationListGridPane;
	}

	private void createConfigurationList() {
		 
		String selectedTestType = fetchTestTypeId(testTypeComboBox.getValue());
		if (selectedTestType != null) {
			readFileAndPopulateListView(selectedTestType);
			
		}
	}
	
	  private String fetchTestTypeId(String testTypeName) {
	        for (TestTypeMasterDetailsDto testType : testTypeDataList) {
	            if (testType.getTestName().equals(testTypeName)) {
	                return testType.getTestTypeId();
	            }
	        }
	        return null;
	    }


	private void readFileAndPopulateListView(String testType) {
		ObservableList<String> fileData = FXCollections.observableArrayList();
		RunConfigurationManagement runConfigMng = new RunConfigurationManagement();
		ConfigDatResponse a=runConfigMng.getConfigData(currentSessionDetails.getUutId(), testType);
		a.getConfigContent();
		fileData.addAll(a.getConfigContent());

		configurationListView.setItems(fileData);
	}
}
