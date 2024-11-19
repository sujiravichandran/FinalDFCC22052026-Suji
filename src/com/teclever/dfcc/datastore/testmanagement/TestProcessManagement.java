package com.teclever.dfcc.datastore.testmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesSelectedTestFiles;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.TrailSessionEntityService;
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

public class TestProcessManagement {

	ObjectId mongoUniqueIdentifier;
	private String tempRdfFileResult="OK";
	
	private String tempDotComFileResult="OK";
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

		Response res = new Response();
		try {
			Debug.printDebug("Test Proces Controll Entty point : "+sessionId+" Stage Id : "+stageId+" repeatCount : "+repeatCount+" ListOfFile "+ listOfFileId+" ContinueWithError "+ continueWithError+" StageName "+stageName+" TestTypeID "+ testTypeId+" ofpConfig "+ofpConfig);
			// Retrieve Highest RunCount Session Stage Mapping Data.
			SessionStagesMapping sessionStagesMapping = getSessionStageMapping(sessionId, stageId);

			if (sessionStagesMapping == null) {
				return createErrorResponse("Failed to retrieve session stage mapping");
			}
			// If AETS process failed to launch, return failure response
			if (checkAndUpdateAetsProcessStatus(testTypeId, ofpConfig)) {
				resetAitessFailureStates();
				return createErrorResponse("AETS Failed to launch");
			}

			resetAitessFailureStates();

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

//			SessionFileManagement sessionFileManagement = new SessionFileManagement();
//			boolean popupflag = sessionFileManagement.getTestFilesRunnedSuccess(sessionId, stageId);
//
//			if (popupflag) {
//				SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
//				SessionTestStateObject.setPopupStageId(stageId);
//			}
		} catch (Exception e) {
			return createErrorResponse("Test Start Unsuccessfull.. ");

		}
		return res;
	}

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
					SelfTestStateObject.getRack1Status().set(false);
					;
				}

				break;
			case "CPCI":

				SelfTestStateObject.updateSelfTestcPCICardstatus(stageId, rdfFileResult);
				switch (SelfTestStateObject.getSelfTestRunningCard()) {

				case B1553:
					SelfTestStateObject.getB1553Status().set(false);

					break;
				case RS422_1:
					SelfTestStateObject.getRs422_1Status().set(false);

					break;
				case RS422_2:
					SelfTestStateObject.getrS422_2Status().set(false);

					break;

				default:
					break;
				}
				break;

			case "MANDATORY":
				Debug.printDebug("----- Mandatory ----");
				LRUTestStateObject.updateLruMandatoryCardstatus(stageId, rdfFileResult);

				switch (LRUTestStateObject.getLRUTestRunningCard()) {

				case SPIL_LINK:
					Debug.printDebug("----- SPIL_LINK ----");
					LRUTestStateObject.getSpilLinkStatus().set(false);
					break;
				case POWER_SUPPLY:
					Debug.printDebug("----- POWER_SUPPLY ----");
					LRUTestStateObject.getPowerSupplyStatus().set(false);
					break;
				case PBIT:
					Debug.printDebug("----- PBIT ----");
					LRUTestStateObject.getPbitStatus().set(false);
					break;
				case AD_DA_INTERFACE:
					Debug.printDebug("----- AD_DA_INTERFACE ----");
					LRUTestStateObject.getAd_daInterfaceStatus().set(false);
					break;
				case INITIALIZE_LRU:
					Debug.printDebug("----- INITIALIZE_LRU ----");
					LRUTestStateObject.getInitializeLRUStatus().set(false);
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
					break;
				case OFP_LOADING:
					LRUTestStateObject.getOfpLoadingStatus().set(false);
					break;
				case PI_CHECK:
					LRUTestStateObject.getPiCheckStatus().set(false);
					break;
				default:
					break;
				}

				break;
			case "SRU":
				LRUTestStateObject.updateSelectedSubStagesList(stageId, "COMPLETED");
				LRUTestStateObject.updateLruSruCardstatus(stageId, rdfFileResult);
				StateMachine.setTestState(TestState.COMPLETED);
				Debug.printDebug("------Stage Id----- " + stageId + "  ------- RDF FILE Result----- " + rdfFileResult);
				break;
			case "SESSION TEST":

				SessionTestStateObject.updateEndLeafMapStatus(stageId, getStageResult(stageId, keysSet));

				// Debug.printDebug("CASE : SESSION TEST");
//				SessionTestStateObject.updateEndLeafMapStatus(stageId, "COMPLETED");
				SessionTestStateObject.getRunningTestLeafStatus().set(true);
				StateMachine.setTestState(TestState.COMPLETED);

				break;

			case "HWATP TEST":

				Debug.printDebug("CASE : HWATP TEST");
				AdvancedTestStateObject.getHwatpTestStatus().set(false);
				break;
			case "INTERFACE TEST":
				Debug.printDebug("CASE : INTERFACE TEST");
				AdvancedTestStateObject.getInterfaceTestStatus().set(false);
				break;

			case "CUSTOM ONE":
				Debug.printDebug("CASE : CUSTOM ONE");
				AdvancedTestStateObject.getCustomTest1Status().set(false);
				break;
			case "CUSTOM TWO":
				Debug.printDebug("CASE : CUSTOM TWO");
				AdvancedTestStateObject.getCustomTest2Status().set(false);
				break;
			default:
				Debug.printDebug("INVALID TEST TYPE ID ");
				break;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	private String getStageResult(String stageId, Set<String> fileIds) {
		Set<String> setOfFileIds = SessionTestStateObject.getStageIdWithFileIds().get(stageId);
		if (setOfFileIds != null && setOfFileIds.size() > 0 && setOfFileIds.equals(fileIds)) {
			return "COMPLETED";
		} else {
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
			if (rdfFileName == null || rdfFileName.equals("USER EXIT") || rdfFileName.equals("RUN TIME ERROR")) {
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

			if (rdfFileName != null && (!rdfFileName.equals("USER EXIT")) && (!rdfFileName.equals("RUN TIME ERROR"))) {

				res.setResponseCode(1);

				// Calling Parsing Method
				// StepParser.parseStepContext(filePath);
				mongoUniqueIdentifier = RdfFileDetailsParser.saveProjectDetailsToMongoDB(sessionId, filePath);
				dStartCount = rdfFileParser.getDStarCount();

				rdfFileStatus = (rdfFileParser.isDStarFound()) ? "NOT OK" : "OK";

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
//				Debug.printDebug(line+"  -- "+(!line.isEmpty())+"   "+(line != ""));
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

	private void runTestProcess(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError, String stageName, String sessionStageMapId, Map<String, String> testFilesIdName,
			Set<String> keysSet) {
		try {

			// For generating unique test file IDs
			SessionStagesTestFilesResultService sessionStageTestFileResult = new SessionStagesTestFilesResultService();

			// Fetching RDF file path from RUN PATH MASTER
			DownloadFileService downloadFileService = new DownloadFileService();
			String rdfFileLocation = downloadFileService
					.getRDFLocationByRunConfigId(StateMachine.currentSessionDetails.getRunConfigId(), "rdf");

			// Populate list of file IDs for repetition
			List<String> listOfFileIds = generateFileIdsList(repeatCount, listOfFileId);

			String rdfFileResult = "OK";
			String testState = null;
			int incrementNum = 0;
			boolean lastCount = false;

			// Outer loop for file IDs
			outerLoop: for (String testFileId : listOfFileIds) {
				incrementNum++;
				
				// Initial : Dot com File Result 
				String dotComFileResult = "OK";

				// Check if file ID has a corresponding name
				if (testFilesIdName.get(testFileId) != null) {

					String testFileName = testFilesIdName.get(testFileId);
					
					if (incrementNum > (listOfFileIds.size() - listOfFileId.size())) {
						lastCount = true;
					}
					TestProcessDto testProcessDto;
					
					if (testFileName.endsWith(".com")) {

						// Read files if .com extension is found
						 testProcessDto =	processDotComFile(testFileName, stageName,
								 rdfFileLocation, stageId,  sessionId, rdfFileResult,  dotComFileResult,
								 continueWithError,  testFileId,  sessionStageMapId,  lastCount,
									sessionStageTestFileResult);
			
					} else {

						 testProcessDto = runTestFile(testFileName, stageName, rdfFileLocation, stageId,
								sessionId, rdfFileResult, dotComFileResult, continueWithError, testFileId,
								sessionStageMapId, lastCount,
								sessionStageTestFileResult.generateUniqueTestFilesResultIdId());
					}
					// check TestState From Response if it stop then exit from the loop.
					if (testProcessDto.getTestState() != null && testProcessDto.getTestState().equals("STOPED")) {
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
				if (stageName.equals("SESSION TEST") || stageName.equals("HWATP TEST")
						|| stageName.equals("INTERFACE TEST") || stageName.equals("SRU")) {
					updateProgressBar(stageName);
				}
			} // Outer loop

			// Update state machine card status
			updateStateMachineCardStatus(stageName, stageId, rdfFileResult, keysSet);

			// Determine stage result
			String stageResult = rdfFileResult.equals("OK") ? "COMPLETED with Success"
					: rdfFileResult.equals("NOT OK") ? "COMPLETED with Failure" : null;

			if (stageName.equals("SESSION TEST")) {
				stageResult = getStageResult(stageId, keysSet);
				//Session Test Popup 
				SessionFileManagement sessionFileManagement = new SessionFileManagement();
				boolean popupflag = sessionFileManagement.getTestFilesRunnedSuccess(sessionId, stageId);

				if (popupflag) {
					Debug.printDebug("Pop-UP Flag True");
					SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
					SessionTestStateObject.setPopupStageId(stageId);
				}
				
			}
			if (testState != null && testState.equals("STOPED")) {
				stageResult = "STOPED";
			}

			// update Stage Result : Session StageMapping
			updateSessionStageMapStatus(sessionStageMapId, stageResult);
			
			
			/*SessionFileManagement sessionFileManagement = new SessionFileManagement();
			boolean popupflag = sessionFileManagement.getTestFilesRunnedSuccess(sessionId, stageId);

			if (popupflag) {
				Debug.printDebug("Test Process Management");
				SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
				SessionTestStateObject.setPopupStageId(stageId);
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TestProcessDto processDotComFile(String fileName,String stageName,
			String rdfFileLocation, String stageId, String sessionId,String rdfFileResult, String dotComFileResult,
			boolean continueWithError, String testFileId, String sessionStageMapId, boolean lastCount,
			SessionStagesTestFilesResultService sessionStageTestFileResult) {
		TestProcessDto responsetestProcessDto = new TestProcessDto();
		int lineCount =0;
		Debug.printDebug("-----  START  ---------");
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				lineCount++;
				if ((!line.isEmpty())) {
					Debug.printDebug("Line Number : "+lineCount + " , Line is : "+line);
					if (line.startsWith("@")) {
						line = line.substring(1);

						if (line.endsWith(".com")) {
							
							//Extracting File Path. To Fetch founded Dot Com File
							File file = new File(fileName);
				            fileName = file.getName();
				            String path = file.getParent();
				            Debug.printDebug("Dot Com File "+line);
				            //Calling itself with Same Parameter (fileName only Change)
							processDotComFile(path+File.separator+line, stageName,
									rdfFileLocation, stageId, sessionId, rdfFileResult, dotComFileResult,
									continueWithError, testFileId, sessionStageMapId, lastCount,sessionStageTestFileResult);

						}
						Debug.printDebug("TPF File "+line);
						TestProcessDto testProcessDto = runTestFile(line, stageName,
								rdfFileLocation, stageId, sessionId, rdfFileResult, dotComFileResult,
								continueWithError, testFileId, sessionStageMapId, lastCount,sessionStageTestFileResult.generateUniqueTestFilesResultIdId());

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

					} else {
						Debug.printDebug("Command is  "+line +" , runCommands : "+AitessProcessControlManagement.getInstance().runCommands);
						// Call writing command to Terminal
						AitessProcessControlManagement.getInstance().runCommands = true;
						
						AitessProcessControlManagement.getInstance().WriteAitess1Command(line + "\n");
						
						
						while (AitessProcessControlManagement.getInstance().runCommands) {
							System.out.print("* " );
								Thread.sleep(10);
							}

						}
//						// Wait for 5 sec to Complete.
//						Thread.sleep(5000);
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
	private TestProcessDto runTestFile(String testFileName, String stageName, String rdfFileLocation, String stageId,
			String sessionId, String rdfFileResult, String dotComFileResult, boolean continueWithError,
			String testFileId, String sessionStageMapId, boolean lastCount, String uniqueTestFilesResultIdId) {

		TestProcessDto testProcessDto = new TestProcessDto();

		try {

			// Wait if text area is not ready
			checkTestisRunning();

			// Update State Machine : Set TextArea to FALSE.
			StateMachine.setTextArea(false);

			// Handle PAUSED or STOPPED states
			if (handleTestState() == true) {
				testProcessDto.setTestState("STOPED");
				return testProcessDto;
			}

			// Test Started Time
			String startTime = String.valueOf(new Date());

			// Getting RDF file Name from PerformTest()
			String rdfFileName = AitessProcessControlManagement.getInstance().performTest(testFileName);
			AitessProcessControlManagement.getInstance().updateUIdfccStatus(false);
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
						SessionTestStateObject.setStageIdWithFileIds(stageId, testFileId);

					}
				} else {
					if (dotComFileResult.equals("OK")) {
						SessionTestStateObject.setStageIdWithFileIds(stageId, testFileId);
					}
				}
			}

			// Test Ended Time
			String endTime = String.valueOf(new Date());

			// ADD File ID to SESSION STAGE SELECTED TEST FILES
			String sessionStageSelectedTestFileId = addSelectedTestFile(testFileId, sessionStageMapId);

			// Save test file result.
			addTestFileResult(uniqueTestFilesResultIdId, sessionId, stageId, testFileId,
					String.valueOf(mongoUniqueIdentifier), rdfFileLocation,
					(rdfFileName != null) ? (rdfFileName != "USER EXIT") ? rdfFileName : "RDF NOT GENERATED"
							: "RDF NOT GENERATED",
					(testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE",
					String.valueOf(testProcessRes.getdStarCount()), startTime, endTime, sessionStageSelectedTestFileId);
			mongoUniqueIdentifier=null;

			//File Copying
			if (stageName.equals("MANDATORY") || stageName.equals("GO NOGO") || stageName.equals("SRU")) {
				
				SessionFileManagement sessionFileManagement = new SessionFileManagement();
				String rdfFile = rdfFileLocation + rdfFileName;
				
				
				SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
				GetObjResponse sessionStages= sessionStagesSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
				SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
				sessionStagesMapping = (SessionStagesMapping) sessionStages.getObject();
				String stagePath = sessionStagesMapping.getPath();
				 
				Path sourcePath = Paths.get(rdfFile);
				Path destinationPath = Paths.get(stagePath);
				sessionFileManagement.copyFilesToOutputFolder(sourcePath, destinationPath);
				
			}
			// Update State Machine to Set TextArea to TRUE.
			StateMachine.setTextArea(true);

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

	private boolean handleTestState() {

		if (StateMachine.getTestState() == TestState.PAUSED) {
			boolean loopFlag = true;
			while (loopFlag) {
				Debug.printDebug(StateMachine.getTestState());

				if (StateMachine.getTestState() == TestState.RUNNING) {

					loopFlag = false;
				} else if (StateMachine.getTestState() == TestState.STOPPED) {
					loopFlag = false;
					return true;
				}
			}

		} else if (StateMachine.getTestState() == TestState.STOPPED) {
			return true;
		}

		return false;
	}

	private void updateProgressBar(String stageName) {
		try {

			switch (stageName) {
			
			case "SRU":
				LRUTestStateObject.getRunnedLRUTestFileCount()
					.set(LRUTestStateObject.getRunnedLRUTestFileCount().get() + 1);
				break;

			case "SESSION TEST":
				SessionTestStateObject.getRunnedTestFileCount()
						.set(SessionTestStateObject.getRunnedTestFileCount().get() + 1);
				break;

			case "HWATP TEST":
				AdvancedTestStateObject.getRunnedHWATPTestFileCount()
						.set(AdvancedTestStateObject.getRunnedHWATPTestFileCount().get() + 1);
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
}
