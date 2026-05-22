package com.teclever.dfcc.dashboard;

import java.util.Date;

public class ChannelValuesDTO {

    private double ch1AECValue;
    private double ch2AECValue;
    private double ch3AECValue;
    private double ch4AECValue;

    private double ch1SCValue;
    private double ch2SCValue;
    private double ch3SCValue;
    private double ch4SCValue;

    private String sessionId;

    // Initialize timestamp so it is never null
    private Date timestamp = new Date();

    // ---------------- Timestamp ----------------

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        if (timestamp != null) {
            this.timestamp = timestamp;
        } else {
            this.timestamp = new Date();
        }
    }

    // ---------------- Update Values ----------------

    public void updateValues(double ch1, double ch2, double ch3, double ch4) {
        this.ch1SCValue = ch1;
        this.ch2SCValue = ch2;
        this.ch3SCValue = ch3;
        this.ch4SCValue = ch4;

        // Update timestamp whenever values change
        this.timestamp = new Date();
    }

    // ---------------- Session ----------------

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // ---------------- AEC Values ----------------

    public double getCh1AECValue() {
        return ch1AECValue;
    }

    public void setCh1AECValue(double ch1aecValue) {
        ch1AECValue = ch1aecValue;
    }

    public double getCh2AECValue() {
        return ch2AECValue;
    }

    public void setCh2AECValue(double ch2aecValue) {
        ch2AECValue = ch2aecValue;
    }

    public double getCh3AECValue() {
        return ch3AECValue;
    }

    public void setCh3AECValue(double ch3aecValue) {
        ch3AECValue = ch3aecValue;
    }

    public double getCh4AECValue() {
        return ch4AECValue;
    }

    public void setCh4AECValue(double ch4aecValue) {
        ch4AECValue = ch4aecValue;
    }

    // ---------------- SC Values ----------------

    public double getCh1SCValue() {
        return ch1SCValue;
    }

    public void setCh1SCValue(double ch1scValue) {
        ch1SCValue = ch1scValue;
    }

    public double getCh2SCValue() {
        return ch2SCValue;
    }

    public void setCh2SCValue(double ch2scValue) {
        ch2SCValue = ch2scValue;
    }

    public double getCh3SCValue() {
        return ch3SCValue;
    }

    public void setCh3SCValue(double ch3scValue) {
        ch3SCValue = ch3scValue;
    }

    public double getCh4SCValue() {
        return ch4SCValue;
    }

    public void setCh4SCValue(double ch4scValue) {
        ch4SCValue = ch4scValue;
    }
}