package org.wanja.hue.sse;

import java.util.ArrayList;
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
import org.wanja.hue.remote.Room;

import io.quarkus.logging.Log;


@Path("/events/room")
@Produces(MediaType.SERVER_SENT_EVENTS)
@ApplicationScoped
public class SSERoomResource implements SSEEventListener<Room> {
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

    @GET
    public void register(@Context SseEventSink sink) throws Exception {
        if (sse == null) {
            Log.infof("No sse available. Component not initialized?");
            return;
        }

        List<Room> emptyRooms = api.allRooms();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for (Room r : emptyRooms) {
            Room r2 = api.roomByName(r.name);
            r2.bridge = null;
            rooms.add(r2);
        }

        String lights = JsonbBuilder.create().toJson(rooms.toArray());
        sink.send(sse.newEvent(lights));

        broadcaster.register(sink);
        
    }

    @Override
    public synchronized void broadcast(List<Room> event) {
        if (sse == null) {
            Log.infof("No sse available. Component not initialized?");
            return;
        }
        Log.infof("Sending a broadcast message with %d updated rooms", event.size());
        broadcaster.broadcast(sse.newEvent(JsonbBuilder.create().toJson(event.toArray())));
        
    }

    
}
