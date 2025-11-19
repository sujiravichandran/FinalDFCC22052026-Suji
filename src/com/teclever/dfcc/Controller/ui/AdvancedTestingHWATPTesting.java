package com.teclever.dfcc.Controller.ui;

import java.io.File;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionTiming;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.SessionTimingService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.StatusBarTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.PopupDialoguShow;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AdvancedTestingHWATPTesting {

	private static final Object COMPLETED = null;
	private static final Object ADVANCED_TEST_HWATP_TEST = null;
	private GridPane tab1MainGridPane = new GridPane();
	private VBox leftSideVBox = new VBox(20);
	private GridPane rightSideGridPane = new GridPane();

	private ObservableList<TestCardData> stageList = FXCollections.observableArrayList();

	private ObservableMap<String, String> testFileMap;

	private VBox searchVBox = new VBox(5);
	private TextField testNameField = new TextField();

	private List<RadioButton> stageListRadioButtons = new ArrayList<>();
	private ListView<RadioButton> stageListView = new ListView<>();

	private List<CheckBox> checkBoxes = new ArrayList<>();
	private ListView<CheckBox> testListView = new ListView<>();
	private VBox testListVBox = new VBox();
	private HBox buttonHBox = new HBox(5);
	private Button startButton = new Button("Start");
	private Button runAllButton = new Button("Run All");

	private Button stopButton = new Button("Stop");
	private Button pauseButton = new Button("Pause");

	private HBox repeatCountHBox = new HBox();
	private Label repeatCountLabel = new Label();
	private Label repeatNotLabel = new Label();
	private TextField repeatCountTextField = new TextField();
	
	private boolean testFileCount = false;

	private String selectedStageId = null;
	private String selectedTestTypeId = null;

	private HBox buttonMainHBox = new HBox(15);
	private HBox allButtonHBox = new HBox(5);
	private HBox progressBarHBox = new HBox(5);
	private ProgressBar testProgressBar = new ProgressBar();
	private Label percentageLabel = new Label("0%");

	private RunConfigurationService runConfigurationService = new RunConfigurationService();
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private TestProcessManagement testProcessManagement = new TestProcessManagement();
	private CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	private PopupDialoguShow popupDialoguShow = new PopupDialoguShow();
	
	private boolean aitess1Updated = false;
	private boolean aitess2Updated = false;

	public AdvancedTestingHWATPTesting() {
		StateMachine.setStageName("ADVANCED HWATP TEST");
	
		initializeSearch();

//		SUJI added for resetting the progress bar once test file are moved::
		StateMachine.resettingProgressBarProperty().addListener((obs, oldVal, newVal) -> {
			Platform.runLater(() -> {
//	    		System.out.println("Entred resetting Progress in Advanced test");
	    	testProgressBar.setProgress(0);
	    	percentageLabel.setText("0%");
	    	StateMachine.setResettingProgressBar(false);
	    	});
		});
		
//		Edited By: SUJI
//		Change Made for Point: 4&39(Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//		Change Made on WDM Status off: popup confirmation Test to proceed or not:
		StateMachine.wdmStatusOfflineCheckProperty().addListener((obs, oldVal, newVal) -> {
//			System.out.println("WDM Status changed:Advaced HWATP " + newVal);
			if(newVal && (StateMachine.getStatusBarRunningTestName().equals("ADVANCED_TEST_HWATP_TEST"))) {
				
			StateMachine.setTestState(TestState.PAUSED);
			 Platform.runLater(() -> {
					popupDialoguShow.wdmStatusPopup();
					StateMachine.setWdmStatusOfflineCheck(false);
					 });
			}
		
		});
		
		StateMachine.cancelTestProperty().addListener((obs, oldVal, newVal) -> {
			
			if(newVal && (StateMachine.getStatusBarRunningTestName().equals("ADVANCED_TEST_HWATP_TEST"))) {
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
				
			}
			StateMachine.setCancelTest(false);
		});
		
	
//		Exit;
//	    Point: 4&39
		
//		SUJI ADDED:
		StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
			if (newState == TestState.STOPPED && !StateMachine.isConfirmTestFileCompleted()) {
			applyUiStatus(StateMachine.getTestState());
			}
		});
		
		
		StateMachine.confirmTestFileCompletedProperty().addListener((obs, oldVal, newVal) -> {
			applyUiState(newVal); 
		});
//		EXIT::
		
	}
	

		
	private void clearTextField() {
		if (!testNameField.getText().isEmpty()) {
			testNameField.clear();
		}
	}

	
	// Edited By: SUJI
//		Change Made for Point: 79(Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//		Change Made on Search bar is not working properly.
	public void initializeSearch() {

		testNameField.setPromptText("Search...");

		testNameField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));

//		testNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//			if (!newValue) {
//				clearTextField();
//			}
//		});

	}
//	Exit;
//	Change Made for Point:  79
	
//	SUJI ADDED::
	private void applyUiStatus(TestState currentState) {
		
	    Platform.runLater(() -> {
             leftSideVBox.setDisable(false);
			 rightSideGridPane.setDisable(false);
	    });
	}
	
	private void applyUiState(boolean isTestFileCompleted) {
	    Platform.runLater(() -> {
	        if (!isTestFileCompleted) {
	        	 leftSideVBox.setDisable(false);
				 rightSideGridPane.setDisable(false);
	        }
	    });
	}
	
//	EXIT::
	
	public GridPane createAdvancedTestingTab1GridPane() {
	
		getHWATPTestingStagesData();
		
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(78);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(22);

		tab1MainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		tab1MainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		tab1MainGridPane.setHgap(5);
		tab1MainGridPane.setVgap(5);
		
		

		tab1MainGridPane.add(createLeftSide(), 0, 0);
		tab1MainGridPane.add(createRightSide(), 1, 0);
		tab1MainGridPane.add(createButtonBox(),0,1, 2, 1);

		return tab1MainGridPane;
	}
	
	// Edited By: SUJI
//	Change Made for Point: 52,72,74(Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//	Change Made on:During initial loading of testing window,Update Status Bar
	public void initialize() {
		StateMachine.aitess1LaunchedProperty().addListener((obs, oldVal, newVal) -> {
		    aitess1Updated = true;
		    Platform.runLater(() -> {
			    checkBothAitessLaunched();
			    });
		});

		StateMachine.aitess2LaunchedProperty().addListener((obs, oldVal, newVal) -> {
		    aitess2Updated = true;
		    Platform.runLater(() -> {
		    checkBothAitessLaunched();
		    });
		});
		
	}
	
	// Method to check both
	
		
		private void checkBothAitessLaunched() {
		    if (aitess1Updated && aitess2Updated) {
		        boolean bothLaunched = StateMachine.aitess1LaunchedProperty().get()
		                                 && StateMachine.aitess2LaunchedProperty().get();
		        if (bothLaunched) {

		            // ✅ This ensures all UI updates are done on the JavaFX Application Thread
		            Platform.runLater(() -> {

		        		
		            	tab1MainGridPane.setDisable(false);
		        			

		            });
		        }
		    }
		}
//		Exit;
//		Point: 52,72,74

//	Before Suji change on - 02-05-2025
	private void getHWATPTestingStagesData() {
		ObservableList<StageObject> observableStageList = FXCollections
				.observableArrayList(StateMachine.getStageDatalist());
		observableStageList.stream()
				.filter(stage -> "Advanced Test".equalsIgnoreCase(stage.getL1StageName())
						&& "HWATP/HSI".equalsIgnoreCase(stage.getL2StageName()))
				.filter(stage -> stage.getL3StageId() != null && stage.getL3StageName() != null).forEach(stage -> {
					TestCardData newCard = new TestCardData(stage.getL3StageId(), stage.getL3StageName(),
							stage.getTestTypeId(), null);
					AdvancedTestStateObject.addHwatpTestList(newCard);
				});
	}
	
//	Before Suji change on - 02-05-2025
//	private void getHWATPTestingStagesData() {
//	    ObservableList<StageObject> observableStageList = FXCollections
//	            .observableArrayList(StateMachine.getStageDatalist());
//
//	    observableStageList.stream()
//	            .filter(stage -> "Advanced Test".equalsIgnoreCase(stage.getL1StageName())
//	                    && "HWATP/HSI".equalsIgnoreCase(stage.getL2StageName()))
//	            .filter(stage -> stage.getL3StageId() != null && stage.getL3StageName() != null)
//	            .forEach(stage -> {
//	            	
//	                TestCardData newCard = new TestCardData(stage.getL3StageId(), stage.getL3StageName(),
//	                        stage.getTestTypeId(), null);
//	                
//	                AdvancedTestStateObject.addHwatpTestList(newCard);
//	            });
//	}

	
	private VBox createLeftSide() {
		
			
		
		leftSideVBox.getStyleClass().add("advanced-testing-left-container");
		stageListView.getStyleClass().add("advanced-testing-radio-list-view");
		stageList.clear();
		stageListView.getItems().clear();

		ToggleGroup toggleGroup = new ToggleGroup();
		stageList = AdvancedTestStateObject.getHwatpTestList();

	
		
		for (TestCardData stage : stageList) {
			RadioButton newRadioButton = new RadioButton(stage.getCardName());
			newRadioButton.setMnemonicParsing(false);
			newRadioButton.setUserData(stage);
			newRadioButton.getStyleClass().add("advanced-testing-radio-button");
			newRadioButton.setWrapText(true);
			newRadioButton.setToggleGroup(toggleGroup);
			stageListRadioButtons.add(newRadioButton);
			stageListView.getItems().add(newRadioButton);
		}

		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
			
				RadioButton selectedRadioButton = (RadioButton) newValue;
				TestCardData stageData = (TestCardData) selectedRadioButton.getUserData();
				getTestListByStageId(stageData.getCardId(), stageData.getTestTypeId());
				selectedStageId = stageData.getCardId();
				selectedTestTypeId = stageData.getTestTypeId();
				
				clearTextField();
				
//				For Clearing Table ADta and Moving Confirmation Flag:
//				if (StateMachine.getTestState() != TestState.RUNNING) {
//					StateMachine.setAdvancedTestOk(true);
//
//				}
			}
		});

		leftSideVBox.setAlignment(Pos.CENTER);
		leftSideVBox.getChildren().addAll(stageListView);
		return leftSideVBox;
	}

	private VBox createSearchFile() {
		searchVBox.getStyleClass().add("advanced-testing-right-text-field");
		searchVBox.setAlignment(Pos.CENTER);
		searchVBox.getChildren().add(testNameField);
		return searchVBox;

	}

	private GridPane createRightSide() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(12);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(88);

//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(32);

		rightSideGridPane.getColumnConstraints().addAll(firstColumn);
		rightSideGridPane.getRowConstraints().addAll(firstRow, secondRow);
		rightSideGridPane.setVgap(5);
		rightSideGridPane.add(createSearchFile(), 0, 0);
		rightSideGridPane.add(createTestListView(), 0, 1);
		//rightSideGridPane.add(createButtonBox(), 0, 2);
		return rightSideGridPane;
	}

	private VBox createTestListView() {
//		testListView.getStyleClass().add("advanced-testing-list-view");

		testListVBox.getChildren().add(testListView);
		testListVBox.getStyleClass().add("advanced-testing-right-container");
		return testListVBox;
	}

	private void getTestListByStageId(String stageId, String testTypeId) {

		String runConfigId = runConfigurationService
				.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
		currentSessionDetails.setRunConfigId(runConfigId);
		String ID = stageId;
		TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
		if (testFileResponse.getTestFilesIdName() == null) {
			testListView.getItems().clear();
			Platform.runLater(() -> {
				Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
			});
			return;
		}

		testFileMap = FXCollections.observableMap(testFileResponse.getTestFilesIdName());

		setTestListViewData(testFileMap, false, stageId);
	}

	private void setTestListViewData(ObservableMap<String, String> testFileMap, boolean checkboxDisable,
			String stageId) {
		testListView.getItems().clear();
		checkBoxes.clear();

		for (Map.Entry<String, String> entry : testFileMap.entrySet()) {
			String filePath = entry.getValue();
			File file = new File(filePath);
			CheckBox newCheckBox = new CheckBox(file.getName());
			newCheckBox.setMnemonicParsing(false);
			newCheckBox.setId(entry.getKey());
			newCheckBox.getStyleClass().add("advanced-testing-checkbox");
			newCheckBox.setWrapText(true);
			if (checkboxDisable) {
				newCheckBox.setDisable(checkboxDisable);
			}
			checkBoxes.add(newCheckBox);
			testListView.getItems().add(newCheckBox);

			newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				boolean anySelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);
				if (StateMachine.getTestState() != TestState.RUNNING) {
					startButton.setDisable(!anySelected);
				}
			});
		}
	}

	private void filterList(String keyword) {
		String trimmedKeyword = keyword.trim();

		if (trimmedKeyword.isEmpty()) {
			setTestListViewData(testFileMap, false, "stageId");
			return;
		} else {
			ObservableMap<String, String> filteredMap = FXCollections.observableHashMap();
			for (Map.Entry<String, String> entry : testFileMap.entrySet()) {

				if (entry.getValue().toLowerCase().contains(trimmedKeyword.toLowerCase())) {
					filteredMap.put(entry.getKey(), entry.getValue());
				}

			}

			setTestListViewData(filteredMap, false, "stageId");
		}

	}

	private void showAlert() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Input Required");
		alert.setHeaderText(null);
		alert.setContentText("Please enter a repeat count between 1 and 100.");
		alert.showAndWait();
	}

	private HBox createButtonBox() {
		buttonHBox.getStyleClass().add("advanced-testing-right-container");
		Image playImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/play.png"));
		Image stopImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/stop.png"));
		Image pauseImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/pause.png"));

		ImageView playImageView = new ImageView(playImage);
		playImageView.getStyleClass().add("button-image");
		playImageView.setFitHeight(25);
		playImageView.setFitWidth(25);
		playImageView.setPreserveRatio(true);
		playImageView.setSmooth(true);

		ImageView stopImageView = new ImageView(stopImage);
		stopImageView.getStyleClass().add("button-image");
		stopImageView.setFitHeight(25);
		stopImageView.setFitWidth(25);
		stopImageView.setPreserveRatio(true);
		stopImageView.setSmooth(true);

		ImageView pauseImageView = new ImageView(pauseImage);
		pauseImageView.getStyleClass().add("button-image");
		pauseImageView.setFitHeight(25);
		pauseImageView.setFitWidth(25);
		pauseImageView.setPreserveRatio(true);
		pauseImageView.setSmooth(true);

		startButton.setGraphic(playImageView);
		startButton.setGraphicTextGap(10);
		stopButton.setGraphic(stopImageView);
		stopButton.setGraphicTextGap(10);
		pauseButton.setGraphic(pauseImageView);
		pauseButton.setGraphicTextGap(10);

		startButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
	
	
		
		runAllButton.setOnAction(e -> {
			StateMachine.setConfirmTestStop(false);
			
			if (StateMachine.isConfirmTestFileCompleted()) {
				Notifications.showWarningAlert("Please Wait until" +StateMachine.getRunningTestName() +" test Completes");
				return;
			}
			
			
			// Set current stage ID
		    StateMachine.setCurrentlySelectedStageId(selectedStageId);

		    // Get previous and current stage IDs
		    String currentStageId = StateMachine.getCurrentlySelectedStageId();
		    String previousStageId = StateMachine.getPreviouslySelectedStageId();

		    // If previous stage is null, initialize it
		    if (previousStageId == null) {
		        StateMachine.setPreviouslySelectedStageId(currentStageId);
		    } else if (!currentStageId.equalsIgnoreCase(previousStageId)) {
		        // Stage has changed — execute RDF block

		        if (!DFCCConstant.FailedStagesRdfPaths.isEmpty()) {
		            boolean popupRDFFiles = false;

		            for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
		                if ("FAILURE".equalsIgnoreCase(copyFileDTO.getStatus())) {
		                    popupRDFFiles = true;
		                    break;
		                }
		            }

		            SessionManagement sessionManagement = new SessionManagement();

		            if (popupRDFFiles) {
		                // Log failed stage
		                sessionManagement.updateSessionStagesResultOnApplicationLogBook(
		                    "Failed", DFCCConstant.FailedStagesRdfPaths.get(0).getStageId()
		                );

		                RdfFileCopyPopupController.rdfFilesListtoShow = new ArrayList<>(DFCCConstant.FailedStagesRdfPaths);
		                SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
		            } else {
		                // Log passed stage
		                sessionManagement.updateSessionStagesResultOnApplicationLogBook(
		                    "Passed", DFCCConstant.FailedStagesRdfPaths.get(0).getStageId()
		                );

		                SessionFileManagement session = new SessionFileManagement();
		                session.copyFilesToOutputFolderWhilePlayButton(DFCCConstant.FailedStagesRdfPaths);
		            }

		            // Clear the list after processing
		            DFCCConstant.FailedStagesRdfPaths.clear();
		            StateMachine.setResettingProgressBar(true);
		        }

		        // Clear advanced test result list on stage change
		        SessionTestStateObject.clearSessionTestResults();
		        SelfTestStateObject.clearselfTestResults();
		        AdvancedTestStateObject.clearAdvancedTestResultsList();
		        LRUTestStateObject.clearlruTestResultsList();

		        // Update previous stage ID to current after processing
		        StateMachine.setPreviouslySelectedStageId(currentStageId);
		    }
			
			// Excel Name:7-July-Observation
            // Point No:3
			// Change Made on rdf file moving
//			if (StateMachine.isAdvancedTestOk()) {
//			SUJI CHANGED CONDITION FOR MOVING BASED ON STAGE ID::(03092025)
			
//			if (StateMachine.getPreviouslySelectedStageId() == null
//					|| StateMachine.getCurrentlySelectedStageId()
//							.equalsIgnoreCase(StateMachine.getPreviouslySelectedStageId())) {
//				StateMachine.setPreviouslySelectedStageId(selectedStageId);
//			}
//			
//			
//			if(StateMachine.getCurrentlySelectedStageId() != StateMachine.getPreviouslySelectedStageId()) {
//
//				//09-07-2025
//				// UPDATING TIME TO DB
//				LocalDateTime currentDateTime = LocalDateTime.now();
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
//				String formattedDate = currentDateTime.format(formatter);
//				
//				StateMachine.setCurrentlySelectedStageId(selectedStageId); 
//				
//				SessionTimingService s = new SessionTimingService();
//				//LOGIC FOR UPDATING SESSION TIMING TABLE
//				if (StateMachine.getPreviouslySelectedStageId() == null
//						|| StateMachine.getCurrentlySelectedStageId()
//								.equalsIgnoreCase(StateMachine.getPreviouslySelectedStageId())) {
//					StateMachine.setPreviouslySelectedStageId(selectedStageId);
//					
//				} else {
//
//					int failedFiles = 0;
//					for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
//						if (!copy.getStatus().equals("SUCCESS")) {
//							failedFiles++;
//						}
//					}
//					StateMachine.setPreviouslySelectedStageId(selectedStageId);
//
//				}
//				
//				
//				
//				// Checking With List
//				boolean popupRDFFiles = false;
//				if (DFCCConstant.FailedStagesRdfPaths.size() > 0) {
//					for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
//						if (copyFileDTO.getStatus().equalsIgnoreCase("FAILURE")) {
//							popupRDFFiles = true;
//						}
//					}
//					
//				
//					
//
//					if (popupRDFFiles) {
//						//Logbook the Stage Results
//						SessionManagement sessionManagement = new SessionManagement();
//						sessionManagement.updateSessionStagesResultOnApplicationLogBook("Failed",DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//
//						
//						
//						RdfFileCopyPopupController.rdfFilesListtoShow = new ArrayList<CopyFileDTO>();
//						for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
//
//							RdfFileCopyPopupController.rdfFilesListtoShow.add(copyFileDTO);
//						}
//						SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
//						DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//					} else {
//						SessionManagement sessionManagement = new SessionManagement();
//						sessionManagement.updateSessionStagesResultOnApplicationLogBook("Passed",DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//
//						SessionFileManagement session = new SessionFileManagement();
//						session.copyFilesToOutputFolderWhilePlayButton(DFCCConstant.FailedStagesRdfPaths);
//						DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//					}
//				}
//
//
//				AdvancedTestStateObject.clearAdvancedTestResultsList();
//				
////		        StateMachine.setAdvancedTestOk(false);
//			}
			
			// Exit
			// Point No:3
			
			
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Run All in HWATP/HSI Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			
			if(StateMachine.isMacroPassing()) {
				return;
			}

			List<String> testFileIds = new ArrayList<>();
			for (CheckBox checkbox : checkBoxes) {
				if (!checkbox.isDisable()) {
					testFileIds.add(checkbox.getId());
				}
			}
			if (testFileIds.size() == 0) {
				testFileCount = true;
				Notifications.showWarningAlert("Please Select Test File...");
				return;
			}
			
//			SUJI ADDED for DISABLE ENABLE LEFT & RIGHT window:
			if(!testFileCount) {
				 Platform.runLater(() -> {
					 leftSideVBox.setDisable(true);
					 rightSideGridPane.setDisable(true);
				testFileCount=false;
				 });
				}
//			EXIT

			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {

				startButton.setDisable(true);
				runAllButton.setDisable(true);
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName. ADVANCED_TEST);
				// Excel Name:7-July-Observation
                // Point No:18
				// Change Made on Status Bar Test Name
				StateMachine.setStatusBarRunningTestName(StatusBarTestName.ADVANCED_TEST_HWATP_TEST);
				// Exit
                //Point No:18
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}
			callStartTest(selectedStageId, "HWATP TEST", selectedTestTypeId, testFileIds);
		});

		startButton.setOnAction(e -> {
			
			if (!startButton.getText().equalsIgnoreCase("Resume")) {

//				// Excel Name:7-July-Observation
//				// Point No:3
//				// Change Made on rdf file moving
//				
//				// New Implemented.....Mani
////				SUJI CHANGED CONDITION FOR MOVING BASED ON STAGE ID::(03092025)
////				if (StateMachine.isAdvancedTestOk()) {
//				
//				if (StateMachine.getPreviouslySelectedStageId() == null
//						|| StateMachine.getCurrentlySelectedStageId()
//								.equalsIgnoreCase(StateMachine.getPreviouslySelectedStageId())) {
//					StateMachine.setPreviouslySelectedStageId(selectedStageId);
//				}
//				
//				if(StateMachine.getCurrentlySelectedStageId() != StateMachine.getPreviouslySelectedStageId()) {
//
//
//					// 09-07-2025
//					// UPDATING TIME TO DB
//					LocalDateTime currentDateTime = LocalDateTime.now();
////					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
////					String formattedDate = currentDateTime.format(formatter);
//					StateMachine.setCurrentlySelectedStageId(selectedStageId);
//
//					SessionTimingService s = new SessionTimingService();
//					// LOGIC FOR UPDATING SESSION TIMING TABLE
//					if (StateMachine.getPreviouslySelectedStageId() == null
//							|| StateMachine.getCurrentlySelectedStageId()
//									.equalsIgnoreCase(StateMachine.getPreviouslySelectedStageId())) {
//						StateMachine.setPreviouslySelectedStageId(selectedStageId);
//
//
//					} else {
//
//						int failedFiles = 0;
//						for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
//							if (!copy.getStatus().equals("SUCCESS")) {
//								failedFiles++;
//							}
//						}
//						
//					}
//
//					// Checking With List
//					boolean popupRDFFiles = false;
//					if (DFCCConstant.FailedStagesRdfPaths.size() > 0) {
//						for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
//
//							if (copyFileDTO.getStatus().equalsIgnoreCase("FAILURE")) {
//								popupRDFFiles = true;
//
//							}
//
//						}
//
//						if (popupRDFFiles) {
//
//							// Logbook the Stage Results
//							SessionManagement sessionManagement = new SessionManagement();
//							sessionManagement.updateSessionStagesResultOnApplicationLogBook("Failed",
//									DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//
//							RdfFileCopyPopupController.rdfFilesListtoShow = new ArrayList<CopyFileDTO>();
//							for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
//								RdfFileCopyPopupController.rdfFilesListtoShow.add(copyFileDTO);
//							}
//							SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
//							DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//						} else {
//							// Logbook the Stage Results
//							SessionManagement sessionManagement = new SessionManagement();
//							sessionManagement.updateSessionStagesResultOnApplicationLogBook("Passed",
//									DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//
//							SessionFileManagement session = new SessionFileManagement();
//							session.copyFilesToOutputFolderWhilePlayButton(DFCCConstant.FailedStagesRdfPaths);
//							DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//						}
//					}
//
//					AdvancedTestStateObject.clearAdvancedTestResultsList();
//
//					StateMachine.setAdvancedTestOk(false);
//				}

				// Exit
				// Point No:3

				StateMachine.setConfirmTestStop(false);
				if (StateMachine.isConfirmTestFileCompleted()) {

					Notifications.showWarningAlert(
							"Please Wait until" + StateMachine.getRunningTestName() + " test Completes");
					return;
				}
			}
			
			// Set current stage ID
		    StateMachine.setCurrentlySelectedStageId(selectedStageId);

		    // Get previous and current stage IDs
		    String currentStageId = StateMachine.getCurrentlySelectedStageId();
		    String previousStageId = StateMachine.getPreviouslySelectedStageId();

		    // If previous stage is null, initialize it
		    if (previousStageId == null) {
		        StateMachine.setPreviouslySelectedStageId(currentStageId);
		    } else if (!currentStageId.equalsIgnoreCase(previousStageId)) {
		        // Stage has changed — execute RDF block

		        if (!DFCCConstant.FailedStagesRdfPaths.isEmpty()) {
		            boolean popupRDFFiles = false;

		            for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
		                if ("FAILURE".equalsIgnoreCase(copyFileDTO.getStatus())) {
		                    popupRDFFiles = true;
		                    break;
		                }
		            }

		            SessionManagement sessionManagement = new SessionManagement();

		            if (popupRDFFiles) {
		                // Log failed stage
		                sessionManagement.updateSessionStagesResultOnApplicationLogBook(
		                    "Failed", DFCCConstant.FailedStagesRdfPaths.get(0).getStageId()
		                );

		                RdfFileCopyPopupController.rdfFilesListtoShow = new ArrayList<>(DFCCConstant.FailedStagesRdfPaths);
		                SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
		            } else {
		                // Log passed stage
		                sessionManagement.updateSessionStagesResultOnApplicationLogBook(
		                    "Passed", DFCCConstant.FailedStagesRdfPaths.get(0).getStageId()
		                );

		                SessionFileManagement session = new SessionFileManagement();
		                session.copyFilesToOutputFolderWhilePlayButton(DFCCConstant.FailedStagesRdfPaths);
		            }

		            // Clear the list after processing
		            DFCCConstant.FailedStagesRdfPaths.clear();
		            StateMachine.setResettingProgressBar(true);
		        }

		        // Clear advanced test result list on stage change
		        SessionTestStateObject.clearSessionTestResults();
		        SelfTestStateObject.clearselfTestResults();
		        AdvancedTestStateObject.clearAdvancedTestResultsList();
		        LRUTestStateObject.clearlruTestResultsList();

		        // Update previous stage ID to current after processing
		        StateMachine.setPreviouslySelectedStageId(currentStageId);
		    }
			
			if (startButton.getText().equalsIgnoreCase("Resume")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Resume in HWATP/HSI Testing");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			} else {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Start in HWATP/HSI Testing");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			}

			if (repeatCountTextField == null) {
				showAlert();
			}

			if (startButton.getText().equalsIgnoreCase("Resume")) {
				StateMachine.setTestState(TestState.RUNNING);

				startButton.setText("Start");
				startButton.setDisable(true);
				pauseButton.setDisable(false);
				stopButton.setDisable(false);
				return;
			}
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			
			if(StateMachine.isMacroPassing()) {
				return;
			}

			List<String> testFileIds = new ArrayList<>();
			for (CheckBox checkbox : checkBoxes) {
				if (checkbox.isSelected()) {
					testFileIds.add(checkbox.getId());
				}
			}
			if (testFileIds.size() == 0) {
				testFileCount = true;
				Notifications.showWarningAlert("Please Select Test File...");
				return;
			}
			
//			SUJI ADDED for DISABLE ENABLE LEFT & RIGHT window:
			if(!testFileCount) {
				 Platform.runLater(() -> {
					 leftSideVBox.setDisable(true);
					 rightSideGridPane.setDisable(true);
					 testFileCount=false;
				 });
				}
//			EXIT
			
			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {

				startButton.setDisable(true);
				runAllButton.setDisable(true);
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName. ADVANCED_TEST);
				// Excel Name:7-July-Observation
                // Point No:18
				// Change Made on Status Bar Test Name
				StateMachine.setStatusBarRunningTestName(StatusBarTestName.ADVANCED_TEST_HWATP_TEST);
				// Exit
                //Point No:18
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}
			
			
			
			callStartTest(selectedStageId, "HWATP TEST", selectedTestTypeId, testFileIds);
		});

		pauseButton.setOnAction(e -> {
			StateMachine.setConfirmTestStop(true);
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Pause in HWATP/HSI Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			StateMachine.setTestState(TestState.PAUSED);
			startButton.setText("Resume");
			pauseButton.setDisable(true);
			startButton.setDisable(false);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e -> {
			if(!StateMachine.isConfirmTestStop()) {
				Notifications.showErrorAlert("Please Wait Aitess is Switching");
				return;
			}else {
				StateMachine.setConfirmTestStop(false);
			}
			
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on Stop in HWATP/HSI Testing");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

			StateMachine.setTestState(TestState.STOPPED);
			startButton.setText("Start");
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			startButton.setDisable(false);
			runAllButton.setDisable(false);

		});

		repeatCountLabel.setText("Repeat Count(1 to 100)");
		//repeatNotLabel.setText("(Note:Enter Value from 1 to 100)");
		repeatNotLabel.setTextFill(Color.WHITE);
		repeatNotLabel.setWrapText(true);
		repeatCountLabel.getStyleClass().add("advanced-testing-repeat-count-label");
		repeatCountTextField.getStyleClass().add("advanced-testing-repeat-count-input");
		repeatCountTextField.setText("1");
		repeatCountHBox.getStyleClass().add("repeat-count-vbox");
		repeatCountTextField.setAlignment(Pos.CENTER);

		TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
		    String newText = change.getControlNewText();

		    // Allow empty string while typing (but fix on focus lost)
		    if (newText.isEmpty()) {
		        return change;
		    }

		    try {
		        int value = Integer.parseInt(newText);
		        if (value >= 1 && value <= 100) {
		            return change;
		        }
		    } catch (NumberFormatException e) {
		        // Invalid characters like "a", etc.
		    }

		    return null; // Reject the change
		});
		repeatCountTextField.setTextFormatter(textFormatter);

		// On focus lost, reset to "1" if the field is empty or invalid
		repeatCountTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
		    if (!newVal) {
		        String text = repeatCountTextField.getText();
		        if (text == null || text.isEmpty()) {
		            repeatCountTextField.setText("1");
		        }
		    }
		});

//		repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//		    if (!newValue.matches("\\d*")) {
//		        repeatCountTextField.setText(oldValue);
//		    } else if (newValue.length() > 3 || newValue.equals("0")) { 
//		        repeatCountTextField.setText(oldValue);
//		    }
//		});

		repeatCountHBox.setAlignment(Pos.CENTER);
		//repeatCountVBox.setPadding(new Insets(5));
		repeatCountHBox.getChildren().addAll(repeatCountLabel, repeatCountTextField, repeatNotLabel);

		buttonHBox.setAlignment(Pos.CENTER);

		testProgressBar.setProgress(0);
		testProgressBar.getStyleClass().add("progress-bar");
		percentageLabel.getStyleClass().add("progress-label");

		progressBarHBox.setAlignment(Pos.CENTER);
		allButtonHBox.setAlignment(Pos.CENTER);

//		buttonMainVBox.setPadding(new Insets(20, 0, 0, 0));
//		progressBarHBox.setPadding(new Insets(5, 0, 0, 0));
		allButtonHBox.getChildren().addAll(runAllButton, startButton, pauseButton, stopButton);
		progressBarHBox.getChildren().addAll(testProgressBar, percentageLabel);
		buttonMainHBox.getChildren().addAll(allButtonHBox, progressBarHBox);

		buttonHBox.getChildren().addAll(repeatCountHBox, buttonMainHBox);

		AdvancedTestStateObject.hwatpTestStatusProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				StateMachine.setTestState(TestState.COMPLETED);
				AdvancedTestStateObject.hwatpTestStatusProperty().set(true);
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
			}

		});
		

		AdvancedTestStateObject.runnedHWATPTestFileCountProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				double percentage = (double) AdvancedTestStateObject.getRunnedHWATPTestFileCount().get()
						/ AdvancedTestStateObject.getTotalHWATPSelectedTestFileCount();
				
//				System.out.println("IN ADvanced TEST PROGRESS BAR TOAL FILE COUNT CHECK "
//						+ AdvancedTestStateObject.getTotalHWATPSelectedTestFileCount());
//				System.out.println("IN ADvanced TEST PROGRESS BAR Runned File Count"
//						+ AdvancedTestStateObject.getRunnedHWATPTestFileCount().get());
				
				double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
				Platform.runLater(() -> {
					testProgressBar.setProgress(roundedPercentage);
					percentageLabel.setText((int) (roundedPercentage * 100) + "%");
				});
			}
			
		});



		return buttonHBox;
	}

	private void callStartTest(String stageId, String stageName, String testTypeId, List<String> testFileIds) {
		Task<Response> task = new Task<Response>() {
			@Override
			protected Response call() throws Exception {

				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;
				int repeatCount = Integer.parseInt(repeatCountTextField.getText());
				int totalTestFileCount = testFileIds.size() * repeatCount;

				AdvancedTestStateObject.setTotalHWATPSelectedTestFileCount(totalTestFileCount);
				Platform.runLater(() -> {
					percentageLabel.setText("0%");
				});
				AdvancedTestStateObject.getRunnedHWATPTestFileCount().set(0);

				return testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID, repeatCount,
						testFileIds, true, stageName, testTypeId, null);

			}
		};

		task.setOnSucceeded(event -> {
			Response response = task.getValue(); // Get the response
			if (response.getResponseCode() == 0) {
				Debug.printDebug("HWATP Test Task Response received: " + response.getResponseMessage());
				StateMachine.setTestState(TestState.PENDING);
				runAllButton.setDisable(false);
				startButton.setDisable(false);
				pauseButton.setDisable(true);
				stopButton.setDisable(true);

				Notifications.showErrorAlert(response.getResponseMessage());
			}
		});

		task.setOnFailed(event -> {
			runAllButton.setDisable(false);
			startButton.setDisable(false);
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			Throwable exception = task.getException();
			Debug.printDebug("HWATP Test Task failed with exception: " + exception.getMessage());
		});

		new Thread(task).start();
	}
}
