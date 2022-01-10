package org.wanja.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;
import org.wanja.hue.remote.StateResponse;

import io.quarkus.logging.Log;

@Singleton
public class LightService {
    

    /**
     * Returns a list of all lights of a given bridge
     * 
     * @param hueService HueService to access
     * @return List of lights
     */
    public List<Light> getAllLights(HueLightsService hueService) {
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

    /**
     * Returns a light by ID
     * @param hueService Bridge to use
     * @param id hue id to return light
     * @return Light
     */
    public Light getLight(HueLightsService hueService, String id) {
        return hueService.getLightById(id);
    }

    /**
     * Sets the state of a light 
     * 
     * @param hueService Hue bridge to use
     * @param id hue id of the light to use
     * @param state State to provide
     * @return new state
     */
    public StateResponse[] setLightState(HueLightsService hueService, String id, State state) {
        return hueService.setLightState(id, state);
    }

    /**
     * Calls the corresponding HueService and returns a list of all rooms belonging to that brige
     * 
     * @param hueService HueService to call
     * @param bridge Bridge whose rooms to get
     * @return a list of all rooms
     */
    public List<Room> getAllRooms(HueLightsService hueService, Bridge bridge) {
        
        List<Room> rooms = new ArrayList<Room>();
        Map<String, Room> roomMap = hueService.getAllGroups();
        Set<String> keys = roomMap.keySet();
        for(String key : keys) {
            Room room = roomMap.get(key);
            if(room.type.equals("Room")) {
                rooms.add(room);
                room.number = key;
                room.bridge = bridge;
                if( room.lights != null && room.lights.length > 0) {
                    room.allLights = new ArrayList<Light>();
                    for( String l : room.lights ){
                        Light light = hueService.getLightById(l);
                        light.number = l;
                        light.roomNumber = key;
                        light.bridgeNumber = room.bridge.bridgeNumber;
                        room.allLights.add(light);
                    }
                }
            }
        }
        return rooms;
    }

    public Room getRoomScene(HueLightsService hueService, Room room) {
        Room bridgeRoom = hueService.getRoomById(room.number);
        room.action = bridgeRoom.action;
        for( Light light : room.allLights ) {
            Light l = getLight(hueService, light.number);
            light.state = l.state;
        }
        return room;
    }

    /**
     * Sets the lights of a complete room
     * @param hueService Bridge to use
     * @param id Hue ID of the room
     * @param action action to do
     */
    public void setRoomScene(HueLightsService hueService, String id, Action action) {
        hueService.setGroupAction(id, action);
    }
}
