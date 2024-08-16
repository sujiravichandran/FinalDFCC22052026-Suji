package com.teclever.dfcc.datastore.dto;

import java.util.Date;

public class UUTLogBookDto {
	

	private int logId;
	private String uutId;
	private String uutSerialNumber;
	private String sessionId;
	private String username;
	private Date timestamp;
	private String details;
	
	
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	public String getUutId() {
		return uutId;
	}
	public void setUutId(String uutId) {
		this.uutId = uutId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	public String getUutSerialNumber() {
		return uutSerialNumber;
	}
	public void setUutSerialNumber(String uutSerialNumber) {
		this.uutSerialNumber = uutSerialNumber;
	}	
	
	public UUTLogBookDto(String uutId, String uutSerialNumber, String sessionId, String username, Date timestamp,
			String details) {
		super();
		this.uutId = uutId;
		this.uutSerialNumber = uutSerialNumber;
		this.sessionId = sessionId;
		this.username = username;
		this.timestamp = timestamp;
		this.details = details;
	}
	public UUTLogBookDto() {
		super();
	}
	
	
	


}
