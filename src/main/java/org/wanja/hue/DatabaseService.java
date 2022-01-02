package org.wanja.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;
import org.wanja.hue.remote.StateResponse;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class DatabaseService {

    @Inject
    LightService lightService;

    @POST
    @Transactional
    @Path("/bridge/init")
    public void updateDatabase() {
        List<Room> rooms = lightService.getAllRooms();
        Bridge bridge = new Bridge();
        bridge.name = "Obergeschoss";
        bridge.bridgeNumber = "1";
        bridge.baseURL = "http://192.168.2.126/api/";
        bridge.authToken = "4ZPmVyHoZGTDn2D32ad6AUOZzcoiXz6tqquzWbKa";
        bridge.persist();

        for (Room r : rooms) {
            r.bridge = bridge;
            for (Light l : r.allLights) {
                l.persist();
            }
            r.persist();
        }
    }

    @GET
    @Path("/bridge")
    public List<Bridge> allBridges() {
        return Bridge.findAll().list();
    }
}
