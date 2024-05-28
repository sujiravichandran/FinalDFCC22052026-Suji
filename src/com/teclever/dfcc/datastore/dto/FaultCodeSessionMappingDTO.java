package com.teclever.dfcc.datastore.dto;

public class FaultCodeSessionMappingDTO {

	private int faultCodeLinkSessionId;
	private String faultCodeId;
	private String stageId;

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

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

}
