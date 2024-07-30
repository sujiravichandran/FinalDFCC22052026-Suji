package com.teclever.dfcc.datastore.testmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesSelectedTestFiles;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.dto.TestProcessResponse;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.resultstore.resultmanagement.RdfFileDetailsParser;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestRunningCard;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject.SessionTestResult;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.aitessRunning;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.rdfFileParser;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class TestProcessManagement {

	ObjectId mongoUniqueIdentifier;

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
			boolean continueWithError, String stageName, String testTypeId) {

		// Service to retrieve session stage mapping
		SessionSelectedStagesService sessionStagesSelectedStage = new SessionSelectedStagesService();
		GetObjResponse getObjResponse = sessionStagesSelectedStage.getSessionStagesMapp(sessionId, stageId);
		SessionStagesMapping sessionStagesMapping = (SessionStagesMapping) getObjResponse.getObject();

		// Retrieve SessionStagesMapping Id (primary key)
		String sessionStageMapId = sessionStagesMapping.getSessionStagesMappingId();
		int runCount = sessionStagesMapping.getRunCount();

		Response res = new Response();

		// Check and update AETS process status
		AitessProcessControlManagement.getInstance().check(testTypeId);

		// If AETS process failed to launch, return failure response
		if (aitessRunning.isAitess1SwitchedFailed() || aitessRunning.isAitess2SwitchedFailed()) {
			res.setResponseCode(0);
			res.setResponseMessage("   AETS Failed to launch ");
			resetAitessFailureStates();
			return res;
		}

		resetAitessFailureStates();

		try {
			// Update SESSION ENTITY with start data
			res = updateSessionEntityStartData(sessionId);
			if (res.getResponseCode() == 0) {
				return res;
			}
			// Update SESSION STAGE MAPPING with Repeat count and Status and RunCount
			res = sessionStagesSelectedStage.updateRepeatAndRunCountStatus(sessionStageMapId, repeatCount, "STARTED");
			if (res.getResponseCode() == 0) {
				return res;
			}

			// Getting File ID and FileName as Key Value.
			TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
			TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);
			Map<String, String> testFilesIdName = testFileResponse.getTestFilesIdName();

			// Runnable to execute the test process
			Runnable runTestThread = () -> {

				System.out.println(" 1 ----  Thread Start ");
				String startTime;
				String endTime;
				String rdfFileName = null;
				String rdfFileResult = "OK";
				String testState = null;
				List<String> listOfFileIds = new ArrayList<>();

				// Service class for generating unique test file IDs
				SessionStagesTestFilesResultService sessionStageTestFileResult = new SessionStagesTestFilesResultService();

				// Getting RDF file path from RUN PATH MASTER
				DownloadFileService downloadFileService = new DownloadFileService();
				String rdfFileLocation = downloadFileService
						.getRDFLocationByRunConfigId(StateMachine.currentSessionDetails.getRunConfigId(), "rdf");

				// Populate list of file IDs for repetition
				for (int i = 1; i <= repeatCount; i++) {

					listOfFileIds.addAll(listOfFileId);

				}

				String testFileName;
				List<String> listOfTestFileNames = new ArrayList<>();
				String sessionStageSelectedTestFileId;

				// Outer loop for file IDs
				outerLoop: for (String testFileId : listOfFileIds) {

					// Check if file ID has a corresponding name
					if (testFilesIdName.get(testFileId) != null) {
						listOfTestFileNames.clear();
						testFileName = testFilesIdName.get(testFileId);

						if (testFileName.endsWith(".com")) {
							// Read files if .com extension is found
							listOfTestFileNames.addAll(dotComFileReader(testFileName));
						} else {
							listOfTestFileNames.add(testFileName);
						}
						
						// Inner loop for file names
						for (String tpfFileName : listOfTestFileNames) {

							// Wait if text area is not ready
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
							// Update State Machine : Set TextArea to FALSE.
							StateMachine.setTextArea(false);

							// Handle PAUSED or STOPPED states
							if (StateMachine.getTestState() == TestState.PAUSED) {
								boolean loopFlag = true;
								while (loopFlag) {
									System.out.println(StateMachine.getTestState());
									if (StateMachine.getTestState() == TestState.RUNNING) {

										loopFlag = false;
									} else if (StateMachine.getTestState() == TestState.STOPPED) {
										testState = "STOPED";
										loopFlag = false;
										break outerLoop;
									}
								}

							} else if (StateMachine.getTestState() == TestState.STOPPED) {
								break outerLoop;
							}

							// Test Started Time
							startTime = String.valueOf(new Date());

							// Getting RDF file Name from PerformTest() 
							rdfFileName = AitessProcessControlManagement.getInstance().performTest(tpfFileName);

							TestProcessResponse testProcessRes = getRdfFileResult(stageName, rdfFileLocation,
									rdfFileName, tpfFileName, stageId, sessionId);
							System.out.println("   ->  RDF File Result : " + rdfFileName);

							// Handle test result
							if (testProcessRes.getResponse().getResponseCode() == 111) {
								if (rdfFileResult.equals("OK")) {
									rdfFileResult = "NOT OK";
								}
								
								// check continue With Error
								if (!continueWithError) {
									break outerLoop;
								}
							}

							// Test Ended Time
							endTime = String.valueOf(new Date());

							// ADD File ID to SESSION STAGE SELECTED TEST FILES
							sessionStageSelectedTestFileId = addSelectedTestFile(testFileId, sessionStageMapId);

							// Save test file result.
							addTestFileResult(sessionStageTestFileResult.generateUniqueTestFilesResultIdId(), sessionId,
									stageId, testFileId, String.valueOf(mongoUniqueIdentifier), rdfFileLocation,
									(rdfFileName != null)
											? (rdfFileName != "USER EXIT") ? rdfFileName : "RDF NOT GENERATED"
											: "RDF NOT GENERATED",
									(testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE",
									String.valueOf(testProcessRes.getdStarCount()), startTime, endTime,
									sessionStageSelectedTestFileId);

							// Update State Machine to Set TextArea to TRUE.
							StateMachine.setTextArea(true);
						} // Inner loop
					}

				} // Outer loop

				// Update state machine card status
				updateStateMachineCardStatus(stageName, stageId, rdfFileResult);

				// Determine stage result
				String stageResult = rdfFileResult.equals("OK") ? "COMPLETED with Success"
						: rdfFileResult.equals("NOT OK") ? "COMPLETED with Failure" : null;

				if (testState.equals("STOPED")) {
					stageResult = "STOPED";
				}
				SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
				if (runCount == 0) {
					// Update SESSION STAGE MAPPING : Set Status to COMPLETED
//					/* Response response = */ sessionStagesSelectedStagesService
//							.updateSessionStagesBySessionIdAndStageId(repeatCount, sessionId, stageId, stageResult);
					sessionStagesSelectedStagesService.updateRepeatAndRunCountStatus(sessionStageMapId, repeatCount,
							stageResult);
				} else {
					sessionStagesSelectedStagesService.addSessionStagesBySessionIdAndStageId(sessionStageMapId,
							repeatCount, (runCount + 1), stageResult);

				}

			};

			// Thread START
			Thread startTestProcessThread = new Thread(runTestThread);
			startTestProcessThread.start();

			res.setResponseCode(1);
			res.setResponseMessage("Test Started ");

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Test Start Unsuccessfull.. ");
		}
		return res;
	}

	// Reset Aitess failure states
	private void resetAitessFailureStates() {
		aitessRunning.setAitess1SwitchedFailed(false);
		aitessRunning.setAitess2SwitchedFailed(false);

	}

	/**
     * Get RDF file result based on stage name and other parameters.
     * @param stageName Name of the stage.
     * @param rdfFileLocation Path to RDF files.
     * @param rdfFileName Name of the RDF file.
     * @param oneFileName Name of the test file.
     * @param stageId ID of the stage.
     * @param sessionId ID of the session.
     * @return TestProcessResponse object with the result.
     */
	private TestProcessResponse getRdfFileResult(String stageName, String rdfFileLocation, String rdfFileName,
			String oneFileName, String stageId, String sessionId) {
		TestProcessResponse testProcessRes = new TestProcessResponse();
		try {
			if (stageName.equals("RACK1")) {
				System.out.println("   ->  rack1TestFileTest()  ");
				testProcessRes = rack1TestFileTest(rdfFileLocation, rdfFileName, oneFileName);

			} else if (stageName.equals("CPCI") || stageName.equals("MANDATORY") || stageName.equals("GO NOGO")
					|| stageName.equals("SRU") || stageName.equals("SESSION TEST")) {
				System.out.println("   ->  7  parseTestFileTest()  ");

				testProcessRes = parseTestFileTest(rdfFileLocation, rdfFileName, stageId, stageName, oneFileName,
						sessionId);

			}
		} catch (Exception e) {
			throw e;
		}
		return testProcessRes;
	}

	//	From SESSION ENTITY : Add Session Start Time.
	private Response updateSessionEntityStartData(String sessionId) {
		Response res = new Response();
		try {
			SessionService sessionService = new SessionService();
			GetObjResponse objResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionEntity = (SessionEntity) objResponse.getObject();
			Date utilDate = new Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			sessionEntity.setStartDate(sqlDate);
			res = sessionService.updateSession(sessionEntity);

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Unsuccessful ");

		}
		return res;

	}

	/**
     * Add selected test file entry to the session stage mapping.
     * @param testFileId ID of the test file.
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
     * @param stageName Name of the stage.
     * @param stageId ID of the stage.
     * @param rdfFileResult Result of the RDF file processing.
     */
	private void updateStateMachineCardStatus(String stageName, String stageId, String rdfFileResult) {
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
				System.out.println("----- Mandatory ----");
				LRUTestStateObject.updateLruMandatoryCardstatus(stageId, rdfFileResult);

				switch (LRUTestStateObject.getLRUTestRunningCard()) {

				case SPIL_LINK:
					System.out.println("----- SPIL_LINK ----");
					LRUTestStateObject.getSpilLinkStatus().set(false);
					break;
				case POWER_SUPPLY:
					System.out.println("----- POWER_SUPPLY ----");
					LRUTestStateObject.getPowerSupplyStatus().set(false);
					break;
				case PBIT:
					System.out.println("----- PBIT ----");
					LRUTestStateObject.getPbitStatus().set(false);
					break;
				case AD_DA_INTERFACE:
					System.out.println("----- AD_DA_INTERFACE ----");
					LRUTestStateObject.getAd_daInterfaceStatus().set(false);
					break;
				case INITIALIZE_LRU:
					System.out.println("----- INITIALIZE_LRU ----");
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
				System.out
						.println("------Stage Id----- " + stageId + "  ------- RDF FILE Result----- " + rdfFileResult);
				break;
			case "SESSION TEST":
				// System.out.println("CASE : SESSION TEST");
				SessionTestStateObject.updateEndLeafMapStatus(stageId, "COMPLETED");
				SessionTestStateObject.getRunningTestLeafStatus().set(true);
				StateMachine.setTestState(TestState.COMPLETED);

				break;
			default:
				System.out.println("INVALID TEST TYPE ID ");
				break;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	  /**
     * Add TEST FILE RESULT entry.
     * @param testFileResultId ID for the test file result.
     * @param sessionId ID of the session.
     * @param stageId ID of the stage.
     * @param testFileId ID of the test file.
     * @param mongoUniqueIdentifier Identifier of MongoDB.
     * @param rdfPath Path to RDF files.
     * @param rdfFileName Name of the RDF file.
     * @param testStatus Result of the test.
     * @param dStarCount Count of DStar.
     * @param startTime Start time of the test.
     * @param endTime End time of the test.
     * @param selectedtestFileId ID for the selected test file entry.
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
			System.out.println("FILE LENGTH  " + file.length());

			while (true) { // Infinite loop
				System.out.println("FILE LENGTH " + file.length()); // Print the file length
				if (file.length() == 0) { // Check if the file length is 0
					try {
						System.out.println("File is empty, waiting for 1 second...");
						Thread.sleep(1000); // Wait for 1 second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else { // If the file is not empty, exit the loop
					System.out.println("File is not empty.");
					break;
				}
			}

			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

				String line;
				String brdNumber = null;
				String brdResult = null;
				res.setResponseCode(1);
				int dStartCount = 0;
				System.out.println("RDF File Name  :" + (rdfFileLocation + rdfFileName));
				while ((line = reader.readLine()) != null) {
					System.out.println("LINE  " + line);
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
					System.out.println(
							"  RACK 1 -------- dStartCount: " + dStartCount + " res.get " + res.getResponseCode());
				}

				System.out.println("  RACK 1 -------- brdResult " + brdResult);
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
				System.out.println("--------------CPCI UPDATE --------  " + filePath + "   " + rdfFileStatus);
			}

			else if (stageName.equals("MANDATORY") || stageName.equals("GO NOGO") || stageName.equals("SRU")) {

				LRUTestResult lRUTestResult = new LRUTestResult(filePath, rdfFileStatus);

				LRUTestStateObject.addTestResult(lRUTestResult);

			} else if (stageName.equals("SESSION TEST")) {

				SessionTestResult sessionTestResult = new SessionTestResult(filePath, rdfFileStatus);

				SessionTestStateObject.addSessionTestResult(sessionTestResult);

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
     * @param testFileName Name of the test file.
     * @return List of file names.
     */
	private List<String> dotComFileReader(String filePath) {
		List<String> listOfFileNames = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
//				System.out.println(line+"  -- "+(!line.isEmpty())+"   "+(line != ""));
				if ((!line.isEmpty())) {
					if (line.startsWith("@")) {
						line = line.substring(1);
						listOfFileNames.add(line);
					}

				}
			}
			for (String line1 : listOfFileNames) {
				System.out.println("Line -->  " + line1);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return listOfFileNames;
	}

//	private void example() {
//
//		try {
//			boolean flag = true;
//			while (true) {
//				// Sleep for 2 minutes (120000 milliseconds)
//				Thread.sleep(2 * 60 * 1000);
//				if (!StateMachine.isTextArea()) {
//
//					while (flag) {
//						try {
//							Thread.sleep(1000);
//							if (!StateMachine.isTextArea()) {
//								flag = false;
//							}
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		} catch (InterruptedException e) {
//			// Handle the exception if the thread is interrupted
//			System.err.println("Thread was interrupted: " + e.getMessage());
//		}
//	}
}
