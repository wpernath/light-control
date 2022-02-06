package org.wanja.hue.weather.model;

import java.text.NumberFormat;

import javax.json.bind.annotation.JsonbProperty;

public class TemperatureForecast {
    public Float day;
    public Float min;
    public Float max;
    public Float night;
    
    @JsonbProperty("eve")
    public Float evening;

    @JsonbProperty("morn")
    public Float morning;

    public String dayString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(day);
    }

    public String nightString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(night);
    }

}
