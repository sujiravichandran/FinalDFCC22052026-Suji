package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class DriverCard {
	
	private String cardName;
	private String msg;
	private Response response;
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DriverCard() {
		super();
	}
	
	public DriverCard(String cardName) {
		this.cardName = cardName;
	}
	
	public DriverCard(String cardName,String msg, Response response) {
		this.cardName = cardName;
		this.msg = msg;
		this.response = response;
		
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}


	

}
