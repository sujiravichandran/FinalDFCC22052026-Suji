package com.teclever.dfcc.datastore.dto;

public class LevelOneDto {

	private String levelOneId;

	private String stageName;

	private String sessionIds;

	private String uutId;

	private String nextLevel;

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
	
}
