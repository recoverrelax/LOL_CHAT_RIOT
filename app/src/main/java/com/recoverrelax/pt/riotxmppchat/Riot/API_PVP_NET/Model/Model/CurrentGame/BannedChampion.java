package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame;

import org.parceler.Parcel;

@Parcel
public class BannedChampion {

    /**
     * The ID of the banned champion
     */
    long championId;

    /**
     *The turn during which the champion was banned
     */
    int pickTurn;

    /**
     * The ID of the team that banned the champion
     */
    long teamId;

    public BannedChampion() {}
}
