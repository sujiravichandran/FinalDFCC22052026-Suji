package com.teclever.dfcc.datastore.dto;

public class StagesTestFilesDetailDTO {

	private String testFileId;
	private String testFileStatus;
	private boolean disableFlag;
	
	public boolean isDisableFlag() {
		return disableFlag;
	}
	public void setDisableFlag(boolean disableFlag) {
		this.disableFlag = disableFlag;
	}
	public String getTestFileId() {
		return testFileId;
	}
	public void setTestFileId(String testFileId) {
		this.testFileId = testFileId;
	}
	public String getTestFileStatus() {
		return testFileStatus;
	}
	public void setTestFileStatus(String testFileStatus) {
		this.testFileStatus = testFileStatus;
	}

}
