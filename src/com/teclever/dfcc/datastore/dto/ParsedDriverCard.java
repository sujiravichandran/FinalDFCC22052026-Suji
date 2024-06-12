package com.teclever.dfcc.datastore.dto;

public class ParsedDriverCard {
    private String cardName;
    private String actualNumberOfCards;

    // Constructors, getters, and setters
    public ParsedDriverCard(String cardName, String actualNumberOfCards) {
        this.cardName = cardName;
        this.actualNumberOfCards = actualNumberOfCards;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getActualNumberOfCards() {
        return actualNumberOfCards;
    }

    public void setActualNumberOfCards(String actualNumberOfCards) {
        this.actualNumberOfCards = actualNumberOfCards;
    }
}
