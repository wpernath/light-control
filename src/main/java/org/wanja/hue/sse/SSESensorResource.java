package org.wanja.hue.sse;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.wanja.hue.PublicApiResource;
import org.wanja.hue.remote.Sensor;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;

@Path("/events/temp")
@Produces(MediaType.SERVER_SENT_EVENTS)
@ApplicationScoped
public class SSESensorResource implements SSEEventListener<Sensor> {

    private Sse sse;
    private SseBroadcaster broadcaster;

    @Inject
    PublicApiResource api;

    @Context
    public synchronized void setSse(Sse sse) {
        if (this.sse != null) {
            Log.infof("SSE already initialized!");
            return;
        }
        Log.info("Initialiazing SSESensors");
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
        this.broadcaster.onClose(eventSink -> Log.infof("OnClose EventSink %s", eventSink));
        this.broadcaster.onError(
                (eventSink, throwable) -> {

                    Log.errorf("OnError EventSink %s, Throwable %s", eventSink, throwable);                    
                    return;
                });
    }

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @GET
    public void register(@Context SseEventSink sink) throws Exception {
        if (sse == null) {
            Log.infof("No sse available. Component not initialized?");
            return;
        }
        
        List<Sensor> allNew = getSensorsToUpdate();
        String lights = JsonbBuilder.create().toJson(allNew.toArray());
        sink.send(sse.newEvent(lights));
        broadcaster.register(sink);
        
    }

    @Scheduled(every = "15s", identity = "temperature-sensors")
    void broadcast() {
        Log.infof("Updating temperature sensors.");
        try {
            List<Sensor> event = getSensorsToUpdate();
            broadcast(event);
        } 
        catch (Exception e) {
            Log.error(e);
        }
    }

    public synchronized void broadcast(List<Sensor> event) {
        if (sse == null) {
            Log.infof("No sse available. Component not initialized?");
            return;
        }
                
        Log.infof("Sending a broadcast message with %d updated sensors", event.size());
        broadcaster.broadcast(sse.newEvent(JsonbBuilder.create().toJson(event.toArray())));
    }

    private List<Sensor> getSensorsToUpdate() throws Exception {
        List<Sensor> all = api.allSensorsByType("ZLLTemperature");
        List<Sensor> allNew = new ArrayList<Sensor>(all.size());
        for( Sensor s1 : all) {
            Sensor s2 = api.sensorById(s1.id);
            allNew.add(s2);
        }

        return allNew;
    }
}
