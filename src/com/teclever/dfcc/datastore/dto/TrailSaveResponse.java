package com.teclever.dfcc.datastore.dto;

import java.util.Map;

public class TrailSaveResponse {

	private String trailSessionId;
	private String trailName;
	private Map<String,String> stageNameMessage;
	private String msg;
	private int code;
	
	public String getTrailSessionId() {
		return trailSessionId;
	}
	public void setTrailSessionId(String trailSessionId) {
		this.trailSessionId = trailSessionId;
	}
	public String getTrailName() {
		return trailName;
	}
	public void setTrailName(String trailName) {
		this.trailName = trailName;
	}
	public Map<String, String> getStageNameMessage() {
		return stageNameMessage;
	}
	public void setStageNameMessage(Map<String, String> stageNameMessage) {
		this.stageNameMessage = stageNameMessage;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	

	
	
	
}
