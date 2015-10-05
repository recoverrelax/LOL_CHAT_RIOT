package com.recoverrelax.pt.riotxmppchat.EventHandling.Event;

public interface NewMessageReceivedNotifyEvent {
    void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel);
}
