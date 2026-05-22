package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PbitOfpSelectionController {

	@FXML
	private ComboBox<String> OFPVersion;

	@FXML
	private HBox addOfpHeading;

	@FXML
	private Button cancel_button;

	@FXML
	private Label headerLabel;

	@FXML
	private Button oKButton;

	@FXML
	private Label ofpch1;

	@FXML
	private Label ofpch2;

	@FXML
	private Label ofpch3;

	@FXML
	private Label ofpch4;

	@FXML
	private RadioButton option1;

	@FXML
	private RadioButton option2;
	
	@FXML
	private ToggleGroup optionGroup;

	@FXML
	private AnchorPane selcetOfp;

	@FXML
	private Label wdmch1;

	@FXML
	private Label wdmch2;

	@FXML
	private Label wdmch3;

	@FXML
	private Label wdmch4;

	private String wdmCh1 = StateMachine.WDMStatus.getChannel1Status();
	private String wdmCh2 = StateMachine.WDMStatus.getChannel2Status();
	private String wdmCh3 = StateMachine.WDMStatus.getChannel3Status();
	private String wdmCh4 = StateMachine.WDMStatus.getChannel4Status();

	private String ofpCh1 = StateMachine.OFPversionStatus.getChannel1Status();
	private String ofpCh2 = StateMachine.OFPversionStatus.getChannel2Status();
	private String ofpCh3 = StateMachine.OFPversionStatus.getChannel3Status();
	private String ofpCh4 = StateMachine.OFPversionStatus.getChannel4Status();
	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
		
	@FXML
	public void initialize() {
		
///		Suji Added for Radio button selection based on options::
		optionGroup = new ToggleGroup();

	    option1.setToggleGroup(optionGroup);
	    option2.setToggleGroup(optionGroup);
//	    Exit::
	    
//	    Suji Added for OFP Selection popup resuse::
	    aitessProcessControlManagement.pbitCheck();
		
		List <String> ofpList = StateMachine.getOfpList();
//		////System.out.println("Popup Ofp List" +ofpList );
		String ofpValueCheck = StateMachine.getOfpValueCheck();
//		////System.out.println("Popup Version" + ofpValueCheck);
//		////System.out.println("OFP  Response Cod Check " + aitessProcessControlManagement.pbitCheck().getResponseCode());

		if (!ofpList.contains(ofpValueCheck) ||
				 aitessProcessControlManagement.pbitCheck().getResponseCode() == 500 ||
				 aitessProcessControlManagement.pbitCheck().getResponseCode() == 400) {
//			////System.out.println("Entred OFP .." + aitessProcessControlManagement.pbitCheck().getResponseCode());
			option1.setDisable(true);
		}else {
//			////System.out.println("Entred OFP Else" + aitessProcessControlManagement.pbitCheck().getResponseCode());
			option1.setDisable(false);
		}
//		Exit::
		
		
		oKButton.setOnAction(e -> handleSaveButtonAction());
		cancel_button.setOnAction(e -> handleCancelButtonAction());

		OFPVersion.setVisible(true);
		wdmch1.setText(wdmCh1);
		wdmch2.setText(wdmCh2);
		wdmch3.setText(wdmCh3);
		wdmch4.setText(wdmCh4);

		ofpch1.setText(ofpCh1);
		ofpch2.setText(ofpCh2);
		ofpch3.setText(ofpCh3);
		ofpch4.setText(ofpCh4);

	}

	private String UUT_ID;
	private String ofpConfigId;
	private ObservableList<OfpConfigurationDto> ofpVersionDataList;
	private ObservableList<String> ofpVersionList = FXCollections.observableArrayList();
	private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();
	private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();

	public String getRUN_CONFIG_ID() {
		return RUN_CONFIG_ID.get();
	}

	public void setRUN_CONFIG_ID(String rUN_CONFIG_ID) {
		RUN_CONFIG_ID.set(rUN_CONFIG_ID);
	}

	public StringProperty runConfigIdProperty() {
		return RUN_CONFIG_ID;
	}

	private String fetchOFPVersion(String ofpVersionName) {
		for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
			if (ofpVersion.getOfpVersion().equals(ofpVersionName)) {
//				////System.out.println("OFP CHECK SUSPECT ::" + ofpVersion);
				return ofpVersion.getOfpConfigId(); // return OFP_7357
			}
		}
		return null;
	}

	@FXML
	void onClickOption1(ActionEvent event) {
		OFPVersion.setVisible(false);
	}

	@FXML
	void onClickOption2(ActionEvent event) {
		OFPVersion.setVisible(true);
//		////System.out.println("Entred Option2 method");

		initializeOfpVersionComboBox();
	}

	@FXML
	private void handleSaveButtonAction() {
		if (option1.isSelected()) {
//			////System.out.println("Check Option1");
			StateMachine.setPbitOption1(true);
		} else if (option2.isSelected()) {
//			////System.out.println("Check Option2");
			StateMachine.setPbitOption2(true);
		}
		if (!option1.isSelected() && !option2.isSelected()) {
			Notifications.showErrorAlert("Please select an option to proceed");
			return;
		}
		if (OFPVersion.isVisible() && (OFPVersion.getValue() == null || OFPVersion.getValue().isEmpty())) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Validation Error");
			alert.setHeaderText(null);
			alert.setContentText("Please select an OFP Version to proceed.");
			alert.initOwner(oKButton.getScene().getWindow());
			alert.showAndWait();
			return;
		}

		

		Stage stage = (Stage) oKButton.getScene().getWindow();
		stage.close();
	}

	private void initializeOfpVersionComboBox() {
		ofpVersionList.clear();
		UUT_ID = StateMachine.currentSessionDetails.getUutId();

		ofpVersionDataList = FXCollections.observableArrayList(ofpConfig.getOfpConfig(UUT_ID));
		for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
			ofpVersionList.add(ofpVersion.getOfpVersion());
		}

		OFPVersion.setItems(ofpVersionList);
		OFPVersion.setOnAction((event) -> {
			ofpConfigId = fetchOFPVersion(OFPVersion.getValue());
			StateMachine.setOfpList(ofpVersionList);
			this.RUN_CONFIG_ID.set(ofpConfigId);
//			////System.out.println("RUN_CONFIG_IDRUN_CONFIG_ID{{{" + RUN_CONFIG_ID);

		});

	}
	
	

//	@FXML
//	private void handleCancelButtonAction() {
//		Stage stage = (Stage) cancel_button.getScene().getWindow();
//		StateMachine.setTestState(TestState.PENDING);
//		stage.close();
//
//	}
	
//	Suji Changed for Popup when user selects cancel test:::
	
	@FXML
	private void handleCancelButtonAction() {
		Stage stage = (Stage) cancel_button.getScene().getWindow();
//		StateMachine.setConfirmTestStop(true);
		StateMachine.setTestState(TestState.STOPPED);
		StateMachine.setConfirmTestFileCompleted(false);
		StateMachine.setCancelTest(true);
		stage.close();

	}
	
	
	

}
