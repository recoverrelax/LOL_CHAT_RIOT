package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

import java.util.IllegalFormatException;

public enum PlayerPosition {

    TOP(1, "Top"),
    MIDDLE(2, "Mid"),
    JUNGLE(3, "Jungle"),
    BOT(4, "Bot");

    int positionId;
    String name;

    PlayerPosition(int positionId, String name){
        this.positionId = positionId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public static PlayerPosition getById(int id){
        for(PlayerPosition pp: PlayerPosition.values()){
            if(pp.getPositionId() == id)
                return pp;
        }
        return null;
    }
}
