package com.teclever.dfcc.model;

public class StageIdName {

	private String stageId;
	private String stageName;
	private String parentId;
	private String testTypeId;
	private boolean mandatoryStatus;
	private boolean continueWithErrorStatus;

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getTestTypeId() {
		return testTypeId;
	}

	public void setTestTypeId(String testTypeId) {
		this.testTypeId = testTypeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public boolean isMandatoryStatus() {
		return mandatoryStatus;
	}

	public void setMandatoryStatus(boolean mandatoryStatus) {
		this.mandatoryStatus = mandatoryStatus;
	}

	public boolean isContinueWithErrorStatus() {
		return continueWithErrorStatus;
	}

	public void setContinueWithErrorStatus(boolean continueWithErrorStatus) {
		this.continueWithErrorStatus = continueWithErrorStatus;
	}
	
	

}
