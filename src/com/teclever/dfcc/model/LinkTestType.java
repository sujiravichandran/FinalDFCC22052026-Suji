package com.teclever.dfcc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LinkTestType {

	private String id;
	private String slNo;
	private String testMode;
	private String resultFileName;
	private String timeOfExecution;
	private String result;
	private final StringProperty testType1 = new SimpleStringProperty();
	private final StringProperty testType2 = new SimpleStringProperty();
	private final StringProperty testType3 = new SimpleStringProperty();

	public String getTestType1() {
	    return testType1.get();
	}

	public void setTestType1(String testType1) {
	    this.testType1.set(testType1);
	}

	public StringProperty testType1Property() {
	    return testType1;
	}

	public String getTestType2() {
	    return testType2.get();
	}

	public void setTestType2(String testType2) {
	    this.testType2.set(testType2);
	}

	public StringProperty testType2Property() {
	    return testType2;
	}

	public String getTestType3() {
	    return testType3.get();
	}

	public void setTestType3(String testType3) {
	    this.testType3.set(testType3);
	}

	public StringProperty testType3Property() {
	    return testType3;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSlNo() {
		return slNo;
	}
	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	
	public String getResultFileName() {
		return resultFileName;
	}

	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}

	public String getTimeOfExecution() {
		return timeOfExecution;
	}
	public void setTimeOfExecution(String timeOfExecution) {
		this.timeOfExecution = timeOfExecution;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

  
	
}
