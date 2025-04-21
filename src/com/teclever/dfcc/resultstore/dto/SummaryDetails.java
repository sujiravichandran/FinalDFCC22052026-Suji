package com.teclever.dfcc.resultstore.dto;

import java.util.Map;

public class SummaryDetails {
	
	private Map<String,String> levelOneIdStartDate;
	private Map<String,String> levelOneIdEndDate;
	private Map<String,String> levelOneIdResult;

	public Map<String, String> getLevelOneIdResult() {
		return levelOneIdResult;
	}
	public void setLevelOneIdResult(Map<String, String> levelOneIdResult) {
		this.levelOneIdResult = levelOneIdResult;
	}
	public Map<String, String> getLevelOneIdStartDate() {
		return levelOneIdStartDate;
	}
	public void setLevelOneIdStartDate(Map<String, String> levelOneIdStartDate) {
		this.levelOneIdStartDate = levelOneIdStartDate;
	}
	public Map<String, String> getLevelOneIdEndDate() {
		return levelOneIdEndDate;
	}
	public void setLevelOneIdEndDate(Map<String, String> levelOneIdEndDate) {
		this.levelOneIdEndDate = levelOneIdEndDate;
	}
	
	
}
