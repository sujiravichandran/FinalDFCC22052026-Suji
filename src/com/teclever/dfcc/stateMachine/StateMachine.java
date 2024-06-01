package com.teclever.dfcc.stateMachine;

import java.util.List;

import com.teclever.dfcc.datastore.dto.StageObject;

public class StateMachine {
	enum TestState {
		PENDING, RUNNING, PAUSED, STOPPED, COMPLETED
	}

	enum RunningTestName {
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


	public static class currentSessionDetails{
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
	
	public static class currentTestDetails{
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
}
