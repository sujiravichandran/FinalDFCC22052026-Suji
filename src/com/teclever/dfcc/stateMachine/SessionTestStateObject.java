package com.teclever.dfcc.stateMachine;

import com.teclever.dfcc.model.StageIdName;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class SessionTestStateObject {

	private static ObservableMap<String , String> l1StageMap = FXCollections.observableHashMap();
	private static ObservableMap<String , StageIdName> l2StageMap = FXCollections.observableHashMap();
	private static ObservableMap<String , StageIdName> l3StageMap = FXCollections.observableHashMap();
	private static ObservableMap<String , StageIdName> l4StageMap = FXCollections.observableHashMap();
	private static ObservableMap<String , StageIdName> l5StageMap = FXCollections.observableHashMap();

	public static ObservableMap<String , String> getL1StageMap() {
		return l1StageMap;
	}
	public static void addL1StageMap(String stageId , String stageName) {
		l1StageMap.put(stageId , stageName);
	}
	
	public static ObservableMap<String , StageIdName> getL2StageMap() {
		return l2StageMap;
	}
	public static void addL2StageMap(String stageId , StageIdName subStageData) {
		l2StageMap.put(stageId , subStageData);
	}

	public static ObservableMap<String , StageIdName> getL3StageMap() {
		return l3StageMap;
	}
	public static void addL3StageMap(String stageId , StageIdName subStageData) {
		l3StageMap.put(stageId , subStageData);
	}
	
	public static ObservableMap<String , StageIdName> getL4StageMap() {
		return l4StageMap;
	}
	public static void addL4StageMap(String stageId , StageIdName subStageData) {
		l4StageMap.put(stageId , subStageData);
	}
	
	public static ObservableMap<String , StageIdName> getL5StageMap() {
		return l5StageMap;
	}
	public static void addL5StageMap(String stageId , StageIdName subStageData) {
		l5StageMap.put(stageId , subStageData);
	}
	

	public static void clearStageMap() {
		l1StageMap.clear();
		l2StageMap.clear();
		l3StageMap.clear();
		l4StageMap.clear();
		l5StageMap.clear();
	}
	
}
