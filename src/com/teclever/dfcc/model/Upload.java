package com.teclever.dfcc.model;

public class Upload {

	private String fileName;

	private String uploadDateAndTime;
	
	private String id;
	
	private String fullPath;
	
	

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getUploadDateAndTime() {
		return uploadDateAndTime;
	}

	public void setUploadDateAndTime(String uploadDateAndTime) {
		this.uploadDateAndTime = uploadDateAndTime;
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

	

}
