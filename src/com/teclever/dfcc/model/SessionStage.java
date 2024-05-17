package com.teclever.dfcc.model;

import java.util.ArrayList;
import java.util.List;

public class SessionStage {
	private String id;
	private String l1_name;
	private String uutType;
	private ArrayList<String> sessionType;
	private boolean hasNext;
	
//	public SessionStage(int id, String l1_name, String uutType, ArrayList<String> sessionType,boolean hasNext) {
//		super();
//		this.id = id;
//		this.l1_name = l1_name;
//		this.uutType = uutType;
//		this.sessionType = sessionType;
//		this.hasNext = hasNext;
//	}
	
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
	@Override
	public String toString() {
		return "SessionStage [id=" + id + ", l1_name=" + l1_name + ", uutType=" + uutType + ", sessionType="
				+ sessionType + ", hasNext=" + hasNext + "]";
	}
	
}

