package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion;

import org.parceler.Parcel;

/**
 * This object contains champion information.
 */
@Parcel
public class Champion_ChampionDto {

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
    int id;

    /**
     * Ranked play enabled flag.
     */
    boolean rankedPlayEnabled;

    public Champion_ChampionDto() {}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBotEnabled() {
        return botEnabled;
    }

    public void setBotEnabled(boolean botEnabled) {
        this.botEnabled = botEnabled;
    }

    public boolean isBotMmEnabled() {
        return botMmEnabled;
    }

    public void setBotMmEnabled(boolean botMmEnabled) {
        this.botMmEnabled = botMmEnabled;
    }

    public boolean isFreeToPlay() {
        return freeToPlay;
    }

    public void setFreeToPlay(boolean freeToPlay) {
        this.freeToPlay = freeToPlay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isRankedPlayEnabled() {
        return rankedPlayEnabled;
    }

    public void setRankedPlayEnabled(boolean rankedPlayEnabled) {
        this.rankedPlayEnabled = rankedPlayEnabled;
    }
}
