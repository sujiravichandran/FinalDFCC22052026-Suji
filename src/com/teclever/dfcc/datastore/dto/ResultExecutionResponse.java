package com.teclever.dfcc.datastore.dto;

import java.util.List;
import java.util.Map;

public class ResultExecutionResponse {
	private List<ResultExecutionDTO> resultDTOList;
	private int code;
	private String msg;
	private String eMsg;
	private String sessionName;
	private String stageName;
	private String systemInfoId;
	private Map<String,String>stageIdName;
	private Map<String,String>testFileIdName;
	private Map<String,String>sessionIdName;
	private String stageId;
	
	

	public String getSystemInfoId() {
		return systemInfoId;
	}

	public void setSystemInfoId(String systemInfoId) {
		this.systemInfoId = systemInfoId;
	}

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

	public Map<String, String> getStageIdName() {
		return stageIdName;
	}

	public void setStageIdName(Map<String, String> stageIdName) {
		this.stageIdName = stageIdName;
	}

	public Map<String, String> getTestFileIdName() {
		return testFileIdName;
	}

	public void setTestFileIdName(Map<String, String> testFileIdName) {
		this.testFileIdName = testFileIdName;
	}

	public Map<String, String> getSessionIdName() {
		return sessionIdName;
	}

	public void setSessionIdName(Map<String, String> sessionIdName) {
		this.sessionIdName = sessionIdName;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	
	
}
