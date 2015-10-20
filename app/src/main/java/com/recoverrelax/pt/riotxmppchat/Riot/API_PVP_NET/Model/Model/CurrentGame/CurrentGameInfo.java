package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants.GameQueueConfigId;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants.MapName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

import pt.reco.myutil.MyDate;

@Parcel
public class CurrentGameInfo {

    /**
     * Banned champion information
     */
    List<BannedChampion> bannedChampions;

    /**
     * The ID of the game
     */
    long gameId;

    /**
     * The amount of time in seconds that has passed since the game started
     */
    long gameLength;

    /**
     * The game mode (Legal values: CLASSIC, ODIN, ARAM, TUTORIAL, ONEFORALL,
     * ASCENSION, FIRSTBLOOD, KINGPORO)
     */
    String gameMode;

    /**
     * The queue type (queue types are documented on the Game Constants page)
     */
    long gameQueueConfigId;

    /**
     * The game start time represented in epoch milliseconds
     */
    long gameStartTime;

    /**
     * The game type (Legal values: CUSTOM_GAME, MATCHED_GAME, TUTORIAL_GAME)
     */
    String gameType;

    /**
     * The ID of the map
     */
    long mapId;

    /**
     * The observer information
     */
    Observer observers;

    /**
     * The participant information
     */
    List<CurrentGameParticipant> participants;

    /**
     * The ID of the platform on which the game is being played
     */
    String platformId;

    /**
     * NOT PART OF THE OBJECT SERIALIZATION
     */


    public CurrentGameInfo() {
    }

    public List<BannedChampion> getBannedChampions() {
        return bannedChampions;
    }

    private long getGameId() {
        return gameId;
    }

    private long getGameLength() {
        return gameLength;
    }

    private long getGameLenghtMinutesFormatted() {
        return Math.min(0, Math.round(gameLength / 60));
    }

    public String getGameMode() {
        return gameMode;
    }

    private long getGameQueueConfigId() {
        return gameQueueConfigId;
    }

    public String getGameQueueFormatted() {
        long gameQueueConfigId = this.gameQueueConfigId;

        GameQueueConfigId byQueueType = GameQueueConfigId.getByQueueType(gameQueueConfigId);
        if (byQueueType != null)
            return byQueueType.getName();
        else
            return "";
    }

    private long getGameStartTime() {
        return gameStartTime;
    }

    private String getGameType() {
        return gameType;
    }

    private long getMapId() {
        return mapId;
    }

    public String getMapName() {
        MapName byId = MapName.getById(this.mapId);
        return byId == null ? "" : byId.getMapName();
    }

    public String getGameStartTimeFormatted() {
        if (gameStartTime == 0)
            return "Now";
        else
            return MyDate.getFormatedDate(new Date(gameStartTime));
    }

    private Observer getObservers() {
        return observers;
    }

    public List<CurrentGameParticipant> getParticipants() {
        return participants;
    }

    private String getPlatformId() {
        return platformId;
    }
}
