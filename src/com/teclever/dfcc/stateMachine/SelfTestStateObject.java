package com.teclever.dfcc.stateMachine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SelfTestStateObject {
	
//	Rack1 Details
	private static String rack1StageId;
	private static String rack1StageName;
	private static String rack1TestTypeId;

	public static String getRack1StageId() {
		return rack1StageId;
	}
	public static void setRack1StageId(String rack1StageId) {
		SelfTestStateObject.rack1StageId = rack1StageId;
	}
	public static String getRack1StageName() {
		return rack1StageName;
	}
	public static void setRack1StageName(String rack1StageName) {
		SelfTestStateObject.rack1StageName = rack1StageName;
	}
	public static String getRack1TestTypeId() {
		return rack1TestTypeId;
	}
	public static void setRack1TestTypeId(String rack1TestTypeId) {
		SelfTestStateObject.rack1TestTypeId = rack1TestTypeId;
	}
	
	

//  Self Test All Card Data Object
	public static class SelfTestCardData {
		private String cardId;
		private String cardName;
		private StringProperty status;

		public SelfTestCardData(String cardId, String cardName, String status) {
			this.cardId = cardId;
			this.cardName = cardName;
			this.status = new SimpleStringProperty(status);
		}
		public String getCardId() {
			return cardId;
		}
		public void setCardId(String cardId) {
			this.cardId = cardId;
		}
		public String getCardName() {
			return cardName;
		}
		public void setCardName(String cardName) {
			this.cardName = cardName;
		}
		public StringProperty statusProperty() {
			return status;
		}
		public String getStatus() {
			return status.get(); 
		}
		public void setStatus(String string) {
			this.status.set(string); 
		}
	}

//  Self Test Rack-1 Card List
	private static ObservableList<SelfTestCardData> selfTestRack1CardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getSelfTestRack1Card() {
		return selfTestRack1CardList;
	}
	public static void clearSelfTestRack1Card() {
		selfTestRack1CardList.clear();
	}
	public static void addSelfTestRack1Card(SelfTestCardData newCardData) {
		selfTestRack1CardList.add(newCardData);
	}
	public static void updateSelfTestRack1Cardstatus(String cardId, String string) {
		for (SelfTestCardData cardData : selfTestRack1CardList) {
			if (cardData.getCardId().equals(cardId.trim())) {
				cardData.setStatus(string);
				break;
			}
		}
	}

	// Self Test cPCI Card List
	private static ObservableList<SelfTestCardData> selfTestcPCICardList = FXCollections.observableArrayList();

	public static ObservableList<SelfTestCardData> getSelfTestcPCICard() {
		return selfTestcPCICardList;
	}
	public static void clearSelfTestcPCICard() {
		selfTestcPCICardList.clear();
	}
	public static void addSelfTestcPCICard(SelfTestCardData newCardData) {
		selfTestcPCICardList.add(newCardData);
	}
	public static void updateSelfTestcPCICardstatus(String cardId, String status) {
		for (SelfTestCardData cardData : selfTestcPCICardList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}

	// Self Test Files Results
	public static class SelfTestResult {
		private String fileName;
		private String result;

		public SelfTestResult(String fileName, String result) {
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

	// Self Test Files Results List
	private static ObservableList<SelfTestResult> selfTestResults = FXCollections.observableArrayList();

	public static ObservableList<SelfTestResult> getTestResults() {
		return selfTestResults;
	}
	public static void clearselfTestResults() {
		selfTestResults.clear();
	}
	public static void addSelfTestResult(SelfTestResult newTestResult) {
		selfTestResults.add(newTestResult);
	}
	public static void updateSelfTestResultstatus( String fileName, String result) {
		for (SelfTestResult testFile : selfTestResults) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}
	
		
	
}
