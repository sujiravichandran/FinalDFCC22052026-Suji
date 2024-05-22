package com.teclever.dfcc.datastore.dto;
public class StagesFilesDTO {
	
	private String testFileId;
	private String testFileName;
	private String levelStage;
	private int testFilesStagesMapping;
	private int aitessRunConfigurationId;
	private String pathMasterId;
	
	
	public String getTestFileName() {
		return testFileName;
	}
	public String getTestFileId() {
		return testFileId;
	}
	public void setTestFileId(String testFileId) {
		this.testFileId = testFileId;
	}
	public void setTestFileName(String testFileName) {
		this.testFileName = testFileName;
	}
	public String getLevelStage() {
		return levelStage;
	}
	public void setLevelStage(String levelStage) {
		this.levelStage = levelStage;
	}
	public int getTestFilesStagesMapping() {
		return testFilesStagesMapping;
	}
	public void setTestFilesStagesMapping(int testFilesStagesMapping) {
		this.testFilesStagesMapping = testFilesStagesMapping;
	}
	public int getAitessRunConfigurationId() {
		return aitessRunConfigurationId;
	}
	public void setAitessRunConfigurationId(int aitessRunConfigurationId) {
		this.aitessRunConfigurationId = aitessRunConfigurationId;
	}
	public String getPathMasterId() {
		return pathMasterId;
	}
	public void setPathMasterId(String pathMasterId) {
		this.pathMasterId = pathMasterId;
	}
		
}