package com.teclever.dfcc.datastore.dto;

public class DbDriverCard {
    private String cardName;
    private String cardIdentificationText;
    private String totalNumberOfCards;

    // Constructors, getters, and setters
    public DbDriverCard(String cardName, String totalNumberOfCards , String cardIdentificationText ) {
        this.cardName = cardName;
        this.cardIdentificationText = cardIdentificationText;
        this.totalNumberOfCards = totalNumberOfCards;
    }

    public DbDriverCard(String cardName,String cardIdentificationText ) {
        this.cardName = cardName;
        this.cardIdentificationText = cardIdentificationText;
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

    public String getTotalNumberOfCards() {
        return totalNumberOfCards;
    }

    public void setTotalNumberOfCards(String totalNumberOfCards) {
        this.totalNumberOfCards = totalNumberOfCards;
    }
}
