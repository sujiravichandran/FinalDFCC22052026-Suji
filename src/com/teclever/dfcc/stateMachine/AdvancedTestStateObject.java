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
	
}
