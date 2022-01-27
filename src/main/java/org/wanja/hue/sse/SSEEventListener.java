package org.wanja.hue.sse;

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.sse.SseEventSink;

public interface SSEEventListener<T> {
    public void register(@Context SseEventSink sink) throws Exception;
    public void broadcast(List<T> event);
}
