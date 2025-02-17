package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ChannelStatusBeforeTestResponse {

    private int responseCode;
    private String responseMessage;
	private List<String> onlineStatus;

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
	public List<String> getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(List<String> onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public ChannelStatusBeforeTestResponse(int responseCode, String responseMessage) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	public ChannelStatusBeforeTestResponse(int responseCode, String responseMessage, List<String> onlineStatus) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.onlineStatus = onlineStatus;
	}
    
	

    
}
