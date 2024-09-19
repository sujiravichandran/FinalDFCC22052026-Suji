package com.teclever.dfcc.datastore.dto;

// NOT USING : while reading dot Com file Line by line This class is Required.(Test Process Management) 
public class TestProcessDto {

	private String testState;

	private String rdfFileResult;

	private String dotComFileResult;


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


}
