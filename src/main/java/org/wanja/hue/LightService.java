package org.wanja.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;
import org.wanja.hue.remote.StateResponse;

@Singleton
public class LightService {
    

    @Inject
    @RestClient
    HueLightsService hueService;

    public List<Light> getAllLights() {
        List<Light> lights = new ArrayList<Light>();
        Map<String, Light> lightMap = hueService.getAllLights();

        Set<String> keys = lightMap.keySet();
        for(String key : keys ) {
            Light light = lightMap.get(key);
            light.number = key;
            lights.add(light);
        }
        return lights;
    }

    public Light getLight(String id) {
        return hueService.getLightById(id);
    }

    public StateResponse[] setLightState(String id, State state) {
        return hueService.setLightState(id, state);
    }

    public List<Room> getAllRooms() {
        
        List<Room> rooms = new ArrayList<Room>();
        Map<String, Room> roomMap = hueService.getAllGroups();
        Set<String> keys = roomMap.keySet();
        for(String key : keys) {
            Room room = roomMap.get(key);
            if(room.type.equals("Room")) {
                rooms.add(room);
                room.number = key;
                if( room.lights != null && room.lights.length > 0) {
                    for( String l : room.lights ){
                        Light light = hueService.getLightById(l);
                        light.number = l;
                        light.roomNumber = key;
                        room.allLights.add(light);
                    }
                }
            }
        }
        return rooms;
    }

    public Room getRoomByName(String name ) {
        List<Room> rooms = getAllRooms();
        for(Room r : rooms) {
            if( r.name.equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }

    public void setRoomScene(String id, Action action) {
        hueService.setGroupAction(id, action);
    }
}
