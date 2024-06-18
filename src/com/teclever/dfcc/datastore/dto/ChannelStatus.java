package com.teclever.dfcc.datastore.dto;

public class ChannelStatus {
	
	private String channelName;
	private String status;
	
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ChannelStatus(String channelName, String status) {
		super();
		this.channelName = channelName;
		this.status = status;
	}
	
	public ChannelStatus() {
		super();
	}

	
	

}
