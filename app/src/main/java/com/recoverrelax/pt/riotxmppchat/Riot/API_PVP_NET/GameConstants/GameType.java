package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum GameType {
    CUSTOM_GAME("Custom games"),
    TUTORIAL_GAME("Tutorial games"),
    MATCHED_GAME("All other games");

    private String description;

    GameType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
