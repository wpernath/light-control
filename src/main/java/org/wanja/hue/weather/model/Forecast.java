package org.wanja.hue.weather.model;

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
    
}
