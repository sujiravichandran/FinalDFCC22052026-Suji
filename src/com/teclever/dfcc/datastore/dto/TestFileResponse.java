package com.teclever.dfcc.datastore.dto;

import java.util.Map;

import com.teclever.datastore.dto.Response;

public class TestFileResponse {

	private Map<String, String> testFilesIdName;

	private Response response;

	public Map<String, String> getTestFilesIdName() {
		return testFilesIdName;
	}

	public void setTestFilesIdName(Map<String, String> testFilesIdName) {
		this.testFilesIdName = testFilesIdName;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
