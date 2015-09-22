package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import org.parceler.Parcel;

/**
 * This object contains game information.
 */
@Parcel
public class GameDto {

    /**
     * Champion ID associated with game.
     */
    int championId;

    /**
     * Date that end game data was recorded, specified as epoch milliseconds.
     */
    long createDate;

    /**
     * Other players associated with the game.
     */
    PlayerDto[] fellowPlayers;

    /**
     * Game ID.
     */
    long gameId;

    /**
     * Game mode. (Legal values: CLASSIC, ODIN, ARAM, TUTORIAL,
     * ONEFORALL, ASCENSION, FIRSTBLOOD, KINGPORO)
     */
    String gameMode;

    /**
     * Game type. (Legal values: CUSTOM_GAME, MATCHED_GAME, TUTORIAL_GAME)
     */
    String gameType;

    /**
     * Invalid flag - ????
     */
    boolean invalid;

    /**
     * ipEarned
     */
    int ipEarned;

    /**
     * level
     */
    int level;

    /**
     * mapId
     */
    int mapId;

    /**
     * ID of first summoner spell.
     */
    int spell1;

    /**
     * ID of second summoner spell.
     */
    int spell2;

    /**
     * Statistics associated with the game for this summoner.
     */
    RawStatsDto stats;

    /**
     * Game sub-type. (Legal values: NONE, NORMAL, BOT, RANKED_SOLO_5x5, RANKED_PREMADE_3x3, RANKED_PREMADE_5x5, ODIN_UNRANKED,
     * RANKED_TEAM_3x3, RANKED_TEAM_5x5, NORMAL_3x3, BOT_3x3, CAP_5x5, ARAM_UNRANKED_5x5, ONEFORALL_5x5, FIRSTBLOOD_1x1, FIRSTBLOOD_2x2,
     * SR_6x6, URF, URF_BOT, NIGHTMARE_BOT, ASCENSION, HEXAKILL, KING_PORO, COUNTER_PICK, BILGEWATER)
     */
    String subType;

    /**
     * Team ID associated with game. Team ID 100 is blue team. Team ID 200 is purple team.
     */
    int teamId;

    public GameDto(){

    }

    public int getChampionId() {
        return championId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public PlayerDto[] getFellowPlayers() {
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
