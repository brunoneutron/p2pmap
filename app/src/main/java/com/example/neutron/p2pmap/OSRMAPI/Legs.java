package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class Legs {
    private String summary;

    private String distance;

    private String duration;

    private Steps[] steps;

    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
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

    public Steps[] getSteps ()
    {
        return steps;
    }

    public void setSteps (Steps[] steps)
    {
        this.steps = steps;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [summary = "+summary+", distance = "+distance+", duration = "+duration+", steps = "+steps+"]";
    }
}
