package org.wanja.hue.weather.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(moonrise * 1000));
    }

    public String moonsetString() {
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(moonset * 1000));
    }

    public String sunriseString() {
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(sunrise * 1000));
    }

    public String sunsetString() {
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(sunset * 1000));
    }

    public String dtString() {
        return new SimpleDateFormat("dd.MM.", Locale.GERMANY).format(new Date(dt*1000));
    }

    // returns day of the week
    public String dayDtString() {
        return new SimpleDateFormat("EE", Locale.GERMANY).format(new Date(dt*1000));
    }

    // returns today / tomorrow or then the date like 22.02.
    public String extDtString() {
        long todayInc = System.currentTimeMillis();
        long today    = todayInc / (1000*60*60*24);
        long tomorrow = today + (1);
        long dtInc    = dt / (60*60*24);
        if( dtInc <= today) return "Today";
        else if( dtInc == tomorrow) return "Tomorrow";
        else if( dtInc > tomorrow) return dtString();
        else return "today= " + today + " / tomorrow=" + tomorrow + " / dt=" + dtInc;
    }

}
