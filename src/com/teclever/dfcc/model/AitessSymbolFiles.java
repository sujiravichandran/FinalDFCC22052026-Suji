package com.teclever.dfcc.model;

public class AitessSymbolFiles {
	private String fileName;

	    public String getFileName() {
	        return fileName;
	    }

	    public void setFileName(String fileName) {
	        this.fileName = fileName;
	    }
	    
	    public static class AitessSymbolDetails {
	    	private String fileName;
	    	private String symbolName;
	    	private String symbolType;
	    	private String min;
	    	private String max;
	    	
	    	public String getFileName() {
	    		return fileName;
	    	}
	    	public void setFileName(String fileName) {
	    		this.fileName = fileName;
	    	}
	    	public String getSymbolName() {
	    		return symbolName;
	    	}
	    	public void setSymbolName(String symbolName) {
	    		this.symbolName = symbolName;
	    	}
	    	public String getSymbolType() {
	    		return symbolType;
	    	}
	    	public void setSymbolType(String symbolType) {
	    		this.symbolType = symbolType;
	    	}
	    	public String getMin() {
	    		return min;
	    	}
	    	public void setMin(String min) {
	    		this.min = min;
	    	}
	    	public String getMax() {
	    		return max;
	    	}
	    	public void setMax(String max) {
	    		this.max = max;
	    	}
	    	@Override
	    	public String toString() {
	    		return "AitessSymbolDetails [symbolName=" + symbolName + ", symbolType=" + symbolType + ", min=" + min
	    				+ ", max=" + max + "]";
	    	}
	    	
	    }

}