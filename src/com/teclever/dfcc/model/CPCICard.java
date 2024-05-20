package com.teclever.dfcc.model;

public class CPCICard {
	
	private int cardId;
	private String cardName;
	private String cardIdentificationText;
	private int totalCards;
	
	

	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
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
