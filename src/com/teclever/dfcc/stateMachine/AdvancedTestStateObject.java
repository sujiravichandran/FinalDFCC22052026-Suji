package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdvancedTestStateObject {

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
}
