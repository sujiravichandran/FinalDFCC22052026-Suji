package com.teclever.dfcc.utils;

import java.util.List;
import java.util.Map;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;

public class Debug {
	public static void printDebug(String message) {
		if (DFCCConstant.isDebug) {
			System.out.println(message);
		}
	}

	public static void printDebug(TestState testState) {
		if (DFCCConstant.isDebug) {
			System.out.println(testState);
		}
	}

	public static void printDebug(List<String> listOfData) {
		if (DFCCConstant.isDebug) {
			System.out.println(listOfData);
		}
	}

	public static void printDebug() {
		if (DFCCConstant.isDebug) {
			System.out.println();
		}
	}

	public static void printDebug(Map<String, String> mapData) {
		if (DFCCConstant.isDebug) {
			System.out.println(mapData);
		}
	}
}
