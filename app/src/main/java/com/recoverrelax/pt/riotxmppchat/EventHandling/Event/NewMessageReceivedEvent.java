package com.recoverrelax.pt.riotxmppchat.EventHandling.Event;

public interface NewMessageReceivedEvent extends Event {
    void onNewMessageReceived(String userXmppAddress, String username, String message, String buttonLabel);
}
