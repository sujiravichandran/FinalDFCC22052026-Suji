package com.teclever.dfcc.Controller.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.LevelOneAddResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.LevelsAddResponse;
//import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.model.SessionStage;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddStageController implements Initializable {
	@FXML
	private HBox addStageFormHeading;

	@FXML
	private AnchorPane addStageMainContainer;

	@FXML
	private Button add_stage1_button;

	@FXML
	private Button add_subStage_button;

	@FXML
	private VBox addstageForm;

	@FXML
	private CheckBox checkboxBls;

	@FXML
	private CheckBox checkboxFru;

	@FXML
	private CheckBox checkboxPqt;

	@FXML
	private CheckBox checkboxProduction;

	@FXML
	private CheckBox checkboxTrails;

	@FXML
	private Label headerLabel;

	@FXML
	private VBox parent_stage_name_box;

	@FXML
	private VBox session_type_box;

	@FXML
	private Button stage_cancel_btn;

	@FXML
	private TextField stage_child_name_field;

	@FXML
	private VBox stage_name_box;

	@FXML
	private TextField stage_name_field;

	@FXML
	private TextField stage_parent_name_field;

	@FXML
	private ComboBox<String> stage_testType_field;

	@FXML
	private VBox sub_stage_name_box;

	@FXML
	private VBox test_type_box;

	@FXML
	private Button add_new_stage_button;
	@FXML
	private Button update_button;

	private StageConfiguration stageConfig = new StageConfiguration();
	private RunConfigurationManagement runConfig = new RunConfigurationManagement();

	private String stage1Name;
	private String UUT_ID;
	private String userType;
	private String PARENT_ID;
	private String CHILD_ID;
	private String TEST_TYPE_ID;
	private SessionStage sessionStage;

	private ObservableList<TestTypeMasterDetailsDto> testTypeDataList;
	private ObservableList<String> testTypeList = FXCollections.observableArrayList();

	private Notifications notify;
	private StageConfigurationController mainPageController;

	public void setMainPageController(StageConfigurationController mainPageController) {
		this.mainPageController = mainPageController;
	}

	public void setNewStageData(String stage1Name, String uutType, String userType) {
		this.stage1Name = stage1Name;
		this.UUT_ID = uutType;
		this.userType = userType;
		setupNewStage(stage1Name);
//		List<SessionMasterDTO> sessionTypeList=stageConfig.getSessionMasterList();
//		System.out.println(sessionTypeList.size());
//		System.out.println("SESSION TYPE LIST: "+sessionTypeList.toString());
	}

	private void setupNewStage(String stage1Name) {
		
		headerLabel.setText("Add New Stage");
		stage_name_field.setText(stage1Name);
		parent_stage_name_box.setVisible(false);
		parent_stage_name_box.setManaged(false);
		sub_stage_name_box.setVisible(false);
		sub_stage_name_box.setManaged(false);
		test_type_box.setVisible(false);
		test_type_box.setManaged(false);
		add_subStage_button.setVisible(false);
		add_subStage_button.setManaged(false);
		update_button.setVisible(false);
		update_button.setManaged(false);
	}

	public void setEditStage1Data(SessionStage stage1Data,String uutType,String userType) {
		this.sessionStage = stage1Data;
		this.UUT_ID = uutType;
		this.userType = userType;
		setupEditStage1(stage1Data);
	}

	private void setupEditStage1(SessionStage stage1Data) {
		headerLabel.setText("Edit Stage1");
		add_new_stage_button.setText("Update Stage");
		stage_name_field.setText(stage1Data.getL1_name());

		List<String> sessionTypes = stage1Data.getSessionType();

		// Check the checkboxes based on the session types
		checkboxProduction.setSelected(sessionTypes.contains("Production"));
		checkboxTrails.setSelected(sessionTypes.contains("Trails"));
		checkboxBls.setSelected(sessionTypes.contains("BLS"));
		checkboxPqt.setSelected(sessionTypes.contains("PQT"));
		checkboxFru.setSelected(sessionTypes.contains("FRU"));

		parent_stage_name_box.setVisible(false);
		parent_stage_name_box.setManaged(false);
		sub_stage_name_box.setVisible(false);
		sub_stage_name_box.setManaged(false);
		test_type_box.setVisible(false);
		test_type_box.setManaged(false);
		add_subStage_button.setVisible(false);
		add_subStage_button.setManaged(false);
		update_button.setVisible(false);
		update_button.setManaged(false);
	}

	@FXML
	void onClickAddStage1(ActionEvent event) {
		String selectedSessionType = "";
		if (checkboxProduction.isSelected()) {
			selectedSessionType += "Production,";
		}
		if (checkboxTrails.isSelected()) {
			selectedSessionType += "Trails,";
		}
		if (checkboxBls.isSelected()) {
			selectedSessionType += "BLS,";
		}
		if (checkboxPqt.isSelected()) {
			selectedSessionType += "PQT,";
		}
		if (checkboxFru.isSelected()) {
			selectedSessionType += "FRU,";
		}
		if (!selectedSessionType.isEmpty()) {
			selectedSessionType = selectedSessionType.substring(0, selectedSessionType.length() - 1);
		}
		if (add_new_stage_button.getText().equals("Add New Stage")) {
			LevelOneAddResponse response = stageConfig.addLevelOneStageMaster(stage1Name, selectedSessionType, UUT_ID,
					userType);
			if (response.getResponse().getResponseCode() == 1) {
				LevelOneDto levelOne = response.getLevelOneResponse();
				notify.showSuccessAlert(response.getResponse().getResponseMessage());
				Stage stage = (Stage) addStageMainContainer.getScene().getWindow();
				stage.close();
				mainPageController.refreshStageList();
//				mainPageController.setStage1PId(levelOne.getLevelOneId());
			} else {
				notify.showErrorAlert(response.getResponse().getResponseMessage());
			}
		} else if (add_new_stage_button.getText().equals("Update Stage")) {
			System.out.println("UPDATE STAGE: " + sessionStage.toString());
			LevelOneAddResponse response = stageConfig.updateLevelOneStageMaster(sessionStage.getId(),stage_name_field.getText(), selectedSessionType, UUT_ID,
					userType);
			if (response.getResponse().getResponseCode() == 1) {
				LevelOneDto levelOne = response.getLevelOneResponse();
				notify.showSuccessAlert(response.getResponse().getResponseMessage());
				Stage stage = (Stage) addStageMainContainer.getScene().getWindow();
				stage.close();
				mainPageController.refreshStageList();
//				mainPageController.setStage1PId(levelOne.getLevelOneId());
			} else {
				notify.showErrorAlert(response.getResponse().getResponseMessage());
			}
		}

	}

	public void setAddSubStageData(String uutID, String pId, String stageName) {
		this.UUT_ID = uutID;
		setupAddSubStage(pId, stageName);
	}

	private void setupAddSubStage(String pId, String stageName) {
		initializeComboBox();
		headerLabel.setText("Add Sub-Stage");
		this.PARENT_ID = pId;
		stage_parent_name_field.setText(stageName);
		session_type_box.setVisible(false);
		session_type_box.setManaged(false);
		stage_name_box.setVisible(false);
		stage_name_box.setManaged(false);
		update_button.setVisible(false);
		update_button.setManaged(false);
		add_new_stage_button.setVisible(false);
		add_new_stage_button.setManaged(false);
	}

	@FXML
	void onClickAddSubStage(ActionEvent event) {
		System.out.println("ttID-onclick: " + TEST_TYPE_ID);
		if (stage_child_name_field.getText().isEmpty()) {
			notify.showSuccessAlert("Please provide Sub stage Name");
		} else {
			LevelsAddResponse response = stageConfig.addStageMasterLevel(PARENT_ID, stage_child_name_field.getText(),
					TEST_TYPE_ID);
			if (response.getResponse().getResponseCode() == 1) {
				notify.showSuccessAlert(response.getResponse().getResponseMessage());
				Stage stage = (Stage) addStageMainContainer.getScene().getWindow();
				stage.close();
				mainPageController.refreshStageList();
			} else {
				notify.showErrorAlert(response.getResponse().getResponseMessage());
			}

		}

	}

	public void setEditStageData(String uutID, String childId, String pId, String stageName, String testType) {
		this.UUT_ID = uutID;
		setupEditStage(childId, pId, stageName, testType);
	}

	private void setupEditStage(String childId, String pId, String stageName, String testType) {
		initializeComboBox();

		this.PARENT_ID = pId;
		this.CHILD_ID = childId;

		headerLabel.setText("Edit Stage");
		session_type_box.setVisible(false);
		session_type_box.setManaged(false);
		parent_stage_name_box.setVisible(false);
		parent_stage_name_box.setManaged(false);
		sub_stage_name_box.setVisible(false);
		sub_stage_name_box.setManaged(false);
		add_subStage_button.setVisible(false);
		add_subStage_button.setManaged(false);
		add_new_stage_button.setVisible(false);
		add_new_stage_button.setManaged(false);
		stage_name_field.setText(stageName);
		stage_testType_field.setValue(testType);
	}

	@FXML
	void onClickUpdateStage(ActionEvent event) {
		System.out.println("ttID-onclick: " + TEST_TYPE_ID);
		System.out.println("UPDATE: " + CHILD_ID + " :" + PARENT_ID + " :" + stage_child_name_field.getText() + " :"
				+ TEST_TYPE_ID);

//			String levelId,String parentId, String stageName, String testTypeId
		LevelsAddResponse response = stageConfig.updateStageMasterLevel(CHILD_ID, PARENT_ID, stage_name_field.getText(),
				TEST_TYPE_ID);
		if (response.getResponse().getResponseCode() == 1) {
			notify.showSuccessAlert(response.getResponse().getResponseMessage());
			Stage stage = (Stage) addStageMainContainer.getScene().getWindow();
			stage.close();
			mainPageController.refreshStageList();
		} else {
			notify.showErrorAlert(response.getResponse().getResponseMessage());
		}
	}

	@FXML
	void onClickCancelStage(ActionEvent event) {
		Stage stage = (Stage) addStageMainContainer.getScene().getWindow();
		stage.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		addStageMainContainer.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AddStage.css").toExternalForm());
	}

	private void initializeComboBox() {
		System.out.println("COMBO_UUT: " + UUT_ID);
		testTypeDataList = FXCollections.observableArrayList(runConfig.getTestTypeByUUTId(UUT_ID));
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			testTypeList.add(testType.getTestName());
		}
		stage_testType_field.setItems(testTypeList);
		if (!testTypeList.isEmpty()) {
			stage_testType_field.setValue(testTypeList.get(0));
			TEST_TYPE_ID = fetchTestTypeId(stage_testType_field.getValue());
		}
		stage_testType_field.setOnAction((event) -> {
			TEST_TYPE_ID = fetchTestTypeId(stage_testType_field.getValue());
		});
	}

	private String fetchTestTypeId(String testTypeName) {
		for (TestTypeMasterDetailsDto testType : testTypeDataList) {
			if (testType.getTestName().equals(testTypeName)) {
				return testType.getTestTypeId();
			}
		}
		return null;
	}

}
