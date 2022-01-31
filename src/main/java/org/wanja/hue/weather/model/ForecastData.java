package org.wanja.hue.weather.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

public class ForecastData {
    public Long dt;
    public Long sunrise;
    public Long sunset;
    public Float temp;

    @JsonbProperty("feels_like")
    public Float feelsLike;
    public Integer pressure;
    public Integer humidity;

    @JsonbProperty("dew_point")
    public Float dewPoint;
    public Float uvi;
    public Integer clouds;
    public Integer visibility;

    @JsonbProperty("wind_speed")
    public Float windSpeed;

    @JsonbProperty("wind_deg")
    public Float windDeg;
    public List<Weather> weather;

}
