package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

import org.jivesoftware.smack.packet.Message;


public class OnNewMessageReceivedEvent {

    private Message message;
    private String messageFrom;
    private String username;

    public OnNewMessageReceivedEvent(Message message, String messageFrom, String username) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Message getMessage() {
        return message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }
}
