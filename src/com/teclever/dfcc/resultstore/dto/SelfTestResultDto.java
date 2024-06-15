package com.teclever.dfcc.resultstore.dto;

public class SelfTestResultDto {
	
	private String macname;
	private String boardName;
	private String dStarInfo;
    private String expectedValue;
    private String measuredValue;
    private String unit;
    private String faultyChannel;
    private String fileName;
    
    
    
	public String getMacname() {
		return macname;
	}
	public void setMacname(String macname) {
		this.macname = macname;
	}
	public String getBoardName() {
		return boardName;
	}
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	public String getdStarInfo() {
		return dStarInfo;
	}
	public void setdStarInfo(String dStarInfo) {
		this.dStarInfo = dStarInfo;
	}
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
	public String getMeasuredValue() {
		return measuredValue;
	}
	public void setMeasuredValue(String measuredValue) {
		this.measuredValue = measuredValue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getFaultyChannel() {
		return faultyChannel;
	}
	public void setFaultyChannel(String faultyChannel) {
		this.faultyChannel = faultyChannel;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
    



}
