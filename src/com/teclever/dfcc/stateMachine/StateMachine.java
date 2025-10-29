package com.teclever.dfcc.stateMachine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.utils.Debug;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
	
//	For displaying in Running Test Name in Status Bar
	public enum StatusBarTestName{
		OTHER, SELF_TEST, SESSION_TEST, ADVANCED_TEST_HWATP_TEST, ADVANCED_TEST_INTERFACE_TEST, ADVANCED_TEST_CUSTOM2_TEST,
		ADVANCED_TEST_CUSTOM1_TEST, LRU_SRU_TEST_MANDATORY_TEST, LRU_SRU_TEST_SRU_TEST, LRU_SRU_TEST_GO_NOGO_TEST
	}
	
	private static final StringProperty statusBarRunningTestName = new SimpleStringProperty();

	public static String getStatusBarRunningTestName() {
	    return statusBarRunningTestName.get();
	}

	public static void setStatusBarRunningTestName(StatusBarTestName testName) {
	    statusBarRunningTestName.set(testName.toString());
	}

	public static StringProperty statusBarRunningTestNameProperty() {
	    return statusBarRunningTestName;
	}



	
	private static final ObjectProperty<TestState> testState = new SimpleObjectProperty<>(TestState.PENDING);
	private static final StringProperty runningTestName = new SimpleStringProperty();

	public static String getRunningTestName() {
		return runningTestName.get();
	}

	public static void setRunningTestName(RunningTestName testName) {
		runningTestName.set(testName.toString());
	}

	public static StringProperty runningTestNameProperty() {
		return runningTestName;
	}

	public static ObjectProperty<TestState> testStateProperty() {
		return testState;
	}

	public static void setTestState(TestState newState) {
//		System.out.println("setTestState :: " + newState);
		testState.set(newState);
	}

	public static TestState getTestState() {
		return testState.get();
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
		private static Date loginSessionDate;

		public static Date getLoginSessionDate() {
			return loginSessionDate;
		}

		public static void setLoginSessionDate(Date loginSessionDate) {
			currentSessionDetails.loginSessionDate = loginSessionDate;
		}

		private static String dfccPartNo;

		public static String getDfccPartNo() {
			return dfccPartNo;
		}

		public static void setDfccPartNo(String dfccPartNo) {
			currentSessionDetails.dfccPartNo = dfccPartNo;
		}

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
	
	private static final BooleanProperty aitess1Launched = new SimpleBooleanProperty(false);
	
	public static BooleanProperty aitess1LaunchedProperty() {
	    return aitess1Launched;
	}

	public static boolean isAitess1Launched() {
	    return aitess1Launched.get();
	}

	public static void setAitess1Launched(boolean value) {
	    aitess1Launched.set(value);
	}

	private static final BooleanProperty aitess2Launched = new SimpleBooleanProperty(false);
	public static BooleanProperty aitess2LaunchedProperty() {
	    return aitess2Launched;
	}

	public static boolean isAitess2Launched() {
	    return aitess2Launched.get();
	}

	public static void setAitess2Launched(boolean value) {
	    aitess2Launched.set(value);
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
		
		private static BooleanProperty dfccOnlineStatus = new SimpleBooleanProperty(false);
		
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
		
//	Suji Added for Getting updated the toggle automaticaly after all channels gets online::(06-08-2025)
		
		public static BooleanProperty dfccOnlineStatusProperty() {
		    return dfccOnlineStatus;
		}

		public static BooleanProperty getDfccOnlineStatus() {
		    return dfccOnlineStatus;
		}

		public static void setDfccOnlineStatus(BooleanProperty dfccOnlineStatus) {
		    dfccCheckStatus.dfccOnlineStatus = dfccOnlineStatus;
		}
		
//Exit::(06-08-2025)
		
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
	public static class powerOnStatus {

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

		private static String channel1Status = "offline";
		private static String channel2Status = "offline";
		private static String channel3Status = "offline";
		private static String channel4Status = "offline";

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
			Platform.runLater(() -> channelSCTemp.channel1Temperature.set(channel1Temperature));
		}

		public static StringProperty channel2TemperatureProperty() {
			return channel2Temperature;
		}

		public static String getChannel2Temperature() {
			return channel2Temperature.get();
		}

		public static void setChannel2Temperature(String channel2Temperature) {
			Platform.runLater(() -> channelSCTemp.channel2Temperature.set(channel2Temperature));
		}

		public static StringProperty channel3TemperatureProperty() {
			return channel3Temperature;
		}

		public static String getChannel3Temperature() {
			return channel3Temperature.get();
		}

		public static void setChannel3Temperature(String channel3Temperature) {
			Platform.runLater(() -> channelSCTemp.channel3Temperature.set(channel3Temperature));
		}

		public static StringProperty channel4TemperatureProperty() {
			return channel4Temperature;
		}

		public static String getChannel4Temperature() {
			return channel4Temperature.get();
		}

		public static void setChannel4Temperature(String channel4Temperature) {
			Platform.runLater(() -> channelSCTemp.channel4Temperature.set(channel4Temperature));
		}

		// COLOR
		private static StringProperty channel1BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel2BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel3BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel4BackgroundColor = new SimpleStringProperty();

		public static StringProperty channel1BackgroundColorProperty() {
			return channel1BackgroundColor;
		}

		public static void setChannel1BackgroundColor(String color) {
			channelSCTemp.channel1BackgroundColor.set(color);
		}

		public static StringProperty channel2BackgroundColorProperty() {
			return channel2BackgroundColor;
		}

		public static void setChannel2BackgroundColor(String color) {
			channelSCTemp.channel2BackgroundColor.set(color);
		}

		public static StringProperty channel3BackgroundColorProperty() {
			return channel3BackgroundColor;
		}

		public static void setChannel3BackgroundColor(String color) {
			channelSCTemp.channel3BackgroundColor.set(color);
		}

		public static StringProperty channel4BackgroundColorProperty() {
			return channel4BackgroundColor;
		}

		public static void setChannel4BackgroundColor(String color) {
			channelSCTemp.channel4BackgroundColor.set(color);
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
//			channelAECTemp.channel1Temperature.set(channel1Temperature);
			Platform.runLater(() ->channelAECTemp.channel1Temperature.set(channel1Temperature));
//			System.out.println("SUJI IN STATEMACHINE AEC 1:: "+channel1Temperature);
		}

		public static StringProperty channel2TemperatureProperty() {
			return channel2Temperature;
		}

		public static String getChannel2Temperature() {
			return channel2Temperature.get();
		}

		public static void setChannel2Temperature(String channel2Temperature) {
//			channelAECTemp.channel2Temperature.set(channel2Temperature);
			Platform.runLater(() ->channelAECTemp.channel2Temperature.set(channel2Temperature));
		}

		public static StringProperty channel3TemperatureProperty() {
			return channel3Temperature;
		}

		public static String getChannel3Temperature() {
			return channel3Temperature.get();
		}

		public static void setChannel3Temperature(String channel3Temperature) {
//			channelAECTemp.channel3Temperature.set(channel3Temperature);
			Platform.runLater(() ->channelAECTemp.channel3Temperature.set(channel3Temperature));
		}

		public static StringProperty channel4TemperatureProperty() {
			return channel4Temperature;
		}

		public static String getChannel4Temperature() {
			return channel4Temperature.get();
		}

		public static void setChannel4Temperature(String channel4Temperature) {
//			channelAECTemp.channel4Temperature.set(channel4Temperature);
			Platform.runLater(() ->channelAECTemp.channel4Temperature.set(channel4Temperature));
		}

		// COLOR
		private static StringProperty channel1BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel2BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel3BackgroundColor = new SimpleStringProperty();
		private static StringProperty channel4BackgroundColor = new SimpleStringProperty();

		public static StringProperty channel1BackgroundColorProperty() {
			return channel1BackgroundColor;
		}

		public static void setChannel1BackgroundColor(String color) {
//			channelAECTemp.channel1BackgroundColor.set(color);
			Platform.runLater(() ->channelAECTemp.channel1BackgroundColor.set(color));
		}

		public static StringProperty channel2BackgroundColorProperty() {
			return channel2BackgroundColor;
		}

		public static void setChannel2BackgroundColor(String color) {
//			channelAECTemp.channel2BackgroundColor.set(color);
			Platform.runLater(() ->channelAECTemp.channel2BackgroundColor.set(color));
		}

		public static StringProperty channel3BackgroundColorProperty() {
			return channel3BackgroundColor;
		}

		public static void setChannel3BackgroundColor(String color) {
			Platform.runLater(() ->channelAECTemp.channel3BackgroundColor.set(color));
//			channelAECTemp.channel3BackgroundColor.set(color);
		}

		public static StringProperty channel4BackgroundColorProperty() {
			return channel4BackgroundColor;
		}

		public static void setChannel4BackgroundColor(String color) {
			Platform.runLater(() ->channelAECTemp.channel4BackgroundColor.set(color));
//			channelAECTemp.channel4BackgroundColor.set(color);
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

		public static double minValue;
		public static double maxValue;

		public static double getMinValue() {
			return minValue;
		}

		public static double setMinValue(double minValue) {
			return boardChannelTemp.minValue = minValue;
		}

		public static double getMaxValue() {
			return maxValue;
		}

		public static double setMaxValue(double maxValue) {
			return boardChannelTemp.maxValue = maxValue;
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

		public static double minValue;
		public static double maxValue;

		public static double getMinValue() {
			return minValue;
		}

		public static void setMinValue(double minValue) {
			boardChannelTempAEC.minValue = minValue;
		}

		public static double getMaxValue() {
			return maxValue;
		}

		public static void setMaxValue(double maxValue) {
			boardChannelTempAEC.maxValue = maxValue;
		}

	}

	// COLOR
	private static StringProperty lessBackgroundColor = new SimpleStringProperty("#ADD8E6");
	private static StringProperty greaterBackgroundColor = new SimpleStringProperty("#FF0000");
	private static StringProperty normalBackgroundColor = new SimpleStringProperty("#32CD32");

	public static StringProperty LessBackgroundColor() {
		return lessBackgroundColor;
	}

	public static void setLessBackgroundColor(String lessBackgroundColor) {
//		StateMachine.lessBackgroundColor.set(lessBackgroundColor);
		Platform.runLater(() ->StateMachine.lessBackgroundColor.set(lessBackgroundColor));
//		System.out.println("SUJI CHECK FOR MK1A COLOR LESSS ::" +StateMachine.lessBackgroundColor.toString() );
	}

	public static StringProperty GreaterBackgroundColor() {
		return greaterBackgroundColor;
	}

	public static void setGreaterBackgroundColor(String greaterBackgroundColor) {
//		StateMachine.greaterBackgroundColor.set(greaterBackgroundColor);
		Platform.runLater(() ->StateMachine.greaterBackgroundColor.set(greaterBackgroundColor));
//		System.out.println("SUJI CHECK FOR MK1A COLOR GREATER ::" +StateMachine.greaterBackgroundColor.toString() );

	}

	public static StringProperty NormalBackgroundColor() {
		return normalBackgroundColor;
	}

	public static void setNormalBackgroundColor(String normalBackgroundColor) {
//		StateMachine.normalBackgroundColor.set(normalBackgroundColor);
		Platform.runLater(() ->StateMachine.normalBackgroundColor.set(normalBackgroundColor));
//		System.out.println("SUJI CHECK FOR MK1A COLOR NORMAL ::" +StateMachine.normalBackgroundColor.toString() );
		
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
		testState.set(TestState.PENDING);
		setRunningTestName(RunningTestName.OTHER);

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
		aitess1Launched.set(false);
		aitess2Launched.set(false);

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

	private static boolean allowToggle = true;

	public static boolean isAllowToggle() {
		return allowToggle;
	}

	public static void setAllowToggle(boolean allowToggle) {
		StateMachine.allowToggle = allowToggle;
	}

//	For Custom Two Test

	private static boolean customTwoTest = false;

	public static boolean isCustomTwoTest() {
		return customTwoTest;
	}

	public static void setCustomTwoTest(boolean customTwoTest) {
		StateMachine.customTwoTest = customTwoTest;
	}

//	For Test Completed Confirmation

//	private static boolean confirmTestFileCompleted = false;
//
//	public static boolean isConfirmTestFileCompleted() {
//		return confirmTestFileCompleted;
//	}
//
//	public static void setConfirmTestFileCompleted(boolean confirmTestFileCompleted) {
//		StateMachine.confirmTestFileCompleted = confirmTestFileCompleted;
//	}
//	for resetting the progressbar once the file has been move and cleared the result view table::
	private static BooleanProperty resettingProgressBar = new SimpleBooleanProperty(false);
	
	public static BooleanProperty resettingProgressBarProperty() {
	    return resettingProgressBar;
	}

	public static void setResettingProgressBar(boolean value) {
	    resettingProgressBar.set(value);
	}

	public static boolean isResettingProgressBar() {
	    return resettingProgressBar.get();
	}

	
	private static BooleanProperty confirmTestFileCompleted = new SimpleBooleanProperty(false);

	
	public static BooleanProperty confirmTestFileCompletedProperty() {
	    return confirmTestFileCompleted;
	}


	public static void setConfirmTestFileCompleted(boolean value) {
	    confirmTestFileCompleted.set(value);
	}


	public static boolean isConfirmTestFileCompleted() {
	    return confirmTestFileCompleted.get();
	}

	
	

//	For Conform Test Stop
	private static boolean confirmTestStop = false;

	public static boolean isConfirmTestStop() {
		return confirmTestStop;
	}

	public static void setConfirmTestStop(boolean confirmTestStop) {
		StateMachine.confirmTestStop = confirmTestStop;
	}

//	For Macro Button
	private static String macroSet;

	public static String getMacroSet() {
		return macroSet;
	}

	public static void setMacroSet(String macroSet) {
		StateMachine.macroSet = macroSet;
	}

//	Pbit Test
	private static String inputPathTestFile;

	public static String getInputPathTestFile() {
		return inputPathTestFile;
	}

	public static void setInputPathTestFile(String inputPathTestFile) {
		StateMachine.inputPathTestFile = inputPathTestFile;
	}

//	No Such FIle Error
	private static boolean noSuchFile = false;

	public static boolean isNoSuchFile() {
		return noSuchFile;
	}

	public static void setNoSuchFile(boolean noSuchFile) {
		StateMachine.noSuchFile = noSuchFile;
	}

//	Mandatory & GoNoGo Stop
	private static boolean mandatoryGonoGo = false;

	public static boolean isMandatoryGonoGo() {
		return mandatoryGonoGo;
	}

	public static void setMandatoryGonoGo(boolean mandatoryGonoGo) {
		StateMachine.mandatoryGonoGo = mandatoryGonoGo;
	}

	// SelfTest

	private static boolean selfTestOn = false;

	public static boolean isSelfTestOn() {
		return selfTestOn;
	}

	public static void setSelfTestOn(boolean selfTestOn) {
		StateMachine.selfTestOn = selfTestOn;
	}

	private static boolean selfTestOn2 = false;

	
	public static boolean isSelfTestOn2() {
		return selfTestOn2;
	}

	public static void setSelfTestOn2(boolean selfTestOn2) {
		StateMachine.selfTestOn2 = selfTestOn2;
	}

	private static boolean selfTestOnL1 = false;
	
	


	public static boolean isSelfTestOnL1() {
		return selfTestOnL1;
	}

	public static void setSelfTestOnL1(boolean selfTestOnL1) {
		StateMachine.selfTestOnL1 = selfTestOnL1;
	}


	private static boolean selfTestOnL2 = false;
	
	

	public static boolean isSelfTestOnL2() {
		return selfTestOnL2;
	}

	public static void setSelfTestOnL2(boolean selfTestOnL2) {
		StateMachine.selfTestOnL2 = selfTestOnL2;
	}




	// private static final BooleanProperty selfTestOn = new
	// SimpleBooleanProperty(false);
//
//	    public static boolean isSelfTestOn() {
//
//	        return selfTestOn.get();
//
//	    }
//
//	    public static void setSelfTestOn(boolean value) {
//
//	        selfTestOn.set(value);
//
//	    }
//
//	    public static BooleanProperty selfTestOnProperty() {
//
//	        return selfTestOn;
//
//	    }
//	Macro Passing:::
	private static boolean macroPassing = false;

	public static boolean isMacroPassing() {
		return macroPassing;
	}

	public static void setMacroPassing(boolean macroPassing) {
		StateMachine.macroPassing = macroPassing;
	}

//	    for SRU Test File Count
	private static boolean sruTestFileCount = false;

	public static boolean isSruTestFileCount() {
		return sruTestFileCount;
	}

	public static void setSruTestFileCount(boolean sruTestFileCount) {
		StateMachine.sruTestFileCount = sruTestFileCount;
	}

//		FOr SRU Option Selection
	private static boolean pbitOption1 = false;

	public static boolean isPbitOption1() {
		return pbitOption1;
	}

	public static void setPbitOption1(boolean pbitOption1) {
		StateMachine.pbitOption1 = pbitOption1;
	}

	private static boolean pbitOption2 = false;

	public static boolean isPbitOption2() {
		return pbitOption2;
	}

	public static void setPbitOption2(boolean pbitOption2) {
		StateMachine.pbitOption2 = pbitOption2;
	}

//	FOr Session Testing Clearing Stage table data and moving files

	private static boolean sessionTestOk = false;

	public static boolean isSessionTestOk() {
		return sessionTestOk;
	}

	public static void setSessionTestOk(boolean sessionTestOk) {
		StateMachine.sessionTestOk = sessionTestOk;
	}

//	FOr SRU Testing Clearing Stage table data and moving files

	private static boolean sruTestOk = false;

	public static boolean isSruTestOk() {
		return sruTestOk;
	}

	public static void setSruTestOk(boolean sruTestOk) {
		StateMachine.sruTestOk = sruTestOk;
	}

//	For Advanced HWATP test clear:::

	private static boolean advancedTestOk = false;

	public static boolean isAdvancedTestOk() {
		return advancedTestOk;
	}

	public static void setAdvancedTestOk(boolean advancedTestOk) {
		StateMachine.advancedTestOk = advancedTestOk;
	}

//	For Advanced Interface test clear:::

	private static boolean advancedTestInterfaceOk = false;

	public static boolean isAdvancedTestInterfaceOk() {
		return advancedTestInterfaceOk;
	}

	public static void setAdvancedTestInterfaceOk(boolean advancedTestInterfaceOk) {
		StateMachine.advancedTestInterfaceOk = advancedTestInterfaceOk;
	}

	// Stage for table Data Clearing & Moving Files:
	private static String stageName;

	public static String getStageName() {
		return stageName;
	}

	public static void setStageName(String stageName) {
		StateMachine.stageName = stageName;
	}

//	For Macro Button Execution:::
	private static final BooleanProperty macroCommand = new SimpleBooleanProperty(false);

	public static boolean isMacroCommand() {
		return macroCommand.get();
	}

	public static void setMacroCommand(boolean value) {
		macroCommand.set(value);
	}

	public static BooleanProperty macroCommandProperty() {
		return macroCommand;
	}

//	For WDM Status::
	private static final BooleanProperty wdmStatusOfflineCheck = new SimpleBooleanProperty(false);

	public static BooleanProperty wdmStatusOfflineCheckProperty() {
		return wdmStatusOfflineCheck;
	}

	public static boolean isWdmStatusOfflineCheck() {
		return wdmStatusOfflineCheck.get();
	}

	public static void setWdmStatusOfflineCheck(boolean value) {
		wdmStatusOfflineCheck.set(value);
	}
	
//	For WDM Ok Continue::
	private static final BooleanProperty proceedTest = new SimpleBooleanProperty(false);

	public static BooleanProperty proceedTestProperty() {
		return proceedTest;
	}

	public static boolean isProceedTest() {
		return proceedTest.get();
	}

	public static void setProceedTest(boolean value) {
		proceedTest.set(value);
	}

	

	
//For Rdf copy falg::
	
	private static boolean rdfCopy = false;

	public static boolean isRdfCopy() {
		return rdfCopy;
	}

	public static void setRdfCopy(boolean rdfCopy) {
		StateMachine.rdfCopy = rdfCopy;
	}
	
//	For Disable Enable Flag:::
	
	private static final BooleanProperty dissableEnable = new SimpleBooleanProperty(false);

	public static BooleanProperty dissableEnableProperty() {
	    return dissableEnable;
	}

	public static boolean isDissableEnable() {
	    return dissableEnable.get();
	}

	public static void setDissableEnable(boolean value) {
	    dissableEnable.set(value);
	}

	
	
//	For Colour Updated Flag:::
	
	private static final BooleanProperty updateColor = new SimpleBooleanProperty(false);

	public static BooleanProperty updateColorProperty() {
	    return updateColor;
	}

	public static boolean isUpdateColor() {
	    return updateColor.get();
	}

	public static void setUpdateColor(boolean value) {
	    updateColor.set(value);
	}
	
//	for Rdf Move Popup Close button while Logout::
	
	private static final BooleanProperty rdfMoveLogout = new SimpleBooleanProperty(false);

	public static BooleanProperty rdfMoveLogoutProperty() {
	    return rdfMoveLogout;
	}

	public static boolean isRdfMoveLogout() {
	    return rdfMoveLogout.get();
	}

	public static void setRdfMoveLogout(boolean value) {
	    rdfMoveLogout.set(value);
	}
	
//	for Terminal pressed "Y":
	private static final BooleanProperty yesFromTerminal = new SimpleBooleanProperty(false);

	public static BooleanProperty yesFromTerminalProperty() {
	    return yesFromTerminal;
	}

	public static boolean isYesFromTerminal() {
	    return yesFromTerminal.get();
	}

	public static void setYesFromTerminal(boolean value) {
	    yesFromTerminal.set(value);
	}
	
	private static boolean responceYesTerminal = false;

	public static boolean isResponceYesTerminal() {
		return responceYesTerminal;
	}

	public static void setResponceYesTerminal(boolean responceYesTerminal) {
		StateMachine.responceYesTerminal = responceYesTerminal;
	}
//

//	For SRU Stop::
	
	private static boolean sruTestStop = false;

	public static boolean isSruTestStop() {
		return sruTestStop;
	}

	public static void setSruTestStop(boolean sruTestStop) {
		StateMachine.sruTestStop = sruTestStop;
	}
	
	
	//ANUJ --- NEW CHANGES
	//Previous Selected Stage
	private static String previouslySelectedStageId;
	private static String currentlySelectedStageId;

	public static String getPreviouslySelectedStageId() {
		return previouslySelectedStageId;
	}

	public static void setPreviouslySelectedStageId(String previouslySelectedStageId) {
		StateMachine.previouslySelectedStageId = previouslySelectedStageId;
	}

	public static String getCurrentlySelectedStageId() {
		return currentlySelectedStageId;
	}

	public static void setCurrentlySelectedStageId(String currentlySelectedStageId) {
		StateMachine.currentlySelectedStageId = currentlySelectedStageId;
	}

//	Changed by Vignesh 31-07-25 - showing last temp checked time
	private static StringProperty tempLastCheckedTime = new SimpleStringProperty();
	
	
	public static StringProperty tempLastCheckedTimeProperty() {
		return tempLastCheckedTime;
	}

	public static void setTempLastCheckedTime(String tempLastCheckedTime) {
		StateMachine.tempLastCheckedTime.set(tempLastCheckedTime);;
	}
	
	public static String getTempLastCheckedTime() {
		return tempLastCheckedTime.get();
	}

//	Added by SUji 19-08-2025 for wdmstatususeraction after cancel::
	private static final BooleanProperty cancelTest = new SimpleBooleanProperty(false);

	public static BooleanProperty cancelTestProperty() {
	    return cancelTest;
	}

	public static boolean isCancelTest() {
	    return cancelTest.get();
	}

	public static void setCancelTest(boolean value) {
	    cancelTest.set(value);
	}
//	SUJI ADDED:::
	private static final BooleanProperty yesEntred = new SimpleBooleanProperty(false);
	
	public static BooleanProperty yesEntredProperty() {
	    return yesEntred;
	}
	
	public static boolean isyesEntred() {
	    return yesEntred.get();
	}
	
	public static void setYesEntred(boolean value) {
		yesEntred.set(value);
	}
	

//SUJI ADDED FOR GETTING SRU STAGE
	private static List<String> selectedStageIds = new ArrayList<>();

	public static void setSelectedStageIds(List<String> stageIds) {
	    selectedStageIds = stageIds;
	}

	public static List<String> getSelectedStageIds() {
	    return selectedStageIds;
	}
	

//	Suji For New OFP CHeck::
	
	private static String ofpValueCheck;

	public static String getOfpValueCheck() {
		return ofpValueCheck;
	}

	public static void setOfpValueCheck(String ofpValueCheck) {
		StateMachine.ofpValueCheck = ofpValueCheck;
	}
	
//	Suji Added Flag for OFP check List::
	
		private static List<String> ofpList;

		public static List<String> getOfpList() {
			return ofpList;
		}

		public static void setOfpList(List<String> ofpList) {
			StateMachine.ofpList = ofpList;
		}
		
//Suji Added for session selected stages:
		private static String sessionPreviouslySelectedStageId;
		private static String SessioncurrentlySelectedStageId;

		public static String getSessionPreviouslySelectedStageId() {
			return sessionPreviouslySelectedStageId;
		}

		public static void setSessionPreviouslySelectedStageId(String sessionPreviouslySelectedStageId) {
			StateMachine.sessionPreviouslySelectedStageId = sessionPreviouslySelectedStageId;
		}

		public static String getSessioncurrentlySelectedStageId() {
			return SessioncurrentlySelectedStageId;
		}

		public static void setSessioncurrentlySelectedStageId(String sessioncurrentlySelectedStageId) {
			SessioncurrentlySelectedStageId = sessioncurrentlySelectedStageId;
		}

//Suji added to store Test tyep Id:
		private static String testTypeId;

		public static String getTestTypeId() {
			return testTypeId;
		}

		public static void setTestTypeId(String testTypeId) {
			StateMachine.testTypeId = testTypeId;
		}
		
		private boolean disableFalgRunnTest = false;

		public boolean isDisableFalgRunnTest() {
			return disableFalgRunnTest;
		}

		public void setDisableFalgRunnTest(boolean disableFalgRunnTest) {
			this.disableFalgRunnTest = disableFalgRunnTest;
		}
		
	

}
