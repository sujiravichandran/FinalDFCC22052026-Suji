package com.teclever.dfcc.datastore.sessionmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.LevelOneResponseDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.entities.LoginSessionDetails;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.service.FaultCodeSessionMappingService;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LoginSessionService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.datastore.dto.FaultCodeSessionMappingDTO;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;

public class SessionManagement {

	public Response createSession(SessionDTO sessionDTO) {
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

			GetObjResponse resObj = sessionService.addSession(sessionDto);
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

			List<FaultCodeSessionMappingDTO> faultCodeList = new ArrayList<FaultCodeSessionMappingDTO>();
			faultCodeList = sessionDTO.getFaultCodeMappingList();
//			List<FaultCodeSessionMapping> faultCodeDataList = new ArrayList<FaultCodeSessionMapping>();
			FaultCodeSessionMappingService faultCodeSessionMappingService = new FaultCodeSessionMappingService();
			for (FaultCodeSessionMappingDTO faultCodeSessionMappingDTO : faultCodeList) {
				faultCodeSessionMappingService.addFaultCodeSession(faultCodeSessionMappingDTO.getFaultCodeId(),
						sessionId);
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
}
