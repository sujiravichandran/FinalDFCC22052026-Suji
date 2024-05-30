package com.teclever.dfcc.model;

public class FaultCodeList {
	private int code;
	private String codeDescription;
	private String faultCodeId;
	private boolean selected;



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

	public String getFaultCodeId() {
		return faultCodeId;
	}

	public void setFaultCodeId(String faultCodeId) {
		this.faultCodeId = faultCodeId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return code + "-" + codeDescription;
	}

}
