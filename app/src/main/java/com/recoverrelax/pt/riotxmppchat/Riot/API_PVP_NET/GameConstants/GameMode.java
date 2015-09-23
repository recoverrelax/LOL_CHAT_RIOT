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

    private String gameMode;

    GameMode(String gameMode){
        this.gameMode = gameMode;
    }

    public static GameMode getBySelf(String gameMode){
        for(GameMode gm: GameMode.values()){
            if(gm.toString().equals(gameMode))
                return gm;
        }
        return null;
    }

    public String getGameMode() {
        return gameMode;
    }
}
