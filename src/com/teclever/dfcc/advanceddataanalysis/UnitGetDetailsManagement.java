package com.teclever.dfcc.advanceddataanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.dto.SessionStagesListResponse;
import com.teclever.datastore.entities.SessionStagesStatus;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesStatusService;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;

public class UnitGetDetailsManagement {
	// Session Details Get By Serial No
	public List<UnitSessionDetailsDTO> getUnitSessionDetailsForSlNo(String dfccSlno) {

		List<UnitSessionDetailsDTO> res = new ArrayList<>();
		try {

			SessionService sessionService = new SessionService();
			SessionResponse resSession = sessionService.getAllSession();

			List<SessionDto> sessionList = resSession.getListOfSession();
			sessionList = sessionList.stream().filter(ses -> ses.getDfccSNo().equals(dfccSlno))
					.collect(Collectors.toList());

			// Ended Filter
//			sessionList = sessionList.stream().filter(ses -> !ses.getEndRemarks().equals("")|| !ses.getEndRemarks().equals(null))
//				.collect(Collectors.toList());

			for (SessionDto sessionDto : sessionList) {

				UnitSessionDetailsDTO unitSessionDetailsDTO = new UnitSessionDetailsDTO();
				unitSessionDetailsDTO.setSessionId(sessionDto.getSessionId());
				unitSessionDetailsDTO.setSessionName(sessionDto.getSessionName());
				res.add(unitSessionDetailsDTO);

			}

		} catch (Exception ex) {
			////System.out.println(ex.getLocalizedMessage());
		}

		return res;
	}
	
	public List<UnitSessionDetailsDTO> getStageDetailsForSession(String sessionId) {

		List<UnitSessionDetailsDTO> res = new ArrayList<>();
		try {
			SessionStagesStatusService sessionStagesStatusService = new SessionStagesStatusService();
			SessionStagesListResponse sessionStagesListResponse = new SessionStagesListResponse();
			sessionStagesListResponse = sessionStagesStatusService.getSessionStagesStatus(sessionId);
			List<SessionStagesStatus> lst = new ArrayList<SessionStagesStatus>();
			lst = sessionStagesListResponse.getSessionStagesList();

			ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
			Map<String, String> stageIdName = new HashMap<String, String>();
			stageIdName = resultExecutionManagement.getStageIdName();
			
			for (SessionStagesStatus sessionStagesStatus : lst) {
				UnitSessionDetailsDTO unitSessionDetailsDTO = new UnitSessionDetailsDTO();

				unitSessionDetailsDTO.setStageId(sessionStagesStatus.getStageId());
				unitSessionDetailsDTO.setStageName(stageIdName.get(sessionStagesStatus.getLevelStageOneId())+"-" + stageIdName.get(sessionStagesStatus.getStageId()));
				////System.out.println("Inside   Method ::Stage Id"+sessionStagesStatus.getStageId()  + "    Stage Name"+stageIdName.get(sessionStagesStatus.getStageId()));
				unitSessionDetailsDTO.setSessionId(sessionId);
				res.add(unitSessionDetailsDTO);
			}

		} catch (Exception ex) {
			////System.out.println(ex.getLocalizedMessage());
		}

		return res;
	}

	public List<String> getAllDfccSerialNo() {
		List<String> uniqueDfccSNoList = new ArrayList<String>();

		try {

			SessionService sessionService = new SessionService();
			SessionResponse resSession = sessionService.getAllSession();

			List<SessionDto> sessionList = resSession.getListOfSession();

			uniqueDfccSNoList = sessionList.stream().map(SessionDto::getDfccSNo) 
					.filter(Objects::nonNull) 
					.distinct() 
					.collect(Collectors.toList());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return uniqueDfccSNoList;
	}

}
