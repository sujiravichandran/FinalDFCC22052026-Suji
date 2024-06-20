package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.RunAitessConfiguration;
import com.teclever.dfcc.utils.CustomButton;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddRunConfigurationController {

	public AddRunConfigurationController() {
		testType = new ComboBox<String>();
		aitessType = new ComboBox<String>();

//		super();
	}

	public static String runuutTypeValue;
	public static String runuutTypeId;
	public static String testTypeValue;
	public static String aitessTypeValue;
	public static String fileConfigName;
	public static String driverName;
	Map<String, String> aitessNameDriverNameMap = new HashMap<String, String>();
	Map<String, Integer> aitessNameAitessId = new HashMap<String, Integer>();
	static Map<Integer, String> aitessIdAitessName = new HashMap<Integer, String>();
	Map<String, String> TestTypeNameId = new HashMap<String, String>();

	RunConfigurationController runConfigurationController = new RunConfigurationController();

	@FXML
	private Pane addrun;

	@FXML
	private HBox hboxCancel;

	@FXML
	private HBox hboxSave;

	@FXML
	private Label headinglbl;

	@FXML
	private Label labelAitessCommand;

	@FXML
	private Label labelAitessname;

	@FXML
	private Label labelDriverName;
	@FXML
	private Button selectConfigfile;

	@FXML
	private ComboBox<String> testType;

	@FXML
	private ComboBox<String> aitessType;

	@FXML
	private TextField textAitessCommand;

	@FXML
	private TextField textDriverName;

	@FXML
	private VBox vBoxDriver;

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	RunConfigurationController mainPageController;

//    
	public void setMainPageController(RunConfigurationController mainPageController) {
		this.mainPageController = mainPageController;
	}

	@FXML
	public void initialize() {

		System.out.println("Initialize method called");
		loadTestTypes(runuutTypeId);
		loadAitessTypes(runuutTypeId);
		setupDriverLabel();
		setupAddButton();
	}

	private void setupAddButton() {
		CustomButton saveButton = new CustomButton("SAVE", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<String> validationErrors = validateFields();
				if (validationErrors.isEmpty()) {
					// Check if data already exists
					boolean dataExists = false;
					HashMap<String, String> nameId = new HashMap<>();
					AitessConfigurationManagement configManager = new AitessConfigurationManagement();
					UUTMasterDetailsDto[] uutDataList = configManager.getAllUUT();

					for (UUTMasterDetailsDto uutType : uutDataList) {
						nameId.put(uutType.getUutType(), uutType.getUutId());
					}

					String uutId = nameId.get(RunConfigurationController.runuutTypeValue);
					RunConfigurationManagement runConfiguration = new RunConfigurationManagement();
					List<RunConfigurationDto> existingConfigs = runConfiguration.getRunConfig(uutId);

					for (RunConfigurationDto config : existingConfigs) {
						if (config.getUutId().equals(uutId)
								&& config.getTestTypeId().equals(TestTypeNameId.get(testTypeValue))) {
							dataExists = true;
							break;
						}
					}

					if (dataExists) {
						// Create a confirmation alert
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
						alert.setHeaderText("Overwrite Confirmation");
						alert.setContentText("Are you sure you want to overwrite this test type?");

						ButtonType buttonYes = new ButtonType("Yes", ButtonData.YES);
						ButtonType buttonNo = new ButtonType("No", ButtonData.NO);
						alert.getButtonTypes().setAll(buttonYes, buttonNo);

						// Show the alert and wait for a response
						Optional<ButtonType> result = alert.showAndWait();
						if (result.isPresent() && result.get() == buttonYes) {
							// User chose YES, proceed with the save operation
							saveRunConfiguration(uutId);
						}
					} else {
						// Data does not exist, proceed with save without confirmation
						saveRunConfiguration(uutId);
					}
				} else {
					// Show alert if validation fails
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Validation Warning");
					alert.setHeaderText("Incomplete Data");
					alert.setContentText(String.join("\n", validationErrors));
					alert.showAndWait();
				}
			}
		});

		saveButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
		saveButton.setAlignment(Pos.CENTER);
		this.hboxSave.getChildren().add(saveButton);
		this.hboxSave.setAlignment(Pos.CENTER);

		CustomButton cancelButton = new CustomButton("CANCEL", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) AddRunConfigurationController.this.addrun.getScene().getWindow();
				stage.close();
			}
		});
		cancelButton.setButtonStyle("170", "30", "17", "Arial", "bold", "#ffffff", "#77ABAE", "#ffffff", "0", "10");
		cancelButton.setAlignment(Pos.CENTER);

		this.hboxCancel.getChildren().add(cancelButton);
		this.hboxCancel.setAlignment(Pos.CENTER);
	}

	private void saveRunConfiguration(String uutId) {
		RunConfigurationDto runaitessConfigurationDTO = new RunConfigurationDto();
		runaitessConfigurationDTO.setAitess(aitessTypeValue);
		runaitessConfigurationDTO.setConfigFile(fileConfigName);
		runaitessConfigurationDTO.setTestTypeId(TestTypeNameId.get(testTypeValue));
		runaitessConfigurationDTO.setDriver(driverName);
		runaitessConfigurationDTO.setUutId(uutId);

		RunConfigurationManagement runConfiguration = new RunConfigurationManagement();
		RunConfigurationResponse res = runConfiguration.addRunConfig(runaitessConfigurationDTO, runuutTypeId);

		mainPageController.testTypeField.setText(testTypeValue);
		mainPageController.aitessType.setText(aitessTypeValue);
		mainPageController.driverLabel.setText(driverName);
		mainPageController.configFile.setText(fileConfigName);

		// Close the popup stage
		Stage stage = (Stage) addrun.getScene().getWindow();
		stage.close();

		// Refresh the main page
		mainPageController.refresh();
	}

	private List<String> validateFields() {
		List<String> errors = new ArrayList<>();

		if (testType.getValue() == null) {
			errors.add("Please enter the TestType.");
		}
		if (aitessType.getValue() == null) {
			errors.add("Please enter the AitessType.");
		}
		if (fileConfigName == null || fileConfigName.isEmpty()) {
			errors.add("Please select the File Configuration.");
		}
		if (driverName == null || driverName.isEmpty()) {
			errors.add("Please enter the Driver Name.");
		}

		return errors;
	}

	public void loadTestTypes(String uutType) {
		RunConfigurationManagement runConfiguration = new RunConfigurationManagement();
		TestTypeMasterDetailsDto[] lst = runConfiguration.getTestTypeByUUTId(uutType);
		ArrayList<String> testLst = new ArrayList<String>();
		TestTypeMasterDetailsDto[] testTypeMasterDetailsDtoArray = lst;
		int n = lst.length;
		int n2 = 0;
		while (n2 < n) {
			TestTypeMasterDetailsDto t = testTypeMasterDetailsDtoArray[n2];
			TestTypeNameId.put(t.getTestName(), t.getTestTypeId());
			testLst.add(t.getTestName());

			++n2;
		}
		System.out.println("uutType" + uutType);
		System.out.println("RUNtest size: " + testLst.size());
		ObservableList<String> types = FXCollections.observableArrayList(testLst);
		System.out.println("TEST TYPE" + types);
		testType.setItems(types);
	}

	@FXML
	void testTypeAction(ActionEvent event) {

		testTypeValue = (String) testType.getValue();
		System.out.println("Test Type ADD RUNCONfig: " + testType.getId());

	}

	@FXML
	void selectConfigfileAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Configuration File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fileChooser.showOpenDialog(this.selectConfigfile.getScene().getWindow());
		if (file != null) {
			String filePath = file.getAbsolutePath();
			this.selectConfigfile.setText(filePath);
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			prefs.put("LastUsedFilePath", filePath);
			fileConfigName = filePath;
		}
	}

	private void setupDriverLabel() {
		TextField driverLabel = new TextField("Driver");
		driverLabel.setPrefWidth(380);
		driverLabel.setPrefHeight(25.0);

		driverLabel.setEditable(false);
		driverLabel.setStyle(
				"-fx-background-color: white; -fx-border-radius:10px; -fx-border-color: #b9bcbd; -fx-background-radius:10px; -fx-border-width: 1px;-fx-font-size: 16px;-fx-alignment: center;-fx-text-fill: black;");

		aitessType.setOnAction(event -> {
			aitessTypeValue = (String) this.aitessType.getValue();
			CompletableFuture.supplyAsync(() -> {
				driverName = fetchDriverNameFromDatabase(aitessTypeValue);
				return driverName;
			}).thenAccept(driverName -> {
				Platform.runLater(() -> {
					driverLabel.setText(driverName);
				});
			}).exceptionally(e -> {
				e.printStackTrace();
				return null;
			});
		});

		vBoxDriver.getChildren().add(driverLabel);
	}

	private String fetchDriverNameFromDatabase(String driverName) {
		try {

			driverName = aitessNameDriverNameMap.get(aitessTypeValue);

			return driverName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadAitessTypes(String runuutTypeValue) {
		try {
			Map<String, String> nameIdMap = DFCCConstant.getUutNameIdMap();
			List<AitessConfigurationDto> uutDataList = this.configManager.getAitessConfig(runuutTypeValue);
			ArrayList<String> aitessTypeList = new ArrayList<String>();
			if (uutDataList.size() > 0) {
				for (AitessConfigurationDto uutType : uutDataList) {
					aitessTypeList.add(uutType.getAitessName());
					aitessNameDriverNameMap.put(uutType.getAitessName(), uutType.getDriverName());
					aitessNameAitessId.put(uutType.getAitessName(), uutType.getAitessId());
					aitessIdAitessName.put(uutType.getAitessId(), uutType.getAitessName());
				}
			}

			System.out.println("Size aitessTypeList------- " + aitessTypeList.size());
			System.out.println("RUNAitesTypeList-------" + aitessTypeList);
			ObservableList<String> types = FXCollections.observableArrayList(aitessTypeList);
			System.out.println("AIteeesTypess----- " + types);

			aitessType.setItems(types);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

