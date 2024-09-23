package com.teclever.dfcc.stateMachine;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.model.StageIdName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public class SessionTestStateObject {

//	Stage Level Data
	private static ObservableMap<String, StageObject> l1StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private static ObservableMap<String, StageIdName> l2StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private static ObservableMap<String, StageIdName> l3StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private static ObservableMap<String, StageIdName> l4StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private static ObservableMap<String, StageIdName> l5StageMap = FXCollections.observableMap(new LinkedHashMap<>());

	public static ObservableMap<String, StageObject> getL1StageMap() {
		return l1StageMap;
	}

	public static void addL1StageMap(String stageId, StageObject stage) {
		l1StageMap.put(stageId, stage);
	}

	public static ObservableMap<String, StageIdName> getL2StageMap() {
		return l2StageMap;
	}

	public static void addL2StageMap(String stageId, StageIdName subStageData) {
		l2StageMap.put(stageId, subStageData);
	}

	public static ObservableMap<String, StageIdName> getL3StageMap() {
		return l3StageMap;
	}

	public static void addL3StageMap(String stageId, StageIdName subStageData) {
		l3StageMap.put(stageId, subStageData);
	}

	public static ObservableMap<String, StageIdName> getL4StageMap() {
		return l4StageMap;
	}

	public static void addL4StageMap(String stageId, StageIdName subStageData) {
		l4StageMap.put(stageId, subStageData);
	}

	public static ObservableMap<String, StageIdName> getL5StageMap() {
		return l5StageMap;
	}

	public static void addL5StageMap(String stageId, StageIdName subStageData) {
		l5StageMap.put(stageId, subStageData);
	}

	public static void clearStageMap() {
		l1StageMap.clear();
		l2StageMap.clear();
		l3StageMap.clear();
		l4StageMap.clear();
		l5StageMap.clear();
	}

//	L1 Mandatory Status
	private static ObservableMap<String, Boolean> l1MandatoryStatus = FXCollections
			.observableMap(new LinkedHashMap<>());

	public static ObservableMap<String, Boolean> getL1MandatoryStatus() {
		return l1MandatoryStatus;
	}

	public static void addL1MandatoryStatus(String stageId, Boolean status) {
		l1MandatoryStatus.put(stageId, status);
	}

//	L1 Continue With Error Status
	private static ObservableMap<String, Boolean> l1ContinueWithErrorStatus = FXCollections
			.observableMap(new LinkedHashMap<>());

	public static ObservableMap<String, Boolean> getL1ContinueWithErrorStatus() {
		return l1ContinueWithErrorStatus;
	}

	public static void addL1ContinueWithErrorStatus(String stageId, Boolean status) {
		l1ContinueWithErrorStatus.put(stageId, status);
	}

	// L1 Stage Id With Corresponding EndLeaf Id
	private static ObservableMap<String, ObservableList<String>> l1StagesWithEndLeadId = FXCollections
			.observableMap(new LinkedHashMap<>());

	public static ObservableMap<String, ObservableList<String>> getL1StagesWithEndLeadId() {
		return l1StagesWithEndLeadId;
	}

	public static void addEndLeafToL1StagesWithEndLeadId(String l1StageId, String endLeafId) {
		ObservableList<String> endLeafIds = l1StagesWithEndLeadId.getOrDefault(l1StageId,
				FXCollections.observableArrayList());
		endLeafIds.add(endLeafId);
		l1StagesWithEndLeadId.put(l1StageId, endLeafIds);
	}

	// List of All Stage EndLeaf With Status
	private static ObservableMap<StageIdName, String> endLeafMap = FXCollections.observableMap(new LinkedHashMap<>());

	public static ObservableMap<StageIdName, String> getEndLeafMap() {
		return endLeafMap;
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

//	Store EndLeaf Completed TestFile 
	private static ObservableMap<String, ObservableSet<String>> stageIdWithFileIds = FXCollections
			.observableMap(new LinkedHashMap<>());

	public static ObservableMap<String, ObservableSet<String>> getStageIdWithFileIds() {
		return stageIdWithFileIds;
	}

	public static void setStageIdWithFileIds(String stageId, String fileId) {
		if (stageIdWithFileIds.containsKey(stageId)) {
			ObservableSet<String> setOfFileIds = stageIdWithFileIds.get(stageId);
			setOfFileIds.add(fileId);
			stageIdWithFileIds.remove(stageId);
			stageIdWithFileIds.put(stageId, setOfFileIds);
		} else {
			ObservableSet<String> setOfFileIds = FXCollections.observableSet(new LinkedHashSet<>());
			setOfFileIds.add(fileId);
			stageIdWithFileIds.put(stageId, setOfFileIds);
		}
	}

	public static void clearStageIdWithFileIds() {
		stageIdWithFileIds.clear();
	}

	public static void removeFileId(String stageId, String testFileId) {
		SessionTestStateObject.getStageIdWithFileIds().get(stageId).remove(testFileId);

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

	public static void updateSessionTestResultStatus(String fileName, String result) {
		for (SessionTestResult testFile : sessionTestResults) {
			if (testFile.getFileName().equals(fileName)) {
				testFile.setResult(result);
				break;
			}
		}
	}

//	Session Test Copying RDF File Popup Action
//	SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);

	private static BooleanProperty isRdfFileCopyPopupStatus = new SimpleBooleanProperty(false);

	public static BooleanProperty isRdfFileCopyPopupStatusProperty() {
		return isRdfFileCopyPopupStatus;
	}

	public static BooleanProperty getIsRdfFileCopyPopupStatus() {
		return isRdfFileCopyPopupStatus;
	}

	public static void setIsRdfFileCopyPopupStatus(BooleanProperty isRdfFileCopyPopupStatus) {
		SessionTestStateObject.isRdfFileCopyPopupStatus = isRdfFileCopyPopupStatus;
	}

	private static String popupStageId;

	public static String getPopupStageId() {
		return popupStageId;
	}

	public static void setPopupStageId(String popupStageId) {
		SessionTestStateObject.popupStageId = popupStageId;
	}

//	Progress Bar
	private static int totalSelectedTestFileCount;
	private static IntegerProperty runnedTestFileCount = new SimpleIntegerProperty(0);

	public static int getTotalSelectedTestFileCount() {
		return totalSelectedTestFileCount;
	}

	public static void setTotalSelectedTestFileCount(int totalSelectedTestFileCount) {
		SessionTestStateObject.totalSelectedTestFileCount = totalSelectedTestFileCount;
	}

	public static IntegerProperty runnedTestFileCountProperty() {
		return runnedTestFileCount;
	}

	public static IntegerProperty getRunnedTestFileCount() {
		return runnedTestFileCount;
	}

	public static void resetSessionTestStateObject() {
		clearStageMap();
		l1MandatoryStatus.clear();
		l1ContinueWithErrorStatus.clear();
		l1StagesWithEndLeadId.clear();
		endLeafMap.clear();
		currentRunningStageId = null;
		runningTestLeafId = null;
		runningTestLeafStatus.set(false);
		stageIdWithFileIds.clear();
		sessionTestResults.clear();
		isRdfFileCopyPopupStatus.set(false);
		popupStageId = null;
		totalSelectedTestFileCount = 0;
		runnedTestFileCount.set(0);
	}

}