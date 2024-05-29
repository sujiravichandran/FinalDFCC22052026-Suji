package com.teclever.dfcc.datastore.sessionmanagement;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.entities.FaultCodeSessionMapping;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.service.FaultCodeSessionMappingService;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.datastore.dto.FaultCodeSessionMappingDTO;
import com.teclever.dfcc.datastore.dto.SessionDTO;
import com.teclever.dfcc.datastore.dto.SessionToStagesMappingDTO;

public class SessionManagement {

	public Response createSession(SessionDTO sessionDTO)
	{
		Response res = new Response();
		try
		{
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
			
			GetObjResponse resObj = sessionService.addSession(sessionDto);
			SessionDto sessionResponseDto = (SessionDto) resObj.getObject();
			String sessionId = sessionResponseDto.getSessionId();
			List<SessionToStagesMappingDTO>sessionStages = new ArrayList<SessionToStagesMappingDTO>();
			sessionStages = sessionDTO.getSessionStagesList();
			List<SessionStagesMapping>sessionToStagesMappingList = new ArrayList<SessionStagesMapping>();
			for(SessionToStagesMappingDTO sessionToStagesMappingDTO:sessionStages)
			{
				SessionStagesMapping sessionStagesMapping = new SessionStagesMapping();
				sessionStagesMapping.setRepeatCount(1);
				sessionStagesMapping.setRunCount(1);
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
			List<FaultCodeSessionMapping>faultCodeDataList= new ArrayList<FaultCodeSessionMapping>();
//			for(FaultCodeSessionMappingDTO faultCodeSessionMappingDTO:faultCodeList)
//			{
//				FaultCodeSessionMapping faultCodeSessionMapping = new FaultCodeSessionMapping();
//				faultCodeSessionMapping.setFaultCodeId(faultCodeSessionMappingDTO.getFaultCodeId());
//				faultCodeSessionMapping.setFaultCodeLinkSessionId(faultCodeSessionMappingDTO.getFaultCodeLinkSessionId());
//				faultCodeSessionMapping.setSessionId(sessionId);
//				faultCodeDataList.add(faultCodeSessionMapping);
//			}
			FaultCodeSessionMappingService faultCodeSessionMappingService= new FaultCodeSessionMappingService();
			for(FaultCodeSessionMappingDTO faultCodeSessionMappingDTO:faultCodeList )
			{
				faultCodeSessionMappingService.addFaultCodeSession(faultCodeSessionMappingDTO.getFaultCodeId(), sessionId);
			}
			res.setResponseCode(1);
			res.setResponseMessage("Session Created Successfully..!");
			
		}catch(Exception ex)
		{
			res.setResponseCode(0);
			res.setResponseMessage("Session Not Created");

		}
		return res;
	}
	
}
