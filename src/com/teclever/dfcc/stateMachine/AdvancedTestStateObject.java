package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdvancedTestStateObject {

//	Advanced Test SubStage Id
	private static String hwatpTestId;
	private static String interfaceTestId;
	private static String customTest1Id;
	private static String customTest2Id;

	public static String getHwatpTestId() {
		return hwatpTestId;
	}
	public static void setHwatpTestId(String hwatpTestId) {
		AdvancedTestStateObject.hwatpTestId = hwatpTestId;
	}
	public static String getInterfaceTestId() {
		return interfaceTestId;
	}
	public static void setInterfaceTestId(String interfaceTestId) {
		AdvancedTestStateObject.interfaceTestId = interfaceTestId;
	}
	public static String getCustomTest1Id() {
		return customTest1Id;
	}
	public static void setCustomTest1Id(String customTest1Id) {
		AdvancedTestStateObject.customTest1Id = customTest1Id;
	}
	public static String getCustomTest2Id() {
		return customTest2Id;
	}
	public static void setCustomTest2Id(String customTest2Id) {
		AdvancedTestStateObject.customTest2Id = customTest2Id;
	}

//  HWATP Test Card List
	private static ObservableList<TestCardData> hwatpTestList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getHwatpTestList() {
		return hwatpTestList;
	}

	public static void clearHwatpTestList() {
		hwatpTestList.clear();
	}

	public static void addHwatpTestList(TestCardData newCardData) {
		hwatpTestList.add(newCardData);
	}

	public static void updateHwatpTeststatus(String cardId, String status) {
		for (TestCardData cardData : hwatpTestList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}

	private static BooleanProperty hwatpTestStatus = new SimpleBooleanProperty(true);

	public static BooleanProperty hwatpTestStatusProperty() {
		return hwatpTestStatus;
	}	public static BooleanProperty getHwatpTestStatus() {
		return hwatpTestStatus;
	}
	public static void setHwatpTestStatus(BooleanProperty hwatpTestStatus) {
		AdvancedTestStateObject.hwatpTestStatus = hwatpTestStatus;
	}

//  Interface Test Card List
	private static ObservableList<TestCardData> interfaceTestList = FXCollections.observableArrayList();

	public static ObservableList<TestCardData> getInterfaceTestList() {
		return interfaceTestList;
	}

	public static void clearInterfaceTestList() {
		interfaceTestList.clear();
	}

	public static void addInterfaceTestList(TestCardData newCardData) {
		interfaceTestList.add(newCardData);
	}

	public static void updateInterfaceTeststatus(String cardId, String status) {
		for (TestCardData cardData : interfaceTestList) {
			if (cardData.getCardId().equals(cardId)) {
				cardData.setStatus(status);
				break;
			}
		}
	}

	private static BooleanProperty interfaceTestStatus = new SimpleBooleanProperty(true);

	public static BooleanProperty interfaceTestStatusProperty() {
		return interfaceTestStatus;
	}
	public static BooleanProperty getInterfaceTestStatus() {
		return interfaceTestStatus;
	}
	public static void setInterfaceTestStatus(BooleanProperty interfaceTestStatus) {
		AdvancedTestStateObject.interfaceTestStatus = interfaceTestStatus;
	}
	
//	Custom Test 1 Status
	private static BooleanProperty customTest1Status = new SimpleBooleanProperty(true);

	public static BooleanProperty customTest1StatusProperty() {
		return customTest1Status;
	}	
	public static BooleanProperty getCustomTest1Status() {
		return customTest1Status;
	}
	public static void setCustomTest1Status(BooleanProperty customTest1Status) {
		AdvancedTestStateObject.customTest1Status = customTest1Status;
	}
	
	
//	Custom Test 2 Status
	private static BooleanProperty customTest2Status = new SimpleBooleanProperty(true);
	
	public static BooleanProperty customTest2StatusProperty() {
		return customTest2Status;
	}	
	public static BooleanProperty getCustomTest2Status() {
		return customTest2Status;
	}
	public static void setCustomTest2Status(BooleanProperty customTest2Status) {
		AdvancedTestStateObject.customTest2Status = customTest2Status;
	}

//	Advanced Test Result
	public static class AdvancedTestResult {
		private String fileName;
		private String result;

		public AdvancedTestResult(String fileName, String result) {
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

//	Advanced Test Result List
	private static ObservableList<AdvancedTestResult> advancedTestResultsList = FXCollections.observableArrayList();

	public static ObservableList<AdvancedTestResult> getTestFilesResultList() {
		return advancedTestResultsList;
	}

	public static void clearAdvancedTestResultsList() {
		advancedTestResultsList.clear();
	}

	public static void addAdvancedTestResult(AdvancedTestResult newTestResult) {
		advancedTestResultsList.add(newTestResult);
	}

	public static void updateAdvancedTestResultsListStatus(String cardName, String fileName, String result) {
		for (AdvancedTestResult testFile : advancedTestResultsList) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}

}
