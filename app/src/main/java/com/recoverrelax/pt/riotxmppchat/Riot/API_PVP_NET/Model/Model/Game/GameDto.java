package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

import java.util.List;

/**
 * This object contains game information.
 */
@Parcel
public class GameDto {

    /**
     * Champion ID associated with game.
     */
    private int championId;

    /**
     * Date that end game data was recorded, specified as epoch milliseconds.
     */
    private long createDate;

    /**
     * Other players associated with the game.
     */
    private List<PlayerDto> fellowPlayers;

    /**
     * Game ID.
     */
    private long gameId;

    /**
     * Game mode. (Legal values: CLASSIC, ODIN, ARAM, TUTORIAL,
     * ONEFORALL, ASCENSION, FIRSTBLOOD, KINGPORO)
     */
    private String gameMode;

    /**
     * Game type. (Legal values: CUSTOM_GAME, MATCHED_GAME, TUTORIAL_GAME)
     */
    private String gameType;

    /**
     * Invalid flag - ????
     */
    private boolean invalid;

    /**
     * ipEarned
     */
    private int ipEarned;

    /**
     * level
     */
    private int level;

    /**
     * mapId
     */
    private int mapId;

    /**
     * ID of first summoner spell.
     */
    private int spell1;

    /**
     * ID of second summoner spell.
     */
    private int spell2;

    /**
     * Statistics associated with the game for this summoner.
     */
    private RawStatsDto stats;

    /**
     * Game sub-type. (Legal values: NONE, NORMAL, BOT, RANKED_SOLO_5x5, RANKED_PREMADE_3x3, RANKED_PREMADE_5x5, ODIN_UNRANKED,
     * RANKED_TEAM_3x3, RANKED_TEAM_5x5, NORMAL_3x3, BOT_3x3, CAP_5x5, ARAM_UNRANKED_5x5, ONEFORALL_5x5, FIRSTBLOOD_1x1, FIRSTBLOOD_2x2,
     * SR_6x6, URF, URF_BOT, NIGHTMARE_BOT, ASCENSION, HEXAKILL, KING_PORO, COUNTER_PICK, BILGEWATER)
     */
    private String subType;

    /**
     * Team ID associated with game. Team ID 100 is blue team. Team ID 200 is purple team.
     */
    private int teamId;

    public GameDto(){

    }

    public int getChampionId() {
        return championId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public List<PlayerDto> getFellowPlayers() {
        return fellowPlayers;
    }

    public long getGameId() {
        return gameId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getGameType() {
        return gameType;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public int getIpEarned() {
        return ipEarned;
    }

    public int getLevel() {
        return level;
    }

    public int getMapId() {
        return mapId;
    }

    public int getSpell1() {
        return spell1;
    }

    public int getSpell2() {
        return spell2;
    }

    public RawStatsDto getStats() {
        return stats;
    }

    public String getSubType() {
        return subType;
    }

    public int getTeamId() {
        return teamId;
    }
}
