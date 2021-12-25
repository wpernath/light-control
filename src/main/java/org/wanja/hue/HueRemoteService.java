package org.wanja.hue;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import java.util.ArrayList;


@RegisterRestClient(configKey = "hue-lights" )
public interface HueRemoteService {

    @GET
    @Path("/lights/{id}")
    Light getLightsById(@PathParam int id);

    @GET
    @Path("/lights")
    ArrayList<Light> getAll();
}
