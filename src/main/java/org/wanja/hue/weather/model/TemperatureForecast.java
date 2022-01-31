package org.wanja.hue.weather.model;

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
}
