package com.teclever.dfcc.model;

public class SelfTest {

	String fileName;
	String result;
	
	public SelfTest(String fileName, String result) {
		super();
		this.fileName = fileName;
		this.result = result;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
