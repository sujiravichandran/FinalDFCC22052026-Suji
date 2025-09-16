package com.teclever.dfcc.dashboard;

import java.util.List;

import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;

public class ProductionSessionDetailsDTO {

	private List<ProductionDashboardDetails> prodList;
	private String dfccSlNo;
	private List<ResultUnitSessionDetailsDTO> sessionList;
	private String msg;
	private String eMsg;
	private int code;
	private int productionSessionSize;

	public List<ResultUnitSessionDetailsDTO> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<ResultUnitSessionDetailsDTO> sessionList) {
		this.sessionList = sessionList;
	}

	public List<ProductionDashboardDetails> getProdList() {
		return prodList;
	}

	public void setProdList(List<ProductionDashboardDetails> prodList) {
		this.prodList = prodList;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String geteMsg() {
		return eMsg;
	}

	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getProductionSessionSize() {
		return productionSessionSize;
	}

	public void setProductionSessionSize(int productionSessionSize) {
		this.productionSessionSize = productionSessionSize;
	}

}
