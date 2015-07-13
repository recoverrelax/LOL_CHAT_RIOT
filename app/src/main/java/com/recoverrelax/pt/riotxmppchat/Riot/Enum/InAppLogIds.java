package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

public enum InAppLogIds {
    FRIEND_ONLINE(0),
    FRIEND_OFFLINE(1),
    FRIEND_PM(2),
    FRIEND_STARTED_GAME(3),
    FRIEND_ENDED_GAME(4);

    private final int operationId;

    InAppLogIds(int operationId){
        this.operationId = operationId;
    }

    public int getOperationId() {
        return operationId;
    }
}
