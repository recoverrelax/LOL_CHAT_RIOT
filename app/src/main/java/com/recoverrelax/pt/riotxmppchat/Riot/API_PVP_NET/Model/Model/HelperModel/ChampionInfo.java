package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;


import java.util.ArrayList;
import java.util.List;

public class ChampionInfo {

    private String championImage;
    private List<String> championSkinImage;
    private String championName;
    private long championId;

    public ChampionInfo() {
    }

    public String getChampionImage() {
        return championImage;
    }

    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }

    public List<String> getChampionSkinImage() {
        return championSkinImage;
    }

    public void setChampionSkinImage(List<String> championSkinImage) {
        this.championSkinImage = championSkinImage;
    }

    public void addChampionSkinsImage(List<String> skinList) {
        this.championSkinImage = new ArrayList<>();
        this.championSkinImage.addAll(skinList);
    }

    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    public long getChampionId() {
        return championId;
    }

    public void setChampionId(long championId) {
        this.championId = championId;
    }

    @Override
    public String toString() {
        return "ChampionInfo{" +
                "championImage='" + championImage + '\'' +
                ", championSkinImage=" + championSkinImage +
                ", championName='" + championName + '\'' +
                ", championId=" + championId +
                '}';
    }
}
