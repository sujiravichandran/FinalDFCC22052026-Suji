package com.teclever.dfcc.datastore.dto;

public class LevelOneDto {

	private String levelOneId;

	private String stageName;

	private String sessionIds;

	private String uutId;

	private String nextLevel;

	private boolean defaultStatus;

	private boolean mandatoryStatus;

	private boolean continueWithErrorStatus;

	private boolean advanceTestStatus;

	public String getLevelOneId() {
		return levelOneId;
	}

	public void setLevelOneId(String levelOneId) {
		this.levelOneId = levelOneId;
	}

	public String getStageName() {
		return stageName;
	}

	public String getSessionIds() {
		return sessionIds;
	}

	public void setSessionIds(String sessionIds) {
		this.sessionIds = sessionIds;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getUutId() {
		return uutId;
	}

	public void setUutId(String uutId) {
		this.uutId = uutId;
	}

	public String getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(String nextLevel) {
		this.nextLevel = nextLevel;
	}

	public boolean isDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(boolean defaultStatus) {
		this.defaultStatus = defaultStatus;
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

	public boolean isAdvanceTestStatus() {
		return advanceTestStatus;
	}

	public void setAdvanceTestStatus(boolean advanceTestStatus) {
		this.advanceTestStatus = advanceTestStatus;
	}

}
