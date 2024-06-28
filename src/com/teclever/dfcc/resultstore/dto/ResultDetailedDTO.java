package com.teclever.dfcc.resultstore.dto;

public class ResultDetailedDTO {
	
	private String testName;
	private String tpgph;
    private String stepName;
    private String expectedValue;
    private String measuredValue;
    private String unit;
    private String signalName;
    private String faultyChannel;
    private String rdfName;
    
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
    
    

}
