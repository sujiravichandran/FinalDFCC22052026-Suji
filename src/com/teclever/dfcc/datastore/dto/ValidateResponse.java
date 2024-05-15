package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class ValidateResponse {

	private List<CheckSum> checkSumList;

	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public List<CheckSum> getCheckSumList() {
		return checkSumList;
	}

	public void setCheckSumList(List<CheckSum> checkSumList) {
		this.checkSumList = checkSumList;
	}

}
