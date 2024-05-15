package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class MacroButtonMapDto {

	private int id;
	private String buttonId;
	private String buttonName;
	private String command;
	private int buttonNumber;
	private String uutType;
	private Response response;
	
	public MacroButtonMapDto( String buttonId,String buttonName, String command,String uutType) {
		this.buttonId=buttonId;
		this.buttonName=buttonName;
		this.command=command;
		this.uutType=uutType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getButtonId() {
		return buttonId;
	}

	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getButtonNumber() {
		return buttonNumber;
	}

	public void setButtonNumber(int buttonNumber) {
		this.buttonNumber = buttonNumber;
	}

	public String getUutType() {
		return uutType;
	}

	public void setUutType(String uutType) {
		this.uutType = uutType;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public MacroButtonMapDto() {
		super();
		// TODO Auto-generated constructor stub
	}

}

