package com.teclever.dfcc.utils;

import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class AitessConfigHeader {

	private ComboBox<String> uut_type_field = new ComboBox<>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_id;

	private ComboBox<String> test_type_field = new ComboBox<>();
	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	private GridPane aitessMiddleGridPane = new GridPane();
	private Label aitessTypeLabel = new Label("AITESS TYPE");
	private Label driverLabel = new Label("DRIVER");
	private Label configFileLabel = new Label("CONFIG FILE");

	private Label ofpVersionLabel = new Label("OFP VERSION");
	private Label ofpNameLabel = new Label("OFP NAME");

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfig = new RunConfigurationManagement();
	private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();

	private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();
	private StringProperty UUT_ID = new SimpleStringProperty();

	public String getRUN_CONFIG_ID() {
		return RUN_CONFIG_ID.get();
	}

	public void setRUN_CONFIG_ID(String rUN_CONFIG_ID) {
		RUN_CONFIG_ID.set(rUN_CONFIG_ID);
	}

	public StringProperty runConfigIdProperty() {
		return RUN_CONFIG_ID;
	}

	public String getUUT_ID() {
		return UUT_ID.get();
	}

	public void setUUT_ID(String uUT_ID) {
		UUT_ID.set(uUT_ID);
	}

	public StringProperty uutIdProperty() {
		return UUT_ID;
	}
	private String boxType;

	public AitessConfigHeader(String boxType) {
		this.boxType = boxType;
		System.out.println("BOXTYPE: " + boxType);
		initializeUUTTypeComboBox();
		initializeTestTypeComboBox();
	}

	public GridPane aitessMiddleContainer() {
		aitessMiddleGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AitessConfigHeader.css").toExternalForm());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(15);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(15);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(15);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(15);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(40);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		aitessMiddleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		aitessMiddleGridPane.getRowConstraints().addAll(firstRow);
		aitessMiddleGridPane.setAlignment(Pos.CENTER);
		aitessMiddleGridPane.setPadding(new Insets(10));
		
		aitessMiddleGridPane.add(createUutTypeField(), 0, 0);
		if (!boxType.equals("OFPMASTER")) {
			aitessMiddleGridPane.add(createConfigLabel(), 4, 0);

			if (boxType.equals("OFP")) {
				aitessMiddleGridPane.add(createOfpNameLabel(), 1, 0);
				aitessMiddleGridPane.add(createOfpVersionLabel(), 2, 0, 2, 1);
			} else {
				aitessMiddleGridPane.add(createTestTypeField(), 1, 0);
				aitessMiddleGridPane.add(createAitessType(), 2, 0);
				aitessMiddleGridPane.add(createDriverNameLabel(), 3, 0);
			}
		}

//		aitessMiddleGridPane.add(createUutTypeField(), 0, 0);
//		aitessMiddleGridPane.add(createConfigLabel(), 4, 0);
//
//		if (boxType.equals("OFP")) {
//			aitessMiddleGridPane.add(createOfpNameLabel(), 1, 0);
//			aitessMiddleGridPane.add(createOfpVersionLabel(), 2, 0, 2, 1);
//		} else {
//			aitessMiddleGridPane.add(createTestTypeField(), 1, 0);
//			aitessMiddleGridPane.add(createAitessType(), 2, 0);
//			aitessMiddleGridPane.add(createDriverNameLabel(), 3, 0);
//		}

		aitessMiddleGridPane.getStyleClass().add("middle-Container");
		return aitessMiddleGridPane;
	}

	private HBox createUutTypeField() {
		uut_type_field.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uut_type_field);

		return uutTypeHBox;
	}

	private HBox createTestTypeField() {
		test_type_field.setPromptText("TEST TYPE");

		HBox testTypeHBox = new HBox(10);
		testTypeHBox.setAlignment(Pos.CENTER);
		testTypeHBox.getChildren().add(test_type_field);

		return testTypeHBox;
	}

	private HBox createAitessType() {

		aitessTypeLabel.setPrefWidth(200);
		aitessTypeLabel.setAlignment(Pos.CENTER);

		Tooltip aitessTooltip = new Tooltip();
		aitessTooltip.textProperty().bind(aitessTypeLabel.textProperty());
		aitessTypeLabel.setTooltip(aitessTooltip);

		HBox aitessTypeHBox = new HBox(10);
		aitessTypeHBox.setAlignment(Pos.CENTER);
		aitessTypeHBox.getChildren().add(aitessTypeLabel);
		aitessTypeHBox.setMaxWidth(Double.MAX_VALUE);

		aitessTypeLabel.getStyleClass().add("label_field");
		return aitessTypeHBox;
	}

	private HBox createDriverNameLabel() {
		driverLabel.setPrefWidth(200);
		driverLabel.setAlignment(Pos.CENTER);
		driverLabel.getStyleClass().add("label_field");

		Tooltip driverTooltip = new Tooltip();
		driverTooltip.textProperty().bind(driverLabel.textProperty());
		driverLabel.setTooltip(driverTooltip);

		HBox driverTypeHBox = new HBox(10);
		driverTypeHBox.setAlignment(Pos.CENTER);
		driverTypeHBox.setMaxWidth(Double.MAX_VALUE);

		driverTypeHBox.getChildren().add(driverLabel);
		return driverTypeHBox;
	}

	private HBox createConfigLabel() {
		configFileLabel.setAlignment(Pos.CENTER);
		configFileLabel.getStyleClass().add("label_field");
		configFileLabel.setMaxWidth(Double.MAX_VALUE);

		Tooltip configFileTooltip = new Tooltip();
		configFileTooltip.textProperty().bind(configFileLabel.textProperty());
		configFileLabel.setTooltip(configFileTooltip);

		HBox configLabelHBox = new HBox(10);
		configLabelHBox.setAlignment(Pos.CENTER);
		configLabelHBox.setMaxWidth(Double.MAX_VALUE);
		configLabelHBox.getChildren().add(configFileLabel);
		configLabelHBox.setPadding(new Insets(0, 10, 0, 10));

		HBox.setHgrow(configFileLabel, Priority.ALWAYS);
		HBox.setHgrow(configLabelHBox, Priority.ALWAYS);

		return configLabelHBox;
	}

	private HBox createOfpNameLabel() {
		ofpNameLabel.setPrefWidth(200);
		ofpNameLabel.setAlignment(Pos.CENTER);
		ofpNameLabel.getStyleClass().add("label_field");

		Tooltip ofpNameTooltip = new Tooltip();
		ofpNameTooltip.textProperty().bind(ofpNameLabel.textProperty());
		ofpNameLabel.setTooltip(ofpNameTooltip);

		HBox ofpTypeHBox = new HBox(10);
		ofpTypeHBox.setAlignment(Pos.CENTER);
		ofpTypeHBox.setMaxWidth(Double.MAX_VALUE);

		ofpTypeHBox.getChildren().add(ofpNameLabel);
		return ofpTypeHBox;
	}

	private HBox createOfpVersionLabel() {
		ofpVersionLabel.setAlignment(Pos.CENTER);
		ofpVersionLabel.getStyleClass().add("label_field");
		ofpVersionLabel.setMaxWidth(Double.MAX_VALUE);

		Tooltip ofpVersionTooltip = new Tooltip();
		ofpVersionTooltip.textProperty().bind(ofpVersionLabel.textProperty());
		ofpVersionLabel.setTooltip(ofpVersionTooltip);

		HBox ofpVersionHBox = new HBox(10);
		ofpVersionHBox.setAlignment(Pos.CENTER);
		ofpVersionHBox.setMaxWidth(Double.MAX_VALUE);
		ofpVersionHBox.getChildren().add(ofpVersionLabel);
		ofpVersionHBox.setPadding(new Insets(0, 15, 0, 10));

		HBox.setHgrow(ofpVersionLabel, Priority.ALWAYS);
		HBox.setHgrow(ofpVersionHBox, Priority.ALWAYS);

		return ofpVersionHBox;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uut_type_field.setItems(uutTypeList);
		uut_type_field.setOnAction((event) -> {
			setUUT_ID(fetchUutId(uut_type_field.getValue()));
			UUT_id=fetchUutId(uut_type_field.getValue());
			if (boxType.equals("OFP")) {
				System.out.println("");
//				String runConfigId = fetchRunConfigIDForOFP(getUUT_ID());
//				setRUN_CONFIG_ID(runConfigId);
				
				String runConfigId=fetchRunConfigIDForOFP(UUT_id);
				setRUN_CONFIG_ID(runConfigId);
			} else {
				testTypeList.clear();
				initializeTestTypeComboBox();
			}
		});
//		uut_type_field.setOnAction((event) -> {
//
//			this.UUT_ID = fetchUutId(uut_type_field.getValue());
//			if (boxType.equals("OFP")) {
//				String runConfigId = fetchRunConfigIDForOFP(UUT_ID);
//				this.RUN_CONFIG_ID.set(runConfigId);
//			} else {
//				testTypeList.clear();
//				initializeTestTypeComboBox();
//			}
//
//		});
	}

	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	private void initializeTestTypeComboBox() {
//		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(getUUT_ID()));
		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(UUT_id));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
		}
		test_type_field.setItems(testTypeList);
		test_type_field.setOnAction((event) -> fetchingTableData());
	}

	private String fetchTestTypeId(String testTypeName) {
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestName().equals(testTypeName)) {
				return testType.getTestTypeId();
			}
		}
		return null;
	}

	private void fetchingTableData() {
		String testTypeId = fetchTestTypeId(test_type_field.getValue());
		String runConfigId = fetchRunConfigID(testTypeId);
		this.RUN_CONFIG_ID.set(runConfigId);
	}

	private String fetchRunConfigIDForOFP(String uutId) {

		List<OfpConfigurationDto> allOfpConfigData = ofpConfig.getAllOfpConfig();
		String runConfigId = null;
		for (OfpConfigurationDto ofpConfigDto : allOfpConfigData) {
			if (ofpConfigDto.getUutId().equals(UUT_id)) {
				runConfigId = ofpConfigDto.getOfpConfigId();
				ofpNameLabel.setText(ofpConfigDto.getOfpName());
				ofpVersionLabel.setText(ofpConfigDto.getOfpVersion());
				configFileLabel.setText(ofpConfigDto.getConfigFile());
				break;
			} else {
				ofpNameLabel.setText("OFP NAME");
				ofpVersionLabel.setText("OFP VERSION");
				configFileLabel.setText("CONFIG FILE");
			}
		}
		return runConfigId;
	}

	private String fetchRunConfigID(String testTypeID) {
		List<RunConfigurationDto> allRunConfigData = runConfig.getAllRunConfig();
		String runConfigId = null;
		for (RunConfigurationDto runConfigDto : allRunConfigData) {
			if (runConfigDto.getUutId().equals(UUT_id) && runConfigDto.getTestTypeId().equals(testTypeID)) {
				runConfigId = runConfigDto.getRunConfigId();
				aitessTypeLabel.setText(runConfigDto.getAitess());
				driverLabel.setText(runConfigDto.getDriver());
				configFileLabel.setText(runConfigDto.getConfigFile());
				break;
			} else {
				aitessTypeLabel.setText("AITESS TYPE");
				driverLabel.setText("DRIVER");
				configFileLabel.setText("CONFIG FILE");
			}
		}
		return runConfigId;
	}

}
