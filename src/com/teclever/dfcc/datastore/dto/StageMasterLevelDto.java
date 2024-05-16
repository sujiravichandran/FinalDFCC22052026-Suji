package com.teclever.dfcc.datastore.dto;

public class StageMasterLevelDto {

	private String levelId;

	private String stageName;

	private String parentId;

	private String testType;

	private String nextLevel;

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(String nextLevel) {
		this.nextLevel = nextLevel;
	}

}
