package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class UserRoleResponse {
	
	private List<UserRoleMasterDto> listOfUserRoleMaster;
	
	private Response response;

	public List<UserRoleMasterDto> getListOfUserRoleMaster() {
		return listOfUserRoleMaster;
	}

	public void setListOfUserRoleMaster(List<UserRoleMasterDto> listOfUserRoleMaster) {
		this.listOfUserRoleMaster = listOfUserRoleMaster;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	

}
