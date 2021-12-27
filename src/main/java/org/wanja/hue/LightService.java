package org.wanja.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;
import org.wanja.hue.remote.StateResponse;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class LightService {
    
    @Inject
    @RestClient
    HueLightsService hueService;

    @GET
    @Path("/lights")
    public List<Light> getAllLights() {
        List<Light> lights = new ArrayList<Light>();
        Map<String, Light> lightMap = hueService.getAllLights();

        Set<String> keys = lightMap.keySet();
        for(String key : keys ) {
            Light light = lightMap.get(key);
            light.id = key;
            lights.add(light);
        }
        return lights;
    }

    @GET
    @Path("/lights/{id}")
    public Light getLight(@PathParam String id) {
        return hueService.getLightById(id);
    }

    @PUT
    @Path("/lights/{id}/state")
    public StateResponse[] setLightState(@PathParam String id, State state) {
        
        return hueService.setLightState(id, state);

    }

    @GET
    @Path("/rooms")
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<Room>();
        Map<String, Room> roomMap = hueService.getAllGroups();
        Set<String> keys = roomMap.keySet();
        for(String key : keys) {
            Room room = roomMap.get(key);
            if(room.type.equals("Room")) {
                rooms.add(room);
                room.id = key;

                if( room.lights != null && room.lights.length > 0) {
                    for( String l : room.lights ){
                        Light light = hueService.getLightById(l);
                        light.id = l;
                        room.allLights.add(light);
                    }
                }
            }
        }
        return rooms;
    }

    @GET
    @Path("/rooms/q/{name}")
    public Room getRoomByName(@PathParam String name ) {
        List<Room> rooms = getAllRooms();
        for(Room r : rooms) {
            if( r.name.equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }

}
