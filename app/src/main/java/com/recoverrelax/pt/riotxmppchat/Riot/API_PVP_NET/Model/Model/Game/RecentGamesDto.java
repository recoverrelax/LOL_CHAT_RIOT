package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

import java.util.List;

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
    private List<GameDto> games;
    /**
     * Summoner ID.
     */
    private long summonerId;

    public RecentGamesDto() {}

    public List<GameDto> getGames() {
        return games;
    }

    public long getSummonerId() {
        return summonerId;
    }
}
