package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Parent;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

@Parcel
public class RecentGamesBySID {

    Game [] games;
    long summonerId;

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }

    public long getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(long summonerId) {
        this.summonerId = summonerId;
    }
}
