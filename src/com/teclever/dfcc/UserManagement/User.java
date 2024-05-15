package com.teclever.dfcc.UserManagement;

public class User {

	private int siNo;

	private String username;

	private String digitalSign;

	private String userType;

	public User(int siNo, String username, String digitalSign, String userType) {

		this.siNo = siNo;

		this.username = username;

		this.digitalSign = digitalSign;

		this.userType = userType;

	}

	public int getSiNo() {

		return siNo;

	}

	public void setsiNo(int siNo) {

		this.siNo = siNo;

	}

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public String getDigitalSign() {

		return digitalSign;

	}

	public void setDigitalSign(String digitalSign) {

		this.digitalSign = digitalSign;

	}

	public String getUserType() {

		return userType;

	}

	public void setUserType(String userType) {

		this.userType = userType;

	}

	@Override

	public String toString() {

		return "S.No: " + siNo + ", User Name: " + username + ", Digital Sign: " + digitalSign + ", User Type: "

				+ userType;

	}
}
