package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum GameMode {

    CLASSIC("Classic Summoner's Rift and Twisted Treeline games"),
    ODIN("Dominion/Crystal Scar games"),
    ARAM("ARAM games"),
    TUTORIAL("Tutorial games"),
    ONEFORALL("One for All games"),
    ASCENSION("Ascension games"),
    FIRSTBLOOD("Snowdown Showdown games"),
    KINGPORO("King Poro games");

    private String name;

    GameMode(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
