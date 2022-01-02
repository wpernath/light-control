package org.wanja.hue.remote;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Bridge extends PanacheEntity {
    public String bridgeNumber;
    public String name;

    public String baseURL;
    public String authToken;
}
