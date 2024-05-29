package com.teclever.dfcc.resultstore.dto;

import java.util.List;

public class RdfFileDetailsDto {

		private String project;
	    private String systemDatabaseFile;
	    private String userDatabaseFile;
	    private String systemMacroFile;
	    private String userMacroFile;
	    private String testPlanFile;
	    private String resultDataFile;
	    private String dateOfExecution;
	    private String timeOfExecution;
	    private List<String> step;
	    private List<String> failedStep;
	    
	    
		public String getProject() {
			return project;
		}
		public void setProject(String project) {
			this.project = project;
		}
		public String getSystemDatabaseFile() {
			return systemDatabaseFile;
		}
		public void setSystemDatabaseFile(String systemDatabaseFile) {
			this.systemDatabaseFile = systemDatabaseFile;
		}
		public String getUserDatabaseFile() {
			return userDatabaseFile;
		}
		public void setUserDatabaseFile(String userDatabaseFile) {
			this.userDatabaseFile = userDatabaseFile;
		}
		public String getSystemMacroFile() {
			return systemMacroFile;
		}
		public void setSystemMacroFile(String systemMacroFile) {
			this.systemMacroFile = systemMacroFile;
		}
		public String getUserMacroFile() {
			return userMacroFile;
		}
		public void setUserMacroFile(String userMacroFile) {
			this.userMacroFile = userMacroFile;
		}
		public String getTestPlanFile() {
			return testPlanFile;
		}
		public void setTestPlanFile(String testPlanFile) {
			this.testPlanFile = testPlanFile;
		}
		public String getResultDataFile() {
			return resultDataFile;
		}
		public void setResultDataFile(String resultDataFile) {
			this.resultDataFile = resultDataFile;
		}
		public String getDateOfExecution() {
			return dateOfExecution;
		}
		public void setDateOfExecution(String dateOfExecution) {
			this.dateOfExecution = dateOfExecution;
		}
		public String getTimeOfExecution() {
			return timeOfExecution;
		}
		public void setTimeOfExecution(String timeOfExecution) {
			this.timeOfExecution = timeOfExecution;
		}
		public List<String> getStep() {
			return step;
		}
		public void setStep(List<String> step) {
			this.step = step;
		}
		public List<String> getFailedStep() {
			return failedStep;
		}
		public void setFailedStep(List<String> failedStep) {
			this.failedStep = failedStep;
		}
		public RdfFileDetailsDto(String project, String systemDatabaseFile, String userDatabaseFile,
				String systemMacroFile, String userMacroFile, String testPlanFile, String resultDataFile,
				String dateOfExecution, String timeOfExecution, List<String> step, List<String> failedStep) {
			super();
			this.project = project;
			this.systemDatabaseFile = systemDatabaseFile;
			this.userDatabaseFile = userDatabaseFile;
			this.systemMacroFile = systemMacroFile;
			this.userMacroFile = userMacroFile;
			this.testPlanFile = testPlanFile;
			this.resultDataFile = resultDataFile;
			this.dateOfExecution = dateOfExecution;
			this.timeOfExecution = timeOfExecution;
			this.step = step;
			this.failedStep = failedStep;
		}
	    
	    
	    
}
