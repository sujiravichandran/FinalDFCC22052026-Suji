package com.teclever.dfcc.datastore.dto;

public class AitessConfigurationDto {

	private int aitessId;
	private String uutId;
	private String aitessName;
	private String aitessCommand;
	private String aitessVersion;
	private String driverName;
	private String driverCommand;
	private String driverVersion;
	private boolean deleteStatus;
	
	public boolean isDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public int getAitessId() {
		return aitessId;
	}
	public void setAitessId(int aitessId) {
		this.aitessId = aitessId;
	}
	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
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
	public String getDriverCommand() {
		return driverCommand;
	}
	public void setDriverCommand(String driverCommand) {
		this.driverCommand = driverCommand;
	}
	public String getDriverVersion() {
		return driverVersion;
	}
	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}
	public AitessConfigurationDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	
	
}
