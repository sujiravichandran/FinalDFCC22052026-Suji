
package com.teclever.dfcc.datastore.dto;

public class FaultCodeDTO {

	private String faultCodeMasterId;

	private int faultCode;
	
	private String uutId;

	private String faultCodeDescription;
	
	private String faultCodeFilePath;

	

	public FaultCodeDTO(String faultCodeMasterId,String uutId, int faultCode, String faultCodeDescription,
			String faultCodeFilePath) {
		super();
		this.faultCodeMasterId = faultCodeMasterId;
		this.uutId = uutId;
		this.faultCode = faultCode;
		this.faultCodeDescription = faultCodeDescription;
		this.faultCodeFilePath = faultCodeFilePath;
	}

	public String getFaultCodeFilePath() {
		return faultCodeFilePath;
	}

	public void setFaultCodeFilePath(String faultCodeFilePath) {
		this.faultCodeFilePath = faultCodeFilePath;
	}

	public String getFaultCodeMasterId() {
		return faultCodeMasterId;
	}

	public void setFaultCodeMasterId(String faultCodeMasterId) {
		this.faultCodeMasterId = faultCodeMasterId;
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

	public String getUutId() {
		return uutId;
	}

	public void setUutId(String uutId) {
		this.uutId = uutId;
	}

	@Override
	public String toString() {
		return "[faultCodeMasterId=" + faultCodeMasterId + ", faultCode=" + faultCode + ", uutId=" + uutId
				+ ", faultCodeDescription=" + faultCodeDescription + ", faultCodeFilePath=" + faultCodeFilePath + "]";
	}
	
	

}