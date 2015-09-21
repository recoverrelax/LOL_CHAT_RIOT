package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum PlayerPosition {

    TOP(1),
    MIDDLE(2),
    JUNGLE(3),
    BOT(4);

    int positionId;

    PlayerPosition(int positionId){
        this.positionId = positionId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }
}
