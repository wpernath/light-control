package org.wanja.hue.remote;

public class State {
    public String alert;
    public String mode;
    public Boolean reachable;
    public Boolean on;
    public Integer bri;

    // next two props are only set if the bulb supports colors
    public String colormode;
    public Long ct;
    public Integer hue;
    public Integer sat;
    public Float[] xy;

    public Boolean toggledState() {
        Boolean newState = false;
        if( on != null && on.booleanValue() )  newState = false;
        if (on != null && !on.booleanValue() ) newState = true;
        this.on = newState;
        return newState;
    }

    public boolean supportsColorChange() {
        if( colormode != null && !colormode.isEmpty()) {
            if( colormode.equalsIgnoreCase("ct") || 
                colormode.equalsIgnoreCase("xy") ||
                colormode.equalsIgnoreCase("hs"))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "State [alert=" + alert + ", bri=" + bri + ", colormode=" + colormode + ", ct=" + ct + ", mode=" + mode
                + ", on=" + on + ", reachable=" + reachable + "]";
    }


    
}
