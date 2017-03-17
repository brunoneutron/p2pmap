package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Tracepoints {
    private String matchings_index;

    private String[] location;

    private String name;

    private String hint;

    private String waypoint_index;

    public String getMatchings_index ()
    {
        return matchings_index;
    }

    public void setMatchings_index (String matchings_index)
    {
        this.matchings_index = matchings_index;
    }

    public String[] getLocation ()
    {
        return location;
    }

    public void setLocation (String[] location)
    {
        this.location = location;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getHint ()
    {
        return hint;
    }

    public void setHint (String hint)
    {
        this.hint = hint;
    }

    public String getWaypoint_index ()
    {
        return waypoint_index;
    }

    public void setWaypoint_index (String waypoint_index)
    {
        this.waypoint_index = waypoint_index;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [matchings_index = "+matchings_index+", location = "+location+", name = "+name+", hint = "+hint+", waypoint_index = "+waypoint_index+"]";
    }
}
