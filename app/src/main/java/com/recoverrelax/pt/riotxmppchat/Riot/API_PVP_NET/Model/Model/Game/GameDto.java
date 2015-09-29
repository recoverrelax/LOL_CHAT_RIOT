package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;

import com.recoverrelax.pt.riotxmppchat.Riot.Enum.TeamCode;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * This object contains game information.
 */
@Parcel
public class GameDto {

    /**
     * Champion ID associated with game.
     */
    private int championId;

    private String championImage; //THIS ATTR IS NOT ORIGINAL FROM THE JSON
    private String spell1Image; //THIS ATTR IS NOT ORIGINAL FROM THE JSON
    private String spell2Image; //THIS ATTR IS NOT ORIGINAL FROM THE JSON

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

    public List<PlayerDto> getFellowPlayersTeam100() {
        List<PlayerDto> fellowPlayersTeam100 = new ArrayList<>();

        for(PlayerDto p: this.fellowPlayers)
            if(p.getTeamId() == TeamCode.TEAM1.id)
                fellowPlayersTeam100.add(p);
        return fellowPlayersTeam100;
    }

    public List<PlayerDto> getFellowPlayersTeam200() {
        List<PlayerDto> fellowPlayersTeam200 = new ArrayList<>();

        for(PlayerDto p: this.fellowPlayers)
            if(p.getTeamId() == TeamCode.TEAM2.id)
                fellowPlayersTeam200.add(p);
        return fellowPlayersTeam200;
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

    public String getChampionImage() {
        return championImage;
    }

    public void setChampionImage(String championImageSquareUrl) {
        this.championImage = championImageSquareUrl;    }

    public String getSpell1Image() {
        return spell1Image;
    }

    public void setSpell1Image(String spell1Image) {
        this.spell1Image = spell1Image;
    }

    public String getSpell2Image() {
        return spell2Image;
    }

    public void setSpell2Image(String spell2Image) {
        this.spell2Image = spell2Image;
    }
}
