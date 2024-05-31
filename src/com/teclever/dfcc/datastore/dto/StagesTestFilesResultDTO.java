package com.teclever.dfcc.datastore.dto;

public class StagesTestFilesResultDTO {

	private String sessionStagesTestFilesResultId;

	private String sessionId;

	private String stageId;

	private String testFileId;

	private String systemResultInfoId;

	private String rdfPath;

	private String rdfFileName;

	private String testStatus;

	private String dStarCount;

	private String startTime;

	public StagesTestFilesResultDTO(String sessionStagesTestFilesResultId, String sessionId, String stageId,
			String testFileId, String systemResultInfoId, String rdfPath, String rdfFileName, String testStatus,
			String dStarCount, String startTime, String endTime) {
		super();
		this.sessionStagesTestFilesResultId = sessionStagesTestFilesResultId;
		this.sessionId = sessionId;
		this.stageId = stageId;
		this.testFileId = testFileId;
		this.systemResultInfoId = systemResultInfoId;
		this.rdfPath = rdfPath;
		this.rdfFileName = rdfFileName;
		this.testStatus = testStatus;
		this.dStarCount = dStarCount;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public StagesTestFilesResultDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSessionStagesTestFilesResultId() {
		return sessionStagesTestFilesResultId;
	}

	public void setSessionStagesTestFilesResultId(String sessionStagesTestFilesResultId) {
		this.sessionStagesTestFilesResultId = sessionStagesTestFilesResultId;
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

	public String getTestFileId() {
		return testFileId;
	}

	public void setTestFileId(String testFileId) {
		this.testFileId = testFileId;
	}

	public String getSystemResultInfoId() {
		return systemResultInfoId;
	}

	public void setSystemResultInfoId(String systemResultInfoId) {
		this.systemResultInfoId = systemResultInfoId;
	}

	public String getRdfPath() {
		return rdfPath;
	}

	public void setRdfPath(String rdfPath) {
		this.rdfPath = rdfPath;
	}

	public String getRdfFileName() {
		return rdfFileName;
	}

	public void setRdfFileName(String rdfFileName) {
		this.rdfFileName = rdfFileName;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	public String getdStarCount() {
		return dStarCount;
	}

	public void setdStarCount(String dStarCount) {
		this.dStarCount = dStarCount;
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

	private String endTime;

}
