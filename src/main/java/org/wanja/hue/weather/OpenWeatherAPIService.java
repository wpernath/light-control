package org.wanja.hue.weather;

import java.util.List;

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.wanja.hue.weather.model.Coordinates;
import org.wanja.hue.weather.model.ForecastResponse;
import org.wanja.hue.weather.model.WeatherResponse;

@RegisterRestClient(configKey = "weather-api")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public interface OpenWeatherAPIService {
    
    @GET
    @Path("/data/2.5/weather")
    public WeatherResponse weatherByCity(@QueryParam("q") String query, @QueryParam("appid") String appId, @QueryParam("units") String units, @QueryParam("lang") String lang);

    @GET
    @Path("/data/2.5/onecall")
    public ForecastResponse onecall(@QueryParam Float lat, @QueryParam Float lon, @QueryParam("appid") String appId, @QueryParam String exclude, @QueryParam String units, @QueryParam String lang);

    // calls for geocoding
    @GET
    @Path("/geo/1.0/direct")
    public List<Coordinates> directGeocode(@QueryParam("q") String query, @QueryParam Integer limit, @QueryParam("appid") String appId);

    @GET
    @Path("/geo/1.0/zip")
    public Coordinates geocodeByZip(@QueryParam String zip, @QueryParam("appid") String appId);

    @GET
    @Path("/geo/1.0/reverse")
    public List<Coordinates> reverseGeocode(@QueryParam Float lat, @QueryParam Float lon, @QueryParam Integer limit, @QueryParam("appid") String appId);
}
