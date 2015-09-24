package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;

public class LiveGameParticipant {

    public long spell1Id;
    private long spell2Id;
    private long championId;
    private String summonerName;

    public LiveGameParticipant(){

    }

    public long getSpell1Id() {
        return spell1Id;
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
}
