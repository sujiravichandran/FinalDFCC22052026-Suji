package com.teclever.dfcc.advanceddataanalysis;

import java.util.Map;

public class UnitSessionDetailsDTO {
	
	private String dfccSlNo;
	private String sessionName;
	private String sessionId;
	private String stageId;
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	private String stageName;
	
	
	public String getDfccSlNo() {
		return dfccSlNo;
	}
	public void setDfccSlNo(String dfccSlNo) {
		this.dfccSlNo = dfccSlNo;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
	
	

}
