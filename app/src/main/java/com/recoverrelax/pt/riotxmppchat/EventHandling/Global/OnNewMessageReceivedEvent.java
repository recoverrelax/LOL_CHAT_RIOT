package com.recoverrelax.pt.riotxmppchat.EventHandling.Global;

import org.jivesoftware.smack.packet.Message;


public class OnNewMessageReceivedEvent {

    private Message message;
    private String messageFrom;

    public OnNewMessageReceivedEvent(Message message, String messageFrom) {
        this.message = message;
        this.messageFrom = messageFrom;
    }

    public Message getMessage() {
        return message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }
}
