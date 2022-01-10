package org.wanja.hue.remote;

import javax.persistence.Entity;
import javax.persistence.Transient;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Light extends PanacheEntity {
    public String number;
    public String manufacturername;
    public String modelid;
    public String name;
    public String productname;
    public String type;
    public String uniqueid;
    public Long   roomId;
    public String bridgeNumber;
    public Boolean isFavorite = false;

    @Transient
    public State state;

    // The room number for Hue
    public String roomNumber;
}
