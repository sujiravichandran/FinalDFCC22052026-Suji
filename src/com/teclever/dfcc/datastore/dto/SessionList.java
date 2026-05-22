package com.teclever.dfcc.datastore.dto;

import java.util.Date;

public class SessionList {

	private String sessionId;

	private String sessionName;
	
	private String serialNo;
	
	private String reportType;
	
	private String userType;
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	private String sessionType;
	
	private String endDate;
	
	public String getEndType() {
		return endType;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setEndType(String endType) {
		this.endType = endType;
	}

	private String endType;
	

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	private Date creationDate;
	
	private String sessionTypeId;
	
	public String getSessionTypeId() {
		return sessionTypeId;
	}

	public void setSessionTypeId(String sessionTypeId) {
		this.sessionTypeId = sessionTypeId;
	}

	public String getUutTypeId() {
		return uutTypeId;
	}

	public void setUutTypeId(String uutTypeId) {
		this.uutTypeId = uutTypeId;
	}

	public String getDfccSNo() {
		return dfccSNo;
	}

	public void setDfccSNo(String dfccSNo) {
		this.dfccSNo = dfccSNo;
	}

	private String uutTypeId;
	
	private String dfccSNo;

	private String ofpConfigId;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getOfpConfigId() {
		return ofpConfigId;
	}

	public void setOfpConfigId(String ofpConfigId) {
		this.ofpConfigId = ofpConfigId;
	}
	
	

}
