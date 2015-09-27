package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Service {

    private List<Incident> incidents;
    private String name;
    private String slug;
    private String status;

    public Service(){}

    public List<Incident> getIncidents() {
        return incidents;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getStatus() {
        return status;
    }
}
