package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

public class FriendStatusChangedEvent {
    private String friendName;
    private String message;

    public FriendStatusChangedEvent(String friendName, String message){
        this.friendName = friendName;
        this.message = message;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getMessage() {
        return message;
    }
}
