package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.entities.CardDetails;

public class CardDetailsResponseDTO {

	private int cardDetailsId;
	private int aitessId;
	private String cardIdentificationText;
	private int totalNumberOfCards;
	private List<CardDetailsDTO> cardLst;
	private int code;
	private String msg;
	private String eMsg;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String geteMsg() {
		return eMsg;
	}
	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
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
	public List<CardDetailsDTO> getCardLst() {
		return cardLst;
	}
	public void setCardLst(List<CardDetailsDTO> cardLst) {
		this.cardLst = cardLst;
	}

	
}
