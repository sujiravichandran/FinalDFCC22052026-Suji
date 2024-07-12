package com.teclever.dfcc.datastore.dto;

import java.util.Map;

import com.teclever.datastore.dto.Response;

public class CustomTestFileResponse {

	private Map<String, String> fileIdAndName;

	private Response response;

	public Map<String, String> getFileIdAndName() {
		return fileIdAndName;
	}

	public void setFileIdAndName(Map<String, String> fileIdAndName) {
		this.fileIdAndName = fileIdAndName;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
