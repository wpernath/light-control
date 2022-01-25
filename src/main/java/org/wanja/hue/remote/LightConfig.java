package org.wanja.hue.remote;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class LightConfig extends PanacheEntity {
    public String archetype;
    public String direction;
    public String function;
    
}
