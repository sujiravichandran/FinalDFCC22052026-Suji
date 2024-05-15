package com.teclever.dfcc.datastore.dto;


public class UUTMasterDetailsDto {

	private String uutId;
	private String uutType;
	

	public UUTMasterDetailsDto(String uutType) {
		this.uutType=uutType;
	}

	public String getUutId() {
		return uutId;
	}

	public void setUutId(String uutId) {
		this.uutId = uutId;
	}

	public String getUutType() {
		return uutType;
	}

	public void setUutType(String uutType) {
		this.uutType = uutType;
	}


}
