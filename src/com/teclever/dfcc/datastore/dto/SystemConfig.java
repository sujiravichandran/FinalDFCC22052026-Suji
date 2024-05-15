package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class SystemConfig {
	
    private String firstLine;
    private String launchType;
    private String adminName;
    private String adminPassword;
    private String loginType;
    private String checksumHeading;
    private List<String> checksums;
    private String pChecksum;
    
    
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	public String getLaunchType() {
		return launchType;
	}
	public void setLaunchType(String launchType) {
		this.launchType = launchType;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getChecksumHeading() {
		return checksumHeading;
	}
	public void setChecksumHeading(String checksumHeading) {
		this.checksumHeading = checksumHeading;
	}
	public List<String> getChecksums() {
		return checksums;
	}
	public void setChecksums(List<String> checksums) {
		this.checksums = checksums;
	}
	public String getpChecksum() {
		return pChecksum;
	}
	public void setpChecksum(String pChecksum) {
		this.pChecksum = pChecksum;
	}
	@Override
	public String toString() {
		return "SystemConfig [firstLine=" + firstLine + ", launchType=" + launchType + ", adminName=" + adminName
				+ ", adminPassword=" + adminPassword + ", loginType=" + loginType + ", checksumHeading="
				+ checksumHeading + ", checksums=" + checksums + ", pChecksum=" + pChecksum + "]";
	} 
    
	 

}
