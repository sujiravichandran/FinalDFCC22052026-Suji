package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class UserGetAllResponse {

	private List<UserLoginDetailsDto> userList;

	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public List<UserLoginDetailsDto> getUserList() {
		return userList;
	}

	public void setUserList(List<UserLoginDetailsDto> userList) {
		this.userList = userList;
	}

}
