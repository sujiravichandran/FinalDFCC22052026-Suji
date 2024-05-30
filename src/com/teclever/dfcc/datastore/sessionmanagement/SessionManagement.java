package com.teclever.dfcc.datastore.sessionmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.LevelOneResponseDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.entities.LoginSessionDetails;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.service.FaultCodeSessionMappingService;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.LoginSessionService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageObject;

public class SessionManagement {
	//	SESSION ENTITY : SAVE SESSION 
	public Response saveSession(SessionDTO sessionDTO) {
		Response res = new Response();
		try {
			SessionService sessionService = new SessionService();
			SessionDto sessionDto = new SessionDto();
			sessionDto.setCreationDate(sessionDTO.getCreationDate());
			sessionDto.setDfccPartNo(sessionDTO.getDfccPartNo());
			sessionDto.setDfccSNo(sessionDTO.getDfccSNo());
			sessionDto.setDfccType(sessionDTO.getDfccType());
			sessionDto.setSessionName(sessionDTO.getSessionName());
			sessionDto.setSessionTypeMasterId(sessionDTO.getSessionTypeMasterId());
			sessionDto.setUserId(sessionDTO.getUserId());
			sessionDto.setUutId(sessionDTO.getUutId());
			sessionDto.setStartDate(sessionDTO.getStartDate());
			sessionDto.setEndDate(sessionDTO.getEndDate());
			sessionDto.setStartRemarks(sessionDTO.getStartRemarks());
//			sessionDto.setEndRemarks(sessionDTO.getEndRemarks());
			GetObjResponse resObj = sessionService.addSession(sessionDto);
			if (resObj.getResponse().getResponseCode() == 0) {

				return resObj.getResponse();
			}
			SessionDto sessionResponseDto = (SessionDto) resObj.getObject();
			String sessionId = sessionResponseDto.getSessionId();
			List<SessionToStagesMappingDTO> sessionStages = new ArrayList<SessionToStagesMappingDTO>();
			sessionStages = sessionDTO.getSessionStagesList();
			List<SessionStagesMapping> sessionToStagesMappingList = new ArrayList<SessionStagesMapping>();
			for (SessionToStagesMappingDTO sessionToStagesMappingDTO : sessionStages) {
				SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
				sessionStagesMapping.setRepeatCount(1);
				sessionStagesMapping.setRunCount(0);
				sessionStagesMapping.setSessionId(sessionId);
				sessionStagesMapping.setStagelLevelId(sessionToStagesMappingDTO.getStagelLevelId());
				sessionStagesMapping.setStatus("pending");
				sessionStagesMapping.setRunDate(null);
				sessionToStagesMappingList.add(sessionStagesMapping);
			}
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			sessionSelectedStagesService.addStagesToSession(sessionToStagesMappingList);

			List<String> faultCodeList = sessionDTO.getFaultCodeMappingList();
			if (faultCodeList.size() > 0) {
				FaultCodeSessionMappingService faultCodeSessionMappingService = new FaultCodeSessionMappingService();
				for (String faultCodeSessionId : faultCodeList) {
					faultCodeSessionMappingService.addFaultCodeSession(faultCodeSessionId, sessionId);
				}
			}
			LoginSessionService loginSessionService = new LoginSessionService();
			LoginSessionDetails loginSessionDetails = new LoginSessionDetails();
			loginSessionDetails.setUserId(sessionDTO.getUserId());
			loginSessionDetails.setSessionId(sessionId);

			loginSessionDetails.setLoginTime(new Date());
			GetObjResponse loginSessionResponse = loginSessionService.addLoginSession(loginSessionDetails);
			if (loginSessionResponse.getObject() != null) {

//				LoginSessionDetails loginSessionEntity = (LoginSessionDetails) loginSessionResponse.getObject();
			}

			res.setResponseCode(1);
			res.setResponseMessage("Session Created Successfully..!");

		} catch (Exception ex) {
			res.setResponseCode(0);
			res.setResponseMessage("Session Not Created");
			ex.printStackTrace();

		}
		return res;
	}

	//	AT FIRST TIME SESSION CREATION 
	public StageMasterLevelOneResponse getLevelOneStageMasterBySessionId(String sessionId) {
		StageMasterLevelOneResponse stageMasterLevelOne = new StageMasterLevelOneResponse();
		try {
			LevelOneMasterService levelOne = new LevelOneMasterService();
			StageLevelResponse serviceResponse = levelOne.getLevelTOneMasterBySessionId(sessionId);

			if (serviceResponse.getResponse().getResponseCode() == 0) {
				stageMasterLevelOne.setResponse(serviceResponse.getResponse());
				stageMasterLevelOne.setLevelOneResponse(null);
				return stageMasterLevelOne;
			}
			List<?> listOfLevelOneStage = serviceResponse.getStageLevelList();
			List<LevelOneDto> listOfLevelOnDto = new ArrayList<>();
			for (Object levelOneStageMaster : listOfLevelOneStage) {
				LevelOneResponseDto levelOneEntity = (LevelOneResponseDto) levelOneStageMaster;

				LevelOneDto levelOneDto = new LevelOneDto();
				levelOneDto.setLevelOneId(levelOneEntity.getLevelOneId());
				levelOneDto.setStageName(levelOneEntity.getStageName());
				levelOneDto.setSessionIds(levelOneEntity.getSessionIds());
				levelOneDto.setUutId(levelOneEntity.getUutId());
				levelOneDto.setNextLevel(levelOneEntity.getNextLevel());
				levelOneDto.setDefaultStatus(levelOneEntity.isDefaultStatus());
				listOfLevelOnDto.add(levelOneDto);

			}
			stageMasterLevelOne.setLevelOneResponse(listOfLevelOnDto);
			stageMasterLevelOne.setResponse(serviceResponse.getResponse());
			return stageMasterLevelOne;
		} catch (Exception e) {
			throw e;
		}
	}

	// SESSION STAGE MAPPING : RETURNING A LEVEL IDs WITH THERE STAGE NAMEs 
	public SessionStageMapResponse getAllSessionStageMapping(String sessionEntityId) {
		SessionStageMapResponse sessionStageMapResponse = new SessionStageMapResponse();
		Response res = new Response();
		try {
			SessionSelectedStagesService sessionSelectedStages = new SessionSelectedStagesService();
			GetResponse getResponse = sessionSelectedStages.getAllSessionStagesBySessionStageId(sessionEntityId);
			if (getResponse.getCode() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				sessionStageMapResponse.setResponse(res);
				return sessionStageMapResponse;
			}
			LevelOneMasterService levelOneService = new LevelOneMasterService();
			Map<String, String> levelOneStage = levelOneService.getAllLevelOneIdAndLevelName();

			LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
			Map<String, String> levelTwoStage = levelTwoService.getAllLevelIdAndLevelName();

			LevelThreeService levelThreeService = new LevelThreeService();
			Map<String, String> levelThreeStage = levelThreeService.getAllLevelIdAndLevelName();

			LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
			Map<String, String> levelFourStage = levelFourService.getAllLevelIdAndLevelName();

			LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
			Map<String, String> levelFiveStage = levelFiveService.getAllLevelIdAndLevelName();

			List<StageObject> listOfStageObject = new ArrayList<>();
			for (Object object : getResponse.getResponseList()) {
				SessionStagesMapping sessionStage = (SessionStagesMapping) object;
				StageObject stageObject = new StageObject();
				stageObject.setL1StageId(sessionStage.getLevelOneStageId());
				stageObject.setL1StageName(levelOneStage.get(sessionStage.getLevelOneStageId()));

				stageObject.setL2StageId(sessionStage.getLevelTwoStageId());
				stageObject.setL2StageName(levelTwoStage.get(sessionStage.getLevelTwoStageId()));

				stageObject.setL3StageId(sessionStage.getLevelThreeStageId());
				stageObject.setL3StageName(levelThreeStage.get(sessionStage.getLevelThreeStageId()));

				stageObject.setL4StageId(sessionStage.getLevelFourStageId());
				stageObject.setL4StageName(levelFourStage.get(sessionStage.getLevelFiveStageId()));

				stageObject.setL5StageId(sessionStage.getLevelFiveStageId());
				stageObject.setL5StageName(levelFiveStage.get(sessionStage.getLevelFiveStageId()));

				stageObject.setTestTypeId(sessionStage.getTestTypeId());

				listOfStageObject.add(stageObject);
			}
			sessionStageMapResponse.setListOfStageObject(listOfStageObject);
			res.setResponseCode(1);
			res.setResponseMessage("Fetch Data Successfull ");
			sessionStageMapResponse.setResponse(res);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull ");
			sessionStageMapResponse.setResponse(res);
		}
		return sessionStageMapResponse;
	}

	//	BASED ON USER ID GET LIST OF SESSION, WHICH DONT HAVE END TIME
	public SessionListResponse getAllSessionData(String userId) {
		SessionListResponse sessionListResponse = new SessionListResponse();
		Response res = new Response();
		try {
			SessionSelectedStagesService sessionSelectedStage = new SessionSelectedStagesService();
			GetResponse getResponse = sessionSelectedStage.getAllSessionData();
			if (getResponse.getCode() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				sessionListResponse.setResponse(res);
				return sessionListResponse;
			}
			List<SessionList> listOfSession = new ArrayList<>();
			for (Object object : getResponse.getResponseList()) {
				SessionEntity sessionEntity = (SessionEntity) object;
				SessionList sessionList = new SessionList();
				sessionList.setSessionId(sessionEntity.getSessionId());
				sessionList.setSessionName(sessionEntity.getSessionName());
				sessionList.setCreationDate(sessionEntity.getCreationDate());
				listOfSession.add(sessionList);

			}
			res.setResponseCode(1);
			res.setResponseMessage(getResponse.getMsg());
			sessionListResponse.setResponse(res);
			sessionListResponse.setListOfSession(listOfSession);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull");
			sessionListResponse.setResponse(res);
		}
		return sessionListResponse;
	}
}
