package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Intersections {
    private String[] location;

    private String[] bearings;

    private String[] entry;

    private String in;

    public String[] getLocation ()
    {
        return location;
    }

    public void setLocation (String[] location)
    {
        this.location = location;
    }

    public String[] getBearings ()
    {
        return bearings;
    }

    public void setBearings (String[] bearings)
    {
        this.bearings = bearings;
    }

    public String[] getEntry ()
    {
        return entry;
    }

    public void setEntry (String[] entry)
    {
        this.entry = entry;
    }

    public String getIn ()
    {
        return in;
    }

    public void setIn (String in)
    {
        this.in = in;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [location = "+location+", bearings = "+bearings+", entry = "+entry+", in = "+in+"]";
    }
}
