package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestResult;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdvancedTestStateObject {
	
//	HWATP Test Card List
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
	}
	public static BooleanProperty getHwatpTestStatus() {
		return hwatpTestStatus;
	}
	public static void setHwatpTestStatus(BooleanProperty hwatpTestStatus) {
		AdvancedTestStateObject.hwatpTestStatus = hwatpTestStatus;
	}

//  LRU Mandatory Test Card List
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


//	Advanced Test Result
	public static class AdvancedTestResult {
		private String fileName;
		private String result;
				
		public AdvancedTestResult( String fileName, String result) {
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
