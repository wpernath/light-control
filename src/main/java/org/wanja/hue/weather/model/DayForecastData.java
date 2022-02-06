package org.wanja.hue.weather.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


    public String moonriseString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(moonrise * 1000));
    }

    public String moonsetString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(moonset * 1000));
    }

    public String sunriseString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(sunrise * 1000));
    }

    public String sunsetString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(sunset * 1000));
    }

    public String dtString() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(dt * 1000));
    }

}
