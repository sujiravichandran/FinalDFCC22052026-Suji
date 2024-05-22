package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class StageMasterLevelOneResponse {

	private List<LevelOneDto> levelOneResponse;
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public List<LevelOneDto> getLevelOneResponse() {
		return levelOneResponse;
	}

	public void setLevelOneResponse(List<LevelOneDto> levelOneResponse) {
		this.levelOneResponse = levelOneResponse;
	}



	
}
