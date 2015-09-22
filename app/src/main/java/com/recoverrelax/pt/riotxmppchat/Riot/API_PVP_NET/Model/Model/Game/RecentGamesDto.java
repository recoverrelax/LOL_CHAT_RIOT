package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

/**
 * /api/lol/{region}/v1.3/game/by-summoner/{summonerId}/recent
 *
 * Get recent games by summoner ID. (REST)
 */
@Parcel
public class RecentGamesDto {

    /**
     * Collection of recent games played (max 10).
     */
    GameDto[] games;
    /**
     * Summoner ID.
     */
    long summonerId;

    public GameDto[] getGameDtos() {
        return games;
    }

    public void setGameDtos(GameDto[] games) {
        this.games = games;
    }

    public long getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(long summonerId) {
        this.summonerId = summonerId;
    }
}
