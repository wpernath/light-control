package org.wanja.hue.remote;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;



public class SensorState  {
    public String lastupdated;
    public Long temperature;

    private Float realTemperature() {
        return ((float )temperature.longValue()) / 100;
    }

    public String printableTemperature() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String celsius = nf. format(realTemperature());
        return celsius;

    }
    public LocalDateTime lastUpdatedTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("pattern");
        return LocalDateTime.from(dtf.parse(lastupdated));
    }
}
