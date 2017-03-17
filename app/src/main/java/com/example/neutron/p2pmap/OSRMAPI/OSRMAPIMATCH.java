package com.example.neutron.p2pmap.OSRMAPI;

/**
 * Created by neutron on 17. 1. 6.
 */

public class OSRMAPIMATCH {
    private Tracepoints[] tracepoints;

    private String code;

    private Matchings[] matchings;

    public Tracepoints[] getTracepoints ()
    {
        return tracepoints;
    }

    public void setTracepoints (Tracepoints[] tracepoints)
    {
        this.tracepoints = tracepoints;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public Matchings[] getMatchings ()
    {
        return matchings;
    }

    public void setMatchings (Matchings[] matchings)
    {
        this.matchings = matchings;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [tracepoints = "+tracepoints+", code = "+code+", matchings = "+matchings+"]";
    }
}
