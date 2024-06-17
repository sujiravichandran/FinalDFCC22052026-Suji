package com.teclever.dfcc.datastore.dto;

public class ChannelTemperature {
	
    private String channelName;
    private String temperatureValue;


    public ChannelTemperature(String channelName, String temperatureValue) {
        this.channelName = channelName;
        this.temperatureValue = temperatureValue;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(String temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    @Override
    public String toString() {
        return channelName + ": " + temperatureValue;
    }
}

