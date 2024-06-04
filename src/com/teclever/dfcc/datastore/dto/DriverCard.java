package com.teclever.dfcc.datastore.dto;

public class DriverCard {
	
	private String cardName;
	private String totalNumberOfCards;
	private String expectedCountOfCards;
	private String msg;
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
	public String getTotalNumberOfCards() {
		return totalNumberOfCards;
	}
	public void setTotalNumberOfCards(String totalNumberOfCards) {
		this.totalNumberOfCards = totalNumberOfCards;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public DriverCard(String cardName, String totalNumberOfCards, String expectedCountOfCards) {
		this.cardName = cardName;
		this.totalNumberOfCards = totalNumberOfCards;
		this.expectedCountOfCards = expectedCountOfCards;
	}
	public DriverCard() {
		super();
	}
	
	public DriverCard(String cardName) {
		this.cardName = cardName;
	}


	

}
