package com.teclever.dfcc.stateMachine;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
		private String testTypeId;
		private StringProperty status;
			
		public SelfTestCardData(String cardId, String cardName, String testTypeId, String string) {
			super();
			this.cardId = cardId;
			this.cardName = cardName;
			this.testTypeId = testTypeId;
			this.status = new SimpleStringProperty(string);
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
		public String getTestTypeId() {
			return testTypeId;
		}
		public void setTestTypeId(String testTypeId) {
			this.testTypeId = testTypeId;
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
	
	

//	For Running Card Status
	public enum SelfTestRunningCard {
		RACK1, B1553, RS422_1, RS422_2
	}
	private static SelfTestRunningCard selfTestRunningCard = SelfTestRunningCard.RACK1;
	
	public static SelfTestRunningCard getSelfTestRunningCard() {
		return selfTestRunningCard;
	}
	public static void setSelfTestRunningCard(SelfTestRunningCard selfTestRunningCard) {
		SelfTestStateObject.selfTestRunningCard = selfTestRunningCard;
	}

	
//	For Each Card Status
	private static BooleanProperty rack1Status = new SimpleBooleanProperty(true);
	private static BooleanProperty b1553Status = new SimpleBooleanProperty(true);
	private static BooleanProperty rs422_1Status = new SimpleBooleanProperty(true);
	private static BooleanProperty rs422_2Status = new SimpleBooleanProperty(true);

	public static BooleanProperty rack1StatusProperty() {
		return rack1Status;
	}
	public static BooleanProperty getRack1Status() {
		return rack1Status;
	}
	public static void setRack1Status(BooleanProperty rack1Status) {
		SelfTestStateObject.rack1Status = rack1Status;
	}
	public static BooleanProperty b1553StatusProperty() {
		return b1553Status;
	}
	public static BooleanProperty getB1553Status() {
		return b1553Status;
	}
	public static void setB1553Status(BooleanProperty b1553Status) {
		SelfTestStateObject.b1553Status = b1553Status;
	}
	public static BooleanProperty rs422_1StatusProperty() {
		return rs422_1Status;
	}
	public static BooleanProperty getRs422_1Status() {
		return rs422_1Status;
	}
	public static void setRs422_1Status(BooleanProperty rs422_1Status) {
		SelfTestStateObject.rs422_1Status = rs422_1Status;
	}
	public static BooleanProperty rs422_2StatusProperty() {
		return rs422_2Status;
	}
	public static BooleanProperty getrS422_2Status() {
		return rs422_2Status;
	}
	public static void setrS422_2Status(BooleanProperty rs422_2Status) {
		SelfTestStateObject.rs422_2Status = rs422_2Status;
	}

	
}
