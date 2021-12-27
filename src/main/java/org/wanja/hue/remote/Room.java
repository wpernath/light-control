package org.wanja.hue.remote;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public String id;
    public String name;
    public String type;
    public Boolean recycle;
    public Action action;
    public String[] lights;

    public List<Light> allLights = new ArrayList<Light>();

}
