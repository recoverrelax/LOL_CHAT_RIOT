package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion;

import org.parceler.Parcel;

/**
 * This object contains champion information.
 */
@Parcel
public class ChampionDto {

    /**
     * Indicates if the champion is active.
     */
    boolean active;

    /**
     * Bot enabled flag (for custom games).
     */
    boolean botEnabled;

    /**
     * Bot Match Made enabled flag (for Co-op vs. AI games).
     */
    boolean botMmEnabled;

    /**
     * Indicates if the champion is free to play. Free to play champions are rotated periodically.
     */
    boolean freeToPlay;

    /**
     * Champion ID. For static information correlating to champion IDs, please refer to the LoL Static Data API.
     */
    long id;

    /**
     * Ranked play enabled flag.
     */
    boolean rankedPlayEnabled;

    public ChampionDto () {}
}
