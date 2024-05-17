
package com.teclever.dfcc.datastore.dto;

public class FaultCodeDTO {

	private int faultCode;

	private String faultCodeDescription;

	public FaultCodeDTO(int faultCode, String faultCodeDescription) {
		super();
		this.faultCode = faultCode;
		this.faultCodeDescription = faultCodeDescription;
	}

	public FaultCodeDTO() {
		super();
	}

	public int getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(int faultCode) {
		this.faultCode = faultCode;
	}

	public String getFaultCodeDescription() {
		return faultCodeDescription;
	}

	public void setFaultCodeDescription(String faultCodeDescription) {
		this.faultCodeDescription = faultCodeDescription;
	}

}