package com.teclever.dfcc.model;

import java.util.ArrayList;

public class SessionSubStage {
	private String id;
	private String pId;
	private String l_name;
	private String testType;
	private boolean hasNext;
	
//	public SessionSubStage(String id, String pId, String l_name, String testType, boolean hasNext) {
//		super();
//		this.id = id;
//		this.pId = pId;
//		this.l_name = l_name;
//		this.testType = testType;
//		this.hasNext = hasNext;
//	}
//	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getL_name() {
		return l_name;
	}
	public void setL_name(String l_name) {
		this.l_name = l_name;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	@Override
	public String toString() {
		return "SessionSubStage [id=" + id + ", pId=" + pId + ", l_name=" + l_name + ", testType=" + testType
				+ ", hasNext=" + hasNext + "]";
	}
	

}
