package com.teclever.dfcc;

public class UserData {
	private static String roleId;

	private static String userId;

	public static String getRoleId() {
		return roleId;
	}

	public static void setRoleId(String roleId) {
		UserData.roleId = roleId;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		UserData.userId = userId;
	}

}
