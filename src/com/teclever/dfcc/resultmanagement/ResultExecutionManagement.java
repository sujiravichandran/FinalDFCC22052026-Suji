package com.teclever.dfcc.resultmanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.LevelFiveStageMaster;
import com.teclever.datastore.entities.LevelFourStageMaster;
import com.teclever.datastore.entities.LevelOneStageMaster;
import com.teclever.datastore.entities.LevelThreeStageMaster;
import com.teclever.datastore.entities.LevelTwoStageMaster;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesSelectedTestFiles;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.entities.UserLoginDetails;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.datastore.service.UserLoginDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.ReportDetails;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsResponse;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.resultstore.dto.ResultDto;
import com.teclever.dfcc.resultstore.resultmanagement.ResultManagement;

public class ResultExecutionManagement {

	// For Getting the List Of ExecutionFiles...For Last Stage For Current SessionId
	public ResultExecutionResponse getResultExecutionListBriefListForStages(String sessionId) {
		ResultExecutionResponse response = new ResultExecutionResponse();

		try {
			SessionService sessionService = new SessionService();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; }
			 * 
			 * 
			 * SessionDto sessionDto = sessionEntityList.get(sessionEntityList.size() - 1);
			 * String sessionId = sessionDto.getSessionId(); String sessionName =
			 * sessionDto.getSessionName(); response.setSessionName(sessionName);
			 */
			String sessionName = "";
			GetObjResponse sessionRes = new GetObjResponse();
			if (!sessionId.substring(0, 4).equals("TSSN")) {
				sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
				SessionEntity sessionEntity = new SessionEntity();
				sessionEntity = (SessionEntity) sessionRes.getObject();
				sessionName = sessionEntity.getSessionName();
				response.setSessionName(sessionName);
			} else {
				sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
				TrailSessionEntity trailSessionEntity = new TrailSessionEntity();
				trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
				sessionName = trailSessionEntity.getTrailSessionName();
				response.setSessionName(sessionName);
			}

			if (sessionRes.getResponse().getResponseCode() != 1) {
				response.setMsg("Problem On Fetching SessionDetails  " + "Exception Msg:"
						+ sessionRes.getResponse().getResponseMessage());
			}

			System.out.println("Code " + sessionRes.getResponse().getResponseCode());
			System.out.println("sessionId" + sessionId);

			System.out.println("getResultExecutionListBriefListForStages");
			response.setSessionName(sessionName);
			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();

			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
			if (lst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}
			SessionStagesTestFilesResult s = lst.get(lst.size() - 1);
			String lastStageId = s.getStageId();

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObjRes = sessionSelectedStagesService.getSessionStagesMapp(sessionId, lastStageId);
			SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
			sessionStagesMapping = (SessionStagesMapping) getObjRes.getObject();
			String sessionStagesMappingId = sessionStagesMapping.getSessionStagesMappingId();

			if (sessionStagesMappingId != null) {
				SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
				GetResponse getResponse = sessionStagesSelectedTestFilesService
						.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
				List<SessionStagesSelectedTestFiles> selectedTestFileInStages = new ArrayList<SessionStagesSelectedTestFiles>();
				selectedTestFileInStages = (List<SessionStagesSelectedTestFiles>) getResponse.getResponseList();
				if (selectedTestFileInStages != null) {
					for (SessionStagesSelectedTestFiles selectedTestFile : selectedTestFileInStages) {
						selectedTestFileIdTestFileId.put(selectedTestFile.getSessionStagesSelectedTestFilesId(),
								selectedTestFile.getTestFilesId());
					}
				}
			}

			lst = lst.stream().filter(filterObj -> filterObj.getStageId().equalsIgnoreCase(lastStageId))
					.collect(Collectors.toList());

			List<ResultExecutionDTO> resultList = new ArrayList();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : lst) {
				ResultExecutionDTO resultExecutionDTO = new ResultExecutionDTO();
				resultExecutionDTO.setDStarCount(sessionStagesTestFilesResult.getdStarCount());
				resultExecutionDTO.setEndTime(sessionStagesTestFilesResult.getEndTime());
				resultExecutionDTO.setSessionName(sessionName);
				resultExecutionDTO.setRdfFile(sessionStagesTestFilesResult.getRdfFileName());
				resultExecutionDTO.setRdfFilePath(sessionStagesTestFilesResult.getRdfPath());
				resultExecutionDTO.setSystemInfoId(sessionStagesTestFilesResult.getSystemResultInfoId());
				if (sessionStagesTestFilesResult.getStageId() != null
						|| !sessionStagesTestFilesResult.getStageId().equals("")) {
					resultExecutionDTO.setStageId(sessionStagesTestFilesResult.getStageId());
					resultExecutionDTO.setStageName(stageIdName.get(sessionStagesTestFilesResult.getStageId()));

				}
				resultExecutionDTO.setTestFileId(
						selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId()));
				System.out.println(
						selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId()));
				resultExecutionDTO.setTestFileName(testFileIdName
						.get(selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId())));
				System.out.println("Test File Name" + testFileIdName
						.get(selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId())));
				resultExecutionDTO.setStatus(sessionStagesTestFilesResult.getTestStatus());
				resultList.add(resultExecutionDTO);

			}
			response.setStageId(lastStageId);
			response.setStageName(stageIdName.get(lastStageId));
			System.out.println("Result List Size" + resultList.size());
			response.setResultDTOList(resultList);
			response.setCode(1);
			response.setMsg("Fetched");

		} catch (Exception ex) {
			response.setCode(0);
			response.seteMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	// Result Execution Brief List For Selected Stages On Session
	public ResultExecutionResponse getResultExecutionListBriefListForSelectedStages(String sessionId, String stageId) {
		ResultExecutionResponse response = new ResultExecutionResponse();

		try {
			SessionService sessionService = new SessionService();
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; }
			 * 
			 * 
			 * SessionDto sessionDto = sessionEntityList.get(sessionEntityList.size() - 1);
			 * String sessionId = sessionDto.getSessionId(); String sessionName =
			 * sessionDto.getSessionName(); response.setSessionName(sessionName);
			 */

			String sessionName = "";
			GetObjResponse sessionRes = new GetObjResponse();
			if (!sessionId.substring(0, 4).equals("TSSN")) {
				sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
				SessionEntity sessionEntity = new SessionEntity();
				sessionEntity = (SessionEntity) sessionRes.getObject();
				sessionName = sessionEntity.getSessionName();
				response.setSessionName(sessionName);
			} else {
				sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
				TrailSessionEntity trailSessionEntity = new TrailSessionEntity();
				trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
				sessionName = trailSessionEntity.getTrailSessionName();
				response.setSessionName(sessionName);
			}
			if (sessionRes.getResponse().getResponseCode() != 1) {
				response.setMsg("Problem On Fetching SessionDetails  " + "Exception Msg:"
						+ sessionRes.getResponse().getResponseMessage());
			}
			System.out.println("Code " + sessionRes.getResponse().getResponseCode());
			System.out.println("sessionId" + sessionId);

			System.out.println("getResultExecutionListBriefListForSelectedStages");

			response.setSessionName(sessionName);
			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObjRes = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
			sessionStagesMapping = (SessionStagesMapping) getObjRes.getObject();
			String sessionStagesMappingId = sessionStagesMapping.getSessionStagesMappingId();
			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();

			if (sessionStagesMappingId != null) {
				SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
				GetResponse getResponse = sessionStagesSelectedTestFilesService
						.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
				List<SessionStagesSelectedTestFiles> selectedTestFileInStages = new ArrayList<SessionStagesSelectedTestFiles>();
				selectedTestFileInStages = (List<SessionStagesSelectedTestFiles>) getResponse.getResponseList();
				if (selectedTestFileInStages != null) {
					for (SessionStagesSelectedTestFiles selectedTestFile : selectedTestFileInStages) {
						selectedTestFileIdTestFileId.put(selectedTestFile.getSessionStagesSelectedTestFilesId(),
								selectedTestFile.getTestFilesId());
					}
				}
			}
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse resTestFiles = sessionStagesTestFilesResultService
					.getTestResultFileBySessionIdAndStageId(sessionId, stageId);
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
			if (lst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}

			List<ResultExecutionDTO> resultList = new ArrayList();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : lst) {
				ResultExecutionDTO resultExecutionDTO = new ResultExecutionDTO();
				resultExecutionDTO.setDStarCount(sessionStagesTestFilesResult.getdStarCount());
				resultExecutionDTO.setEndTime(sessionStagesTestFilesResult.getEndTime());
				resultExecutionDTO.setSessionName(sessionName);
				resultExecutionDTO.setRdfFile(sessionStagesTestFilesResult.getRdfFileName());
				resultExecutionDTO.setRdfFilePath(sessionStagesTestFilesResult.getRdfPath());
				resultExecutionDTO.setSystemInfoId(sessionStagesTestFilesResult.getSystemResultInfoId());
				if (sessionStagesTestFilesResult.getStageId() != null
						|| !sessionStagesTestFilesResult.getStageId().equals("")) {
					resultExecutionDTO.setStageId(sessionStagesTestFilesResult.getStageId());
					resultExecutionDTO.setStageName(stageIdName.get(sessionStagesTestFilesResult.getStageId()));

				}
				resultExecutionDTO.setTestFileId(
						selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId()));
				resultExecutionDTO.setTestFileName(testFileIdName
						.get(selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId())));
				resultExecutionDTO.setStatus(sessionStagesTestFilesResult.getTestStatus());
				resultList.add(resultExecutionDTO);

			}

			// Filtering For the StageId
			response.setStageId(stageId);
			response.setStageName(stageIdName.get(stageId));
			System.out.println("Result List Size" + resultList.size());
			response.setResultDTOList(resultList);
			response.setCode(1);
			response.setMsg("Fetched");

		} catch (Exception ex) {
			response.setCode(0);
			response.seteMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}
	
	
	// For Getting the List Of ExecutionFiles For Session...
		public ResultExecutionResponse getResultExecutionListBriefListForSession(String sessionId) {
			ResultExecutionResponse response = new ResultExecutionResponse();
			try {
				// Session Details
				SessionService sessionService = new SessionService();
				TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();

				String sessionName = "";
				GetObjResponse sessionRes = new GetObjResponse();
				if (!sessionId.substring(0, 4).equals("TSSN")) {
					sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
					SessionEntity sessionEntity = new SessionEntity();
					sessionEntity = (SessionEntity) sessionRes.getObject();
					sessionName = sessionEntity.getSessionName();
					response.setSessionName(sessionName);
				} else {
					sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
					TrailSessionEntity trailSessionEntity = new TrailSessionEntity();
					trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
					sessionName = trailSessionEntity.getTrailSessionName();
					response.setSessionName(sessionName);
				}

				if (sessionRes.getResponse().getResponseCode() != 1) {
					response.setMsg("Problem On Fetching SessionDetails  " + "Exception Msg:"
							+ sessionRes.getResponse().getResponseMessage());
				}
				System.out.println("Code " + sessionRes.getResponse().getResponseCode());
				System.out.println("sessionId" + sessionId);

				SessionEntity sessionEntity = new SessionEntity();
				sessionEntity = (SessionEntity) sessionRes.getObject();
				System.out.println("getResultExecutionListBriefListForSession");
				Map<String, String> stageIdName = getStageIdName();

				// TestFiles Fetching
				Map<String, String> testFileIdName = getTestFileIdName();
				
				
							Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();

							SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
							GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
							System.out.println("--------------------------------"+resTestFiles.getCode() +"  Msg"+resTestFiles.getMsg());
							List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
							lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
							
							if (lst.size() < 1) {
								response.setMsg("No TPF Files...");
								response.setCode(0);
								return response;
							}
						
							SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
							GetResponse getRes = sessionSelectedStagesService.getAllSessionStagesBySessionStageId(sessionId);
							List<SessionStagesMapping> sessionStagesMappingList = new ArrayList<SessionStagesMapping>();
							sessionStagesMappingList = (List<SessionStagesMapping>) getRes.getResponseList();
							for (SessionStagesMapping sessionStagesMapping : sessionStagesMappingList) {
								String sessionStagesMappingId = sessionStagesMapping.getSessionStagesMappingId();

								if (sessionStagesMappingId != null) {
									SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
									GetResponse getResponse = sessionStagesSelectedTestFilesService
											.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
									List<SessionStagesSelectedTestFiles> selectedTestFileInStages = new ArrayList<SessionStagesSelectedTestFiles>();
									selectedTestFileInStages = (List<SessionStagesSelectedTestFiles>) getResponse
											.getResponseList();
									if (selectedTestFileInStages != null) {
										for (SessionStagesSelectedTestFiles selectedTestFile : selectedTestFileInStages) {
											selectedTestFileIdTestFileId.put(
													selectedTestFile.getSessionStagesSelectedTestFilesId(),
													selectedTestFile.getTestFilesId());
										}
									}
								}
							}
				

				/*SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
				GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
				List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
				lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();*/
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
					resultExecutionDTO.setTestFileId(selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId()));
					
					resultExecutionDTO
							.setTestFileName(testFileIdName.get(selectedTestFileIdTestFileId.get(sessionStagesTestFilesResult.getSelectedtestFileId())));
					resultList.add(resultExecutionDTO);

				}
				response.setResultDTOList(resultList);
				response.setCode(1);
			} catch (Exception ex) {
				response.setCode(0);
				response.seteMsg("Not Fetched");
				response.seteMsg(ex.getLocalizedMessage());
			}
			return response;
		}

	// Detailed Result Last Stage For Current SessionId
	public ResultDetailedResponse getResultExecutionDetailedListForStages(String sessionId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {

			SessionService sessionService = new SessionService();
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; } SessionDto sessionDto =
			 * sessionEntityList.get(sessionEntityList.size() - 1); String sessionId =
			 * sessionDto.getSessionId(); String sessionName = sessionDto.getSessionName();
			 */

			// Fetching the SessionId
			List<String> systemInfoIdList = new ArrayList<String>();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultLst = (List<SessionStagesTestFilesResult>) res.getResponseList();
			if (sessionStagesTestFilesResultLst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}
			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();
			System.out.println("Before Stage Id Filter Size" + sessionStagesTestFilesResultLst.size());
			SessionStagesTestFilesResult lastSessionStagesTestFilesResult = sessionStagesTestFilesResultLst
					.get(sessionStagesTestFilesResultLst.size() - 1);
			String stageId = lastSessionStagesTestFilesResult.getStageId();
			System.out.println("Stage Id" + stageId);

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObjRes = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
			sessionStagesMapping = (SessionStagesMapping) getObjRes.getObject();
			String sessionStagesMappingId = sessionStagesMapping.getSessionStagesMappingId();
			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();

			if (sessionStagesMappingId != null) {
				SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
				GetResponse getResponse = sessionStagesSelectedTestFilesService
						.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
				List<SessionStagesSelectedTestFiles> selectedTestFileInStages = new ArrayList<SessionStagesSelectedTestFiles>();
				selectedTestFileInStages = (List<SessionStagesSelectedTestFiles>) getResponse.getResponseList();
				if (selectedTestFileInStages != null) {
					for (SessionStagesSelectedTestFiles selectedTestFile : selectedTestFileInStages) {
						selectedTestFileIdTestFileId.put(selectedTestFile.getSessionStagesSelectedTestFilesId(),
								selectedTestFile.getTestFilesId());
					}
				}
			}

			sessionStagesTestFilesResultLst = sessionStagesTestFilesResultLst.stream()
					.filter(stage -> stage.getStageId().equals(stageId)).collect(Collectors.toList());
			Map<String, String> objectIdTestFileId = new HashMap<String, String>();
			Map<String, String> objectIdstageId = new HashMap<String, String>();
			Map<String, String> objectIdSelectedTestFileId = new HashMap<String, String>();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultLst) {
				systemInfoIdList.add(sessionStagesTestFilesResult.getSystemResultInfoId());
				objectIdTestFileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSessionStagesTestFilesResultId());
				objectIdstageId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getStageId());
				objectIdSelectedTestFileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSelectedtestFileId());

			}
			System.out.println("Size Of Test File Execution" + sessionStagesTestFilesResultLst.size());
			List<ResultDto> lstResults = new ArrayList<ResultDto>();
			List<ResultDetailedDTO> resultDetailedList = new ArrayList<ResultDetailedDTO>();

			for (String systemInfoId : systemInfoIdList) {
				ObjectId objectId = new ObjectId(systemInfoId);
				List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
				lstInterResults = ResultManagement.getResult(sessionId, objectId);
				// lstResults.addAll(lstInterResults);
				if(lstInterResults!=null)
				{
					System.out.println("List Inter Results Size"+lstInterResults.size());
				}

				for (ResultDto resultDto : lstInterResults) {
					ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
					
					Map<String,String>fac =  resultDto.getFaultyChannel();
					String channelValues ="";
					for(String s :fac.keySet())
					{
						channelValues = s + "-"+ fac.get(s)+";";
					}
					
					resultDetailedDTO.setFaultyChannel(channelValues);
					System.out.println(resultDto.getFaultyChannel());
					resultDetailedDTO.setExpectedValue(resultDto.getExpectedValue());
					System.out.println(resultDto.getExpectedValue());
					resultDetailedDTO.setMeasuredValue(resultDto.getMeasuredValue());
					System.out.println(resultDto.getMeasuredValue());
					resultDetailedDTO.setRdfName(resultDto.getFileName());
					System.out.println(resultDto.getFileName());
					resultDetailedDTO.setSignalName(resultDto.getSignalName());
					System.out.println(resultDto.getSignalName());
					resultDetailedDTO.setStepName(resultDto.getStepName());
					System.out.println(resultDto.getStepName());
					resultDetailedDTO.setTestName(stageIdName.get(objectIdstageId.get(systemInfoId)));
					resultDetailedDTO.setTpfFileName(testFileIdName
							.get(selectedTestFileIdTestFileId.get(objectIdSelectedTestFileId.get(systemInfoId))));
					resultDetailedDTO.setTpgph(resultDto.getTpgph());
					resultDetailedDTO.setUnit(resultDto.getUnit());
					System.out.println("ResultDto--->" + resultDto.getFileName());
					resultDetailedList.add(resultDetailedDTO);
				}
			}
			response.setStageId(stageId);
			response.setStageName(stageIdName.get(stageId));
			response.setCode(1);
			response.setResultDetailedList(resultDetailedList);
			response.setMsg("Fetched Successfully");

		} catch (Exception ex) {
			response.setCode(1);
			response.setMsg("Issue Successfully");
			response.setMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());

		}
		return response;
	}

	// Result Execution Detailed List For Selected Stages On SessionId
	public ResultDetailedResponse getResultExecutionDetailedListForStages(String sessionId, String stageId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {

			SessionService sessionService = new SessionService();
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; } SessionDto sessionDto =
			 * sessionEntityList.get(sessionEntityList.size() - 1); String sessionId =
			 * sessionDto.getSessionId(); String sessionName = sessionDto.getSessionName();
			 */

			// Fetching the SessionId
			List<String> systemInfoIdList = new ArrayList<String>();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res = sessionStagesTestFilesResultService.getTestResultFileBySessionIdAndStageId(sessionId,
					stageId);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultLst = (List<SessionStagesTestFilesResult>) res.getResponseList();
			if (sessionStagesTestFilesResultLst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}

			// To Find Session Stages Mapping Id
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObjRes = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
			sessionStagesMapping = (SessionStagesMapping) getObjRes.getObject();
			String sessionStagesMappingId = sessionStagesMapping.getSessionStagesMappingId();
			Map<String, String> selectedTestFileIdTestFileId = new HashMap<String, String>();

			if (sessionStagesMappingId != null) {
				SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
				GetResponse getResponse = sessionStagesSelectedTestFilesService
						.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
				List<SessionStagesSelectedTestFiles> selectedTestFileInStages = new ArrayList<SessionStagesSelectedTestFiles>();
				selectedTestFileInStages = (List<SessionStagesSelectedTestFiles>) getResponse.getResponseList();
				if (selectedTestFileInStages != null) {
					for (SessionStagesSelectedTestFiles selectedTestFile : selectedTestFileInStages) {
						selectedTestFileIdTestFileId.put(selectedTestFile.getSessionStagesSelectedTestFilesId(),
								selectedTestFile.getTestFilesId());
					}
				}
			}

			Map<String, String> stageIdName = getStageIdName();
			Map<String, String> testFileResultIdSelectedTestFileId = new HashMap<String, String>();
			Map<String, String> objectIdSelectedTestfileId = new HashMap<String, String>();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();
			System.out.println("Before Stage Id Filter Size" + sessionStagesTestFilesResultLst.size());
			Map<String, String> objectIdTestFileId = new HashMap<String, String>();
			Map<String, String> objectIdstageId = new HashMap<String, String>();
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultLst) {
				systemInfoIdList.add(sessionStagesTestFilesResult.getSystemResultInfoId());
				objectIdTestFileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSessionStagesTestFilesResultId());
				objectIdstageId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getStageId());
				testFileResultIdSelectedTestFileId.put(sessionStagesTestFilesResult.getSessionStagesTestFilesResultId(),
						sessionStagesTestFilesResult.getSelectedtestFileId());
				objectIdSelectedTestfileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSelectedtestFileId());
			}
			System.out.println("Size Of Test File Execution" + sessionStagesTestFilesResultLst.size());
			List<ResultDto> lstResults = new ArrayList<ResultDto>();
			List<ResultDetailedDTO> resultDetailedList = new ArrayList<ResultDetailedDTO>();

			for (String systemInfoId : systemInfoIdList) {
				ObjectId objectId = new ObjectId(systemInfoId);
				List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
				lstInterResults = ResultManagement.getResult(sessionId, objectId);
				// lstResults.addAll(lstInterResults);

				for (ResultDto resultDto : lstInterResults) {
					ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
					Map<String,String>fac =  resultDto.getFaultyChannel();
					String channelValues ="";
					for(String s :fac.keySet())
					{
						channelValues = s + "-"+ fac.get(s)+";";
					}
					
					resultDetailedDTO.setFaultyChannel(channelValues);
						resultDetailedDTO.setExpectedValue(resultDto.getExpectedValue());
					resultDetailedDTO.setMeasuredValue(resultDto.getMeasuredValue());
					resultDetailedDTO.setRdfName(resultDto.getFileName());
					resultDetailedDTO.setSignalName(resultDto.getSignalName());
					resultDetailedDTO.setStepName(resultDto.getStepName());
					resultDetailedDTO.setTestName(stageIdName.get(objectIdstageId.get(systemInfoId)));
					String selectedTestFileId = objectIdSelectedTestfileId.get(systemInfoId);
					resultDetailedDTO
							.setTpfFileName(testFileIdName.get(selectedTestFileIdTestFileId.get(selectedTestFileId)));
					resultDetailedDTO.setTpgph(resultDto.getTpgph());
					resultDetailedDTO.setUnit(resultDto.getUnit());
					System.out.println("ResultDto--->" + resultDto.getFileName());
					resultDetailedList.add(resultDetailedDTO);
				}
			}

			response.setCode(1);
			response.setResultDetailedList(resultDetailedList);
			response.setMsg("Fetched Successfully");

		} catch (Exception ex) {
			response.setCode(0);
			response.setMsg("Issue Successfully");
			response.setMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());

		}
		return response;
	}

	

	// For Detailed Report For All Stages In Session
	public ResultDetailedResponse getResultExecutionListDetailedListForSession(String sessionId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {

			SessionService sessionService = new SessionService();
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; } SessionDto sessionDto =
			 * sessionEntityList.get(sessionEntityList.size() - 1); String sessionId =
			 * sessionDto.getSessionId(); String sessionName = sessionDto.getSessionName();
			 */

			// Fetching the SessionId
			List<String> systemInfoIdList = new ArrayList<String>();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultLst = (List<SessionStagesTestFilesResult>) res.getResponseList();

			if (sessionStagesTestFilesResultLst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}
			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();
			System.out.println("Before Stage Id Filter Size" + sessionStagesTestFilesResultLst.size());
			/*
			 * SessionStagesTestFilesResult lastSessionStagesTestFilesResult =
			 * sessionStagesTestFilesResultLst .get(sessionStagesTestFilesResultLst.size() -
			 * 1); String stageId = lastSessionStagesTestFilesResult.getStageId();
			 * System.out.println("Stage Id"+ stageId); sessionStagesTestFilesResultLst =
			 * sessionStagesTestFilesResultLst.stream() .filter(stage ->
			 * stage.getStageId().equals(stageId)).collect(Collectors.toList());
			 */
			Map<String, String> objectIdTestFileId = new HashMap<String, String>();
			Map<String, String> objectIdstageId = new HashMap<String, String>();

			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultLst) {
				systemInfoIdList.add(sessionStagesTestFilesResult.getSystemResultInfoId());
				objectIdTestFileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSessionStagesTestFilesResultId());
				objectIdstageId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getStageId());

			}
			System.out.println("Size Of Test File Execution" + sessionStagesTestFilesResultLst.size());
			List<ResultDto> lstResults = new ArrayList<ResultDto>();
			List<ResultDetailedDTO> resultDetailedList = new ArrayList<ResultDetailedDTO>();

			for (String systemInfoId : systemInfoIdList) {
				ObjectId objectId = new ObjectId(systemInfoId);
				List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
				lstInterResults = ResultManagement.getResult(sessionId, objectId);
				// lstResults.addAll(lstInterResults);

				for (ResultDto resultDto : lstInterResults) {
					ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
					Map<String,String>fac =  resultDto.getFaultyChannel();
					String channelValues ="";
					for(String s :fac.keySet())
					{
						channelValues = s + "-"+ fac.get(s)+";";
					}
					
					resultDetailedDTO.setFaultyChannel(channelValues);
						resultDetailedDTO.setExpectedValue(resultDto.getExpectedValue());
					resultDetailedDTO.setMeasuredValue(resultDto.getMeasuredValue());
					resultDetailedDTO.setRdfName(resultDto.getFileName());
					resultDetailedDTO.setSignalName(resultDto.getSignalName());
					resultDetailedDTO.setStepName(resultDto.getStepName());
					resultDetailedDTO.setTestName(stageIdName.get(objectIdstageId.get(systemInfoId)));
					resultDetailedDTO.setTpfFileName(testFileIdName.get(objectIdTestFileId.get(systemInfoId)));
					resultDetailedDTO.setTpgph(resultDto.getTpgph());
					resultDetailedDTO.setUnit(resultDto.getUnit());
					System.out.println("ResultDto--->" + resultDto.getFileName());
					resultDetailedDTO.setStageId(objectIdstageId.get(systemInfoId));
					resultDetailedList.add(resultDetailedDTO);
				}
			}

			response.setCode(1);
			response.setResultDetailedList(resultDetailedList);
			response.setMsg("Fetched Successfully");

		} catch (Exception ex) {
			response.setCode(0);
			response.setMsg("Issue Successfully");
			response.setMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());

		}
		return response;

	}

	// Current Unit Detailed Report
	public ResultDetailedResponse getResultExecutionListDetailedListForUnit(String uUtTypeId) {
		ResultDetailedResponse response = new ResultDetailedResponse();
		try {

			// Session Details
			SessionService sessionService = new SessionService();
			SessionResponse s = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<SessionDto>();
			sessionList = s.getListOfSession();
			sessionList = sessionList.stream().filter(session -> session.getUutId().equals(uUtTypeId))
					.collect(Collectors.toList());

			List<String> sessionIds = new ArrayList<String>();
			Map<String, Map<String, String>> sessionObjMap = new HashMap<String, Map<String, String>>();
			for (SessionDto sessionDto : sessionList) {
				Map<String, String> inObj = new HashMap<String, String>();
				inObj.put("sessionId", sessionDto.getSessionId());
				inObj.put("dfccSNo", sessionDto.getDfccSNo());
				inObj.put("dfccPartNo", sessionDto.getDfccPartNo());
				inObj.put("sessionName", sessionDto.getSessionName());
				inObj.put("startRemarks", sessionDto.getStartRemarks());
				inObj.put("uUtId", sessionDto.getUutId());
				sessionObjMap.put(sessionDto.getSessionId(), inObj);
				sessionIds.add(sessionDto.getSessionId());
			}
			/*
			 * SessionResponse sessionResponse = sessionService.getAllSession();
			 * List<SessionDto> sessionEntityList = new ArrayList<SessionDto>();
			 * sessionEntityList = sessionResponse.getListOfSession(); if
			 * (sessionEntityList.size() > 1) { response.setMsg("No Sessions...");
			 * response.setCode(0); return response; } SessionDto sessionDto =
			 * sessionEntityList.get(sessionEntityList.size() - 1); String sessionId =
			 * sessionDto.getSessionId(); String sessionName = sessionDto.getSessionName();
			 */

			// Fetching the SessionId
			List<String> systemInfoIdList = new ArrayList<String>();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultLst = new ArrayList<SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultLst = sessionStagesTestFilesResultService
					.getTestResultFileByStageIdByUutyType(sessionIds);

			if (sessionStagesTestFilesResultLst.size() < 1) {
				response.setMsg("No TPF Files...");
				response.setCode(0);
				return response;
			}
			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();
			System.out.println("Before Stage Id Filter Size" + sessionStagesTestFilesResultLst.size());
			/*
			 * SessionStagesTestFilesResult lastSessionStagesTestFilesResult =
			 * sessionStagesTestFilesResultLst .get(sessionStagesTestFilesResultLst.size() -
			 * 1); String stageId = lastSessionStagesTestFilesResult.getStageId();
			 * System.out.println("Stage Id"+ stageId); sessionStagesTestFilesResultLst =
			 * sessionStagesTestFilesResultLst.stream() .filter(stage ->
			 * stage.getStageId().equals(stageId)).collect(Collectors.toList());
			 */
			Map<String, String> objectIdTestFileId = new HashMap<String, String>();
			Map<String, String> objectIdstageId = new HashMap<String, String>();

			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultLst) {
				systemInfoIdList.add(sessionStagesTestFilesResult.getSystemResultInfoId());
				objectIdTestFileId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getSessionStagesTestFilesResultId());
				objectIdstageId.put(sessionStagesTestFilesResult.getSystemResultInfoId(),
						sessionStagesTestFilesResult.getStageId());

			}
			System.out.println("Size Of Test File Execution" + sessionStagesTestFilesResultLst.size());
			List<ResultDto> lstResults = new ArrayList<ResultDto>();
			List<ResultDetailedDTO> resultDetailedList = new ArrayList<ResultDetailedDTO>();
			for (String sessionId : sessionIds) {

				for (String systemInfoId : systemInfoIdList) {
					ObjectId objectId = new ObjectId(systemInfoId);
					List<ResultDto> lstInterResults = new ArrayList<ResultDto>();
					lstInterResults = ResultManagement.getResult(sessionId, objectId);
					// lstResults.addAll(lstInterResults);

					for (ResultDto resultDto : lstInterResults) {
						ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
						Map<String,String>fac =  resultDto.getFaultyChannel();
						String channelValues ="";
						for(String s1 :fac.keySet())
						{
							channelValues = s1 + "-"+ fac.get(s1)+";";
						}
						
						resultDetailedDTO.setFaultyChannel(channelValues);
							resultDetailedDTO.setExpectedValue(resultDto.getExpectedValue());
						resultDetailedDTO.setMeasuredValue(resultDto.getMeasuredValue());
						resultDetailedDTO.setRdfName(resultDto.getFileName());
						resultDetailedDTO.setSignalName(resultDto.getSignalName());
						resultDetailedDTO.setStepName(resultDto.getStepName());
						resultDetailedDTO.setTestName(stageIdName.get(objectIdstageId.get(systemInfoId)));
						resultDetailedDTO.setTpfFileName(testFileIdName.get(objectIdTestFileId.get(systemInfoId)));
						resultDetailedDTO.setTpgph(resultDto.getTpgph());
						resultDetailedDTO.setUnit(resultDto.getUnit());
						System.out.println("ResultDto--->" + resultDto.getFileName());
						resultDetailedList.add(resultDetailedDTO);
					}
				}
			}
			response.setCode(1);
			response.setResultDetailedList(resultDetailedList);
			response.setMsg("Fetched Successfully");

		} catch (Exception ex) {
			response.setCode(1);
			response.setMsg("Issue Successfully");
			response.setMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());

		}
		return response;

	}

	// For Getting the List Of ExecutionFiles...
	public ResultExecutionResponse getResultExecutionListBriefListForUnit(String uUtTypeId) {
		ResultExecutionResponse response = new ResultExecutionResponse();
		try {
			// Session Details
			SessionService sessionService = new SessionService();
			SessionResponse s = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<SessionDto>();
			sessionList = s.getListOfSession();
			sessionList = sessionList.stream().filter(session -> session.getUutId().equals(uUtTypeId))
					.collect(Collectors.toList());

			List<String> sessionIds = new ArrayList<String>();
			Map<String, String> sessionIdName = new HashMap<String, String>();
			Map<String, Map<String, String>> sessionObjMap = new HashMap<String, Map<String, String>>();
			for (SessionDto sessionDto : sessionList) {
				Map<String, String> inObj = new HashMap<String, String>();
				inObj.put("sessionId", sessionDto.getSessionId());
				inObj.put("dfccSNo", sessionDto.getDfccSNo());
				inObj.put("dfccPartNo", sessionDto.getDfccPartNo());
				inObj.put("sessionName", sessionDto.getSessionName());
				inObj.put("startRemarks", sessionDto.getStartRemarks());
				inObj.put("uUtId", sessionDto.getUutId());
				sessionObjMap.put(sessionDto.getSessionId(), inObj);
				sessionIds.add(sessionDto.getSessionId());
				sessionIdName.put(sessionDto.getSessionId(), sessionDto.getSessionName());
			}

			Map<String, String> stageIdName = getStageIdName();

			// TestFiles Fetching
			Map<String, String> testFileIdName = getTestFileIdName();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = sessionStagesTestFilesResultService.getTestResultFileByStageIdByUutyType(sessionIds);

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
				resultExecutionDTO.setTestFileId(sessionStagesTestFilesResult.getSelectedtestFileId());
				resultExecutionDTO
						.setTestFileName(testFileIdName.get(sessionStagesTestFilesResult.getSelectedtestFileId()));
				resultExecutionDTO.setSessionName(sessionIdName.get(sessionStagesTestFilesResult.getSessionId()));

			}

			response.setSessionIdName(sessionIdName);
			response.setTestFileIdName(testFileIdName);
			response.setStageIdName(stageIdName);

		} catch (Exception ex) {
			response.setCode(0);
			response.seteMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
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

		stagesTestFilesResultLst = stagesTestFilesResultLst.stream().filter(stage -> stage.getStageId().equals(stageId))
				.collect(Collectors.toList());

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

	public ReportDetails getDetailsForForStages() {
		ReportDetails reportDetails = new ReportDetails();

		try {

		} catch (Exception ex) {

		}
		return reportDetails;
	}

	public Map<String, String> getCurrentExecutionResults(String sessionId) {
		Map<String, String> stageOrSessionId = new HashMap<String, String>();
		try {
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse resTestFiles = sessionStagesTestFilesResultService.getTestResultFileByStageId(sessionId);
			List<SessionStagesTestFilesResult> lst = new ArrayList<SessionStagesTestFilesResult>();
			lst = (List<SessionStagesTestFilesResult>) resTestFiles.getResponseList();
			int lastListCount = lst.size() - 1;
			SessionStagesTestFilesResult sessionStagesTestFilesResults = new SessionStagesTestFilesResult();
			sessionStagesTestFilesResults = lst.get(lastListCount);
			stageOrSessionId.put("stageId", sessionStagesTestFilesResults.getStageId());
			stageOrSessionId.put("sessionId", sessionStagesTestFilesResults.getSessionId());

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return stageOrSessionId;
	}

	public Map<String, String> getSessionDetailsBySessionId(String sessionId) {
		Map<String, String> sessionDetailsMap = new HashMap<String, String>();
		try {
			SessionService sessionService = new SessionService();

			GetObjResponse sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionDto = new SessionEntity();
			sessionDto = (SessionEntity) sessionRes.getObject();
			sessionDetailsMap.put("sessionId", sessionId);
			sessionDetailsMap.put("dfccPartNo", sessionDto.getDfccPartNo());
			sessionDetailsMap.put("dfccSNo", sessionDto.getDfccSNo());
			sessionDetailsMap.put("userId", sessionDto.getUserId());
			sessionDetailsMap.put("sessionName", sessionDto.getSessionName());
			sessionDetailsMap.put("uUtID", sessionDto.getUutId());
			sessionDetailsMap.put("startRemarks", sessionDto.getStartRemarks());
			UserLoginDetailsService userDetailsService = new UserLoginDetailsService();
			UserLoginDetails userDetails = userDetailsService.getUserByUserId(sessionDto.getUserId());
			if (userDetails != null) {
				sessionDetailsMap.put("userName", userDetails.getLoginName());
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return sessionDetailsMap;
	}

	// For Getting Unit Sessions..
	public ResultUnitSessionDetailsResponse getSessionDetailsForResultsByUnit(String uutTypeId) {
		ResultUnitSessionDetailsResponse res = new ResultUnitSessionDetailsResponse();

		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(UserData.getRoleId());

			SessionService sessionService = new SessionService();
//			SessionResponse sessionResponse = sessionService.getAllSession();
			GetResponse getResponse = sessionService.getAllSessionDataByUUTId(uutTypeId);
			
			TrailSessionEntityService trailSessionService = new TrailSessionEntityService();
			TrailSessionResponse trailSessionResponse = trailSessionService.getDeActiveTrailSessionId();
			List<ResultUnitSessionDetailsDTO> resultUnitSessionDetailsDTOList = new ArrayList<ResultUnitSessionDetailsDTO>();
			Map<String, String> sessionIdName = new HashMap<String, String>();
			SessionMasterService sessionMasterService = new SessionMasterService();
			GetResponse responseMaster = sessionMasterService.getAllSessionMaster();
			List<SessionMaster> sessionMasterList = new ArrayList<SessionMaster>();
			sessionMasterList = (List<SessionMaster>) responseMaster.getResponseList();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res1 = new GetResponse();
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultList = new ArrayList<SessionStagesTestFilesResult>();
			res1 = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutTypeId);

			sessionStagesTestFilesResultList = (List<SessionStagesTestFilesResult>) res1.getResponseList();
			for (SessionMaster sessionMaster : sessionMasterList) {
				sessionIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
			}

//			for (SessionDto sessionDetails : sessionResponse.getListOfSession()) {
			for (Object object : getResponse.getResponseList()) {
				SessionEntity sessionDetails = (SessionEntity) object;

				if (sessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
						&& setOfUserLoginId.contains(sessionDetails.getUserId())) {

					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
					if (sessionDetails.getEndDate() != null) {
						resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDate().toString());

					} else {
						resultUnitSessionDetailsDTO.setEndTime("Not Done");

					}
					if (sessionDetails.getStartDate() != null) {
						resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDate().toString());

					} else {
						resultUnitSessionDetailsDTO.setStartTime("Not Started");

					}
					resultUnitSessionDetailsDTO.setSessionName(sessionDetails.getSessionName());
					resultUnitSessionDetailsDTO.setSessionId(sessionDetails.getSessionId());
					String status = "Pending";
					if (sessionDetails.getStartDate() != null) {
						status = "Started";
					}
					String results = "-";
					if (sessionDetails.getEndDate() != null) {
						status = "Completed";
					}
					resultUnitSessionDetailsDTO.setSessionStatus(status);
					if (status.equals("Completed")) {
						if (sessionStagesTestFilesResultList != null) {
							List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySession = sessionStagesTestFilesResultList
									.stream().filter(ses -> ses.getSessionId().equals(sessionDetails.getSessionId()))
									.collect(Collectors.toList());
							if (sessionStagesTestFilesResultListBySession != null) {
								List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySessionfailed = sessionStagesTestFilesResultListBySession
										.stream().filter(ses -> ses.getSessionId().equals("FAILURE"))
										.collect(Collectors.toList());
								if (sessionStagesTestFilesResultListBySessionfailed != null) {
									results = "Failure";
								}
							}
						}
					}
					resultUnitSessionDetailsDTO.setSessionResults(results);
					resultUnitSessionDetailsDTO
							.setSessionType(sessionIdName.get(sessionDetails.getSessionTypeMasterId()));
					// resultUnitSessionDetailsDTO.setStartTime(uutTypeId);
					resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
				}
			}

			for (TrailSessionDto sessionDetails : trailSessionResponse.getListOfSession()) {
				if (sessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
						&& setOfUserLoginId.contains(sessionDetails.getCreatedBy())) {
					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
					if (sessionDetails.getEndDate() != null) {
						resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDate().toString());

					} else {
						resultUnitSessionDetailsDTO.setEndTime("Not Done");

					}
					if (sessionDetails.getStartDate() != null) {
						resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDate().toString());

					} else {
						resultUnitSessionDetailsDTO.setStartTime("Not Started");

					}

					resultUnitSessionDetailsDTO.setSessionName(sessionDetails.getSessionName());
					resultUnitSessionDetailsDTO.setSessionId(sessionDetails.getSessionId());
					String status = "Pending";
					if (sessionDetails.getStartDate() != null) {
						status = "Started";
					}
					if (sessionDetails.getEndDate() != null) {
						status = "Completed";
					}
					String results = "-";
					resultUnitSessionDetailsDTO.setSessionStatus(status);
					if (status.equals("Completed")) {
						List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySession = sessionStagesTestFilesResultList
								.stream().filter(ses -> ses.getSessionId().equals(sessionDetails.getSessionId()))
								.collect(Collectors.toList());
						if (sessionStagesTestFilesResultListBySession != null) {
							List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySessionfailed = sessionStagesTestFilesResultListBySession
									.stream().filter(ses -> ses.getSessionId().equals("FAILURE"))
									.collect(Collectors.toList());
							if (sessionStagesTestFilesResultListBySessionfailed != null) {
								results = "Failure";
							}
						}
					}

					resultUnitSessionDetailsDTO.setSessionResults(results);
					resultUnitSessionDetailsDTO
							.setSessionType(sessionIdName.get(sessionDetails.getSessionTypeMasterId()));
					// resultUnitSessionDetailsDTO.setStartTime(uutTypeId);
					resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
				}
			}
			res.setResultUnitSessionDetailsDTOList(resultUnitSessionDetailsDTOList);
			res.setTotalNoOfSessions(resultUnitSessionDetailsDTOList.size());
			res.setUutTypeId(uutTypeId);
			res.setCode(1);
			res.setMsg("Data Fetched");

		} catch (Exception ex) {
			res.setCode(0);
			res.setMsg("Not Fetched..");
			res.seteMsg(ex.getLocalizedMessage());
			System.err.println(ex.getLocalizedMessage());
		}
		return res;
	}

	// For Getting Session Stages...
	public ResultSessionStagesDetailsResponse getStagesDetailsForSession(String sessionId) {
		ResultSessionStagesDetailsResponse response = new ResultSessionStagesDetailsResponse();
		try {
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetResponse getStagesResponse = sessionSelectedStagesService.getAllSessionStagesBySessionStageId(sessionId);
			List<SessionStagesMapping> stagesDetailsList = new ArrayList<SessionStagesMapping>();
			stagesDetailsList = (List<SessionStagesMapping>) getStagesResponse.getResponseList();

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res1 = new GetResponse();
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultList = new ArrayList<SessionStagesTestFilesResult>();
			res1 = sessionStagesTestFilesResultService.getTestResultFileBySessionIdOrderByDate(sessionId);
			sessionStagesTestFilesResultList = (List<SessionStagesTestFilesResult>) res1.getResponseList();
			SessionManagement sessionManagement = new SessionManagement();
			Map<String, String> stagesIdName = sessionManagement.getAllStageIdName();
			List<ResultSessionStagesDetailsDTO> resultSessionStagesDetailsDTOList = new ArrayList<ResultSessionStagesDetailsDTO>();

			String levelId = "";
			for (SessionStagesMapping sessionStagesMapping : stagesDetailsList) {

				if (sessionStagesMapping.getLevelTwoStageId() != null) {
					levelId = sessionStagesMapping.getLevelTwoStageId();
				}
				if (sessionStagesMapping.getLevelThreeStageId() != null) {
					levelId = sessionStagesMapping.getLevelThreeStageId();
				}
				if (sessionStagesMapping.getLevelFourStageId() != null) {
					levelId = sessionStagesMapping.getLevelFourStageId();
				}
				if (sessionStagesMapping.getLevelFiveStageId() != null) {
					levelId = sessionStagesMapping.getLevelFiveStageId();
				}
				String stageId = levelId;
				String levelName = stagesIdName.get(levelId);
				String startTime = "-";
				String endTime = "-";
				int failedFiles = 0;
				int files = 0;
				// System.out.println("SIZE"+sessionStagesTestFilesResultList.size());
				if (sessionStagesTestFilesResultList != null && stageId != null) {
					List<SessionStagesTestFilesResult> stgesfilesList = sessionStagesTestFilesResultList = sessionStagesTestFilesResultList
							.stream().filter(ses -> ses.getStageId().equals(stageId)).collect(Collectors.toList());
					if (stgesfilesList != null) {
						startTime = sessionStagesTestFilesResultList.get(0).getStartTime();
						endTime = sessionStagesTestFilesResultList.get(sessionStagesTestFilesResultList.size() - 1)
								.getEndTime();
					}
					for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultList) {
						if (!sessionStagesTestFilesResult.getTestStatus().equals("Success")) {
							failedFiles++;
						}
						files++;
					}
				}

				ResultSessionStagesDetailsDTO resultSessionStagesDetailsDTO = new ResultSessionStagesDetailsDTO();
				resultSessionStagesDetailsDTO.setFailedFiles(failedFiles);
				resultSessionStagesDetailsDTO.setEndTime(endTime);
				resultSessionStagesDetailsDTO.setStartTime(startTime);
				if (!startTime.equals("-") && !endTime.equals("-")) {
					SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					Date d1 = format.parse(startTime);
					Date d2 = format.parse(endTime);
					long differenceInMillis = d2.getTime() - d1.getTime();

					long differenceInSeconds = differenceInMillis / 1000;
					long differenceInMinutes = differenceInSeconds / 60;
					long differenceInHours = differenceInMinutes / 60;
					long differenceInDays = differenceInHours / 24;
					resultSessionStagesDetailsDTO.setTimeTakenForExecution(differenceInMinutes + "");
					resultSessionStagesDetailsDTO.setStatus("COMPLETED");
				} else {
					resultSessionStagesDetailsDTO.setTimeTakenForExecution("-");
					resultSessionStagesDetailsDTO.setStatus("Pending");
				}
				if (failedFiles > 0) {
					resultSessionStagesDetailsDTO.setResult("Failed");
				} else if (resultSessionStagesDetailsDTO.getStatus().equalsIgnoreCase("Pending")) {
					resultSessionStagesDetailsDTO.setResult("-");
				}

				else if (failedFiles == 0 && resultSessionStagesDetailsDTO.getStatus().equalsIgnoreCase("COMPLETED")) {
					resultSessionStagesDetailsDTO.setResult("Success");

				}
				resultSessionStagesDetailsDTO.setStageMappingId(sessionStagesMapping.getSessionStagesMappingId());
				resultSessionStagesDetailsDTO.setStageId(levelId);
				resultSessionStagesDetailsDTO.setStage(stagesIdName.get(levelId));
				resultSessionStagesDetailsDTOList.add(resultSessionStagesDetailsDTO);

			}
			response.setTotalNoOfStages(resultSessionStagesDetailsDTOList.size());
			response.setResultSessionStagesDetailsDTOList(resultSessionStagesDetailsDTOList);
			response.setCode(1);
			response.setMsg("Fetched");

		} catch (Exception ex) {
			response.setCode(0);
			response.setMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
			System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	// For Getting Session Stages...

}