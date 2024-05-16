package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class StageMasterLevelsResponse {

	private List<StageMasterLevelDto> levelsResponse;

	private Response response;

	public List<StageMasterLevelDto> getLevelsResponse() {
		return levelsResponse;
	}

	public void setLevelsResponse(List<StageMasterLevelDto> levelsResponse) {
		this.levelsResponse = levelsResponse;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
