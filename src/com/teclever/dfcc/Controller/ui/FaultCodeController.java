package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.model.FaultCodeConfig;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

public class FaultCodeController {

	   private GridPane faultCodeConfigMainGridPane = new GridPane();
	    private GridPane faultCodeConfigTableGridPane = new GridPane();
	    private GridPane midGridPane = new GridPane();

//	    private HBox midHBoxUUTType = new HBox(10);
	    private HBox midHBoxOFPVersion = new HBox(10);

	    private Button addUserBtn = new Button("ADD FAULT CODES");

//	    public ComboBox<String> uutTypeField = new ComboBox<>();
	    public ComboBox<String> ofpVersionField = new ComboBox<>();
	    private String UUT_ID;
	    private String ofpConfigId;

	    private ObservableList<OfpConfigurationDto> ofpVersionDataList;
	    private ObservableList<String> ofpVersionList = FXCollections.observableArrayList();

	    private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();

	    private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();
	    FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();

	    public void refreshFaultCodeConfigList() {
	        setTableData();
	    }

	    public String getRUN_CONFIG_ID() {
	        return RUN_CONFIG_ID.get();
	    }

	    public void setRUN_CONFIG_ID(String rUN_CONFIG_ID) {
	        RUN_CONFIG_ID.set(rUN_CONFIG_ID);
	    }

	    public StringProperty runConfigIdProperty() {
	        return RUN_CONFIG_ID;
	    }

	    public GridPane createFaultCodeConfigGridPane() {
	    	initializeOfpVersionComboBox();
	        faultCodeConfigMainGridPane.getStylesheets()
	                .add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/FaultCodeConfiguration.css").toExternalForm());

	        ColumnConstraints firstColumn = new ColumnConstraints();
	        firstColumn.setPercentWidth(100);

	        RowConstraints firstRow = new RowConstraints();
	        firstRow.setPercentHeight(9);
	        RowConstraints secondRow = new RowConstraints();
	        secondRow.setPercentHeight(91);


//	        faultCodeConfigMainGridPane.setPadding(new Insets(10));
	        faultCodeConfigMainGridPane.setVgap(5);
	        faultCodeConfigMainGridPane.setHgap(5);
	        faultCodeConfigMainGridPane.getColumnConstraints().addAll(firstColumn);
	        faultCodeConfigMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

	        faultCodeConfigMainGridPane.add(faultCodeMidContainer(), 0, 0);
	        faultCodeConfigMainGridPane.add(createFaultCodeConfigTable(), 0, 1);
	        return faultCodeConfigMainGridPane;
	    }

	   

//	    private void initializeUUTTypeComboBox() {
//	        uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
//	        for (UUTMasterDetailsDto uut : uutDataList) {
//	            uutTypeList.add(uut.getUutType());
//	        }
//	        uutTypeField.setItems(uutTypeList);
//	        uutTypeField.setOnAction((event) -> {
//	            UUT_ID = fetchUutId(uutTypeField.getValue());
//	            initializeOfpVersionComboBox();
////	            addUserBtn.setDisable(false);
//	            setTableData();
//	        });
//	    }

//	    private String fetchUutId(String uutType) {
//	        for (UUTMasterDetailsDto uut : uutDataList) {
//	            if (uut.getUutType().equals(uutType)) {
//	                return uut.getUutId();
//	            }
//	        }
//	        return null;
//	    }

	    private GridPane faultCodeMidContainer() {
	        ColumnConstraints firstColumn = new ColumnConstraints();
	        firstColumn.setPercentWidth(100);


	        RowConstraints firstRow = new RowConstraints();
	        firstRow.setPercentHeight(100);

	        midGridPane.getColumnConstraints().addAll(firstColumn);
	        midGridPane.getRowConstraints().addAll(firstRow);

//	        midGridPane.add(createUUTypeComboBox(), 0, 0);
	        midGridPane.add(createOFPVersionComboBox(), 0, 0);

	        midGridPane.getStyleClass().add("fault-code-Container");
	        return midGridPane;
	    }

//	    private HBox createUUTypeComboBox() {
//	        uutTypeField.setPromptText("UUT TYPE");
//	        midHBoxUUTType.setPadding(new Insets(0, 0, 0, 18.5));
//	        midHBoxUUTType.setAlignment(Pos.CENTER_LEFT);
//	        midHBoxUUTType.getChildren().add(uutTypeField);
//
//	        return midHBoxUUTType;
//	    }

	    private void initializeOfpVersionComboBox() {
	        ofpVersionList.clear();
	       UUT_ID= StateMachine.currentSessionDetails.getUutId();
	  	  
	        ofpVersionDataList = FXCollections.observableArrayList(ofpConfig.getOfpConfig(UUT_ID));
	        for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
	            ofpVersionList.add(ofpVersion.getOfpVersion());
	        }
	        ofpVersionField.setItems(ofpVersionList);
	        ofpVersionField.setOnAction((event) -> {
	        	addUserBtn.setDisable(false);
	            ofpConfigId = fetchOFPVersion(ofpVersionField.getValue());
	            this.RUN_CONFIG_ID.set(ofpConfigId);
	            setTableData();
	            
	        });
	        
	    }

	    private String fetchOFPVersion(String ofpVersionName) {
	        for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
	            if (ofpVersion.getOfpVersion().equals(ofpVersionName)) {
	                return ofpVersion.getOfpVersion();
	            }
	        }
	        return null;
	    }

	    private HBox createOFPVersionComboBox() {
	        ofpVersionField.setPromptText("OFP Version");
	        midHBoxOFPVersion.getStyleClass().add("faultcode-user-custom-tab-container");
	        
//	        midHBoxOFPVersion.setPadding(new Insets(0, 0, 0, 0));
	        midHBoxOFPVersion.setAlignment(Pos.CENTER_RIGHT);
	        midHBoxOFPVersion.getChildren().add(ofpVersionField);

	        return midHBoxOFPVersion;
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
	        if (ofpConfigId != null) {
	            FaultCodeResponse faultCodeList = faultCodeConfiguration.getFaultCodeList(UUT_ID, ofpConfigId);
	            ObservableList<FaultCodeConfig> tableData = FXCollections.observableArrayList();
	            if (faultCodeList.getResponse().getResponseCode() != 0) {
	                for (FaultCodeDTO faultCode : faultCodeList.getFaultCodeList()) {
	                    FaultCodeConfig faultCodeData = new FaultCodeConfig();
	                    faultCodeData.setId(faultCode.getFaultCodeMasterId());
	                    faultCodeData.setFaultCode(faultCode.getFaultCode());
	                    faultCodeData.setFilePath(faultCode.getFaultCodeFilePath());

	                    tableData.add(faultCodeData);
	                }
	            }

	            TableViewFactory<FaultCodeConfig> userFactory = new FaultCodeConfigTableViewFactory();
	            CustomTableView<FaultCodeConfig> customTableView = userFactory.createTableView(tableData, false, false);

	            faultCodeConfigTableGridPane.getChildren().clear(); // Clear existing table if any
	            faultCodeConfigTableGridPane.add(customTableView, 0, 0);
	        }
	    }
	
}
