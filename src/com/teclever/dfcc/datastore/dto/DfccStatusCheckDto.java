package com.teclever.dfcc.datastore.dto;

public class DfccStatusCheckDto {

	private int id;
	private int loginSessionId;
	private String uutId;	
	private String boardName;	
	private String data;	
	private String channel1;	
	private String channel2;	
	private String channel3;
	private String channel4;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLoginSessionId() {
		return loginSessionId;
	}

	public void setLoginSessionId(int loginSessionId) {
		this.loginSessionId = loginSessionId;
	}

	public String getUutId() {
		return uutId;
	}

	public void setUutId(String uutId) {
		this.uutId = uutId;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getChannel1() {
		return channel1;
	}

	public void setChannel1(String channel1) {
		this.channel1 = channel1;
	}

	public String getChannel2() {
		return channel2;
	}

	public void setChannel2(String channel2) {
		this.channel2 = channel2;
	}

	public String getChannel3() {
		return channel3;
	}

	public void setChannel3(String channel3) {
		this.channel3 = channel3;
	}

	public String getChannel4() {
		return channel4;
	}

	public void setChannel4(String channel4) {
		this.channel4 = channel4;
	}

	public DfccStatusCheckDto() {
		super();
	}

	
	
}
