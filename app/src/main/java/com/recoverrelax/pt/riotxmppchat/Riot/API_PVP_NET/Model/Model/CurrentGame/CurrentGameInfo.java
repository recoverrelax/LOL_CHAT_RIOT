package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame;

import org.parceler.Parcel;

@Parcel
public class CurrentGameInfo {

    /**
     * Banned champion information
     */
    BannedChampion [] bannedChampions;

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
    CurrentGameParticipant participants;

    /**
     * The ID of the platform on which the game is being played
     */
    String platformId;

    public CurrentGameInfo() {}
}
