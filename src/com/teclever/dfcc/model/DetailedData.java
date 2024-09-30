package com.teclever.dfcc.model;

public class DetailedData {
	
	private int id;
	private String slNo;
	private String testName;
	private String rdfName;
	private String tpgphNo;
	private String stepNo;
	private String signalName;
	private String expectedValue;
	private String measuredValueCh1_Ch2_Ch3_Ch4;
	private String faultyChannel;
	private String unit;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSlNo() {
		return slNo;
	}
	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getRdfName() {
		return rdfName;
	}
	public void setRdfName(String rdfName) {
		this.rdfName = rdfName;
	}
	public String getTpgphNo() {
		return tpgphNo;
	}
	public void setTpgphNo(String tpgphNo) {
		this.tpgphNo = tpgphNo;
	}
	public String getStepNo() {
		return stepNo;
	}
	public void setStepNo(String stepNo) {
		this.stepNo = stepNo;
	}
	public String getSignalName() {
		return signalName;
	}
	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
	public String getMeasuredValueCh1Ch2Ch3Ch4() {
		return measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public void setMeasuredValueCh1Ch2Ch3Ch4(String measuredValueCh1_Ch2_Ch3_Ch4) {
		this.measuredValueCh1_Ch2_Ch3_Ch4 = measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public String getFaultyChannel() {
		return faultyChannel;
	}
	public void setFaultyChannel(String faultyChannel) {
		this.faultyChannel = faultyChannel;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	
}
