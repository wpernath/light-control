package org.wanja.hue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.wanja.hue.config.HueBridgeConfig;
import org.wanja.hue.config.HueBridgeConfig.HueBridge;
import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;

import io.quarkus.panache.common.Sort;
import io.quarkus.logging.Log;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class PublicApiResource {

    @Inject
    LightService lightService;

    @Inject
    HueBridgeConfig bridgeConfig;


    HueLightsService hueServiceByBridge(Bridge b) throws MalformedURLException {
        return RestClientBuilder.newBuilder()
                    .baseUrl(new URL(b.baseURL + b.authToken))
                    .build(HueLightsService.class);
    }

    /**
     * Deletes all bridges, lights and rooms from the database
     * 
     * @throws Exception
     */
    @DELETE
    @Transactional
    @Path("/bridge/init")
    public void deleteDatabase() throws Exception {
        Log.info("Deleting Database...");
        List<Bridge> bridges = allBridges();
        List<Room> rooms = allRooms();

        Log.infof("Deleting %d rooms", rooms.size());
        for( Room r : rooms ) {
            Log.infof("Deleting Room %s", r.name);
            r.delete();
            for( Light l : r.allLights ) {
                Log.infof("Deleting Light %s", l.name);
                l.delete();
            }
        }

        Log.infof("Deleting %d bridges", bridges.size());
        for(Bridge b : bridges ) {
            Log.infof("Deleting Bridge %s", b.name);
            b.delete();
        }
    }

    /**
     * Initializes the Database with all rooms and lights and all bridges
     * 
     * @throws Exception
     */
    @POST
    @Transactional
    @Path("/bridge/init")
    public void updateDatabase() throws Exception {
        List<Bridge> bridges = new ArrayList<Bridge>();
        Set<HueBridge> configBridges = bridgeConfig.bridges();
        int bridgeNum = 0;
        long roomNum = 0;
        long lightNum = 0;

        if( !allBridges().isEmpty()) {
            Log.infof("This Database is already initialized. Call DELETE first, if you want to reinitialize the DB!");
            return;
        }
        Log.info("Initializing Database...");
        
        for(HueBridge b : configBridges ) {
            Bridge bridge       = new Bridge();
            bridge.authToken    = b.authToken();
            bridge.baseURL      = b.baseURL();
            bridge.name         = b.name();
            bridge.bridgeNumber = String.valueOf(bridgeNum);
            bridges.add(bridge);
            bridge.persist();
            bridgeNum++;

            Log.infof("Creating Bridge %s recieving input at %s", b.name(), bridge.baseURL);
        }

        for( Bridge b : bridges) {
            Log.infof("Scanning Bridge %s",  b.name);
            HueLightsService hueService;
            hueService = hueServiceByBridge(b);

            List<Room> rooms = lightService.getAllRooms(hueService, b);

            for (Room r : rooms) {
                roomNum++;
                Log.infof("Creating Room %s on Bridge %s", r.name, b.name);
                r.bridge = b;
                r.persist();
                for (Light l : r.allLights) {
                    lightNum++;
                    Log.infof("Creating Light %s in Room %s", l.name, r.name);
                    l.roomId = r.id;
                    l.persist();
                }
            }
        }
        Log.infof("Initialized Database with %d Bridges, %d Rooms and %d Lights.", bridgeNum, roomNum, lightNum);
    }

    @GET
    @Path("/bridge")
    public List<Bridge> allBridges() {
        return Bridge.findAll(Sort.by("bridgeNumber").ascending()).list();
    }

    @GET
    @Path("/rooms")
    public List<Room> allRooms() {
        Log.infof("Getting a list of all Rooms");
        List<Room> rooms = Room.findAll(Sort.by("name").ascending()).list();
        Log.infof("  Got %d rooms", rooms.size());
        return rooms;
    }

    @GET
    @Path("/rooms/q")
    public Room roomByName(@QueryParam("rn") String roomName) throws Exception {
        Room room = Room.find("name", roomName).firstResult();
        if (room == null)
            throw new WebApplicationException("No room named '" + roomName + "' found!");

        Log.infof("Room '%s' found. Populating...", roomName);
        HueLightsService service = hueServiceByBridge(room.bridge);
        room = lightService.getRoomScene(service, room);

        return room;
    }

    @PUT
    @Path("/rooms/q")
    public void setRoomByName(@QueryParam("rn") String roomName, Action action) throws Exception {
        Room room = Room.find("name", roomName).firstResult();
        if (room == null)
            throw new WebApplicationException("No room named '" + roomName + "' found!");

        HueLightsService service = hueServiceByBridge(room.bridge);
        lightService.setRoomScene(service, room.number, action);
    }
    
    @GET
    @Path("/lights/q")
    public List<Light> lightsByRoomName(@QueryParam("rn") String roomName) throws Exception {
        Room room = roomByName(roomName);
        List<Light> lights = room.allLights;
        return lights;
    }

    @PUT
    @Path("/lights/q")
    public void setLightStateById(@QueryParam("id") String lightId, State state) throws Exception {
        Light light = Light.findById(Long.parseLong(lightId));
        if( light == null ) throw new WebApplicationException("No Light with id " + lightId + " found!");
        Room room = Room.findById(light.roomId);
        Bridge bridge = room.bridge;
        HueLightsService service = hueServiceByBridge(bridge);

        lightService.setLightState(service, light.number, state);
        
    }
}
