package com.teclever.dfcc.Controller.ui;

import java.io.File;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.model.FaultCodeConfig;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
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
    private HBox midHBoxOFPVersion = new HBox(10);

    private Button addUserBtn = new Button("ADD FAULT CODES");

    public ComboBox<String> uutTypeField = new ComboBox<>();
    public ComboBox<String> ofpVersionField = new ComboBox<>();
    private ObservableList<UUTMasterDetailsDto> uutDataList;
    private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
    private String UUT_ID;
    private String ofpConfigId;

    private ObservableList<OfpConfigurationDto> ofpVersionDataList;
    private ObservableList<String> ofpVersionList = FXCollections.observableArrayList();

    private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();

    private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
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
        initializeUUTTypeComboBox();
        faultCodeConfigMainGridPane.getStylesheets()
                .add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/FaultCodeConfiguration.css").toExternalForm());
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
        faultCodeConfigMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

        faultCodeConfigMainGridPane.add(createFaultCodeConfigTitleGridPane(), 0, 0);
        faultCodeConfigMainGridPane.add(faultCodeMidContainer(), 0, 1);
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
            UUT_ID = fetchUutId(uutTypeField.getValue());
            initializeOfpVersionComboBox();
            setTableData();
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

    private GridPane faultCodeMidContainer() {
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(15);

        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(85);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(100);

        midGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
        midGridPane.getRowConstraints().addAll(firstRow);

        midGridPane.add(createUUTypeComboBox(), 0, 0);
        midGridPane.add(createOFPVersionComboBox(), 1, 0);

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

    private void initializeOfpVersionComboBox() {
        ofpVersionList.clear();
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
                return ofpVersion.getOfpConfigId();
            }
        }
        return null;
    }

    private HBox createOFPVersionComboBox() {
        ofpVersionField.setPromptText("OFP Version");
        midHBoxOFPVersion.setPadding(new Insets(0, 0, 0, 18.5));
        midHBoxOFPVersion.setAlignment(Pos.CENTER_LEFT);
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
        ObservableList<FaultCodeConfig> tableData = FXCollections.observableArrayList();

        if (UUT_ID != null && ofpConfigId != null) {
            FaultCodeResponse faultCodeList = faultCodeConfiguration.getFaultCodeList(UUT_ID, ofpConfigId);
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
        }
       
        TableViewFactory<FaultCodeConfig> userFactory = new FaultCodeConfigTableViewFactory();
        CustomTableView<FaultCodeConfig> customTableView = userFactory.createTableView(tableData, false, false);

        faultCodeConfigTableGridPane.getChildren().clear(); // Clear existing table if any
        faultCodeConfigTableGridPane.add(customTableView, 0, 0);
    }

    private void uploadfile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(faultCodeConfigMainGridPane.getScene().getWindow());
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            FaultCodeResponse response = faultCodeConfiguration.faultCodeFile(filePath, UUT_ID, ofpConfigId);
            System.out.println("OFP" + ofpConfigId);
            System.out.println("UUT" + UUT_ID);
            System.out.println(response.getResponse().getResponseCode() + "   " + response.getResponse().getResponseMessage());
            if (response.getResponse().getResponseCode() == 1) {
                Notifications.showSuccessAlert("File Uploaded Successfully");
                refreshFaultCodeConfigList();
            } else if (response.getResponse().getResponseCode() == 0) {
                Notifications.showErrorAlert(response.getResponse().getResponseMessage());
            }
        }
    }
}
