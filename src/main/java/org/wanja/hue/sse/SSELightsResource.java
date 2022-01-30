package org.wanja.hue.sse;



import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.wanja.hue.PublicApiResource;
import org.wanja.hue.remote.Light;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;



@Path("/events/lights")
@Produces(MediaType.SERVER_SENT_EVENTS)
@ApplicationScoped
public class SSELightsResource implements SSEEventListener<Light> {
    
    
    private Sse sse;
    private SseBroadcaster broadcaster;

    @Inject
    PublicApiResource api;

    @Context
    public synchronized void setSse(Sse sse) {
        if( this.sse != null ) {
            Log.infof("SSE already initialized!");
            return;
        }
        Log.info("Initialiazing SSELights");
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
        this.broadcaster.onClose(eventSink -> Log.infof("OnClose EventSink %s", eventSink));
        this.broadcaster.onError(
                (eventSink, throwable) -> {

                    Log.errorf("OnError EventSink %s, Throwable %s", eventSink, throwable);                    
                    return;
                });
    }

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
        if( sse == null ) {
            Log.infof("No sse available. Component not initialized?");
            return;
        }
        List<Light> allLights = api.allLights();

        String lights = JsonbBuilder.create().toJson(allLights.toArray());
        sink.send(sse.newEvent(lights));

        broadcaster.register(sink);
    }

    @Scheduled(every = "15s", identity = "lights")
    public void broadcast() {
        try {
            List<Light> allLights = api.allLights();
            broadcast(allLights);
        } 
        catch (Exception e) {
            Log.error(e);
        }
    }

    public synchronized void broadcast(List<Light> lights) {
        if(sse == null ) {
            Log.info("No sse available. Component not initialized?");
            return;
        }
        Log.infof("Sending a broadcast message with %d updated lights",lights.size() );
        broadcaster.broadcast(sse.newEvent(JsonbBuilder.create().toJson(lights.toArray())));
    }
}
