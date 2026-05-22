package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.reportgeneration.ReportGeneration;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class UnitDataTableViewFactory implements TableViewFactory<UnitData> {
	@Override
	public CustomTableView<UnitData> createTableView(ObservableList<UnitData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, UnitData.class, addUserColumn, addCheckboxColumn);
	}
}

public class CurrentUnitResultController {

	private GridPane currentUnitResultGridPane = new GridPane();
	private GridPane currentUnitResultHeadingGridPane = new GridPane();
	private GridPane currentUnitResultOptionGridPane = new GridPane();
	private GridPane currentUnitResultTableGridPane = new GridPane();
	private ReportGeneration reportGeneration = new ReportGeneration();
	
	private ProgressIndicator progressIndicator = new ProgressIndicator();
	private VBox progressbox = new VBox();
	
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Print");

	private HBox titleBox = new HBox();
	private Label title = new Label();
	private SessionService s = new SessionService();
	private TrailSessionEntityService t = new TrailSessionEntityService();
	private GridPane filterResultGridPane = new GridPane();

	private ComboBox<String> uutTypeField = new ComboBox<String>();	
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private HBox selectionBoxSESSION = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private List<TrailSessionDto> sessionListTrail = new ArrayList<TrailSessionDto>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String session_ID;
	private String selectedSno;
	private String selectedUttId;

	private ScrollPane tableScrollPane = new ScrollPane();

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

	private TableViewFactory<UnitData> unitDataFactory = new UnitDataTableViewFactory();
	private CustomTableView<UnitData> unitDataTableView;

	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();

	public CurrentUnitResultController() {

	    unitDataList.clear();

	    // Normal sessions
	    SessionResponse s1 = s.getAllSession();
	    sessionList = s1.getListOfSession();

	    // Trial sessions
	    TrailSessionResponse t1 = t.getActiveTrailSessionId();
	    sessionListTrail = t1.getListOfSession();

//	    ////System.out.println("Normal session size: " + sessionList.size());
//	    ////System.out.println("Trial session size: " + sessionListTrail.size());
	}


	public GridPane createcurrentUnitResultGridPane() {
		currentUnitResultGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
						.toExternalForm());
		currentUnitResultGridPane.getStyleClass().add("current-execution-result-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		currentUnitResultGridPane.setVgap(5);
		currentUnitResultGridPane.setPadding(new Insets(5));
		currentUnitResultGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		currentUnitResultGridPane.add(createHeadingBox(), 0, 0);
		currentUnitResultGridPane.add(createFilterSelectionGridPane(), 0, 1);
		currentUnitResultGridPane.add(createcurrentUnitResultTableGridPane(), 0, 2);
		
		initializeUUTTypeComboBox();
		
		return currentUnitResultGridPane;
	}
	
	private GridPane createFilterSelectionGridPane() {
		filterResultGridPane.getStyleClass().add("current-execution-result-tabs-container");

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


		filterResultGridPane.setPadding(new Insets(5));

		filterResultGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		filterResultGridPane.getRowConstraints().addAll(firstRow);

		filterResultGridPane.add(createUutBox(), 0, 0);
		filterResultGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		filterResultGridPane.add(createSessionComboBox(), 2, 0);
		filterResultGridPane.add(createDownloadButton(), 3, 0);
		
		return filterResultGridPane;
	}
	
	
	

	private void getCurrentUnitResultData(String uutId, String sessionId) {
		unitDataList.clear();
		ResultUnitSessionDetailsResponse response = resultExecutionManagement.getSessionDetailsForResultsByUnit(uutId, sessionId);
		if (response.getCode() == 1 && response.getResultUnitSessionDetailsDTOList() != null) {
			int i = 1;
			for (ResultUnitSessionDetailsDTO data : response.getResultUnitSessionDetailsDTOList()) {
				UnitData newUnitData = new UnitData();

				newUnitData.setId(data.getSessionId());
				newUnitData.setSlNo(String.valueOf(i));
				newUnitData.setSessionType(data.getSessionType());
				newUnitData.setSessionName(data.getSessionName());
				newUnitData.setStartTime(data.getStartTime());
				newUnitData.setEndTime(data.getEndTime());
				newUnitData.setSessionStatus(data.getSessionStatus());
				newUnitData.setSessionResult(data.getSessionResults());
				newUnitData.setStartRemarks(data.getStratRemarks());
				newUnitData.setEndRemarks(data.getEndRemarks());
				i++;
				unitDataList.add(newUnitData);
			}
		} else if (response.getCode() == 0) {
			Notifications.showErrorAlert(response.getMsg());
		}
		
		tableScrollPane.setFitToWidth(unitDataList.size() == 0);
		
	}
	
	//s27/10032026 added for serial number based filtration
	private void getCurrentUnitResultData1(String uutId, String dfccSNo) {
	    unitDataList.clear();
	    
	    // Filter all sessions for this UUT and S/N
	    List<SessionDto> filteredSessions = sessionList.stream()
	            .filter(s -> s.getUutId().equals(uutId) && s.getDfccSNo().equals(dfccSNo))
	            .collect(Collectors.toList());
	    
	    int i = 1;
	    for (SessionDto session : filteredSessions) {
	        ResultUnitSessionDetailsResponse response = resultExecutionManagement
	                .getSessionDetailsForResultsByUnit(uutId, session.getSessionId());
	        
	        if (response.getCode() == 1 && response.getResultUnitSessionDetailsDTOList() != null) {
	            for (ResultUnitSessionDetailsDTO data : response.getResultUnitSessionDetailsDTOList()) {
	                UnitData newUnitData = new UnitData();

	                newUnitData.setId(data.getSessionId());
	                newUnitData.setSlNo(String.valueOf(i));
	                newUnitData.setSessionType(data.getSessionType());
	                newUnitData.setSessionName(data.getSessionName());
	                newUnitData.setStartTime(data.getStartTime());
	                newUnitData.setEndTime(data.getEndTime());
	                newUnitData.setSessionStatus(data.getSessionStatus());
	                newUnitData.setSessionResult(data.getSessionResults());
	                newUnitData.setStartRemarks(data.getStratRemarks());
	                newUnitData.setEndRemarks(data.getEndRemarks());
	                i++;
	                unitDataList.add(newUnitData);
	            }
	        } else if (response.getCode() == 0) {
	            Notifications.showErrorAlert(response.getMsg());
	        }
	    }}
	
	
	
//	private void initializeDfccSNComboBox(String uutTypeId) {
//
//	    dfccSNList.clear();
//	    Set<String> seenDfccSNos = new HashSet<>();
//
//	    if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {
//
//	        List<SessionDto> filterSessionList = sessionList.stream()
//	                .filter(t -> t.getUutId().equals(uutTypeId))
//	                .collect(Collectors.toList());
//
//	        for (SessionDto dfccSn : filterSessionList) {
//	            String dfccSNo = dfccSn.getDfccSNo();
//	            if (seenDfccSNos.add(dfccSNo)) {
//	                dfccSNList.add(dfccSNo);
//	            }
//	        }
//
//	    } else {
//
//	        List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
//	                .filter(t -> t.getUutId().equals(uutTypeId))
//	                .collect(Collectors.toList());
//	        for (TrailSessionDto dfccSn : filterSessionList) {
//	            String dfccSNo = dfccSn.getDfccSNo();
//	            if (seenDfccSNos.add(dfccSNo)) {
//	                dfccSNList.add(dfccSNo);
//	            }
//	        }
//	    }
//	    slNoField.setItems(dfccSNList);
//	    addSearchFunctionality(slNoField, dfccSNList);
//	    slNoField.setOnAction(event -> {
//	        String selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
//	        selectedSno = selectedSerialNo;
//
//	        if (selectedSerialNo != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
//	        	getCurrentUnitResultData1(selectedUttId, selectedSno);
//	            initializeSessionComboBox(selectedSerialNo);
//	        }else {
//	        	getCurrentUnitResultData1(selectedUttId, selectedSno);
//	        	initializeTrialSessionComboBox(selectedSerialNo);
//	        }
//	    });
//
//	  
//	}
	
	
	private void initializeDfccSNComboBox(String uutTypeId) {

		dfccSNList.clear();
		Set<String> seenDfccSNos = new HashSet<>();

		SessionManagement sessionManagement = new SessionManagement();
		Set<String> userIds = sessionManagement.getUserIdsWithRoleIds();

		if (!currentSessionDetails.getSessionId().startsWith("TSSN")) {
			List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
					.collect(Collectors.toList());
			// Mani Added TO 18-03-2026 //
			filterSessionList = filterSessionList.stream().filter(e -> !userIds.contains(e.getUserId()))
					.collect(Collectors.toList());

			for (SessionDto dfccSn : filterSessionList) {
				String dfccSNo = dfccSn.getDfccSNo();
				if (seenDfccSNos.add(dfccSNo)) {
					dfccSNList.add(dfccSNo);
				}
			}

		} else {

			List<TrailSessionDto> filterSessionList = sessionListTrail.stream()
					.filter(t -> t.getUutId().equals(uutTypeId)).collect(Collectors.toList());

			// Mani Added TO 18-03-2026 //
			filterSessionList = filterSessionList.stream().filter(e -> !userIds.contains(e.getUserId()))
					.collect(Collectors.toList());

			for (TrailSessionDto dfccSn : filterSessionList) {
				String dfccSNo = dfccSn.getDfccSNo();
				if (seenDfccSNos.add(dfccSNo)) {
					dfccSNList.add(dfccSNo);
				}
			}
		}
		slNoField.setItems(dfccSNList);
		addSearchFunctionality(slNoField, dfccSNList);
		slNoField.setOnAction(event -> {
			String selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
			selectedSno = selectedSerialNo;

			if (selectedSerialNo != null && !currentSessionDetails.getSessionId().startsWith("TSSN")) {
				initializeSessionComboBox(selectedSerialNo);
				getCurrentUnitResultData1(selectedUttId, selectedSno);
			} else {
				initializeTrialSessionComboBox(selectedSerialNo);
				getCurrentUnitResultData1(selectedUttId, selectedSno);
			}
		});

	}
	
	
	

	
	// UUT SERIAL NUMBER FIELD
		private HBox createUUTSerialNoComboBox() {
			slNoField.setPromptText("UUT S/N");
			selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
			selectionHBoxUUTSN.setAlignment(Pos.CENTER_LEFT);
			slNoField.setEditable(true);
			selectionHBoxUUTSN.getChildren().add(slNoField);

			return selectionHBoxUUTSN;
		}
		
		
		
		// SESSION FIELD
		private HBox createSessionComboBox() {
			sessionNameField.setPromptText("Session");
			selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
			selectionBoxSESSION.setAlignment(Pos.CENTER_LEFT);
			sessionNameField.setEditable(true);
			selectionBoxSESSION.getChildren().add(sessionNameField);

			return selectionBoxSESSION;
		}
		
		
//	    // UUT SESSION NAME TYPE FIELD
//		private void initializeSessionComboBox(String selectedDfccNo) {
//			sessionTypeList.clear();
//
//			List<SessionDto> filterSessionList = sessionList.stream()
//			        .filter(t ->
//			                Objects.equals(t.getUutId(), selectedUttId) &&
//			                Objects.equals(t.getDfccSNo(), selectedSno) 
//			                
//			        )
//			        .sorted(Comparator
//			                .comparing(SessionDto::getUutId)
//			                .thenComparing(SessionDto::getDfccSNo))
//			        .collect(Collectors.toList());
//
//			
//			for (SessionDto sessionName : filterSessionList) {
//				sessionTypeList.add(sessionName.getSessionName());
//			}
//			sessionNameField.setItems(sessionTypeList);
//			addSearchFunctionality(sessionNameField, sessionTypeList);
//			sessionNameField.setOnAction(event -> {
//				String selectedSession = sessionNameField.getSelectionModel().getSelectedItem();
//				////System.out.println("selectedSession" + selectedSession);
//				String sessionId = fetchSessionId(selectedSession);
//				////System.out.println("Check Session ID" + sessionId);
//				getCurrentUnitResultData(selectedUttId, sessionId);
//				session_ID = sessionId;
//				});
//
//			
//		}
		
		
		private void initializeSessionComboBox(String selectedDfccNo) {
			sessionTypeList.clear();
			
			
			SessionManagement sessionManagement = new SessionManagement();
			Set<String>	userIds = sessionManagement.getUserIdsWithRoleIds();

			List<SessionDto> filterSessionList = sessionList.stream()
			        .filter(t ->
			                Objects.equals(t.getUutId(), selectedUttId) &&
			                Objects.equals(t.getDfccSNo(), selectedSno) &&
			                t.getEndDate() == null  && !userIds.contains(t.getUserId()) //Last Filter Condition Added For the User ROle Sessions
			        )
			        .sorted(Comparator
			                .comparing(SessionDto::getUutId)
			                .thenComparing(SessionDto::getDfccSNo))
			        .collect(Collectors.toList());

			
			for (SessionDto sessionName : filterSessionList) {
				sessionTypeList.add(sessionName.getSessionName());
			}
			sessionNameField.setItems(sessionTypeList);
			addSearchFunctionality(sessionNameField, sessionTypeList);
			sessionNameField.setOnAction(event -> {
				String selectedSession = sessionNameField.getSelectionModel().getSelectedItem();
				////System.out.println("selectedSession" + selectedSession);
				String sessionId = fetchSessionId(selectedSession);
				////System.out.println("Check Session ID" + sessionId);
				getCurrentUnitResultData(selectedUttId, sessionId);
				session_ID = sessionId;
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
		                    Objects.equals(t.getDfccSNo(), selectedSno) &&
		                    t.getEndDate() == null
		            )
		            .sorted(Comparator
		                    .comparing(TrailSessionDto::getUutId)
		                    .thenComparing(TrailSessionDto::getDfccSNo))
		            .collect(Collectors.toList());

		    for (TrailSessionDto sessionName : filterSessionList) {
		        sessionTypeList.add(sessionName.getSessionName());
		    }

		    sessionNameField.setOnAction(event -> {
		        String selectedSession = sessionNameField.getSelectionModel().getSelectedItem();
		        ////System.out.println("selectedTrialSession: " + selectedSession);

		        String sessionIdTrial = fetchSessionTrialId(selectedSession);
		        ////System.out.println("Trial Session ID: " + sessionIdTrial);

		        getCurrentUnitResultData(selectedUttId, sessionIdTrial);
		        session_ID = selectedSession;
		    });

		    sessionNameField.setItems(sessionTypeList);
		}

		
		

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentUnitResultHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("UNIT RESULTS");
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);

		currentUnitResultHeadingGridPane.add(titleBox, 0, 0);

		return currentUnitResultHeadingGridPane;
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
			String uutId = fetchUutId(selectedUUTType);
			selectedUttId=uutId;
			
			initializeDfccSNComboBox(uutId);
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
	
	private String fetchSessionId(String sessionType) {
		for (SessionDto sessionId : sessionList) {
			if (sessionId.getSessionName().equals(sessionType)) {
				return sessionId.getSessionId();
			}
		}
		return null;
	}
	
	private String fetchSessionTrialId(String sessionType) {
		for (TrailSessionDto sessionId : sessionListTrail) {
			if (sessionId.getSessionName().equals(sessionType)) {
				return sessionId.getSessionId();
			}
		}
		return null;
	}

	private GridPane createcurrentUnitResultTableGridPane() {
		currentUnitResultTableGridPane.getStyleClass().add("current-execution-result-tabs-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentUnitResultTableGridPane.setPadding(new Insets(5));

		currentUnitResultTableGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultTableGridPane.getRowConstraints().addAll(firstRow);

		currentUnitResultTableGridPane.add(createCurrentUnitResultTable(), 0, 0);
		return currentUnitResultTableGridPane;
	}
	
	ViewReportController viewReportController = new ViewReportController();
	
	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);
		downloadButton.setOnAction(e -> {

		    if (selectedUttId == null || selectedSno == null) {
		        Notifications.showErrorAlert("Please Select UUT Type, Serial No, properly.");
		        return;
		    }

		    Task<Response> task = new Task<>() {
		        @Override
		        protected Response call() throws Exception {
		            // 🔥 Heavy work runs in background thread
		            return reportGeneration.generateBreifReportForCurrentUutType(
		                    selectedUttId,
		                    selectedSno,
		                    session_ID
		            );
		        }
		    };

		    // ✅ Show indicator automatically when task starts
		    task.setOnRunning(evt -> showProgressIndicator());

		    // ✅ When task finishes successfully
		    task.setOnSucceeded(evt -> {
		        hideProgressIndicator();

		        Response res = task.getValue();
		        viewReportController.viewReportPopup(res);

		        // Clear selections AFTER report opens
		        sessionNameField.getSelectionModel().clearSelection();
		        uutTypeField.getSelectionModel().clearSelection();
		        slNoField.getSelectionModel().clearSelection();
		    });

		    // ❌ If something fails
		    task.setOnFailed(evt -> {
		        hideProgressIndicator();
		        task.getException().printStackTrace();
		        Notifications.showErrorAlert("Error generating report.");
		    });

		    new Thread(task).start();
		});
		return buttonBox;
	}

	private ScrollPane createCurrentUnitResultTable() {

		unitDataTableView = unitDataFactory.createTableView(unitDataList, true, false);
		
//		Changed by Vignesh 31-07-25 for moving icon location
		if(!unitDataTableView.getColumns().isEmpty()) {
			TableColumn<UnitData, ?> lastColumn = unitDataTableView.getColumns().get(unitDataTableView.getColumns().size() - 1);
			//unitDataTableView.getColumns().removeLast();
			//Mani Changes By Vignesh Comment
			unitDataTableView.getColumns().remove(lastColumn);
			unitDataTableView.getColumns().add(0, lastColumn);	
		}

		unitDataTableView.getColumns().forEach(column -> {
			if (!column.getText().isEmpty()) {
				column.setMinWidth(column.getText().length() * 14);
//				updateUnitData((TableColumn<UnitData, String>) column);
			}
		});
//Berfore Changing for Loadung cursor
		unitDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
//			Platform.runLater(() -> {
//		        currentUnitResultGridPane.getScene().setCursor(Cursor.WAIT);
//		        currentUnitResultGridPane.getScene().getRoot().setDisable(true);
//		    });
			ObservableList<UnitData> selectedItems = unitDataTableView.getSelectedItems();
			for (UnitData rowData : selectedItems) {
				
				UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

				GridPane bottomMidTopGridPane = (GridPane) currentUnitResultGridPane.getParent().getParent()
						.getParent();
				UserCenterContentController.currentSessionName = rowData.getSessionName(); // srini 2/8/25

				userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Session Results",
						rowData.getId(), null);
				break;
			}
//			Platform.runLater(() -> {
//			currentUnitResultGridPane.getScene().setCursor(Cursor.DEFAULT);
//			currentUnitResultGridPane.getScene().getRoot().setDisable(false);
//			 });
		});
		
////		After Loading for Cursor Wait
//		unitDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
//		    Platform.runLater(() -> {
//		        currentUnitResultGridPane.getScene().setCursor(Cursor.WAIT);
//		        setControlsDisabled(currentUnitResultGridPane.getScene().getRoot(), true);
//		    });
//
//		    ObservableList<UnitData> selectedItems = unitDataTableView.getSelectedItems();
//
//		    if (!selectedItems.isEmpty()) {
//		        UnitData rowData = selectedItems.get(0); // Process only the first item
//		        UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();
//		        GridPane bottomMidTopGridPane = (GridPane) currentUnitResultGridPane.getParent().getParent().getParent();
//
//		        // Run data loading in a separate thread
//		        Task<Void> task = new Task<>() {
//		            @Override
//		            protected Void call() {
//		            	 currentUnitResultGridPane.getScene().setCursor(Cursor.DEFAULT);
//		                userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Session Results",
//		                        rowData.getId(), null);
//		               
//		                return null;
//		            }
//
//		          
//		        };
//
//		        new Thread(task).start();
//		    }
//		});


		tableScrollPane.setContent(unitDataTableView);
		tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}
	
	private void setControlsDisabled(Node root, boolean disabled) {
	    for (Node node : root.lookupAll("*")) {
	        if (node instanceof Control) {
	            ((Control) node).setDisable(disabled);
	        }
	    }
	}

//	private void updateUnitData(TableColumn<UnitData, String> column) {
//		column.setCellFactory(col -> new TableCell<UnitData, String>() {
//			private Label label;
//
//			@Override
//			protected void updateItem(String item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item == null || empty) {
//					setText(null);
//					setGraphic(null);
//				} else {
//					if (label == null) {
//						label = new Label();
//						label.setWrapText(false);
//						label.setAlignment(Pos.CENTER);
//						setStyle("-fx-alignment: CENTER;");
//						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//					}
//					label.setText(item);
//					label.setStyle("-fx-text-fill: white;");
//					label.setMinWidth(label.getText().length() * 12);
//					setGraphic(label);
//					this.setMinWidth(label.getText().length() * 12);
//					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
//				}
//			}
//		});
//	}
	
	 private void showProgressIndicator() {
			StackPane parentStackPane= (StackPane) currentUnitResultGridPane.getParent().getParent();
		if (!parentStackPane.getChildren().contains(progressbox)) {
			progressbox.getChildren().add(progressIndicator);
			progressbox.setAlignment(Pos.CENTER);
			parentStackPane.getChildren().add(progressbox);
		}
	}

		private void hideProgressIndicator() {
			StackPane parentStackPane= (StackPane) currentUnitResultGridPane.getParent().getParent();
			if(parentStackPane.getChildren().contains(progressbox)) {
				parentStackPane.getChildren().remove(progressbox);
			progressbox.getChildren().clear(); // Clean up for next use
			}
		}

}
