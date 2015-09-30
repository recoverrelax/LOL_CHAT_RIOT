package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;

public class TeamInfo {
    String playerImage;
    String playerName;
    long playerId;

    public TeamInfo(){}

    public TeamInfo(long playerId, String playerImage){
        this.playerId = playerId;
        this.playerImage = playerImage;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
