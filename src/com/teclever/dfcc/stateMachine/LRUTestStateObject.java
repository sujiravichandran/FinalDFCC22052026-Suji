package com.teclever.dfcc.stateMachine;


import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LRUTestStateObject {

//  LRU Mandatory Test Card List
	private static ObservableList<TestCardData> lruMandatoryCardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getLruMandatoryCardList() {
		return lruMandatoryCardList;
	}
	public static void clearLruMandatoryCardList() {
		lruMandatoryCardList.clear();
	}
	public static void addLruMandatoryCard(TestCardData newCardData) {
		lruMandatoryCardList.add(newCardData);
	}
	public static void updateLruMandatoryCardstatus(String cardId, String status) {
		for (TestCardData cardData : lruMandatoryCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//  LRU SRU Test Card List
	private static ObservableList<TestCardData> lruSruCardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getLruSruCardList() {
		return lruSruCardList;
	}
	public static void clearLruSruCardList() {
		lruSruCardList.clear();
	}
	public static void addLruSruCard(TestCardData newCardData) {
		lruSruCardList.add(newCardData);
	}
	public static void updateLruSruCardstatus(String cardId, String status) {
		for (TestCardData cardData : lruSruCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
//  LRU GO and NOGO Test Card List
	private static ObservableList<TestCardData> lruGoAndNogoCardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getLruGoAndNogoCardList() {
		return lruGoAndNogoCardList;
	}
	public static void clearLruGoAndNogoCardList() {
		lruGoAndNogoCardList.clear();
	}
	public static void addLruGoAndNogoCard(TestCardData newCardData) {
		lruGoAndNogoCardList.add(newCardData);
	}
	public static void updateLruGoAndNogoCardstatus(String cardId, String status) {
		for (TestCardData cardData : lruGoAndNogoCardList) {
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
	private static ObservableList<TestCardData> sruSubCardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getSRUSubCardList() {
		return sruSubCardList;
	}
	public static void clearSRUSubCardList() {
		sruSubCardList.clear();
	}
	public static void addSRUSubCardList(TestCardData newCardData) {
		sruSubCardList.add(newCardData);
	}
	public static void updateSRUSubCardList(String cardId, String status) {
		for (TestCardData cardData : sruSubCardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
	// Method to get all test files from different lists
	public static ObservableList<TestCardData> getAllTestFiles() {
	    ObservableList<TestCardData> allTestFiles = FXCollections.observableArrayList();
	    
	    // Add all items from LRU Mandatory Test Card List
	    allTestFiles.addAll(lruMandatoryCardList);
	    
	    // Add all items from LRU SRU Test Card List
	    allTestFiles.addAll(lruSruCardList);
	    
	    // Add all items from LRU GO and NOGO Test Card List
	    allTestFiles.addAll(lruGoAndNogoCardList);
	    
	    allTestFiles.addAll(sruSubCardList);
	    
	    
	    
	    return allTestFiles;
	}
	
//	Selected SRU Sub Stages List
	private static ObservableList<TestCardData> selectedSubStagesList = FXCollections.observableArrayList();
	
	public static ObservableList<TestCardData> getSelectedSubStagesList () {
		return selectedSubStagesList;
	}
	public static void clearSelectedSubStagesList() {
		selectedSubStagesList.clear();
	}
	public static void addSelectedSubStagesList(TestCardData newCardData) {
		selectedSubStagesList.add(newCardData);
	}
	public static void removeSelectedSubStagesList(TestCardData newCardData) {
		selectedSubStagesList.remove(newCardData);
	}
	public static void updateSelectedSubStagesList(String cardId, String status) {
		for (TestCardData cardData : selectedSubStagesList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}
	
	
//	LRU SRU Progress Bar for Mandatory Test
	
	
	
	
//	LRU SRU Test Progress Bar for SRU Test
	private static int totalLRUSelectedTestFileCount;
	private static IntegerProperty runnedLRUTestFileCount = new SimpleIntegerProperty(0);

	public static int getTotalLRUSelectedTestFileCount() {
		return totalLRUSelectedTestFileCount;
	}
	public static void setTotalLRUSelectedTestFileCount(int totalLRUSelectedTestFileCount) {
		LRUTestStateObject.totalLRUSelectedTestFileCount = totalLRUSelectedTestFileCount;
	}
    public static IntegerProperty runnedLRUTestFileCountProperty() {
        return runnedLRUTestFileCount;
    }
    public static IntegerProperty getRunnedLRUTestFileCount() {
        return runnedLRUTestFileCount;
    }
	
	public static void resetLRUTestStateObject() {
	    // Clear Mandatory Test Card List
	    clearLruMandatoryCardList();

	    // Clear SRU Test Card List
	    clearLruSruCardList();

	    // Clear GO and NOGO Test Card List
	    clearLruGoAndNogoCardList();

	    // Clear Test Result List
	    clearlruTestResultsList();

	    // Reset Running Card Status
	    lruTestRunningCard = LRUTestRunningCard.SPIL_LINK;

	    // Reset Stage Status Properties
	    isMandatoryFifthCardStatus.set(false);
	    mandatoryTestStatus.set(false);

	    // Reset Mandatory Each Card Status
	    spilLinkStatus.set(true);
	    pbitStatus.set(true);
	    initializeLRUStatus.set(true);
	    powerSupplyStatus.set(true);
	    ad_daInterfaceStatus.set(true);

	    // Reset GO NOGO Each Card Status
	    completeTestStatus.set(true);
	    ofpLoadingStatus.set(true);
	    piCheckStatus.set(true);

	    // Clear Selected SRU Test Sub Stage
	    clearSRUSubCardList();

	    // Clear Selected SRU Sub Stages List
	    clearSelectedSubStagesList();
	    
	    // Clear Progress Bar Data
        totalLRUSelectedTestFileCount = 0;
        runnedLRUTestFileCount.set(0);	    

	}

	
}
