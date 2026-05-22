package com.teclever.dfcc.resultstore.dto;

public class ResultDetailedDTO {
	
	private String testName;
	private String tpgph;
    private String stepName;
    private String expectedValue;
    private String expectedMinValue;
    private String exceptedMaxValue;
    private String measuredValue;
    private String unit;
    private String tpfFileName;
    private String signalName;
    private String faultyChannel;
    private String rdfName;
	private String stageId;
    private String stageName;
    private String faultySRU;
    private String testMode;
    private String faultyChannelValue;
    private String unitSerialNo;
    private String unitSession;
    
    
    
	public String getUnitSerialNo() {
		return unitSerialNo;
	}
	public void setUnitSerialNo(String unitSerialNo) {
		this.unitSerialNo = unitSerialNo;
	}
	public String getUnitSession() {
		return unitSession;
	}
	public void setUnitSession(String unitSession) {
		this.unitSession = unitSession;
	}
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
  	
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getTpfFileName() {
		return tpfFileName;
	}
	public void setTpfFileName(String tpfFileName) {
		this.tpfFileName = tpfFileName;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getTpgph() {
		return tpgph;
	}
	public void setTpgph(String tpgph) {
		this.tpgph = tpgph;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
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
	public String getSignalName() {
		return signalName;
	}
	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}
	public String getFaultyChannel() {
		return faultyChannel;
	}
	public void setFaultyChannel(String faultyChannel) {
		this.faultyChannel = faultyChannel;
	}
	public String getRdfName() {
		return rdfName;
	}
	public void setRdfName(String rdfName) {
		this.rdfName = rdfName;
	}
	public String getExpectedMinValue() {
		return expectedMinValue;
	}
	public void setExpectedMinValue(String expectedMinValue) {
		this.expectedMinValue = expectedMinValue;
	}
	public String getExceptedMaxValue() {
		return exceptedMaxValue;
	}
	public void setExceptedMaxValue(String exceptedMaxValue) {
		this.exceptedMaxValue = exceptedMaxValue;
	}
	public String getFaultySRU() {
		return faultySRU;
	}
	public void setFaultySRU(String faultySRU) {
		this.faultySRU = faultySRU;
	}
    
  
}