package com.teclever.dfcc.datastore.dto;

import java.util.List;

// NOT USING : while reading dot Com file Line by line This class is Required.(Test Process Management) 
public class TestProcessDto {

	private String testState;

	private String rdfFileResult;

	private String dotComFileResult;
	
	private int responseCode;
	private List<String> onlineStatus;


	public String getTestState() {
		return testState;
	}

	public void setTestState(String testState) {
		this.testState = testState;
	}

	public String getRdfFileResult() {
		return rdfFileResult;
	}

	public void setRdfFileResult(String rdfFileResult) {
		this.rdfFileResult = rdfFileResult;
	}

	public String getDotComFileResult() {
		return dotComFileResult;
	}

	public void setDotComFileResult(String dotComFileResult) {
		this.dotComFileResult = dotComFileResult;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public List<String> getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(List<String> onlineStatus) {
		this.onlineStatus = onlineStatus;
	}


}
