package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class SessionStagesRunningStatusDTO {
	
	private int responseCode;
	private String responseMsg;
	private List<CopyFileDTO> failedFilesList;
	private String stageId;
	private String stageName;
	private String status;
	private boolean resultFlag;
	private String stageResult;
	private List<String> completedIds;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public List<CopyFileDTO> getFailedFilesList() {
		return failedFilesList;
	}
	public void setFailedFilesList(List<CopyFileDTO> failedFilesList) {
		this.failedFilesList = failedFilesList;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStageResult() {
		return stageResult;
	}
	public void setStageResult(String stageResult) {
		this.stageResult = stageResult;
	}
	public boolean isResultFlag() {
		return resultFlag;
	}
	public void setResultFlag(boolean resultFlag) {
		this.resultFlag = resultFlag;
	}
	public List<String> getCompletedIds() {
		return completedIds;
	}
	public void setCompletedIds(List<String> completedIds) {
		this.completedIds = completedIds;
	}
	
	

}
