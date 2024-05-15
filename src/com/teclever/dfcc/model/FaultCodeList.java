package com.teclever.dfcc.model;

public class FaultCodeList {
	private int code;
	private String codeDescription;

	public FaultCodeList(int code, String codeDescription) {
		this.code = code;
		this.codeDescription = codeDescription;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCodeDescription() {
		return codeDescription;
	}

	public void setCodeDescription(String codeDescription) {
		this.codeDescription = codeDescription;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return code + "-" + codeDescription;
	}

}
