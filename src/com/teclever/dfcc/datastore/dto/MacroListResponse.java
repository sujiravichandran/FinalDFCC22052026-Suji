package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class MacroListResponse {

	List<MacroDto> listOfMacroDto;

	Response response;

	public List<MacroDto> getListOfMacroDto() {
		return listOfMacroDto;
	}

	public void setListOfMacroDto(List<MacroDto> listOfMacroDto) {
		this.listOfMacroDto = listOfMacroDto;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
