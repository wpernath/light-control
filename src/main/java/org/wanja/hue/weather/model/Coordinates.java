package org.wanja.hue.weather.model;

import java.util.Map;

import javax.json.bind.annotation.JsonbProperty;

public class Coordinates {
    public String name;
    public String country;
    public String zip;

    @JsonbProperty(value = "local_names")
    public Map<String, String> localNames;

    public Float lat;
    public Float lon;

}
