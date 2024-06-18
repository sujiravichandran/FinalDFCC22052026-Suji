package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ChannelStatusResponse {
	
	private String responseMsg;
	private int responseCode;
	private List<ChannelStatus> channelStatus;
	
	
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
	public List<ChannelStatus> getChannelStatus() {
		return channelStatus;
	}
	public void setChannelStatus(List<ChannelStatus> channelStatus) {
		this.channelStatus = channelStatus;
	}
	
	public ChannelStatusResponse(String responseMsg, int responseCode, List<ChannelStatus> channelStatus) {
		super();
		this.responseMsg = responseMsg;
		this.responseCode = responseCode;
		this.channelStatus = channelStatus;
	}
	public ChannelStatusResponse() {
		super();
	}
	
	
	

}
