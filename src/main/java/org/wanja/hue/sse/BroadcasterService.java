package org.wanja.hue.sse;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;

@ApplicationScoped
public class BroadcasterService {
    @Inject
    SSELightsResource lightsResource;

    @Inject
    SSERoomResource roomResource;

    public void broadcastLightChanges(Light l) {
        List<Light> lights = new ArrayList<>();
        lights.add(l);
        lightsResource.broadcast(lights);
    }

    public void broadcastLightsChanges(List<Light> lights) {
        lightsResource.broadcast(lights);
    }

    public void broadcastRoomChanges(List<Room> rooms) {
        roomResource.broadcast(rooms);
    }

    public void broadcastRoomChanges(Room room) {
        List<Room> rooms = new ArrayList<Room>();
        rooms.add(room);
        roomResource.broadcast(rooms);
    }


}
