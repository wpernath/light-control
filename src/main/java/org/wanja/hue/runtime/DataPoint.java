package org.wanja.hue.runtime;

import java.util.Date;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class DataPoint extends PanacheEntity {
    public Long sensorId;
    public Integer temperature;
    public Date timeTaken;
    
}
