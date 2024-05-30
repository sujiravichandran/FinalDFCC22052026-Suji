package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class SessionListResponse {

	private List<SessionList> listOfSession;

	private Response response;

	public List<SessionList> getListOfSession() {
		return listOfSession;
	}

	public void setListOfSession(List<SessionList> listOfSession) {
		this.listOfSession = listOfSession;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
