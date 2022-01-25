package org.wanja.hue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.wanja.hue.config.HueBridgeConfig;
import org.wanja.hue.config.HueBridgeConfig.HueBridge;
import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.Sensor;
import org.wanja.hue.remote.SensorConfig;
import org.wanja.hue.remote.LightState;

import io.quarkus.panache.common.Sort;
import io.quarkus.logging.Log;

/**
 * 
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class PublicApiResource {

    @Inject
    HueBridgeService lightService;

    @Inject
    HueBridgeConfig bridgeConfig;

    Map<Long, HueLightsService> cachedBridges = new HashMap<Long, HueLightsService>();

    HueLightsService hueServiceByBridge(Bridge b) throws IllegalStateException, RestClientDefinitionException, MalformedURLException  {
        HueLightsService service = cachedBridges.get(b.id);

        if( service == null ) {
            
            service = RestClientBuilder.newBuilder()
                    .baseUrl(new URL(b.baseURL + b.authToken))
                    .build(HueLightsService.class);
            cachedBridges.put(b.id, service);
        }
        return service;
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
        cachedBridges.clear();
        List<Bridge> bridges = allBridges(false);
        List<Room> rooms = allRooms();
        List<Sensor> sensors = allSensors();
        
        Log.infof("Deleting %d rooms", rooms.size());
        for( Room r : rooms ) {
            Log.infof("Deleting Room %s", r.name);
            r.delete();
            for( Light l : r.allLights ) {
                Log.infof("Deleting Light %s", l.name);
                l.delete();
            }
        }

        Log.infof("Deleting %d sensors", sensors.size());
        for( Sensor s : sensors) {
            Log.infof("Deleting Sensor %s", s.name);
            s.delete();
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
        long sensorNum = 0;

        if( !allBridges(false).isEmpty()) {
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
            HueLightsService hueService = hueServiceByBridge(b);

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
                    l.config.persist();
                    l.persist();
                }
            }

            Log.infof("Scanning sensors on bridge %s", b.name);
            List<Sensor> sensors = lightService.allSensors(hueService);
            for( Sensor s : sensors) {
                s.bridge = b;
                Log.infof("Creating Sensor %s of Type %s on Bridge %s", s.name, s.type, b.name);
                s.config.persist();
                s.bridgeId = b.id;
                s.bridgeNumber = b.bridgeNumber;
                s.persist();
                sensorNum++;
            }
        }
        Log.infof("Initialized Database with %d Bridges, %d Rooms, %d Sensors and %d Lights.", bridgeNum, roomNum, sensorNum, lightNum);
    }

    @GET
    @Path("/bridge")
    public List<Bridge> allBridges(@QueryParam("full") Boolean full ) throws Exception {
        List<Bridge> bridges =  Bridge.findAll(Sort.by("name").ascending()).list();
        if( full != null && full.booleanValue()) {
            for( Bridge b : bridges ) {
                HueLightsService service = hueServiceByBridge(b);
                Bridge bridgeInfo   = service.getBridgeInfo();
                b.apiversion        = bridgeInfo.apiversion;
                b.bridgeid          = bridgeInfo.bridgeid;
                b.datastoreversion  = bridgeInfo.datastoreversion;
                b.mac               = bridgeInfo.mac;
                b.modelid           = bridgeInfo.modelid;
                b.swversion         = bridgeInfo.swversion;
            }
        }
        return bridges;
    }


    //@GET 
    //@Path("/sensors")
    public List<Sensor> allSensors() {
        List<Sensor> sensors = Sensor.findAll(Sort.by("bridgeNumber").ascending()).list();
        return sensors;
    }

    @GET
    @Path("/sensors") 
    public List<Sensor> allSensorsByType(@QueryParam String type) {
        if( type == null ) return allSensors();
        else {
            List<Sensor> sensors = Sensor.find("type", Sort.by("name"), type).list();
            return sensors;
        }
    }
    
    @GET
    @Path("/sensors/{id}")
    public Sensor sensorById(@PathParam Long id) throws Exception {
        Sensor sensor   = Sensor.findById(id);
        Sensor full     = lightService.findSensorById(hueServiceByBridge(sensor.bridge), sensor.sensorNumber);
        sensor.config   = full.config;
        sensor.state    = full.state;
        return sensor;
    }

    
    @PUT
    @Path("/sensors/{id}")
    @Transactional
    public void updateSensorById(@PathParam Long id, SensorConfig state) throws Exception {
        Log.infof("Updating Sensor %d with new name %s", id, state.newName);
        Sensor sensor = Sensor.findById(id);
        sensor.name   = state.newName;
        if( state.isFavorite != null) {
            sensor.isFavorite = state.isFavorite;
        }
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
    @Path("/rooms/classes")
    public List<Room> allClasses() {
        List<Room> rooms = allRooms();
        for( Room r : rooms ){
            r.action = null;
            r.bridge = null;
            r.allLights = null;
            r.lights = null;
        }
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

        // iterate through list of lights to see if ONE is on to turn on the complete room
        // should be coming from Room.action from Hue... but it doesn't
        room.action.on=false;
        for( Light l : room.allLights){
            if( l.state.on ){
                room.action.on=true;
                break;
            }
        }
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
    public void setLightStateById(@QueryParam("id") String lightId, LightState state) throws Exception {
        Light light = Light.findById(Long.parseLong(lightId));
        if( light == null ) throw new WebApplicationException("No Light with id " + lightId + " found!");
        Room room = Room.findById(light.roomId);
        Bridge bridge = room.bridge;
        HueLightsService service = hueServiceByBridge(bridge);

        lightService.setLightState(service, light.number, state);
    }

    @GET
    @Path("/lights/toggle")
    @Consumes(MediaType.TEXT_PLAIN)
    public void toggleLightById(@QueryParam Long id, @QueryParam Boolean on, @QueryParam Integer bri) throws MalformedURLException {
        Light light = Light.findById(id);
        if (light == null)
            throw new WebApplicationException("No Light with id " + id + " found!");
        Room room = Room.findById(light.roomId);
        Bridge bridge = room.bridge;
        HueLightsService service = hueServiceByBridge(bridge);

        LightState state = new LightState();
        state.on = on;
        state.bri = bri;
        lightService.setLightState(service, light.number, state);
    }

    @GET
    @Path("/rooms/toggle")
    @Consumes(MediaType.TEXT_PLAIN)
    public void toggleRoomById(@QueryParam Long id, @QueryParam Boolean on, @QueryParam Integer bri)
            throws MalformedURLException {
        Room room = Room.findById(id);
        if (room == null)
            throw new WebApplicationException("No Room with id " + id + " found!");
        Bridge bridge = room.bridge;
        HueLightsService service = hueServiceByBridge(bridge);

        Action state = new Action();
        state.on = on;
        state.bri = bri;
        lightService.setRoomScene(service, room.number, state);
    }


}
