package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.swing.table.DefaultTableModel;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.RunAitessConfiguration;
import com.teclever.dfcc.utils.CustomTableView;
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
import javafx.scene.control.ButtonType;
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
	private HBox midHBoxTestType = new HBox(10);
	private HBox midHBoxAitessType = new HBox(10);
	private HBox midHBoxDriverName = new HBox(10);
	private HBox midHBoxConfigFile = new HBox(10);

	public ComboBox<String> uutTypeField;
//	public ComboBox<String> testTypeField;
	Label testTypeField;
	Label aitessType = new Label("AITESS TYPE");
	Label driverLabel = new Label("Driver");
	Label configFile = new Label("SELECT CONFIG FILE");

	private DefaultTableModel tableModel;
	private static final int ID_COLUMN_INDEX = 0;

	private HBox bottomHbox = new HBox(30);

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private RunConfigurationManagement runconfigManager = new RunConfigurationManagement();

	public RunConfigurationController() {
		uutTypeField = new ComboBox<>();
//		testTypeField = new ComboBox<>();
		loadUUTTypes();
		setupDisplayTable(runuutTypeId);
		setupDriverLabel();

////		uutTypeField.setDisable(false);
//        testTypeField.setVisible(false);
//        aitessType.setVisible(false);	
//        driverLabel.setVisible(false);
//        configFile.setVisible(false);
	}

	public void refresh() {
		runuutTypeAction();

	}
//	void DataSet() {
//		runConfigurationController.aitessType.setVisible(true);
//		runConfigurationController.configFile.setVisible(true);
//		runConfigurationController.driverLabel.setVisible(true);
//		runConfigurationController.testTypeField.setVisible(true);
//		}

	public GridPane runconfigurationGridPane() {

		runconfigurationGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/RunConfiguration.css").toExternalForm());
		
		runconfigurationGridPane.getStyleClass().add("runConfiguration-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

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
		pageTitle.setPadding(new Insets(0, 0, 0, 20));
		titleHbox.setAlignment(Pos.CENTER_LEFT);
		titleHbox.getChildren().add(pageTitle);
		return titleHbox;
	}

	private HBox createButtonHbox() {

		Button addButton = new Button("ADD AITESS");
		addButton.setOnAction(e -> onClickGETButton());
		buttonHbox.setPadding(new Insets(0, 20, 0, 0));

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
		midGridPane.add(createTestTypeComboBox(), 1, 0);
		midGridPane.add(createAitessType(), 2, 0);
		midGridPane.add(createDriverNameLabel(), 3, 0);
		midGridPane.add(createConfigLabel(), 4, 0);

		midGridPane.getStyleClass().add("runConfiguration-Container");
//	midGridPane.getChildren().addAll(midHBoxUUTType);
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

//	    uut_type_field.setPadding(new Insets( 0,0,0,20));
		midHBoxUUTType.setAlignment(Pos.CENTER);
		midHBoxUUTType.getChildren().add(uutTypeField);

		return midHBoxUUTType;
	}

	private HBox createTestTypeComboBox() {
		testTypeField = new Label("TEST TYPE");
		testTypeField.setPadding(new Insets(10));
		testTypeField.setPrefWidth(200);
		testTypeField.setAlignment(Pos.CENTER);
		testTypeField.setPadding(new Insets(0, 0, 0, 0));
		midHBoxTestType.setAlignment(Pos.CENTER);
		midHBoxTestType.setPrefWidth(200);
		testTypeField.getStyleClass().add("runConfiguration-AiteesType");
		midHBoxTestType.setAlignment(Pos.CENTER);
		midHBoxTestType.getChildren().add(testTypeField);

		return midHBoxTestType;
	}

	private HBox createAitessType() {

		aitessType.setPadding(new Insets(10));
		aitessType.setPrefWidth(200);
		aitessType.setAlignment(Pos.CENTER);
		aitessType.setPadding(new Insets(0, 0, 0, 0));
		midHBoxAitessType.setAlignment(Pos.CENTER);
		midHBoxAitessType.getChildren().add(aitessType);
		midHBoxAitessType.setPrefWidth(200);
		aitessType.getStyleClass().add("runConfiguration-AiteesType");

		return midHBoxAitessType;
	}

	private HBox createDriverNameLabel() {

//		Label driverName = new Label("DRIVER NAME");
		driverLabel.setPadding(new Insets(10));
		driverLabel.setPrefWidth(200);

		driverLabel.setAlignment(Pos.CENTER);
		driverLabel.setPadding(new Insets(0, 0, 0, 0));
		midHBoxDriverName.setAlignment(Pos.CENTER);
		midHBoxDriverName.getChildren().add(driverLabel);
		midHBoxDriverName.setPrefWidth(200);
		driverLabel.getStyleClass().add("runConfiguration-DriveName");

		return midHBoxDriverName;
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

	private HBox createConfigLabel() {

//		Label configFile = new Label("SELECT CONFIG FILE");
//	    configFile.setPadding(new Insets(10));
		configFile.setPrefWidth(600);
		configFile.setAlignment(Pos.CENTER);
		midHBoxConfigFile.setPadding(new Insets(0, 20, 0, 0));
		midHBoxConfigFile.setAlignment(Pos.CENTER);
		midHBoxConfigFile.getChildren().add(configFile);
		configFile.getStyleClass().add("runConfiguration-ConfigFile");
		midHBoxConfigFile.setPrefWidth(650);
		// Set the HBox to span multiple columns
		GridPane.setColumnSpan(midHBoxConfigFile, 10);

		return midHBoxConfigFile;
	}

	private void onClickGETButton() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/teclever/dfcc/ui/fxml/AddRun.fxml"));
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

	private HBox runConfigurationBottomContainer() {
		bottomHbox.getStyleClass().add("runConfiguration-Container");

		return bottomHbox;

	}

	private void setupDisplayTable(String uutId) {

		System.out.println("Entered Diaplay");
		Map uutIdNameMap = DFCCConstant.getUutIdNameMap();
		RunConfigurationManagement runConfiguration = new RunConfigurationManagement();
		List<RunConfigurationDto> lst = runConfiguration.getRunConfig(uutId);
		System.out.println("lst Size" + lst.size() + "     uutId" + uutId);
		ObservableList<RunAitessConfiguration> driverData = FXCollections.observableArrayList();
		for (RunConfigurationDto RunAitessConfiguration2 : lst) {

			// public RunAitessConfiguration(String uuttype, String testType, String
			// configFile, String aitess, String driver) {
			RunAitessConfiguration runAitessData = new RunAitessConfiguration();
			runAitessData.setUuttype((String) uutIdNameMap.get(RunAitessConfiguration2.getUutId()));
			runAitessData.setTestType(testTypeNameId.get(RunAitessConfiguration2.getTestTypeId()));
			runAitessData.setAitess(RunAitessConfiguration2.getAitess());
			runAitessData.setDriver(RunAitessConfiguration2.getDriver());
			runAitessData.setConfigFile(RunAitessConfiguration2.getConfigFile());
			runAitessData.setRunConfigId(RunAitessConfiguration2.getRunConfigId());
			driverData.add(runAitessData);

		}
		RunAitessTableViewFactory driverFactory = new RunAitessTableViewFactory();
		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);

		customTableView.hideColumn("RUN CONFIG ID");

//		customTableView.setPrefWidth(1321.0);
		customTableView.setPrefWidth(1613.0);
		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<RunAitessConfiguration> selectedItems = customTableView.getSelectedItems();
			for (RunAitessConfiguration runAitess : selectedItems) {
				handleDeleteButtonClicked(runAitess);
			}
		});

		this.bottomHbox.getChildren().clear();
		this.bottomHbox.getChildren().add(customTableView);
	}

	void runuutTypeAction() {
		System.out.println("Entered Into runuutTypeAction ");
		runuutTypeValue = (String) uutTypeField.getValue();
		System.out.println("runuutTypeValue" + runuutTypeValue);
		Map nameIdMap = DFCCConstant.getUutNameIdMap();
		runuutTypeId = (String) nameIdMap.get(runuutTypeValue);
		AddRunConfigurationController addRunConfigurationController = new AddRunConfigurationController();
		AddRunConfigurationController.runuutTypeId = runuutTypeId;
		AddRunConfigurationController.runuutTypeValue = runuutTypeValue;
		setupDisplayTable(runuutTypeId);
		System.out.println("id" + runuutTypeId);
		RunConfigurationManagement rcm = new RunConfigurationManagement();
		TestTypeMasterDetailsDto[] array = rcm.getTestTypeByUUTId(runuutTypeId);
		TestTypeMasterDetailsDto[] testTypeMasterDetailsDtoArray = array;
		int n = array.length;
		int n2 = 0;
		while (n2 < n) {
			TestTypeMasterDetailsDto t = testTypeMasterDetailsDtoArray[n2];
			testTypeNameId.put(t.getTestTypeId(), t.getTestName());
			++n2;
		}
		addRunConfigurationController.loadAitessTypes(runuutTypeId);
		addRunConfigurationController.loadTestTypes(runuutTypeId);
//		System.out.println("DropDown     " + (String) uutTypeField.getValue());

	}

	private void handleDeleteButtonClicked(RunAitessConfiguration runConfigDto) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText(
				"Are you sure you want to delete Aitess Run Configuration: " + runConfigDto.getAitess() + "?");

		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		alert.showAndWait().ifPresent(buttonType -> {
			if (buttonType == buttonTypeYes) {
				deleteRunConfig(runConfigDto.getRunConfigId());
			}
		});
	}

	private void deleteRunConfig(String runConfigId) {
		RunConfigurationManagement runConfManagement = new RunConfigurationManagement();
		runConfManagement.deleteRunConfig(runConfigId);
		runuutTypeAction();

	}

	public void alertBox(String text) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("INFORMATION");
		alert.setContentText(text);
		alert.showAndWait();
	}

	//

}