package com.teclever.dfcc.model;

public class SessionData {
	
	private int id;
	private String slNo;
	private String stage;
	private String startTime;
	private String endTime;
	private String status;
	private String result;
	private String timeTakenForExecution;
	private String noOfFilesExecuted;
	private String failedFiles;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	
	
}
