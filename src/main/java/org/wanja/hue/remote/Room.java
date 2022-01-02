package org.wanja.hue.remote;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Room extends PanacheEntity {
    @ManyToOne
    public Bridge bridge;

    public String number;
    public String name;
    public String type;
    public Boolean recycle;

    @Transient
    public Action action;

    @Transient
    public String[] lights;

    @OneToMany(mappedBy = "roomNumber")
    public List<Light> allLights = new ArrayList<Light>();

}
