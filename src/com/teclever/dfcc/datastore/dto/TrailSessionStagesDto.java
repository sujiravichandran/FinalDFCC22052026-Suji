package com.teclever.dfcc.datastore.dto;

import java.util.List;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;

public class TrailSessionStagesDto {

	private List<StageObject> listOfStageObject;

	private Response response;

	private SessionDto sessionDto;
	
	private int code;
	
	private int popupMsg;
	
		public List<StageObject> getListOfStageObject() {
		return listOfStageObject;
	}

	public void setListOfStageObject(List<StageObject> listOfStageObject) {
		this.listOfStageObject = listOfStageObject;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public SessionDto getSessionDto() {
		return sessionDto;
	}

	public void setSessionDto(SessionDto sessionDto) {
		this.sessionDto = sessionDto;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getPopupMsg() {
		return popupMsg;
	}

	public void setPopupMsg(int popupMsg) {
		this.popupMsg = popupMsg;
	}

	
}
