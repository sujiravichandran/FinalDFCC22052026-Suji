package com.teclever.dfcc.datastore.dto;

import java.sql.Blob;
import java.sql.Date;

import com.teclever.datastore.dto.Response;

public class UserLoginDetailsDto {

	private String userId;

	private String loginName;

	private String password;

	private Blob digitalSignature;

	private String roleId;

	private String roleName;
	
	private Date date;

	private Response response;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Blob getDigitalSignature() {
		return digitalSignature;
	}

	public void setDigitalSignature(Blob digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
