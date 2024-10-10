package com.teclever.dfcc.datastore.dto;

import java.util.List;

public class LogOutFileCopyResponse {

	private int code;
	private String msg;
	private String eMsg;
	private boolean flag;
	private List<CopyFileDTO> copyFileDTOList;
	private String sessionName;
	private String sessionPath;
	private List<String>stageIds;
	

	
	public List<String> getStageIds() {
		return stageIds;
	}

	public void setStageIds(List<String> stageIds) {
		this.stageIds = stageIds;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String geteMsg() {
		return eMsg;
	}

	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public List<CopyFileDTO> getCopyFileDTOList() {
		return copyFileDTOList;
	}

	public void setCopyFileDTOList(List<CopyFileDTO> copyFileDTOList) {
		this.copyFileDTOList = copyFileDTOList;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionPath() {
		return sessionPath;
	}

	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

}
