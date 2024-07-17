package com.teclever.dfcc.model;

public class SubStage {
	private String id;
	private String pId;
	private String l_name;
	private String testType;
	private boolean hasNext;
	private boolean parentDefault;
	private boolean parentAdvancedTest;
	
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

	
	public boolean isParentDefault() {
		return parentDefault;
	}

	public void setParentDefault(boolean parentDefault) {
		this.parentDefault = parentDefault;
	}

	public boolean isParentAdvancedTest() {
		return parentAdvancedTest;
	}

	public void setParentAdvancedTest(boolean parentAdvancedTest) {
		this.parentAdvancedTest = parentAdvancedTest;
	}

	@Override
	public String toString() {
		return "SubStage [id=" + id + ", pId=" + pId + ", l_name=" + l_name + ", testType=" + testType + ", hasNext="
				+ hasNext + ", parentDefault=" + parentDefault + ", parentAdvancedTest=" + parentAdvancedTest + "]";
	}

	
	

}
