package org.wanja.hue.weather.model;

import java.util.List;

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

    
}
