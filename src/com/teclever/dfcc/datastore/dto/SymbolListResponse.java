package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class SymbolListResponse {

	private List<SymbolDto> listOfSymbolDto;

	private Response response;

	public List<SymbolDto> getListOfSymbolDto() {
		return listOfSymbolDto;
	}

	public void setListOfSymbolDto(List<SymbolDto> listOfSymbolDto) {
		this.listOfSymbolDto = listOfSymbolDto;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
