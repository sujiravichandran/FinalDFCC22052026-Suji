package com.teclever.dfcc.stateMachine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TestCardDataObject {
	
//  Test Card Data Object
	public static class TestCardData {
		private String cardId;
		private String cardName;
		private String testTypeId;
		private StringProperty status;
			
		public TestCardData(String cardId, String cardName, String testTypeId, String string) {
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
}
