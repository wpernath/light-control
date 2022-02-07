package org.wanja.hue.weather.model;

import java.text.NumberFormat;

import javax.json.bind.annotation.JsonbProperty;

public class Forecast {
    public Float temp;

    @JsonbProperty("feels_like")
    public Float feelsLike;

    @JsonbProperty("temp_min")
    public Float tempMin;

    @JsonbProperty("temp_max")
    public Float tempMax;
    public Integer pressure;
    public Integer humidity;

    @JsonbProperty("sea_level")
    public Integer seaLevel;

    @JsonbProperty("grnd_level")
    public Integer grndLevel;
 
    public String tempString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(temp);
    }

    public String tempMinString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(tempMin);
    }

    public String tempMaxString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(tempMax);
    }

    public String tempFeelsLikeString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(feelsLike);
    }

}
