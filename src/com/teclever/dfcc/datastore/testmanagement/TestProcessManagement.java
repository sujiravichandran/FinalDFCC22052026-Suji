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
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.StagesTestFilesResultDTO;
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
import com.teclever.dfcc.stateMachine.StateMachine.aitessRunning;
import com.teclever.dfcc.stateMachine.StateMachine.rdfFileParser;

public class TestProcessManagement {

	ObjectId ob;

	public Response testProcesControl(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError, String stageName, String testTypeId) {

		Response res = new Response();
		System.out.println("--------START---------");

		// => Check and Update Aitess Running.
		AitessProcessControlManagement.getInstance().check(testTypeId);

		System.out.println(" -> AETS Check Completed  ");

		if (aitessRunning.isAitess1SwitchedFailed() || aitessRunning.isAitess2SwitchedFailed()) {
			res.setResponseCode(0);
			res.setResponseMessage("   AETS Failed to launch ");
			aitessRunning.setAitess1SwitchedFailed(false);
			aitessRunning.setAitess2SwitchedFailed(false);
			return res;
		}
		aitessRunning.setAitess1SwitchedFailed(false);
		aitessRunning.setAitess2SwitchedFailed(false);

		try {
			// Update SESSION ENTITY and SESSION STAGE MAPPING
			res = updateStatusAndRunCount(sessionId, stageId, repeatCount);
			if (res.getResponseCode() == 0) {
				return res;
			}
			// ADD list of File IDs to SESSION STAGE SELECTED TEST FILES
			res = addSelectedTestFile(listOfFileId, stageId);
			if (res.getResponseCode() == 0) {
				return res;
			}
			// Getting File ID and FileName as Key Value.
			TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
			TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);
			Map<String, String> testFilesIdName = testFileResponse.getTestFilesIdName();

			// Thread Function
			Runnable runTestThread = () -> {

				System.out.println(" 1 ----  Thread Start ");
				String startTime;
				String endTime;
				String rdfFileName = null;
				String rdfFileResult = "OK";
				String testState = null;
				List<String> listOfFileIds = new ArrayList<>();

				// Service Class Object Creation For getting unique key(primary key)
				SessionStagesTestFilesResultService sessionStageTestFileResult = new SessionStagesTestFilesResultService();

				// Getting RDF file path from RUN PATH MASTER
				DownloadFileService downloadFileService = new DownloadFileService();
				String rdfFileLocation = downloadFileService
						.getRDFLocationByRunConfigId(StateMachine.currentSessionDetails.getRunConfigId(), "rdf");

				for (int i = 1; i <= repeatCount; i++) {

					listOfFileIds.addAll(listOfFileId);

				}

				String testFileName;
				List<String> listOfTestFileNames = new ArrayList<>();

				// FOR LOOP (1) : List Of File IDs
				outerLoop: for (String testFileId : listOfFileIds) {

					System.out.println("   ->  OUTER LOOP  ");

					if (testFilesIdName.get(testFileId) != null) {
						listOfTestFileNames.clear();
						testFileName = testFilesIdName.get(testFileId);
						System.out.println("   ->  Test File Name ::" + testFileName);
						if (testFileName.endsWith(".com")) {
							listOfTestFileNames.addAll(dotComFileReader(testFileName));
						} else {
							listOfTestFileNames.add(testFileName);
						}
						// FOR LOOP (2) : List Of File Names
						for (String tpfFileName : listOfTestFileNames) {
							System.out.println("   ->  INNER LOOP  ");

							// Update State Machine : Set TextArea to FALSE.
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
							StateMachine.setTextArea(false);

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

							if (testProcessRes.getResponse().getResponseCode() == 111) {
								if (rdfFileResult.equals("OK")) {
									rdfFileResult = "NOT OK";
								}
							} else {
								if (rdfFileResult.equals("OK")) {
									rdfFileResult = "OK";
								}
							}

							// Test Ended Time
							endTime = String.valueOf(new Date());

							// Calling Test File saving method.
							addTestFileResult(sessionStageTestFileResult.generateUniqueTestFilesResultIdId(), sessionId,
									stageId, testFileId, String.valueOf(ob), rdfFileLocation,
									(rdfFileName != null)
											? (rdfFileName != "USER EXIT") ? rdfFileName : "RDF NOT GENERATED"
											: "RDF NOT GENERATED",
									(testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE",
									String.valueOf(testProcessRes.getdStarCount()), startTime, endTime);

							// Update State Machine : Set TextArea to TRUE.
							StateMachine.setTextArea(true);
						} // (2)
					}

				} // (1)

				updateStateMachineCardStatus(stageName, stageId, rdfFileResult);

				String stageResult = rdfFileResult.equals("OK") ? "COMPLETED with Success"
						: rdfFileResult.equals("NOT OK") ? "COMPLETED with Failure" : null;
				
				if (testState.equals("STOPED")) {
					stageResult = "STOPED";
				}
				// Update SESSION STAGE MAPPING : Set Status to COMPLETED
				SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
				Response response = sessionStagesSelectedStagesService
						.updateSessionStagesBySessionIdAndStageId(repeatCount, sessionId, stageId, stageResult);
				System.out.println("Stage Result is Updated to DB  " + response.getResponseMessage());
				System.out.println("--------END---------");
				System.out.println();

			};

			// Thread START
			Thread selfTestThread = new Thread(runTestThread);
			selfTestThread.start();

			res.setResponseCode(1);
			res.setResponseMessage("Test Started ");

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Test Start Unsuccessfull.. ");
		}
		return res;
	}

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
//	From SESSION STAGES MAPPING : Add RepeatCount,Status,RunCount.
	private Response updateStatusAndRunCount(String sessionId, String stageId, int repeatCount) {
		Response res = new Response();
		try {
			SessionService sessionService = new SessionService();
			GetObjResponse objResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionEntity = (SessionEntity) objResponse.getObject();
			Date utilDate = new Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			sessionEntity.setStartDate(sqlDate);
			res = sessionService.updateSession(sessionEntity);
			if (res.getResponseCode() == 0) {
				return res;
			}
			SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
			res = sessionStagesSelectedStagesService.updateSessionStagesBySessionIdAndStageId(repeatCount, sessionId,
					stageId, "STARTED");
			if (res.getResponseCode() == 0) {
				return res;
			}
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Unsuccessful ");

		}
		return res;

	}

	private Response addSelectedTestFile(List<String> testFileId, String stageId) {
		Response res = new Response();
		try {
			SessionStagesSelectedTestFilesService selectedTestFile = new SessionStagesSelectedTestFilesService();
			res = selectedTestFile.addSelectedFilesToStages(testFileId, stageId);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Add Selected Test File Unsuccessful ");
		}
		return res;
	}

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

	// ADD TEST FILE RESULT
	private Response addTestFileResult(String sessionStagesTestFilesResultId, String sessionId, String stageId,
			String testFileId, String systemResultInfoId, String rdfPath, String rdfFileName, String testStatus,
			String dStarCount, String startTime, String endTime) {
		Response res = new Response();
		try {
			SessionStagesTestFilesResultService selectedTestFile = new SessionStagesTestFilesResultService();
			SessionStagesTestFilesResult testFileResult = new SessionStagesTestFilesResult();
			testFileResult.setSessionStagesTestFilesResultId(sessionStagesTestFilesResultId);
			testFileResult.setSessionId(sessionId);
			testFileResult.setStageId(stageId);
			testFileResult.setTestFileId(testFileId);
			testFileResult.setSystemResultInfoId(systemResultInfoId);
			testFileResult.setRdfPath(rdfPath);
			testFileResult.setRdfFileName(rdfFileName);
			testFileResult.setTestStatus(testStatus);
			testFileResult.setdStarCount(dStarCount);
			testFileResult.setStartTime(startTime);
			testFileResult.setEndTime(endTime);

			GetObjResponse getObjResponse = selectedTestFile.addTestFilesResult(testFileResult);
			res = getObjResponse.getResponse();
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Add Selected Test File Unsuccessful ");
		}
		return res;
	}

	// NOT USING : LIST OF TEST FILE RESULT SAVE
	public Response addTestFileResult(List<StagesTestFilesResultDTO> listOfTestFileResultData) {
		Response res = new Response();
		try {
			SessionStagesTestFilesResultService selectedTestFile = new SessionStagesTestFilesResultService();
			List<SessionStagesTestFilesResult> listOfTestFileResult = new ArrayList<>();
			for (StagesTestFilesResultDTO stagesTestFilesResultDTO : listOfTestFileResultData) {
				SessionStagesTestFilesResult testFileResult = new SessionStagesTestFilesResult();
				// testFileResult.setSessionStagesTestFilesResultId(stagesTestFilesResultDTO.getSessionStagesTestFilesResultId());
				testFileResult.setSessionId(stagesTestFilesResultDTO.getSessionId());
				testFileResult.setStageId(stagesTestFilesResultDTO.getStageId());
				testFileResult.setTestFileId(stagesTestFilesResultDTO.getTestFileId());
				testFileResult.setSystemResultInfoId(stagesTestFilesResultDTO.getSystemResultInfoId());
				testFileResult.setRdfPath(stagesTestFilesResultDTO.getRdfPath());
				testFileResult.setRdfFileName(stagesTestFilesResultDTO.getRdfFileName());
				testFileResult.setTestStatus(stagesTestFilesResultDTO.getTestStatus());
				testFileResult.setdStarCount(stagesTestFilesResultDTO.getdStarCount());
				testFileResult.setStartTime(stagesTestFilesResultDTO.getStartTime());
				testFileResult.setEndTime(stagesTestFilesResultDTO.getEndTime());

				listOfTestFileResult.add(testFileResult);
			}
			GetResponse getObjResponse = selectedTestFile.addListOfTestFilesResult(listOfTestFileResult);
			List<StagesTestFilesResultDTO> listOfTestFileResultDto = new ArrayList<>();
			for (Object obj : getObjResponse.getResponseList()) {
				SessionStagesTestFilesResult testFileResult = (SessionStagesTestFilesResult) obj;
				StagesTestFilesResultDTO stagesTestFilesResultDTO = new StagesTestFilesResultDTO(
						testFileResult.getSessionStagesTestFilesResultId(), testFileResult.getSessionId(),
						testFileResult.getStageId(), testFileResult.getTestFileId(),
						testFileResult.getSystemResultInfoId(), testFileResult.getRdfPath(),
						testFileResult.getRdfFileName(), testFileResult.getTestStatus(), testFileResult.getdStarCount(),
						testFileResult.getStartTime(), testFileResult.getEndTime());
				listOfTestFileResultDto.add(stagesTestFilesResultDTO);
			}
			// returnlistOfTestFileResultDto;
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
				ob = RdfFileDetailsParser.saveProjectDetailsToMongoDB(sessionId, filePath);
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

	public List<String> dotComFileReader(String filePath) {
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
