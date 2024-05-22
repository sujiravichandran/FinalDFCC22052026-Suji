package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class FaultCodeAddResponse {

	private FaultCodeDTO faultCodeMaster;

	private Response response;

	public FaultCodeDTO getFaultCodeMaster() {
		return faultCodeMaster;
	}

	public void setFaultCodeMaster(FaultCodeDTO faultCodeMaster) {
		this.faultCodeMaster = faultCodeMaster;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
