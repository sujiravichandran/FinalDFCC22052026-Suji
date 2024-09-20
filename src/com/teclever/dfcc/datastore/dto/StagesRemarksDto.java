package com.teclever.dfcc.datastore.dto;

public class StagesRemarksDto {
	
	private int remarksId;
	private String levelOneStageId;
	private String remarks;
	private String sessionId;
	private String reportType;
	
	
	public int getRemarksId() {
		return remarksId;
	}
	public void setRemarksId(int remarksId) {
		this.remarksId = remarksId;
	}
	public String getLevelOneStageId() {
		return levelOneStageId;
	}
	public void setLevelOneStageId(String levelOneStageId) {
		this.levelOneStageId = levelOneStageId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public StagesRemarksDto() {
		super();
	}

	

}
