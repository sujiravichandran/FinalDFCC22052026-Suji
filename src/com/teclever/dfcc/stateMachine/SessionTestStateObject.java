package com.teclever.dfcc.stateMachine;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.teclever.dfcc.model.StageIdName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class SessionTestStateObject {

    private static ObservableMap<String, String> l1StageMap = FXCollections.observableMap(new LinkedHashMap<>());
    private static ObservableMap<String, StageIdName> l2StageMap = FXCollections.observableMap(new LinkedHashMap<>());
    private static ObservableMap<String, StageIdName> l3StageMap = FXCollections.observableMap(new LinkedHashMap<>());
    private static ObservableMap<String, StageIdName> l4StageMap = FXCollections.observableMap(new LinkedHashMap<>());
    private static ObservableMap<String, StageIdName> l5StageMap = FXCollections.observableMap(new LinkedHashMap<>());

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

	// Self Test Files Results
	public static class SessionTestResult {
		private String fileName;
		private String result;
	
		public SessionTestResult(String fileName, String result) {
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

// Session Test Files Results List
	private static ObservableList<SessionTestResult> sessionTestResults = FXCollections.observableArrayList();

	public static ObservableList<SessionTestResult> getSessionTestResults() {
		return sessionTestResults;
	}
	public static void clearSessionTestResults() {
		sessionTestResults.clear();
	}
	public static void addSessionTestResult(SessionTestResult newTestResult) {
		sessionTestResults.add(newTestResult);
	}
	public static void updateSessionTestResultStatus( String fileName, String result) {
		for (SessionTestResult testFile : sessionTestResults) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}
	
// L1 Stage Id With Corresponding EndLeaf Id
	private static ObservableMap<String, ObservableList<String>> l1StagesWithEndLeadId = FXCollections.observableMap(new LinkedHashMap<>());
	
	public static ObservableMap<String, ObservableList<String>> getL1StagesWithEndLeadId() {
		return l1StagesWithEndLeadId;
	}
	public static void setL1StagesWithEndLeadId(ObservableMap<String, ObservableList<String>> l1StagesWithEndLeadId) {
		SessionTestStateObject.l1StagesWithEndLeadId = l1StagesWithEndLeadId;
	}
	public static void addEndLeafToL1StagesWithEndLeadId(String l1StageId, String endLeafId) {
	    ObservableList<String> endLeafIds = l1StagesWithEndLeadId.getOrDefault(l1StageId, FXCollections.observableArrayList());
	    endLeafIds.add(endLeafId);
	    l1StagesWithEndLeadId.put(l1StageId, endLeafIds);
	}

	


// List of All Stage EndLeaf With Status
	private static ObservableMap<StageIdName,String> endLeafMap = FXCollections.observableMap(new LinkedHashMap<>());
	
	public static ObservableMap<StageIdName,String> getEndLeafMap() {
		return endLeafMap;
	}
	public static void setEndLeafMap(ObservableMap<StageIdName,String> endLeafList) {
		SessionTestStateObject.endLeafMap = endLeafList;
	}
	public static void clearEndLeafMap() {
		endLeafMap.clear();
	}
	
	public static void updateEndLeafMapStatus(String stageId, String status) {
		for (Entry<StageIdName, String> endLeaf : endLeafMap.entrySet()) {
			if (endLeaf.getKey().getStageId().equals(stageId)) {
				endLeafMap.put(endLeaf.getKey(), status);
				break;
			}
		}
	}
	

//	Running Test Leaf Id
	private static String currentRunningStageId;
	private static String runningTestLeafId;
	private static BooleanProperty runningTestLeafStatus = new SimpleBooleanProperty(false);
		
	public static String getCurrentRunningStageId() {
		return currentRunningStageId;
	}
	public static void setCurrentRunningStageId(String currentRunningStageId) {
		SessionTestStateObject.currentRunningStageId = currentRunningStageId;
	}
	public static String getRunningTestLeafId() {
		return runningTestLeafId;
	}
	public static void setRunningTestLeafId(String runningTestLeafId) {
		SessionTestStateObject.runningTestLeafId = runningTestLeafId;
	}
	public static BooleanProperty runningTestLeafStatusProperty() {
		return runningTestLeafStatus;
	}
	public static BooleanProperty getRunningTestLeafStatus() {
		return runningTestLeafStatus;
	}
	public static void setRunningTestLeafStatus(BooleanProperty runningTestLeafStatus) {
		SessionTestStateObject.runningTestLeafStatus = runningTestLeafStatus;
	}


	//	Selected Stages Test File List
	private static ObservableList<String> selectedStageTestFileList = FXCollections.observableArrayList();
	
	public static ObservableList<String> getSelectedStageTestFileList () {
		return selectedStageTestFileList;
	}
	public static void clearSelectedStageTestFileList () {
		selectedStageTestFileList.clear();
	}
	public static void addSelectedStageTestFileList (String newFile) {
		selectedStageTestFileList.add(newFile);
	}

	 
}
