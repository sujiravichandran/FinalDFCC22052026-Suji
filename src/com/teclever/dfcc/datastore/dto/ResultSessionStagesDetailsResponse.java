package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ResultSessionStagesDetailsResponse {
	
	 private List<ResultSessionStagesDetailsDTO> resultSessionStagesDetailsDTOList;
	 private int code;
	 private String msg;
	 private String sessionId;
	 private String sessionName;
	 private int totalNoOfStages;
	 private String eMsg;
	 
	public List<ResultSessionStagesDetailsDTO> getResultSessionStagesDetailsDTOList() {
		return resultSessionStagesDetailsDTOList;
	}
	public void setResultSessionStagesDetailsDTOList(
			List<ResultSessionStagesDetailsDTO> resultSessionStagesDetailsDTOList) {
		this.resultSessionStagesDetailsDTOList = resultSessionStagesDetailsDTOList;
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
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String geteMsg() {
		return eMsg;
	}
	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
	public int getTotalNoOfStages() {
		return totalNoOfStages;
	}
	public void setTotalNoOfStages(int totalNoOfStages) {
		this.totalNoOfStages = totalNoOfStages;
	}
	 	 

}
