package com.teclever.dfcc.model;

import java.util.Date;

public class SessionDetails {
	private String sessionName;
	private Date date;
	private String sessionId;
	
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionName() {
		return sessionName;
	}public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
