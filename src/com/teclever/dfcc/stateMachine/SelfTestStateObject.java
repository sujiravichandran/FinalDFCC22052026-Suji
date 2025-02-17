package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

//  Self Test Rack-1 Card List

	private static ObservableList<TestCardData> selfTestRack1CardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getSelfTestRack1Card() {
		return selfTestRack1CardList;
	}

	public static void clearSelfTestRack1Card() {
		selfTestRack1CardList.clear();
	}

	public static void addSelfTestRack1Card(TestCardData newCardData) {
		selfTestRack1CardList.add(newCardData);
	}

	public static void updateSelfTestRack1Cardstatus(String cardId, String string) {
		for (TestCardData cardData : selfTestRack1CardList) {
			if (cardData.getCardId().equals(cardId.trim())) {
				cardData.setStatus(string);
				break;
			}
		}
	}

//	Self Test Progress Bar
	private static int totalSelfTestFileCount;

	private static IntegerProperty runnedSelfTestFileCount = new SimpleIntegerProperty(0);

	public static int getTotalSelfTestFileCount() {
		return totalSelfTestFileCount;
	}

	public static void setTotalSelfTestFileCount(int totalSelfTestFileCount) {
		SelfTestStateObject.totalSelfTestFileCount = totalSelfTestFileCount;
	}

	public static IntegerProperty runnedSelfTestFileCountProperty() {
		return runnedSelfTestFileCount;
	}

	public static IntegerProperty getRunnedSelfTestFileCount() {
		return runnedSelfTestFileCount;
	}

	
	
	// Self Test cPCI Card List
	private static ObservableList<TestCardData> selfTestcPCICardList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getSelfTestcPCICard() {
		return selfTestcPCICardList;
	}

	public static void clearSelfTestcPCICard() {
		selfTestcPCICardList.clear();
	}

	public static void addSelfTestcPCICard(TestCardData newCardData) {
		selfTestcPCICardList.add(newCardData);
	}

	public static void updateSelfTestcPCICardstatus(String cardId, String status) {
		for (TestCardData cardData : selfTestcPCICardList) {
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

	public static void updateSelfTestResultstatus(String fileName, String result) {
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
	
	
	//Suji
	public static String getCPCIFile1() {
	    // Logic to fetch the first CPCI file
	    return "File1";
	}

	public static String getCPCIFile2() {
	    // Logic to fetch the second CPCI file
	    return "File2";
	}

	public static String getCPCIFile3() {
	    // Logic to fetch the third CPCI file
	    return "File3";
	}
	
	public static String getCPCIFile4() {
	    // Logic to fetch the third CPCI file
	    return "File4";
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

	public static void resetSelfTestStateObject() {
		// Reset Rack1 Details
		rack1StageId = null;
		rack1StageName = null;
		rack1TestTypeId = null;

		// Clear Self Test Rack-1 Card List
		clearSelfTestRack1Card();

		// Clear Self Test cPCI Card List
		clearSelfTestcPCICard();

		// Clear Self Test Files Results List
		clearselfTestResults();

		// Reset Running Card Status
		selfTestRunningCard = SelfTestRunningCard.RACK1;

		// Reset Card Status Properties
		rack1Status.set(true);
		b1553Status.set(true);
		rs422_1Status.set(true);
		rs422_2Status.set(true);

		totalSelfTestFileCount = 0;
		runnedSelfTestFileCount.set(0);
	}

}
