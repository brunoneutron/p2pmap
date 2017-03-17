package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Maneuver {
    private String modifier;

    private String[] location;

    private String bearing_after;

    private String bearing_before;

    private String type;

    public String getModifier ()
    {
        return modifier;
    }

    public void setModifier (String modifier)
    {
        this.modifier = modifier;
    }

    public String[] getLocation ()
    {
        return location;
    }

    public void setLocation (String[] location)
    {
        this.location = location;
    }

    public String getBearing_after ()
    {
        return bearing_after;
    }

    public void setBearing_after (String bearing_after)
    {
        this.bearing_after = bearing_after;
    }

    public String getBearing_before ()
    {
        return bearing_before;
    }

    public void setBearing_before (String bearing_before)
    {
        this.bearing_before = bearing_before;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [modifier = "+modifier+", location = "+location+", bearing_after = "+bearing_after+", bearing_before = "+bearing_before+", type = "+type+"]";
    }
}
