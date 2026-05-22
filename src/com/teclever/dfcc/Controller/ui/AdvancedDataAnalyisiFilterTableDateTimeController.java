package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.AdaFilter1;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class AdvancedDataAnalyisiFilterTableDateTimeController {

	private GridPane adaFiletrDateTimeMainContainerGridPane = new GridPane();

//	UUT
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();

//	Signal Name
	private ComboBox<String> signalNameField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> signalNameDataList;
	private ObservableList<String> signalNameTypeList = FXCollections.observableArrayList();

//Step No
	private ComboBox<String> stepField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> stepDataList;
	private ObservableList<String> stepTypeList = FXCollections.observableArrayList();

//TPGPH No
	private ComboBox<String> tpgphField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> tpgphDataList;
	private ObservableList<String> tpgphTypeList = FXCollections.observableArrayList();
	
//	SNo
	private ComboBox<String> serialnumber = new ComboBox<String>();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();

	private StackPane adaFilterDateTimeStackPane = new StackPane();

	private TableViewFactory<AdaFilter1> adaFilterDateTimeDataFactory = new ADAFilter1TableViewFactory();

	private ObservableList<AdaFilter1> adaFiletrDateTimeDataList = FXCollections.observableArrayList();

	private CustomTableView<AdaFilter1> adaFiletrDateTimeDataTableView;
	
	private String uutId = "UUT1";
	
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private Map<String, String> sessionNameId = new HashMap<String, String>();
	private SessionService s = new SessionService();
	private TrailSessionEntityService t = new TrailSessionEntityService();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	
	public AdvancedDataAnalyisiFilterTableDateTimeController() {

	    // Normal sessions
	    SessionResponse s1 = s.getAllSession();
	    sessionList = s1.getListOfSession();

	    // Trial sessions
	    TrailSessionResponse t1 = t.getActiveTrailSessionId();
	    sessionListTrail = t1.getListOfSession();
		initializeUUTTypeComboBox();
	}

	public GridPane createFilterDateTimeTableContainerGridPane() {
		adaFiletrDateTimeMainContainerGridPane.getChildren().clear();
		adaFiletrDateTimeMainContainerGridPane.getColumnConstraints().clear();
		adaFiletrDateTimeMainContainerGridPane.getRowConstraints().clear();

		adaFiletrDateTimeMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/ManualTesting.css").toExternalForm());
		adaFiletrDateTimeMainContainerGridPane.getStyleClass().add("advanced-testing-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);

		adaFiletrDateTimeMainContainerGridPane.setHgap(10);
		adaFiletrDateTimeMainContainerGridPane.setVgap(10);

		adaFiletrDateTimeMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		adaFiletrDateTimeMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
		adaFiletrDateTimeMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));
//
		adaFiletrDateTimeMainContainerGridPane.add(createFilterGridePane(), 0, 0);
		adaFiletrDateTimeMainContainerGridPane.add(createFilterDateTimeTable(), 0, 1);
//		

		return adaFiletrDateTimeMainContainerGridPane;
	}

	private GridPane createFilterGridePane() {
		GridPane filterGridPane = new GridPane();

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

		filterGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		filterGridPane.getRowConstraints().addAll(firstRow);

		HBox uutHbox = new HBox(10);
		uutHbox.setAlignment(Pos.CENTER_LEFT);
		uutHbox.setFillHeight(true);
		uutTypeField.setPromptText("select UUT Type");
		signalNameField.setPromptText("select Signal Name");
		stepField.setPromptText("select Step");
		tpgphField.setPromptText("select TPGPH");
		uutHbox.getChildren().add(uutTypeField);

		HBox signalHbox = new HBox(10);
		signalHbox.setAlignment(Pos.CENTER);
		signalHbox.getChildren().addAll(signalNameField);

		HBox stepHbox = new HBox(10);
		stepHbox.setAlignment(Pos.CENTER_RIGHT);
		stepHbox.getChildren().addAll(stepField);

		HBox tpgphHbox = new HBox(10);
		tpgphHbox.setAlignment(Pos.CENTER_RIGHT);
		tpgphHbox.getChildren().addAll(tpgphField);
		
		HBox sNoHbox = new HBox(10);
		sNoHbox.setAlignment(Pos.CENTER_RIGHT);
		sNoHbox.getChildren().addAll(serialnumber);
		serialnumber.setPromptText("select Serial No");

		filterGridPane.add(uutHbox, 0, 0);
		filterGridPane.add(sNoHbox, 1, 0);
		

		return filterGridPane;
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
	    }
	    serialnumber.setItems(dfccSNList);
	    serialnumber.setOnAction(event -> {
	    	String selectedSerialNumber = serialnumber.getSelectionModel().getSelectedItem();
	    });

	   
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

	private ScrollPane createFilterDateTimeTable() {
		adaFiletrDateTimeDataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(adaFiletrDateTimeDataTableView);
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				adaFiletrDateTimeDataTableView = adaFilterDateTimeDataFactory.createTableView(adaFiletrDateTimeDataList, false,
						false);

				adaFiletrDateTimeDataTableView.getColumns().forEach(column -> {

//			column.setMinWidth(column.getText().length() * 14);
					String colNamne = column.getText();
//			////System.out.println(colNamne);
					switch (colNamne) {

					default:
//				column.setMinWidth(120);
//				column.setMaxWidth(120);
						break;
					}

				});

				if (adaFiletrDateTimeDataList.size() == 0) {
					tableScrollPane.setFitToWidth(true);
				}

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane.setContent(adaFiletrDateTimeDataTableView);
					tableScrollPane.setFitToHeight(true);
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

}
