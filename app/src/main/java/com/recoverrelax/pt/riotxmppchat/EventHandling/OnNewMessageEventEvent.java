package com.recoverrelax.pt.riotxmppchat.EventHandling;

public class OnNewMessageEventEvent {
    private String userXmppAddress;

    public OnNewMessageEventEvent(String userXmppAddress){
        this.userXmppAddress = userXmppAddress;
    }

    public String getUserXmppAddress() {
        return userXmppAddress;
    }
}
