package org.wanja.hue.remote;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Sensor extends PanacheEntity {
    @ManyToOne
    public Bridge bridge;
    public Long bridgeId;
    public String bridgeNumber;

    public Long sensorNumber;
    public String manufacturername;
    public String modelid;
    public String name;
    public String productname;
    public String type;
    public String swversion;
    public String uniqueid;
    public Boolean isFavorite = true;

    @OneToOne
    public SensorConfig config;

    @Transient
    public SensorState state;
 
    
}
