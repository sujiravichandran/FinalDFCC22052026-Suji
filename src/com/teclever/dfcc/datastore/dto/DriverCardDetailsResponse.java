package com.teclever.dfcc.datastore.dto;

import java.util.List;
import com.teclever.datastore.dto.Response;

public class DriverCardDetailsResponse {
	
    private List<DriverCard> driverCardDetails;
    private List<String> processIds;
	private Response response;
	
	
	
	public List<DriverCard> getDriverCardDetails() {
		return driverCardDetails;
	}
	public void setDriverCardDetails(List<DriverCard> driverCardDetails) {
		this.driverCardDetails = driverCardDetails;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	
	public List<String> getProcessIds() {
		return processIds;
	}
	public void setProcessIds(List<String> processIds) {
		this.processIds = processIds;
	}
	public DriverCardDetailsResponse() {
		super();
	}
	
	
	

}
