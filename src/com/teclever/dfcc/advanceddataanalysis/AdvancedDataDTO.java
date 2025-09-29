package com.teclever.dfcc.advanceddataanalysis;

import java.util.List;

import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;

public class AdvancedDataDTO {
	
	private String uutType;
	private List<AdvancedDataUnitsDetailsDTO> advancedDataUnitsDetailsDTOList;

	private List<ResultDetailedDTO> resultDetailedList;
	private List<ResultExecutionDTO> resultBriefList;
	private List<ResultUnitSessionDetailsDTO> allSessionList;
	

}
