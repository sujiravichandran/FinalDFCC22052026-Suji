package com.teclever.dfcc.datastore.dto;

import java.util.List;
import java.util.Map;

public class AddCustomFileResponse {
	
	private int responseCode;
	private String responseMsg;
	private String eMsg;
	private List<AddFilesDetailsDTO>addedFilesDetailsList;
	private List<AddFilesDetailsDTO>existingFileDetailsList;

	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public List<AddFilesDetailsDTO> getExistingFileDetailsList() {
		return existingFileDetailsList;
	}
	public void setExistingFileDetailsList(List<AddFilesDetailsDTO> existingFileDetailsList) {
		this.existingFileDetailsList = existingFileDetailsList;
	}
	public String geteMsg() {
		return eMsg;
	}
	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
	public List<AddFilesDetailsDTO> getAddedFilesDetailsList() {
		return addedFilesDetailsList;
	}
	public void setAddedFilesDetailsList(List<AddFilesDetailsDTO> addedFilesDetailsList) {
		this.addedFilesDetailsList = addedFilesDetailsList;
	}
	

}
