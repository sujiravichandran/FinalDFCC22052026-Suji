package com.teclever.dfcc.datastore.dto;


public class OfpConfigurationDto {
	
	private String ofpConfigId;
	private String uutId;
	private String ofpName;
	private String ofpVersion;
	private String configFile;
	public String getOfpConfigId() {
		return ofpConfigId;
	}
	public void setOfpConfigId(String ofpConfigId) {
		this.ofpConfigId = ofpConfigId;
	}
	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
	}
	public String getOfpName() {
		return ofpName;
	}
	public void setOfpName(String ofpName) {
		this.ofpName = ofpName;
	}
	public String getOfpVersion() {
		return ofpVersion;
	}
	public void setOfpVersion(String ofpVersion) {
		this.ofpVersion = ofpVersion;
	}
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public OfpConfigurationDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}