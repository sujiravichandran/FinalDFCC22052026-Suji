package com.teclever.dfcc.resultmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.entities.LevelFiveStageMaster;
import com.teclever.datastore.entities.LevelFourStageMaster;
import com.teclever.datastore.entities.LevelOneStageMaster;
import com.teclever.datastore.entities.LevelThreeStageMaster;
import com.teclever.datastore.entities.LevelTwoStageMaster;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.resultstore.dto.ResultDto;
import com.teclever.dfcc.resultstore.resultmanagement.ResultManagement;

public class ResultExecutionManagement {

	// For Getting the List Of ExecutionFiles...
	public ResultExecutionResponse geResulttExecutionListBriefListForStages(String sessionId,String stageId) {
		ResultExecutionResponse response = new ResultExecutionResponse();
		try {
			// Session Details
			SessionService sessionService = new SessionService();
			GetObjResponse sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);

			if (sessionRes.getResponse().getResponseCode() != 1) {
				response.setMsg("Problem On Fetching SessionDetails  " + "Exception Msg:"
						+ sessionRes.getResponse().getResponseMessage());
			}
			SessionEntity sessionEntity = new SessionEntity();
			sessionEntity = (SessionEntity) sessionRes.getObject();
			String sessionName = sessionEntity.getSessionName();

			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
			List<ResultExecutionDTO> resultList = new ArrayList();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : lst) {
				ResultExecutionDTO resultExecutionDTO = new ResultExecutionDTO();
				resultExecutionDTO.setDStarCount(sessionStagesTestFilesResult.getdStarCount());
				resultExecutionDTO.setEndTime(sessionStagesTestFilesResult.getEndTime());
				resultExecutionDTO.setRdfFile(sessionStagesTestFilesResult.getRdfFileName());
				resultExecutionDTO.setRdfFilePath(sessionStagesTestFilesResult.getRdfPath());
				resultExecutionDTO.setSystemInfoId(sessionStagesTestFilesResult.getSystemResultInfoId());
				if (sessionStagesTestFilesResult.getStageId() != null
						|| !sessionStagesTestFilesResult.getStageId().equals("")) {
					resultExecutionDTO.setStageId(sessionStagesTestFilesResult.getStageId());
					resultExecutionDTO.setStageName(stageIdName.get(sessionStagesTestFilesResult.getStageId()));

				}
				resultExecutionDTO.setTestFileId(sessionStagesTestFilesResult.getTestFileId());
				resultExecutionDTO.setTestFileName(testFileIdName.get(sessionStagesTestFilesResult.getTestFileId()));
				resultList.add(resultExecutionDTO);
				
			}
			//Filtering For the StageId
			resultList = resultList.stream()
					.filter(stage -> stage.getStageId().equals(stageId)).collect(Collectors.toList());

			response.setResultDTOList(resultList);

		} catch (Exception ex) {
			response.setCode(0);
			response.seteMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
		}
		return response;
	}
	
	// For Getting the List Of ExecutionFiles...
	public ResultExecutionResponse geResulttExecutionListBriefListForSession(String sessionId) {
		ResultExecutionResponse response = new ResultExecutionResponse();
		try {
			// Session Details
			SessionService sessionService = new SessionService();
			GetObjResponse sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);

			if (sessionRes.getResponse().getResponseCode() != 1) {
				response.setMsg("Problem On Fetching SessionDetails  " + "Exception Msg:"
						+ sessionRes.getResponse().getResponseMessage());
			}
			SessionEntity sessionEntity = new SessionEntity();
			sessionEntity = (SessionEntity) sessionRes.getObject();
			String sessionName = sessionEntity.getSessionName();

			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
			List<ResultExecutionDTO> resultList = new ArrayList();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : lst) {
				ResultExecutionDTO resultExecutionDTO = new ResultExecutionDTO();
				resultExecutionDTO.setDStarCount(sessionStagesTestFilesResult.getdStarCount());
				resultExecutionDTO.setEndTime(sessionStagesTestFilesResult.getEndTime());
				resultExecutionDTO.setRdfFile(sessionStagesTestFilesResult.getRdfFileName());
				resultExecutionDTO.setRdfFilePath(sessionStagesTestFilesResult.getRdfPath());
				resultExecutionDTO.setSystemInfoId(sessionStagesTestFilesResult.getSystemResultInfoId());
				if (sessionStagesTestFilesResult.getStageId() != null
						|| !sessionStagesTestFilesResult.getStageId().equals("")) {
					resultExecutionDTO.setStageId(sessionStagesTestFilesResult.getStageId());
					resultExecutionDTO.setStageName(stageIdName.get(sessionStagesTestFilesResult.getStageId()));

				}
				resultExecutionDTO.setTestFileId(sessionStagesTestFilesResult.getTestFileId());
				resultExecutionDTO.setTestFileName(testFileIdName.get(sessionStagesTestFilesResult.getTestFileId()));

			}

		} catch (Exception ex) {
			response.setCode(0);
			response.seteMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
		}
		return response;
	}


	public ResultDetailedResponse geResulttExecutionListDetailedListForStages(String sessionId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {
			
			
			
			
		} catch (Exception ex) {

		}
		return response;
	}
	
	public ResultDetailedResponse geResulttExecutionListDetailedListForSession(String sessionId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {
		List<ResultDto>lst =	getResultForSessionTpfExecution(sessionId);
		
		} catch (Exception ex) {

		}
		return response;
	}

	
	
	public Map<String, String> getStageIdName() {

		Map<String, String> stageIdName = new HashMap<String, String>();
		try {
			// Map<String, String> stageIdName = new HashMap<String, String>();

			// Level One Stage Master Fetching
			LevelOneMasterService lvlOneService = new LevelOneMasterService();
			GetResponse levOneResponse = lvlOneService.getLevelOneMaster();
			List<LevelOneStageMaster> level1MasterList = new ArrayList<LevelOneStageMaster>();
			level1MasterList = (List<LevelOneStageMaster>) levOneResponse.getResponseList();
			for (LevelOneStageMaster levelOneStageMaster : level1MasterList) {
				stageIdName.put(levelOneStageMaster.getLevelOneStageId(), levelOneStageMaster.getStageName());
			}

			// Level Two Stage Master Fetching
			LevelTwoMasterService lvlTwoService = new LevelTwoMasterService();
			GetResponse levTwoeResponse = lvlTwoService.getLevelTwoMaster();
			List<LevelTwoStageMaster> level2MasterList = new ArrayList<LevelTwoStageMaster>();
			level2MasterList = (List<LevelTwoStageMaster>) levTwoeResponse.getResponseList();
			for (LevelTwoStageMaster levelTwoStageMaster : level2MasterList) {
				stageIdName.put(levelTwoStageMaster.getLevelTwoStageId(), levelTwoStageMaster.getStageName());
			}

			// Level Three Stage Master Fetching
			LevelThreeService lvlThreeService = new LevelThreeService();
			GetResponse levTThreeResponse = lvlThreeService.getLevelThreeMaster();
			List<LevelThreeStageMaster> level3MasterList = new ArrayList<LevelThreeStageMaster>();
			level3MasterList = (List<LevelThreeStageMaster>) levTThreeResponse.getResponseList();
			for (LevelThreeStageMaster levelThreeStageMaster : level3MasterList) {
				stageIdName.put(levelThreeStageMaster.getLevelThreeStageId(), levelThreeStageMaster.getStageName());
			}

			// Level Four Stage Master Fetching
			LevelFourMasterSevice lvlFourService = new LevelFourMasterSevice();
			GetResponse levFourResponse = lvlFourService.getLevelFourMaster();
			List<LevelFourStageMaster> level4MasterList = new ArrayList<LevelFourStageMaster>();
			level4MasterList = (List<LevelFourStageMaster>) levFourResponse.getResponseList();
			for (LevelFourStageMaster levelFourStageMaster : level4MasterList) {
				stageIdName.put(levelFourStageMaster.getLevelFourStageId(), levelFourStageMaster.getStageName());
			}

			// TestFiles Name

			// Level Five Stage Master Fetching
			LevelFiveMasterService lvlFiveService = new LevelFiveMasterService();
			GetResponse levFiveResponse = lvlFiveService.getLevelFiveMaster();
			List<LevelFiveStageMaster> level5MasterList = new ArrayList<LevelFiveStageMaster>();
			level5MasterList = (List<LevelFiveStageMaster>) levFiveResponse.getResponseList();
			for (LevelFiveStageMaster levelFiveStageMaster : level5MasterList) {
				stageIdName.put(levelFiveStageMaster.getLevelFiveStageId(), levelFiveStageMaster.getStageName());
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		return stageIdName;
	}

	public Map<String, String> getTestFileIdName() {
		Map<String, String> testFileIdName = new HashMap<String, String>();
		try {

			TestFileService testFileService = new TestFileService();
			List<TestFile> testFilesResponses = testFileService.getAllTestFiles();
			for (TestFile testFile : testFilesResponses) {
				testFileIdName.put(testFile.getTestFileId(), testFile.getTestFileName());
			}
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		return testFileIdName;
	}

	// To Get Details From Mongo DB For Current Execution
	public List<ResultDto> getResultForSingleTpfExecution(String sessionId, String systemInfoId) {
		List<ResultDto> lstResults = new ArrayList<ResultDto>();
		ObjectId objectId = new ObjectId(systemInfoId);
		lstResults = ResultManagement.getResult(sessionId, objectId);
		return lstResults;
	}

	// To Get Details From Mongo DB For Selected Session
	public List<ResultDto> getResultForSessionTpfExecution(String sessionId) {
		List<ResultDto> lstResults = new ArrayList<ResultDto>();
		// Get All systemInfoId From StagesTestFileResultsReport For Session
		List<String> systemInfoIdList = new ArrayList<String>();
		SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
		GetResponse res = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
		List<SessionStagesTestFilesResult> sessionStagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
		sessionStagesTestFilesResultLst = (List<SessionStagesTestFilesResult>) res.getResponseList();
		for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultLst) {
			systemInfoIdList.add(sessionStagesTestFilesResult.getSystemResultInfoId());
		}
		for (String systemInfoId : systemInfoIdList) {
			ObjectId objectId = new ObjectId(systemInfoId);
			List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
			lstInterResults = ResultManagement.getResult(sessionId, objectId);
			lstResults.addAll(lstInterResults);
		}
		return lstResults;
	}

	// To Get Details From Mongo DB For Selected Stage
	public List<ResultDto> getResultForStageTpfExecution(String sessionId, String stageId) {
		List<ResultDto> lstResults = new ArrayList<ResultDto>();

		SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
		GetResponse res = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
		List<SessionStagesTestFilesResult> stagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
		stagesTestFilesResultLst = (List<SessionStagesTestFilesResult>) res.getResponseList();

		stagesTestFilesResultLst = stagesTestFilesResultLst.stream()
				.filter(stage -> stage.getStageId().equals(stageId)).collect(Collectors.toList());

		List<String> systemInfoIdList = new ArrayList<String>();
		for (SessionStagesTestFilesResult SessionStagesTestFilesResult : stagesTestFilesResultLst) {
			systemInfoIdList.add(SessionStagesTestFilesResult.getSystemResultInfoId());
		}
		for (String systemInfoId : systemInfoIdList) {
			ObjectId objectId = new ObjectId(systemInfoId);
			List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
			lstInterResults = ResultManagement.getResult(sessionId, objectId);
			lstResults.addAll(lstInterResults);
		}
		return lstResults;
	}

	// To Get Details From Mongo DB For UUT Type
	public List<ResultDto> getResultForUutTypeTpfExecution(String sessionId, String stageId, String systemInfoId) {
		List<ResultDto> lstResults = new ArrayList<ResultDto>();
		return lstResults;
	}

}