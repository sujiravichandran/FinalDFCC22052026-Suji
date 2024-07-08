package com.teclever.dfcc.datastore.dto;

public class CardMemoryDto {
	
	private int id;
	private String memoryType;
	private String startAddress;
	private String endAddress;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMemoryType() {
		return memoryType;
	}
	public void setMemoryType(String memoryType) {
		this.memoryType = memoryType;
	}
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	
	
	public CardMemoryDto() {
		super();
	}
	
	

}
