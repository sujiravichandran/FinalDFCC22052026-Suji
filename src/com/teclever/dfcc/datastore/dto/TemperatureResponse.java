package com.teclever.dfcc.datastore.dto;

import java.util.List;
import java.util.Map;

public class TemperatureResponse {

    private String responseMsg;
    private int responseCode;
    private List<ChannelTemperature> temperatures;
    private Map<String, List<ChannelTemperature>> boardTemperatureMap;

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
	public List<ChannelTemperature> getTemperatures() {
		return temperatures;
	}
	public void setTemperatures(List<ChannelTemperature> temperatures) {
		this.temperatures = temperatures;
	}
	

	public Map<String, List<ChannelTemperature>> getBoardTemperatureMap() {
		return boardTemperatureMap;
	}
	public void setBoardTemperatureMap(Map<String, List<ChannelTemperature>> boardTemperatureMap) {
		this.boardTemperatureMap = boardTemperatureMap;
	}
	public TemperatureResponse() {
		super();
	}

	
}
