package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.RunAitessConfiguration;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class RunAitessTableViewFactory implements TableViewFactory<RunAitessConfiguration> {

	@Override
	public CustomTableView<RunAitessConfiguration> createTableView(ObservableList<RunAitessConfiguration> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, RunAitessConfiguration.class, addUserColumn, addCheckboxColumn);

	}

}

public class RunConfigurationController {

	public static String runuutTypeValue;
	public static String runuutTypeId;
	public static String testTypeValue;
	public static String aitessTypeValue;
	public static String fileConfigName;
	public static String driverName;
	Map<String, String> aitessNameDriverNameMap = new HashMap<String, String>();
	Map<Integer, String> aitessIDName = new HashMap<Integer, String>();
	Map<String, String> testTypeNameId = new HashMap<String, String>();

	private GridPane runconfigurationGridPane = new GridPane();
	private HBox titleHbox = new HBox(10);
	private HBox buttonHbox = new HBox(10);
	private GridPane titleGridPane = new GridPane();
	private GridPane midGridPane = new GridPane();
	private HBox midHBoxUUTType = new HBox(10);

	public ComboBox<String> uutTypeField;
	Label testTypeField;
	Label aitessType = new Label("AITESS TYPE");
	Label driverLabel = new Label("Driver");
	Label configFile = new Label("SELECT CONFIG FILE");

	private HBox bottomHbox = new HBox(30);

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runConfigurationManager = new RunConfigurationManagement();

	public RunConfigurationController() {
		uutTypeField = new ComboBox<>();
		loadUUTTypes();
		setupDriverLabel();
	}

	public void refresh() {
		runuutTypeAction();
	}

	public GridPane runconfigurationGridPane() {

		runconfigurationGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/RunConfiguration.css").toExternalForm());
		
		runconfigurationGridPane.getStyleClass().add("runConfiguration-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

		runconfigurationGridPane.setPadding(new Insets(10));
		runconfigurationGridPane.setVgap(5);

		runconfigurationGridPane.getColumnConstraints().addAll(firstColumn);
		runconfigurationGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		runconfigurationGridPane.add(createtitleGridPane(), 0, 0);
		runconfigurationGridPane.add(runConfigurationMidContainer(), 0, 1);
		runconfigurationGridPane.add(runConfigurationBottomContainer(), 0, 2);

		return runconfigurationGridPane;

	}

	public GridPane createtitleGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		titleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		titleGridPane.getRowConstraints().addAll(firstRow);

		titleGridPane.add(titleHbox(), 0, 0);
		titleGridPane.add(createButtonHbox(), 1, 0);

		return titleGridPane;

	}

	private HBox titleHbox() {

		Label pageTitle = new Label("RUN CONFIGURATION");
		pageTitle.getStyleClass().add("runConfiguration-headerLabel");
		titleHbox.setAlignment(Pos.CENTER_LEFT);
		titleHbox.getChildren().add(pageTitle);
		return titleHbox;
	}

	private HBox createButtonHbox() {

		Button addButton = new Button("ADD RUN CONFIG");
		addButton.setOnAction(e -> onClickGETButton());

		buttonHbox.setAlignment(Pos.CENTER_RIGHT);
		buttonHbox.getChildren().add(addButton);
		return buttonHbox;
	}

	private GridPane runConfigurationMidContainer() {
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

		uutTypeField.setOnAction((event) -> runuutTypeAction());

		midGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn);
		midGridPane.getRowConstraints().addAll(firstRow);

		midGridPane.add(createUUTypeComboBox(), 0, 0);

		midGridPane.getStyleClass().add("runConfiguration-Container");
		return midGridPane;
	}

	private void loadUUTTypes() {
		try {
			ArrayList<String> uutTypeList = new ArrayList<String>();
			UUTMasterDetailsDto[] uutDataList;
			UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList = this.configManager.getAllUUT();
			int n = uutDataList.length;
			int n2 = 0;
			while (n2 < n) {
				UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
				uutTypeList.add(uutType.getUutType());
				++n2;
			}
			ObservableList types = FXCollections.observableArrayList(uutTypeList);
			uutTypeField.setItems(types);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HBox createUUTypeComboBox() {

		uutTypeField.setPromptText("UUT TYPE");
		midHBoxUUTType.setPadding(new Insets(0, 0, 0, 18.5));
		midHBoxUUTType.setAlignment(Pos.CENTER);
		midHBoxUUTType.getChildren().add(uutTypeField);

		return midHBoxUUTType;
	}


	private void setupDriverLabel() {
		aitessTypeValue = (String) this.uutTypeField.getValue();
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


	private void onClickGETButton() {
		 String selectedUUTType = uutTypeField.getValue();
		    
		    if (selectedUUTType == null || selectedUUTType.isEmpty()) {
		        Alert alert = new Alert(AlertType.WARNING);
		        alert.setTitle("Warning");
		        alert.setHeaderText(null);
		        alert.setContentText("Please select a UUT Type.");

		        alert.showAndWait();
		    } else {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AddRun.fxml"));
			Parent root = loader.load();

			Stage popupStage = new Stage();
			AddRunConfigurationController controller = loader.getController();
			controller.setMainPageController(this);
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(root);
			
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
			double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 400) / 2;
			popupStage.setX(centerX);
			popupStage.setY(centerY);
			
			popupStage.setScene(scene);
			popupStage.showAndWait();

			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
	private HBox runConfigurationBottomContainer() {
		bottomHbox.getStyleClass().add("runConfiguration-Container");

		return bottomHbox;

	}

	private void setupDisplayTable(String uutId) {
	    Map<String, String> uutIdNameMap = DFCCConstant.getUutIdNameMap();
	    List<RunConfigurationDto> runConfigList = runConfigurationManager.getRunConfig(uutId);
	    
	    // Update the observable list
	    ObservableList<RunAitessConfiguration> driverData = FXCollections.observableArrayList();
	    for (RunConfigurationDto runConfig : runConfigList) {
	        RunAitessConfiguration runAitessData = new RunAitessConfiguration();
	        runAitessData.setUuttype(uutIdNameMap.get(runConfig.getUutId()));
	        runAitessData.setTestType(testTypeNameId.get(runConfig.getTestTypeId()));
	        runAitessData.setAitess(runConfig.getAitess());
	        runAitessData.setDriver(runConfig.getDriver());
	        runAitessData.setAitess1ConfigFile(runConfig.getConfigFile());
	        runAitessData.setAitess2ConfigFile(runConfig.getAitess2ConfigFile());
	        runAitessData.setId(runConfig.getRunConfigId());
	        driverData.add(runAitessData);
	    }

	    Platform.runLater(() -> {
	        RunAitessTableViewFactory driverFactory = new RunAitessTableViewFactory();
	        CustomTableView<RunAitessConfiguration> customTableView = driverFactory.createTableView(driverData, true, false);

	        customTableView.setPrefWidth(1613.0);
	        customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
	            ObservableList<RunAitessConfiguration> selectedItems = customTableView.getSelectedItems();
	            for (RunAitessConfiguration runAitess : selectedItems) {
	                handleDeleteButtonClicked(runAitess);
	            }
	        });

	        bottomHbox.getChildren().clear();
	        bottomHbox.getChildren().add(customTableView);
	    });
	}

	void runuutTypeAction() {
	    runuutTypeValue = uutTypeField.getValue();
	    Map<String, String> nameIdMap = DFCCConstant.getUutNameIdMap();
	    runuutTypeId = nameIdMap.get(runuutTypeValue);
	    AddRunConfigurationController runConfigController = new AddRunConfigurationController();
	    AddRunConfigurationController.runuutTypeId = runuutTypeId;
	    AddRunConfigurationController.runuutTypeValue = runuutTypeValue;

	    // Clear previous test type name IDs
	    testTypeNameId.clear();
	    
	    // Fetch test types based on the selected UUT ID
	    RunConfigurationManagement runConfigManager = new RunConfigurationManagement();
	    TestTypeMasterDetailsDto[] testTypeArray = runConfigManager.getTestTypeByUUTId(runuutTypeId);
	    for (TestTypeMasterDetailsDto testType : testTypeArray) {
	        testTypeNameId.put(testType.getTestTypeId(), testType.getTestName());
	    }

	    // Load AITESS and Test Types
	    runConfigController.loadAitessTypes(runuutTypeId);
	    runConfigController.loadTestTypes(runuutTypeId);

	    // Update the table with the new data
	    setupDisplayTable(runuutTypeId);
	}

	private void handleDeleteButtonClicked(RunAitessConfiguration runConfigDto) {	
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to delete Aitess Run Configuration: " + runConfigDto.getAitess() + "?";
		Notifications.showConfirmationDialog(title, contentText, () -> deleteRunConfig(runConfigDto.getId()));
	}

	private void deleteRunConfig(String runConfigId) {
		Response response = runConfigurationManager.deleteRunConfigById(runConfigId);
		if(response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
		runuutTypeAction();

	}

}