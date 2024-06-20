package com.teclever.dfcc.datastore.dto;

public class ChannelStatus {

	private String channel1;
	private String channel2;
	private String channel3;
	private String channel4;

	public ChannelStatus(String channel1, String channel2, String channel3, String channel4) {
		this.channel1 = channel1;
		this.channel2 = channel2;
		this.channel3 = channel3;
		this.channel4 = channel4;
	}

	public String getChannel1() {
		return channel1;
	}

	public String getChannel2() {
		return channel2;
	}

	public String getChannel3() {
		return channel3;
	}

	public String getChannel4() {
		return channel4;
	}

	public ChannelStatus() {
		super();
	}

}
