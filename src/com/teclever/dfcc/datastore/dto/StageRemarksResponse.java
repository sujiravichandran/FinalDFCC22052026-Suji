package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class StageRemarksResponse {
	
	private String responseMsg;
	private int responseCode;
	private List<StagesRemarksDto> remarks;
	
	
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public List<StagesRemarksDto> getRemarks() {
		return remarks;
	}
	public void setRemarks(List<StagesRemarksDto> remarks) {
		this.remarks = remarks;
	}
	
	

}
