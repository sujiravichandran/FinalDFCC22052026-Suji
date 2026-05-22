package com.teclever.dfcc.model;

public class BriefData {
	
	private String id;
	private String slNo;
	private String testMode;
	private String resultFileName;
	private String timeOfExecution;
	private String result;
	
	
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
	
	
	public String getResultFileName() {
		return resultFileName;
	}
	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}
	public String getTimeOfExecution() {
		return timeOfExecution;
	}
	public void setTimeOfExecution(String timeOfExecution) {
		this.timeOfExecution = timeOfExecution;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	
	
}
