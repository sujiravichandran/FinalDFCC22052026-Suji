package com.teclever.dfcc.datastore.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChannelTemperature {

    private StringProperty channel1Temp;
    private StringProperty channel2Temp;
    private StringProperty channel3Temp;
    private StringProperty channel4Temp;

    public ChannelTemperature() {
        this.channel1Temp = new SimpleStringProperty();
        this.channel2Temp = new SimpleStringProperty();
        this.channel3Temp = new SimpleStringProperty();
        this.channel4Temp = new SimpleStringProperty();
    }

    public ChannelTemperature(String channel1Temp, String channel2Temp, String channel3Temp, String channel4Temp) {
        this.channel1Temp = new SimpleStringProperty(channel1Temp);
        this.channel2Temp = new SimpleStringProperty(channel2Temp);
        this.channel3Temp = new SimpleStringProperty(channel3Temp);
        this.channel4Temp = new SimpleStringProperty(channel4Temp);
    }

    public StringProperty channel1TempProperty() {
        return channel1Temp;
    }

    public String getChannel1Temp() {
        return channel1Temp.get();
    }

    public void setChannel1Temp(String channel1Temp) {
        this.channel1Temp.set(channel1Temp);
    }

    public StringProperty channel2TempProperty() {
        return channel2Temp;
    }

    public String getChannel2Temp() {
        return channel2Temp.get();
    }

    public void setChannel2Temp(String channel2Temp) {
        this.channel2Temp.set(channel2Temp);
    }

    public StringProperty channel3TempProperty() {
        return channel3Temp;
    }

    public String getChannel3Temp() {
        return channel3Temp.get();
    }

    public void setChannel3Temp(String channel3Temp) {
        this.channel3Temp.set(channel3Temp);
    }

    public StringProperty channel4TempProperty() {
        return channel4Temp;
    }

    public String getChannel4Temp() {
        return channel4Temp.get();
    }

    public void setChannel4Temp(String channel4Temp) {
        this.channel4Temp.set(channel4Temp);
    }

    @Override
    public String toString() {
        return "[channel1Temp=" + channel1Temp.get() + ", channel2Temp=" + channel2Temp.get() +
               ", channel3Temp=" + channel3Temp.get() + ", channel4Temp=" + channel4Temp.get() + "]";
    }
}
