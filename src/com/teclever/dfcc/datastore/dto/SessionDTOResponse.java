package com.teclever.dfcc.datastore.dto;

import java.sql.Date;
import java.util.List;

import com.teclever.datastore.dto.Response;

public class SessionDTOResponse {

	private String sessionId;
	private String uutId;
	private String sessionName;
	private String sessionTypeMasterId;
	private String userId;
	private String dfccSNo;
	private String dfccPartNo;
	private Date creationDate;
	private Date startDate;
	private Date endDate;
	private String startRemarks;
	private List<StageObject> sessionStagesList;
	private List<FaultCodeDTO> faultCodeMappingList;

	private Response response;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUutId() {
		return uutId;
	}

	public void setUutId(String uutId) {
		this.uutId = uutId;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionTypeMasterId() {
		return sessionTypeMasterId;
	}

	public void setSessionTypeMasterId(String sessionTypeMasterId) {
		this.sessionTypeMasterId = sessionTypeMasterId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDfccSNo() {
		return dfccSNo;
	}

	public void setDfccSNo(String dfccSNo) {
		this.dfccSNo = dfccSNo;
	}

	public String getDfccPartNo() {
		return dfccPartNo;
	}

	public void setDfccPartNo(String dfccPartNo) {
		this.dfccPartNo = dfccPartNo;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartRemarks() {
		return startRemarks;
	}

	public void setStartRemarks(String startRemarks) {
		this.startRemarks = startRemarks;
	}

	public List<StageObject> getSessionStagesList() {
		return sessionStagesList;
	}

	public void setSessionStagesList(List<StageObject> sessionStagesList) {
		this.sessionStagesList = sessionStagesList;
	}

	public List<FaultCodeDTO> getFaultCodeMappingList() {
		return faultCodeMappingList;
	}

	public void setFaultCodeMappingList(List<FaultCodeDTO> faultCodeMappingList) {
		this.faultCodeMappingList = faultCodeMappingList;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
