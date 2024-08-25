package com.teclever.dfcc.datastore.dto;

public class CopyFileDTO {
	
	private String copyingFileId;
	private String rdfFiledName;
	private String rdfFileNamewithPath;
	private String rdfFilePath;
	private String copyingId;
	
	public String getCopyingFileId() {
		return copyingFileId;
	}
	public void setCopyingFileId(String copyingFileId) {
		this.copyingFileId = copyingFileId;
	}
	public String getRdfFiledName() {
		return rdfFiledName;
	}
	public void setRdfFiledName(String rdfFiledName) {
		this.rdfFiledName = rdfFiledName;
	}
	public String getRdfFileNamewithPath() {
		return rdfFileNamewithPath;
	}
	public void setRdfFileNamewithPath(String rdfFileNamewithPath) {
		this.rdfFileNamewithPath = rdfFileNamewithPath;
	}
	public String getRdfFilePath() {
		return rdfFilePath;
	}
	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}
	
	

}
