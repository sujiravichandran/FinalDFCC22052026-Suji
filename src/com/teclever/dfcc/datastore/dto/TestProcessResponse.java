
package com.teclever.dfcc.datastore.dto;
import java.util.Map;
import com.teclever.datastore.dto.Response;
public class TestProcessResponse {
	private Map<String, String> testProcessResult;
	private Response response;
	public Map<String, String> getTestProcessResult() {
		return testProcessResult;
	}
	public void setTestProcessResult(Map<String, String> testProcessResult) {
		this.testProcessResult = testProcessResult;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
}