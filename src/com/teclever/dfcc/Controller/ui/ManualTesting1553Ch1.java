package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.entities.RDF1553BCode;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.DataAnalysis1553_BManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.model.Ch11553Table1;
import com.teclever.dfcc.model.Ch11553Table2;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

class ManualTesting1Ch1DataTableViewFactory implements TableViewFactory<Ch11553Table1> {
	@Override
	public CustomTableView<Ch11553Table1> createTableView(ObservableList<Ch11553Table1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Ch11553Table1.class, addUserColumn, addCheckboxColumn);
	}
}

class ManualTesting2Ch1DataTableViewFactory implements TableViewFactory<Ch11553Table2> {
	@Override
	public CustomTableView<Ch11553Table2> createTableView(ObservableList<Ch11553Table2> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Ch11553Table2.class, addUserColumn, addCheckboxColumn);
	}
}

public class ManualTesting1553Ch1 {

	private GridPane ch1MainContainerGridPane = new GridPane();

	private HBox headingHbox1 = new HBox(10);
	private HBox headingHbox2 = new HBox(10);
	private Label titleLabel = new Label("1553B INTERFACE - TIME TAG REGISTER VERIFICATION");
	private Label chLabel = new Label("DFCC CHANNEL 1");

	private HBox serialHbox = new HBox(10);
	private Label dfccSNoLabel = new Label("DFCC Serial Number : ");
	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ComboBox<String> sessioName = new ComboBox<String>();
	private Label dfccSessionName = new Label("Session Name : ");

	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private TextField dfccSNoText = new TextField();
	private Label editableLabel = new Label("Click to edit");
	private TextField editField = new TextField(editableLabel.getText());

	private HBox alternateHbox = new HBox(10);
	private Label dfccNameLabel = new Label("DFCC Alternate Name : ");
	private TextField dfccNameText = new TextField();

	private Label stageNameLabel = new Label("Stage Name");
	private ComboBox<String> stageName = new ComboBox<String>();

	private StackPane ch11553Table1StackPane = new StackPane();
	private GridPane ch11553Table1GridPane = new GridPane();

	private ObservableList<Ch11553Table1> ch11553Table1DataList = FXCollections.observableArrayList();
	private CustomTableView<Ch11553Table1> ch11553Table1DataTableView;
	private TableViewFactory<Ch11553Table1> ch11553Table1DataFactory = new ManualTesting1Ch1DataTableViewFactory();

	private StackPane ch11553Table2StackPane = new StackPane();
	private GridPane ch11553Table2GridPane = new GridPane();

	private ObservableList<Ch11553Table2> ch11553Table2DataList = FXCollections.observableArrayList();
	private CustomTableView<Ch11553Table2> ch11553Table2DataTableView;
	private TableViewFactory<Ch11553Table2> ch11553Table2DataFactory = new ManualTesting2Ch1DataTableViewFactory();

	private HBox noteHBox = new HBox();
	private Label noteLabel = new Label(
			"NOTE: Valid Range for TTR Value is 0-65535(Decimal). If the computed Difference at STEP '02' is Negative, a Value of 65536 has to be added to account for the Wrap-around Frature.");

	private HBox tbNoHBox = new HBox();
	private Label tbNol = new Label("TB NO.:");
	private Label tbNor = new Label("127");
	private Label passFail = new Label("PASS/FAIL");
	private SessionService s = new SessionService();
	private HBox testCarriedOutHBox = new HBox();
	private Label testCarriedOutBy = new Label("TEST CARRIED OUT BY:");

	private HBox dateHBox = new HBox();
	private Label date = new Label("DATE: ");

	private HBox printButtonHBox = new HBox();
	private Button printButton = new Button("Print");

	private String selectedSessionId;
	private String selectedStageName;

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	List<SessionDto> sessionList = new ArrayList<SessionDto>();
	List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();

	public ManualTesting1553Ch1() {

		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

		for (SessionDto session : sessionList) {
			sessionNameId.put(session.getSessionName(), session.getSessionId());

		}
		getSerialNo();
	}

	public GridPane createlinkFilesMainContainerGridPane() {
		ch1MainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		ch1MainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(15);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(5);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(25);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(25);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(5);

		RowConstraints sixthRow = new RowConstraints();
		sixthRow.setPercentHeight(5);

		RowConstraints seventhRow = new RowConstraints();
		seventhRow.setPercentHeight(10);

		RowConstraints eighthRow = new RowConstraints();
		eighthRow.setPercentHeight(10);

		ch1MainContainerGridPane.setHgap(10);
		ch1MainContainerGridPane.setVgap(10);

		ch1MainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		ch1MainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,
				sixthRow, seventhRow, eighthRow);
		ch1MainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		ch1MainContainerGridPane.add(createHeadingBox(), 0, 0);
		ch1MainContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);
		ch1MainContainerGridPane.add(create1553Table1Content(), 0, 2);
		ch1MainContainerGridPane.add(create1553Table2Content(), 0, 3);
		ch1MainContainerGridPane.add(noteGridPane(), 0, 4);
		ch1MainContainerGridPane.add(tbGridPane(), 0, 5);
		ch1MainContainerGridPane.add(testCarriedOutGridPane(), 0, 6);
		ch1MainContainerGridPane.add(dateGridPane(), 0, 7);

		return ch1MainContainerGridPane;
	}

	private GridPane createHeadingBox() {
		GridPane topBoxGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

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

		return topBoxGridPane;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow);

		dfccSNoLabel.getStyleClass().add("nonheading-label");

		HBox serialHbox = new HBox(10);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);

		dfccSessionName.getStyleClass().add("nonheading-label");
		sessionHbox.getChildren().addAll(dfccSessionName, sessioName);

		serialHbox.getChildren().addAll(dfccSNoLabel, serialnumber);

		dfccNameLabel.getStyleClass().add("nonheading-label");

		HBox alternateHbox = new HBox(10);
		alternateHbox.setAlignment(Pos.CENTER_RIGHT);
		alternateHbox.getChildren().addAll(dfccNameLabel, dfccNameText);

		HBox stageNameHbox = new HBox(10);
		stageNameHbox.setAlignment(Pos.CENTER_RIGHT);
		stageNameLabel.getStyleClass().add("nonheading-label");
		stageNameHbox.getChildren().addAll(stageNameLabel, stageName);

		serialAlternateGridPane.add(serialHbox, 0, 0);
		serialAlternateGridPane.add(sessionHbox, 1, 0);
		serialAlternateGridPane.add(stageNameHbox, 2, 0);
//		serialAlternateGridPane.add(alternateHbox, 3, 0);

		GridPane.setHgrow(serialHbox, Priority.ALWAYS);
		GridPane.setHgrow(alternateHbox, Priority.ALWAYS);

		return serialAlternateGridPane;
	}

	private void getSerialNo() {
		List serailNumber = FXCollections.observableArrayList(unitGetDetailsManagement.getAllDfccSerialNo());
		serialnumber.setItems((ObservableList<String>) serailNumber);

		serialnumber.setOnAction((event) -> {

			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
			System.out.println("selectedSerialNumber" + selectedSerialNumber);
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
		System.out.println("Stage Name Session id" + sessionId);
		stageList = unitGetDetailsManagement.getStageDetailsForSession(selectedSessionId);
		System.out.println("stageList" + stageList);
		for (UnitSessionDetailsDTO stageName : stageList) {
			stageNameId.put(stageName.getStageId(), stageName.getStageName());
		}

		System.out.println("stageNameIdstageNameIdstageNameId" + stageNameId.values());
		stageName.setItems(FXCollections.observableArrayList(stageNameId.values()));

		stageName.setOnAction((event) -> {
			selectedStageName = stageName.getSelectionModel().getSelectedItem();
			System.out.println("selectedStageName" + selectedStageName);
			create1553Table1DataTable();
		});

	}

	private StackPane create1553Table1Content() {
		ch11553Table1StackPane.getStyleClass().add("tab-content-container");
		ch11553Table1StackPane.getChildren().clear();
		ch11553Table1StackPane.getChildren().add(ch11553TableResultGridPane());
		return ch11553Table1StackPane;
	}

	private GridPane ch11553TableResultGridPane() {
//		ch11553Table1GridPane.getStyleClass().add("current-execution-result-tabs-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		ch11553Table1GridPane.setPadding(new Insets(5));

		ch11553Table1GridPane.getColumnConstraints().addAll(firstColumn);
		ch11553Table1GridPane.getRowConstraints().addAll(firstRow);

		ch11553Table1GridPane.add(create1553Table1DataTable(), 0, 0);
		return ch11553Table1GridPane;
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
				System.out.println("SESSION NAME" + selectedSessionId);
				List<RDF1553BCode> dataList = dataAnalysis1553_BManagement.get1553BValuesForChannel(selectedSessionId,
						selectedStageName, "ch1");

				System.out.println("Data list size: " + dataList.size());

				if (dataList != null && !dataList.isEmpty()) {
					for (RDF1553BCode rdf : dataList) {
						Ch11553Table1 newCh11553T1Data = new Ch11553Table1();
						newCh11553T1Data.setStep("");
						newCh11553T1Data.setOperation("");
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

					ch11553Table1DataTableView.getColumns().forEach(column -> {
						String colName = column.getText();

						switch (colName) {
						
						case "TTR1_I":
							column.setText(null);
							column.setMinWidth(130);
							column.setMaxWidth(132);
							Label mainHeader = new Label("TTR1_I Contents of 003C C03C(Decimal)");
							Label subHeader = new Label("TEST: 6216");

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
							Label mainHeader1 = new Label("TTR1_I Contents of 003C C07C(Decimal)");
							Label subHeader1 = new Label("TEST: 6232");

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
							Label mainHeader2 = new Label("TTR1_I Contents of 003C C03C(Decimal)");
							Label subHeader2 = new Label("TEST: 6248");

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
							Label mainHeader3 = new Label("TTR1_I Contents of 003C C03C(Decimal)");
							Label subHeader3 = new Label("TEST: 6264");

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
							Label mainHeader4 = new Label("TTR1_I Contents of 003C C13C(Decimal)");
							Label subHeader4 = new Label("TEST: 6280");

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
							Label mainHeader5 = new Label("TTR1_I Contents of 003C C17C(Decimal)");
							Label subHeader5 = new Label("TEST: 6296");

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
							Label mainHeader6 = new Label("TTR1_I Contents of 003C C1BC(Decimal)");
							Label subHeader6 = new Label("TEST: 6312");

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
				List<RDF1553BCode> dataList = dataAnalysis1553_BManagement.get1553BValuesForChannel(selectedSessionId,
						selectedStageName, "ch1");
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

					ch11553Table2DataTableView.getColumns().forEach(column -> {
						switch (column.getText()) {
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
		testCarriedOutHBox.getChildren().addAll(testCarriedOutBy, imageLabel);
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
		System.out.println("datePicker" + datePicker);
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		dateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		dateGridPane.getRowConstraints().add(firstRow);

		date.getStyleClass().add("nonheading-label");
		printButtonHBox.setAlignment(Pos.CENTER_RIGHT);

		printButtonHBox.getChildren().add(printButton);

		dateHBox.getChildren().addAll(date, datePicker);

		dateHBox.setAlignment(Pos.CENTER_RIGHT);

		dateGridPane.add(printButtonHBox, 0, 0);
		dateGridPane.add(dateHBox, 1, 0);

		return dateGridPane;
	}

}
