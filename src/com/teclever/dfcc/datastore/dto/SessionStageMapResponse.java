package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class SessionStageMapResponse {

	private List<StageObject> listOfStageObject;

	private Response response;

	public List<StageObject> getListOfStageObject() {
		return listOfStageObject;
	}

	public void setListOfStageObject(List<StageObject> listOfStageObject) {
		this.listOfStageObject = listOfStageObject;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
