package com.teclever.dfcc.resultstore.dto;

import java.util.List;

public class ResultDetailedResponse {
	private List<ResultDetailedDTO> resultDetailedList;	
	private int code;
	private String msg;
	private String eMsg;
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
