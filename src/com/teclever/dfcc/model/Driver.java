package com.teclever.dfcc.model;

public class Driver {
	
	private int sNo;

	private String cardName;

	private int noCards;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public int getNoCards() {
		return noCards;
	}

	public void setNoCards(int noCards) {
		this.noCards = noCards;
	}

	public Driver(int sNo, String cardName, int noCards) {
		super();
		this.sNo = sNo;
		this.cardName = cardName;
		this.noCards = noCards;
	}
	
	
}
