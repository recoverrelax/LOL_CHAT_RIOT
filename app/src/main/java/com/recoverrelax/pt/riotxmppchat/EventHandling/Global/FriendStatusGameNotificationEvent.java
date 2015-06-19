package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

public class FriendStatusGameNotificationEvent {
    private String friendName;
    private String friendXmppAddress;

    public FriendStatusGameNotificationEvent(String friendName, String friendXmppAddress) {
        this.friendName = friendName;
        this.friendXmppAddress = friendXmppAddress;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendXmppAddress() {
        return friendXmppAddress;
    }
}
