package org.wanja.hue.remote;

import java.text.NumberFormat;

public class LightState {
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

    public float brightnessPercent() {
        return (((float)bri/254)*100);
    }

    public String brightnessPercentString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(brightnessPercent());
    }

    @Override
    public String toString() {
        return "State [alert=" + alert + ", bri=" + bri + ", colormode=" + colormode + ", ct=" + ct + ", mode=" + mode
                + ", on=" + on + ", reachable=" + reachable + "]";
    }


    
}
