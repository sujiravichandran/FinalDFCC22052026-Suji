package com.teclever.dfcc.datastore.dto;

public class CheckSum {

	private int sNo;
	private String file;
	private String checksumValue;
	private String msg;
	public int getsNo() {
		return sNo;
	}
	
	public CheckSum() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CheckSum(int sNo, String file, String checksumValue, String msg) {
		super();
		this.sNo = sNo;
		this.file = file;
		this.checksumValue = checksumValue;
		this.msg = msg;
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
	
	public CheckSum(String file, String msg) {
		this.file = file;
		this.msg = msg;
	}
	
		
}
