package com.teclever.dfcc.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.DashBoardFilesUploading;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.SessionTiming;
import com.teclever.datastore.entities.UUTLogBook;
import com.teclever.datastore.response.UUTLogBookResponse;
import com.teclever.datastore.service.DashBoardFilesUploadServices;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.SessionTimingService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.datastore.service.UUTLogBookService;
import com.teclever.datastore.service.UserLoginDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsResponse;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.dto.SessionDTOResponse;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Debug;

public class DashboardManagement {

	// For Getting Session Details By Last Sl No ....
	public ResultUnitSessionDetailsResponse getSessionDetailsForResultsByUnit(String uutTypeId) {
		ResultUnitSessionDetailsResponse res = new ResultUnitSessionDetailsResponse();
		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(UserData.getRoleId());
			SessionService sessionService = new SessionService();
//			SessionResponse sessionResponse = sessionService.getAllSession();
			GetResponse getResponse = sessionService.getAllSessionDataByUUTId(uutTypeId);

			TrailSessionEntityService trailSessionService = new TrailSessionEntityService();
			TrailSessionResponse trailSessionResponse = trailSessionService.getAllSession();
			List<ResultUnitSessionDetailsDTO> resultUnitSessionDetailsDTOList = new ArrayList<ResultUnitSessionDetailsDTO>();
			Map<String, String> sessionIdName = new HashMap<String, String>();
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultList = Collections.emptyList();
			;
			SessionMasterService sessionMasterService = new SessionMasterService();
			GetResponse responseMaster = sessionMasterService.getAllSessionMaster();
			List<SessionMaster> sessionMasterList = new ArrayList<SessionMaster>();
			sessionMasterList = (List<SessionMaster>) responseMaster.getResponseList();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res1 = new GetResponse();

			res1 = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutTypeId);

			if (res1 != null && res1.getResponseList() != null) {
				sessionStagesTestFilesResultList = (List<SessionStagesTestFilesResult>) res1.getResponseList();
			} else {
				sessionStagesTestFilesResultList = new ArrayList<>();
			}

			for (SessionMaster sessionMaster : sessionMasterList) {
				sessionIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				String sessionMasterList1 = sessionMaster.getSessionTypeName();
			}
//				for (SessionDto sessionDetails : sessionResponse.getListOfSession()) {
			if (getResponse.getResponseList() != null) {
				for (Object object : getResponse.getResponseList()) {
					SessionEntity sessionDetails = (SessionEntity) object;
//						if (sessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
//								&& setOfUserLoginId.contains(sessionDetails.getUserId())) {
					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
					if (sessionDetails.getEndDate() != null) {
						// resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDate().toString());
						resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDateTime());
					} else {
						resultUnitSessionDetailsDTO.setEndTime("Not Done");
					}
					if (sessionDetails.getStartDate() != null) {
						// resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDate().toString());
						resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDateTime());
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
					DFCCConstant.sessionIdDfccSlNo.put(sessionDetails.getSessionId(), sessionDetails.getDfccSNo());
					resultUnitSessionDetailsDTO.setSessionSlNo(sessionDetails.getDfccSNo());
					resultUnitSessionDetailsDTO.setSessionResults(results);
					resultUnitSessionDetailsDTO
							.setSessionType(sessionIdName.get(sessionDetails.getSessionTypeMasterId()));
					resultUnitSessionDetailsDTO.setStratRemarks(sessionDetails.getStartRemarks());
					resultUnitSessionDetailsDTO.setEndRemarks(sessionDetails.getEndRemarks());
					// resultUnitSessionDetailsDTO.setStartTime(uutTypeId);
					resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
//						}
				}

				for (TrailSessionDto trailsessionDetails : trailSessionResponse.getListOfSession()) {
//						if (trailsessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
//								&& setOfUserLoginId.contains(trailsessionDetails.getCreatedBy())) {

					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
					// Set end time and start time
					if (trailsessionDetails.getEndDate() != null) {
						// resultUnitSessionDetailsDTO.setEndTime(trailsessionDetails.getEndDate().toString());
						resultUnitSessionDetailsDTO.setEndTime(trailsessionDetails.getEndDateTime());
					} else {
						resultUnitSessionDetailsDTO.setEndTime("Not Done");
					}

					if (trailsessionDetails.getStartDate() != null) {
						// resultUnitSessionDetailsDTO.setStartTime(trailsessionDetails.getStartDate().toString());
						resultUnitSessionDetailsDTO.setStartTime(trailsessionDetails.getStartDateTime());
					} else {
						resultUnitSessionDetailsDTO.setStartTime("Not Started");
					}

					// Set session details
					resultUnitSessionDetailsDTO.setSessionName(trailsessionDetails.getSessionName());
					resultUnitSessionDetailsDTO.setSessionId(trailsessionDetails.getSessionId());

					resultUnitSessionDetailsDTO.setStratRemarks(trailsessionDetails.getStartRemarks());
					resultUnitSessionDetailsDTO.setEndRemarks(trailsessionDetails.getEndRemarks());

					// Determine session status
					String status = "Pending";
					if (trailsessionDetails.getStartDate() != null) {
						status = "Started"; // Session started but not finished
					}
					if (trailsessionDetails.getEndDate() != null) {
						status = "Completed"; // Session has been completed
					}

					// Set session status
					resultUnitSessionDetailsDTO.setSessionStatus(status);

					// Determine session results
					String results = "-";
					if ("Completed".equals(status)) {
						List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySession = sessionStagesTestFilesResultList
								.stream().filter(ses -> ses.getSessionId().equals(trailsessionDetails.getSessionId()))
								.collect(Collectors.toList());

						if (sessionStagesTestFilesResultListBySession != null) {
							List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySessionfailed = sessionStagesTestFilesResultListBySession
									.stream().filter(ses -> "FAILURE".equals(ses.getSessionId()))
									.collect(Collectors.toList());

							if (sessionStagesTestFilesResultListBySessionfailed != null) {
								results = "Failure";
							}
						}
					}
					DFCCConstant.sessionIdDfccSlNo.put(trailsessionDetails.getSessionId(),
							trailsessionDetails.getDfccSNo());

					// Set remarks

					// Set session results
					resultUnitSessionDetailsDTO.setSessionResults(results);

					// Set session type from sessionMasterList
					resultUnitSessionDetailsDTO
							.setSessionType(sessionIdName.get(trailsessionDetails.getSessionTypeMasterId()));

					resultUnitSessionDetailsDTO.setSessionSlNo(trailsessionDetails.getDfccSNo());

					// Add to result list
					resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
				}
			}

//				}
			String currentSlNo = DFCCConstant.sessionIdDfccSlNo.get(StateMachine.currentSessionDetails.getSessionId());
			res.setResultUnitSessionDetailsDTOList(resultUnitSessionDetailsDTOList);

			// Filter For Last DFCC SlNo...If Needed All Sno Then Comment Below Filter
			resultUnitSessionDetailsDTOList = resultUnitSessionDetailsDTOList.stream()
					.filter(e -> e.getSessionSlNo().equalsIgnoreCase(currentSlNo)).collect(Collectors.toList());

			res.setResultUnitSessionDetailsDTOList(resultUnitSessionDetailsDTOList);

			res.setTotalNoOfSessions(resultUnitSessionDetailsDTOList.size());
			res.setUutTypeId(uutTypeId);
			res.setCode(1);
			res.setMsg("Data Fetched");
		} catch (Exception ex) {
			res.setCode(0);
			res.setMsg("Not Fetched..");
			res.seteMsg(ex.getLocalizedMessage());
//			System.err.println(ex.getLocalizedMessage());
		}
		return res;
	}
	
	
	// For Getting Session Details By Last Sl No ....
	public ResultUnitSessionDetailsResponse getSessionDetailsForResultsByUnitUUT(String uutTypeId) {
		ResultUnitSessionDetailsResponse res = new ResultUnitSessionDetailsResponse();
		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(UserData.getRoleId());
			SessionService sessionService = new SessionService();
//			SessionResponse sessionResponse = sessionService.getAllSession();
			GetResponse getResponse = sessionService.getAllSessionDataByUUTId(uutTypeId);

			TrailSessionEntityService trailSessionService = new TrailSessionEntityService();
			TrailSessionResponse trailSessionResponse = trailSessionService.getAllSession();
			List<ResultUnitSessionDetailsDTO> resultUnitSessionDetailsDTOList = new ArrayList<ResultUnitSessionDetailsDTO>();
			Map<String, String> sessionIdName = new HashMap<String, String>();
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultList = Collections.emptyList();
			;
			SessionMasterService sessionMasterService = new SessionMasterService();
			GetResponse responseMaster = sessionMasterService.getAllSessionMaster();
			List<SessionMaster> sessionMasterList = new ArrayList<SessionMaster>();
			sessionMasterList = (List<SessionMaster>) responseMaster.getResponseList();
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse res1 = new GetResponse();

			res1 = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutTypeId);

			if (res1 != null && res1.getResponseList() != null) {
				sessionStagesTestFilesResultList = (List<SessionStagesTestFilesResult>) res1.getResponseList();
			} else {
				sessionStagesTestFilesResultList = new ArrayList<>();
			}

			for (SessionMaster sessionMaster : sessionMasterList) {
				sessionIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				String sessionMasterList1 = sessionMaster.getSessionTypeName();
			}
//				for (SessionDto sessionDetails : sessionResponse.getListOfSession()) {
			if (getResponse.getResponseList() != null) {
				for (Object object : getResponse.getResponseList()) {
					SessionEntity sessionDetails = (SessionEntity) object;
//						if (sessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
//								&& setOfUserLoginId.contains(sessionDetails.getUserId())) {
					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
					if (sessionDetails.getEndDate() != null) {
						// resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDate().toString());
						resultUnitSessionDetailsDTO.setEndTime(sessionDetails.getEndDateTime());
					} else {
						resultUnitSessionDetailsDTO.setEndTime("Not Done");
					}
					if (sessionDetails.getStartDate() != null) {
						// resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDate().toString());
						resultUnitSessionDetailsDTO.setStartTime(sessionDetails.getStartDateTime());
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
					DFCCConstant.sessionIdDfccSlNo.put(sessionDetails.getSessionId(), sessionDetails.getDfccSNo());
					resultUnitSessionDetailsDTO.setSessionSlNo(sessionDetails.getDfccSNo());
					resultUnitSessionDetailsDTO.setSessionResults(results);
					resultUnitSessionDetailsDTO
							.setSessionType(sessionIdName.get(sessionDetails.getSessionTypeMasterId()));
					resultUnitSessionDetailsDTO.setStratRemarks(sessionDetails.getStartRemarks());
					resultUnitSessionDetailsDTO.setEndRemarks(sessionDetails.getEndRemarks());
					// resultUnitSessionDetailsDTO.setStartTime(uutTypeId);
					resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
//						}
				}

				for (TrailSessionDto trailsessionDetails : trailSessionResponse.getListOfSession()) {
					if (trailsessionDetails.getUutId().equals(uutTypeId)) {
//						if (trailsessionDetails.getUutId().equals(uutTypeId) && setOfUserLoginId != null
//								&& setOfUserLoginId.contains(trailsessionDetails.getCreatedBy())) {

						ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = new ResultUnitSessionDetailsDTO();
						// Set end time and start time
						if (trailsessionDetails.getEndDate() != null) {
							// resultUnitSessionDetailsDTO.setEndTime(trailsessionDetails.getEndDate().toString());
							resultUnitSessionDetailsDTO.setEndTime(trailsessionDetails.getEndDateTime());
						} else {
							resultUnitSessionDetailsDTO.setEndTime("Not Done");
						}

						if (trailsessionDetails.getStartDate() != null) {
							// resultUnitSessionDetailsDTO.setStartTime(trailsessionDetails.getStartDate().toString());
							resultUnitSessionDetailsDTO.setStartTime(trailsessionDetails.getStartDateTime());
						} else {
							resultUnitSessionDetailsDTO.setStartTime("Not Started");
						}

						// Set session details
						resultUnitSessionDetailsDTO.setSessionName(trailsessionDetails.getSessionName());
						resultUnitSessionDetailsDTO.setSessionId(trailsessionDetails.getSessionId());

						resultUnitSessionDetailsDTO.setStratRemarks(trailsessionDetails.getStartRemarks());
						resultUnitSessionDetailsDTO.setEndRemarks(trailsessionDetails.getEndRemarks());

						// Determine session status
						String status = "Pending";
						if (trailsessionDetails.getStartDate() != null) {
							status = "Started"; // Session started but not finished
						}
						if (trailsessionDetails.getEndDate() != null) {
							status = "Completed"; // Session has been completed
						}

						// Set session status
						resultUnitSessionDetailsDTO.setSessionStatus(status);

						// Determine session results
						String results = "-";
						if ("Completed".equals(status)) {
							List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySession = sessionStagesTestFilesResultList
									.stream()
									.filter(ses -> ses.getSessionId().equals(trailsessionDetails.getSessionId()))
									.collect(Collectors.toList());

							if (sessionStagesTestFilesResultListBySession != null) {
								List<SessionStagesTestFilesResult> sessionStagesTestFilesResultListBySessionfailed = sessionStagesTestFilesResultListBySession
										.stream().filter(ses -> "FAILURE".equals(ses.getSessionId()))
										.collect(Collectors.toList());

								if (sessionStagesTestFilesResultListBySessionfailed != null) {
									results = "Failure";
								}
							}
						}
						DFCCConstant.sessionIdDfccSlNo.put(trailsessionDetails.getSessionId(),
								trailsessionDetails.getDfccSNo());

						// Set remarks

						// Set session results
						resultUnitSessionDetailsDTO.setSessionResults(results);

						// Set session type from sessionMasterList
						resultUnitSessionDetailsDTO
								.setSessionType(sessionIdName.get(trailsessionDetails.getSessionTypeMasterId()));

						resultUnitSessionDetailsDTO.setSessionSlNo(trailsessionDetails.getDfccSNo());

						// Add to result list
						resultUnitSessionDetailsDTOList.add(resultUnitSessionDetailsDTO);
					}
				}
			}
			
			
			

//				}
			String currentSlNo = DFCCConstant.sessionIdDfccSlNo.get(StateMachine.currentSessionDetails.getSessionId());
			res.setResultUnitSessionDetailsDTOList(resultUnitSessionDetailsDTOList);

			// Filter For Last DFCC SlNo...If Needed All Sno Then Comment Below Filter
//			resultUnitSessionDetailsDTOList = resultUnitSessionDetailsDTOList.stream()
//					.filter(e -> e.getSessionSlNo().equalsIgnoreCase(currentSlNo)).collect(Collectors.toList());
//
//			res.setResultUnitSessionDetailsDTOList(resultUnitSessionDetailsDTOList);

			res.setTotalNoOfSessions(resultUnitSessionDetailsDTOList.size());
			res.setUutTypeId(uutTypeId);
			res.setCode(1);
			res.setMsg("Data Fetched");
		} catch (Exception ex) {
			res.setCode(0);
			res.setMsg("Not Fetched..");
			res.seteMsg(ex.getLocalizedMessage());
//			System.err.println(ex.getLocalizedMessage());
		}
		return res;
	}

	// For Production Type Details By Sl No.....
	public ProductionSessionDetailsDTO getProductionDetailsForDashBoard(String uutType) {
		ProductionSessionDetailsDTO response = new ProductionSessionDetailsDTO();
		try {
			ResultUnitSessionDetailsResponse res = new ResultUnitSessionDetailsResponse();
			ResultUnitSessionDetailsResponse resultUnitSessionDetailsResponse = getSessionDetailsForResultsByUnit(
					uutType);

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();

			response.setSessionList(sessionList);

			List<ResultUnitSessionDetailsDTO> productionSessionList = sessionList.stream()
					.filter(E -> E.getSessionType().equals("Production")).collect(Collectors.toList());

			List<ProductionDashboardDetails> productionDashboardDetailsList = new ArrayList<ProductionDashboardDetails>();
			for (ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO : productionSessionList) {

				ProductionDashboardDetails productionDashboardDetails = new ProductionDashboardDetails();
				productionDashboardDetails.setSessionId(resultUnitSessionDetailsDTO.getSessionId());
				productionDashboardDetails.setStartTime(resultUnitSessionDetailsDTO.getStartTime());
				productionDashboardDetails.setEndTimeTime(resultUnitSessionDetailsDTO.getEndTime());
				productionDashboardDetails.setSessionStatus(resultUnitSessionDetailsDTO.getSessionStatus());
				productionDashboardDetailsList.add(productionDashboardDetails);

			}
			response.setCode(1);
			response.setProdList(productionDashboardDetailsList);
			response.setProductionSessionSize(productionDashboardDetailsList.size());

		} catch (Exception ex) {
//			////System.out.println(ex.getMessage());
			response.setCode(-1);
		}

		return response;

	}

	// For PQT Details....
	public PQTSessionDetailsDTO getPQTDetailsForDashBoard(String uutType) {
		PQTSessionDetailsDTO response = new PQTSessionDetailsDTO();
		try {

			ResultUnitSessionDetailsResponse resultUnitSessionDetailsResponse = getSessionDetailsForResultsByUnit(
					uutType);

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();
			List<ResultUnitSessionDetailsDTO> pqtSessionList = sessionList.stream()
					.filter(sesType -> sesType.getSessionType().equals("PQT")).collect(Collectors.toList());
			if (pqtSessionList.size() > 0) {
				ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = pqtSessionList.get(pqtSessionList.size() - 1);
				response.setPqtConducted(true);
				response.setStartTime(resultUnitSessionDetailsDTO.getStartTime());
				response.setSessionName(resultUnitSessionDetailsDTO.getSessionName());

				String sessionId = resultUnitSessionDetailsDTO.getSessionId();

				ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
				ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
				resultExecutionResponse = resultExecutionManagement
						.getResultExecutionListBriefListForSession(sessionId);

				List<ResultExecutionDTO> resultDTOList = resultExecutionResponse.getResultDTOList();
				if (!resultDTOList.isEmpty()) {
					ResultExecutionDTO resultExecutionDTO = resultDTOList.get(resultDTOList.size() - 1);
					response.setEndTimeTime(resultExecutionDTO.getEndTime());
					response.setLastPQTStage(resultExecutionDTO.getTestMode());

				} else {
					response.setEndTimeTime("Not Started");
					response.setLastPQTStage("-");
				}

			} else {
				response.setPqtConducted(false);
				response.setStartTime("Not Conducted");
				response.setSessionName("-");
			}

		} catch (Exception ex) {
//			////System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	// get All Details For Dashboard
	public DashBoardDetailsDTO getDashboardDetails(String uutTypeId) {
		DashBoardDetailsDTO response = new DashBoardDetailsDTO();

		try {
			ProductionSessionDetailsDTO productionSessionDetailsDTO = new ProductionSessionDetailsDTO();
			productionSessionDetailsDTO = getProductionDetailsForDashBoard(uutTypeId);
			ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
			// Production Session Details List
			response.setProductionSessionDetailsDTOList(productionSessionDetailsDTO.getProdList());

			// PQT Details
			PQTSessionDetailsDTO pqtResponse = new PQTSessionDetailsDTO();
			List<ResultUnitSessionDetailsDTO> sessionList = productionSessionDetailsDTO.getSessionList();

			// All Session Details List By Sl No...
			response.setAllSessionListBySlNo(sessionList);

			List<ResultUnitSessionDetailsDTO> pqtSessionList = sessionList.stream()
					.filter(sesType -> sesType.getSessionType().equals("PQT")).collect(Collectors.toList());
			if (pqtSessionList.size() > 0) {
				ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = pqtSessionList.get(pqtSessionList.size() - 1);
				pqtResponse.setPqtConducted(true);
				pqtResponse.setStartTime(resultUnitSessionDetailsDTO.getStartTime());
				pqtResponse.setSessionName(resultUnitSessionDetailsDTO.getSessionName());

				String sessionId = resultUnitSessionDetailsDTO.getSessionId();

				ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
				resultExecutionResponse = resultExecutionManagement
						.getResultExecutionListBriefListForSession(sessionId);

				List<ResultExecutionDTO> resultDTOList = resultExecutionResponse.getResultDTOList(); // get the list
				if (!resultDTOList.isEmpty()) {
					ResultExecutionDTO resultExecutionDTO = resultDTOList.get(resultDTOList.size() - 1); // get last
																											// element
					pqtResponse.setEndTimeTime(resultExecutionDTO.getEndTime());
					pqtResponse.setLastPQTStage(resultExecutionDTO.getTestMode());

				} else {
					pqtResponse.setEndTimeTime("Not Started");
					pqtResponse.setLastPQTStage("-");
				}

			} else {
				pqtResponse.setPqtConducted(false);
				pqtResponse.setStartTime("Not Conducted");
				pqtResponse.setSessionName("-");
			}

			// PQT Details
			response.setpQTSessionDetailsDTO(pqtResponse);

			String slNo = DFCCConstant.sessionIdDfccSlNo.get(StateMachine.currentSessionDetails.getSessionId());

			// All Failure List
			ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
			resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForUnit(uutTypeId,
					slNo);
			response.setResultDetailedList(resultDetailedResponse.getResultDetailedList());

			response.setLastTestedDate(resultDetailedResponse.getLastTestedDate());
			response.setSerialNo(slNo);

		} catch (Exception ex) {

			response.setEmsg(ex.getLocalizedMessage());

		}
		return response;
	}

	public Map<String, Map<String, String>> getBlsDetailsOfMK1AMK2() {
		Map<String, Map<String, String>> blsDetailsMap = new HashMap<String, Map<String, String>>();
		try {
			Map<String, String> mk1ADetails = new HashMap<String, String>();
			Map<String, String> mk2Details = new HashMap<String, String>();
			SessionService sessionService = new SessionService();
			SessionResponse sessionResponse = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<>();
			sessionList = sessionResponse.getListOfSession();
			List<SessionDto> sessionListUUUT2 = new ArrayList<>();
			sessionListUUUT2 = sessionList.stream().filter(s -> s.getUutId().equals("UUT2"))
					.collect(Collectors.toList());
			mk1ADetails.put("totalSessions", sessionListUUUT2.size() + "");
			sessionListUUUT2 = sessionListUUUT2.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());
			mk1ADetails.put("blsSessions", sessionListUUUT2.size() + "");
			List<String> sessionIdsMk1A = sessionListUUUT2.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			List<SessionStagesTestFilesResult> mk1ATestFileResultList = sessionStagesTestFilesResultService
					.getTestResultFileByStageIdByUutyType(sessionIdsMk1A);
			if (mk1ATestFileResultList.size() > 0) {
				mk1ADetails.put("lastRunMk1A",
						mk1ATestFileResultList.get(mk1ATestFileResultList.size() - 1).getEndTime());
				GetObjResponse getObjResMk1 = sessionService.getSessionDetailBySessionStageId(
						mk1ATestFileResultList.get(mk1ATestFileResultList.size() - 1).getSessionId());
				SessionEntity sessionEntityMk1a = new SessionEntity();
				sessionEntityMk1a = (SessionEntity) getObjResMk1.getObject();
				mk1ADetails.put("lastRunMk1ABlsSessionName", sessionEntityMk1a.getSessionName());
			} else {
				mk1ADetails.put("lastRunMk1A", "-");
				mk1ADetails.put("lastRunMk1ABlsSessionName", "-");
			}

			List<SessionDto> sessionListUUUT3 = new ArrayList<>();
			List<String> sessionIdsMk2 = sessionListUUUT3.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());
			sessionListUUUT3 = sessionList.stream().filter(s -> s.getUutId().equals("UUT3"))
					.collect(Collectors.toList());
			mk2Details.put("totalSessions", sessionListUUUT3.size() + "");
			sessionListUUUT3 = sessionListUUUT3.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());
			mk2Details.put("blsSessions", sessionListUUUT3.size() + "");

			List<SessionStagesTestFilesResult> mk2TestFileResultList = sessionStagesTestFilesResultService
					.getTestResultFileByStageIdByUutyType(sessionIdsMk2);
			if (mk2TestFileResultList.size() > 0) {
				mk2Details.put("lastRunMk2", mk2TestFileResultList.get(mk2TestFileResultList.size() - 1).getEndTime());
				GetObjResponse getObjResMk2 = sessionService.getSessionDetailBySessionStageId(
						mk1ATestFileResultList.get(mk1ATestFileResultList.size() - 1).getSessionId());
				SessionEntity sessionEntityMk2 = new SessionEntity();
				sessionEntityMk2 = (SessionEntity) getObjResMk2.getObject();
			} else {
				mk2Details.put("lastRunMk2", "-");
				mk2Details.put("lastRunMk2BlsSessionName", "-");
			}

			blsDetailsMap.put("MK1A", mk1ADetails);
			blsDetailsMap.put("MK2", mk2Details);

		} catch (Exception ex) {

		}
		return blsDetailsMap;
	}

	// Get The Temperature For BLS Each Unit
	public Map<String, List<ChannelValuesDTO>> getTempartureForBls(String sessionId) {
		Map<String, List<ChannelValuesDTO>> blsTemp = new HashMap<String, List<ChannelValuesDTO>>();

		try {
			UUTLogBookService uUTLogBookService = new UUTLogBookService();
			UUTLogBookResponse uUTLogBookResponse = uUTLogBookService.getUUTLogBooks(null, null, sessionId, null, null,
					null);

			List<UUTLogBook> logBooks = new ArrayList<UUTLogBook>();
			logBooks = uUTLogBookResponse.getLogBooks();

			List<UUTLogBook> mK1LogBooksList = new ArrayList<UUTLogBook>();
			List<UUTLogBook> mK1ALogBooksList = new ArrayList<UUTLogBook>();
			List<UUTLogBook> mK2LogBooksList = new ArrayList<UUTLogBook>();

			// MK1 Log Books
			mK1LogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT1"))
					.collect(Collectors.toList());

			// MK1A Log Books
			mK1ALogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT2"))
					.collect(Collectors.toList());

			// MK2 Log Books
			mK2LogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT3"))
					.collect(Collectors.toList());

			SessionService sessionService = new SessionService();
			SessionResponse sessionResponse = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<>();
			sessionList = sessionResponse.getListOfSession();

			// Session Filter By BLS MK1
			List<SessionDto> sessionListBlsMk1 = new ArrayList<>();
			sessionListBlsMk1 = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			// Session Filter By BLS MK1A
			List<SessionDto> sessionListBlsMk1A = new ArrayList<>();
			sessionListBlsMk1A = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			List<String> sessionIdsMk1A = sessionListBlsMk1A.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());

			// Session Filter By BLS MK2
			List<SessionDto> sessionListBlsMk2 = new ArrayList<>();
			sessionListBlsMk2 = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			List<String> sessionIdsMk2 = sessionListBlsMk2.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return blsTemp;
	}
	
//	public  TemperatureDashBoardDTO getTemparatureBySessionId(String sessionId)
//	{
//		TemperatureDashBoardDTO response = new TemperatureDashBoardDTO();
//		try {
//			// Fetch From UUTLogBook
//			UUTLogBookService uUTLogBookService = new UUTLogBookService();
//			UUTLogBookResponse uUTLogBookResponse = uUTLogBookService.getUUTLogBooks(null, null, sessionId, null, null,
//					null);
//
//			List<UUTLogBook> logBooks = new ArrayList<UUTLogBook>();
//			logBooks = uUTLogBookResponse.getLogBooks();
//
//			List<ChannelValuesDTO> sessionChannelsDetails = new ArrayList<ChannelValuesDTO>();
//
//			for (UUTLogBook uUTLogBook : logBooks) {
//				ChannelValuesDTO channelValuesDTO = new ChannelValuesDTO();
//				channelValuesDTO = extractTemparture(uUTLogBook.getDetails());
//				if (channelValuesDTO.getCh1AECValue() != 0.0 || channelValuesDTO.getCh1SCValue() != 0.0
//						|| channelValuesDTO.getCh2AECValue() != 0.0 || channelValuesDTO.getCh2SCValue() != 0.0
//						|| channelValuesDTO.getCh3AECValue() != 0.0 || channelValuesDTO.getCh3SCValue() != 0.0
//						|| channelValuesDTO.getCh4AECValue() != 0.0 || channelValuesDTO.getCh4SCValue() != 0.0) {
//
//					channelValuesDTO.setSessionId(uUTLogBook.getSessionId());
//					sessionChannelsDetails.add(channelValuesDTO);
//				}
//			}
//			response.setBlsChannelsValues(sessionChannelsDetails);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return response;
//	}
	
	
	
	
	
	public  TemperatureDashBoardDTO getTemparatureBySessionId(String sessionId)
	{
		TemperatureDashBoardDTO response = new TemperatureDashBoardDTO();
		try {
			// Fetch From UUTLogBook
			UUTLogBookService uUTLogBookService = new UUTLogBookService();
			UUTLogBookResponse uUTLogBookResponse = uUTLogBookService.getUUTLogBooks(null, null, sessionId, null, null,
					null);

			List<UUTLogBook> logBooks = new ArrayList<UUTLogBook>();
			logBooks = uUTLogBookResponse.getLogBooks();
			

			List<ChannelValuesDTO> sessionChannelsDetails = new ArrayList<ChannelValuesDTO>();
			
			boolean isAec = false;
			boolean isSc = false;
			
			////System.out.println("SUJI TEMP DATA Check Dashboard mgmt :" +logBooks.size() + "Session Id : " +  sessionId);
			
			double lowTempAECCh1 = Double.MAX_VALUE;;
			double lowTempAECCh2 = Double.MAX_VALUE;;
			double lowTempAECCh3 = Double.MAX_VALUE;;
			double lowTempAECCh4 = Double.MAX_VALUE;;
			
			
			double highTempAECCh1 = Double.MIN_VALUE;
			double highTempAECCh2 = Double.MIN_VALUE;
			double highTempAECCh3 = Double.MIN_VALUE;
			double highTempAECCh4 = Double.MIN_VALUE;
			
			
			double highTempSCCh1 = Double.MIN_VALUE;
			double highTempSCCh2 = Double.MIN_VALUE;
			double highTempSCCh3 = Double.MIN_VALUE;
			double highTempSCCh4 = Double.MIN_VALUE;
			
			
			double lowTempSCCh1 = Double.MAX_VALUE;;
			double lowTempSCCh2 = Double.MAX_VALUE;;
			double lowTempSCCh3 = Double.MAX_VALUE;;
			double lowTempSCCh4 = Double.MAX_VALUE;;

			for (UUTLogBook uUTLogBook : logBooks) {
				ChannelValuesDTO channelValuesDTO = new ChannelValuesDTO();
				channelValuesDTO = extractTemparture(uUTLogBook.getDetails());
				
				if (channelValuesDTO.getCh1AECValue() != 0.0 || channelValuesDTO.getCh1SCValue() != 0.0
						|| channelValuesDTO.getCh2AECValue() != 0.0 || channelValuesDTO.getCh2SCValue() != 0.0
						|| channelValuesDTO.getCh3AECValue() != 0.0 || channelValuesDTO.getCh3SCValue() != 0.0
						|| channelValuesDTO.getCh4AECValue() != 0.0 || channelValuesDTO.getCh4SCValue() != 0.0) {

					channelValuesDTO.setSessionId(uUTLogBook.getSessionId());
					
//					
//					//System.out.println("CHeck log books::" +uUTLogBook.getTimestamp() );
//					//System.out.println("channelValuesDTO" +channelValuesDTO );
					
					 channelValuesDTO.setTimestamp(uUTLogBook.getTimestamp());
					 
//					 //System.out.println("SUJIIIIIITimestamp : " + channelValuesDTO.getTimestamp());
					
					sessionChannelsDetails.add(channelValuesDTO);
					
				       // -------- AEC LOW TEMP --------
			        if (channelValuesDTO.getCh1AECValue() != 0.0)
			        {
			            lowTempAECCh1 = Math.min(lowTempAECCh1, channelValuesDTO.getCh1AECValue());
			            isAec= true;
			        }

			        if (channelValuesDTO.getCh2AECValue() != 0.0)
			            lowTempAECCh2 = Math.min(lowTempAECCh2, channelValuesDTO.getCh2AECValue());

			        if (channelValuesDTO.getCh3AECValue() != 0.0)
			            lowTempAECCh3 = Math.min(lowTempAECCh3, channelValuesDTO.getCh3AECValue());

			        if (channelValuesDTO.getCh4AECValue() != 0.0)
			            lowTempAECCh4 = Math.min(lowTempAECCh4, channelValuesDTO.getCh4AECValue());
					
			        // -------- SC LOW TEMP --------
			        if (channelValuesDTO.getCh1SCValue() != 0.0)
			            lowTempSCCh1 = Math.min(lowTempSCCh1, channelValuesDTO.getCh1SCValue());

			        if (channelValuesDTO.getCh2SCValue() != 0.0)
			            lowTempSCCh2 = Math.min(lowTempSCCh2, channelValuesDTO.getCh2SCValue());

			        if (channelValuesDTO.getCh3SCValue() != 0.0)
			            lowTempSCCh3 = Math.min(lowTempSCCh3, channelValuesDTO.getCh3SCValue());

			        if (channelValuesDTO.getCh4SCValue() != 0.0)
			            lowTempSCCh4 = Math.min(lowTempSCCh4, channelValuesDTO.getCh4SCValue());
			        
			        
			        // -------- AEC HIGH TEMP --------
			        if (channelValuesDTO.getCh1AECValue() != 0.0)
			            highTempAECCh1 = Math.max(highTempAECCh1, channelValuesDTO.getCh1AECValue());

			        if (channelValuesDTO.getCh2AECValue() != 0.0)
			            highTempAECCh2 = Math.max(highTempAECCh2, channelValuesDTO.getCh2AECValue());

			        if (channelValuesDTO.getCh3AECValue() != 0.0)
			            highTempAECCh3 = Math.max(highTempAECCh3, channelValuesDTO.getCh3AECValue());

			        if (channelValuesDTO.getCh4AECValue() != 0.0)
			            highTempAECCh4 = Math.max(highTempAECCh4, channelValuesDTO.getCh4AECValue());
			        
			     // -------- SC HIGH TEMP --------
			        if (channelValuesDTO.getCh1SCValue() != 0.0)
			        {
			            highTempSCCh1 = Math.max(highTempSCCh1, channelValuesDTO.getCh1SCValue());
			            isSc = true;
			        }

			        if (channelValuesDTO.getCh2SCValue() != 0.0)
			            highTempSCCh2 = Math.max(highTempSCCh2, channelValuesDTO.getCh2SCValue());

			        if (channelValuesDTO.getCh3SCValue() != 0.0)
			            highTempSCCh3 = Math.max(highTempSCCh3, channelValuesDTO.getCh3SCValue());

			        if (channelValuesDTO.getCh4SCValue() != 0.0)
			            highTempSCCh4 = Math.max(highTempSCCh4, channelValuesDTO.getCh4SCValue());
					
				}
			}
			response.setBlsChannelsValues(sessionChannelsDetails);
			
			if(isSc)
			{
			response.setHighTempBlsSCCh1(String.valueOf(highTempSCCh1));
			response.setHighTempBlSCCh2(String.valueOf(highTempSCCh1));
			response.setHighTempBlsSCCh3(String.valueOf(highTempSCCh3));
			response.setHighTempBlsSCCh4(String.valueOf(highTempSCCh4));
			
			response.setLowTempBlsSCCh1(String.valueOf(lowTempSCCh1));
			response.setLowTempBlSCCh2(String.valueOf(lowTempSCCh1));
			response.setLowTempBlsSCCh3(String.valueOf(lowTempSCCh3));
			response.setLowTempBlsSCCh4(String.valueOf(lowTempSCCh4));
			}
			
			if(isAec)
			{
			response.setHighTempBlsAECCh1(String.valueOf(highTempAECCh1));
			response.setHighTempBlsAECCh2(String.valueOf(highTempAECCh2));
			response.setHighTempBlsAECCh3(String.valueOf(highTempAECCh3));
			response.setHighTempBlsAECCh4(String.valueOf(highTempAECCh4));
			
			response.setLowTempBlsAECCh1(String.valueOf(lowTempAECCh1));
			response.setLowTempBlsAECCh2(String.valueOf(lowTempAECCh2));
			response.setLowTempBlsAECCh3(String.valueOf(lowTempAECCh3));
			response.setLowTempBlsAECCh4(String.valueOf(lowTempAECCh4));
			}
			
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}
	 

	public Map<String, TemperatureDashBoardDTO> getTemperatureValuesDashboard() {
		Map<String, TemperatureDashBoardDTO> dashboardChannelValues = new HashMap<String, TemperatureDashBoardDTO>();
		try {

			TemperatureDashBoardDTO temperatureDashBoardDTOMk1 = new TemperatureDashBoardDTO();
			TemperatureDashBoardDTO temperatureDashBoardDTOMk1A = new TemperatureDashBoardDTO();
			TemperatureDashBoardDTO temperatureDashBoardDTOMk2 = new TemperatureDashBoardDTO();

			UUTLogBookService uUTLogBookService = new UUTLogBookService();
			UUTLogBookResponse uUTLogBookResponse = uUTLogBookService.getUUTLogBooks(null, null, null, null, null,
					null);

			List<UUTLogBook> logBooks = new ArrayList<UUTLogBook>();
			logBooks = uUTLogBookResponse.getLogBooks();

			List<UUTLogBook> mK1LogBooksList = new ArrayList<UUTLogBook>();
			List<UUTLogBook> mK1ALogBooksList = new ArrayList<UUTLogBook>();
			List<UUTLogBook> mK2LogBooksList = new ArrayList<UUTLogBook>();

			// MK1 Log Books
			mK1LogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT1"))
					.collect(Collectors.toList());
			// MK1A Log Books
			mK1ALogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT2"))
					.collect(Collectors.toList());
			// MK2 Log Books
			mK2LogBooksList = logBooks.stream().filter(ses -> ses.getUutId().equals("UUT3"))
					.collect(Collectors.toList());

			// For All Sessions
			List<ChannelValuesDTO> mk1ChannelsDetails = new ArrayList<ChannelValuesDTO>();
			List<ChannelValuesDTO> mk1AChannelsDetails = new ArrayList<ChannelValuesDTO>();
			List<ChannelValuesDTO> mk2ChannelsDetails = new ArrayList<ChannelValuesDTO>();

			// For Bls Sessions
			List<ChannelValuesDTO> mk1BlsChannelsDetails = new ArrayList<ChannelValuesDTO>();
			List<ChannelValuesDTO> mk1ABlsChannelsDetails = new ArrayList<ChannelValuesDTO>();
			List<ChannelValuesDTO> mk2BlsChannelsDetails = new ArrayList<ChannelValuesDTO>();

			// Bls Session Id List Fetching..
			SessionService sessionService = new SessionService();
			SessionResponse sessionResponse = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<>();
			sessionList = sessionResponse.getListOfSession();

			// Session Filter By BLS MK1
			List<SessionDto> sessionListBlsMk1 = new ArrayList<>();
			sessionListBlsMk1 = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			List<String> sessionIdsMk1 = sessionListBlsMk1.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());

			// Session Filter By BLS MK1A
			List<SessionDto> sessionListBlsMk1A = new ArrayList<>();
			sessionListBlsMk1A = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			List<String> sessionIdsMk1A = sessionListBlsMk1A.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());

			// Session Filter By BLS MK2
			List<SessionDto> sessionListBlsMk2 = new ArrayList<>();
			sessionListBlsMk2 = sessionList.stream().filter(s -> s.getSessionTypeMasterId().equals("ST2"))
					.collect(Collectors.toList());

			List<String> sessionIdsMk2 = sessionListBlsMk2.stream().map(SessionDto::getSessionId)
					.collect(Collectors.toList());

			for (UUTLogBook uUTLogBook : mK1LogBooksList) {
				ChannelValuesDTO channelValuesDTO = new ChannelValuesDTO();
				channelValuesDTO = extractTemparture(uUTLogBook.getDetails());
				if (channelValuesDTO.getCh1AECValue() != 0.0 || channelValuesDTO.getCh1SCValue() != 0.0
						|| channelValuesDTO.getCh2AECValue() != 0.0 || channelValuesDTO.getCh2SCValue() != 0.0
						|| channelValuesDTO.getCh3AECValue() != 0.0 || channelValuesDTO.getCh3SCValue() != 0.0
						|| channelValuesDTO.getCh4AECValue() != 0.0 || channelValuesDTO.getCh4SCValue() != 0.0) {

					channelValuesDTO.setSessionId(uUTLogBook.getSessionId());
					mk1ChannelsDetails.add(channelValuesDTO);
					if (sessionIdsMk1.contains(uUTLogBook.getSessionId())) {
						mk1BlsChannelsDetails.add(channelValuesDTO);
					}
				}
			}

			temperatureDashBoardDTOMk1.setBlsChannelsValues(mk1BlsChannelsDetails);
			temperatureDashBoardDTOMk1.setUnitChannelsValues(mk1ChannelsDetails);

			// MK1A - All Channel Details
			for (UUTLogBook uUTLogBook : mK1ALogBooksList) {
				ChannelValuesDTO channelValuesDTO = new ChannelValuesDTO();
				channelValuesDTO = extractTemparture(uUTLogBook.getDetails());
				if (channelValuesDTO.getCh1AECValue() != 0.0 || channelValuesDTO.getCh1SCValue() != 0.0
						|| channelValuesDTO.getCh2AECValue() != 0.0 || channelValuesDTO.getCh2SCValue() != 0.0
						|| channelValuesDTO.getCh3AECValue() != 0.0 || channelValuesDTO.getCh3SCValue() != 0.0
						|| channelValuesDTO.getCh4AECValue() != 0.0 || channelValuesDTO.getCh4SCValue() != 0.0) {

					channelValuesDTO.setSessionId(uUTLogBook.getSessionId());
					mk1AChannelsDetails.add(channelValuesDTO);
					if (sessionIdsMk1A.contains(uUTLogBook.getSessionId())) {
						mk1ABlsChannelsDetails.add(channelValuesDTO);
					}
				}
			}
			temperatureDashBoardDTOMk1A.setBlsChannelsValues(mk1ABlsChannelsDetails);
			temperatureDashBoardDTOMk1A.setUnitChannelsValues(mk1AChannelsDetails);

			// MK2 - All Channel Details
			for (UUTLogBook uUTLogBook : mK2LogBooksList) {
				ChannelValuesDTO channelValuesDTO = new ChannelValuesDTO();
				channelValuesDTO = extractTemparture(uUTLogBook.getDetails());
				if (channelValuesDTO.getCh1AECValue() != 0.0 || channelValuesDTO.getCh1SCValue() != 0
						|| channelValuesDTO.getCh2AECValue() != 0.0 || channelValuesDTO.getCh2SCValue() != 0.0
						|| channelValuesDTO.getCh3AECValue() != 0.0 || channelValuesDTO.getCh3SCValue() != 0.0
						|| channelValuesDTO.getCh4AECValue() != 0.0 || channelValuesDTO.getCh4SCValue() != 0.0) {

					channelValuesDTO.setSessionId(uUTLogBook.getSessionId());
					mk2ChannelsDetails.add(channelValuesDTO);
					if (sessionIdsMk2.contains(uUTLogBook.getSessionId())) {
						mk2BlsChannelsDetails.add(channelValuesDTO);
					}
				}
			}

			temperatureDashBoardDTOMk2.setBlsChannelsValues(mk2BlsChannelsDetails);
			temperatureDashBoardDTOMk2.setUnitChannelsValues(mk2ChannelsDetails);

			dashboardChannelValues.put("MK1", temperatureDashBoardDTOMk1);
			dashboardChannelValues.put("MK1A", temperatureDashBoardDTOMk1A);
			dashboardChannelValues.put("MK2", temperatureDashBoardDTOMk2);

		} catch (Exception ex) {
			////System.out.println(ex.getLocalizedMessage());
		}
		return dashboardChannelValues;
	}

	public ChannelValuesDTO getTempForUnit(String line) {
		ChannelValuesDTO response = new ChannelValuesDTO();
		try {

		} catch (Exception ex) {

			////System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	public static Map<String, Double> extractChannelValues(String line) {
		Map<String, Double> channelValues = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("CH(\\d+):\\s*([\\d.]+)");
		Matcher matcher = pattern.matcher(line);

		while (matcher.find()) {
			String channel = "CH" + matcher.group(1);
			double value = Double.parseDouble(matcher.group(2));
			channelValues.put(channel, value);
		}

		return channelValues;
	}

	public ChannelValuesDTO extractTemparture(String line) {

		ChannelValuesDTO dto = new ChannelValuesDTO();
		dto.setTimestamp(new Date()); 
		try {
			Pattern CHANNEL_PATTERN = Pattern.compile("CH(\\d+):\\s*([\\d.]+)");
			Matcher matcher = CHANNEL_PATTERN.matcher(line);

			boolean isAEC = line.contains("AEC");
			boolean isSC = line.contains("SC");

			while (matcher.find()) {
				int channel = Integer.parseInt(matcher.group(1));
				double value = Double.parseDouble(matcher.group(2));

				if (isAEC) {
					switch (channel) {
					case 1:
						dto.setCh1AECValue(value);
						break;
					case 2:
						dto.setCh2AECValue(value);
						break;
					case 3:
						dto.setCh3AECValue(value);
						break;
					case 4:
						dto.setCh4AECValue(value);
						break;
					default:
						break;
					}
				} else if (isSC) {
					switch (channel) {
					case 1:
						dto.setCh1SCValue(value);
						break;
					case 2:
						dto.setCh2SCValue(value);
						break;
					case 3:
						dto.setCh3SCValue(value);
						break;
					case 4:
						dto.setCh4SCValue(value);
						break;
					default:
						break;
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(); // optional: log the error
		}

		return dto;
	}
	
	
	public Response addDashboardFile(String uutType, String serialNo, String fileUploaded) {

	    Response response = new Response();

	    try {
	        DashBoardFilesUploadServices dashBoardFilesUploadServices =
	                new DashBoardFilesUploadServices();

	        DashBoardFilesUploading dashBoardFilesUploading =
	                new DashBoardFilesUploading();

	        response = dashBoardFilesUploadServices.addingFilesUpload(
	                uutType, serialNo, dashBoardFilesUploading, fileUploaded);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return response;
	}

	
	public List<DashBoardFilesUploading> getDashboardFiles(String uutType, String sNo) {

	    List<DashBoardFilesUploading> files = new ArrayList<>();

	    try {
	        DashBoardFilesUploadServices service =
	                new DashBoardFilesUploadServices();

	        GetObjResponse getObj = service.getUplodedFile(uutType, sNo);

	        if (getObj != null && getObj.getObject() != null) {
	            files = (List<DashBoardFilesUploading>) getObj.getObject();
	        }

	    } catch (Exception ex) {
	        ////System.out.println(ex.getLocalizedMessage());
	    }

	    return files;
	}
	
//	Failure stages::
	public ResultSessionStagesDetailsResponse getStagesFailureDetailsForSession(String sessionId) {
		DFCCConstant.dashBoardLoading =true;
//		//System.out.println("Backend Mani Starting Area ::1 " +DFCCConstant.dashBoardLoading );
		ResultSessionStagesDetailsResponse response = new ResultSessionStagesDetailsResponse();
		try {
			ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
					
			// Add Both Files In Result DTO List...
			List<ResultSessionStagesDetailsDTO> finalList = new ArrayList<ResultSessionStagesDetailsDTO>();

			// finalList.addAll(sessionStagesListRunnedList);
			SessionManagement sessionManagemment = new SessionManagement();
			
			Map<String, String> stageIdName = sessionManagemment.getAllStageIdName();
			SessionDTOResponse  sesionDto = sessionManagemment.getSessionDetailById(sessionId);
			
			SessionTimingService SessionTimingService = new SessionTimingService();
			List<SessionTiming> otherStagesList = SessionTimingService.getSessionTimeBySessionId(sessionId);
			List<ResultSessionStagesDetailsDTO> otherStagesListRunnedList = new ArrayList<ResultSessionStagesDetailsDTO>();

			Map<String, String> stageIdStatus = new HashMap<String, String>();
			stageIdStatus = sessionManagemment.getStageIdStatus(sessionId);
			Map<String, String> stageIdPath = new HashMap<String, String>();
			stageIdPath = sessionManagemment.getStageIdFullPath();

			for (SessionTiming sessionTiming : otherStagesList) {
				ResultSessionStagesDetailsDTO otherStages = new ResultSessionStagesDetailsDTO();

				String parentName = sessionManagemment.getFullPathForLeafIds(sessionTiming.getStageId());
				////System.out.println("StageId Path Map"+stageIdPath);
				////System.out.println("stageId"+sessionTiming.getStageId());
				//String parentName = stageIdPath.get(sessionTiming.getStageId());
				parentName = parentName.substring(0, parentName.indexOf("/"));
				if (parentName.equals("LRU Test") || parentName.equals("Advanced Test")
						|| parentName.equals("Self Test")) {
					otherStages.setTestMode(parentName + "-" + stageIdName.get(sessionTiming.getStageId()));

					otherStages.setTestType(parentName);
				} else {
					otherStages.setTestMode(
							"SESSION TEST - " + parentName + "-" + stageIdName.get(sessionTiming.getStageId()));
					otherStages.setTestType("SESSION");

				}

				otherStages.setStartTime(sessionTiming.getStartDateTime());
				otherStages.setEndTime(sessionTiming.getEndDateTime());
				otherStages.setNoOfFilesExecuted(sessionTiming.getNoOfFileExecuted());
				otherStages.setFailedFiles(sessionTiming.getNoOfFailedExecuted());
				otherStages.setTimeTakenForExecution(resultExecutionManagement.formatSecondsToHHMMSS(sessionTiming.getRunnedSeconds()));

				if (sessionTiming.getNoOfFailedExecuted() == 0) {
					otherStages.setResult("Success");
				} else {
					otherStages.setResult("Failed");
				}

				if (!sessionTiming.getStageType().equals("OTHER")) {
					if (!stageIdStatus.get(sessionTiming.getStageId()).equals("pending")) {

						if (stageIdStatus.get(sessionTiming.getStageId()).equals("completed")) {
							// if (sessionTiming.getStageId().equals("completed")) {
							otherStages.setStatus("Completed");
							otherStages.setResult("Pass");
						} else {
							otherStages.setResult("Fail");
							otherStages.setStatus("Completed");
						}

					} else {
						otherStages.setResult("pending");
						otherStages.setStatus("pending");
					}
				} else {
					if (sessionTiming.getNoOfFailedExecuted() == 0 && sessionTiming.getNoOfFileExecuted() == 0) {
						otherStages.setStatus("running");
						otherStages.setResult("running");
					} else {
						otherStages.setResult("Completed");
						if (sessionTiming.getNoOfFailedExecuted() > 0) {
							otherStages.setStatus("Completed");
							otherStages.setResult("Fail");
						} else {
							otherStages.setStatus("Completed");
							otherStages.setResult("Pass");
						}
					}
				}
				otherStages.setStageId(sessionTiming.getStageId());
				otherStages.setStage(sesionDto.getSessionName());
				otherStages.setStageMappingId(sessionTiming.getStageType());
				finalList.add(otherStages);
			}
			DFCCConstant.dashBoardLoading =false;
//			//System.out.println("Backend Mani False Area ::1 " +DFCCConstant.dashBoardLoading );
			finalList = finalList.stream().filter(e->e.getResult().equals("Fail")).collect(Collectors.toList());
			response.setResultSessionStagesDetailsDTOList(finalList);
			response.setCode(1);
			response.setMsg("Fetched");
			
		} catch (Exception ex) {
			DFCCConstant.dashBoardLoading =false;
//			//System.out.println("Backend Mani False Area ::1 " +DFCCConstant.dashBoardLoading );
			response.setCode(0);
			response.setMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
			ex.printStackTrace();
			Debug.printDebug(ex.getLocalizedMessage());
			
		}
		return response;
	}
	
	
	public ResultSessionStagesDetailsResponse getStagesFailureDetailsForSessionIDS(String sessionId) {
		DFCCConstant.dashBoardLoading =true;
//		//System.out.println("Backend Mani Starting Area ::1 " +DFCCConstant.dashBoardLoading );
		ResultSessionStagesDetailsResponse response = new ResultSessionStagesDetailsResponse();
		try {
			ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
					
			// Add Both Files In Result DTO List...
			List<ResultSessionStagesDetailsDTO> finalList = new ArrayList<ResultSessionStagesDetailsDTO>();

			// finalList.addAll(sessionStagesListRunnedList);
			SessionManagement sessionManagemment = new SessionManagement();
			
			Map<String, String> stageIdName = sessionManagemment.getAllStageIdName();
			SessionDTOResponse  sesionDto = sessionManagemment.getSessionDetailById(sessionId);
			
			
			
			
			
			SessionTimingService SessionTimingService = new SessionTimingService();
			List<SessionTiming> otherStagesList = SessionTimingService.getSessionTimeBySessionId(sessionId);
			List<ResultSessionStagesDetailsDTO> otherStagesListRunnedList = new ArrayList<ResultSessionStagesDetailsDTO>();

			Map<String, String> stageIdStatus = new HashMap<String, String>();
			stageIdStatus = sessionManagemment.getStageIdStatus(sessionId);
			Map<String, String> stageIdPath = new HashMap<String, String>();
			stageIdPath = sessionManagemment.getStageIdFullPath();

			for (SessionTiming sessionTiming : otherStagesList) {
				ResultSessionStagesDetailsDTO otherStages = new ResultSessionStagesDetailsDTO();

				String parentName = sessionManagemment.getFullPathForLeafIds(sessionTiming.getStageId());
				////System.out.println("StageId Path Map"+stageIdPath);
				////System.out.println("stageId"+sessionTiming.getStageId());
				//String parentName = stageIdPath.get(sessionTiming.getStageId());
				parentName = parentName.substring(0, parentName.indexOf("/"));
				if (parentName.equals("LRU Test") || parentName.equals("Advanced Test")
						|| parentName.equals("Self Test")) {
					otherStages.setTestMode(parentName + "-" + stageIdName.get(sessionTiming.getStageId()));

					otherStages.setTestType(parentName);
				} else {
					otherStages.setTestMode(
							"SESSION TEST - " + parentName + "-" + stageIdName.get(sessionTiming.getStageId()));
					otherStages.setTestType("SESSION");

				}

				otherStages.setStartTime(sessionTiming.getStartDateTime());
				otherStages.setEndTime(sessionTiming.getEndDateTime());
				otherStages.setNoOfFilesExecuted(sessionTiming.getNoOfFileExecuted());
				otherStages.setFailedFiles(sessionTiming.getNoOfFailedExecuted());
				otherStages.setTimeTakenForExecution(resultExecutionManagement.formatSecondsToHHMMSS(sessionTiming.getRunnedSeconds()));

				if (sessionTiming.getNoOfFailedExecuted() == 0) {
					otherStages.setResult("Success");
				} else {
					otherStages.setResult("Failed");
				}

				if (!sessionTiming.getStageType().equals("OTHER")) {
					if (!stageIdStatus.get(sessionTiming.getStageId()).equals("pending")) {

						if (stageIdStatus.get(sessionTiming.getStageId()).equals("completed")) {
							// if (sessionTiming.getStageId().equals("completed")) {
							otherStages.setStatus("Completed");
							otherStages.setResult("Pass");
						} else {
							otherStages.setResult("Fail");
							otherStages.setStatus("Completed");
						}

					} else {
						otherStages.setResult("pending");
						otherStages.setStatus("pending");
					}
				} else {
					if (sessionTiming.getNoOfFailedExecuted() == 0 && sessionTiming.getNoOfFileExecuted() == 0) {
						otherStages.setStatus("running");
						otherStages.setResult("running");
					} else {
						otherStages.setResult("Completed");
						if (sessionTiming.getNoOfFailedExecuted() > 0) {
							otherStages.setStatus("Completed");
							otherStages.setResult("Fail");
						} else {
							otherStages.setStatus("Completed");
							otherStages.setResult("Pass");
						}
					}
				}
				otherStages.setStageId(sessionTiming.getStageId());
				otherStages.setStage(sesionDto.getSessionName());
				otherStages.setStageMappingId(sessionTiming.getStageType());
				finalList.add(otherStages);
			}
			DFCCConstant.dashBoardLoading =false;
//			//System.out.println("Backend Mani False Area ::1 " +DFCCConstant.dashBoardLoading );
			finalList = finalList.stream().filter(e->e.getResult().equals("Fail")).collect(Collectors.toList());
			response.setResultSessionStagesDetailsDTOList(finalList);
			response.setCode(1);
			response.setMsg("Fetched");
			
		} catch (Exception ex) {
			DFCCConstant.dashBoardLoading =false;
//			//System.out.println("Backend Mani False Area ::1 " +DFCCConstant.dashBoardLoading );
			response.setCode(0);
			response.setMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
			ex.printStackTrace();
			Debug.printDebug(ex.getLocalizedMessage());
			
		}
		return response;
	}
	
	




}
