package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class FaultCodeResponse {

	private List<FaultCodeDTO> faultCodeList;

	private Response response;

	public List<FaultCodeDTO> getFaultCodeList() {
		return faultCodeList;
	}

	public void setFaultCodeList(List<FaultCodeDTO> faultCodeList) {
		this.faultCodeList = faultCodeList;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
