package org.wanja.hue.sse;


import java.util.Arrays;
import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.bind.JsonbBuilder;
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

import org.wanja.hue.PublicApiResource;
import org.wanja.hue.remote.Light;

import io.vertx.core.json.JsonArray;


@Path("/events/lights")
@Produces(MediaType.SERVER_SENT_EVENTS)
@Singleton
public class SSELightsResource implements SSEEventListener<Light> {
    
    @Context
    private Sse sse;
    
    @Inject
    PublicApiResource api;

    private volatile SseBroadcaster sseBroadcaster;


    /**
     * Register for light updates. Once you're calling this, you get
     * an JSON Array of all ligths with current state
     * 
     * JavaScript example:
     * 
     * let events = new EventSource("/events/lights");
     * events.onmessage = function(event) {
     *      allLights = JSON.parse(event.data);
     *      console.log("This is coming from the server:");
     *      for( let i = 0; i < allLights.length; i++ ) {
     *          console.log(allLights[i].id + " - " + allLights[i].name);
     *      }
     * };
     * 
     * @param sink
     * @throws Exception
     */
    @GET
    public void register(@Context SseEventSink sink) throws Exception {
        List<Light> allLights = api.allLights();

        String lights = JsonbBuilder.create().toJson(allLights.toArray());
        sink.send(sse.newEvent(lights));

        if( sseBroadcaster == null ) sseBroadcaster = sse.newBroadcaster();
        sseBroadcaster.register(sink);
    }

    public void broadcast(List<Light> lights) {
        if (sseBroadcaster == null)
            sseBroadcaster = sse.newBroadcaster();
        

        sseBroadcaster.broadcast(sse.newEvent(JsonbBuilder.create().toJson(lights.toArray())));
    }
}
