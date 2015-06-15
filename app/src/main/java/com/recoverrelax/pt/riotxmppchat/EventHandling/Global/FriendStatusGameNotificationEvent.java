package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

public class FriendStatusGameNotificationEvent {
    private String friendName;
    private String friendXmppAddress;
    private State state;

    public FriendStatusGameNotificationEvent(String friendName, String friendXmppAddress, State state) {
        this.state = state;
        this.friendName = friendName;
        this.friendXmppAddress = friendXmppAddress;
    }

    public State getState() {
        return state;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendXmppAddress() {
        return friendXmppAddress;
    }

    public enum State{
        STARTED,
        LEFT;
    }
}
