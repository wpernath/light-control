package org.wanja.hue.remote;

public class State {
    public String alert;
    public String mode;
    public Boolean reachable;
    public Boolean on;
    public Integer bri;

    public Boolean toggledState() {
        Boolean newState = false;
        if( on != null && on.booleanValue() )  newState = false;
        if (on != null && !on.booleanValue() ) newState = true;
        this.on = newState;
        return newState;
    }

}
