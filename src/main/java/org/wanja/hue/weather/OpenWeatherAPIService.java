package org.wanja.hue.weather;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.wanja.hue.weather.model.ForecastResponse;
import org.wanja.hue.weather.model.WeatherResponse;

@RegisterRestClient(configKey = "weather-api")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public interface OpenWeatherAPIService {
    
    @GET
    @Path("/weather")
    public WeatherResponse weatherByCity(@QueryParam("q") String query, @QueryParam("appid") String appId, @QueryParam("units") String units, @QueryParam("lang") String lang);

    @GET
    @Path("/onecall")
    public ForecastResponse onecall(@QueryParam Float lat, @QueryParam Float lon, @QueryParam("appid") String appId, @QueryParam String exclude, @QueryParam String units, @QueryParam String lang);
}
