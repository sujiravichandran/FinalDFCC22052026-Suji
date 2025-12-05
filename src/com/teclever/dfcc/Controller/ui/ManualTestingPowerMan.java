package com.teclever.dfcc.Controller.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.management.Notification;

import org.hibernate.internal.build.AllowSysOut;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.entities.PowerManDataAnalysis;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.PowerManMk1;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
//JavaFX / image
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class ManualTesting1PowerManDataTableViewFactory implements TableViewFactory<PowerManMk1> {
	@Override
	public CustomTableView<PowerManMk1> createTableView(ObservableList<PowerManMk1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, PowerManMk1.class, addUserColumn, addCheckboxColumn);
	}
}

class ManualTesting2PowerManDataTableViewFactory implements TableViewFactory<PowerManMk1> {
	@Override
	public CustomTableView<PowerManMk1> createTableView(ObservableList<PowerManMk1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, PowerManMk1.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTestingPowerMan {

	private GridPane powerManMk1MainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private HBox headingHbox3 = new HBox(10);
	private Label titleLabel = new Label();
	private Label chLabel = new Label();
	private Label chLabel2 = new Label();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();

	private HBox serialHbox = new HBox(10);
	private Label dfccSNoLabel = new Label("DFCC Serial Number : ");
	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ComboBox<String> sessioName = new ComboBox<String>();
	private Label dfccSessionName = new Label("Session Name : ");

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private TextField dfccSNoText = new TextField();
	private Label editableLabel = new Label("Click to edit");
	private TextField editField = new TextField(editableLabel.getText());
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private Label stageNameLabel = new Label("Stage Name");
	private ComboBox<String> stageName = new ComboBox<String>();

	private StackPane powerManMk1Table1StackPane = new StackPane();
	private GridPane powerManMk1Table1GridPane = new GridPane();
	private StackPane powerManMk12Table1StackPane = new StackPane();
	private GridPane powerManMk12Table1GridPane = new GridPane();

	private StackPane powerManMk1Table2StackPane = new StackPane();
	private GridPane powerManMk1Table2GridPane = new GridPane();
	private GridPane powerManMk1Table22GridPane = new GridPane();

	private StackPane powerManMk1Table22StackPane = new StackPane();

	private ObservableList<PowerManMk1> powerManMk1Table1DataList = FXCollections.observableArrayList();
	private CustomTableView<PowerManMk1> powerManMk1Table1DataTableView;
	private TableViewFactory<PowerManMk1> powerManMk1Table1DataFactory = new ManualTesting1PowerManDataTableViewFactory();
	
	private ObservableList<PowerManMk1> powerManMk12Table1DataList = FXCollections.observableArrayList();
	private CustomTableView<PowerManMk1> powerManMk12Table1DataTableView;
	private TableViewFactory<PowerManMk1> powerManMk12Table1DataFactory = new ManualTesting1PowerManDataTableViewFactory();

	private ObservableList<PowerManMk1> powerManMk1Table2DataList = FXCollections.observableArrayList();
	private CustomTableView<PowerManMk1> powerManMk1Table2DataTableView;
	private TableViewFactory<PowerManMk1> powerManMk1Table2DataFactory = new ManualTesting1PowerManDataTableViewFactory();
	
	private ObservableList<PowerManMk1> powerManMk1Table22DataList = FXCollections.observableArrayList();
	private CustomTableView<PowerManMk1> powerManMk1Table22DataTableView;
	private TableViewFactory<PowerManMk1> powerManMk1Table22DataFactory = new ManualTesting1PowerManDataTableViewFactory();
	private String uutId = "UUT1";
	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label();

	private Label tmpUutLabel;
	private Label tmpSerialLabel;
	private Label tmpSessionLabel;
	private Label tmpStageLabel;

	private HBox tbNoHBox = new HBox();
	private Label tbNol = new Label("TB NO.:022 ");
	private Label passFail = new Label();
	private Label testRep = new Label("TESTING REP: ");
	private Label testRepDate = new Label("DATE: ");
	private Label tiqmRep = new Label("TI/QM REP: ");
	private Label tiqmRepDate = new Label("DATE: ");
	private SessionService s = new SessionService();
	private HBox testCarriedOutHBox = new HBox();
	private Label testCarriedOutBy = new Label("TEST CARRIED OUT BY:");

	private HBox dateHBox = new HBox();
	private Label date = new Label("DATE: ");

	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");
	private Button saveButton = new Button("Save");
	private Button fetchButton = new Button("Fetch");

	private String selectedSessionId;
	private String selectedStageName;
	private String selectedStageId;

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String t1Ch1;
	private String t1Ch2;
	private String t1Ch3;
	private String t1Ch4;

	private String t2Ch1;
	private String t2Ch2;
	private String t2Ch3;
	private String t2Ch4;
	
	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();

	public ManualTestingPowerMan() {

		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		for (SessionDto session : sessionList) {
			sessionNameId.put(session.getSessionName(), session.getSessionId());

		}

		initializeUUTTypeComboBox();

	}

	public GridPane createlinkFilesMainContainerGridPane() {
		printButton.setDisable(true);
		powerManMk1MainContainerGridPane.getChildren().clear();
		powerManMk1MainContainerGridPane.getColumnConstraints().clear();
		powerManMk1MainContainerGridPane.getRowConstraints().clear();

		powerManMk1MainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		powerManMk1MainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(5);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(17);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(17);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(10);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(10);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(10);

		RowConstraints eigthRow = new RowConstraints();
		eigthRow.setPercentHeight(10);

		powerManMk1MainContainerGridPane.setHgap(10);
		powerManMk1MainContainerGridPane.setVgap(10);

		powerManMk1MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		powerManMk1MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,
				sixthRow, seventhRow, eigthRow);
		powerManMk1MainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		powerManMk1MainContainerGridPane.add(createHeadingBox(), 0, 0);
		powerManMk1MainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		if ("UUT1".equalsIgnoreCase(uutId)) {
			powerManMk1MainContainerGridPane.add(createPowerManMk1Table1Content(), 0, 2);
			powerManMk1MainContainerGridPane.add(createPowerManMk12Table1Content(), 0, 3);
		} else {
			powerManMk1MainContainerGridPane.add(createPowerManMk1Table2Content(), 0, 2);
			powerManMk1MainContainerGridPane.add(createPowerManMk1Table22Content(), 0, 3);
		}

		powerManMk1MainContainerGridPane.add(createNoteBox(), 0, 4);
		powerManMk1MainContainerGridPane.add(createRepBox(), 0, 5);
		powerManMk1MainContainerGridPane.add(createRepDateBox(), 0, 6);
		powerManMk1MainContainerGridPane.add(createPrintButtonHbox(), 0, 7);

		return powerManMk1MainContainerGridPane;
	}

	private GridPane createHeadingBox() {

		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(33);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(33);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);

		topBoxGridPane.getColumnConstraints().addAll(firstColumn);
		topBoxGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		// 🔥 CREATE NEW INSTANCES every time (NO reuse)
		HBox headingHbox1 = new HBox();
		HBox headingHbox2 = new HBox();
		HBox headingHbox3 = new HBox();

		Label title = new Label(titleLabel.getText());
		Label ch = new Label(chLabel.getText());
		Label ch2 = new Label(chLabel2.getText());

		headingHbox1.setAlignment(Pos.CENTER);
		title.setUnderline(true);
		title.getStyleClass().add("advanced-testing-title");

		headingHbox2.setAlignment(Pos.CENTER);
		ch.getStyleClass().add("advanced-testing-title");

		headingHbox3.setAlignment(Pos.CENTER);
		ch2.getStyleClass().add("advanced-testing-title");
		noteLabel.setStyle("advanced-testing-title");

		headingHbox1.getChildren().add(title);
		headingHbox2.getChildren().add(ch);
		headingHbox3.getChildren().add(ch2);

		topBoxGridPane.add(headingHbox1, 0, 0);
		topBoxGridPane.add(headingHbox2, 0, 1);
		topBoxGridPane.add(headingHbox3, 0, 2);

		return topBoxGridPane;
	}

	private GridPane createNoteBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(60);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(20);

		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(20);

		grid.getColumnConstraints().addAll(col1, col2, col3);

		grid.add(noteLabel, 0, 0);
		grid.add(tbNol, 1, 0);
		grid.add(passFail, 2, 0);
		noteLabel.getStyleClass().add("nonheading-label");
		tbNol.getStyleClass().add("nonheading-label");
		passFail.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(tbNol, HPos.RIGHT);
		GridPane.setHalignment(passFail, HPos.LEFT);

		return grid;
	}

	private GridPane createRepBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		grid.getColumnConstraints().addAll(col1, col2);

		grid.add(testRep, 0, 0);
		grid.add(tiqmRep, 1, 0);
		testRep.getStyleClass().add("nonheading-label");
		tiqmRep.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(testRep, HPos.CENTER);
		GridPane.setHalignment(tiqmRep, HPos.CENTER);

		return grid;
	}

	private GridPane createRepDateBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		grid.getColumnConstraints().addAll(col1, col2);

		grid.add(testRepDate, 0, 0);
		grid.add(tiqmRepDate, 1, 0);

		testRepDate.getStyleClass().add("nonheading-label");
		tiqmRepDate.getStyleClass().add("nonheading-label");

		GridPane.setHalignment(testRepDate, HPos.CENTER);
		GridPane.setHalignment(tiqmRepDate, HPos.CENTER);

		return grid;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow);

		dfccSNoLabel.getStyleClass().add("nonheading-label");

		HBox uutHbox = new HBox(10);
		uutHbox.setAlignment(Pos.CENTER_LEFT);
		uutHbox.setFillHeight(true);
		uutTypeField.setPromptText("select UUT Type");
		uutHbox.getChildren().add(uutTypeField);

		HBox serialHbox = new HBox(10);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);

		dfccSessionName.getStyleClass().add("nonheading-label");
		sessionHbox.getChildren().addAll(sessioName);
		sessioName.setPromptText("select Session");
		serialHbox.getChildren().addAll(serialnumber);
		serialnumber.setPromptText("select Serial No");
		stageName.setPromptText("select Stage Name");

		HBox stageNameHbox = new HBox(10);
		stageNameHbox.setAlignment(Pos.CENTER_RIGHT);
		stageNameLabel.getStyleClass().add("nonheading-label");
		stageNameHbox.getChildren().addAll(stageName);

		serialAlternateGridPane.add(uutHbox, 0, 0);
		serialAlternateGridPane.add(serialHbox, 1, 0);
		serialAlternateGridPane.add(sessionHbox, 2, 0);
		serialAlternateGridPane.add(stageNameHbox, 3, 0);

		return serialAlternateGridPane;
	}

	private void getSerialNo() {
		List serailNumber = FXCollections.observableArrayList(unitGetDetailsManagement.getAllDfccSerialNo());
		serialnumber.setItems((ObservableList<String>) serailNumber);

	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
			
			printButton.setDisable(true);

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initalizeSerialNoComboBox(uutId);

			if (uutId.equalsIgnoreCase("UUT1")) {
				titleLabel.setText("LCA DFCC MK-1 POWER CHECK OBSERVATIONS");
				chLabel.setText("SPIN MOTOR EXCITATION FREQUENCIES");
				chLabel2.setText("(AS OBSERVED AT BREAK-OUT-BOX IN AETS MK-1)");
				noteLabel.setText("IMPORTANT: These Test Result are 'FOR RECORD PURPOSE ONLY.'");

			} else if (uutId.equalsIgnoreCase("UUT2")) {
				titleLabel.setText("LCA DFCC MK-1A POWER CHECK OBSERVATIONS");
				chLabel.setText("SPIN MOTOR EXCITATION FREQUENCIES");
				chLabel2.setText("(AS OBSERVED AT BREAK-OUT-BOX IN AETS MK-1A)");
				noteLabel.setText("TEST EQUIPMENT USED: Oscilloscope or Multimeter");
			} else {
				titleLabel.setText("LCA DFCC MK-2 POWER CHECK OBSERVATIONS");
				chLabel.setText("SPIN MOTOR EXCITATION FREQUENCIES");
				chLabel2.setText("(AS OBSERVED AT BREAK-OUT-BOX IN AETS MK-2)");
				noteLabel.setText("TEST EQUIPMENT USED: Oscilloscope or Multimeter");
			}
			createlinkFilesMainContainerGridPane();
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

	private void initalizeSerialNoComboBox(String uutId) {
		dfccSNList.clear();
		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutId))
				.collect(Collectors.toList());

		Set<String> seenDfccSNos = new HashSet<>();

		for (SessionDto dfccSn : filterSessionList) {
			String dfccSNo = dfccSn.getDfccSNo();
			if (seenDfccSNos.add(dfccSNo)) {
				dfccSNList.add(dfccSNo);
			}
		}
		serialnumber.setItems(dfccSNList);
		serialnumber.setOnAction((event) -> {

			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
			initializeSessionComboBox(selectedSerialNumber);

		});
	}

	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();

		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo))
				.collect(Collectors.toList());

		for (SessionDto sessionName : filterSessionList) {
			sessionTypeList.add(sessionName.getSessionName());

		}

		sessioName.setItems(sessionTypeList);
		sessioName.setOnAction(event -> {
			String selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
			if (selectedSessionName != null) {
				SessionDto selectedSession = filterSessionList.stream()
						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);

				if (selectedSession != null) {
					selectedSessionId = selectedSession.getSessionId();
				}
				getStageName(selectedSessionId);
			}
		});

	}

	private void getStageName(String sessionId) {
		stageList = unitGetDetailsManagement.getStageDetailsForSession(selectedSessionId);
		System.out.println("stageList" + stageList);
		for (UnitSessionDetailsDTO stageName : stageList) {
			stageNameId.put(stageName.getStageId(), stageName.getStageName());
		}

		stageName.setItems(FXCollections.observableArrayList(stageNameId.values()));

		stageName.setOnAction((event) -> {
			selectedStageName = stageName.getSelectionModel().getSelectedItem();

			selectedStageId = stageNameId.entrySet()
			    .stream()
			    .filter(e -> selectedStageName.equals(e.getValue()))
			    .map(Map.Entry::getKey)
			    .findFirst()
			    .orElse(null);
		});
	}

	private StackPane createPowerManMk1Table1Content() {

		powerManMk1Table1StackPane.getChildren().clear();
		powerManMk1Table1StackPane.getStyleClass().add("tab-content-container");
		powerManMk1Table1StackPane.getChildren().clear();
		powerManMk1Table1StackPane.getChildren().add(powerManMk1TableResultGridPane());
		return powerManMk1Table1StackPane;
	}

	private StackPane createPowerManMk12Table1Content() {
		powerManMk12Table1StackPane.getChildren().clear();
		powerManMk12Table1StackPane.getStyleClass().add("tab-content-container");
		powerManMk12Table1StackPane.getChildren().clear();
		powerManMk12Table1StackPane.getChildren().add(powerManMk12TableResultGridPane());
		return powerManMk12Table1StackPane;
	}

	private GridPane powerManMk1TableResultGridPane() {
		powerManMk1Table1GridPane.getChildren().clear();
		powerManMk1Table1GridPane.getColumnConstraints().clear();
		powerManMk1Table1GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		powerManMk1Table1GridPane.getColumnConstraints().add(firstColumn);
		powerManMk1Table1GridPane.getRowConstraints().add(firstRow);

		powerManMk1Table1GridPane.add(createPowerManMk1Table1DataTable(), 0, 0);

		return powerManMk1Table1GridPane;
	}

	private GridPane powerManMk12TableResultGridPane() {
		powerManMk12Table1GridPane.getChildren().clear();
		powerManMk12Table1GridPane.getColumnConstraints().clear();
		powerManMk12Table1GridPane.getRowConstraints().clear();
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		powerManMk12Table1GridPane.getColumnConstraints().add(firstColumn);
		powerManMk12Table1GridPane.getRowConstraints().add(firstRow);

		powerManMk12Table1GridPane.add(createPowermanMk12Table1DataTable(), 0, 0);

		return powerManMk12Table1GridPane;
	}

	private ScrollPane createPowerManMk1Table1DataTable() {

		powerManMk1Table1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(powerManMk1Table1DataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// --- ONLY ONE DEFAULT ROW ---
				PowerManMk1 dto = new PowerManMk1();

				dto.setInputVoltageSetting("28");
				dto.setDescription("HIGH TO RETURN");
				dto.setExpectedValue("385.4 - 412.6");
//
//				dto.setChannel1("");
//				dto.setChannel2("");
//				dto.setChannel3("");
//				dto.setChannel4("");

				powerManMk1Table1DataList.add(dto);

				// Create table after data is ready
				powerManMk1Table1DataTableView = powerManMk1Table1DataFactory.createTableView(powerManMk1Table1DataList,
						false, false);

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					powerManMk1Table1DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						case "INPUT VOLTAGE SETTING":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(130);
							Label mainHeader = new Label("INPUT VOLTAGE SETTING\n(VOLTS DC)");
							mainHeader.setWrapText(true);
							mainHeader.setTextAlignment(TextAlignment.CENTER);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinWidth(130);
							mainHeader.setMaxWidth(130);
							VBox headerBox = new VBox(mainHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("inputvoltagesetting"));
							break;

						case "DESCRIPTION":
							column.setText(null);
							column.setMinWidth(300);
							column.setMaxWidth(300);
							Label mainHeader1 = new Label("DESCRIPTION\n(Monitored Parameter)");
							mainHeader1.setWrapText(true);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinWidth(300);
							mainHeader1.setMaxWidth(300);
							VBox headerBox1 = new VBox(mainHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("description"));
							break;

						case "EXPECTED VALUE":
							column.setText(null);
							column.setMinWidth(350);
							column.setMaxWidth(350);
							Label mainHeader2 = new Label("EXPECTED VALUE\n(HZ)");
							mainHeader2.setWrapText(true);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinWidth(350);
							mainHeader2.setMaxWidth(350);
							VBox headerBox2 = new VBox(mainHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("expectedvalue"));
							break;

						case "CHANNEL1":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader3 = new Label("CHANNEL 1\n(BOB2 - 104: P17 -80/79)");
							mainHeader3.setWrapText(true);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinWidth(185);
							mainHeader3.setMaxWidth(185);
							VBox headerBox3 = new VBox(mainHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
							break;

						case "CHANNEL2":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader4 = new Label("CHANNEL 2\n(BOB2 - 104: P17 -81/82)");
							mainHeader4.setWrapText(true);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinWidth(185);
							mainHeader4.setMaxWidth(185);
							VBox headerBox4 = new VBox(mainHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel2"));
							break;

						case "CHANNEL3":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader5 = new Label("CHANNEL 3\n(BOB2 - 104: P17 -86/87)");
							mainHeader5.setWrapText(true);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinWidth(185);
							mainHeader5.setMaxWidth(185);
							VBox headerBox5 = new VBox(mainHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel3"));
							break;

						case "CHANNEL4":
							column.setText(null);
							column.setMinWidth(190);
							column.setMaxWidth(190);
							Label mainHeader6 = new Label("CHANNEL 4\n(BOB2 - 104: P17 -89/90)");
							mainHeader6.setWrapText(true);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setMinWidth(190);
							mainHeader6.setMaxWidth(190);
							VBox headerBox6 = new VBox(mainHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel4"));
							break;

						default:
//							column.setMinWidth(120);
//							column.setMaxWidth(120);
							break;
						}

					});

					tableScrollPane.setContent(powerManMk1Table1DataTableView);
					powerManMk1Table1DataTableView.setEditable(true);

					// -------- INPUT VOLTAGE ----------
					TableColumn<PowerManMk1, String> inputColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(0);

					inputColumn.setCellValueFactory(new PropertyValueFactory<>("inputVoltageSetting"));
					inputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					inputColumn.setOnEditCommit(e -> e.getRowValue().setInputVoltageSetting(e.getNewValue()));

					// -------- DESCRIPTION ----------
					TableColumn<PowerManMk1, String> descColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(1);

					descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
					descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					descColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

					// -------- EXPECTED VALUE ----------
					TableColumn<PowerManMk1, String> expectedColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(2);

					expectedColumn.setCellValueFactory(new PropertyValueFactory<>("expectedValue"));
					expectedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					expectedColumn.setOnEditCommit(e -> e.getRowValue().setExpectedValue(e.getNewValue()));

					// -------- CHANNEL 1 ----------
					TableColumn<PowerManMk1, String> ch1Column = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(3);

					ch1Column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
					ch1Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch1Column.setOnEditCommit(e -> e.getRowValue().setChannel1(e.getNewValue()));

					// -------- CHANNEL 2 ----------
					TableColumn<PowerManMk1, String> ch2Column = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(4);

					ch2Column.setCellValueFactory(new PropertyValueFactory<>("channel2"));
					ch2Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch2Column.setOnEditCommit(e -> e.getRowValue().setChannel2(e.getNewValue()));

					// -------- CHANNEL 3 ----------
					TableColumn<PowerManMk1, String> ch3Column = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(5);

					ch3Column.setCellValueFactory(new PropertyValueFactory<>("channel3"));
					ch3Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch3Column.setOnEditCommit(e -> e.getRowValue().setChannel3(e.getNewValue()));

					// -------- CHANNEL 4 ----------
					TableColumn<PowerManMk1, String> ch4Column = (TableColumn<PowerManMk1, String>) powerManMk1Table1DataTableView
							.getColumns().get(6);

					ch4Column.setCellValueFactory(new PropertyValueFactory<>("channel4"));
					ch4Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch4Column.setOnEditCommit(e -> e.getRowValue().setChannel4(e.getNewValue()));

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to load PowerMan MK1 data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	private ScrollPane createPowermanMk12Table1DataTable() {

		powerManMk12Table1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(powerManMk12Table1DataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// --- ONLY ONE DEFAULT ROW ---
				PowerManMk1 dto = new PowerManMk1();

				dto.setInputVoltageSetting("28");
				dto.setDescription("LOW TO RETURN");
				dto.setExpectedValue("385.4 - 412.6");

				dto.setChannel1("");
				dto.setChannel2("");
				dto.setChannel3("");
				dto.setChannel4("");

				t2Ch1 = dto.getChannel1();
				t2Ch2 = dto.getChannel1();
				t2Ch3 = dto.getChannel1();
				t2Ch4 = dto.getChannel1();

				powerManMk12Table1DataList.add(dto);

				// Create table after data is ready
				powerManMk12Table1DataTableView = powerManMk1Table1DataFactory
						.createTableView(powerManMk12Table1DataList, false, false);

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					powerManMk12Table1DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();
						System.out.println("check column" + colName);

						switch (colName) {
						case "INPUT VOLTAGE SETTING":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(130);
							Label mainHeader = new Label("INPUT VOLTAGE SETTING\n(VOLTS DC)");
							mainHeader.setWrapText(true);
							mainHeader.setTextAlignment(TextAlignment.CENTER);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinWidth(130);
							mainHeader.setMaxWidth(130);
							VBox headerBox = new VBox(mainHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("inputvoltagesetting"));
							break;

						case "DESCRIPTION":
							column.setText(null);
							column.setMinWidth(300);
							column.setMaxWidth(300);
							Label mainHeader1 = new Label("DESCRIPTION\n(Monitored Parameter)");
							mainHeader1.setWrapText(true);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinWidth(300);
							mainHeader1.setMaxWidth(300);
							VBox headerBox1 = new VBox(mainHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("description"));
							break;

						case "EXPECTED VALUE":
							column.setText(null);
							column.setMinWidth(350);
							column.setMaxWidth(350);
							Label mainHeader2 = new Label("EXPECTED VALUE\n(HZ)");
							mainHeader2.setWrapText(true);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinWidth(350);
							mainHeader2.setMaxWidth(350);
							VBox headerBox2 = new VBox(mainHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("expectedvalue"));
							break;

						case "CHANNEL1":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader3 = new Label("CHANNEL 1\n(BOB2 - 104: P17 -80/79)");
							mainHeader3.setWrapText(true);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinWidth(185);
							mainHeader3.setMaxWidth(185);
							VBox headerBox3 = new VBox(mainHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
							break;

						case "CHANNEL2":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader4 = new Label("CHANNEL 2\n(BOB2 - 104: P17 -81/82)");
							mainHeader4.setWrapText(true);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinWidth(185);
							mainHeader4.setMaxWidth(185);
							VBox headerBox4 = new VBox(mainHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel2"));
							break;

						case "CHANNEL3":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader5 = new Label("CHANNEL 3\n(BOB2 - 104: P17 -86/87)");
							mainHeader5.setWrapText(true);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinWidth(185);
							mainHeader5.setMaxWidth(185);
							VBox headerBox5 = new VBox(mainHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel3"));
							break;

						case "CHANNEL4":
							column.setText(null);
							column.setMinWidth(190);
							column.setMaxWidth(190);
							Label mainHeader6 = new Label("CHANNEL 4\n(BOB2 - 104: P17 -89/90)");
							mainHeader6.setWrapText(true);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setMinWidth(190);
							mainHeader6.setMaxWidth(190);
							VBox headerBox6 = new VBox(mainHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel4"));
							break;

						default:
//								column.setMinWidth(120);
//								column.setMaxWidth(120);
							break;
						}

					});

					tableScrollPane.setContent(powerManMk12Table1DataTableView);
					powerManMk12Table1DataTableView.setEditable(true);

					// -------- INPUT VOLTAGE ----------
					TableColumn<PowerManMk1, String> inputColumn = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(0);

					inputColumn.setCellValueFactory(new PropertyValueFactory<>("inputVoltageSetting"));
					inputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					inputColumn.setOnEditCommit(e -> e.getRowValue().setInputVoltageSetting(e.getNewValue()));

					// -------- DESCRIPTION ----------
					TableColumn<PowerManMk1, String> descColumn = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(1);

					descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
					descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					descColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

					// -------- EXPECTED VALUE ----------
					TableColumn<PowerManMk1, String> expectedColumn = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(2);

					expectedColumn.setCellValueFactory(new PropertyValueFactory<>("expectedValue"));
					expectedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					expectedColumn.setOnEditCommit(e -> e.getRowValue().setExpectedValue(e.getNewValue()));

					// -------- CHANNEL 1 ----------
					TableColumn<PowerManMk1, String> ch1Column = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(3);

					ch1Column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
					ch1Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch1Column.setOnEditCommit(e -> e.getRowValue().setChannel1(e.getNewValue()));

					// -------- CHANNEL 2 ----------
					TableColumn<PowerManMk1, String> ch2Column = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(4);

					ch2Column.setCellValueFactory(new PropertyValueFactory<>("channel2"));
					ch2Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch2Column.setOnEditCommit(e -> e.getRowValue().setChannel2(e.getNewValue()));

					// -------- CHANNEL 3 ----------
					TableColumn<PowerManMk1, String> ch3Column = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(5);

					ch3Column.setCellValueFactory(new PropertyValueFactory<>("channel3"));
					ch3Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch3Column.setOnEditCommit(e -> e.getRowValue().setChannel3(e.getNewValue()));

					// -------- CHANNEL 4 ----------
					TableColumn<PowerManMk1, String> ch4Column = (TableColumn<PowerManMk1, String>) powerManMk12Table1DataTableView
							.getColumns().get(6);

					ch4Column.setCellValueFactory(new PropertyValueFactory<>("channel4"));
					ch4Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch4Column.setOnEditCommit(e -> e.getRowValue().setChannel4(e.getNewValue()));

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to load PowerMan MK1 data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	private StackPane createPowerManMk1Table2Content() {
		powerManMk1Table2StackPane.getChildren().clear();
		powerManMk1Table2StackPane.getStyleClass().add("tab-content-container");
		powerManMk1Table2StackPane.getChildren().clear();
		powerManMk1Table2StackPane.getChildren().add(powerManMk1Table2ResultGridPane());

		return powerManMk1Table2StackPane;
	}

	private StackPane createPowerManMk1Table22Content() {
		powerManMk1Table22StackPane.getChildren().clear();
		powerManMk1Table22StackPane.getStyleClass().add("tab-content-container");
		powerManMk1Table22StackPane.getChildren().clear();

		powerManMk1Table22StackPane.getChildren().add(powerManMk1Table22ResultGridPane());

		return powerManMk1Table22StackPane;
	}

	private GridPane powerManMk1Table2ResultGridPane() {

		powerManMk1Table2GridPane.getChildren().clear();
		powerManMk1Table2GridPane.getColumnConstraints().clear();
		powerManMk1Table2GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		ch11553Table1GridPane.setPadding(new Insets(5));

		powerManMk1Table2GridPane.getColumnConstraints().addAll(firstColumn);
		powerManMk1Table2GridPane.getRowConstraints().addAll(firstRow);

		powerManMk1Table2GridPane.add(createPowermanMk1Table2DataTable(), 0, 0);

		return powerManMk1Table2GridPane;
	}

	private GridPane powerManMk1Table22ResultGridPane() {

		powerManMk1Table22GridPane.getChildren().clear();
		powerManMk1Table22GridPane.getColumnConstraints().clear();
		powerManMk1Table22GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		ch11553Table1GridPane.setPadding(new Insets(5));

		powerManMk1Table22GridPane.getColumnConstraints().addAll(firstColumn);
		powerManMk1Table22GridPane.getRowConstraints().addAll(firstRow);

		powerManMk1Table22GridPane.add(createPowermanMk1Table22DataTable(), 0, 0);

		return powerManMk1Table22GridPane;
	}

	public ScrollPane createPowermanMk1Table2DataTable() {
		powerManMk1Table2DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(powerManMk12Table1DataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// --- ONLY ONE DEFAULT ROW ---
				PowerManMk1 dto = new PowerManMk1();

				dto.setInputVoltageSetting("28");
				dto.setDescription("HIGH TO RETURN");
				dto.setExpectedValue("385.4 - 412.6");

				dto.setChannel1("");
				dto.setChannel2("");
				dto.setChannel3("");
				dto.setChannel4("");

				powerManMk1Table2DataList.add(dto);

				// Create table after data is ready
				powerManMk1Table2DataTableView = powerManMk1Table2DataFactory.createTableView(powerManMk1Table2DataList,
						false, false);

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					powerManMk1Table2DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						case "INPUT VOLTAGE SETTING":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(130);
							Label mainHeader = new Label("INPUT VOLTAGE SETTING\n(VOLTS DC)");
							mainHeader.setWrapText(true);
							mainHeader.setTextAlignment(TextAlignment.CENTER);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinWidth(130);
							mainHeader.setMaxWidth(130);
							VBox headerBox = new VBox(mainHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("inputvoltagesetting"));
							break;

						case "DESCRIPTION":
							column.setText(null);
							column.setMinWidth(300);
							column.setMaxWidth(300);
							Label mainHeader1 = new Label("DESCRIPTION\n(Monitored Parameter)");
							mainHeader1.setWrapText(true);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinWidth(300);
							mainHeader1.setMaxWidth(300);
							VBox headerBox1 = new VBox(mainHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("description"));
							break;

						case "EXPECTED VALUE":
							column.setText(null);
							column.setMinWidth(350);
							column.setMaxWidth(350);
							Label mainHeader2 = new Label("EXPECTED VALUE\n(HZ)");
							mainHeader2.setWrapText(true);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinWidth(350);
							mainHeader2.setMaxWidth(350);
							VBox headerBox2 = new VBox(mainHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("expectedvalue"));
							break;

						case "CHANNEL1":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader3 = new Label("CHANNEL 1\\n(DFCC-J5-93/76)\\n AETS:J47-1/15)");
							mainHeader3.setWrapText(true);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinWidth(185);
							mainHeader3.setMaxWidth(185);
							VBox headerBox3 = new VBox(mainHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
							break;

						case "CHANNEL2":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader4 = new Label("CHANNEL 2\\n(DFCC-J11-93/76)\\n AETS:J47-28/41)");
							mainHeader4.setWrapText(true);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinWidth(185);
							mainHeader4.setMaxWidth(185);
							VBox headerBox4 = new VBox(mainHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel2"));
							break;

						case "CHANNEL3":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader5 = new Label("CHANNEL 3\\n(DFCC-J17-93/76)\\n AETS:J17-93/76)");
							mainHeader5.setWrapText(true);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinWidth(185);
							mainHeader5.setMaxWidth(185);
							VBox headerBox5 = new VBox(mainHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel3"));
							break;

						case "CHANNEL4":
							column.setText(null);
							column.setMinWidth(190);
							column.setMaxWidth(190);
							Label mainHeader6 = new Label("CHANNEL 4\\n(DFCC-J47-78/92)\\n AETS:J47-78/92)");
							mainHeader6.setWrapText(true);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setMinWidth(190);
							mainHeader6.setMaxWidth(190);
							VBox headerBox6 = new VBox(mainHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel4"));
							break;

						default:
//							column.setMinWidth(120);
//							column.setMaxWidth(120);
							break;
						}

					});

					tableScrollPane.setContent(powerManMk1Table2DataTableView);
					powerManMk1Table2DataTableView.setEditable(true);

					// -------- INPUT VOLTAGE ----------
					TableColumn<PowerManMk1, String> inputColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(0);

					inputColumn.setCellValueFactory(new PropertyValueFactory<>("inputVoltageSetting"));
					inputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					inputColumn.setOnEditCommit(e -> e.getRowValue().setInputVoltageSetting(e.getNewValue()));

					// -------- DESCRIPTION ----------
					TableColumn<PowerManMk1, String> descColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(1);

					descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
					descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					descColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

					// -------- EXPECTED VALUE ----------
					TableColumn<PowerManMk1, String> expectedColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(2);

					expectedColumn.setCellValueFactory(new PropertyValueFactory<>("expectedValue"));
					expectedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					expectedColumn.setOnEditCommit(e -> e.getRowValue().setExpectedValue(e.getNewValue()));

					// -------- CHANNEL 1 ----------
					TableColumn<PowerManMk1, String> ch1Column = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(3);

					ch1Column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
					ch1Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch1Column.setOnEditCommit(e -> e.getRowValue().setChannel1(e.getNewValue()));

					// -------- CHANNEL 2 ----------
					TableColumn<PowerManMk1, String> ch2Column = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(4);

					ch2Column.setCellValueFactory(new PropertyValueFactory<>("channel2"));
					ch2Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch2Column.setOnEditCommit(e -> e.getRowValue().setChannel2(e.getNewValue()));

					// -------- CHANNEL 3 ----------
					TableColumn<PowerManMk1, String> ch3Column = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(5);

					ch3Column.setCellValueFactory(new PropertyValueFactory<>("channel3"));
					ch3Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch3Column.setOnEditCommit(e -> e.getRowValue().setChannel3(e.getNewValue()));

					// -------- CHANNEL 4 ----------
					TableColumn<PowerManMk1, String> ch4Column = (TableColumn<PowerManMk1, String>) powerManMk1Table2DataTableView
							.getColumns().get(6);

					ch4Column.setCellValueFactory(new PropertyValueFactory<>("channel4"));
					ch4Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch4Column.setOnEditCommit(e -> e.getRowValue().setChannel4(e.getNewValue()));

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to load PowerMan MK1 data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	public ScrollPane createPowermanMk1Table22DataTable() {
		powerManMk1Table22DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(powerManMk1Table22DataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// --- ONLY ONE DEFAULT ROW ---
				PowerManMk1 dto = new PowerManMk1();

				dto.setInputVoltageSetting("28");
				dto.setDescription("LOW TO RETURN");
				dto.setExpectedValue("385.4 - 412.6");

				dto.setChannel1("");
				dto.setChannel2("");
				dto.setChannel3("");
				dto.setChannel4("");

				powerManMk1Table22DataList.add(dto);

				// Create table after data is ready
				powerManMk1Table22DataTableView = powerManMk1Table22DataFactory
						.createTableView(powerManMk1Table22DataList, false, false);

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					powerManMk1Table22DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						case "INPUT VOLTAGE SETTING":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(130);
							Label mainHeader = new Label("INPUT VOLTAGE SETTING\n(VOLTS DC)");
							mainHeader.setWrapText(true);
							mainHeader.setTextAlignment(TextAlignment.CENTER);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinWidth(130);
							mainHeader.setMaxWidth(130);
							VBox headerBox = new VBox(mainHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("inputvoltagesetting"));
							break;

						case "DESCRIPTION":
							column.setText(null);
							column.setMinWidth(300);
							column.setMaxWidth(300);
							Label mainHeader1 = new Label("DESCRIPTION\n(Monitored Parameter)");
							mainHeader1.setWrapText(true);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinWidth(300);
							mainHeader1.setMaxWidth(300);
							VBox headerBox1 = new VBox(mainHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("description"));
							break;

						case "EXPECTED VALUE":
							column.setText(null);
							column.setMinWidth(350);
							column.setMaxWidth(350);
							Label mainHeader2 = new Label("EXPECTED VALUE\n(HZ)");
							mainHeader2.setWrapText(true);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinWidth(350);
							mainHeader2.setMaxWidth(350);
							VBox headerBox2 = new VBox(mainHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("expectedvalue"));
							break;

						case "CHANNEL1":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader3 = new Label("CHANNEL 1\n(DFCC-J5-85/76)\n AETS:J47-2/15)");
							mainHeader3.setWrapText(true);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinWidth(185);
							mainHeader3.setMaxWidth(185);
							VBox headerBox3 = new VBox(mainHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
							break;

						case "CHANNEL2":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader4 = new Label("CHANNEL 2\n(DFCC-J11-85/76)\\n AETS:J47-29/41)");
							mainHeader4.setWrapText(true);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinWidth(185);
							mainHeader4.setMaxWidth(185);
							VBox headerBox4 = new VBox(mainHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel2"));
							break;

						case "CHANNEL3":
							column.setText(null);
							column.setMinWidth(185);
							column.setMaxWidth(185);
							Label mainHeader5 = new Label("CHANNEL 3\n(DFCC-J17-85/76)\\n AETS:J47-57/68)");
							mainHeader5.setWrapText(true);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinWidth(185);
							mainHeader5.setMaxWidth(185);
							VBox headerBox5 = new VBox(mainHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel3"));
							break;

						case "CHANNEL4":
							column.setText(null);
							column.setMinWidth(190);
							column.setMaxWidth(190);
							Label mainHeader6 = new Label("CHANNEL 4\n(DFCC-J23-85/76)\\n AETS:J47-79/92)");
							mainHeader6.setWrapText(true);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setMinWidth(190);
							mainHeader6.setMaxWidth(190);
							VBox headerBox6 = new VBox(mainHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("chennel4"));
							break;

						default:
//								column.setMinWidth(120);
//								column.setMaxWidth(120);
							break;
						}

					});

					tableScrollPane.setContent(powerManMk1Table22DataTableView);
					powerManMk1Table22DataTableView.setEditable(true);

					// -------- INPUT VOLTAGE ----------
					TableColumn<PowerManMk1, String> inputColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(0);

					inputColumn.setCellValueFactory(new PropertyValueFactory<>("inputVoltageSetting"));
					inputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					inputColumn.setOnEditCommit(e -> e.getRowValue().setInputVoltageSetting(e.getNewValue()));

					// -------- DESCRIPTION ----------
					TableColumn<PowerManMk1, String> descColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(1);

					descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
					descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					descColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

					// -------- EXPECTED VALUE ----------
					TableColumn<PowerManMk1, String> expectedColumn = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(2);

					expectedColumn.setCellValueFactory(new PropertyValueFactory<>("expectedValue"));
					expectedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					expectedColumn.setOnEditCommit(e -> e.getRowValue().setExpectedValue(e.getNewValue()));

					// -------- CHANNEL 1 ----------
					TableColumn<PowerManMk1, String> ch1Column = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(3);

					ch1Column.setCellValueFactory(new PropertyValueFactory<>("channel1"));
					ch1Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch1Column.setOnEditCommit(e -> e.getRowValue().setChannel1(e.getNewValue()));

					// -------- CHANNEL 2 ----------
					TableColumn<PowerManMk1, String> ch2Column = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(4);

					ch2Column.setCellValueFactory(new PropertyValueFactory<>("channel2"));
					ch2Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch2Column.setOnEditCommit(e -> e.getRowValue().setChannel2(e.getNewValue()));

					// -------- CHANNEL 3 ----------
					TableColumn<PowerManMk1, String> ch3Column = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(5);

					ch3Column.setCellValueFactory(new PropertyValueFactory<>("channel3"));
					ch3Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch3Column.setOnEditCommit(e -> e.getRowValue().setChannel3(e.getNewValue()));

					// -------- CHANNEL 4 ----------
					TableColumn<PowerManMk1, String> ch4Column = (TableColumn<PowerManMk1, String>) powerManMk1Table22DataTableView
							.getColumns().get(6);

					ch4Column.setCellValueFactory(new PropertyValueFactory<>("channel4"));
					ch4Column.setCellFactory(TextFieldTableCell.forTableColumn());
					ch4Column.setOnEditCommit(e -> e.getRowValue().setChannel4(e.getNewValue()));

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to load PowerMan MK1 data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	private boolean isInRange(String value) {
		try {
			double v = Double.parseDouble(value);
			return v >= 385.4 && v <= 412.6;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean allChannelsInRange(String ch1, String ch2, String ch3, String ch4) {
		return isInRange(ch1) && isInRange(ch2) && isInRange(ch3) && isInRange(ch4);
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	private boolean isEmpty(String value) {
	    return value == null || value.trim().isEmpty();
	}

	private HBox createPrintButtonHbox() {

		HBox printButtonContainer = new HBox(); // only create once
		printButtonContainer.setAlignment(Pos.CENTER);
		printButtonContainer.setSpacing(5);
		printButtonContainer.getChildren().addAll(saveButton,fetchButton, printButton);

		saveButton.setOnAction(e -> {
			if(selectedSessionId == null || selectedStageId == null ) {
				Notifications.showErrorAlert("Please select Session and Stage.");
				return;
			}
			if(uutId.equalsIgnoreCase("UUT1")) {
			PowerManMk1 dto = powerManMk1Table1DataList.get(0);
			PowerManMk1 dto2 = powerManMk12Table1DataList.get(0);
			PowerManDataAnalysis powerMan = new PowerManDataAnalysis();
			if (isEmpty(dto.getChannel1()) || isEmpty(dto.getChannel2()) || isEmpty(dto.getChannel3()) || isEmpty(dto.getChannel4()) ||
				    isEmpty(dto2.getChannel1()) || isEmpty(dto2.getChannel2()) || isEmpty(dto2.getChannel3()) || isEmpty(dto2.getChannel4())) {

				    Notifications.showErrorAlert("Please enter value in all channels");
				    return;
				}
				
			powerMan.setFirstRowch1(dto.getChannel1());
			powerMan.setFirstRowch2(dto.getChannel2());
			powerMan.setFirstRowch3(dto.getChannel3());
			powerMan.setFirstRowch4(dto.getChannel4());
			powerMan.setSecondRowch1(dto2.getChannel1());
			powerMan.setSecondRowch2(dto2.getChannel2());
			powerMan.setSecondRowch3(dto2.getChannel3());
			powerMan.setSecondRowch4(dto2.getChannel4());
			powerMan.setSessionId(selectedSessionId);
			powerMan.setStageId(selectedStageId);
			System.out.println("Check dto :" +dto.getChannel1() );
			advancedDataAnalysisManagement.addPowerManConfig(powerMan);
			Notifications.showSuccessAlert("Data added Successfully.");
			printButton.setDisable(false);
			}else {
				PowerManMk1 dto = powerManMk1Table2DataList.get(0);
				PowerManMk1 dto2 = powerManMk1Table22DataList.get(0);
				PowerManDataAnalysis powerMan = new PowerManDataAnalysis();
				if (isEmpty(dto.getChannel1()) || isEmpty(dto.getChannel2()) || isEmpty(dto.getChannel3()) || isEmpty(dto.getChannel4()) ||
					    isEmpty(dto2.getChannel1()) || isEmpty(dto2.getChannel2()) || isEmpty(dto2.getChannel3()) || isEmpty(dto2.getChannel4())) {

					    Notifications.showErrorAlert("Please enter value in all channels");
					    return;
					}

				powerMan.setFirstRowch1(dto.getChannel1());
				powerMan.setFirstRowch2(dto.getChannel2());
				powerMan.setFirstRowch3(dto.getChannel3());
				powerMan.setFirstRowch4(dto.getChannel4());
				powerMan.setSecondRowch1(dto2.getChannel1());
				powerMan.setSecondRowch2(dto2.getChannel2());
				powerMan.setSecondRowch3(dto2.getChannel3());
				powerMan.setSecondRowch4(dto2.getChannel4());
				powerMan.setSessionId(selectedSessionId);
				powerMan.setStageId(selectedStageId);
				System.out.println("Check dto :" +dto.getChannel1() );
				advancedDataAnalysisManagement.addPowerManConfig(powerMan);
				Notifications.showSuccessAlert("Data added Successfully.");
				printButton.setDisable(false);
			}
			
			
			
			
		});
		
		fetchButton.setOnAction(e -> {
			 PowerManDataAnalysis entity = advancedDataAnalysisManagement.getPowerManConfig(selectedSessionId, selectedStageId);
			 if(selectedSessionId == null || selectedStageId == null ) {
					Notifications.showErrorAlert("Please select Session and Stage.");
					return;
				}
			
			if(entity == null) {
				Notifications.showErrorAlert("Not configured");
				return;
			}
			if(uutId.equalsIgnoreCase("UUT1")) {
			PowerManMk1 dto = powerManMk1Table1DataList.get(0);
			PowerManMk1 dto2 = powerManMk12Table1DataList.get(0);
			dto.setChannel1(entity.getFirstRowch1());
			dto.setChannel2(entity.getFirstRowch2());
			dto.setChannel3(entity.getFirstRowch3());
			dto.setChannel4(entity.getFirstRowch4());
			
			dto2.setChannel1(entity.getSecondRowch1());
			dto2.setChannel2(entity.getSecondRowch2());
			dto2.setChannel3(entity.getSecondRowch3());
			dto2.setChannel4(entity.getSecondRowch4());
			
			 powerManMk1Table1DataTableView.refresh();
			 powerManMk1Table2DataTableView.refresh();
			 Notifications.showSuccessAlert("Data fetched Successfully.");
			}else {
				PowerManMk1 dto = powerManMk1Table2DataList.get(0);
				PowerManMk1 dto2 = powerManMk1Table22DataList.get(0);
				dto.setChannel1(entity.getFirstRowch1());
				dto.setChannel2(entity.getFirstRowch2());
				dto.setChannel3(entity.getFirstRowch3());
				dto.setChannel4(entity.getFirstRowch4());
				
				dto2.setChannel1(entity.getSecondRowch1());
				dto2.setChannel2(entity.getSecondRowch2());
				dto2.setChannel3(entity.getSecondRowch3());
				dto2.setChannel4(entity.getSecondRowch4());
				
				 powerManMk1Table1DataTableView.refresh();
				 powerManMk1Table2DataTableView.refresh();
				 Notifications.showSuccessAlert("Data fetched Successfully.");
			}
			
		});
		
//		checkButton.setOnAction(e -> {
//
//			if ("UUT1".equalsIgnoreCase(uutId)) {
//				PowerManMk1 dto1 = powerManMk1Table1DataList.get(0);
//				PowerManMk1 dto12 = powerManMk12Table1DataList.get(0);
//				if (isNullOrEmpty(dto1.getChannel1()) || isNullOrEmpty(dto1.getChannel2())
//						|| isNullOrEmpty(dto1.getChannel3()) || isNullOrEmpty(dto1.getChannel4())
//						|| isNullOrEmpty(dto12.getChannel1()) || isNullOrEmpty(dto12.getChannel2())
//						|| isNullOrEmpty(dto12.getChannel3()) || isNullOrEmpty(dto12.getChannel4())) {
//
//					Notifications.showErrorAlert("Please enter all the values");
//					return;
//				}
//
//				t1Ch1 = dto1.getChannel1();
//				t1Ch2 = dto1.getChannel2();
//				t1Ch3 = dto1.getChannel3();
//				t1Ch4 = dto1.getChannel4();
//
//				t2Ch1 = dto12.getChannel1();
//				t2Ch2 = dto12.getChannel2();
//				t2Ch3 = dto12.getChannel3();
//				t2Ch4 = dto12.getChannel4();
//
//				printButton.setDisable(false);
//			} else {
//				PowerManMk1 dto2 = powerManMk1Table2DataList.get(0);
//				PowerManMk1 dto22 = powerManMk1Table22DataList.get(0);
//				if (isNullOrEmpty(dto2.getChannel1()) || isNullOrEmpty(dto2.getChannel2())
//						|| isNullOrEmpty(dto2.getChannel3()) || isNullOrEmpty(dto2.getChannel4())
//						|| isNullOrEmpty(dto22.getChannel1()) || isNullOrEmpty(dto22.getChannel2())
//						|| isNullOrEmpty(dto22.getChannel3()) || isNullOrEmpty(dto22.getChannel4())) {
//
//					Notifications.showErrorAlert("Please enter all the values");
//					return;
//				}
//
//				t1Ch1 = dto2.getChannel1();
//				t1Ch2 = dto2.getChannel2();
//				t1Ch3 = dto2.getChannel3();
//				t1Ch4 = dto2.getChannel4();
//
//				t2Ch1 = dto22.getChannel1();
//				t2Ch2 = dto22.getChannel2();
//				t2Ch3 = dto22.getChannel3();
//				t2Ch4 = dto22.getChannel4();
//				printButton.setDisable(false);
//			}
//
//			if ((allChannelsInRange(t1Ch1, t1Ch2, t1Ch3, t1Ch4)) && (allChannelsInRange(t2Ch1, t2Ch2, t2Ch3, t2Ch4))) {
//				passFail.setText("PASS");
//
//			} else {
//				passFail.setText("FAIL");
//			}
//
//		});

		printButton.setOnAction(e -> {

			if (uutTypeField.getValue() == null || serialnumber.getValue() == null || sessioName.getValue() == null
					|| stageName.getValue() == null) {
				Notifications.showErrorAlert("Please select all the required details to print.");
				return;
			}

			printButtonContainer.setVisible(false);
			printButtonContainer.setManaged(false);

			powerManMk1MainContainerGridPane.applyCss();
			powerManMk1MainContainerGridPane.layout();

			Stage stage = (Stage) powerManMk1MainContainerGridPane.getScene().getWindow();

			tmpUutLabel = new Label(uutTypeField.getValue() == null ? "" : uutTypeField.getValue());
			tmpSerialLabel = new Label(serialnumber.getValue() == null ? "" : serialnumber.getValue());
			tmpSerialLabel.setWrapText(true);
			tmpSessionLabel = new Label(sessioName.getValue() == null ? "" : sessioName.getValue());
			tmpSessionLabel.setWrapText(true);
			tmpStageLabel = new Label(stageName.getValue() == null ? "" : stageName.getValue());
			tmpUutLabel.getStyleClass().add("nonheading-label");
			tmpSerialLabel.getStyleClass().add("nonheading-label");
			tmpSessionLabel.getStyleClass().add("nonheading-label");
			tmpStageLabel.getStyleClass().add("nonheading-label");
			HBox uutBox = (HBox) uutTypeField.getParent();
			HBox serialBox = (HBox) serialnumber.getParent();
			HBox sessionBox = (HBox) sessioName.getParent();
			HBox stageBox = (HBox) stageName.getParent();

			try {

				uutTypeField.setVisible(false);
				serialnumber.setVisible(false);
				sessioName.setVisible(false);
				stageName.setVisible(false);

				uutBox.getChildren().add(tmpUutLabel);
				serialBox.getChildren().add(tmpSerialLabel);
				sessionBox.getChildren().add(tmpSessionLabel);
				stageBox.getChildren().add(tmpStageLabel);

				powerManMk1MainContainerGridPane.applyCss();
				powerManMk1MainContainerGridPane.layout();

				exportPageToPDF(stage, powerManMk1MainContainerGridPane);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {

				uutBox.getChildren().remove(tmpUutLabel);
				serialBox.getChildren().remove(tmpSerialLabel);
				sessionBox.getChildren().remove(tmpSessionLabel);
				stageBox.getChildren().remove(tmpStageLabel);

				uutTypeField.setVisible(true);
				serialnumber.setVisible(true);
				sessioName.setVisible(true);
				stageName.setVisible(true);

				printButtonContainer.setVisible(true);
				printButtonContainer.setManaged(true);

				powerManMk1MainContainerGridPane.applyCss();
				powerManMk1MainContainerGridPane.layout();
			}
		});

		return printButtonContainer;
	}

	private void exportPageToPDF(Stage stage, GridPane root) {

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save PDF");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

		File file = chooser.showSaveDialog(stage);
		if (file == null)
			return;

		try {
			// Take snapshot of full page
			SnapshotParameters params = new SnapshotParameters();
			params.setTransform(new Scale(2, 2)); // Higher resolution
			WritableImage image = root.snapshot(params, null);

			// Convert snapshot to BufferedImage
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

			// Convert BufferedImage → ImageData
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			ImageData imgData = ImageDataFactory.create(baos.toByteArray());
			Image pdfImage = new Image(imgData);

			// Create PDF document
			PdfWriter writer = new PdfWriter(file.getAbsolutePath());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A3.rotate());

			// Fit to page
			pdfImage.scaleToFit(PageSize.A3.rotate().getWidth(), PageSize.A3.rotate().getHeight());
			pdfImage.setAutoScale(true);

			// Add image to PDF
			document.add(pdfImage);

			document.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
