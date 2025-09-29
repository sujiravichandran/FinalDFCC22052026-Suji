package com.teclever.dfcc.model;

import java.util.List;
import java.util.Map;

public class BrowseFileDetailedData {
	private String slNo;
	private String filePath;
	private String rdfName;
	private String tpgphNo;
    private String stepName;
    private String expectedValue;
    private String expectedMinValue;
    private String expectedMaxValue;
    private String unit;
    private String signalName;
    private String faultyChannel;
    private String faultySRU;
    private String measuredValueCh1_Ch2_Ch3_Ch4;


	
	public String getMeasuredValueCh1_Ch2_Ch3_Ch4() {
		return measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public void setMeasuredValueCh1_Ch2_Ch3_Ch4(String measuredValueCh1_Ch2_Ch3_Ch4) {
		this.measuredValueCh1_Ch2_Ch3_Ch4 = measuredValueCh1_Ch2_Ch3_Ch4;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getSlNo() {
		return slNo;
	}
	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}

	public String getTpgphNo() {
		return tpgphNo;
	}
	public void setTpgphNo(String tpgphNo) {
		this.tpgphNo = tpgphNo;
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
	public String getExpectedMinValue() {
		return expectedMinValue;
	}
	public void setExpectedMinValue(String expec) {
		this.expectedMinValue = expec;
	}
	
	public String getExpectedMaxValue() {
		return expectedMaxValue;
	}
	public void setExpectedMaxValue(String expectedMaxValue) {
		this.expectedMaxValue = expectedMaxValue;
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
	public void setFaultyChannel(String string) {
		this.faultyChannel = string;
	}
	public String getRdfName() {
		return rdfName;
	}
	public void setRdfName(String rdfName) {
		this.rdfName = rdfName;
	}
	
	public String getFaultySRU() {
		return faultySRU;
	}
	public void setFaultySRU(String faultySRU) {
		this.faultySRU = faultySRU;
	}
	
	
	
}
