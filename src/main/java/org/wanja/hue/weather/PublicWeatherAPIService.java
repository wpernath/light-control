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
import org.wanja.hue.weather.model.Coordinates;
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

    @ConfigProperty(name="weather.default.city")
    String defaultCity;

    @ConfigProperty(name="weather.default.zip")
    String defaultZip;

    @ConfigProperty(name="weather.default.country")
    String defaultCountry;


    @Inject
    @RestClient
    OpenWeatherAPIService service;

    LoadingCache<CityResource, WeatherResponse> weatherCache;
    LoadingCache<Coordinates, ForecastResponse> forecastCache;

    public PublicWeatherAPIService() {
        Log.info("PublicWeatherAPIService() created");

        weatherCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(k -> loadWeatherData(k));

        forecastCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build(k -> loadForecastData(k));

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

    /**
     * Private method to be used to fill the cache
     * @param coors coordinates of place to be used
     * @return weather forecast response
     */
    private ForecastResponse loadForecastData(Coordinates coors) {
        return service.onecall(coors.lat, coors.lon, weatherAPIKey, "minutely,hourly", "metric", "de");
    }

    @GET
    @Path("city")
    public WeatherResponse weatherByCity(CityResource city) {
        Log.infof("weatherByCity(%s, %s, %s)", city.city, city.zip, city.country);
        return weatherCache.get(city, k->loadWeatherData(k));
    }

    @GET
    @Path("default-city")
    public WeatherResponse weatherByCity() {
        CityResource city=new CityResource(defaultCity, defaultZip, defaultCountry);
        Log.infof("weatherByCity(%s, %s, %s)", city.city, city.zip, city.country);
        return weatherByCity(city);
    }

    @GET
    @Path("default-forecast")
    public ForecastResponse weatherForecast() {
        CityResource city=new CityResource(defaultCity, defaultZip, defaultCountry);
        Log.infof("weatherForecast(%s, %s, %s)", city.city, city.zip, city.country);
        return weatherForecast(city);
    }

    @GET
    @Path("forecast")
    public ForecastResponse weatherForecast(CityResource city) {
        Log.infof("weatherForecast(%s, %s, %s)", city.city, city.zip, city.country);

        WeatherResponse wr = weatherCache.get(city, k->loadWeatherData(city));

        if( wr != null ) {
            return service.onecall(
                    wr.coord.lat, 
                    wr.coord.lon, 
                    weatherAPIKey, 
                    "minutely,hourly", 
                    "metric", 
                    "de"
            );
        }
        return null;
    }


}
