package com.teclever.dfcc.datastore.dto;

public class SessionStagesToSelectedFileMappingDTO {

	private int sessionStagesSelectedTestFilesId;

	private String stagesId;

	private String testFilesId;

	public int getSessionStagesSelectedTestFilesId() {
		return sessionStagesSelectedTestFilesId;
	}

	public void setSessionStagesSelectedTestFilesId(int sessionStagesSelectedTestFilesId) {
		this.sessionStagesSelectedTestFilesId = sessionStagesSelectedTestFilesId;
	}

	public String getStagesId() {
		return stagesId;
	}

	public void setStagesId(String stagesId) {
		this.stagesId = stagesId;
	}

	public String getTestFilesId() {
		return testFilesId;
	}

	public void setTestFilesId(String testFilesId) {
		this.testFilesId = testFilesId;
	}

}
