package com.teclever.dfcc.dashboard;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TemperatureDashBoardDTO {
	
	private String lowTempBlsAECCh1;
	private String lowTempBlsAECCh2;
	private String lowTempBlsAECCh3;
	private String lowTempBlsAECCh4;
	
	private String highTempBlsSCCh1;
	private String highTempBlSCCh2;
	private String highTempBlsSCCh3;
	private String highTempBlsSCCh4;
	
	private String lowTempBlsSCCh1;
	private String lowTempBlSCCh2;
	private String lowTempBlsSCCh3;
	private String lowTempBlsSCCh4;
	
	private String highTempBlsAECCh1;
	private String highTempBlsAECCh2;
	private String highTempBlsAECCh3;
	private String highTempBlsAECCh4;

	
	
	private List<ChannelValuesDTO> blsChannelsValues;
	private List<ChannelValuesDTO> unitChannelsValues;
	
	private Map<String,String>sessionIdName;
	
	private Date timestamp;
	
	
	
	
	
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getLowTempBlsSCCh1() {
		return lowTempBlsSCCh1;
	}
	public void setLowTempBlsSCCh1(String lowTempBlsSCCh1) {
		this.lowTempBlsSCCh1 = lowTempBlsSCCh1;
	}
	public String getLowTempBlSCCh2() {
		return lowTempBlSCCh2;
	}
	public void setLowTempBlSCCh2(String lowTempBlSCCh2) {
		this.lowTempBlSCCh2 = lowTempBlSCCh2;
	}
	public String getLowTempBlsSCCh3() {
		return lowTempBlsSCCh3;
	}
	public void setLowTempBlsSCCh3(String lowTempBlsSCCh3) {
		this.lowTempBlsSCCh3 = lowTempBlsSCCh3;
	}
	public String getLowTempBlsSCCh4() {
		return lowTempBlsSCCh4;
	}
	public void setLowTempBlsSCCh4(String lowTempBlsSCCh4) {
		this.lowTempBlsSCCh4 = lowTempBlsSCCh4;
	}
	public String getHighTempBlsAECCh1() {
		return highTempBlsAECCh1;
	}
	public void setHighTempBlsAECCh1(String highTempBlsAECCh1) {
		this.highTempBlsAECCh1 = highTempBlsAECCh1;
	}
	public String getHighTempBlsAECCh2() {
		return highTempBlsAECCh2;
	}
	public void setHighTempBlsAECCh2(String highTempBlsAECCh2) {
		this.highTempBlsAECCh2 = highTempBlsAECCh2;
	}
	public String getHighTempBlsAECCh3() {
		return highTempBlsAECCh3;
	}
	public void setHighTempBlsAECCh3(String highTempBlsAECCh3) {
		this.highTempBlsAECCh3 = highTempBlsAECCh3;
	}
	public String getHighTempBlsAECCh4() {
		return highTempBlsAECCh4;
	}
	public void setHighTempBlsAECCh4(String highTempBlsAECCh4) {
		this.highTempBlsAECCh4 = highTempBlsAECCh4;
	}
	public String getLowTempBlsAECCh1() {
		return lowTempBlsAECCh1;
	}
	public void setLowTempBlsAECCh1(String lowTempBlsAECCh1) {
		this.lowTempBlsAECCh1 = lowTempBlsAECCh1;
	}
	public String getLowTempBlsAECCh2() {
		return lowTempBlsAECCh2;
	}
	public void setLowTempBlsAECCh2(String lowTempBlsAECCh2) {
		this.lowTempBlsAECCh2 = lowTempBlsAECCh2;
	}
	public String getLowTempBlsAECCh3() {
		return lowTempBlsAECCh3;
	}
	public void setLowTempBlsAECCh3(String lowTempBlsAECCh3) {
		this.lowTempBlsAECCh3 = lowTempBlsAECCh3;
	}
	public String getLowTempBlsAECCh4() {
		return lowTempBlsAECCh4;
	}
	public void setLowTempBlsAECCh4(String lowTempBlsAECCh4) {
		this.lowTempBlsAECCh4 = lowTempBlsAECCh4;
	}
	public String getHighTempBlsSCCh1() {
		return highTempBlsSCCh1;
	}
	public void setHighTempBlsSCCh1(String highTempBlsSCCh1) {
		this.highTempBlsSCCh1 = highTempBlsSCCh1;
	}
	public String getHighTempBlSCCh2() {
		return highTempBlSCCh2;
	}
	public void setHighTempBlSCCh2(String highTempBlSCCh2) {
		this.highTempBlSCCh2 = highTempBlSCCh2;
	}
	public String getHighTempBlsSCCh3() {
		return highTempBlsSCCh3;
	}
	public void setHighTempBlsSCCh3(String highTempBlsSCCh3) {
		this.highTempBlsSCCh3 = highTempBlsSCCh3;
	}
	public String getHighTempBlsSCCh4() {
		return highTempBlsSCCh4;
	}
	public void setHighTempBlsSCCh4(String highTempBlsSCCh4) {
		this.highTempBlsSCCh4 = highTempBlsSCCh4;
	}

	public List<ChannelValuesDTO> getBlsChannelsValues() {
		return blsChannelsValues;
	}
	public void setBlsChannelsValues(List<ChannelValuesDTO> blsChannelsValues) {
		this.blsChannelsValues = blsChannelsValues;
	}
	public List<ChannelValuesDTO> getUnitChannelsValues() {
		return unitChannelsValues;
	}
	public void setUnitChannelsValues(List<ChannelValuesDTO> unitChannelsValues) {
		this.unitChannelsValues = unitChannelsValues;
	}
	public Map<String, String> getSessionIdName() {
		return sessionIdName;
	}
	public void setSessionIdName(Map<String, String> sessionIdName) {
		this.sessionIdName = sessionIdName;
	}	
	
}
