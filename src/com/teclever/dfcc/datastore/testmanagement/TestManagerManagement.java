package com.teclever.dfcc.datastore.testmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.StagesTestFilesResultDTO;

public class TestManagerManagement {
		
	public void startTest(String sessionId, String stageId, String testTypeId,String listOfTestFiles,int repeatCount, boolean stopOnError)
	{
		//get uutId from STATE MACHINE
		String uutId = "UUT1";
		
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		//getCurrentRunConfigId Details FROM STATE MACHINE
		String smRunConfigId = "RUN001";
		//get currentRunConfigId Details from DB in Object
		AitessConfigurationDetails currentAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);
		
				
		//get RunConfigId based on uutId and testTypeId
		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
		//get runConfigId Details from DB in object
		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);
		
		System.out.println();
		
			
		if(currentAitess.getDriverName().equals(aitess.getDriverName()))
		{
			System.out.println("<< Drivers Matched >> "+ "currentAitess: "+currentAitess.getDriverName()+ " ::: " +"aitess: " + aitess.getDriverName());
		}else 
		{
			System.out.println("<< Drivers NOT Matched >> "+ "currentAitess: "+currentAitess.getDriverName()+ " ::: " +"aitess: " + aitess.getDriverName());
		}
	
		
		if(currentAitess.getAitessName().equals(aitess.getAitessName()))
		{
			System.out.println("<< Aitess Matched >> "+ "currentAitess: "+currentAitess.getAitessName()+ " ::: " +"aitess: " + aitess.getAitessName());
			
		}else
		{
			System.out.println("<< Aitess NOT Matched >> "+ "currentAitess: "+currentAitess.getAitessName()+ " ::: " +"aitess: " + aitess.getAitessName());

		}
		
		
		if(currentAitess.getConfigFile().equals(aitess.getConfigFile()))
		{
			System.out.println("<< ConfigFile Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());

		}else 
		{
			System.out.println("<< ConfigFile NOT Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());

		}
		
		
		
	}
	
	
	public Response updateStatusAndRunCount(String sessionId, String testTypeId, int repeatCount) {
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
					testTypeId);
			if (res.getResponseCode() == 0) {
				return res;
			}
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Unsuccessful ");

		}
		return res;

	}

	public Response addSelectedTestFile(List<String> testFileId, String stageId) {
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
			GetResponse getObjResponse = selectedTestFile.addTestFilesResult(listOfTestFileResult);
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
//			return listOfTestFileResultDto;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Add Selected Test File Unsuccessful ");
		}
		return res;
	}

}
