package com.teclever.dfcc.datastore.dto;

public class TestFileDto {

	private String testFileId;
	private String testFileName;
	private String runPathMasterId;
	private boolean selected = false;

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

	public String getRunPathMasterId() {
		return runPathMasterId;
	}

	public void setRunPathMasterId(String runPathMasterId) {
		this.runPathMasterId = runPathMasterId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
