package org.wanja.hue.remote;

public class Action {
    public String alert;
    public Integer bri;
    public String colormode;
    public Integer ct;
    public String effect;
    public Boolean on;
    public Integer hue;
    public Integer sat;
    public Integer transitiontime;
    public Integer briInc;
    public Integer satInc;
    public Integer hueInc;
    public String scene;
    @Override
    public String toString() {
        return "Action [alert=" + alert + ", bri=" + bri + ", briInc=" + briInc + ", colormode=" + colormode + ", ct="
                + ct + ", effect=" + effect + ", hue=" + hue + ", hueInc=" + hueInc + ", on=" + on + ", sat=" + sat
                + ", satInc=" + satInc + ", scene=" + scene + ", transitiontime=" + transitiontime + "]";
    }
}
