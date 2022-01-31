package org.wanja.hue.weather;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.wanja.hue.weather.model.CityResource;
import org.wanja.hue.weather.model.ForecastResponse;
import org.wanja.hue.weather.model.WeatherResponse;

import io.quarkus.logging.Log;

@Path("/api/weather")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PublicWeatherAPIService {
    
    @ConfigProperty(name = "weather.api-key")
    String weatherAPIKey;

    @Inject
    @RestClient
    OpenWeatherAPIService service;

    LoadingCache<CityResource, WeatherResponse> cache;

    public PublicWeatherAPIService() {
        Log.info("PublicWeatherAPIService() created");

        cache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(k -> loadWeatherData(k));
    }

    /**
     * Private method to be used to fill the cache
     * 
     * @param city weather data of city identified by cityrespource
     * @return weather response data
     */
    private WeatherResponse loadWeatherData(CityResource city) {
        return service.weatherByCity(city.queryString(), weatherAPIKey, "metric", "de");
    }

    @GET
    @Path("city")
    public WeatherResponse weatherByCity(CityResource city) {
        Log.infof("weatherByCity(%s, %s, %s)", city.city, city.zip, city.country);
        return cache.get(city, k->loadWeatherData(k));
    }

    @GET
    @Path("forecast")
    public ForecastResponse weatherForecast(CityResource city) {
        Log.infof("weatherForecast(%s, %s, %s)", city.city, city.zip, city.country);

        WeatherResponse wr = cache.get(city, k->loadWeatherData(city));

        if( wr != null ) {
            return service.onecall(
                    wr.coord.lat, 
                    wr.coord.lon, 
                    weatherAPIKey, 
                    "hourly,minutely", 
                    "metric", 
                    "de"
            );
        }
        return null;
    }
}
