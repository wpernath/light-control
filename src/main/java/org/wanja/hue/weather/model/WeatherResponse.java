package org.wanja.hue.weather.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherResponse {

    public Coordinates coord;
    public List<Weather> weather;
    public String base;

    public Forecast main;
    public Integer visibility;
    public Wind wind;
    public Long dt;
    public WeatherSys sys;
    public Integer timezone;
    public Long id;
    public String name;
    public Integer cod;

    public String dtString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(dt * 1000));
    }
    
}
