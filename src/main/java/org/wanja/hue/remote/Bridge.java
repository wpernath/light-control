package org.wanja.hue.remote;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Bridge extends PanacheEntity {
    public String bridgeNumber;
    public String name;

    public String baseURL;
    public String authToken;

    public String apiversion;
    public String bridgeid;
    public String datastoreversion;
    public String mac;
    public String modelid;
    public String swversion;

}
