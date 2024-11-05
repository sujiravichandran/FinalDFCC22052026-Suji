package com.teclever.dfcc.stateMachine;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.utils.Debug;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class StateMachine {
	public enum TestState {
		PENDING, RUNNING, PAUSED, STOPPED, COMPLETED
	}

	public enum RunningTestName {
		OTHER, SELF_TEST, LRU_SRU_TEST, SESSION_TEST, ADVANCED_TEST
	}

	private static TestState testState = TestState.PENDING;
	private static RunningTestName runningTestName = RunningTestName.OTHER;

	public static TestState getTestState() {
		return testState;
	}

	public static void setTestState(TestState newState) {
		testState = newState;
	}

	public static RunningTestName getRunningTestName() {
		return runningTestName;
	}

	public static void setRunningTestName(RunningTestName newState) {
		runningTestName = newState;
	}

	public static class currentSessionDetails {
		private static String userId;
		private static String uutId;
		private static String uutType;
		private static String dfccSerialNumber;
		private static String sessionId;
		private static String sessionName;
		private static String sessionTypeID;
		private static String sessionTypeName;
		private static String runConfigId;
		private static int loginSessionId;

		public static String getUserId() {
			return userId;
		}

		public static void setUserId(String userId) {
			currentSessionDetails.userId = userId;
		}

		public static String getUutId() {
			return uutId;
		}

		public static void setUutId(String uutId) {
			currentSessionDetails.uutId = uutId;
		}

		public static String getUutType() {
			return uutType;
		}

		public static void setUutType(String uutType) {
			currentSessionDetails.uutType = uutType;
		}

		public static String getSessionId() {
			return sessionId;
		}

		public static void setSessionId(String sessionId) {
			currentSessionDetails.sessionId = sessionId;
		}

		public static String getSessionName() {
			return sessionName;
		}

		public static void setSessionName(String sessionName) {
			currentSessionDetails.sessionName = sessionName;
		}

		public static String getSessionTypeID() {
			return sessionTypeID;
		}

		public static void setSessionTypeID(String sessionTypeID) {
			currentSessionDetails.sessionTypeID = sessionTypeID;
		}

		public static String getSessionTypeName() {
			return sessionTypeName;
		}

		public static void setSessionTypeName(String sessionTypeName) {
			currentSessionDetails.sessionTypeName = sessionTypeName;
		}

		public static String getRunConfigId() {
			return runConfigId;
		}

		public static void setRunConfigId(String runConfigId) {
			Debug.printDebug(runConfigId);
			currentSessionDetails.runConfigId = runConfigId;
		}

		public static int getLoginSessionId() {
			return loginSessionId;
		}

		public static void setLoginSessionId(int loginSessionId) {
			currentSessionDetails.loginSessionId = loginSessionId;
		}

		public static String getDfccSerialNumber() {
			return dfccSerialNumber;
		}

		public static void setDfccSerialNumber(String dfccSerialNumber) {
			currentSessionDetails.dfccSerialNumber = dfccSerialNumber;
		}

	}

	public static class currentTestDetails {
		private static String runningTestPageId;
		private static String testType;
		private static int testFileCount;
		private static int repeatCount;
		private static double completionPercentage;

		public static String getRunningTestPageId() {
			return runningTestPageId;
		}

		public static void setRunningTestPageId(String runningTestPageId) {
			currentTestDetails.runningTestPageId = runningTestPageId;
		}

		public static String getTestType() {
			return testType;
		}

		public static void setTestType(String testType) {
			currentTestDetails.testType = testType;
		}

		public static int getTestFileCount() {
			return testFileCount;
		}

		public static void setTestFileCount(int testFileCount) {
			currentTestDetails.testFileCount = testFileCount;
		}

		public static int getRepeatCount() {
			return repeatCount;
		}

		public static void setRepeatCount(int repeatCount) {
			currentTestDetails.repeatCount = repeatCount;
		}

		public static double getCompletionPercentage() {
			return completionPercentage;
		}

		public static void setCompletionPercentage(double completionPercentage) {
			currentTestDetails.completionPercentage = completionPercentage;
		}

	}

	private static List<StageObject> stageDatalist;

	public static List<StageObject> getStageDatalist() {
		return stageDatalist;
	}

	public static void setStageDatalist(List<StageObject> stageDatalist) {
		StateMachine.stageDatalist = stageDatalist;
	}

	// USER ACTION
	private static String userAction;
	private static String userExitError;

	public static String getUserExitError() {
		return userExitError;
	}

	public static void setUserExitError(String userExitError) {
		StateMachine.userExitError = userExitError;
	}

	private static BooleanProperty userActionFlag = new SimpleBooleanProperty(false);

	public static String getUserAction() {
		return userAction;
	}

	public static void setUserAction(String userAction) {
		StateMachine.userAction = userAction;
	}

	public static BooleanProperty userActionFlagProperty() {
		return userActionFlag;
	}

	public static BooleanProperty getUserActionFlag() {
		return userActionFlag;
	}

	public static void setUserActionFlag(BooleanProperty userActionFlag) {
		StateMachine.userActionFlag = userActionFlag;
	}

	// LAUNCH AITESS 1, 2
	private static boolean aitess1Launched = false;
	private static boolean aitess2Launched = false;

	public static boolean isAitess1Launched() {
		return aitess1Launched;
	}

	public static void setAitess1Launched(boolean aitess1Launched) {
		StateMachine.aitess1Launched = aitess1Launched;
	}

	public static boolean isAitess2Launched() {
		return aitess2Launched;
	}

	public static void setAitess2Launched(boolean aitess2Launched) {
		StateMachine.aitess2Launched = aitess2Launched;
	}

	// DISABLING TEXT AREA FLAG
	private static boolean textArea = true;

	public static boolean isTextArea() {
		return textArea;
	}

	public static void setTextArea(boolean textArea) {
		StateMachine.textArea = textArea;
	}

	// AITESS 2
	public static class dfccCheckStatus {

		private static BooleanProperty dfccPowerStatus = new SimpleBooleanProperty(false);
		private static String dfccPowerOnCommand;
		private static String dfccPowerOffCommand;
		private static String onlineStatusCommand;
		private static String mk1ScTemperatureCommand;
		private static String mk1AecTemperatureCommand;

		private static String scTemperatureCommand;
		private static String aecTemperatureCommand;

		private static String ofpVersionStatusCommand;
		private static String wdmStatusCommand;
		
		private static String dfccPowerOnStatus;
		
		

		public static String getDfccPowerOnStatus() {
			return dfccPowerOnStatus;
		}

		public static void setDfccPowerOnStatus(String dfccPowerOnStatus) {
			dfccCheckStatus.dfccPowerOnStatus = dfccPowerOnStatus;
		}

		public static BooleanProperty dfccPowerStatusProperty() {
			return dfccPowerStatus;
		}

		public static BooleanProperty getDfccPowerStatus() {
			return dfccPowerStatus;
		}

		public static void setDfccPowerStatus(BooleanProperty dfccPowerStatus) {
			dfccCheckStatus.dfccPowerStatus = dfccPowerStatus;
		}

		public static String getDfccPowerOnCommand() {
			return dfccPowerOnCommand;
		}

		public static void setDfccPowerOnCommand(String dfccPowerOnCommand) {
			dfccCheckStatus.dfccPowerOnCommand = dfccPowerOnCommand;
		}

		public static String getDfccPowerOffCommand() {
			return dfccPowerOffCommand;
		}

		public static void setDfccPowerOffCommand(String dfccPowerOffCommand) {
			dfccCheckStatus.dfccPowerOffCommand = dfccPowerOffCommand;
		}

		public static String getOnlineStatusCommand() {
			return onlineStatusCommand;
		}

		public static void setOnlineStatusCommand(String onlineStatusCommand) {
			dfccCheckStatus.onlineStatusCommand = onlineStatusCommand;
		}

		public static String getMk1ScTemperatureCommand() {
			return mk1ScTemperatureCommand;
		}

		public static void setMk1ScTemperatureCommand(String mk1ScTemperatureCommand) {
			dfccCheckStatus.mk1ScTemperatureCommand = mk1ScTemperatureCommand;
		}

		public static String getMk1AecTemperatureCommand() {
			return mk1AecTemperatureCommand;
		}

		public static void setMk1AecTemperatureCommand(String mk1AecTemperatureCommand) {
			dfccCheckStatus.mk1AecTemperatureCommand = mk1AecTemperatureCommand;
		}

		public static String getScTemperatureCommand() {
			return scTemperatureCommand;
		}

		public static void setScTemperatureCommand(String scTemperatureCommand) {
			dfccCheckStatus.scTemperatureCommand = scTemperatureCommand;
		}

		public static String getAecTemperatureCommand() {
			return aecTemperatureCommand;
		}

		public static void setAecTemperatureCommand(String aecTemperatureCommand) {
			dfccCheckStatus.aecTemperatureCommand = aecTemperatureCommand;
		}

		public static String getOfpVersionStatusCommand() {
			return ofpVersionStatusCommand;
		}

		public static void setOfpVersionStatusCommand(String ofpVersionStatusCommand) {
			dfccCheckStatus.ofpVersionStatusCommand = ofpVersionStatusCommand;
		}

		public static String getWdmStatusCommand() {
			return wdmStatusCommand;
		}

		public static void setWdmStatusCommand(String wdmStatusCommand) {
			dfccCheckStatus.wdmStatusCommand = wdmStatusCommand;
		}

	}

	// STORING DATA FOR DFCC STATUS CHECK AITESS 2

	// POWER ON STATUS
	public static class powerOnStatus{

		private static StringProperty channel1Status = new SimpleStringProperty();
		private static StringProperty channel2Status = new SimpleStringProperty();
		private static StringProperty channel3Status = new SimpleStringProperty();
		private static StringProperty channel4Status = new SimpleStringProperty();
		
		
		private static String minValue;
		private static String maxValue;
		
		
		public static String getMinValue() {
			return minValue;
		}

		public static void setMinValue(String minValue) {
			powerOnStatus.minValue = minValue;
		}

		public static String getMaxValue() {
			return maxValue;
		}

		public static void setMaxValue(String maxValue) {
			powerOnStatus.maxValue = maxValue;
		}

		public static StringProperty channel1StatusProperty() {
			return channel1Status;
		}
		
		public static String getChannel1Status() {
			return channel1Status.get();
		}
		
		public static void setChannel1Status(String channel1Status) {
			powerOnStatus.channel1Status.set(channel1Status);
		}
		
		
		public static StringProperty channel2StatusProperty() {
			return channel2Status;
		}
		
		public static String getChannel2Status() {
			return channel2Status.get();
		}
		
		public static void setChannel2Status(String channel2Status) {
			powerOnStatus.channel2Status.set(channel2Status);
		}
		
		
		public static StringProperty channel3StatusProperty() {
			return channel3Status;
		}
		
		public static String getChannel3Status() {
			return channel3Status.get();
		}
		
		public static void setChannel3Status(String channel3Status) {
			powerOnStatus.channel3Status.set(channel3Status);
		}
		
		public static StringProperty channel4StatusProperty() {
			return channel4Status;
		}
		
		public static String getChannel4Status() {
			return channel4Status.get();
		}
		
		public static void setChannel4Status(String channel4Status) {
			powerOnStatus.channel4Status.set(channel4Status);
		}
		

	}
	
	
	// ONLINE STATUS
	public static class OnlineStatus {

		private static StringProperty channel1Status = new SimpleStringProperty();
		private static StringProperty channel2Status = new SimpleStringProperty();
		private static StringProperty channel3Status = new SimpleStringProperty();
		private static StringProperty channel4Status = new SimpleStringProperty();

		public static StringProperty channel1StatusProperty() {
			return channel1Status;
		}

		public static String getChannel1Status() {
			return channel1Status.get();
		}

		public static void setChannel1Status(String channel1Status) {
			OnlineStatus.channel1Status.set(channel1Status);
		}

		public static StringProperty channel2StatusProperty() {
			return channel2Status;
		}

		public static String getChannel2Status() {
			return channel2Status.get();
		}

		public static void setChannel2Status(String channel2Status) {
			OnlineStatus.channel2Status.set(channel2Status);
		}

		public static StringProperty channel3StatusProperty() {
			return channel3Status;
		}

		public static String getChannel3Status() {
			return channel3Status.get();
		}

		public static void setChannel3Status(String channel3Status) {
			OnlineStatus.channel3Status.set(channel3Status);
		}

		public static StringProperty channel4StatusProperty() {
			return channel4Status;
		}

		public static String getChannel4Status() {
			return channel4Status.get();
		}

		public static void setChannel4Status(String channel4Status) {
			OnlineStatus.channel4Status.set(channel4Status);
		}
	}

	// WDM STATUS
	public static class WDMStatus {

		private static String channel1Status;
		private static String channel2Status;
		private static String channel3Status;
		private static String channel4Status;

		public static String getChannel1Status() {
			return channel1Status;
		}

		public static void setChannel1Status(String channel1Status) {
			WDMStatus.channel1Status = channel1Status;
		}

		public static String getChannel2Status() {
			return channel2Status;
		}

		public static void setChannel2Status(String channel2Status) {
			WDMStatus.channel2Status = channel2Status;
		}

		public static String getChannel3Status() {
			return channel3Status;
		}

		public static void setChannel3Status(String channel3Status) {
			WDMStatus.channel3Status = channel3Status;
		}

		public static String getChannel4Status() {
			return channel4Status;
		}

		public static void setChannel4Status(String channel4Status) {
			WDMStatus.channel4Status = channel4Status;
		}

	}

	// OFP VERSION STATUS
	public static class OFPversionStatus {

		private static String channel1Status;
		private static String channel2Status;
		private static String channel3Status;
		private static String channel4Status;

		public static String getChannel1Status() {
			return channel1Status;
		}

		public static void setChannel1Status(String channel1Status) {
			OFPversionStatus.channel1Status = channel1Status;
		}

		public static String getChannel2Status() {
			return channel2Status;
		}

		public static void setChannel2Status(String channel2Status) {
			OFPversionStatus.channel2Status = channel2Status;
		}

		public static String getChannel3Status() {
			return channel3Status;
		}

		public static void setChannel3Status(String channel3Status) {
			OFPversionStatus.channel3Status = channel3Status;
		}

		public static String getChannel4Status() {
			return channel4Status;
		}

		public static void setChannel4Status(String channel4Status) {
			OFPversionStatus.channel4Status = channel4Status;
		}

	}

	// MK1 SC TEMPERATURE
	public static class channelSCTemp {
		private static StringProperty channel1Temperature = new SimpleStringProperty();
		private static StringProperty channel2Temperature = new SimpleStringProperty();
		private static StringProperty channel3Temperature = new SimpleStringProperty();
		private static StringProperty channel4Temperature = new SimpleStringProperty();

		public static StringProperty channel1TemperatureProperty() {
			return channel1Temperature;
		}

		public static String getChannel1Temperature() {
			return channel1Temperature.get();
		}

		public static void setChannel1Temperature(String channel1Temperature) {
			channelSCTemp.channel1Temperature.set(channel1Temperature);
		}

		public static StringProperty channel2TemperatureProperty() {
			return channel2Temperature;
		}

		public static String getChannel2Temperature() {
			return channel2Temperature.get();
		}

		public static void setChannel2Temperature(String channel2Temperature) {
			channelSCTemp.channel2Temperature.set(channel2Temperature);
		}

		public static StringProperty channel3TemperatureProperty() {
			return channel3Temperature;
		}

		public static String getChannel3Temperature() {
			return channel3Temperature.get();
		}

		public static void setChannel3Temperature(String channel3Temperature) {
			channelSCTemp.channel3Temperature.set(channel3Temperature);
		}

		public static StringProperty channel4TemperatureProperty() {
			return channel4Temperature;
		}

		public static String getChannel4Temperature() {
			return channel4Temperature.get();
		}

		public static void setChannel4Temperature(String channel4Temperature) {
			channelSCTemp.channel4Temperature.set(channel4Temperature);
		}
	}

	// MK1 AEC TEMPERATURE
	public static class channelAECTemp {

		private static StringProperty channel1Temperature = new SimpleStringProperty();
		private static StringProperty channel2Temperature = new SimpleStringProperty();
		private static StringProperty channel3Temperature = new SimpleStringProperty();
		private static StringProperty channel4Temperature = new SimpleStringProperty();

		public static StringProperty channel1TemperatureProperty() {
			return channel1Temperature;
		}

		public static String getChannel1Temperature() {
			return channel1Temperature.get();
		}

		public static void setChannel1Temperature(String channel1Temperature) {
			channelAECTemp.channel1Temperature.set(channel1Temperature);
		}

		public static StringProperty channel2TemperatureProperty() {
			return channel2Temperature;
		}

		public static String getChannel2Temperature() {
			return channel2Temperature.get();
		}

		public static void setChannel2Temperature(String channel2Temperature) {
			channelAECTemp.channel2Temperature.set(channel2Temperature);
		}

		public static StringProperty channel3TemperatureProperty() {
			return channel3Temperature;
		}

		public static String getChannel3Temperature() {
			return channel3Temperature.get();
		}

		public static void setChannel3Temperature(String channel3Temperature) {
			channelAECTemp.channel3Temperature.set(channel3Temperature);
		}

		public static StringProperty channel4TemperatureProperty() {
			return channel4Temperature;
		}

		public static String getChannel4Temperature() {
			return channel4Temperature.get();
		}

		public static void setChannel4Temperature(String channel4Temperature) {
			channelAECTemp.channel4Temperature.set(channel4Temperature);
		}
	}

	// MK1a MK2 TEMPERATURE
	public static class boardChannelTemp {

		public static ObservableMap<String, ChannelTemperature> boardTemperatureMap = FXCollections
				.observableMap(new HashMap<>());

		public static ObservableMap<String, ChannelTemperature> getBoardTemperatureMap() {
			return boardTemperatureMap;
		}

		public static void addBoardTemperatureMap(String key, ChannelTemperature value) {
			boardTemperatureMap.put(key, value);
		}

		public static void setBoardTemperatureMap(ObservableMap<String, ChannelTemperature> boardTemperatureMap) {
			boardChannelTemp.boardTemperatureMap.clear();
			boardChannelTemp.boardTemperatureMap.putAll(boardTemperatureMap);
		}
	}
	
	public static class boardChannelTempAEC {

		public static ObservableMap<String, ChannelTemperature> boardTemperatureMap = FXCollections
				.observableMap(new HashMap<>());

		public static ObservableMap<String, ChannelTemperature> getBoardTemperatureMap() {
			return boardTemperatureMap;
		}

		public static void addBoardTemperatureMap(String key, ChannelTemperature value) {
			boardTemperatureMap.put(key, value);
		}

		public static void setBoardTemperatureMap(ObservableMap<String, ChannelTemperature> boardTemperatureMap) {
			boardChannelTempAEC.boardTemperatureMap.clear();
			boardChannelTempAEC.boardTemperatureMap.putAll(boardTemperatureMap);
		}
	}
	
	
	
	
	

	// FOR RDF FILE PARSER
	public static class rdfFileParser {
		private static int dStarCount = 0;
		private static boolean dStarFound = false;
		private static boolean parseFileError = false;

		public static void setDStarCount(int count) {
			dStarCount = count;
		}

		public static int getDStarCount() {
			return dStarCount;
		}

		public static void setDStarFound(boolean found) {
			dStarFound = found;
		}

		public static boolean isDStarFound() {
			return dStarFound;
		}

		public static void incrementDStarCount() {
			dStarCount++;
		}

		public static boolean isParseFileError() {
			return parseFileError;
		}

		public static void setParseFileError(boolean parseFileError) {
			rdfFileParser.parseFileError = parseFileError;
		}

	}

	public static class aitessRunning {
		private static volatile boolean aitess1Exited = true;
		private static volatile boolean aitess2Exited = true;
		private static volatile boolean aitess1Switched = false;
		private static volatile boolean aitess1SwitchedFailed = false;
		private static volatile boolean aitess2Switched = false;
		private static volatile boolean aitess2SwitchedFailed = false;
		private static volatile boolean aitess1ReloadConfigured = false;

		public static synchronized boolean isAitess1Exited() {
			return aitess1Exited;
		}

		public static synchronized void setAitess1Exited(boolean aitess1Exited) {
			aitessRunning.aitess1Exited = aitess1Exited;
		}

		public static synchronized boolean isAitess2Exited() {
			return aitess2Exited;
		}

		public static synchronized void setAitess2Exited(boolean aitess2Exited) {
			aitessRunning.aitess2Exited = aitess2Exited;
		}

		public static synchronized boolean isAitess1Switched() {
			return aitess1Switched;
		}

		public static synchronized void setAitess1Switched(boolean aitess1Switched) {
			Debug.printDebug("aitess1 flag:: " + aitess1Switched);
			aitessRunning.aitess1Switched = aitess1Switched;
		}

		public static synchronized boolean isAitess2Switched() {
			return aitess2Switched;
		}

		public static synchronized void setAitess2Switched(boolean aitess2Switched) {
			Debug.printDebug("aitess2 flag:: " + aitess2Switched);
			aitessRunning.aitess2Switched = aitess2Switched;
		}

		public static synchronized boolean isAitess1SwitchedFailed() {
			return aitess1SwitchedFailed;
		}

		public static synchronized void setAitess1SwitchedFailed(boolean aitess1SwitchedFailed) {
			aitessRunning.aitess1SwitchedFailed = aitess1SwitchedFailed;
		}

		public static synchronized boolean isAitess2SwitchedFailed() {
			return aitess2SwitchedFailed;
		}

		public static synchronized void setAitess2SwitchedFailed(boolean aitess2SwitchedFailed) {
			aitessRunning.aitess2SwitchedFailed = aitess2SwitchedFailed;
		}

		public static synchronized boolean isAitess1ReloadConfigured() {
			return aitess1ReloadConfigured;
		}

		public static synchronized void setAitess1ReloadConfigured(boolean aitess1ReloadConfigured) {
			aitessRunning.aitess1ReloadConfigured = aitess1ReloadConfigured;
		}

	}

	private static String previousRunConfigId;

	public static String getPreviousRunConfigId() {
		return previousRunConfigId;
	}

	public static void setPreviousRunConfigId(String previousRunConfigId) {
		StateMachine.previousRunConfigId = previousRunConfigId;
	}

	// FOR SESSION FOLDERS
	// homelocation
	private static Path homelocation;

	public static Path getHomelocation() {
		return homelocation;
	}

	public static void setHomelocation(Path homelocation) {
		StateMachine.homelocation = homelocation;
	}

	// output
	private static Path outputLocation;

	public static Path getOutputLocation() {
		return outputLocation;
	}

	public static void setOutputLocation(Path outputLocation) {
		StateMachine.outputLocation = outputLocation;
	}

	// scriptFileLocation
	private static String scriptFileLocation;

	public static String getScriptFileLocation() {
		return scriptFileLocation;
	}

	public static void setScriptFileLocation(String scriptFileLocation) {
		StateMachine.scriptFileLocation = scriptFileLocation;
	}

	// current user
	private static String currentUserLogin;

	public static String getCurrentUserLogin() {
		return currentUserLogin;
	}

	public static void setCurrentUserLogin(String currentUserLogin) {
		StateMachine.currentUserLogin = currentUserLogin;
	}

	// command
	private static boolean runCommand = false;
	private static volatile boolean aitess1CommandFinished = false;

	public static synchronized boolean isAitess1CommandFinished() {
		return aitess1CommandFinished;
	}

	public static synchronized void setAitess1CommandFinished(boolean aitess1CommandFinished) {
		StateMachine.aitess1CommandFinished = aitess1CommandFinished;
	}

	public static boolean isRunCommand() {
		return runCommand;
	}

	public static void setRunCommand(boolean runCommand) {
		StateMachine.runCommand = runCommand;
	}

	public static void resetStateMachine() {
		// Reset TestState and RunningTestName
		testState = TestState.PENDING;
		runningTestName = RunningTestName.OTHER;

		// Reset currentSessionDetails
		currentSessionDetails.setUserId(null);
		currentSessionDetails.setUutId(null);
		currentSessionDetails.setUutType(null);
		currentSessionDetails.setDfccSerialNumber(null);
		currentSessionDetails.setSessionId(null);
		currentSessionDetails.setSessionName(null);
		currentSessionDetails.setSessionTypeID(null);
		currentSessionDetails.setSessionTypeName(null);
		currentSessionDetails.setRunConfigId(null);
		currentSessionDetails.setLoginSessionId(0);

		// Reset currentTestDetails
		currentTestDetails.setRunningTestPageId(null);
		currentTestDetails.setTestType(null);
		currentTestDetails.setTestFileCount(0);
		currentTestDetails.setRepeatCount(0);
		currentTestDetails.setCompletionPercentage(0.0);

		// Reset stageDatalist
		stageDatalist = null;

		// Reset user action and flags
		userAction = null;
		userExitError = null;
		userActionFlag.set(false);

		// Reset AITESS launch flags
		aitess1Launched = false;
		aitess2Launched = false;

		// Reset text area flag
		textArea = true;

		// Reset dfccCheckStatus
		dfccCheckStatus.setDfccPowerStatus(new SimpleBooleanProperty(false));
		dfccCheckStatus.setDfccPowerOnCommand(null);
		dfccCheckStatus.setDfccPowerOffCommand(null);
		dfccCheckStatus.setOnlineStatusCommand(null);
		dfccCheckStatus.setMk1ScTemperatureCommand(null);
		dfccCheckStatus.setMk1AecTemperatureCommand(null);
		dfccCheckStatus.setScTemperatureCommand(null);
		dfccCheckStatus.setAecTemperatureCommand(null);
		dfccCheckStatus.setOfpVersionStatusCommand(null);
		dfccCheckStatus.setWdmStatusCommand(null);

		// Reset OnlineStatus
		OnlineStatus.channel1Status.set("");
		OnlineStatus.channel2Status.set("");
		OnlineStatus.channel3Status.set("");
		OnlineStatus.channel4Status.set("");

		// Reset WDMStatus
		WDMStatus.channel1Status = "";
		WDMStatus.channel2Status = "";
		WDMStatus.channel3Status = "";
		WDMStatus.channel4Status = "";

		// Reset OFPversionStatus
		OFPversionStatus.channel1Status = "";
		OFPversionStatus.channel2Status = "";
		OFPversionStatus.channel3Status = "";
		OFPversionStatus.channel4Status = "";

		// Reset Channel SC and AEC Temperatures
		channelSCTemp.channel1Temperature.set("");
		channelSCTemp.channel2Temperature.set("");
		channelSCTemp.channel3Temperature.set("");
		channelSCTemp.channel4Temperature.set("");

		channelAECTemp.channel1Temperature.set("");
		channelAECTemp.channel2Temperature.set("");
		channelAECTemp.channel3Temperature.set("");
		channelAECTemp.channel4Temperature.set("");

		// Reset Board Channel Temperature Map
		boardChannelTemp.boardTemperatureMap.clear();

		// Reset RDF File Parser State
		rdfFileParser.setDStarCount(0);
		rdfFileParser.setDStarFound(false);
		rdfFileParser.setParseFileError(false);

		// Reset AITESS Running States
		aitessRunning.setAitess1Exited(true);
		aitessRunning.setAitess2Exited(true);
		aitessRunning.setAitess1Switched(false);
		aitessRunning.setAitess1SwitchedFailed(false);
		aitessRunning.setAitess2Switched(false);
		aitessRunning.setAitess2SwitchedFailed(false);
		aitessRunning.setAitess1ReloadConfigured(false);

		// Reset State Machine Data
		previousRunConfigId = null;
		homelocation = null;
		outputLocation = null;
		scriptFileLocation = null;
		currentUserLogin = null;
		runCommand = false;
		aitess1CommandFinished = false;
	}

}
