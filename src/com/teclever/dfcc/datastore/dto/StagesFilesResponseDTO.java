package com.teclever.dfcc.datastore.dto;
import java.util.List;
public class StagesFilesResponseDTO {
	
	public int code;
	public String msg;
	public String emsg;
	public List<StagesFilesDTO> responseList;
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
	public String getEmsg() {
		return emsg;
	}
	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}
	public List<StagesFilesDTO> getResponseList() {
		return responseList;
	}
	public void setResponseList(List<StagesFilesDTO> responseList) {
		this.responseList = responseList;
	}
	
	
}