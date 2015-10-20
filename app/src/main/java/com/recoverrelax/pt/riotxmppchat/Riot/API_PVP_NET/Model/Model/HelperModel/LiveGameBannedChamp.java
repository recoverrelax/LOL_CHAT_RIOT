package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;

public class LiveGameBannedChamp {

    private int championID;
    private String championImage;
    private int teamId;

    public LiveGameBannedChamp() {

    }

    public LiveGameBannedChamp(int championID, int teamId) {
        this.championID = championID;
        this.teamId = teamId;
    }

    public int getChampionID() {
        return championID;
    }

    public void setChampionID(int championID) {
        this.championID = championID;
    }

    public String getChampionImage() {
        return championImage;
    }

    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LiveGameBannedChamp))
            return false;

        LiveGameBannedChamp bci = (LiveGameBannedChamp) o;
        return bci.championID == this.championID;
    }
}
