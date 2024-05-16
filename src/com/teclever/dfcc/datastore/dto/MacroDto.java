package com.teclever.dfcc.datastore.dto;

public class MacroDto {
	private int macroId;
    private String macroName;
    private String fileName;
    private String runPathMasterId;
	public int getMacroId() {
		return macroId;
	}
	public void setMacroId(int macroId) {
		this.macroId = macroId;
	}
	public String getMacroName() {
		return macroName;
	}
	public void setMacroName(String macroName) {
		this.macroName = macroName;
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
	public MacroDto() {
		super();
	}

	public MacroDto(String macroName, String fileName, String runPathMasterId)
	{
		this.macroName = macroName;
		this.fileName = fileName;
		this.runPathMasterId = runPathMasterId;
	}
	

    
    

}
