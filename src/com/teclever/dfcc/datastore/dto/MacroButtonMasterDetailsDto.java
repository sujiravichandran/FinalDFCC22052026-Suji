package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class MacroButtonMasterDetailsDto {

	private String buttonId;
	private int buttonNumber;
	private String uutId;;
	private Response response;
	public String getButtonId() {
		return buttonId;
	}
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}
	public int getButtonNumber() {
		return buttonNumber;
	}
	public void setButtonNumber(int buttonNumber) {
		this.buttonNumber = buttonNumber;
	}
	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}


	
	
	
}

