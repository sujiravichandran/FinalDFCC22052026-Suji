package com.teclever.dfcc.datastore.sessionmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.LevelOneResponseDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.LevelFiveStageMaster;
import com.teclever.datastore.entities.LevelFourStageMaster;
import com.teclever.datastore.entities.LevelOneStageMaster;
import com.teclever.datastore.entities.LevelThreeStageMaster;
import com.teclever.datastore.entities.LevelTwoStageMaster;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.StagesRemarks;
import com.teclever.datastore.entities.TestFilesStagesMapping;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.response.StagesRemarksResponse;
import com.teclever.datastore.response.UUTMasterDetailsServiceResponse;
import com.teclever.datastore.service.FaultCodeSessionMappingService;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.LoginSessionService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.StagesRemarksService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.datastore.service.UUTMasterDetailsService;
import com.teclever.datastore.service.UserLoginDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.HistoryReportResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionDTOResponse;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.StageRemarksResponse;
import com.teclever.dfcc.datastore.dto.StagesRemarksDto;
import com.teclever.dfcc.datastore.dto.TrailSaveResponse;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Debug;

public class SessionManagement {
	private final static String COMPLETED ="Completed";
	private final static String PENDING ="pending";
	private final static String PARTIAL ="Partial";
	List<SessionToStagesMappingDTO> sessionStages = new ArrayList<>();

	// API:: GET ALL SESSION FOR HISTORY REPORTS
	public HistoryReportResponse getAllSessionInfo() {
		SessionService service = new SessionService();
		SessionResponse serviceResponse = service.getAllSession();
		HistoryReportResponse response = new HistoryReportResponse();
		List<SessionDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponse().getResponseCode() == 1) {
			List<SessionDto> configurationList = serviceResponse.getListOfSession();
			for (SessionDto configuration : configurationList) {
				dtoList.add(configuration);
				response.setResponseCode(1);
				response.setResponseMsg("FETCHED SESSION DETAILS");
				response.setListOfSession(dtoList);
			}
		} else {
			response.setResponseCode(0);
			response.setResponseMsg("FAILED TO FETCH SESSION DETAILS");
			response.setListOfSession(null);
			System.err.println(
					"Failed to fetch All Session Details: " + serviceResponse.getResponse().getResponseMessage());
		}

		return response;
	}

	// SESSION ENTITY : SAVE SESSION
	public Response saveSession(SessionDTO sessionDTO) {
		Response res = new Response();
		try {
			SessionService sessionService = new SessionService();
			sessionDTO.setSessionName(sessionDTO.getSessionName().replace(":", " "));
			SessionDto sessionDto = new SessionDto();
			Date utilDate = new Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			String sessionPath = new File(
					SessionManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
			Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();
			sessionDto.setCreationDate(sqlDate);
			sessionDto.setDfccPartNo(sessionDTO.getDfccPartNo());
			sessionDto.setDfccSNo(sessionDTO.getDfccSNo());
			sessionDto.setSessionName(sessionDTO.getSessionName());
			sessionDto.setSessionTypeMasterId(sessionDTO.getSessionTypeMasterId());
			sessionDto.setUserId(sessionDTO.getUserId());
			sessionDto.setUutId(sessionDTO.getUutId());
			sessionDto.setStartDate(sessionDTO.getStartDate());
			sessionDto.setStartRemarks(sessionDTO.getStartRemarks());
			sessionDto.setEndRemarks(sessionDTO.getEndRemarks());
			sessionDto.setOfpConfigId(sessionDTO.getOfpConfigId());
			sessionPath = sessionPath + File.separator +"."+"output"+ File.separator+ uutIdName.get(sessionDTO.getUutId()) + File.separator
					+ sessionDTO.getDfccPartNo() + File.separator + sessionDTO.getSessionName();
			sessionDto.setPath(sessionPath);
			// SESSION ENTITY : ADD
			GetObjResponse resObj = sessionService.addSession(sessionDto);
			if (resObj.getResponse().getResponseCode() == 0) {
				return resObj.getResponse();
			}
			SessionDto sessionResponseDto = (SessionDto) resObj.getObject();
			String sessionId = sessionResponseDto.getSessionId();

			// SessionId Update to StateMachine
			StateMachine.currentSessionDetails.setSessionId(sessionId);

			List<SessionToStagesMappingDTO> sessionStages = sessionDTO.getSessionStagesList();

			sessionStages.addAll(getDefaultStatusLevelData(sessionDTO.getUutId()));

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

			// For Adding Path In Table
			List<SessionToStagesMappingDTO> dbSessionStages = new ArrayList<SessionToStagesMappingDTO>();

			List<List<String>> levels = new ArrayList<List<String>>();
			for (SessionToStagesMappingDTO sessionToStagesMappingDTO : sessionStages) {
				List<String> subLevels = new ArrayList<String>();
				String path = sessionPath;
				path = path + File.separator + levelOneStage.get(sessionToStagesMappingDTO.getLevelOneStageId());
				subLevels.add(levelOneStage.get(sessionToStagesMappingDTO.getLevelOneStageId()));
				if (sessionToStagesMappingDTO.getLevelTwoStageId() != null
						&& !sessionToStagesMappingDTO.getLevelTwoStageId().equals("")) {
					path = path + File.separator + levelTwoStage.get(sessionToStagesMappingDTO.getLevelTwoStageId());
					subLevels.add(levelTwoStage.get(sessionToStagesMappingDTO.getLevelTwoStageId()));

				}

				if (sessionToStagesMappingDTO.getLevelThreeStageId() != null
						&& !sessionToStagesMappingDTO.getLevelThreeStageId().equals("")) {
					path = path + File.separator
							+ levelThreeStage.get(sessionToStagesMappingDTO.getLevelThreeStageId());
					subLevels.add(levelThreeStage.get(sessionToStagesMappingDTO.getLevelThreeStageId()));

				}
				if (sessionToStagesMappingDTO.getLevelFourStageId() != null
						&& !sessionToStagesMappingDTO.getLevelFourStageId().equals("")) {
					path = path + File.separator + levelFourStage.get(sessionToStagesMappingDTO.getLevelFourStageId());
					subLevels.add(levelFourStage.get(sessionToStagesMappingDTO.getLevelFourStageId()));

				}
				if (sessionToStagesMappingDTO.getLevelFiveStageId() != null
						&& !sessionToStagesMappingDTO.getLevelFiveStageId().equals("")) {
					path = path + File.separator + levelFiveStage.get(sessionToStagesMappingDTO.getLevelFiveStageId());
					subLevels.add(levelFiveStage.get(sessionToStagesMappingDTO.getLevelFiveStageId()));
				}
				path = path + File.separator;
				levels.add(subLevels);
				sessionToStagesMappingDTO.setPath(path);
				dbSessionStages.add(sessionToStagesMappingDTO);
			}

			SessionFileManagement sessionFileManagement = new SessionFileManagement();
			String uutName = uutIdName.get(sessionDTO.getUutId());

			sessionFileManagement.createSessionFolders(uutName, sessionDTO.getDfccPartNo(), sessionDTO.getSessionName(),
					levels);

			List<SessionStagesMapping> sessionToStagesMappingList = new ArrayList<SessionStagesMapping>();
			Set<String> setOfL1Ids = new LinkedHashSet<>();
			for (SessionToStagesMappingDTO sessionToStagesMappingDTO : dbSessionStages) {

				// for (SessionToStagesMappingDTO sessionToStagesMappingDTO : sessionStages) {
				SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
				sessionStagesMapping.setRepeatCount(1);
				sessionStagesMapping.setRunCount(0);
				sessionStagesMapping.setSessionId(sessionId);
				sessionStagesMapping.setStagelLevelId(sessionToStagesMappingDTO.getStagelLevelId());
				sessionStagesMapping.setStatus("PENDING");
				sessionStagesMapping.setRunDate(null);
				sessionStagesMapping.setTestTypeId(sessionToStagesMappingDTO.getTestTypeId());
				sessionStagesMapping.setLevelOneStageId(sessionToStagesMappingDTO.getLevelOneStageId());
				sessionStagesMapping.setLevelTwoStageId(sessionToStagesMappingDTO.getLevelTwoStageId());
				sessionStagesMapping.setLevelThreeStageId(sessionToStagesMappingDTO.getLevelThreeStageId());
				sessionStagesMapping.setLevelFourStageId(sessionToStagesMappingDTO.getLevelFourStageId());
				sessionStagesMapping.setLevelFiveStageId(sessionToStagesMappingDTO.getLevelFiveStageId());
				sessionStagesMapping.setPath(sessionToStagesMappingDTO.getPath());
				sessionToStagesMappingList.add(sessionStagesMapping);
				if (!setOfL1Ids.contains(sessionToStagesMappingDTO.getLevelOneStageId())) {
					setOfL1Ids.add(sessionToStagesMappingDTO.getLevelOneStageId());
				}
			}
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			// SESSION STAGE MAPPING : ADD
			res = sessionSelectedStagesService.addStagesToSession(sessionToStagesMappingList);
			if (res.getResponseCode() == 0) {
				sessionService.deleteSessionEntity(sessionId);
			} else {
				List<String> faultCodeList = sessionDTO.getFaultCodeMappingList();
				if (faultCodeList.size() > 0) {
					FaultCodeSessionMappingService faultCodeSessionMappingService = new FaultCodeSessionMappingService();
					for (String faultCodeSessionId : faultCodeList) {
						faultCodeSessionMappingService.addFaultCodeSession(faultCodeSessionId, sessionId);
					}
				}
			}

			// LOGIN SESSION DETAILS : UPDATE (SESSION ID)
			addCurrentlyUsingSessionId(sessionId);

			addStagesRemarks(setOfL1Ids);
			
			res.setResponseCode(1);
			res.setResponseMessage("Session Created Successfully..!");

		} catch (Exception ex) {
			res.setResponseCode(0);
			res.setResponseMessage("Session Not Created" + ex.getLocalizedMessage());
			ex.printStackTrace();

		}
		return res;
	}

	// AT FIRST TIME SESSION CREATION
	public StageMasterLevelOneResponse getLevelOneStageMasterBySessionId(String uutId, String sessionId) {
		StageMasterLevelOneResponse stageMasterLevelOne = new StageMasterLevelOneResponse();
		try {
			LevelOneMasterService levelOne = new LevelOneMasterService();
			StageLevelResponse serviceResponse = levelOne.getLevelTOneMasterBySessionId(uutId, sessionId);

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
				levelOneDto.setMandatoryStatus(levelOneEntity.isMandatoryStatus());
				levelOneDto.setContinueWithErrorStatus(levelOneEntity.isContinueWithErrorStatus());
				levelOneDto.setAdvanceTestStatus(levelOneEntity.isAdvanceTestStatus());
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

			Map<String, LevelOneStageMaster> levelOneStageWithObject = levelOneService.getAllLevelOneWithId();

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

				stageObject.setSessionStagesMappingId(sessionStage.getSessionStagesMappingId());

				stageObject.setL1StageId(sessionStage.getLevelOneStageId());
				stageObject.setL1StageName(levelOneStage.get(sessionStage.getLevelOneStageId()));

				stageObject.setL2StageId(sessionStage.getLevelTwoStageId());
				stageObject.setL2StageName(levelTwoStage.get(sessionStage.getLevelTwoStageId()));

				stageObject.setL3StageId(sessionStage.getLevelThreeStageId());
				stageObject.setL3StageName(levelThreeStage.get(sessionStage.getLevelThreeStageId()));

				stageObject.setL4StageId(sessionStage.getLevelFourStageId());
				stageObject.setL4StageName(levelFourStage.get(sessionStage.getLevelFourStageId()));

				stageObject.setL5StageId(sessionStage.getLevelFiveStageId());
				stageObject.setL5StageName(levelFiveStage.get(sessionStage.getLevelFiveStageId()));

				stageObject.setTestTypeId(sessionStage.getTestTypeId());

				stageObject.setStatus(sessionStage.getStatus());

				stageObject.setMandatoryStatus(
						levelOneStageWithObject.get(sessionStage.getLevelOneStageId()).isMandatory());
				stageObject.setContinueWithErrorStatus(
						levelOneStageWithObject.get(sessionStage.getLevelOneStageId()).isContinuewitheror());
				stageObject.setAdvanceStatus(
						levelOneStageWithObject.get(sessionStage.getLevelOneStageId()).isAdvancestatus());
				stageObject.setDefaultStatus(
						levelOneStageWithObject.get(sessionStage.getLevelOneStageId()).isDefaultStatus());

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

	// BASED ON USER ID GET LIST OF SESSION, WHICH DONT HAVE END TIME
	public SessionListResponse getAllSessionData(String userId) {
		SessionListResponse sessionListResponse = new SessionListResponse();
		Response res = new Response();
		try {

			// To Picking Trail Sessions...
			TrailSessionResponse trailSessionResponse = new TrailSessionResponse();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			trailSessionResponse = trailSessionEntityService.getActiveTrailSessionId();
			List<SessionList> listOfSession = new ArrayList<>();

			List<TrailSessionDto> trailActiveSession = new ArrayList<TrailSessionDto>();
			trailActiveSession = trailSessionResponse.getListOfSession();
			for (TrailSessionDto trailSessionDto : trailActiveSession) {
				SessionList sessionList = new SessionList();
				sessionList.setSessionId(trailSessionDto.getSessionId());
				sessionList.setSessionName(trailSessionDto.getSessionName());
				sessionList.setCreationDate(trailSessionDto.getCreationDate());
				listOfSession.add(sessionList);

			}

			// For Picking Others Sessions...
			SessionSelectedStagesService sessionSelectedStage = new SessionSelectedStagesService();
			GetResponse getResponse = sessionSelectedStage.getAllSessionData(userId);

			if (getResponse.getCode() == 0) {
				if (listOfSession.size() > 0) {
					res.setResponseCode(1);
					res.setResponseMessage(getResponse.geteMsg() + "  Error On Session Enity....");
					sessionListResponse.setResponse(res);
					sessionListResponse.setListOfSession(listOfSession);
					return sessionListResponse;

				}
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				sessionListResponse.setResponse(res);
				return sessionListResponse;
			}

			for (Object object : getResponse.getResponseList()) {
				SessionEntity sessionEntity = (SessionEntity) object;
				SessionList sessionList = new SessionList();
				sessionList.setSessionId(sessionEntity.getSessionId());
				sessionList.setSessionName(sessionEntity.getSessionName());
				sessionList.setCreationDate(sessionEntity.getCreationDate());
				sessionList.setOfpConfigId(sessionEntity.getOfpConfigId());
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

	// BASED ON ROLE ID AND UUT ID GET LIST OF SESSION
	public SessionListResponse getSessionDataByUUTId(String uutId) {
		SessionListResponse sessionListResponse = new SessionListResponse();
		Response res = new Response();
		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(UserData.getRoleId());

			// To Picking Trail Sessions...
			TrailSessionResponse trailSessionResponse = new TrailSessionResponse();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			trailSessionResponse = trailSessionEntityService.getAllSession();
//
			List<SessionList> listOfSession = new ArrayList<>();
//
			List<TrailSessionDto> trailActiveSession = new ArrayList<TrailSessionDto>();
			trailActiveSession = trailSessionResponse.getListOfSession();
			for (TrailSessionDto trailSessionDto : trailActiveSession) {
				if (trailSessionDto.getUutId().equals(uutId) && setOfUserLoginId != null
						&& setOfUserLoginId.contains(trailSessionDto.getCreatedBy())) {
					SessionList sessionList = new SessionList();
					sessionList.setSessionId(trailSessionDto.getSessionId());
					sessionList.setSessionName(trailSessionDto.getSessionName());
					sessionList.setCreationDate(trailSessionDto.getCreationDate());
					listOfSession.add(sessionList);
				}

			}

			// For Picking Others Sessions...
			SessionService sessionSelectedStage = new SessionService();
			GetResponse getResponse = sessionSelectedStage.getAllSessionDataByUUTId(uutId);

			if (getResponse.getCode() == 0) {
				if (listOfSession.size() > 0) {
					res.setResponseCode(1);
					res.setResponseMessage(getResponse.geteMsg() + "  Error On Session Enity....");
					sessionListResponse.setResponse(res);
					sessionListResponse.setListOfSession(listOfSession);
					return sessionListResponse;

				}
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				sessionListResponse.setResponse(res);
				return sessionListResponse;
			}

			for (Object object : getResponse.getResponseList()) {
				SessionEntity sessionEntity = (SessionEntity) object;

				if (setOfUserLoginId != null && setOfUserLoginId.contains(sessionEntity.getUserId())) {
					SessionList sessionList = new SessionList();
					sessionList.setSessionId(sessionEntity.getSessionId());
					sessionList.setSessionName(sessionEntity.getSessionName());
					sessionList.setCreationDate(sessionEntity.getCreationDate());
					sessionList.setOfpConfigId(sessionEntity.getOfpConfigId());
					listOfSession.add(sessionList);
				}

			}

			res.setResponseCode(1);
			res.setResponseMessage(getResponse.getMsg());
			sessionListResponse.setResponse(res);
			sessionListResponse.setListOfSession(listOfSession);
		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull");
			sessionListResponse.setResponse(res);
		}
		return sessionListResponse;
	}

	public SessionDTOResponse getSessionDetailById(String sessionEntityId) {
		SessionDTOResponse sessionDtoResponse = new SessionDTOResponse();
		Response res = new Response();
		try {

			if (sessionEntityId.substring(0, 4).equals("TSSN")) {
				TrailSessionEntityService sessionService = new TrailSessionEntityService();
				GetObjResponse getObjResponse = sessionService.getSessionDetailBySessionId(sessionEntityId);
				// SessionStageMapResponse sessionSrageResponse =
				// getAllSessionStageMapping(sessionEntityId);
				if (getObjResponse.getResponse().getResponseCode() == 1) {

					TrailSessionEntity sessionEntity = (TrailSessionEntity) getObjResponse.getObject();
					sessionDtoResponse.setSessionId(sessionEntity.getTrailSessionId());
					sessionDtoResponse.setUutId(sessionEntity.getUutTypeId());
					sessionDtoResponse.setSessionName(sessionEntity.getTrailSessionName());
					sessionDtoResponse.setSessionTypeMasterId("ST4");
					sessionDtoResponse.setDfccSNo(sessionEntity.getDfccSNo());
					sessionDtoResponse.setDfccPartNo(sessionEntity.getDfccPartNo());
					sessionDtoResponse.setCreationDate(sessionEntity.getCreationDate());
					sessionDtoResponse.setStartDate(sessionEntity.getStartDate());
					sessionDtoResponse.setEndDate(sessionEntity.getEndDate());
					sessionDtoResponse.setStartRemarks(sessionEntity.getStartRemarks());
					Response res1 = new Response();
					res1.setResponseCode(getObjResponse.getResponse().getResponseCode());
					res1.setResponseMessage(getObjResponse.getResponse().getResponseMessage());
					sessionDtoResponse.setResponse(res1);
					return sessionDtoResponse;
				}

			}
			SessionService sessionService = new SessionService();
			GetObjResponse getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionEntityId);
			SessionStageMapResponse sessionSrageResponse = getAllSessionStageMapping(sessionEntityId);
			if (getObjResponse.getResponse().getResponseCode() == 0
					|| sessionSrageResponse.getResponse().getResponseCode() == 0) {
				sessionDtoResponse.setResponse(getObjResponse.getResponse());
				return sessionDtoResponse;
			}
			SessionEntity sessionEntity = (SessionEntity) getObjResponse.getObject();
			sessionDtoResponse.setSessionId(sessionEntity.getSessionId());
			sessionDtoResponse.setUutId(sessionEntity.getUutId());
			sessionDtoResponse.setSessionName(sessionEntity.getSessionName());
			sessionDtoResponse.setSessionTypeMasterId(sessionEntity.getSessionTypeMasterId());
			sessionDtoResponse.setUserId(sessionEntity.getUserId());
			sessionDtoResponse.setDfccSNo(sessionEntity.getDfccSNo());
			sessionDtoResponse.setDfccPartNo(sessionEntity.getDfccPartNo());
			sessionDtoResponse.setCreationDate(sessionEntity.getCreationDate());
			sessionDtoResponse.setStartDate(sessionEntity.getStartDate());
			sessionDtoResponse.setEndDate(sessionEntity.getEndDate());
			sessionDtoResponse.setStartRemarks(sessionEntity.getStartRemarks());
			sessionDtoResponse.setOfpConfigId(sessionEntity.getOfpConfigId());

			sessionDtoResponse.setSessionStagesList(sessionSrageResponse.getListOfStageObject());

			FaultCodeSessionMappingService faultCodeSessionMap = new FaultCodeSessionMappingService();
			List<String> ListOfFaultCodeIds = faultCodeSessionMap.getFaultCodeBySessionId(sessionEntityId);

			if (ListOfFaultCodeIds != null && ListOfFaultCodeIds.size() > 0) {
				List<FaultCodeDTO> faultCodeMappingList = new ArrayList<>();

				FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
				FaultCodeResponse faultCodeResponse = faultCodeConfiguration.getFaultCodeList(sessionEntity.getUutId(),
						sessionEntity.getOfpConfigId());
				Map<String, FaultCodeDTO> faultCodePKeyWithDto = new HashMap<>();

				for (FaultCodeDTO faultCode : faultCodeResponse.getFaultCodeList()) {
					faultCodePKeyWithDto.put(faultCode.getFaultCodeMasterId(), faultCode);
				}
				for (String faultCodePKey : ListOfFaultCodeIds) {
					if (faultCodePKeyWithDto.get(faultCodePKey) != null) {
						faultCodeMappingList.add(faultCodePKeyWithDto.get(faultCodePKey));
					}
				}
				sessionDtoResponse.setFaultCodeMappingList(faultCodeMappingList);
			}

			res.setResponseCode(1);
			res.setResponseMessage("Fetch Data Successfull");
			sessionDtoResponse.setResponse(res);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull");
			sessionDtoResponse.setResponse(res);
			e.printStackTrace();
		}
		return sessionDtoResponse;
	}

	// Get Trail Session Details
	public SessionDTOResponse getTrailSessionDetailById(String sessionEntityId) {
		SessionDTOResponse sessionDtoResponse = new SessionDTOResponse();
		Response res = new Response();
		try {
			TrailSessionEntityService sessionService = new TrailSessionEntityService();
			GetObjResponse getObjResponse = sessionService.getSessionDetailBySessionId(sessionEntityId);
			SessionStageMapResponse sessionStageResponse = getAllSessionStageMapping(sessionEntityId);
			if (getObjResponse.getResponse().getResponseCode() == 0
					|| sessionStageResponse.getResponse().getResponseCode() == 0) {
				sessionDtoResponse.setResponse(getObjResponse.getResponse());
				return sessionDtoResponse;
			}
			TrailSessionEntity sessionEntity = (TrailSessionEntity) getObjResponse.getObject();
			sessionDtoResponse.setSessionId(sessionEntity.getTrailSessionId());
			sessionDtoResponse.setUutId(sessionEntity.getUutTypeId());
			sessionDtoResponse.setSessionName(sessionEntity.getTrailSessionName());
			sessionDtoResponse.setSessionTypeMasterId("ST4");
			sessionDtoResponse.setUserId(sessionEntity.getCreatedBy());
			sessionDtoResponse.setDfccSNo(sessionEntity.getDfccSNo());
			sessionDtoResponse.setDfccPartNo(sessionEntity.getDfccPartNo());
			sessionDtoResponse.setCreationDate(sessionEntity.getCreationDate());
			sessionDtoResponse.setStartDate(sessionEntity.getStartDate());
			sessionDtoResponse.setEndDate(sessionEntity.getEndDate());
			sessionDtoResponse.setStartRemarks(sessionEntity.getStartRemarks());

			sessionDtoResponse.setSessionStagesList(sessionStageResponse.getListOfStageObject());

			/*
			 * FaultCodeSessionMappingService faultCodeSessionMap = new
			 * FaultCodeSessionMappingService(); List<String> ListOfFaultCodeIds =
			 * faultCodeSessionMap.getFaultCodeBySessionId(sessionEntityId);
			 * 
			 * 
			 * if (ListOfFaultCodeIds != null && ListOfFaultCodeIds.size() > 0) {
			 * List<FaultCodeDTO> faultCodeMappingList = new ArrayList<>();
			 * 
			 * FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
			 * FaultCodeResponse faultCodeResponse =
			 * faultCodeConfiguration.getFaultCodeList(sessionEntity.getUutTypeId(),
			 * sessionEntity.getOfpConfigId()); Map<String, FaultCodeDTO>
			 * faultCodePKeyWithDto = new HashMap<>();
			 * 
			 * for (FaultCodeDTO faultCode : faultCodeResponse.getFaultCodeList()) {
			 * faultCodePKeyWithDto.put(faultCode.getFaultCodeMasterId(), faultCode); } for
			 * (String faultCodePKey : ListOfFaultCodeIds) { if
			 * (faultCodePKeyWithDto.get(faultCodePKey) != null) {
			 * faultCodeMappingList.add(faultCodePKeyWithDto.get(faultCodePKey)); } }
			 * sessionDtoResponse.setFaultCodeMappingList(faultCodeMappingList); }
			 */

			res.setResponseCode(1);
			res.setResponseMessage("Fetch Data Successfull");
			sessionDtoResponse.setResponse(res);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull");
			sessionDtoResponse.setResponse(res);
			e.printStackTrace();
		}
		return sessionDtoResponse;
	}

	// LOGIN SESSION : Add session Id
	public Response addCurrentlyUsingSessionId(String sessionId) {
		Response res = new Response();
		try {
			LoginSessionService loginSessionService = new LoginSessionService();

			loginSessionService.updateLoginSession(currentSessionDetails.getLoginSessionId(), sessionId, null);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Login Session Unsuccessful ");
		}
		return res;
	}

	// LOGIN SESSION : Add User Logout Time
	public Response addLogoutTime() {
		Response res = new Response();
		try {
			LoginSessionService loginSessionService = new LoginSessionService();
			res = loginSessionService.updateLoginSession(currentSessionDetails.getLoginSessionId(), null, new Date());

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Login Session Unsuccessful ");
		}
		return res;
	}

	public void updateActiveSession() {

	}

	/*
	 * private List<SessionToStagesMappingDTO> getAdvanceTestSubLevelData(String
	 * uutId) {
	 * 
	 * List<SessionToStagesMappingDTO> listOfSessionToStagesMappingDTO = new
	 * ArrayList<>(); try { String advanceLevelOne = null; String advanceLevelTwo =
	 * null; String lruLevelOneId = null; String lruLevelTwoId = null; List<String>
	 * advaceLevelList = new ArrayList<>();
	 * 
	 * LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
	 * List<LevelOneResponseDto> levelOneList =
	 * levelOneMasterService.getLevelOneByUUTId(uutId);
	 * 
	 * for (LevelOneResponseDto levelOneId : levelOneList) { if
	 * (levelOneId.isAdvanceTestStatus()) { advanceLevelOne =
	 * levelOneId.getLevelOneId(); } if
	 * (levelOneId.getStageName().startsWith("LRU")) { lruLevelOneId =
	 * levelOneId.getLevelOneId(); } }
	 * 
	 * LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
	 * Map<String, List<LevelTwoStageMaster>> levelTwoMap =
	 * levelTwoService.getListOfEntityWithParentId();
	 * 
	 * List<LevelTwoStageMaster> advanceLevelList =
	 * levelTwoMap.get(advanceLevelOne); for (LevelTwoStageMaster l2 :
	 * advanceLevelList) { if (l2.getStageName().equalsIgnoreCase("Interface")) {
	 * advanceLevelTwo = l2.getLevelTwoStageId();
	 * 
	 * } else {
	 * 
	 * if (l2.getNextLevel().equals("N")) { listOfSessionToStagesMappingDTO.add(
	 * objCreation(advanceLevelOne, advanceLevelTwo, null, null, null,
	 * l2.getTestTypeId())); } else if (l2.getNextLevel().equals("Y")) {
	 * 
	 * advaceLevelList.add(l2.getLevelTwoStageId()); } }
	 * 
	 * } List<LevelTwoStageMaster> lruLevellist = levelTwoMap.get(lruLevelOneId);
	 * for (LevelTwoStageMaster l2 : lruLevellist) { if
	 * (l2.getStageName().startsWith("SRU")) { lruLevelTwoId =
	 * l2.getLevelTwoStageId(); } }
	 * 
	 * // Debug.printDebug("advanceLevelOne  " + advanceLevelOne +
	 * "   lruLevelOneId " + lruLevelOneId); //
	 * Debug.printDebug("advanceLevelTwo  " + advanceLevelTwo +
	 * "   lruLevelTwoId " + lruLevelTwoId);
	 * 
	 * LevelThreeService levelThreeService = new LevelThreeService(); Map<String,
	 * List<LevelThreeStageMaster>> levelThreeMap =
	 * levelThreeService.getListOfEntityWithParentId();
	 * 
	 * LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
	 * Map<String, List<LevelFourStageMaster>> levelFourMap =
	 * levelFourService.getListOfEntityWithParentId();
	 * 
	 * LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
	 * Map<String, List<LevelFiveStageMaster>> levelFiveMap =
	 * levelFiveService.getListOfEntityWithParentId();
	 * 
	 * listOfSessionToStagesMappingDTO.addAll(getSubStagesIdsWithTestType(
	 * lruLevelTwoId, advanceLevelOne, advanceLevelTwo, levelThreeMap, levelFourMap,
	 * levelFiveMap));
	 * 
	 * for (String levelTwoIds : advaceLevelList) {
	 * listOfSessionToStagesMappingDTO.addAll(getSubStagesIdsWithTestType(
	 * levelTwoIds, advanceLevelOne, advanceLevelTwo, levelThreeMap, levelFourMap,
	 * levelFiveMap));
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return
	 * listOfSessionToStagesMappingDTO; }
	 */

	private List<SessionToStagesMappingDTO> getDefaultStatusLevelData(String uutId) {

		List<SessionToStagesMappingDTO> listOfSessionToStagesMappingDTO = new ArrayList<>();
		try {

			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
			List<LevelOneResponseDto> levelOneList = levelOneMasterService.getLevelOneByUUTId(uutId);

			List<LevelOneResponseDto> listOflevelOneStsge = new ArrayList<>();

			// Filtering Level One stages based on default or advanced test status
			for (LevelOneResponseDto levelOneId : levelOneList) {
				if (levelOneId.isDefaultStatus() || levelOneId.isAdvanceTestStatus()) {
					listOflevelOneStsge.add(levelOneId);
				}
			}
			// Level One : Check level One have Filtered Data.
			if (listOflevelOneStsge.size() != 0) {

				LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
				Map<String, List<LevelTwoStageMaster>> levelTwoMap = levelTwoService.getListOfEntityWithParentId();

				LevelThreeService levelThreeService = new LevelThreeService();
				Map<String, List<LevelThreeStageMaster>> levelThreeMap = levelThreeService
						.getListOfEntityWithParentId();

				LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
				Map<String, List<LevelFourStageMaster>> levelFourMap = levelFourService.getListOfEntityWithParentId();

				LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
				Map<String, List<LevelFiveStageMaster>> levelFiveMap = levelFiveService.getListOfEntityWithParentId();

				// Level One
				for (LevelOneResponseDto levelOneStage : listOflevelOneStsge) {
					// Next Level : N
					if (levelOneStage.getNextLevel().equals("N")) {
						listOfSessionToStagesMappingDTO
								.add(objCreation(levelOneStage.getLevelOneId(), null, null, null, null, null));
					}
					// Next Level : Y
					else if (levelOneStage.getNextLevel().equals("Y")) {

						// Level Two : Check level Two have Level One parent Id.
						if (levelTwoMap.get(levelOneStage.getLevelOneId()) != null) {
							List<LevelTwoStageMaster> listOfLevelTwoStage = levelTwoMap
									.get(levelOneStage.getLevelOneId());
							for (LevelTwoStageMaster levelTwoStage : listOfLevelTwoStage) {
								// Next Level : N
								if (levelTwoStage.getNextLevel().equals("N")) {
									listOfSessionToStagesMappingDTO.add(objCreation(levelOneStage.getLevelOneId(),
											levelTwoStage.getLevelTwoStageId(), null, null, null,
											levelTwoStage.getTestTypeId()));
								}
								// Next Level : Y
								else if (levelTwoStage.getNextLevel().equals("Y")) {
									// Level Three : Check level Three have Level Two parent Id.
									if (levelThreeMap.get(levelTwoStage.getLevelTwoStageId()) != null) {

										List<LevelThreeStageMaster> levelThreelist = levelThreeMap
												.get(levelTwoStage.getLevelTwoStageId());

										for (LevelThreeStageMaster l3 : levelThreelist) {
											// Next Level : N
											if (l3.getNextLevel().equals("N")) {
												listOfSessionToStagesMappingDTO.add(objCreation(
														levelOneStage.getLevelOneId(),
														levelTwoStage.getLevelTwoStageId(), l3.getLevelThreeStageId(),
														null, null, l3.getTestTypeId()));
											}
											// Next Level : Y
											else if (l3.getNextLevel().equals("Y")) {
												// Level Four : Check level Four have Level Three parent Id.
												if (levelFourMap.get(l3.getLevelThreeStageId()) != null) {
													List<LevelFourStageMaster> l4stage = levelFourMap
															.get(l3.getLevelThreeStageId());

													for (LevelFourStageMaster l4 : l4stage) {
														// Next Level : N
														if (l4.getNextLevel().equals("N")) {
															listOfSessionToStagesMappingDTO.add(objCreation(
																	levelOneStage.getLevelOneId(),
																	levelTwoStage.getLevelTwoStageId(),
																	l3.getLevelThreeStageId(), l4.getLevelFourStageId(),
																	null, l4.getTestTypeId()));

														}
														// Next Level : Y
														else if (l4.getNextLevel().equals("Y")) {
															// Level Five : Check level Five have Level Four parent Id.
															if (levelFiveMap.get(l4.getLevelFourStageId()) != null) {
																List<LevelFiveStageMaster> l5stage = levelFiveMap
																		.get(l4.getLevelFourStageId());

																for (LevelFiveStageMaster l5 : l5stage) {
																	// Next Level : N
																	if (l4.getNextLevel().equals("N")) {
																		listOfSessionToStagesMappingDTO.add(objCreation(
																				levelOneStage.getLevelOneId(),
																				levelTwoStage.getLevelTwoStageId(),
																				l3.getLevelThreeStageId(),
																				l4.getLevelFourStageId(),
																				l5.getLevelFiveStageId(),
																				l5.getTestTypeId()));

																	}
																}

															} // Level Five
														}
													}
												} // Level Four
											}
										}
									} // Level Three

								}
							}
						} // Level Two
					}

				}
			} // Level One

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return listOfSessionToStagesMappingDTO;
	}

	private SessionToStagesMappingDTO objCreation(String l1, String l2, String l3, String l4, String l5,
			String testTypeId) {

		SessionToStagesMappingDTO sessionToStagesMappingDTO = new SessionToStagesMappingDTO();
		sessionToStagesMappingDTO.setLevelOneStageId(l1);
		sessionToStagesMappingDTO.setLevelTwoStageId(l2);
		sessionToStagesMappingDTO.setLevelThreeStageId(l3);
		sessionToStagesMappingDTO.setLevelFourStageId(l4);
		sessionToStagesMappingDTO.setLevelFiveStageId(l5);
		sessionToStagesMappingDTO.setTestTypeId(testTypeId);

		return sessionToStagesMappingDTO;

	}

	public Map<String, String> getAllStageIdName() {
		Map<String, String> allStageIdName = new HashMap<String, String>();
		try {
			LevelOneMasterService levelOneService = new LevelOneMasterService();
			Map<String, String> levelOneStage = levelOneService.getAllLevelOneIdAndLevelName();
			allStageIdName.putAll(levelOneStage);
			LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
			Map<String, String> levelTwoStage = levelTwoService.getAllLevelIdAndLevelName();
			allStageIdName.putAll(levelTwoStage);
			LevelThreeService levelThreeService = new LevelThreeService();
			Map<String, String> levelThreeStage = levelThreeService.getAllLevelIdAndLevelName();
			allStageIdName.putAll(levelThreeStage);
			LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
			Map<String, String> levelFourStage = levelFourService.getAllLevelIdAndLevelName();
			allStageIdName.putAll(levelFourStage);
			LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
			Map<String, String> levelFiveStage = levelFiveService.getAllLevelIdAndLevelName();
			allStageIdName.putAll(levelFiveStage);
		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return allStageIdName;
	}

	public boolean getFinalizeStatus() {
		boolean isConfig = false;
		try {
			TrailSessionEntity sessionEntity = new TrailSessionEntity();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			GetObjResponse objRes = trailSessionEntityService.getActiveTrailSessionObject();
			sessionEntity = (TrailSessionEntity) objRes.getObject();
			isConfig = sessionEntity.isRunned();
		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		System.out.println("Is Reconfig Status"+ getReconfigStatus());
		return isConfig;
	}
	
	
	
	public boolean getReconfigStatus() {
		boolean isReConfigAvail = true;
		try {
			TrailSessionEntity sessionEntity = new TrailSessionEntity();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			GetObjResponse objRes = trailSessionEntityService.getActiveTrailSessionObject();
			sessionEntity = (TrailSessionEntity) objRes.getObject();
			String trailSessionId = sessionEntity.getTrailSessionId();

			SessionStagesTestFilesResultService s = new SessionStagesTestFilesResultService();
			GetResponse getResponse = s.getTestResultFileByStageId(trailSessionId);
			List<SessionStagesTestFilesResult> lst = new ArrayList();
			System.out.println("Name"+trailSessionId);
			lst = (List<SessionStagesTestFilesResult>) getResponse.getResponseList();
			System.out.println(lst.size());
			if (lst.size()>0) {
				isReConfigAvail = false;
			}

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return isReConfigAvail;
	}	

	// Trail Method - To Save The Trails Entity
	public GetObjResponse saveTrailSessionEntity(SessionDTO sessionDTO) {
		GetObjResponse res = new GetObjResponse();
		try {
			sessionDTO.setSessionName(sessionDTO.getSessionName().replace(":", " "));
			TrailSessionDto sessionDto = new TrailSessionDto();
			Date utilDate = new Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			String sessionPath = new File(
					SessionManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
			Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();
			sessionDto.setCreationDate(sqlDate);
			sessionDto.setDfccPartNo(sessionDTO.getDfccPartNo());
			Debug.printDebug(sessionDTO.getDfccSNo() + " Checking");
			sessionDto.setDfccSNo(sessionDTO.getDfccSNo());
			sessionDto.setSessionName(sessionDTO.getSessionName());
			sessionDto.setSessionTypeMasterId(sessionDTO.getSessionTypeMasterId());
			sessionDto.setUserId(sessionDTO.getUserId());
			sessionDto.setUutId(sessionDTO.getUutId());
			sessionDto.setStartDate(sessionDTO.getStartDate());
			sessionDto.setStartRemarks(sessionDTO.getStartRemarks());
			// sessionDto.setOfpConfigId(sessionDTO.getOfpConfigId());
			sessionPath = sessionPath + File.separator + uutIdName.get(sessionDTO.getUutId()) + File.separator
					+ sessionDTO.getDfccPartNo() + File.separator + sessionDTO.getSessionName();
			sessionDto.setPath(sessionPath);

			// SESSION ENTITY : ADD
			TrailSessionEntityService sessionService = new TrailSessionEntityService();
			res = sessionService.addSession(sessionDto);

			TrailSessionEntity s = (TrailSessionEntity) res.getObject();
			Debug.printDebug("Trail Session Id" + s.getTrailSessionId());
			StateMachine.currentSessionDetails.setSessionId(s.getTrailSessionId());

		} catch (Exception ex) {
			Response res1 = new Response();
			res1.setResponseCode(0);
			res1.setResponseMessage("Trail Session Not Added");
			res.setResponse(res1);

		}
		return res;
	}

	// Trails Method Save the Trail Session..With Stages Mapping
	public TrailSaveResponse finalize(String trailSessionId) {
		TrailSaveResponse res = new TrailSaveResponse();
		try {
			TrailSessionEntityService sessionService = new TrailSessionEntityService();
			GetObjResponse obj = sessionService.getSessionDetailBySessionId(trailSessionId);
			TrailSessionEntity trailEntitySession = (TrailSessionEntity) obj.getObject();
			String sessionPath = trailEntitySession.getPath();
			Debug.printDebug("Trail Session path" + sessionPath);
			List<String> leafIds = getStagesMappingLeafIdForTrails(trailEntitySession.getUutTypeId());
			Map<String, String> stageNameMsg = validateIsAllLeafHavingTestFiles(leafIds);
			Debug.printDebug("Leaf Ids" + leafIds);
			// SessionId Update to StateMachine
			// StateMachine.currentSessionDetails.setSessionId(trailEntitySession.getTrailSessionId());
			// Validate the Leafs Having Test Files..
			if (stageNameMsg.size() > 0) {
				res.setCode(0);
				res.setMsg("Some Of Stages Not Configured With Test Files..");
				res.setStageNameMessage(stageNameMsg);
				return res;
			}

			List<SessionToStagesMappingDTO> sessionStages = getAllTrailStageLevelData(
					trailEntitySession.getUutTypeId());
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

			// For Adding Path In Table
			List<SessionToStagesMappingDTO> dbSessionStages = new ArrayList<SessionToStagesMappingDTO>();

			List<List<String>> levels = new ArrayList<List<String>>();
			for (SessionToStagesMappingDTO sessionToStagesMappingDTO : sessionStages) {
				List<String> subLevels = new ArrayList<String>();
				String path = sessionPath;
				path = path + File.separator + levelOneStage.get(sessionToStagesMappingDTO.getLevelOneStageId());
				subLevels.add(levelOneStage.get(sessionToStagesMappingDTO.getLevelOneStageId()));
				if (sessionToStagesMappingDTO.getLevelTwoStageId() != null
						&& !sessionToStagesMappingDTO.getLevelTwoStageId().equals("")) {
					path = path + File.separator + levelTwoStage.get(sessionToStagesMappingDTO.getLevelTwoStageId());
					subLevels.add(levelTwoStage.get(sessionToStagesMappingDTO.getLevelTwoStageId()));

				}

				if (sessionToStagesMappingDTO.getLevelThreeStageId() != null
						&& !sessionToStagesMappingDTO.getLevelThreeStageId().equals("")) {
					path = path + File.separator
							+ levelThreeStage.get(sessionToStagesMappingDTO.getLevelThreeStageId());
					subLevels.add(levelThreeStage.get(sessionToStagesMappingDTO.getLevelThreeStageId()));

				}
				if (sessionToStagesMappingDTO.getLevelFourStageId() != null
						&& !sessionToStagesMappingDTO.getLevelFourStageId().equals("")) {
					path = path + File.separator + levelFourStage.get(sessionToStagesMappingDTO.getLevelFourStageId());
					subLevels.add(levelFourStage.get(sessionToStagesMappingDTO.getLevelFourStageId()));

				}
				if (sessionToStagesMappingDTO.getLevelFiveStageId() != null
						&& !sessionToStagesMappingDTO.getLevelFiveStageId().equals("")) {
					path = path + File.separator + levelFiveStage.get(sessionToStagesMappingDTO.getLevelFiveStageId());
					subLevels.add(levelFiveStage.get(sessionToStagesMappingDTO.getLevelFiveStageId()));
				}
				path = path + File.separator;
				levels.add(subLevels);
				sessionToStagesMappingDTO.setPath(path);
				dbSessionStages.add(sessionToStagesMappingDTO);
			}
			Map<String, String> uutIdNameMap = new HashMap<String, String>();
			UUTMasterDetailsService uUTMasterDetailsService = new UUTMasterDetailsService();
			UUTMasterDetailsServiceResponse uUTMasterDetailsServiceResponse = new UUTMasterDetailsServiceResponse();
			uUTMasterDetailsServiceResponse = uUTMasterDetailsService.getAllUutDetails();
			String[][] uutIdName = uUTMasterDetailsServiceResponse.getData();
			for (int i = 0; i < uutIdName.length; i++) {
				if (uutIdName[i].length == 2) {
					String key = uutIdName[i][0];
					String value = uutIdName[i][1];
					uutIdNameMap.put(key, value);
				} else {
					Debug.printDebug("Invalid entry at row " + i);
				}
			}

			SessionFileManagement sessionFileManagement = new SessionFileManagement();
			Debug.printDebug();
			String uutName = uutIdNameMap.get(trailEntitySession.getUutTypeId());
			Debug.printDebug("uutName" + uutName);
			sessionFileManagement.createSessionFolders(uutName, trailEntitySession.getDfccPartNo(),
					trailEntitySession.getTrailSessionName(), levels);

			List<SessionStagesMapping> sessionToStagesMappingList = new ArrayList<SessionStagesMapping>();

			for (SessionToStagesMappingDTO sessionToStagesMappingDTO : dbSessionStages) {

				// for (SessionToStagesMappingDTO sessionToStagesMappingDTO : sessionStages) {
				SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
				sessionStagesMapping.setRepeatCount(1);
				sessionStagesMapping.setRunCount(0);
				sessionStagesMapping.setSessionId(trailSessionId);
				sessionStagesMapping.setStagelLevelId(sessionToStagesMappingDTO.getStagelLevelId());
				sessionStagesMapping.setStatus("PENDING");
				sessionStagesMapping.setRunDate(null);
				sessionStagesMapping.setTestTypeId(sessionToStagesMappingDTO.getTestTypeId());
				sessionStagesMapping.setLevelOneStageId(sessionToStagesMappingDTO.getLevelOneStageId());
				sessionStagesMapping.setLevelTwoStageId(sessionToStagesMappingDTO.getLevelTwoStageId());
				sessionStagesMapping.setLevelThreeStageId(sessionToStagesMappingDTO.getLevelThreeStageId());
				sessionStagesMapping.setLevelFourStageId(sessionToStagesMappingDTO.getLevelFourStageId());
				sessionStagesMapping.setLevelFiveStageId(sessionToStagesMappingDTO.getLevelFiveStageId());
				sessionStagesMapping.setPath(sessionToStagesMappingDTO.getPath());
				sessionToStagesMappingList.add(sessionStagesMapping);
			}
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			// SESSION STAGE MAPPING : ADD
			Response stagesRes = new Response();
			stagesRes = sessionSelectedStagesService.addStagesToSession(sessionToStagesMappingList);
			if (stagesRes.getResponseCode() == 0) {
				// sessionService.deleteSessionEntity(trailSessionId);
			} else {
				// trailEntitySession.setRunned(true);
				sessionService.updateRunStatus(trailSessionId);
			}

			res.setCode(1);
			res.setMsg("Session Mapped Successfully..!");

		} catch (Exception ex) {
			res.setCode(0);
			res.setMsg("Session Not Mapped With Stages" + ex.getLocalizedMessage());
			ex.printStackTrace();

		}
		return res;
	}

	// Trails Method Before Finalize
	public Map<String, String> validateIsAllLeafHavingTestFiles(List<String> leafIds) {
		Map<String, String> StageNameValidateMessage = new HashMap<String, String>();
		LevelOneMasterService level1MasterService = new LevelOneMasterService();
		LevelTwoMasterService level2MasterService = new LevelTwoMasterService();
		LevelThreeService level3MasterService = new LevelThreeService();
		LevelFourMasterSevice level4MasterService = new LevelFourMasterSevice();
		LevelFiveMasterService level5MasterService = new LevelFiveMasterService();

		try {
			TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
			GetResponse response = testFilesStagesMappingService.getAllTestFilesStagesMapping();
			List<TestFilesStagesMapping> testFileList = (List<TestFilesStagesMapping>) response.getResponseList();
			Map<String, String> leafIdName = getAllStageIdName();

			for (String leafId : leafIds) {

				List<TestFilesStagesMapping> testFileFilterList = testFileList.stream()
						.filter(testFileMapping -> testFileMapping.getStageLevel().equals(leafId))
						.collect(Collectors.toList());
				if (testFileFilterList.size() > 0) {
					// String stageName = leafIdName.get(leafId);
					// StageNameValidateMessage.put(stageName, "Test File(s) Configured");

				} else {
					String stageName = leafIdName.get(leafId);
					StageNameValidateMessage.put(getFullPathForLeafIds(leafId),
							"Test File(s) Not Configured Pls Configure..");

				}

			}
		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return StageNameValidateMessage;
	}

	public String getFullPathForLeafIds(String leafId) {
		String msg = "";
		try {
			Map<String, String> leafIdName = getAllStageIdName();

			LevelOneMasterService level1MasterService = new LevelOneMasterService();
			LevelTwoMasterService level2MasterService = new LevelTwoMasterService();
			LevelThreeService level3MasterService = new LevelThreeService();
			LevelFourMasterSevice level4MasterService = new LevelFourMasterSevice();
			LevelFiveMasterService level5MasterService = new LevelFiveMasterService();

			if (leafId.substring(0, 2).equalsIgnoreCase("L1")) {
				msg = leafIdName.get(leafId);
			}
			if (leafId.substring(0, 2).equalsIgnoreCase("L2")) {
				LevelTwoStageMaster l2Master = new LevelTwoStageMaster();
				LevelOneStageMaster l1Master = new LevelOneStageMaster();

				l2Master = level2MasterService.getLevelTwoMasterByLevelId(leafId);
				l1Master = level1MasterService.getLevelOneMasterByLevelId(l2Master.getLevelOneRefernce());

				msg = leafIdName.get(l2Master.getLevelOneRefernce()) + "/" + leafIdName.get(leafId);

			}

			if (leafId.substring(0, 2).equalsIgnoreCase("L3")) {
				LevelThreeStageMaster l3Master = new LevelThreeStageMaster();
				LevelTwoStageMaster l2Master = new LevelTwoStageMaster();
				LevelOneStageMaster l1Master = new LevelOneStageMaster();

				l3Master = level3MasterService.getLevelThreeMasterByLevelId(leafId);
				l2Master = level2MasterService.getLevelTwoMasterByLevelId(l3Master.getLevelTwoRefernce());
				l1Master = level1MasterService.getLevelOneMasterByLevelId(l2Master.getLevelOneRefernce());

				msg = leafIdName.get(l2Master.getLevelOneRefernce()) + "/"
						+ leafIdName.get(l2Master.getLevelTwoStageId()) + "/" + leafIdName.get(leafId);

			}

			if (leafId.substring(0, 2).equalsIgnoreCase("L4")) {
				LevelFourStageMaster l4Master = new LevelFourStageMaster();
				LevelThreeStageMaster l3Master = new LevelThreeStageMaster();
				LevelTwoStageMaster l2Master = new LevelTwoStageMaster();
				LevelOneStageMaster l1Master = new LevelOneStageMaster();

				l4Master = level4MasterService.getLevelFourMasterBy(leafId);
				l3Master = level3MasterService.getLevelThreeMasterByLevelId(l4Master.getLevelThreeRefernce());
				l2Master = level2MasterService.getLevelTwoMasterByLevelId(l3Master.getLevelTwoRefernce());
				l1Master = level1MasterService.getLevelOneMasterByLevelId(l2Master.getLevelOneRefernce());

				msg = leafIdName.get(l2Master.getLevelOneRefernce()) + "/"
						+ leafIdName.get(l2Master.getLevelTwoStageId()) + "/"
						+ leafIdName.get(l3Master.getLevelThreeStageId()) + "/" + leafIdName.get(leafId);

			}

			if (leafId.substring(0, 2).equalsIgnoreCase("L5")) {
				LevelFiveStageMaster l5Master = new LevelFiveStageMaster();
				LevelFourStageMaster l4Master = new LevelFourStageMaster();
				LevelThreeStageMaster l3Master = new LevelThreeStageMaster();
				LevelTwoStageMaster l2Master = new LevelTwoStageMaster();
				LevelOneStageMaster l1Master = new LevelOneStageMaster();

				l5Master = level5MasterService.getLevelFiveMasterBy(leafId);
				l4Master = level4MasterService.getLevelFourMasterBy(l5Master.getLevelFourRefernce());
				l3Master = level3MasterService.getLevelThreeMasterByLevelId(l4Master.getLevelThreeRefernce());
				l2Master = level2MasterService.getLevelTwoMasterByLevelId(l3Master.getLevelTwoRefernce());
				l1Master = level1MasterService.getLevelOneMasterByLevelId(l2Master.getLevelOneRefernce());

				msg = leafIdName.get(l2Master.getLevelOneRefernce()) + "/"
						+ leafIdName.get(l2Master.getLevelTwoStageId()) + "/"
						+ leafIdName.get(l3Master.getLevelThreeStageId()) + "/"
						+ leafIdName.get(l4Master.getLevelFourStageId()) + "/" + leafIdName.get(leafId);

			}

		} catch (Exception ex) {

			Debug.printDebug("Errro On Fetching" + ex.getLocalizedMessage());
		}
		return msg;
	}

	// Trails Method Get Stages For Trails
	public List<String> getStagesMappingLeafIdForTrails(String uutId) {
		List<String> finalLeafIds = new ArrayList<String>();
		try {
			LevelOneMasterService levelOneService = new LevelOneMasterService();
			String levelId = "";
			StageLevelResponse stagelevelResponse = levelOneService.getLevelTOneMasterBySessionId(uutId, "ST4");

			List<LevelOneResponseDto> lst = (List<LevelOneResponseDto>) stagelevelResponse.getStageLevelList();
			
			//Filter the Advance Flag and Mandatory Flag

			lst = lst.stream().filter(filterObj -> filterObj.isAdvanceTestStatus()==false && filterObj.isDefaultStatus()==false )
					.collect(Collectors.toList());

			Map<String, List<String>> firstLevelIdFinLeaf = new HashMap<String, List<String>>();

			List<String> secondLeafParent = new ArrayList<String>();
			List<String> thirdLeafParent = new ArrayList<String>();
			List<String> fourLeafParent = new ArrayList<String>();
			List<String> fiveLeafParent = new ArrayList<String>();

			for (LevelOneResponseDto levelOneResponseDto : lst) {
				if (!levelOneResponseDto.getNextLevel().equals("N")) {
					secondLeafParent.add(levelOneResponseDto.getLevelOneId());
				} else {
					finalLeafIds.add(levelOneResponseDto.getLevelOneId());
				}
			}

			Map<String, LevelTwoStageMaster> level2Map = new HashMap<String, LevelTwoStageMaster>();
			LevelTwoMasterService level2Service = new LevelTwoMasterService();
			level2Map = level2Service.getLevelTwoStageMasterMap();

			Map<String, LevelThreeStageMaster> level3Map = new HashMap<String, LevelThreeStageMaster>();
			LevelThreeService level3Service = new LevelThreeService();
			level3Map = level3Service.getLevelThreeStageMasterMap();

			Map<String, LevelFourStageMaster> level4Map = new HashMap<String, LevelFourStageMaster>();
			LevelFourMasterSevice level4Service = new LevelFourMasterSevice();
			level4Map = level4Service.getLevelFourStageMasterMap();

			Map<String, LevelFiveStageMaster> level5Map = new HashMap<String, LevelFiveStageMaster>();
			LevelFiveMasterService level5Service = new LevelFiveMasterService();
			level5Map = level5Service.getLevelFiveStageMasterMap();

			// To Find the Final Leaf of Second And Find Next Level of Third Leaf

			List<LevelTwoStageMaster> levelTwoMasterList = new ArrayList<>(level2Map.values());

			if (secondLeafParent.size() > 0) {
				for (String id : secondLeafParent) {
					List<LevelTwoStageMaster> levelTwoMasterListnew = levelTwoMasterList.stream()
							.filter(lvl2 -> lvl2.getLevelOneRefernce().equals(id)).collect(Collectors.toList());
					for (LevelTwoStageMaster levelTwoStageMaster : levelTwoMasterListnew) {
						if (!levelTwoStageMaster.getNextLevel().equals("Y")) {
							finalLeafIds.add(levelTwoStageMaster.getLevelTwoStageId());
						} else {
							thirdLeafParent.add(levelTwoStageMaster.getLevelTwoStageId());
						}
					}
				}
			}

			List<LevelThreeStageMaster> levelThreeMasterList = new ArrayList<>(level3Map.values());
			if (thirdLeafParent.size() > 0) {
				for (String id : thirdLeafParent) {
					List<LevelThreeStageMaster> levelThreeMasterFilterList = levelThreeMasterList.stream()
							.filter(lvl3 -> lvl3.getLevelTwoRefernce().equals(id)).collect(Collectors.toList());
					for (LevelThreeStageMaster levelThreeStageMaster : levelThreeMasterFilterList) {
						if (!levelThreeStageMaster.getNextLevel().equals("Y")) {
							finalLeafIds.add(levelThreeStageMaster.getLevelThreeStageId());
						} else {
							fourLeafParent.add(levelThreeStageMaster.getLevelThreeStageId());
						}
					}
				}
			}

			List<LevelFourStageMaster> levelFourMasterList = new ArrayList<>(level4Map.values());
			if (fourLeafParent.size() > 0) {
				for (String id : fourLeafParent) {
					List<LevelFourStageMaster> levelFourMasterFilterList = levelFourMasterList.stream()
							.filter(lvl4 -> lvl4.getLevelThreeRefernce().equals(id)).collect(Collectors.toList());
					for (LevelFourStageMaster levelFourStageMaster : levelFourMasterFilterList) {
						if (!levelFourStageMaster.getNextLevel().equals("Y")) {
							finalLeafIds.add(levelFourStageMaster.getLevelFourStageId());
						} else {
							fiveLeafParent.add(levelFourStageMaster.getLevelFourStageId());
						}
					}
				}
			}

			List<LevelFiveStageMaster> levelFiveMasterList = new ArrayList<>(level5Map.values());
			if (fiveLeafParent.size() > 0) {
				for (String id : fiveLeafParent) {
					List<LevelFiveStageMaster> levelFiveMasterFilterList = levelFiveMasterList.stream()
							.filter(lvl5 -> lvl5.getLevelFourRefernce().equals(id)).collect(Collectors.toList());
					for (LevelFiveStageMaster levelFiveStageMaster : levelFiveMasterFilterList) {
						finalLeafIds.add(levelFiveStageMaster.getLevelFiveStageId());

					}
				}
			}

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return finalLeafIds;
	}

	// Trails Method
	public boolean isActiveTrailsPresent() {
		boolean isActive = false;
		try {
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			TrailSessionResponse trailResponse = trailSessionEntityService.getActiveTrailSessionId();
			if (trailResponse.getListOfSession().size() > 0) {
				isActive = true;
			}
		} catch (Exception ex) {
		}

		return isActive;
	}

	// Trails Method
	public List<SessionToStagesMappingDTO> getAllTrailStageLevelData(String uutId) {

		List<SessionToStagesMappingDTO> listOfSessionToStagesMappingDTO = new ArrayList<>();
		try {

			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
			// Here We Get All Stages Configured With Trail Sessions...
			StageLevelResponse stageLevelResponse = levelOneMasterService.getLevelTOneMasterBySessionId(uutId, "ST4");

			List<LevelOneResponseDto> levelOneList = (List<LevelOneResponseDto>) stageLevelResponse.getStageLevelList();
			List<LevelOneResponseDto> listOflevelOneStsge = new ArrayList<>();

			for (LevelOneResponseDto levelOneId : levelOneList) {
				listOflevelOneStsge.add(levelOneId);
			}
			// Level One : Check level One have Filtered Data.
			if (listOflevelOneStsge.size() != 0) {

				LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
				Map<String, List<LevelTwoStageMaster>> levelTwoMap = levelTwoService.getListOfEntityWithParentId();

				LevelThreeService levelThreeService = new LevelThreeService();
				Map<String, List<LevelThreeStageMaster>> levelThreeMap = levelThreeService
						.getListOfEntityWithParentId();

				LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
				Map<String, List<LevelFourStageMaster>> levelFourMap = levelFourService.getListOfEntityWithParentId();

				LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
				Map<String, List<LevelFiveStageMaster>> levelFiveMap = levelFiveService.getListOfEntityWithParentId();

				// Level One
				for (LevelOneResponseDto levelOneStage : listOflevelOneStsge) {
					// Next Level : N
					if (levelOneStage.getNextLevel().equals("N")) {
						listOfSessionToStagesMappingDTO
								.add(objCreation(levelOneStage.getLevelOneId(), null, null, null, null, null));
					}
					// Next Level : Y
					else if (levelOneStage.getNextLevel().equals("Y")) {

						// Level Two : Check level Two have Level One parent Id.
						if (levelTwoMap.get(levelOneStage.getLevelOneId()) != null) {
							List<LevelTwoStageMaster> listOfLevelTwoStage = levelTwoMap
									.get(levelOneStage.getLevelOneId());
							for (LevelTwoStageMaster levelTwoStage : listOfLevelTwoStage) {
								// Next Level : N
								if (levelTwoStage.getNextLevel().equals("N")) {
									listOfSessionToStagesMappingDTO.add(objCreation(levelOneStage.getLevelOneId(),
											levelTwoStage.getLevelTwoStageId(), null, null, null,
											levelTwoStage.getTestTypeId()));
								}
								// Next Level : Y
								else if (levelTwoStage.getNextLevel().equals("Y")) {
									// Level Three : Check level Three have Level Two parent Id.
									if (levelThreeMap.get(levelTwoStage.getLevelTwoStageId()) != null) {

										List<LevelThreeStageMaster> levelThreelist = levelThreeMap
												.get(levelTwoStage.getLevelTwoStageId());

										for (LevelThreeStageMaster l3 : levelThreelist) {
											// Next Level : N
											if (l3.getNextLevel().equals("N")) {
												listOfSessionToStagesMappingDTO.add(objCreation(
														levelOneStage.getLevelOneId(),
														levelTwoStage.getLevelTwoStageId(), l3.getLevelThreeStageId(),
														null, null, l3.getTestTypeId()));
											}
											// Next Level : Y
											else if (l3.getNextLevel().equals("Y")) {
												// Level Four : Check level Four have Level Three parent Id.
												if (levelFourMap.get(l3.getLevelThreeStageId()) != null) {
													List<LevelFourStageMaster> l4stage = levelFourMap
															.get(l3.getLevelThreeStageId());

													for (LevelFourStageMaster l4 : l4stage) {
														// Next Level : N
														if (l4.getNextLevel().equals("N")) {
															listOfSessionToStagesMappingDTO.add(objCreation(
																	levelOneStage.getLevelOneId(),
																	levelTwoStage.getLevelTwoStageId(),
																	l3.getLevelThreeStageId(), l4.getLevelFourStageId(),
																	null, l4.getTestTypeId()));

														}
														// Next Level : Y
														else if (l4.getNextLevel().equals("Y")) {
															// Level Five : Check level Five have Level Four parent Id.
															if (levelFiveMap.get(l4.getLevelFourStageId()) != null) {
																List<LevelFiveStageMaster> l5stage = levelFiveMap
																		.get(l4.getLevelFourStageId());

																for (LevelFiveStageMaster l5 : l5stage) {
																	// Next Level : N
																	if (l5.getNextLevel().equals("N")) {
																		listOfSessionToStagesMappingDTO.add(objCreation(
																				levelOneStage.getLevelOneId(),
																				levelTwoStage.getLevelTwoStageId(),
																				l3.getLevelThreeStageId(),
																				l4.getLevelFourStageId(),

																				l5.getLevelFiveStageId(),
																				l5.getTestTypeId()));

																	}
																}

															} // Level Five
														}
													}
												} // Level Four
											}
										}
									} // Level Three

								}
							}
						} // Level Two
					}

				}
			} // Level One

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return listOfSessionToStagesMappingDTO;
	}

	// ADD SESSION END TIME
	public Response endSession(String endRemarks) {
		Response res = new Response();

		try {
			AitessProcessControlManagement.getInstance().endAllProcessOnLogout();
			String sessionId = currentSessionDetails.getSessionId();
			if (sessionId.substring(0, 4).equals("TSSN")) {
				TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
				res = trailSessionEntityService.updateStatus(sessionId, endRemarks);
			} else {
				SessionService sessionService = new SessionService();
				GetObjResponse s = sessionService.getSessionDetailBySessionStageId(sessionId);
				if (s.getResponse().getResponseCode() == 0) {
					return s.getResponse();
				}
				SessionEntity ss = (SessionEntity) s.getObject();
				Date utilDate = new Date();
				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				ss.setEndDate(sqlDate);
				ss.setEndRemarks(endRemarks);
				res = sessionService.updateSession(ss);
			}

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Failed to End Session " + e.getLocalizedMessage());
		}
		return res;

	}

	// STAGES REMARKS
	// UPDATE
	public StagesRemarksResponse updateStagesRemarks(List<StagesRemarksDto> stagesRemarksDto) {
		StagesRemarksService service = new StagesRemarksService();

		List<StagesRemarks> remarkList = new ArrayList<>();
		for (StagesRemarksDto newRemark : stagesRemarksDto) {
			StagesRemarks stagesRemarks = new StagesRemarks();
			stagesRemarks.setLevelOneStageId(newRemark.getLevelOneStageId());
			stagesRemarks.setRemarks(newRemark.getRemarks());
			stagesRemarks.setSessionId(newRemark.getSessionId());
			remarkList.add(stagesRemarks);
		}
		StagesRemarksResponse serviceResponse = service.updateStagesRemarks(remarkList);
		return serviceResponse;
	}
	
	//SAVE
	public void addStagesRemarks(Set<String> setOfL1Ids) {
		StagesRemarksService service = new StagesRemarksService();
		for (String s : setOfL1Ids) {
			StagesRemarks stagesRemarks = new StagesRemarks();
			stagesRemarks.setLevelOneStageId(s);
			stagesRemarks.setSessionId(currentSessionDetails.getSessionId());
			stagesRemarks.setReportType("ESS");
			service.addOrUpdateStagesRemarks(stagesRemarks);
		}
	}
	
	// GET
	public StageRemarksResponse getStagesRemarks(String sessionId, String reportType) {
		StagesRemarksService service = new StagesRemarksService();
		StagesRemarksResponse serviceResponse = service.getStagesRemarks(sessionId, reportType);
		StageRemarksResponse response = new StageRemarksResponse();

		List<StagesRemarksDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<StagesRemarks> remarksList = serviceResponse.getRemarks();
			LevelOneMasterService levelOneService = new LevelOneMasterService();
			Map<String, String> levelOneStage = levelOneService.getAllLevelOneIdAndLevelName();

			for (StagesRemarks remarks : remarksList) {
				StagesRemarksDto dto = new StagesRemarksDto();
				dto.setRemarksId(remarks.getRemarksId());
				dto.setLevelOneStageId(remarks.getLevelOneStageId());
				dto.setLevelOneName(levelOneStage.get(remarks.getLevelOneStageId()));
				dto.setRemarks(remarks.getRemarks());
				dto.setSessionId(remarks.getSessionId());
				dto.setReportType(remarks.getReportType());

				dtoList.add(dto);
				response.setResponseCode(1);
				response.setResponseMsg("SUCCESS");
				response.setRemarks(dtoList);
			}
		} else {
			response.setResponseCode(0);
			response.setResponseMsg("FAILED TO FETCH");
			response.setRemarks(null);
			System.err.println("Failed to fetch stages remarks Details: " + serviceResponse.getResponseMessage());
		}

		return response;
	}
	
	//  BASED ON ROLE ID FETCH SESSION DATA
	public SessionListResponse getAllSessionDataByRoleId(String roleId) {
		SessionListResponse sessionListResponse = new SessionListResponse();
		Response res = new Response();
		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(roleId);

			// To Picking Trail Sessions...
			TrailSessionResponse trailSessionResponse = new TrailSessionResponse();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			trailSessionResponse = trailSessionEntityService.getActiveTrailSessionId();
			List<SessionList> listOfSession = new ArrayList<>();

			List<TrailSessionDto> trailActiveSession = new ArrayList<TrailSessionDto>();
			trailActiveSession = trailSessionResponse.getListOfSession();
			for (TrailSessionDto trailSessionDto : trailActiveSession) {
				SessionList sessionList = new SessionList();
				sessionList.setSessionId(trailSessionDto.getSessionId());
				sessionList.setSessionName(trailSessionDto.getSessionName());
				sessionList.setCreationDate(trailSessionDto.getCreationDate());
				listOfSession.add(sessionList);

			}

			// For Picking Others Sessions...
			SessionService sessionSelectedStage = new SessionService();
			GetResponse getResponse = sessionSelectedStage.getAllSessionDataWithDescOrder();

			if (getResponse.getCode() == 0) {
				if (listOfSession.size() > 0) {
					res.setResponseCode(1);
					res.setResponseMessage(getResponse.geteMsg() + "  Error On Session Enity....");
					sessionListResponse.setResponse(res);
					sessionListResponse.setListOfSession(listOfSession);
					return sessionListResponse;

				}
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				sessionListResponse.setResponse(res);
				return sessionListResponse;
			}

			for (Object object : getResponse.getResponseList()) {
				SessionEntity sessionEntity = (SessionEntity) object;
				
				if (setOfUserLoginId != null && setOfUserLoginId.contains(sessionEntity.getUserId())) {

					SessionList sessionList = new SessionList();
					sessionList.setSessionId(sessionEntity.getSessionId());
					sessionList.setSessionName(sessionEntity.getSessionName());
					sessionList.setCreationDate(sessionEntity.getCreationDate());
					sessionList.setOfpConfigId(sessionEntity.getOfpConfigId());
					listOfSession.add(sessionList);
				}

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


	// SESSION STAGE MAPPING : RETURNING A LEVEL IDs WITH THERE TEST STATUS
	public SessionStageMapResponse getAllSessionStage_IdsWithResult(String sessionEntityId) {
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
			Set<String> levelOneIds = new LinkedHashSet<>();
			Map<String, Set<String>> levelTwoIdsWithParantId = new LinkedHashMap<>(); // Example- Key : L1_018 , Value: [L2_219, L2_220, L2_221]
			Map<String, Set<String>> levelThreeIdsWithParantId = new LinkedHashMap<>();// Example- Key : L2_221 , Value:  [L3_160, L3_161]
			Map<String, Set<String>> levelfourIdsWithParantId = new LinkedHashMap<>();// Example- Key : L3_178  , Value:  [L4_207]
			Map<String, Set<String>> levelfiveIdsWithParantId = new LinkedHashMap<>();// Example- Key : L4_203  , Value:  [L5_050]
			Map<String, String> leafStageResult = new LinkedHashMap<>();// Example- Key : L2_050  , Value:  pending

			
			for (Object object : getResponse.getResponseList()) {
				
				SessionStagesMapping sessionStage = (SessionStagesMapping) object;
				
				// Add L1 IDs
				levelOneIds.add(sessionStage.getLevelOneStageId());
				
				// Add L2 IDs
				if (sessionStage.getLevelTwoStageId() != null) {
					if (levelTwoIdsWithParantId.get(sessionStage.getLevelOneStageId()) != null) {
						if (!levelTwoIdsWithParantId.get(sessionStage.getLevelOneStageId())
								.contains(sessionStage.getLevelTwoStageId())) {
							levelTwoIdsWithParantId.get(sessionStage.getLevelOneStageId())
									.add(sessionStage.getLevelTwoStageId());
						}
					} else {
						Set<String> levelTwoIds = new LinkedHashSet<>();
						levelTwoIds.add(sessionStage.getLevelTwoStageId());
						levelTwoIdsWithParantId.put(sessionStage.getLevelOneStageId(), levelTwoIds);
					}

				} else {
					leafStageResult.put(sessionStage.getLevelOneStageId(), sessionStage.getStatus().toLowerCase().contains(COMPLETED.toLowerCase())? COMPLETED:PENDING);
				}
				
				// Add L3 IDs
				if (sessionStage.getLevelThreeStageId() != null) {
					if (levelThreeIdsWithParantId.get(sessionStage.getLevelTwoStageId()) != null) {
						if (!levelThreeIdsWithParantId.get(sessionStage.getLevelTwoStageId())
								.contains(sessionStage.getLevelThreeStageId())) {
							levelThreeIdsWithParantId.get(sessionStage.getLevelTwoStageId())
									.add(sessionStage.getLevelThreeStageId());
						}
					} else {
						Set<String> levelThreeIds = new LinkedHashSet<>();
						levelThreeIds.add(sessionStage.getLevelThreeStageId());
						levelThreeIdsWithParantId.put(sessionStage.getLevelTwoStageId(), levelThreeIds);
					}

				} else {
					leafStageResult.put(sessionStage.getLevelTwoStageId(), sessionStage.getStatus().toLowerCase().contains(COMPLETED.toLowerCase())?COMPLETED:PENDING);
				}

				// Add L4 IDs
				if (sessionStage.getLevelFourStageId() != null) {
					if (levelfourIdsWithParantId.get(sessionStage.getLevelThreeStageId()) != null) {
						if (!levelfourIdsWithParantId.get(sessionStage.getLevelThreeStageId())
								.contains(sessionStage.getLevelFourStageId())) {
							levelfourIdsWithParantId.get(sessionStage.getLevelThreeStageId())
									.add(sessionStage.getLevelFourStageId());
						}
					} else {
						Set<String> levelFourIds = new LinkedHashSet<>();
						levelFourIds.add(sessionStage.getLevelFourStageId());
						levelfourIdsWithParantId.put(sessionStage.getLevelThreeStageId(), levelFourIds);
					}

				} else {
					leafStageResult.put(sessionStage.getLevelThreeStageId(), sessionStage.getStatus().toLowerCase().contains(COMPLETED.toLowerCase())?COMPLETED:PENDING);
				}

				// Add L5 IDs
				if (sessionStage.getLevelFiveStageId() != null) {
					if (levelfiveIdsWithParantId.get(sessionStage.getLevelFourStageId()) != null) {
						if (!levelfiveIdsWithParantId.get(sessionStage.getLevelFourStageId())
								.contains(sessionStage.getLevelFiveStageId())) {
							levelfiveIdsWithParantId.get(sessionStage.getLevelFourStageId())
									.add(sessionStage.getLevelFiveStageId());
						}
					} else {
						Set<String> levelFiveIds = new LinkedHashSet<>();
						levelFiveIds.add(sessionStage.getLevelFiveStageId());
						levelfiveIdsWithParantId.put(sessionStage.getLevelFourStageId(), levelFiveIds);
					}
					leafStageResult.put(sessionStage.getLevelFiveStageId(), sessionStage.getStatus().toLowerCase().contains(COMPLETED.toLowerCase())? COMPLETED:PENDING);
				} else {
					leafStageResult.put(sessionStage.getLevelFourStageId(), sessionStage.getStatus().toLowerCase().contains(COMPLETED.toLowerCase())? COMPLETED:PENDING);
				}

			}


			List<StageObject> listOfStageObject = new ArrayList<>();
			// L1 Start 
			for (String levelOneId : levelOneIds) {
				StageObject stageObject = new StageObject();
				if (levelTwoIdsWithParantId.get(levelOneId) != null) {
					String l1Status = PENDING;
					boolean pending1 = false;
					boolean completed1 = false;
					
					// L2 Start 
					for (String levelTwo : levelTwoIdsWithParantId.get(levelOneId)) {
						StageObject stageObjectL2 = new StageObject();
						if (levelThreeIdsWithParantId.get(levelTwo) != null) {
							String l2Status = PENDING;
							boolean pending2 = false;
							boolean completed2 = false;
							
							// L3 Start 
							for (String levelThreeId : levelThreeIdsWithParantId.get(levelTwo)) {
								StageObject stageObjectL3 = new StageObject();
								if (levelfourIdsWithParantId.get(levelThreeId) != null) {
									String l3Status = PENDING;
									boolean pending3 = false;
									boolean completed3 = false;
									
									// L4 Start 
									for (String levelFourId : levelfourIdsWithParantId.get(levelThreeId)) {
										StageObject stageObjectL4 = new StageObject();
										if (levelfiveIdsWithParantId.get(levelFourId) != null) {
											String l4Status = PENDING;
											boolean pending4 = false;
											boolean completed4 = false;
											
											// L5 Start 
											for (String levelFiveId : levelfiveIdsWithParantId.get(levelFourId)) {
												StageObject stageObjectL5 = new StageObject();

												if (leafStageResult.get(levelFiveId).equals(PENDING)) {
													pending4 = true;
												} else if (leafStageResult.get(levelFiveId).toLowerCase()
														.startsWith(COMPLETED.toLowerCase())) {
													completed4 = true;
												}
												stageObjectL5 = stageObjCreation(levelOneId, levelTwo, levelThreeId,
														levelFourId, levelFiveId, leafStageResult.get(levelFiveId));
												listOfStageObject.add(stageObjectL5);
											}// L5 End 
											
											if (pending4 && completed4) {
												l4Status = PARTIAL;
											} else if (completed4) {
												l4Status = COMPLETED;
											}
											leafStageResult.put(levelFourId, l4Status);
											stageObjectL4 = stageObjCreation(levelOneId, levelTwo, levelThreeId,
													levelFourId, null, l4Status);
											pending4 = false;
											completed4 = false;
										} else {
											stageObjectL4 = stageObjCreation(levelOneId, levelTwo, levelThreeId,
													levelFourId, null, leafStageResult.get(levelFourId));
										}
										listOfStageObject.add(stageObjectL4);

										if (leafStageResult.get(levelFourId).equals(PENDING)) {
											pending3 = true;
										} else if (leafStageResult.get(levelFourId).toLowerCase()
												.startsWith(COMPLETED.toLowerCase())) {
											completed3 = true;
										} else if (leafStageResult.get(levelFourId).equals(PARTIAL)) {
											l3Status = PARTIAL;
										}
									}// L4 End 
									
									if (l3Status.equals(PARTIAL)) {
										l3Status = PARTIAL;
									} else if (pending3 && completed3) {
										l3Status = PARTIAL;
									} else if (completed3) {
										l3Status = COMPLETED;
									}
									leafStageResult.put(levelThreeId, l3Status);
									stageObjectL3 = stageObjCreation(levelOneId, levelTwo, levelThreeId, null, null,
											leafStageResult.get(levelThreeId));
									pending3 = false;
									completed3 = false;
								} else {
									stageObjectL3 = stageObjCreation(levelOneId, levelTwo, levelThreeId, null, null,
											leafStageResult.get(levelThreeId));
								}
								listOfStageObject.add(stageObjectL3);

								if (leafStageResult.get(levelThreeId).equals(PENDING)) {
									pending2 = true;
								} else if (leafStageResult.get(levelThreeId).toLowerCase().startsWith(COMPLETED.toLowerCase())) {
									completed2 = true;
								} else if (leafStageResult.get(levelThreeId).equals(PARTIAL)) {
									l2Status = PARTIAL;
								}
							}// L3 End 
							
							if (l2Status.equals(PARTIAL)) {
								l2Status = PARTIAL;
							} else if (pending2 && completed2) {
								l2Status = PARTIAL;
							} else if (completed2) {
								l2Status = COMPLETED;
							}
							leafStageResult.put(levelTwo, l2Status);
							stageObjectL2 = stageObjCreation(levelOneId, levelTwo, null, null, null,
									leafStageResult.get(levelTwo));
							pending2 = false;
							completed2 = false;
						} else {
							stageObjectL2 = stageObjCreation(levelOneId, levelTwo, null, null, null,
									leafStageResult.get(levelTwo));
						}
						listOfStageObject.add(stageObjectL2);
						if (leafStageResult.get(levelTwo).equals(PENDING)) {
							pending1 = true;
						} else if (leafStageResult.get(levelTwo).toLowerCase().startsWith(COMPLETED.toLowerCase())) {
							completed1 = true;
						} else if (leafStageResult.get(levelTwo).equals(PARTIAL)) {
							l1Status = PARTIAL;
						}
					}// L2 End 
					
					if (l1Status.equals(PARTIAL)) {
						l1Status = PARTIAL;
					} else if (pending1 && completed1) {
						l1Status = PARTIAL;
					} else if (completed1) {
						l1Status = COMPLETED;
					}
					leafStageResult.put(levelOneId, l1Status);
					stageObject = stageObjCreation(levelOneId, null, null, null, null, leafStageResult.get(levelOneId));
					pending1 = false;
					completed1 = false;
				} else {
					stageObject = stageObjCreation(levelOneId, null, null, null, null, leafStageResult.get(levelOneId));
				}
				listOfStageObject.add(stageObject);
			}// L1 End 
			
			sessionStageMapResponse.setListOfStageObject(listOfStageObject);
			res.setResponseCode(1);
			res.setResponseMessage("Fetch Data Successfull ");
			sessionStageMapResponse.setResponse(res);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessfull ");
			e.printStackTrace();
			sessionStageMapResponse.setResponse(res);
		}
		return sessionStageMapResponse;
	}

	private StageObject stageObjCreation(String l1, String l2, String l3, String l4, String l5,
			String status) {

		StageObject stageObj = new StageObject();
		stageObj.setL1StageId(l1);
		stageObj.setL2StageId(l2);
		stageObj.setL3StageId(l3);
		stageObj.setL4StageId(l4);
		stageObj.setL5StageId(l5);
		stageObj.setStatus(status);

		return stageObj;

	}
}