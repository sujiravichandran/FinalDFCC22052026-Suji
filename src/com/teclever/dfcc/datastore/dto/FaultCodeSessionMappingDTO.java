package com.teclever.dfcc.datastore.dto;

public class FaultCodeSessionMappingDTO {

	private int faultCodeLinkSessionId;
	private String faultCodeId;
	private String sessionId;

	public int getFaultCodeLinkSessionId() {
		return faultCodeLinkSessionId;
	}

	public void setFaultCodeLinkSessionId(int faultCodeLinkSessionId) {
		this.faultCodeLinkSessionId = faultCodeLinkSessionId;
	}

	public String getFaultCodeId() {
		return faultCodeId;
	}

	public void setFaultCodeId(String faultCodeId) {
		this.faultCodeId = faultCodeId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
