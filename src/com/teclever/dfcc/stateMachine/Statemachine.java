package com.teclever.dfcc.stateMachine;

import java.util.List;

import com.teclever.dfcc.datastore.dto.StageObject;

public class StateMachine {
	enum TestState {
		PENDING,
		RUNNING,
		PAUSED, 
		STOPPED,
		COMPLETED
	}
	enum RunningTestName {
		OTHER,
		SELF_TEST,
		LRU_SRU_TEST,
		SESSION_TEST,
		ADVANCED_TEST
	}
	private static TestState testState = TestState.PENDING;
	private static RunningTestName runningTestName = RunningTestName.OTHER ;

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
		private String userId;
		private String uutId;
		private String uutType;
		private String testType;
		private String sessionId;
		private String sessionName;
		private String sessionTypeID;
		private String sessionTypeName;
		private String runConfigId;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUutId() {
			return uutId;
		}
		public void setUutId(String uutId) {
			this.uutId = uutId;
		}
		public String getUutType() {
			return uutType;
		}
		public void setUutType(String uutType) {
			this.uutType = uutType;
		}
		public String getTestType() {
			return testType;
		}
		public void setTestType(String testType) {
			this.testType = testType;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getSessionName() {
			return sessionName;
		}
		public void setSessionName(String sessionName) {
			this.sessionName = sessionName;
		}
		public String getSessionTypeID() {
			return sessionTypeID;
		}
		public void setSessionTypeID(String sessionTypeID) {
			this.sessionTypeID = sessionTypeID;
		}
		public String getSessionTypeName() {
			return sessionTypeName;
		}
		public void setSessionTypeName(String sessionTypeName) {
			this.sessionTypeName = sessionTypeName;
		}
		public String getRunConfigId() {
			return runConfigId;
		}
		public void setRunConfigId(String runConfigId) {
			this.runConfigId = runConfigId;
		}
				
	}
	
	public static class currentTestDetails{
		private String runningTestPageId;
		private String testType;
		private int testFileCount;
		private int repeatCount;
		private double completionPercentage;
		
		public String getRunningTestPageId() {
			return runningTestPageId;
		}
		public void setRunningTestPageId(String runningTestPageId) {
			this.runningTestPageId = runningTestPageId;
		}
		public String getTestType() {
			return testType;
		}
		public void setTestType(String testType) {
			this.testType = testType;
		}
		public int getTestFileCount() {
			return testFileCount;
		}
		public void setTestFileCount(int testFileCount) {
			this.testFileCount = testFileCount;
		}
		public int getRepeatCount() {
			return repeatCount;
		}
		public void setRepeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
		}
		public double getCompletionPercentage() {
			return completionPercentage;
		}
		public void setCompletionPercentage(double completionPercentage) {
			this.completionPercentage = completionPercentage;
		}
		
	}
	
	private static List<StageObject> stageDatalist ;
	
	public static List<StageObject> getStageDatalist() {
		return stageDatalist;
	}
	public static void setStageDatalist(List<StageObject> stageDatalist) {
		StateMachine.stageDatalist = stageDatalist;
	}
}

