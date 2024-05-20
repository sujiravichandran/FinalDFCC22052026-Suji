package com.teclever.dfcc.datastore.dto;

import java.util.List;
import java.util.Map;

public class AddCustomFileResponse {
	
	private int code;
	private String msg;
	private String eMsg;
	private Map<String,String>copiedFileNameMsgMap;
	private Map<String,String>existFileNameMsgMap;
	private List<AddFilesDetailsDTO>addedFilesDetailsList;
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
	public Map<String, String> getCopiedFileNameMsgMap() {
		return copiedFileNameMsgMap;
	}
	public void setCopiedFileNameMsgMap(Map<String, String> copiedFileNameMsgMap) {
		this.copiedFileNameMsgMap = copiedFileNameMsgMap;
	}
	public Map<String, String> getExistFileNameMsgMap() {
		return existFileNameMsgMap;
	}
	public void setExistFileNameMsgMap(Map<String, String> existFileNameMsgMap) {
		this.existFileNameMsgMap = existFileNameMsgMap;
	}
	public List<AddFilesDetailsDTO> getAddedFilesDetailsList() {
		return addedFilesDetailsList;
	}
	public void setAddedFilesDetailsList(List<AddFilesDetailsDTO> addedFilesDetailsList) {
		this.addedFilesDetailsList = addedFilesDetailsList;
	}
	

}
