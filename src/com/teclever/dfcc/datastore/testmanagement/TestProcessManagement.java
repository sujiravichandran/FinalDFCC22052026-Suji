package com.teclever.dfcc.datastore.testmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.StagesTestFilesResultDTO;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestCardData;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;

public class TestProcessManagement {

	public Response testProcesControl(String sessionId, String stageId, int repeatCount, List<String> listOfFileId,
			boolean continueWithError) {
		Response res = new Response();
		try {
			res = updateStatusAndRunCount(sessionId, stageId, repeatCount);
			if (res.getResponseCode() == 0) {
				return res;
			}
			res = addSelectedTestFile(listOfFileId, stageId);
			if (res.getResponseCode() == 0) {
				return res;
			}
			TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
			TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(stageId);

			Map<String, String> testFilesIdName = testFileResponse.getTestFilesIdName();
			int i = 1;
			for (String fileId : listOfFileId) {
				i++;
				if (testFilesIdName.get(fileId) != null) {
					Thread.sleep(1000);
					SelfTestResult selfTestFile = new SelfTestResult("Card 1", testFilesIdName.get(fileId),
							(i % 2 == 0) ? "OK" : "NOT OK");
					SelfTestStateObject.addSelfTestResult(selfTestFile);
				}
			}
			res.setResponseCode(1);
			res.setResponseMessage("Task Successful ");
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Task Unsuccessful ");
		}
		return res;
	}

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
					stageId);
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
	public Response addTestFileResult(StagesTestFilesResultDTO stagesTestFilesResultDTO) {
		Response res = new Response();
		try {
			SessionStagesTestFilesResultService selectedTestFile = new SessionStagesTestFilesResultService();
			SessionStagesTestFilesResult testFileResult = new SessionStagesTestFilesResult();
			testFileResult
					.setSessionStagesTestFilesResultId(stagesTestFilesResultDTO.getSessionStagesTestFilesResultId());
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
				testFileResult.setSessionStagesTestFilesResultId(
						stagesTestFilesResultDTO.getSessionStagesTestFilesResultId());
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

}
