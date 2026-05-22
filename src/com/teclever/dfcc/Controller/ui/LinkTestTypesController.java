package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.FailureTypeStatus;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitGetDetailsManagement;
import com.teclever.dfcc.advanceddataanalysis.UnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.LinkTestType;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

class LinkTestTypeDataTableViewFactory implements TableViewFactory<LinkTestType> {
	@Override
	public CustomTableView<LinkTestType> createTableView(ObservableList<LinkTestType> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, LinkTestType.class, addUserColumn, addCheckboxColumn);
	}
}

public class LinkTestTypesController {

	private GridPane linkFilesMainContainerGridPane = new GridPane();
	private GridPane testTypeContainerGridPane = new GridPane();

	private ObservableList<LinkTestType> linkTestTypeTable1DataList = FXCollections.observableArrayList();
	private CustomTableView<LinkTestType> linkTestTypeTable1DataTableView;
	private TableViewFactory<LinkTestType> linkTestTypeTable1DataFactory = new LinkTestTypeDataTableViewFactory();

	private HBox headingHbox = new HBox(10);

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();

	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private Button submit = new Button("Submit");
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String uutId;

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label(" Link Fail Type");
	private SessionService s = new SessionService();

	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();

	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ComboBox<String> sessioName = new ComboBox<String>();
	private ComboBox<String> stageName = new ComboBox<String>();

	private String selectedSessionId;
	private String selectedStageName;

	private boolean addGetFlag = false;

	private UnitGetDetailsManagement unitGetDetailsManagement = new UnitGetDetailsManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private Map<String, String> stageNameId = new HashMap<String, String>();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<UnitSessionDetailsDTO> stageList = new ArrayList<UnitSessionDetailsDTO>();

	private Button saveButton = new Button("Save");
	private Button fetchButton = new Button("Fetch");
	private String selectedStageId;

	private String selectedUttId;
	private String selectedSNo;
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	
	private TrailSessionEntityService t = new TrailSessionEntityService();
	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();

	public LinkTestTypesController() {
		  unitDataList.clear();

		    // Normal sessions
		    SessionResponse s1 = s.getAllSession();
		    sessionList = s1.getListOfSession();

		    // Trial sessions
		    TrailSessionResponse t1 = t.getActiveTrailSessionId();
		    sessionListTrail = t1.getListOfSession();

	}

	public GridPane createlinkTestTypesMainContainerGridPane() {
		initializeUUTTypeComboBox();
		linkFilesMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		linkFilesMainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		linkFilesMainContainerGridPane.setHgap(10);
		linkFilesMainContainerGridPane.setVgap(10);

		linkFilesMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		linkFilesMainContainerGridPane.getRowConstraints().addAll(firstRow);
		linkFilesMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

		linkFilesMainContainerGridPane.add(createCumulativeHoursGridPane(), 0, 0);

		return linkFilesMainContainerGridPane;
	}

	private GridPane createCumulativeHoursGridPane() {

		testTypeContainerGridPane.setHgap(5);
		testTypeContainerGridPane.setVgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(10);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(83);

		testTypeContainerGridPane.getColumnConstraints().addAll(firstColumn);
		testTypeContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		testTypeContainerGridPane.add(headingGridPane(), 0, 0);
		testTypeContainerGridPane.add(createSerialAlternateGridePane(), 0, 1);

		if (addGetFlag) {
			testTypeContainerGridPane.add(createGetLinkTestTypeDataTable(), 0, 2);
		} else {
			testTypeContainerGridPane.add(createLinkTestTypeDataTable(), 0, 2);
		}

		return testTypeContainerGridPane;
	}

	public GridPane headingGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);
		headingGridPane.getStyleClass().add("dataanalysis-testing-build-save");
		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		HBox headingHbox = new HBox(10);
		headingHbox.getStyleClass().add("dataanalysis-testing-second-container");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private GridPane createSerialAlternateGridePane() {
		GridPane serialAlternateGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(16);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(16);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(16);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(16);

		ColumnConstraints fivthColumn = new ColumnConstraints();
		fivthColumn.setPercentWidth(16);

		ColumnConstraints sixthColumn = new ColumnConstraints();
		sixthColumn.setPercentWidth(16);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		serialAlternateGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fivthColumn, sixthColumn);
		serialAlternateGridPane.getRowConstraints().addAll(firstRow);

		HBox uutHbox = new HBox(10);
		uutHbox.setAlignment(Pos.CENTER_LEFT);
		uutHbox.setFillHeight(true);
		uutTypeField.setPromptText("select UUT Type");
		serialnumber.setPromptText("select Serial No");
		serialnumber.setEditable(true);
		sessioName.setPromptText("select Session");
		sessioName.setEditable(true);
		stageName.setPromptText("select Stage Name");
		stageName.setEditable(true);

		uutHbox.getChildren().add(uutTypeField);

		HBox serialHbox = new HBox(10);
		serialHbox.setAlignment(Pos.CENTER_LEFT);
		serialHbox.setFillHeight(true);

		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);

		sessionHbox.getChildren().addAll(sessioName);

		serialHbox.getChildren().addAll(serialnumber);

		HBox stageNameHbox = new HBox(10);
		stageNameHbox.setAlignment(Pos.CENTER_RIGHT);
		stageNameHbox.getChildren().addAll(stageName);

		serialAlternateGridPane.add(uutHbox, 0, 0);
		serialAlternateGridPane.add(serialHbox, 1, 0);
		serialAlternateGridPane.add(sessionHbox, 2, 0);
		serialAlternateGridPane.add(stageNameHbox, 3, 0);
		serialAlternateGridPane.add(createPrintButtonHbox(), 4, 0);
		serialAlternateGridPane.add(createFetchButtonHbox(), 5, 0);

		GridPane.setHgrow(serialHbox, Priority.ALWAYS);

		return serialAlternateGridPane;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {
			stageName.getSelectionModel().clearSelection();
			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			uutId = fetchUutId(selectedUUTType);
			initalizeSerialNoComboBox(uutId);
			addGetFlag = false;

			selectedUttId = uutId;

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

//	private void initalizeSerialNoComboBox(String uutId) {
//		dfccSNList.clear();
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutId))
//				.collect(Collectors.toList());
//
//		Set<String> seenDfccSNos = new HashSet<>();
//
//		for (SessionDto dfccSn : filterSessionList) {
//			String dfccSNo = dfccSn.getDfccSNo();
//			if (seenDfccSNos.add(dfccSNo)) {
//				dfccSNList.add(dfccSNo);
//			}
//		}
//		serialnumber.setItems(dfccSNList);
//		serialnumber.setOnAction((event) -> {
//
//			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
//			selectedSNo = serialnumber.getSelectionModel().getSelectedItem();
//			initializeSessionComboBox(selectedSerialNumber);
//
//		});
//	}
	
	private void initalizeSerialNoComboBox(String uutTypeId) {

	    dfccSNList.clear();
	    Set<String> seenDfccSNos = new HashSet<>();

	    if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {

	        List<SessionDto> filterSessionList = sessionList.stream()
	                .filter(t -> t.getUutId().equals(uutTypeId))
	                .collect(Collectors.toList());

	        for (SessionDto dfccSn : filterSessionList) {
	            String dfccSNo = dfccSn.getDfccSNo();
	            if (seenDfccSNos.add(dfccSNo)) {
	                dfccSNList.add(dfccSNo);
	            }
	        }

	    } else {

	        List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
	                .filter(t -> t.getUutId().equals(uutTypeId))
	                .collect(Collectors.toList());
	        for (TrailSessionDto dfccSn : filterSessionList) {
	            String dfccSNo = dfccSn.getDfccSNo();
	            if (seenDfccSNos.add(dfccSNo)) {
	                dfccSNList.add(dfccSNo);
	            }
	        }
	        ////System.out.println("Suji check Link Failty pe ::: " +dfccSNList.size() );
	    }
	    
	    
	    serialnumber.setItems(dfccSNList);
	    
	    
	    addSearchFunctionality(serialnumber, dfccSNList);
	    
	    
	    serialnumber.setOnAction((event) -> {

			String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
			selectedSNo = serialnumber.getSelectionModel().getSelectedItem();
			 if (selectedSNo != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
		            initializeSessionComboBox(selectedSNo);
		        }else {
		        	initializeTrialSessionComboBox(selectedSNo);
		        }

		});

	    
	}
	
	private void addSearchFunctionality(ComboBox<String> comboBox, ObservableList<String> originalItems) {

		comboBox.setEditable(true);
		comboBox.setItems(originalItems);

		TextField editor = comboBox.getEditor();

		editor.setOnKeyReleased(event -> {

			String text = editor.getText();

			ObservableList<String> filteredList = FXCollections.observableArrayList();

			if (text == null || text.isEmpty()) {
				filteredList.addAll(originalItems);
			} else {
				for (String item : originalItems) {
					if (item.toLowerCase().contains(text.toLowerCase())) {
						filteredList.add(item);
					}
				}
			}

			comboBox.setItems(filteredList);
			comboBox.getEditor().positionCaret(text.length());
			comboBox.show();
		});

// Prevent auto-selection
		comboBox.setOnAction(e -> {
			if (comboBox.getSelectionModel().getSelectedItem() != null) {
				editor.setText(comboBox.getSelectionModel().getSelectedItem());
			}
		});
	}

	
	private void initializeTrialSessionComboBox(String selectedDfccNo) {

	    sessionTypeList.clear();

	    List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
	            .filter(t ->
	                    Objects.equals(t.getUutId(), selectedUttId) &&
	                    Objects.equals(t.getDfccSNo(), selectedSNo) &&
	                    t.getEndDate() == null
	            )
	            .sorted(Comparator
	                    .comparing(TrailSessionDto::getUutId)
	                    .thenComparing(TrailSessionDto::getDfccSNo))
	            .collect(Collectors.toList());

	    for (TrailSessionDto sessionName : filterSessionList) {
	        sessionTypeList.add(sessionName.getSessionName());
	    }
	    sessioName.setItems(sessionTypeList);
	    
	    addSearchFunctionality(sessioName, sessionTypeList);
	    
	    sessioName.setOnAction(event -> {
			String selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
			if (selectedSessionName != null) {
				TrailSessionDto selectedSession = filterSessionList.stream()
						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);

				if (selectedSession != null) {
					selectedSessionId = selectedSession.getSessionId();
				}
				getStageName(selectedSessionId);
			}
		});

	   
	}

	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();
//
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo))
//				.collect(Collectors.toList());

		List<SessionDto> filterSessionList = sessionList.stream()
				.filter(t -> Objects.equals(t.getUutId(), selectedUttId) && Objects.equals(t.getDfccSNo(), selectedSNo)
						&& t.getEndDate() == null)
				.sorted(Comparator.comparing(SessionDto::getUutId).thenComparing(SessionDto::getDfccSNo))
				.collect(Collectors.toList());

		for (SessionDto sessionName : filterSessionList) {
			sessionTypeList.add(sessionName.getSessionName());

		}

		sessioName.setItems(sessionTypeList);
	    
	    addSearchFunctionality(sessioName, sessionTypeList);
		
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

		// IMPORTANT: clear old data to avoid accumulation
		stageNameId.clear();
		stageName.getItems().clear();

		for (UnitSessionDetailsDTO dto : stageList) {
			if (dto.getStageId() != null && dto.getStageName() != null) {
				stageNameId.put(dto.getStageId(), dto.getStageName());
			}
		}

		// Filter out null values explicitly
		List<String> filteredStageNames = stageNameId.values().stream().filter(Objects::nonNull).toList();


		stageName.setItems(FXCollections.observableArrayList(filteredStageNames));

	    
	    addSearchFunctionality(stageName, FXCollections.observableArrayList(filteredStageNames));
		
		
		stageName.setOnAction(event -> {

			selectedStageName = stageName.getSelectionModel().getSelectedItem();

			selectedStageId = stageNameId.entrySet().stream()
					.filter(e -> Objects.equals(selectedStageName, e.getValue())).map(Map.Entry::getKey).findFirst()
					.orElse(null);

			createLinkTestTypeDataTable();
		});
	}

	private HBox createPrintButtonHbox() {

		HBox saveButtonContainer = new HBox(); // only create once
		saveButtonContainer.setAlignment(Pos.CENTER);
		saveButtonContainer.setSpacing(5);
		saveButtonContainer.getChildren().addAll(saveButton);
		if (DFCCConstant.linkTestType) {
			saveButton.setDisable(true);
		}

		saveButton.setOnAction(e -> {
			if (selectedSessionId == null || selectedStageId == null) {
				Notifications.showErrorAlert("Please select Session and Stage.");
				return;
			}
			addGetFlag = false;
			addtoDatabase();

		});

		return saveButtonContainer;
	}

	private HBox createFetchButtonHbox() {

		HBox fetchButtonContainer = new HBox(); // only create once
		fetchButtonContainer.setAlignment(Pos.CENTER);
		fetchButtonContainer.setSpacing(5);
		fetchButtonContainer.getChildren().addAll(fetchButton);

		fetchButton.setOnAction(e -> {
			if (selectedSessionId == null || selectedStageId == null) {
				Notifications.showErrorAlert("Please select Session and Stage.");
				return;
			}
			addGetFlag = true;
			createGetLinkTestTypeDataTable();

		});

		return fetchButtonContainer;
	}

	private void addtoDatabase() {

		List<FailureTypeStatus> failureTypeStatusList = new ArrayList<>();

		for (LinkTestType row : linkTestTypeTable1DataList) {

			FailureTypeStatus status = new FailureTypeStatus();
			status.setTestFileId(row.getId());
			status.setTestFailureType1(row.getTestType1());
			status.setTestFailureType2(row.getTestType2());
			status.setTestFailureType3(row.getTestType3());
			status.setSessionId(selectedSessionId);
			status.setStageId(selectedStageId);
			status.setTimeOfExecution(row.getTimeOfExecution());
			status.setResult(row.getResult());
			status.setExecuteFileName(row.getResultFileName());
//			status.setExecuteFileName(row.getExecutedFileName());
			status.setTestMode(row.getTestMode());

			failureTypeStatusList.add(status);
		}

		if (failureTypeStatusList.isEmpty()) {
			Notifications.showWarningAlert("No records to save");
			return;
		}

		Response response = resultExecutionManagement.addFailureType(failureTypeStatusList, selectedSessionId,
				selectedStageId);

		if (response.getResponseCode() == 1) {
			Notifications.showSuccessAlert("Data added Successfully");
		} else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	private ScrollPane createLinkTestTypeDataTable() {

		linkTestTypeTable1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane();

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				ResultExecutionResponse response = new ResultExecutionResponse();

				if (selectedSessionId != null && selectedStageId != null) {
					response = resultExecutionManagement
							.getResultExecutionListBriefListForSelectedStages(selectedSessionId, selectedStageId);
				}

				if (response.getCode() == 1 && response.getResultDTOList() != null) {

					int i = 1;
					for (ResultExecutionDTO data : response.getResultDTOList()) {

						LinkTestType newData = new LinkTestType();

						newData.setId(data.getTestFileId());
						newData.setSlNo(String.valueOf(i));
						newData.setResultFileName(data.getRdfFile());
//						newData.setExecutedFileName(data.getRdfFile());

						if (data.getEndTime() != null) {
							try {
								SimpleDateFormat dbForm = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss",
										Locale.ENGLISH);
								SimpleDateFormat displayForm = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								Date parsed = dbForm.parse(data.getEndTime());
								newData.setTimeOfExecution(displayForm.format(parsed));
							} catch (Exception e) {
								newData.setTimeOfExecution("Invalid Date");
							}
						}

						if (!"SUCCESS".equals(data.getStatus())) {
							newData.setResult("FAIL");
						} else {
							newData.setResult("PASS");
							newData.setTestType1("");
						}

						newData.setTestMode(data.getTestMode());
						linkTestTypeTable1DataList.add(newData);
						i++;
					}
				}

				Platform.runLater(() -> {

					linkTestTypeTable1DataTableView = linkTestTypeTable1DataFactory
							.createTableView(linkTestTypeTable1DataList, false, false);

					linkTestTypeTable1DataTableView.setEditable(true);

					/* ================= TEST TYPE COLUMN ================= */
					// REMOVE AUTO COLUMN
					linkTestTypeTable1DataTableView.getColumns()
							.removeIf(col -> "TEST TYPE".equalsIgnoreCase(col.getText()));

					ObservableList<String> typeList = FXCollections.observableArrayList("Workmanship Failure", "Component Failure", "Design Issue");

					TableColumn<LinkTestType, String> testTypeColumn = new TableColumn<>("FAIL TYPE1");
					TableColumn<LinkTestType, String> testTypeColumn2 = new TableColumn<>("FAIL TYPE2");
					TableColumn<LinkTestType, String> testTypeColumn3= new TableColumn<>("FAIL TYPE3");

					testTypeColumn.setMinWidth(180);
					testTypeColumn.setMaxWidth(180);
					testTypeColumn.setEditable(true);
					
					testTypeColumn2.setMinWidth(180);
					testTypeColumn2.setMaxWidth(180);
					testTypeColumn2.setEditable(true);
					
					testTypeColumn3.setMinWidth(180);
					testTypeColumn3.setMaxWidth(180);
					testTypeColumn3.setEditable(true);

					testTypeColumn.setCellValueFactory(cellData -> cellData.getValue().testType1Property());
					testTypeColumn2.setCellValueFactory(cellData -> cellData.getValue().testType2Property());
					testTypeColumn3.setCellValueFactory(cellData -> cellData.getValue().testType3Property());

					testTypeColumn.setCellFactory(col -> new TableCell<LinkTestType, String>() {

					    private final ComboBox<String> comboBox = new ComboBox<>(typeList);
					    private final Label label = new Label();

					    {
					        comboBox.setPrefWidth(160);

					        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
					            if (!addGetFlag && getIndex() >= 0
					                    && getIndex() < getTableView().getItems().size()) {
					                getTableView().getItems().get(getIndex()).setTestType1(newVal);
					            }
					        });
					    }

					    @Override
					    protected void updateItem(String item, boolean empty) {
					        super.updateItem(item, empty);

					        if (empty || getIndex() < 0) {
					            setGraphic(null);
					            setText(null);
					            return;
					        }

					        LinkTestType rowData = getTableView().getItems().get(getIndex());

					        if ("FAIL".equals(rowData.getResult())) {

					            // set value for both
					            comboBox.setValue(rowData.getTestType1());
					            label.setText(rowData.getTestType1());

					            if (addGetFlag) {
					                // ✅ VIEW MODE → Label only
					                setGraphic(label);
					            } else {
					                // ✅ EDIT MODE → ComboBox
					                setGraphic(comboBox);
					            }

					            setText(null);
					            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

					        } else {
					            setGraphic(null);
					            setText("");
					            setContentDisplay(ContentDisplay.TEXT_ONLY);
					        }
					    }
					});

					testTypeColumn2.setCellFactory(col -> new TableCell<LinkTestType, String>() {

					    private final ComboBox<String> comboBox = new ComboBox<>(typeList);
					    private final Label label = new Label();

					    {
					        comboBox.setPrefWidth(160);

					        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
					            if (!addGetFlag && getIndex() >= 0
					                    && getIndex() < getTableView().getItems().size()) {
					                getTableView().getItems().get(getIndex()).setTestType2(newVal);
					            }
					        });
					    }

					    @Override
					    protected void updateItem(String item, boolean empty) {
					        super.updateItem(item, empty);

					        if (empty || getIndex() < 0) {
					            setGraphic(null);
					            setText(null);
					            return;
					        }

					        LinkTestType rowData = getTableView().getItems().get(getIndex());

					        if ("FAIL".equals(rowData.getResult())) {

					            // set value for both
					            comboBox.setValue(rowData.getTestType2());
					            label.setText(rowData.getTestType2());

					            if (addGetFlag) {
					                // ✅ VIEW MODE → Label only
					                setGraphic(label);
					            } else {
					                // ✅ EDIT MODE → ComboBox
					                setGraphic(comboBox);
					            }

					            setText(null);
					            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

					        } else {
					            setGraphic(null);
					            setText("");
					            setContentDisplay(ContentDisplay.TEXT_ONLY);
					        }
					    }
					});
					
					testTypeColumn3.setCellFactory(col -> new TableCell<LinkTestType, String>() {

					    private final ComboBox<String> comboBox = new ComboBox<>(typeList);
					    private final Label label = new Label();

					    {
					        comboBox.setPrefWidth(160);

					        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
					            if (!addGetFlag && getIndex() >= 0
					                    && getIndex() < getTableView().getItems().size()) {
					                getTableView().getItems().get(getIndex()).setTestType3(newVal);
					            }
					        });
					    }

					    @Override
					    protected void updateItem(String item, boolean empty) {
					        super.updateItem(item, empty);

					        if (empty || getIndex() < 0) {
					            setGraphic(null);
					            setText(null);
					            return;
					        }

					        LinkTestType rowData = getTableView().getItems().get(getIndex());

					        if ("FAIL".equals(rowData.getResult())) {

					            // set value for both
					            comboBox.setValue(rowData.getTestType3());
					            label.setText(rowData.getTestType3());

					            if (addGetFlag) {
					                // ✅ VIEW MODE → Label only
					                setGraphic(label);
					            } else {
					                // ✅ EDIT MODE → ComboBox
					                setGraphic(comboBox);
					            }

					            setText(null);
					            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

					        } else {
					            setGraphic(null);
					            setText("");
					            setContentDisplay(ContentDisplay.TEXT_ONLY);
					        }
					    }
					});
					linkTestTypeTable1DataTableView.getColumns()
				    .removeIf(col -> col.getText().toUpperCase().startsWith("TEST TYPE"));

					linkTestTypeTable1DataTableView.getColumns().addAll(testTypeColumn,testTypeColumn2,testTypeColumn3);


					linkTestTypeTable1DataTableView.getColumns().forEach(column -> {

						switch (column.getText()) {

						case "SL NO":
							column.setMinWidth(100);
							column.setMaxWidth(100);
							break;

						case "TEST MODE":
							column.setMinWidth(320);
							column.setMaxWidth(320);
							break;

						case "RESULT FILE NAME":
							column.setMinWidth(420);
							column.setMaxWidth(420);
							break;

						case "TIME OF EXECUTION":
							column.setMinWidth(260);
							column.setMaxWidth(260);
							break;

						case "RESULT":
							column.setMinWidth(120);
							column.setMaxWidth(120);
							break;

						case "TEST TYPE1":
						case "TEST TYPE2":
						case "TEST TYPE3":
							column.setMinWidth(230);
							column.setMaxWidth(230);
							break;
						}

//	                    updateBriefData((TableColumn<LinkTestType, String>) column);
					});

					tableScrollPane.setContent(linkTestTypeTable1DataTableView);
					tableScrollPane.setFitToHeight(true);
				});

				return null;
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};

		new Thread(task).start();

		return tableScrollPane;
	}

	private ScrollPane createGetLinkTestTypeDataTable() {

		linkTestTypeTable1DataList.clear();

		ScrollPane tableScrollPane = new ScrollPane();

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				List<FailureTypeStatus> failureList = new ArrayList<>();

				if (selectedSessionId != null && selectedStageId != null) {
					failureList = resultExecutionManagement.getFailureTypeBySessionIdStageId(selectedSessionId,
							selectedStageId);
				}

				int i = 1;
				for (FailureTypeStatus failure : failureList) {

					LinkTestType row = new LinkTestType();

					row.setSlNo(String.valueOf(i));

					row.setTestType1(failure.getTestFailureType1());
					row.setTestType2(failure.getTestFailureType2());
					row.setTestType3(failure.getTestFailureType3());
					row.setTimeOfExecution(failure.getTimeOfExecution());
					row.setResultFileName(failure.getExecuteFileName());
//					row.setExecutedFileName(failure.getExecuteFileName());
					row.setTestMode(failure.getTestMode());

					row.setResult(failure.getResult());

					linkTestTypeTable1DataList.add(row);
					i++;
				}

				Platform.runLater(() -> {

					linkTestTypeTable1DataTableView = linkTestTypeTable1DataFactory
							.createTableView(linkTestTypeTable1DataList, false, false);

					linkTestTypeTable1DataTableView.setEditable(false);
					linkTestTypeTable1DataTableView.getColumns()
							.removeIf(col -> col.getText() == null || col.getText().trim().isBlank());

					linkTestTypeTable1DataTableView.getColumns().forEach(column -> {

						switch (column.getText()) {

						case "SL NO":
							column.setMinWidth(100);
							column.setMaxWidth(100);
							break;

						case "RESULT FILE NAME":
							column.setMinWidth(420);
							column.setMaxWidth(420);
							break;

						case "TEST MODE":
							column.setMinWidth(320);
							column.setMaxWidth(320);
							break;

						case "TEST TYPE1":
							column.setMinWidth(230);
							column.setMaxWidth(230);
							break;

						case "TEST TYPE2":
							column.setMinWidth(230);
							column.setMaxWidth(230);
							break;

						case "TEST TYPE3":
							column.setMinWidth(230);
							column.setMaxWidth(230);
							break;
						}
					});

					tableScrollPane.setContent(linkTestTypeTable1DataTableView);
					tableScrollPane.setFitToHeight(true);
					Platform.runLater(() -> Notifications.showSuccessAlert("Data Fetched Successfully"));
				});

				return null;
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve failure data"));
			}
		};

		new Thread(task).start();

		return tableScrollPane;
	}

	private void updateBriefData(TableColumn<LinkTestType, String> column) {
		column.setCellFactory(col -> new TableCell<LinkTestType, String>() {
			private Label label;

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setGraphic(null);
				} else {
					if (label == null) {
						label = new Label();
//						SAI CHANGED FOR RESULT TAB
						if ("TEST MODE".equalsIgnoreCase(column.getText())
								|| "EXECUTED FILE NAME".equalsIgnoreCase(column.getText())) {
							label.setWrapText(true);
						} else {
							label.setWrapText(false);
						}

//						label.setWrapText(false);
						label.setAlignment(Pos.CENTER);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						setStyle("-fx-alignment: CENTER;");
					}
					label.setText(item);

					// Set color based on result
					if ("FAIL".equalsIgnoreCase(item)) {
						label.setStyle("-fx-text-fill: red; -fx-effect: dropshadow(one-pass-box, white, 5, 2, 0, 0);");
					} else if ("PASS".equalsIgnoreCase(item)) {
						label.setStyle(
								"-fx-text-fill: green; -fx-effect: dropshadow(one-pass-box, white, 5, 2, 0, 0);");
					} else {
						label.setStyle("-fx-text-fill: black; ");
					}
//					SAI CHANGED FOR RESULT TAB
//					label.setMinWidth(label.getText().length() * 18);
					setGraphic(label);
//					this.setMinWidth(label.getText().length() * 18);
//					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
				}
			}
		});
	}

}
