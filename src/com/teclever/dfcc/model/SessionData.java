package com.teclever.dfcc.model;

public class SessionData {
	
	private String id;
	private String slNo;
//	Changed by Vignesh 31-07-25 for moving testMode location	
	private String testMode;
	private String stage;
	private String status;
	private String result;
	private String noOfFilesExecuted;
	private String failedFiles;
	private String startTime;
	private String endTime;
	private String timeTakenForExecution;
	private String sessionType;
	
	
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSlNo() {
		return slNo;
	}
	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTimeTakenForExecution() {
		return timeTakenForExecution;
	}
	public void setTimeTakenForExecution(String timeTakenForExecution) {
		this.timeTakenForExecution = timeTakenForExecution;
	}
	public String getNoOfFilesExecuted() {
		return noOfFilesExecuted;
	}
	public void setNoOfFilesExecuted(String noOfFilesExecuted) {
		this.noOfFilesExecuted = noOfFilesExecuted;
	}
	public String getFailedFiles() {
		return failedFiles;
	}
	public void setFailedFiles(String failedFiles) {
		this.failedFiles = failedFiles;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	
	
}
