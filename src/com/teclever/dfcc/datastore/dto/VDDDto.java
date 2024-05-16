package com.teclever.dfcc.datastore.dto;

public class VDDDto {

	private String fileCheckSum;
	private String filePath;
	private String fileName;
	private String baseFileName;

	
	public VDDDto(String fileCheckSum, String filePath, String fileName, String baseFileName) {
		super();
		this.fileCheckSum = fileCheckSum;
		this.filePath = filePath;
		this.fileName = fileName;
		this.baseFileName = baseFileName;
	}

	public VDDDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFileCheckSum() {
		return fileCheckSum;
	}

	public void setFileCheckSum(String fileCheckSum) {
		this.fileCheckSum = fileCheckSum;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBaseFileName() {
		return baseFileName;
	}

	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

}
