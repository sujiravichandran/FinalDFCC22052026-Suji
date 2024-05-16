package com.teclever.dfcc.datastore.dto;


public class TestTypeMasterDetailsDto {

	private String testTypeId;
	private String uutId;
	private String testName;
	
	
	public String getTestTypeId() {
		return testTypeId;
	}
	public void setTestTypeId(String testTypeId) {
		this.testTypeId = testTypeId;
	}
	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	@Override
	public String toString() {
		return "TestTypeMasterDetailsDto [testTypeId=" + testTypeId + ", uutId=" + uutId + ", testName=" + testName
				+ "]";
	}

	
	
	
}