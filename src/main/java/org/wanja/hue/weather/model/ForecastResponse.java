package org.wanja.hue.weather.model;

import java.util.List;

public class ForecastResponse {
    public Float lat;
    public Float lon;
    public String timezone;
    public Integer timezoneOffset;
    public ForecastData current;
    public List<DayForecastData> daily;
    public List<AlertData> alerts;
}
