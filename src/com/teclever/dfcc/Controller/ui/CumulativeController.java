package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Cumulative;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

class CumulativeTableViewFactory implements TableViewFactory<Cumulative> {
	@Override
	public CustomTableView<Cumulative> createTableView(ObservableList<Cumulative> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Cumulative.class, addUserColumn, addCheckboxColumn);
	}
}

public class CumulativeController {

	private GridPane cumulativeResultGridPane = new GridPane();
	private GridPane cumulatuveallContainerGridPane = new GridPane();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();

	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();

	private ObservableList<Cumulative> cumulativeDataList = FXCollections.observableArrayList();
	private TableViewFactory<Cumulative> cumulativeDataFactory = new CumulativeTableViewFactory();
	private CustomTableView<Cumulative> cumulativeDataTableView;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private Button submit = new Button("Submit");
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	private String uutId;

	
	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("Cumulative Result");
	private SessionService s = new SessionService();

	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();
	
	private Label cumulativeHours = new Label("Cumulative Hours : ");
	private Label cumulativeHoursValue = new Label();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();

	private TrailSessionEntityService t = new TrailSessionEntityService();

	public CumulativeController() {
		unitDataList.clear();
		// Normal sessions
				SessionResponse s1 = s.getAllSession();
				sessionList = s1.getListOfSession();

				// Trial sessions
				TrailSessionResponse t1 = t.getActiveTrailSessionId();
				sessionListTrail = t1.getListOfSession();

	}

	public GridPane cumulativeResultGridPane() {
		initializeUUTTypeComboBox();
		cumulativeResultGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
						.toExternalForm());
		cumulativeResultGridPane.getStyleClass().add("current-execution-result-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		cumulativeResultGridPane.setPadding(new Insets(5));
		cumulativeResultGridPane.getColumnConstraints().addAll(firstColumn);
		cumulativeResultGridPane.getRowConstraints().addAll(firstRow);

		cumulativeResultGridPane.add(createCumulativeHoursGridPane(), 0, 0);
		return cumulativeResultGridPane;
	}

	private GridPane createCumulativeHoursGridPane() {

		cumulatuveallContainerGridPane.setHgap(5);
		cumulatuveallContainerGridPane.setVgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(10);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(83);

		cumulatuveallContainerGridPane.getColumnConstraints().addAll(firstColumn);
		cumulatuveallContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		cumulatuveallContainerGridPane.add(headingGridPane(), 0, 0);
		cumulatuveallContainerGridPane.add(createFilterSelectionGridPane(), 0, 1);
		cumulatuveallContainerGridPane.add(createCumulativeDeatailsHBox(), 0, 2);

		return cumulatuveallContainerGridPane;
	}

	private GridPane createFilterSelectionGridPane() {
		GridPane filterResultGridPane = new GridPane();

		filterResultGridPane.getStyleClass().add("data-analysis-second-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		filterResultGridPane.setPadding(new Insets(5));

		filterResultGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		filterResultGridPane.getRowConstraints().addAll(firstRow);

		filterResultGridPane.add(createUutBox(), 0, 0);
		filterResultGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		filterResultGridPane.add(createSubmitButton(), 2, 0);

		return filterResultGridPane;
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

//	private void initializeDfccSNComboBox(String uutTypeId) {
//		dfccSNList.clear();
//		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
//				.collect(Collectors.toList());
//		Set<String> seenDfccSNos = new HashSet<>();
//
//		for (SessionDto dfccSn : filterSessionList) {
//			String dfccSNo = dfccSn.getDfccSNo();
//			if (seenDfccSNos.add(dfccSNo)) {
//				dfccSNList.add(dfccSNo);
//			}
//		}
//
//	}
	
	private void initializeDfccSNComboBox(String uutTypeId) {

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
	    
	    

	    
	}
	
	

// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		HBox selectionHBoxUUTSN = new HBox(10);
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER);
		slNoField.setEditable(true);
		selectionHBoxUUTSN.getChildren().add(slNoField);
		slNoField.setItems(dfccSNList);
		  addSearchFunctionality(slNoField, dfccSNList);
		slNoField.setOnAction(event -> {
			Platform.runLater(() -> {
				submit.setDisable(false);
			});
			DFCCConstant.selectedSNo = slNoField.getSelectionModel().getSelectedItem();

		});
		

		return selectionHBoxUUTSN;
	}

	private HBox createSubmitButton() {
		HBox submitHBox = new HBox();
		submitHBox.setAlignment(Pos.CENTER);

		submitHBox.getChildren().add(submit);

		submit.setOnAction(event -> {
			Platform.runLater(() -> {
				if (uutTypeField.getSelectionModel().getSelectedItem() == null
						|| slNoField.getSelectionModel().getSelectedItem() == null) {

					Notifications.showErrorAlert("Please select UUT Type & Serial No");
					return;
				}
			
				cumulativeValueSet();
//				createCumulativeDataTable();

			});
		});

		return submitHBox;
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
	private HBox createCumulativeDeatailsHBox() {
		HBox cumulativeDetailsHBox = new HBox();
		cumulativeDetailsHBox.setAlignment(Pos.CENTER);

		cumulativeDetailsHBox.getChildren().addAll(cumulativeHours,cumulativeHoursValue);
		
		cumulativeHours.getStyleClass().add("title-label");
		cumulativeHoursValue.getStyleClass().add("title-label");


		return cumulativeDetailsHBox;
	}
	
	private void cumulativeValueSet() {
		 Map<String, String> hoursCumulative =
                 advancedDataAnalysisManagement.getCummulativeTimeAllUUTs();

         long totalSeconds = hoursCumulative.values()
                 .stream()
                 .mapToLong(CumulativeController.this::toSeconds)
                 .sum();

         
         Platform.runLater(() -> {
        	    cumulativeHoursValue.setText(toHHMMSS(totalSeconds));
        	});
	}
	
	
	
	
	private StackPane createCumulativeDataTable() {

	    cumulativeDataList.clear();

	    ScrollPane tableScrollPane = new ScrollPane();
	    tableScrollPane.setFitToWidth(true);
	    tableScrollPane.setFitToHeight(true);

	    Task<Cumulative> task = new Task<Cumulative>() {

	        @Override
	        protected Cumulative call() {

	            Map<String, String> hoursCumulative =
	                    advancedDataAnalysisManagement.getCummulativeTimeAllUUTs();

	            long totalSeconds = hoursCumulative.values()
	                    .stream()
	                    .mapToLong(CumulativeController.this::toSeconds)
	                    .sum();

	            Cumulative cumulative = new Cumulative();
	            cumulative.setTotalHours(toHHMMSS(totalSeconds));

	            return cumulative;
	        }

	        @Override
	        protected void succeeded() {

	            cumulativeDataList.add(getValue());

	            cumulativeDataTableView =
	                    cumulativeDataFactory.createTableView(cumulativeDataList, false, false);

	            cumulativeDataTableView.getColumns().forEach(column -> {
	                if ("Total Hours".equals(column.getText())) {
	                    column.setMinWidth(500);
	                    column.setMaxWidth(500);
	                }
	            });

	            tableScrollPane.setContent(cumulativeDataTableView);
	        }

	        @Override
	        protected void failed() {
	            getException().printStackTrace();
	            Notifications.showErrorAlert("Failed to retrieve data");
	        }
	    };

	    new Thread(task).start();

	    StackPane centerPane = new StackPane(tableScrollPane);
	    centerPane.setAlignment(Pos.CENTER);

	    return centerPane;   // ✅ THIS is the fix
	}


	private long toSeconds(String time) {
	    if (time == null || time.isEmpty()) {
	        return 0;
	    }

	    String[] parts = time.split(":"); // HH:mm:ss
	    long hours = Long.parseLong(parts[0]);
	    long minutes = Long.parseLong(parts[1]);
	    long seconds = Long.parseLong(parts[2]);

	    return hours * 3600 + minutes * 60 + seconds;
	}

	
	private String toHHMMSS(long totalSeconds) {
	    long hours = totalSeconds / 3600;
	    long minutes = (totalSeconds % 3600) / 60;
	    long seconds = totalSeconds % 60;

	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}


}
