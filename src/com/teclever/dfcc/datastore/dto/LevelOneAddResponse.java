package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class LevelOneAddResponse {

	private LevelOneDto levelOneResponse;
	private Response response;

	public LevelOneDto getLevelOneResponse() {
		return levelOneResponse;
	}

	public void setLevelOneResponse(LevelOneDto levelOneResponse) {
		this.levelOneResponse = levelOneResponse;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
