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

    /**
     * NOT PART OF THE OBJECT SERIALIZATION
     */

    String championImage;

    public BannedChampion() {}

    public long getChampionId() {
        return championId;
    }

    public long getTeamId() {
        return teamId;
    }

    /**
     * This method should not be used. ChampionImage is not part of the Json Return Array
     */
    public String getChampionImage() {
        return championImage;
    }

    /**
     * This method should not be used. ChampionImage is not part of the Json Return Array
     */
    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }
}
