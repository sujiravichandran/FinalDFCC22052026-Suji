
package com.teclever.dfcc.datastore.dto;

public class FaultCodeDTO {

	private String faultCodeMasterId;

	private int faultCode;
	
	private String uutId;
	
	private String ofpConfigId;

	private String faultCodeDescription;
	
	private String faultCodeFilePath;

	

	public FaultCodeDTO(String faultCodeMasterId,String uutId,String ofpConfigId, int faultCode, String faultCodeDescription,
			String faultCodeFilePath) {
		super();
		this.faultCodeMasterId = faultCodeMasterId;
		this.uutId = uutId;
		this.ofpConfigId = ofpConfigId;
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

	public String getOfpConfigId() {
		return ofpConfigId;
	}

	public void setOfpConfigId(String ofpConfigId) {
		this.ofpConfigId = ofpConfigId;
	}

	@Override
	public String toString() {
		return "faultCodeMasterId=" + faultCodeMasterId + ", faultCode=" + faultCode + ", uutId=" + uutId
				+ ", ofpConfigId=" + ofpConfigId + ", faultCodeDescription=" + faultCodeDescription
				+ ", faultCodeFilePath=" + faultCodeFilePath + "";
	}

	
	

}