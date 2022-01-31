package org.wanja.hue.remote;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;

import io.quarkus.logging.Log;

public class CachedHueBridgeService implements HueLightsService {
    Bridge bridge;
    HueLightsService service;
    

    LoadingCache<String, Light> lightCache;
    LoadingCache<String, Room>  roomCache;
    LoadingCache<Long, Sensor> sensorCache;

    public CachedHueBridgeService(Bridge bridge) throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
        this.bridge = bridge;

        Log.infof("Initializing Cached Bridge API on %s", bridge.name);
        this.service = RestClientBuilder.newBuilder()
                .baseUrl(new URL(bridge.baseURL + bridge.authToken))
                .build(HueLightsService.class);

        // create Caches
        lightCache = Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                //.weakKeys()
                //.weakValues()
                .build(k -> service.getLightById(k));

        sensorCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                //.weakKeys()
                //.weakValues()
                .build(k -> service.sensorById(k));

        roomCache   = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                //.weakKeys()
                //.weakValues()
                .build(k -> service.getRoomById(k));
    }

    @Override
    public Bridge getBridgeInfo() {
        Log.infof("Getting Bridge info of %s", bridge.name);
        return service.getBridgeInfo();
    }

    @Override
    public Light getLightById(String id) {
        Log.infof("Getting light %s of bridge %s", id, bridge.name);
        return lightCache.get(id, k->service.getLightById(id));
    }

    @Override
    public Map<String, Light> getAllLights() {        
        Log.infof("Getting all lights of bridge %s", bridge.name);
        if( lightCache.asMap().isEmpty() ) {
            Log.infof("Lights are empty.");
            Map<String, Light> lightMap = service.getAllLights();
            for( String id : lightMap.keySet() ) {
                lightCache.put(id, lightMap.get(id));
            }
        }
        return lightCache.asMap();
    }

    @Override
    public Room getRoomById(String id) {
        Log.infof("Getting room %s of bridge %s", id, bridge.name);
        return roomCache.get(id, k-> service.getRoomById(k));
    }

    @Override
    public Map<String, Room> getAllGroups() {
        Log.infof("Getting all rooms of bridge %s", bridge.name);
        if( roomCache.asMap().isEmpty() ){
            Log.infof("Room list is empty");
            Map<String, Room> roomMap = service.getAllGroups();
            for( String id : roomMap.keySet() ){
                roomCache.put(id, roomMap.get(id));
            }
        }
        return roomCache.asMap();
    }

    @Override
    public StateResponse[] setLightState(String id, LightState state) {
        Log.infof("Setting light state of %s on bridge %s to state %s", id, bridge.name, state);
        Light l = getLightById(id);
        if( state.on != null ) l.state.on = state.on;
        if( state.bri != null ) l.state.bri = state.bri;
        if( state.hue != null ) l.state.hue = state.hue;
        if( state.sat != null ) l.state.sat = state.sat;
        return service.setLightState(id, state);
    }

    @Override
    public void setGroupAction(String id, Action action) {
        Log.infof("Setting room state of %s on bridge %s to state %s", id, bridge.name, action);
        Room r = getRoomById(id);
        if( action.on != null ) r.action.on = action.on;
        if( action.bri != null) r.action.bri = action.bri;
        if( action.hue != null) r.action.hue = action.hue;
        if( action.sat != null ) r.action.sat = action.sat;
        service.setGroupAction(id, action);

        // Don't forget to update the light states in our cache
        for( String lid : r.lights ) {
            Light l = getLightById(lid);
            if (action.on != null)
                l.state.on = action.on;
        }
    }

    @Override
    public Map<Long, Sensor> allSensors() {
        Log.infof("Reading all sensors from bridge %s", bridge.name);
        if( sensorCache.asMap().isEmpty() ) {
            Map<Long, Sensor> sensorMap = service.allSensors();
            for(Long id: sensorMap.keySet()) {
                sensorCache.put(id, sensorMap.get(id));
            }
        }
        return sensorCache.asMap();
    }

    @Override
    public Sensor sensorById(Long id) {
        Log.infof("reading sensor %d of bridge %s", id, bridge.name);
        return sensorCache.get(id, k->service.sensorById(id));
    }

    
}
