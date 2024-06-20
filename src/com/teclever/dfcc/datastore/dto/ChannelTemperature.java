package com.teclever.dfcc.datastore.dto;

public class ChannelTemperature {
	
	private String channel1Temp;
	private String channel2Temp;
	private String channel3Temp;
	private String channel4Temp;
	
	
	public String getChannel1Temp() {
		return channel1Temp;
	}
	public void setChannel1Temp(String channel1Temp) {
		this.channel1Temp = channel1Temp;
	}
	public String getChannel2Temp() {
		return channel2Temp;
	}
	public void setChannel2Temp(String channel2Temp) {
		this.channel2Temp = channel2Temp;
	}
	public String getChannel3Temp() {
		return channel3Temp;
	}
	public void setChannel3Temp(String channel3Temp) {
		this.channel3Temp = channel3Temp;
	}
	public String getChannel4Temp() {
		return channel4Temp;
	}
	public void setChannel4Temp(String channel4Temp) {
		this.channel4Temp = channel4Temp;
	}
	
	
	public ChannelTemperature() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChannelTemperature(String channel1Temp, String channel2Temp, String channel3Temp, String channel4Temp) {
		super();
		this.channel1Temp = channel1Temp;
		this.channel2Temp = channel2Temp;
		this.channel3Temp = channel3Temp;
		this.channel4Temp = channel4Temp;
	}
	
	@Override
	public String toString() {
		return "[channel1Temp=" + channel1Temp + ", channel2Temp=" + channel2Temp + ", channel3Temp="
				+ channel3Temp + ", channel4Temp=" + channel4Temp + "]";
	}
	
}

