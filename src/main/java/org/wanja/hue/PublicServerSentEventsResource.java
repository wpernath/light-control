package org.wanja.hue;

import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;


import com.fasterxml.jackson.core.JsonProcessingException;

import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.wanja.hue.remote.Light;

import io.vertx.core.json.JsonArray;


@Path("/events")
@Produces(MediaType.SERVER_SENT_EVENTS)
@Singleton
public class PublicServerSentEventsResource {
    
    @Context
    private Sse sse;
    
    @Inject
    PublicApiResource api;

    private volatile SseBroadcaster sseBroadcaster;


    @GET
    public void register(@Context SseEventSink sink) throws IllegalStateException, RestClientDefinitionException, MalformedURLException, JsonProcessingException {
        //List<Room> allRooms = api.allRooms();
        //ObjectMapper om = new ObjectMapper();
        //String t = om.writeValueAsString(allRooms.toArray());
        
        sink.send(sse.newEvent("servus!"));

        if( sseBroadcaster == null ) sseBroadcaster = sse.newBroadcaster();
        sseBroadcaster.register(sink);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void broadcast(Light light) {
        if (sseBroadcaster == null)
            sseBroadcaster = sse.newBroadcaster();
        
        JsonArray jaw = new JsonArray();
        jaw.add(light);
        sseBroadcaster.broadcast(sse.newEvent(jaw.toString()));
    }
}
