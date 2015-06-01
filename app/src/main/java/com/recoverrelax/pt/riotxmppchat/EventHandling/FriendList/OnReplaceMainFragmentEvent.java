package com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList;

public class OnReplaceMainFragmentEvent {

    String friendUsername;
    String friendXmppName;

    public OnReplaceMainFragmentEvent(String friendUsername, String friendXmppName) {
        this.friendUsername = friendUsername;
        this.friendXmppName = friendXmppName;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public String getFriendXmppName() {
        return friendXmppName;
    }
}
