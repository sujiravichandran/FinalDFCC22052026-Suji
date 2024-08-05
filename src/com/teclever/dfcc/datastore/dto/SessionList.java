package com.teclever.dfcc.datastore.dto;

import java.util.Date;

public class SessionList {

	private String sessionId;

	private String sessionName;

	private Date creationDate;

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
