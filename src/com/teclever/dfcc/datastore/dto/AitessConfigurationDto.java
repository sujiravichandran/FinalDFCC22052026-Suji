package com.teclever.dfcc.datastore.dto;

public class AitessConfigurationDto {

	private int aitessId;
	private String aitessName;
	private String aitessCommand;
	private String aitessVersion;
	private String driverName;
	private String loadDriverCommand;
	private String unloadDriverCommand;
	private boolean deleteStatus;
	
	
	public int getAitessId() {
		return aitessId;
	}
	public void setAitessId(int aitessId) {
		this.aitessId = aitessId;
	}
	
	public String getAitessName() {
		return aitessName;
	}
	public void setAitessName(String aitessName) {
		this.aitessName = aitessName;
	}
	public String getAitessCommand() {
		return aitessCommand;
	}
	public void setAitessCommand(String aitessCommand) {
		this.aitessCommand = aitessCommand;
	}
	public String getAitessVersion() {
		return aitessVersion;
	}
	public void setAitessVersion(String aitessVersion) {
		this.aitessVersion = aitessVersion;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getLoadDriverCommand() {
		return loadDriverCommand;
	}
	public void setLoadDriverCommand(String loadDriverCommand) {
		this.loadDriverCommand = loadDriverCommand;
	}
	public String getUnloadDriverCommand() {
		return unloadDriverCommand;
	}
	public void setUnloadDriverCommand(String unloadDriverCommand) {
		this.unloadDriverCommand = unloadDriverCommand;
	}
	
	public boolean isDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public AitessConfigurationDto() {
		super();
	}
	
	

	
	
}
