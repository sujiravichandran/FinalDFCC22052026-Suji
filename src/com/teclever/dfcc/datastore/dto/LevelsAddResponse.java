package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class LevelsAddResponse {

	private StageMasterLevelDto levelsResponse;

	private Response response;

	public StageMasterLevelDto getLevelsResponse() {
		return levelsResponse;
	}

	public void setLevelsResponse(StageMasterLevelDto levelsResponse) {
		this.levelsResponse = levelsResponse;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
