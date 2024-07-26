package com.teclever.dfcc.datastore.sessionmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.LevelOneResponseDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.entities.LevelFiveStageMaster;
import com.teclever.datastore.entities.LevelFourStageMaster;
import com.teclever.datastore.entities.LevelOneStageMaster;
import com.teclever.datastore.entities.LevelThreeStageMaster;
import com.teclever.datastore.entities.LevelTwoStageMaster;
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
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionDTOResponse;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class SessionManagement {
	List<SessionToStagesMappingDTO> sessionStages = new ArrayList<>();

	// SESSION ENTITY : SAVE SESSION
	public Response saveSession(SessionDTO sessionDTO) {
		Response res = new Response();
		try {
			SessionService sessionService = new SessionService();
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
			sessionPath = sessionPath + File.separator + uutIdName.get(sessionDTO.getUutId()) + File.separator
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

			sessionStages.addAll(getAdvanceTestSubLevelData(sessionDTO.getUutId()));

			LevelOneMasterService levelOneService = new LevelOneMasterService();
			Map<String, String> levelOneStage = levelOneService.getAllLevelOneIdAndLevelName();

			// Map<String, LevelOneStageMaster> levelOneStageWithObject =
			// levelOneService.getAllLevelOneWithId();

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
			for (SessionToStagesMappingDTO s : dbSessionStages) {
				System.out.println("path" + s.getPath());
			}

			//

			List<SessionStagesMapping> sessionToStagesMappingList = new ArrayList<SessionStagesMapping>();
//			/
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

			LoginSessionService loginSessionService = new LoginSessionService();

			// LOGIN SESSION DETAILS : UPDATE (SESSION ID)

			loginSessionService.updateLoginSession(currentSessionDetails.getLoginSessionId(), sessionId, null);

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
			SessionSelectedStagesService sessionSelectedStage = new SessionSelectedStagesService();
			GetResponse getResponse = sessionSelectedStage.getAllSessionData(userId);
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

	public SessionDTOResponse getSessionDetailById(String sessionEntityId) {
		SessionDTOResponse sessionDtoResponse = new SessionDTOResponse();
		Response res = new Response();
		try {
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

			sessionDtoResponse.setSessionStagesList(sessionSrageResponse.getListOfStageObject());

			FaultCodeSessionMappingService faultCodeSessionMap = new FaultCodeSessionMappingService();
			List<String> ListOfFaultCodeIds = faultCodeSessionMap.getFaultCodeBySessionId(sessionEntityId);
			if (ListOfFaultCodeIds != null && ListOfFaultCodeIds.size() > 0) {
				List<FaultCodeDTO> faultCodeMappingList = new ArrayList<>();

				FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
				FaultCodeResponse faultCodeResponse = faultCodeConfiguration.getFaultCodeList();
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

	public Response updateLoginSession(String input) {
		Response res = new Response();
		try {
			LoginSessionService loginSessionService = new LoginSessionService();
			if (input != null && input.equals("LOGOUT")) {
				res = loginSessionService.updateLoginSession(currentSessionDetails.getLoginSessionId(), null,
						new Date());
			} else {
				res = loginSessionService.updateLoginSession(currentSessionDetails.getLoginSessionId(),
						currentSessionDetails.getSessionId(), null);
			}

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Update Login Session Unsuccessful ");
		}
		return res;
	}

	private List<SessionToStagesMappingDTO> getAdvanceTestSubLevelData(String uutId) {

		List<SessionToStagesMappingDTO> listOfSessionToStagesMappingDTO = new ArrayList<>();
		try {
			String advanceLevelOne = null;
			String advanceLevelTwo = null;
			String lruLevelOneId = null;
			String lruLevelTwoId = null;
			List<String> advaceLevelList = new ArrayList<>();

			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
			List<LevelOneResponseDto> levelOneList = levelOneMasterService.getLevelOneByUUTId(uutId);

			for (LevelOneResponseDto levelOneId : levelOneList) {
				if (levelOneId.isAdvanceTestStatus()) {
					advanceLevelOne = levelOneId.getLevelOneId();
				}
				if (levelOneId.getStageName().startsWith("LRU")) {
					lruLevelOneId = levelOneId.getLevelOneId();
				}
			}

			LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
			Map<String, List<LevelTwoStageMaster>> levelTwoMap = levelTwoService.getListOfEntityWithParentId();

			List<LevelTwoStageMaster> advanceLevelList = levelTwoMap.get(advanceLevelOne);
			for (LevelTwoStageMaster l2 : advanceLevelList) {
				if (l2.getStageName().equalsIgnoreCase("Interface")) {
					advanceLevelTwo = l2.getLevelTwoStageId();

				} else {

					if (l2.getNextLevel().equals("N")) {
						listOfSessionToStagesMappingDTO.add(
								objCreation(advanceLevelOne, advanceLevelTwo, null, null, null, l2.getTestTypeId()));
					} else if (l2.getNextLevel().equals("Y")) {

						advaceLevelList.add(l2.getLevelTwoStageId());
					}
				}

			}
			List<LevelTwoStageMaster> lruLevellist = levelTwoMap.get(lruLevelOneId);
			for (LevelTwoStageMaster l2 : lruLevellist) {
				if (l2.getStageName().startsWith("SRU")) {
					lruLevelTwoId = l2.getLevelTwoStageId();
				}
			}

//			System.out.println("advanceLevelOne  " + advanceLevelOne + "   lruLevelOneId " + lruLevelOneId);
//			System.out.println("advanceLevelTwo  " + advanceLevelTwo + "   lruLevelTwoId " + lruLevelTwoId);

			LevelThreeService levelThreeService = new LevelThreeService();
			Map<String, List<LevelThreeStageMaster>> levelThreeMap = levelThreeService.getListOfEntityWithParentId();

			LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
			Map<String, List<LevelFourStageMaster>> levelFourMap = levelFourService.getListOfEntityWithParentId();

			LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
			Map<String, List<LevelFiveStageMaster>> levelFiveMap = levelFiveService.getListOfEntityWithParentId();

			listOfSessionToStagesMappingDTO.addAll(getSubStagesIdsWithTestType(lruLevelTwoId, advanceLevelOne,
					advanceLevelTwo, levelThreeMap, levelFourMap, levelFiveMap));

			for (String levelTwoIds : advaceLevelList) {
				listOfSessionToStagesMappingDTO.addAll(getSubStagesIdsWithTestType(levelTwoIds, advanceLevelOne,
						advanceLevelTwo, levelThreeMap, levelFourMap, levelFiveMap));

			}

		} catch (Exception e) {
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

	private List<SessionToStagesMappingDTO> getSubStagesIdsWithTestType(String lruLevelTwoId, String advanceLevelOne,
			String advanceLevelTwo, Map<String, List<LevelThreeStageMaster>> levelThreeMap,
			Map<String, List<LevelFourStageMaster>> levelFourMap,
			Map<String, List<LevelFiveStageMaster>> levelFiveMap) {
		List<SessionToStagesMappingDTO> listOfSessionToStagesMappingDTO = new ArrayList<>();
		try {
			// IF (1)- Checking LevelThree Have LevekTwo Id as parent Id
			if (levelThreeMap.get(lruLevelTwoId) != null) {

				List<LevelThreeStageMaster> levelThreelist = levelThreeMap.get(lruLevelTwoId);
				// FOR (1)
				for (LevelThreeStageMaster l3 : levelThreelist) {

					if (l3.getNextLevel().equals("N")) {
						listOfSessionToStagesMappingDTO.add(objCreation(advanceLevelOne, advanceLevelTwo,
								l3.getLevelThreeStageId(), null, null, l3.getTestTypeId()));
					} else if (l3.getNextLevel().equals("Y")) {
						// IF (2)
						if (levelFourMap.get(l3.getLevelThreeStageId()) != null) {
							List<LevelFourStageMaster> l4stage = levelFourMap.get(l3.getLevelThreeStageId());
							// FOR (2)
							for (LevelFourStageMaster l4 : l4stage) {
								if (l4.getNextLevel().equals("N")) {
									listOfSessionToStagesMappingDTO.add(
											objCreation(advanceLevelOne, advanceLevelTwo, l3.getLevelThreeStageId(),
													l4.getLevelFourStageId(), null, l4.getTestTypeId()));

								} else if (l4.getNextLevel().equals("Y")) {
									// IF (3)
									if (levelFiveMap.get(l4.getLevelFourStageId()) != null) {
										List<LevelFiveStageMaster> l5stage = levelFiveMap.get(l4.getLevelFourStageId());
										// FOR (3)
										for (LevelFiveStageMaster l5 : l5stage) {
											if (l4.getNextLevel().equals("N")) {
												listOfSessionToStagesMappingDTO
														.add(objCreation(advanceLevelOne, advanceLevelTwo,
																l3.getLevelThreeStageId(), l4.getLevelFourStageId(),
																l5.getLevelFiveStageId(), l5.getTestTypeId()));

											}
										} // For (3)

									} // IF (3)
								}
							} // FOR (2)
						} // IF (2)
					}
				} // For (1)
			} // IF (1)
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return listOfSessionToStagesMappingDTO;
	}
}
