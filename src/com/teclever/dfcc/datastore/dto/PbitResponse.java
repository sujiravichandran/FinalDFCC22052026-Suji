package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class PbitResponse {
	
	private int responseCode;
	private String responseMessage;
	private List<String> ofpStatus;
	private List<String> wdmStatus;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public List<String> getOfpStatus() {
		return ofpStatus;
	}
	public void setOfpStatus(List<String> ofpStatus) {
		this.ofpStatus = ofpStatus;
	}
	public List<String> getWdmStatus() {
		return wdmStatus;
	}
	public void setWdmStatus(List<String> wdmStatus) {
		this.wdmStatus = wdmStatus;
	}
	
	
	

}
