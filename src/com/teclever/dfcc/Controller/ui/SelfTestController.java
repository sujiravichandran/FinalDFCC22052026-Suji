package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.internal.build.AllowSysOut;

import javafx.beans.property.ReadOnlyStringWrapper;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.SessionTimingService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelStatusBeforeTestResponse;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestRunningCard;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.StatusBarTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class SelfTestController {

	private GridPane selfTestMainContainerGridPane = new GridPane();

	private HBox headingHbox = new HBox(10);

	private HBox topButton = new HBox(10);

	private GridPane headingGridPane = new GridPane();
	
	private GridPane progressButtonGridPane = new GridPane();

	private GridPane midContainerGridPane = new GridPane();

	private HBox midTopHbox1 = new HBox(10);

	private HBox midTopHbox2 = new HBox(10);

	private VBox midTopVbox1 = new VBox(10);

	private VBox midTopVbox2 = new VBox(10);

	private VBox midTopVbox3 = new VBox(10);

	private Button startTest = new Button("START TEST");

	private HBox progressBarHBox = new HBox(5);

	private ProgressBar testProgressBar = new ProgressBar();
	private Label percentageLabel = new Label("0%");

	private double progress = 0.1;

	private Label pageHeading = new Label("SELF TEST");
	private Label rack2 = new Label("cPCI");
	private Label rack1 = new Label("RACK-1");

	private int incrementCounter = 0;
	
	private String selectedStageId = null;

	private boolean aitess1Updated = false;
	private boolean aitess2Updated = false;

	private TableView<SelfTestResult> selfTestTable = new TableView<>();

	TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	TestProcessManagement testProcessManagement = new TestProcessManagement();
	RunConfigurationService runConfigurationService = new RunConfigurationService();
	
	public SelfTestController() {
//		SUJI added for resetting the progress bar once test file are moved::
		StateMachine.resettingProgressBarProperty().addListener((obs, oldVal, newVal) -> {
		    if(newVal) {
		    	Platform.runLater(() -> {
//		    		////System.out.println("Entred resetting Progress in Self test");
		    	testProgressBar.setProgress(0);
		    	percentageLabel.setText("0%");
		    	StateMachine.setResettingProgressBar(false);
		    	});
		    }
		});
		
	}
	

	public GridPane createSelfTestMainContainerGridPane() {
		

		selfTestMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());
		selfTestMainContainerGridPane.getStyleClass().add("selfTest-main-container");

		getData();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);
		
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(10);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(33);

		selfTestMainContainerGridPane.setVgap(5);

		selfTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		selfTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		selfTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		selfTestMainContainerGridPane.add(headingGridPane(), 0, 0);
		selfTestMainContainerGridPane.add(selfTestMidContainer(), 0, 1);
		selfTestMainContainerGridPane.add(progressButtonGridPane(), 0, 2);
		selfTestMainContainerGridPane.add(selfTestBottomContainer(), 0, 3);

		return selfTestMainContainerGridPane;
	}
	
	

		
		
//		private void checkBothAitessLaunched() {
//		    if (aitess1Updated && aitess2Updated) {
//		        boolean bothLaunched = StateMachine.aitess1LaunchedProperty().get()
//		                                 && StateMachine.aitess2LaunchedProperty().get();
//		        if (bothLaunched) {
//		            ////System.out.println("Both AITESS1 and AITESS2 have launched and updated. INItialize");
//
//		            // ✅ This ensures all UI updates are done on the JavaFX Application Thread
//		            Platform.runLater(() -> {
//
//		            	selfTestMainContainerGridPane.setDisable(false);
//		        	
//		        			
//
//		            });
//		        }
//		    }
//		}
//		Exit;
//		Point: 52,72,74

	private void getData() {

		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);
		
		

		observableStageList.stream().filter(stage -> "Self Test".equalsIgnoreCase(stage.getL1StageName()))
				.filter(stage -> stage.getL3StageId() != null).sorted((stage1, stage2) -> {

					int id1 = Integer.parseInt(stage1.getL3StageId().split("_")[1]);
					int id2 = Integer.parseInt(stage2.getL3StageId().split("_")[1]);
					return Integer.compare(id1, id2);
				}).forEach(stage -> {
					TestCardData newCard = new TestCardData(stage.getL3StageId(), stage.getL3StageName(),
							stage.getTestTypeId(), null);

					if ("cPCI".equalsIgnoreCase(stage.getL2StageName())) {
						SelfTestStateObject.addSelfTestcPCICard(newCard);
					}
				});

		observableStageList.stream().filter(stage -> "Self Test".equalsIgnoreCase(stage.getL1StageName()))
				.forEach(stage -> {
					if ("RACK-1".equalsIgnoreCase(stage.getL2StageName())) {
						SelfTestStateObject.setRack1StageId(stage.getL2StageId());
						SelfTestStateObject.setRack1StageName(stage.getL2StageName());
						SelfTestStateObject.setRack1TestTypeId(stage.getTestTypeId());
					}
				});

		if (SelfTestStateObject.getSelfTestRack1Card().size() < 1) {
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd1", "Card-1(RUD)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd2", "Card-2(LIE)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd3", "Card-3(LOE)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd4", "Card-4(RIE)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd5", "Card-5(ROE)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd6", "Card-6(LIS, LMS, LOS)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd7", "Card-7(RIS, RMS, ROS)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd8", "Card-8(PCS, RPS)", null, null));
			SelfTestStateObject
					.addSelfTestRack1Card(new TestCardData("brd9", "Card-9(AOSS-1, A0SS-2, RA, LA)", null, null));
			SelfTestStateObject
					.addSelfTestRack1Card(new TestCardData("brd10", "Card-10(CWP,SPDCM,REFPRB))", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd11", "Card-11(ASA, DPSC)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd12", "Card-12(GSE,FTI)", null, null));
			SelfTestStateObject
					.addSelfTestRack1Card(new TestCardData("brd13", "Card-13(AD,AMD,CSD, SSCDR, MIP)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd14", "Card-14(LG, FCP, FTU)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd15", "Card-15(RSA-1)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(new TestCardData("brd16", "Card-16(RSA-2)", null, null));
			SelfTestStateObject
					.addSelfTestRack1Card(new TestCardData("brd17", "Card-17(APP, AOA EXC MON)", null, null));
			SelfTestStateObject
					.addSelfTestRack1Card(new TestCardData("brd18", "Card-18(OPEN/GND, AO, DIFF AI/AO)", null, null));
			SelfTestStateObject.addSelfTestRack1Card(
					new TestCardData("brd19", "Card-19(SPARE LVDT, 28V/OPEN, OPEN/GND)", null, null));

		}

	}

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);
		headingGridPane.getStyleClass().add("selfTest-top-container");

		headingGridPane.add(headingHbox(), 0, 0);
//		headingGridPane.add(progressBar(), 1, 0);
//		headingGridPane.add(topButton(), 2, 0);

		return headingGridPane;

	}
	
	public GridPane progressButtonGridPane() {
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(70);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		progressButtonGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		progressButtonGridPane.getRowConstraints().addAll(firstRow);
		progressButtonGridPane.getStyleClass().add("selfTest-progressbutton-Container");
		
		progressButtonGridPane.add(progressBar(), 0, 0);
		progressButtonGridPane.add(topButton(), 1, 0);
		
		
		return progressButtonGridPane;
	}

	private HBox headingHbox() {

		pageHeading.getStyleClass().add("headerLabel");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private HBox progressBar() {

		progressBarHBox.getChildren().addAll(testProgressBar, percentageLabel);
		progressBarHBox.setAlignment(Pos.CENTER);

		return progressBarHBox;
	}
	
	private void updateSessionTiming(String stageId) {
	    LocalDateTime currentDateTime = LocalDateTime.now();
	    StateMachine.setCurrentlySelectedStageId(stageId);

	    SessionTimingService s = new SessionTimingService();

	    if (StateMachine.getPreviouslySelectedStageId() == null ||
	        StateMachine.getCurrentlySelectedStageId().equalsIgnoreCase(StateMachine.getPreviouslySelectedStageId())) {

	        StateMachine.setPreviouslySelectedStageId(stageId);
//	        s.addSessionTime(currentSessionDetails.getSessionId(), stageId, currentDateTime.toString(), "", 0, 0);

	    } else {
	        int failedFiles = 0;
	        for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
	            if (!copy.getStatus().equalsIgnoreCase("SUCCESS")) {
	                failedFiles++;
	            }
	        }

//	        s.addSessionTime(currentSessionDetails.getSessionId(),
//	                         StateMachine.getPreviouslySelectedStageId(),
//	                         "", currentDateTime.toString(),
//	                         DFCCConstant.FailedStagesRdfPaths.size(),
//	                         failedFiles);
//
//	        s.addSessionTime(currentSessionDetails.getSessionId(), stageId, currentDateTime.toString(), "", 0, 0);
	        StateMachine.setPreviouslySelectedStageId(stageId);
	    }
	}


	private HBox topButton() {

		topButton.setPadding(new Insets(0, 5, 0, 0));
		topButton.setAlignment(Pos.CENTER_LEFT);
		topButton.getChildren().add(startTest);
	
		CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
//	
		
		startTest.setOnAction(e -> {
			
			if (!checkAitessStatus.isBothAitessOn()) {
				
				return;
			}
			
			if( DFCCConstant.dashBoardLoading ) {
				Notifications.showWarningAlert("Please wait dashboard failure history is loading." );
				return;
			}
			
			if(StateMachine.isMacroPassing()) {
				return;
			}
//	NOTE::		Based on Sridhar Comment on(10092025)based on Power On Status We can Run the Self Test::
			String ch1 = powerOnStatus.getChannel1Status();
			String ch2 = powerOnStatus.getChannel2Status();
			String ch3 = powerOnStatus.getChannel3Status();
			String ch4 = powerOnStatus.getChannel4Status();
			
					
			if (ch1 != null && powerOnStatus.getChannel1Status().equalsIgnoreCase("online")
					|| ch2 != null && powerOnStatus.getChannel2Status().equalsIgnoreCase("online")
					|| ch3 != null && powerOnStatus.getChannel3Status().equalsIgnoreCase("online")
					|| ch4 != null && powerOnStatus.getChannel4Status().equalsIgnoreCase("online")) {
				
				Notifications.showErrorAlert("DFCC is On please turn off DFCC and start the test");
				
				return;
			}else {
			

//			boolean allNull = (ch1 == null || ch2 == null || ch3 == null || ch4 == null);
//			if(allNull) {
//				Notifications.showErrorAlert("Please toggle the 'Power On' switch OFF and ON, then try running the test again");
//				
//				return;
//			}
			
			boolean allOffline = "offline".equalsIgnoreCase(ch1)
			                  && "offline".equalsIgnoreCase(ch2)
			                  && "offline".equalsIgnoreCase(ch3)
			                  && "offline".equalsIgnoreCase(ch4);

			if (allOffline) {
			 
			
			//09-07-2025
			// UPDATING TIME TO DB
			  // === Update session timing before checking RDF popup ===
//		    String initialStageId = SelfTestStateObject.getRack1StageId(); // Use Rack1StageId as default
//		    updateSessionTiming(initialStageId);

			// Excel Name:7-July-Observation
            // Point No:3
			// Change Made on rdf file moving
			
//			boolean popupRDFFiles = false;
//			if (DFCCConstant.FailedStagesRdfPaths.size() > 0) {
//				for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
//
//					if (copyFileDTO.getStatus().equalsIgnoreCase("FAILURE")) {
//						popupRDFFiles = true;
//
//					}
//				}
//
//				if (popupRDFFiles) {
//					
//					//Logbook the Stage Results
//					SessionManagement sessionManagement = new SessionManagement();
//					sessionManagement.updateSessionStagesResultOnApplicationLogBook("Failed",DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//					
////					////System.out.println(	"Failure Size :::"+DFCCConstant.FailedStagesRdfPaths.size());
//					RdfFileCopyPopupController.rdfFilesListtoShow = new ArrayList<CopyFileDTO>();
//					for(CopyFileDTO copyFileDTO:DFCCConstant.FailedStagesRdfPaths)
//					{
//						RdfFileCopyPopupController.rdfFilesListtoShow.add(copyFileDTO);
//					}
//					SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
//					DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//				} else {
//					
//					//Logbook the Stage Results
//					SessionManagement sessionManagement = new SessionManagement();
//					sessionManagement.updateSessionStagesResultOnApplicationLogBook("Passed",DFCCConstant.FailedStagesRdfPaths.get(0).getStageId());
//					
//					SessionFileManagement session = new SessionFileManagement();
////					////System.out.println("DFCCConstant.FailedStagesRdfPaths  Size"+DFCCConstant.FailedStagesRdfPaths.size());
//					session.copyFilesToOutputFolderWhilePlayButton(DFCCConstant.FailedStagesRdfPaths);
//					DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
//				}
//			}
//			
//			selfTestTable.getItems().clear();
			
			// Exit
			// Point No:3
			
			
			
			
			if (StateMachine.isConfirmTestFileCompleted()) {

				Notifications.showWarningAlert("Please Wait until " +StateMachine.getRunningTestName() +" test Completes");
				return;
			}
			// Set current stage ID
			 String initialStageId = SelfTestStateObject.getRack1StageId();
		    StateMachine.setCurrentlySelectedStageId(initialStageId);

		    // Get previous and current stage IDs
		    String currentStageId = StateMachine.getCurrentlySelectedStageId();
		    DFCCConstant.currentTestStageId = currentStageId;
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
//		            DFCCConstant.FailedStagesRdfPaths.clear();
		            
		            StateMachine.setResettingProgressBar(true);
		        }
		        
		        if(DFCCConstant.rdfMoveCanceled) {
					DFCCConstant.rdfMoveCanceled= false;
					return;
				}

		        // Clear advanced test result list on stage change
//		        if(!DFCCConstant.closedFileMove) {
//		        	SessionTestStateObject.clearSessionTestResults();
//			        SelfTestStateObject.clearselfTestResults();
//			        AdvancedTestStateObject.clearAdvancedTestResultsList();
//			        LRUTestStateObject.clearlruTestResultsList();
//					}
//
//		        // Update previous stage ID to current after processing
//		        StateMachine.setPreviouslySelectedStageId(currentStageId);
		    }
			
			
			StateMachine.setSelfTestOn(true);
			StateMachine.setSelfTestOn2(true);
			StateMachine.setSelfTestOnL1(true);
			StateMachine.setSelfTestOnL2(true);
			incrementCounter = 0;
			
			

	
			
			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING || currentState == TestState.COMPLETED
					|| currentState == TestState.STOPPED) {
				resetSelftTestStateMachineStatus();
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.SELF_TEST);
				StateMachine.setStatusBarRunningTestName(StatusBarTestName.SELF_TEST);
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on self test START button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			} else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startTest.setDisable(false);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startTest.setDisable(false);
				return;
			}

			ObservableList<TestCardData> cpciCardList = SelfTestStateObject.getSelfTestcPCICard();

			SelfTestStateObject.setTotalSelfTestFileCount(5);
			
			 // RACK1 Start
		    if (SelfTestStateObject.getRack1Status().get()) {
		        SelfTestStateObject.setSelfTestRunningCard(SelfTestRunningCard.RACK1);
		        String stageId = SelfTestStateObject.getRack1StageId();
//		        updateSessionTiming(stageId);
		        callStartTesting(stageId, "RACK1", SelfTestStateObject.getRack1TestTypeId());
		    }

		    // CPCI B1553
		    SelfTestStateObject.rack1StatusProperty().addListener((observable, oldValue, newValue) -> {
		        if (!newValue) {
		            SelfTestStateObject.setSelfTestRunningCard(SelfTestRunningCard.B1553);
		            String stageId = cpciCardList.get(0).getCardId();
//		            updateSessionTiming(stageId);
		            callStartTesting(stageId, "CPCI", cpciCardList.get(0).getTestTypeId());
		            SelfTestStateObject.getRack1Status().set(true);
		        }
		    });

		    // CPCI RS422_1
		    SelfTestStateObject.b1553StatusProperty().addListener((observable, oldValue, newValue) -> {
		        if (!newValue) {
		            SelfTestStateObject.setSelfTestRunningCard(SelfTestRunningCard.RS422_1);
		            String stageId = cpciCardList.get(1).getCardId();
//		            updateSessionTiming(stageId);
		            callStartTesting(stageId, "CPCI", cpciCardList.get(1).getTestTypeId());
		            SelfTestStateObject.getB1553Status().set(true);
		        }
		    });

		    // CPCI RS422_2
		    SelfTestStateObject.rs422_1StatusProperty().addListener((observable, oldValue, newValue) -> {
		        if (!newValue) {
		            SelfTestStateObject.setSelfTestRunningCard(SelfTestRunningCard.RS422_2);
		            String stageId = cpciCardList.get(2).getCardId();
//		            updateSessionTiming(stageId);
		            callStartTesting(stageId, "CPCI", cpciCardList.get(2).getTestTypeId());
		            SelfTestStateObject.getRs422_1Status().set(true);
		        }
		    });
			SelfTestStateObject.rs422_2StatusProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue) {
					StateMachine.setTestState(TestState.COMPLETED);
					startTest.setDisable(false);
					SelfTestStateObject.getrS422_2Status().set(true);
				}
		});
			}
			}
		});
		testProgressBar.setProgress(0);
		testProgressBar.getStyleClass().add("progress-bar");
		percentageLabel.getStyleClass().add("progress-label");

//		SelfTestStateObject.setTotalSelfTestFileCount(5); // Replace 10 with your total file count
//
//		new Thread(() -> {
//		    for (int count = 1; count <= SelfTestStateObject.getTotalSelfTestFileCount(); count++) {
//		        try {
//		            // Simulate file processing delay (e.g., 500ms)
//		            Thread.sleep(500);
//
//		            // Use a final variable to capture the loop variable
//		            final int currentCount = count;
//
//		            // Update the runnedSelfTestFileCount
//		            Platform.runLater(() -> SelfTestStateObject.getRunnedSelfTestFileCount().set(currentCount));
//		        } catch (InterruptedException e) {
//		            e.printStackTrace();
//		        }
//		    }
//		}).start();


		SelfTestStateObject.runnedSelfTestFileCountProperty().addListener((observable, oldValue, newValue) -> {

			if (newValue != null) {

				int fileCount = SelfTestStateObject.getRunnedSelfTestFileCount().get();
				if (fileCount > 0) {

					incrementCounter++;


					double percentage = (double) incrementCounter / SelfTestStateObject.getTotalSelfTestFileCount();

					double roundedPercentage = Math.round(percentage * 100.0) / 100.0;

					Platform.runLater(() -> {
						testProgressBar.setProgress(roundedPercentage);
						percentageLabel.setText((int) (roundedPercentage * 100) + "%");
					});
				}
			}
		});
		
		return topButton;
	}

	private void resetSelftTestStateMachineStatus() {
		for (TestCardData cardData : SelfTestStateObject.getSelfTestRack1Card()) {
			SelfTestStateObject.updateSelfTestRack1Cardstatus(cardData.getCardId(), null);
		}
		for (TestCardData cardData : SelfTestStateObject.getSelfTestcPCICard()) {
			SelfTestStateObject.updateSelfTestcPCICardstatus(cardData.getCardId(), null);
		}
	}

	private void callStartTesting(String stageId, String stageName, String testTypeId) {
		Task<Response> task = new Task<Response>() {

			@Override
			protected Response call() throws Exception {
				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);

				String ID = stageId;
				TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
				if (testFileResponse.getTestFilesIdName() == null) {
					Platform.runLater(() -> {
						Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
						startTest.setDisable(false);
						StateMachine.setTestState(TestState.COMPLETED);
					});
					return null;
				}
				Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
				List<String> testFileList = new ArrayList<>(testFileMap.keySet());

				int totalTestFileCount = SelfTestStateObject.getTotalSelfTestFileCount();

				SelfTestStateObject.setTotalSelfTestFileCount(totalTestFileCount);
//				Platform.runLater(() -> {
//					percentageLabel.setText("0%");
//				});
				SelfTestStateObject.getRunnedSelfTestFileCount().set(0);

				return testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID, 1,
						testFileList, true, stageName, testTypeId, null);

			}
		};

		task.setOnSucceeded(event -> {
			Response response = task.getValue(); // Get the response
			if (response.getResponseCode() == 0) {
				Debug.printDebug("Self Test Task Response received: " + response.getResponseMessage());

				StateMachine.setTestState(TestState.PENDING);
				startTest.setDisable(false);

				Notifications.showErrorAlert(response.getResponseMessage());
			}
		});

		task.setOnFailed(event -> {
			Throwable exception = task.getException();
			Debug.printDebug("Self Test Task failed with exception: " + exception.getMessage());
		});

		new Thread(task).start();
	}

	private GridPane selfTestMidContainer() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33.33);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33.33);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33.33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(8);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(92);

		midContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		midContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);

		midContainerGridPane.add(midTopHbox1(), 0, 0, 2, 1);

		midContainerGridPane.add(midTopHbox2(), 2, 0);

		midContainerGridPane.add(midTopVbox1(), 0, 1);

		midContainerGridPane.add(midTopVbox2(), 1, 1);

		midContainerGridPane.add(midTopVbox3(), 2, 1);

		midContainerGridPane.setHgap(5);

		midContainerGridPane.setVgap(5);

		midContainerGridPane.getStyleClass().add("selfTest-mid-Container");
		midContainerGridPane.setAlignment(Pos.CENTER);
		return midContainerGridPane;
	}

	private HBox midTopHbox1() {

		rack1.getStyleClass().add("racklabel");
		midTopHbox1.getStyleClass().add("rackhbox");
		midTopHbox1.setAlignment(Pos.CENTER);
		midTopHbox1.getChildren().add(rack1);

		return midTopHbox1;
	}

	private HBox midTopHbox2() {

		rack2.getStyleClass().add("racklabel");

		midTopHbox2.getStyleClass().add("rackhbox");
		midTopHbox2.setAlignment(Pos.CENTER);
		midTopHbox2.getChildren().add(rack2);

		return midTopHbox2;
	}

	private VBox midTopVbox1() {
		ObservableList<TestCardData> rack1LeftList = SelfTestStateObject.getSelfTestRack1Card();

		midTopVbox1.setPadding(new Insets(0, 5, 0, 5));
		midTopVbox1.getStyleClass().add("rackVbox");
		midTopVbox1.setAlignment(Pos.CENTER);

		int count = 8;
		for (int i = 0; i <= count; i++) {
			TestCardData cardData = rack1LeftList.get(i);
			Label label = new Label(cardData.getCardName());
			label.setId(cardData.getCardId());
			label.getStyleClass().add("selTest-label-left");
			label.setMaxWidth(Double.MAX_VALUE);
			label.setPrefHeight(35);
			label.setPadding(new Insets(5));

			label.getStyleClass().add("default-status");

			cardData.statusProperty().addListener((observable, oldValue, newValue) -> {
				label.getStyleClass().removeAll("ok-status", "not-ok-status", "default-status");
				if (newValue != null) {
					if (newValue.equals("OK")) {
						label.getStyleClass().add("ok-status");
					} else if (newValue.equals("NOT OK")) {
						label.getStyleClass().add("not-ok-status");
					}
				} else {
					label.getStyleClass().add("default-status");
				}
			});

			midTopVbox1.getChildren().add(label);

		}
		return midTopVbox1;
	}

	private VBox midTopVbox2() {
		ObservableList<TestCardData> rack1RightList = SelfTestStateObject.getSelfTestRack1Card();

		midTopVbox2.setPadding(new Insets(0, 5, 0, 5));
		midTopVbox2.getStyleClass().add("rackVbox");
		midTopVbox2.setAlignment(Pos.CENTER);

		int count = 18;
		for (int i = 9; i <= count; i++) {
			TestCardData cardData = rack1RightList.get(i);
			Label label = new Label(cardData.getCardName());
			label.setId(cardData.getCardId());
			label.getStyleClass().add("selTest-label-left");
			label.setMaxWidth(Double.MAX_VALUE);
			label.setPrefHeight(32);
			label.setPadding(new Insets(0, 5, 0, 5));

			label.getStyleClass().add("default-status");

			cardData.statusProperty().addListener((observable, oldValue, newValue) -> {
				label.getStyleClass().removeAll("ok-status", "not-ok-status", "default-status");
				if (newValue != null) {
					if (newValue.equals("OK")) {
						label.getStyleClass().add("ok-status");
					} else if (newValue.equals("NOT OK")) {
						label.getStyleClass().add("not-ok-status");
					}
				} else {
					label.getStyleClass().add("default-status");
				}
			});

			midTopVbox2.getChildren().add(label);
		}
		return midTopVbox2;
	}

	private VBox midTopVbox3() {
		ObservableList<TestCardData> cPCIList = SelfTestStateObject.getSelfTestcPCICard();
		
		//23032026 change based on sridhar sir comments
		Label note1 = new Label("Note: Connect Loopback for P39 and P50(RS422)");
		note1.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
		//note1.setWrapText(true);
		note1.setMaxWidth(300); // adjust width as needed
		note1.setAlignment(Pos.CENTER_LEFT);
		midTopVbox3.getChildren().add(note1);
		    
		midTopVbox3.setPadding(new Insets(0, 5, 0, 5));
		midTopVbox3.getStyleClass().add("rackVbox");
		midTopVbox3.setAlignment(Pos.CENTER);

		int count = 2;
		for (int i = 0; i <= count; i++) {
			TestCardData cardData = cPCIList.get(i);
			Label label = new Label(cardData.getCardName());
			label.setId(cardData.getCardId());
			label.getStyleClass().add("selTest-label-left");
			label.setMaxWidth(Double.MAX_VALUE);
			label.setPrefHeight(35);
			label.setPadding(new Insets(5));

			label.getStyleClass().add("default-status");

			cardData.statusProperty().addListener((observable, oldValue, newValue) -> {
				label.getStyleClass().removeAll("ok-status", "not-ok-status", "default-status");
				if (newValue != null) {
					if (newValue.equals("OK")) {
						label.getStyleClass().add("ok-status");
					} else if (newValue.equals("NOT OK")) {
						label.getStyleClass().add("not-ok-status");
					}
				} else {
					label.getStyleClass().add("default-status");
				}
			});

			midTopVbox3.getChildren().add(label);
		}
		return midTopVbox3;
	}

	private TableView<SelfTestResult> selfTestBottomContainer() {
		selfTestTable = createTableViewSelf();

		return selfTestTable;

	}

//	private TableView<SelfTestResult> createTableViewSelf() {
//		TableView<SelfTestResult> tableView = new TableView<>();
//		tableView.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());
//
//		tableView.getStyleClass().add("check-sum-table");
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//		tableView.setPrefHeight(900);
//
//		TableColumn<SelfTestResult, String> fileNameColumn = new TableColumn<>("File Name");
//		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
//		fileNameColumn.setReorderable(false);
//		fileNameColumn.setSortable(false);
//		fileNameColumn.setMaxWidth(825);
//		fileNameColumn.setStyle("-fx-alignment: CENTER;");
//
//		// Custom cell to show ellipsis for file path
//		fileNameColumn
//				.setCellFactory(new Callback<TableColumn<SelfTestResult, String>, TableCell<SelfTestResult, String>>() {
//					@Override
//					public TableCell<SelfTestResult, String> call(TableColumn<SelfTestResult, String> col) {
//						return new TableCell<SelfTestResult, String>() {
//							@Override
//							protected void updateItem(String filePath, boolean empty) {
//								super.updateItem(filePath, empty);
//								if (empty || filePath == null) {
//									setText(null);
//								} else {
//									File file = new File(filePath);
//									setText(file.getName());
//								}
//							}
//						};
//					}
//				});
//
//		TableColumn<SelfTestResult, String> resultColumn = new TableColumn<>("Result");
//
//		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
//		resultColumn.setReorderable(false);
//		resultColumn.setSortable(false);
//		resultColumn.setMaxWidth(300);
//		resultColumn.setStyle("-fx-alignment: CENTER;");
//		rewriteColumn(resultColumn);
//
//		SelfTestStateObject.getTestResults().addListener((ListChangeListener<? super SelfTestResult>) change -> {
//			while (change.next()) {
//				if (change.wasAdded()) {
//					int lastIndex = SelfTestStateObject.getTestResults().size() - 1;
//					Platform.runLater(() -> {
//						tableView.scrollTo(lastIndex);
//						tableView.getSelectionModel().select(lastIndex);
//						tableView.getFocusModel().focus(lastIndex);
//					});
//				}
//			}
//		});
//
//		// View Button Column
//		TableColumn<SelfTestResult, Void> viewButtonColumn = new TableColumn<>();
//		viewButtonColumn.setCellFactory(col -> new TableCell<SelfTestResult, Void>() {
//			private final Button viewButton = new Button("View");
//
//			{
//				viewButton.setOnAction(e -> {
//					SelfTestResult selfTestResult = getTableView().getItems().get(getIndex());
//					File file = new File(selfTestResult.getFileName());
//
//					// Check if the file exists before trying to open it
//					if (file.exists()) {
//						try {
//
//							String os = System.getProperty("os.name").toLowerCase();
//							if (os.contains("win")) {
//								// Windows-specific code
//								Desktop desktop = Desktop.getDesktop();
//								if (desktop.isSupported(Desktop.Action.OPEN)) {
//									desktop.open(file);
//								} else {
//									Debug.printDebug("Open action not supported on this platform.");
//								}
//							} else if (os.contains("nix") || os.contains("nux")) {
//								// Linux-specific code using xdg-open 121161
//								// Ensure the file path is absolute
//								File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
//								new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
//							} else {
//								Debug.printDebug("Unsupported OS: " + os);
//							}
//						} catch (IOException ex) {
//							Debug.printDebug("Error opening file: " + ex.getMessage());
//						}
//					} else {
//						Debug.printDebug("File does not exist: " + file.getAbsolutePath());
//					}
//				});
//			}
//
//			@Override
//			protected void updateItem(Void item, boolean empty) {
//				super.updateItem(item, empty);
//				if (empty) {
//					setGraphic(null);
//				} else {
//					setGraphic(viewButton);
//				}
//			}
//		});
//		viewButtonColumn.setReorderable(false);
//		viewButtonColumn.setSortable(false);
//		viewButtonColumn.setMaxWidth(100);
//
//		tableView.getColumns().addAll(fileNameColumn, resultColumn, viewButtonColumn);
//		tableView.setItems(SelfTestStateObject.getTestResults());
//
//		return tableView;
//	}
	
	private TableView<SelfTestResult> createTableViewSelf() {

		TableView<SelfTestResult> tableView = new TableView<>();

		tableView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());

		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setPrefHeight(900);

		// --- SlNo Column ---
		TableColumn<SelfTestResult, String> slNoColumn = new TableColumn<>("SL No.");
		slNoColumn.setCellValueFactory(cellData -> {
			int index = tableView.getItems().indexOf(cellData.getValue()) + 1;
			return new ReadOnlyStringWrapper(String.valueOf(index));
		});
		slNoColumn.setReorderable(false);
		slNoColumn.setSortable(false);
		slNoColumn.setMaxWidth(100);
		slNoColumn.setStyle("-fx-alignment: CENTER;");

		// --- File Name Column ---
		TableColumn<SelfTestResult, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setMaxWidth(825);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		fileNameColumn.setCellFactory(new Callback<TableColumn<SelfTestResult, String>, TableCell<SelfTestResult, String>>() {
			@Override
			public TableCell<SelfTestResult, String> call(TableColumn<SelfTestResult, String> col) {
				return new TableCell<SelfTestResult, String>() {
					@Override
					protected void updateItem(String filePath, boolean empty) {
						super.updateItem(filePath, empty);
						if (empty || filePath == null) {
							setText(null);
						} else {
							File file = new File(filePath);
							setText(file.getName());
						}
					}
				};
			}
		});

		// --- Result Column ---
		TableColumn<SelfTestResult, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setMaxWidth(300);
		resultColumn.setStyle("-fx-alignment: CENTER;");
		rewriteColumn(resultColumn);

		// --- Scroll to last added row ---
		SelfTestStateObject.getTestResults().addListener((ListChangeListener<? super SelfTestResult>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					int lastIndex = SelfTestStateObject.getTestResults().size() - 1;
					Platform.runLater(() -> {
						tableView.scrollTo(lastIndex);
						tableView.getSelectionModel().select(lastIndex);
						tableView.getFocusModel().focus(lastIndex);
					});
				}
			}
		});

		// --- View Button Column ---
		TableColumn<SelfTestResult, Void> viewButtonColumn = new TableColumn<>();
		viewButtonColumn.setCellFactory(col -> new TableCell<SelfTestResult, Void>() {
			private final Button viewButton = new Button("View");

			{
				viewButton.setOnAction(e -> {
					SelfTestResult selfTestResult = getTableView().getItems().get(getIndex());
					File file = new File(selfTestResult.getFileName());

					if (file.exists()) {
						try {
							String os = System.getProperty("os.name").toLowerCase();
							if (os.contains("win")) {
								Desktop desktop = Desktop.getDesktop();
								if (desktop.isSupported(Desktop.Action.OPEN)) {
									desktop.open(file);
								} else {
									Debug.printDebug("Open action not supported on this platform.");
								}
							} else if (os.contains("nix") || os.contains("nux")) {
								File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
								new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
							} else {
								Debug.printDebug("Unsupported OS: " + os);
							}
						} catch (IOException ex) {
							Debug.printDebug("Error opening file: " + ex.getMessage());
						}
					} else {
						Debug.printDebug("File does not exist: " + file.getAbsolutePath());
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : viewButton);
			}
		});
		viewButtonColumn.setReorderable(false);
		viewButtonColumn.setSortable(false);
		viewButtonColumn.setMaxWidth(100);

		// --- Add all columns to the table ---
		tableView.getColumns().addAll(slNoColumn, fileNameColumn, resultColumn, viewButtonColumn);
		tableView.setItems(SelfTestStateObject.getTestResults());

		return tableView;
	}


	private void rewriteColumn(TableColumn<SelfTestResult, String> resultColumn) {
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setCellFactory(column -> new TableCell<SelfTestResult, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: green;-fx-alignment: CENTER;-fx-text-fill:white");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					} else {
						setText(item);
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					}
				}
			}
		});
	}

}
