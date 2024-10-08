package com.teclever.dfcc.resultstore.dto;

import java.util.List;
import java.util.Map;

public class StepDto {
	
    private String testPlanFile;
    private String resultDataFile;
    private String tpgph;
    private String step;
    private String input;
    private List<String> readingInfo;
    private String dStarInfo;
    private String signalName;
    private String unit;
    private String expectedValue; 
    private List<String> upperLimit;
    private List<String> lowerLimit;
    private Map<String,String> faultyChannel;
    private List<String> measuredValue;
    private String faultySRU;
    
    
	public StepDto() {

	}
		
	public String getTestPlanFile() {
		return testPlanFile;
	}
	public void setTestPlanFile(String testPlanFile) {
		this.testPlanFile = testPlanFile;
	}
	public String getResultDataFile() {
		return resultDataFile;
	}
	public void setResultDataFile(String resultDataFile) {
		this.resultDataFile = resultDataFile;
	}
	public String getTpgph() {
		return tpgph;
	}
	public void setTpgph(String tpgph) {
		this.tpgph = tpgph;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public List<String> getReadingInfo() {
		return readingInfo;
	}
	public void setReadingInfo(List<String> readingInfo) {
		this.readingInfo = readingInfo;
	}
	public String getdStarInfo() {
		return dStarInfo;
	}
	public void setdStarInfo(String dStarInfo) {
		this.dStarInfo = dStarInfo;
	}
	public String getSignalName() {
		return signalName;
	}
	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
	public List<String> getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(List<String> upperLimit) {
		this.upperLimit = upperLimit;
	}
	public List<String> getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(List<String> lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public Map<String,String> getFaultyChannel() {
		return faultyChannel;
	}
	public void setFaultyChannel(Map<String,String> faultyChannel) {
		this.faultyChannel = faultyChannel;
	}
	public List<String> getMeasuredValue() {
		return measuredValue;
	}
	public void setMeasuredValue(List<String> measuredValue) {
		this.measuredValue = measuredValue;
	}

	public String getFaultySRU() {
		return faultySRU;
	}

	public void setFaultySRU(String faultySRU) {
		this.faultySRU = faultySRU;
	}
    
    
    
    
    

	
}
