package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.entities.FaultySRU;

public class FaultySruResponse {
	
	private int responseCode;
    private String responseMessage;
    private List<FaultySRU> faultySRUs;
    
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
	public List<FaultySRU> getFaultySRUs() {
		return faultySRUs;
	}
	public void setFaultySRUs(List<FaultySRU> faultySRUs) {
		this.faultySRUs = faultySRUs;
	}
    
    
    

}
