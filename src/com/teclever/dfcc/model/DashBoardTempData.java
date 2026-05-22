package com.teclever.dfcc.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DashBoardTempData {

    private final StringProperty channel = new SimpleStringProperty();

    private final DoubleProperty ch1 = new SimpleDoubleProperty();
    private final DoubleProperty ch2 = new SimpleDoubleProperty();
    private final DoubleProperty ch3 = new SimpleDoubleProperty();
    private final DoubleProperty ch4 = new SimpleDoubleProperty();

    private final DoubleProperty lowSC = new SimpleDoubleProperty();
    private final DoubleProperty highSC = new SimpleDoubleProperty();

    private final DoubleProperty lowAEC = new SimpleDoubleProperty();
    private final DoubleProperty highAEC = new SimpleDoubleProperty();

    // CHANNEL
    public String getChannel() { return channel.get(); }
    public void setChannel(String v) { channel.set(v); }
    public StringProperty channelProperty() { return channel; }

    // CH1
    public double getCh1() { return ch1.get(); }
    public void setCh1(double v) { ch1.set(v); }
    public DoubleProperty ch1Property() { return ch1; }

    // CH2
    public double getCh2() { return ch2.get(); }
    public void setCh2(double v) { ch2.set(v); }
    public DoubleProperty ch2Property() { return ch2; }

    // CH3
    public double getCh3() { return ch3.get(); }
    public void setCh3(double v) { ch3.set(v); }
    public DoubleProperty ch3Property() { return ch3; }

    // CH4
    public double getCh4() { return ch4.get(); }
    public void setCh4(double v) { ch4.set(v); }
    public DoubleProperty ch4Property() { return ch4; }

    // LOW SC
    public double getLowSC() { return lowSC.get(); }
    public void setLowSC(double v) { lowSC.set(v); }
    public DoubleProperty lowSCProperty() { return lowSC; }

    // HIGH SC
    public double getHighSC() { return highSC.get(); }
    public void setHighSC(double v) { highSC.set(v); }
    public DoubleProperty highSCProperty() { return highSC; }

    // LOW AEC
    public double getLowAEC() { return lowAEC.get(); }
    public void setLowAEC(double v) { lowAEC.set(v); }
    public DoubleProperty lowAECProperty() { return lowAEC; }

    // HIGH AEC
    public double getHighAEC() { return highAEC.get(); }
    public void setHighAEC(double v) { highAEC.set(v); }
    public DoubleProperty highAECProperty() { return highAEC; }
}
