package com.teclever.dfcc.model;

public class FaultCodeConfig{

	private String id;
	private int faultCode;
	private String description;
	private String filePath;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(int faultCode) {
		this.faultCode = faultCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	
}
