package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum GameType {
    CUSTOM_GAME("Custom games"),
    TUTORIAL_GAME("Tutorial games"),
    MATCHED_GAME("All other games");

    private String name;

    GameType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
