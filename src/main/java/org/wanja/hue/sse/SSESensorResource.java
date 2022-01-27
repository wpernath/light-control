package org.wanja.hue.sse;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.core.MediaType;

import org.wanja.hue.remote.Sensor;

@Path("/events/sensors")
@Produces(MediaType.SERVER_SENT_EVENTS)
@Singleton
public class SSESensorResource implements SSEEventListener<Sensor> {

    @Override
    public void register(SseEventSink sink) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void broadcast(List<Sensor> event) {
        // TODO Auto-generated method stub
        
    }

    
}
