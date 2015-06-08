package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

public class FriendLeftGameNotification {
    String message;
    String friendName;
    String friendXmppAddress;

    public FriendLeftGameNotification(String message, String friendName, String friendXmppAddress) {
        this.message = message;
        this.friendName = friendName;
        this.friendXmppAddress = friendXmppAddress;
    }

    public String getMessage() {
        return message;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendXmppAddress() {
        return friendXmppAddress;
    }
}
