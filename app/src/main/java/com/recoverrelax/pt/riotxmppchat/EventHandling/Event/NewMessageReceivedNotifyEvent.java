package com.recoverrelax.pt.riotxmppchat.EventHandling.Event;

public interface NewMessageReceivedNotifyEvent extends Event {
    void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel);
}
