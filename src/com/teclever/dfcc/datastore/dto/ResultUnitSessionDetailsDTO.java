package com.teclever.dfcc.datastore.dto;

public class ResultUnitSessionDetailsDTO {

	private int sNo;
	private String sessionId;
	private String sessionType;
	private String sessionName;
	private String startTime;
	private String endTime;
	private String sessionStatus;
	private String sessionResults;
	private String stratRemarks;
	private String endRemarks;
	
	public String getStratRemarks() {
		return stratRemarks;
	}
	public void setStratRemarks(String stratRemarks) {
		this.stratRemarks = stratRemarks;
	}
	public String getEndRemarks() {
		return endRemarks;
	}
	public void setEndRemarks(String endRemarks) {
		this.endRemarks = endRemarks;
	}
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSessionStatus() {
		return sessionStatus;
	}
	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}
	public String getSessionResults() {
		return sessionResults;
	}
	public void setSessionResults(String sessionResults) {
		this.sessionResults = sessionResults;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
	

}
