package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class MacroButtonMasterDetailsDto {

	private String buttonId;
	private String uutType;
	private int buttonNumber;
	private Response response;

	public String getButtonId() {
		return buttonId;
	}

	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}

	public String getUutType() {
		return uutType;
	}

	public void setUutType(String uutType) {
		this.uutType = uutType;
	}

	public int getButtonNumber() {
		return buttonNumber;
	}

	public void setButtonNumber(int buttonNumber) {
		this.buttonNumber = buttonNumber;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	
}

