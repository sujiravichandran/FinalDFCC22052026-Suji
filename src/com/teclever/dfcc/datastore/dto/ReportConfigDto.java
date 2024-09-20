package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ReportConfigDto {

	private String reportConfigId;// Primary Key

	private List<String> fileNameWitFullPath;

	private String sessionId;

	private String reportType;// "PQT" "ESS" "DataPack"

	private String levelOneName;

	private String levelOneId;

	private String levelTwoName;

	private String levelTwoId;

	private String levelThreeName;

	private String levelThreeId;

	private String levelFourName;

	private String levelFourId;

	private String levelFiveName;

	private String levelFiveId;

	private String fileName;
	
	private String uploadDate;
	
	
	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getReportConfigId() {
		return reportConfigId;
	}

	public void setReportConfigId(String reportConfigId) {
		this.reportConfigId = reportConfigId;
	}

	public List<String> getFileNameWitFullPath() {
		return fileNameWitFullPath;
	}

	public void setFileNameWitFullPath(List<String> fileNameWitFullPath) {
		this.fileNameWitFullPath = fileNameWitFullPath;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getLevelOneId() {
		return levelOneId;
	}

	public void setLevelOneId(String levelOneId) {
		this.levelOneId = levelOneId;
	}

	public String getLevelTwoId() {
		return levelTwoId;
	}

	public void setLevelTwoId(String levelTwoId) {
		this.levelTwoId = levelTwoId;
	}

	public String getLevelThreeId() {
		return levelThreeId;
	}

	public void setLevelThreeId(String levelThreeId) {
		this.levelThreeId = levelThreeId;
	}

	public String getLevelFourId() {
		return levelFourId;
	}

	public void setLevelFourId(String levelFourId) {
		this.levelFourId = levelFourId;
	}

	public String getLevelFiveId() {
		return levelFiveId;
	}

	public void setLevelFiveId(String levelFiveId) {
		this.levelFiveId = levelFiveId;
	}

	public String getLevelOneName() {
		return levelOneName;
	}

	public void setLevelOneName(String levelOneName) {
		this.levelOneName = levelOneName;
	}

	public String getLevelTwoName() {
		return levelTwoName;
	}

	public void setLevelTwoName(String levelTwoName) {
		this.levelTwoName = levelTwoName;
	}

	public String getLevelThreeName() {
		return levelThreeName;
	}

	public void setLevelThreeName(String levelThreeName) {
		this.levelThreeName = levelThreeName;
	}

	public String getLevelFourName() {
		return levelFourName;
	}

	public void setLevelFourName(String levelFourName) {
		this.levelFourName = levelFourName;
	}

	public String getLevelFiveName() {
		return levelFiveName;
	}

	public void setLevelFiveName(String levelFiveName) {
		this.levelFiveName = levelFiveName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
