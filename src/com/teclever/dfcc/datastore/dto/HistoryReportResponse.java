package com.teclever.dfcc.datastore.dto;

import java.util.List;
import com.teclever.datastore.dto.SessionDto;

public class HistoryReportResponse {
	
	private List<SessionDto> listOfSession;
	private String responseMsg;
	private int responseCode;
	public List<SessionDto> getListOfSession() {
		return listOfSession;
	}
	public void setListOfSession(List<SessionDto> listOfSession) {
		this.listOfSession = listOfSession;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	
	
}
