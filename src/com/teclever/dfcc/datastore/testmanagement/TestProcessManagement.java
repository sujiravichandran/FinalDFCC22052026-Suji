package com.teclever.dfcc.datastore.testmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesSelectedTestFiles;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.SessionTiming;
import com.teclever.datastore.entities.TestFilesStagesMapping;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.SessionTimingService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ChannelStatusBeforeTestResponse;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.dto.TestProcessDto;
import com.teclever.dfcc.datastore.dto.TestProcessResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.resultstore.resultmanagement.RdfFileDetailsParser;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject.AdvancedTestResult;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestRunningCard;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject.SessionTestResult;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.aitessRunning;
//import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.aitessRunning;
//import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.rdfFileParser;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.rdfFileParser;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

public class TestProcessManagement {

	ObjectId mongoUniqueIdentifier; // Mongo DB Save ID

	private String tempRdfFileResult = "OK";

	private String tempDotComFileResult = "OK";

	/**
	 * Main method to start the test process.
	 * 
	 * @param sessionId         The session identifier.
	 * @param stageId           The stage identifier.
	 * @param repeatCount       The number of times the test should be repeated.
	 * @param listOfFileId      List of file identifiers to be used in the test.
	 * @param continueWithError Flag to indicate whether to continue with errors.
	 * @param stageName         Name of the stage (e.g., RACK1, CPCI).
	 * @param testTypeId        Type of the test.
	 * @return Response object with the result of the operation.
	 */

	public Response testProcesControl(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError, String stageName, String testTypeId, String ofpConfig) {

		DFCCConstant.testTypeId = testTypeId;

		Response res = new Response();

		try {
//			 System.out.println("Check Test Type ID:  "+ testTypeId);
//		Suji Added:	 
		StateMachine.setTestTypeId(testTypeId);
//			 Exit
//		System.out.println("Test Proces Controll Entry point : " + sessionId + " Stage Id : " + stageId + " repeatCount : "
//							+ repeatCount + " ListOfFile " + listOfFileId + " ContinueWithError " + continueWithError
//							+ " StageName " + stageName + " TestTypeID " + testTypeId + " ofpConfig " + ofpConfig);
		
			Debug.printDebug(
					"Test Proces Controll Entry point : " + sessionId + " Stage Id : " + stageId + " repeatCount : "
							+ repeatCount + " ListOfFile " + listOfFileId + " ContinueWithError " + continueWithError
							+ " StageName " + stageName + " TestTypeID " + testTypeId + " ofpConfig " + ofpConfig);

			// Retrieve Highest RunCount Session Stage Mapping Data.
			SessionStagesMapping sessionStagesMapping = getSessionStageMapping(sessionId, stageId);

			if (sessionStagesMapping == null) {
				return createErrorResponse("Failed to retrieve session stage mapping");
			}

			// If AETS process failed to launch, return failure response
			if (checkAndUpdateAetsProcessStatus(testTypeId, ofpConfig)) {
				resetAitessFailureStates();
				Debug.printDebug("AETS Switch Failed");
				return createErrorResponse("AETS Failed to launch");
			}

			resetAitessFailureStates();

			// SUJI Logic : To check All Four Channels are Switched
			res = otherThanrack1AndCpciChanges(stageName);
			if (res.getResponseCode() == 0) {
				return res;
			}

			// Update SESSION ENTITY with start data
			res = updateSessionEntityStartData(sessionId);
			if (res.getResponseCode() == 0) {
				return res;
			}

			// ADD Or Update SESSION STAGE MAPPING Based on RunCount
			String sessionStageMapId = getSessionStageMappingIdAndRunCount(
					sessionStagesMapping.getSessionStagesMappingId(), repeatCount, sessionStagesMapping.getRunCount());

			// Update SESSION STAGE MAPPING Status
			res = updateSessionStageMapStatus(sessionStageMapId, "STARTED");
			if (res.getResponseCode() == 0) {
				return res;
			}

			// Fetching Selected TestFile IDs From DB.
			Map<String, String> testFilesIdName = getStageSelectedTestFileIds(stageId);
			Set<String> keysSet = new HashSet<>(testFilesIdName.keySet());

			Thread startTestProcessThread = new Thread(() -> runTestProcess(sessionId, stageId, repeatCount,
					listOfFileId, continueWithError, stageName, sessionStageMapId, testFilesIdName, keysSet));

			// Thread START
			startTestProcessThread.start();

			res.setResponseCode(1);
			res.setResponseMessage("Test Started ");

		} catch (Exception e) {
			return createErrorResponse("Test Start Unsuccessfull.. ");

		}

//		System.out.println("Test ");
		return res;
	}

	// Comunicating with DB to get Highest run count SESSION STAGE MAPPING
	// Informantion.
	private SessionStagesMapping getSessionStageMapping(String sessionId, String stageId) {
		SessionSelectedStagesService sessionStagesSelectedStage = new SessionSelectedStagesService();
		GetObjResponse getObjResponse = sessionStagesSelectedStage.getSessionStagesMapp(sessionId, stageId);
		return (SessionStagesMapping) getObjResponse.getObject();
	}

	// Reset Aitess failure states
	private void resetAitessFailureStates() {
		aitessRunning.setAitess1SwitchedFailed(false);
		aitessRunning.setAitess2SwitchedFailed(false);

	}

	private Response updateSessionStageMapStatus(String sessionStageMapId, String status) {
		SessionSelectedStagesService sessionStagesSelectedStage = new SessionSelectedStagesService();
		return sessionStagesSelectedStage.updateSessionStageMapStatus(sessionStageMapId, status);
	}

	private Response createErrorResponse(String message) {
		Response res = new Response();
		res.setResponseCode(0);
		res.setResponseMessage(message);
		return res;
	}

	private boolean checkAndUpdateAetsProcessStatus(String testTypeId, String ofpConfig) {
		if (ofpConfig == null) {
			// Check and update AETS process status
			AitessProcessControlManagement.getInstance().check(testTypeId);
		} else if (ofpConfig != null) {
			// Check and update AETS process status
			AitessProcessControlManagement.getInstance().check1(testTypeId, ofpConfig);
		}

		// If AETS process failed to launch, return failure response
		return aitessRunning.isAitess1SwitchedFailed() || aitessRunning.isAitess2SwitchedFailed();
	}

	private Map<String, String> getStageSelectedTestFileIds(String stageId) {
		// Getting File ID and FileName as Key Value.
		TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
		TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);
		return testFileResponse.getTestFilesIdName();
	}

	private String getSessionStageMappingIdAndRunCount(String sessionStageMapId, int repeatCount, int runCount) {
		SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();

		String sessionStageMappingId;
		if (runCount == 0) {
			// Update SESSION STAGE MAPPING By Run Count 1
			sessionStagesSelectedStagesService.updateRepeatAndRunCountStatus(sessionStageMapId, repeatCount);

			sessionStageMappingId = sessionStageMapId;

		} else {
			// Add One More row into Session Stage Mapping and Increment RunCount
			SessionStagesMapping returnRes = sessionStagesSelectedStagesService
					.addSessionStagesBySessionIdAndStageId(sessionStageMapId, repeatCount, (runCount + 1));

			sessionStageMappingId = returnRes.getSessionStagesMappingId();

		}
		return sessionStageMappingId;
	}

	/**
	 * Get RDF file result based on stage name and other parameters.
	 * 
	 * @param stageName       Name of the stage.
	 * @param rdfFileLocation Path to RDF files.
	 * @param rdfFileName     Name of the RDF file.
	 * @param oneFileName     Name of the test file.
	 * @param stageId         ID of the stage.
	 * @param sessionId       ID of the session.
	 * @return TestProcessResponse object with the result.
	 */
	private TestProcessResponse getRdfFileResult(String stageName, String rdfFileLocation, String rdfFileName,
			String oneFileName, String stageId, String sessionId) {
		TestProcessResponse testProcessRes = new TestProcessResponse();
		try {
			if (stageName.equals("RACK1")) {
				Debug.printDebug("   ->  rack1TestFileTest()  ");
				testProcessRes = rack1TestFileTest(rdfFileLocation, rdfFileName, oneFileName);

			} else if (stageName.equals("CPCI") || stageName.equals("MANDATORY") || stageName.equals("GO NOGO")
					|| stageName.equals("SRU") || stageName.equals("SESSION TEST") || stageName.equals("HWATP TEST")
					|| stageName.equals("INTERFACE TEST") || stageName.equals("CUSTOM ONE")
					|| stageName.equals("CUSTOM TWO")) {
				Debug.printDebug("   ->  7  parseTestFileTest()  ");

				testProcessRes = parseTestFileTest(rdfFileLocation, rdfFileName, stageId, stageName, oneFileName,
						sessionId);

			}
		} catch (Exception e) {
			throw e;
		}
		return testProcessRes;
	}

	// From SESSION ENTITY : Add Session Start Time.
	private Response updateSessionEntityStartData(String sessionId) {
		Response res = new Response();
		// If Trails
		if (sessionId.substring(0, 4).equals("TSSN")) {
			try {
				TrailSessionEntityService sessionService = new TrailSessionEntityService();
				GetObjResponse objResponse = sessionService.getSessionDetailBySessionId(sessionId);
				TrailSessionEntity sessionEntity = (TrailSessionEntity) objResponse.getObject();
				if (sessionEntity.getStartDate() == null) {
					Date utilDate = new Date();
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
					sessionEntity.setStartDate(sqlDate);

					LocalDateTime now = LocalDateTime.now();

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					String formattedDateTime = now.format(formatter);

					sessionEntity.setStartDateTime(formattedDateTime);
					res = sessionService.updateSession(sessionEntity);

				} else {
					res.setResponseCode(1);
					res.setResponseMessage("Trail Session Already Started ");
				}
			} catch (Exception e) {
				res.setResponseCode(0);
				res.setResponseMessage("Update Unsuccessful ");

			}
			return res;
		}

		try {
			SessionService sessionService = new SessionService();
			GetObjResponse objResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionEntity = (SessionEntity) objResponse.getObject();
			if (sessionEntity.getStartDate() == null) {
				Date utilDate = new Date();
				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				sessionEntity.setStartDate(sqlDate);
				LocalDateTime now = LocalDateTime.now();

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				String formattedDateTime = now.format(formatter);

				sessionEntity.setStartDateTime(formattedDateTime);
				res = sessionService.updateSession(sessionEntity);

			} else {
				res.setResponseCode(1);
				res.setResponseMessage("Session Already Started ");
			}
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Unsuccessful ");

		}
		return res;

	}

	/**
	 * Add selected test file entry to the session stage mapping.
	 * 
	 * @param testFileId        ID of the test file.
	 * @param sessionStageMapId ID of the session stage mapping.
	 * @return Generated session stage selected test file ID.
	 */
	private String addSelectedTestFile(String testFileId, String sessionstageMappingId) {
		GetObjResponse res = new GetObjResponse();
		try {
			SessionStagesSelectedTestFilesService selectedTestFile = new SessionStagesSelectedTestFilesService();
			res = selectedTestFile.addSelectedFileToStages(testFileId, sessionstageMappingId);
			SessionStagesSelectedTestFiles sessionsFiles = (SessionStagesSelectedTestFiles) res.getObject();
			return sessionsFiles.getSessionStagesSelectedTestFilesId();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the state machine card status.
	 * 
	 * @param stageName     Name of the stage.
	 * @param stageId       ID of the stage.
	 * @param rdfFileResult Result of the RDF file processing.
	 * @param keysSet       Set of FileIds of Stage From DB.
	 */
	private void updateStateMachineCardStatus(String stageName, String stageId, String rdfFileResult,
			Set<String> keysSet) {
		try {

			switch (stageName) {
			case "RACK1":

				if (SelfTestStateObject.getSelfTestRunningCard() == SelfTestRunningCard.RACK1) {
					StateMachine.setConfirmTestFileCompleted(false);
					SelfTestStateObject.getRack1Status().set(false);
					;
				}

				break;
			case "CPCI":

				SelfTestStateObject.updateSelfTestcPCICardstatus(stageId, rdfFileResult);
				switch (SelfTestStateObject.getSelfTestRunningCard()) {

				case B1553:
					SelfTestStateObject.getB1553Status().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case RS422_1:
					SelfTestStateObject.getRs422_1Status().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case RS422_2:
					SelfTestStateObject.getrS422_2Status().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;

				default:
					break;
				}
				break;

			case "MANDATORY":
				Debug.printDebug("----- Mandatory ---- " + stageId + "  Result : " + rdfFileResult);
				LRUTestStateObject.updateLruMandatoryCardstatus(stageId, rdfFileResult);

				switch (LRUTestStateObject.getLRUTestRunningCard()) {

				case SPIL_LINK:
					Debug.printDebug("----- SPIL_LINK ----");
					LRUTestStateObject.getSpilLinkStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case POWER_SUPPLY:
					Debug.printDebug("----- POWER_SUPPLY ----");
					LRUTestStateObject.getPowerSupplyStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case PBIT:
					Debug.printDebug("----- PBIT ----");
					LRUTestStateObject.getPbitStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case AD_DA_INTERFACE:
					Debug.printDebug("----- AD_DA_INTERFACE ----");
					LRUTestStateObject.getAd_daInterfaceStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case INITIALIZE_LRU:
					Debug.printDebug("----- INITIALIZE_LRU ----");
					LRUTestStateObject.getInitializeLRUStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;

				default:
					break;
				}

				break;
			case "GO NOGO":
				LRUTestStateObject.updateLruGoAndNogoCardstatus(stageId, rdfFileResult);

				switch (LRUTestStateObject.getLRUTestRunningCard()) {

				case COMPLETE_TEST:
					LRUTestStateObject.getCompleteTestStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case OFP_LOADING:
					LRUTestStateObject.getOfpLoadingStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				case PI_CHECK:
					LRUTestStateObject.getPiCheckStatus().set(false);
					StateMachine.setConfirmTestFileCompleted(false);
					break;
				default:
					break;
				}

				break;
			case "SRU":
				LRUTestStateObject.updateSelectedSubStagesList(stageId, "COMPLETED");
				LRUTestStateObject.updateLruSruCardstatus(stageId, rdfFileResult);
				// Mani Added For DFCC Power ON Enabling
//				System.out.println("Card COUNT On Seleted SRU" + DFCCConstant.cardCount);
				if (DFCCConstant.cardCount == DFCCConstant.currentCount) {
					StateMachine.setTestState(TestState.COMPLETED);
					DFCCConstant.cardCount = 0;
					DFCCConstant.currentCount = 0;
				} else {
					DFCCConstant.currentCount++;
				}
//				System.out.println("After Checking COUNT On Seleted SRU" + DFCCConstant.currentCount);
				// Comented By Mani
				// StateMachine.setTestState(TestState.COMPLETED);
				Debug.printDebug("------Stage Id----- " + stageId + "  ------- RDF FILE Result----- " + rdfFileResult);
				StateMachine.setConfirmTestFileCompleted(false);
				break;
			case "SESSION TEST":

//				System.out.println("CASE : SESSION TEST");
				// SessionTestStateObject.updateEndLeafMapStatus(stageId, "COMPLETED");
				StateMachine.setTestState(TestState.COMPLETED);

				SessionTestStateObject.updateEndLeafMapStatus(stageId, getStageResult(stageId, keysSet));
//				System.out.println("STAGE  ID " + stageId + " RESULT " + getStageResult(stageId, keysSet));
				SessionTestStateObject.getRunningTestLeafStatus().set(true);
				StateMachine.setConfirmTestFileCompleted(false);

				break;

			case "HWATP TEST":

				Debug.printDebug("CASE : HWATP TEST");
				AdvancedTestStateObject.getHwatpTestStatus().set(false);
				StateMachine.setConfirmTestFileCompleted(false);
				break;
			case "INTERFACE TEST":
				Debug.printDebug("CASE : INTERFACE TEST");
				AdvancedTestStateObject.getInterfaceTestStatus().set(false);
				StateMachine.setConfirmTestFileCompleted(false);
				break;

			case "CUSTOM ONE":
				Debug.printDebug("CASE : CUSTOM ONE");
				AdvancedTestStateObject.getCustomTest1Status().set(false);
				StateMachine.setConfirmTestFileCompleted(false);
				break;
			case "CUSTOM TWO":
				Debug.printDebug("CASE : CUSTOM TWO");
				AdvancedTestStateObject.getCustomTest2Status().set(false);
				StateMachine.setConfirmTestFileCompleted(false);
				break;
			default:
				Debug.printDebug("INVALID TEST TYPE ID ");
				break;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	private String getStageResult1(String stageId, Set<String> fileIds) {
		Set<String> setOfFileIds = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
		if (setOfFileIds != null && setOfFileIds.size() > 0 && setOfFileIds.equals(fileIds)) {
			return "COMPLETED";

		} else {
			return "pending";
		}
	}

	private String getStageResult2(String stageId, Set<String> fileIds) {
		Set<String> setOfFileIds = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
		if (setOfFileIds != null && setOfFileIds.size() > 0 && setOfFileIds.equals(fileIds)) {
			// return "COMPLETED";
//			System.out.println("IF BLOCK");
			return "pending";
		} else {
			// return "pending";
//			System.out.println("ELSE BLOCK");
			boolean containsFail = removeFileCheck();
			String result = "COMPLETED";
			if (containsFail) {
				result = "pending";
			}

			return result;
		}
	}

	private String getStageResult(String stageId, Set<String> fileIds) {
		Set<String> setOfFileIds = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
		if (setOfFileIds != null && setOfFileIds.size() > 0 && setOfFileIds.equals(fileIds)) {
//			System.out.println("IF BLOCK");

			// boolean containsFail = removeFileCheck();
			boolean containsFail = removeFileCheck(StateMachine.currentSessionDetails.getSessionId(),
					DFCCConstant.stageId, DFCCConstant.sessionStageMapId, DFCCConstant.repeatCount);

//			System.out.println("containsFail" + containsFail);

			// Mani For Repeat Count Issue
			// SessionFileManagement sessionFileManagement = new SessionFileManagement();
			// boolean containsFail =
			// sessionFileManagement.getStatusFlagOfSelectedFile(StateMachine.currentSessionDetails.getSessionId(),
			// DFCCConstant.stageId,
			// DFCCConstant.sessionStageMapId, DFCCConstant.repeatCount,
			// DFCCConstant.continueWithErrorFlag,true);
//			System.out.println("Remove Checker" + containsFail);
			String result = "COMPLETED";

			if (!containsFail) {
				result = "pending";
			}

			if (DFCCConstant.continueWithErrorFlag) {
				result = "COMPLETED";
			}

			return result;

		} else {
//			System.out.println("ELSE BLOCK");
			return "pending";

		}
	}

	/**
	 * Add TEST FILE RESULT entry.
	 * 
	 * @param testFileResultId      ID for the test file result.
	 * @param sessionId             ID of the session.
	 * @param stageId               ID of the stage.
	 * @param testFileId            ID of the test file.
	 * @param mongoUniqueIdentifier Identifier of MongoDB.
	 * @param rdfPath               Path to RDF files.
	 * @param rdfFileName           Name of the RDF file.
	 * @param testStatus            Result of the test.
	 * @param dStarCount            Count of DStar.
	 * @param startTime             Start time of the test.
	 * @param endTime               End time of the test.
	 * @param selectedtestFileId    ID for the selected test file entry.
	 */
	private Response addTestFileResult(String sessionStagesTestFilesResultId, String sessionId, String stageId,
			String testFileId, String mongoUniqueIdentifier, String rdfPath, String rdfFileName, String testStatus,
			String dStarCount, String startTime, String endTime, String selectedtestFileId) {
		Response res = new Response();
		try {
			SessionStagesTestFilesResultService selectedTestFile = new SessionStagesTestFilesResultService();
			SessionStagesTestFilesResult testFileResult = new SessionStagesTestFilesResult();

			testFileResult.setSessionStagesTestFilesResultId(sessionStagesTestFilesResultId);
			testFileResult.setSessionId(sessionId);
			testFileResult.setStageId(stageId);
			testFileResult.setSelectedtestFileId(selectedtestFileId);
			testFileResult.setSystemResultInfoId(mongoUniqueIdentifier);
			testFileResult.setRdfPath(rdfPath);
			testFileResult.setRdfFileName(rdfFileName);
			testFileResult.setTestStatus(testStatus);
			testFileResult.setdStarCount(dStarCount);
			testFileResult.setStartTime(startTime);
			testFileResult.setUutTypeId(currentSessionDetails.getUutId());
			testFileResult.setEndTime(endTime);
			GetObjResponse getObjResponse = selectedTestFile.addTestFilesResult(testFileResult);
			res = getObjResponse.getResponse();
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Add Selected Test File Unsuccessful ");
		}
		return res;
	}

	// Parse SelfTestFile
	private TestProcessResponse rack1TestFileTest(String rdfFileLocation, String rdfFileName, String tpfFileName) {
		TestProcessResponse testProcessResponse = new TestProcessResponse();
		Response res = new Response();
		Map<String, String> brdresult = new HashMap<>();
		try {
			if (rdfFileName == null || rdfFileName.equals("USER EXIT") || rdfFileName.equals("RUN TIME ERROR")
					|| rdfFileName.equals("FILE NOT FOUND ERROR")) {
				for (int i = 1; i <= 19; i++) {
					SelfTestStateObject.updateSelfTestRack1Cardstatus("brd" + i, "NOT OK");
				}

				res.setResponseCode(111);
				testProcessResponse.setResponse(res);
				res.setResponseMessage("Unsuccessful ");
				testProcessResponse.setTestProcessResult(brdresult);
				updateRack1FileResult(rdfFileLocation, rdfFileName, tpfFileName, res.getResponseCode());
				return testProcessResponse;

			}

			File file = new File(rdfFileLocation + rdfFileName);
			Debug.printDebug("FILE LENGTH  " + file.length());

			while (true) { // Infinite loop
				Debug.printDebug("FILE LENGTH " + file.length()); // Print the file length
				if (file.length() == 0) { // Check if the file length is 0
					try {
						Debug.printDebug("File is empty, waiting for 1 second...");
						Thread.sleep(1000); // Wait for 1 second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else { // If the file is not empty, exit the loop
					Debug.printDebug("File is not empty.");
					break;
				}
			}

			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

				String line;
				String brdNumber = null;
				String brdResult = null;
				res.setResponseCode(1);
				int dStartCount = 0;
				Debug.printDebug("RDF File Name  :" + (rdfFileLocation + rdfFileName));
				while ((line = reader.readLine()) != null) {
					Debug.printDebug("LINE  " + line);
					if (line.startsWith("S> brd")) {

						brdNumber = line.substring(line.indexOf(" "), line.indexOf("_"));
						if (brdresult.get(brdNumber) != null) {
							if (brdresult.get(brdNumber) != "NOT OK") {
								brdresult.put(brdNumber, "OK");
							}
						} else {
							brdresult.put(brdNumber, "OK");
						}
					} else if (line.startsWith("D*> ")) {
						if (brdresult.get(brdNumber) != null) {
							if (brdresult.get(brdNumber) != "NOT OK") {
								dStartCount++;
								brdresult.put(brdNumber, "NOT OK");
								res.setResponseCode(111);
								brdResult = null;
							}
						} else {
							dStartCount++;
							res.setResponseCode(111);
							brdResult = "BRD";
						}
					}
					Debug.printDebug(
							"  RACK 1 -------- dStartCount: " + dStartCount + " res.get " + res.getResponseCode());
				}

				Debug.printDebug("  RACK 1 -------- brdResult " + brdResult);
				if (brdResult != null && brdResult.equals("BRD")) {
					for (int i = 1; i <= 19; i++) {
						SelfTestStateObject.updateSelfTestRack1Cardstatus("brd" + i, "NOT OK");
					}

					res.setResponseCode(111);
					testProcessResponse.setResponse(res);
					res.setResponseMessage("Unsuccessful ");
					testProcessResponse.setTestProcessResult(brdresult);
					reader.close();
					updateRack1FileResult(rdfFileLocation, rdfFileName, tpfFileName, res.getResponseCode());
					return testProcessResponse;

				}

				for (Map.Entry<String, String> entry : brdresult.entrySet()) {
					SelfTestStateObject.updateSelfTestRack1Cardstatus(entry.getKey(), entry.getValue());
				}

				res.setResponseMessage("Successful ");
				testProcessResponse.setdStarCount(dStartCount);
				reader.close();
				updateRack1FileResult(rdfFileLocation, rdfFileName, tpfFileName, res.getResponseCode());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Unsuccessful ");
			e.printStackTrace();
		}
		testProcessResponse.setResponse(res);
		testProcessResponse.setTestProcessResult(brdresult);
		return testProcessResponse;
	}

	private void updateRack1FileResult(String rdfFileLocation, String rdfFileName, String tpfFileName,
			int responseCode) {
		try {
			String filePath = rdfFileLocation + rdfFileName;
			String rdfFileStatus = (responseCode == 111) ? "NOT OK" : "OK";

			if (rdfFileName == null) {
				filePath = tpfFileName;
				rdfFileStatus = "Parse Error";
			} else if (rdfFileName.equals("USER EXIT")) {
				filePath = tpfFileName;
				rdfFileStatus = "Failed";
			} else if (rdfFileName.equals("RUN TIME ERROR")) {
				filePath = tpfFileName;
				rdfFileStatus = "Run Time Error";
			} else if (rdfFileName.equals("FILE NOT FOUND ERROR")) {
				filePath = tpfFileName;
				rdfFileStatus = "File Not Found Error";
			}
			SelfTestResult selfTestFile = new SelfTestResult(filePath, rdfFileStatus);

			SelfTestStateObject.addSelfTestResult(selfTestFile);

		} catch (Exception e) {
			throw e;
		}
	}

	private TestProcessResponse parseTestFileTest(String rdfFileLocaltion, String rdfFileName, String stageId,
			String stageName, String tpfFileName, String sessionId) {
		TestProcessResponse testProcessResponse = new TestProcessResponse();

		Response res = new Response();
		int dStartCount = 0;
		try {
			String filePath = rdfFileLocaltion + rdfFileName;
			String rdfFileStatus;

			if (rdfFileName != null && (!rdfFileName.equals("USER EXIT")) && (!rdfFileName.equals("RUN TIME ERROR"))
					&& (!rdfFileName.equals("FILE NOT FOUND ERROR"))) {

				res.setResponseCode(1);

				// Calling Parsing Method
				// StepParser.parseStepContext(filePath);

				// RDF Issue
				// Thread.sleep(5000);

				// Edited By: SUJI
				// Change Made for Point: 99(Mail:7 July status ||
				// Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
				// Change Made on: Detailed Data Result tab is not properly woking:

				waitFileUptoWrite(filePath);

				// Exit:
				// Point:99

				mongoUniqueIdentifier = RdfFileDetailsParser.saveProjectDetailsToMongoDB(sessionId, filePath);
				dStartCount = rdfFileParser.getDStarCount();
				// System.out.println("From Test Process ");
				// System.out.println("Rdf FilePath :"+filePath );
				// System.out.println("Session Id"+sessionId);
				 System.out.println("D* Count :"+dStartCount);

				rdfFileStatus = (rdfFileParser.isDStarFound()) ? "NOT OK" : "OK";
				// System.out.println("rdfFileStatus For D* Issue"+ rdfFileStatus);

				if (rdfFileParser.isParseFileError()) {
					rdfFileStatus = "Parse Error";
					res.setResponseCode(111);
				}

				if (rdfFileParser.isDStarFound()) {
					res.setResponseCode(111);
				}
				rdfFileParser.setDStarCount(0);
				rdfFileParser.setDStarFound(false);
				rdfFileParser.setParseFileError(false);

			} else if (rdfFileName != null && (rdfFileName.equals("USER EXIT"))) {
				filePath = tpfFileName;
				rdfFileStatus = "Failed";
				res.setResponseCode(111);
			} else if (rdfFileName != null && (rdfFileName.equals("RUN TIME ERROR"))) {
				filePath = tpfFileName;
				rdfFileStatus = "Run Time Error";
				res.setResponseCode(111);
			} // FILE NOT FOUND ERROR
			else if (rdfFileName != null && (rdfFileName.equals("FILE NOT FOUND ERROR"))) {
				filePath = tpfFileName;
				rdfFileStatus = "File Not Found Error";
				dStartCount = (-1);
				Notifications.showErrorAlert("File Not Found Error");

				res.setResponseCode(111);
			} else {
				filePath = tpfFileName;
				rdfFileStatus = "Parse Error";
				dStartCount = (-1);
				res.setResponseCode(111);
			}

			if (stageName.equals("CPCI")) {

				SelfTestResult selfTestFileCPCI = new SelfTestResult(filePath, rdfFileStatus);

				SelfTestStateObject.addSelfTestResult(selfTestFileCPCI);
				Debug.printDebug("--------------CPCI UPDATE --------  " + filePath + "   " + rdfFileStatus);
			}

			else if (stageName.equals("MANDATORY") || stageName.equals("GO NOGO") || stageName.equals("SRU")) {

				LRUTestResult lRUTestResult = new LRUTestResult(filePath, rdfFileStatus);

				LRUTestStateObject.addTestResult(lRUTestResult);

			} else if (stageName.equals("SESSION TEST")) {

				SessionTestResult sessionTestResult = new SessionTestResult(filePath, rdfFileStatus);

				SessionTestStateObject.addSessionTestResult(sessionTestResult);

			} else if (stageName.equals("HWATP TEST") || stageName.equals("INTERFACE TEST")
					|| stageName.equals("CUSTOM ONE") || stageName.equals("CUSTOM TWO")) {
				// System.out.println("Advanced File Path"+ filePath +" RdfFileStatus
				// "+rdfFileStatus);
				AdvancedTestResult advancedTestResult = new AdvancedTestResult(filePath, rdfFileStatus);
				AdvancedTestStateObject.addAdvancedTestResult(advancedTestResult);
			}

			res.setResponseMessage("Successful ");
			testProcessResponse.setdStarCount(dStartCount);

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Unsuccessful ");
			e.printStackTrace();
		}
		testProcessResponse.setResponse(res);

		return testProcessResponse;
	}

	private boolean waitFileUptoWrite(String filePath) {
		boolean ret = false;
		try {
			int loop = 0, loop1 = 0, curr = 0, past = 0;
			int sleep = 100;

			while (loop++ <= 30) {
				curr = (int) filePath.length();
				if (curr < 1) {
					Thread.sleep(sleep);
				} else {
					if (curr == past) {
						if (loop1++ == 2) {
							ret = true;
							break;
						} else {
							Thread.sleep(sleep);
						}
					} else {
						loop1 = 0;
						past = curr;
						Thread.sleep(sleep);
					}
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	/**
	 * Reads DotCom(.com) file and returns list of file names.
	 * 
	 * @param testFileName Name of the test file.
	 * @return List of file names.
	 */
	private List<String> dotComFileReader(String filePath) {
		List<String> listOfFileNames = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Debug.printDebug(line+" -- "+(!line.isEmpty())+" "+(line != ""));
				if ((!line.isEmpty())) {
					if (line.startsWith("@")) {
						line = line.substring(1);
						listOfFileNames.add(line);
					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return listOfFileNames;
	}

	public static List<String> fileNameforDotComList = new ArrayList<String>();
	public static List<String> sflcntList = new ArrayList<String>();

	// To Find the Total File Count On SRU:ProgressBar
	private List<String> getTestFilesSruTotalCount(List<String> stageIds) {
		List<String> fileIdByStages = new ArrayList<String>();
		for (String stageId : stageIds) {
			TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
			TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);
			Map<String, String> testFileResponseMap = testFileResponse.getTestFilesIdName();
			// System.out.println(testFileResponseMap);
			// List<String> fileIds =
			// testFileResponseMap.keySet().stream().collect(Collectors.toList());

			Map<String, String> testFilesIdName = getStageSelectedTestFileIds(stageId);

			// System.out.println("testFilesIdNametestFilesIdNametestFilesIdName SUJI II " +
			// testFilesIdName.values());

			fileIdByStages.addAll(testFilesIdName.values());
		}
		return fileIdByStages;
	}

	private void runTestProcess(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError, String stageName, String sessionStageMapId, Map<String, String> testFilesIdName,
			Set<String> keysSet) {
		try {
			// While Entering Adding File To Test State Object...

			for (String testFileId : listOfFileId) {
				SessionTestStateObject.setStageIdWithFileIds(stageId, testFileId);
			}

			// For Enabling Status On Continue With Error
			DFCCConstant.continueWithErrorFlag = continueWithError;
			DFCCConstant.repeatCount = repeatCount;
			DFCCConstant.stageId = stageId;
			DFCCConstant.sessionStageMapId = sessionStageMapId;
			DFCCConstant.testStarttime = String.valueOf(new Date());

			// Adding the Entry to Session Stages With Timing....

			SessionTimingService sessionTimingService = new SessionTimingService();
			if (stageName.equals("SESSION TEST")) {
				SessionTiming sessionTiming = new SessionTiming();
				sessionTiming = sessionTimingService.getStageTimingDetails(sessionId, stageId);
				if (sessionTiming.getStartDateTime().equals("Not Started")) {
					// (String sessionId, String stageId, String startTime, String endDateTime, int
					// noOfFilesExectued, int noFilesFailed, long runnedSeconds)
					sessionTimingService.updateSessionTimeForSessionTest(sessionId, stageId, DFCCConstant.testStarttime,
							"Running", 0, 0, 0);
				} else {
					sessionTimingService.updateSessionTimeForSessionTest(sessionId, stageId,
							sessionTiming.getStartDateTime(), "Running", sessionTiming.getNoOfFileExecuted(),
							sessionTiming.getNoOfFailedExecuted(), sessionTiming.getRunnedSeconds());
				}

			} else {
				sessionTimingService.addSessionTime(sessionId, stageId, DFCCConstant.testStarttime, "Running", 0, 0, 0);
			}

			// For generating unique test file IDs
			SessionStagesTestFilesResultService sessionStageTestFileResult = new SessionStagesTestFilesResultService();

			// Fetching RDF file path from RUN PATH MASTER
			DownloadFileService downloadFileService = new DownloadFileService();
			String rdfFileLocation = downloadFileService
					.getRDFLocationByRunConfigId(StateMachine.currentSessionDetails.getRunConfigId(), "rdf");

			// Populate list of file IDs for repetition
			List<String> listOfFileIds = generateFileIdsList(repeatCount, listOfFileId);

//			System.out.println("STAGE NAME : " + stageName + ", REPEATCOUNT : " + repeatCount + ", LIST OF FILE ID "
//					+ listOfFileId + ", Total FILE IDS * Repeat count : " + listOfFileIds);

			String rdfFileResult = "OK";
			String testState = null; // START, STOP, PAUSE
			int incrementNum = 0; // NUMBER OF TPF FILE PROCESSING COUNT
			boolean lastCount = false; // USED WHEN REPEAT COUNT IS MORE THEN ONE

//			// VIJAY : 31-JULY : Change 1:: To Know Test is STOPPED In com File.
			boolean comFileTestFlag = false; // change 1: END
			List<String> sruFileCount = StateMachine.getSelectedStageIds();
//			System.out.println("Check sruFileCount " + sruFileCount);

			// Fetch What Are Files Associated With Stages:ProgressBar
			List<String> listOfIdsSru = getTestFilesSruTotalCount(sruFileCount); // FETCHING LIST OF TEST FILE NAMES
																					// BASED ON STAGE ID
//			System.out.println("listOfIdsSrulistOfIdsSrulistOfIdsSru" + listOfIdsSru);
			fileNameforDotComList.clear();

			// Change for:ProgressBar
			if (!StateMachine.isSruTestFileCount() && !stageName.equals("SRU")) {
				for (String testFileIdName1 : listOfFileIds) {

					fileNameforDotComList.add(testFilesIdName.get(testFileIdName1));
				}
			} else {

				for (String testFileIdName1 : listOfIdsSru) {
					System.out.println("testFileIdName1" + testFileIdName1);
					fileNameforDotComList.add(testFileIdName1);
					StateMachine.setSruTestFileCount(false);
				}
			}
//SUJI Changed for .tst file with repeat count progress bar is not working :
			String fullList = String.join(",", fileNameforDotComList);
//			System.out.println("SUJI : FULL LIST ::" + fullList);

			int sflcnt = countFilesFromString(fullList);
//			System.out.println("SUJI ::sflcnt Count CHECH %% " + sflcnt);

			int nonDotComFileCount = (int) fileNameforDotComList.stream().filter(name -> !name.endsWith(".com"))
					.count();
//			System.out.println("nonDotComFileCount: " + nonDotComFileCount);

			int filesInsideDotCom = sflcnt - nonDotComFileCount;

			int totalFileCount = (filesInsideDotCom * repeatCount) + nonDotComFileCount;
//			System.out.println("SUJI ::totalFileCount Count CHECH ** " + totalFileCount);

			// Update Progress Bar
			sflcntupdateProgressBar(stageName, totalFileCount);

//			EXIT:

			sflcntupdateProgressBar(stageName, totalFileCount);
//			System.out.println("SUJI ::sflcnt Repeat Count Check::" + repeatCount);
//			System.out.println("SUJI ::sflcnt Count CHECH %%" + sflcnt);
//			System.out.println("SUJI ::totalFileCount Count CHECH **" + totalFileCount);

			StateMachine.setConfirmTestFileCompleted(true);

//			System.out.println("BEFOR  outerloop : " + listOfFileIds.size());

			// Outer loop for file IDs
			outerLoop: for (String testFileId : listOfFileIds) {

				// System.out.println("Stage Name Id ::: "+testFileId);
				incrementNum++;
				// System.out.println("SRU --- Entred Runt Test Total Test File ");
				// Initial : Dot com File Result
				String dotComFileResult = "OK";

				// Check if file ID has a corresponding name
				if (testFilesIdName.get(testFileId) != null) {

					String testFileName = testFilesIdName.get(testFileId);
					// System.out.println("Test File Name"+testFileName);

					if (incrementNum > (listOfFileIds.size() - listOfFileId.size())) {
						lastCount = true;
					}
					TestProcessDto testProcessDto;

					if (testFileName.endsWith(".com")) {
						tempRdfFileResult = "OK";
						tempDotComFileResult = "OK";
						// Read files if .com extension is found
						testProcessDto = processDotComFile(testFileName, stageName, rdfFileLocation, stageId, sessionId,
								rdfFileResult, dotComFileResult, continueWithError, testFileId, sessionStageMapId,
								lastCount, sessionStageTestFileResult);

//						// VIJAY : 31-JULY : Change 2:: To Know Test is STOPPED In Com File.

						String state = testProcessDto.getTestState();
//						System.out.println("Outside IF Test Process DTO  ::" + state);

						if (state != null && (state.equals("STOPED") || state.equals("FILE NOT FOUND ERROR"))
								&& (StateMachine.getTestState() != TestState.COMPLETED)) {
//							System.out.println("Inside IF Test Process DTO  ::" + state);

							// if (testProcessDto.getTestState() != null &&
							// testProcessDto.getTestState().equals("STOPED") ||
							// testProcessDto.getTestState().equals("FILE NOT FOUND ERROR")) {

							keysSet.remove(testFileId);
							SessionTestStateObject.removeFileId(stageId, testFileId);
							comFileTestFlag = true;
						}

//						System.out.println("There Checking COM FILE" + StateMachine.getTestState());
						if (StateMachine.getTestState().equals(TestState.STOPPED)) {
							keysSet.remove(testFileId);
							SessionTestStateObject.removeFileId(stageId, testFileId);
							comFileTestFlag = true;
						}

					} else {
						// Mani Details Data Change 26-AUG
						String testFileResultId = sessionStageTestFileResult.generateUniqueTestFilesResultIdIdOld();
						DFCCConstant.testFileResultId = testFileResultId;

						StateMachine.setConfirmTestStop(true);
						testProcessDto = runTestFile(testFileName, stageName, rdfFileLocation, stageId, sessionId,
								rdfFileResult, dotComFileResult, continueWithError, testFileId, sessionStageMapId,
								lastCount, testFileResultId);

						// File Removed On the While Stopping On the S
						String state = testProcessDto.getTestState();
						if (StateMachine.getTestState().equals(TestState.STOPPED)) {
							keysSet.remove(testFileId);
							SessionTestStateObject.removeFileId(stageId, testFileId);
							// comFileTestFlag = true;
						}

					}

					// Anuj : 31-JULY :: It will not run while running dotcom file
					AitessProcessControlManagement.getInstance().updateUIdfccStatus(false);

					// check TestState From Response if it stop then exit from the loop.
					if (testProcessDto.getTestState() != null && testProcessDto.getTestState().equals("STOPED")) {
//						System.out.println("Entred outer loop");
						testState = "STOPED";

						break outerLoop;
					}
					// update RdfFileResult
					if (testProcessDto.getRdfFileResult() != null
							&& testProcessDto.getRdfFileResult().equals("NOT OK")) {
						rdfFileResult = testProcessDto.getRdfFileResult();

					}
					// update dotComFileResult.
					if (testProcessDto.getDotComFileResult() != null
							&& testProcessDto.getDotComFileResult().equals("NOT OK")) {
						dotComFileResult = testProcessDto.getDotComFileResult();
					}

				}
				// SessionTestStateObject.getRunnedTestFileCount().set(SessionTestStateObject.getRunnedTestFileCount().get()+1);
//				if (stageName.equals("RACK1") || stageName.equals("CPCI") || stageName.equals("MANDATORY")
//						|| stageName.equals("GO NOGO") || stageName.equals("SESSION TEST")
//						|| stageName.equals("HWATP TEST") || stageName.equals("INTERFACE TEST")
//						|| stageName.equals("SRU")) {
//					updateProgressBar(stageName);
//					System.out.println("runTestProcess + updateProgressBar");
//				}

			} // Outer loop
				// VIJAY : 31-JULY : Change 3:: To Know Test is STOPPED In Com File.
			if (comFileTestFlag == true) {
				rdfFileResult = "NOT OK";
				comFileTestFlag = false; // Reseting Flag
			}
//			 Change 3: END

			// Stopped File Removing..29-08-2025
//			System.out.println("STOPPED CHECKING DFCC COLOUR FLAG" + DFCCConstant.stopColourFlag);
			if (DFCCConstant.stopColourFlag) {
				for (String testFileId : listOfFileId) {
					keysSet.remove(testFileId);
					SessionTestStateObject.removeFileId(stageId, testFileId);
				}
			}

			// .....Contains Fails and testFiles Id Need to Remove - 21-08-2025
			if (!continueWithError) {
				// boolean containsFail = removeFileCheck();

				boolean containsFail = removeFileCheck(StateMachine.currentSessionDetails.getSessionId(),
						DFCCConstant.stageId, DFCCConstant.sessionStageMapId, DFCCConstant.repeatCount);

//				System.out.println("Continue With Error   Flag:::" + continueWithError);
//				System.out.println("Contains Fail ::" + containsFail);
				if (!containsFail) {
					for (String testFileId : listOfFileId) {
						keysSet.remove(testFileId);
						SessionTestStateObject.removeFileId(stageId, testFileId);
					}
				}
			}
			// ....Contains Fails and testFiles Id Need to Remove - 21-08-2025

			// Determine stage result
			String stageResult = rdfFileResult.equals("OK") ? "COMPLETED with Success"
					: rdfFileResult.equals("NOT OK") ? "COMPLETED with Failure" : null;

			if (stageName.equals("SESSION TEST")) {
				stageResult = getStageResult(stageId, keysSet);

				if (continueWithError) {
//					System.out.println("Enter Stage Result ::" + stageResult);
					stageResult = "COMPLETED";
//					System.out.println("Enter Stage Result ::" + stageResult);
				}
			}

			if (testState != null && testState.equals("STOPED")) {
//				System.out.println("Entred Outer 2nd teststae after stop9999999999999999");
				listOfFileIds = new ArrayList<>();
				stageResult = "STOPED";
			}

			SessionFileManagement sessionFileManagement = new SessionFileManagement();
			long runnedSeconds = sessionFileManagement.getRunnedSeconds(DFCCConstant.testStarttime,
					DFCCConstant.testEndTime);
			// Entry the Stage End Time
			if (stageName.equals("SESSION TEST")) {
				SessionTiming sessionTiming = new SessionTiming();
				sessionTiming = sessionTimingService.getStageTimingDetails(sessionId, stageId);

				// (String sessionId, String stageId, String startTime, String endDateTime, int
				// noOfFilesExectued, int noFilesFailed, long runnedSeconds)
//				System.out.println("Session Before Total File Count :::" + DFCCConstant.totalFilesCount);
//				System.out.println("Session After Failed File Count  :::" + DFCCConstant.failedFilesCount);

				Map<String, Integer> countMap = getTotalFailCount(sessionId, stageId, DFCCConstant.sessionStageMapId);

//				System.out.println("Session Total Count  :" + countMap.get("totalCount"));
//				System.out.println("Session TotalFail Count  :" + countMap.get("failCount"));

				sessionTimingService.updateSessionTimeForSessionTest(sessionId, stageId,
						sessionTiming.getStartDateTime(), DFCCConstant.testEndTime,
						sessionTiming.getNoOfFileExecuted() + DFCCConstant.totalFilesCount,
						sessionTiming.getNoOfFailedExecuted() + DFCCConstant.failedFilesCount,
						sessionTiming.getRunnedSeconds() + runnedSeconds);

			} else {
//				System.out.println("Total File Count While Making Entry:::" + DFCCConstant.totalFilesCount);
//				System.out.println("Failed File Count  :::" + DFCCConstant.failedFilesCount);
				Map<String, Integer> countMap = getTotalFailCount(sessionId, stageId, DFCCConstant.sessionStageMapId);

//				System.out.println("Other Before Total File Count :::" + DFCCConstant.totalFilesCount);
//				System.out.println("Other After Failed File Count  :::" + DFCCConstant.failedFilesCount);

				sessionTimingService.updateSessionTimeForSessionTest(sessionId, stageId, DFCCConstant.testStarttime,
						DFCCConstant.testEndTime, DFCCConstant.totalFilesCount, DFCCConstant.failedFilesCount,
						runnedSeconds);
			}

			// update Stage Result : Session StageMapping
			updateSessionStageMapStatus(sessionStageMapId, stageResult);

			// Update state machine card

			updateStateMachineCardStatus(stageName, stageId, rdfFileResult, keysSet);

			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
			DFCCConstant.endTime = currentDateTime.format(formatter);

//			SUJI ADDED FLAG FOR .com File Stop issue:::(11-08-2025)
//			System.out.println("Before Enter Stop Colour Flag" + DFCCConstant.stopColourFlag);

			if (!DFCCConstant.stopColourFlag) {
//				System.out.println("After Enter Stop Colour Flag" + DFCCConstant.stopColourFlag);
				boolean dissableEnableflag = sessionFileManagement.getStatusFlagOfSelectedFile(sessionId, stageId,
						sessionStageMapId, repeatCount, continueWithError, false);
//				System.out.println("Disable Flag :::" + dissableEnableflag);
				StateMachine.setDissableEnable(dissableEnableflag);
				// flag Need to Update Session Testing Files Of Selected File-Ids
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String line;

	// String path;
	@SuppressWarnings({ "unlikely-arg-type", "unlikely-arg-type" })
	private TestProcessDto processDotComFile(String fileName, String stageName, String rdfFileLocation, String stageId,
			String sessionId, String rdfFileResult, String dotComFileResult, boolean continueWithError,
			String testFileId, String sessionStageMapId, boolean lastCount,
			SessionStagesTestFilesResultService sessionStageTestFileResult) {
		TestProcessDto responsetestProcessDto = new TestProcessDto();
		int lineCount = 0;
		Debug.printDebug("-----  START  ---------");

		Debug.printDebug("FILE NAME : " + fileName);
		Debug.printDebug(
				"Befor Starting Com File -- RDF : " + tempRdfFileResult + "  -- DOTCOM : " + tempDotComFileResult);

		String fileNam;
		File file = new File(fileName);

		if (StateMachine.getInputPathTestFile() != null) {
			fileNam = StateMachine.getInputPathTestFile() + file.getName();
		} else {
			fileNam = fileName;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(fileNam))) {
			Pattern pattern = Pattern.compile("\\b\\S+\\.com\\b");
			while ((line = br.readLine()) != null) {
				lineCount++;
				if ((!line.isEmpty())) {
					Debug.printDebug("Line Number : " + lineCount + " , Line is : " + line);
					if (line.startsWith("!"))
						continue; //
					if (line.startsWith("@") || line.contains(".com")) {
						line = line.substring(1);
						System.out.println("SUJI : LINE CHECK IN PROCESSDOTCOM FILE" + line);
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							// Extract and print the .com file name
							String comFileName = matcher.group();

							// Extracting File Path. To Fetch founded Dot Com File

							String fileName1 = file.getName();
							fileName1.trim();
							String path;

							if (StateMachine.getInputPathTestFile() != null) {

								path = StateMachine.getInputPathTestFile() + fileName1;
							} else {
								path = file.getAbsolutePath();// "/opt/dfcc-mk1/HW_ATP/input/";
							}

							path.trim();
							// VIGNESH CHNAGE for path correction
							int indexToTake = (path.length() - fileName1.length());
							path = path.substring(0, indexToTake);

							Debug.printDebug("Dot Com File " + comFileName);
							// System.out.println("SRU --- Entred .com file Method ");
							// Calling itself with Same Parameter (fileName only Change)
							processDotComFile(path + comFileName, stageName, rdfFileLocation, stageId, sessionId,
									rdfFileResult, dotComFileResult, continueWithError, testFileId, sessionStageMapId,
									lastCount, sessionStageTestFileResult);

						} else {
							StateMachine.setConfirmTestStop(true); // TO KNOW TEST IS RUNNING
							Debug.printDebug("TPF File " + line);

							// Mani Details Data Change 26-AUG
							String testFileResultId = sessionStageTestFileResult.generateUniqueTestFilesResultIdIdOld();
							DFCCConstant.testFileResultId = testFileResultId;
							TestProcessDto testProcessDto = runTestFile(line, stageName, rdfFileLocation, stageId,
									sessionId, rdfFileResult, dotComFileResult, continueWithError, testFileId,
									sessionStageMapId, lastCount, testFileResultId);

							// check TestState From Response if it stop then exit from the loop.
							if (testProcessDto.getTestState() != null
									&& testProcessDto.getTestState().equals("STOPED")) {
								return testProcessDto;
							}
							// update RdfFileResult
							if (testProcessDto.getRdfFileResult() != null
									&& testProcessDto.getRdfFileResult().equals("NOT OK")) {
								tempRdfFileResult = testProcessDto.getRdfFileResult();

							}
							// update dotComFileResult.
							if (testProcessDto.getDotComFileResult() != null
									&& testProcessDto.getDotComFileResult().equals("NOT OK")) {
								tempDotComFileResult = testProcessDto.getDotComFileResult();
							}

//							if (stageName.equals("RACK1") || stageName.equals("CPCI") || stageName.equals("MANDATORY")
//									|| stageName.equals("GO NOGO") || stageName.equals("SESSION TEST")
//									|| stageName.equals("HWATP TEST") || stageName.equals("INTERFACE TEST")
//									|| stageName.equals("SRU")) {
//								updateProgressBar(stageName);
//								System.out.println("processDotComFile + updateProgressBar");
//							}

						}
					} else {
						Debug.printDebug("Command is  " + line + " , runCommands : "
								+ AitessProcessControlManagement.getInstance().runCommands);

						// VIJAY : 31-JULY : Change 4:: To Stop running of Macro or command.
						if (handleTestState()) {
							TestProcessDto testProcessDto = new TestProcessDto();
							testProcessDto.setTestState("STOPED");

							StateMachine.setTextArea(true);
							return testProcessDto;
						} // change 4 :END

						// Call writing command to Terminal
						AitessProcessControlManagement.getInstance().runCommands = true;

						AitessProcessControlManagement.getInstance().WriteAitess1Command(line + "\n");

						while (AitessProcessControlManagement.getInstance().runCommands) {
							System.out.print("* ");
							Thread.sleep(10);
						}

					}

				}

			}
		} // Reading While loop.
		catch (Exception e) {
			e.printStackTrace();
		}
		Debug.printDebug("------- COMPLETED --------");
		responsetestProcessDto.setRdfFileResult(tempRdfFileResult);
		responsetestProcessDto.setDotComFileResult(tempDotComFileResult);
		return responsetestProcessDto;
	}

	private static final Set<String> processedFiles = new HashSet<>();

	// Test file progress indication
	// after chatgpt
	private static int countFilesFromString(String data) {
		processedFiles.clear();
		int fileCount = 0;
		String[] lines = data.split(",");

		for (String line : lines) {
			line = line.trim();
//			System.out.println("SUJI : LINE CHECK IN countFilesFromString FILE" + line);
			if (line.startsWith("!"))
				continue; // Ignore comments
			if (line.startsWith("@"))
				line = line.substring(1); // Remove '@' symbol

			if (line.contains(".tst") || line.contains(".tpf")) {
				fileCount++;
			} else if (line.endsWith(".com")) {
//				fileCount++;
				File file = new File(line);
				String fullPath = file.getAbsolutePath();
//				System.out.println("SUJI FILE LINE CHECK::" + fullPath);
				// Change on:29-04-2025
				if (StateMachine.getInputPathTestFile() != null) {
					fileCount += countFilesRecursively(StateMachine.getInputPathTestFile() + file.getName());
				} else {
					fileCount += countFilesRecursively(fullPath);
				}

				// Debugging output

			}
		}
//		System.out.println("SUJI CHECK countFilesFromString fileCount" + fileCount);
		return fileCount;
	}

	// Test file progress indication
	// after chatgpt
	private static int countFilesRecursively(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile() || processedFiles.contains(filePath)) {
			return 0;
		}

		processedFiles.add(filePath);
		int fileCount = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("!"))
					continue; // Skip comments

				String[] parts = line.split("[,\\s]+"); // Ensure correct splitting

				for (String part : parts) {
					part = part.trim();
					if (part.startsWith("@"))
						part = part.substring(1); // Remove '@' if present

					if (part.endsWith(".tst") || part.endsWith(".tpf")) {
						fileCount++;
					} else if (part.endsWith(".com")) {
						File nestedFile = new File(file.getParent(), part);

						// Change on:29-04-2025
						if (StateMachine.getInputPathTestFile() != null) {
							fileCount += countFilesRecursively(
									StateMachine.getInputPathTestFile() + nestedFile.getName());
						} else {

							fileCount += countFilesRecursively(nestedFile.getAbsolutePath());
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading file: " + filePath);
		}

		return fileCount;
	}

	private TestProcessDto runTestFile(String testFileName, String stageName, String rdfFileLocation, String stageId,
			String sessionId, String rdfFileResult, String dotComFileResult, boolean continueWithError,
			String testFileId, String sessionStageMapId, boolean lastCount, String uniqueTestFilesResultIdId) {

		TestProcessDto testProcessDto = new TestProcessDto();

		try {

			// System.out.println("SRU --- Entred Run Test File Method ");
			// Wait if text area is not ready
			checkTestisRunning();

			// Update State Machine : Set TextArea to FALSE.
			StateMachine.setTextArea(false);

			StateMachine.setConfirmTestStop(true);

			// Handle PAUSED or STOPPED states
			if (handleTestState()) {

				testProcessDto.setTestState("STOPED");
//				System.out.println("Entred in handle test state call method.........." + testProcessDto.getTestState());
				StateMachine.setTextArea(true);
				return testProcessDto;
			}

			ChannelStatusBeforeTestResponse channelState = AitessProcessControlManagement.getInstance()
					.checkChannelStatusBeforeAnyTest();

			int responseCode = channelState.getResponseCode();

			if (!stageName.equals("RACK1") && !stageName.equals("CPCI") && (responseCode == 0 || responseCode == 2)) {

				if (responseCode == 0) {
//					System.out.println("Entered 0");
					testProcessDto.setTestState("STOPED");
					StateMachine.setTextArea(true);
					return testProcessDto;
				} else if (responseCode == 2) {
//					System.out.println("Entered 2");
					StateMachine.setWdmStatusOfflineCheck(true);

					if (handleTestState()) {
						testProcessDto.setTestState("STOPED");
						StateMachine.setTextArea(true);
						return testProcessDto;
					}

				}
			}

			// Exit;
			// Change Made for Point: 4&39

			// Test Started Time
			String startTime = String.valueOf(new Date());

			// Getting RDF file Name from PerformTest()
			String rdfFileName = AitessProcessControlManagement.getInstance().performTest(testFileName);

			// Anuj : 31-JULY :: Commented
//			AitessProcessControlManagement.getInstance().updateUIdfccStatus(false);
			TestProcessResponse testProcessRes = getRdfFileResult(stageName, rdfFileLocation, rdfFileName, testFileName,
					stageId, sessionId);

			if (lastCount) {
				// Handle test result
				if (testProcessRes.getResponse().getResponseCode() == 111) {
					if (rdfFileResult.equals("OK")) {
						testProcessDto.setRdfFileResult("NOT OK");

					}

					if (dotComFileResult.equals("OK")) {
						testProcessDto.setDotComFileResult("NOT OK");
					}

					// check continue With Error
					if (continueWithError) {
						// SessionTestStateObject.setStageIdWithFileIds(stageId, testFileId);

					}
				} else {
					if (dotComFileResult.equals("OK")) {
						// SessionTestStateObject.setStageIdWithFileIds(stageId, testFileId);
					}
				}
			}

			// Test Ended Time...
			String endTime = String.valueOf(new Date());
			DFCCConstant.testEndTime = endTime;

			// ADD File ID to SESSION STAGE SELECTED TEST FILES
			String sessionStageSelectedTestFileId = addSelectedTestFile(testFileId, sessionStageMapId);

			// Save test file result.
			addTestFileResult(uniqueTestFilesResultIdId, sessionId, stageId, testFileId,
					String.valueOf(mongoUniqueIdentifier), rdfFileLocation,
					(rdfFileName != "FILE NOT FOUND ERROR")
							? (rdfFileName != null) ? (rdfFileName != "USER EXIT") ? rdfFileName : "RDF NOT GENERATED"
									: "RDF NOT GENERATED"
							: "RDF NOT GENERATED",
					(testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE",
					String.valueOf(testProcessRes.getdStarCount()), startTime, endTime, sessionStageSelectedTestFileId);
			mongoUniqueIdentifier = null;

			// File Copying
			// if (stageName.equals("MANDATORY") || stageName.equals("GO NOGO") ||
			// stageName.equals("SRU")) {
//Suji added for self test::
			if (stageName.equals("RACK1") || stageName.equals("CPCI") || stageName.equals("MANDATORY")
					|| stageName.equals("GO NOGO") || stageName.equals("SRU") || stageName.equals("SESSION TEST")
					|| stageName.equals("HWATP TEST") || stageName.equals("INTERFACE TEST")) {

				if (rdfFileName != null && !rdfFileName.equals("USER EXIT") && !rdfFileName.equals("RUN TIME ERROR")
						&& !rdfFileName.equals("FILE NOT FOUND ERROR")) {
					SessionFileManagement sessionFileManagement = new SessionFileManagement();
					String rdfFile = rdfFileLocation + rdfFileName;

					SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
					GetObjResponse sessionStages = sessionStagesSelectedStagesService.getSessionStagesMapp(sessionId,
							stageId);
					SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
					sessionStagesMapping = (SessionStagesMapping) sessionStages.getObject();
					String stagePath = sessionStagesMapping.getPath();

					Path sourcePath = Paths.get(rdfFile);
					Path destinationPath = Paths.get(stagePath);

					CopyFileDTO copyFileDTO = new CopyFileDTO();
					copyFileDTO.setRdfFileNamewithPath(rdfFileLocation + rdfFileName);
					copyFileDTO.setRdfFilePath(rdfFileLocation);
					copyFileDTO.setRdfFiledName(rdfFileName);
					copyFileDTO.setStageId(stageId);
					// System.out.println("Stage Id Power Auto ::"+stageId);
					copyFileDTO.setStagePath(stagePath);
					copyFileDTO.setCopyingFileId(uniqueTestFilesResultIdId);
					copyFileDTO.setdStarCount(testProcessRes.getdStarCount());
					copyFileDTO
							.setStatus((testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE");

					DFCCConstant.FailedStagesRdfPaths.add(copyFileDTO);

					if (DFCCConstant.logOutmoveFiles.containsKey(stageId)) {
						DFCCConstant.logOutmoveFiles.get(stageId).add(null);
					} else {
						List<CopyFileDTO> lst = new ArrayList<CopyFileDTO>();
						lst.add(copyFileDTO);
						DFCCConstant.logOutmoveFiles.put(stagePath, lst);
					}

				}

			}
			// Update State Machine to Set TextArea to TRUE.
			StateMachine.setTextArea(true);

//			Changed based K2 Instruction:::(20-08-2025)
			if (stageName.equals("RACK1") || stageName.equals("CPCI") || stageName.equals("MANDATORY")
					|| stageName.equals("GO NOGO") || stageName.equals("SESSION TEST") || stageName.equals("HWATP TEST")
					|| stageName.equals("INTERFACE TEST") || stageName.equals("SRU")) {
				updateProgressBar(stageName);
//				System.out.println("runTestProcess + updateProgressBar");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return testProcessDto;

	}

	private List<String> generateFileIdsList(int repeatCount, List<String> listOfFileId) {
		List<String> listOfFileIds = new ArrayList<>();
		for (int i = 1; i <= repeatCount; i++) {

			listOfFileIds.addAll(listOfFileId);

		}
//		System.out.println("SUJI CHECK listOfFileIds" + listOfFileIds);
		return listOfFileIds;
	}

	private List<String> getTestFileNames(String testFileName) {

		List<String> listOfTestFileNames = new ArrayList<>();
		if (testFileName.endsWith(".com")) {
			// Read files if .com extension is found
			listOfTestFileNames.addAll(dotComFileReader(testFileName));
		} else {
			listOfTestFileNames.add(testFileName);
		}
		return listOfTestFileNames;
	}

	private void checkTestisRunning() {
		if (!StateMachine.isTextArea()) {
			boolean flag = true;
			while (flag) {
				try {
					Thread.sleep(1000);
					if (StateMachine.isTextArea()) {
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Befor chaning ::
	private boolean handleTestState() {
//		System.out.println("Entred Handle testState method mmmm");

		if (StateMachine.getTestState() == TestState.PAUSED) {
			boolean loopFlag = true;
			while (loopFlag) {
				Debug.printDebug(StateMachine.getTestState());
//				System.out.println("Check puase resume::" + StateMachine.getTestState());
				if (StateMachine.getTestState() == TestState.RUNNING) {
					Debug.printDebug(StateMachine.getTestState());
					loopFlag = false;
				} else if (StateMachine.getTestState() == TestState.STOPPED) {
					loopFlag = false;
					Debug.printDebug(StateMachine.getTestState());
					return true;
				}
			}

		} else if (StateMachine.getTestState() == TestState.STOPPED) {
//			System.out.println("handle Stop Test Method condition");
			Debug.printDebug(StateMachine.getTestState());
			// System.out.println("Entred Stop Test )))))))))))))))))");
			return true;
		}

		return false;
	}

	private void updateProgressBar(String stageName) {
		try {

			switch (stageName) {

			case "RACK1":
				SelfTestStateObject.getRunnedSelfTestFileCount()
						.set(SelfTestStateObject.getRunnedSelfTestFileCount().get() + 1);

			case "CPCI":
				SelfTestStateObject.getRunnedSelfTestFileCount()
						.set(SelfTestStateObject.getRunnedSelfTestFileCount().get() + 1);

			case "MANDATORY":
				LRUTestStateObject.getRunnedLRUTestFileCount()
						.set(LRUTestStateObject.getRunnedLRUTestFileCount().get() + 1);
				break;

			case "GO NOGO":
				LRUTestStateObject.getRunnedLRUTestFileCount()
						.set(LRUTestStateObject.getRunnedLRUTestFileCount().get() + 1);
				break;

			case "SRU":
				LRUTestStateObject.getRunnedLRUTestFileCount()
						.set(LRUTestStateObject.getRunnedLRUTestFileCount().get() + 1);
				break;

			case "SESSION TEST":
				SessionTestStateObject.getRunnedTestFileCount()
						.set(SessionTestStateObject.getRunnedTestFileCount().get() + 1);
				break;

			case "HWATP TEST":
//				System.out.println("Before Inside HWATP TEST" + AdvancedTestStateObject.getRunnedHWATPTestFileCount());
				AdvancedTestStateObject.getRunnedHWATPTestFileCount()
						.set(AdvancedTestStateObject.getRunnedHWATPTestFileCount().get() + 1);
//				System.out.println("After Inside HWATP TEST" + AdvancedTestStateObject.getRunnedHWATPTestFileCount());
				break;
			case "INTERFACE TEST":
				AdvancedTestStateObject.getRunnedInterfaceTestFileCount()
						.set(AdvancedTestStateObject.getRunnedInterfaceTestFileCount().get() + 1);
				break;

			default:
				Debug.printDebug("INVALID Stage Name : " + stageName);
				break;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public Response runCommand(String command, String testTypeId, String testName) {
		Response res = new Response();
		try {

			// If AETS process failed to launch, return failure response
			if (checkAndUpdateAetsProcessStatus(testTypeId, null)) {
				resetAitessFailureStates();
				return createErrorResponse("AETS Failed to launch");
			}

//			System.out.println("Test Type Id ::Checking" + testTypeId);

			resetAitessFailureStates();
			if (testName.equals("CUSTOM ONE")) {

				StateMachine.setRunCommand(true);
			} else {

			}
			// Call writing command to Terminal
			AitessProcessControlManagement.getInstance().WriteAitess1Command(command + "\n");

		} catch (Exception e) {
			return createErrorResponse("Test Failled  " + e.getLocalizedMessage());
		}
		return res;
	}

	private Response otherThanrack1AndCpciChanges(String stageName) {
		Response res = new Response();
		if (!stageName.equals("RACK1") && !stageName.equals("CPCI")) {
			ChannelStatusBeforeTestResponse channelState = AitessProcessControlManagement.getInstance()
					.checkChannelStatusBeforeAnyTest();
			if (channelState.getResponseCode() == 0) {

				StateMachine.setConfirmTestStop(true);
				Debug.printDebug("Channel is Offline");

				res.setResponseCode(0); // FAIL
				res.setResponseMessage(channelState.getResponseMessage());

				return res;
			}
//			System.out.println("CHANNEL IS ONLINE");
			res.setResponseCode(1); // PASS
			res.setResponseMessage(channelState.getResponseMessage());

			return res;
		} else {
			res.setResponseCode(1);
			return res;
		}
	}

	// Contains Fails and testFiles Id Need to Remove - 21-08-2025
	public boolean removeFileCheck() {

		try {
//			System.out.println("SIZE :::" + DFCCConstant.FailedStagesRdfPaths.size());

			List<CopyFileDTO> failureList = DFCCConstant.FailedStagesRdfPaths.stream()
					.filter(e -> !e.getStatus().equals("SUCCESS")).collect(Collectors.toList());
//			System.out.println();

			/*
			 * for (CopyFileDTO copyFileDTO : DFCCConstant.FailedStagesRdfPaths) {
			 * System.out.println("MANI Suspect Result2 :::" + copyFileDTO.getStatus()); if
			 * (copyFileDTO.getStatus().equals("FAILURE")); { removeFlag = true; break; } }
			 */
//			System.out.println("failureList.size()" + failureList.size());
			if (failureList.size() > 0)
				return true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean removeFileCheck(String sessionId, String stageId, String sessionStagesMappingId, int repeatCount) {
		boolean response = false;

		try {

//			System.out.println("GetStatusFlagOfSelectedFile");
//			System.out.println("Sequence BE:::");
//			System.out.println("Stage ID for Runned :: " + stageId);
//			System.out.println("SessionSTage Map ID:::" + sessionStagesMappingId);
			// Session Selected Stages Mapping Service
//					SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
//					GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
//					SessionStagesMapping session = new SessionStagesMapping();
			//
//					session = (SessionStagesMapping) getObject.getObject();
//					String sessionStagesMappingId = session.getSessionStagesMappingId();
//					int repeatCount = session.getRepeatCount();			

			// Collecting the All Test FileId in the Stages
			TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
			GetResponse getResponseFileMapping = testFilesStagesMappingService
					.getTestFilesStagesMappingByLastLevelReference(stageId);
			List<TestFilesStagesMapping> testFilesStagesMappingList = new ArrayList();
			testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();

			List<String> testFileIdsInStage = new ArrayList<>();
			for (TestFilesStagesMapping testFilesStagesMapping : testFilesStagesMappingList) {
				testFileIdsInStage.add(testFilesStagesMapping.getTestFileId());
			}

			SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			GetResponse selectedGetResponse = sessionStagesSelectedTestFilesService
					.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);

			List<SessionStagesSelectedTestFiles> selectTestFileIds = new ArrayList();
			selectTestFileIds = (List<SessionStagesSelectedTestFiles>) selectedGetResponse.getResponseList();
			List<String> selectedFileIds = new ArrayList<String>();
			List<String> fileIdsSelected = new ArrayList<String>();
			List<String> nonDuplicatedFileIds = new ArrayList<String>();
			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();
			for (SessionStagesSelectedTestFiles sessionStagesSelectedTestFiles : selectTestFileIds) {

				// if(sessionStagesSelectedTestFiles.getTestFilesId().equals(testFileId))
				selectedFileIds.add(sessionStagesSelectedTestFiles.getSessionStagesSelectedTestFilesId());
				fileIdsSelected.add(sessionStagesSelectedTestFiles.getTestFilesId());
				selectedTestFileIdTestFileId.put(sessionStagesSelectedTestFiles.getSessionStagesSelectedTestFilesId(),
						sessionStagesSelectedTestFiles.getTestFilesId());
				if (!nonDuplicatedFileIds.contains(sessionStagesSelectedTestFiles.getTestFilesId())) {
					nonDuplicatedFileIds.add(sessionStagesSelectedTestFiles.getTestFilesId());
				}
			}

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse responseTestFile = sessionStagesTestFilesResultService
					.getSelectedTestFilesByIds(selectedFileIds);
			List<SessionStagesTestFilesResult> testFileResults = new ArrayList<>();
			testFileResults = (List<SessionStagesTestFilesResult>) responseTestFile.getResponseList();
			int testFileResultCount = 0;
			int filterTestCount = 0;

			// Files Count : 10
			// Repeat Count : 2
			// TotalFiles : 20

			if (repeatCount > 1) {
				testFileResultCount = testFileResults.size();
				filterTestCount = testFileResultCount / repeatCount;
				testFileResults = testFileResults.subList(filterTestCount, testFileResultCount);
			}

			// Filter Based On The Repeat Count..

			List<SessionStagesTestFilesResult> failureList = new ArrayList<SessionStagesTestFilesResult>();
			failureList = testFileResults.stream().filter(ses -> !ses.getTestStatus().equals("SUCCESS"))
					.collect(Collectors.toList());
//			System.out.println(" Failure List Size" + failureList.size());

			boolean rDFNotGeneratedError = false;

			if (failureList.size() > 0) {
				response = false;
			} else {
				response = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public Map<String, Integer> getTotalFailCount(String sessionId, String stageId, String sessionStagesMappingId) {
		Map<String, Integer> response = new HashMap<String, Integer>();

		try {

			DFCCConstant.failedFilesCount = 0;
			DFCCConstant.totalFilesCount = 0;

//			System.out.println("GetStatusFlagOfSelectedFile");
//			System.out.println("Sequence BE:::");
//			System.out.println("Stage ID for Runned :: " + stageId);
//			System.out.println("SessionSTage Map ID:::" + sessionStagesMappingId);
			// Session Selected Stages Mapping Service
//					SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
//					GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
//					SessionStagesMapping session = new SessionStagesMapping();
			//
//					session = (SessionStagesMapping) getObject.getObject();
//					String sessionStagesMappingId = session.getSessionStagesMappingId();
//					int repeatCount = session.getRepeatCount();			

			// Collecting the All Test FileId in the Stages
			TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
			GetResponse getResponseFileMapping = testFilesStagesMappingService
					.getTestFilesStagesMappingByLastLevelReference(stageId);
			List<TestFilesStagesMapping> testFilesStagesMappingList = new ArrayList();
			testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();

			List<String> testFileIdsInStage = new ArrayList<>();
			for (TestFilesStagesMapping testFilesStagesMapping : testFilesStagesMappingList) {
				testFileIdsInStage.add(testFilesStagesMapping.getTestFileId());
			}

			SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			GetResponse selectedGetResponse = sessionStagesSelectedTestFilesService
					.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);

			List<SessionStagesSelectedTestFiles> selectTestFileIds = new ArrayList();
			selectTestFileIds = (List<SessionStagesSelectedTestFiles>) selectedGetResponse.getResponseList();
			List<String> selectedFileIds = new ArrayList<String>();
			List<String> fileIdsSelected = new ArrayList<String>();
			List<String> nonDuplicatedFileIds = new ArrayList<String>();
			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();
			for (SessionStagesSelectedTestFiles sessionStagesSelectedTestFiles : selectTestFileIds) {

				// if(sessionStagesSelectedTestFiles.getTestFilesId().equals(testFileId))
				selectedFileIds.add(sessionStagesSelectedTestFiles.getSessionStagesSelectedTestFilesId());
				fileIdsSelected.add(sessionStagesSelectedTestFiles.getTestFilesId());
				selectedTestFileIdTestFileId.put(sessionStagesSelectedTestFiles.getSessionStagesSelectedTestFilesId(),
						sessionStagesSelectedTestFiles.getTestFilesId());
				if (!nonDuplicatedFileIds.contains(sessionStagesSelectedTestFiles.getTestFilesId())) {
					nonDuplicatedFileIds.add(sessionStagesSelectedTestFiles.getTestFilesId());
				}
			}

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse responseTestFile = sessionStagesTestFilesResultService
					.getSelectedTestFilesByIds(selectedFileIds);
			List<SessionStagesTestFilesResult> testFileResults = new ArrayList<>();
			testFileResults = (List<SessionStagesTestFilesResult>) responseTestFile.getResponseList();
			int testFileResultCount = 0;
			int filterTestCount = 0;

			response.put("totalCount", testFileResults.size());
			DFCCConstant.totalFilesCount = testFileResults.size();

			List<SessionStagesTestFilesResult> failureList = new ArrayList<SessionStagesTestFilesResult>();
			failureList = testFileResults.stream().filter(ses -> !ses.getTestStatus().equals("SUCCESS"))
					.collect(Collectors.toList());
//			System.out.println(" Failure List Size" + failureList.size());

			boolean rDFNotGeneratedError = false;

			if (failureList.size() > 0) {
				response.put("failCount", failureList.size());
				DFCCConstant.failedFilesCount = failureList.size();
			} else {
				response.put("failCount", 0);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	// Contains Fails and testFiles Id Need to Remove - 21-08-2025

	// SUJI LOGIC : sflcnt is totalFileCount
	private void sflcntupdateProgressBar(String stageName, int sflcnt) {
		if (stageName.equals("SESSION TEST")) {

			SessionTestStateObject.setTotalSelectedTestFileCount(sflcnt);
		} else if (stageName.equals("HWATP TEST")) {
			AdvancedTestStateObject.setTotalHWATPSelectedTestFileCount(sflcnt);

		}

		else if (stageName.equals("INTERFACE TEST")) {

			AdvancedTestStateObject.setTotalInterfaceSelectedTestFileCount(sflcnt);
		}

		else if (stageName.equals("SRU")) {
			// System.out.println("SRU --- Entred Total Test FIle in Run Test FIle ");
			LRUTestStateObject.setTotalLRUSelectedTestFileCount(sflcnt);

		} else if (stageName.equals("MANDATORY")) {

			LRUTestStateObject.setTotalLRUSelectedTestFileCount(sflcnt);
		} else if (stageName.equals("GO NOGO")) {

			LRUTestStateObject.setTotalLRUSelectedTestFileCount(sflcnt);
		}
	}
}
