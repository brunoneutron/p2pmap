package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Matchings {
    private String duration;

    private String distance;

    private Legs[] legs;

    private String confidence;

    private Geometry geometry;

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getDistance ()
    {
        return distance;
    }

    public void setDistance (String distance)
    {
        this.distance = distance;
    }

    public Legs[] getLegs ()
    {
        return legs;
    }

    public void setLegs (Legs[] legs)
    {
        this.legs = legs;
    }

    public String getConfidence ()
    {
        return confidence;
    }

    public void setConfidence (String confidence)
    {
        this.confidence = confidence;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [duration = "+duration+", distance = "+distance+", legs = "+legs+", confidence = "+confidence+", geometry = "+geometry+"]";
    }
}
