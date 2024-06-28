package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ResultExecutionResponse {
	private List<ResultExecutionDTO> resultDTOList;
	private int code;
	private String msg;
	private String eMsg;
	private String sessionName;
	private String stageName;

	public List<ResultExecutionDTO> getResultDTOList() {
		return resultDTOList;
	}

	public void setResultDTOList(List<ResultExecutionDTO> resultDTOList) {
		this.resultDTOList = resultDTOList;
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
	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

}
