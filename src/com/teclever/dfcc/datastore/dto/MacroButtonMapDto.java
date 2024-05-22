package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class MacroButtonMapDto {

	private int id;
	private String buttonId;
	private int buttonNumber;
	private String buttonName;
	private String command;
	private String uutId;
	private Response response;
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
	public int getButtonNumber() {
		return buttonNumber;
	}
	public void setButtonNumber(int buttonNumber) {
		this.buttonNumber = buttonNumber;
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
	public MacroButtonMapDto(String buttonId, int buttonNumber, String buttonName, String command, String uutId,
			Response response) {
		super();
		this.buttonId = buttonId;
		this.buttonNumber = buttonNumber;
		this.buttonName = buttonName;
		this.command = command;
		this.uutId = uutId;
		this.response = response;
	}
	public MacroButtonMapDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}

