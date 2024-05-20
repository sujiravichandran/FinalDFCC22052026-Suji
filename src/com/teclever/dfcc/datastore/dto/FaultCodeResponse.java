package com.teclever.dfcc.datastore.dto;

import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;

public class FaultCodeResponse {

	private List<FaultCodeDTO> faultCodeList;

	private Response response;

	private Map<Integer, String> mapResponse;

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

	public Map<Integer, String> getMapResponse() {
		return mapResponse;
	}

	public void setMapResponse(Map<Integer, String> mapResponse) {
		this.mapResponse = mapResponse;
	}

}
