package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class DfccStatusCommandResponse {

	private List<DfccStatusCommandDto> listOfDfccStatusCommandDto;

	private Response response;

	public List<DfccStatusCommandDto> getListOfDfccStatusCommandDto() {
		return listOfDfccStatusCommandDto;
	}

	public void setListOfDfccStatusCommandDto(List<DfccStatusCommandDto> listOfDfccStatusCommandDto) {
		this.listOfDfccStatusCommandDto = listOfDfccStatusCommandDto;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
