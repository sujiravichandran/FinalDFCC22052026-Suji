package com.teclever.dfcc.model;

import java.util.ArrayList;
import java.util.List;

public class StageOne {
	private String id;
	private String l1_name;
	private String uutType;
	private ArrayList<String> sessionType;
	private boolean hasNext;
	private boolean isDefault;
	private boolean isMandatory;
	private boolean isContinueWithError;
	private boolean isAdvancedTest;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getL1_name() {
		return l1_name;
	}
	public void setL1_name(String l1_name) {
		this.l1_name = l1_name;
	}
	public String getUutType() {
		return uutType;
	}
	public void setUutType(String uutType) {
		this.uutType = uutType;
	}
	public ArrayList<String> getSessionType() {
		return sessionType;
	}
	public void setSessionType(ArrayList<String> sessionType) {
		this.sessionType = sessionType;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
		
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public boolean isContinueWithError() {
		return isContinueWithError;
	}
	public void setContinueWithError(boolean isContinueWithError) {
		this.isContinueWithError = isContinueWithError;
	}
	public boolean isAdvancedTest() {
		return isAdvancedTest;
	}
	public void setAdvancedTest(boolean isAdvancedTest) {
		this.isAdvancedTest = isAdvancedTest;
	}
	
	
	@Override
	public String toString() {
		return "StageOne [id=" + id + ", l1_name=" + l1_name + ", uutType=" + uutType + ", sessionType=" + sessionType
				+ ", hasNext=" + hasNext + ", isDefault=" + isDefault + ", isMandatory=" + isMandatory
				+ ", isContinueWithError=" + isContinueWithError + ", isAdvancedTest=" + isAdvancedTest + "]";
	}
	
	
	
}

