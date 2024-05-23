package com.teclever.dfcc.model;

public class CPCICard {
	
	private int id;
	private String cardName;
	private String cardIdentificationText;
	private int totalCards;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardIdentificationText() {
		return cardIdentificationText;
	}
	public void setCardIdentificationText(String cardIdentificationText) {
		this.cardIdentificationText = cardIdentificationText;
	}
	public int getTotalCards() {
		return totalCards;
	}
	public void setTotalCards(int totalCards) {
		this.totalCards = totalCards;
	}
	
	
}
