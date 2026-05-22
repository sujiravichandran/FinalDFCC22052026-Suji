package com.teclever.dfcc.model;

public class DetailedData {
	
	private int id;
	private String slNo;
	private String unitSerialNo;
	private String sessionName;
	private String stageName;
	private String testMode;
	private String testName;
	private String rdfName;
	private String tpgphNo;
	private String stepNo;
	private String signalName;
	private String expectedValue;
	private String measuredValueCh1_Ch2_Ch3_Ch4;
	private String faultySru;
	private String unit;
	
	public String getUnitSerialNo() {
		return unitSerialNo;
	}
	public void setUnitSerialNo(String unitSerialNo) {
		this.unitSerialNo = unitSerialNo;
	}
	
	
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	private String faultyChannelValue;
	
	
	public String getFaultyChannelValue() {
		return faultyChannelValue;
	}
	public void setFaultyChannelValue(String faultyChannelValue) {
		this.faultyChannelValue = faultyChannelValue;
	}
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
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
	public String getMeasuredValueCh1_Ch2_Ch3_Ch4() {
		return measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public void setMeasuredValueCh1_Ch2_Ch3_Ch4(String measuredValueCh1_Ch2_Ch3_Ch4) {
		this.measuredValueCh1_Ch2_Ch3_Ch4 = measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public String getFaultySru() {
		return faultySru;
	}
	public void setFaultySru(String faultySru) {
		this.faultySru = faultySru;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	
}
