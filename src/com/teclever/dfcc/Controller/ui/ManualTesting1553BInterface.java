package com.teclever.dfcc.Controller.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Interface1553B;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import javafx.util.StringConverter;

class ManualTesting1553BDataTableViewFactory implements TableViewFactory<Interface1553B> {
	@Override
	public CustomTableView<Interface1553B> createTableView(ObservableList<Interface1553B> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Interface1553B.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTesting1553BInterface {

	private GridPane interface1553BMainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private HBox headingHbox3 = new HBox(10);
	private Label titleLabel = new Label("1553B INTERFACE MODE CODE CHECKS");

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

	private StackPane interface1553BTable1StackPane = new StackPane();
	private GridPane interface1553BTable1GridPane = new GridPane();

	private StackPane interface1553BTable22StackPane = new StackPane();

	private ObservableList<Interface1553B> interface1553BTable1DataList = FXCollections.observableArrayList();
	private CustomTableView<Interface1553B> interface1553BTable1DataTableView;
	private TableViewFactory<Interface1553B> interface1553BTable1DataFactory = new ManualTesting1553BDataTableViewFactory();

	private String uutId = "UUT1";
	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label(
	        "NOTE:\n"
	        + "(1) Testing should be done Two Channels at a time. Ch 1 and Ch 2 are tested First. Ch 3 and Ch 4 are tested Next.\n"
	        + "\n"
	        + "(2) @ → Data ‘1717’ to be Loaded at SBA 100V and Monitored at Memory Location 003C8204 in DFCC.\n"
	        + "\n"
	        + "(3) $ → Results in ‘Last Commanded Word’ Transmitted from DFCC (‘A811’ for Ch 1 & 3, ‘B011’ for Ch 2 & 4)\n"
	        + "\n"
	        + "(4) $ → Results in ‘Last Commanded Word’ Transmitted from DFCC. For the procedure mentioned in Appendix 13, Last Command word is (‘ABF1’ for Ch1 & 3, ‘B3F1’ for Ch2 & 4)"
	);

	private Label tmpUutLabel;
	private Label tmpSerialLabel;
	private Label tmpSessionLabel;
	private Label tmpStageLabel;

	private HBox tbNoHBox = new HBox();
	private Label testEquipment = new Label("TEST EQUIPMENT USED: SerialBus Analyser, LORAL SBA 100V or Equivalent(PATS++)");
	private Label tbNol1 = new Label("TB NO.:134  PASS/FAIL");
	private Label passFail1 = new Label("PASS/FAIL");
	private Label testRep = new Label("TESTING REP: ");
	private Label testRepDate = new Label("DATE: ");
	private Label tiqmRep = new Label("TI/QM REP: ");
	private Label tiqmRepDate = new Label("DATE: ");
	private SessionService s = new SessionService();


	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");
	private Button checkButton = new Button("Check");

	private String selectedSessionId;
	private String selectedStageName;

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

	public ManualTesting1553BInterface() {

		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		for (SessionDto session : sessionList) {
			sessionNameId.put(session.getSessionName(), session.getSessionId());

		}

		initializeUUTTypeComboBox();

	}

	public GridPane create1553BMainContainerGridPane() {
		interface1553BMainContainerGridPane.getChildren().clear();
		interface1553BMainContainerGridPane.getColumnConstraints().clear();
		interface1553BMainContainerGridPane.getRowConstraints().clear();

		interface1553BMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		interface1553BMainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(5);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(85);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(25);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(3);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(3);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(5);
		


		interface1553BMainContainerGridPane.setHgap(2);
		interface1553BMainContainerGridPane.setVgap(2);

		interface1553BMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		interface1553BMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow,
				fivthRow, sixthRow, seventhRow);
		interface1553BMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		interface1553BMainContainerGridPane.add(createHeadingBox(), 0, 0);
		interface1553BMainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		interface1553BMainContainerGridPane.add(create1553BInterfaceTableContent(), 0, 2);
		interface1553BMainContainerGridPane.add(createNoteBox(), 0, 3);
		interface1553BMainContainerGridPane.add(createTestEquipmentBox(), 0, 4);
		interface1553BMainContainerGridPane.add(createRepBox(), 0, 5);
		interface1553BMainContainerGridPane.add(createRepDateBox(), 0, 6);

		return interface1553BMainContainerGridPane;
	}

	private GridPane createHeadingBox() {

		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(90);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(10);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		topBoxGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		topBoxGridPane.getRowConstraints().addAll(firstRow);

		// 🔥 CREATE NEW INSTANCES every time (NO reuse)
		HBox headingHbox1 = new HBox();

		Label title = new Label(titleLabel.getText());
		titleLabel.setUnderline(true);

		headingHbox1.setAlignment(Pos.CENTER);
		title.setUnderline(true);
		title.getStyleClass().add("advanced-testing-title");

		noteLabel.setStyle("advanced-testing-title");

		headingHbox1.getChildren().add(title);

		topBoxGridPane.add(headingHbox1, 0, 0);
		topBoxGridPane.add(createPrintButtonHbox(), 1, 0);

		return topBoxGridPane;
	}

	private GridPane createNoteBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(100);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);


		grid.getColumnConstraints().addAll(col1);

		grid.getRowConstraints().addAll(row1);

		grid.add(noteLabel, 0, 0);
	

		noteLabel.getStyleClass().add("nonheading-label");




		return grid;
	}
	
	private GridPane createTestEquipmentBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(70);
		
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(30);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);


		grid.getColumnConstraints().addAll(col1);

		grid.getRowConstraints().addAll(row1);

		grid.add(testEquipment, 0, 0);
		grid.add(tbNol1, 1, 0);
	
		tbNol1.getStyleClass().add("nonheading-label");
		testEquipment.getStyleClass().add("nonheading-label");




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

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initalizeSerialNoComboBox(uutId);

			create1553BMainContainerGridPane();
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
		for (UnitSessionDetailsDTO stageName : stageList) {
			stageNameId.put(stageName.getStageId(), stageName.getStageName());
		}

		stageName.setItems(FXCollections.observableArrayList(stageNameId.values()));

		stageName.setOnAction((event) -> {
			selectedStageName = stageName.getSelectionModel().getSelectedItem();
		});

	}

	private StackPane create1553BInterfaceTableContent() {

		interface1553BTable1StackPane.getChildren().clear();
		interface1553BTable1StackPane.getStyleClass().add("tab-content-container");
		interface1553BTable1StackPane.getChildren().clear();
		interface1553BTable1StackPane.getChildren().add(interface1553BableResultGridPane());
		return interface1553BTable1StackPane;
	}

	private GridPane interface1553BableResultGridPane() {
		interface1553BTable1GridPane.getChildren().clear();
		interface1553BTable1GridPane.getColumnConstraints().clear();
		interface1553BTable1GridPane.getRowConstraints().clear();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		interface1553BTable1GridPane.getColumnConstraints().add(firstColumn);
		interface1553BTable1GridPane.getRowConstraints().add(firstRow);

		interface1553BTable1GridPane.add(create1553BIntrefaceTable1DataTable(), 0, 0);

		return interface1553BTable1GridPane;
	}



	private ScrollPane create1553BIntrefaceTable1DataTable() {

	    interface1553BTable1DataList.clear();

	    // ---- ADD 7 DEFAULT ROWS ----
	    for (int i = 0; i < 7; i++) {
	        interface1553BTable1DataList.add(new Interface1553B());
	    }

	    ScrollPane tableScrollPane = new ScrollPane();
	    tableScrollPane.setFitToHeight(true);
	    tableScrollPane.setFitToWidth(true); // ensure ScrollPane expands horizontally

	    Task<Void> task = new Task<Void>() {
	        @Override
	        protected Void call() {

	            Platform.runLater(() -> {

	                interface1553BTable1DataTableView =
	                        interface1553BTable1DataFactory.createTableView(
	                                interface1553BTable1DataList, false, false);
	                
	                interface1553BTable1DataTableView.getColumns().forEach(column -> {
                        String colName = column.getText();

                        switch(colName) {
                            case "C1":
                                column.setText(null);
                                

                                Label mainHeader = new Label("01");
                                mainHeader.setWrapText(true);
                                mainHeader.setTextAlignment(TextAlignment.CENTER);
                                mainHeader.setAlignment(Pos.CENTER);
                          

                                VBox headerBox = new VBox(mainHeader);
                                headerBox.setAlignment(Pos.CENTER);
                                headerBox.setSpacing(2);
                                headerBox.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox);
                                column.setCellValueFactory(new PropertyValueFactory<>("c1"));
                                break;
                            case "C2":
                                column.setText(null);
                                column.setMinWidth(130);
                                column.setMaxWidth(130);

                                Label mainHeader1 = new Label("02");
                                mainHeader1.setWrapText(true);
                                mainHeader1.setTextAlignment(TextAlignment.CENTER);
                                mainHeader1.setAlignment(Pos.CENTER);
 

                                VBox headerBox1 = new VBox(mainHeader1);
                                headerBox1.setAlignment(Pos.CENTER);
                                headerBox1.setSpacing(2);
                                headerBox1.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox1);
                                column.setCellValueFactory(new PropertyValueFactory<>("c2"));
                                break;
                                
                            case "C3":
                                column.setText(null);
                             

                                Label mainHeader2 = new Label("03");
                                mainHeader2.setWrapText(true);
                                mainHeader2.setTextAlignment(TextAlignment.CENTER);
                                mainHeader2.setAlignment(Pos.CENTER);


                                VBox headerBox2 = new VBox(mainHeader2);
                                headerBox2.setAlignment(Pos.CENTER);
                                headerBox2.setSpacing(2);
                                headerBox2.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox2);
                                column.setCellValueFactory(new PropertyValueFactory<>("c3"));
                                break;
                                
                            case "C4":
                                column.setText(null);
                 

                                Label mainHeader3 = new Label("04");
                                mainHeader3.setWrapText(true);
                                mainHeader3.setTextAlignment(TextAlignment.CENTER);
                                mainHeader3.setAlignment(Pos.CENTER);
               

                                VBox headerBox3 = new VBox(mainHeader3);
                                headerBox3.setAlignment(Pos.CENTER);
                                headerBox3.setSpacing(2);
                                headerBox3.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox3);
                                column.setCellValueFactory(new PropertyValueFactory<>("c4"));
                                break;
                                
                            case "C5":
                                column.setText(null);
   

                                Label mainHeader4 = new Label("05");
                                mainHeader4.setWrapText(true);
                                mainHeader4.setTextAlignment(TextAlignment.CENTER);
                                mainHeader4.setAlignment(Pos.CENTER);
               

                                VBox headerBox4 = new VBox(mainHeader4);
                                headerBox4.setAlignment(Pos.CENTER);
                                headerBox4.setSpacing(2);
                                headerBox4.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox4);
                                column.setCellValueFactory(new PropertyValueFactory<>("c5"));
                                break;
                                
                            case "C6":
                                column.setText(null);

                                Label mainHeader5 = new Label("06");
                                mainHeader5.setWrapText(true);
                                mainHeader5.setTextAlignment(TextAlignment.CENTER);
                                mainHeader5.setAlignment(Pos.CENTER);
               

                                VBox headerBox5 = new VBox(mainHeader5);
                                headerBox5.setAlignment(Pos.CENTER);
                                headerBox5.setSpacing(2);
                                headerBox5.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox5);
                                column.setCellValueFactory(new PropertyValueFactory<>("c6"));
                                break;
                                
                            case "C7":
                                column.setText(null);

                                Label mainHeader6 = new Label("07");
                                mainHeader6.setWrapText(true);
                                mainHeader6.setTextAlignment(TextAlignment.CENTER);
                                mainHeader6.setAlignment(Pos.CENTER);
               

                                VBox headerBox6 = new VBox(mainHeader6);
                                headerBox6.setAlignment(Pos.CENTER);
                                headerBox6.setSpacing(2);
                                headerBox6.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox6);
                                column.setCellValueFactory(new PropertyValueFactory<>("c7"));
                                break;
                                
                            case "C8":
                                column.setText(null);

                                Label mainHeader7 = new Label("08");
                                mainHeader7.setWrapText(true);
                                mainHeader7.setTextAlignment(TextAlignment.CENTER);
                                mainHeader7.setAlignment(Pos.CENTER);
               

                                VBox headerBox7 = new VBox(mainHeader7);
                                headerBox7.setAlignment(Pos.CENTER);
                                headerBox7.setSpacing(2);
                                headerBox7.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox7);
                                column.setCellValueFactory(new PropertyValueFactory<>("c8"));
                                break;

                            case "C16":
                                column.setText(null);

                                Label mainHeader8 = new Label("16");
                                mainHeader8.setWrapText(true);
                                mainHeader8.setTextAlignment(TextAlignment.CENTER);
                                mainHeader8.setAlignment(Pos.CENTER);
               

                                VBox headerBox8 = new VBox(mainHeader8);
                                headerBox8.setAlignment(Pos.CENTER);
                                headerBox8.setSpacing(2);
                                headerBox8.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox8);
                                column.setCellValueFactory(new PropertyValueFactory<>("c16"));
                                break;
                                
                            case "C17":
                                column.setText(null);

                                Label mainHeader9 = new Label("17");
                                mainHeader9.setWrapText(true);
                                mainHeader9.setTextAlignment(TextAlignment.CENTER);
                                mainHeader9.setAlignment(Pos.CENTER);
               

                                VBox headerBox9 = new VBox(mainHeader9);
                                headerBox9.setAlignment(Pos.CENTER);
                                headerBox9.setSpacing(2);
                                headerBox9.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox9);
                                column.setCellValueFactory(new PropertyValueFactory<>("c17"));
                                break;
                                
                                
                            case "C18":
                                column.setText(null);

                                Label mainHeader10 = new Label("18");
                                mainHeader10.setWrapText(true);
                                mainHeader10.setTextAlignment(TextAlignment.CENTER);
                                mainHeader10.setAlignment(Pos.CENTER);
               

                                VBox headerBox10 = new VBox(mainHeader10);
                                headerBox10.setAlignment(Pos.CENTER);
                                headerBox10.setSpacing(2);
                                headerBox10.setPadding(new Insets(4, 2, 4, 2));

                                column.setGraphic(headerBox10);
                                column.setCellValueFactory(new PropertyValueFactory<>("c18"));
                                break;
                                
                                case "C19":
                                    column.setText(null);

                                    Label mainHeader11 = new Label("19");
                                    mainHeader11.setWrapText(true);
                                    mainHeader11.setTextAlignment(TextAlignment.CENTER);
                                    mainHeader11.setAlignment(Pos.CENTER);
                   

                                    VBox headerBox11 = new VBox(mainHeader11);
                                    headerBox11.setAlignment(Pos.CENTER);
                                    headerBox11.setSpacing(2);
                                    headerBox11.setPadding(new Insets(4, 2, 4, 2));

                                    column.setGraphic(headerBox11);
                                    column.setCellValueFactory(new PropertyValueFactory<>("c19"));
                                    break;
                                
                            

                            default:
                                // Optional default settings
                                break;
                        }
                    });

	                // --- MAKE TABLE FULL WIDTH ---
	                interface1553BTable1DataTableView.setColumnResizePolicy(
	                        TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
	                );

	                List<TableColumn<Interface1553B, ?>> dtoColumns =
	                        new ArrayList<>(interface1553BTable1DataTableView.getColumns());

	                interface1553BTable1DataTableView.getColumns().clear();

	                // ---------- TOP MERGED HEADERS ----------
	                TableColumn<Interface1553B, ?> testResultHeader =
	                        new TableColumn<>("Test Result Status (P: Pass; F: Fail)");
	                testResultHeader.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

	                TableColumn<Interface1553B, ?> modeHeader =
	                        new TableColumn<>("MODE CODE");
	                modeHeader.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

	                // ---------- LEFT LABEL COLUMN ----------
	                TableColumn<Interface1553B, String> leftLabelCol = new TableColumn<>("");
	                leftLabelCol.setMinWidth(250);
	                leftLabelCol.setMaxWidth(250);
	                leftLabelCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");

	                List<String> leftLabels = Arrays.asList(
	                        "Associated Data word (HEX)",
	                        "Data Word Address in DFCC",
	                        "T/R Status",
	                        "Channel 1",
	                        "Channel 2",
	                        "Channel 3",
	                        "Channel 4"
	                );

	                leftLabelCol.setCellValueFactory(cell -> {
	                    int idx = interface1553BTable1DataTableView.getItems().indexOf(cell.getValue());
	                    return new SimpleStringProperty(
	                            idx >= 0 && idx < leftLabels.size() ? leftLabels.get(idx) : ""
	                    );
	                });

	                dtoColumns.add(0, leftLabelCol);

	                // ---------- DEFAULT VALUES ----------
	                List<String> defaultDataWords = Arrays.asList(
	                        "--", "--", "--", "--", "--", "--", "--", "--",
	                        "1616", "@", "$", "1919"
	                );

	                List<String> defaultDataWords1 = Arrays.asList(
	                        "--", "--", "--", "--", "--", "--", "--", "--",
	                        "003C8200", "--", "--", "003C820C"
	                );

	                List<String> defaultDataWords2 = Arrays.asList(
	                        "T", "T", "T", "T", "T", "T", "T", "T",
	                        "T", "R", "T", "T"
	                );

	                // ---------- APPLY CELL FACTORY TO ALL COLUMNS ----------
	                for (int colPos = 0; colPos < dtoColumns.size(); colPos++) {

	                    TableColumn<Interface1553B, ?> rawCol = dtoColumns.get(colPos);

	                    if (colPos == 0) { // left label column (non-editable)
	                        rawCol.setEditable(false);
	                        continue;
	                    }

	                    @SuppressWarnings({"unchecked", "rawtypes"})
	                    TableColumn<Interface1553B, Object> col = (TableColumn) rawCol;

	                    final int dataIndex = colPos - 1;
	                    
	               

	                    

	                    col.setCellFactory(param ->
	                            new TextFieldTableCell<>(
	                                    new StringConverter<Object>() {
	                                        @Override
	                                        public String toString(Object object) {
	                                            return object == null ? "" : object.toString();
	                                        }

	                                        @Override
	                                        public Object fromString(String string) {
	                                            return string;
	                                        }
	                                    }
	                            ) {
	                                @Override
									public void updateItem(Object item, boolean empty) {
	                                    super.updateItem(item, empty);

	                                    if (empty) {
	                                        setText(null);
	                                        setGraphic(null);
	                                        return;
	                                    }

	                                    int row = getIndex();

	                                    // default row 0
	                                    if (row == 0) {
	                                        setText(defaultDataWords.get(dataIndex));
	                                        return;
	                                    }

	                                    // default row 1
	                                    if (row == 1) {
	                                        setText(defaultDataWords1.get(dataIndex));
	                                        return;
	                                    }

	                                    // default row 2
	                                    if (row == 2) {
	                                        setText(defaultDataWords2.get(dataIndex));
	                                        return;
	                                    }

	                                    setText(item == null ? "" : item.toString());
	                                }

	                                @Override
	                                public void commitEdit(Object newValue) {
	                                    super.commitEdit(newValue);

	                                    int row = getIndex();
	                                    Interface1553B dto = getTableView().getItems().get(row);

	                                    // If DTO has a method to set value per column, call it:
	                                    // dto.setValue(dataIndex, newValue == null ? "" : newValue.toString());

	                                    getTableView().refresh();
	                                }
	                            });
	                    
	                    

	                    col.setEditable(true);
	                }

	// ---------- GROUP COLUMNS ----------
	modeHeader.getColumns().addAll(dtoColumns);testResultHeader.getColumns().add(modeHeader);

	interface1553BTable1DataTableView.getColumns().add(testResultHeader);interface1553BTable1DataTableView.setEditable(true);

	tableScrollPane.setContent(interface1553BTable1DataTableView);

	});

	return null;}

	@Override
	protected void failed() {
		Platform.runLater(() -> Notifications.showErrorAlert("Failed to load 1553B Interface Mode Code table"));
	}

	};

	new Thread(task).start();return tableScrollPane;}

	private Label createMergedCell(String text, boolean bold, int span) {
		Label lbl = new Label(text);
		lbl.setMinHeight(35);
		lbl.setAlignment(Pos.CENTER);
		lbl.setMaxWidth(Double.MAX_VALUE);
		lbl.setStyle("-fx-border-color: black; -fx-padding: 5;" + (bold ? "-fx-font-weight: bold;" : ""));
		return lbl;
	}

	private Label createCell(String text, boolean bold) {
		Label lbl = new Label(text);
		lbl.setMinSize(80, 35);
		lbl.setAlignment(Pos.CENTER);
		lbl.setStyle("-fx-border-color: black; -fx-padding: 5;" + (bold ? "-fx-font-weight: bold;" : ""));
		return lbl;
	}

	private void addRow(GridPane table, int rowIndex, String rowTitle, String... values) {
		Label title = createCell(rowTitle, true);
		table.add(title, 0, rowIndex);

		for (int i = 0; i < values.length; i++) {
			table.add(createCell(values[i], false), i + 1, rowIndex);
		}
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

	private HBox createPrintButtonHbox() {

		HBox printButtonContainer = new HBox(); // only create once
		printButtonContainer.setAlignment(Pos.CENTER);
		printButtonContainer.setSpacing(5);
		printButtonContainer.getChildren().addAll(printButton);

		checkButton.setOnAction(e -> {

		});

		printButton.setOnAction(e -> {

			if (uutTypeField.getValue() == null || serialnumber.getValue() == null || sessioName.getValue() == null
					|| stageName.getValue() == null) {
				Notifications.showErrorAlert("Please select all the required details to print.");
				return;
			}

			printButtonContainer.setVisible(false);
			printButtonContainer.setManaged(false);

			interface1553BMainContainerGridPane.applyCss();
			interface1553BMainContainerGridPane.layout();

			Stage stage = (Stage) interface1553BMainContainerGridPane.getScene().getWindow();

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

				interface1553BMainContainerGridPane.applyCss();
				interface1553BMainContainerGridPane.layout();

				exportPageToPDF(stage, interface1553BMainContainerGridPane);

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

				interface1553BMainContainerGridPane.applyCss();
				interface1553BMainContainerGridPane.layout();
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
