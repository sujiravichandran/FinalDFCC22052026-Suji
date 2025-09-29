package com.teclever.dfcc.advanceddataanalysis;

import java.util.List;

import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;

public class AdvancedDataAnalysisDTO {
	
	private List<AdvancedDataUnitsDetailsDTO> unitsDetails;
	private List<ResultUnitSessionDetailsDTO> sessionList;

	public List<AdvancedDataUnitsDetailsDTO> getUnitsDetails() {
		return unitsDetails;
	}

	public void setUnitsDetails(List<AdvancedDataUnitsDetailsDTO> unitsDetails) {
		this.unitsDetails = unitsDetails;
	}

	public List<ResultUnitSessionDetailsDTO> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<ResultUnitSessionDetailsDTO> sessionList) {
		this.sessionList = sessionList;
	}
	
	

}
