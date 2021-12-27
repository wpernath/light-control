package org.wanja.hue.remote;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "hue-rooms")
public interface HueRoomsService {
    @GET
    @Path("/rooms")
    ArrayList<Room> getAll();

}
