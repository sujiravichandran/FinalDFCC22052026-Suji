package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class CopyingListDTO {
	private List<CopyFileDTO> lst;
	private int code;
	private String toPath;
	private String fromPath;
	private String codeMsg;
	
	
	public List<CopyFileDTO> getLst() {
		return lst;
	}
	public void setLst(List<CopyFileDTO> lst) {
		this.lst = lst;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getCodeMsg() {
		return codeMsg;
	}
	public void setCodeMsg(String codeMsg) {
		this.codeMsg = codeMsg;
	}
	public String getToPath() {
		return toPath;
	}
	public void setToPath(String toPath) {
		this.toPath = toPath;
	}
	public String getFromPath() {
		return fromPath;
	}
	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}
	
	

}
