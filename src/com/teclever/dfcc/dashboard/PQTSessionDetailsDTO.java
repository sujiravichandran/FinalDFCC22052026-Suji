package com.teclever.dfcc.dashboard;

public class PQTSessionDetailsDTO {
	private String sessionName;
	private String sessionId;
	private String lastPQTStage;
	private String startTime;
	private boolean pqtConducted = false;
	private String pqtCreatedDate;
	private String endTimeTime;
	private String sessionStatus;
	
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTimeTime() {
		return endTimeTime;
	}
	public void setEndTimeTime(String endTimeTime) {
		this.endTimeTime = endTimeTime;
	}
	public String getSessionStatus() {
		return sessionStatus;
	}
	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}
	public boolean isPqtConducted() {
		return pqtConducted;
	}
	public void setPqtConducted(boolean pqtConducted) {
		this.pqtConducted = pqtConducted;
	}
	public String getLastPQTStage() {
		return lastPQTStage;
	}
	public void setLastPQTStage(String lastPQTStage) {
		this.lastPQTStage = lastPQTStage;
	}
	public String getPqtCreatedDate() {
		return pqtCreatedDate;
	}
	public void setPqtCreatedDate(String pqtCreatedDate) {
		this.pqtCreatedDate = pqtCreatedDate;
	}
	
	

}
