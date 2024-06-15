package com.teclever.dfcc.resultstore.dto;

import java.util.List;

public class SelfTestRdfFileDetailsDto {
	
	private String project;
    private String systemDatabaseFile;
    private String userDatabaseFile;
    private String systemMacroFile;
    private String userMacroFile;
    private String testPlanFile;
    private String resultDataFile;
    private String dateOfExecution;
    private String timeOfExecution;
    private List<String> board;
    private List<String> failedBoard;
    
    
    
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
	public List<String> getBoard() {
		return board;
	}
	public void setBoard(List<String> board) {
		this.board = board;
	}
	public List<String> getFailedBoard() {
		return failedBoard;
	}
	public void setFailedBoard(List<String> failedBoard) {
		this.failedBoard = failedBoard;
	}
	public SelfTestRdfFileDetailsDto(String project, String systemDatabaseFile, String userDatabaseFile,
			String systemMacroFile, String userMacroFile, String testPlanFile, String resultDataFile,
			String dateOfExecution, String timeOfExecution, List<String> board, List<String> failedBoard) {
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
		this.board = board;
		this.failedBoard = failedBoard;
	}
    
    
    
    

}
