package com.teclever.dfcc.datastore.dto;

public class ConfigDatResponse {
    private String configContent;
    private int responseCode;
    private String responseMessage;

    public ConfigDatResponse() {
    }

    public ConfigDatResponse(String configContent, int responseCode, String responseMessage) {
        this.configContent = configContent;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "ConfigDataResponse{" +
                "configContent='" + configContent + '\'' +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
