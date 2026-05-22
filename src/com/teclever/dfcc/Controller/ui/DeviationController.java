package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.StepDeviationDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.model.Cumulative;
import com.teclever.dfcc.model.DeviationResult;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

class DeviationTableViewFactory implements TableViewFactory<DeviationResult> {
	@Override
	public CustomTableView<DeviationResult> createTableView(ObservableList<DeviationResult> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DeviationResult.class, addUserColumn, addCheckboxColumn);
	}
}

public class DeviationController {

	private GridPane deviationResultGridPane = new GridPane();
	private GridPane deviationStepContainerGridPane = new GridPane();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();

	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();

	private ObservableList<DeviationResult> deviationDataList = FXCollections.observableArrayList();
	private TableViewFactory<DeviationResult> deviationDataFactory = new DeviationTableViewFactory();
	private CustomTableView<DeviationResult> deviationDataTableView;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private Button submit = new Button("Submit");

	private Label stepNo = new Label("Step:");
	
	private TextField stepNoT = new TextField();
	private String stepNotValue;
	
	private ComboBox<String> getStepNo = new ComboBox<String>();
	private ObservableList<String> stepNoListFinal = FXCollections.observableArrayList();

	private Label reductionRange = new Label("Reduction:");
	private TextField reductionRangeText = new TextField();

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String uutId;

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("Deviation Results");
	private SessionService s = new SessionService();

	private ComboBox<String> sessioName = new ComboBox<String>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();

	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();
	
	private String enterdStepNo;
	private double entredReductionNo;

	private String selectedUttId;
	private String selectedSno;
	private String selectedSessionName;
	
	public DeviationController() {
		unitDataList.clear();
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();

	}

	public GridPane deviationResultGridPane() {
		initializeUUTTypeComboBox();
		deviationResultGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
						.toExternalForm());
		deviationResultGridPane.getStyleClass().add("current-execution-result-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		deviationResultGridPane.setPadding(new Insets(5));
		deviationResultGridPane.getColumnConstraints().addAll(firstColumn);
		deviationResultGridPane.getRowConstraints().addAll(firstRow);

		deviationResultGridPane.add(createCumulativeHoursGridPane(), 0, 0);
		return deviationResultGridPane;
	}

	private GridPane createCumulativeHoursGridPane() {

		deviationStepContainerGridPane.setHgap(5);
		deviationStepContainerGridPane.setVgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(10);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(83);


		deviationStepContainerGridPane.getColumnConstraints().addAll(firstColumn);
		deviationStepContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		deviationStepContainerGridPane.add(headingGridPane(), 0, 0);
		deviationStepContainerGridPane.add(createFilterSelectionGridPane(), 0, 1);
		deviationStepContainerGridPane.add(createDeviationDataTable(), 0, 2);

		return deviationStepContainerGridPane;
	}

	private GridPane createFilterSelectionGridPane() {
		GridPane filterResultGridPane = new GridPane();

		filterResultGridPane.getStyleClass().add("data-analysis-second-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(30);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(10);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		filterResultGridPane.setPadding(new Insets(5));

		filterResultGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		filterResultGridPane.getRowConstraints().addAll(firstRow);

		filterResultGridPane.add(createUutBox(), 0, 0);
		filterResultGridPane.add(createsetpNoBox(), 1, 0);
		filterResultGridPane.add(createReductionBox(), 2, 0);
		filterResultGridPane.add(createSubmitButton(), 3, 0);

		return filterResultGridPane;
	}
	
	private GridPane createDataEnterGridPane() {
		GridPane dataEnterGridPane = new GridPane();

		dataEnterGridPane.getStyleClass().add("data-analysis-second-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		dataEnterGridPane.setPadding(new Insets(5));

		dataEnterGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		dataEnterGridPane.getRowConstraints().addAll(firstRow);
		
		dataEnterGridPane.add(createsetpNoBox(), 0, 0);
		dataEnterGridPane.add(createReductionBox(), 1, 0);
		dataEnterGridPane.add(createSubmitButton(), 2, 0);

		return dataEnterGridPane;
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

	private HBox createUutBox() {
		uutTypeField.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uutTypeField);

		return uutTypeHBox;
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
			initializeDfccSNComboBox(uutId);
			DFCCConstant.selectedUut = uutId;
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

	private void initializeDfccSNComboBox(String uutTypeId) {
		dfccSNList.clear();
		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
				.collect(Collectors.toList());
		Set<String> seenDfccSNos = new HashSet<>();

		for (SessionDto dfccSn : filterSessionList) {
			String dfccSNo = dfccSn.getDfccSNo();
			if (seenDfccSNos.add(dfccSNo)) {
				dfccSNList.add(dfccSNo);
			}
		}

	}

// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		HBox selectionHBoxUUTSN = new HBox(10);
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER);
		selectionHBoxUUTSN.getChildren().add(slNoField);
		slNoField.setOnAction(event -> {
			String selectedSerialNumber =  slNoField.getSelectionModel().getSelectedItem();
			selectedSno = slNoField.getSelectionModel().getSelectedItem();
			initializeSessionComboBox(selectedSerialNumber);
			
			
		});
		slNoField.setItems(dfccSNList);

		return selectionHBoxUUTSN;
	}

	private HBox createSessionHbox() {
		HBox sessionHbox = new HBox(10);
		sessionHbox.setAlignment(Pos.CENTER);
		sessioName.setPromptText("Select Session");
		sessionHbox.getChildren().addAll(sessioName);
		return sessionHbox;
	}

	private void initializeSessionComboBox(String selectedDfccNo) {
		sessionTypeList.clear();

//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getDfccSNo().equals(selectedDfccNo))
//				.collect(Collectors.toList());
		
		List<SessionDto> filterSessionList = sessionList.stream()
		        .filter(t ->
		                Objects.equals(t.getUutId(), selectedUttId) &&
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
			selectedSessionName = sessioName.getSelectionModel().getSelectedItem();
			if (selectedSessionName != null) {
				SessionDto selectedSession = filterSessionList.stream()
						.filter(s -> s.getSessionName().equals(selectedSessionName)).findFirst().orElse(null);

			
				
				DFCCConstant.selectedSession = selectedSession.getSessionId();
				getStepNoList();
			}
		});

	}

	private HBox createSubmitButton() {
		HBox submitHBox = new HBox();
		submitHBox.setAlignment(Pos.CENTER);

		submitHBox.getChildren().add(submit);

//		submit.setOnAction(event -> {
//			Platform.runLater(() -> {
//				if (uutTypeField.getSelectionModel().getSelectedItem() == null 
//					    || stepNoT.getText().trim().isEmpty() 
//					    || reductionRangeText.getText().trim().isEmpty()) {
//
//					    Notifications.showErrorAlert("Please select UUT Type & Enter Step No & Enter reduction No");
//					    return;
//					}
//				stepNotValue =stepNoT.getText();
//			    entredReductionNo =  Double.parseDouble(reductionRangeText.getText().trim());
//			    createDeviationDataTable();
//			});
//		});
		
		submit.setOnAction(event -> {
		    Platform.runLater(() -> {
		        // Get trimmed values from text fields
		        String stepText = stepNoT.getText() != null ? stepNoT.getText().trim() : "";
		        String reductionText = reductionRangeText.getText() != null ? reductionRangeText.getText().trim() : "";

		        // Validate all required fields
		        if (uutTypeField.getSelectionModel().getSelectedItem() == null
		                || stepText.isEmpty()
		                || reductionText.isEmpty()) {

		            Notifications.showErrorAlert("Please select UUT Type & Enter Step No & Enter reduction No");
		            return;
		        }

		        // Assign step value
		        stepNotValue = stepText;

		        // Parse reduction number safely
		        try {
		            entredReductionNo = Double.parseDouble(reductionText);
		        } catch (NumberFormatException e) {
		            Notifications.showErrorAlert("Please enter a valid number for reduction");
		            return;
		        }

		        // Call the method to create the deviation data table
		        createDeviationDataTable();
		    });
		});

		return submitHBox;
	}

	private HBox createsetpNoBox() {
		stepNoT.setPromptText("Enter Step");
	    HBox stepNoHBox = new HBox(10);
	    stepNoHBox.setAlignment(Pos.CENTER);
	    stepNo.getStyleClass().add("title-label");
	    stepNoT.getStyleClass().add("title-label");
	    stepNoHBox.getChildren().addAll(stepNo, stepNoT);

	    return stepNoHBox;
	}


	private HBox createReductionBox() {

		HBox reductionRangeHBox = new HBox(10);
		reductionRangeHBox.setAlignment(Pos.CENTER);
		reductionRange.getStyleClass().add("title-label");
		reductionRangeText.getStyleClass().add("title-label");
		reductionRangeText.setPromptText("Enter Reduction");
		reductionRangeHBox.getChildren().addAll(reductionRange,reductionRangeText);
		
		return reductionRangeHBox;
	}
	
	private void getStepNoList() {
		List<String> stepNoList = advancedDataAnalysisManagement.getStepsForSession(DFCCConstant.selectedSession);
		stepNoListFinal.addAll(stepNoList);
		////System.out.println("Step List Size: "+ stepNoList.size() + DFCCConstant.selectedSession);
		getStepNo.setItems(stepNoListFinal);
		getStepNo.setOnAction(event -> {
			enterdStepNo = getStepNo.getValue();
		});
		
	}

	private ScrollPane createDeviationDataTable() {
//		//System.out.println("Check " + selectedUttId +stepNotValue+entredReductionNo );
		deviationDataList.clear();

		// Create TableView first
		deviationDataTableView = deviationDataFactory.createTableView(deviationDataList, false, false);

		ScrollPane tableScrollPane = new ScrollPane(deviationDataTableView);
		tableScrollPane.setFitToHeight(true);

		Task<ObservableList<DeviationResult>> task = new Task<ObservableList<DeviationResult>>() {

			@Override
			protected ObservableList<DeviationResult> call() throws Exception {

				ObservableList<DeviationResult> tempList = FXCollections.observableArrayList();

				// 🔹 Correct return type
				List<StepDeviationDTO> dtoList = advancedDataAnalysisManagement.getDeviationForStep(selectedUttId, stepNotValue,
						entredReductionNo);

				if (dtoList.size()>0) {
					for (StepDeviationDTO dto : dtoList) {

						DeviationResult result = new DeviationResult();
						result.setUut(dto.getUut());
						result.setSessionName(selectedSessionName);
						result.setRdfFileName(dto.getRdfFileName());
						result.setStepNo(dto.getStepNo());
						result.setReduceRange(dto.getReduceRange());
						result.setNominalRange(dto.getNominalRange());
						result.setExceptedValue(dto.getExceptedValue());
						result.setNewMin(dto.getNewMin());
						result.setNewMax(dto.getNewMax());
						result.setMaximum(dto.getMaximum());
						result.setMinimum(dto.getMinimum());
						result.setExceptedValue(dto.getExceptedValue());
						result.setChannel1Value(dto.getChannel1Value());
						result.setChannel2Value(dto.getChannel2Value());
						result.setChannel3Value(dto.getChannel3Value());
						result.setChannel4Value(dto.getChannel4Value());

						tempList.add(result);
					}
				}else if(selectedUttId!=null)
				{
					  Notifications.showErrorAlert("Please Enter a valid Step");
					 
				}

				return tempList;
			}
		};

		task.setOnSucceeded(event -> {
			deviationDataList.setAll(task.getValue());
			deviationDataTableView.refresh();
		});

		task.setOnFailed(event -> {
			task.getException().printStackTrace();
		});
		
		deviationDataTableView.getColumns().forEach(column -> {

			String colNamne=column.getText();
			switch (colNamne) {
			case "CHANNEL1VALUE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
			case "MINIMUM":
				column.setMinWidth(350);
				column.setMaxWidth(350);
				break;
			case "MAXIMUM":
				column.setMinWidth(350);
				column.setMaxWidth(350);
				break;
			case "CHANNEL2VALUE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
				
			case "CHANNEL3VALUE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
			case "CHANNEL4VALUE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
			case "NOMINAL RANGE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
			case "DATE":
				column.setMinWidth(200);
				column.setMaxWidth(200);
				break;
				
			case "EXCEPTED VALUE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
				
			case "REDUCE RANGE":
				column.setMinWidth(300);
				column.setMaxWidth(300);
				break;
			case "STEP NO":
				column.setMinWidth(150);
				column.setMaxWidth(150);
				break;
				
			case "NEW MIN":
				column.setMinWidth(120);
				column.setMaxWidth(120);
				break;
				
			case "NEW MAX":
				column.setMinWidth(120);
				column.setMaxWidth(120);
				break;


			default:
//				column.setMinWidth(120);
//				column.setMaxWidth(120);
				break;
			}
			
		});

		new Thread(task).start();

		return tableScrollPane;
	}

}
