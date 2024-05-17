package com.teclever.dfcc.datastore.dto;

public class SessionMasterDTO {
	private String sessionMasterId;
	private String sessionTypeName;
	
	public String getSessionMasterId() {
		return sessionMasterId;
	}
	public void setSessionMasterId(String sessionMasterId) {
		this.sessionMasterId = sessionMasterId;
	}
	public String getSessionTypeName() {
		return sessionTypeName;
	}
	public void setSessionTypeName(String sessionTypeName) {
		this.sessionTypeName = sessionTypeName;
	}
	@Override
	public String toString() {
		return "SessionMasterDTO [sessionMasterId=" + sessionMasterId + ", sessionTypeName=" + sessionTypeName + "]";
	}
}
