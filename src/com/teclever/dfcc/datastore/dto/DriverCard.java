package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class DriverCard {
	
	private String cardName;
	private String foundedNumberOfCards;
	private String expectedCountOfCards;
	private String msg;
	private Response response;
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	public String getExpectedCountOfCards() {
		return expectedCountOfCards;
	}
	public void setExpectedCountOfCards(String expectedCountOfCards) {
		this.expectedCountOfCards = expectedCountOfCards;
	}

	public String getFoundedNumberOfCards() {
		return foundedNumberOfCards;
	}
	public void setFoundedNumberOfCards(String foundedNumberOfCards) {
		this.foundedNumberOfCards = foundedNumberOfCards;
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
	public DriverCard(String cardName, String foundedNumberOfCards,Response response) {
		super();
		this.cardName = cardName;
		this.foundedNumberOfCards = foundedNumberOfCards;
		this.response = response;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}


	

}
