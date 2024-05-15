package com.teclever.dfcc.model;

import java.sql.Blob;

public class User {

	private String userId;

	private String userName;
	
	private String roleType;
	
	private Blob digitalSignature;


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public Blob getDigitalSignature() {
		return digitalSignature;
	}

	public void setDigitalSignature(Blob blob) {
		this.digitalSignature = blob;
	}

	
}
