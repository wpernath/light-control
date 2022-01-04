package org.wanja.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.URL;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.wanja.hue.remote.Action;
import org.wanja.hue.remote.Bridge;
import org.wanja.hue.remote.HueLightsService;
import org.wanja.hue.remote.Light;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.State;
import org.wanja.hue.remote.StateResponse;

import io.quarkus.panache.common.Sort;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class DatabaseService {

    @Inject
    LightService lightService;

    @POST
    @Transactional
    @Path("/bridge/init")
    public void updateDatabase() throws Exception {
        List<Bridge> bridges = new ArrayList<Bridge>();
        Bridge bridgeOben = new Bridge();
        bridgeOben.name = "Obergeschoss";
        bridgeOben.bridgeNumber = "1";
        bridgeOben.baseURL = "http://192.168.2.126/api/";
        bridgeOben.authToken = "4ZPmVyHoZGTDn2D32ad6AUOZzcoiXz6tqquzWbKa";
        bridgeOben.persist();
        bridges.add(bridgeOben);

        Bridge bridgeUnten = new Bridge();
        bridgeUnten.name = "Erdgeschoss";
        bridgeUnten.bridgeNumber = "2";
        bridgeUnten.baseURL = "http://hue-bridge/api/";
        bridgeUnten.authToken = "vjCRkBw9L3PIGVshwfyH93bxKJ8YwGlQx9t7wLY5";
        bridgeUnten.persist();
        bridges.add(bridgeUnten);

        for( Bridge b : bridges){
            System.out.println("Scanning " + b.name);
            HueLightsService hueService;
            hueService = RestClientBuilder.newBuilder().baseUrl(new URL(b.baseURL + b.authToken)).build(HueLightsService.class);

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
        return Bridge.findAll().list();
    }

    @GET
    @Path("/bridge/rooms")
    public List<Room> allRooms() {
        List<Room> rooms = Room.findAll(Sort.by("name").ascending()).list();
        //for( Room r : rooms ) {
        //    r.bridge = null;
        //}
        return rooms;
    }

}
