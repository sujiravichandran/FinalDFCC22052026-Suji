package com.teclever.dfcc.datastore.dto;

public class SymbolDto {
	
	private int symbolId;
	private String symbolName;
	private String symbolType;
	private String min;
	private String max;
	private String fileName;
    private String runPathMasterId;
    private String readValue;
    private String writeValue;
    private String fullSymbolLine;
    
    

	public String getFullSymbolLine() {
		return fullSymbolLine;
	}

	public void setFullSymbolLine(String fullSymbolLine) {
		this.fullSymbolLine = fullSymbolLine;
	}

	public int getSymbolId() {
		return symbolId;
	}

	public void setSymbolId(int symbolId) {
		this.symbolId = symbolId;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRunPathMasterId() {
		return runPathMasterId;
	}

	public void setRunPathMasterId(String runPathMasterId) {
		this.runPathMasterId = runPathMasterId;
	}
	
	

	public String getReadValue() {
		return readValue;
	}

	public void setReadValue(String readValue) {
		this.readValue = readValue;
	}

	public String getWriteValue() {
		return writeValue;
	}

	public void setWriteValue(String writeValue) {
		this.writeValue = writeValue;
	}

	public SymbolDto(String symbolName, String symbolType, String min, String max, String fileName , String runPathMasterId, String readValue, String writeValue,String fullSymbolLine)
	{
		this.symbolName = symbolName;
		this.symbolType = symbolType;
		this.min = min;
		this.max  = max;
		this.fileName =  fileName;
		this.runPathMasterId = runPathMasterId;
		this.readValue = readValue;
		this.writeValue = writeValue;
		this.fullSymbolLine =fullSymbolLine;
	}

	public SymbolDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
