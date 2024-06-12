package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestCardData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LRUTestStateObject {

//  LRU Mandatory Test Card List
	private static ObservableList<SelfTestCardData> lruMandatoryCardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getLruMandatoryCardList() {
		return lruMandatoryCardList;
	}
	public static void clearLruMandatoryCardList() {
		lruMandatoryCardList.clear();
	}
	public static void addLruMandatoryCard(SelfTestCardData newCardData) {
		lruMandatoryCardList.add(newCardData);
	}
	public static void updateLruMandatoryCardstatus(String cardId, String cardName, int status) {
		for (SelfTestCardData cardData : lruMandatoryCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//  LRU SRU Test Card List
	private static ObservableList<SelfTestCardData> lruSruCardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getLruSruCardList() {
		return lruSruCardList;
	}
	public static void clearLruSruCardList() {
		lruSruCardList.clear();
	}
	public static void addLruSruCard(SelfTestCardData newCardData) {
		lruSruCardList.add(newCardData);
	}
	public static void updateLruSruCardstatus(String cardId, String cardName, int status) {
		for (SelfTestCardData cardData : lruSruCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//  LRU GO and NOGO Test Card List
	private static ObservableList<SelfTestCardData> lruGoAndNogoCardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getLruGoAndNogoCardList() {
		return lruGoAndNogoCardList;
	}
	public static void clearLruGoAndNogoCardList() {
		lruGoAndNogoCardList.clear();
	}
	public static void addLruGoAndNogoCard(SelfTestCardData newCardData) {
		lruGoAndNogoCardList.add(newCardData);
	}
	public static void updateLruGoAndNogoCardstatus(String cardId, String cardName, int status) {
		for (SelfTestCardData cardData : lruGoAndNogoCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//	LRU Test Result
	public static class LRUTestResult {
		private String cardName;
		private String fileName;
		private String result;
				
		public LRUTestResult(String cardName, String fileName, String result) {
			super();
			this.cardName = cardName;
			this.fileName = fileName;
			this.result = result;
		}
		public String getCardName() {
			return cardName;
		}
		public void setCardName(String cardName) {
			this.cardName = cardName;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		
	}

//	LRU Test Result List
	private static ObservableList<LRUTestResult> lruTestResultsList = FXCollections.observableArrayList();
	public static ObservableList<LRUTestResult> getTestFilesResultList() {
		return lruTestResultsList;
	}
	public static void clearlruTestResultsList() {
		lruTestResultsList.clear();
	}
	public static void addTestResult(LRUTestResult newTestResult) {
		lruTestResultsList.add(newTestResult);
	}
	public static void updatelruTestResultsListtatus(String cardName, String fileName, String result) {
		for (LRUTestResult testFile : lruTestResultsList) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}
	
	
}
