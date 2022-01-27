package org.wanja.hue.sse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
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
import org.wanja.hue.remote.Room;


@Path("/events/room")
@Produces(MediaType.SERVER_SENT_EVENTS)
@Singleton
public class SSERoomResource implements SSEEventListener<Room> {
    @Context
    private Sse sse;

    @Inject
    PublicApiResource api;

    private volatile SseBroadcaster sseBroadcaster;

    @GET
    public void register(@Context SseEventSink sink) throws Exception {
        List<Room> emptyRooms = api.allRooms();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for (Room r : emptyRooms) {
            Room r2 = api.roomByName(r.name);
            r2.bridge = null;
            rooms.add(r2);
        }

        String lights = JsonbBuilder.create().toJson(rooms.toArray());
        sink.send(sse.newEvent(lights));

        if (sseBroadcaster == null)
            sseBroadcaster = sse.newBroadcaster();
        sseBroadcaster.register(sink);
        
    }

    @Override
    public void broadcast(List<Room> event) {
        if (sseBroadcaster == null)
            sseBroadcaster = sse.newBroadcaster();
        sseBroadcaster.broadcast(sse.newEvent(JsonbBuilder.create().toJson(event.toArray())));
        
    }

    
}
