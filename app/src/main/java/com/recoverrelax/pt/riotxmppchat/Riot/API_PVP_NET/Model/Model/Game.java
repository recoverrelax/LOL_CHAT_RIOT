package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.SubModel.FellowPlayer;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.SubModel.Stat;

import org.parceler.Parcel;

@Parcel
public class Game {

    int championId;
    long createDate;
    FellowPlayer [] fellowPlayers;
    long gameId;
    String gameMode; // GameMode
    String gameType;
    boolean invalid;
    int ipEarned;
    int level;
    int mapId;
    int spell1;
    int spell2;
    Stat stats;
    String subType;
    int teamId;

    Game(){}

    public int getChampionId() {
        return championId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public FellowPlayer[] getFellowPlayers() {
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

    public Stat getStats() {
        return stats;
    }

    public String getSubType() {
        return subType;
    }

    public int getTeamId() {
        return teamId;
    }
}
