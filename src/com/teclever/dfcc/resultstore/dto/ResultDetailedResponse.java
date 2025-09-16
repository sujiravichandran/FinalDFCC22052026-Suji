package com.teclever.dfcc.resultstore.dto;

import java.util.List;

public class ResultDetailedResponse {
	private List<ResultDetailedDTO> resultDetailedList;	
	private int code;
	private String msg;
	private String eMsg;
	private String stageId;
	private String stageName;
	private String sessionName;
	private String lastTestedDate;
	
	
	public String getLastTestedDate() {
		return lastTestedDate;
	}
	public void setLastTestedDate(String lastTestedDate) {
		this.lastTestedDate = lastTestedDate;
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
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public List<ResultDetailedDTO> getResultDetailedList() {
		return resultDetailedList;
	}
	public void setResultDetailedList(List<ResultDetailedDTO> resultDetailedList) {
		this.resultDetailedList = resultDetailedList;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String geteMsg() {
		return eMsg;
	}
	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
	
	

}
