package org.wanja.hue.render;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wanja.hue.PublicApiResource;
import org.wanja.hue.remote.Room;
import org.wanja.hue.remote.Light;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.logging.Log;

@Path("/ui")
@Produces(MediaType.TEXT_HTML)
public class PublicRenderResource {
    @Inject
    Template index;

    @Inject
    PublicApiResource api;

    @GET
    @Path("/index")
    public TemplateInstance renderIndex() {
        Log.infof("Rendering index page");
        TemplateInstance ti = index.data("state", "index");
        ti.data("rooms", api.allRooms());
        ti.data("selectedRoom", null);
        return ti;
    }

    @GET
    @Path("/room")
    public TemplateInstance renderRoom(@QueryParam("room") String name) throws Exception {
        Log.infof("Rendering room %s", name);
        TemplateInstance ti = index.data("state", "room");
        ti.data("rooms", api.allRooms());
        ti.data("selectedRoom", api.roomByName(name));
        return ti;
    }

    @GET
    @Path("/favorites")
    public TemplateInstance renderFavorites() throws Exception {
        Log.infof("Rendering Favorites");
        List<Room> emptyRooms = api.allRooms();
        List<Light> allLights = new ArrayList();
        List<Room> rooms = new ArrayList<Room>(emptyRooms.size());
        for(Room r : emptyRooms ){
            rooms.add(api.roomByName(r.name));
            allLights.addAll(r.allLights);
        }
        TemplateInstance ti = index.data("state", "favorites");
        ti.data("rooms", rooms);
        ti.data("allLights", allLights);
        return ti;
    }

}
