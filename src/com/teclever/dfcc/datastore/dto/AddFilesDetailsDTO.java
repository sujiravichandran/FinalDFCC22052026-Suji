package com.teclever.dfcc.datastore.dto;

public class AddFilesDetailsDTO {
	private String fileName;
	private String filePath;
	private String checkSumDetails;
	private String fromFilePath;
	private String masterPathId;
	private String location;
	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCheckSumDetails() {
		return checkSumDetails;
	}
	public void setCheckSumDetails(String checkSumDetails) {
		this.checkSumDetails = checkSumDetails;
	}
	public String getFromFilePath() {
		return fromFilePath;
	}
	public void setFromFilePath(String fromFilePath) {
		this.fromFilePath = fromFilePath;
	}
	public String getMasterPathId() {
		return masterPathId;
	}
	public void setMasterPathId(String masterPathId) {
		this.masterPathId = masterPathId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	

}
