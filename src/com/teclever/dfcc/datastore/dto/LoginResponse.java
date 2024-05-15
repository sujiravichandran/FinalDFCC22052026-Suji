package com.teclever.dfcc.datastore.dto;

import com.teclever.datastore.dto.Response;

public class LoginResponse {


	public String loginName;
//	public String userName;
//	public String changePwdFlag;
	public String roleId;
//	private String roleName;
	private Response response;

	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
//	public String getUserName() {
//		return userName;
//	}
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//	public String getChangePwdFlag() {
//		return changePwdFlag;
//	}
//	public void setChangePwdFlag(String changePwdFlag) {
//		this.changePwdFlag = changePwdFlag;
//	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
//	public String getRoleName() {
//		return roleName;
//	}
//	public void setRoleName(String roleName) {
//		this.roleName = roleName;
//	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	
	
	
}
