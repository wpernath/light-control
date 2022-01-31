package org.wanja.hue.weather.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

public class DayForecastData {
    public Long dt;
    public Long sunrise;
    public Long sunset;
    public Long moonrise;
    public Long moonset;

    @JsonbProperty("moon_phase")
    public Float moonPhase;

    public TemperatureForecast temp;

    @JsonbProperty("feels_like")
    public TemperatureForecast feelsLike;

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
