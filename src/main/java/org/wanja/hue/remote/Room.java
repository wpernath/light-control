package org.wanja.hue.remote;

import java.util.Collections;
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

    @OneToMany(mappedBy = "roomId" )
    public List<Light> allLights = Collections.emptyList();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((recycle == null) ? 0 : recycle.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Room other = (Room) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (recycle == null) {
            if (other.recycle != null)
                return false;
        } else if (!recycle.equals(other.recycle))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    
}
