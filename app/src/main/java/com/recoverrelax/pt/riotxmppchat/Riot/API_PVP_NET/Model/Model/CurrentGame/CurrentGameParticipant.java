package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame;

import org.parceler.Parcel;

@Parcel
public class CurrentGameParticipant {

    /**
     * Flag indicating whether or not this participant is a bot
     */
    boolean bot;

    /**
     * The ID of the champion played by this participant
     */
    long championId;

    /**
     * The masteries used by this participant
     */
    Mastery [] masteries;

    /**
     * The ID of the profile icon used by this participant
     */
    long profileIcon;

    /**
     * The runes used by this participant
     */
    Rune [] runes;

    /**
     * The ID of the first summoner spell used by this participant
     */
    long spell1Id;

    /**
     * The ID of the second summoner spell used by this participant
     */
    long spell2Id;

    /**
     * The summoner ID of this participant
     */
    long summonerId;

    /**
     * The summoner name of this participant
     */
    String summonerName;

    /**
     * The team ID of this participant, indicating the participant's team
     */
    long teamId;

    public CurrentGameParticipant() {}
}
