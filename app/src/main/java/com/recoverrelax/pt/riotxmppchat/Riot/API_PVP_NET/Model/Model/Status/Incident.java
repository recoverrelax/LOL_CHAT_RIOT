package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Incident {

    private boolean active;
    private String created_at;
    private long id;
    private List<Message> updates;

    public Incident(){

    }

    public boolean isActive() {
        return active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public List<Message> getUpdates() {
        return updates;
    }
}
