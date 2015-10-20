package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Summoner;

import org.parceler.Parcel;

@Parcel
public class SummonerDto {

    private long id;
    private String name;
    private long revisionDate;
    private long summonerLevel;

    public SummonerDto() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getRevisionDate() {
        return revisionDate;
    }

    public long getSummonerLevel() {
        return summonerLevel;
    }
}
