package com.teclever.dfcc.datastore.dto;

public class CardDetailsDTO {
	private int cardDetailsId;
	private int aitessId;
	private String cardIdentificationText;
	private int totalNumberOfCards;
	
	public int getCardDetailsId() {
		return cardDetailsId;
	}
	public void setCardDetailsId(int cardDetailsId) {
		this.cardDetailsId = cardDetailsId;
	}
	public int getAitessId() {
		return aitessId;
	}
	public void setAitessId(int aitessId) {
		this.aitessId = aitessId;
	}
	public String getCardIdentificationText() {
		return cardIdentificationText;
	}
	public void setCardIdentificationText(String cardIdentificationText) {
		this.cardIdentificationText = cardIdentificationText;
	}
	public int getTotalNumberOfCards() {
		return totalNumberOfCards;
	}
	public void setTotalNumberOfCards(int totalNumberOfCards) {
		this.totalNumberOfCards = totalNumberOfCards;
	} 
	
	

}
