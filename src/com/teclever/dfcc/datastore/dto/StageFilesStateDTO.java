package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class StageFilesStateDTO {
	
	private String stageId;
	private String stageColurStatus;
	private List<String> failedFileNames;
	private List<String> passedFileNames;
	private List<StagesTestFilesDetailDTO> lst;
	private boolean continueWithError;
	private boolean disableFlag;
	private String stageStatus;
	
	
	
	public List<StagesTestFilesDetailDTO> getLst() {
		return lst;
	}
	public void setLst(List<StagesTestFilesDetailDTO> lst) {
		this.lst = lst;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getStageColurStatus() {
		return stageColurStatus;
	}
	public void setStageColurStatus(String stageColurStatus) {
		this.stageColurStatus = stageColurStatus;
	}
	public List<String> getFailedFileNames() {
		return failedFileNames;
	}
	public void setFailedFileNames(List<String> failedFileNames) {
		this.failedFileNames = failedFileNames;
	}
	public List<String> getPassedFileNames() {
		return passedFileNames;
	}
	public void setPassedFileNames(List<String> passedFileNames) {
		this.passedFileNames = passedFileNames;
	}
	public boolean isContinueWithError() {
		return continueWithError;
	}
	public void setContinueWithError(boolean continueWithError) {
		this.continueWithError = continueWithError;
	}
	public boolean isDisableFlag() {
		return disableFlag;
	}
	public void setDisableFlag(boolean disableFlag) {
		this.disableFlag = disableFlag;
	}
	public String getStageStatus() {
		return stageStatus;
	}
	public void setStageStatus(String stageStatus) {
		this.stageStatus = stageStatus;
	}
	

}
