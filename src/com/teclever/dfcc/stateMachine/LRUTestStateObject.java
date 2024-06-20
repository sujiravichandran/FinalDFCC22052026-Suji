package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestCardData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	public static void updateLruMandatoryCardstatus(String cardId, String status) {
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
	public static void updateLruSruCardstatus(String cardId, String status) {
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
	public static void updateLruGoAndNogoCardstatus(String cardId, String status) {
		for (SelfTestCardData cardData : lruGoAndNogoCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//	LRU Test Result
	public static class LRUTestResult {
		private String fileName;
		private String result;
				
		public LRUTestResult( String fileName, String result) {
			super();
			this.fileName = fileName;
			this.result = result;
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
	public static void updatelruTestResultsListStatus(String cardName, String fileName, String result) {
		for (LRUTestResult testFile : lruTestResultsList) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}
	
//	For Running Card Status
	public enum LRUTestRunningCard {
		SPIL_LINK, POWER_SUPPLY, PBIT, AD_DA_INTERFACE, INITIALIZE_LRU,
		DIGITAL, ANALOG1_LEFT, ANALOG1_RIGHT, ANALOG2, ALL_SRUs,
		COMPLETE_TEST, OFP_LOADING, PI_CHECK
	}
	private static LRUTestRunningCard lruTestRunningCard = LRUTestRunningCard.SPIL_LINK;
	
	public static LRUTestRunningCard getLRUTestRunningCard() {
		return lruTestRunningCard;
	}
	public static void setLRUTestRunningCard(LRUTestRunningCard lruTestRunningCard) {
		LRUTestStateObject.lruTestRunningCard = lruTestRunningCard;
	}
	
//	Stage Status
	private static BooleanProperty isMandatoryFifthCardStatus = new SimpleBooleanProperty(false);
	private static BooleanProperty mandatoryTestStatus = new SimpleBooleanProperty(false);


	public static BooleanProperty isMandatoryFifthCardStatusProperty() {
		return isMandatoryFifthCardStatus;
	}
	public static BooleanProperty getIsMandatoryFifthCardStatus() {
		return isMandatoryFifthCardStatus;
	}
	public static void setIsMandatoryFifthCardStatus(BooleanProperty isMandatoryFifthCardStatus) {
		LRUTestStateObject.isMandatoryFifthCardStatus = isMandatoryFifthCardStatus;
	}
	public static BooleanProperty mandatoryTestStatusProperty() {
		return mandatoryTestStatus;
	}
	public static BooleanProperty getMandatoryTestStatus() {
		return mandatoryTestStatus;
	}
	public static void setMandatoryTestStatus(BooleanProperty mandatoryTestStatus) {
		LRUTestStateObject.mandatoryTestStatus = mandatoryTestStatus;
	}

//	Mandatory Each Card Status
	private static BooleanProperty spilLinkStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty pbitStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty initializeLRUStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty powerSupplyStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty ad_daInterfaceStatus = new SimpleBooleanProperty(true);

	public static BooleanProperty spilLinkStatusProperty() {
		return spilLinkStatus;
	}
	public static BooleanProperty getSpilLinkStatus() {
		return spilLinkStatus;
	}
	public static void setSpilLinkStatus(BooleanProperty spilLinkStatus) {
		LRUTestStateObject.spilLinkStatus = spilLinkStatus;
	}
	public static BooleanProperty pbitStatusProperty() {
		return pbitStatus;
	}
	public static BooleanProperty getPbitStatus() {
		return pbitStatus;
	}
	public static void setPbitStatus(BooleanProperty pbitStatus) {
		LRUTestStateObject.pbitStatus = pbitStatus;
	}
	public static BooleanProperty initializeLRUStatusProperty() {
		return initializeLRUStatus;
	}
	public static BooleanProperty getInitializeLRUStatus() {
		return initializeLRUStatus;
	}
	public static void setInitializeLRUStatus(BooleanProperty initializeLRUStatus) {
		LRUTestStateObject.initializeLRUStatus = initializeLRUStatus;
	}
	public static BooleanProperty powerSupplyStatusProperty() {
		return powerSupplyStatus;
	}
	public static BooleanProperty getPowerSupplyStatus() {
		return powerSupplyStatus;
	}
	public static void setPowerSupplyStatus(BooleanProperty powerSupplyStatus) {
		LRUTestStateObject.powerSupplyStatus = powerSupplyStatus;
	}
	public static BooleanProperty ad_daInterfaceStatusProperty() {
		return ad_daInterfaceStatus;
	}
	public static BooleanProperty getAd_daInterfaceStatus() {
		return ad_daInterfaceStatus;
	}
	public static void setAd_daInterfaceStatus(BooleanProperty ad_daInterfaceStatus) {
		LRUTestStateObject.ad_daInterfaceStatus = ad_daInterfaceStatus;
	}
	
//	GO NOGO Each Card Status
	private static BooleanProperty completeTestStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty ofpLoadingStatus = new SimpleBooleanProperty(true);
	private static BooleanProperty piCheckStatus = new SimpleBooleanProperty(true);
	
	public static BooleanProperty completeTestStatusProperty() {
		return completeTestStatus;
	}
	public static BooleanProperty getCompleteTestStatus() {
		return completeTestStatus;
	}
	public static void setCompleteTestStatus(BooleanProperty completeTestStatus) {
		LRUTestStateObject.completeTestStatus = completeTestStatus;
	}
	public static BooleanProperty ofpLoadingStatusProperty() {
		return ofpLoadingStatus;
	}
	public static BooleanProperty getOfpLoadingStatus() {
		return ofpLoadingStatus;
	}
	public static void setOfpLoadingStatus(BooleanProperty ofpLoadingStatus) {
		LRUTestStateObject.ofpLoadingStatus = ofpLoadingStatus;
	}
	public static BooleanProperty piCheckStatusProperty() {
		return piCheckStatus;
	}
	public static BooleanProperty getPiCheckStatus() {
		return piCheckStatus;
	}
	public static void setPiCheckStatus(BooleanProperty piCheckStatus) {
		LRUTestStateObject.piCheckStatus = piCheckStatus;
	}
	
	
//	Selected SRU Test Sub Stage
	private static ObservableList<SelfTestCardData> sruSubCardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getSRUSubCardList() {
		return sruSubCardList;
	}
	public static void clearSRUSubCardList() {
		sruSubCardList.clear();
	}
	public static void addSRUSubCardList(SelfTestCardData newCardData) {
		sruSubCardList.add(newCardData);
	}
	public static void updateSRUSubCardList(String cardId, String status) {
		for (SelfTestCardData cardData : sruSubCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//	Selected SRU Sub Stages List
	private static ObservableList<SelfTestCardData> selectedSubStagesList = FXCollections.observableArrayList();
	
	public static ObservableList<SelfTestCardData> getSelectedSubStagesList () {
		return selectedSubStagesList;
	}
	public static void clearSelectedSubStagesList() {
		selectedSubStagesList.clear();
	}
	public static void addSelectedSubStagesList(SelfTestCardData newCardData) {
		selectedSubStagesList.add(newCardData);
	}
	public static void removeSelectedSubStagesList(SelfTestCardData newCardData) {
		selectedSubStagesList.remove(newCardData);
	}
	public static void updateSelectedSubStagesList(String cardId, String status) {
		for (SelfTestCardData cardData : selectedSubStagesList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
}
