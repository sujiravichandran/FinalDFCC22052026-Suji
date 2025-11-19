package com.teclever.dfcc.dashboard;

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
	
	private String lowTempAECCh1;
	private String lowTempAECCh2;
	private String lowTempAECCh3;
	private String lowTempAECCh4;
	
	private String highTempSCCh1;
	private String highTempSCCh2;
	private String highTempSCCh3;
	private String highTempSCCh4;
	
	
	private List<ChannelValuesDTO> blsChannelsValues;
	private List<ChannelValuesDTO> unitChannelsValues;
	
	private Map<String,String>sessionIdName;
	
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
	public String getLowTempAECCh1() {
		return lowTempAECCh1;
	}
	public void setLowTempAECCh1(String lowTempAECCh1) {
		this.lowTempAECCh1 = lowTempAECCh1;
	}
	public String getLowTempAECCh2() {
		return lowTempAECCh2;
	}
	public void setLowTempAECCh2(String lowTempAECCh2) {
		this.lowTempAECCh2 = lowTempAECCh2;
	}
	public String getLowTempAECCh3() {
		return lowTempAECCh3;
	}
	public void setLowTempAECCh3(String lowTempAECCh3) {
		this.lowTempAECCh3 = lowTempAECCh3;
	}
	public String getLowTempAECCh4() {
		return lowTempAECCh4;
	}
	public void setLowTempAECCh4(String lowTempAECCh4) {
		this.lowTempAECCh4 = lowTempAECCh4;
	}
	public String getHighTempSCCh1() {
		return highTempSCCh1;
	}
	public void setHighTempSCCh1(String highTempSCCh1) {
		this.highTempSCCh1 = highTempSCCh1;
	}
	public String getHighTempSCCh2() {
		return highTempSCCh2;
	}
	public void setHighTempSCCh2(String highTempSCCh2) {
		this.highTempSCCh2 = highTempSCCh2;
	}
	public String getHighTempSCCh3() {
		return highTempSCCh3;
	}
	public void setHighTempSCCh3(String highTempSCCh3) {
		this.highTempSCCh3 = highTempSCCh3;
	}
	public String getHighTempSCCh4() {
		return highTempSCCh4;
	}
	public void setHighTempSCCh4(String highTempSCCh4) {
		this.highTempSCCh4 = highTempSCCh4;
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
