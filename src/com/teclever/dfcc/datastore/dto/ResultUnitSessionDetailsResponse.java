package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ResultUnitSessionDetailsResponse {
	
	 private List<ResultUnitSessionDetailsDTO> resultUnitSessionDetailsDTOList;
	 private int code;
	 private String msg;
	 private String uutTypeId;
	 private String uutTypeName;
	 private String eMsg;
	 private int totalNoOfSessions;
	 
	public List<ResultUnitSessionDetailsDTO> getResultUnitSessionDetailsDTOList() {
		return resultUnitSessionDetailsDTOList;
	}
	public void setResultUnitSessionDetailsDTOList(List<ResultUnitSessionDetailsDTO> resultUnitSessionDetailsDTOList) {
		this.resultUnitSessionDetailsDTOList = resultUnitSessionDetailsDTOList;
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
	public String getUutTypeId() {
		return uutTypeId;
	}
	public void setUutTypeId(String uutTypeId) {
		this.uutTypeId = uutTypeId;
	}
	public String getUutTypeName() {
		return uutTypeName;
	}
	public void setUutTypeName(String uutTypeName) {
		this.uutTypeName = uutTypeName;
	}
	public String geteMsg() {
		return eMsg;
	}
	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
	public int getTotalNoOfSessions() {
		return totalNoOfSessions;
	}
	public void setTotalNoOfSessions(int totalNoOfSessions) {
		this.totalNoOfSessions = totalNoOfSessions;
	}
	 
	 

}
