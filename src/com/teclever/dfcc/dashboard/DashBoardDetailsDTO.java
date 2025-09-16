package com.teclever.dfcc.dashboard;

import java.util.List;

import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;

public class DashBoardDetailsDTO {
	
	private String uutType;
	private String serialNo;
	private List<ProductionDashboardDetails> productionSessionDetailsDTOList;
	private PQTSessionDetailsDTO pQTSessionDetailsDTO; 
	private List<ResultDetailedDTO> resultDetailedList;
	private List<ResultExecutionDTO> resultBriefList;
	private List<ResultUnitSessionDetailsDTO> allSessionListBySlNo;
	private int code;
	private String msg;
	private String emsg;
	private String lastTestedDate;
	
	public String getLastTestedDate() {
		return lastTestedDate;
	}
	public void setLastTestedDate(String lastTestedDate) {
		this.lastTestedDate = lastTestedDate;
	}
	public String getUutType() {
		return uutType;
	}
	public void setUutType(String uutType) {
		this.uutType = uutType;
	}
	
	
	public List<ResultUnitSessionDetailsDTO> getAllSessionListBySlNo() {
		return allSessionListBySlNo;
	}
	public void setAllSessionListBySlNo(List<ResultUnitSessionDetailsDTO> allSessionListBySlNo) {
		this.allSessionListBySlNo = allSessionListBySlNo;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getEmsg() {
		return emsg;
	}
	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public List<ProductionDashboardDetails> getProductionSessionDetailsDTOList() {
		return productionSessionDetailsDTOList;
	}
	public void setProductionSessionDetailsDTOList(List<ProductionDashboardDetails> productionSessionDetailsDTOList) {
		this.productionSessionDetailsDTOList = productionSessionDetailsDTOList;
	}
	public PQTSessionDetailsDTO getpQTSessionDetailsDTO() {
		return pQTSessionDetailsDTO;
	}
	public void setpQTSessionDetailsDTO(PQTSessionDetailsDTO pQTSessionDetailsDTO) {
		this.pQTSessionDetailsDTO = pQTSessionDetailsDTO;
	}
	public List<ResultDetailedDTO> getResultDetailedList() {
		return resultDetailedList;
	}
	public void setResultDetailedList(List<ResultDetailedDTO> resultDetailedList) {
		this.resultDetailedList = resultDetailedList;
	}
	public List<ResultExecutionDTO> getResultBriefList() {
		return resultBriefList;
	}
	public void setResultBriefList(List<ResultExecutionDTO> resultBriefList) {
		this.resultBriefList = resultBriefList;
	}

	
}
