package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Steps {
    private Intersections[] intersections;

    private String distance;

    private String duration;

    private String name;

    private Maneuver maneuver;

    private String mode;

    private Geometry geometry;

    public Intersections[] getIntersections ()
    {
        return intersections;
    }

    public void setIntersections (Intersections[] intersections)
    {
        this.intersections = intersections;
    }

    public String getDistance ()
    {
        return distance;
    }

    public void setDistance (String distance)
    {
        this.distance = distance;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Maneuver getManeuver ()
    {
        return maneuver;
    }

    public void setManeuver (Maneuver maneuver)
    {
        this.maneuver = maneuver;
    }

    public String getMode ()
    {
        return mode;
    }

    public void setMode (String mode)
    {
        this.mode = mode;
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
        return "ClassPojo [intersections = "+intersections+", distance = "+distance+", duration = "+duration+", name = "+name+", maneuver = "+maneuver+", mode = "+mode+", geometry = "+geometry+"]";
    }
}
