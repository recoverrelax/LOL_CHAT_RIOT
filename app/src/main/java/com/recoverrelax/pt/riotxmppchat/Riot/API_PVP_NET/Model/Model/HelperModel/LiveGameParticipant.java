package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;

public class LiveGameParticipant {

    public long spell1Id;
    private long spell2Id;
    private long championId;
    private long teamID;

    private String summonerName;

    private String championImage;
    private String spell1Image;
    private String spell2Image;

    public LiveGameParticipant(){

    }

    public LiveGameParticipant(long spell1Id, long spell2Id, long championId, String summonerName, long teamId){
        this.spell1Id = spell1Id;
        this.spell2Id = spell2Id;
        this.championId = championId;
        this.summonerName = summonerName;
        this.teamID = teamId;
    }


    public long getTeamID() {
        return teamID;
    }

    public long getSpell1Id() {
        return spell1Id;
    }

    public String getChampionImage() {
        return championImage;
    }

    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }

    public void setSpell1Id(long spell1Id) {
        this.spell1Id = spell1Id;
    }

    public long getSpell2Id() {
        return spell2Id;
    }

    public void setSpell2Id(long spell2Id) {
        this.spell2Id = spell2Id;
    }

    public long getChampionId() {
        return championId;
    }

    public void setChampionId(long championId) {
        this.championId = championId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSpell1Image() {
        return spell1Image;
    }

    public String getSpell2Image() {
        return spell2Image;
    }


}
