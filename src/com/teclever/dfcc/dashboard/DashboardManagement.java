package com.teclever.dfcc.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.TrailSessionDto;
import com.teclever.datastore.dto.TrailSessionResponse;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.datastore.service.UserLoginDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class DashboardManagement {

	// For Getting Session Details By Last Sl No ....
	public ResultUnitSessionDetailsResponse getSessionDetailsForResultsByUnit(String uutTypeId) {
		ResultUnitSessionDetailsResponse res = new ResultUnitSessionDetailsResponse();
		try {
			UserLoginDetailsService userLoginDetailsService = new UserLoginDetailsService();
			Set<String> setOfUserLoginId = userLoginDetailsService.getUsersByRoleId(UserData.getRoleId());
			SessionService sessionService = new SessionService();
//				SessionResponse sessionResponse = sessionService.getAllSession();
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
//			System.out.println(ex.getMessage());
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
				ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = pqtSessionList.get(pqtSessionList.size()-1);
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
//			System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	// get All Details For Dashboard
	public DashBoardDetailsDTO getDashboardDetails(String uutTypeId) {
		DashBoardDetailsDTO response = new DashBoardDetailsDTO();

		try {
			 ProductionSessionDetailsDTO productionSessionDetailsDTO  = new ProductionSessionDetailsDTO();
			 productionSessionDetailsDTO= getProductionDetailsForDashBoard(uutTypeId);
			 ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
			 //Production Session Details List
			 response.setProductionSessionDetailsDTOList(productionSessionDetailsDTO.getProdList());
			
			 //PQT Details
			 PQTSessionDetailsDTO pqtResponse = new PQTSessionDetailsDTO();
			 List<ResultUnitSessionDetailsDTO>	 sessionList = productionSessionDetailsDTO.getSessionList();
			 
			 //All Session Details List By Sl No...
			 response.setAllSessionListBySlNo(sessionList);
			 
				List<ResultUnitSessionDetailsDTO> pqtSessionList = sessionList.stream()
						.filter(sesType -> sesType.getSessionType().equals("PQT")).collect(Collectors.toList());
				if (pqtSessionList.size() > 0) {
					ResultUnitSessionDetailsDTO resultUnitSessionDetailsDTO = pqtSessionList.get(pqtSessionList.size()-1);
					pqtResponse.setPqtConducted(true);
					pqtResponse.setStartTime(resultUnitSessionDetailsDTO.getStartTime());
					pqtResponse.setSessionName(resultUnitSessionDetailsDTO.getSessionName());

					String sessionId = resultUnitSessionDetailsDTO.getSessionId();

		
					ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
					resultExecutionResponse = resultExecutionManagement
							.getResultExecutionListBriefListForSession(sessionId);

					List<ResultExecutionDTO> resultDTOList = resultExecutionResponse.getResultDTOList(); // get the list
					if (!resultDTOList.isEmpty()) {
					    ResultExecutionDTO resultExecutionDTO = resultDTOList.get(resultDTOList.size() - 1); // get last element
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
			 
			 //PQT Details 	
			 response.setpQTSessionDetailsDTO(pqtResponse);
			 
			 
			 String slNo = DFCCConstant.sessionIdDfccSlNo.get(StateMachine.currentSessionDetails.getSessionId());
			 
			 //All Failure List
			 ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
			 resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForUnit(uutTypeId,slNo);
			 response.setResultDetailedList(resultDetailedResponse.getResultDetailedList());
			 
			 response.setLastTestedDate(resultDetailedResponse.getLastTestedDate());
			 response.setSerialNo(slNo);
			
		} catch (Exception ex) {

			response.setEmsg(ex.getLocalizedMessage());

		}
		return response;
	}

}
