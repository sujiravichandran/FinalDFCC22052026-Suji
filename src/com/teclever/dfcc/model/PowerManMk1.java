package com.teclever.dfcc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PowerManMk1 {

    private String inputVoltageSetting;
    private String description;
    private String expectedValue;

    private final StringProperty channel1 = new SimpleStringProperty("");
    private final StringProperty channel2 = new SimpleStringProperty("");
    private final StringProperty channel3 = new SimpleStringProperty("");
    private final StringProperty channel4 = new SimpleStringProperty("");

    // Channel 1
    public String getChannel1() { return channel1.get(); }
    public void setChannel1(String value) { channel1.set(value); }
    public StringProperty channel1Property() { return channel1; }

    // Channel 2
    public String getChannel2() { return channel2.get(); }
    public void setChannel2(String value) { channel2.set(value); }
    public StringProperty channel2Property() { return channel2; }

    // Channel 3
    public String getChannel3() { return channel3.get(); }
    public void setChannel3(String value) { channel3.set(value); }
    public StringProperty channel3Property() { return channel3; }

    // Channel 4
    public String getChannel4() { return channel4.get(); }
    public void setChannel4(String value) { channel4.set(value); }
    public StringProperty channel4Property() { return channel4; }

    // Other properties
    public String getInputVoltageSetting() { return inputVoltageSetting; }
    public void setInputVoltageSetting(String inputVoltageSetting) { this.inputVoltageSetting = inputVoltageSetting; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
}
