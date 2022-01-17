package org.wanja.hue.render;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wanja.hue.PublicApiResource;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.Sensor;
import org.wanja.hue.remote.Light;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.logging.Log;

@Path("/ui")
@Produces(MediaType.TEXT_HTML)
public class PublicRenderResource {
    @Inject
    Template index;

    @Inject
    PublicApiResource api;

    private List<Sensor> allTemperatureSensors() throws Exception {
        List<Sensor> temperatur = api.allSensorsByType("ZLLTemperature");
        for(Sensor s : temperatur){
            s.state = api.sensorById(s.id).state;
        }
        return temperatur;
    }

    @GET
    @Path("/index")
    public TemplateInstance renderIndex() throws Exception {
        Log.infof("Rendering index page");
        List<Room> emptyRooms = api.allRooms();
        List<Light> allLights = new ArrayList<Light>();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for(Room r : emptyRooms ){
            rooms.add(api.roomByName(r.name));
            allLights.addAll(r.allLights);
        }

        TemplateInstance ti = index.data("state", "index");
        ti.data("rooms", rooms);
        ti.data("selectedRoom", null);
        ti.data("bridges", api.allBridges(true));
        ti.data("sensors", api.allSensors());
        ti.data("lights", allLights);
        ti.data("temperatureSensors", allTemperatureSensors());
        return ti;
    }

    @GET
    @Path("/room")
    public TemplateInstance renderRoom(@QueryParam("room") String name) throws Exception {
        Log.infof("Rendering room %s", name);
        List<Room> rooms = api.allRooms();
        Room selected = api.roomByName(name);
        int idx = rooms.indexOf(selected);

        if( rooms.contains(selected)) {
            rooms.remove(selected);
            rooms.add(idx, selected);
        }

        Log.infof("Selected Room %s contains Action info: %s", name, selected.action);
        TemplateInstance ti = index.data("state", "room");
        ti.data("rooms", rooms);
        ti.data("selectedRoom", selected);
        ti.data("temperatureSensors", allTemperatureSensors());
        return ti;
    }

    @GET
    @Path("/favorites")
    public TemplateInstance renderFavorites() throws Exception {
        Log.infof("Rendering Favorites");
        List<Room> emptyRooms = api.allRooms();
        List<Light> allLights = new ArrayList<Light>();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for(Room r : emptyRooms ){
            rooms.add(api.roomByName(r.name));
            allLights.addAll(r.allLights);
        }
        TemplateInstance ti = index.data("state", "favorites");
        ti.data("rooms", rooms);
        ti.data("allLights", allLights);
        ti.data("temperatureSensors", allTemperatureSensors());
        return ti;
    }

    @GET
    @Path("/all-rooms")
    public TemplateInstance renderAllRooms() throws Exception {
        Log.infof("Rendering all Rooms");
        List<Room> emptyRooms = api.allRooms();
        List<Light> allLights = new ArrayList<Light>();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for (Room r : emptyRooms) {
            rooms.add(api.roomByName(r.name));
            allLights.addAll(r.allLights);
        }
        TemplateInstance ti = index.data("state", "all-rooms");
        ti.data("rooms", rooms);
        ti.data("allLights", allLights);
        ti.data("temperatureSensors", allTemperatureSensors());
        return ti;
    }

    @GET
    @Path("/sensors")
    public TemplateInstance renderSensors() throws Exception {
        Log.infof("Rendering sensors");
        TemplateInstance ti = index.data("state", "sensors");
        ti.data("rooms", api.allRooms());
        ti.data("sensors", api.allSensors());
        ti.data("selectedRoom", null);
        ti.data("bridges", api.allBridges(true));
        ti.data("temperatureSensors", allTemperatureSensors());
        return ti;
    }
}
