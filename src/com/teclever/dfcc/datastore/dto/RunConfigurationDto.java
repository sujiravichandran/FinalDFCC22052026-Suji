package com.teclever.dfcc.datastore.dto;

public class RunConfigurationDto {
	
	private String runConfigId;
	private String uutId;
	private String testTypeId;
	private String configFile;
	private String aitess2ConfigFile;
	private String aitess;
	private String driver;
	public String getRunConfigId() {
		return runConfigId;
	}

	public void setRunConfigId(String runConfigId) {
		this.runConfigId = runConfigId;
	}

	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
	}
	public String getTestTypeId() {
		return testTypeId;
	}
	public void setTestTypeId(String testTypeId) {
		this.testTypeId = testTypeId;
	}
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getAitess() {
		return aitess;
	}
	public void setAitess(String aitess) {
		this.aitess = aitess;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public String getAitess2ConfigFile() {
		return aitess2ConfigFile;
	}

	public void setAitess2ConfigFile(String aitess2ConfigFile) {
		this.aitess2ConfigFile = aitess2ConfigFile;
	}

	public RunConfigurationDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}

