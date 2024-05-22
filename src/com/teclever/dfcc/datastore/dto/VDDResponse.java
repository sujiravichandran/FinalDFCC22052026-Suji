package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class VDDResponse {

	private List<VDDDto> vDDList;

	private Response response;

	public List<VDDDto> getvDDList() {
		return vDDList;
	}

	public void setvDDList(List<VDDDto> vDDList) {
		this.vDDList = vDDList;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
