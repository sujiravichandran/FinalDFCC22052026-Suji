package com.teclever.dfcc.model;

public class SessionDetails {
	private String sessionName;
	private String date;
	
	public SessionDetails(String sessionName, String date) {
		this.sessionName = sessionName;
		this.date = date;
		
	}
	public String getSessionName() {
		return sessionName;
	}public String getDate() {
		return date;
	}public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}public void setDate(String date) {
		this.date = date;
	}
}
