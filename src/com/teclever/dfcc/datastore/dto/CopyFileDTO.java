package com.teclever.dfcc.datastore.dto;

public class CopyFileDTO {
	
	private String copyingFileId;
	private String stagePath;
	private String rdfFiledName;
	private String rdfFileNamewithPath;
	private String rdfFilePath;
	private String copyingId;
	private String status;
	private String stageName;
	private String stageId;
	private boolean flag;
	private int dStarCount;
	
	
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
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
	public String getCopyingId() {
		return copyingId;
	}
	public void setCopyingId(String copyingId) {
		this.copyingId = copyingId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStagePath() {
		return stagePath;
	}
	public void setStagePath(String stagePath) {
		this.stagePath = stagePath;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public int getdStarCount() {
		return dStarCount;
	}
	public void setdStarCount(int dStarCount) {
		this.dStarCount = dStarCount;
	}
	
	

}
