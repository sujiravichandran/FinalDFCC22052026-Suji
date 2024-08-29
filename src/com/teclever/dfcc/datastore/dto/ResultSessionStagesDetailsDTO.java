package com.teclever.dfcc.datastore.dto;

public class ResultSessionStagesDetailsDTO {
	
	private String sessionId;
	private String stageMappingId;
	private String stage;
	private String startTime;
	private String endTime;
	private String status;
	private String result;
	private String timeTakenForExecution;
	private int noOfFilesExecuted;
	private int failedFiles;
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getStageMappingId() {
		return stageMappingId;
	}
	public void setStageMappingId(String stageMappingId) {
		this.stageMappingId = stageMappingId;
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
	public int getNoOfFilesExecuted() {
		return noOfFilesExecuted;
	}
	public void setNoOfFilesExecuted(int noOfFilesExecuted) {
		this.noOfFilesExecuted = noOfFilesExecuted;
	}
	public int getFailedFiles() {
		return failedFiles;
	}
	public void setFailedFiles(int failedFiles) {
		this.failedFiles = failedFiles;
	}
	
	
	
	

}
