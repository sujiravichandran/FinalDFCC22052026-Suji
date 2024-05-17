package com.teclever.dfcc.datastore.dto;

public class DownloadFileDto {
	
	private int downloadFileId;
	private String downloadFileName;
    private String runPathMasterId;
    
    
	public int getDownloadFileId() {
		return downloadFileId;
	}
	public void setDownloadFileId(int downloadFileId) {
		this.downloadFileId = downloadFileId;
	}
	public String getDownloadFileName() {
		return downloadFileName;
	}
	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}
	public String getRunPathMasterId() {
		return runPathMasterId;
	}
	public void setRunPathMasterId(String runPathMasterId) {
		this.runPathMasterId = runPathMasterId;
	}

    
    
}
