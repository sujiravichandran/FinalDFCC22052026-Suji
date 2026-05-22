package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.utils.Notifications;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class OfpLoadingPopUpController {

	
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
	    private AnchorPane selcetOfp;

	    private String UUT_ID;
		private String ofpConfigId;
		private ObservableList<OfpConfigurationDto> ofpVersionDataList;
		private ObservableList<String> ofpVersionList = FXCollections.observableArrayList();
		private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();
		private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();
		

		@FXML
		public void initialize() {
			oKButton.setOnAction(e -> handleSaveButtonAction());
			cancel_button.setOnAction(e -> handleCancelButtonAction());
			initializeOfpVersionComboBox();
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

		private String fetchOFPVersion(String ofpVersionName) {
			for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
				if (ofpVersion.getOfpVersion().equals(ofpVersionName)) {
					return ofpVersion.getOfpConfigId(); // return OFP_7357
				}
			}
			return null;
		}
		
		@FXML
		private void handleSaveButtonAction() {
			
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

		
		@FXML
		private void handleCancelButtonAction() {
			Stage stage = (Stage) cancel_button.getScene().getWindow();
			StateMachine.setTestState(TestState.STOPPED);
			StateMachine.setConfirmTestFileCompleted(false);
			StateMachine.setCancelTest(true);
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
				this.RUN_CONFIG_ID.set(ofpConfigId);
//				////System.out.println("RUN_CONFIG_IDRUN_CONFIG_ID{{{" + RUN_CONFIG_ID);

			});

		}

	
	
}
