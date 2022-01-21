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
    public String productid;
    public String productname;
    public String swconfigid;
    public String swversion;
    public String type;
    public String uniqueid;

    // next 3 properties are only used by the app and are not coming from HUE
    public Long   roomId;
    public String bridgeNumber;
    public Boolean isFavorite = true;

    @Transient
    public State state = new State();

    // The room number for Hue
    public String roomNumber;

    @Override
    public String toString() {
        return "Light [manufacturername=" + manufacturername + ", modelid=" + modelid + ", name=" + name + ", number="
                + number + ", state=" + state + ", swconfigid=" + swconfigid + ", swversion=" + swversion + ", type="
                + type + ", uniqueid=" + uniqueid + "]";
    }


    
}
