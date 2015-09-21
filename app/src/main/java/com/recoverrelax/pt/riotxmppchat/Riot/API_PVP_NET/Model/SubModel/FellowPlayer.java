package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.SubModel;

import org.parceler.Parcel;

@Parcel
public class FellowPlayer {

    int championId;
    long summonerId;
    int teamId;

    public FellowPlayer() {}

    public int getChampionId() {
        return championId;
    }

    public long getSummonerId() {
        return summonerId;
    }

    public int getTeamId() {
        return teamId;
    }
}
