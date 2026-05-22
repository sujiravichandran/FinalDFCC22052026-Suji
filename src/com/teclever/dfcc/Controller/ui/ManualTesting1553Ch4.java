package com.teclever.dfcc.Controller.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.entities.RDF1553BCode;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.DataAnalysis1553_BManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Ch11553Table1;
import com.teclever.dfcc.model.Ch11553Table2;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
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
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

class ManualTesting1Ch4DataTableViewFactory implements TableViewFactory<Ch11553Table1> {
	@Override
	public CustomTableView<Ch11553Table1> createTableView(ObservableList<Ch11553Table1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Ch11553Table1.class, addUserColumn, addCheckboxColumn);
	}
}

class ManualTesting2Ch4DataTableViewFactory implements TableViewFactory<Ch11553Table2> {
	@Override
	public CustomTableView<Ch11553Table2> createTableView(ObservableList<Ch11553Table2> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Ch11553Table2.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTesting1553Ch4 {

	private GridPane ch1MainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private Label titleLabel = new Label("1553B INTERFACE - TIME TAG REGISTER VERIFICATION");
	private Label chLabel = new Label("DFCC CHANNEL 4");
	private Button submit = new Button("Submit");
	private Pane dynamicContent = new VBox();
	
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private String uutId = "UUT1";
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private HBox serialHbox = new HBox(10);
//	private Label dfccSNoLabel = new Label("DFCC Serial Number : ");
	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ComboBox<String> sessioName = new ComboBox<String>();
//	private Label dfccSessionName = new Label("Session Name : ");

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private TextField dfccSNoText = new TextField();
	private Label editableLabel = new Label("Click to edit");
	private TextField editField = new TextField(editableLabel.getText());

	private HBox alternateHbox = new HBox(10);
	private Label dfccNameLabel = new Label("DFCC Alternate Name : ");
	private TextField dfccNameText = new TextField();

//	private Label stageNameLabel = new Label("Stage Name");
	private ComboBox<String> stageName = new ComboBox<String>();

	private StackPane ch11553Table1StackPane = new StackPane();
	private GridPane ch11553Table1GridPane = new GridPane();

	private ObservableList<Ch11553Table1> ch11553Table1DataList = FXCollections.observableArrayList();
	private CustomTableView<Ch11553Table1> ch11553Table1DataTableView;
	private TableViewFactory<Ch11553Table1> ch11553Table1DataFactory = new ManualTesting1Ch4DataTableViewFactory();

	private StackPane ch11553Table2StackPane = new StackPane();
	private GridPane ch11553Table2GridPane = new GridPane();

	private ObservableList<Ch11553Table2> ch11553Table2DataList = FXCollections.observableArrayList();
	private CustomTableView<Ch11553Table2> ch11553Table2DataTableView;
	private TableViewFactory<Ch11553Table2> ch11553Table2DataFactory = new ManualTesting2Ch4DataTableViewFactory();

	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label(
			"NOTE: Valid Range for TTR Value is 0-65535(Decimal). If the computed Difference at STEP '02' is Negative, a Value of 65536 has to be added to account for the Wrap-around Frature.");

	private HBox tbNoHBox = new HBox();
	private Label tbNol = new Label("TB NO.:127 ");
	private Label tbNor = new Label("127");
	private Label passFail = new Label("PASS/FAIL");
	private SessionService s = new SessionService();
	private HBox testCarriedOutHBox = new HBox();
	private Label testCarriedOutBy = new Label("TEST CARRIED OUT BY:");

	private HBox dateHBox = new HBox();
	private Label date = new Label("DATE: ");
	
	private Label testRep = new Label("TESTING REP: ");
	private Label testRepDate = new Label("DATE: ");
	private Label tiqmRep = new Label("TI/QM REP: ");
	private Label tiqmRepDate = new Label("DATE: ");


	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");
	
	private Label selectedUutName = new Label();
	private Label selectedSerialName = new Label();
	private Label selectedSessionName = new Label();
	private Label selectedStageNameLabel = new Label();

	private String selectedSessionId;
	private String selectedStageName;
	private String selectedStageId;
	
	private Label tmpUutLabel;
	private Label tmpSerialLabel ;
	private Label tmpSessionLabel ;
	private Label tmpStageLabel;

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	List<SessionDto> sessionList = new ArrayList<SessionDto>();
	List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();
	
	private String selectedUutId;
	private String selectedSno;

	public ManualTesting1553Ch4() {

		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		for (SessionDto session : sessionList) {
			sessionNameId.put(session.getSessionName(), session.getSessionId());

		}
		initializeUUTTypeComboBox();
		
	
	}

	private void resetPage() {

	    dynamicContent.getChildren().clear();
	    ch11553Table2DataList.clear();

	
	}
	public GridPane createlinkFilesMainContainerGridPane() {
		ch1MainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		ch1MainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(9);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(35);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(37);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(5);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(5);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(5);



		ch1MainContainerGridPane.setHgap(10);
		ch1MainContainerGridPane.setVgap(10);

		ch1MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		ch1MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,
				sixthRow, seventhRow);
		ch1MainContainerGridPane.setPadding(new Insets(2, 2, 2, 2));

		ch1MainContainerGridPane.add(createHeadingBox(), 0, 0);
		ch1MainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		ch1MainContainerGridPane.add(dynamicContent, 0, 2);
		ch1MainContainerGridPane.add(create1553Table2Content(), 0, 3);
		ch1MainContainerGridPane.add(createNoteBox(), 0, 4);
		ch1MainContainerGridPane.add(createRepBox(), 0, 5);
		ch1MainContainerGridPane.add(createRepDateBox(), 0, 6);

		return ch1MainContainerGridPane;
	}

	private GridPane createHeadingBox() {
		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(80);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		headingHbox1.setAlignment(Pos.CENTER);
		titleLabel.setUnderline(true);
		titleLabel.getStyleClass().add("advanced-testing-title");

		headingHbox2.setAlignment(Pos.CENTER);
		chLabel.getStyleClass().add("advanced-testing-title");

		topBoxGridPane.getColumnConstraints().addAll(firstColumn);
		topBoxGridPane.getRowConstraints().addAll(firstRow, secondRow);

		headingHbox1.getChildren().add(titleLabel);
		headingHbox2.getChildren().add(chLabel);

		topBoxGridPane.add(headingHbox1, 0, 0);
		topBoxGridPane.add(headingHbox2, 0, 1);
		topBoxGridPane.add(dateGridPane(), 1, 0);
		

		return topBoxGridPane;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(10);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(15);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(50);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow);
		
		
//		selectedUutName.textProperty().bind(DFCCConstant.showUut);
//		selectedSerialName.textProperty().bind(DFCCConstant.showSerialNo);
//		selectedSessionName.textProperty().bind(DFCCConstant.showSession);
//		selectedStageNameLabel.textProperty().bind(DFCCConstant.showStage);
		
		
		
		selectedUutName.setText(DFCCConstant.showUut);
		selectedSerialName.setText(DFCCConstant.showSerialNo);
		selectedSessionName.setText(DFCCConstant.showSession);
		selectedStageNameLabel.setText(DFCCConstant.showStage);
		
		selectedUutName.getStyleClass().add("nonheading-label");
		selectedSerialName.getStyleClass().add("nonheading-label");
		selectedSessionName.getStyleClass().add("nonheading-label");
		selectedStageNameLabel.getStyleClass().add("nonheading-label");
		Platform.runLater(() -> {
		serialAlternateGridPane.add(selectedUutName, 0, 0);
		serialAlternateGridPane.add(selectedSerialName, 1, 0);
		serialAlternateGridPane.add(selectedSessionName, 2, 0);
		serialAlternateGridPane.add(selectedStageNameLabel, 3, 0);
		});


		return serialAlternateGridPane;
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

			selectedUutId = uutId;
			
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
			selectedSno = serialnumber.getSelectionModel().getSelectedItem();
			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
			initializeSessionComboBox(selectedSerialNumber);
			
			
		});
	}


	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();

//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo))
//				.collect(Collectors.toList());
		
		List<SessionDto> filterSessionList = sessionList.stream()
		        .filter(t ->
		                Objects.equals(t.getUutId(), selectedUutId) &&
		                Objects.equals(t.getDfccSNo(), selectedSno) &&
		                t.getEndDate() == null
		        )
		        .sorted(Comparator
		                .comparing(SessionDto::getUutId)
		                .thenComparing(SessionDto::getDfccSNo))
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

//	private void getStageName(String sessionId) {
//		////System.out.println("Stage Name Session id" + sessionId);
//		stageList = unitGetDetailsManagement.getStageDetailsForSession(selectedSessionId);
//		////System.out.println("stageList" + stageList);
//		for (UnitSessionDetailsDTO stageName : stageList) {
//			stageNameId.put(stageName.getStageId(), stageName.getStageName());
//		}
//
//		////System.out.println("stageNameIdstageNameIdstageNameId" + stageNameId.values());
//		stageName.setItems(FXCollections.observableArrayList(stageNameId.values()));
//
//		stageName.setOnAction((event) -> {
//			selectedStageName = stageName.getSelectionModel().getSelectedItem();
//			////System.out.println("selectedStageName" + selectedStageName);
//			create1553Table1DataTable();
//		});
//
//	}
	
	private void getStageName(String sessionId) {

	    stageList = unitGetDetailsManagement
	            .getStageDetailsForSession(selectedSessionId);

	    // IMPORTANT: clear old data to avoid accumulation
	    stageNameId.clear();
	    stageName.getItems().clear();

	    for (UnitSessionDetailsDTO dto : stageList) {
	        if (dto.getStageId() != null && dto.getStageName() != null) {
	            stageNameId.put(dto.getStageId(), dto.getStageName());
	        }
	    }

	    // Filter out null values explicitly
	    List<String> filteredStageNames = stageNameId.values()
	            .stream()
	            .filter(Objects::nonNull)
	            .distinct()
	            .toList();


	    stageName.setItems(FXCollections.observableArrayList(filteredStageNames));

	    stageName.setOnAction(event -> {

	        selectedStageName = stageName.getSelectionModel().getSelectedItem();
	        selectedStageId = stageNameId.entrySet()
	                .stream()
	                .filter(e -> Objects.equals(selectedStageName, e.getValue()))
	                .map(Map.Entry::getKey)
	                .findFirst()
	                .orElse(null);

	    });
	}

	private StackPane create1553Table1Content() {
	    StackPane pane = new StackPane();
	    pane.getStyleClass().add("tab-content-container");
	    pane.getChildren().add(ch11553TableResultGridPane());
	    return pane;
	}


	private GridPane ch11553TableResultGridPane() {

	    GridPane grid = new GridPane();

	    ColumnConstraints firstColumn = new ColumnConstraints();
	    firstColumn.setPercentWidth(100);

	    RowConstraints firstRow = new RowConstraints();
	    firstRow.setPercentHeight(100);

	    grid.getColumnConstraints().add(firstColumn);
	    grid.getRowConstraints().add(firstRow);

	    grid.add(create1553Table1DataTable(), 0, 0);

	    return grid;
	}

	public ScrollPane create1553Table1DataTable() {
		ch11553Table1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane();
		tableScrollPane.setFitToWidth(true);
		tableScrollPane.setFitToHeight(true);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				DataAnalysis1553_BManagement dataAnalysis1553_BManagement = new DataAnalysis1553_BManagement();
				List<RDF1553BCode> dataList = dataAnalysis1553_BManagement.get1553BValuesForChannel(DFCCConstant.selectedSessionId1553,
						DFCCConstant.selectedStageId1553, "ch4");


				if (dataList != null && !dataList.isEmpty()) {
					for (RDF1553BCode rdf : dataList) {
						Ch11553Table1 newCh11553T1Data = new Ch11553Table1();
						newCh11553T1Data.setStep("01");
						newCh11553T1Data.setOperation("Record TTR Value");
						newCh11553T1Data.setTtr1_I(rdf.getValueCh_1());
						newCh11553T1Data.setTtr1_II(rdf.getValueCh_2());
						newCh11553T1Data.setTtr1_III(rdf.getValueCh_3());
						newCh11553T1Data.setTtr1_IV(rdf.getValueCh_4());
						newCh11553T1Data.setTtr1_V(rdf.getValueCh_5());
						newCh11553T1Data.setTtr1_VI(rdf.getValueCh_6());
						newCh11553T1Data.setTtr1_VII(rdf.getValueCh_7());
						ch11553Table1DataList.add(newCh11553T1Data);
					}
				}

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {
					ch11553Table1DataTableView = ch11553Table1DataFactory.createTableView(ch11553Table1DataList, false,
							false);
					
					Platform.runLater(() -> {
						create1553Table2DataTable();
					
					});

					ch11553Table1DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						
						case "TTR1_I":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader = new Label("TTR4_I Contents of 003C C03C\n(Decimal)");
							}else {
							 mainHeader = new Label("TTR4_I Contents of 206C C03C\n(Decimal)");
							}
							
							Label subHeader = new Label("TEST: 6612");

							mainHeader.setWrapText(true);
							mainHeader.setAlignment(Pos.CENTER);
							mainHeader.setMinHeight(50);
							mainHeader.setPrefHeight(110);
							mainHeader.setTextAlignment(TextAlignment.CENTER);

							subHeader.setAlignment(Pos.CENTER);
							subHeader.setTextAlignment(TextAlignment.CENTER);
							subHeader.setStyle("-fx-font-size: 10px;");

							Separator separator = new Separator();
							separator.setMaxWidth(170);
							separator.setStyle("-fx-background-color: black;");

							VBox headerBox = new VBox(mainHeader, separator, subHeader);
							headerBox.setAlignment(Pos.CENTER);
							headerBox.setSpacing(2);
							headerBox.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_I"));
							break;

						case "TTR1_II":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader1;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader1 = new Label("TTR4_II Contents of 003C C07C\n(Decimal)");
							}else {
							 mainHeader1 = new Label("TTR4_II Contents of 206C C07C\n(Decimal)");
							}
							Label subHeader1 = new Label("TEST: 6628");

							mainHeader1.setWrapText(true);
							mainHeader1.setAlignment(Pos.CENTER);
							mainHeader1.setMinHeight(50);
							mainHeader1.setPrefHeight(110);
							mainHeader1.setTextAlignment(TextAlignment.CENTER);

							subHeader1.setWrapText(true);
							subHeader1.setAlignment(Pos.CENTER);
							subHeader1.setTextAlignment(TextAlignment.CENTER);
							subHeader1.setStyle("-fx-font-size: 10px;");

							Separator separator1 = new Separator();
							separator1.setMaxWidth(170);
							separator1.setStyle("-fx-background-color: black;");

							VBox headerBox1 = new VBox(mainHeader1, separator1, subHeader1);
							headerBox1.setAlignment(Pos.CENTER);
							headerBox1.setSpacing(2);
							headerBox1.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox1);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_II"));
							break;

						case "TTR1_III":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader2;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader2 = new Label("TTR4_III Contents of 003C C03BC\n(Decimal)");
							}else {
							 mainHeader2 = new Label("TTR4_III Contents of 206C C0BC\n(Decimal)");
							}
							Label subHeader2 = new Label("TEST: 6644");

							mainHeader2.setWrapText(true);
							mainHeader2.setAlignment(Pos.CENTER);
							mainHeader2.setMinHeight(50);
							mainHeader2.setPrefHeight(110);
							mainHeader2.setTextAlignment(TextAlignment.CENTER);

							subHeader2.setWrapText(true);
							subHeader2.setAlignment(Pos.CENTER);
							subHeader2.setTextAlignment(TextAlignment.CENTER);
							subHeader2.setStyle("-fx-font-size: 10px;");

							Separator separator2 = new Separator();
							separator2.setMaxWidth(170);
							separator2.setStyle("-fx-background-color: black;");

							VBox headerBox2 = new VBox(mainHeader2, separator2, subHeader2);
							headerBox2.setAlignment(Pos.CENTER);
							headerBox2.setSpacing(2);
							headerBox2.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox2);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_III"));
							break;

						case "TTR1_IV":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader3;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader3 = new Label("TTR4_IV Contents of 003C C0FC\n(Decimal)");
							}else {
							 mainHeader3 = new Label("TTR4_IV Contents of 206C C0FC\n(Decimal)");
							}
							
							Label subHeader3 = new Label("TEST: 6396");

							mainHeader3.setWrapText(true);
							mainHeader3.setAlignment(Pos.CENTER);
							mainHeader3.setMinHeight(50);
							mainHeader3.setPrefHeight(110);
							mainHeader3.setTextAlignment(TextAlignment.CENTER);

							subHeader3.setWrapText(true);
							subHeader3.setAlignment(Pos.CENTER);
							subHeader3.setTextAlignment(TextAlignment.CENTER);
							subHeader3.setStyle("-fx-font-size: 10px;");

							Separator separator3 = new Separator();
							separator3.setMaxWidth(170);
							separator3.setStyle("-fx-background-color: black;");

							VBox headerBox3 = new VBox(mainHeader3, separator3, subHeader3);
							headerBox3.setAlignment(Pos.CENTER);
							headerBox3.setSpacing(2);
							headerBox3.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox3);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_IV"));
							break;

						case "TTR1_V":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader4;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader4 = new Label("TTR4_V Contents of 306C C13C\n(Decimal)");
							}else {
							 mainHeader4 = new Label("TTR4_V Contents of 206C C13C\n(Decimal)");
							}
							Label subHeader4 = new Label("TEST: 6676");

							mainHeader4.setWrapText(true);
							mainHeader4.setAlignment(Pos.CENTER);
							mainHeader4.setMinHeight(50);
							mainHeader4.setPrefHeight(110);
							mainHeader4.setTextAlignment(TextAlignment.CENTER);

							subHeader4.setWrapText(true);
							subHeader4.setAlignment(Pos.CENTER);
							subHeader4.setTextAlignment(TextAlignment.CENTER);
							subHeader4.setStyle("-fx-font-size: 10px;");

							Separator separator4 = new Separator();
							separator4.setMaxWidth(170);
							separator4.setStyle("-fx-background-color: black;");

							VBox headerBox4 = new VBox(mainHeader4, separator4, subHeader4);
							headerBox4.setAlignment(Pos.CENTER);
							headerBox4.setSpacing(2);
							headerBox4.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox4);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_V"));
							break;

						case "TTR1_VI":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader5;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader5 = new Label("TTR4_VI Contents of 306C C17C\n(Decimal)");
							}else {
							 mainHeader5 = new Label("TTR4_VI Contents of 206C C17C\n(Decimal)");
							}
							Label subHeader5 = new Label("TEST: 6692");

							mainHeader5.setWrapText(true);
							mainHeader5.setAlignment(Pos.CENTER);
							mainHeader5.setMinHeight(50);
							mainHeader5.setPrefHeight(110);
							mainHeader5.setTextAlignment(TextAlignment.CENTER);

							subHeader5.setWrapText(true);
							subHeader5.setAlignment(Pos.CENTER);
							subHeader5.setTextAlignment(TextAlignment.CENTER);
							subHeader5.setStyle("-fx-font-size: 10px;");

							Separator separator5 = new Separator();
							separator5.setMaxWidth(170);
							separator5.setStyle("-fx-background-color: black;");

							VBox headerBox5 = new VBox(mainHeader5, separator5, subHeader5);
							headerBox5.setAlignment(Pos.CENTER);
							headerBox5.setSpacing(2);
							headerBox5.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox5);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_VI"));
							break;

						case "TTR1_VII":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label  mainHeader6;
							if(uutId.equalsIgnoreCase("UUT1")) {
								mainHeader6 = new Label("TTR4_VII Contents of 306C C1BC\n(Decimal)");
							}else {
							 mainHeader6 = new Label("TTR4_VII Contents of 206C C1BC\n(Decimal)");
							}
							Label subHeader6 = new Label("TEST: 6708");

							mainHeader6.setWrapText(true);
							mainHeader6.setMinHeight(50);
							mainHeader6.setPrefHeight(110);
							mainHeader6.setAlignment(Pos.CENTER);
							mainHeader6.setTextAlignment(TextAlignment.CENTER);

							subHeader6.setWrapText(true);
							subHeader6.setAlignment(Pos.CENTER);
							subHeader6.setTextAlignment(TextAlignment.CENTER);
							subHeader6.setStyle("-fx-font-size: 10px;");

							Separator separator6 = new Separator();
							separator6.setMaxWidth(170);
							separator6.setStyle("-fx-background-color: black;");

							VBox headerBox6 = new VBox(mainHeader6, separator6, subHeader6);
							headerBox6.setAlignment(Pos.CENTER);
							headerBox6.setSpacing(2);
							headerBox6.setPadding(new Insets(4, 2, 4, 2));

							column.setGraphic(headerBox6);
							column.setCellValueFactory(new PropertyValueFactory<>("ttr1_VII"));
							break;

						default:
//							column.setMinWidth(120);
//							column.setMaxWidth(120);
							break;
						}

					});

					// ✅ Add TableView to ScrollPane
					tableScrollPane.setContent(ch11553Table1DataTableView);

					if (ch11553Table1DataList.isEmpty()) {
						tableScrollPane.setFitToWidth(true);
					}
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}


	private StackPane create1553Table2Content() {
		ch11553Table2StackPane.getStyleClass().add("tab-content-container");
		ch11553Table2StackPane.getChildren().clear();
		ch11553Table2StackPane.getChildren().add(ch11553Table2ResultGridPane());
		return ch11553Table2StackPane;
	}

	private GridPane ch11553Table2ResultGridPane() {
//		ch11553Table1GridPane.getStyleClass().add("current-execution-result-tabs-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ch11553Table2GridPane.setPadding(new Insets(5));

		ch11553Table2GridPane.getColumnConstraints().addAll(firstColumn);
		ch11553Table2GridPane.getRowConstraints().addAll(firstRow);

		ch11553Table2GridPane.add(create1553Table2DataTable(), 0, 0);
		return ch11553Table2GridPane;
	}

	public ScrollPane create1553Table2DataTable() {
		ch11553Table2DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane();
		tableScrollPane.setFitToHeight(true);
		tableScrollPane.setFitToWidth(true);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				DataAnalysis1553_BManagement dataAnalysis1553_BManagement = new DataAnalysis1553_BManagement();
				List<RDF1553BCode> dataList = dataAnalysis1553_BManagement.get1553BValuesForChannel(DFCCConstant.selectedSessionId1553,
						DFCCConstant.selectedStageId1553, "ch4");
				if (dataList != null && !dataList.isEmpty()) {
					for (RDF1553BCode rdf : dataList) {
						Ch11553Table2 newCh11553T2Data = new Ch11553Table2();
						newCh11553T2Data.setStep("02");
						newCh11553T2Data.setOperation("Compute Difference");
						newCh11553T2Data.setTTR1I(rdf.getDiff_1and2());
						newCh11553T2Data.setTTR1II(rdf.getDiff_2and3());
						newCh11553T2Data.setTTR1III(rdf.getDiff_3and4());
						newCh11553T2Data.setTTR1IV(rdf.getDiff_4and5());
						newCh11553T2Data.setTTR1V(rdf.getDiff_5and6());
						newCh11553T2Data.setTTR1VI(rdf.getDiff_6and7());

						ch11553Table2DataList.add(newCh11553T2Data);

						Ch11553Table2 resRow = new Ch11553Table2();
						resRow.setStep("03");
						resRow.setOperation("DECLARE:\n(P: Pass; F: Fail)\n P:Diff = 312 / 313 \n F: OTHER-WISE");

						resRow.setTTR1I(rdf.getRes_1and2());
						resRow.setTTR1II(rdf.getRes_2and3());
						resRow.setTTR1III(rdf.getRes_3and4());
						resRow.setTTR1IV(rdf.getRes_4and5());
						resRow.setTTR1V(rdf.getRes_5and6());
						resRow.setTTR1VI(rdf.getRes_6and7());
						ch11553Table2DataList.add(resRow);
					}
				}

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {
					ch11553Table2DataTableView = ch11553Table2DataFactory.createTableView(ch11553Table2DataList, false,
							false);
					
					 ch11553Table2DataTableView.setColumnResizePolicy(
							  ch11553Table2DataTableView.UNCONSTRAINED_RESIZE_POLICY
		                );

					ch11553Table2DataTableView.getColumns().forEach(column -> {
						switch (column.getText()) {
						case "OPERATION":
							column.setMinWidth(300);
							column.setMaxWidth(300);
							break;
						
						case "TTR1I":
							column.setText("TTR1_II - TTR1_I");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						case "TTR1II":
							column.setText("TTR1_III - TTR1_II");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						case "TTR1III":
							column.setText("TTR1_IV - TTR1_III");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						case "TTR1IV":
							column.setText("TTR1_V - TTR1_IV");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						case "TTR1V":
							column.setText("TTR1_VI - TTR1_V");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						case "TTR1VI":
							column.setText("TTR1_VII - TTR1_VI");
							column.setMinWidth(200);
							column.setMaxWidth(200);
							break;
						default:
							break;
						}
					});

					ch11553Table2DataTableView.setFixedCellSize(-1); // allow dynamic row height

					// Assuming your table has a column named "operation"
					TableColumn<Ch11553Table2, String> operationColumn = (TableColumn<Ch11553Table2, String>) ch11553Table2DataTableView
							.getColumns().stream().filter(c -> c.getText().equalsIgnoreCase("operation")).findFirst()
							.orElse(null);

					if (operationColumn != null) {
						// Enable wrapping inside the cell
						operationColumn.setCellFactory(col -> new TableCell<Ch11553Table2, String>() {
							private final Text text = new Text();

							{
								text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
								setGraphic(text);
							}

							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty || item == null) {
									text.setText(null);
									setGraphic(null);
								} else {
									text.setText(item);
									setGraphic(text);
								}
							}
						});
					}

					// Allow variable row heights
					ch11553Table2DataTableView.setFixedCellSize(-1);

					// Optionally: increase row height for DECLARE rows
					ch11553Table2DataTableView.setRowFactory(tv -> new TableRow<Ch11553Table2>() {
						@Override
						protected void updateItem(Ch11553Table2 item, boolean empty) {
							super.updateItem(item, empty);
							if (empty || item == null) {
								setPrefHeight(Control.USE_COMPUTED_SIZE);
								return;
							}
							if (item.getOperation() != null && item.getOperation().startsWith("DECLARE")) {
								setPrefHeight(80); // Adjust this value as needed
							} else {
								setPrefHeight(Control.USE_COMPUTED_SIZE);
							}
						}
					});

					// Set table into scroll pane
					tableScrollPane.setContent(ch11553Table2DataTableView);

					if (ch11553Table2DataList.isEmpty()) {
						tableScrollPane.setFitToWidth(true);
					}
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};

		new Thread(task).start();

		return tableScrollPane;
	}

	private GridPane noteGridPane() {
		GridPane noteGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		noteGridPane.getColumnConstraints().add(firstColumn);
		noteGridPane.getRowConstraints().add(firstRow);

		noteGridPane.setPadding(new Insets(5));
		noteLabel.setWrapText(true);

		noteHBox.getChildren().add(noteLabel);
		noteHBox.setAlignment(Pos.CENTER_LEFT);

		noteGridPane.add(noteHBox, 0, 0);

		return noteGridPane;
	}
	
	private GridPane createNoteBox() {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(70);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(20);

		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(10);

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

	private GridPane tbGridPane() {
		GridPane tbGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		tbGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		tbGridPane.getRowConstraints().add(firstRow);

		tbNol.getStyleClass().add("nonheading-label");
		tbNor.getStyleClass().add("nonheading-label");
		passFail.getStyleClass().add("nonheading-label");

		tbNoHBox.getChildren().addAll(tbNol, tbNor, passFail);

//	    tbGridPane.setPadding(new Insets(5));
		tbNoHBox.setAlignment(Pos.CENTER_RIGHT);

		tbGridPane.add(tbNoHBox, 1, 0);

		return tbGridPane;
	}

	private GridPane testCarriedOutGridPane() {
		GridPane testCarriedOutGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testCarriedOutGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		testCarriedOutGridPane.getRowConstraints().add(firstRow);

//	    testCarriedOutGridPane.setPadding(new Insets(10));
//	    testCarriedOutGridPane.setHgap(10);

		testCarriedOutBy.getStyleClass().add("nonheading-label");

		Label imageLabel = new Label("Click to Select Image");
		imageLabel.setPrefSize(150, 50);
		imageLabel.setAlignment(Pos.CENTER);
		imageLabel.setStyle("-fx-border-color: gray; " + "-fx-border-width: 2; " + "-fx-background-color: #f5f5f5; "
				+ "-fx-text-fill: #666;");

		// When user clicks label → open file chooser and display image
		imageLabel.setOnMouseClicked(e -> openImageFileChooser(imageLabel));

		// --- Add components to layout ---
		testCarriedOutHBox.getChildren().addAll(testCarriedOutBy);
		testCarriedOutHBox.setAlignment(Pos.CENTER_RIGHT);
		testCarriedOutHBox.setSpacing(10);

		testCarriedOutGridPane.add(testCarriedOutHBox, 1, 0);

		return testCarriedOutGridPane;
	}

	private void openImageFileChooser(Label imageLabel) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select an Image");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

		File selectedFile = fileChooser.showOpenDialog(imageLabel.getScene().getWindow());

		if (selectedFile != null) {
			Image image = new Image(selectedFile.toURI().toString());

			ImageView imageView = new ImageView(image);
			imageView.setFitWidth(imageLabel.getWidth());
			imageView.setFitHeight(imageLabel.getHeight());
			imageView.setPreserveRatio(true);

			imageLabel.setGraphic(imageView);
			imageLabel.setText(null); // remove placeholder text
			imageLabel.setStyle("-fx-border-color: gray; -fx-border-width: 2;"); // optional: keep border
		}
	}
	
	private void exportPageToPDF(Stage stage, GridPane root) {
	    FileChooser chooser = new FileChooser();
	    chooser.setTitle("Save PDF");
	    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

	    File file = chooser.showSaveDialog(stage);
	    if (file == null) return;

	    try {
	        // Wrap text for session name label (or any labels you need)
	        root.getChildren().forEach(node -> {
	            if (node instanceof Label label) {
	                label.setWrapText(true);   // Enable wrap text
	                label.setMaxWidth(300);    // Set max width so text wraps properly
	            }
	        });

	        // Take snapshot of the GridPane at higher resolution
	        SnapshotParameters params = new SnapshotParameters();
	        params.setTransform(new Scale(2, 2)); // 2x resolution
	        WritableImage fxImage = root.snapshot(params, null);

	        // Convert JavaFX WritableImage → BufferedImage
	        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

	        // Convert BufferedImage → byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedImage, "png", baos);
	        byte[] imageBytes = baos.toByteArray();

	        // Create iText ImageData from byte array
	        com.itextpdf.io.image.ImageData imgData = com.itextpdf.io.image.ImageDataFactory.create(imageBytes);
	        com.itextpdf.layout.element.Image pdfImage = new com.itextpdf.layout.element.Image(imgData);

	        // Create PDF writer and document
	        PdfWriter writer = new PdfWriter(file.getAbsolutePath());
	        PdfDocument pdfDoc = new PdfDocument(writer);
	        Document document = new Document(pdfDoc, PageSize.A3.rotate());

	        // Fit image to page
	        pdfImage.scaleToFit(PageSize.A3.rotate().getWidth(), PageSize.A3.rotate().getHeight());
	        pdfImage.setAutoScale(true);

	        // Add image to PDF
	        document.add(pdfImage);
	        document.close();

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        Notifications.showErrorAlert("Error generating PDF: " + ex.getMessage());
	    }
	}

	private void addLabelToParent(Label original, Label tmp) {
	    if (original.getParent() instanceof GridPane grid) {
	        Integer col = GridPane.getColumnIndex(original);
	        Integer row = GridPane.getRowIndex(original);
	        col = (col == null) ? 0 : col;
	        row = (row == null) ? 0 : row;
	        grid.add(tmp, col, row);
	    } else if (original.getParent() instanceof Pane pane) {
	        pane.getChildren().add(tmp);
	    }
	}

	private void removeLabelFromParent(Label original, Label tmp) {
	    if (original.getParent() instanceof GridPane grid) {
	        grid.getChildren().remove(tmp);
	    } else if (original.getParent() instanceof Pane pane) {
	        pane.getChildren().remove(tmp);
	    }
	}

	private GridPane dateGridPane() {
		GridPane dateGridPane = new GridPane();
		DatePicker datePicker = new DatePicker(LocalDate.now()); // Default to today
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		datePicker.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				if (date.isAfter(LocalDate.now())) {
					setDisable(true);
					setStyle("-fx-background-color: #E0E0E0;"); // Gray-out future dates
				}
			}
		});

		datePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				return (date != null) ? dateFormatter.format(date) : "";
			}

			@Override
			public LocalDate fromString(String string) {
				return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
			}
		});

		datePicker.setPromptText("dd-MM-yyyy");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		

		dateGridPane.getColumnConstraints().addAll(firstColumn);
		dateGridPane.getRowConstraints().add(firstRow);

		date.getStyleClass().add("nonheading-label");
		printButtonHBox.setAlignment(Pos.CENTER_RIGHT);
		
		
		printButton.setOnAction(event -> {

		    // 1️⃣ Validation
		    if (selectedUutName.getText() == null || selectedSerialName.getText() == null
		            || selectedSessionName.getText() == null || selectedStageNameLabel.getText() == null) {
		        Notifications.showErrorAlert("Please select all the required details to save.");
		        return;
		    }
		    
		    if (ch11553Table1DataList.isEmpty()) {
		        Notifications.showErrorAlert("No data available to export.");
		        return;
		    }

		    // 2️⃣ Hide Print Button Section
		    printButtonHBox.setVisible(false);
		    printButtonHBox.setManaged(false);

		    ch1MainContainerGridPane.applyCss();
		    ch1MainContainerGridPane.layout();

		    Stage stage = (Stage) ch1MainContainerGridPane.getScene().getWindow();

		    // 3️⃣ Create Temporary Labels (with wrap for long text)

			 tmpUutLabel = new Label("UUT Type : " + 
			            (DFCCConstant.showUut));

			    tmpSerialLabel = new Label("Serial No : " + 
			            (DFCCConstant.showSerialNo));

			    tmpSessionLabel = new Label("Session Name : " + 
			            (DFCCConstant.showSession));

			    tmpStageLabel = new Label("Stage Name : " + 
			            (DFCCConstant.showStage));

		    tmpUutLabel.getStyleClass().add("nonheading-label");
		    tmpSerialLabel.getStyleClass().add("nonheading-label");
		    tmpSessionLabel.getStyleClass().add("nonheading-label");
		    tmpStageLabel.getStyleClass().add("nonheading-label");

		    // Wrap text for long session names
		    tmpSessionLabel.setWrapText(true);

		    try {
		        // 4️⃣ Hide original Labels
		        selectedUutName.setVisible(false);
		        selectedSerialName.setVisible(false);
		        selectedSessionName.setVisible(false);
		        selectedStageNameLabel.setVisible(false);

		        // 5️⃣ Add temporary labels to the same parent
		        addLabelToParent(selectedUutName, tmpUutLabel);
		        addLabelToParent(selectedSerialName, tmpSerialLabel);
		        addLabelToParent(selectedSessionName, tmpSessionLabel);
		        addLabelToParent(selectedStageNameLabel, tmpStageLabel);

		        ch1MainContainerGridPane.applyCss();
		        ch1MainContainerGridPane.layout();

		        // 6️⃣ Export to PDF
		        exportPageToPDF(stage, ch1MainContainerGridPane);
		        Notifications.showSuccessAlert("Report has been downloaded successfully");

		    } catch (Exception ex) {
		        ex.printStackTrace();
		        Notifications.showErrorAlert("Failed to export PDF");
		    } finally {
		        // 7️⃣ Restore UI
		        removeLabelFromParent(selectedUutName, tmpUutLabel);
		        removeLabelFromParent(selectedSerialName, tmpSerialLabel);
		        removeLabelFromParent(selectedSessionName, tmpSessionLabel);
		        removeLabelFromParent(selectedStageNameLabel, tmpStageLabel);

		        selectedUutName.setVisible(true);
		        selectedSerialName.setVisible(true);
		        selectedSessionName.setVisible(true);
		        selectedStageNameLabel.setVisible(true);

		        printButtonHBox.setVisible(true);
		        printButtonHBox.setManaged(true);

		        ch1MainContainerGridPane.applyCss();
		        ch1MainContainerGridPane.layout();
		    }
		});
	    
		
		 HBox submitHBox = new HBox();
		    submitHBox.setAlignment(Pos.CENTER);
		    
		    submitHBox.getChildren().add(submit);

			submit.setOnAction(event -> {
			    Platform.runLater(() -> {
			        GridPane serialGrid = createSerialAlternateGridePane();

			        dynamicContent.getChildren().clear();

			        dynamicContent.getChildren().add(serialGrid);

			        StackPane popup = create1553Table1Content();
			        dynamicContent.getChildren().add(popup);

			        VBox.setVgrow(popup, Priority.ALWAYS);
			    });
			});

		printButtonHBox.getChildren().addAll(submitHBox, printButton);
		printButtonHBox.setSpacing(5);

		dateHBox.setAlignment(Pos.CENTER_RIGHT);

		dateGridPane.add(printButtonHBox, 0, 0);
		dateGridPane.add(dateHBox, 1, 0);

		return dateGridPane;
	}

}
