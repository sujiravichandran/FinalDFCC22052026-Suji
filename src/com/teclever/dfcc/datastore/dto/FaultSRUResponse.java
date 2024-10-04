package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class FaultSRUResponse {
	
    private int responseCode;
    private String responseMessage;
    private List<FaultySRUDto> faultySRUs;
    
    
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
	public List<FaultySRUDto> getFaultySRUs() {
		return faultySRUs;
	}
	public void setFaultySRUs(List<FaultySRUDto> faultySRUs) {
		this.faultySRUs = faultySRUs;
	}
    
    
    

}
