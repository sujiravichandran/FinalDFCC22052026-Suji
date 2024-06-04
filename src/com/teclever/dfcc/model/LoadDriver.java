package com.teclever.dfcc.model;

public class LoadDriver {
	private String cardName;
	private String expectedCardCount;
	private String actualCardCount;
	private String status;
	
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
		
	public String getExpectedCardCount() {
		return expectedCardCount;
	}
	public void setExpectedCardCount(String expectedCardCount) {
		this.expectedCardCount = expectedCardCount;
	}
	public String getActualCardCount() {
		return actualCardCount;
	}
	public void setActualCardCount(String actualCardCount) {
		this.actualCardCount = actualCardCount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
