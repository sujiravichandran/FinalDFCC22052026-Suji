package com.teclever.dfcc.datastore.testmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestRunningCard;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestRunningCard;
import com.teclever.dfcc.stateMachine.StateMachine;

public class TestProcessManagement {

	/*
	 * public Response testProcesControl(String sessionId, String stageId, int
	 * repeatCount, List<String> listOfFileId, boolean continueWithError, String
	 * stageName) { // Method Return // TestProcessResponse testProcessResponse =
	 * new TestProcessResponse();
	 * 
	 * Response res = new Response(); // => Check Aitess Running.
	 * 
	 * try { // Update SESSION ENTITY and SESSION STAGE MAPPING res =
	 * updateStatusAndRunCount(sessionId, stageId, repeatCount); if
	 * (res.getResponseCode() == 0) { return res; } // ADD list of File IDs to
	 * SESSION STAGE SELECTED TEST FILES res = addSelectedTestFile(listOfFileId,
	 * stageId); if (res.getResponseCode() == 0) { //
	 * testProcessResponse.setResponse(res); return res; } // Getting File ID and
	 * FileName as Key Value. TestPlanFileManagement testPlanFileManagement = new
	 * TestPlanFileManagement(); TestFileResponse testFileResponse =
	 * testPlanFileManagement.getSelectedTestFilesFromStage(stageId); Map<String,
	 * String> testFilesIdName = testFileResponse.getTestFilesIdName();
	 * 
	 * // Thread Function Runnable runTestThread = () -> {
	 * 
	 * TestProcessResponse testProcessRes = new TestProcessResponse();
	 * 
	 * Response response = new Response();
	 * 
	 * // Update State Machine : Set TextArea to FALSE. if
	 * (StateMachine.isTextArea()) { StateMachine.setTextArea(false); } String
	 * startTime; String endTime; String rdfFileName = null; int dStarCount = 0;
	 * String rdfFileResult = null; SessionStagesTestFilesResultService
	 * sessionStageTestFileResult = new SessionStagesTestFilesResultService();
	 * 
	 * 
	 * // For Each Loop of List of File IDs for (String testFileId : listOfFileId) {
	 * // Test Started Time startTime = String.valueOf(new Date());
	 * 
	 * if (testFilesIdName.get(testFileId) != null) {
	 * 
	 * 
	 * SelfTestResult selfTestFile; switch (stageName) { case "RACK1":
	 * 
	 * 
	 * // Update State Machine. selfTestFile = new SelfTestResult("RDF FILE", "OK"
	 * );
	 * 
	 * SelfTestStateObject.addSelfTestResult(selfTestFile); if
	 * (SelfTestStateObject.getSelfTestRunningCard() == SelfTestRunningCard.RACK1) {
	 * SelfTestStateObject.getRack1Status().set(false); ; } break; case "CPCI": //
	 * testProcessRes = parseTestFileTest(rdfFileLocaltion + rdfFileName, stageId);
	 * // RdfFileDetailsParser.saveProjectDetailsToMongoDB(testResultId,
	 * rdfFileLocaltion + rdfFileName); System.out.println("CPCI Is Called");
	 * 
	 * selfTestFile = new SelfTestResult("RDF FILE", "OK");
	 * 
	 * SelfTestStateObject.addSelfTestResult(selfTestFile);
	 * 
	 * switch (SelfTestStateObject.getSelfTestRunningCard()) {
	 * 
	 * case B1553: SelfTestStateObject.getB1553Status().set(false); break; case
	 * RS422_1: SelfTestStateObject.getRs422_1Status().set(false); break; case
	 * RS422_2: SelfTestStateObject.getrS422_2Status().set(false); break;
	 * 
	 * default: break; } break;
	 * 
	 * case "MANDATORY":
	 * 
	 * 
	 * LRUTestResult lRUTestResult = new LRUTestResult(stageId, "RDF FILE ", "OK");
	 * LRUTestStateObject.addTestResult(lRUTestResult); switch
	 * (LRUTestStateObject.getSelfTestRunningCard()) {
	 * 
	 * case SPIL_LINK: LRUTestStateObject.getSpilLinkStatus().set(true); break; case
	 * POWER_SUPPLY: LRUTestStateObject.getPowerSupplyStatus().set(true); break;
	 * case PBIT: LRUTestStateObject.getPbitStatus().set(true); break; case
	 * AD_DA_INTERFACE: LRUTestStateObject.getAd_daInterfaceStatus().set(true);
	 * break; case INITIALIZE_LRU:
	 * LRUTestStateObject.getInitializeLRUStatus().set(true); break;
	 * 
	 * default: break; }
	 * 
	 * break; case "GO NOGO":
	 * 
	 * 
	 * LRUTestResult lRUTestResult1 = new LRUTestResult(stageId, "RDF FILE ", "OK");
	 * LRUTestStateObject.addTestResult(lRUTestResult1);
	 * 
	 * switch (LRUTestStateObject.getSelfTestRunningCard()) {
	 * 
	 * case COMPLETE_TEST: LRUTestStateObject.getCompleteTestStatus().set(false);
	 * break; case OFP_LOADING: LRUTestStateObject.getOfpLoadingStatus().set(false);
	 * break; case PI_CHECK: LRUTestStateObject.getPiCheckStatus().set(false);
	 * break; default: break; }
	 * 
	 * break; case "SRU":
	 * 
	 * 
	 * LRUTestResult lRUTestResult2 = new LRUTestResult(stageId, "RDF FILE ",
	 * (testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
	 * LRUTestStateObject.addTestResult(lRUTestResult2);
	 * 
	 * break; default: System.out.println("INVALID TEST TYPE ID "+stageName); break;
	 * }
	 * 
	 * } // Test Ended Time endTime = String.valueOf(new Date());
	 * 
	 * 
	 * 
	 * // Calling Test File saving method.
	 * addTestFileResult(sessionStageTestFileResult.
	 * generateUniqueTestFilesResultIdId(), sessionId, stageId, testFileId,
	 * "SystemResultInfoId", "NO USE", rdfFileName, "SUCCESS" ,
	 * String.valueOf(testProcessRes.getdStarCount()), startTime, endTime); } //
	 * Update State Machine : Set TextArea to TRUE. StateMachine.setTextArea(true);
	 * 
	 * // Update SESSION STAGE MAPPING : Set Status to STOPED
	 * SessionSelectedStagesService sessionStagesSelectedStagesService = new
	 * SessionSelectedStagesService(); response =
	 * sessionStagesSelectedStagesService.
	 * updateSessionStagesBySessionIdAndTestTypeId(repeatCount, sessionId, stageId,
	 * "STOPED.."); }; // Thread START Thread selfTestThread = new
	 * Thread(runTestThread); selfTestThread.start();
	 * 
	 * res.setResponseCode(1); res.setResponseMessage("Test Started ");
	 * 
	 * } catch (Exception e) { res.setResponseCode(0);
	 * res.setResponseMessage("Test Start Unsuccessfull.. "); } return res; }
	 */

	public Response testProcesControl(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError, String stageName) {
		// Method Return
//		TestProcessResponse testProcessResponse = new TestProcessResponse();

		Response res = new Response();
		// => Check Aitess Running.

		try {
			// Update SESSION ENTITY and SESSION STAGE MAPPING
			res = updateStatusAndRunCount(sessionId, stageId, repeatCount);
			if (res.getResponseCode() == 0) {
				return res;
			}
			// ADD list of File IDs to SESSION STAGE SELECTED TEST FILES
			res = addSelectedTestFile(listOfFileId, stageId);
			if (res.getResponseCode() == 0) {
//				testProcessResponse.setResponse(res);
				return res;
			}
			// Getting File ID and FileName as Key Value.
			TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
			TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);
			Map<String, String> testFilesIdName = testFileResponse.getTestFilesIdName();

			// Thread Function
			Runnable runTestThread = () -> {

				TestProcessResponse testProcessRes = new TestProcessResponse();

				Response response = new Response();

				// Update State Machine : Set TextArea to FALSE.
				if (StateMachine.isTextArea()) {
					StateMachine.setTextArea(false);
				}
				String startTime;
				String endTime;
				String rdfFileName = null;
				int dStarCount = 0;
				String rdfFileResult = null;
				SessionStagesTestFilesResultService sessionStageTestFileResult = new SessionStagesTestFilesResultService();

				// Getting RDF file path from RUN PATH MASTER
				DownloadFileService downloadFileService = new DownloadFileService();
				String rdfFileLocaltion = downloadFileService
						.getRDFLocationByRunConfigId(StateMachine.currentSessionDetails.getRunConfigId(), "rdf");

//				String testResultId=sessionStageTestFileResult.generateUniqueTestFilesResultIdId();

				// For Each Loop of List of File IDs
				for (String testFileId : listOfFileId) {
					// Test Started Time
					startTime = String.valueOf(new Date());

					if (testFilesIdName.get(testFileId) != null) {

						// Getting RDF file Name from PerformTest()
						rdfFileName = AitessProcessControlManagement.getInstance()
								.performTest(testFilesIdName.get(testFileId));
						SelfTestResult selfTestFile;
						// Get TestTypeId From StateMachine;
						switch (stageName) {
						case "RACK1":

							// Parse SelfTest RDF file.
							testProcessRes = selfTestFileTest(rdfFileLocaltion + rdfFileName);

							// Update State Machine.
							selfTestFile = new SelfTestResult(rdfFileLocaltion + rdfFileName,
									(testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
//							dStarCount = testProcessRes.getdStarCount();
//							rdfFileResult = (testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS"
//									: "FAILURE";
							SelfTestStateObject.addSelfTestResult(selfTestFile);
							if (SelfTestStateObject.getSelfTestRunningCard() == SelfTestRunningCard.RACK1) {
								SelfTestStateObject.getRack1Status().set(false);
								;
							}
							break;
						case "CPCI":
							testProcessRes = parseTestFileTest(rdfFileLocaltion + rdfFileName, stageId);
//							RdfFileDetailsParser.saveProjectDetailsToMongoDB(testResultId, rdfFileLocaltion + rdfFileName);
							System.out.println("CASE : CPCI ");

							selfTestFile = new SelfTestResult(rdfFileLocaltion + rdfFileName,
									(testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
//							dStarCount = testProcessRes.getdStarCount();
//							rdfFileResult = (testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS"
//									: "FAILURE";
							SelfTestStateObject.addSelfTestResult(selfTestFile);

//							if (SelfTestStateObject.getSelfTestRunningCard() == SelfTestRunningCard.B1553) {
//								SelfTestStateObject.getB1553Status().set(false);
//								;
//							}
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
							System.out.println("CASE : MANDATORY ");
							testProcessRes = parseTestFileTest(rdfFileLocaltion + rdfFileName, stageId);

							LRUTestResult lRUTestResult = new LRUTestResult(stageId, rdfFileLocaltion + rdfFileName,
									(testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
							LRUTestStateObject.addTestResult(lRUTestResult);

							switch (LRUTestStateObject.getSelfTestRunningCard()) {

							case SPIL_LINK:
								System.out.println("CASE : SPLIL LINK");
								LRUTestStateObject.getSpilLinkStatus().set(false);
								break;
							case POWER_SUPPLY:
								System.out.println("CASE : POWER_SUPPLY ");
								LRUTestStateObject.getPowerSupplyStatus().set(false);
								break;
							case PBIT:
								System.out.println("CASE :PBIT ");
								LRUTestStateObject.getPbitStatus().set(false);
								break;
							case AD_DA_INTERFACE:
								System.out.println("CASE : AD_DA_INTERFACE ");
								LRUTestStateObject.getAd_daInterfaceStatus().set(false);
								break;
							case INITIALIZE_LRU:
								System.out.println("CASE : INITIALIZE_LRU ");
								LRUTestStateObject.getInitializeLRUStatus().set(false);
								break;
//							case DIGITAL:
////								LRUTestStateObject.get().set(false);
//								break;
//							case ANALOG1_LEFT:
////								LRUTestStateObject.get().set(false);
//								break;
//							case ANALOG1_RIGHT:
//								LRUTestStateObject.get().set(false);
//								break;
//							case ANALOG2:
////								LRUTestStateObject.getSpilLinkStatus().set(false);
//								break;
//							case ALL_SRUs:
////								LRUTestStateObject.getSpilLinkStatus().set(false);
//								break;

							default:
								break;
							}

							break;
						case "GO NOGO":
							System.out.println("CASE : GO NOGO ");
							testProcessRes = parseTestFileTest(rdfFileLocaltion + rdfFileName, stageId);

							LRUTestResult lRUTestResult1 = new LRUTestResult(stageId, rdfFileLocaltion + rdfFileName,
									(testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
							LRUTestStateObject.addTestResult(lRUTestResult1);

							switch (LRUTestStateObject.getSelfTestRunningCard()) {

							case COMPLETE_TEST:
								System.out.println("CASE : COMPLETE_TEST ");
								LRUTestStateObject.getCompleteTestStatus().set(false);
								break;
							case OFP_LOADING:
								System.out.println("CASE : OFP_LOADING ");
								LRUTestStateObject.getOfpLoadingStatus().set(false);
								break;
							case PI_CHECK:
								System.out.println("CASE : PI_CHECK ");
								LRUTestStateObject.getPiCheckStatus().set(false);
								break;
							default:
								break;
							}

							break;
						case "SRU":

							testProcessRes = parseTestFileTest(rdfFileLocaltion + rdfFileName, stageId);

							LRUTestResult lRUTestResult2 = new LRUTestResult(stageId, rdfFileLocaltion + rdfFileName,
									(testProcessRes.getResponse().getResponseCode() != 111) ? "OK" : "NOT OK");
							LRUTestStateObject.addTestResult(lRUTestResult2);

							break;
						default:
							System.out.println("INVALID TEST TYPE ID ");
							break;
						}

					}
					// Test Ended Time
					endTime = String.valueOf(new Date());

//					String testResultId=sessionStageTestFileResult.generateUniqueTestFilesResultIdId();
//					// Update MONGO DB.
//					RdfFileDetailsParser.saveProjectDetailsToMongoDB(testResultId, rdfFileLocaltion + rdfFileName);

					// ADD Test File Result to MYSQL DB : " SystemResultInfoId " is MONGO DB Id.
//					StagesTestFilesResultDTO stagesTestFilesResultDTO = new StagesTestFilesResultDTO(
//							sessionStageTestFileResult.generateUniqueTestFilesResultIdId(), sessionId, stageId,
//							testFileId, "SystemResultInfoId", rdfFileLocaltion, rdfFileName,
//							rdfFileResult,
//							String.valueOf(dStarCount), startTime, endTime);

					// Calling Test File saving method.
					addTestFileResult(sessionStageTestFileResult.generateUniqueTestFilesResultIdId(), sessionId,
							stageId, testFileId, "SystemResultInfoId", rdfFileLocaltion, rdfFileName,
							(testProcessRes.getResponse().getResponseCode() != 111) ? "SUCCESS" : "FAILURE",
							String.valueOf(testProcessRes.getdStarCount()), startTime, endTime);
				}
				// Update State Machine : Set TextArea to TRUE.
				StateMachine.setTextArea(true);

				// Update SESSION STAGE MAPPING : Set Status to STOPED
				SessionSelectedStagesService sessionStagesSelectedStagesService = new SessionSelectedStagesService();
				response = sessionStagesSelectedStagesService.updateSessionStagesBySessionIdAndTestTypeId(repeatCount,
						sessionId, stageId, "STOPED..");
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
			res = sessionStagesSelectedStagesService.updateSessionStagesBySessionIdAndTestTypeId(repeatCount, sessionId,
					stageId, "STARTED..");
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

	// ADD TEST FILE RESULT
	public Response addTestFileResult(String sessionStagesTestFilesResultId, String sessionId, String stageId,
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
//			SessionStagesTestFilesResult testFileResultresponse = (SessionStagesTestFilesResult) getObjResponse
//					.getObject();
//			StagesTestFilesResultDTO stagesTestFile = new StagesTestFilesResultDTO(
//					testFileResultresponse.getSessionStagesTestFilesResultId(), testFileResultresponse.getSessionId(),
//					testFileResultresponse.getStageId(), testFileResultresponse.getTestFileId(),
//					testFileResultresponse.getSystemResultInfoId(), testFileResultresponse.getRdfPath(),
//					testFileResultresponse.getRdfFileName(), testFileResultresponse.getTestStatus(),
//					testFileResultresponse.getdStarCount(), testFileResultresponse.getStartTime(),
//					testFileResultresponse.getEndTime());
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
//				testFileResult.setSessionStagesTestFilesResultId(stagesTestFilesResultDTO.getSessionStagesTestFilesResultId());
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
	public TestProcessResponse selfTestFileTest(String filePath) {
		TestProcessResponse testProcessResponse = new TestProcessResponse();
		Response res = new Response();
		Map<String, String> brdresult = new HashMap<>();
		try {
//			String baseFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			System.out.println("Reading file:-  " + filePath);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			String brdNumber = null;
			System.out.println("selfTestFileTest");
			res.setResponseCode(1);
			int dStartCount = 0;
			while ((line = reader.readLine()) != null) {
				System.out.println("LINE  " + line);
				if (line.startsWith("S> brd")) {
//					System.out.println(line.substring(line.indexOf(" "), line.indexOf("_")));
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
						}
					}
				}
			}
			int i = 1;
			for (Map.Entry<String, String> entry : brdresult.entrySet()) {
//				System.out.println(" " + (i++) + entry.getKey() + "  " + entry.getValue());
				SelfTestStateObject.updateSelfTestRack1Cardstatus(entry.getKey(), entry.getValue());
			}
			res.setResponseMessage("Successful ");
			testProcessResponse.setdStarCount(dStartCount);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Unsuccessful ");
//			throw e;
		}
		testProcessResponse.setResponse(res);
		testProcessResponse.setTestProcessResult(brdresult);
		return testProcessResponse;
	}

	public TestProcessResponse parseTestFileTest(String filePath, String stageId) {
		TestProcessResponse testProcessResponse = new TestProcessResponse();
		Response res = new Response();
//		Map<String, String> brdresult = new HashMap<>();
		int dStartCount = 0;
		try {
			System.out.println("CPCI file:-  " + filePath);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			String brdNumber = null;

			res.setResponseCode(1);

			while ((line = reader.readLine()) != null) {
				System.out.println("LINE  " + line);

				if (line.startsWith("D*> ")) {
					dStartCount++;
					res.setResponseCode(111);
				}
			}
			int i = 1;
//			for (Map.Entry<String, String> entry : brdresult.entrySet()) {
//				System.out.println(" " + (i++) + entry.getKey() + "  " + entry.getValue());
			SelfTestStateObject.updateSelfTestcPCICardstatus(stageId, (dStartCount > 0) ? "NOT OK" : "OK");
//			}
			res.setResponseMessage("Successful ");
			testProcessResponse.setdStarCount(dStartCount);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Unsuccessful ");
//			throw e;
		}
		testProcessResponse.setResponse(res);

//		testProcessResponse.setRdfResult((dStartCount > 0) ? "OK" : "NOT OK");
		return testProcessResponse;
	}
}
