package com.teclever.dfcc.datastore.dto;

public class CheckSum {
	private int sNo;
	private String file;
	private String checksumValue;
	private String msg;
	
	
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getChecksumValue() {
		return checksumValue;
	}
	public void setChecksumValue(String checksumValue) {
		this.checksumValue = checksumValue;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}









