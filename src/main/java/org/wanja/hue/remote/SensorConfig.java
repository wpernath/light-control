package org.wanja.hue.remote;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class SensorConfig extends PanacheEntity {
    public String newName;
    public Boolean  isFavorite;
    
    public String alert;
    public Integer battery;

    @Column(name = "is_on")
    public Boolean on;

    @Column(name = "has_led_indication")
    public Boolean ledindication;

    @Column(name = "is_reachable")
    public Boolean reachable;
}
