package com.teclever.dfcc.model;

import java.sql.Blob;

public class User {

	private String id;
	private String userName;
	private String roleType;
	public  Blob digitalSignature;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
