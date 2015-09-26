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

    /**
     *
     */

    String championImage;
    String spell1Image;
    String spell2Image;

    public CurrentGameParticipant() {}

    public boolean isBot() {
        return bot;
    }

    public long getChampionId() {
        return championId;
    }

    public Mastery[] getMasteries() {
        return masteries;
    }

    public long getProfileIcon() {
        return profileIcon;
    }

    public Rune[] getRunes() {
        return runes;
    }

    public long getSpell1Id() {
        return spell1Id;
    }

    public long getSpell2Id() {
        return spell2Id;
    }

    public long getSummonerId() {
        return summonerId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public long getTeamId() {
        return teamId;
    }

    /**
     * This method should not be used. ChampionImage is not part of the Json Return Array
     */
    public String getChampionImage() {
        return championImage;
    }

    /**
     * This method should not be used. ChampionImage is not part of the Json Return Array
     */
    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }

    public String getSpell2Image() {
        return spell2Image;
    }

    public void setSpell2Image(String spell2Image) {
        this.spell2Image = spell2Image;
    }

    public String getSpell1Image() {
        return spell1Image;
    }

    public void setSpell1Image(String spell1Image) {
        this.spell1Image = spell1Image;
    }
}
