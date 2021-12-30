package org.wanja.hue.remote;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import java.util.Map;



@RegisterRestClient(configKey = "hue-lights" )
public interface HueLightsService {

    @GET
    @Path("/lights/{id}")
    Light getLightById(@PathParam String id);

    @GET
    @Path("/lights")
    Map<String, Light> getAllLights();

    @GET
    @Path("/groups/{id}")
    Room getRoomById(@PathParam String id);

    @GET
    @Path("/groups")
    Map<String, Room> getAllGroups();

    @PUT
    @Path("/lights/{id}/state")
    StateResponse[] setLightState(@PathParam String id, State state);

    @PUT
    @Path("/groups/{id}/action")
    void setGroupAction(@PathParam String id, Action action);
}
