package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class ResultExecutionResponse {
	private List<ResultExecutionDTO> resultDTOList;
	private int code;
	private String msg;
	private String eMsg;

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

}
