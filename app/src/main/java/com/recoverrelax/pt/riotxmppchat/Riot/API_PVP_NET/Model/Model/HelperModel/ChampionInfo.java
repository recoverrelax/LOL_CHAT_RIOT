package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;


import java.util.ArrayList;
import java.util.List;

public class ChampionInfo {

    private String championImage;
    private List<String> championSkinImage;

    public ChampionInfo(){}

    public String getChampionImage() {
        return championImage;
    }

    public void setChampionImage(String championImage) {
        this.championImage = championImage;
    }

    public List<String> getChampionSkinImage() {
        return championSkinImage;
    }

    public void addChampionSkinsImage(List<String> skinList) {
        this.championSkinImage = new ArrayList<>();
        this.championSkinImage.addAll(skinList);
    }
}
