package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.teclever.dfcc.model.FaultCodeList;
import com.teclever.dfcc.model.SessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SessionController implements Initializable {
	private String userType;

	public void setUserType(String userType) {
		System.out.println("Session userType: " + userType);
		this.userType = userType;
	}

	public String getUserType() {
		return userType;
	}
	  @FXML
	    private HBox bottom_button_box;

	    @FXML
	    private Button cancel_button;

	    @FXML
	    private Button create_session_button;

	    @FXML
	    private TextField dfcc_partNo_textField;

	    @FXML
	    private TextField dfcc_sNo_textField;

	    @FXML
	    private Button fault_code_button;

	    @FXML
	    private VBox fault_code_container;

	    @FXML
	    private HBox name_hbox;

	    @FXML
	    private VBox parent_vbox;

	    @FXML
	    private TextArea selected_fault_code_textArea;

	    @FXML
	    private AnchorPane sessionParentContainer;

	    @FXML
	    private Button session_add_button;

	    @FXML
	    private Label session_header;

	    @FXML
	    private VBox session_left_container;

	    @FXML
	    private VBox session_middle_container;

	    @FXML
	    private TextField session_name_field;

	    @FXML
	    private VBox session_right_container;

	    @FXML
	    private TreeView<String> session_stage_treeView;

	    @FXML
	    private VBox session_table_container;

	    @FXML
	    private ComboBox<String> session_type_field;

	    @FXML
	    private TextArea start_remarks_field;

	    @FXML
	    private ComboBox<String> uut_type_field;
	

	private List<String> selectedStages = new ArrayList<>();
	private CustomTableView<SessionDetails> customTableView_session;
	private CustomTableView<FaultCodeList> customTableView_faultCode;

	private String[] uutTypeList = { "DFCC Mk-1", "DFCC Mk-1A", "DFCC Mk-2" };

	private String[] sessionTypeList = { "Production", "FRU", "Trails", "BLS", "PQT" };
	private String[] sessionStageList = { "Initial Electrical Survey", "ESS-Burn-In", "ESS-Thermal cycle",
			"Final Electrical", "Pi-Checks" };
	private String[] productionStageList = { "ProductionSessionStage1", "ProductionSessionStage2",
			"ProductionSessionStage3", "ProductionSessionStage4", "ProductionSessionStage5", "ProductionSessionStage6",
			"ProductionSessionStage7", "ProductionSessionStage8", "ProductionSessionStage9",
			"ProductionSessionStage10" };
	private String[] fruStageList = { "FRUSessionStage1", "FRUSessionStage2", "FRUSessionStage3", "FRUSessionStage4",
			"FRUSessionStage5", "FRUSessionStage6", "FRUSessionStage7", "FRUSessionStage8", "FRUSessionStage9",
			"FRUSessionStage10" };
	private String[] trailStageList = { "trailSessionStage1", "trailSessionStage2", "trailSessionStage3",
			"trailSessionStage4", "trailSessionStage5", "trailSessionStage6", "trailSessionStage7" };
	private String[] blsStageList = { "Bls-SessionStage1", "Bls-SessionStage2", "Bls-SessionStage3",
			"Bls-SessionStage4", "Bls-SessionStage5", "Bls-SessionStage6" };
	private String[] pqtStageList = { "PQT SessionStage1", "PQT SessionStage2", "PQT SessionStage3",
			"PQT SessionStage4", "PQT SessionStage5", "PQT SessionStage6", "PQT SessionStage7" };

	private final ObservableList<SessionDetails> sessionData = FXCollections.observableArrayList(
			new SessionDetails("session78", "2/9/2023"), new SessionDetails("session12", "5/10/2023"),
			new SessionDetails("session598", "11/11/2022"), new SessionDetails("session4", "8/2/2024"),
			new SessionDetails("session5", "30/01/2024"));
	private final ObservableList<FaultCodeList> faultCodeData = FXCollections.observableArrayList(
			new FaultCodeList(000, "No Faults"), new FaultCodeList(110, "Local Channel Checksum Fault OFP"),
			new FaultCodeList(111, "Channel ID Fault"), new FaultCodeList(120, "Loacl vs Left channel version ID"),
			new FaultCodeList(121, "Loacl vs Right channel version ID Fault"),
			new FaultCodeList(122, "Loacl vs Other channel version ID Fault"),
			new FaultCodeList(143, "CPU Discrete I/O Fault"),
			new FaultCodeList(120, "Loacl vs Left channel version ID Fault"),
			new FaultCodeList(121, "Loacl vs Right channel version ID Fault"),
			new FaultCodeList(122, "Loacl vs Other channel version ID Fault"),
			new FaultCodeList(143, "CPU Discrete I/O Fault"));

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		sessionParentContainer.getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/Session.css").toExternalForm());
		 session_name_field.focusedProperty().addListener((obs, oldValue, newValue) -> {
		        if (newValue) { 
		        	enablingSessionTable(true);
		        }
		    });
		uut_type_field.getItems().addAll(uutTypeList);
		session_add_button.setDisable(true);
		create_session_button.setText("Open Session");
		enablingFunction(true);
	}
	public void initializeSession() {
//		if (userType == "SQUADRON USER") {
		if (userType.equals("RL_ID_4")) {
			session_middle_container.setVisible(false);
			session_middle_container.setManaged(false);
		} else {
			session_right_container.setDisable(true);
		}
//		showLoadDriverPopup();
		setupSessionDetailsTableView();
		setupFaultCodeTableView();
		setupSearchableSessionName();
		setupSessionTypeField();
	}

	@FXML
	void onClickFaultCode(ActionEvent event) {
		selected_fault_code_textArea.clear();
		ObservableList<FaultCodeList> selectedFaultCodes = customTableView_faultCode.getSelectedItems();
		if (selectedFaultCodes != null && !selectedFaultCodes.isEmpty()) {
			for (FaultCodeList faultCode : selectedFaultCodes) {
				selected_fault_code_textArea.appendText(faultCode + "\n");
			}
		} else {
			selected_fault_code_textArea.appendText("Fault codes not selected \n");
			System.out.println("no code");
		}
	}

	private void setupSessionStageTreeView() {
		CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>("Session Stages");

		CheckBoxTreeItem<String> initialElectricalSurveyItem = new CheckBoxTreeItem<>("Initial Electrical Survey");
		CheckBoxTreeItem<String> eSSBurnInItem = new CheckBoxTreeItem<>("ESS-Burn-In");
		CheckBoxTreeItem<String> eSSThermalCycleItem = new CheckBoxTreeItem<>("ESS-Thermal cycle");
		CheckBoxTreeItem<String> finalElectricalItem = new CheckBoxTreeItem<>("Final Electrical");
		CheckBoxTreeItem<String> piChecksItem = new CheckBoxTreeItem<>("Pi-Checks");

		initialElectricalSurveyItem.getChildren().addAll(createStageItems(productionStageList));
		eSSBurnInItem.getChildren().addAll(createStageItems(fruStageList));
		eSSThermalCycleItem.getChildren().addAll(createStageItems(trailStageList));
		finalElectricalItem.getChildren().addAll(createStageItems(blsStageList));
		piChecksItem.getChildren().addAll(createStageItems(pqtStageList));

		rootItem.getChildren().addAll(initialElectricalSurveyItem, eSSBurnInItem, eSSThermalCycleItem,
				finalElectricalItem, piChecksItem);

		session_stage_treeView.setRoot(rootItem);
		session_stage_treeView.setShowRoot(false);
		session_stage_treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
	}

	private List<TreeItem<String>> createStageItems(String[] stages) {
		List<TreeItem<String>> items = new ArrayList<>();
		for (String stage : stages) {
			CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(stage);
			items.add(item);
		}
		return items;
	}

	private void setupFaultCodeTableView() {
		TableViewFactory<FaultCodeList> factory1 = new FaultCodeTableViewFactory();
		customTableView_faultCode = factory1.createTableView(faultCodeData, false, true);
		fault_code_container.getChildren().add(customTableView_faultCode);
		customTableView_faultCode.setVisible(true);
		customTableView_faultCode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void setupSessionDetailsTableView() {
		TableViewFactory<SessionDetails> factory = new SessionDetailsTableViewFactory();
		customTableView_session = factory.createTableView(sessionData, false, false);
		session_table_container.getChildren().add(customTableView_session);

		customTableView_session.setVisible(true);
		enablingSessionTable(false);
		customTableView_session.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						session_name_field.setText(newSelection.getSessionName());
						enablingSessionTable(false);
						enablingFunction(false);
					}
				});

	}

	private void setupSearchableSessionName() {
		FilteredList<SessionDetails> filteredList = new FilteredList<>(sessionData, p -> true);
		SortedList<SessionDetails> sortedList = new SortedList<>(filteredList);
		session_name_field.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(sessionDetail -> {
				if (newValue.isEmpty()) {
					session_add_button.setDisable(false);
					create_session_button.setText("Create Session");
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				return sessionDetail.getSessionName().toLowerCase().contains(lowerCaseFilter);
			});
			sortedList.comparatorProperty().bind(customTableView_session.comparatorProperty());
			customTableView_session.setItems(sortedList);
			if (filteredList.isEmpty()) {
				session_add_button.setDisable(false);
				create_session_button.setText("Create Session");
				enablingFunction(true);
			} else {
				enablingSessionTable(true);
				session_add_button.setDisable(true);
				create_session_button.setText("Open Session");
			}
		});
	}

	@FXML
	void onClickCancel(ActionEvent event) throws IOException {
		Stage stage = (Stage) sessionParentContainer.getScene().getWindow();
		stage.close();
		
	}

	@FXML
	void onClickCreate(ActionEvent event) throws IOException {
		
//		UserDashboardController userDashboardController = new UserDashboardController();
//		sessionParentContainer.getParent().getChildren().add(userDashboardController.createUserDashboard());
		
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("/com/teclever/dfcc/ui/fxml/Dashboard.fxml"));
//		Parent root = loader.load();
//		sessionParentContainer.getChildren().add(root);
	}
	
	@FXML
	void onSessionNameAdd(ActionEvent event) {
		enablingSessionTable(false);
		enablingFunction(false);
	}
	
	private void setupSessionTypeField() {
		session_type_field.getItems().addAll(sessionTypeList);
		session_type_field.setOnAction(event -> {
			String selectedSessionType = session_type_field.getValue();
			if (selectedSessionType != null) {
				if (selectedSessionType.equals("FRU")) {
					setupSessionStageTreeView();
					session_right_container.setDisable(false);
				} else {
					setupSessionStageTreeView();
					session_right_container.setDisable(true);
				}
			}

		});
	}
	
	

	public void showLoadDriverPopup() { 
		try {
			FXMLLoader driverStatusLoader = new FXMLLoader(getClass().getResource("/com/teclever/dfcc/ui/fxml/LoadDriver.fxml"));
			Parent loadDriverRoot = driverStatusLoader.load();
//			LoadDriverController loadDriverController = driverStatusLoader.getController();
			Stage popupStage = new Stage();
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(loadDriverRoot);
			popupStage.centerOnScreen();
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enablingFunction(boolean value) {
		uut_type_field.setDisable(value);
		dfcc_sNo_textField.setDisable(value);
		dfcc_partNo_textField.setDisable(value);
		start_remarks_field.setDisable(value);

		session_type_field.setDisable(value);
		session_stage_treeView.setDisable(value);

		selected_fault_code_textArea.setDisable(value);
		selected_fault_code_textArea.clear();
		create_session_button.setDisable(value);
//		if (userType == "SQUADRON USER") {
			if (userType == "RL_ID_4") {
			session_right_container.setDisable(value);
		}
	}
	private void enablingSessionTable(boolean value) {
		session_table_container.setVisible(value);
		session_table_container.setManaged(value);
		if(value) {
			start_remarks_field.setPrefHeight(30.0);
		}else {
			start_remarks_field.setPrefHeight(300.0);
		}
	}
}

class SessionDetailsTableViewFactory implements TableViewFactory<SessionDetails> {
	@Override
	public CustomTableView<SessionDetails> createTableView(ObservableList<SessionDetails> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, SessionDetails.class, addUserColumn, addCheckboxColumn);
	}
}

class FaultCodeTableViewFactory implements TableViewFactory<FaultCodeList> {
	@Override
	public CustomTableView<FaultCodeList> createTableView(ObservableList<FaultCodeList> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, FaultCodeList.class, addUserColumn, addCheckboxColumn);
	}
}
