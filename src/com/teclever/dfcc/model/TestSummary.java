package com.teclever.dfcc.model;

public class TestSummary {
	private String fileName;
	private String result;
	private String channel;
	public TestSummary(String fileName, String result, String channel) {
		super();
		this.fileName = fileName;
		this.result = result;
		this.channel = channel;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	

}
