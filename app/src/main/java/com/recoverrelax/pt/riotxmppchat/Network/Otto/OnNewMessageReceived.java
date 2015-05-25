package com.recoverrelax.pt.riotxmppchat.Network.Otto;

public class OnNewMessageReceived {
    private String from;

    public OnNewMessageReceived(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}
