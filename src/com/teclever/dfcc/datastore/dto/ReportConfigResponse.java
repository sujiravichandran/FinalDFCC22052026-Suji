package com.teclever.dfcc.datastore.dto;

import java.util.List;

import com.teclever.datastore.dto.Response;

public class ReportConfigResponse {

	private List<ReportConfigDto> listOfReportConfigDto;

	private Response response;

	public List<ReportConfigDto> getListOfReportConfigDto() {
		return listOfReportConfigDto;
	}

	public void setListOfReportConfigDto(List<ReportConfigDto> listOfReportConfigDto) {
		this.listOfReportConfigDto = listOfReportConfigDto;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
