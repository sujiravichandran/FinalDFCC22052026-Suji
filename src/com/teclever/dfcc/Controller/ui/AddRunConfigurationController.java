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
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddRunConfigurationController {

	public AddRunConfigurationController() {
		testType = new ComboBox<String>();
		aitessType = new ComboBox<String>();
	}

	public static String runuutTypeValue;
	public static String runuutTypeId;
	public static String testTypeValue;
	public static String aitessTypeValue;
	public static String fileConfigName;
	public static String fileConfigName2;
	public static String driverName;

	Map<String, String> aitessNameDriverNameMap = new HashMap<String, String>();
	Map<String, Integer> aitessNameAitessId = new HashMap<String, Integer>();
	static Map<Integer, String> aitessIdAitessName = new HashMap<Integer, String>();
	Map<String, String> TestTypeNameId = new HashMap<String, String>();

	RunConfigurationController runConfigurationController = new RunConfigurationController();

	@FXML
    private Pane addrun;

    @FXML
    private ComboBox<String> aitessType;

    @FXML
    private Button cancelButton;

    @FXML
    private Label headinglbl;

    @FXML
    private Label labelAitessname;

    @FXML
    private Label labelAitessname1;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectConfigfile1;

    @FXML
    private Button selectConfigfile2;

    @FXML
    private ComboBox<String> testType;

    @FXML
    private HBox vBoxDriver1;

    @FXML
    private HBox vBoxDriver2;

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfiguration = new RunConfigurationManagement();
	
	RunConfigurationController mainPageController;

//    
	public void setMainPageController(RunConfigurationController mainPageController) {
		this.mainPageController = mainPageController;
	}

	@FXML
	public void initialize() {

		loadTestTypes(runuutTypeId);
		loadAitessTypes(runuutTypeId);
		setupDriverLabel();
		setupAddButton();
		setupCancelButton();

		// Set fixed size for the stage after the scene is fully initialized
		Platform.runLater(() -> {
			Stage stage = (Stage) addrun.getScene().getWindow();
			stage.setMinWidth(400); // Set your desired width
			stage.setMaxWidth(444);
			stage.setMinHeight(557); // Set your desired height
			stage.setMaxHeight(557);
			stage.setResizable(false);
		});
	}

	private void setupAddButton() {

		saveButton.setOnAction(e -> {
			handleSave();
		});
		saveButton.setAlignment(Pos.CENTER);
		this.saveButton.setAlignment(Pos.CENTER);
	}
		private void setupCancelButton() {
		cancelButton.setOnAction(e -> {
			handleCancel();
		});

		this.cancelButton.setAlignment(Pos.CENTER);
	}

	private void handleCancel() {

		Stage stage = (Stage) AddRunConfigurationController.this.addrun.getScene().getWindow();
		stage.close();
	}

	private void handleSave() {
		List<String> validationErrors = validateFields();
		if (validationErrors.isEmpty()) {
			
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

	private void saveRunConfiguration(String uutId) {
	     Stage stage = (Stage) addrun.getScene().getWindow();
	     
		 Platform.runLater(() -> {
	            stage.getScene().setCursor(Cursor.WAIT);
	            setControlsDisabled(stage.getScene().getRoot(), true);
		 });
		RunConfigurationDto runaitessConfigurationDTO = new RunConfigurationDto();
		runaitessConfigurationDTO.setAitess(aitessTypeValue);
		runaitessConfigurationDTO.setConfigFile(fileConfigName);
		runaitessConfigurationDTO.setAitess2ConfigFile(fileConfigName2);
		System.out.println("Aitess 2 Config File"+fileConfigName2);
		runaitessConfigurationDTO.setTestTypeId(TestTypeNameId.get(testTypeValue));
		runaitessConfigurationDTO.setDriver(driverName);
		runaitessConfigurationDTO.setUutId(uutId);
	
		 Task<RunConfigurationResponse> saveTask = new Task<RunConfigurationResponse>() {
		        @Override
		        protected RunConfigurationResponse call() throws Exception {
		        	RunConfigurationResponse res = runConfiguration.addRunConfig(runaitessConfigurationDTO, runuutTypeId);
		        	return res;
		        }
		    };

		    saveTask.setOnSucceeded(event -> {
		        Platform.runLater(() -> {
		        	stage.getScene().setCursor(Cursor.DEFAULT);
			        setControlsDisabled(stage.getScene().getRoot(), false);
		            stage.close();
		            mainPageController.refresh();
		        });
		        Notifications.showSuccessAlert(saveTask.getValue().getResponseMessage());
		    });

		    saveTask.setOnFailed(event -> {
		        Platform.runLater(() -> {
		        	stage.getScene().setCursor(Cursor.DEFAULT);
			        setControlsDisabled(stage.getScene().getRoot(), false);
		        });
		        Notifications.showErrorAlert(saveTask.getValue().getResponseMessage());
		    });

		    // Start the task on a background thread
		    new Thread(saveTask).start();


	}
	
	private void setControlsDisabled(Node root, boolean disabled) {
	    for (Node node : root.lookupAll("*")) {
	        if (node instanceof Control) {
	            ((Control) node).setDisable(disabled);
	        }
	    }
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
		
		if (fileConfigName2 == null || fileConfigName2.isEmpty()) {
			errors.add("Please select the File Configuration2.");
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

		ObservableList<String> types = FXCollections.observableArrayList(testLst);

		testType.setItems(types);
	}

	@FXML
	void testTypeAction(ActionEvent event) {

		testTypeValue = (String) testType.getValue();

	}

	@FXML
	void selectConfigfileAction1(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Configuration File");
		  fileChooser.getExtensionFilters()
          .addAll(new FileChooser.ExtensionFilter("Excel Files", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file1 = fileChooser.showOpenDialog(this.selectConfigfile1.getScene().getWindow());
		if (file1 != null) {
			String filePath = file1.getAbsolutePath();
			this.selectConfigfile1.setText(filePath);
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			prefs.put("LastUsedFilePath", filePath);
			fileConfigName = filePath;
		}
	}
		@FXML
		void selectConfigfileAction2(ActionEvent event) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select a Configuration File");
			 fileChooser.getExtensionFilters()
	          .addAll(new FileChooser.ExtensionFilter("Excel Files", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file2 = fileChooser.showOpenDialog(this.selectConfigfile2.getScene().getWindow());
		if (file2 != null) {
			String filePath = file2.getAbsolutePath();
			this.selectConfigfile2.setText(filePath);
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			prefs.put("LastUsedFilePath", filePath);
			fileConfigName2 = filePath;
		}
	}

	private void setupDriverLabel() {
		TextField driverLabel = new TextField("Driver");
		


		driverLabel.setEditable(false);
		driverLabel.setStyle(
				"-fx-background-color: white; -fx-border-radius:10px; -fx-border-color: #b9bcbd; -fx-background-radius:10px; -fx-border-width: 1px;-fx-font-size: 16px;-fx-alignment: center;-fx-text-fill: black;");

	    driverLabel.prefWidthProperty().bind(vBoxDriver1.widthProperty());

		
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
		
		driverLabel.setEditable(false);
		driverLabel.setStyle(
				"-fx-background-color: white; -fx-border-radius:10px; -fx-border-color: #b9bcbd; -fx-background-radius:10px; -fx-border-width: 1px;-fx-font-size: 16px;-fx-alignment: center;-fx-text-fill: black;");

	    driverLabel.prefWidthProperty().bind(vBoxDriver1.widthProperty());

		
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

		vBoxDriver1.getChildren().add(driverLabel);
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
			List<AitessConfigurationDto> uutDataList = this.configManager.getAitessConfig();
			ArrayList<String> aitessTypeList = new ArrayList<String>();
			if (uutDataList.size() > 0) {
				for (AitessConfigurationDto uutType : uutDataList) {
					aitessTypeList.add(uutType.getAitessName());
					aitessNameDriverNameMap.put(uutType.getAitessName(), uutType.getDriverName());
					aitessNameAitessId.put(uutType.getAitessName(), uutType.getAitessId());
					aitessIdAitessName.put(uutType.getAitessId(), uutType.getAitessName());
				}
			}

			ObservableList<String> types = FXCollections.observableArrayList(aitessTypeList);

			aitessType.setItems(types);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
