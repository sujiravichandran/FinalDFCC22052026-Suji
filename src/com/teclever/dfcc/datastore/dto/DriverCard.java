package com.teclever.dfcc.datastore.dto;

public class DriverCard {
	
	private String cardName;
	private String totalNumberOfCards;
	private String msg;
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
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
	public DriverCard(String cardName, String totalNumberOfCards) {
		this.cardName = cardName;
		this.totalNumberOfCards = totalNumberOfCards;
	}
	public DriverCard() {
		super();
	}
	
	public DriverCard(String cardName) {
		this.cardName = cardName;
	}


	

}
