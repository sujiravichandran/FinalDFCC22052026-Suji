package com.teclever.dfcc.stateMachine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LRUTestStateObject {
	
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

	private static ObservableList<LRUTestResult> lruTestResults = FXCollections.observableArrayList();

	public static ObservableList<LRUTestResult> getTestFiles() {
		return lruTestResults;
	}

	public static void clearlruTestResults() {
		lruTestResults.clear();
	}

	public static void addTestResult(LRUTestResult newTestResult) {
		lruTestResults.add(newTestResult);
	}

	public static void updatelruTestResultstatus(String cardName, String fileName, String result) {
		for (LRUTestResult testFile : lruTestResults) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}
	
	
}
