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

import io.quarkus.panache.common.Sort;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class DatabaseService {

    @Inject
    LightService lightService;

    @Inject
    HueBridgeConfig bridgeConfig;

    @GET
    @Path("/test")
    public String test() {
        return bridgeConfig.paul();
    }

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
        List<Bridge> bridges = allBridges();
        List<Room> rooms = allRooms();

        for( Room r : rooms ) {
            r.delete();
            for( Light l : r.allLights ) {
                l.delete();
            }
        }

        for(Bridge b : bridges ) {
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
        for(HueBridge b : configBridges ) {
            Bridge bridge       = new Bridge();
            bridge.authToken    = b.authToken();
            bridge.baseURL      = b.baseURL();
            bridge.name         = b.name();
            bridge.bridgeNumber = String.valueOf(bridgeNum);
            bridges.add(bridge);
            bridge.persist();
            bridgeNum++;
        }

        for( Bridge b : bridges) {
            System.out.println("Scanning " + b.name);
            HueLightsService hueService;
            hueService = hueServiceByBridge(b);

            List<Room> rooms = lightService.getAllRooms(hueService, b);

            for (Room r : rooms) {
                r.bridge = b;
                r.persist();
                for (Light l : r.allLights) {
                    l.roomId = r.id;
                    l.persist();
                }
            }
        }
    }

    @GET
    @Path("/bridge")
    public List<Bridge> allBridges() {
        return Bridge.findAll(Sort.by("bridgeNumber").ascending()).list();
    }

    @GET
    @Path("/rooms")
    public List<Room> allRooms() {
        List<Room> rooms = Room.findAll(Sort.by("name").ascending()).list();
        return rooms;
    }

    @GET
    @Path("/rooms/q")
    public Room roomByName(@QueryParam("rn") String roomName) throws Exception {
        Room room = Room.find("name", roomName).firstResult();
        if (room == null)
            throw new WebApplicationException("No room named '" + roomName + "' found!");

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


}
