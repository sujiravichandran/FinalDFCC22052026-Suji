package com.teclever.dfcc.datastore.dto;

import java.util.List;
import com.teclever.datastore.dto.Response;

public class ChecksumResponse {
	
	private List<ChecksumDto> vDDList;
	private Response response;
	
	
	public List<ChecksumDto> getvDDList() {
		return vDDList;
	}
	public void setvDDList(List<ChecksumDto> vDDList) {
		this.vDDList = vDDList;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	
	
	
	

}
