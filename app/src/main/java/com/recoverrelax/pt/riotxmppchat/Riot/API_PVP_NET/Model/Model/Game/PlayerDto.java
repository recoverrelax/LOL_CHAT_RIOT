package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

/**
 *  This object contains player information.
 */
@Parcel
public class PlayerDto {

    /**
     * Champion id associated with player.
     */
    int championId;

    /**
     * Summoner id associated with player.
     */
    long summonerId;

    /**
     * Team id associated with player.
     */
    int teamId;

    public PlayerDto() {}

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
