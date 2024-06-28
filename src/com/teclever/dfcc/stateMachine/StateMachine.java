package com.teclever.dfcc.stateMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.StageObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
			currentSessionDetails.runConfigId = runConfigId;
		}

		public static int getLoginSessionId() {
			return loginSessionId;
		}

		public static void setLoginSessionId(int loginSessionId) {
			currentSessionDetails.loginSessionId = loginSessionId;
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
	
	
	
	
	
	//AITESS 2 
	public static class dfccCheckStatus{
		
		private static boolean dfccPowerStatus;			//for checking status ON or OFF
		private static String dfccPowerOnCommand;
		private static String dfccPowerOffCommand;
		private static String onlineStatusCommand;
		private static String mk1ScTemperatureCommand;
		private static String mk1AecTemperatureCommand;
		
		private static String mk1aTemperatureCommand;
		private static String mk2TemperatureCommand;
		
		
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
		public static Boolean getDfccPowerStatus() {
			return dfccPowerStatus;
		}
		public static void setDfccPowerStatus(Boolean dfccPowerStatus) {
			dfccCheckStatus.dfccPowerStatus = dfccPowerStatus;
		}
		public static String getMk1aTemperatureCommand() {
			return mk1aTemperatureCommand;
		}
		public static void setMk1aTemperatureCommand(String mk1aTemperatureCommand) {
			dfccCheckStatus.mk1aTemperatureCommand = mk1aTemperatureCommand;
		}
		public static String getMk2TemperatureCommand() {
			return mk2TemperatureCommand;
		}
		public static void setMk2TemperatureCommand(String mk2TemperatureCommand) {
			dfccCheckStatus.mk2TemperatureCommand = mk2TemperatureCommand;
		}
				
		
	}
	
	
	//STORING DATA FOR DFCC STATUS CHECK AITESS 2
	
	//ONLINE STATUS
	public static class OnlineStatus{
		
		private static String channel1Status;
		private static String channel2Status;
		private static String channel3Status;
		private static String channel4Status;
		
		public static String getChannel1Status() {
			return channel1Status;
		}
		public static void setChannel1Status(String channel1Status) {
			OnlineStatus.channel1Status = channel1Status;
		}
		public static String getChannel2Status() {
			return channel2Status;
		}
		public static void setChannel2Status(String channel2Status) {
			OnlineStatus.channel2Status = channel2Status;
		}
		public static String getChannel3Status() {
			return channel3Status;
		}
		public static void setChannel3Status(String channel3Status) {
			OnlineStatus.channel3Status = channel3Status;
		}
		public static String getChannel4Status() {
			return channel4Status;
		}
		public static void setChannel4Status(String channel4Status) {
			OnlineStatus.channel4Status = channel4Status;
		}	

	}
	
	
	//MK1 SC AEC TEMPERATURE
	public static class channelTemp
	{
		private static String channel1Temperature;
		private static String channel2Temperature;
		private static String channel3Temperature;
		private static String channel4Temperature;
		
		
		public static String getChannel1Temperature() {
			return channel1Temperature;
		}
		public static void setChannel1Temperature(String channel1Temperature) {
			channelTemp.channel1Temperature = channel1Temperature;
		}
		public static String getChannel2Temperature() {
			return channel2Temperature;
		}
		public static void setChannel2Temperature(String channel2Temperature) {
			channelTemp.channel2Temperature = channel2Temperature;
		}
		public static String getChannel3Temperature() {
			return channel3Temperature;
		}
		public static void setChannel3Temperature(String channel3Temperature) {
			channelTemp.channel3Temperature = channel3Temperature;
		}
		public static String getChannel4Temperature() {
			return channel4Temperature;
		}
		public static void setChannel4Temperature(String channel4Temperature) {
			channelTemp.channel4Temperature = channel4Temperature;
		}	
	}
	
	
	//MK1a MK2 TEMPERATURE
	public static class boardChannelTemp{
		
	    public static Map<String, List<ChannelTemperature>> boardTemperatureMap;

		public static Map<String, List<ChannelTemperature>> getBoardTemperatureMap() {
			return boardTemperatureMap;
		}

		public static void setBoardTemperatureMap(Map<String, List<ChannelTemperature>> boardTemperatureMap) {
			boardChannelTemp.boardTemperatureMap = boardTemperatureMap;
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

		
		
		public static class aitessRunning{
			private static boolean aitess1Exited = true;
			private static boolean aitess2Exited = true;
			private static boolean aitess1Switched = false;
			private static boolean aitess1SwitchedFailed = false;
			private static boolean aitess2Switched = false;
			private static boolean aitess2SwitchedFailed = false;

			
			public static boolean isAitess1Exited() {
				return aitess1Exited;
			}
			public static void setAitess1Exited(boolean aitess1Exited) {
				aitessRunning.aitess1Exited = aitess1Exited;
			}
			public static boolean isAitess2Exited() {
				return aitess2Exited;
			}
			public static void setAitess2Exited(boolean aitess2Exited) {
				aitessRunning.aitess2Exited = aitess2Exited;
			}
			public static boolean isAitess1Switched() {
				return aitess1Switched;
			}
			public static void setAitess1Switched(boolean aitess1Switched) {
				aitessRunning.aitess1Switched = aitess1Switched;
			}
			public static boolean isAitess2Switched() {
				return aitess2Switched;
			}
			public static void setAitess2Switched(boolean aitess2Switched) {
				aitessRunning.aitess2Switched = aitess2Switched;
			}
			public static boolean isAitess1SwitchedFailed() {
				return aitess1SwitchedFailed;
			}
			public static void setAitess1SwitchedFailed(boolean aitess1SwitchedFailed) {
				aitessRunning.aitess1SwitchedFailed = aitess1SwitchedFailed;
			}
			public static boolean isAitess2SwitchedFailed() {
				return aitess2SwitchedFailed;
			}
			public static void setAitess2SwitchedFailed(boolean aitess2SwitchedFailed) {
				aitessRunning.aitess2SwitchedFailed = aitess2SwitchedFailed;
			}
			
			
				
		}
		
		
		
		
		
	}

}
