package org.wanja.hue.weather;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.wanja.hue.weather.model.CityResource;
import org.wanja.hue.weather.model.Coordinates;
import org.wanja.hue.weather.model.ForecastResponse;
import org.wanja.hue.weather.model.GeocodeResponse;
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
    LoadingCache<CityResource, Coordinates> geomapCache;

    public PublicWeatherAPIService() {
        Log.info("PublicWeatherAPIService() created");

        weatherCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(k -> loadWeatherData(k));

        forecastCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build(k -> loadForecastData(k));

        geomapCache = Caffeine.newBuilder()
                .build(k -> loadGeomapData(k));
    }

    /**
     * Calls the geocode api and returns lat/lon of a city resource
     * @param city city whoose lat/lon coordinates should be returned
     * @return Coordinates
     */
    private Coordinates loadGeomapData(CityResource city) {
        List<Coordinates> gr = service.directGeocode(
            new StringBuilder().append(city.city).append(",").append(city.country).toString(), 
            1, 
            weatherAPIKey
        );

        if( gr != null && gr.size() > 0 ) 
            return gr.get(0);
        else 
            return null;
    }

    /**
     * Calls the geocode API and returns lat/lon of a city resource
     * @param city
     * @return
     */
    private Coordinates loadGeomapByZipData(CityResource city) {
        return service.geocodeByZip(
                new StringBuilder().append(city.zip).append(",").append(city.country).toString(),
                weatherAPIKey
        );
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
    @Path("geocode")
    public Coordinates coordinatesByCity(CityResource city) {
        Log.infof("coordinatesByCity(%s, %s, %s)", city.city, city.zip, city.country);
        if( city.city == null || city.country == null ) throw new WebApplicationException("This call needs a city and a country code");
        return geomapCache.get(city, k -> loadGeomapData(k));
    }

    @GET
    @Path("geocode-zip")
    public Coordinates coordinatesByZip(CityResource city) {
        Log.infof("coordinatesByCity(%s, %s, %s)", city.city, city.zip, city.country);
        return loadGeomapByZipData(city);
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

        Coordinates wr = geomapCache.get(city, k->loadGeomapData(city));

        if( wr != null ) {
            return service.onecall(
                    wr.lat, 
                    wr.lon, 
                    weatherAPIKey, 
                    "minutely,hourly", 
                    "metric", 
                    "de"
            );
        }
        return null;
    }


}
