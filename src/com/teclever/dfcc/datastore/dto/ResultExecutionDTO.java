package com.teclever.dfcc.datastore.dto;

public class ResultExecutionDTO {
	
	private String rdfFile;
	private String rdfFilePath;
	private String DStarCount;
	private String status;
	private String endTime;
	private String testFileId;
	private String testFileName;
	private String stageId;
	private String sessionId;
	private String sessionSerialNo;
	private String systemInfoId;
	private String testMode;
	
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	public String getSystemInfoId() {
		return systemInfoId;
	}
	public void setSystemInfoId(String systemInfoId) {
		this.systemInfoId = systemInfoId;
	}
	private String stageName;
	private String sessionName;
	
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getRdfFile() {
		return rdfFile;
	}
	public void setRdfFile(String rdfFile) {
		this.rdfFile = rdfFile;
	}
	public String getRdfFilePath() {
		return rdfFilePath;
	}
	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}
	public String getDStarCount() {
		return DStarCount;
	}
	public void setDStarCount(String dStarCount) {
		DStarCount = dStarCount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTestFileId() {
		return testFileId;
	}
	public void setTestFileId(String testFileId) {
		this.testFileId = testFileId;
	}
	public String getTestFileName() {
		return testFileName;
	}
	public void setTestFileName(String testFileName) {
		this.testFileName = testFileName;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionSerialNo() {
		return sessionSerialNo;
	}
	public void setSessionSerialNo(String sessionSerialNo) {
		this.sessionSerialNo = sessionSerialNo;
	}
	
	

}
