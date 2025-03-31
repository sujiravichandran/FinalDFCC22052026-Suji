package com.teclever.dfcc.resultstore.dto;

import java.util.List;
import java.util.Map;

public class ResultDto {

    private String tpgph;
    private String stepName;
    private String expectedValue;
    private String measuredValue;
    private String unit;
    private String signalName;
    private Map<String,String> faultyChannel;
    private String fileName;
    private String faultySRU;
    private String testMode;
    private List<String> dStarChannels;
    
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
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
	public Map<String, String> getFaultyChannel() {
		return faultyChannel;
	}
	public void setFaultyChannel(Map<String, String> faultyChannel) {
		this.faultyChannel = faultyChannel;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFaultySRU() {
		return faultySRU;
	}
	public void setFaultySRU(String faultySRU) {
		this.faultySRU = faultySRU;
	}
	
	public List<String> getdStarChannels() {
		return dStarChannels;
	}
	public void setdStarChannels(List<String> dStarChannels) {
		this.dStarChannels = dStarChannels;
	}
	public ResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResultDto(String tpgph, String stepName, String expectedValue, String measuredValue, String unit,
			String signalName, Map<String, String> faultyChannel, String fileName,String faultySRU) {
		super();
		this.tpgph = tpgph;
		this.stepName = stepName;
		this.expectedValue = expectedValue;
		this.measuredValue = measuredValue;
		this.unit = unit;
		this.signalName = signalName;
		this.faultyChannel = faultyChannel;
		this.fileName = fileName;
		this.faultySRU = faultySRU;
	}
    

    
}
